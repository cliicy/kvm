package com.arcserve.kvm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.AbstractMap.SimpleEntry;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.apache.log4j.Logger;
import org.openwsman.Client;

import com.arcserve.linux.WinCommand;
import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.Classes.ClassInstance;
import com.arcserve.winrm.Classes.ClassInstanceProperty;
import com.arcserve.winrm.Classes.ClassReference;
import com.arcserve.winrm.cimv2.File.Copy;
import com.arcserve.winrm.cimv2.File.Delete;
import com.arcserve.winrm.cimv2.File.Rename;
import com.arcserve.winrm.hyperv.v1.Storage.CreateDynamicVirtualHardDisk;
import com.arcserve.winrm.hyperv.v1.VirtualSystem.RequestStateChange;
import com.arcserve.winrm.hyperv.v1.Management.AddVirtualSystemResources;
import com.arcserve.winrm.hyperv.v1.Management.GetSummaryInformation;
import com.arcserve.winrm.hyperv.v1.Management.CreateVirtualSystemSnapshot;
import com.arcserve.winrm.hyperv.v1.Management.ModifyVirtualSystemResources;
import com.arcserve.winrm.hyperv.v1.Management.RemoveVirtualSystemResources;
import com.arcserve.winrm.hyperv.v1.Management.RemoveVirtualSystemSnapshot;
import com.arcserve.winrm.hyperv.v1.Networking.CreateSwitch;
import com.arcserve.winrm.hyperv.v1.Networking.CreateSwitchPort;
import com.arcserve.winrm.hyperv.v1.Networking.DeleteSwitch;
import com.arcserve.winrm.hyperv.v1.Management.DestroyVirtualSystem;
import com.arcserve.winrm.hyperv.v1.Management.DefineVirtualSystem;
import com.arcserve.winrm.hyperv.v1.Management.ModifyVirtualSystem;

public abstract class APIv1 {
    private static Logger logger = Logger.getLogger(APIv1.class);
    
    public static APIContract createAgainstTarget(final Classes classes, final Client targetServer, final String serverName, final String systemRoot) {
        return new APIContract() {
            private String getDefaultVMCfgPath(ClassInstance svcSetting) {
                if(svcSetting == null) 
                    svcSetting = classes.createReference("Msvm_VirtualSystemManagementServiceSettingData").enumerateInstances(targetServer)[0];
                return svcSetting.getProperty("DefaultExternalDataRoot").getSimpleValue();
            }
            
            private String getDefaultVMDiskPath(ClassInstance svcSetting) {
                if(svcSetting == null) 
                    svcSetting = classes.createReference("Msvm_VirtualSystemManagementServiceSettingData").enumerateInstances(targetServer)[0];
                return svcSetting.getProperty("DefaultVirtualHardDiskPath").getSimpleValue();
            }
            @Override
            public String createVM(int generation, String vmStoragePath, String vmName, String bootOption,
                Integer cpuCount, Long memorySize, List<SimpleEntry<String, String>> nicToAddByMacAndHostNetworkName, String cdPath, List<SimpleEntry<String, Long>> disks)
                throws HyperVException {
                if(generation != 1) 
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Cannot create generation " + generation + " vm on this server!");
                /** Get server settings */
                ClassInstance[] svcSettings = classes.createReference("Msvm_VirtualSystemManagementServiceSettingData").enumerateInstances(
                    targetServer);
                assert (svcSettings.length == 1) : "Couldn't get Msvm_VirtualSystemManagementServiceSettingData";
                
                if(this.isDuplicateVMName(vmName))
                    vmName = String.format("%s %d", vmName, System.currentTimeMillis());
                
                /** Create the VM */
                DefineVirtualSystem.Output output;
                String vmUuid;
                try {
                    if ((vmStoragePath == null) || vmStoragePath.isEmpty())
                        output = DefineVirtualSystem.invoke(classes, targetServer, serverName, vmName, getDefaultVMCfgPath(svcSettings[0]));
                    else 
                        output = DefineVirtualSystem.invoke(classes, targetServer, serverName, vmName, vmStoragePath);
                    int ret = output.getReturnValue();
                    
                    if (ret == 4096) {
                        /** In *experience*, this case will *never* happen on successful VM creation.
                         *  If this branch is reached, it must be some error happened.
                         *  But, if indeed this branch shows a successful VM creation, we cannot handle it.
                         *  And will throw exception.
                         * */
                        ConcreteJob job = output.getConcreteJobRef();
                        try {
                            while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    continue;
                                }
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, job.toString(), job.getErrorDescription());
                        else
                            /** Throws even if it succeeded... */
                            throw new Exception("Unexpected!");
                    } else if (ret != 0)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to create vm.\n" + ret + ": " + output.getReturnValueDescription());
                    
                    vmUuid = output.getResultingSystemUuid();
                    
                    if ((vmUuid == null) || vmUuid.isEmpty())
                        throw new Exception("Failed to create vm.");
                } catch(HyperVException hve) {
                    throw hve;
                } catch(Exception invokeEx) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, invokeEx.getMessage());
                }
                
                
                /** Configure created VM */
                try {
                    ClassReference vssdRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                        "InstanceID", "Microsoft:" + vmUuid);
                    ClassInstance vssd = vssdRef.getInstance(targetServer);
                    if (vssd == null)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to init winrm classes resource.");
                    
                    changeBootOrder(vmUuid, vssd, bootOption);
//                    if(bootOption != null) {
//                        /** Set boot order */
//                        switch (bootOption) {
//                        case "CD":
//                            vssd.getProperty("BootOrder").setSimpleArrayValue(new String[] { "1", "2", "0", "3" });
//                            break;
//                        case "DISK":
//                            vssd.getProperty("BootOrder").setSimpleArrayValue(new String[] { "2", "1", "0", "3" });
//                            break;
//                        default:
//                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Unknown boot order option " + bootOption);
//                        }
//                    }
//                    
//                    ModifyVirtualSystem.Output adjBootRes = ModifyVirtualSystem.invoke(classes, targetServer, serverName, vmUuid, vssd);
//                    int adjBootRet = adjBootRes.getReturnValue();
//                    
//                    if (adjBootRet == 4096) {
//                        ConcreteJob job = adjBootRes.getConcreteJobRef();
//                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
//                            try {
//                                Thread.sleep(100);
//                            } catch (InterruptedException e) {
//                                continue;
//                            }
//                        }
//                        if (!job.isSuccess())
//                            throw new Exception(job.toString());
//                    } else if (adjBootRet != 0)
//                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, adjBootRes.getReturnValueDescription());
                    
