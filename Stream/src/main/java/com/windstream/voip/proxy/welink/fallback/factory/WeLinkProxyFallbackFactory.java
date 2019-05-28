package com.windstream.voip.proxy.welink.fallback.factory;

import org.springframework.stereotype.Component;

import com.windstream.voip.proxy.welink.WeLinkServiceProxy;
import com.windstream.voip.proxy.welink.fallback.WeLinkServiceProxyFallback;

import feign.hystrix.FallbackFactory;

@Component
public class WeLinkProxyFallbackFactory implements FallbackFactory<WeLinkServiceProxy> {

	@Override
	public WeLinkServiceProxy create(Throwable cause) {
		 return new WeLinkServiceProxyFallback(cause);
	}
	
}
