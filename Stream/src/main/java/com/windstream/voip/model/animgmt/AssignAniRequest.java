package com.windstream.voip.model.animgmt;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.windstream.voip.model.common.Ani;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AssignAniRequest {
	
	@NotBlank(message="loggedInUserId must not be blank.")
	private String loggedInUserId;
	@NotBlank(message="targetUserId must not be blank.")
	private String targetUserId;
	@NotBlank(message="First name must not be blank.")
	private String targetUserFirstName;
	@NotBlank(message="Last name must not be blank.")
	private String targetUserLastName;
	private List<Ani> anis;
	
}
