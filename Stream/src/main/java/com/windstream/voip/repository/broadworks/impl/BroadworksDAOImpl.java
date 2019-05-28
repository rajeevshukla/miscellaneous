package com.windstream.voip.repository.broadworks.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.xml.transform.StringSource;

import com.ssf.allusersummary.EnterpriseGroupsRequestDTO;
import com.ssf.allusersummary.GetAllUsersSummaryByEnterpriseAndGroupsResponseDTO;
import com.ssf.allusersummary.GetAllUsersSummaryByEnterpriseAndGroupsResponseDTO.GroupUsersSummaryList;
import com.ssf.allusersummary.GroupUsersSummaryDTO;
import com.ssf.getenterprisegroups.GetGroupsInEnterpriseRequestDTO;
import com.ssf.getenterprisegroups.GetGroupsInEnterpriseResponseDTO;
import com.ssf.modifyusers.ModifyUsersRequestDTO;
import com.ssf.modifyusers.ModifyUsersRequestDTO.Users;
import com.ssf.modifyusers.ModifyUsersResponse;
import com.ssf.modifyusers.UserModifyDTO;
import com.ssf.modifyusers.UserModifyDetailsDTO;
import com.windstream.voip.annotations.LogExecutionTime;
import com.windstream.voip.config.VoIPProperties;
import com.windstream.voip.converter.AllUserSummaryToLinesInfoConverter;
import com.windstream.voip.exception.VoIPServiceException;
import com.windstream.voip.model.common.EntGroupLines;
import com.windstream.voip.model.common.Group;
import com.windstream.voip.model.jaxb.AdminApiResponse;
import com.windstream.voip.repository.broadworks.BroadworksDAO;
import com.windstream.voip.soap.client.BWAuthTokenSoapClient;
import com.windstream.voip.soap.client.SSFSoapClient;
import com.windstream.voip.soap.client.VoIPAdminApiSoapClient;

import bw.managed.authtoken.GetTokenForUser;
import bw.managed.authtoken.GetTokenForUserResponse;
import bw.managed.voipadmin.AddEnterpriseAdmin;
import bw.managed.voipadmin.AddEnterpriseAdminResponse;
import bw.managed.voipadmin.AddEnterpriseGroupAdmin;
import bw.managed.voipadmin.AddEnterpriseGroupAdminResponse;
import bw.managed.voipadmin.DeleteEnterpriseAdmin;
import bw.managed.voipadmin.DeleteEnterpriseAdminResponse;
import bw.managed.voipadmin.DeleteEnterpriseGroupAdmin;
import bw.managed.voipadmin.DeleteEnterpriseGroupAdminResponse;
import bw.managed.voipadmin.EditEnterpriseAdmin;
import bw.managed.voipadmin.EditEnterpriseAdminResponse;
import bw.managed.voipadmin.EditEnterpriseGroupAdmin;
import bw.managed.voipadmin.EditEnterpriseGroupAdminResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@CacheConfig(cacheNames="bwCache")
public class BroadworksDAOImpl implements BroadworksDAO {

	@Autowired
	BWAuthTokenSoapClient bwAuthtokenClient;

	@Autowired
	VoIPAdminApiSoapClient voipAdminApiSoapClient;

	@Autowired
	@Qualifier("byos")
	
	Jaxb2Marshaller jaxb2Marshaller;

	@Autowired
	VoIPProperties properties;

	@Autowired 
	SSFSoapClient ssfSoapClient;

	@LogExecutionTime
	@Async("bwAsyncExecutor")
	@Override
	@Cacheable
	public CompletableFuture<List<Group>> getGroupsForEnterprise(String enterpriseId) {
		GetGroupsInEnterpriseRequestDTO request = new GetGroupsInEnterpriseRequestDTO();
		request.setServiceProviderId(enterpriseId);
		List<Group> groups;

		GetGroupsInEnterpriseResponseDTO  response = ssfSoapClient.getGroupsByEnterprise(request);

		if(response.getGroups()!= null) {
			groups = response.getGroups().stream()
					.map((group)-> new Group(enterpriseId, group.getGroupId(), group.getGroupName()))
					.collect(Collectors.toList());
		} else {
			groups = Collections.emptyList();
		}

		return CompletableFuture.completedFuture(groups);

	}

