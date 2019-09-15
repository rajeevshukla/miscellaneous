package com.example.demo;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarketRecommendationApplication {

	public static void main(String[] args) {
//		SpringApplication.run(MarketRecommendationApplication.class, args);
String query="APPL";
	try {
			Document document = Jsoup.connect("https://www.google.com/search?q=" + query /* + "&num=20" */).userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)").get();
		
			System.out.println(document.select("h3:containsOwn(Top stories)").first()
					)
		
				;
	
			Elements links = document.select("a[href]");
			for (Element link : links) {

				String temp = link.attr("href");		
				System.out.println(temp);
			}
	
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
		
	}

}
