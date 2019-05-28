package com.windstream.voip.soap.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.ssf.allusersummary.EnterpriseGroupsRequestDTO;
import com.ssf.allusersummary.GetAllUsersSummaryByEnterpriseAndGroups;
import com.ssf.allusersummary.GetAllUsersSummaryByEnterpriseAndGroupsResponse;
import com.ssf.allusersummary.GetAllUsersSummaryByEnterpriseAndGroupsResponseDTO;
import com.ssf.getenterprisegroups.GetGroupsByEnterprise;
import com.ssf.getenterprisegroups.GetGroupsByEnterpriseResponse;
import com.ssf.getenterprisegroups.GetGroupsInEnterpriseRequestDTO;
import com.ssf.getenterprisegroups.GetGroupsInEnterpriseResponseDTO;
import com.ssf.modifyusers.ModifyUsers;
import com.ssf.modifyusers.ModifyUsersRequestDTO;
import com.ssf.modifyusers.ModifyUsersResponse;
import com.windstream.voip.config.VoIPProperties;

@Component
public class SSFSoapClient {

	@Autowired
	VoIPProperties properties;

	@Autowired
	@Qualifier("ent-groups-wst")
	WebServiceTemplate entGroupsWST;
	
	@Autowired
	@Qualifier("all-users-summary-wst")
	WebServiceTemplate allUsersSummaryWST;
	
	@Autowired
	@Qualifier("modify-users-wst")
	WebServiceTemplate modifyUsersWST;
	

	public GetGroupsInEnterpriseResponseDTO  getGroupsByEnterprise(GetGroupsInEnterpriseRequestDTO request) {
		GetGroupsByEnterprise requestPayload = new GetGroupsByEnterprise();
		requestPayload.setArg0(request);
		return ((GetGroupsByEnterpriseResponse)entGroupsWST.marshalSendAndReceive(properties.getUrl().getEnterpriseGroups(), requestPayload)).getReturn();
	}


	public ModifyUsersResponse modifyUsers(ModifyUsersRequestDTO requestPayload) { 
		
		ModifyUsers modifyUsers = new ModifyUsers();
		modifyUsers.setArg0(requestPayload);
		
		System.out.println("Modifying users"+ requestPayload);
		return (ModifyUsersResponse)modifyUsersWST.marshalSendAndReceive(properties.getUrl().getModifyUsers(), modifyUsers);
	}


	public GetAllUsersSummaryByEnterpriseAndGroupsResponseDTO getAllUsersSummaryByEnterpriseGroups(EnterpriseGroupsRequestDTO requestDto) { 

		GetAllUsersSummaryByEnterpriseAndGroups requestPayload = new GetAllUsersSummaryByEnterpriseAndGroups();
		requestPayload.setArg0(requestDto);
		
		return ((GetAllUsersSummaryByEnterpriseAndGroupsResponse)allUsersSummaryWST.marshalSendAndReceive(properties.getUrl().getAllUsersSummaryByEntGroups(), requestPayload)).getReturn();

	}

}
