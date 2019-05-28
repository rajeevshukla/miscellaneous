package com.windstream.voip.model.user;

import java.util.List;

import com.windstream.voip.model.common.AccessLevel;
import com.windstream.voip.model.common.Ani;
import com.windstream.voip.model.common.Enterprise;
import com.windstream.voip.model.common.Group;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VoIPUserDetailsResponse {

	private String userId;
	private AccessControls accessControls;

	@Data
	@ToString
	public static class AccessControls {
		private AccessLevel accessLevel;
		private List<Enterprise> enterprises;
		private List<Group> groups;
		private List<Ani> assignedANIs;
	}

}
