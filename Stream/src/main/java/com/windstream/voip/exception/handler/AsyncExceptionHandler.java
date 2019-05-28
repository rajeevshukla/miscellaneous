package com.windstream.voip.exception.handler;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

/**
 * Handling uncaught exceptions if any. This would be worst case scenario to
 * just print them on log file so that none of the async exception goes missing.
 *
 */
@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(AsyncExceptionHandler.class);

	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {

		logger.error(":::::::START:::::::: Uncaught async exception found:::START::::");
		logger.error("Method Name : " + method.getName());
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			logger.error("Param Name:" + param);
		}
		logger.error("::::::END::::::::: Uncaught async exception found:::END::::");
	}

}
