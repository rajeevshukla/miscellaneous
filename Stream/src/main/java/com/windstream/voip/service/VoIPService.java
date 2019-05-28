package com.windstream.voip.service;

import java.util.List;
import java.util.Optional;

import com.windstream.voip.exception.VoIPServiceException;
import com.windstream.voip.model.animgmt.AssignAniRequest;
import com.windstream.voip.model.animgmt.UnAssignAniRequest;
import com.windstream.voip.model.enterprises.EnterpriseGroupsResponse;
import com.windstream.voip.model.permission.VoIPPermissionRequest;
import com.windstream.voip.model.sso.SSORespsone;
import com.windstream.voip.model.user.BWUserDetails;
import com.windstream.voip.model.user.LinesDetailsResponse;
import com.windstream.voip.model.user.UpdateUserRequest;
import com.windstream.voip.model.user.UserRequest;
import com.windstream.voip.model.user.VoIPUserDetailsResponse;
public interface VoIPService {
	

	/**
	 * @param user
	 * @throws VoIPServiceException
	 * @since 
	 *    v1
	 */
	public void saveUser(UserRequest user) throws VoIPServiceException;
	
	/**
	 * @param user
	 * @throws VoIPServiceException
	 */
	public void updateUser(UpdateUserRequest user) throws VoIPServiceException;

	/**
	 * @param userId
	 * @throws VoIPServiceException
	 * @since 
	 *    v1
	 */
	public void deleteUser(String userId, Optional<Long> userKey) throws VoIPServiceException;
	
	/**
	 * @param userId
	 * @return
	 * @throws VoIPServiceException if there is no user available for the userId
	 * @since 
	 *    v1
	 */
	public List<BWUserDetails> getBwUserDetailsForUserId(String userId) throws VoIPServiceException;
	
	
	/**
	 * @param userId
	 * @return
	 * @throws VoIPServiceException
	 * @since 
	 *    v1
	 */
	public SSORespsone getSsoURL(String userId, Optional<Long> userKey) throws VoIPServiceException;
	
	/**
	 * Returns User's details for given busEntityId and/or userId.
	 * 
	 * @param busEntityId
	 * @throws VoIPServiceException
	 * @since 
	 *    v1
	 */
	public List<VoIPUserDetailsResponse> getUsers(Long busEntityId, Optional<String> userId) throws VoIPServiceException;
	
	/**
	 * @param userId
	 * @param enterpriseId
	 * @return
	 * @throws VoIPServiceException
	 * @since 
	 *    v1
	 */
	public EnterpriseGroupsResponse getEnterpriseGroups(String userId, Optional<String> enterpriseId) throws VoIPServiceException;
	
	
	/**
	 * @param request
	 * @throws VoIPServiceException
	 * @since 
	 *    v1
	 */
	public void saveOrUpdateVoIPPermissions(VoIPPermissionRequest request) throws VoIPServiceException;
	
	/**
	 * @param userId
	 * @param enterpriseId
	 * @param groupId
	 * @return
	 * @throws VoIPServiceException
	 * @since 
	 *    v1
	 */
	public List<LinesDetailsResponse>  getLines(String userId, Optional<String> enterpriseId, Optional<String> groupId) throws VoIPServiceException;
	
	/**
	 * @param request
	 * @throws VoIPServiceException
	 * @since 
	 *    v1
	 */
	public void assignAni(AssignAniRequest  request) throws VoIPServiceException;
	
	/**
	 * @param request
	 * @throws VoIPServiceException
	 * @since 
	 *    v1
	 */
	public void unassignAni(UnAssignAniRequest request) throws VoIPServiceException;
	
	
	/**
	 * Returns User's details for given busEntityId and/or userId.
	 * 
	 * @param busEntityId
	 * @throws VoIPServiceException
	 * @since 
	 *    v2
	 */
	public List<VoIPUserDetailsResponse> getAllVoIPAccountUsers(String userId) throws VoIPServiceException;
	
	
	/**
	 * Return userDetails for the userId
	 * @param userId
	 * @return
	 * @throws VoIPServiceException
	 * @since 
	 *   v2
	 */
	public VoIPUserDetailsResponse getUserDetails(String userId) throws VoIPServiceException;
	
}
