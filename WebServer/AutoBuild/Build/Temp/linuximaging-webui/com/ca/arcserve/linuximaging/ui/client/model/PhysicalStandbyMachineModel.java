package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class PhysicalStandbyMachineModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -871860289205705548L;
	
	private String macAddress;

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
}
