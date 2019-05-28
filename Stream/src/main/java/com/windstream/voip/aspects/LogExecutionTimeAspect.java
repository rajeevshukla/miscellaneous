package com.windstream.voip.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LogExecutionTimeAspect {

	@Around("execution(* *(..)) && @annotation(com.windstream.voip.annotations.LogExecutionTime)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();

		Object returnObject = joinPoint.proceed();

		long executionTime = System.currentTimeMillis() - startTime;
		log.info("{} executed in {} secs.", joinPoint.getSignature(), executionTime / 1000.0f);
		return returnObject;
	}
}