                    /** Get processor setting */
                    String processorSettingUuid = null;
                    for (ClassInstance inst : vssdRef.relatedInstances(targetServer, "Msvm_ProcessorSettingData"))
                        processorSettingUuid = inst.getProperty("InstanceID").getSimpleValue();
                    /** Get memory setting */
                    String memorySettingUuid = null;
                    for (ClassInstance inst : vssdRef.relatedInstances(targetServer, "Msvm_MemorySettingData"))
                        memorySettingUuid = inst.getProperty("InstanceID").getSimpleValue();
                    
                    if (processorSettingUuid == null || memorySettingUuid == null || processorSettingUuid.isEmpty() ||
                        memorySettingUuid.isEmpty())
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Invalid VM configuration.");
                    
                    /** Update memory and processor */
                    ClassInstance processorSetting = classes.createInstance("Msvm_ProcessorSettingData");
                    ClassInstance memorySetting = classes.createInstance("Msvm_MemorySettingData");
                    processorSetting.getProperty("InstanceID").setSimpleValue(processorSettingUuid);
                    processorSetting.getProperty("VirtualQuantity").setSimpleValue(cpuCount.toString());
                    memorySetting.getProperty("InstanceID").setSimpleValue(memorySettingUuid);
                    memorySetting.getProperty("VirtualQuantity").setSimpleValue(memorySize.toString());
                    
                    ModifyVirtualSystemResources.Output adjRes = ModifyVirtualSystemResources.invoke(classes, targetServer, serverName, vmUuid,
                        processorSetting, memorySetting);
                    int adjResRet = adjRes.getReturnValue();
                    
