package com.arcserve.winrm.hyperv.v1.Management;

import java.util.ArrayList;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutputWithJob;
import com.arcserve.winrm.Classes.ClassInstance;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class AddVirtualSystemResources implements Method {
    public interface Output extends MethodOutputWithJob {
        public ClassReference[] getCreatedResources();
    }
    
    /** 
     * Adds resources to an existing virtual system.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param vmUuid The uuid of target vm.
     * @param resSettings The resources to add.
     * @return {@link AddVirtualSystemResources.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/cc160705(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String targetHostname, String vmUuid, ClassInstance... resSettings) throws Exception {
        ClassReference vsms = classes.createReference("Msvm_VirtualSystemManagementService");
        vsms.addSelector("CreationClassName", "Msvm_VirtualSystemManagementService");
        vsms.addSelector("Name", "vmms");
        vsms.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vsms.addSelector("SystemName", targetHostname);
        
        ClassReference targetVm = classes.createReference("Msvm_ComputerSystem");
        targetVm.addSelector("Name", vmUuid);
        
        ArrayList<ClassReference> createdRes = new ArrayList<ClassReference>();
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vsms.invokeMethod(conn, "AddVirtualSystemResources",
            classes.ARG("TargetSystem", targetVm),
            classes.ARG("ResourceSettingData", resSettings))) {
            Element resElem = JDomUtils.buildElementFromString(resPart);
            
            if (resElem.getName().equals("ReturnValue")) {
                try {
                    retVal = Integer.parseInt(resElem.getText());
                } catch (Exception e) {
                    throw new Exception("Invalid response.");
                }
            } else if (resElem.getName().equals("NewResources")) {
                if (resElem.getChildren().size() > 0)
                    createdRes.add(classes.createReferenceFromSoapResponseElement(resElem));
            } else if (resElem.getName().equals("Job")) {
                if (resElem.getChildren().size() > 0)
                    job = classes.createReferenceFromSoapResponseElement(resElem);
            } else
                throw new Exception("Invalid response.");
        }
        
        final int finalRetVal = retVal.intValue();
        final ConcreteJob finalConcreteJob = new ConcreteJob(job);
        final ClassReference[] finalRes = createdRes.toArray(new ClassReference[createdRes.size()]);
        
        return new Output() {
            public ClassReference[] getCreatedResources() {
                return finalRes;
            }
            
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
                else if (finalRetVal == 1)
                    return "Not Supported";
                else if (finalRetVal == 2)
                    return "Failed";
                else if (finalRetVal == 3)
                    return "Timeout";
                else if (finalRetVal == 4)
                    return "Invalid Parameter";
                else
                    return "Unknown: " + finalRetVal;
            }
        };
    }
}
