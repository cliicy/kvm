package com.ca.arcserve.hypervisor.data;

public class VirtualMachineDefaultSetting {
	
	private String networkAdapterType;
	
	private String scsiController;
	
	private String ideController;
	
	private String guestOsID;

	private String supportedMaxVmVersion;
	
	public String getNetworkAdapterType() {
		return networkAdapterType;
	}

	public void setNetworkAdapterType(String networkAdapterType) {
		this.networkAdapterType = networkAdapterType;
	}

	public String getScsiController() {
		return scsiController;
	}

	public void setScsiController(String scsiController) {
		this.scsiController = scsiController;
	}

	public String getIdeController() {
		return ideController;
	}

	public void setIdeController(String ideController) {
		this.ideController = ideController;
	}

	public String getGuestOsID() {
		return guestOsID;
	}

	public void setGuestOsID(String guestOsID) {
		this.guestOsID = guestOsID;
	}

	public String getSupportedMaxVmVersion() {
		return supportedMaxVmVersion;
	}

	public void setSupportedMaxVmVersion(String supportedMaxVmVersion) {
		this.supportedMaxVmVersion = supportedMaxVmVersion;
	}
	
}
