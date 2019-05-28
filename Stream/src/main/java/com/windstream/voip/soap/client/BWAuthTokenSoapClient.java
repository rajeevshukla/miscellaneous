package com.windstream.voip.soap.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.windstream.voip.config.VoIPProperties;

@Component
public class BWAuthTokenSoapClient {

	@Autowired
	VoIPProperties properties;

	@Autowired
	@Qualifier("byos-wst")
	WebServiceTemplate serviceTemplate;

	public Object getAuthToken(Object request) {
		return serviceTemplate.marshalSendAndReceive(properties.getUrl().getByosToken(), request);
	}
}
