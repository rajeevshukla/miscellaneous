package com.windstream.voip.model.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.ToString;

@XmlRootElement(name = "response")
@Data
@ToString
public class AdminApiResponse {
	private String code;
	private String message;
	private String value;

}
