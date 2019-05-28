package com.windstream.voip.model.common;

public enum AccessLevel {
	MULTIPLE(4), ENTERPRISE(3), GROUP(2), ANI(1), NONE(0);
	private final int accessLevel;

	private AccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}

	public int getAccessLevel() {
		return accessLevel;
	}
}
