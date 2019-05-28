package com.developervisits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@SpringBootApplication
@Configuration
@ImportResource("integration-context.xml")
public class SpringIntegrationApplication implements ApplicationRunner{


	/*@Autowired
	@Qualifier("inputChannel")
	DirectChannel inputChannel;*/

	@Autowired
	PrinterGateway gateway;

	public static void main(String[] args) {
		SpringApplication.run(SpringIntegrationApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {



		Message<String> message= MessageBuilder.withPayload("Sending message payload").setHeader("HeaderName", "Header value").build();


		/*MessagingTemplate template = new MessagingTemplate(inputChannel);

	Message<String> m =	(Message<String>)template.sendAndReceive(message);

	System.out.println("Result found:"+m.getPayload());
		 */

		List<Future<Message<String>>> futures = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			futures.add(this.gateway.print(MessageBuilder.withPayload("Message"+i).build()));
		}

		for (Iterator iterator = futures.iterator(); iterator.hasNext();) {
			Future<Message<String>> future = (Future<Message<String>>) iterator.next();
			System.out.println(future.get().getPayload());
		}
		

	}

}
