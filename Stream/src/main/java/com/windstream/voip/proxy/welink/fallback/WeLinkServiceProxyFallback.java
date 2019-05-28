package com.windstream.voip.proxy.welink.fallback;

import java.util.Collections;
import java.util.List;

import com.windstream.voip.proxy.welink.WeLinkServiceProxy;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WeLinkServiceProxyFallback implements WeLinkServiceProxy {

	
	private Throwable cause;
	public WeLinkServiceProxyFallback(Throwable cause) {
		
		this.cause = cause;
	}
	@Override
	public List<Long> getGlobalAccountIdsForBusEntityId(Long busEntityId) {
	 log.error("Fallback method is called for busEntityId:{}",busEntityId, cause);
	 if(cause instanceof FeignException) {
		 log.error("Error in calling welink proxy response status:{}",((FeignException)cause).status());
	 }
	  return	Collections.emptyList(); 
	}
}
