package Test;

import oracle.odi.core.OdiInstance;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.TransactionCallbackWithoutResult;
import oracle.odi.core.persistence.transaction.support.TransactionTemplate;
import oracle.odi.domain.project.OdiFolder;
import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.topology.OdiContext;
import oracle.odi.domain.topology.OdiContextualSchemaMapping;
import oracle.odi.domain.topology.OdiDataServer;
import oracle.odi.domain.topology.OdiLogicalSchema;
import oracle.odi.domain.topology.OdiPhysicalSchema;
import oracle.odi.domain.topology.OdiTechnology;
import oracle.odi.domain.topology.finder.IOdiTechnologyFinder;
import oracle.odi.domain.util.ObfuscatedString;
import oracle.odi.publicapi.samples.SimpleOdiInstanceHandle;

public class MyFirstSKDCode 
{

	private static OdiDataServer oracleDataServer;
	protected static String pStringToObfuscate;
	protected static OdiContext context;
	
	/**
	* @param args
	*/
	public static void main(String[] args) 
	{
	
		final SimpleOdiInstanceHandle odiInstanceHandle = SimpleOdiInstanceHandle.create("jdbc:oracle:thin:@localhost:1521:xe",
				"oracle.jdbc.OracleDriver",
				"snpm1",
				"oracle1",
				"WORKREP1",
				"SUPERVISOR",
				"SUNOPSIS");
		final OdiInstance odiInstance = odiInstanceHandle.getOdiInstance();
		
		try 
		{
			TransactionTemplate tx = new TransactionTemplate(odiInstance.getTransactionManager());
			
			tx.execute(new TransactionCallbackWithoutResult()
			{			
				@SuppressWarnings("deprecation")
				protected void doInTransactionWithoutResult(ITransactionStatus pStatus)
				{			
					OdiProject proj = new OdiProject("TEST_PROJECT", "TEST_PROJECT");
					OdiContext context = new OdiContext("DEVELOPMENT"); // New Context
					context.setDefaultContext(true);
					
					@SuppressWarnings("unused")
					OdiFolder fold = new OdiFolder(proj, "Training_Folder");
					
					odiInstance.getTransactionalEntityManager().persist(proj);
					odiInstance.getTransactionalEntityManager().persist(context);
					
					OdiTechnology oracleTechnology = ((IOdiTechnologyFinder) odiInstance
					.getTransactionalEntityManager().getFinder(
					OdiTechnology.class)).findByCode("ORACLE");
					
					if (oracleTechnology != null)
					{
						oracleDataServer = new OdiDataServer(oracleTechnology,"ORCL_SOURCE");
						
						oracleDataServer.setConnectionSettings(new OdiDataServer.JdbcSettings("jdbc:oracle:thin:@localhost:1521:xe","oracle.jdbc.driver.OracleDriver"));
						
						oracleDataServer.setUsername("system");
						oracleDataServer.setPassword(ObfuscatedString.obfuscate("system"));			
					}
				
					// Creating a New Physical Schema
					
					OdiPhysicalSchema oraclePhysicalSchema = new OdiPhysicalSchema(
					oracleDataServer);
					// Set additional information on the schema
					oraclePhysicalSchema.setSchemaName("SOURCE_PS");
					oraclePhysicalSchema.setWorkSchemaName("SOURCE_PS");
					
					// Create the logical schema and the mapping to it in the
					// context
					OdiLogicalSchema oracleLogicalSchema = new OdiLogicalSchema(oracleTechnology, "SOURCE_LS");
					new OdiContextualSchemaMapping(context,oracleLogicalSchema, oraclePhysicalSchema);
					
					// Persist the Data Server , Physical and Logical Schema
					
					odiInstance.getTransactionalEntityManager().persist(oracleTechnology);
					
					System.out.println("Done");
					}
				});
			}	
		finally
		{
			odiInstanceHandle.release();
		}
		
	}
}