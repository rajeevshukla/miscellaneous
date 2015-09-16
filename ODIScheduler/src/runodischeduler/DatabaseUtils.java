package runodischeduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {

	private volatile static DatabaseUtils databaseUtils;

	private DatabaseUtils() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return;
		}
		System.out.println("Oracle JDBC Driver Registered!");
	}

	public static DatabaseUtils getInstance(){
		if(null==databaseUtils) {
			synchronized (DatabaseUtils.class) {
				if(null==databaseUtils){
					databaseUtils=new DatabaseUtils();
				}
			}
		}
		return databaseUtils;
	}

	public Connection getConnection(){
 
		 System.out.println("INFO: Initilizing DB connection");

		Connection connection = null;

		try {
			
			connection = DriverManager.getConnection(
					ApplicationUtils.getCustomProperty("db.connectionurl"), ApplicationUtils.getCustomProperty("db.username"),
					ApplicationUtils.getCustomProperty("db.password"));

		} catch (Throwable e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		return connection;
	}




}

