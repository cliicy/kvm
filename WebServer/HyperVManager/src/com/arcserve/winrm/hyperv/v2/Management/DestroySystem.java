package com.arcserve.winrm.hyperv.v2.Management;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutputWithJob;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class DestroySystem implements Method {
    public interface Output extends MethodOutputWithJob {}
    
    /** 
     * Delete a VM
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param vmUuid The uuid of target vm.
     * @return {@link DestroySystem.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/hh850045(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String vmUuid) throws Exception {
        ClassReference vsms = classes.createReference("Msvm_VirtualSystemManagementService");
        vsms.addSelector("Name", "vmms");
        vsms.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vsms.addSelector("CreationClassName", "Msvm_VirtualSystemManagementService");
        
        ClassReference targetVm = classes.createReference("Msvm_ComputerSystem");
        targetVm.addSelector("Name", vmUuid);
        
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vsms.invokeMethod(conn, "DestroySystem", classes.ARG("AffectedSystem", targetVm))) {
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
                else
                    return "Unknown";
            }
        };
    }
}
