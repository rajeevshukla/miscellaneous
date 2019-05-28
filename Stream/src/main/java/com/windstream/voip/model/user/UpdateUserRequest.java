package com.windstream.voip.model.user;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
	
	@NotBlank(message="currentUserId must not be blank.")
	private String currentUserId;
	@NotBlank(message="newUserId must not be blank.")
	private String newUserId;
	@NotBlank(message="firstName must not be blank.")
	private String firstName;
	@NotBlank(message="lastName must not be blank.")
	private String lastName;
	
}
