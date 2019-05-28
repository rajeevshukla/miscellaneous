package com.windstream.voip.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.windstream.voip.annotations.LogExecutionTime;
import com.windstream.voip.config.VoIPProperties;
import com.windstream.voip.converter.BwUsersToVoIPUserDetailsResponseConverter;
import com.windstream.voip.exception.EnterpriseNotFoundException;
import com.windstream.voip.exception.UserNotFoundException;
import com.windstream.voip.exception.VoIPServiceException;
import com.windstream.voip.model.animgmt.AssignAniRequest;
import com.windstream.voip.model.animgmt.UnAssignAniRequest;
import com.windstream.voip.model.common.Ani;
import com.windstream.voip.model.common.BWUserType;
import com.windstream.voip.model.common.EntGroupLines;
import com.windstream.voip.model.common.Enterprise;
import com.windstream.voip.model.common.Group;
import com.windstream.voip.model.enterprises.EnterpriseGroupsResponse;
import com.windstream.voip.model.permission.VoIPPermissionRequest;
import com.windstream.voip.model.sso.SSORespsone;
import com.windstream.voip.model.user.BWUserDetails;
import com.windstream.voip.model.user.LinesDetailsResponse;
import com.windstream.voip.model.user.LinesDetailsResponse.AniDetails;
import com.windstream.voip.model.user.UpdateUserRequest;
import com.windstream.voip.model.user.UserRequest;
import com.windstream.voip.model.user.VoIPUserDetailsResponse;
import com.windstream.voip.proxy.welink.WeLinkServiceProxy;
import com.windstream.voip.repository.broadworks.BroadworksDAO;
import com.windstream.voip.repository.voip.VoIPDAO;
import com.windstream.voip.service.VoIPService;
import com.windstream.voip.util.ApplicationUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@Slf4j
public class VoIPServiceImpl implements VoIPService {

	@Autowired
	BroadworksDAO broadworksDao;

	@Autowired
	VoIPDAO voipDao;

	@Autowired
	VoIPProperties properties;

	@Autowired
	WeLinkServiceProxy weLinkProxyService;

	@Override
	@LogExecutionTime
	public void saveUser(UserRequest user) throws VoIPServiceException {

		// checking if busEntityId is present
		if (!checkIfBusEntityIdExist(user.getBusEntityId())) {
			log.info("Calling welink proxy to to pull global account Ids for busEntityId: {}, userId:{}",
					user.getBusEntityId(), user.getUserId());
			List<Long> globalAcctIdList = weLinkProxyService.getGlobalAccountIdsForBusEntityId(user.getBusEntityId());
			if (globalAcctIdList.isEmpty()) {
				throw new VoIPServiceException("Busniess entity Id:" + user.getBusEntityId() + " does not exist.");
			} else {
				log.info("Found globalActIds from winlink for busEntityId:{}, globalActIds:{}", user.getBusEntityId(),
						globalAcctIdList);
				fetchAndSaveActDetailsAndEntpriseId(globalAcctIdList, user.getBusEntityId());
			}
		}

		if (!checkIfUserIsValidAndExist(user)) {
			saveAdminUser(user);
		} else {
			try {
				// check if there is any addition enterprise Id added
				List<String> list = voipDao.getAvailableUnAssignedEnterpriseIds(user.getUserId(), user.getBusEntityId());
				if (!list.isEmpty())
					saveAdminUser(user);
				voipDao.deleteInvalidBwUsers(user.getUserId(), user.getBusEntityId());
			} catch (Exception e) {
				log.error("Error in creating user", e);
				throw new VoIPServiceException("Error in creating/deleting users", e);
			}
			updateUser(new UpdateUserRequest(user.getUserId(), user.getUserId(), user.getFirstName(), user.getLastName()));
		}
	}

