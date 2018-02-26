package com.ca.arcserve.hypervisor.data;

import java.util.List;

public class VirtualMachineInfo {
	
	private List<NetworkInfo> networkAdapters;
	
	private String uuid;
	
	private String vmName;

	public List<NetworkInfo> getNetworkAdapters() {
		return networkAdapters;
	}

	public void setNetworkAdapters(List<NetworkInfo> networkAdapters) {
		this.networkAdapters = networkAdapters;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	
}
