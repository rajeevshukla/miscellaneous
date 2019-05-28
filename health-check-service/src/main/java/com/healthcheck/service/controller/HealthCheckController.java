package com.healthcheck.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthcheck.service.annoation.EnrichedLogging;

@RestController
public class HealthCheckController {

	
	
	@GetMapping("GetRunningInstances")
	@EnrichedLogging
	public ResponseEntity<Object> getRunningInsances() { 
		
		System.out.println("Get Running instances is called....");
		
		return null;
	}
}
