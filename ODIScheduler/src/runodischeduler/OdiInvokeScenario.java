package runodischeduler;
/*  OdiInvokeScenario class  */


import oracle.odi.runtime.agent.invocation.ExecutionInfo;
import oracle.odi.runtime.agent.invocation.InvocationException;
import oracle.odi.runtime.agent.invocation.RemoteRuntimeAgentInvoker;
import oracle.odi.runtime.agent.invocation.StartupParams;

public class OdiInvokeScenario {

	private DatabaseManager dbManager;
	private int jobId;
	private int maxRetries;

	public OdiInvokeScenario(DatabaseManager databaseManager,int jobId) {

		this.dbManager = databaseManager;
		this.jobId = jobId;
		this.maxRetries = Integer.parseInt(ApplicationUtils.getCustomProperty("noOfRetry"));

	}

	public void runScenario(String odiHost,String odiUser,String odiPassword,
			String odiScenName,String odiScenVersion,
			StartupParams odiStartupParams,String odiKeywords,
			String odiContextCode,int odiLogLevel,
			String odiSessionName,boolean odiSynchronous,
			String odiWorkRepName) throws InvocationException {
		try{

			//Starting the session on the remote agent.
			RemoteRuntimeAgentInvoker remoteRuntimeAgentInvoker = 
					new RemoteRuntimeAgentInvoker(odiHost,odiUser,
							odiPassword.toCharArray());
			//Parameter: 
			//pScenName,pScenVersion,pVariables,pKeywords,
			//pContextCode, pLogLevel, pSessionName, pSynchronous, pWorkRepName
			//pVariables ==> startupParams = new StartupParams();

			ExecutionInfo exeInfo = 
					remoteRuntimeAgentInvoker.invokeStartScenario(odiScenName,odiScenVersion,odiStartupParams,
							odiKeywords,odiContextCode,odiLogLevel,
							odiSessionName,odiSynchronous,odiWorkRepName);

			//Retrieve the session ID.
			System.out.println(
					"-----------------------------------" + "\n" +  
							"Scenario Started in Session : " + exeInfo.getSessionId() + "\n" + 
							"Scenario Session Status : " + exeInfo.getSessionStatus() + "\n" +
					"-----------------------------------" );

			if(exeInfo.getSessionStatus().name().equalsIgnoreCase("ERROR")){
				int failedCount=getFailureCounterValue(jobId);
				if(failedCount >= maxRetries){
					System.out.println("FATAL :========= JOB FAILED PERMANENTLY ======");
					dbManager.updateJobDetailsInDB(jobId, exeInfo.getSessionId(), "FAILED");
				}else {
					System.out.println("INFO:===== JOB FAILED TEMPORARILY ===== RETIRES :"+failedCount+1);
					updateFailureCounter(jobId);
				}
			}else {
				System.out.println(" INFO :====== JOB FINISHED =========== ");
				dbManager.updateJobDetailsInDB(jobId, exeInfo.getSessionId(), exeInfo.getSessionStatus().name());
				removeFailureCounter(jobId);
			}
		}
		catch (Exception e)    {
			e.printStackTrace();
		}
	}

	private void updateFailureCounter(int jobId){
		if( ApplicationUtils.JOBID_FAILURECOUNT.containsKey(jobId)){
			int temp=ApplicationUtils.JOBID_FAILURECOUNT.get(jobId);
			temp=temp+1;
			ApplicationUtils.JOBID_FAILURECOUNT.put(jobId,temp);
		}else {
			ApplicationUtils.JOBID_FAILURECOUNT.put(jobId, 1);
		}
	}


	private int getFailureCounterValue(int jobId){
		int failedCount = 0;
		if(ApplicationUtils.JOBID_FAILURECOUNT.containsKey(jobId)){
			failedCount = ApplicationUtils.JOBID_FAILURECOUNT.get(jobId);
		}
		return failedCount;
	}

	private void removeFailureCounter(int jobId){
		if(ApplicationUtils.JOBID_FAILURECOUNT.containsKey(jobId)){
			ApplicationUtils.JOBID_FAILURECOUNT.remove(jobId);
		}
	}
}

