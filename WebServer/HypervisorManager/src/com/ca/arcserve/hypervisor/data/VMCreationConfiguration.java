package com.ca.arcserve.hypervisor.data;

import java.util.List;

public class VMCreationConfiguration {
	
	public static final String HYPERV_VM_GENERATION_1 = "1";
	public static final String HYPERV_VM_GENERATION_2 = "2";

	private String name;
	private int cpu;
	private Long memory;
	private String guest;
	private String targetStorage;
	private String resourcePoolId;
	private List<NetworkInfo> targetNetwork;
	private BootDeviceType bootOption;
	private CDRomInfo cdInfo;
	private List<DiskInfo> disks;
	private boolean isUEFI;
	private String vmVersion;

	public static VMCreationConfiguration getHypervisorDefaultConfig(
			HypervisorType type) {
		VMCreationConfiguration res = null;
		switch (type) {
		case ESX:
			res = new VMCreationConfiguration();
			res.setCpuCount(1);
			res.setMemorySize(512 * 1024 * 1024l);
			return res;
		case OVM:
		case RHEV:
		case XEN:
		default:
			return res;
		}
	}

	/**
	 * @return the cpu count
	 */
	public int getCpuCount() {
		return cpu;
	}

	/**
	 * @param cpu
	 *            the cpu count to set
	 */
	public void setCpuCount(int cpu) {
		this.cpu = cpu;
	}

	/**
	 * @return the memory
	 */
	public Long getMemorySize() {
		return memory;
	}

	/**
	 * @param memory
	 *            the memory to set
	 */
	public void setMemorySize(Long memory) {
		this.memory = memory;
	}

	/**
	 * @return the targetStorage
	 */
	public String getTargetStorage() {
		return targetStorage;
	}

	/**
	 * @param targetStorage
	 *            the targetStorage to set
	 */
	public void setTargetStorage(String targetStorage) {
		this.targetStorage = targetStorage;
	}

	/**
	 * @return the targetNetwork
	 */
	public List<NetworkInfo> getTargetNetwork() {
		return targetNetwork;
	}

	/**
	 * @param targetNetwork
	 *            the targetNetwork to set
	 */
	public void setTargetNetwork(List<NetworkInfo> targetNetwork) {
		this.targetNetwork = targetNetwork;
	}

	/**
	 * @return the bootOption
	 */
	public BootDeviceType getBootOption() {
		return bootOption;
	}

	/**
	 * @param bootOption
	 *            the bootOption to set
	 */
	public void setBootOption(BootDeviceType bootOption) {
		this.bootOption = bootOption;
	}

	/**
	 * @return the disks
	 */
	public List<DiskInfo> getDisks() {
		return disks;
	}

	/**
	 * @param disks
	 *            the disks to set
	 */
	public void setDisks(List<DiskInfo> disks) {
		this.disks = disks;
	}

	/**
	 * @return the vm name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the vm name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the guest template
	 */
	public String getGuest() {
		return guest;
	}

	/**
	 * @param guest
	 *            the guest template name to set
	 */
	public void setGuest(String guest) {
		this.guest = guest;
	}

	public CDRomInfo getCdInfo() {
		return cdInfo;
	}

	public void setCdInfo(CDRomInfo cdInfo) {
		this.cdInfo = cdInfo;
	}

	public String getResourcePoolId() {
		return resourcePoolId;
	}

	public void setResourcePoolId(String resourcePoolId) {
		this.resourcePoolId = resourcePoolId;
	}

	public boolean isUEFI() {
		return isUEFI;
	}

	public void setUEFI(boolean isUEFI) {
		this.isUEFI = isUEFI;
	}

	public String getVmVersion() {
		return vmVersion;
	}

	public void setVmVersion(String vmVersion) {
		this.vmVersion = vmVersion;
	}

}