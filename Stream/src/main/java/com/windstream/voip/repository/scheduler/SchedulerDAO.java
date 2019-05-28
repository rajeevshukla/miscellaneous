package com.windstream.voip.repository.scheduler;

public interface SchedulerDAO {

	public void fetchAndSaveEnterpriseIdNameFromBW() throws Exception;
	
	public void fetchAndSaveGroupIdNameFromBW() throws Exception;
	
}
