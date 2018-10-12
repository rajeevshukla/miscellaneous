package com.java8.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args) throws IOException {
	System.out.println("Please wait for a while program is running and it can take time depending on the size of the file");
    List<Path> ls = Files.walk(Paths.get("/home/pathtoyourlog files home directory"))
    		.filter(t->t.toString().endsWith(".log")).
    		filter(t->(System.currentTimeMillis() - t.toAbsolutePath().toFile().lastModified())<1*60*60*1000) // filtering files which are modified in less than hours
    		.collect(Collectors.toList()); // collecting them into list.
   
    System.out.println("Total files found:"+ ls.size());
    
    //Processing on multithreaded mode to consume all the cpu cores available on system.. 
    ls.parallelStream().forEach(Main::searchTexts);
   
	}
	
	
	
	// this method will be called for log file which has been modified in last 1 hour
	public static void searchTexts(Path file) {
    		
		
		// NOw there could be performance problem if file is big like 1gb or something. So first figure out how much time does it take to search a text in a single file then 
		// multiply that with number of files.  
		
		// A better options is grep but same problem you won't be much flexible to work on that. 
		 String textToSearchInFiile="Exception";
		 // 
	     System.out.println("Searching text:"+textToSearchInFiile +" in :"+file.toAbsolutePath());
	 	 long count=0;
		try {
			// if you have multiple things to search you have to below this line to search that inside filter method. 
			count = Files.lines(file).parallel().filter(t->t.contains(textToSearchInFiile)).count();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 	 // now you can append this result into some file here. 
	 	System.out.println("For text:\""+textToSearchInFiile +"\" total occurance found:"+count +" fileLocation:"+ file.toAbsolutePath());
		
		
		
		
	}
	
	
	
	
}
