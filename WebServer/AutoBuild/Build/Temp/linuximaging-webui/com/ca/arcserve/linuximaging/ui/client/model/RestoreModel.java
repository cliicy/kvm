package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RestoreModel extends BaseModelData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6925920618634239738L;
	private String d2dServer;
	private RestoreType restoreType;
	private String templateID;
	private String uuid;
	private String jobName;
//	private String encryptionPassword;
	public BackupLocationInfoModel backupLocationInfoModel;
	private String machine;
	private String machineName;
	private int machineType;
	private String machineUUID;
	private ServerInfoModel serverInfoModel;
	private RecoveryPointModel recoveryPoint;
	private List<RestoreTargetModel> restoreTargetList;
	private List<VMRestoreTargetModel> vmRestoreTargetList;
	public D2DTimeModel startTime;
	private String serverScriptBeforeJob;
	private String serverScriptAfterJob;
	private String targetScriptBeforeJob;
	private String targetScriptAfterJob; 
	private String targetScriptReadyForUseJob;
	private Boolean estimateFileSize;
	private Boolean enableWakeOnLan;
	private String newUsername;
	private String newPassword;
	private String localDevice;
	private String localPath;
	private String excludeTargetDisks;
	private ServerInfoModel linuxD2DServer;
	private String attachedRestoreJobUUID;
	private RestoreType attachedRestoreType;
	private int lengthOftime;
	private String application;
	private String nfsShareOption;

	public RestoreModel(){
		setRestoreType(RestoreType.BMR);
		setUuid(null);
		setJobName(null);
		setMachine(null);
		setRecoveryPoint(null);
		setEstimateFileSize(true);
		setEnableWakeOnLan(false);
	}
	
	public String getD2dServer() {
		return d2dServer;
	}

	public void setD2dServer(String d2dServer) {
		this.d2dServer = d2dServer;
	}
	public RestoreType getRestoreType() {
		return restoreType;
	}
	public void setRestoreType(RestoreType restoreType) {
		this.restoreType = restoreType;
	}
	
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
//	public String getEncryptionPassword() {
//		return encryptionPassword;
//	}
//	public void setEncryptionPassword(String encryptionPassword) {
//		this.encryptionPassword = encryptionPassword;
//	}
	public String getMachine() {
		return machine;
	}
	public void setMachine(String machine) {
		this.machine = machine;
	}
	public RecoveryPointModel getRecoveryPoint() {
		return recoveryPoint;
	}
	public void setRecoveryPoint(RecoveryPointModel recoveryPoint) {
		this.recoveryPoint = recoveryPoint;
	}
	public List<RestoreTargetModel> getRestoreTargetList() {
		return restoreTargetList;
	}
	public void setRestoreTargetList(List<RestoreTargetModel> restoreTargetList) {
		this.restoreTargetList = restoreTargetList;
	}

	public List<VMRestoreTargetModel> getVmRestoreTargetList() {
		return vmRestoreTargetList;
	}

	public void setVmRestoreTargetList(
			List<VMRestoreTargetModel> vmRestoreTargetList) {
		this.vmRestoreTargetList = vmRestoreTargetList;
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
	
	public String getTargetScriptReadyForUseJob() {
		return targetScriptReadyForUseJob;
	}

	public void setTargetScriptReadyForUseJob(String targetScriptReadyForUseJob) {
		this.targetScriptReadyForUseJob = targetScriptReadyForUseJob;
	}

	public Boolean getEstimateFileSize() {
		return estimateFileSize;
	}

	public void setEstimateFileSize(Boolean estimateFileSize) {
		this.estimateFileSize = estimateFileSize;
	}

	public Boolean getEnableWakeOnLan() {
		return enableWakeOnLan;
	}

	public void setEnableWakeOnLan(Boolean enableWakeOnLan) {
		this.enableWakeOnLan = enableWakeOnLan;
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

	public String getLocalDevice() {
		return localDevice;
	}

	public void setLocalDevice(String localDevice) {
		this.localDevice = localDevice;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getExcludeTargetDisks() {
		return excludeTargetDisks;
	}

	public void setExcludeTargetDisks(String excludeTargetDisks) {
		this.excludeTargetDisks = excludeTargetDisks;
	}


	public ServerInfoModel getServerInfoModel() {
		return serverInfoModel;
	}

	public void setServerInfoModel(ServerInfoModel serverInfoModel) {
		this.serverInfoModel = serverInfoModel;
	}

	public ServerInfoModel getLinuxD2DServer() {
		return linuxD2DServer;
	}

	public void setLinuxD2DServer(ServerInfoModel linuxD2DServer) {
		this.linuxD2DServer = linuxD2DServer;
	}

	public String getAttachedRestoreJobUUID() {
		return attachedRestoreJobUUID;
	}

	public void setAttachedRestoreJobUUID(String attachedRestoreJobUUID) {
		this.attachedRestoreJobUUID = attachedRestoreJobUUID;
	}

	public RestoreType getAttachedRestoreType() {
		return attachedRestoreType;
	}

	public void setAttachedRestoreType(RestoreType attachedRestoreType) {
		this.attachedRestoreType = attachedRestoreType;
	}

	public int getMachineType() {
		return machineType;
	}

	public void setMachineType(int machineType) {
		this.machineType = machineType;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getMachineUUID() {
		return machineUUID;
	}

	public void setMachineUUID(String machineUUID) {
		this.machineUUID = machineUUID;
	}

	public int getLengthOftime() {
		return lengthOftime;
	}

	public void setLengthOftime(int lengthOftime) {
		this.lengthOftime = lengthOftime;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getNfsShareOption() {
		return nfsShareOption;
	}

	public void setNfsShareOption(String nfsShareOption) {
		this.nfsShareOption = nfsShareOption;
	}
}
