package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.BackupTemplateModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupModel extends BaseModelData {
	
	public static final int SCHEDULE_TYPE_NOW = 1;
	public static final int SCHEDULE_TYPE_ONCE = 2;
	public static final int SCHEDULE_TYPE_MANUALLY = 3;
	public static final int SCHEDULE_TYPE_REPEAT_DAILY = 4;
	public static final int SCHEDULE_TYPE_REPEAT_WEEKLY = 5;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6925920618634239738L;
	private String d2dServer;
	private List<NodeModel> targetList;
	private String excludeVolumes;
	private String excludeFiles;
	private boolean isExclude;
	private BackupTemplateModel destInfo ;
	private String jobName;
	private String uuid;
	private String templateID;
	private int scheduleType;
	public DailyScheduleModel dailySchedule;
	public RetentionModel retentionModel;
	public WeeklyScheduleModel weeklySchedule;
	public D2DTimeModel startTime;
	private String serverScriptBeforeJob;
	private String serverScriptAfterJob;
	private String targetScriptBeforeJob;
	private String targetScriptAfterJob; 
	private String targetScriptBeforeSnapshot;
	private String targetScriptAfterSnapshot;
	private long throttle;
	private boolean isDisable;

	public String getD2dServer() {
		return d2dServer;
	}

	public void setD2dServer(String d2dServer) {
		this.d2dServer = d2dServer;
	}

	public List<NodeModel> getTargetList() {
		return targetList;
	}

	public void setTargetList(List<NodeModel> targetList) {
		this.targetList = targetList;
	}

	public String getExcludeVolumes() {
		return excludeVolumes;
	}

	public void setExcludeVolumes(String excludeVolumes) {
		this.excludeVolumes = excludeVolumes;
	}

	public BackupTemplateModel getDestInfo() {
		return destInfo;
	}

	public void setDestInfo(BackupTemplateModel destInfo) {
		this.destInfo = destInfo;
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

	public String getTargetScriptBeforeSnapshot() {
		return targetScriptBeforeSnapshot;
	}

	public void setTargetScriptBeforeSnapshot(String targetScriptBeforeSnapshot) {
		this.targetScriptBeforeSnapshot = targetScriptBeforeSnapshot;
	}

	public String getTargetScriptAfterSnapshot() {
		return targetScriptAfterSnapshot;
	}

	public void setTargetScriptAfterSnapshot(String targetScriptAfterSnapshot) {
		this.targetScriptAfterSnapshot = targetScriptAfterSnapshot;
	}

	public int getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(int scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getTemplateID() {
		return templateID;
	}

	public void setTemplateID(String templateID) {
		this.templateID = templateID;
	}

	public long getThrottle() {
		return throttle;
	}

	public void setThrottle(long throttle) {
		this.throttle = throttle;
	}

	public String getExcludeFiles() {
		return excludeFiles;
	}

	public void setExcludeFiles(String excludeFiles) {
		this.excludeFiles = excludeFiles;
	}

	public boolean isDisable() {
		return isDisable;
	}

	public void setDisable(boolean isDisable) {
		this.isDisable = isDisable;
	}

	public boolean isExclude() {
		return isExclude;
	}

	public void setExclude(boolean isExclude) {
		this.isExclude = isExclude;
	}
}
