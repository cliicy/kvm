package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RecoveryPointModel extends BaseModelData {

	private static final long serialVersionUID = 1448916318634207299L;

	// private RecoveryPointItemModel[] items;

	public String getHbbuPath() {
		return get("hbbuPath");
	}

	public void setHbbuPath(String hbbuPath) {
		set("hbbuPath", hbbuPath);
	}

	public void setOsType(String osType) {
		set("osType", osType);
	}

	public String getOsType() {
		return get("osType");
	}

	public Long getTime() {
		return (Long) get("time");
	}

	public void setTime(Long time) {
		set("time", time);
	}

	public Integer getBackupType() {
		return (Integer) get("backupType");
	}

	public void setBackupType(Integer backupType) {
		set("backupType", backupType);
	}

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public Integer getCompression() {
		return get("Compression");
	}

	public void setCompression(Integer compression) {
		set("Compression", compression);
	}

	public String getEncryptAlgoName() {
		return (String) get("encryptAlgoName");
	}

	public void setEncryptAlgoName(String encryptAlgoName) {
		set("encryptAlgoName", encryptAlgoName);
	}

	public String getEncryptionPassword() {
		return (String) get("encryptionPassword");
	}

	public void setEncryptionPassword(String encryptionPassword) {
		set("encryptionPassword", encryptionPassword);
	}

	public String getEncryptionPasswordHash() {
		return (String) get("encryptionPasswordHash");
	}

	public void setEncryptionPasswordHash(String encryptionPasswordHash) {
		set("encryptionPasswordHash", encryptionPasswordHash);
	}

	/**
	 * @gwt.typeArgs <com.ca.arcflash.ui.client.model.RecoveryPointItemModel>
	 */
	public List<RecoveryPointItemModel> items;

	public List<GridTreeNode> files;

	// public List<com.ca.arcflash.ui.client.model.CatalogInfoModel>
	// listOfCatalogInfo;
	//
	// public List<com.ca.arcflash.ui.client.model.GridTreeNode> listOfEdbNodes;
	// // for Exchange GRT only
	public Integer getBootVolumeExist() {
		return (Integer) get("bootVolumeExist");
	}

	public void setBootVolumeExist(Integer exist) {
		set("bootVolumeExist", exist);
	}

	public Integer getBootVolumeBackup() {
		return (Integer) get("bootVolumeBackup");
	}

	public void setBootVolumeBackup(Integer backup) {
		set("bootVolumeBackup", backup);
	}

	public Integer getRootVolumeBackup() {
		return (Integer) get("rootVolumeExist");
	}

	public void setRootVolumeBackup(Integer backup) {
		set("rootVolumeExist", backup);
	}

	public Integer getRecoverySetStartFlag() {
		return get("recoverySetStartFlag");
	}

	public void setRecoverySetStartFlag(Integer recoverySetStartFlag) {
		set("recoverySetStartFlag", recoverySetStartFlag);
	}

	public Integer getBKAdvSch() {
		return get("bkAdvSch");
	}

	public void setBKAdvSch(Integer bkAdvSch) {
		set("bkAdvSch", bkAdvSch);
	}

	public Double getVersion() {
		return get("version");
	}

	public void setVersion(Double version) {
		set("version", version);
	}
	
	public void setUEFI(Boolean isUEFI){
		set("isUEFI",isUEFI);
	}
	
	public Boolean isUEFI(){
		return get("isUEFI");
	}
	
	public AssuredRecoveryTestResultModel getAssruedRecoveryTestResult(){
		return get("assruedRecoveryTestResult");
	}
	
	public void setAssruedRecoveryTestResult(AssuredRecoveryTestResultModel assuredRecoveryTestResult){
		set("assruedRecoveryTestResult",assuredRecoveryTestResult);
	}
}
