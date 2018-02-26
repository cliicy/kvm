package com.arcserve.winrm.hyperv.v2.Management;

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

public abstract class ModifyResourceSettings implements Method {
    public interface Output extends MethodOutputWithJob {
        public ClassReference[] getModifiedResources();
    }
    
    /** 
     * Removes virtual resource settings from a virtual machine configuration. When applied to parts of a
     *  current virtual machine configuration, the active virtual machine may be removed.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param resSettings The resources to modify.
     * @return {@link ModifyResourceSettings.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/hh850099(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, ClassInstance... resSettings) throws Exception {
        ClassReference vsms = classes.createReference("Msvm_VirtualSystemManagementService");
        vsms.addSelector("Name", "vmms");
        vsms.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vsms.addSelector("CreationClassName", "Msvm_VirtualSystemManagementService");
        
        ArrayList<ClassReference> modifiedRes = new ArrayList<ClassReference>();
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vsms.invokeMethod(conn, "ModifyResourceSettings",
            classes.ARG("ResourceSettings", resSettings))) {
            Element resElem = JDomUtils.buildElementFromString(resPart);
            
            if (resElem.getName().equals("ReturnValue")) {
                try {
                    retVal = Integer.parseInt(resElem.getText());
                } catch (Exception e) {
                    throw new Exception("Invalid response.");
                }
            } else if (resElem.getName().equals("ResultingResourceSettings")) {
                if (resElem.getChildren().size() > 0)
                    modifiedRes.add(classes.createReferenceFromSoapResponseElement(resElem));
            } else if (resElem.getName().equals("Job")) {
                if (resElem.getChildren().size() > 0)
                    job = classes.createReferenceFromSoapResponseElement(resElem);
            } else
                throw new Exception("Invalid response.");
        }
        
        final int finalRetVal = retVal.intValue();
        final ConcreteJob finalConcreteJob = new ConcreteJob(job);
        final ClassReference[] finalModifiedRes = modifiedRes.toArray(new ClassReference[modifiedRes.size()]);
        
        return new Output() {
            public ClassReference[] getModifiedResources() {
                return finalModifiedRes;
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
                    return "Method Parameters Checked - Job Started";
                else if (finalRetVal == 1)
                    return "Not Supported";
                else if (finalRetVal == 2)
                    return "Failed";
                else if (finalRetVal == 3)
                    return "Timeout";
                else if (finalRetVal == 4)
                    return "Invalid Parameter";
                else if (finalRetVal == 5)
                    return "Invalid State";
                else if (finalRetVal == 6)
                    return "Incompatible Parameters";
                else
                    return "Unknown: " + finalRetVal;
            }
        };
    }
}
