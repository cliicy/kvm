package com.ca.arcserve.linuximaging.ui.client.model.dashboard;

import com.extjs.gxt.ui.client.data.BaseModelData;


public class D2DServerInfoModel extends BaseModelData {
	public static final int ERROR=0;
	public static final int TERMINATE=1;
	public static final int VALID=2;
	public static final int TRIAL=3;
	public static final int EXPIRED=4;
	public static final int WILL_EXPIRE=5;
	public static final int WG_COUNT=6;
	public static final int GLIC_NOT_INSTALL=7;
//	public static final int BETA=10;
//	public static final int BETA_EXPIRED=11;
	
	private static final long serialVersionUID = -198278034697047063L;
	/**
	 * 
	 */
	private String manufacture;
	private String model;
	private String cpuType;
	private String osVersion;
	private Long upTime;
	private int runningJobs;
	private boolean restoreUtility;
	private int license;
	private int licenseCount;
	public String getManufacture() {
		return manufacture;
	}
	public void setManufacture(String manufacture) {
		this.manufacture = manufacture;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getCpuType() {
		return cpuType;
	}
	public void setCpuType(String cpuType) {
		this.cpuType = cpuType;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public Long getUpTime() {
		return upTime;
	}
	public void setUpTime(Long upTime) {
		this.upTime = upTime;
	}
	public int getRunningJobs() {
		return runningJobs;
	}
	public void setRunningJobs(int runningJobs) {
		this.runningJobs = runningJobs;
	}
	public boolean isRestoreUtility() {
		return restoreUtility;
	}
	public void setRestoreUtility(boolean restoreUtility) {
		this.restoreUtility = restoreUtility;
	}
	public int getLicense() {
		return license;
	}
	public void setLicense(int license) {
		this.license = license;
	}
	public int getLicenseCount() {
		return licenseCount;
	}
	public void setLicenseCount(int licenseCount) {
		this.licenseCount = licenseCount;
	}
	
}
