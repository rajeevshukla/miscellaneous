package com.healthcheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.healthcheck.service.proxy.GetRunningAppsEurekaServiceProxy;

@SpringBootApplication
@EnableFeignClients("com.healthcheck.service.proxy")
public class HealthCheckServiceApplication implements CommandLineRunner {

	@Autowired
	GetRunningAppsEurekaServiceProxy proxy;
	
	public static void main(String[] args) {
		SpringApplication.run(HealthCheckServiceApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		System.out.println(proxy.getGitHubUserDetails());
		
		//similarily you can call this one change url in app.props and then change in proxy class 
		//System.out.println(proxy.getRunningApps());
		
		
	}
}
