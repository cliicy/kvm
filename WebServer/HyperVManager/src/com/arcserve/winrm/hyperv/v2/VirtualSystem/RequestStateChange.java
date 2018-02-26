package com.arcserve.winrm.hyperv.v2.VirtualSystem;

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
        /** Corresponds to CIM_EnabledLogicalElement.EnabledState = Other. */
        Other("1"),
        /** Corresponds to CIM_EnabledLogicalElement.EnabledState = Enabled. */
        Running("2"),
        /** Corresponds to CIM_EnabledLogicalElement.EnabledState = Disabled. */
        Off("3"),
        /** Valid in version 1 (V1) of Hyper-V only. The virtual machine is shutting down via the shutdown service. Corresponds to CIM_EnabledLogicalElement.EnabledState = ShuttingDown. */
        Stopping("4"),
        /** Corresponds to CIM_EnabledLogicalElement.EnabledState = Enabled but offline. */
        Saved("6"),
        /** Corresponds to CIM_EnabledLogicalElement.EnabledState = Quiesce, Enabled but paused. */
        Paused("9"),
        /** State transition from Off or Saved to Running. */
        Starting("10"),
        /** Reset the virtual machine. Corresponds to CIM_EnabledLogicalElement.EnabledState = Reset. */
        Reset("11"),
        /** In version 1 (V1) of Hyper-V, corresponds to EnabledStateSaving. */
        Saving("32773"),
        /** In version 1 (V1) of Hyper-V, corresponds to EnabledStatePausing. */
        Pausing("32776"),
        /** In version 1 (V1) of Hyper-V, corresponds to EnabledStateResuming. State transition from Paused to Running. */
        Resuming("32777"),
        /** Corresponds to EnabledStateFastSuspend. */
        FastSaved("32779"),
        /** Corresponds to EnabledStateFastSuspending. State transition from Running to FastSaved. */
        FastSaving("32780");
        
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
     * Power on/off a VM.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param vmUuid The uuid of target vm.
     * @param requestedState {@link RequestStateChange.VmState} enumeration that the requested state the vm should trun into.
     * @return {@link RequestStateChange.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/hh850279(v=vs.85).aspx
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
                else if (finalRetVal == 32769)
                    return "Access denied";
                else if (finalRetVal == 32775)
                    return "Invalid state for this operation";
                else if (finalRetVal == 4096)
                    return "Method Parameters Checked - Transition Started";
                else
                    return "Unknown";
            }
        };
    }
}
