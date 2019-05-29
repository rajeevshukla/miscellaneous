package com.gl.spark;

import org.apache.spark.serializer.KryoRegistrator;

import com.esotericsoftware.kryo.Kryo;

public class ClassRegstrator implements KryoRegistrator{

	@Override
	public void registerClasses(Kryo kryo) {
//		kryo.register(MobileDataUses.class);
		System.out.println("Regsitrator called...");
//		kryo.register(ApplicationUtils.class);
	}
}
