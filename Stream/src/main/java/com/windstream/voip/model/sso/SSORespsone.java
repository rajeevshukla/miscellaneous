package com.windstream.voip.model.sso;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SSORespsone {

	private String url;
	private SSOStatus status;
	private ManagebleEntities details;

	public static enum SSOStatus {
		MULTIPLE_ENTITIES_FOUND, URL_FOUND;
	}

	@Data
	@ToString
	public static class ManagebleEntities {
		private List<Entity> enterprises;
		private List<Entity> groups;
		private List<Entity> anis;
	}

	@Data
	@AllArgsConstructor
	@ToString
	public static class Entity {
		private Long userKey;
		private String displayValue;

	}
}
