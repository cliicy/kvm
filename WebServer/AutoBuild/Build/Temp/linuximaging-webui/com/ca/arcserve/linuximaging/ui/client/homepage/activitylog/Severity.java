package com.ca.arcserve.linuximaging.ui.client.homepage.activitylog;

public enum Severity {
	
	All(-1),
	Information(1),
	Error(2),
	Warning(3),
	ErrorAndWarning(4);
	
	private int value;
	
	private Severity(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}

	public static Severity parse(int severity) {
		switch (severity) {
		case -1:
			return All;
		case 1:
			return Information;
		case 2:
			return Error;
		case 3:
			return Warning;
		case 4:
			return ErrorAndWarning;
		default:
			return null;
		}
	}

}
