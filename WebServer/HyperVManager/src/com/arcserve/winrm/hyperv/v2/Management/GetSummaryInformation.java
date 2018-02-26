package com.arcserve.winrm.hyperv.v2.Management;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutput;
import com.arcserve.winrm.Classes.ClassInstance;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class GetSummaryInformation implements Method {
    public interface Output extends MethodOutput {
        public ClassInstance[] getSummaries();
    }
    
    /** 
     * Get summary informations of all VMs.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @return {@link GetSummaryInformation.Output} type object containing result info   
     * @throws Exception 
     *  
     * @see http://msdn.microsoft.com/en-us/library/hh850062(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn) throws Exception {
        ClassReference vsms = classes.createReference("Msvm_VirtualSystemManagementService");
        vsms.addSelector("Name", "vmms");
        vsms.addSelector("SystemCreationClassName", "Msvm_ComputerSystem");
        vsms.addSelector("CreationClassName", "Msvm_VirtualSystemManagementService");
        
        List<ClassInstance> summaries = new ArrayList<ClassInstance>();
        Integer retVal = -1;
        /** 
         *  0: Name    
         *  1: Element Name         
         *  2: Creation Time   
         *  4: Number of Processors                  
         *100: EnabledState            
         *101: ProcessorLoad               
         *103: MemoryUsage                
         *105: Uptime            
         *106: GuestOperatingSystem    
         */
        for (String resPart : vsms.invokeMethod(conn, "GetSummaryInformation", classes.ARG(
            "RequestedInformation", new String[] { "0", "1", "2", "4", "100", "101", "103", "105", "106" }))) {
            Element resElem = JDomUtils.buildElementFromString(resPart);
            
            if (resElem.getName().equals("ReturnValue")) {
                try {
                    retVal = Integer.parseInt(resElem.getText());
                } catch (Exception e) {
                    throw new Exception("Invalid response.");
                }
            } else if (resElem.getName().equals("SummaryInformation"))
                summaries.add(classes.createInstanceFromSoapResponseElement(resElem.setName("Msvm_SummaryInformation")));
            else
                throw new Exception("Invalid response.");
        }
        
        final ClassInstance[] finalSummaries = summaries.toArray(new ClassInstance[summaries.size()]);
        final int finalRetVal = retVal.intValue();
        
        return new Output() {
            public int getReturnValue() {
                return finalRetVal;
            }
            
            public ClassInstance[] getSummaries() {
                return finalSummaries;
            }
            
            public String getReturnValueDescription() {
                if (finalRetVal == 32768)
                    return "Failed";
                else if (finalRetVal == 32769)
                    return "Access Denied";
                else if (finalRetVal == 32773)
                    return "Invalid parameter";
                else if (finalRetVal == 32778)
                    return "Out of memory";
                else if (finalRetVal == 0)
                    return "Completed with No Error";
                else
                    return "Unknown";
            }
        };
    }
}