	@Override
	public void updateUser(UpdateUserRequest user) throws VoIPServiceException {
		log.info("Updating user: " + user);
		/**
		 * At present only firstName and lastName will be updated. Future enhancement,
		 * call SSF's modifyUser api call to update first name, last name in Broadworks.
		 */
		boolean isEligibleForUpdate = false;
		List<BWUserDetails> users = getBwUserDetailsForUserId(user.getCurrentUserId());

		if (!users.isEmpty()) {
			// all the users would be same except their type, hence picking first one.
			BWUserDetails bwUser = users.get(0);
			if (!bwUser.getFirstName().equals(user.getFirstName())
					|| !bwUser.getLastName().equals(user.getLastName()) || !user.getCurrentUserId().equals(user.getNewUserId())) {
				isEligibleForUpdate = true;
			}
		}
		if (isEligibleForUpdate) {
			log.info("User:{} is eligible for update", user.getCurrentUserId());
			try {
				voipDao.updateUser(user);
				users.forEach(u->{
					if(u.isEnterpriseAdmin()) { 
						broadworksDao.updateEnterpriseAdmin(u.getBwUserId(), u.getEnterpriseId(),user.getFirstName(), user.getLastName());
					} else if(u.isGroupAdmin()) { 
						broadworksDao.updateGroupAdmin(u.getBwUserId(), u.getEnterpriseId(),u.getGroupId(),user.getFirstName(), user.getLastName());
					} else if(u.isAniUser()) { 
						List<String> aniList = new ArrayList<>();
						aniList.add(u.getAni());
						broadworksDao.updateAniUserDetails(user.getFirstName(), user.getLastName(), u.getEnterpriseId(), u.getGroupId(), aniList);
						//TODO future enhancement  : Group all the anis of same enterprise and group, and pass them into a single call instead of having multiple 
						//call for anis having same groups.  
					}

				});

			} catch (Exception e) {
				log.error("Unable to update user:{}", user, e);
				throw new VoIPServiceException("Unable to update user details for userId:" + user.getCurrentUserId());
			}
		}
	}

	@Override
	public void deleteUser(String userId, Optional<Long> userKey) throws VoIPServiceException {

		List<BWUserDetails> bwUserList = getBwUserDetailsForUserId(userId);

		if(userKey.isPresent()) { 
			bwUserList = bwUserList.stream().filter(user-> user.getUserKey().equals(userKey.get())).collect(Collectors.toList());
		}
		// first Delete from local db and then delete from bw.
		try {
			voipDao.deleteUser(userId, userKey);
		} catch (Exception e) {
			log.error("Unable to delete user for userId:{}", userId, e);
			throw new VoIPServiceException("Unable to delete user for userId:" + userId, e);
		}

		// deleting all users simultaneously in bw.
		bwUserList.stream().forEach(bwUser -> {
			if (bwUser.isEnterpriseAdmin()) {
				broadworksDao.deleteEnterpriseAdmin(bwUser.getEnterpriseId(), bwUser.getBwUserId())
				.exceptionally(ex -> {
					log.error("Error while deleting users in broadworks for userId:{}", userId, ex);
					return null;
				});
			} else if (bwUser.isGroupAdmin()) {
				broadworksDao.deleteGroupAdmin(bwUser.getEnterpriseId(), bwUser.getGroupId(), bwUser.getBwUserId())
				.exceptionally(ex -> {
					log.error("Error while deleting users in broadworks for userId:" + userId, ex);
					return null;
				});
			}
		});



	}

	@Override
	public List<BWUserDetails> getBwUserDetailsForUserId(String userId) throws VoIPServiceException {
		List<BWUserDetails> users = null;
		try {
			users = voipDao.getBwUsersForUserId(userId);

		} catch (Exception e) {
			log.error("Unable to fetch users for userId:{}", userId, e);
			throw new VoIPServiceException("Unable to fetch users for userId:" + userId, e);
		}
		return users;
	}

	@Override
	@LogExecutionTime
	public SSORespsone getSsoURL(String userId, Optional<Long> entityId) throws VoIPServiceException {
		SSORespsone ssoRespsone = new SSORespsone();
		String bwUserId = null;
		List<BWUserDetails> users = getBwUserDetailsForUserId(userId);

		if (users.isEmpty()) {
			log.error("User details not found for userId:{}", userId);
			throw new UserNotFoundException("User not found for userId:" + userId);
		}

		// checking if entityId is not null
		if (entityId.isPresent()) {
			BWUserDetails userDetails = ApplicationUtils.getBwUserDetialsForBwUserId(users, entityId.get());
			bwUserId = userDetails.getBwUserId();
		}

		try {

			if (users.size() > 1 && !entityId.isPresent()) {
				ssoRespsone.setStatus(SSORespsone.SSOStatus.MULTIPLE_ENTITIES_FOUND);
				SSORespsone.ManagebleEntities en = new SSORespsone.ManagebleEntities();
				en.setAnis(ApplicationUtils.getEntityListForSSOResponse(users, BWUserType.ANI));
				en.setGroups(ApplicationUtils.getEntityListForSSOResponse(users, BWUserType.GROUP_ADMIN));
				en.setEnterprises(ApplicationUtils.getEntityListForSSOResponse(users, BWUserType.ENTERPRISE_ADMIN));
				ssoRespsone.setDetails(en);
			} else if (users.size() == 1) {
				bwUserId = users.get(0).getBwUserId();

			}
			if (!ApplicationUtils.isEmpty(bwUserId)) {
				String url = broadworksDao.getSSOURL(bwUserId);

				if (url.indexOf("?EA_ERR=") > 0) {
					throw new VoIPServiceException("Unable to get the correct URL from Broadwroks. Url:" + url);
				}

				ssoRespsone.setStatus(SSORespsone.SSOStatus.URL_FOUND);
				ssoRespsone.setUrl(url);
			}

		} catch (Exception e) {
			log.error("Error while getting getting URL for userId::{}", userId, e);
			throw new VoIPServiceException("Unable to fetch URL for userId:" + userId, e);
		}
		return ssoRespsone;
	}

