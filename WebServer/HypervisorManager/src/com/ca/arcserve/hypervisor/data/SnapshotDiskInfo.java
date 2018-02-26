package com.ca.arcserve.hypervisor.data;

public class SnapshotDiskInfo {

	private String diskURL = null;
	private String changeID = null;
	private int deviceKey = 0;

	public String getDiskURL() {
		return diskURL;
	}

	public void setDiskURL(String diskURL) {
		this.diskURL = diskURL;
	}

	public String getChangeID() {
		return changeID;
	}

	public void setChangeID(String changeID) {
		this.changeID = changeID;
	}

	public int getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(int deviceKey) {
		this.deviceKey = deviceKey;
	}

}
