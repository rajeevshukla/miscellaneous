package com.windstream.voip.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.ToString;

@Configuration
@ConfigurationProperties(prefix = "voip")
@Data
@ToString
public class VoIPProperties {

	private URL url = new URL();
	private String byoscluster;
	private int enableAsyncAfter;

	@Data
	@ToString
	public static class URL {

		private String enterpriseGroups;
		private String welinkService;
		private String byosToken;
		private String voipAdminApi;
		private String allUsersSummaryByEntGroups;
		private String modifyUsers;
		private String allAnisByUPDevTypeProdType;

	}

}