	private List<String> getAvailableUnassignedEnterpriseIds(Long businessEntityId, String userId) throws VoIPServiceException {
		List<String> entList = null;
		try {
			entList = voipDao.getAvailableUnAssignedEnterpriseIds(userId, businessEntityId);
		} catch (Exception e) {
			log.error("Error while getting enterprise groups for businessEntityId:{}", businessEntityId, e);
			throw new VoIPServiceException("Unable to fetch enterprise groups for account:" + businessEntityId, e);
		}

		return entList;
	}

	private void saveAdminUser(UserRequest user) throws VoIPServiceException {
		List<String> enterpriseIds = getAvailableUnassignedEnterpriseIds(user.getBusEntityId(), user.getUserId());

		if (enterpriseIds.isEmpty()) {
			throw new EnterpriseNotFoundException("No enterprise gorups found for account:" + user.getBusEntityId());
		}

		List<CompletableFuture<Boolean>> list = enterpriseIds.stream().map(enterpriseId -> {
			return createEnterpriseAdminUserInBW(enterpriseId, user);
		}).collect(Collectors.toList());

		// making synchronous call if size is less than expected else async call
		if (list.size() <= properties.getEnableAsyncAfter()) {
			list.forEach(t -> {
				try {
					log.info("Enterprise admin user created:{}", t.get()); // Just for waiting to make call synchronous
				} catch (InterruptedException | ExecutionException e) {
					// If any of the enterprise admin user creation failed(in case of multiple)
					// throwing an exception as failed
					throw new VoIPServiceException(e.getCause().getMessage());
				}
			});
		}
	}

