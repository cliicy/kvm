package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VirtualMachineInfoModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1187221690328921174L;

	public void setVirtualMachineName(String vmName) {
		set("vmName", vmName);
	}

	public String getVirtualMachineName() {
		return get("vmName");
	}

	public void setDataStoreName(String dsName) {
		set("dsName", dsName);
	}

	public String getDataStoreName() {
		return get("dsName");
	}

	public void setDataStoreId(String dsId) {
		set("dsId", dsId);
	}

	public String getDataStoreId() {
		return get("dsId");
	}

	public void setVirtualMachineHostName(String hostname) {
		set("hostname", hostname);
	}

	public String getVirtualMachineHostName() {
		return get("hostname");
	}

	public Integer getVirtualMachineMemory() {
		return get("memory");
	}

	public void setVirtualMachineMemory(Integer memory) {
		set("memory", memory);
	}
	
	public void setVirtualMachineMemoryUnit(Integer memoryUnit){
		set("memoryUnit",memoryUnit);
	}
	
	public Integer getVirtualMachineMemoryUnit(){
		return get("memoryUnit");
	}

	public void setNetwork_IsDHCP(Boolean isDHCP) {
		set("isDHCP", isDHCP);
	}

	public Boolean getNetwork_IsDHCP() {
		return get("isDHCP");
	}

	public void setNetwork_ipAddress(String network_ipAddress) {
		set("network_ipAddress", network_ipAddress);
	}

	public String getNetwork_ipAddress() {
		return get("network_ipAddress");
	}

	public void setNetwork_subnetMask(String network_subnetMask) {
		set("network_subnetMask", network_subnetMask);
	}

	public String getNetwork_subnetMask() {
		return get("network_subnetMask");
	}

	public void setNetwork_gateway(String network_gateway) {
		set("network_gateway", network_gateway);
	}

	public String getNetwork_gateway() {
		return get("network_gateway");
	}

	public void setNetwork_dnsServer(String network_dnsServer) {
		set("network_dnsServer", network_dnsServer);
	}

	public String getNetwork_dnsServer() {
		return get("network_dnsServer");
	}

	public void setReboot(Boolean reboot) {
		set("reboot", reboot);
	}

	public Boolean getReboot() {
		return get("reboot");
	}
}