                    if (adjResRet == 4096) {
                        ConcreteJob job = adjRes.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, job.toString(), job.getErrorDescription());
                    } else if (adjResRet != 0)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, adjRes.getReturnValueDescription());
                    
                    /** Create default NIC targetServerected to specified network */
                    if (nicToAddByMacAndHostNetworkName != null) {
                        for(SimpleEntry<String, String> nicHostNetwork: nicToAddByMacAndHostNetworkName) {
                            if(nicHostNetwork != null) {
                                String mac = this.addNicCard(vmUuid, null, nicHostNetwork.getKey(), nicHostNetwork.getValue());
                                if ((mac == null) || mac.isEmpty())
                                    throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, "Failed to create nic for vm");
                            }
                        }
                    }
                    
                    /** Find the default IDE controllers */
                    ClassInstance[] ideControllers = new ClassInstance[] { null, null };
                    for (ClassInstance inst : vssdRef.relatedInstances(targetServer, "Msvm_ResourceAllocationSettingData")) {
                        String subType = inst.getProperty("ResourceSubType").getSimpleValue();
                        if ((subType != null) && subType.equals("Microsoft Emulated IDE Controller"))
                            ideControllers[Integer.parseInt(inst.getProperty("Address").getSimpleValue())] = inst;
                    }
                    if ((ideControllers[0] == null) || (ideControllers[1] == null))
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Could not find the default IDE controller");
                    
                    /** Add our default SCSI controller */
                    ClassInstance defSCSI = getResDefaultSettingData(6, "Microsoft Synthetic SCSI Controller");
                    if (defSCSI == null)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Could not find the default SCSI controller");
                    
                    AddVirtualSystemResources.Output addSCSIRes = AddVirtualSystemResources.invoke(classes, targetServer, serverName, vmUuid,
                        defSCSI);
                    int addSCSIResRet = addSCSIRes.getReturnValue();
                    
                    if (addSCSIResRet == 4096) {
                        ConcreteJob job = addSCSIRes.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, job.toString(), job.getErrorDescription());
                        else
                            /** Sorry :'( */
                            throw new Exception("Sorry :'(");
                    } else if (addSCSIResRet != 0)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, addSCSIRes.getReturnValueDescription());
                    
                    ClassInstance scsiController = null;
                    for (ClassInstance inst : vssdRef.relatedInstances(targetServer, "Msvm_ResourceAllocationSettingData")) {
                        String subType = inst.getProperty("ResourceSubType").getSimpleValue();
                        if ((subType != null) && subType.equals("Microsoft Synthetic SCSI Controller"))
                            scsiController = inst;
                    }
                    if (scsiController == null)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Cannot get Microsoft Synthetic SCSI Controller");
                    
                    /** Attach CD and set ISO if needed, always on IDE0/0 */
                    if ((cdPath != null) && (!cdPath.isEmpty())) {
                        if(cdPath.indexOf(":") < 0) {
                            // the cd is put to default vm disk folder
                            cdPath = getDefaultVMDiskPath(svcSettings[0]) + "\\" + cdPath;
                        }
                        if(!addDiskImage(vmUuid, "Microsoft Synthetic DVD Drive", "Microsoft Virtual CD/DVD Disk", ideControllers[0], 0, cdPath))
                            throw new HyperVException(HyperVException.ERROR_CHANGE_CD_ROM, "Failed to attach ISO");
                    }
                    
                    /** Create disks, support 3 IDE and 64(HyperV actually supports 64 * 4) SCSI hard drives */
                    int currentIDEDiskCount = 0;
                    int currentSCSIDiskCount = 0;
                    if ((disks != null) && (disks.size() > 0))
                        for (SimpleEntry<String, Long> disk : disks) {
                            String controller = disk.getKey().split("\n", 2)[0];
                            String storage = disk.getKey().split("\n", 2)[1];
                            Long size = disk.getValue();
                            
                            if ((currentIDEDiskCount > 3) || (currentSCSIDiskCount > 64))
                                throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Too many disks");
                            /** Create the disk image first */
                            String fullPath = null;
                            String fileName = vmName.replaceAll("[\\\\/:*\"\\?<>|]", "_") + "-" + vmUuid + "-" + (currentIDEDiskCount + currentSCSIDiskCount) + ".vhd";
                            if ((storage != null) && !storage.isEmpty())
                                fullPath = storage + "\\" + fileName;
                            else if ((vmStoragePath != null) && !vmStoragePath.isEmpty())
                                fullPath = vmStoragePath + "\\" + fileName;
                            else
                                fullPath = getDefaultVMDiskPath(svcSettings[0]) + "\\" + fileName;
                            fullPath = fullPath.replace("\\\\", "\\");
                            
                            CreateDynamicVirtualHardDisk.Output createVHDRes = CreateDynamicVirtualHardDisk.invoke(
                                classes, targetServer, serverName, fullPath, size);
                            int createVHDResRet = createVHDRes.getReturnValue();
                            
                            if (createVHDResRet == 4096) {
                                ConcreteJob job = createVHDRes.getConcreteJobRef();
                                while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        continue;
                                    }
                                }
                                if (!job.isSuccess())
                                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, job.toString(), job.getErrorDescription());
                            } else if (createVHDResRet != 0)
                                throw new HyperVException(HyperVException.ERROR_CREATE_VM, createVHDRes.getReturnValueDescription());
                            
                            /** Attach to specified controller */
                            String diskControllerType = controller.toUpperCase();
                            if (diskControllerType.equals("IDE")) {
                                if (!addDiskImage(vmUuid, "Microsoft Synthetic Disk Drive", "Microsoft Virtual Hard Disk",
                                    ideControllers[(currentIDEDiskCount + 1) / 2], (currentIDEDiskCount + 1) % 2, fullPath))
                                    throw new Exception("Failed to add IDE disk");
                                currentIDEDiskCount += 1;
                            } else if (diskControllerType.equals("SCSI")) {
                                if (!addDiskImage(vmUuid, "Microsoft Synthetic Disk Drive", "Microsoft Virtual Hard Disk",
                                    scsiController, currentSCSIDiskCount, fullPath))
                                    throw new Exception("Failed to add SCSI disk");
                                currentSCSIDiskCount += 1;
                            } else
                                throw new Exception("Unsupported disk controller type " + diskControllerType);
                        }
                } catch(HyperVException hve) {
                  throw hve;  
                } catch(Exception e) {
                    this.deleteVM(vmUuid);
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to configure created vm. " + e.getMessage());
                }
                
                return vmUuid;
            }
            
            private boolean isDuplicateVMName(String vmName) {
                try {
                    GetSummaryInformation.Output output = GetSummaryInformation.invoke(classes, targetServer,
                        serverName);
                    int ret = output.getReturnValue();
                    
                    if (ret != 0)
                        throw new Exception(output.getReturnValueDescription());


                    for (ClassInstance summary : output.getSummaries()) 
                        if(vmName.equalsIgnoreCase(this.getVirtualMachineName(summary.getProperty("Name").getSimpleValue())))
                            return true;
                    return false;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public boolean deleteVM(String uuid) throws HyperVException {
                try {
                    DestroyVirtualSystem.Output output = DestroyVirtualSystem.invoke(classes, targetServer, serverName, uuid);
                    int ret = output.getReturnValue();
                    
                    if (ret == 0)
                        return true;
                    else if (ret == 4096) {
                        ConcreteJob job = output.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_DELETE_VM, job.toString(), job.getErrorDescription());
                        else
                            return true;
                    } else
                        throw new Exception(output.getReturnValueDescription());
                } catch (HyperVException hve) {
                    throw hve;
                } catch(Exception e) {
                    throw new HyperVException(HyperVException.ERROR_DELETE_VM, e.getMessage());
                }
            }
            
            @Override
            public boolean startVM(String uuid) throws HyperVException {
                try {
                    RequestStateChange.Output output = RequestStateChange.invoke(classes, targetServer, uuid,
                        RequestStateChange.VmState.Enabled);
                    int ret = output.getReturnValue();
                    
                    if (ret == 0)
                        return true;
                    else if (ret == 4096) {
                        ConcreteJob job = output.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
                        else
                            return true;
                    } else
                        throw new Exception(output.getReturnValueDescription());                    
                } catch (HyperVException hve) {
                    throw hve;
                } catch(Exception e) {
                    throw new HyperVException(HyperVException.ERROR_START_VM, e.getMessage());
                }
            }
            
            @Override
            public boolean stopVM(String uuid) throws HyperVException {
                try {
                    RequestStateChange.Output output = RequestStateChange.invoke(classes, targetServer, uuid,
                        RequestStateChange.VmState.Disabled);
                    int ret = output.getReturnValue();
                    
                    if (ret == 0)
                        return true;
                    else if (ret == 4096) {
                        ConcreteJob job = output.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_STOP_VM, job.toString(), job.getErrorDescription());
                        else
                            return true;
                    } else
                        throw new Exception(output.getReturnValueDescription());
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_STOP_VM, e.getMessage());
                }
            }
            
            @Override
            public Map<String, List<String>> getMACListForVMs() throws HyperVException {
                try {
                    Map<String, List<String>> vms = new HashMap<String, List<String>>();
                    
                    GetSummaryInformation.Output output = GetSummaryInformation.invoke(classes, targetServer,
                        serverName);
                    int ret = output.getReturnValue();
                    
                    if (ret != 0)
                        throw new Exception(output.getReturnValueDescription());
                    
                    for (ClassInstance summary : output.getSummaries()) {
                        String vmuuid = summary.getProperty("Name").getSimpleValue();
                        List<String> MACs = new ArrayList<String>();
                        
                        for (ClassInstance inst : classes.createReference("Msvm_VirtualSystemSettingData")
                            .addSelector("InstanceID", "Microsoft:" + vmuuid)
                            .relatedInstances(targetServer, "Msvm_SyntheticEthernetPortSettingData"))
                            if (inst.getProperty("StaticMacAddress").getSimpleValue().toUpperCase().equals("TRUE"))
                                MACs.add(inst.getProperty("Address").getSimpleValue());
                        
                        vms.put(vmuuid, MACs);
                    }
                    
                    return vms;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, e.getMessage());
                }
            }
            
            @Override
            public List<SimpleEntry<String, String>> getVirtualMachineMACs(String uuid) throws HyperVException {
                try {
                    ClassReference targetVmRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                        "InstanceID", "Microsoft:" + uuid);
                    ClassInstance targetVm = targetVmRef.getInstance(targetServer);
                    if (targetVm == null)
                        throw new Exception("Target VM does not exist " + uuid);

                    List<SimpleEntry<String, String>> MACs = new ArrayList<SimpleEntry<String, String>>();
                    
                    for (ClassInstance inst : targetVmRef.relatedInstances(targetServer,
                        "Msvm_SyntheticEthernetPortSettingData"))
                        if (inst.getProperty("StaticMacAddress").getSimpleValue().toUpperCase().equals("TRUE")) {
                            String systemName = inst.getProperty("Connection").getSimpleArrayValue()[0];
                            int idx = systemName.indexOf("SystemName=");
                            systemName = systemName.substring(idx, idx + 48).replace("SystemName=", "").replaceAll("\"", "");
                            for (ClassInstance vs :classes.createReference("Msvm_VirtualSwitch").enumerateInstances(targetServer))
                                if(vs.getProperty("Name").getSimpleValue().equalsIgnoreCase(systemName))
                                    MACs.add(new AbstractMap.SimpleEntry<String, String>(inst.getProperty("Address").getSimpleValue(), 
                                        vs.getProperty("ElementName").getSimpleValue()));      
                        }
                    
                    return MACs;
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, e.getMessage());
                }
            }

            @Override
            public List<String> getHypervisorNetworkList() throws HyperVException {
                List<String> networks = new ArrayList<String>();
                for (ClassInstance inst : classes.createReference("Msvm_VirtualSwitch").enumerateInstances(targetServer))
                    networks.add(inst.getProperty("ElementName").getSimpleValue());
                return networks;
            }
            
            @Override
            public String createHypervisorNetwork(String networkName) throws HyperVException {
                try {
                    CreateSwitch.Output output = CreateSwitch.invoke(classes, targetServer, serverName, networkName);
                    int ret = output.getReturnValue();
                    
                    if (ret == 0)
                        return output.getResultSwitchUuid();
                    else if (ret == 4096) {
                        ConcreteJob job = output.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CREATE_NETWORK, job.toString(), job.getErrorDescription());
                        else
                            throw new Exception("ASYNC JOB");
                    } else
                        throw new HyperVException(HyperVException.ERROR_CREATE_NETWORK, output.getReturnValueDescription());
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_NETWORK, e.getMessage());
                }
            }
            
            @Override
            public boolean removeHypervisorNetwork(String networkName) throws HyperVException {
                try {
                    ClassInstance targetSwitch = null;
                    
                    for (ClassInstance inst : classes.createReference("Msvm_VirtualSwitch")
                        .enumerateInstances(targetServer, "ElementName = '" + networkName + "'"))
                        targetSwitch = inst;
                    
                    if (targetSwitch == null)
                        throw new Exception("Cannot find target switch " + networkName);
                    
                    DeleteSwitch.Output output = DeleteSwitch.invoke(classes, targetServer, serverName,
                        targetSwitch.getProperty("Name").getSimpleValue());
                    int ret = output.getReturnValue();
                    
                    if (ret == 0)
                        return true;
                    else if (ret == 4096) {
                        ConcreteJob job = output.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CREATE_NETWORK, job.toString(), job.getErrorDescription());
                        else
                            return true;
                    } else
                        throw new HyperVException(HyperVException.ERROR_CREATE_NETWORK, output.getReturnValueDescription());
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_NETWORK, e.getMessage());
                }
            }
            
            @Override
            public boolean changeCDRom(String uuid, String imageFilePath) throws HyperVException {
                try {                    
                    /** Get the VM */
                    ClassReference vssdRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                        "InstanceID", "Microsoft:" + uuid);
                    ClassInstance vssd = vssdRef.getInstance(targetServer);
                    if (vssd == null)
                        throw new HyperVException(HyperVException.ERROR_CHANGE_CD_ROM,"Cannot find target vm " + uuid);
                    
                    ArrayList<ClassInstance> cdDrives = new ArrayList<ClassInstance>(); 
                    ArrayList<ClassReference> cdImages = new ArrayList<ClassReference>();                     
                    
                    /** Remove all existing CD images, if any. And find the DVD controller, if possible. */
                    for (ClassInstance inst : vssdRef.relatedInstances(targetServer,
                        "Msvm_ResourceAllocationSettingData")) {
                        String resSubType = inst.getProperty("ResourceSubType").getSimpleValue();
                        if ((resSubType != null) && resSubType.endsWith("Microsoft Virtual CD/DVD Disk"))
                            cdImages.add(classes.createReference("Msvm_ResourceAllocationSettingData")
                                .addSelector("InstanceID", inst.getProperty("InstanceID").getSimpleValue()));
                        if ((resSubType != null) && resSubType.endsWith("Microsoft Synthetic DVD Drive"))
                            cdDrives.add(inst);
                    }
                    RemoveVirtualSystemResources.Output rmRes = RemoveVirtualSystemResources.invoke(classes,
                        targetServer, serverName, uuid,
                        cdImages.toArray(new ClassReference[cdImages.size()]));
                    int rmResRet = rmRes.getReturnValue();
                    
                    if (rmResRet == 4096) {
                        ConcreteJob job = rmRes.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CHANGE_CD_ROM, job.toString(), job.getErrorDescription());
                    } else if (rmResRet != 0)
                        throw new Exception(rmRes.getReturnValueDescription());

                    if ((imageFilePath != null) && !imageFilePath.isEmpty()) {
                        if(imageFilePath.indexOf(":") < 0) {
                            // the cd is put to default vm disk folder
                            imageFilePath = getDefaultVMDiskPath(null) + "\\" + imageFilePath;
                        }
                        
                        if(cdDrives.size() == 0) {
                            /** 
                             * No CD found, we need to creat one 
                             * Always create it on IDE controller 0 and pos 0.
                             * If this is occupied by another disk,
                             * it means user changed manually.
                             * Do not support this case.
                             * */
                            ClassInstance ideController0 = null;
                            for (ClassInstance inst : vssdRef.relatedInstances(targetServer,
                                "Msvm_ResourceAllocationSettingData")) {
                                String subType = inst.getProperty("ResourceSubType").getSimpleValue();
                                String addr = inst.getProperty("Address").getSimpleValue();
                                if ((subType != null) && subType.equals("Microsoft Emulated IDE Controller") &&
                                    (addr != null) && addr.equals("0"))
                                    ideController0 = inst;
                            }
                            if (ideController0 == null)
                                throw new HyperVException(HyperVException.ERROR_CHANGE_CD_ROM, "Cannot find Microsoft Emulated IDE Controller");
                            cdDrives.add(createDiskDrive(uuid, "Microsoft Synthetic DVD Drive", ideController0, 0));
                        }
                        
                        return addImageToDrive(uuid, cdDrives.get(0), "Microsoft Virtual CD/DVD Disk", imageFilePath);
                    }
                    
                    return true;
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CHANGE_CD_ROM, e.getMessage());
                }
            }

            @Override
            public String addNicCard(String vmuuid, String adapterType, String mac, String networkName) throws HyperVException {
                try {
                    ClassInstance targetSwitch = null;
                    
                    if ((networkName == null) || networkName.isEmpty())
                        throw new Exception("Invalid empty argument " + networkName);
                    
                    for (ClassInstance inst : classes.createReference("Msvm_VirtualSwitch")
                        .enumerateInstances(targetServer, "ElementName = '" + networkName + "'"))
                        targetSwitch = inst;
                    
                    if (targetSwitch == null)
                        throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, "Cannot find target switch " + networkName);
                    
                    if(mac == null || mac.isEmpty())
                        mac = this.randomMacString();
                    
                    CreateSwitchPort.Output csp = CreateSwitchPort.invoke(classes, targetServer, serverName,
                        targetSwitch.getProperty("Name").getSimpleValue(), "");
                    int ret = csp.getReturnValue();
                    
                    ClassInstance targetSwitchPort = null;
                    
                    if (ret == 0)
                        targetSwitchPort = csp.getResultSwitchPort().getInstance(targetServer);
                    else if (ret == 4096) {
                        ConcreteJob job = csp.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new Exception(job.toString());
                        else
                            throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, "Cannot get created switch port!", job.getErrorDescription());
                    } else
                        throw new Exception(csp.getReturnValueDescription());
                    
                    if (targetSwitchPort == null)
                        throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, "Cannot get created switch port!");
                    
                    /** Create the network card */
                    ClassInstance nicDefaultSetting = this.getResDefaultSettingData(10,
                        "Microsoft Synthetic Ethernet Port");
                    if (nicDefaultSetting == null)
                        throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, "Cannot get nicDefaultSetting!");
                    
                    nicDefaultSetting.getProperty("Connection").setSimpleArrayValue(
                        new String[] {
                        "\\\\" + serverName +
                            "\\root\\virtualization:Msvm_SwitchPort.CreationClassName=\"Msvm_SwitchPort\",Name=\"" +
                            targetSwitchPort.getProperty("Name").getSimpleValue() +
                            "\",SystemCreationClassName=\"Msvm_VirtualSwitch\",SystemName=\"" +
                            targetSwitchPort.getProperty("SystemName").getSimpleValue() + "\"" });
                    nicDefaultSetting.getProperty("ElementName").setSimpleValue("Arcserve Virtual Lab Network Adapter");
                    nicDefaultSetting.getProperty("VirtualSystemIdentifiers").setSimpleArrayValue(
                        new String[] { "{" + UUID.randomUUID().toString() + "}" });
                    nicDefaultSetting.getProperty("StaticMacAddress").setSimpleValue("TRUE");
                    nicDefaultSetting.getProperty("Address").setSimpleValue(mac);
                    
                    AddVirtualSystemResources.Output output = AddVirtualSystemResources.invoke(classes, targetServer,
                        serverName, vmuuid,
                        nicDefaultSetting);
                    ret = output.getReturnValue();
                    
                    ClassInstance targetNic = null;
                    
                    if (ret == 0)
                        targetNic = output.getCreatedResources()[0].getInstance(targetServer);
                    else if (ret == 4096) {
                        ConcreteJob job = output.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, job.toString(), job.getErrorDescription());
                        else
                            for (ClassInstance inst : classes.createReference("Msvm_SyntheticEthernetPortSettingData").enumerateInstances(
                                targetServer,
                                "ElementName = 'Arcserve Virtual Lab Network Adapter' and Address = '" +
                                    nicDefaultSetting.getProperty("Address").getSimpleValue() + "'"))
                                targetNic = inst;
                    } else
                        throw new Exception(output.getReturnValueDescription());
                    
                    if (targetNic == null)
                        throw new HyperVException(HyperVException.ERROR_CONFIG_NIC,"Failed to create target Nic");
                    
                    return targetNic.getProperty("Address").getSimpleValue();
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, e.getMessage());
                }
            }
            
            @Override
            public boolean removeNicCard(String vmuuid, String networkName) throws HyperVException {
                try {
                    /** Find target vm setting data */
                    ClassReference vssdRef = classes.createReference("Msvm_VirtualSystemSettingData")
                        .addSelector("InstanceID", "Microsoft:" + vmuuid);
                    ClassInstance vssd = vssdRef.getInstance(targetServer);
                    
                    if (vssd == null)
                        throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, "Cannot find target vm " + vmuuid);
                    
                    /** Find target switch */
                    ClassInstance targetSwitch = null;
                    
                    for (ClassInstance inst : classes.createReference("Msvm_VirtualSwitch")
                        .enumerateInstances(targetServer, "ElementName = '" + networkName + "'"))
                        targetSwitch = inst;
                    
                    if (targetSwitch == null)
                        throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, "Cannot find target switch " + networkName);
                    
                    ClassReference targetSwitchRef = classes.createReference("Msvm_VirtualSwitch")
                        .addSelector("Name", targetSwitch.getProperty("Name").getSimpleValue())
                        .addSelector("CreationClassName", "Msvm_VirtualSwitch");
                    
                    /** Ports related to this switch */
                    ClassInstance[] targetSwitchPorts = targetSwitchRef.relatedInstances(targetServer,
                        "Msvm_SwitchPort");
                    
                    /** Find NICs that uses these ports */
                    ArrayList<ClassReference> nics = new ArrayList<ClassReference>();
                    for (ClassInstance inst : vssdRef.relatedInstances(targetServer,
                        "Msvm_SyntheticEthernetPortSettingData"))
                        for (ClassInstance port : targetSwitchPorts) {
                            String[] conns = inst.getProperty("Connection").getSimpleArrayValue();
                            if ((conns != null) &&
                                (conns.length > 0) &&
                                conns[0].toUpperCase().contains(
                                    port.getProperty("SystemName").getSimpleValue().toUpperCase()) &&
                                conns[0].toUpperCase().contains(port.getProperty("Name").getSimpleValue().toUpperCase()))
                                nics.add(classes.createReference("Msvm_SyntheticEthernetPortSettingData")
                                    .addSelector("InstanceID", inst.getProperty("InstanceID").getSimpleValue()));
                        }
                    
                    /** Remove them */
                    RemoveVirtualSystemResources.Output output =
                        RemoveVirtualSystemResources.invoke(classes, targetServer, serverName, vmuuid,
                            nics.toArray(new ClassReference[nics.size()]));
                    int ret = output.getReturnValue();
                    
                    if (ret == 0)
                        return true;
                    else if (ret == 4096) {
                        ConcreteJob job = output.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, job.toString(), job.getErrorDescription());
                        else
                            return true;
                    } else
                        throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, output.getReturnValueDescription());
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, e.getMessage());
                }
            }
            
            @Override
            public String createSnapshot(String vmuuid, String snapshotName, String description) throws HyperVException {
                try {
                    CreateVirtualSystemSnapshot.Output outputCreate = CreateVirtualSystemSnapshot.invoke(classes,
                        targetServer, serverName,
                        vmuuid);
                    int ret = outputCreate.getReturnValue();
                    String snapUuid;
                    
                    if (ret == 0)
                        snapUuid = outputCreate.getResultSnapshotUuid();
                    else if (ret == 4096) {
                        ConcreteJob job = outputCreate.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, job.toString(), job.getErrorDescription());
                        else
                            snapUuid = null;
                        /** We have to find the uuid manually... so sad :( */
                    } else
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, outputCreate.getReturnValueDescription());
                    
                    if ((snapUuid == null) || snapUuid.isEmpty()) {
                        /** The snapshot is created successfully, but we need to find it by ourself ... */
                        ClassInstance vmInst = classes.createReference("Msvm_VirtualSystemSettingData")
                            .addSelector("InstanceID", "Microsoft:" + vmuuid.toUpperCase()).getInstance(targetServer);
                        /** This may not be the most accurate, but we use the latest snapshot as if it were created by us. */
                        snapUuid = vmInst.getProperty("Parent").getSimpleValue();
                        if (snapUuid == null || snapUuid.isEmpty())
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Snapshot is not created.");
                        int uuidIdx = snapUuid.lastIndexOf("Microsoft:");
                        if (uuidIdx < 0)
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Unknown Error");
                        uuidIdx += 10;
                        /** 10 is the length of "Microsoft:" */
                        snapUuid = snapUuid.substring(uuidIdx, uuidIdx + 36);
                        /** 36 is the length of uuid. */
                    }
                    
                    /** Modify the name and description of the snapshot. */
                    ClassInstance vmConfig = classes.createInstance("Msvm_VirtualSystemSettingData");
                    vmConfig.getProperty("InstanceID").setSimpleValue("Microsoft:" + snapUuid.toUpperCase());
                    vmConfig.getProperty("ElementName").setSimpleValue(snapshotName);
                    vmConfig.getProperty("Notes").setSimpleValue(description);
                    
                    ModifyVirtualSystem.Output outputModify = ModifyVirtualSystem.invoke(classes, targetServer,
                        serverName, vmuuid, vmConfig);
                    ret = outputModify.getReturnValue();
                    
                    if (ret == 0)
                        return snapUuid;
                    else if (ret == 4096) {
                        ConcreteJob job = outputModify.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, job.toString(), job.getErrorDescription());
                        else
                            return snapUuid;
                    } else
                        throw new Exception(outputModify.getReturnValueDescription());
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, e.getMessage());
                }
            }
            
            @Override
            public boolean removeSnapshot(String vmuuid, String snapshotId) throws HyperVException {
                try {
                    RemoveVirtualSystemSnapshot.Output output = RemoveVirtualSystemSnapshot.invoke(classes,
                        targetServer, serverName, snapshotId);
                    int ret = output.getReturnValue();
                    
                    if (ret == 0)
                        return true;
                    else if (ret == 4096) {
                        ConcreteJob job = output.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, job.toString(), job.getErrorDescription());
                        else
                            return true;
                    } else
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, output.getReturnValueDescription());
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, e.getMessage());
                }
            }
            
            @Override
            public int putFile(String targetHost, String userName, String password, String datastore, String filename,
                String filePath) throws HyperVException {
                String targetTempFileName = "";
                if((!filename.contains(":")) && ((datastore == null) || datastore.isEmpty()))
                    datastore = getDefaultVMDiskPath(null);
                logger.info("using datastore: " + datastore);
                try {
                    String targetFilePath = "";
                    if ((datastore != null) && !datastore.isEmpty())
                        targetFilePath = datastore + "\\";
                    if ((filename != null) && !filename.isEmpty())
                        targetFilePath = targetFilePath + filename;
                    targetFilePath = targetFilePath.replace("\\\\", "\\");
                    
                    FileInputStream fis = null;
                    SmbFileOutputStream sos = null;
                    try {
                        fis = new FileInputStream(filePath);
                        String domain = serverName;
                        String user = null;
                        logger.debug("processing username: " + userName);
                        if(userName.indexOf('\\') >= 0) {
                            domain = userName.substring(0, userName.indexOf('\\'));
                            user = userName.substring(userName.indexOf('\\') + 1);
                        } else if (userName.indexOf('/') >= 0) {
                            domain = userName.substring(0, userName.indexOf('/'));
                            user = userName.substring(userName.indexOf('/') + 1);
                        } else user = userName;
                        logger.info(String.format("Using domain/user %s/%s to log on target %s", domain, user, targetHost));
                        if(!getDirectoryExists(datastore)) {
                            logger.info("Trying to create the absent data storage dir: " + datastore);
                            WinCommand.Result res = WinCommand.On(targetHost, domain + "\\" + user, password)
                                .runCommand("SETLOCAL ENABLEEXTENSIONS\nMD \"" + datastore + "\"\n");
                            logger.info("Create dir result: " + res.getOutput() + "\nRetCode: " + res.getReturnCode());
                        }
                        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, user, password);
                        Boolean needRetry = false;
                        do {
                            targetTempFileName = "arcserve-" + UUID.randomUUID().toString();
                            SmbFile targetTempFile = new SmbFile("smb://" + targetHost + "/ADMIN$/Temp/" +
                                targetTempFileName, auth);
                            if (targetTempFile.exists())
                                needRetry = true;
                            else {
                                byte[] buffer = new byte[1024 * 1024];
                                int count = -1;
                                sos = new SmbFileOutputStream(targetTempFile);
                                while ((count = fis.read(buffer)) != -1)
                                    sos.write(buffer, 0, count);
                                needRetry = false;
                            }
                        } while (needRetry);
                    } catch (IOException e) {
                        throw new HyperVException(HyperVException.ERROR_PUT_FILE, e.getMessage() + "\n" + "local file: " + filePath + "\n" + "target file: " +
                            targetTempFileName);
                    } finally {
                        if (fis != null)
                            try {
                                fis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        if (sos != null)
                            try {
                                sos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                    
                    /** Try deleting target first, ignore any error */
                    try { Delete.invoke(classes, targetServer, targetFilePath); }
                    catch (Exception e) { logger.debug("WRN: Delete file failed: " + e.getMessage()); }
                    
                    /** Get the temporary file to target location, try rename first */
                    Rename.Output mvRes = Rename.invoke(classes, targetServer, systemRoot + "\\Temp\\" +
                        targetTempFileName,
                        targetFilePath);
                    switch (mvRes.getReturnValue()) {
                    case 0:
                        /** Rename file succeeded, no more actions required */
                        return 0;
                    default:
                        /** Then try copy instead of move */
                        Copy.Output cpRes = Copy.invoke(classes, targetServer, systemRoot + "\\Temp\\" +
                            targetTempFileName,
                            targetFilePath);
                        switch (cpRes.getReturnValue()) {
                        case 0:
                            return 0;
                        default:
                            throw new HyperVException(HyperVException.ERROR_PUT_FILE, cpRes.getReturnValueDescription());
                        }
                    }
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_PUT_FILE, e.getMessage());
                } finally {
                    /** remove the temporary file */
                    try {
                        Delete.Output delRes;
                        delRes = Delete.invoke(classes, targetServer, systemRoot + "\\Temp\\" +
                            targetTempFileName);
                        switch (delRes.getReturnValue()) {
                        case 0:
                            /** Delete temporary file success */
                            break;
                        default:
                            throw new HyperVException(HyperVException.ERROR_DELETE_FILE, delRes.getReturnValueDescription());
                        }
                    } catch (Exception e) {
                        /** Just warning here ... */
                        logger.debug("WRN: Delete file failed: " + e.getMessage());
                    }                
                }
            }
            
            @Override
            public int deleteFile(String datastore, String filename) throws HyperVException {
                if((!filename.contains(":")) && ((datastore == null) || datastore.isEmpty()))
                    datastore = getDefaultVMDiskPath(null);
                logger.info("using datastore: " + datastore);
                try {
                    String targetFilePath = "";
                    if ((datastore != null) && !datastore.isEmpty())
                        targetFilePath = datastore + "\\";
                    if ((filename != null) && !filename.isEmpty())
                        targetFilePath = targetFilePath + filename;
                    targetFilePath = targetFilePath.replace("\\\\", "\\");
                    
                    Delete.Output delRes = Delete.invoke(classes, targetServer, targetFilePath);
                    int delResRet = delRes.getReturnValue();
                    
                    if (delResRet == 0)
                        return delResRet;
                    else
                        throw new HyperVException(HyperVException.ERROR_DELETE_FILE,delRes.getReturnValueDescription());
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_DELETE_FILE, e.getMessage());
                }
            }
            
            private String randomMacString() {
                String randomPart = "";
                for (int i = 0; i < 3; i++)
                    randomPart += String.format("%1$2X", (byte) (Math.random() * 256));
                return "00155d" + randomPart;
            }
            
            private ClassInstance getResDefaultSettingData(int resType, String resSubType) {
                String query = null;
                if (resType >= 0)
                    query = "ResourceType = " + resType + " AND ResourceSubType = '" + resSubType + "'";
                else
                    query = "ResourceSubType = '" + resSubType + "'";
                
                for (ClassInstance inst : classes.createReference("Msvm_AllocationCapabilities").enumerateInstances(
                    targetServer, query)) {
                    for (ClassInstance part : classes.createReference("Msvm_SettingsDefineCapabilities")
                        .enumerateInstances(targetServer, "ValueRange = 0 AND GroupComponent = '" +
                            ("\\\\" + serverName + "\\root\\virtualization:" + inst.getClassName() + ".InstanceID=\"" +
                                inst.getProperty("InstanceID").getSimpleValue() + "\"").replace("\\", "\\\\") + "'"))
                        return part.getProperty("PartComponent").getReferenceValue().getInstance(targetServer);
                }
                return null;
            }
            
            private ClassInstance getResDefaultSettingData(String resSubType) {
                return getResDefaultSettingData(-1, resSubType);
            }
            
            private ClassInstance createDiskDrive(String vmUuid, String driveType, ClassInstance controller, Integer addressOnController) throws HyperVException, Exception{
                ClassInstance defaultSettings = getResDefaultSettingData(driveType);
                if (defaultSettings == null)
                    throw new Exception("Can't find default settings for this drive type: " + driveType);
                
                defaultSettings.getProperty("Parent").setSimpleValue(
                    "\\\\" + serverName +
                        "\\root\\virtualization:Msvm_ResourceAllocationSettingData.InstanceID='" +
                        controller.getProperty("InstanceID").getSimpleValue() + "'");
                defaultSettings.getProperty("Address").setSimpleValue(addressOnController.toString());
                
                AddVirtualSystemResources.Output addDriveRes = AddVirtualSystemResources.invoke(classes, targetServer, serverName, vmUuid,
                    defaultSettings);
                int addDriveResRet = addDriveRes.getReturnValue();
                
                if (addDriveResRet == 4096) {
                    ConcreteJob job = addDriveRes.getConcreteJobRef();
                    while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                    if (!job.isSuccess())
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, job.toString(), job.getErrorDescription());
                    else
                        throw new Exception("Failed to create disk drive.");
                } else if (addDriveResRet != 0)
                    throw new Exception(addDriveRes.getReturnValueDescription());
                
                ClassReference[] res = addDriveRes.getCreatedResources();
                if ((res == null) || (res.length != 1))
                    throw new Exception("Failed to create disk drive.");
                ClassInstance createdDrive = res[0].getInstance(targetServer);
                if (createdDrive == null)
                    throw new Exception("Failed to create disk drive.");              
                /** System.out.println(defaultSettings.dumpPropertiesForDebug()); */
                return createdDrive;
            }
            
            private Boolean addImageToDrive(String vmUuid, ClassInstance createdDrive, String imageType, String imageFilePath) throws HyperVException, Exception {
                if ((imageFilePath != null) && !imageFilePath.isEmpty()) {
                    ClassInstance imageDefault = getResDefaultSettingData(imageType);
                    if (imageDefault == null)
                        throw new Exception("Can't find default settings for this image type: " + imageType);
                    
                    imageDefault.getProperty("Parent").setSimpleValue(
                        "\\\\" + serverName +
                            "\\root\\virtualization:Msvm_StorageAllocationSettingData.InstanceID='" +
                            createdDrive.getProperty("InstanceID").getSimpleValue() + "'");
                    imageDefault.getProperty("Connection").setSimpleArrayValue(new String[] { imageFilePath });
                    
                    AddVirtualSystemResources.Output addImageRes = AddVirtualSystemResources.invoke(classes, targetServer,
                        serverName, vmUuid, imageDefault);
                    int addImageResRet = addImageRes.getReturnValue();
                    
                    if (addImageResRet == 4096) {
                        ConcreteJob job = addImageRes.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, job.toString() + "\nImage file: " + imageFilePath, job.getErrorDescription());
                    } else if (addImageResRet != 0)
                        throw new Exception(addImageRes.getReturnValueDescription() + "\nImage file: " + imageFilePath);
                }                
                /** System.out.println(imageDefault.dumpPropertiesForDebug()); */
                return true;
            }
            
            private Boolean addDiskImage(String vmUuid, String driveType, String imageType, ClassInstance controller,
                Integer addressOnController, String imageFilePath) throws HyperVException, Exception {
                return addImageToDrive(vmUuid, createDiskDrive(vmUuid, driveType, controller, addressOnController), imageType, imageFilePath);
            }

            @Override
            public String[] getVirtualDisksList(String vmUuid) throws HyperVException {
                ArrayList<String> result = new ArrayList<String>(); 
                try {
                    ClassReference vssdRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                        "InstanceID", "Microsoft:" + vmUuid);
                    for (ClassInstance inst : vssdRef.relatedInstances(targetServer, "Msvm_ResourceAllocationSettingData")) {
                        String subType = inst.getProperty("ResourceSubType").getSimpleValue();
                        if ((subType != null) && subType.equals("Microsoft Virtual Hard Disk"))
                            for(String f: inst.getProperty("Connection").getSimpleArrayValue())
                                result.add(f);
                    }
                } catch(Exception e) {
                    return new String[0];
                }
                return result.toArray(new String[result.size()]);
            }

            @Override
            public String getVMPowerState(String vmuuid) throws HyperVException {
                try {
                    GetSummaryInformation.Output output = GetSummaryInformation.invoke(classes, targetServer,
                        serverName);
                    int ret = output.getReturnValue();
                    
                    if (ret != 0)
                        throw new Exception(output.getReturnValueDescription());
                    
                    for (ClassInstance summary : output.getSummaries()) {
                        String uuid = summary.getProperty("Name").getSimpleValue().trim();
                        if (!vmuuid.trim().equalsIgnoreCase(uuid))
                            continue;
                        else
                            return summary.getProperty("EnabledState").getSimpleValue();
                    }
                    return null;
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, e.getMessage());
                }
            }

            @Override
            public String getVirtualMachineName(String vmuuid) throws HyperVException {
                try {
                    ClassReference targetVmRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                        "InstanceID", "Microsoft:" + vmuuid);
                    ClassInstance targetVm = targetVmRef.getInstance(targetServer);
                    if (targetVm == null)
                        throw new Exception("Target VM does not exist " + vmuuid);
                    
                    return targetVm.getProperty("ElementName").getSimpleValue();
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, e.getMessage());
                }
            }

            @Override
            public boolean getDirectoryExists(String path) throws HyperVException {
                try {
                    while(path.trim().endsWith("\\"))
                        path = path.trim().substring(0, path.trim().length() - 2);
                    ClassReference targetPath = classes.createReference("Win32_Directory").addSelector("Name", path);
                    ClassInstance targetDir = targetPath.getInstance(targetServer);
                    return (targetDir != null);
                } catch(Exception e) {
                    logger.info("Failed to locate directory path: " + path, e);
                    return false;
                }
            }
            private void changeBootOrder(String vmUuid, ClassInstance vssd, String bootOption) throws HyperVException, Exception {
                /** Set boot order */
                switch (bootOption) {
                case "CD":
                    vssd.getProperty("BootOrder").setSimpleArrayValue(new String[] { "1", "2", "0", "3" });
                    break;
                case "DISK":
                    vssd.getProperty("BootOrder").setSimpleArrayValue(new String[] { "2", "1", "0", "3" });
                    break;
                default:
                    throw new HyperVException(HyperVException.ERROR_CHANGE_CD_ROM, "Unknown boot option " +
                        bootOption);
                }
                
                ModifyVirtualSystem.Output adjBootRes = ModifyVirtualSystem.invoke(classes, targetServer,
                    serverName, vmUuid, vssd);
                int adjBootRet = adjBootRes.getReturnValue();
                
                if (adjBootRet == 4096) {
                    ConcreteJob job = adjBootRes.getConcreteJobRef();
                    while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                    if (!job.isSuccess())
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, job.toString(), job.getErrorDescription());
                } else if (adjBootRet != 0)
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, adjBootRes.getReturnValueDescription());
            }
            @Override
            public void changeBootOrder(String vmUuid, String bootOption) throws HyperVException {
                try {
                    /** Get the VM */
                    ClassReference vssdRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                        "InstanceID", "Microsoft:" + vmUuid);
                    ClassInstance vssd = vssdRef.getInstance(targetServer);
                    if (vssd == null)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to load winrm classes.");
                    
                    changeBootOrder(vmUuid, vssd, bootOption);
                }  catch (HyperVException e) {
                    throw e;
                } catch (Exception e) {
                    logger.error("ERR: Failed to change boot order gen 1: " + e.getMessage());
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, "ERR: Failed to change boot order gen 1: " + e.getMessage());
                }
            }

            @Override
            public boolean setVirtualMachineProperty(String vmuuid, String key, String val) throws HyperVException {
                try {
                    ClassReference targetVmRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                        "InstanceID", "Microsoft:" + vmuuid);
                    ClassInstance targetVm = targetVmRef.getInstance(targetServer);
                    if (targetVm == null)
                        throw new Exception("Target VM does not exist " + vmuuid);

                    ClassInstanceProperty cProp = targetVm.getProperty(key);
                    switch(cProp.getPropertyType()) {
                    case SIMPLE_VALUE:
                        cProp.setSimpleValue(val);
                        break;
                    case SIMPLE_ARRAY_VALUE:
                        cProp.setSimpleArrayValue(Arrays.asList(val).toArray(new String[1]));
                        break;
                    default:
                        throw new Exception("Not supported property type: " + cProp.getPropertyType().toString());
                    }
                    
                    ModifyVirtualSystem.Output changePropRes = ModifyVirtualSystem.invoke(classes, targetServer, serverName, vmuuid, targetVm);
                    int changePropRet = changePropRes.getReturnValue();
                    
                    if (changePropRet == 4096) {
                        ConcreteJob job = changePropRes.getConcreteJobRef();
                        while (!job.refreshConcreteJob(targetServer).isFinalized()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                continue;
                            }
                        }
                        if (!job.isSuccess())
                            throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
                    } else if (changePropRet != 0)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, changePropRes.getReturnValueDescription());
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, e.getMessage());
                }
                return true;
            }

            @Override
            public String getVirtualMachineProperty(String vmuuid, String prop) throws HyperVException {
                try {
                    ClassReference targetVmRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                        "InstanceID", "Microsoft:" + vmuuid);
                    ClassInstance targetVm = targetVmRef.getInstance(targetServer);
                    if (targetVm == null)
                        throw new Exception("Target VM does not exist " + vmuuid);
                    
                    ClassInstanceProperty cProp = targetVm.getProperty(prop);
                    switch(cProp.getPropertyType()) {
                    case SIMPLE_VALUE:
                        return cProp.getSimpleValue();
                    case SIMPLE_ARRAY_VALUE:
                        return cProp.getSimpleArrayValue()[0];
                    default:
                        throw new Exception("Not supported property type: " + cProp.getPropertyType().toString());
                    }
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, e.getMessage());
                }
            }
        };
    }
}
