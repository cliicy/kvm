package com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus.model;


import com.extjs.gxt.ui.client.data.BaseModelData;

public class DataModalJobStatus extends BaseModelData{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4011605711677645971L;
	
	public Long getJobId(){
		return get("jobId");
	}
	public void setJobId(Long id){
		set("jobId",id);
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
	public Long getStartTime() {
		return get("startTime");
	}
	public void setStartTime(Long startTime) {
		set("startTime", startTime);
	}
	public Long getElapsedTime() {
		return get("elapsedTime");
	}
	public void setElapsedTime(Long elapsedTime) {
		set("elapsedTime", elapsedTime);
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

}
