package com.arcserve.hyperv;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

public interface APIContract {
    /**
     * disks each entry example: <[String]"{controller}\n{storage}", [Long]size>
     * @param generation TODO
     * */
    public String createVM(int generation, String vmStoragePath, String vmName, String bootOption,
        Integer cpuCount, Long memorySize, List<SimpleEntry<String, String>> nicToAddByMacAndHostNetworkName, String cdPath, List<SimpleEntry<String, Long>> disks)
        throws HyperVException;
    
    public boolean deleteVM(String uuid) throws HyperVException;
    
    public boolean startVM(String uuid) throws HyperVException;
    
    public boolean stopVM(String uuid) throws HyperVException;
    
    public Map<String, List<String>> getMACListForVMs() throws HyperVException;
    
    public List<SimpleEntry<String, String>> getVirtualMachineMACs(String uuid) throws HyperVException;
    
    public List<String> getHypervisorNetworkList() throws HyperVException;
    
    public String createHypervisorNetwork(String networkName) throws HyperVException;
    
    public boolean removeHypervisorNetwork(String networkName) throws HyperVException;
    
    /** We'll always remove things and re-add with new CDRomInfo */
    public boolean changeCDRom(String uuid, String imageFilePath) throws HyperVException;
    
    public void changeBootOrder(String vmUuid, String bootOption) throws HyperVException;
    
    public String addNicCard(String vmuuid, String adapterType, String mac, String networkName) throws HyperVException;
    
    /** Removes all NICs related to the specified network */
    public boolean removeNicCard(String vmuuid, String networkName) throws HyperVException;
    
    public String createSnapshot(String vmuuid, String snapshotName, String description) throws HyperVException;
    
    public boolean removeSnapshot(String vmuuid, String snapshotId) throws HyperVException;
    
    /** datastore is used as parent folder, ie. full file path is always [`datastore' + '\' +] `filename' */
    public int putFile(String targetHost, String userName, String password, String datastore, String filename,
        String filePath) throws HyperVException;
    
    /** datastore is used as parent folder, ie. full file path is always [`datastore' + '\' +] `filename' */
    public int deleteFile(String datastore, String filename) throws HyperVException;
    
    public String[] getVirtualDisksList(String vmUuid) throws HyperVException;
    
    public String getVMPowerState(String vmuuid) throws HyperVException; 
    
    public String getVirtualMachineName(String vmuuid) throws HyperVException;
    
    public boolean getDirectoryExists(String path) throws HyperVException;
    
    public boolean setVirtualMachineProperty(String vmuuid, String key, String val) throws HyperVException;
    
    public String getVirtualMachineProperty(String vmuuid, String prop) throws HyperVException;
}
