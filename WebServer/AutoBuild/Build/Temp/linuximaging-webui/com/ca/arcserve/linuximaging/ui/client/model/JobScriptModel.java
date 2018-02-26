package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class JobScriptModel extends BaseModelData {
	
	public static final int JOB_METHOD_COMMON_BMR = 0;
	public static final int JOB_METHOD_INSTANT_BMR = 1;
	public static final int JOB_METHOD_INSTANT_BMR_WITH_AUTO_RESTORE = 2;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7897064576026850625L;
	public JobScriptModel(){
		this.setJobName(null);
		this.setJobType(null);
		this.setRepeat(null);
		this.setTemplateID(null);
	}
	public String getTemplateID() {
		return get("templateID");
	}
	public void setTemplateID(String templateID) {
		set("templateID",templateID);
	}
	public String getJobName() {
		return get("jobName");
	}
	public void setJobName(String jobName) {
		set("jobName",jobName);
	}
	public Integer getJobType() {
		return get("jobType");
	}
	public void setJobType(Integer jobType) {
		set("jobType",jobType);
	}
	public Boolean isRepeat() {
		return get("repeat");
	}
	public void setRepeat(Boolean repeat) {
		set("repeat",repeat);
	}
	public void setBackupLocation(String backupLocation){
		set("backupLocation",backupLocation);
	}
	public String getBackupLocation(){
		return get("backupLocation");
	}
}
