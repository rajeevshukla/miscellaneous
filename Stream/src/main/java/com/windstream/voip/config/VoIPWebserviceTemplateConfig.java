package com.windstream.voip.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class VoIPWebserviceTemplateConfig {
	@Bean(name= "byos")
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPaths("bw.managed.authtoken",
				"bw.managed.voipadmin",
				"com.windstream.voip.model.jaxb");
		return marshaller;
	}
	
	@Bean(name= "ent-groups")
	public Jaxb2Marshaller entGroupsMarshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPaths(
				"com.ssf.getenterprisegroups");
		return marshaller;
	}
	@Bean(name= "all-users-summary")
	public Jaxb2Marshaller allUsersSummaryMarshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPaths("com.ssf.allusersummary");
		return marshaller;
	}
	@Bean(name= "modify-users")
	public Jaxb2Marshaller modifyUsersMarshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPaths("com.ssf.modifyusers");
		return marshaller;
	}
	

	@Bean(name= "byos-wst")
	public WebServiceTemplate byosWebserivceTemplate(@Qualifier("byos")Jaxb2Marshaller marshaller) {
		WebServiceTemplate serviceTemplate = new WebServiceTemplate();
		serviceTemplate.setMarshaller(marshaller);
		serviceTemplate.setUnmarshaller(marshaller);
//		serviceTemplate.setMessageSender(httpComponentMessageSender());
		return serviceTemplate;
	}
	
	@Bean(name= "ent-groups-wst")
	public WebServiceTemplate entGroupsWST(@Qualifier("ent-groups")Jaxb2Marshaller marshaller) {
		WebServiceTemplate serviceTemplate = new WebServiceTemplate();
		serviceTemplate.setMarshaller(marshaller);
		serviceTemplate.setUnmarshaller(marshaller);
//		serviceTemplate.setMessageSender(httpComponentMessageSender());
		return serviceTemplate;
	}
	@Bean(name= "all-users-summary-wst")
	public WebServiceTemplate allUsersSummaryWST(@Qualifier("all-users-summary") Jaxb2Marshaller marshaller) {
		WebServiceTemplate serviceTemplate = new WebServiceTemplate();
		serviceTemplate.setMarshaller(marshaller);
		serviceTemplate.setUnmarshaller(marshaller);
//		serviceTemplate.setMessageSender(httpComponentMessageSender());
		return serviceTemplate;
	}
	@Bean(name= "modify-users-wst")
	public WebServiceTemplate modifyUsersWST(@Qualifier("modify-users") Jaxb2Marshaller marshaller) {
		WebServiceTemplate serviceTemplate = new WebServiceTemplate();
		serviceTemplate.setMarshaller(marshaller);
		serviceTemplate.setUnmarshaller(marshaller);
//		serviceTemplate.setMessageSender(httpComponentMessageSender());
		return serviceTemplate;
	}

	/*
	 * @Bean public HttpComponentsMessageSender httpComponentMessageSender() {
	 * HttpComponentsMessageSender sender = new HttpComponentsMessageSender();
	 * sender.setConnectionTimeout(10); sender.setReadTimeout(10); return sender; }
	 */
}
