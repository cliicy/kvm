package com.arcserve.winrm.cimv2.File;

import org.jdom.Element;
import org.openwsman.Client;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.JDomUtils;
import com.arcserve.winrm.Method;
import com.arcserve.winrm.MethodOutput;
import com.arcserve.winrm.Classes.ClassReference;

public abstract class Delete implements Method {
    public interface Output extends MethodOutput {}
    
    /** 
     * The Delete method deletes the logical file (or directory) that is specified in the object path. This method is inherited from CIM_LogicalFile.
     * 
     * @param classes The winrm classes definition schema.
     * @param conn The connection to make request with.
     * @param targetFile The source file to delete.
     * @return {@link Delete.Output} type object containing result info
     * @throws Exception 
     * 
     * @see http://msdn.microsoft.com/en-us/library/aa389875(v=vs.85).aspx
     * */
    public static Output invoke(Classes classes, Client conn, String targetFile) throws Exception {
        ClassReference vsms = classes.createReference("CIM_DataFile");
        vsms.addSelector("Name", targetFile);
        
        Integer retVal = -1;
        
        for (String resPart : vsms.invokeMethod(conn, "Delete")) {
            Element resElem = JDomUtils.buildElementFromString(resPart);
            
            if (resElem.getName().equals("ReturnValue")) {
                try {
                    retVal = Integer.parseInt(resElem.getText());
                } catch (Exception e) {
                    throw new Exception("Invalid response.");
                }
            } else
                throw new Exception("Invalid response.");
        }
        
        final int finalRetVal = retVal.intValue();
        
        return new Output() {
            public int getReturnValue() {
                return finalRetVal;
            }
            
            public String getReturnValueDescription() {
                if (finalRetVal == 0)
                    return "Success.";
                else if (finalRetVal == 2)
                    return "Access denied.";
                else if (finalRetVal == 8)
                    return "Unspecified failure.";
                else if (finalRetVal == 9)
                    return "Invalid object.";
                else if (finalRetVal == 10)
                    return "Object already exists.";
                else if (finalRetVal == 11)
                    return "File system not NTFS.";
                else if (finalRetVal == 12)
                    return "Platform not Windows.";
                else if (finalRetVal == 13)
                    return "Drive not the same.";
                else if (finalRetVal == 14)
                    return "Directory not empty.";
                else if (finalRetVal == 15)
                    return "Sharing violation.";
                else if (finalRetVal == 16)
                    return "Invalid start file.";
                else if (finalRetVal == 17)
                    return "Privilege not held.";
                else if (finalRetVal == 21)
                    return "Invalid parameter.";
                else
                    return "Unknown";
            }
        };
    }
}
