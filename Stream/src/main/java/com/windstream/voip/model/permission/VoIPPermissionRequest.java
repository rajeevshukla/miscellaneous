package com.windstream.voip.model.permission;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VoIPPermissionRequest {
	
	@NotBlank(message="Selected UserId must not be blank.")
	private String selectedUserId;
	@NotBlank(message="LoggedIn UserId must not be blank.")
	private String loggedUserId;
	@NotBlank(message="First Name must not be blank.")
	private String firstName;
	@NotBlank(message="Last Name must not be blank.")
	private String lastName;
	
	private List<String> enterprises;
	
	private List<Group> groups;
	

	public List<String> getEnterprises() { 
		if(enterprises == null) 
			enterprises = new ArrayList<>();
		return enterprises;
	}
	
	public List<Group> getGroups() { 
		if(groups == null) 
			groups = new ArrayList<>();
		return groups;
	}
	
	
	@Data
	@ToString
	public static class Group { 
		private String groupId;
		private String enterpriseId;
	}
}


