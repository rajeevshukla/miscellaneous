package com.windstream.voip.soap.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.windstream.voip.config.VoIPProperties;

@Component
public class VoIPAdminApiSoapClient {
     
	  @Autowired
	  VoIPProperties properties;
	  
      @Autowired()
      @Qualifier("byos-wst")
      WebServiceTemplate serviceTemplate;
      
	public <T,Y> Y  execute(T requestPayload, Class<Y> returnType) {
		return returnType.cast(serviceTemplate.marshalSendAndReceive(properties.getUrl().getVoipAdminApi(), requestPayload));
	}
}