	@LogExecutionTime
	@Async("bwAsyncExecutor")
	@Override
	public CompletableFuture<String> createEnterpriseAdmin(String enterpriseId, String firstName, String lasName) {

		AddEnterpriseAdmin addEnterpriseAdmin = new AddEnterpriseAdmin();
		addEnterpriseAdmin.setEnterpriseId(enterpriseId);
		addEnterpriseAdmin.setCluster(properties.getByoscluster());
		addEnterpriseAdmin.setNameFirst(firstName);
		addEnterpriseAdmin.setNameLast(lasName);
		AddEnterpriseAdminResponse response = voipAdminApiSoapClient.execute(addEnterpriseAdmin,
				AddEnterpriseAdminResponse.class);

		AdminApiResponse adminApiResponse = unmarshalToResponseObj(response.getReturn());
		if (!adminApiResponse.getCode().equalsIgnoreCase("0")) {
			log.error(
					"Error in creating enterprise admin for enterpriseId:{}, Msg:",enterpriseId, adminApiResponse);
			throw new VoIPServiceException(adminApiResponse.getMessage());
		}

		return CompletableFuture.completedFuture(adminApiResponse.getValue());
	}

	@LogExecutionTime
	@Async("bwAsyncExecutor")
	@Override
	public CompletableFuture<String> createGroupAdmin(String enterpriseId, String groupId, String firstName, String lastName) {

		AddEnterpriseGroupAdmin addEnterpriseGroupAdmin = new AddEnterpriseGroupAdmin();
		addEnterpriseGroupAdmin.setEnterpriseId(enterpriseId);
		addEnterpriseGroupAdmin.setGroupId(groupId);
		addEnterpriseGroupAdmin.setCluster(properties.getByoscluster());
		addEnterpriseGroupAdmin.setNameFirst(firstName);
		addEnterpriseGroupAdmin.setNameLast(lastName);

		AddEnterpriseGroupAdminResponse response = voipAdminApiSoapClient.execute(addEnterpriseGroupAdmin,
				AddEnterpriseGroupAdminResponse.class);

		AdminApiResponse adminApiResponse = unmarshalToResponseObj(response.getReturn());

		if (!adminApiResponse.getCode().equalsIgnoreCase("0")) {
			log.error("Error in creating group admin for enterpriseId:" + enterpriseId + "groupId:" + groupId
					+ " Msg:" + adminApiResponse);
			throw new VoIPServiceException(adminApiResponse.getMessage());
		}
		return CompletableFuture.completedFuture(adminApiResponse.getValue()); // groupAdminId
	}

