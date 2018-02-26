package com.arcserve.winrm.hyperv.v1.Storage;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutputWithJob;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class CreateDynamicVirtualHardDisk implements Method {
    public interface Output extends MethodOutputWithJob {}
    
    /** 
     * Creates a dynamically expanding virtual hard disk (.vhd) file.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param serverName The target server name.
     * @param fullPath The path to store the disk.
     * @param size The desired disk size, in bytes.
     * @return {@link CreateDynamicVirtualHardDisk.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/cc136778(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String serverName, String fullPath, Long size) throws Exception {
        ClassReference vhdsvc = classes.createReference("Msvm_ImageManagementService");
        vhdsvc.addSelector("CreationClassName", "Msvm_ImageManagementService");
        vhdsvc.addSelector("Name", "vhdsvc");
        vhdsvc.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vhdsvc.addSelector("SystemName", serverName);
        
        /** In case it's not aligned... */
        size = size / 512 * 512;
        
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vhdsvc.invokeMethod(conn, "CreateDynamicVirtualHardDisk",
            classes.ARG("Path", fullPath),
            classes.ARG("MaxInternalSize", size.toString()))) {
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
