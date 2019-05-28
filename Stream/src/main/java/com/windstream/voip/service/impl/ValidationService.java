package com.windstream.voip.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.windstream.voip.model.common.Ani;
import com.windstream.voip.model.enterprises.EnterpriseGroupsResponse;
import com.windstream.voip.model.user.BWUserDetails;
import com.windstream.voip.model.user.LinesDetailsResponse;
import com.windstream.voip.model.user.LinesDetailsResponse.AniDetails;
import com.windstream.voip.service.VoIPService;
import com.windstream.voip.util.ApplicationUtils;

@Service
public class ValidationService {


	@Autowired
	VoIPService voIPService;

	public boolean checkIfEnterpriseIsValid(String userId, String enterpriseId) { 

		boolean isValid = false;
		List<BWUserDetails> userList = voIPService.getBwUserDetailsForUserId(userId);

		if(ApplicationUtils.isValidList(userList)) { 
			isValid = userList.stream().filter(u->u.getEnterpriseId().equals(enterpriseId)).count()>0;
		}

		return isValid;
	}

	public boolean checkIfGroupIsValid(String userId, String enterpriseId, List<String> groupIds) {
		boolean isValid = false;
		EnterpriseGroupsResponse response = voIPService.getEnterpriseGroups(userId, Optional.of(enterpriseId));

		if(ApplicationUtils.isValidList(groupIds) && ApplicationUtils.isValidList(response.getGroups())) { 

			long totalMatched =  groupIds.stream().filter(groupId->{
				return response.getGroups().stream().filter(g->g.getGroupId().equals(groupId)).count()>0;
			}).count();

			if( totalMatched == groupIds.size()) {
				isValid = true;
			} 
		}
		return isValid;
	}

	public boolean doesUserExist(String userId) { 
		boolean isExist = false;
		List<BWUserDetails> userList =  voIPService.getBwUserDetailsForUserId(userId);

		if(ApplicationUtils.isValidList(userList)) {
			isExist = true;
		}
		return isExist;
	}

	public boolean checkIfAnisAreValid(String loggedInUserId, List<Ani> aniList) { 
		// Obtaining all the anis by grouping them into enterprise and group
		Map<Pair<String, String>, List<Ani>> entGroupAnis = aniList.stream().collect(Collectors.groupingBy(ani->Pair.of(ani.getEnterpriseId(), ani.getGroupId())));

		// if any of the ani is not matched then below stream would return non zero value. 
		long count = 	entGroupAnis.entrySet().stream().map(entrySet->{
			// fetch anis for groups and then validate them ..
			List<LinesDetailsResponse> lines =  voIPService.getLines(loggedInUserId, Optional.of(entrySet.getKey().getLeft()), Optional.of(entrySet.getKey().getRight()));
			if(ApplicationUtils.isValidList(lines)) {
				List<AniDetails> availableAnis = lines.get(0).getAnis();
				List<Ani> requestedAnis = entrySet.getValue();
				if(!compareAnis(requestedAnis, availableAnis)) {
					return false;
				}
				return true;
			} else {
				return false;
			}
		}).filter(isMatched -> !isMatched).count();
		// if count is not zero means invalid ani is passed.
		return count == 0; 
	}

	private boolean compareAnis(List<Ani> requestedAnis , List<AniDetails> availableAnis) {

		long totalMatched = requestedAnis.stream()
				.filter(ani-> availableAnis.stream()
						.filter(toAni-> ani.getAni().equals(toAni.getAni()))
						.count()==1)
				.count();

		return totalMatched == requestedAnis.size();
	}



}
