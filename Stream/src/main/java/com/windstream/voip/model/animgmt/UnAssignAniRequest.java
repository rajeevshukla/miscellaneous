package com.windstream.voip.model.animgmt;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.windstream.voip.model.common.Ani;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UnAssignAniRequest {

	@NotBlank(message="loggedInUserId must not be blank.")
	private String loggedInUserId;
	@NotBlank(message="targetUserId must not be blank.")
	private String targetUserId;
	
	private List<Ani> anis;
}
