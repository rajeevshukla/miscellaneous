package com.windstream.voip.model.user;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

	@NotBlank(message = "User Id must not be blank.")
	@Size(min = 1, max = 100, message = "userId longer than 100 chars is not supported.")
	private String userId;

	@NotNull(message = "Busniess entityId must not be blank.")
	@Min(value = 1, message = "busEntityId can not be 0 or less.")
	private Long busEntityId;

	@NotBlank(message = "firstName must not be blank.")
	@Size(min = 1, max = 100, message = "firstName longer than 100 chars is not supported.")
	private String firstName;

	@NotBlank(message = "Last name must not be blank.")
	@Size(min = 1, max = 100, message = "lastName longer than 100 chars is not supported.")
	private String lastName;
	
}
