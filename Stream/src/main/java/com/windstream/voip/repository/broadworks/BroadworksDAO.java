package com.windstream.voip.repository.broadworks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.windstream.voip.model.common.EntGroupLines;
import com.windstream.voip.model.common.Group;

public interface BroadworksDAO {

	public CompletableFuture<List<Group>> getGroupsForEnterprise(String enterpriseId);

	/**
	 * Creates enterrpise admin user for given enterprise.
	 * 
	 * @return String - Enterprise Admin userId in broadworks
	 */
	public CompletableFuture<String> createEnterpriseAdmin(String enterpriseId, String firstName, String lasName);

	/**
	 * Creates group admin user for given enterprise and group.
	 * 
	 * @return String - Group admin userId in broadworks
	 */
	public CompletableFuture<String> createGroupAdmin(String enterpriseId, String groupId, String firstName, String lastName);

	/**
	 * @param enterpriseId
	 * @param enterpriseaAdminUserId
	 */
	public CompletableFuture<Void> deleteEnterpriseAdmin(String enterpriseId, String enterpriseaAdminUserId);

	/**
	 * 
	 * @param enterpriseId
	 * @param groupId
	 * @param groupAdminUserId
	 */
	public CompletableFuture<Void> deleteGroupAdmin(String enterpriseId, String groupId, String groupAdminUserId);
	
	
	/**
	 * To get The broadworks native portal SSO URL. 
	 * @param broadworksUserId
	 * @return
	 */
	public String getSSOURL(String broadworksUserId);
	
	
	
	/**
	 * @param firstName
	 * @param lastName
	 * @param enterpriseId
	 * @param groupId
	 * @param ani
	 */
	public void updateAniUserDetails(String firstName, String lastName, String enterpriseId, String groupId, List<String> ani);
	
	
	/**
	 * @param enterpirseAdminId
	 * @param enterpriseId
	 * @param firstName
	 * @param lastName
	 */
	public void updateEnterpriseAdmin(String enterpirseAdminId, String enterpriseId, String firstName, String lastName);
	
	
	/**
	 * @param groupAdminId
	 * @param enterpriseId
	 * @param groupId
	 * @param firstName
	 * @param lastName
	 */
	public void updateGroupAdmin(String groupAdminId, String enterpriseId, String groupId, String firstName, String lastName);
	
	
	/**
	 * @param enterpriseId
	 * @param groupId
	 */
	public CompletableFuture<List<EntGroupLines>> getAllUsersSummaryByEntGroups(String enterpriseId, List<String> groupId);
	

}
