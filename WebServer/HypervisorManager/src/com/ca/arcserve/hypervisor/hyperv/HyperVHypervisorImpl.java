package com.ca.arcserve.hypervisor.hyperv;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.arcserve.hyperv.HyperVException;
import com.ca.arcserve.hypervisor.IHypervisor;
import com.ca.arcserve.hypervisor.data.BootDeviceType;
import com.ca.arcserve.hypervisor.data.CDRomInfo;
import com.ca.arcserve.hypervisor.data.DiskInfo;
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
import com.ca.arcserve.hypervisor.exception.HypervisorErrorCode;
import com.ca.arcserve.hypervisor.exception.HypervisorException;

public class HyperVHypervisorImpl implements IHypervisor {
    
    private static final Logger logger = Logger.getLogger(HyperVHypervisorImpl.class);
    private com.arcserve.hyperv.Hypervisor hypervisor = new com.arcserve.hyperv.Hypervisor();
    
    @Override
    public Boolean connect(HypervisorInfo hypervisor) throws HypervisorException {
        try {
            if (hypervisor != null)
                return this.hypervisor.connect(hypervisor.getName(), hypervisor.getUsername(),
                    hypervisor.getPassword(), hypervisor.getProtocol(), hypervisor.getPort());
        } catch (HyperVException hve) {
        	if(HypervisorErrorCode.ERROR_INVALID_HOST.equals(hve.getErrorCode())){
        		throw new HypervisorException(HypervisorErrorCode.ERROR_INVALID_HYPERV,hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        	}
            throw new HypervisorException(hve.getErrorCode(), hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        } catch (Exception e) {
            logger.error("Failed to connect to target HyperV server. ", e);
            throw new HypervisorException(HypervisorErrorCode.ERROR_INVALID_HYPERV, e.getMessage());
        }
        return false;
    }
    
    @Override
    public void close() {
        this.hypervisor.close();
    }
    
    @Override
    public List<SubHypervisorInfo> getSubHosts() {
        return new ArrayList<SubHypervisorInfo>();
    }
    
    @Override
    public String createVM(SubHypervisorInfo subHost, VMCreationConfiguration config) throws HypervisorException {
        try {
            String imageFilePath = "";
            CDRomInfo cdInfo = config.getCdInfo();
            if (cdInfo != null) {
                if ((cdInfo.getStorage() != null) && !cdInfo.getStorage().isEmpty())
                    imageFilePath = cdInfo.getStorage() + "\\";
                if ((cdInfo.getFilename() != null) && !cdInfo.getFilename().isEmpty())
                    imageFilePath = (imageFilePath + cdInfo.getFilename()).replace("\\\\", "\\");
            }
            List<SimpleEntry<String, Long>> disks = new ArrayList<SimpleEntry<String, Long>>();
            if (config.getDisks() != null) {
                for (DiskInfo disk : config.getDisks()){
                    disks.add(new AbstractMap.SimpleEntry<String, Long>(
                        disk.getDiskController() + "\n" + (disk.getStorage() == null ? "" : disk.getStorage()), disk.getSize() * 1024l));
                }
            }
            if (config.getBootOption() == null) {
                config.setBootOption(BootDeviceType.DISK);
            }
            List<SimpleEntry<String, String>> adapters = new ArrayList<SimpleEntry<String, String>>();
            for (NetworkInfo network : config.getTargetNetwork()) {
            	String mac = randomMacString();
                SimpleEntry<String,String> adapter = new SimpleEntry<String,String>(mac,network.getNetworkName());
                adapters.add(adapter);
                network.setMacAddress(this.normalizeMAC(mac));
                
            }
            if (config.getVmVersion() != null && config.getVmVersion().equals(VMCreationConfiguration.HYPERV_VM_GENERATION_2)) {
                if(!this.hypervisor.supportsGeneration2VM())
                    throw new HypervisorException(HypervisorErrorCode.ERROR_CREATE_VM, "Host HyperV does not support generation 2 vm.");
                return this.hypervisor.createVM(2,
                    config.getTargetStorage() == null ? "" : config.getTargetStorage(), config.getName(), config.getBootOption() == null ? null
                        : config.getBootOption().toString(), config.getCpuCount(),
                    config.getMemorySize(), adapters, imageFilePath, disks);
            } else
                return this.hypervisor.createVM(
                	config.getTargetStorage() == null ? "" : config.getTargetStorage(), config.getName(), config.getBootOption() == null ? null
                        : config.getBootOption().toString(), config.getCpuCount(),
                    config.getMemorySize(), adapters, imageFilePath, disks);
        } catch (HyperVException hve) {
        	logger.error("HyperVException ",hve);
            throw new HypervisorException(hve.getErrorCode(), hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        } catch (Exception e) {
            logger.error("Failed to create VM on HyperV server. ", e);
            throw new HypervisorException(HypervisorErrorCode.ERROR_CREATE_VM, e.getMessage());
        }
    }
    
    private String randomMacString() {
        String randomPart = "";
        for (int i = 0; i < 3; i++)
            randomPart += String.format("%1$2X", (byte) (Math.random() * 256));
        return "00155d" + randomPart.replaceAll(" ", "0");
    }
    
    @Override
    public boolean deleteVM(String uuid) throws HypervisorException {
        try {
            return this.hypervisor.deleteVM(uuid);
        } catch(HyperVException hve) {
            throw new HypervisorException(hve.getErrorCode(), hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        } catch (Exception e) {
            logger.error("Failed to delete VM(" + uuid + ") on HyperV server. ", e);
            throw new HypervisorException(HypervisorErrorCode.ERROR_DELETE_VM, e.getMessage());
        }
    }
    
    @Override
    public boolean startVM(String uuid) throws HypervisorException {
        try {
            return this.hypervisor.startVM(uuid);
        } catch(HyperVException hve) {
            throw new HypervisorException(hve.getErrorCode(), hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        } catch (Exception e) {
            logger.error("Failed to start VM(" + uuid + ") on HyperV server. ", e);
            throw new HypervisorException(HypervisorErrorCode.ERROR_START_VM, e.getMessage());
        }
    }
    
    @Override
    public boolean stopVM(String uuid) throws HypervisorException {
        try {
            return this.hypervisor.stopVM(uuid);
        } catch (Exception e) {
            logger.error("Failed to stop VM(" + uuid + ") on HyperV server. ", e);
            return false;
        }
    }
    
    @Override
    public List<VirtualMachineInfo> getVMList(SubHypervisorInfo subHost) {
        List<VirtualMachineInfo> vms = new ArrayList<VirtualMachineInfo>();
        
        try {
            for (Entry<String, List<String>> vm : this.hypervisor.getMACListForVMs().entrySet()) {
                VirtualMachineInfo vmInfo = new VirtualMachineInfo();
                List<String> adapters = this.normalizeMACs(vm.getValue());
                List<NetworkInfo> networkAdapters = new ArrayList<NetworkInfo>();
                for(String a : adapters){
                	NetworkInfo adapter = new NetworkInfo();
    				adapter.setAdapterType("");
    				adapter.setMacAddress(a);
    				adapter.setNetworkName("");
    				networkAdapters.add(adapter);
                }
                vmInfo.setNetworkAdapters(networkAdapters);
                vmInfo.setUuid(vm.getKey());
                vms.add(vmInfo);
            }
        } catch (Exception e) {
            logger.error("Failed to list VM's on HyperV server. ", e);
            return vms;
        }
        
        return vms;
    }
    
    @Override
    public VirtualMachineInfo getVirtualMachineInfo(String uuid) {
        VirtualMachineInfo vmInfo = new VirtualMachineInfo();
        
        try {
            vmInfo.setUuid(uuid);
            List<SimpleEntry<String, String>> adapters = this.hypervisor.getVirtualMachineMACs(uuid);
            //List<String> adapters = this.normalizeMACs(this.hypervisor.getVirtualMachineMACs(uuid));
            List<NetworkInfo> networkAdapters = new ArrayList<NetworkInfo>();
            for(SimpleEntry<String, String> a : adapters){
            	NetworkInfo adapter = new NetworkInfo();
				adapter.setAdapterType("");
				adapter.setMacAddress(this.normalizeMAC(a.getKey()));
				adapter.setNetworkName(a.getValue());
				networkAdapters.add(adapter);
            }
            vmInfo.setNetworkAdapters(networkAdapters);
            vmInfo.setVmName(this.hypervisor.getVirtualMachineName(uuid));
        } catch (Exception e) {
            logger.error("Failed to get info of VM(" + uuid + ") on HyperV server. ", e);
            return null;
        }
        
        return vmInfo;
    }
    
    @Override
    public VirtualMachineDefaultSetting getVirtualMachineDefaultSetting(String subHost, String guestOS) {
        // No template in HyperV, ignore guestOS
        VirtualMachineDefaultSetting defaultSetting = new VirtualMachineDefaultSetting();
        defaultSetting.setScsiController("SCSI");
        defaultSetting.setIdeController("IDE");
        // NO default values for HyperV
        
        return defaultSetting;
    }
    
    @Override
    public List<String> getNetworkList(SubHypervisorInfo subHost) {
        try {
            return this.hypervisor.getHypervisorNetworkList();
        } catch (Exception e) {
            logger.error("Failed to list host network on HyperV server. ", e);
            return new ArrayList<String>();
        }
        
    }
    
    @Override
    public String createHypervisorNetwork(String subHost, String networkName) throws HypervisorException {
        try {
            return this.hypervisor.createHypervisorNetwork(networkName);
        } catch(HyperVException hve) {
            throw new HypervisorException(hve.getErrorCode(), hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        } catch (Exception e) {
            logger.error("Failed to create host network(" + networkName + ") on HyperV server. ", e);
            throw new HypervisorException(HypervisorErrorCode.ERROR_CREATE_NETWORK, e.getMessage());
        }
    }
    
    @Override
    public boolean removeHypervisorNetwork(String subHost, String networkName) throws HypervisorException {
        try {
            return this.hypervisor.removeHypervisorNetwork(networkName);
        } catch(HyperVException hve) {
            throw new HypervisorException(hve.getErrorCode(), hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        } catch (Exception e) {
            logger.error("Failed to delete host network(" + networkName + ") on HyperV server. ", e);
            throw new HypervisorException(HypervisorErrorCode.ERROR_CREATE_NETWORK, e.getMessage());
        }
    }
    
    @Override
    public boolean changeCDRom(String uuid, CDRomInfo cdInfo) throws HypervisorException {
        String imageFilePath = "";
        if (cdInfo != null) {
            if ((cdInfo.getStorage() != null) && !cdInfo.getStorage().isEmpty())
                imageFilePath = cdInfo.getStorage() + "\\";
            if ((cdInfo.getFilename() != null) && !cdInfo.getFilename().isEmpty())
                imageFilePath = (imageFilePath + cdInfo.getFilename()).replace("\\\\", "\\");
        }
        try {
            return this.hypervisor.changeCDRom(uuid, imageFilePath);
        } catch(HyperVException hve) {
            throw new HypervisorException(hve.getErrorCode(), hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        } catch (Exception e) {
            logger.error("Failed to change DVD ROM for VM(" + uuid + ") on HyperV server. ", e);
            throw new HypervisorException(HypervisorErrorCode.ERROR_CHANGE_CD_ROM, e.getMessage());
        }
    }
    
    @Override
    public String addNicCard(String vmuuid, String subHost, NetworkInfo networkInfo) throws HypervisorException {
        try {
        	return this.normalizeMAC(this.hypervisor.addNicCard(vmuuid, networkInfo.getAdapterType(),randomMacString(),
                networkInfo.getNetworkName()));
        } catch(HyperVException hve) {
            throw new HypervisorException(hve.getErrorCode(), hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        } catch (Exception e) {
            logger.error("Failed to add network adapter for VM(" + vmuuid + ") on HyperV server. ", e);
            throw new HypervisorException(HypervisorErrorCode.ERROR_CONFIG_NIC, e.getMessage());
        }
    }
    
    @Override
    public boolean removeNicCard(String vmuuid, String subHost, String networkName) {
        try {
            return this.hypervisor.removeNicCard(vmuuid, networkName);
        } catch (Exception e) {
            logger.error("Failed to remove network adapter(host network " + networkName + ") for VM(" + vmuuid +
                ") on HyperV server. ", e);
            return false;
        }
    }
    
    @Override
    public String createSnapshot(String vmuuid, String snapshotName, String description) {
        try {
            return this.hypervisor.createSnapshot(vmuuid, snapshotName, description);
        } catch (Exception e) {
            logger.error("Failed to create snapshot for VM(" + vmuuid + ") on HyperV server. ", e);
            return null;
        }
    }
    
    @Override
    public boolean removeSnapshot(String vmuuid, String snapshotId) {
        try {
            return this.hypervisor.removeSnapshot(vmuuid, snapshotId);
        } catch (Exception e) {
            logger.error("Failed to delete snapshot(" + snapshotId + ") for VM(" + vmuuid + ") on HyperV server. ", e);
            return false;
        }
    }
    
    @Override
    public int enableChangeTracking(String vmuuid, boolean isEnable) {
        throw new Error("Not implementing vmware specific functionality.");
    }
    
    @Override
    public List<SnapshotDiskInfo> getSnapshotDiskInfo(String vmuuid, String snapshotId) {
        throw new Error("Not implementing vmware specific functionality.");
    }
    
    @Override
    public SnapshotDiskSectorInfo generateBitMapForDisk(String vmuuid, String snapshotId, String diskChangeId,
        Integer diskDeviceKey, String filePath, Integer chunkSize, Integer sectorSize) {
        throw new Error("Not implementing vmware specific functionality.");
    }
    
    @Override
    public List<ResourcePool> getResourcePoolList(SubHypervisorInfo subHost) {
        /** HyperV have no *obvious* concept of resource pool, set to null list for now. */
        return new ArrayList<ResourcePool>();
    }
    
    @Override
    public List<StorageInfo> getStorageList(SubHypervisorInfo subHost) {
        /** HyperV have no concept of Storage. */
        return new ArrayList<StorageInfo>();
    }
    
    @Override
    public int putFile(HypervisorInfo hypervisor, String subHost, String datastore, String filename, String filePath)
        throws HypervisorException {
        try {
            return this.hypervisor.putFile(hypervisor.getName(), hypervisor.getUsername(), hypervisor.getPassword(),
                datastore, filename, filePath);
        } catch(HyperVException hve) {
            throw new HypervisorException(hve.getErrorCode(), hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        } catch (Exception e) {
            logger.error("Failed to upload file(" + filePath + ") to HyperV server(" + datastore + ", " + filename +
                ") . ", e);
            throw new HypervisorException(HypervisorErrorCode.ERROR_PUT_FILE, e.getMessage());
        }
    }
    
    @Override
    public int deleteFile(HypervisorInfo hypervisor, String subHost, String datastore, String filename)
        throws HypervisorException {
        try {
            return this.hypervisor.deleteFile(datastore, filename);
        } catch(HyperVException hve) {
            throw new HypervisorException(hve.getErrorCode(), hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        } catch (Exception e) {
            logger.error("Failed to remove file(" + datastore + ", " + filename + ") from HyperV server. ", e);
            throw new HypervisorException(HypervisorErrorCode.ERROR_DELETE_FILE, e.getMessage());
        }
    }
    
    private String normalizeMAC(String mac) throws Exception {
        if(mac == null || mac.isEmpty()) 
            throw new Exception("Empty MAC address found! Possible error already occurred in previous steps.");
        else {
            if(mac.length() == 12) {
                String result = "";
                for(int i = 0; i < 6; i ++) result += mac.substring(i * 2, i * 2 + 2) + ":";
                return result.replaceAll(":$", "");
            } else if(mac.length() == 17) return mac;
            else throw new Exception("Invalid MAC address(" + mac + ") found! Possible error already occurred in previous steps.");
        }
    }
    
    private List<String> normalizeMACs(List<String> macs) throws Exception {
        List<String> result = new ArrayList<String>();
        
        for(String mac: macs) result.add(this.normalizeMAC(mac));
        
        return result;
    }

    @Override
    public boolean addSerialPort(String vmuuid, String subHost,
            String datastore, String fileName) throws HypervisorException {
        return true;
    }

    @Override
    public boolean addConfigurationParameter(String vmuuid, String subHost,
            Map<String, String> parameters) throws HypervisorException {
        boolean ret = true;
    	if(parameters != null){
        	for(String key : parameters.keySet()){
        		try {
					ret = this.hypervisor.setVirtualMachineProperty(vmuuid, key, parameters.get(key));
				} catch (Exception e) {
					logger.error("Failed to add configuration parameter.",e);
				}
        		if(!ret){
        			logger.error("Failed to add configuration paramter key: " + key + " value: " + parameters.get(key));
        		}
        	}
        }
    	return ret;
    }

    @Override
    public boolean changeBootOrder(String vmuuid, BootDeviceType bootDevice)
            throws HypervisorException {
    	try {
            this.hypervisor.changeBootOrder(vmuuid, bootDevice.toString());
            return true;
        } catch(HyperVException hve) {
            throw new HypervisorException(hve.getErrorCode(), hve.getErrorMessage(),hve.getHyperVSystemErrorMessage());
        } catch (Exception e) {
            logger.error("Failed to change boot order.",e);
            return false;
        } 
    	
    }

    @Override
    public String createNasDatastore(SubHypervisorInfo subHost,
            NasDatastoreInfo datastore) throws HypervisorException {
        return null;
    }

    @Override
    public boolean removeDatastore(SubHypervisorInfo subHost,
            String datastoreName) throws HypervisorException {
        return true;
    }

    @Override
    public int getHypervisorLevel() {
        return HypervisorInfo.HYPERVISOR_LEVEL_TOP;
    }

    @Override
    public PowerState getVMPowerStatus(String vmuuid) {
        try {
            switch(this.hypervisor.getVMPowerStatus(vmuuid)) {
            case 2:        //Enabled
            case 32770:    //Starting
                return PowerState.PoweredOn;
            case 3:        //Disabled
            case 32774:    //Stopping
                return PowerState.PoweredOff;
            case 32768:    //Paused
            case 32769:    //Suspended
            case 32771:    //Snapshotting
            case 32773:    //Saving
            case 32776:    //Pausing
            case 32777:    //Resuming
                return PowerState.Suspended;
            case 0:        //Unknown
            default: return PowerState.ErrorFault;
            }           
        } catch(Exception e) {
            logger.error("Failed to get vm(" + vmuuid + ") power status.", e);
            return PowerState.ErrorFault;
        }
    }
   
    @Override
    public boolean uploadVMNvRamFile(String vmuuid, String file) {
        return true;
    }

    @Override
    public boolean isSupportedVM(int requireVmVersion) {
        boolean isSupportG2 = this.hypervisor.supportsGeneration2VM();
        int supportedVersion = isSupportG2 ? 2 : 1;
        return supportedVersion >= requireVmVersion;
    }

	@Override
	public String checkStorageExist(SubHypervisorInfo subHost,String storageName) {
		if(storageName == null || storageName.equals("")){
			return "";
		}
		try {
			if(hypervisor.getDirectoryExists(storageName)){
				return storageName;
			}else{
				return null;
			}
		} catch (Exception e) {
			logger.error("checkStorageExist exception ",e);
			return null;
		}
	}

	@Override
	public void refreshDatastore(SubHypervisorInfo subHost, String datastoreName) {
		
	}

	@Override
	public boolean shutdownOS(String uuid) throws HypervisorException {
		return this.stopVM(uuid);
	}
}
