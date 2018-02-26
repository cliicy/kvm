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

public abstract class DefineVirtualSystem implements Method {
    public interface Output extends MethodOutputWithJob {
        public String getResultingSystemUuid();
    }
    
    /** 
     * Creates a new virtual computer system instance. Properties that are not specified will be populated with default values.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param name The desired name of resulting vm.
     * @param vmStoragePath The path to store the vm configuration.
     * @return {@link DefineVirtualSystem.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/cc136786(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String targetHostname, String name, String vmStoragePath) throws Exception {
        ClassReference vsms = classes.createReference("Msvm_VirtualSystemManagementService");
        vsms.addSelector("CreationClassName", "Msvm_VirtualSystemManagementService");
        vsms.addSelector("Name", "vmms");
        vsms.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vsms.addSelector("SystemName", targetHostname);
        
        ClassInstance vmConfig = classes.createInstance("Msvm_VirtualSystemGlobalSettingData");
        if ((name == null) || name.isEmpty())
            name = "Arcserve UDP Virtual Lab Virtual Machine";
        vmConfig.getProperty("ElementName").setSimpleValue(name);
        if ((vmStoragePath != null) && !vmStoragePath.isEmpty())
            vmConfig.getProperty("ExternalDataRoot").setSimpleValue(vmStoragePath);
        
        ClassReference resSystem = null;
        ClassReference job = null;
        Integer retVal = -1;
        String additionalDebugInfo = vmConfig.getDtdText() + "\n";
        
        for (String resPart : vsms.invokeMethod(conn, "DefineVirtualSystem", classes.ARG("SystemSettingData", vmConfig))) {
            Element resElem = JDomUtils.buildElementFromString(resPart);
            additionalDebugInfo += resPart + "\n";
            if (resElem.getName().equals("ReturnValue")) {
                try {
                    retVal = Integer.parseInt(resElem.getText());
                } catch (Exception e) {
                    throw new Exception("Invalid response: " + resElem.getText());
                }
            } else if (resElem.getName().equals("DefinedSystem")) {
                if (resElem.getChildren().size() > 0)
                    resSystem = classes.createReferenceFromSoapResponseElement(resElem);
            } else if (resElem.getName().equals("Job")) {
                if (resElem.getChildren().size() > 0)
                    job = classes.createReferenceFromSoapResponseElement(resElem);
            } else
                throw new Exception("Invalid response.");
        }
        
        final int finalRetVal = retVal.intValue();
        final ConcreteJob finalConcreteJob = new ConcreteJob(job);
        final ClassInstance finalresSystem = (resSystem == null) ? null : resSystem.getInstance(conn);
        final String finalAdditionalDebugInfo = additionalDebugInfo;
        
        return new Output() {
            public String getResultingSystemUuid() {
                return (finalresSystem == null) ? null : finalresSystem.getProperty("Name").getSimpleValue();
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
                else
                    return "Unknown: " + finalAdditionalDebugInfo;
            }
        };
    }
}
