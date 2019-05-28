package com.windstream.voip.validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.windstream.voip.model.permission.VoIPPermissionRequest;
import com.windstream.voip.model.permission.VoIPPermissionRequest.Group;
import com.windstream.voip.service.impl.ValidationService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SavePermisisonRequestValidator implements Validator {


	@Autowired
	ValidationService validationService;

	@Override
	public boolean supports(Class<?> clazz) {
		return VoIPPermissionRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
      log.info("Save Permission validator is called");   
		VoIPPermissionRequest  request = VoIPPermissionRequest.class.cast(target);

		String loggedInUserId = request.getLoggedUserId();

		List<String> enterprises = request.getEnterprises();
		List<Group> groups = request.getGroups();

		Set<String> distinctEnterprises = new HashSet<>();


		if(enterprises != null ) {
			distinctEnterprises.addAll(enterprises.stream().collect(Collectors.toSet()));
		}
		if(groups != null) { 
			distinctEnterprises.addAll(groups.stream()
					.filter(g-> !distinctEnterprises.contains(g.getEnterpriseId()))
					.map(g->g.getEnterpriseId())
					.collect(Collectors.toSet()));
		}

		if(distinctEnterprises.isEmpty()) { 
			errors.rejectValue("enterprises", "groups.groupId", "Enterprises and groups both can't be empty. Select at least one.");
		} else { 

			List<String> rejectedEnterpriseIds = validateEnterprises(loggedInUserId, distinctEnterprises); 
			List<Map.Entry<String, List<String>>> rejectedGroupIds = validateGroups(loggedInUserId,
					groups);

			if(rejectedEnterpriseIds != null && !rejectedEnterpriseIds.isEmpty()) {
				errors.rejectValue("enterprises", "enterprises", "Enterprise Ids" + rejectedEnterpriseIds+" are not valid for userId:"+loggedInUserId);
			}

			if(!rejectedGroupIds.isEmpty()) {
				errors.rejectValue("groups", "groups.groupId", rejectedGroupIds +" Groups are not valid for userId:"+loggedInUserId);
			}
		}

	}

	/**
	 * Validate enterpriseIds 
	 * 
	 * @param loggedInUserId
	 * @param distinctEnterprises
	 * @return rejected EnterpriseIds
	 */
	private List<String> validateEnterprises(String loggedInUserId, Set<String> distinctEnterprises) {
		List<String> rejectedEnterpriseIds = new ArrayList<>();

		distinctEnterprises.forEach(enterpriseId-> { 
			if(!validationService.checkIfEnterpriseIsValid(loggedInUserId, enterpriseId)) {
				rejectedEnterpriseIds.add(enterpriseId);
			}
		});
		return rejectedEnterpriseIds;
	}

	/**
	 * Validate groups against loggedInUserId. 
	 * 
	 * @param loggedInUserId
	 * @param groups
	 * @return rejected group Ids which are not valid for given loggedInUserId
	 */
	private List<Map.Entry<String, List<String>>> validateGroups(String loggedInUserId,
			List<Group> groups) {
		Map<String, List<String>> enterpriseIdGroupIdListMap =	getEnterpriseGroupListMap(groups);
		List<Map.Entry<String, List<String>>> rejectedGroupIds = enterpriseIdGroupIdListMap.entrySet().stream().map(entrySet->{ 

			if(!validationService.checkIfGroupIsValid(loggedInUserId, entrySet.getKey(), entrySet.getValue())) {
				return entrySet;
			}
			return null;

		}).filter(group -> group!= null).collect(Collectors.toList());
		return rejectedGroupIds;
	}


	/**
	 * Return enterprise groupList pair for list of all groups.  
	 * 
	 * @param groups
	 * @return map of enterpriseId groupList. 
	 */
	private Map<String, List<String>> getEnterpriseGroupListMap(List<Group> groups) {
		Map<String, List<String>> enterpriseIdGroupListMap = new HashMap<>();

		if(groups!=null && !groups.isEmpty()) { 
			groups.forEach(group->{ 
				if(!enterpriseIdGroupListMap.containsKey(group.getGroupId())) {
					enterpriseIdGroupListMap.put(group.getEnterpriseId(), new ArrayList<>());
				}
				enterpriseIdGroupListMap.get(group.getEnterpriseId()).add(group.getGroupId());
			});
		}
		return enterpriseIdGroupListMap;
	}

}
