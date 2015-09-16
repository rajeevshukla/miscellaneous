package runodischeduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ApplicationUtils {
	public static final Properties PROPERTIES=new Properties();
	public static final Map<Integer, Integer> JOBID_FAILURECOUNT=new HashMap<Integer, Integer>();
	
	
	public static String getCustomProperty(String propertyKey) {
			return PROPERTIES.getProperty(propertyKey);
	}
	
	
}
