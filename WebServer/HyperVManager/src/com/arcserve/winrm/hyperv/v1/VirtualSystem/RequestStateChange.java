package com.arcserve.winrm.hyperv.v1.VirtualSystem;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.ConcreteJob;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutputWithJob;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class RequestStateChange implements Method {
    public enum VmState {
        /** Turns the VM on. */
        Enabled("2"),
        /** Turns the VM off. */
        Disabled("3"),
        /** A hard reset of the VM. */
        Reboot("10"),
        /** For future use. */
        Reset("11"),
        /** Pauses the VM. */
        Paused("32768"),
        /** Saves the state of the VM. */
        Suspended("32769");
        
        String state;
        
        VmState(String initState) {
            this.state = initState;
        }
        
        public String get() {
            return this.state;
        }
    }
    
    public interface Output extends MethodOutputWithJob {}
    
    /** 
     * Requests that the state of the computer system be changed to the value specified in the RequestedState parameter. 
     * Invoking the RequestStateChange method multiple times could result in earlier requests being overwritten or lost. 
     * If 0 is returned, then the task completed successfully. Any other return code indicates an error condition.
     * While the state change is in progress, the RequestedState property is changed to the value of the RequestedState parameter.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param vmUuid The uuid of target vm.
     * @param requestedState {@link RequestStateChange.VmState} enumeration that the requested state the vm should trun into.
     * @return {@link RequestStateChange.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/cc723874(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String vmUuid, VmState requestedState) throws Exception {
        ClassReference vmRef = classes.createReference("Msvm_ComputerSystem");
        vmRef.addSelector("Name", vmUuid);
        
        ClassReference job = null;
        Integer retVal = -1;
        
        for (String resPart : vmRef.invokeMethod(conn, "RequestStateChange",
            classes.ARG("RequestedState", requestedState.get()))) {
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
                    return "Method Parameters Checked - Transition Started";
                else if (finalRetVal == 32768)
                    return "Failed";
                else if (finalRetVal == 32769)
                    return "Access denied";
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
