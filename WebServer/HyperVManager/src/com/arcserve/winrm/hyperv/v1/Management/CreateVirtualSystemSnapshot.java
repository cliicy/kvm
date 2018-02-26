package com.arcserve.winrm.hyperv.v1.Management;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutputWithJob;
import com.arcserve.winrm.Classes.ClassInstance;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class CreateVirtualSystemSnapshot implements Method {
    public interface Output extends MethodOutputWithJob {
        String getResultSnapshotUuid();
    }
    
    /** 
     * Creates a snapshot of a virtual computer system that is in the "Enabled", "Disabled", or "Suspended" state. 
     * The result of the snapshot is a new instance of Msvm_VirtualSystemSettingData representing the settings for
     * the virtual computer system at the time that the snapshot is taken. In addition, a new instance of each
     * CIM_ResourceAllocationSettingData object will also be created to represent the settings for the devices in 
     * the virtual system at the time that the snapshot is taken.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param vmUuid The uuid of target vm.
     * @return {@link CreateVirtualSystemSnapshot.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/cc136785(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String targetHostname, String vmUuid) throws Exception {
        ClassReference vsms = classes.createReference("Msvm_VirtualSystemManagementService");
        vsms.addSelector("CreationClassName", "Msvm_VirtualSystemManagementService");
        vsms.addSelector("Name", "vmms");
        vsms.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vsms.addSelector("SystemName", targetHostname);
        
        ClassReference targetVm = classes.createReference("Msvm_ComputerSystem");
        targetVm.addSelector("Name", vmUuid);
        
        ClassReference resSnapshot = null;
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vsms.invokeMethod(conn, "CreateVirtualSystemSnapshot",
            classes.ARG("SourceSystem", targetVm))) {
            Element resElem = JDomUtils.buildElementFromString(resPart);
            
            if (resElem.getName().equals("ReturnValue")) {
                try {
                    retVal = Integer.parseInt(resElem.getText());
                } catch (Exception e) {
                    throw new Exception("Invalid response.");
                }
            } else if (resElem.getName().equals("SnapshotSettingData")) {
                if (resElem.getChildren().size() > 0)
                    resSnapshot = classes.createReferenceFromSoapResponseElement(resElem);
            } else if (resElem.getName().equals("Job")) {
                if (resElem.getChildren().size() > 0)
                    job = classes.createReferenceFromSoapResponseElement(resElem);
            } else
                throw new Exception("Invalid response.");
        }
        
        final int finalRetVal = retVal.intValue();
        final ConcreteJob finalConcreteJob = new ConcreteJob(job);
        final ClassInstance finalResSnapshot = (resSnapshot == null) ? null : resSnapshot.getInstance(conn);
        
        return new Output() {
            public String getResultSnapshotUuid() {
                System.out.println(finalConcreteJob.dump());
                return (finalResSnapshot == null) ? null : finalResSnapshot.getProperty("Name").getSimpleValue();
            }
            
            public int getReturnValue() {
                return finalRetVal;
            }
            
            public ConcreteJob getConcreteJobRef() {
                return finalConcreteJob;
            }
            
            public String getReturnValueDescription() {
                if (finalRetVal == 0)
                    return "Completed with No Error";
                else if (finalRetVal == 4096)
                    return "Method Parameters Checked - Job Started";
                else if (finalRetVal == 1)
                    return "Not Supported";
                else if (finalRetVal == 2)
                    return "Failed ";
                else if (finalRetVal == 3)
                    return "Timeout ";
                else if (finalRetVal == 4)
                    return "Invalid Parameter";
                else if (finalRetVal == 5)
                    return "Invalid State";
                else if (finalRetVal == 6)
                    return "Invalid Type";
                else
                    return "Unknown";
            }
        };
    }
}
