package runodischeduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ODIMain {


	private static boolean jobStatus=false;


	public static void main(String[] args) {
		init(args);
		
	  ScheduledExecutorService executorService=Executors.newSingleThreadScheduledExecutor();
	  executorService.scheduleWithFixedDelay(new  Runnable() {
		
		@Override
		public void run() {
			if(!jobStatus){
				System.out.println("Schedular started.");
				
				ODIMain odiMain=new ODIMain();
				odiMain.startJob();
				
			}else  {
				System.out.println("INFO :: Job is already running");
			}
		}
	}, 0,Long.parseLong(ApplicationUtils.getCustomProperty("schedulerTimeInMinute")),TimeUnit.MINUTES);
}


	private  void startJob(){
		jobStatus=true;
		System.out.println("INFO : Job started...!!!");

		try {
			ODIJobService jobService=new ODIJobService();
			jobService.runJob();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		System.out.println("INFO : Job finished.");
		jobStatus=false;
	}

	private static void init(String ... args){
		if(args.length!=1){
			System.out.println("ERROR : Properties file path is missing please provide it as an argument.");
			System.exit(0);
		}
		String propertyFilePath=args[0];

		File file=new File(propertyFilePath);
		if(!file.exists()) {
			System.out.println("Error : Propeties file does not exists on its location:"+ file.getPath());
		}
		try {
			ApplicationUtils.PROPERTIES.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}




}
