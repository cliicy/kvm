package com.ca.arcserve.linuximaging.ui.client.model.dashboard;

import com.extjs.gxt.ui.client.data.BaseModelData;


public class ResourceUsageModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6445025215156281809L;

	private Float cpuUsage;
	private Long physicalMemoryTotal;
	private Long physicalMemoryFree;
	private Long swapSizeFree;
	private Long swapSizeTotal;
	private Long installationVolumeTotal;
	private Long installationVolumeFree;
	public Float getCpuUsage() {
		return cpuUsage;
	}
	public void setCpuUsage(Float cpuUsage) {
		this.cpuUsage = cpuUsage;
	}
	public Long getPhysicalMemoryTotal() {
		return physicalMemoryTotal;
	}
	public void setPhysicalMemoryTotal(Long physicalMemoryTotal) {
		this.physicalMemoryTotal = physicalMemoryTotal;
	}
	public Long getPhysicalMemoryFree() {
		return physicalMemoryFree;
	}
	public void setPhysicalMemoryFree(Long physicalMemoryFree) {
		this.physicalMemoryFree = physicalMemoryFree;
	}
	public Long getSwapSizeFree() {
		return swapSizeFree;
	}
	public void setSwapSizeFree(Long swapSizeFree) {
		this.swapSizeFree = swapSizeFree;
	}
	public Long getSwapSizeTotal() {
		return swapSizeTotal;
	}
	public void setSwapSizeTotal(Long swapSizeTotal) {
		this.swapSizeTotal = swapSizeTotal;
	}
	public Long getInstallationVolumeTotal() {
		return installationVolumeTotal;
	}
	public void setInstallationVolumeTotal(Long installationVolumeTotal) {
		this.installationVolumeTotal = installationVolumeTotal;
	}
	public Long getInstallationVolumeFree() {
		return installationVolumeFree;
	}
	public void setInstallationVolumeFree(Long installationVolumeFree) {
		this.installationVolumeFree = installationVolumeFree;
	}
	
}
