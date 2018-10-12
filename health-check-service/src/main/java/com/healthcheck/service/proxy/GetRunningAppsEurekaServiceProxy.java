package com.healthcheck.service.proxy;

import javax.ws.rs.core.MediaType;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthcheck.service.model.Applications;

@FeignClient(name="eureka-proxy",url="${eureka.uri}")
public interface GetRunningAppsEurekaServiceProxy {

	
	
	@GetMapping(value="/eureka/apps",consumes= {MediaType.APPLICATION_XML})
	public Applications getRunningApps();
	// we need to generate java object model from that xml and then provide the root class here 
	//as a return type
	
     @GetMapping(value="/users")
     public String getGitHubUserDetails();
	 
	
	
	
}
