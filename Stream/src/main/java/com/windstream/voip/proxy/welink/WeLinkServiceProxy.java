package com.windstream.voip.proxy.welink;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.windstream.voip.proxy.welink.fallback.factory.WeLinkProxyFallbackFactory;

@FeignClient(name="WeLinkProxy",url="${voip.url.welinkservice}", fallbackFactory=WeLinkProxyFallbackFactory.class)
public interface WeLinkServiceProxy {
	
	@GetMapping("/v2/BusinessEntity/{BusinessEntityId}/BillingAccount")
	public List<Long> getGlobalAccountIdsForBusEntityId(@PathVariable(required=true,name="BusinessEntityId") Long busEntityId) ;
	
}


