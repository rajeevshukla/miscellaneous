package runodischeduler;

import java.util.Properties;

public class ApplicationUtils {
	public static final Properties PROPERTIES = new Properties();
	public static boolean jobFinished = false;
	public static int jobFailedCouter = 0;

	public static String getCustomProperty(String propertyKey) {
		return PROPERTIES.getProperty(propertyKey);
	}

	public static boolean isJobFinished() {
		return jobFinished;
	}

	public static void setJobFinished(boolean jobFinished) {
		ApplicationUtils.jobFinished = jobFinished;
	}

	public static int getJobFailedCouter() {
		return jobFailedCouter;
	}

	public static void resetJobFailedCounter() {
		ApplicationUtils.jobFailedCouter = 0;
	}

	public static int updateJobFailedCounter() {
		return jobFailedCouter = jobFailedCouter + 1;
	}

}
