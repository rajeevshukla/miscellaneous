package com.healthcheck.service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.healthcheck.service.model.AppInstanceDetails;
import com.healthcheck.service.proxy.EurekaAppServiceProxy;
import com.healthcheck.service.utils.AppUtils;

@Service
public class HealthCheckService {

	@Autowired
	EurekaAppServiceProxy eurekaAppServiceProxy;
	
	
	public String compareAndFetchRunningApps() { 
		// pull from eureka
		
		
		eurekaAppServiceProxy.getRunningApps();
		
		// load static instances. 
		
	 try {
		List<AppInstanceDetails> appList = 	AppUtils.loadStaticInstances();
		
		// now compare it here and return all of then into a new java class object which will 
	 // automatically converted into json object by spring framework. 
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		return "this should not be sttring... a list of object.";
	}
	
	
	
}
