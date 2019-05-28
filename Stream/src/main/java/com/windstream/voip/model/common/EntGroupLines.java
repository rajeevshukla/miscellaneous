package com.windstream.voip.model.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class EntGroupLines {

	private String enterpriseId;
	private String groupId;
	//lines or phones for given enterpriseId/groupId
	private List<String> anis;
	
	
}
