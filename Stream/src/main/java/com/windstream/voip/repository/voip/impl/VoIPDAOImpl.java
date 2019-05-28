package com.windstream.voip.repository.voip.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.windstream.voip.model.common.BWUserType;
import com.windstream.voip.model.user.BWUserDetails;
import com.windstream.voip.model.user.UpdateUserRequest;
import com.windstream.voip.model.user.UserRequest;
import com.windstream.voip.repository.voip.VoIPDAO;
import com.windstream.voip.util.ApplicationUtils;

@Repository
public class VoIPDAOImpl implements VoIPDAO {

	@Autowired
	@Qualifier("voip-jdbc-template")
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<String> getAvailableUnAssignedEnterpriseIds(String userId, Long businessEntityId) throws Exception {

		String getEnterpriseIdSQL = "SELECT beem.enterprise_id " + 
				"FROM   voip.bus_entity_ent_map beem " + 
				"       LEFT JOIN broadworks_users bu " + 
				"              ON beem.bus_entity_id = bu.bus_entity_id " + 
				"                 AND beem.enterprise_id = bu.enterprise_id " + 
				"                 AND bu.user_id = ? " + 
				"WHERE  beem.bus_entity_id = ? " + 
				"       AND beem.is_active = ? " + 
				"       AND bu.user_id IS NULL ";
		List<String> entityIdList = jdbcTemplate.queryForList(getEnterpriseIdSQL, new Object[] { userId, businessEntityId, Boolean.TRUE},
				String.class);
		return entityIdList;

	}

	@Override
	public boolean checkIfBusEntityIdPresent(Long busEntityId) throws Exception {
		String getEnterpriseIdSQL = "select count(*) from voip.BUS_ENTITY_ACT_MAP where BUS_ENTITY_ID=?";
		Integer count = jdbcTemplate.queryForObject(getEnterpriseIdSQL, new Object[] { busEntityId},
				Integer.class);
		return count>0;
	}

	@Override
	public void saveUser(String enterpriseAdminId, String enterpriseId, String groupId, String ani, BWUserType userType, UserRequest request)
			throws Exception {

		String insertQuery = "insert into BROADWORKS_USERS "
				+ "(USER_ID, BW_USER_ID, FIRST_NAME, LAST_NAME, BUS_ENTITY_ID, USER_TYPE, ENTERPRISE_ID, GROUP_ID, ANI)"
				+ "values" + "(?,?,?,?,?,?,?,?,?)";

		jdbcTemplate.update(insertQuery, request.getUserId(), enterpriseAdminId, request.getFirstName(),
				request.getLastName(), request.getBusEntityId(), userType.getValue(), enterpriseId, groupId, ani);

	}

	@Override
	public void updateUser(UpdateUserRequest request) throws Exception {
		String updateQuery = "update BROADWORKS_USERS set first_name=? , last_name=?, user_id=? where user_id=?";
		jdbcTemplate.update(updateQuery, request.getFirstName(),request.getLastName(), request.getNewUserId(),request.getCurrentUserId());
	}

	@Override
	public void deleteUser(String userId, Optional<Long> userKey) throws Exception {

		StringBuilder deleteQuery = new StringBuilder( "delete from BROADWORKS_USERS where user_id=? ");
		if(userKey.isPresent()) {
			deleteQuery.append(" and user_key=?");
			jdbcTemplate.update(deleteQuery.toString(), userId, userKey.get());
		} else {
			jdbcTemplate.update(deleteQuery.toString(), userId);
		}
	}

	@Override
	public List<BWUserDetails> getBwUsersForUserId(String userId) throws Exception {

		String getUsers = "select bu.* , be.enterprise_name, bg.group_name from voip.BROADWORKS_USERS bu left join BW_ENTERPRISES be on be.enterprise_id = bu.enterprise_id"
				+ " left join BW_GROUPS bg on bg.enterprise_id=bu.enterprise_id and bg.group_id=bu.group_id where user_id=?";
		List<BWUserDetails> userList = jdbcTemplate.query(getUsers, new Object[] { userId }, (rs, rowNo) -> {
			BWUserDetails bwUsers = new BWUserDetails();
			bwUsers.setFirstName(rs.getString("first_name"));
			bwUsers.setLastName(rs.getString("last_name"));
			bwUsers.setBwUserId(rs.getString("bw_user_id"));
			bwUsers.setUserType(rs.getInt("user_type"));
			bwUsers.setEnterpriseId(rs.getString("enterprise_id"));
			bwUsers.setGroupId(rs.getString("group_id"));
			bwUsers.setAni(rs.getString("ani"));
			bwUsers.setBusEntityId(rs.getLong("bus_entity_id"));
			bwUsers.setUserKey(rs.getLong("user_key"));
			bwUsers.setUserId(rs.getString("user_id"));
			//if enterprise_name is blank then consider enterprise Id as name(same holds true for group)
			bwUsers.setEnterpriseName(ApplicationUtils.isEmpty(rs.getString("enterprise_name"))? rs.getString("enterprise_id"): rs.getString("enterprise_name"));
			bwUsers.setGroupName(ApplicationUtils.isEmpty(rs.getString("group_name"))? rs.getString("group_id"): rs.getString("group_name"));
			return bwUsers;
		});
		return userList;
	}

