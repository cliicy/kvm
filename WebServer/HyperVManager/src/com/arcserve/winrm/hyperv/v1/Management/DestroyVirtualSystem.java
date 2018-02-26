package com.arcserve.winrm.hyperv.v1.Management;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutputWithJob;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class DestroyVirtualSystem implements Method {
    public interface Output extends MethodOutputWithJob {}
    
    /** 
     * Removes the previously defined virtual computer system from the management scope of the host system. 
     * Any associated resource definitions will also be removed. The virtual computer system must be in the
     * powered off or saved state prior to calling this method.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param vmUuid The uuid of target vm.
     * @return {@link DestroyVirtualSystem.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/cc136790(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String targetHostname, String vmUuid) throws Exception {
        ClassReference vsms = classes.createReference("Msvm_VirtualSystemManagementService");
        vsms.addSelector("CreationClassName", "Msvm_VirtualSystemManagementService");
        vsms.addSelector("Name", "vmms");
        vsms.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vsms.addSelector("SystemName", targetHostname);
        
        ClassReference targetVm = classes.createReference("Msvm_ComputerSystem");
        targetVm.addSelector("Name", vmUuid);
        
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vsms.invokeMethod(conn, "DestroyVirtualSystem", classes.ARG("ComputerSystem", targetVm))) {
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
