package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupMachineModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3182240746503360029L;
	
	public static final int TYPE_TAPE_MACHINE = 1;
	public static final int TYPE_HBBU_MACHINE = 2;

	private int recoveryPointCount;

	private int recoverySetCount;

	private long recoveryPointSize;

	private int machineType;
	
	private String machinePath;
	
	private String machineUUID;

	private String vmHost;
	
	private String vmIp;

	private Date firstDate;

	private Date lastDate;

	public String getMachineName() {
		return get("machineName");
	}

	public void setMachineName(String machineName) {
		set("machineName", machineName);
	}

	public int getRecoveryPointCount() {
		return recoveryPointCount;
	}

	public void setRecoveryPointCount(int recoveryPointCount) {
		this.recoveryPointCount = recoveryPointCount;
	}

	public int getRecoverySetCount() {
		return recoverySetCount;
	}

	public void setRecoverySetCount(int recoverySetCount) {
		this.recoverySetCount = recoverySetCount;
	}

	public long getRecoveryPointSize() {
		return recoveryPointSize;
	}

	public void setRecoveryPointSize(long recoveryPointSize) {
		this.recoveryPointSize = recoveryPointSize;
	}

	public Date getFirstDate() {
		return firstDate;
	}

	public void setFirstDate(Date firstDate) {
		this.firstDate = firstDate;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public int getMachineType() {
		return machineType;
	}

	public void setMachineType(int machineType) {
		this.machineType = machineType;
	}

	public String getVmHost() {
		return vmHost;
	}

	public void setVmHost(String vmHost) {
		this.vmHost = vmHost;
	}
	
	public String getVmIp() {
		return vmIp;
	}

	public void setVmIp(String vmIp) {
		this.vmIp = vmIp;
	}

	public String getMachinePath() {
		return machinePath;
	}

	public void setMachinePath(String machinePath) {
		this.machinePath = machinePath;
	}

	public String getMachineUUID() {
		return machineUUID;
	}

	public void setMachineUUID(String machineUUID) {
		this.machineUUID = machineUUID;
	}

}
