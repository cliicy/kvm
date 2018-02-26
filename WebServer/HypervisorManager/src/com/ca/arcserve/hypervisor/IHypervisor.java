package com.ca.arcserve.hypervisor;

import java.util.List;
import java.util.Map;

import com.ca.arcserve.hypervisor.data.BootDeviceType;
import com.ca.arcserve.hypervisor.data.CDRomInfo;
import com.ca.arcserve.hypervisor.data.HypervisorInfo;
import com.ca.arcserve.hypervisor.data.NasDatastoreInfo;
import com.ca.arcserve.hypervisor.data.NetworkInfo;
import com.ca.arcserve.hypervisor.data.PowerState;
import com.ca.arcserve.hypervisor.data.ResourcePool;
import com.ca.arcserve.hypervisor.data.SnapshotDiskInfo;
import com.ca.arcserve.hypervisor.data.SnapshotDiskSectorInfo;
import com.ca.arcserve.hypervisor.data.StorageInfo;
import com.ca.arcserve.hypervisor.data.SubHypervisorInfo;
import com.ca.arcserve.hypervisor.data.VMCreationConfiguration;
import com.ca.arcserve.hypervisor.data.VirtualMachineDefaultSetting;
import com.ca.arcserve.hypervisor.data.VirtualMachineInfo;
import com.ca.arcserve.hypervisor.exception.HypervisorException;

public interface IHypervisor {
	public Boolean connect(HypervisorInfo hypervisor) throws HypervisorException;

	public void close();

	public List<SubHypervisorInfo> getSubHosts();

	/*
	 * Returns the UUID of created VM on success.
	 */
	public String createVM(SubHypervisorInfo subHost, VMCreationConfiguration config)
			throws HypervisorException;

	public boolean deleteVM(String uuid) throws HypervisorException;

	public boolean startVM(String uuid) throws HypervisorException;

	public boolean stopVM(String uuid) throws HypervisorException;
	
	public boolean shutdownOS(String uuid) throws HypervisorException;

	public List<VirtualMachineInfo> getVMList(SubHypervisorInfo subHost);

	public VirtualMachineInfo getVirtualMachineInfo(String uuid)throws HypervisorException;

	public VirtualMachineDefaultSetting getVirtualMachineDefaultSetting(String subHost,String guestOS);
	
	/*
	 * Returns the identifier of created network, usually the same with it's
	 * name.
	 */
	public String createHypervisorNetwork(String subHost, String networkName)
			throws HypervisorException;

	public boolean removeHypervisorNetwork(String subHost, String networkName)
			throws HypervisorException;
	
	public boolean changeCDRom(String uuid, CDRomInfo cdInfo)
			throws HypervisorException;

	/**
	 * return macaddress
	 * 
	 * @param vmuuid
	 * @param subHost
	 * @param networkInfo
	 * @return
	 */
	public String addNicCard(String vmuuid, String subHost, NetworkInfo networkInfo)
			throws HypervisorException;

	public boolean removeNicCard(String vmuuid, String subHost,String networkName);
	
	public String createSnapshot(String vmuuid, String snapshotName,
			String description);

	public boolean removeSnapshot(String vmuuid, String snapshotId);

	public int enableChangeTracking(String vmuuid, boolean isEnable);

	public List<SnapshotDiskInfo> getSnapshotDiskInfo(String vmuuid,
			String snapshotId);

	public SnapshotDiskSectorInfo generateBitMapForDisk(String vmuuid,
			String snapshotId, String diskChangeId, Integer diskDeviceKey,
			String filePath, Integer chunkSize, Integer sectorSize);
	
	public List<ResourcePool> getResourcePoolList(SubHypervisorInfo subHost);
	public List<StorageInfo> getStorageList(SubHypervisorInfo subHost);
	public void refreshDatastore(SubHypervisorInfo subHost,String datastoreName);
	public List<String> getNetworkList(SubHypervisorInfo subHost);
	public int putFile(HypervisorInfo hypervisor,String subHost,String datastore, String filename, String filePath) throws HypervisorException;
	public int deleteFile(HypervisorInfo hypervisor,String subHost,String datastore, String filename) throws HypervisorException;
	public boolean addSerialPort(String vmuuid,String subHost,String datastore,String fileName) throws HypervisorException;
	public boolean addConfigurationParameter(String vmuuid,String subHost,Map<String,String> parameters) throws HypervisorException;
	public boolean changeBootOrder(String vmuuid,BootDeviceType bootDevice) throws HypervisorException;
	public String createNasDatastore(SubHypervisorInfo subHost, NasDatastoreInfo datastore) throws HypervisorException;
	public boolean removeDatastore(SubHypervisorInfo subHost,String datastoreName) throws HypervisorException;
	public int getHypervisorLevel();
	public PowerState getVMPowerStatus(String vmuuid) throws HypervisorException;
	public boolean uploadVMNvRamFile(String vmuuid, String file);
	public boolean isSupportedVM(int requireVmVersion);
	public String checkStorageExist(SubHypervisorInfo subHost,String storageName);
}
