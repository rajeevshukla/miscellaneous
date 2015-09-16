package runodischeduler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;

public class DatabaseManager {




	/**
	 * This method will return data  i.e. job_id|scenName|scenVer|duetime from database  null if not found
	 * 
	 */

	public  String fetchJobDetailsFromDB() throws Exception{

	 //	System.out.println("INFO: Fetching database connection");
		Connection connection =DatabaseUtils.getInstance().getConnection();
	//	System.out.println("Connection : "+connection);

		String result=null;

		if(null!=connection){
			CallableStatement callableStatement=connection.prepareCall("call STG_MDM.PKG_COMMON.SP_GET_SCENARIO_DETAILS(?,?)");
			callableStatement.setString(1, "001");
			callableStatement.registerOutParameter(2,OracleTypes.VARCHAR);
			callableStatement.execute();
			result =callableStatement.getString(2);
			connection.close();
			return  result;
		}else {
			System.out.println("ERROR : unable to obtain db connection ");
		}
		return null;
	}

	public void updateJobDetailsInDB(int jobId,long sessionId,String sessionStatus){

	//	System.out.println("INFO: Fetching database connection");
		Connection connection =DatabaseUtils.getInstance().getConnection();
		if(null!=connection){
			try {
				CallableStatement callableStatement=connection.prepareCall("call STG_MDM.PKG_COMMON.SP_UPDATE_JOB_STATUS(?,?,?)");
				callableStatement.setInt(1, jobId);;
				callableStatement.setInt(3, new Long(sessionId).intValue());
				callableStatement.setString(2, sessionStatus);
				callableStatement.execute();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("ERROR : unable to obtain db connection ");
		}
	}

}
