package com.arcserve.winrm.hyperv.v1.Networking;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutputWithJob;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class DeleteSwitchPort implements Method {
    public interface Output extends MethodOutputWithJob {}
    
    /** 
     * Deletes a virtual switch port.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param serverName The target server name.
     * @param switchPortUuid The uuid of target switch port.
     * @return {@link DeleteSwitchPort.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/cc136788(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String serverName, String switchPortUuid) throws Exception {
        ClassReference vsms = classes.createReference("Msvm_VirtualSwitchManagementService");
        vsms.addSelector("CreationClassName", "Msvm_VirtualSwitchManagementService");
        vsms.addSelector("Name", "nvspwmi");
        vsms.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vsms.addSelector("SystemName", serverName.toUpperCase());
        
        ClassReference targetSwitchPort = classes.createReference("Msvm_SwitchPort");
        targetSwitchPort.addSelector("CreationClassName", "Msvm_SwitchPort");
        targetSwitchPort.addSelector("Name", switchPortUuid);
        
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vsms.invokeMethod(conn, "DeleteSwitchPort", classes.ARG("SwitchPort", targetSwitchPort))) {
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
                else if (finalRetVal == 1)
                    return "Not Supported";
                else if (finalRetVal == 2)
                    return "Failed ";
                else if (finalRetVal == 3)
                    return "Timeout";
                else if (finalRetVal == 4)
                    return "Invalid Parameter";
                else if (finalRetVal == 5)
                    return "Invalid State";
                else
                    return "Unknown";
            }
        };
    }
}
