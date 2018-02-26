package com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus.model;

public class DataModalJobStatusAll extends DataModalJobStatus{
	/**
	 * 
	 */
	private static final long serialVersionUID = -988734953162322435L;

	public void setTargetServer(String targetServer) {
		set("targetServer", targetServer);
	}

	public String getTargetServer() {
		return get("targetServer");
	}
}
