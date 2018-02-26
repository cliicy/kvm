package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VersionInfoModel extends BaseModelData {

	private static final long serialVersionUID = 5664230335630062206L;
	
	private String productName="CA ARCserve<small><sup>&reg;</sup></small> D2D for Linux";
	
	private boolean isLiveCD;
	
	private boolean isSupportWakeOnLan;
	
	private boolean isLicensed = false;
	
	private String version;
	
	private String buildNumber;
	
	private boolean enableNonRootUser;
	
	private ServerInfoModel managedServer;
	
	private boolean isLiveCDIsoExist;

	private boolean isShowDefaultUserWhenLogin;
	
	private String defaultUser;
	
	private int uiLogoutTime;
	private boolean enableVM;
	private boolean isEnableExcludeFile;
	private ServerCapabilityModel serverCapabilityModel;
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public boolean isLiveCD() {
		return isLiveCD;
	}

	public void setLiveCD(boolean isLiveCD) {
		this.isLiveCD = isLiveCD;
	}

	public boolean isSupportWakeOnLan() {
		return isSupportWakeOnLan;
	}

	public void setSupportWakeOnLan(boolean isSupportWakeOnLan) {
		this.isSupportWakeOnLan = isSupportWakeOnLan;
	}

	public boolean isLicensed() {
		return isLicensed;
	}

	public void setLicensed(boolean isLicensed) {
		this.isLicensed = isLicensed;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getTimeZoneOffset() {
		return get("timeZoneOffset");
	}
	public void setTimeZoneOffset(Integer timeZoneOffset) {
		set("timeZoneOffset", timeZoneOffset);
	}

	public ServerInfoModel getManagedServer() {
		return managedServer;
	}

	public void setManagedServer(ServerInfoModel managedServer) {
		this.managedServer = managedServer;
	}

	public void setEnableNonRootUser(boolean enableNonRootUser) {
		this.enableNonRootUser = enableNonRootUser;
	}

	public boolean isEnableNonRootUser() {
		return enableNonRootUser;
	}

	public boolean isLiveCDIsoExist() {
		return isLiveCDIsoExist;
	}

	public void setLiveCDIsoExist(boolean isLiveCDIsoExist) {
		this.isLiveCDIsoExist = isLiveCDIsoExist;
	}

	public boolean isShowDefaultUserWhenLogin() {
		return isShowDefaultUserWhenLogin;
	}

	public void setShowDefaultUserWhenLogin(boolean isShowDefaultUserWhenLogin) {
		this.isShowDefaultUserWhenLogin = isShowDefaultUserWhenLogin;
	}

	public String getDefaultUser() {
		return defaultUser;
	}

	public void setDefaultUser(String defaultUser) {
		this.defaultUser = defaultUser;
	}

	public int getUiLogoutTime() {
		return uiLogoutTime;
	}

	public void setUiLogoutTime(int uiLogoutTime) {
		this.uiLogoutTime = uiLogoutTime;
	}

	public void setEnableVM(boolean enableVM) {
		this.enableVM = enableVM;
	}

	public boolean isEnableVM() {
		return enableVM;
	}

	public boolean isEnableExcludeFile() {
		return isEnableExcludeFile;
	}

	public void setEnableExcludeFile(boolean isEnableExcludeFile) {
		this.isEnableExcludeFile = isEnableExcludeFile;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	public ServerCapabilityModel getServerCapabilityModel() {
		return serverCapabilityModel;
	}

	public void setServerCapabilityModel(ServerCapabilityModel serverCapabilityModel) {
		this.serverCapabilityModel = serverCapabilityModel;
	}
	
}
