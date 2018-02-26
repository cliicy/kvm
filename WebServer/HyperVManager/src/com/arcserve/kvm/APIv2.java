package com.arcserve.kvm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.apache.log4j.Logger;
import org.openwsman.Client;

import com.arcserve.linux.WinCommand;
import com.arcserve.winrm.Classes;
import com.arcserve.winrm.Classes.ClassInstance;
import com.arcserve.winrm.Classes.ClassInstanceProperty;
import com.arcserve.winrm.Classes.ClassReference;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.cimv2.File.Copy;
import com.arcserve.winrm.cimv2.File.Delete;
import com.arcserve.winrm.cimv2.File.Rename;
import com.arcserve.winrm.hyperv.v2.Management.AddResourceSettings;
import com.arcserve.winrm.hyperv.v2.Management.CreateSnapshot;
import com.arcserve.winrm.hyperv.v2.Management.DefineSystem;
import com.arcserve.winrm.hyperv.v2.Management.DestroySnapshot;
import com.arcserve.winrm.hyperv.v2.Management.DestroySystem;
import com.arcserve.winrm.hyperv.v2.Management.GetSummaryInformation;
import com.arcserve.winrm.hyperv.v2.Management.ModifyResourceSettings;
import com.arcserve.winrm.hyperv.v2.Management.ModifySystemSettings;
import com.arcserve.winrm.hyperv.v2.Management.RemoveResourceSettings;
import com.arcserve.winrm.hyperv.v2.Storage.CreateVirtualHardDisk;
import com.arcserve.winrm.hyperv.v2.VirtualSystem.RequestStateChange;

public class APIv2 {
    private static Logger logger = Logger.getLogger(APIv2.class);
        
    public static APIContract createAgainstTarget(final Classes classes, final Client targetServer,
        final String serverName, final String systemRoot) {
        return new APIContract() {
            private ClassInstance getDefaultSettingFromResourcePoolWithQuery(String wql) throws Exception {
                ClassInstance pool = null;
                for (ClassInstance inst : classes.createReference("Msvm_ResourcePool")
                    .enumerateInstances(targetServer, wql))
                    pool = inst;
                if (pool == null)
                    return null;
                
                ClassReference allocCapRef = classes.createReference("Msvm_AllocationCapabilities")
                    .addSelector("InstanceID", pool.getProperty("InstanceID").getSimpleValue());
                ClassInstance allocCap = allocCapRef.getInstance(targetServer);
                if (allocCap == null)
                    return null;
                
                for (ClassInstance inst : allocCapRef.relatedInstances(targetServer, null,
                    "Msvm_SettingsDefineCapabilities"))
                    if (inst.getProperty("InstanceID").getSimpleValue().toUpperCase().endsWith("DEFAULT"))
                        return inst;
                
                return null;
            }
            
            private ClassInstance getDefaultSettingFromResourcePool(Integer resType) throws Exception {
                return getDefaultSettingFromResourcePoolWithQuery("ResourceType = " + resType.toString() +
                    " And Primordial = True");
            }
            
            private ClassInstance getDefaultSettingFromResourcePool(String resSubType) throws Exception {
                /**
                 * resSubType should be one of the following values:
                 * 
                 * Microsoft:Hyper-V:Synthetic Disk Drive
                 * Microsoft:Hyper-V:FibreChannel targetServerection
                 * Microsoft:Hyper-V:Synthetic FibreChannel Port
                 * Microsoft:Hyper-V:Physical Disk Drive
                 * Microsoft:Hyper-V:Memory
                 * Microsoft:Hyper-V:Emulated Ethernet Port
                 * Microsoft:Hyper-V:Synthetic Ethernet Port
                 * Microsoft:Hyper-V:Virtual Hard Disk
                 * Microsoft:Hyper-V:Ethernet targetServerection
                 * Microsoft:Hyper-V:Storage Logical Unit
                 * Microsoft:Hyper-V:Synthetic DVD Drive
                 * Microsoft:Hyper-V:Synthetic SCSI Controller
                 * Microsoft:Hyper-V:Synthetic Display Controller
                 * Microsoft:Hyper-V:Virtual CD/DVD Disk
                 * Microsoft:Hyper-V:Virtual Floppy Disk
                 * Microsoft:Hyper-V:Synthetic Mouse
                 * */
                if (!resSubType.startsWith("Microsoft:Hyper-V:"))
                    resSubType = "Microsoft:Hyper-V:" + resSubType;
                return getDefaultSettingFromResourcePoolWithQuery("ResourceSubType = '" + resSubType +
                    "' And Primordial = True");
            }
            private ClassInstance createDiskDrive(String vmUuid, String driveType, ClassInstance controller, Integer addressOnController) throws Exception{
                ClassInstance defaultSettings = getDefaultSettingFromResourcePool(driveType);
                if (defaultSettings == null)
                    throw new Exception("Can't find default settings for this drive type: " + driveType);
                
                defaultSettings.getProperty("Parent").setSimpleValue(
                    "\\\\" + serverName +
                        "\\root\\virtualization\\v2:Msvm_ResourceAllocationSettingData.InstanceID='" +
                        controller.getProperty("InstanceID").getSimpleValue() + "'");
                defaultSettings.getProperty("AddressOnParent").setSimpleValue(addressOnController.toString());
                
                AddResourceSettings.Output addDriveRes = AddResourceSettings.invoke(classes, targetServer, vmUuid,
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
                        throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
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
            private Boolean addImageToDrive(String vmUuid, ClassInstance createdDrive, String imageType, String imageFilePath) throws Exception {
                if ((imageFilePath != null) && !imageFilePath.isEmpty()) {
                    ClassInstance imageDefault = getDefaultSettingFromResourcePool(imageType);
                    if (imageDefault == null)
                        throw new Exception("Can't find default settings for this image type: " + imageType);
                    
                    imageDefault.getProperty("Parent").setSimpleValue(
                        "\\\\" + serverName +
                            "\\root\\virtualization\\v2:Msvm_StorageAllocationSettingData.InstanceID='" +
                            createdDrive.getProperty("InstanceID").getSimpleValue() + "'");
                    imageDefault.getProperty("HostResource").setSimpleArrayValue(new String[] { imageFilePath });
                    
                    AddResourceSettings.Output addImageRes = AddResourceSettings.invoke(classes, targetServer, vmUuid,
                        imageDefault);
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
                        if (!job.isSuccess()) {
                            logger.info(job.toString() + "\nImage file: " + imageFilePath);
                            throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
                        }
                    } else if (addImageResRet != 0) {
                        logger.info(addImageRes.getReturnValueDescription() + "\nImage file: " + imageFilePath);
                        throw new Exception(addImageRes.getReturnValueDescription() + "\nImage file: " + imageFilePath);
                    }
                }                
                /** System.out.println(imageDefault.dumpPropertiesForDebug()); */
                return true;
            }
            private Boolean addDiskImage(String vmUuid, String driveType, String imageType, ClassInstance controller,
                Integer addressOnController, String imageFilePath) throws Exception {
                return addImageToDrive(vmUuid, createDiskDrive(vmUuid, driveType, controller, addressOnController), imageType, imageFilePath);
            }
            
            private boolean isDuplicateVMName(String vmName) {
                try {
                    GetSummaryInformation.Output output = GetSummaryInformation.invoke(classes, targetServer);
                    int ret = output.getReturnValue();
                    
                    if (ret != 0)
                        throw new Exception(output.getReturnValueDescription());
                    
                    for (ClassInstance summary : output.getSummaries())
                        if (vmName.equalsIgnoreCase(this.getVirtualMachineName(summary.getProperty("Name").getSimpleValue())))
                            return true;
                    return false;
                } catch (Exception e) {
                    return true;
                }
            }
            
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
            
            private void changeBootOrderGen1(ClassInstance vssd, String bootOption) throws HyperVException {
                try {
                    if(isGeneration2VM(vssd))
                        throw new Exception("Wrong Generation! Gen 1 expected!");
                    
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
                    
                    ModifySystemSettings.Output adjBootRes = ModifySystemSettings.invoke(classes, targetServer,
                        vssd);
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
                            throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
                    } else if (adjBootRet != 0)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, adjBootRes.getReturnValueDescription());
                }  catch (HyperVException e) {
                    throw e;
                } catch (Exception e) {
                    logger.error("ERR: Failed to change boot order gen 1: " + e.getMessage());
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, "ERR: Failed to change boot order gen 1: " + e.getMessage());
                }                    
            }
            
