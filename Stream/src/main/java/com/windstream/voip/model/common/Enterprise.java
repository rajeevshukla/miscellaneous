package com.windstream.voip.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class Enterprise {

	private String enterpriseId;
	private String enterpriseName;
	

	@Override
	public boolean equals(Object obj) {
		return this.getEnterpriseId().equals(((Enterprise)obj).getEnterpriseId());
	}
	
	@Override
	public int hashCode() {
		return getEnterpriseId().hashCode();
	}
}