	private CompletableFuture<Boolean> createEnterpriseAdminUserInBW(String enterpriseId, UserRequest user) {
		return broadworksDao.createEnterpriseAdmin(enterpriseId, user.getFirstName(), user.getLastName())
				.thenApply(enterpriseAdminId -> createVoIPUserInDB(enterpriseAdminId, enterpriseId, null, null, BWUserType.ENTERPRISE_ADMIN, user))
				.exceptionally(ex -> {
					log.error(
							"Error in creating enterprise admin in broadworks enterpriseId:{}, userId:{}",
							enterpriseId, user.getUserId(), ex);
					throw new VoIPServiceException(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
				});
	}

	private CompletableFuture<Boolean> createGroupAdminUserInBW(String enterpriseId, String groupId, UserRequest user) {
		return broadworksDao.createGroupAdmin(enterpriseId, groupId, user.getFirstName(), user.getLastName())
				.thenApply(groupAdminId -> createVoIPUserInDB(groupAdminId, enterpriseId, groupId, null, BWUserType.GROUP_ADMIN, user))
				.exceptionally(ex -> {
					log.error(
							"Error in creating group admin in broadworks enterpriseId:{}, groupId{}, userId:{}",
							enterpriseId, groupId, user.getUserId(), ex);
					throw new VoIPServiceException(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
				});
	}

	private boolean createVoIPUserInDB(String bwAdminUserId, String enterpriseId, String groupId, String ani, BWUserType userType, UserRequest user) {
		boolean status = false;
		try {
			voipDao.saveUser(bwAdminUserId, enterpriseId, groupId, ani, userType, user);
			status = true;
		} catch (Exception e) {
			log.error("Unable to save broadworks user in database userId:{}, enterpriseId:{}, bwUserId:{}",
					user.getUserId(), enterpriseId, bwAdminUserId, e);
			throw new VoIPServiceException("Unable to save admin user in voip db", e);
		}
		log.info("User has been created in bw as well as in local db, bwUserId:{} for userId:{}", bwAdminUserId, user.getUserId());
		return status;
	}

	private void fetchAndSaveActDetailsAndEntpriseId(List<Long> globalAccountIds, Long busEntityId) {

		try {
			voipDao.saveGlobalActIdsForBusEntityId(globalAccountIds, busEntityId);
			log.info("Global account Ids for businessEntId is saved successfully. BusEntId:{}", busEntityId);
			// sync bus entity enterprise details
			if (!voipDao.fetchAndSaveEnterpriseId(busEntityId))
				throw new EnterpriseNotFoundException(
						"EnterpriseId does not exist for the businessEntityId:" + busEntityId);

		} catch (EnterpriseNotFoundException e) {
			log.error("Enterprise id does not exist in CAMP for busEntityId:{} and globalAcctIds:{}", busEntityId,
					globalAccountIds);
			throw e;
		} catch (Exception e) {
			log.error("Unable to save global Account ids for business EntityId: {} globalActId:{}", busEntityId,
					globalAccountIds, e);
			throw new VoIPServiceException("Unable to save global Account ids for business EntityId:" + busEntityId);
		}
	}

	private boolean checkIfUserIsValidAndExist(UserRequest user) {
		boolean userExist = false;

		List<BWUserDetails> users = null;
		try {
			users = getBwUserDetailsForUserId(user.getUserId());
			if (!users.isEmpty()) {
				userExist = true;
			}
		} catch (VoIPServiceException e) {
			log.error("User does not exist for userId:{}", user.getUserId(),e);
			userExist = false; // ignoring exception.
		}
		return userExist;
	}

	private boolean checkIfBusEntityIdExist(Long busEntityId) throws VoIPServiceException {
		boolean busEntityIdExist = false;

		try {
			busEntityIdExist = voipDao.checkIfBusEntityIdPresent(busEntityId);
		} catch (Exception e) {
			busEntityIdExist = false;
			log.error("Error in checking busEntityId existance:{}", busEntityId, e);
			throw new VoIPServiceException("busEntityId: " + busEntityId + " does not exist.");
		}
		return busEntityIdExist;
	}
	@LogExecutionTime
	@Override
	public List<VoIPUserDetailsResponse> getUsers(Long busEntityId, Optional<String> userId) throws VoIPServiceException {

		List<BWUserDetails> bwUserDetails;
		checkIfBusEntityIdExist(busEntityId); 
		if(userId.isPresent()) {
			UserRequest request = new UserRequest();
			request.setUserId(userId.get());
			request.setBusEntityId(busEntityId);
			//validating userId against bustEntityId  
			checkIfUserIsValidAndExist(request);
		}

		try {
			bwUserDetails = voipDao.getAllUsers(busEntityId);
			if(userId.isPresent()) {
				bwUserDetails = bwUserDetails.stream().filter(user-> user.getUserId().equals(userId.get())).collect(Collectors.toList());
			}

		} catch (Exception e) {
			log.error("Error in fetching users for busEntityId {}", busEntityId, e);
			throw new VoIPServiceException("Error in fetching users for business entity Id:" + busEntityId);
		}
		if (bwUserDetails.isEmpty()) {  
			if(!userId.isPresent()) {
				throw new VoIPServiceException("No Users found for the given business entity Id.");
			}
			throw new VoIPServiceException("No Users found for the given business entity Id and UserId");
		}

		BwUsersToVoIPUserDetailsResponseConverter converter = new BwUsersToVoIPUserDetailsResponseConverter();
		List<VoIPUserDetailsResponse> userDetailsResponses = converter.convert(bwUserDetails);
		return userDetailsResponses;
	}

	@LogExecutionTime
	@Override
	public EnterpriseGroupsResponse getEnterpriseGroups(String userId, Optional<String> enterpriseId)
			throws VoIPServiceException {
		List<BWUserDetails> allUserRepresentations = getBwUserDetailsForUserId(userId);

		if(allUserRepresentations.isEmpty()) {
			throw new UserNotFoundException("No user exits for userId:"+userId);
		}

		EnterpriseGroupsResponse response = new EnterpriseGroupsResponse();

		List<Enterprise> enterpriseList = ApplicationUtils.extractEnterprises(allUserRepresentations);

		if(enterpriseList.isEmpty()) {
			throw new VoIPServiceException("No enterprise Id exist for userId:"+userId);
		}

		if(enterpriseId.isPresent()) {
			enterpriseList = enterpriseList.stream()
					.filter((enterprise)-> enterprise.getEnterpriseId().equals(enterpriseId.get()))
					.collect(Collectors.toList());
			if(enterpriseList.isEmpty()) {
				throw new VoIPServiceException("Enterprise Id:"+enterpriseId.get()+" does not exist.");
			}
		}

		List<CompletableFuture<List<Group>>> completableFuturesGroups = new ArrayList<>();

		enterpriseList.stream().forEach((enterprise)->{
			completableFuturesGroups.add(broadworksDao.getGroupsForEnterprise(enterprise.getEnterpriseId()));
		}); 

		List<List<Group>> listOfListOfGroups = new ArrayList<>();

		completableFuturesGroups.stream().forEach((future)-> {
			try {
				listOfListOfGroups.add(future.get());
			} catch (InterruptedException | ExecutionException e) {
				log.error("Error in fetching groups from broadworks",e);
				throw new VoIPServiceException("Error in fetching groups from broadwroks.");
			}
		});

		response.setEnterprises(enterpriseList);
		response.setGroups(listOfListOfGroups.stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList()));
		return response;
	}

	@Override
	@LogExecutionTime
	public void saveOrUpdateVoIPPermissions(VoIPPermissionRequest request) throws VoIPServiceException {
		List<BWUserDetails> userList =	getBwUserDetailsForUserId(request.getSelectedUserId());

		//removing those groups from request whose enterprise is also selected to create enterprise admin 

		request.setGroups(request.getGroups().stream().filter(g-> !request.getEnterprises().contains(g.getEnterpriseId())).collect(Collectors.toList()));

		List<BWUserDetails> loggedInUserList = getBwUserDetailsForUserId(request.getLoggedUserId());
		if(loggedInUserList.isEmpty()) {
			throw new UserNotFoundException("loggedInUserId:"+ request.getLoggedUserId()+" does not exist");
		}
		List<String> enterpriseIdToCreateAdmins = null;
		List<Group> groupIdsToCreateadmins = null;
		if(!userList.isEmpty()) { 
			List<BWUserDetails>  usersToBeDeleted = getUsersEligibleToDelete(request, userList);

			usersToBeDeleted.stream().forEach(user-> {
				deleteUser(user.getUserId(), Optional.of(user.getUserKey()));
			});

			enterpriseIdToCreateAdmins = filterEnterpriseIdsToCreateAdmin(request, userList);
			groupIdsToCreateadmins = filterGroupIdsToCreateAdmin(request, userList);
		} else { 
			enterpriseIdToCreateAdmins = request.getEnterprises();
			groupIdsToCreateadmins = request.getGroups().stream().map(g-> new Group(g.getGroupId(), g.getEnterpriseId())).collect(Collectors.toList());
		}

		UserRequest user = new  UserRequest();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setUserId(request.getSelectedUserId());
		List<CompletableFuture<Boolean>> enterpriseAdminFuture = new ArrayList<>();
		List<CompletableFuture<Boolean>> groupAdminFuture = new ArrayList<>();

		//creating enterprise admins	
		enterpriseIdToCreateAdmins.forEach(enterpriseId-> {
			//Determining busEntityId for enterpriseId
			Optional<BWUserDetails> bwUser = ApplicationUtils.getBusEntityIdForEnterpriseId(loggedInUserList, enterpriseId);
			if(!bwUser.isPresent()) {
				throw new VoIPServiceException("No BusEntityId exists for entepriseId:"+enterpriseId+" userId:"+request.getLoggedUserId());
			}
			//setting exact busEntityId based on enterpriseId and userId
			user.setBusEntityId(bwUser.get().getBusEntityId());
			enterpriseAdminFuture.add(createEnterpriseAdminUserInBW(enterpriseId, user));
		});


		//creating group admins
		groupIdsToCreateadmins.forEach(group->{
			Optional<BWUserDetails> bwUser = ApplicationUtils.getBusEntityIdForEnterpriseId(userList, group.getEnterpriseId());
			if(!bwUser.isPresent()) {
				throw new VoIPServiceException("NO BusEntityId exists for entepriseId:"+group.getEnterpriseId()+" userId:"+request.getLoggedUserId());
			}
			user.setBusEntityId(bwUser.get().getBusEntityId());
			groupAdminFuture.add(createGroupAdminUserInBW(group.getEnterpriseId(), group.getGroupId(), user));
		});

		//making enterprise admin calls synchronous to propagate exception to client if there is any
		enterpriseAdminFuture.forEach(f->{
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				log.error("Error in creating enterprise admin.", e);
				throw new VoIPServiceException("Error in creating enterprise in broadworks", e);
			}
		});

		//making group admin calls synchronous to propagate exception to client if there is any
		groupAdminFuture.forEach(f->{
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				log.error("Error in creating group admin.", e);
				throw new VoIPServiceException("Error in creating group admin in broadworks", e);
			}
		});


	}



	/**
	 * Return those user's enterprise/Group Id which is not present in request and they will be part of delete operation.
	 * 
	 * @param request
	 * @param userList
	 * @return
	 */
	private List<BWUserDetails> getUsersEligibleToDelete(VoIPPermissionRequest request, List<BWUserDetails> userList) { 
		//checking enterprise admin which are eligible to get deleted 

		Set<String> enterpriseIdSet = request.getEnterprises().stream().collect(Collectors.toSet());

		List<BWUserDetails> entUsersToBeDeleted = userList.stream()
				.filter(user-> user.isEnterpriseAdmin())
				.filter(user-> !enterpriseIdSet.contains(user.getEnterpriseId()))
				.collect(Collectors.toList());

		//checking group admins which are eligible to get deleted
		Set<VoIPPermissionRequest.Group> requestedGroupIdSet = request.getGroups().stream().collect(Collectors.toSet());
		//filtering users which are group admin and their entepriseId and groupId not matches with groupId and enterpriseId passed in request they would be eligible for delete

		List<BWUserDetails> groupUsersToBeDeleted = userList.stream().
				filter(user-> user.isGroupAdmin()).
				filter(user-> requestedGroupIdSet.stream()
						.filter(group-> user.getGroupId()
								.equals(group.getGroupId()) && user.getEnterpriseId()
								.equals(group.getEnterpriseId())
								).count()==0)
				.collect(Collectors.toList());

		List<BWUserDetails> usersToBeDeleted = new ArrayList<>();
		usersToBeDeleted.addAll(entUsersToBeDeleted);
		usersToBeDeleted.addAll(groupUsersToBeDeleted);

		return usersToBeDeleted;
	}


	@LogExecutionTime
	@Override
	public List<LinesDetailsResponse> getLines(String userId, Optional<String> enterpriseId, Optional<String> groupId)
			throws VoIPServiceException {

		List<BWUserDetails> userList = getBwUserDetailsForUserId(userId);
		if(userList.isEmpty()) {
			throw new UserNotFoundException("UserId:"+userId+" does not exist.");
		}
		List<Long> busEntityIdList = ApplicationUtils.getDistinctBusEntityList(userList);

		Map<String, List<String>> enterpriseIdGroupListMap = new HashMap<>();
		List<String> groupList = new ArrayList<>();
		if(enterpriseId.isPresent()) { 
			enterpriseIdGroupListMap.put(enterpriseId.get(), null); // null means assuming all groups are selected. 
			if(groupId.isPresent()) { 
				groupList.add(groupId.get());
				enterpriseIdGroupListMap.put(enterpriseId.get(), groupList);
			} else  { 
				enterpriseIdGroupListMap = getEnterpriseIdGroupIdMap(userId, enterpriseId);
			}
		} else { 
			enterpriseIdGroupListMap = getEnterpriseIdGroupIdMap(userId, Optional.empty());
		}

		// 1. fetching all users summary for selected enterprise Ids and groups
		List<CompletableFuture<List<EntGroupLines>>> linesFuture = enterpriseIdGroupListMap.entrySet()
				.stream().map(entry->
				broadworksDao.getAllUsersSummaryByEntGroups(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());


		// 2. Fetching all the bwUsers having ani assigned 
		final	Map<String, String> aniUserIdMap = new HashMap<>();

		List<BWUserDetails> users = new ArrayList<>();
		//fetching all the users across the busSystemIds
		busEntityIdList.forEach(busEntityId->{
			try {
				List<BWUserDetails> tempUsers =  voipDao.getAllUsers(busEntityId);
				users.addAll(tempUsers);
			} catch (Exception e) {
				log.error("Error in getting ani users from DB. ",e );
			}
		});

		List<BWUserDetails> assignedAniUsers = users.stream().filter(user-> user.isAniUser()).collect(Collectors.toList());
		aniUserIdMap.putAll(assignedAniUsers.stream().collect(Collectors.toMap(BWUserDetails::getAni, BWUserDetails::getUserId)));


		List<List<EntGroupLines>> listOfListOfLines =  linesFuture.stream().map( lineFuture -> {
			try {
				return lineFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				log.error("Error in fetching lines for userId:{}", userId,e);
			}
			return null;
		})
				.filter(lineDetailsResponse-> lineDetailsResponse != null)
				.collect(Collectors.toList());

		//converting list of list Into single list.
		//List<LinesDetailsResponse> linesDetailsResponses =	 listOfListOfLines.stream().flatMap(List::stream).collect(Collectors.toList());
		List<EntGroupLines> linesDetails =	 listOfListOfLines.stream().flatMap(List::stream).collect(Collectors.toList());

		List<LinesDetailsResponse> linesDetailsResponses = 
				linesDetails.stream().map (lineDetail -> 
				new LinesDetailsResponse (
						lineDetail.getEnterpriseId(), 
						lineDetail.getGroupId(), 
						lineDetail.getAnis().stream()
						.map (
								ani -> {
									return new AniDetails(ani);
								})
						.collect(Collectors.toList())))
				.collect(Collectors.toList());


		///3. mapping assigned userIds to every line. 

		linesDetailsResponses.forEach(lineDetail -> { 
			lineDetail.getAnis().forEach(aniDetails ->{ 
				if(aniUserIdMap.containsKey(aniDetails.getAni())) {
					aniDetails.setUserId(aniUserIdMap.get(aniDetails.getAni()));
				}
			});
		});
		return linesDetailsResponses;
	}

	private Map<String, List<String>> getEnterpriseIdGroupIdMap(String userId, Optional<String> enterpriseId) { 
		Map<String, List<String>> enterpriseIdGroupListMap = new HashMap<>();
		//fetch all the enterprises and corresponding groups for a user
		EnterpriseGroupsResponse enterpriseGroupsResponse = getEnterpriseGroups(userId,enterpriseId);
		enterpriseGroupsResponse.getGroups().forEach(group-> { 
			if(enterpriseIdGroupListMap.get(group.getEnterpriseId())!=null) { 
				enterpriseIdGroupListMap.get(group.getEnterpriseId()).add(group.getGroupId());
			} else { 
				ArrayList<String> groups = new ArrayList<>();
				groups.add(group.getGroupId());
				enterpriseIdGroupListMap.put(group.getEnterpriseId(), groups);
			}
		});
		return enterpriseIdGroupListMap;
	}

	/**
	 * Filter and return those entperiseIds which can be used to create enterprise admins and broadworks 
	 * 
	 * @param request
	 * @param userList
	 * @return
	 */
	private List<String> filterEnterpriseIdsToCreateAdmin(VoIPPermissionRequest request, List<BWUserDetails> userList) { 
		// Converting into set to remove duplicates 
		Set<String> enterpriseIdSet = request.getEnterprises().stream().collect(Collectors.toSet());

		List<String> exitingEnterpriseIds = ApplicationUtils.extractEnterprises(userList).stream().map(enterprise->enterprise.getEnterpriseId()).collect(Collectors.toList());

		List<String> newEntepriseIds =	enterpriseIdSet.stream().filter(enterpriseId-> !exitingEnterpriseIds.contains(enterpriseId)).collect(Collectors.toList());

		return newEntepriseIds;

	}

	/**
	 * Filter and return groups which can be used to create group admins in broadworks.
	 * 
	 * @param request
	 * @param userList
	 * @return
	 */
	private List<Group> filterGroupIdsToCreateAdmin(VoIPPermissionRequest request, List<BWUserDetails> userList) {

		Set<Group> requestedGroups = request.getGroups().stream()
				.map(requestedGroup-> new Group(requestedGroup.getGroupId(), requestedGroup.getEnterpriseId()))
				.collect(Collectors.toSet());

		List<Group> existingGroupIds = ApplicationUtils.extractGroups(userList);
		List<Group> newGroupIds  = requestedGroups.stream()
				.filter(group-> !existingGroupIds.contains(group))
				.collect(Collectors.toList());

		return newGroupIds;

	}

	@LogExecutionTime
	@Override
	public void assignAni(AssignAniRequest request) throws VoIPServiceException {

		// check if ani is already assigned.
		validateIfAniAlreadyAssigned(request);

		UserRequest user = new UserRequest();

		List<BWUserDetails> loggedInUserDetails = getBwUserDetailsForUserId(request.getLoggedInUserId());
		if(loggedInUserDetails == null || loggedInUserDetails.isEmpty()) {
			throw new UserNotFoundException("UserId:"+request.getLoggedInUserId()+" does not exist.");
		}


		if(ApplicationUtils.isValidList(request.getAnis())) { 

			user.setUserId(request.getTargetUserId());
			user.setFirstName(request.getTargetUserFirstName());
			user.setLastName(request.getTargetUserLastName());

			request.getAnis().forEach(ani-> {
				// Looking for matching busEntityId for the enteprise Id so that we can save user along with correct busEntityId
				Optional<BWUserDetails> bwUser = loggedInUserDetails.stream().
						filter(u->u.getEnterpriseId().equals(ani.getEnterpriseId())).
						findFirst();

				//handling worst case scenario just in case validation is not applied 
				if(!bwUser.isPresent()) {
					throw new VoIPServiceException("No busEntityId found for userId:"+request.getLoggedInUserId() + " enterpriseId:"+ani.getEnterpriseId());
				}
				user.setBusEntityId(bwUser.get().getBusEntityId());
				createVoIPUserInDB(ani.getAni(), ani.getEnterpriseId(), ani.getGroupId(),ani.getAni(), BWUserType.ANI, user);
			});

			// Obtaining all the anis by grouping them into enterprise and group
			Map<Pair<String, String>, List<Ani>> entGroupAnis = request.getAnis().stream().collect(Collectors.groupingBy(ani->Pair.of(ani.getEnterpriseId(), ani.getGroupId())));

			entGroupAnis.entrySet().forEach(entrySet->{
				broadworksDao.updateAniUserDetails(user.getFirstName(), user.getLastName(), entrySet.getKey().getLeft(), entrySet.getKey().getRight(), extractAnis(entrySet.getValue()));
			});
		}
	}


	private void validateIfAniAlreadyAssigned(AssignAniRequest request) {
		try {
			List<String> anis = request.getAnis().stream().map(ani->ani.getAni()).collect(Collectors.toList());
			if(voipDao.checkIfAnisIsAlreadyAssigned(anis)) { 
				throw new VoIPServiceException("Please select only unassigned Anis.");
			}
		} catch (Exception e) {
			log.error("Error while validating ani assignment anis{}",request.getAnis() ,e);
			throw new VoIPServiceException("Error while validating ani assignment.",e);
		}
	}

	@LogExecutionTime
	@Override
	public void unassignAni(UnAssignAniRequest request) throws VoIPServiceException {

		List<String> anis = extractAnis(request.getAnis());
		List<BWUserDetails> targetUserDetails = getBwUserDetailsForUserId(request.getTargetUserId());
		List<BWUserDetails> usersToBeDeleted = targetUserDetails.stream()
				.filter(u->u.isAniUser())
				.filter(u->anis.contains(u.getAni()))
				.collect(Collectors.toList());

		if(usersToBeDeleted.isEmpty()) {
			throw new VoIPServiceException("Selected ani is not assigned to user:"+request.getTargetUserId());
		}

		usersToBeDeleted.forEach(u->{ 
			try {
				voipDao.deleteUser(u.getUserId(), Optional.of(u.getUserKey()));
			} catch (Exception e) {
				log.error("Error in unassigning the ani",e);
				throw new VoIPServiceException("Error in unassigning ani for userId:"+request.getTargetUserId(),e);
			}
		});

	}


	/**
	 * Extracting all the phone numbers from aniDetails list and returning back only list of phone numbers.
	 * @param aniDetailsList
	 * @return
	 */
	private List<String> extractAnis(List<Ani> aniDetailsList) { 
		return	aniDetailsList.stream().map(ani->ani.getAni()).collect(Collectors.toList());
	}

	@Override
	public List<VoIPUserDetailsResponse> getAllVoIPAccountUsers(String userId) throws VoIPServiceException {

		List<BWUserDetails> userList = getBwUserDetailsForUserId(userId);

		if(userList == null || userList.isEmpty()) {
			throw  new UserNotFoundException("User does not exists for userId:"+userId);
		}

		List <Long> busEntityIdList = ApplicationUtils.getDistinctBusEntityList(userList); 

		List<BWUserDetails> accountUsersList = new ArrayList<>();

		busEntityIdList.forEach(busEntityId->{
			try {
				List<BWUserDetails> bwUserList = voipDao.getAllUsers(busEntityId);
				if(bwUserList !=null && !bwUserList.isEmpty()) {
					accountUsersList.addAll(bwUserList);
				}
			} catch (Exception e) {
				log.error("Error in fetching all users for busEntityId:"+busEntityId);
			}
		});

		BwUsersToVoIPUserDetailsResponseConverter converter = new BwUsersToVoIPUserDetailsResponseConverter();
		return	converter.convert(accountUsersList);
	}


	@Override
	public VoIPUserDetailsResponse getUserDetails(String userId) throws VoIPServiceException {

		List<BWUserDetails> bwUserList =  getBwUserDetailsForUserId(userId);

		if(bwUserList == null || bwUserList.isEmpty()) {
			throw new VoIPServiceException("userId:"+userId +" does not exists.");
		}
		BwUsersToVoIPUserDetailsResponseConverter converter = new BwUsersToVoIPUserDetailsResponseConverter();
		return	converter.convert(bwUserList).get(0);
	}

}
