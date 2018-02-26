package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupLocationInfoModel extends BaseModelData {
	public static final int TYPE_NFS = 1;
	public static final int TYPE_CIFS = 2;
	public static final int TYPE_SOURCE_LOCAL = 3;
	public static final int TYPE_SERVER_LOCAL = 4;
	public static final int UNIT_TYPE_MB = 2;
	public static final int UNIT_TYPE_PERCENT = 1;
	public static final int TYPE_RPS_SERVER = 5;
	public static final int TYPE_AMAZON_S3 = 6;
	private ServerInfoModel serverInfoModel;
	private DatastoreModel datastoreModel;
	/**
	 * 
	 */
	private static final long serialVersionUID = 9191886050702580806L;

	public BackupLocationInfoModel() {
	}

	public void setServerInfoModel(ServerInfoModel serverInfoModel) {
		this.serverInfoModel = serverInfoModel;
	}

	public ServerInfoModel getServerInfoModel() {
		return this.serverInfoModel;
	}

	public void setDatastoreModel(DatastoreModel datastoreModel) {
		this.datastoreModel = datastoreModel;
	}

	public DatastoreModel getDatastoreModel() {
		return this.datastoreModel;
	}

	public void setUUID(String uuid) {
		set("uuid", uuid);
	}

	public String getUUID() {
		return get("uuid");
	}

	public Integer getEnableDedup() {
		return get("enableDedupe");
	}

	public void setEnableDedup(Integer enableDedup) {
		set("enableDedupe", enableDedup);
	}

	public void setDisplayName(String displayName) {
		set("displayName", displayName);
	}

	public String getDisplayName() {
		return get("displayName");
	}

	public String getSessionLocation() {
		return get("SessionLocation");
	}

	public void setSessionLocation(String sessionLocation) {
		set("SessionLocation", sessionLocation);
	}

	public String getUser() {
		return get("User");
	}

	public void setUser(String user) {
		set("User", user);
	}

	public String getPassword() {
		return get("Password");
	}

	public void setPassword(String password) {
		set("Password", password);
	}

	public Long getFreeSize() {
		return get("FreeSize");
	}

	public void setFreeSize(Long freeSize) {
		set("FreeSize", freeSize);
	}

	public Long getTotalSize() {
		return get("TotalSize");
	}

	public void setTotalSize(Long totalSize) {
		set("TotalSize", totalSize);
	}

	public Integer getType() {
		return get("Type");
	}

	public void setType(Integer type) {
		set("Type", type);
	}

	public void setRunScript(Boolean isRunScript) {
		set("RunScript", isRunScript);
	}

	public Boolean isRunScript() {
		return get("RunScript");
	}

	public void setScript(String script) {
		set("script", script);
	}

	public String getScript() {
		return get("script");
	}

	public void setFreeSizeAlert(Long freeSizeAlert) {
		set("freeSizeAlert", freeSizeAlert);
	}

	public Long getFreeSizeAlert() {
		return get("freeSizeAlert");
	}

	public void setFreeSizeAlertUnit(Integer unit) {
		set("freeSizeAlertUnit", unit);
	}

	public Integer getFreeSizeAlertUnit() {
		return get("freeSizeAlertUnit");
	}

	public void setJobLimit(Integer jobLimit) {
		set("jobLimit", jobLimit);
	}

	public Integer getJobLimit() {
		return get("jobLimit");
	}

	public void setCurrentJobCount(Integer currentJobCount) {
		set("currentJobCount", currentJobCount);
	}

	public Integer getCurrentJobCount() {
		return get("currentJobCount");
	}

	public void setWaitingJobCount(Integer waitingJobCount) {
		set("waitingJobCount", waitingJobCount);
	}

	public Integer getWaitingJobCount() {
		return get("waitingJobCount");
	}
	
	public void setS3CifsShareUsername(String username){
		set("s3CifsShareUsername",username);
	}
	
	public String getS3CifsShareUsername(){
		return get("s3CifsShareUsername");
	}
	
	public void setS3CifsSharePassword(String password){
		set("s3CifsSharePassword",password);
	}
	
	public String getS3CifsSharePassword(){
		return get("s3CifsSharePassword");
	}
	
	public void setS3CifsSharePort(Integer port){
		set("s3CifsSharePort",port);
	}
	
	public Integer getS3CifsSharePort(){
		return get("s3CifsSharePort");
	}
	
	public void setEnableS3CifsShare(Boolean isEnableS3CifsShare){
		set("isEnableS3CifsShare",isEnableS3CifsShare);
	}
	
	public Boolean isEnableS3CifsShare(){
		return get("isEnableS3CifsShare");
	}
}
