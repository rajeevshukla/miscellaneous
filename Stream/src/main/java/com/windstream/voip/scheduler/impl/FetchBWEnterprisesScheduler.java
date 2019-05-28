package com.windstream.voip.scheduler.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.windstream.voip.annotations.LogExecutionTime;
import com.windstream.voip.repository.scheduler.SchedulerDAO;
import com.windstream.voip.scheduler.SchedulerService;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;

@Component
@Slf4j
public class FetchBWEnterprisesScheduler implements SchedulerService {

	@Autowired
	SchedulerDAO dao;
	

	@Override
	@Scheduled(cron = "${bw.fetchEnterprise.scheduler}")
	@SchedulerLock(name="bwEnterpriseSchedulerFetcher")
	@LogExecutionTime
	public void execute() throws Throwable {
		log.info("===== Starting FetchBWEnterpriseScheduler =====");
		dao.fetchAndSaveEnterpriseIdNameFromBW();
		
		dao.fetchAndSaveGroupIdNameFromBW();
		
		log.info("==== Completed FetchBWEnterpriseScheduler ======");
	}

}