            private void changeBootOrderGen2(ClassReference vssdRef, ClassInstance vssd, String bootOption) throws HyperVException {
                try {
                    if(!isGeneration2VM(vssd))
                        throw new Exception("Wrong Generation! Gen 2 expected!");
                    
                    ClassInstance scsiController = null;
                    for (ClassInstance inst : vssdRef.relatedInstances(targetServer,
                        "Msvm_ResourceAllocationSettingData")) {
                        String subType = inst.getProperty("ResourceSubType").getSimpleValue();
                        if ((subType != null) && subType.equals("Microsoft:Hyper-V:Synthetic SCSI Controller")) {
                            if(scsiController == null) scsiController = inst;
                            else logger.error("Invalid scsi controller: \n" + inst.dumpPropertiesForDebug());
                        }
                    }
                    if (scsiController == null)
                        throw new Exception("No valid disk/cdrom scsi controller class found");
                    
                    String[] bootSourceOrder = vssd.getProperty("BootSourceOrder").getSimpleArrayValue();
                    ArrayList<String> bootOrder = new ArrayList<String>();
                    
                    Map<String, String> bootSources = new HashMap<String, String>();
                    for(String bootSource: bootSourceOrder) {
                        ClassReference bssd = classes.createReference("Msvm_BootSourceSettingData").addSelector( "InstanceID", 
                            bootSource.substring(bootSource.indexOf("Microsoft:")).replace("\"", "").replace("\\\\", "\\"));
                        for (ClassInstance inst : bssd.relatedInstances(targetServer, "Msvm_ResourceAllocationSettingData")) {
                            /**
                             * Microsoft:Hyper-V:Synthetic Disk Drive
                             * Microsoft:Hyper-V:Synthetic DVD Drive
                             * ... and others
                             * */
                            bootSources.put(bootSource, inst.getProperty("ResourceSubType").getSimpleValue());
                        }
                    }
                    
                    /** Set boot order */
                    switch (bootOption) {
                    case "CD":
                        for (Map.Entry<String, String> bootEntry : bootSources.entrySet())
                            if (bootEntry.getValue().contains("DVD") || 
                                bootEntry.getValue().contains("CD")) {
                                bootOrder.add(bootEntry.getKey());
                                break;
                            }
                        for (Map.Entry<String, String> bootEntry : bootSources.entrySet())
                            if (!bootEntry.getValue().contains("DVD") &&
                                !bootEntry.getValue().contains("CD")) {
                                bootOrder.add(bootEntry.getKey());
                            }
                        break;
                    case "DISK":
                        for (Map.Entry<String, String> bootEntry : bootSources.entrySet())
                            if (!bootEntry.getValue().contains("DVD") &&
                                !bootEntry.getValue().contains("CD")) {
                                bootOrder.add(bootEntry.getKey());
                            }
                        for (Map.Entry<String, String> bootEntry : bootSources.entrySet())
                            if (bootEntry.getValue().contains("DVD") || 
                                bootEntry.getValue().contains("CD")) {
                                bootOrder.add(bootEntry.getKey());
                                break;
                            }
                        break;
                    default:
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Unknown boot option " +
                            bootOption);
                    }
                    
                    vssd.getProperty("BootSourceOrder").setSimpleArrayValue(
                        bootOrder.toArray(new String[bootOrder.size()]));
                    
                    ModifySystemSettings.Output adjBootRes = ModifySystemSettings.invoke(classes, targetServer,
                        vssd);
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
                            throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
                    } else if (adjBootRet != 0)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, adjBootRes.getReturnValueDescription());
                }  catch (HyperVException e) {
                    throw e;
                } catch (Exception e) {
                    logger.error("ERR: Failed to change boot order gen 1: " + e.getMessage());
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, "ERR: Failed to change boot order gen 2: " + e.getMessage());
                }                    
            }
            
            public void changeBootOrder(String vmUuid, String bootOption) throws HyperVException {
                try {
                    /** Get the VM */
                    ClassReference vssdRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                        "InstanceID", "Microsoft:" + vmUuid);
                    ClassInstance vssd = vssdRef.getInstance(targetServer);
                    if (vssd == null)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to load winrm classes.");
                    if(isGeneration2VM(vssd))
                        changeBootOrderGen2(vssdRef, vssd, bootOption);
                    else
                        changeBootOrderGen1(vssd, bootOption);
                }  catch (HyperVException e) {
                    throw e;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, e.getMessage());
                }   
            }
            
            private String createGen1VM(int generation, String vmStoragePath, String vmName, String bootOption,
                Integer cpuCount, Long memorySize, List<SimpleEntry<String, String>> nicToAddByMacAndHostNetworkName, String cdPath,
                List<SimpleEntry<String, Long>> disks)
                throws HyperVException {
                try {
                    /** Get server settings */
                    ClassInstance[] svcSettings = classes.createReference(
                        "Msvm_VirtualSystemManagementServiceSettingData").enumerateInstances(
                        targetServer);
                    assert (svcSettings.length == 1) : "Couldn't get Msvm_VirtualSystemManagementServiceSettingData";
                    
                    if (this.isDuplicateVMName(vmName))
                        vmName = String.format("%s %d", vmName, System.currentTimeMillis());
                    
                    /** Create the VM */
                    DefineSystem.Output output = null;
                    if ((vmStoragePath == null) || vmStoragePath.isEmpty())
                        output = DefineSystem.invoke(classes, targetServer, vmName, getDefaultVMCfgPath(svcSettings[0]));
                    else 
                        output = DefineSystem.invoke(classes, targetServer, vmName, vmStoragePath);
                    int ret = output.getReturnValue();
                    
                    if (ret == 4096) {
                        /** In *experience*, this case will *never* happen on successful VM creation.
                         *  If this branch is reached, it must be some error happened.
                         *  But, if indeed this branch shows a successful VM creation, we cannot handle it.
                         *  And will throw exception.
                         * */
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
                            /** Throws even if it succeeded... */
                            throw new Exception("Unexpected!");
                    } else if (ret != 0)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to create vm.\n" + ret +
                            ": " + output.getReturnValueDescription());
                    
                    String vmUuid = output.getResultingSystemUuid();
                    if ((vmUuid == null) || vmUuid.isEmpty())
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to create vm. Unknown error!");
                    
                    /** Configure created VM */
                    try {
                        ClassReference vssdRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                            "InstanceID", "Microsoft:" + vmUuid);
                        ClassInstance vssd = vssdRef.getInstance(targetServer);
                        if (vssd == null)
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to load winrm classes.");
                        
                        changeBootOrderGen1(vssd, bootOption);
//                        if (bootOption != null) {
//                            /** Set boot order */
//                            switch (bootOption) {
//                            case "CD":
//                                vssd.getProperty("BootOrder").setSimpleArrayValue(new String[] { "1", "2", "0", "3" });
//                                break;
//                            case "DISK":
//                                vssd.getProperty("BootOrder").setSimpleArrayValue(new String[] { "2", "1", "0", "3" });
//                                break;
//                            default:
//                                throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Unknown boot option " +
//                                    bootOption);
//                            }
//                        }
//                        
//                        ModifySystemSettings.Output adjBootRes = ModifySystemSettings.invoke(classes, targetServer,
//                            vssd);
//                        int adjBootRet = adjBootRes.getReturnValue();
//                        
//                        if (adjBootRet == 4096) {
//                            ConcreteJob job = adjBootRes.getConcreteJobRef();
//                            while (!job.refreshConcreteJob(targetServer).isFinalized()) {
//                                try {
//                                    Thread.sleep(100);
//                                } catch (InterruptedException e) {
//                                    continue;
//                                }
//                            }
//                            if (!job.isSuccess())
//                                throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
//                        } else if (adjBootRet != 0)
//                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, adjBootRes.getReturnValueDescription());
//                        
                        /** Get processor setting */
                        String processorSettingUuid = null;
                        for (ClassInstance inst : classes.createReference("Msvm_VirtualSystemSettingData")
                            .addSelector("InstanceID", "Microsoft:" + vmUuid).relatedInstances(targetServer,
                                "Msvm_ProcessorSettingData"))
                            processorSettingUuid = inst.getProperty("InstanceID").getSimpleValue();
                        /** Get memory setting */
                        String memorySettingUuid = null;
                        for (ClassInstance inst : classes.createReference("Msvm_VirtualSystemSettingData")
                            .addSelector("InstanceID", "Microsoft:" + vmUuid).relatedInstances(targetServer,
                                "Msvm_MemorySettingData"))
                            memorySettingUuid = inst.getProperty("InstanceID").getSimpleValue();
                        
                        if (processorSettingUuid == null || memorySettingUuid == null ||
                            processorSettingUuid.isEmpty() ||
                            memorySettingUuid.isEmpty())
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to configure vm processor or memory.");
                        
                        /** Update memory and processor */
                        ClassInstance processorSetting = classes.createInstance("Msvm_ProcessorSettingData");
                        ClassInstance memorySetting = classes.createInstance("Msvm_MemorySettingData");
                        processorSetting.getProperty("InstanceID").setSimpleValue(processorSettingUuid);
                        processorSetting.getProperty("VirtualQuantity").setSimpleValue(cpuCount.toString());
                        memorySetting.getProperty("InstanceID").setSimpleValue(memorySettingUuid);
                        memorySetting.getProperty("VirtualQuantity").setSimpleValue(memorySize.toString());
                        
                        ModifyResourceSettings.Output adjRes = ModifyResourceSettings.invoke(classes, targetServer,
                            processorSetting,
                            memorySetting);
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
                            for (SimpleEntry<String, String> nicHostNetwork : nicToAddByMacAndHostNetworkName) {
                                if (nicHostNetwork != null) {
                                    String mac = this.addNicCard(vmUuid, null, nicHostNetwork.getKey(), nicHostNetwork.getValue());
                                    if ((mac == null) || mac.isEmpty())
                                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to create nic for vm");
                                }
                            }
                        }
                        
                        /** Find the default IDE controllers */
                        ClassInstance[] ideControllers = new ClassInstance[] { null, null };
                        for (ClassInstance inst : vssdRef.relatedInstances(targetServer,
                            "Msvm_ResourceAllocationSettingData")) {
                            String subType = inst.getProperty("ResourceSubType").getSimpleValue();
                            if ((subType != null) && subType.equals("Microsoft:Hyper-V:Emulated IDE Controller"))
                                ideControllers[Integer.parseInt(inst.getProperty("Address").getSimpleValue())] = inst;
                        }
                        if ((ideControllers[0] == null) || (ideControllers[1] == null))
                            throw new Exception("No disk controllor class found");
                        
                        /** Add our default SCSI controller */
                        ClassInstance defSCSI = getDefaultSettingFromResourcePool("Synthetic SCSI Controller");
                        if (defSCSI == null)
                            throw new Exception("No disk controller class found");
                        
                        AddResourceSettings.Output addSCSIRes = AddResourceSettings.invoke(classes, targetServer,
                            vmUuid, defSCSI);
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
                                /** :'( */
                                throw new Exception();
                        } else if (addSCSIResRet != 0)
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, addSCSIRes.getReturnValueDescription());
                        
                        ClassInstance scsiController = null;
                        for (ClassInstance inst : vssdRef.relatedInstances(targetServer,
                            "Msvm_ResourceAllocationSettingData")) {
                            String subType = inst.getProperty("ResourceSubType").getSimpleValue();
                            if ((subType != null) && subType.equals("Microsoft:Hyper-V:Synthetic SCSI Controller"))
                                scsiController = inst;
                        }
                        if (scsiController == null)
                            throw new Exception("No disk controller class found");
                        
                        /** Attach CD and set ISO if needed, always on IDE0/0 */
                        if ((cdPath != null) && (!cdPath.isEmpty())) {
                            if(cdPath.indexOf(":") < 0) {
                                // the cd is put to default vm disk folder
                                cdPath = getDefaultVMDiskPath(svcSettings[0]) + "\\" + cdPath;
                            }
                            if(!addDiskImage(vmUuid, "Synthetic DVD Drive", "Virtual CD/DVD Disk", ideControllers[0], 0, cdPath))
                                throw new HyperVException(HyperVException.ERROR_CHANGE_CD_ROM, "No CD controller class found");
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
                                    throw new Exception("Too many disk for HyperV");
                                /** Create the disk image first */
                                String fullPath = null;
                                String fileName = vmName.replaceAll("[\\\\/:*\"\\?<>|]", "_") + "-" + vmUuid + "-" + (currentIDEDiskCount + currentSCSIDiskCount) + ".vhdx";
                                if ((storage != null) && !storage.isEmpty())
                                    fullPath = storage + "\\" + fileName;
                                else if ((vmStoragePath != null) && !vmStoragePath.isEmpty())
                                    fullPath = vmStoragePath + "\\" + fileName;
                                else
                                    fullPath = getDefaultVMDiskPath(svcSettings[0]) + "\\" + fileName;
                                fullPath = fullPath.replace("\\\\", "\\");
                                
                                CreateVirtualHardDisk.Output createVHDRes = CreateVirtualHardDisk.invoke(classes,
                                    targetServer,
                                    fullPath, size);
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
                                        throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
                                } else if (createVHDResRet != 0)
                                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, createVHDRes.getReturnValueDescription());
                                
                                /** Attach to specified controller */
                                String diskControllerType = controller.toUpperCase();
                                if (diskControllerType.equals("IDE")) {
                                    if (!addDiskImage(vmUuid, "Synthetic Disk Drive", "Virtual Hard Disk",
                                        ideControllers[(currentIDEDiskCount + 1) / 2], (currentIDEDiskCount + 1) % 2,
                                        fullPath))
                                        throw new Exception("Failed to allocate disk");
                                    currentIDEDiskCount += 1;
                                } else if (diskControllerType.equals("SCSI")) {
                                    if (!addDiskImage(vmUuid, "Synthetic Disk Drive", "Virtual Hard Disk",
                                        scsiController, currentSCSIDiskCount, fullPath))
                                        throw new Exception("Failed to allocate disk");
                                    currentSCSIDiskCount += 1;
                                } else
                                    throw new Exception("Unsupported disk controller type " + controller);
                            }
                    } catch (Exception e) {
                        this.deleteVM(vmUuid);
                        throw new Exception("Failed to configure created vm. " + e.getMessage());
                    }
                    
                    return vmUuid;
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, e.getMessage());
                }
            }
            
            private String createGen2VM(int generation, String vmStoragePath, String vmName, String bootOption,
                Integer cpuCount, Long memorySize, List<SimpleEntry<String, String>> nicToAddByMacAndHostNetworkName, String cdPath,
                List<SimpleEntry<String, Long>> disks)
                throws HyperVException {
                try {
                    /** Get server settings */
                    ClassInstance[] svcSettings = classes.createReference(
                        "Msvm_VirtualSystemManagementServiceSettingData").enumerateInstances(
                        targetServer);
                    assert (svcSettings.length == 1) : "Couldn't get Msvm_VirtualSystemManagementServiceSettingData";

                    if (this.isDuplicateVMName(vmName))
                        vmName = String.format("%s %d", vmName, System.currentTimeMillis());
                    
                    /** Create the VM */
                    DefineSystem.Output output = null;
                    if ((vmStoragePath == null) || vmStoragePath.isEmpty())
                        output = DefineSystem.invoke(classes, targetServer, vmName, getDefaultVMCfgPath(svcSettings[0]), true);
                    else
                        output = DefineSystem.invoke(classes, targetServer, vmName, vmStoragePath, true);
                    int ret = output.getReturnValue();
                    
                    if (ret == 4096) {
                        /** In *experience*, this case will *never* happen on successful VM creation.
                         *  If this branch is reached, it must be some error happened.
                         *  But, if indeed this branch shows a successful VM creation, we cannot handle it.
                         *  And will throw exception.
                         * */
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
                            /** Throws even if it succeeded... */
                            throw new Exception("Unexpected!");
                    } else if (ret != 0)
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to create vm.\n" + ret +
                            ": " + output.getReturnValueDescription());
                    
                    String vmUuid = output.getResultingSystemUuid();
                    if ((vmUuid == null) || vmUuid.isEmpty())
                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to create vm. Unknown error!");
                    
                    /** Configure created VM */
                    try {
                        ClassReference vssdRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                            "InstanceID", "Microsoft:" + vmUuid);
                        ClassInstance vssd = vssdRef.getInstance(targetServer);
                        if (vssd == null)
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to load winrm classes.");
                        
                        /** Get processor setting */
                        String processorSettingUuid = null;
                        for (ClassInstance inst : classes.createReference("Msvm_VirtualSystemSettingData")
                            .addSelector("InstanceID", "Microsoft:" + vmUuid).relatedInstances(targetServer,
                                "Msvm_ProcessorSettingData"))
                            processorSettingUuid = inst.getProperty("InstanceID").getSimpleValue();
                        /** Get memory setting */
                        String memorySettingUuid = null;
                        for (ClassInstance inst : classes.createReference("Msvm_VirtualSystemSettingData")
                            .addSelector("InstanceID", "Microsoft:" + vmUuid).relatedInstances(targetServer,
                                "Msvm_MemorySettingData"))
                            memorySettingUuid = inst.getProperty("InstanceID").getSimpleValue();
                        
                        if (processorSettingUuid == null || memorySettingUuid == null ||
                            processorSettingUuid.isEmpty() ||
                            memorySettingUuid.isEmpty())
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to configure vm processor or memory.");
                        
                        /** Update memory and processor */
                        ClassInstance processorSetting = classes.createInstance("Msvm_ProcessorSettingData");
                        ClassInstance memorySetting = classes.createInstance("Msvm_MemorySettingData");
                        processorSetting.getProperty("InstanceID").setSimpleValue(processorSettingUuid);
                        processorSetting.getProperty("VirtualQuantity").setSimpleValue(cpuCount.toString());
                        memorySetting.getProperty("InstanceID").setSimpleValue(memorySettingUuid);
                        memorySetting.getProperty("VirtualQuantity").setSimpleValue(memorySize.toString());
                        
                        ModifyResourceSettings.Output adjRes = ModifyResourceSettings.invoke(classes, targetServer,
                            processorSetting,
                            memorySetting);
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
                                throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
                        } else if (adjResRet != 0)
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, adjRes.getReturnValueDescription());
                        
                        /** Create default NIC targetServerected to specified network */
                        if (nicToAddByMacAndHostNetworkName != null) {
                            for (SimpleEntry<String, String> nicHostNetwork : nicToAddByMacAndHostNetworkName) {
                                if (nicHostNetwork != null) {
                                    String mac = this.addNicCard(vmUuid, null, nicHostNetwork.getKey(), nicHostNetwork.getValue());
                                    if ((mac == null) || mac.isEmpty())
                                        throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to create nic for vm");
                                }
                            }
                        }
                        
                        /** Add our default SCSI controller */
                        ClassInstance defSCSI = getDefaultSettingFromResourcePool("Synthetic SCSI Controller");
                        if (defSCSI == null)
                            throw new Exception("No disk controller class found");
                        
                        AddResourceSettings.Output addSCSIRes = AddResourceSettings.invoke(classes, targetServer,
                            vmUuid, defSCSI);
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
                                throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
                            else
                                /** :'( */
                                throw new Exception();
                        } else if (addSCSIResRet != 0)
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, addSCSIRes.getReturnValueDescription());
                        
                        ClassInstance scsiController = null;
                        for (ClassInstance inst : vssdRef.relatedInstances(targetServer,
                            "Msvm_ResourceAllocationSettingData")) {
                            String subType = inst.getProperty("ResourceSubType").getSimpleValue();
                            if ((subType != null) && subType.equals("Microsoft:Hyper-V:Synthetic SCSI Controller")) {
                                if(scsiController == null) scsiController = inst;
                                else logger.error("Invalid scsi controller: \n" + inst.dumpPropertiesForDebug());
                            }
                        }
                        if (scsiController == null)
                            throw new Exception("No valid disk/cdrom scsi controller class found");
                        
                        /** Create disks, support only 64(Actually, HyperV supports 64 * 4) SCSI hard drives on the first scsi controller */
                        int currentSCSIDiskCount = 0;
                        if ((disks != null) && (disks.size() > 0))
                            for (SimpleEntry<String, Long> disk : disks) {
                                String controller = disk.getKey().split("\n", 2)[0];
                                String storage = disk.getKey().split("\n", 2)[1];
                                Long size = disk.getValue();
                                
                                if (currentSCSIDiskCount > 63)
                                    throw new Exception("Too many disk for HyperV");
                                /** Create the disk image first */
                                String fullPath = null;
                                String fileName = vmName.replaceAll("[\\\\/:*\"\\?<>|]", "_") + "-" + vmUuid + "-" + currentSCSIDiskCount + ".vhdx";
                                if ((storage != null) && !storage.isEmpty())
                                    fullPath = storage + "\\" + fileName;
                                else if ((vmStoragePath != null) && !vmStoragePath.isEmpty())
                                    fullPath = vmStoragePath + "\\" + fileName;
                                else
                                    fullPath = getDefaultVMDiskPath(svcSettings[0]) + "\\" + fileName;
                                fullPath = fullPath.replace("\\\\", "\\");
                                
                                CreateVirtualHardDisk.Output createVHDRes = CreateVirtualHardDisk.invoke(classes,
                                    targetServer,
                                    fullPath, size);
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
                                        throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
                                } else if (createVHDResRet != 0)
                                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, createVHDRes.getReturnValueDescription());
                                
                                /** Attach to specified controller */
                                String diskControllerType = controller.toUpperCase();
                                if (diskControllerType.equals("IDE")) {
                                    throw new Exception("Generation 2 VM do not support IDE controllers.");
                                } else if (diskControllerType.equals("SCSI")) {
                                    if (!addDiskImage(vmUuid, "Synthetic Disk Drive", "Virtual Hard Disk",
                                        scsiController, currentSCSIDiskCount, fullPath))
                                        throw new Exception("Failed to allocate disk");
                                    currentSCSIDiskCount += 1;
                                } else
                                    throw new Exception("Unsupported disk controller type " + controller);
                                logger.debug("Added " + currentSCSIDiskCount + " disk(s).");
                            }
                        
                        /** Attach CD and set ISO if needed, always on the last one of SCSI0*/
                        if ((cdPath != null) && (!cdPath.isEmpty())) {
                            if(cdPath.indexOf(":") < 0) {
                                // the cd is put to default vm disk folder
                                cdPath = getDefaultVMDiskPath(svcSettings[0]) + "\\" + cdPath;
                            }
                            if(!addDiskImage(vmUuid, "Synthetic DVD Drive", "Virtual CD/DVD Disk", scsiController, currentSCSIDiskCount, cdPath))
                                throw new HyperVException(HyperVException.ERROR_CHANGE_CD_ROM, "No CD controller class found");
                        }
                        
                        // reload vssd settings
                        vssd = vssdRef.getInstance(targetServer);
                        if (vssd == null)
                            throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Failed to load winrm classes.");
                        changeBootOrderGen2(vssdRef, vssd, bootOption);
