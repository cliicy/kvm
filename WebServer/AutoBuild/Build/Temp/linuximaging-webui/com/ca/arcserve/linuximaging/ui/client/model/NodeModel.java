package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.List;

public class NodeModel extends ServiceInfoModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2329390361824479192L;

	/**
	 * 
	 */
	public NodeModel() {
		setServerName(null);
		setUserName(null);
		setPasswd(null);
		setDescription(null);
		setProtected(Boolean.FALSE);
		setJobName(null);
		setOperatingSystem(null);
	}

	public NodeModel(String server, String user, String pass, String desc,
			String OS) {
		setServerName(server);
		setUserName(user);
		setPasswd(pass);
		setDescription(desc);
		setProtected(Boolean.FALSE);
		setJobName(null);
		setOperatingSystem(OS);
	}

	public void setServerName(String serverName) {
		set("server", serverName);
	}

	public String getServerName() {
		return get("server");
	}

	public void setUserName(String userName) {
		set("user", userName);
	}

	public String getUserName() {
		return get("user");
	}

	public void setUUID(String uuid) {
		set("uuid", uuid);
	}

	public String getUUID() {
		return get("uuid");
	}

	public void setPasswd(String passwd) {
		set("passwd", passwd);
	}

	public String getPasswd() {
		return get("passwd");
	}

	public void setDescription(String description) {
		set("description", description);
	}

	public String getDescription() {
		return get("description");
	}

	public NodeConnectionModel connInfo = null;
	public List<VolumeInfoModel> excludeVolumes = null;

	public void setProtected(Boolean nodeProtected) {
		set("protected", nodeProtected);
	}

	public Boolean getProtected() {
		return get("protected");
	}

	public String getJobName() {
		return get("jobName");
	}

	public void setJobName(String jobName) {
		set("jobName", jobName);
	}

	public void setOperatingSystem(String operatingSystem) {
		set("operatingSystem", operatingSystem);
	}

	public String getOperatingSystem() {
		return get("operatingSystem");
	}

	public void setLastResult(Integer lastResult) {
		set("lastResult", lastResult);
	}

	public Integer getLastResult() {
		return get("lastResult");
	}

	public void setRecoveryPointCount(Integer recoveryPointCount) {
		set("recoveryPointCount", recoveryPointCount);
	}

	public Integer getRecoveryPointCount() {
		return get("recoveryPointCount");
	}

	public void setRecoverySetCount(Integer recoverySetCount) {
		set("recoverySetCount", recoverySetCount);
	}

	public Integer getRecoverySetCount() {
		return get("recoverySetCount");
	}

	public void setPriority(Integer priority) {
		set("priority", priority);
	}

	public Integer getPriority() {
		return get("priority");
	}

	public void setExclude(Boolean exclude) {
		set("isExclude", exclude);
	}

	public Boolean isExclude() {
		return get("isExclude");
	}
	
	public void setBackupLocationType(Integer backupLocationType){
		set("backupLocationType",backupLocationType);
	}
	
	public Integer getBackupLocationType(){
		return get("backupLocationType");
	}
}
