package com.windstream.voip.model.user;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRequestV2 {

	@NotBlank(message = "User Id must not be blank.")
	@Size(min = 1, max = 100, message = "userId longer than 100 chars is not supported.")
	private String userId;

	@NotNull(message = "Busniess entityId must not be blank.")
	@Valid
	private List<Long> busEntityIds;

	@NotBlank(message = "firstName must not be blank.")
	@Size(min = 1, max = 100, message = "firstName longer than 100 chars is not supported.")
	private String firstName;

	@NotBlank(message = "Last name must not be blank.")
	@Size(min = 1, max = 100, message = "lastName longer than 100 chars is not supported.")
	private String lastName;

}
