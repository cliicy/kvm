package com.arcserve.winrm.hyperv.v2.Management;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutputWithJob;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class DestroySnapshotTree implements Method {
    public interface Output extends MethodOutputWithJob {}
    
    /** 
     * Deletes a snapshot tree.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param vmUuid The uuid of target snapshot.
     * @return {@link DestroySnapshotTree.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/hh850042(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String snapUuid) throws Exception {
        ClassReference vsms = classes.createReference("Msvm_VirtualSystemSnapshotService");
        vsms.addSelector("Name", "vssnapsvc");
        vsms.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vsms.addSelector("CreationClassName", "Msvm_VirtualSystemSnapshotService");
        
        ClassReference targetSnap = classes.createReference("Msvm_VirtualSystemSettingData ");
        targetSnap.addSelector("InstanceID", "Microsoft:" + snapUuid.toUpperCase());
        
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vsms.invokeMethod(conn, "DestroySnapshotTree",
            classes.ARG("SnapshotSettingData", targetSnap))) {
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
                    return "Completed with No Error";
                else if (finalRetVal == 4096)
                    return "Method Parameters Checked - Job Started";
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
                else
                    return "Unknown";
            }
        };
    }
}
