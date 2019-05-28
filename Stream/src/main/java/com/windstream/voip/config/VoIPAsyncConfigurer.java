package com.windstream.voip.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.windstream.voip.exception.handler.AsyncExceptionHandler;

@Configuration
@EnableAsync
public class VoIPAsyncConfigurer implements AsyncConfigurer {
	
	private static final int BW_CORE_THREAD_POOL_SIZE = 50;
	private static final int BW_MAX_THREAD_POOL_SIZE = 100;
	private static final int BW_QUEUE_SIZE = 200;


	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncExceptionHandler();
	}

	@Bean(name = "bwAsyncExecutor")
	public TaskExecutor broadworksAsyncExecutor() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(BW_CORE_THREAD_POOL_SIZE);
		executor.setMaxPoolSize(BW_MAX_THREAD_POOL_SIZE);
		executor.setQueueCapacity(BW_QUEUE_SIZE);
		executor.setThreadNamePrefix("BroadworksThreadPool-");
		executor.initialize();
		return executor;
	}

}
