package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class JobStatusModel extends BaseModelData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4011605711677645971L;
	
	public static final long JOBSCRIPT_ID= -1;
	
	public D2DTimeModel startTime;
	
	public BackupLocationInfoModel backupLocationInfoMode;

	public String getJobUuid() {
		return get("uuid");
	}

	public void setJobUuid(String uuid) {
		set("uuid", uuid);
	}

	public Long getJobID() {
		return get("jobId");
	}

	public void setJobID(Long id) {
		set("jobId", id);
	}

	public String getJobName() {
		return get("jobName");
	}

	public void setJobName(String jobName) {
		set("jobName", jobName);
	}

	public Integer getJobType() {
		return get("jobType");
	}

	public void setJobType(Integer jobType) {
		set("jobType", jobType);
	}

	public String getNodeName() {
		return get("nodeName");
	}

	public void setNodeName(String nodeName) {
		set("nodeName", nodeName);
	}

	public String getNodeUser() {
		return get("nodeUser");
	}

	public void setNodeUser(String nodeUser) {
		set("nodeUser", nodeUser);
	}

	public String getNodePassword() {
		return get("nodePassword");
	}

	public void setNodePassword(String nodePassword) {
		set("nodePassword", nodePassword);
	}

	public Integer getCompression() {
		return get("Compression");
	}

	public void setCompression(Integer compression) {
		set("Compression", compression);
	}

	public String getEncryptionName() {
		return get("EncryptionName");
	}

	public void setEncryptionName(String encryptionName) {
		set("EncryptionName", encryptionName);
	}

	public String getEncryptionPassword() {
		return get("EncryptionPassword");
	}

	public void setEncryptionPassword(String encryptionPassword) {
		set("EncryptionPassword", encryptionPassword);
	}
	
	public String getCleanIncompleteSession() {
		return get("cleanIncompleteSession");
	}

	public void setCleanIncompleteSession(String cleanIncompleteSession) {
		set("cleanIncompleteSession", cleanIncompleteSession);
	}
	
	public String getIncludeVolumes() {
		return get("includeVolumes");
	}

	public void setIncludeVolumes(String includeVolumes) {
		set("includeVolumes", includeVolumes);
	}
	
	public String getExcludeVolumes() {
		return get("excludeVolumes");
	}

	public void setExcludeVolumes(String excludeVolumes) {
		set("excludeVolumes", excludeVolumes);
	}

	public Integer getJobStatus() {
		return get("jobStatus");
	}

	public void setJobStatus(Integer jobStatus) {
		set("jobStatus", jobStatus);
	}

	public Integer getJobPhase() {
		return get("jobPhase");
	}

	public void setJobPhase(Integer jobPhase) {
		set("jobPhase", jobPhase);
	}

	public void setProgress(Integer progress) {
		set("progress", progress);
	}

	public Integer getProgress() {
		return get("progress");
	}

	public void setVolume(String volume) {
		set("volume", volume);
	}

	public String getVolume() {
		return get("volume");
	}
	
	public Long getExecuteTime() {
		return get("executeTime");
	}

	public void setExecuteTime(Long startTime) {
		set("executeTime", startTime);
	}

	public Long getElapsedTime() {
		return get("elapsedTime");
	}

	public void setElapsedTime(Long elapsedTime) {
		set("elapsedTime", elapsedTime);
	}

	public Long getFinishTime() {
		return get("finishTime");
	}

	public void setFinishTime(Long finishTime) {
		set("finishTime", finishTime);
	}

	public Long getProcessedData() {
		return get("processedData");
	}

	public void setProcessedData(Long processedData) {
		set("processedData", processedData);
	}

	public Long getThroughput() {
		return get("throughput");
	}

	public void setThroughput(Long throughput) {
		set("throughput", throughput);
	}

	public Integer getLastResult() {
		return get("lastResult");
	}

	public void setLastResult(Integer lastResult) {
		set("lastResult", lastResult);
	}
	
	public void setWriteThroughput(Long writeThroughput){
		set("writeThroughput",writeThroughput);
	}
	
	public Long getWriteThroughput(){
		return get("writeThroughput");
	}
	
	public void setWriteData(Long writeData){
		set("writeData",writeData);
	}
	
	public Long getWriteData(){
		return get("writeData");
	}
	
	public Integer getJobMethod(){
		return get("jobMethod");
	}
	
	public void setJobMethod(Integer jobMethod){
		set("jobMethod",jobMethod);
	}
}