	@Override
	public void saveGlobalActIdsForBusEntityId(List<Long> globalActIds, Long busEntityId) throws Exception {
		String insertQuery ="insert into voip.BUS_ENTITY_ACT_MAP(bus_entity_id, global_account_id) values(?,?)";
		jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, busEntityId);
				ps.setLong(2, globalActIds.get(i));
			}

			@Override
			public int getBatchSize() {
				return globalActIds.size();
			}
		});
	}
	@Override
	public boolean fetchAndSaveEnterpriseId(Long busEntityId) throws Exception {

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
				.withSchemaName("voip")
				.withCatalogName("PK_VOIP")
				.withProcedureName("FETCH_AND_INSERT_ENT_ID")
				.declareParameters(
						new SqlParameter("p_bus_entity_id", Types.NUMERIC),
						new SqlOutParameter("x_out", Types.INTEGER)			
						);
		SqlParameterSource param = new MapSqlParameterSource().addValue("p_bus_entity_id", busEntityId);
		Map<String, Object> output = call.execute(param);
		Integer vReturn = (Integer)output.get("x_out");
		// 1 means enterprise fetched successfully/ 0 means not found. 
		return vReturn.intValue()==1;


	}

	@Override
	public void deleteInvalidBwUsers(String userId, Long busEntityId) throws Exception {

		String deleteQuery = "DELETE FROM broadworks_users " + 
				"WHERE  rowid IN (SELECT bu.rowid " + 
				"                 FROM   broadworks_users bu " + 
				"                        JOIN bus_entity_ent_map beem " + 
				"                          ON beem.bus_entity_id = bu.bus_entity_id " + 
				"                             AND beem.enterprise_id = bu.enterprise_id " + 
				"                 WHERE  bu.bus_entity_id = ? " + 
				"                        AND beem.is_active = ? " + 
				"                        AND bu.user_id = ?)";
		jdbcTemplate.update(deleteQuery, busEntityId, Boolean.FALSE, userId);
	}

	@Override
	public List<BWUserDetails> getAllUsers(Long busEnitityId) throws Exception {
		String getUsers = "select bu.* , be.enterprise_name, bg.group_name from voip.BROADWORKS_USERS bu left join BW_ENTERPRISES be on be.enterprise_id = bu.enterprise_id"
				+ " left join BW_GROUPS bg on bg.enterprise_id=bu.enterprise_id and bg.group_id=bu.group_id where bu.bus_entity_id=?";
		List<BWUserDetails> userList = jdbcTemplate.query(getUsers, new Object[] { busEnitityId }, (rs, rowNo) -> {
			BWUserDetails bwUsers = new BWUserDetails();
			bwUsers.setFirstName(rs.getString("first_name"));
			bwUsers.setLastName(rs.getString("last_name"));
			bwUsers.setBwUserId(rs.getString("bw_user_id"));
			bwUsers.setUserType(rs.getInt("user_type"));
			bwUsers.setEnterpriseId(rs.getString("enterprise_id"));
			bwUsers.setGroupId(rs.getString("group_id"));
			bwUsers.setAni(rs.getString("ani"));
			bwUsers.setBusEntityId(rs.getLong("bus_entity_id"));
			bwUsers.setUserKey(rs.getLong("user_key"));
			bwUsers.setUserId(rs.getString("user_id"));
			//if enterprise_name is blank then consider enterprise Id as name
			bwUsers.setEnterpriseName(ApplicationUtils.isEmpty(rs.getString("enterprise_name"))? rs.getString("enterprise_id"): rs.getString("enterprise_name"));
			bwUsers.setGroupName(ApplicationUtils.isEmpty(rs.getString("group_name"))? rs.getString("group_id"): rs.getString("group_name"));
			return bwUsers;
		});
		return userList;
	}


	@Override
	public boolean checkIfAnisIsAlreadyAssigned(List<String> anis) throws Exception {

		String getUsers = "select count(*) as count from voip.broadworks_users where bw_user_id in (?)";
		Integer count = jdbcTemplate.query(getUsers, anis.toArray(), (rs)->{
			if(rs.next()) {
			return rs.getInt(1);
			} else {
				return 0;
			}
		});
		return  count.intValue()>0;
	}
}
