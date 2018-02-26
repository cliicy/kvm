package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class JobStatusFilterModel extends BaseModelData {
	public static final int FILTER_ALL=-1;
	public static final int JOB_TYPE_BACKUP=1;
	public static final int JOB_TYPE_RESTORE=2;
	
	public static final int JOB_STATUS_INACTIVE=0;
	public static final int JOB_STATUS_ACTIVE=1;
	public static final int JOB_STATUS_WAITING=2;
	
	private static final long serialVersionUID = -6335635557743610318L;
	private int jobType;
	private String jobName;
	private String nodeName;
	private int status;
	private int lastResult;
	private String backupLocation;
	
	public int getJobType() {
		return jobType;
	}
	public void setJobType(int type) {
		this.jobType = type;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobName() {
		return jobName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getLastResult() {
		return lastResult;
	}

	public void setLastResult(int lastResult) {
		this.lastResult = lastResult;
	}
	public String getBackupLocation() {
		return backupLocation;
	}
	public void setBackupLocation(String backupLocation) {
		this.backupLocation = backupLocation;
	}

}
