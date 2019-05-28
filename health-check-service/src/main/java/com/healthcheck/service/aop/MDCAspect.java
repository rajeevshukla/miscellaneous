package com.healthcheck.service.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect @Component
public class MDCAspect {

	@Pointcut("* com.healthcheck.*.*() && args(name,..)")
	public void modifyMDC(String name) {}

	@Before("modifyMDC(name)")
	public void modifyMDC2(String name)  {}
	
}
