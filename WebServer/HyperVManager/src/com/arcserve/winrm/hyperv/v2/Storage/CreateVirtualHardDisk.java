package com.arcserve.winrm.hyperv.v2.Storage;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutputWithJob;
import com.arcserve.winrm.Classes.ClassInstance;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class CreateVirtualHardDisk implements Method {
    public interface Output extends MethodOutputWithJob {}
    
    /** 
     * Creates a virtual hard disk file.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param fullPath The path to store the disk.
     * @param size The desired disk size, in bytes.
     * @return {@link CreateVirtualHardDisk.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/hh850039(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String fullPath, Long size) throws Exception {
        ClassReference vhdsvc = classes.createReference("Msvm_ImageManagementService");
        vhdsvc.addSelector("Name", "vhdsvc");
        vhdsvc.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vhdsvc.addSelector("CreationClassName", "Msvm_ImageManagementService");
        
        ClassInstance diskConfig = classes.createInstance("Msvm_VirtualHardDiskSettingData");
        diskConfig.getProperty("MaxInternalSize").setSimpleValue(size.toString());
        /** size in bytes */
        diskConfig.getProperty("Format").setSimpleValue("3");
        /** VHD(2), VHDX(3) */
        diskConfig.getProperty("Type").setSimpleValue("3");
        /** Fixed(2), Dynamic(3), Differencing(4) */
        if ((fullPath != null) && !fullPath.isEmpty())
            diskConfig.getProperty("Path").setSimpleValue(fullPath);
        
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vhdsvc.invokeMethod(conn, "CreateVirtualHardDisk",
            classes.ARG("VirtualDiskSettingData", diskConfig.getDtdText()))) {
            Element resElem = JDomUtils.buildElementFromString(resPart);
            
            if (resElem.getName().equals("ReturnValue")) {
                try {
                    retVal = Integer.parseInt(resElem.getText());
                } catch (Exception e) {
                    throw new Exception("Invalid response.");
                }
            } else if (resElem.getName().equals("Job")) {
                if (resElem.getChildren().size() > 0)
                    job = classes.createReferenceFromSoapResponseElement(resElem);
            } else
                throw new Exception("Invalid response.");
        }
        
        final int finalRetVal = retVal.intValue();
        final ConcreteJob finalConcreteJob = new ConcreteJob(job);
        
        return new Output() {
            public int getReturnValue() {
                return finalRetVal;
            }
            
            public ConcreteJob getConcreteJobRef() {
                return finalConcreteJob;
            }
            
            public String getReturnValueDescription() {
                if (finalRetVal == 0)
                    return "No Error";
                else if (finalRetVal == 4096)
                    return "Method Parameters Checked - Transition Started";
                else if (finalRetVal == 32768)
                    return "Failed";
                else if (finalRetVal == 32769)
                    return "Access Denied";
                else if (finalRetVal == 32770)
                    return "Not Supported";
                else if (finalRetVal == 32771)
                    return "Status is unknown";
                else if (finalRetVal == 32772)
                    return "Timeout";
                else if (finalRetVal == 32773)
                    return "Invalid parameter";
                else if (finalRetVal == 32774)
                    return "System is in use";
                else if (finalRetVal == 32775)
                    return "Invalid state for this operation";
                else if (finalRetVal == 32776)
                    return "Incorrect data type";
                else if (finalRetVal == 32777)
                    return "System is not available";
                else if (finalRetVal == 32778)
                    return "Out of memory";
                else if (finalRetVal == 32779)
                    return "File not found";
                else
                    return "Unknown";
            }
        };
    }
}
