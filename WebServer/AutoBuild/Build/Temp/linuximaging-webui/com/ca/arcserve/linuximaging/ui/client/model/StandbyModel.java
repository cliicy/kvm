package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class StandbyModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1742208606895954417L;
	private String templateID;
	private String uuid;
	private String jobName;
	private List<NodeModel> sourceNodeList;
	private boolean isAutoStartMachine;
	private int startMachineMethod;
	private int heartBeatTime;
	private int heartBeatFrequency;
	private String serverScriptBeforeJob;
	private String serverScriptAfterJob;
	private String targetScriptBeforeJob;
	private String targetScriptAfterJob; 
	private String newUsername;
	private String newPassword;
	private String localPath;
	private StandbyType standbyType;
	public String getTemplateID() {
		return templateID;
	}
	public void setTemplateID(String templateID) {
		this.templateID = templateID;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public List<NodeModel> getSourceNodeList() {
		return sourceNodeList;
	}
	public void setSourceNodeList(List<NodeModel> sourceNodeList) {
		this.sourceNodeList = sourceNodeList;
	}
	public boolean isAutoStartMachine() {
		return isAutoStartMachine;
	}
	public void setAutoStartMachine(boolean isAutoStartMachine) {
		this.isAutoStartMachine = isAutoStartMachine;
	}
	public int getStartMachineMethod() {
		return startMachineMethod;
	}
	public void setStartMachineMethod(int startMachineMethod) {
		this.startMachineMethod = startMachineMethod;
	}
	public int getHeartBeatTime() {
		return heartBeatTime;
	}
	public void setHeartBeatTime(int heartBeatTime) {
		this.heartBeatTime = heartBeatTime;
	}
	public int getHeartBeatFrequency() {
		return heartBeatFrequency;
	}
	public void setHeartBeatFrequency(int heartBeatFrequency) {
		this.heartBeatFrequency = heartBeatFrequency;
	}
	public String getServerScriptBeforeJob() {
		return serverScriptBeforeJob;
	}
	public void setServerScriptBeforeJob(String serverScriptBeforeJob) {
		this.serverScriptBeforeJob = serverScriptBeforeJob;
	}
	public String getServerScriptAfterJob() {
		return serverScriptAfterJob;
	}
	public void setServerScriptAfterJob(String serverScriptAfterJob) {
		this.serverScriptAfterJob = serverScriptAfterJob;
	}
	public String getTargetScriptBeforeJob() {
		return targetScriptBeforeJob;
	}
	public void setTargetScriptBeforeJob(String targetScriptBeforeJob) {
		this.targetScriptBeforeJob = targetScriptBeforeJob;
	}
	public String getTargetScriptAfterJob() {
		return targetScriptAfterJob;
	}
	public void setTargetScriptAfterJob(String targetScriptAfterJob) {
		this.targetScriptAfterJob = targetScriptAfterJob;
	}
	public String getNewUsername() {
		return newUsername;
	}
	public void setNewUsername(String newUsername) {
		this.newUsername = newUsername;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public StandbyType getStandbyType() {
		return standbyType;
	}
	public void setStandbyType(StandbyType standbyType) {
		this.standbyType = standbyType;
	}
}
