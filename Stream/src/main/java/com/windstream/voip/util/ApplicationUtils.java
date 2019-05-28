package com.windstream.voip.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.windstream.voip.exception.UserNotFoundException;
import com.windstream.voip.model.common.AccessLevel;
import com.windstream.voip.model.common.Ani;
import com.windstream.voip.model.common.BWUserType;
import com.windstream.voip.model.common.Enterprise;
import com.windstream.voip.model.common.Group;
import com.windstream.voip.model.sso.SSORespsone.Entity;
import com.windstream.voip.model.user.BWUserDetails;

public class ApplicationUtils {

	public static boolean isEmpty(String string) {
		if (string != null && !string.trim().isEmpty())
			return false;
		return true;
	}

	/**
	 * @param broadworksUsers
	 * @return {@link BWUserDetails} having highest VoIP Permission
	 */
	public static BWUserDetails getHighestPermUserObject(List<BWUserDetails> broadworksUsers) {

		BWUserDetails userDetails = null;
		if (broadworksUsers != null && !broadworksUsers.isEmpty()) {
			//setting max userType as first object hence sorting in reversed order.
			userDetails = broadworksUsers.stream().sorted(Comparator.comparing(BWUserDetails::getUserType).reversed())
					.findFirst().get();
		}
		return userDetails;
	}

	/**
	 * It will used in Url from client like /userId/bwUserId
	 * 
	 * @param users    - List Of Users
	 * @param userType BWUserType
	 * @return listOf Entity having key and display value.
	 */
	public static List<Entity> getEntityListForSSOResponse(List<BWUserDetails> users, BWUserType userType) {
		List<Entity> entityList = null;
		if (users != null && !users.isEmpty()) {

			entityList = users.stream().filter((user) -> user.getUserType() == userType.getValue()).map((u) -> {
				if (isEnterpriseAdmin(userType)) {
					return new Entity(u.getUserKey(), u.getEnterpriseName());
				} else if (isGroupAdmin(userType)) {
					return new Entity(u.getUserKey(), u.getGroupId());
				} else {
					return new Entity(u.getUserKey(), u.getAni());
				}
			}).collect(Collectors.toList());
		}
		return entityList;
	}

	public static BWUserDetails getBwUserDetialsForBwUserId(List<BWUserDetails> bwUserList, Long userKey)
			throws UserNotFoundException {
		return bwUserList.stream().filter(user -> user.getUserKey().equals(userKey)).findFirst()
				.orElseThrow(() -> new UserNotFoundException("userKey:" + userKey + " does not exist"));
	}

	public static boolean isEnterpriseAdmin(BWUserType userType) {
		return userType.getValue() == BWUserType.ENTERPRISE_ADMIN.getValue();
	}

	public static boolean isGroupAdmin(BWUserType userType) {
		return userType.getValue() == BWUserType.GROUP_ADMIN.getValue();
	}

	public static boolean isAni(BWUserType userType) {
		return userType.getValue() == BWUserType.ANI.getValue();
	}

	public static AccessLevel getHighestAccessLevel(List<BWUserDetails> userList) {

		BWUserDetails userHavingHighestAccess = getHighestPermUserObject(userList);
		return Arrays.stream(AccessLevel.values())
				.filter(acessLevel -> acessLevel.getAccessLevel() == userHavingHighestAccess.getUserType())
				.findFirst()
				.get();

	}
	
	public static List<Ani> extractAnis(List<BWUserDetails> userList) {

		return userList.stream().filter(u -> u.isAniUser())
				.map(u -> new Ani(u.getAni(), u.getGroupId(), u.getEnterpriseId()))
				.collect(Collectors.toList());
	}

	public static List<Group> extractGroups(List<BWUserDetails> userList) {

		return userList.stream().filter(u -> u.isGroupAdmin())
				.map(u -> new Group(u.getEnterpriseId(), u.getGroupId(), u.getGroupName()))
				.collect(Collectors.toList());
	}
	
	public static List<Enterprise> extractEnterprises(List<BWUserDetails> userList) { 
		
		return userList.stream().filter(u-> u.isEnterpriseAdmin())
				.map(u->new Enterprise(u.getEnterpriseId(), u.getEnterpriseName())).collect(Collectors.toList());
	}

	
	public static boolean isValidList(List<?> list) { 
		return list != null && !list.isEmpty();
	}
	
	
	/**
	 * Lambda function to return predicate to create distinct elements
	 * @param keyExtractor
	 * @return
	 */
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Set<Object> seen = ConcurrentHashMap.newKeySet();
	    return t -> seen.add(keyExtractor.apply(t));
	}
	
	
	public static Optional<BWUserDetails> getBusEntityIdForEnterpriseId(List<BWUserDetails> userList, String enterpriseId) {
		return userList.stream().filter(user->user.getEnterpriseId().equals(enterpriseId)).findFirst();
	}
	
	public static List<Long> getDistinctBusEntityList(List<BWUserDetails> userrList) {
		
		return userrList.stream()
		.filter(distinctByKey(BWUserDetails::getBusEntityId))
		.map(user->user.getBusEntityId())
		.collect(Collectors.toList());
		
	}
}
