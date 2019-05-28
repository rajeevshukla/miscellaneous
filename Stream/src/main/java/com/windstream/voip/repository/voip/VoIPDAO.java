package com.windstream.voip.repository.voip;

import java.util.List;
import java.util.Optional;

import com.windstream.voip.model.common.BWUserType;
import com.windstream.voip.model.user.BWUserDetails;
import com.windstream.voip.model.user.UpdateUserRequest;
import com.windstream.voip.model.user.UserRequest;

public interface VoIPDAO {

	/**
	 * Get list of available valid enterpriseIds that will be used to create admin users for given busEntityId and userId. 
	 * 
	 * @param businessEntityId
	 * @return
	 * @throws Exception
	 * @since  v1
	 */
	public List<String> getAvailableUnAssignedEnterpriseIds(String userId, Long businessEntityId) throws Exception;
	
	
	/**
	 * Checks whether given business entity Id is present in db or not
	 * @param busEntityId
	 * @return
	 * @throws Exception
	 * @since  v1
	 */
	public boolean checkIfBusEntityIdPresent(Long busEntityId) throws Exception;
	

	/**
	 * @param bwAdminUserId
	 * @param enterpriseId
	 * @param groupId
	 * @param request
	 * @throws Exception
	 * @since  v1
	 */
	public void saveUser(String bwAdminUserId, String enterpriseId, String groupId, String ani, BWUserType userType, UserRequest request)
			throws Exception;

	/**
	 * @param request
	 * @throws Exception
	 * @since  v1
	 */
	public void updateUser(UpdateUserRequest request) throws Exception;

	/**
	 * @param userId
	 * @throws Exception
	 * @since  v1
	 */
	public void deleteUser(String userId, Optional<Long> userKey) throws Exception;

	/**
	 * @param userId
	 * @return
	 * @throws Exception
	 * @since  v1
	 */
	public List<BWUserDetails> getBwUsersForUserId(String userId) throws Exception;
	
	/**
	 * @param globalActIds
	 * @param busEntityId
	 * @throws Exception
	 * @since  v1
	 */
	public void saveGlobalActIdsForBusEntityId(List<Long> globalActIds, Long busEntityId) throws Exception;
	
	/**
	 * @param busEntityId
	 * @return
	 * @throws Exception
	 * @since  v1
	 */
	public boolean fetchAndSaveEnterpriseId(Long busEntityId) throws Exception;
	
	/**
	 * @param userId
	 * @param busEntityId
	 * @throws Exception
	 * @since 	v1
	 */
	public void deleteInvalidBwUsers(String userId, Long busEntityId) throws Exception;
	
	
	/**
	 * @param busEntityId
	 * @throws Exception
	 * @since 	v1
	 */
	public List<BWUserDetails> getAllUsers(Long busEntityId) throws Exception ;
	
   
	/**
	 * @param anis
	 * @return
	 * @throws Exception
	 * @since v1
	 */
	public boolean checkIfAnisIsAlreadyAssigned(List<String> anis) throws Exception;
	
}
