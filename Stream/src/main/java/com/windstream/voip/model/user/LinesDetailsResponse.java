package com.windstream.voip.model.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class LinesDetailsResponse {

	private String enterpriseId;
	private String groupId;
	private List<AniDetails> anis;
	
	
	@Data
	@ToString
	public static class AniDetails { 
		private String ani;
		private String userId;
//		private Boolean displayResetMenuLink;

		public AniDetails(String ani) { 
			this.ani = ani;
		}

	}
}
