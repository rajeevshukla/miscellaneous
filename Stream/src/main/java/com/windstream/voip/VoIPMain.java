package com.windstream.voip;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

import com.windstream.voip.config.VoIPProperties;


@SpringBootApplication
public class VoIPMain implements CommandLineRunner{

	@Autowired
	VoIPProperties properties;
	
	@Value("${server.port:8080}")
	int serverPort;
	
	
	@Value("${jasypt.encryptor.password:encrypterkeynotset}")
	String secret;
	
	
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(VoIPMain.class);
		springApplication.addListeners(new ApplicationPidFileWriter("voip-service.pid"));
		springApplication.run(args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		System.out.println(properties);
		System.out.println("!! VoIP Service is UP on port:"+serverPort+" !!"+ secret);
	}
	
}