//                        if (bootOption != null) {
//                            /** Refresh our vm setting data */
//                            vssd = vssdRef.getInstance(targetServer);
//                            
//                            /** First CD and the DISKs on the only SCSI controller */
//                            String disksIdentifier = scsiController.getProperty("InstanceID").getSimpleValue().replace(
//                                "\\", "\\\\");
//                            String cdIdentifier = disksIdentifier + "\\\\0";
//                            
//                            String[] bootSourceOrder = vssd.getProperty("BootSourceOrder").getSimpleArrayValue();
//                            ArrayList<String> bootOrder = new ArrayList<String>();
//                            
//                            /** Set boot order */
//                            switch (bootOption) {
//                            case "CD":
//                                for (String bootEntry : bootSourceOrder)
//                                    if (bootEntry.contains(cdIdentifier)) {
//                                        bootOrder.add(bootEntry);
//                                        break;
//                                    }
//                                for (String bootEntry : bootSourceOrder)
//                                    if (bootEntry.contains(disksIdentifier) && !bootEntry.contains(cdIdentifier)) {
//                                        bootOrder.add(bootEntry);
//                                    }
////                                for (String bootEntry : bootSourceOrder)
////                                    if (!bootEntry.contains(disksIdentifier)) {
////                                        bootOrder.add(bootEntry);
////                                    }
//                                break;
//                            case "DISK":
//                                for (String bootEntry : bootSourceOrder)
//                                    if (bootEntry.contains(disksIdentifier) && !bootEntry.contains(cdIdentifier)) {
//                                        bootOrder.add(bootEntry);
//                                    }
//                                for (String bootEntry : bootSourceOrder)
//                                    if (bootEntry.contains(cdIdentifier)) {
//                                        bootOrder.add(bootEntry);
//                                        break;
//                                    }
////                                for (String bootEntry : bootSourceOrder)
////                                    if (!bootEntry.contains(disksIdentifier)) {
////                                        bootOrder.add(bootEntry);
////                                    }
//                                break;
//                            default:
//                                throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Unknown boot option " +
//                                    bootOption);
//                            }
//                            
//                            vssd.getProperty("BootSourceOrder").setSimpleArrayValue(
//                                bootOrder.toArray(new String[bootOrder.size()]));
//                            
//                            ModifySystemSettings.Output adjBootRes = ModifySystemSettings.invoke(classes, targetServer,
//                                vssd);
//                            int adjBootRet = adjBootRes.getReturnValue();
//                            
//                            if (adjBootRet == 4096) {
//                                ConcreteJob job = adjBootRes.getConcreteJobRef();
//                                while (!job.refreshConcreteJob(targetServer).isFinalized()) {
//                                    try {
//                                        Thread.sleep(100);
//                                    } catch (InterruptedException e) {
//                                        continue;
//                                    }
//                                }
//                                if (!job.isSuccess())
//                                    throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
//                            } else if (adjBootRet != 0)
//                                throw new HyperVException(HyperVException.ERROR_CREATE_VM, adjBootRes.getReturnValueDescription());
//                        }
                    } catch (HyperVException hve) {
                        this.deleteVM(vmUuid);
                        throw hve;
                    } catch (Exception e) {
                        this.deleteVM(vmUuid);
                        throw new Exception("Failed to configure created vm. " + e.getMessage());
                    }
                    
                    return vmUuid;
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, e.getMessage());
                }
            }
            
            @SuppressWarnings("unused")
            private boolean isGeneration2VM(String vmUuid) throws Exception {
                /** Get the VM */
                ClassReference vssdRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                    "InstanceID", "Microsoft:" + vmUuid);
                return isGeneration2VM(vssdRef.getInstance(targetServer));
            }
            private boolean isGeneration2VM(ClassInstance vssd) throws Exception {
                if (vssd == null)
                    throw new Exception("VM does not exist.");
                try {
                    return vssd.getProperty("VirtualSystemSubType").getSimpleValue().equalsIgnoreCase("Microsoft:Hyper-V:SubType:2");
                } catch(Exception e) {
                    /** APIv1 probably do not have VirtualSystemSubType property, return false under this condition */
                    return false;
                }
            }
            
            /**
             * disks each entry example: <[String]"{controller}\n{storage}", [Long]size>
             * @throws Exception 
             * */
            @Override
            public String createVM(int generation, String vmStoragePath, String vmName, String bootOption,
                Integer cpuCount, Long memorySize, List<SimpleEntry<String, String>> nicToAddByMacAndHostNetworkName, String cdPath,
                List<SimpleEntry<String, Long>> disks)
                throws HyperVException {
                switch (generation) {
                case 1:
                    return createGen1VM(generation, vmStoragePath, vmName, bootOption, cpuCount, memorySize,
                        nicToAddByMacAndHostNetworkName, cdPath, disks);
                case 2:
                    return createGen2VM(generation, vmStoragePath, vmName, bootOption, cpuCount, memorySize,
                        nicToAddByMacAndHostNetworkName, cdPath, disks);
                default:
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, "Cannot create generation " +
                        generation + " vm on this server!");
                }
            }
            
            @Override
            public boolean deleteVM(String uuid) throws HyperVException {
                try {
                    DestroySystem.Output output = DestroySystem.invoke(classes, targetServer, uuid);
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
                        throw new HyperVException(HyperVException.ERROR_DELETE_VM, output.getReturnValueDescription());
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_DELETE_VM, e.getMessage());
                }
            }
            
            @Override
            public boolean startVM(String uuid) throws HyperVException {
                try {
                    RequestStateChange.Output output = RequestStateChange.invoke(classes, targetServer, uuid,
                        RequestStateChange.VmState.Running);
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
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_START_VM, e.getMessage());
                }
            }
            
            @Override
            public boolean stopVM(String uuid) throws HyperVException {
                try {
                    RequestStateChange.Output output = RequestStateChange.invoke(classes, targetServer, uuid,
                        RequestStateChange.VmState.Off);
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
                    
                    GetSummaryInformation.Output output = GetSummaryInformation.invoke(classes, targetServer);
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
                } catch (HyperVException hve) {
                    throw hve;
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
                        throw new Exception();

                    List<SimpleEntry<String, String>> MACs = new ArrayList<SimpleEntry<String, String>>();
                    
                    for (ClassInstance inst : targetVmRef.relatedInstances(targetServer,
                        "Msvm_SyntheticEthernetPortSettingData"))
                        if (inst.getProperty("StaticMacAddress").getSimpleValue().toUpperCase().equals("TRUE")) {
                            
                            for (ClassInstance instAlloc: targetVmRef.relatedInstances(targetServer, "Msvm_EthernetPortAllocationSettingData")) {
                                if(instAlloc.getProperty("Parent").getSimpleValue().replace("\\\\", "\\").contains(inst.getProperty("InstanceID").getSimpleValue())) {
                                    String switchId = instAlloc.getProperty("HostResource").getSimpleArrayValue()[0];
                                    switchId = switchId.substring(switchId.lastIndexOf("Name=")).replace("Name=", "").replaceAll("\"", "");
                                    logger.debug("The switch id is " + switchId);
                                    ClassInstance switchInst =  classes.createReference("Msvm_VirtualEthernetSwitch").addSelector("CreationClassName", "Msvm_VirtualEthernetSwitch")
                                        .addSelector("Name", switchId).getInstance(targetServer);
                                    MACs.add(new AbstractMap.SimpleEntry<String, String>(inst.getProperty("Address").getSimpleValue(),
                                        switchInst.getProperty("ElementName").getSimpleValue()));
                                }
                            }                                    
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
                for (ClassInstance inst : classes.createReference("Msvm_VirtualEthernetSwitch").enumerateInstances(
                    targetServer))
                    networks.add(inst.getProperty("ElementName").getSimpleValue());
                return networks;
            }
            
            @Override
            public String createHypervisorNetwork(String networkName) throws HyperVException {
                try {
                    com.arcserve.winrm.hyperv.v2.Networking.DefineSystem.Output output =
                        com.arcserve.winrm.hyperv.v2.Networking.DefineSystem.invoke(classes, targetServer, networkName);
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
                        throw new Exception(output.getReturnValueDescription()); 
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
                    
                    for (ClassInstance inst : classes.createReference("Msvm_VirtualEthernetSwitch")
                        .enumerateInstances(targetServer, "ElementName = '" + networkName + "'"))
                        targetSwitch = inst;
                    
                    if (targetSwitch == null)
                        throw new Exception();
                    
                    com.arcserve.winrm.hyperv.v2.Networking.DestroySystem.Output output =
                        com.arcserve.winrm.hyperv.v2.Networking.DestroySystem.invoke(classes, targetServer,
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
                        throw new Exception(output.getReturnValueDescription()); 
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_NETWORK, e.getMessage());
                }
            }
            
            /** 
             * @throws HyperVException */
            @Override
            public boolean changeCDRom(String uuid, String imageFilePath) throws HyperVException {
                try {
                    /** Get the VM */
                    ClassReference vssdRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                        "InstanceID", "Microsoft:" + uuid);
                    ClassInstance vssd = vssdRef.getInstance(targetServer);
                    if (vssd == null)
                        throw new Exception("");

                    ArrayList<ClassInstance> cdDrives = new ArrayList<ClassInstance>(); 
                    ArrayList<ClassReference> cdImages = new ArrayList<ClassReference>();     
                    
                    /** Remove the existing CD image and drive, if any */
                    for (ClassInstance inst : vssdRef.relatedInstances(targetServer,
                        "Msvm_StorageAllocationSettingData")) {
                        String resSubType = inst.getProperty("ResourceSubType").getSimpleValue();
                        if ((resSubType != null) && resSubType.endsWith("Virtual CD/DVD Disk"))
                            cdImages.add(classes.createReference("Msvm_StorageAllocationSettingData")
                                .addSelector("InstanceID", inst.getProperty("InstanceID").getSimpleValue()));
                    }
                    for (ClassInstance inst : vssdRef.relatedInstances(targetServer,
                        "Msvm_ResourceAllocationSettingData")) {
                        String resSubType = inst.getProperty("ResourceSubType").getSimpleValue();
                        if ((resSubType != null) && resSubType.endsWith("Synthetic DVD Drive"))
                            cdDrives.add(inst);
                    }
                    
                    RemoveResourceSettings.Output rmRes = RemoveResourceSettings.invoke(classes, targetServer,
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
                            throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
                    } else if (rmResRet != 0)
                        throw new Exception(rmRes.getReturnValueDescription());
                    if ((imageFilePath != null) && !imageFilePath.isEmpty()) {
                        if(imageFilePath.indexOf(":") < 0) {
                            // the cd is put to default vm disk folder
                            imageFilePath = getDefaultVMDiskPath(null) + "\\" + imageFilePath;
                        }         
                        if(cdDrives.size() == 0) {
                            ClassInstance targetController = null;
                            String controllerTypeName = "";
                            if(isGeneration2VM(vssd))      
                                controllerTypeName = "Microsoft:Hyper-V:Synthetic SCSI Controller";
                            else controllerTypeName = "Microsoft:Hyper-V:Emulated IDE Controller";
                            for (ClassInstance inst : vssdRef.relatedInstances(targetServer,
                                "Msvm_ResourceAllocationSettingData")) {
                                String subType = inst.getProperty("ResourceSubType").getSimpleValue();
                                String addr = inst.getProperty("Address").getSimpleValue();
                                if ((subType != null) && subType.equals(controllerTypeName)) {
                                    if(addr == null) /** Gen 2 */
                                        targetController = inst;
                                    else if (addr.equals("0")) /** Gen 1 */
                                        targetController = inst;
                                    else continue;                                    
                                }
                            }
                            if (targetController == null)
                                throw new HyperVException(HyperVException.ERROR_CHANGE_CD_ROM, "Cannot find target Disk Controller: " + controllerTypeName);
                            int unusedScsiAddr = 0;
                            if(isGeneration2VM(vssd)) {
                                for(ClassInstance inst: vssdRef.relatedInstances(targetServer, "Msvm_ResourceAllocationSettingData")) {
                                    if(inst.getProperty("ResourceSubType").getSimpleValue().endsWith("Drive") && 
                                        inst.getProperty("InstanceID").getSimpleValue().contains(targetController.getProperty("InstanceID").getSimpleValue())) {
                                        int currAddr = Integer.parseInt(inst.getProperty("AddressOnParent").getSimpleValue());
                                        logger.debug("Found " + inst.getProperty("ResourceSubType").getSimpleValue() + " on " + currAddr + ", previous " + unusedScsiAddr);
                                        if(currAddr >= unusedScsiAddr)
                                            unusedScsiAddr = currAddr + 1;
                                    }
                                }
                                if(unusedScsiAddr > 63)
                                    throw new HyperVException(HyperVException.ERROR_CHANGE_CD_ROM, "Too many existing disks!");
                            }
                            cdDrives.add(this.createDiskDrive(uuid, "Synthetic DVD Drive", targetController, unusedScsiAddr));
                        }
                        return addImageToDrive(uuid, cdDrives.get(0), "Virtual CD/DVD Disk", imageFilePath);
                    }
                    
                    return true; 
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CHANGE_CD_ROM, e.getMessage());
                }
            }
            
            private String randomMacString() {
                String randomPart = "";
                for (int i = 0; i < 3; i++)
                    randomPart += String.format("%1$2X", (byte) (Math.random() * 256));
                return "00155d" + randomPart;
            }
            
            @Override
            public String addNicCard(String vmuuid, String adapterType, String mac, String networkName) throws HyperVException {
                try {
                    String hostNetworkName = networkName;
                    ClassInstance targetSwitch = null;
                    
                    if ((networkName == null) || networkName.isEmpty())
                        throw new Exception();
                    
                    for (ClassInstance inst : classes.createReference("Msvm_VirtualEthernetSwitch")
                        .enumerateInstances(targetServer, "ElementName = '" + hostNetworkName + "'"))
                        targetSwitch = inst;
                    
                    if (targetSwitch == null)
                        throw new Exception();
                    
                    if(mac == null || mac.isEmpty())
                        mac = this.randomMacString();
                    
                    /** Create the network card */
                    ClassInstance nicDefaultSetting = getDefaultSettingFromResourcePool("Synthetic Ethernet Port");
                    if (nicDefaultSetting == null)
                        throw new Exception();
                    
                    nicDefaultSetting.getProperty("ElementName").setSimpleValue("Arcserve Virtual Lab Network Adapter");
                    nicDefaultSetting.getProperty("VirtualSystemIdentifiers").setSimpleArrayValue(
                        new String[] { "{" + UUID.randomUUID().toString() + "}" });
                    nicDefaultSetting.getProperty("StaticMacAddress").setSimpleValue("TRUE");
                    nicDefaultSetting.getProperty("Address").setSimpleValue(mac);
                    
                    AddResourceSettings.Output output = AddResourceSettings.invoke(classes, targetServer, vmuuid,
                        nicDefaultSetting);
                    int ret = output.getReturnValue();
                    
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
                        /** Find the target nic by the "VirtualSystemIdentifiers" field. */
                        else
                            for (ClassInstance inst : classes.createReference("Msvm_SyntheticEthernetPortSettingData").enumerateInstances(
                                targetServer,
                                "ElementName = 'Arcserve Virtual Lab Network Adapter' and Address = '" +
                                    nicDefaultSetting.getProperty("Address").getSimpleValue() + "'"))
                                targetNic = inst;
                    } else
                        throw new Exception(output.getReturnValueDescription());
                    
                    if (targetNic == null)
                        throw new Exception();
                    
                    /** Create the network card and network allocation (EthernetPortAllocationSettingData) */
                    ClassInstance nicAllocDefaultSetting = this.getDefaultSettingFromResourcePool(33);
                    if (nicAllocDefaultSetting == null)
                        throw new Exception();
                    
                    nicAllocDefaultSetting.getProperty("Parent").setSimpleValue(
                        "\\\\" + serverName +
                            "\\root\\virtualization\\v2:Msvm_SyntheticEthernetPortSettingData.InstanceID='" +
                            targetNic.getProperty("InstanceID").getSimpleValue() + "'");
                    nicAllocDefaultSetting.getProperty("HostResource").setSimpleArrayValue(
                        new String[] { "\\\\" + serverName +
                            "\\root\\virtualization\\v2:Msvm_VirtualEthernetSwitch.Name='" +
                            targetSwitch.getProperty("Name").getSimpleValue() + "'" });
                    
                    output = AddResourceSettings.invoke(classes, targetServer, vmuuid, nicAllocDefaultSetting);
                    ret = output.getReturnValue();
                    
                    ClassInstance targetNicAlloc = null;
                    
                    if (ret == 0)
                        targetNicAlloc = output.getCreatedResources()[0].getInstance(targetServer);
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
                        /** Find the target nic by the "VirtualSystemIdentifiers" field. */
                        else
                            for (ClassInstance inst : classes.createReference("Msvm_EthernetPortAllocationSettingData").enumerateInstances(
                                targetServer,
                                "Parent Like '%" + targetNic.getProperty("InstanceID").getSimpleValue() + "%'"))
                                targetNicAlloc = inst;
                    } else
                        throw new Exception(output.getReturnValueDescription());
                    
                    if (targetNicAlloc == null)
                        throw new Exception();
                    
                    return targetNic.getProperty("Address").getSimpleValue(); 
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, e.getMessage());
                }
            }
            
            /** Removes all NICs related to the specified network 
             * @throws Exception */
            @Override
            public boolean removeNicCard(String vmuuid, String networkName) throws HyperVException {
                try {
                    /** Find target switch */
                    ClassInstance targetSwitch = null;
                    
                    for (ClassInstance inst : classes.createReference("Msvm_VirtualEthernetSwitch")
                        .enumerateInstances(targetServer, "ElementName = '" + networkName + "'"))
                        targetSwitch = inst;
                    
                    if (targetSwitch == null)
                        throw new Exception();
                    
                    /** Find NICs for this VM */
                    ArrayList<ClassInstance> targetNICs = new ArrayList<ClassInstance>();
                    for (ClassInstance inst : classes.createReference("Msvm_EthernetPortAllocationSettingData")
                        .enumerateInstances(targetServer, "InstanceID Like '%" + vmuuid.toUpperCase() + "%'")) {
                        String[] hostRes = inst.getProperty("HostResource").getSimpleArrayValue();
                        if (hostRes.length == 1) {
                            if (hostRes[0].toUpperCase().contains(
                                targetSwitch.getProperty("Name").getSimpleValue().toUpperCase())) {
                                /** This targetServerection is set to always targetServerect to this switch. */
                                targetNICs.add(inst);
                            } else
                                continue;
                        } else if (hostRes.length == 0) {
                            /**
                             * If the targetServerection does not have a hard affinity to this switch, it could
                             * still be currently targetServerected to this switch if it is configured to dynamically
                             * targetServerect to a switch and the virtual machine has been started.
                             * */
                            for (ClassInstance port : classes.createReference("Msvm_EthernetPortAllocationSettingData")
                                .addSelector("InstanceID", inst.getProperty("InstanceID").getSimpleValue()).relatedInstances(
                                    targetServer)) {
                                if (port.getClassName().equals("Msvm_EthernetSwitchPort") &&
                                    port.getProperty("SystemName").getSimpleValue().toUpperCase()
                                        .equals(targetSwitch.getProperty("Name").getSimpleValue().toUpperCase())) {
                                    targetNICs.add(inst);
                                }
                            }
                        } else
                            throw new Exception();
                    }
                    
                    ArrayList<ClassReference> targetNICRefs = new ArrayList<ClassReference>();
                    for (ClassInstance inst : targetNICs) {
                        targetNICRefs.add(classes.createReference("Msvm_EthernetPortAllocationSettingData").addSelector(
                            "InstanceID", inst.getProperty("InstanceID").getSimpleValue()));
                        String[] parentPath = inst.getProperty("Parent").getSimpleValue().split(":", 2)[1].split("\\.",
                            2);
                        ClassReference nicSettingData = classes.createReference(parentPath[0]);
                        for (String sel : parentPath[1].split(",")) {
                            String[] kvp = sel.split("=", 2);
                            nicSettingData.addSelector(kvp[0], kvp[1].replace('"', ' ').replace("\\\\", "\\").trim());
                            //System.out.println(kvp[0] + ": " + kvp[1].replace('"', ' ').replace("\\\\", "\\").trim());
                        }
                        targetNICRefs.add(nicSettingData);
                    }
                    
                    /** Remove them */
                    RemoveResourceSettings.Output output = RemoveResourceSettings.invoke(classes, targetServer,
                        targetNICRefs.toArray(new ClassReference[targetNICRefs.size()]));
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
                        throw new Exception(output.getReturnValueDescription()); 
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CONFIG_NIC, e.getMessage());
                }
            }
            
            @Override
            public String createSnapshot(String vmuuid, String snapshotName, String description) throws HyperVException {
                try {
                    CreateSnapshot.Output outputCreate = CreateSnapshot.invoke(classes, targetServer, vmuuid);
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
                            throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
                        else
                            snapUuid = null;
                        /** We have to find the uuid manually... so sad :( */
                    } else
                        throw new Exception(outputCreate.getReturnValueDescription());
                    
                    if ((snapUuid == null) || snapUuid.isEmpty()) {
                        /** The snapshot is created successfully, but we need to find it by ourself ... */
                        ClassInstance vmInst = classes.createReference("Msvm_VirtualSystemSettingData")
                            .addSelector("InstanceID", "Microsoft:" + vmuuid.toUpperCase()).getInstance(targetServer);
                        /** This may not be the most accurate, but we use the latest snapshot as if it were created by us. */
                        /** ex: Parent: \\LUVYU01-HYPERV\root\virtualization\v2:Msvm_VirtualSystemSettingData.InstanceID="Microsoft:52870366-70FC-4A35-B351-6AED09D0CD69"*/
                        snapUuid = vmInst.getProperty("Parent").getSimpleValue();
                        if (snapUuid == null || snapUuid.isEmpty())
                            throw new Exception("Snapshot is not created.");
                        int uuidIdx = snapUuid.lastIndexOf("Microsoft:");
                        if (uuidIdx < 0)
                            throw new Exception("Unknown Error");
                        uuidIdx += 10;
                        /** 10 is the length of "Microsoft:" */
                        snapUuid = snapUuid.substring(uuidIdx, uuidIdx + 36);
                        /** 36 is the length of uuid. */
                    }
                    
                    /** Modify the name and description of the snapshot. */
                    ClassInstance vmConfig = classes.createInstance("Msvm_VirtualSystemSettingData");
                    vmConfig.getProperty("InstanceID").setSimpleValue("Microsoft:" + snapUuid.toUpperCase());
                    vmConfig.getProperty("ElementName").setSimpleValue(snapshotName);
                    vmConfig.getProperty("Notes").setSimpleArrayValue(description.split("\n"));
                    
                    ModifySystemSettings.Output outputModify = ModifySystemSettings.invoke(classes, targetServer,
                        vmConfig);
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
                            throw new HyperVException(HyperVException.ERROR_START_VM, job.toString(), job.getErrorDescription());
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
                    DestroySnapshot.Output output = DestroySnapshot.invoke(classes, targetServer, snapshotId);
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
                        throw new Exception(output.getReturnValueDescription()); 
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_CREATE_VM, e.getMessage());
                }
            }
            
            /** datastore is used as parent folder, ie. full file path is always [`datastore' + '\' +] `filename' 
             * @throws Exception */
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
                        logger.info("Failed to put file: " + e.getMessage() + "\n" +
                            "local file: " + filePath + "\n" + "target file: " +
                            targetTempFileName);
                        throw new HyperVException(HyperVException.ERROR_PUT_FILE, e.getMessage() + "\n" +
                            "local file: " + filePath + "\n" + "target file: " +
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
                    try {
                        Delete.invoke(classes, targetServer, targetFilePath);
                    } catch (Exception e) {
                        logger.debug("WRN: Delete file failed: " + e.getMessage());
                    }
                    
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
                    logger.info("Unknown HVE: put file failed: " + hve.getErrorMessage());
                    throw hve;
                } catch (Exception e) {
                    logger.info("Unknown Error: put file failed: ", e);
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
                        logger.info("Removed remote tmp file");
                    } catch (HyperVException e) {
                        /** Just warning here ... */
                        logger.debug("WRN: Delete file failed: " + e.getErrorMessage());
                    } catch (Exception e) {
                        /** Just warning here ... */
                        logger.debug("WRN: Delete file failed: " + e.getMessage());
                    }
                }
            }
            
            /** datastore is used as parent folder, ie. full file path is always [`datastore' + '\' +] `filename' 
             * @throws Exception */
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
                        throw new Exception(delRes.getReturnValueDescription()); 
                } catch (HyperVException hve) {
                    throw hve;
                } catch (Exception e) {
                    throw new HyperVException(HyperVException.ERROR_DELETE_FILE, e.getMessage());
                }
            }
            
            @Override
            public String[] getVirtualDisksList(String vmUuid) throws HyperVException {
                ArrayList<String> result = new ArrayList<String>();
                try {
                    ClassReference vssdRef = classes.createReference("Msvm_VirtualSystemSettingData").addSelector(
                        "InstanceID", "Microsoft:" + vmUuid);
                    for (ClassInstance inst : vssdRef.relatedInstances(targetServer,
                        "Msvm_StorageAllocationSettingData")) {
                        String subType = inst.getProperty("ResourceSubType").getSimpleValue();
                        if ((subType != null) && subType.contains("Virtual Hard Disk"))
                            for (String f : inst.getProperty("HostResource").getSimpleArrayValue())
                                result.add(f);
                    }
                } catch (Exception e) {
                    return new String[0];
                }
                return result.toArray(new String[result.size()]);
            }
            
            @Override
            public String getVMPowerState(String vmuuid) throws HyperVException {
                try {
                    GetSummaryInformation.Output output = GetSummaryInformation.invoke(classes, targetServer);
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
                        path = path.trim().substring(0, path.trim().length() - 1);
                    ClassReference targetPath = classes.createReference("Win32_Directory").addSelector("Name", path);
                    ClassInstance targetDir = targetPath.getInstance(targetServer);
                    return (targetDir != null);
                } catch(Exception e) {
                    logger.info("Failed to locate directory path: " + path, e);
                    return false;
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
                    
                    ModifySystemSettings.Output changePropRes = ModifySystemSettings.invoke(classes, targetServer, targetVm);
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
