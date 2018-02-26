package com.arcserve.winrm.hyperv.v2.Networking;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutputWithJob;
import com.arcserve.winrm.Classes.ClassInstance;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class DefineSystem implements Method {
    public interface Output extends MethodOutputWithJob {
        public String getResultSwitchUuid();
    }
    
    /** 
     * Define a VM, with default settings.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param name The desired name of resulting switch.
     * @return {@link DefineSystem.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/hh850040(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String name) throws Exception {
        ClassReference vsms = classes.createReference("Msvm_VirtualEthernetSwitchManagementService");
        vsms.addSelector("Name", "nvspwmi");
        vsms.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vsms.addSelector("CreationClassName", "Msvm_VirtualEthernetSwitchManagementService");
        
        ClassInstance vmConfig = classes.createInstance("Msvm_VirtualEthernetSwitchSettingData");
        vmConfig.getProperty("ElementName").setSimpleValue(name);
        
        ClassReference resSwitch = null;
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vsms.invokeMethod(conn, "DefineSystem",
            classes.ARG("SystemSettings", vmConfig.getDtdText()))) {
            Element resElem = JDomUtils.buildElementFromString(resPart);
            
            if (resElem.getName().equals("ReturnValue")) {
                try {
                    retVal = Integer.parseInt(resElem.getText());
                } catch (Exception e) {
                    throw new Exception("Invalid response.");
                }
            } else if (resElem.getName().equals("ResultingSystem")) {
                if (resElem.getChildren().size() > 0)
                    resSwitch = classes.createReferenceFromSoapResponseElement(resElem);
            } else if (resElem.getName().equals("Job")) {
                if (resElem.getChildren().size() > 0)
                    job = classes.createReferenceFromSoapResponseElement(resElem);
            } else
                throw new Exception("Invalid response.");
        }
        
        final int finalRetVal = retVal.intValue();
        final ConcreteJob finalConcreteJob = new ConcreteJob(job);
        final ClassInstance finalResSwitch = (resSwitch == null) ? null : resSwitch.getInstance(conn);
        
        return new Output() {
            public String getResultSwitchUuid() {
                return (finalResSwitch == null) ? null : finalResSwitch.getProperty("Name").getSimpleValue();
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
                    return "Failed ";
                else if (finalRetVal == 3)
                    return "Timeout";
                else if (finalRetVal == 4)
                    return "Invalid Parameter";
                else
                    return "Unknown";
            }
        };
    }
}
