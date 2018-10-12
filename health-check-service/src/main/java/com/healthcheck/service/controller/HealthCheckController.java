package com.healthcheck.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

	
	
	@GetMapping("GetRunningInstances")
	
	public ResponseEntity<Object> getRunningInsances() { 
		
		
		return null;
	}
}
