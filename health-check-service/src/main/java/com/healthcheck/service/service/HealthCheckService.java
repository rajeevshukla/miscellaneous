package com.healthcheck.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.healthcheck.service.proxy.EurekaAppServiceProxy;

@Service
public class HealthCheckService {

	@Autowired
	EurekaAppServiceProxy eurekaAppServiceProxy;
	
	
	public String compareAndFetchRunningApps() { 
		// pull from eureka
			
		eurekaAppServiceProxy.getRunningApps();
		
		// load static instances. 

		
		return "this should not be sttring... a list of object after comparing with eureka and static data";
	}
	
	
	
}
