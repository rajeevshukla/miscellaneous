package com.windstream.voip.model.user;

import com.windstream.voip.model.common.BWUserType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BWUserDetails  {

	private String userId;
	private Long userKey;
	private String bwUserId;
	private Integer userType;
	private String firstName;
	private String lastName;
	private Long busEntityId;
	private String enterpriseId;
	private String enterpriseName;
	private String groupId;
	private String groupName;
	private String ani;

	public boolean isEnterpriseAdmin() {
		return this.userType == BWUserType.ENTERPRISE_ADMIN.getValue();
	}

	public boolean isGroupAdmin() {
		return this.userType == BWUserType.GROUP_ADMIN.getValue();
	}

	public boolean isAniUser() {
		return this.userType == BWUserType.ANI.getValue();
	}

}
