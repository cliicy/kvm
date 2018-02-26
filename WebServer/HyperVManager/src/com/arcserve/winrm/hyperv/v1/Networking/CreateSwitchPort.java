package com.arcserve.winrm.hyperv.v1.Networking;

import java.util.UUID;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutputWithJob;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class CreateSwitchPort implements Method {
    public interface Output extends MethodOutputWithJob {
        public ClassReference getResultSwitchPort();
    }
    
    /** 
     * Creates a new port on a virtual switch.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param serverName The target server name.
     * @param switchUuid The uuid of the switch.
     * @param name The name of the port.
     * @return {@link CreateSwitchPort.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/cc136782(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String serverName, String switchUuid, String name) throws Exception {
        ClassReference vsms = classes.createReference("Msvm_VirtualSwitchManagementService");
        vsms.addSelector("CreationClassName", "Msvm_VirtualSwitchManagementService");
        vsms.addSelector("Name", "nvspwmi");
        vsms.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vsms.addSelector("SystemName", serverName.toUpperCase());
        
        ClassReference switchRef = classes.createReference("Msvm_VirtualSwitch");
        switchRef.addSelector("CreationClassName", "Msvm_VirtualSwitch");
        switchRef.addSelector("Name", switchUuid);
        
        ClassReference resSwitchPort = null;
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vsms.invokeMethod(conn, "CreateSwitchPort",
            classes.ARG("VirtualSwitch", switchRef),
            classes.ARG("Name", UUID.randomUUID().toString()),
            classes.ARG("FriendlyName", name),
            classes.ARG("ScopeOfResidence", ""))) {
            Element resElem = JDomUtils.buildElementFromString(resPart);
            
            if (resElem.getName().equals("ReturnValue")) {
                try {
                    retVal = Integer.parseInt(resElem.getText());
                } catch (Exception e) {
                    throw new Exception("Invalid response.");
                }
            } else if (resElem.getName().equals("CreatedSwitchPort")) {
                if (resElem.getChildren().size() > 0)
                    resSwitchPort = classes.createReferenceFromSoapResponseElement(resElem);
            } else if (resElem.getName().equals("Job")) {
                if (resElem.getChildren().size() > 0)
                    job = classes.createReferenceFromSoapResponseElement(resElem);
            } else
                throw new Exception("Invalid response.");
        }
        
        final int finalRetVal = retVal.intValue();
        final ConcreteJob finalConcreteJob = new ConcreteJob(job);
        final ClassReference finalResSwitch = (resSwitchPort == null) ? null : resSwitchPort;
        
        return new Output() {
            public ClassReference getResultSwitchPort() {
                return finalResSwitch;
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
