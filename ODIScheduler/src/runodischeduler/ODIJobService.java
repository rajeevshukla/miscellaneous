package runodischeduler;

import oracle.odi.runtime.agent.invocation.InvocationException;
import oracle.odi.runtime.agent.invocation.StartupParams;


public class ODIJobService {


	private String odiHost;
	private String odiUser;
	private String odiPassword;
	private String odiScenName;
	private String odiContextCode;
	private String odiWorkRepName;
	private String odiScenVersion;
	
	public ODIJobService() {
		odiHost = ApplicationUtils.getCustomProperty("odiHost");
		odiUser = ApplicationUtils.getCustomProperty("odiUser"); //INSERT ODI USER
		odiPassword =ApplicationUtils.getCustomProperty("odiPassword");  //INSERT ODI PASSWORD
		odiScenName =  ApplicationUtils.getCustomProperty("odiscenName"); //INSERT NAME OF SCENARIO ODI
		odiScenVersion =ApplicationUtils.getCustomProperty("odiScenVersion"); //INSERT VERSION OF SCENARIO ODI
		//INSERT THE CONTEXT FOR SCENARIO ODI "GLOBAL" SUCH AS DEFAULT
		odiContextCode = ApplicationUtils.getCustomProperty("odiContextCode"); 
		odiWorkRepName = ApplicationUtils.getCustomProperty("odiWorkRepName"); //INSERT NAME OF YOUR WORK REPOSITORY
	}

	public  void runJob() throws Throwable{

		System.out.println("INFO :Property details " + ApplicationUtils.PROPERTIES);
		StartupParams odiStartupParams = new StartupParams();

		//IF YOU HAVE PARAMETERS FOR SCENARIO ODI INSERT HERE 
		odiStartupParams = null;
		String odiKeywords = null;

		String odiSessionName = null; 
		long  timeToSleep=0;
		int odiLogLevel = 5; //INSERT LOG LEVEL
		boolean odiSynchronous = true; //TRUE FOR SYNC MODE, FALSE ASYNC MODE

		String result = null;
		DatabaseManager databaseManager = new DatabaseManager();
		try {
			result=databaseManager.fetchJobDetailsFromDB();
		} catch (Exception e1) {
			e1.printStackTrace();
		}


		while(result!=null){
			System.out.println("Job found :"+result);
			String data[] = result.split("\\|");
			int jobId=Integer.parseInt(data[0]);
			odiScenName = data[1];
			odiScenVersion = data[2];
			timeToSleep = Long.parseLong(data[3]);
			System.out.println("INFO: Going to sleep for "+ timeToSleep+ " Minutes");
			try {
				Thread.sleep(timeToSleep*60*1000);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			OdiInvokeScenario call = new OdiInvokeScenario(databaseManager,jobId);
			//UPDATING JOB STATUS IN DB
			databaseManager.updateJobDetailsInDB(jobId, 0, "RUNNING");			

			System.out.println("INFO: Executing job right away !!");
			
			System.out.println("Job INFO ::  ODIScenName:"+odiSessionName+" OdiHost:"+odiHost+" OdiScenVersion:"+odiScenName);

			try {
				call.runScenario(odiHost,odiUser,odiPassword,odiScenName,odiScenVersion,
						odiStartupParams,odiKeywords,odiContextCode,
						odiLogLevel,odiSessionName,odiSynchronous,
						odiWorkRepName);
			} catch (InvocationException e) {
				e.printStackTrace();
			}
			System.out.println("INFO :  ==== CHECKING ANOTHER JOB DETAILS ====");
			result=databaseManager.fetchJobDetailsFromDB();
		}
		System.out.println("INFO: No More job found. Exiting  !!!");

	}


}