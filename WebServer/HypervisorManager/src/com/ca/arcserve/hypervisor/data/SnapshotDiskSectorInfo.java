package com.ca.arcserve.hypervisor.data;

public class SnapshotDiskSectorInfo {
	
	private long totalNumSectors = 0;
	private long usedNumSectors = 0;
	
	public long getDiskUsedNumSectors() {
		return usedNumSectors;
	}
	public void setDiskUsedNumSectors(long usedNumSectors) {
		this.usedNumSectors = usedNumSectors;
	}
	public long getDiskTotalNumSectors() {
		return totalNumSectors;
	}
	public void setDiskTotalNumSectors(long totalNumSectors) {
		this.totalNumSectors = totalNumSectors;
	}

}
