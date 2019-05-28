package com.windstream.voip.model.common;

public enum BWUserType {

	ENTERPRISE_ADMIN(3), GROUP_ADMIN(2), ANI(1);

	private int value;

	private BWUserType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
