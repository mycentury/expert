package com.expert.constant;

public enum RoleType {
	SYSTEM("S", 5),
	GENERAL("G", 3),
	VISITOR("V", 1);
	private String code;
	private int priority;

	private RoleType(String code, int priority) {
		this.code = code;
		this.priority = priority;
	}

	public String getCode() {
		return code;
	}

	public int getPriority() {
		return priority;
	}
}