	@Async("bwAsyncExecutor")
	@Override
	public CompletableFuture<Void> deleteEnterpriseAdmin(String enterpriseId, String enterpriseAdminId) {
		DeleteEnterpriseAdmin deleteEnterpriseAdmin = new DeleteEnterpriseAdmin();
		deleteEnterpriseAdmin.setCluster(properties.getByoscluster());
		deleteEnterpriseAdmin.setEnterpriseId(enterpriseId);
		deleteEnterpriseAdmin.setAdminId(enterpriseAdminId);
		DeleteEnterpriseAdminResponse adminResponse = voipAdminApiSoapClient.execute(deleteEnterpriseAdmin,
				DeleteEnterpriseAdminResponse.class);

		AdminApiResponse response = unmarshalToResponseObj(adminResponse.getReturn());
		if (!response.getCode().equalsIgnoreCase("0")) {
			log.error("Error in enterprise admin for enterpriseId:" + enterpriseId + "enterpriseAdminUserId:"
					+ enterpriseAdminId + " Msg:" + response.getMessage());
			throw new VoIPServiceException(response.getMessage());
		}

		return CompletableFuture.completedFuture(null);
	}
	@Async("bwAsyncExecutor")
	@Override
	public CompletableFuture<Void> deleteGroupAdmin(String enterpriseId, String groupId, String groupAdminUserId) {

		DeleteEnterpriseGroupAdmin deleteEnterpriseGroupAdmin = new DeleteEnterpriseGroupAdmin();
		deleteEnterpriseGroupAdmin.setCluster(properties.getByoscluster());
		deleteEnterpriseGroupAdmin.setEnterpriseId(enterpriseId);
		deleteEnterpriseGroupAdmin.setGroupId(groupId);
		deleteEnterpriseGroupAdmin.setAdminId(groupAdminUserId);

		DeleteEnterpriseGroupAdminResponse adminResponse = voipAdminApiSoapClient.execute(deleteEnterpriseGroupAdmin,
				DeleteEnterpriseGroupAdminResponse.class);

		AdminApiResponse response = unmarshalToResponseObj(adminResponse.getReturn());
		if (!response.getCode().equalsIgnoreCase("0")) {
			log.error("Error in enterprise admin for enterpriseId:" + enterpriseId + "groupAdminUserId:"
					+ groupAdminUserId + " Msg:" + response.getMessage());
			throw new VoIPServiceException(response.getMessage());
		}
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public String getSSOURL(String broadworksUserId) {
		GetTokenForUser forUser = new GetTokenForUser();
		forUser.setCluster(properties.getByoscluster());
		forUser.setUserId(broadworksUserId);
		GetTokenForUserResponse response = (GetTokenForUserResponse) bwAuthtokenClient.getAuthToken(forUser);
		return response.getReturn();
	}

	private AdminApiResponse unmarshalToResponseObj(String resposneString) {
		AdminApiResponse adminApiResponse = (AdminApiResponse) jaxb2Marshaller
				.unmarshal(new StringSource(resposneString));
		return adminApiResponse;
	}

	@Override
	public void updateAniUserDetails(String firstName, String lastName, String enterpriseId, String groupId, List<String> aniList) {

		ModifyUsersRequestDTO usersRequestDTO = new ModifyUsersRequestDTO();

		usersRequestDTO.setGroupId(groupId);
		usersRequestDTO.setServiceProviderId(enterpriseId);
		Users users = new Users();
		users.getUsers().addAll(aniList.stream()
				.map(ani-> createUserModifyDTO(ani, createUserModifyDetails(firstName, lastName)))
				.collect(Collectors.toList()));
		usersRequestDTO.setUsers(users);

		ModifyUsersResponse response = ssfSoapClient.modifyUsers(usersRequestDTO);
		log.info("Modify User repsonse:"+response.getReturn());

	}


	@Override
	public void updateEnterpriseAdmin(String enterpriseAdminId, String enterpriseId, String firstName,
			String lastName) {

		EditEnterpriseAdmin editEnterpriseAdmin = new EditEnterpriseAdmin();
		editEnterpriseAdmin.setAdminId(enterpriseAdminId);
		editEnterpriseAdmin.setEnterpriseId(enterpriseId);
		editEnterpriseAdmin.setNameFirst(firstName);
		editEnterpriseAdmin.setNameLast(lastName);
		
		editEnterpriseAdmin.setCluster(properties.getByoscluster());

		EditEnterpriseAdminResponse response = voipAdminApiSoapClient.execute(editEnterpriseAdmin, EditEnterpriseAdminResponse.class);
 
		log.debug("Updated enterprise admin"+response);
  
	}

	@Override
	public void updateGroupAdmin(String groupAdminId, String enterpriseId, String groupId, String firstName,
			String lastName) {

		EditEnterpriseGroupAdmin editEnterpriseGroupAdmin = new EditEnterpriseGroupAdmin();
		editEnterpriseGroupAdmin.setEnterpriseId(enterpriseId);
		editEnterpriseGroupAdmin.setGroupId(groupId);
		editEnterpriseGroupAdmin.setNameFirst(firstName);
		editEnterpriseGroupAdmin.setNameLast(lastName);
		editEnterpriseGroupAdmin.setCluster(properties.getByoscluster());
		editEnterpriseGroupAdmin.setAdminId(groupAdminId);

		EditEnterpriseGroupAdminResponse response = voipAdminApiSoapClient.execute(editEnterpriseGroupAdmin, EditEnterpriseGroupAdminResponse.class);

		log.debug("Updated Group admin:"+response.toString());

	}

	@Override
	@Async("bwAsyncExecutor")
	@Cacheable({"bwCache"})
	public CompletableFuture<List<EntGroupLines>> getAllUsersSummaryByEntGroups(String enterpriseId, List<String> groupIds) {


		EnterpriseGroupsRequestDTO requestDto = new EnterpriseGroupsRequestDTO();

		requestDto.setServiceProviderId(enterpriseId);
		requestDto.getGroupIds().addAll(groupIds);

		GetAllUsersSummaryByEnterpriseAndGroupsResponseDTO responseDTO = ssfSoapClient.getAllUsersSummaryByEnterpriseGroups(requestDto);

		GroupUsersSummaryList summaryList = responseDTO.getGroupUsersSummaryList();
		
		List<GroupUsersSummaryDTO> groupUserSummaryies =  summaryList.getGroupUsersSummaries();
		
		AllUserSummaryToLinesInfoConverter converter = new AllUserSummaryToLinesInfoConverter(enterpriseId);
		List<EntGroupLines> response = converter.convert(groupUserSummaryies);
		
		return CompletableFuture.completedFuture(response);

	}

	private UserModifyDetailsDTO createUserModifyDetails(String firstName, String lastName) {
		UserModifyDetailsDTO detailsDTO = new UserModifyDetailsDTO();
		detailsDTO.setFirstName(firstName);
		detailsDTO.setLastName(lastName);
		return detailsDTO;
	}

	private UserModifyDTO createUserModifyDTO(String ani, UserModifyDetailsDTO userDetails) { 

		UserModifyDTO modifyDTO = new UserModifyDTO();
		modifyDTO.setUserId(ani);
		modifyDTO.setUserModifyDetails(userDetails);
		return modifyDTO;
	}


}
