package com.windstream.voip.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.windstream.voip.annotations.LogExecutionTime;
import com.windstream.voip.exception.VoIPServiceException;
import com.windstream.voip.model.animgmt.AssignAniRequest;
import com.windstream.voip.model.animgmt.UnAssignAniRequest;
import com.windstream.voip.model.enterprises.EnterpriseGroupsResponse;
import com.windstream.voip.model.permission.VoIPPermissionRequest;
import com.windstream.voip.model.sso.SSORespsone;
import com.windstream.voip.model.user.UpdateUserRequest;
import com.windstream.voip.model.user.UserRequest;
import com.windstream.voip.model.user.UserRequestV2;
import com.windstream.voip.model.user.VoIPUserDetailsResponse;
import com.windstream.voip.service.VoIPService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

@RestController
@Api(tags = { "VoIP API" }, produces = "application/json", consumes = "application/json")
public class VoIPController {

	private final static Logger logger = LoggerFactory.getLogger(VoIPController.class);

	@Autowired
	VoIPService voipService;

	@Autowired
	private Validator[] validators;

	@PostMapping("/v1/voip/adminUsers")
	public void saveUserV1(@Valid @RequestBody UserRequest user) {
		logger.info("Request recieved to save user: " + user);
		voipService.saveUser(user);
	}
	
	@PostMapping("/v2/voip/adminUsers")
	@LogExecutionTime
	public void saveUserV2(@Valid @RequestBody UserRequestV2 user) {
		logger.info("Request recieved to save user(v2): " + user);
		List<String> errorMsgs = new ArrayList<>();
		// Looping through each business EntityId one by one
		user.getBusEntityIds().forEach((busEntityId)->{
			UserRequest userRequest = new UserRequest(user.getUserId(), busEntityId, user.getFirstName(), user.getLastName());
			try {
				voipService.saveUser(userRequest);
			}catch(Exception e) {
				errorMsgs.add(e.getMessage());
				logger.error("Error in saving Users:",e);
			}
		});
		
		if(!errorMsgs.isEmpty()) {
			throw new VoIPServiceException(errorMsgs.toString());
		}
		
	}

	@DeleteMapping({"/v1/voip/users/{userId}","/v2/voip/users/{userId}"})
	public void deleteUser(@PathVariable String userId) {
		logger.info("Request recieved to delete user:{}", userId);
		voipService.deleteUser(userId, Optional.empty());
	}
	
	
	@PostMapping({"/v1/voip/users","/v2/voip/users"})
	public void updateUser(@Valid @RequestBody UpdateUserRequest request) { 
		logger.info("Request recieved to update user:{}", request);
		voipService.updateUser(request);
	}

	/**
	 *
	 * @param userId
	 * @param userKey Optional.
	 * @return
	 */
	@GetMapping(value = { "/v1/voip/ssoUrl/{userId}/{userKey}",
	"/v1/voip/ssoUrl/{userId}",
	"/v2/voip/ssoUrl/{userId}/{userKey}",
	"/v2/voip/ssoUrl/{userId}" 
	})
	public ResponseEntity<SSORespsone> getSsoURL(@PathVariable(name = "userId", required = true) String userId,
			@ApiParam(required = false) @PathVariable(required = false) Optional<Long> userKey) {
		logger.info("Request received for getSSOUrl for UserId:{}", userId);
		SSORespsone respsone = voipService.getSsoURL(userId, userKey);
		return new ResponseEntity<>(respsone, HttpStatus.OK);

	}

	@PostMapping({"/v1/voip/permissions","/v2/voip/permissions"})
	public void saveOrUpdatePermissions(@RequestBody @Valid VoIPPermissionRequest request) {
		voipService.saveOrUpdateVoIPPermissions(request);
	}


	@GetMapping(value= {"/v1/voip/enterprises/{userId}",
			"/v1/voip/enterprises/{userId}/{enterpriseId}",
			"/v2/voip/enterprises/{userId}",
			"/v2/voip/enterprises/{userId}/{enterpriseId}"})
	public ResponseEntity<EnterpriseGroupsResponse> getEnterpriseGroups(@PathVariable(required=true) String userId , @ApiParam(required=false) @PathVariable(required = false) Optional<String> enterpriseId) {
		return new ResponseEntity<EnterpriseGroupsResponse>(voipService.getEnterpriseGroups(userId, enterpriseId), HttpStatus.OK);
	}

	@GetMapping(value={"/v1/voip/users/{busEntityId}",
			"/v1/voip/users/{busEntityId}/{userId}"})
	public ResponseEntity<List<VoIPUserDetailsResponse>> getAllVoIPUsers(@PathVariable(required=true) Long busEntityId, @ApiParam(required=false) @PathVariable(required=false) Optional<String> userId) {
		return new ResponseEntity<List<VoIPUserDetailsResponse>>(voipService.getUsers(busEntityId, userId), HttpStatus.OK);
	}
	
	@GetMapping(value="/v2/voip/accountUsers/{userId}")
	public ResponseEntity<List<VoIPUserDetailsResponse>>  getAllVoIPAccountUsers(@PathVariable(required=true) String userId) {
		return new ResponseEntity<List<VoIPUserDetailsResponse>>(voipService.getAllVoIPAccountUsers(userId), HttpStatus.OK);
	}
	
	
	@GetMapping(value="/v2/voip/userDetails/{userId}")
	public ResponseEntity<VoIPUserDetailsResponse>  getUserDetailsForUserId(@PathVariable(required=true) String userId) { 
		return new ResponseEntity<VoIPUserDetailsResponse>(voipService.getUserDetails(userId), HttpStatus.OK);
	}
	

	@GetMapping(value= {"/v1/voip/lines/{userId}",
			"/v1/voip/lines/{userId}/{enterpriseId}/{groupId}",
			"/v1/voip/lines/{userId}/{enterpriseId}",
			
			"/v2/voip/lines/{userId}",
			"/v2/voip/lines/{userId}/{enterpriseId}/{groupId}",
			"/v2/voip/lines/{userId}/{enterpriseId}"
	
	})
	public ResponseEntity<Object>  getAllLines(@PathVariable String userId,@ApiParam(required=false)@PathVariable(required=false) Optional<String> enterpriseId, @ApiParam(required=false)@PathVariable(required=false) Optional<String> groupId) {
		return new ResponseEntity<Object>( voipService.getLines(userId, enterpriseId, groupId), HttpStatus.OK);
	}


	@PostMapping({"/v1/voip/assign","/v2/voip/assign"})
	public void assingAni(@RequestBody @Valid AssignAniRequest request) {
		voipService.assignAni(request);
	}

	@PostMapping({"/v1/voip/unassign","/v2/voip/unassign"})
	public void unassignAni(@RequestBody @Valid UnAssignAniRequest request) {
		voipService.unassignAni(request);
	}
	
	
	/** Adding all custom validators into WebDataBinder context */	
	@InitBinder
	public void setUpValidators(WebDataBinder webDataBinder) {
		for (Validator validator : validators) {
			if (validator != null && webDataBinder.getTarget() !=null && validator.supports(webDataBinder.getTarget().getClass())
					&& validator.getClass().getName().contains("com.windstream.voip.validators"))
				webDataBinder.addValidators(validator);
		}
	}
}
