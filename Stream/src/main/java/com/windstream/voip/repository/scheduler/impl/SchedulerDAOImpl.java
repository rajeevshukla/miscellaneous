package com.windstream.voip.repository.scheduler.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.windstream.voip.model.common.Enterprise;
import com.windstream.voip.model.common.Group;
import com.windstream.voip.repository.scheduler.SchedulerDAO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SchedulerDAOImpl  implements SchedulerDAO{

	@Autowired
	@Qualifier("bw-jdbc-template")
	private JdbcTemplate bwJdbcTemplate;

	@Autowired
	@Qualifier("voip-jdbc-template")
	private JdbcTemplate voipJdbcTemplate;
	
	
	public void fetchAndSaveEnterpriseIdNameFromBW()  {
		final String query = "SELECT DISTINCT T1.svc_provider_id as enterprise_id, " + 
				"                CASE " + 
				"                  WHEN T2.noofrecords = 1 THEN T1.name " + 
				"                  WHEN T2.noofrecords > 1 THEN T1.svc_provider_id " + 
				"                end AS enterprise_name " + 
				"FROM   voice_db.BWAS_SVC_PROVIDER T1 " + 
				"       JOIN (SELECT svc_provider_id, " + 
				"                    Count(*) AS noOfRecords " + 
				"             FROM   voice_db.BWAS_SVC_PROVIDER " + 
				"             GROUP  BY BINARY svc_provider_id) T2 " + 
				"         ON T1.SVC_PROVIDER_ID= T2.SVC_PROVIDER_ID  AND BINARY T1.SVC_PROVIDER_ID= T2.SVC_PROVIDER_ID";
		log.info("Fetching enterprise Id name from broadworks db.");
		PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(query);
		pscf.setResultSetType(ResultSet.TYPE_FORWARD_ONLY);
		PreparedStatementCreator psc = pscf.newPreparedStatementCreator(Collections.emptyList());
		bwJdbcTemplate.setFetchSize(10000);
		truncateTable("BW_ENTERPRISES_TEMP");
		bwJdbcTemplate.execute(psc,	(ps)-> {						
						List<Enterprise> enterprises = new ArrayList<>();
						ResultSet resultSet = ps.executeQuery();

						while (resultSet.next()) {
							enterprises.add(
									new Enterprise(resultSet.getString("enterprise_id"), resultSet.getString("enterprise_name")));
							if (enterprises.size() == 10000) {
								insertEnterpriseIdNameInDB(enterprises);
								enterprises.clear();
							}
						}
						if(!enterprises.isEmpty()) {
							insertEnterpriseIdNameInDB(enterprises);
							enterprises.clear();
						}
						return null; 
				});
	}
	
	public void fetchAndSaveGroupIdNameFromBW() { 
		String query="SELECT e.svc_provider_id as enterprise_id, " + 
				"       g.cust_group_id as group_id, " + 
				"       g.NAME as group_name " + 
				"FROM   voice_db.BWAS_SVC_PROVIDER e " + 
				"       LEFT JOIN voice_db.BWAS_CUST_GROUP g " + 
				"              ON  g.svc_provider_uid = e.svc_provider_uid " + 
				"WHERE  g.cust_group_id IS NOT NULL "; 
	
		log.info("Fetching Group Id name from broadworks db.");
		PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(query);
		pscf.setResultSetType(ResultSet.TYPE_FORWARD_ONLY);
		PreparedStatementCreator psc = pscf.newPreparedStatementCreator(Collections.emptyList());
		bwJdbcTemplate.setFetchSize(10000);
		truncateTable("BW_GROUPS_TEMP");
		bwJdbcTemplate.execute(psc,	(ps)-> {						
						List<Group> groups = new ArrayList<>();
						ResultSet resultSet = ps.executeQuery();

						while (resultSet.next()) {
							groups.add(
									new Group(resultSet.getString("enterprise_id"),resultSet.getString("group_id"), resultSet.getString("group_name")));
							if (groups.size() == 10000) {
								insertGroupIdNameInDB(groups);
								groups.clear();
							}
						}
						if(!groups.isEmpty()) {
							insertGroupIdNameInDB(groups);
							groups.clear();
						}
						return null; 
				});
		
		
		
	}
	
	
	
	

	private void truncateTable(String tableName) {
		String truncateQuery = "truncate table " + tableName;
		log.info("Truncating {} table", tableName);
		voipJdbcTemplate.execute(truncateQuery);

	}
	
	private void insertEnterpriseIdNameInDB(List<Enterprise> enterpriseList) {
		final String insertQuery = "INSERT INTO BW_ENTERPRISES_TEMP(ENTERPRISE_ID, ENTERPRISE_NAME) values(?,?)";
		final int batchSize = 1000;
		List<List<Enterprise>> batchList = Lists.partition(enterpriseList, batchSize);
		log.info("Total batch job set found:{}", batchList.size());
		
		batchList.forEach((employeesBatch)->{
			voipJdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					ps.setString(1, employeesBatch.get(i).getEnterpriseId());
					ps.setString(2, employeesBatch.get(i).getEnterpriseName());

				}

				@Override
				public int getBatchSize() {
					return employeesBatch.size();
				}
			});
		});

	}
	
	
	private void insertGroupIdNameInDB(List<Group> groupList) {
		final String insertQuery = "INSERT INTO BW_GROUPS_TEMP(ENTERPRISE_ID, GROUP_ID, GROUP_NAME) values(?,?,?)";
		final int batchSize = 1000;
		List<List<Group>> batchList = Lists.partition(groupList, batchSize);
		log.info("Total batch job set found:{}", batchList.size());
		
		batchList.forEach((employeesBatch)->{
			voipJdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					ps.setString(1, employeesBatch.get(i).getEnterpriseId());
					ps.setString(2, employeesBatch.get(i).getGroupId());
					ps.setString(3, employeesBatch.get(i).getGroupName());

				}

				@Override
				public int getBatchSize() {
					return employeesBatch.size();
				}
			});
		});

	}
}
