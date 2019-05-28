package com.windstream.voip.model.enterprises;

import java.util.ArrayList;
import java.util.List;

import com.windstream.voip.model.common.Enterprise;
import com.windstream.voip.model.common.Group;

import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class EnterpriseGroupsResponse {
	
	private List<Enterprise> enterprises;
	private List<Group> groups;

	public List<Group> getGroups() {
		if(groups == null)
			  groups = new ArrayList<>();
		return groups;
	}
	
	public List<Enterprise> getEnterprises() {
		 if(enterprises == null)
			 enterprises = new ArrayList<>();
		return enterprises;
	}

}
