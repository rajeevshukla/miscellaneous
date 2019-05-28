package com.windstream.voip.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;

import com.windstream.voip.model.common.AccessLevel;
import com.windstream.voip.model.common.Group;
import com.windstream.voip.model.user.BWUserDetails;
import com.windstream.voip.model.user.VoIPUserDetailsResponse;
import com.windstream.voip.util.ApplicationUtils;

public class BwUsersToVoIPUserDetailsResponseConverter
		implements Converter<List<BWUserDetails>, List<VoIPUserDetailsResponse>> {

	public List<VoIPUserDetailsResponse> convert(java.util.List<BWUserDetails> source) {

		Map<String, List<BWUserDetails>> userIdUserListMap = source.stream()
				.collect(Collectors.groupingBy(BWUserDetails::getUserId));

		List<VoIPUserDetailsResponse> userDetailsResponseList = new ArrayList<>();
		userIdUserListMap.forEach((userId, userList) -> {

			VoIPUserDetailsResponse userDetailsResponse = new VoIPUserDetailsResponse();
			userDetailsResponse.setUserId(userId);

			VoIPUserDetailsResponse.AccessControls accessControl = new VoIPUserDetailsResponse.AccessControls();
			accessControl.setAssignedANIs(ApplicationUtils.extractAnis(userList));
		   List<Group> groups = ApplicationUtils.extractGroups(userList);
			accessControl.setGroups(groups);
			accessControl.setEnterprises(ApplicationUtils.extractEnterprises(userList));
			AccessLevel accessLevel = ApplicationUtils.getHighestAccessLevel(userList);
			if(accessLevel.equals(AccessLevel.ENTERPRISE) && !groups.isEmpty()) {
				//setting MULTIPLE Explicitly 
				accessControl.setAccessLevel(AccessLevel.MULTIPLE);
			} else {
				accessControl.setAccessLevel(accessLevel);
			}
			
			userDetailsResponse.setAccessControls(accessControl);
			userDetailsResponseList.add(userDetailsResponse);

		});

		return userDetailsResponseList;
	};

	

}
