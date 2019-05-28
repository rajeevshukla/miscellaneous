package com.developervisits;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

public class PrintService {

	public Message<?> print(Message<String> message) {
		System.out.println("Print Service Called:"+message.getPayload());

		return MessageBuilder.withPayload("Replying.."+message.getPayload()).setHeader("header", "TEst").build();
	}
}
