package com.arcserve.hyperv.remote.config;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import com.arcserve.linux.WinCommand;

public abstract class AutoDetectHyperV {
    private static Logger logger = Logger.getLogger(AutoDetectHyperV.class);
    
    public interface HyperVStatus {
        Boolean getHaveHyperV();
        
        Boolean getWinRMEnabled();
        
        Boolean getWinRMConfigCompatible();
        
        Map<Integer, String> getWinRMListeners();
    }
    
    @SuppressWarnings("unused")
    private final static String scriptChkWinRM         = "com/arcserve/hyperv/remote/config/checkwinrm";
    @SuppressWarnings("unused")
    private final static String scriptConfigWinRM      = "com/arcserve/hyperv/remote/config/configwinrm";
    @SuppressWarnings("unused")
    private final static String scriptQuickConfigWinRM = "com/arcserve/hyperv/remote/config/quickconfigwinrm";
    
    private final static String firewallRuleName = "\"arcserve UDP Instant VM service\"";
    private final static String[] scriptChkWinRMContent = {
        "chcp 437",
        "echo HyperVExists",
        "sc query vmms | FINDSTR \"STATE\"",
        "echo ServiceState",
        "sc query WinRM | FINDSTR \"STATE\"",
        "echo WinRMConfig",
        "cmd /C winrm get winrm/Config/Service | FINDSTR \"AllowUnencrypted Basic\"",
        "echo WinRMListeners",
        "cmd /C winrm enumerate winrm/Config/Listener"
    }; 
    private final static String[] scriptConfigWinRMContent = { 
        "cmd /C winrm set winrm/Config/Service @{AllowUnencrypted=\"true\"}",
        "cmd /C winrm set winrm/Config/Service/Auth @{Basic=\"true\"}",
        "cmd /C net localgroup WinRMRemoteWMIUsers__ /ADD",
        "cmd /C net localgroup WinRMRemoteWMIUsers__ [USERPLACEHOLDER] /ADD",
        "NET STOP WinRM",
        "NET START WinRM"
    }; 
    private final static String[] scriptQuickConfigWinRMContent = { 
        "cmd /C winrm quickconfig -quiet -force"
    }; 
    private final static String[] scriptQuickConfigSecureWinRMContent = { 
        "cmd /C winrm quickconfig -quiet -force -transport:https"
    }; 
    
    private final static String[] scriptShowCurrentFirewallRule = {
        "chcp 437",
        "set RULENAME=" + firewallRuleName,
        "netsh advfirewall firewall show rule name=%RULENAME% dir=in"
    };
    private final static String[] scriptAddFirewallRule = {
        "chcp 437",
        "set RULENAME=" + firewallRuleName,
        "set WINRMPORT=[PORTPLACEHOLDER]",
        "netsh advfirewall firewall add rule name=%RULENAME% localport=%WINRMPORT% dir=in action=allow protocol=tcp enable=yes"
    };

    @SuppressWarnings("unused")
    private static String getResource(String resPath) throws Exception {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            InputStream reader = Thread.currentThread().getContextClassLoader().getResourceAsStream(resPath);
            byte[] buff = new byte[1024];
            int rc = 0;
            while ((rc = reader.read(buff, 0, 100)) > 0) {
                output.write(buff, 0, rc);
            }
            reader.close();
            return output.toString();
        } catch (Exception e) {
            throw new Exception("Cannot get resource " + resPath + "!\n" + e.getMessage());
        }
    }
    private static String getResource(String[] contentArr) throws Exception {
        String result = "";
        for(int i = 0; i < contentArr.length; i++)
            result += contentArr[i] + '\n';
        return result;
    }
    
    public static HyperVStatus getInformation(String targetHost, String adminAccount, String adminPassword)
        throws Exception {
        try {
            WinCommand.Result chkResult = WinCommand.On(targetHost, adminAccount, adminPassword).runCommand(
                getResource(scriptChkWinRMContent));
            // Ignore the return code upon the check script.
//            if (chkResult.getReturnCode() != 0)
//                throw new Exception("Failed to check target box, RC: " + chkResult.getReturnCode() + " Output: " +
//                    chkResult.getOutput());
            /* Example Output:
                HyperVExists
                        STATE              : 4  RUNNING
                ServiceState
                        STATE              : 4  RUNNING
                WinRMConfig
                    AllowUnencrypted = true
                        Basic = true
                WinRMListeners
                Listener
                    Address = *
                    Transport = HTTPS
                    Port = 5986
                    Hostname
                    Enabled = true
                    URLPrefix = wsman
                    CertificateThumbprint
                    ListeningOn = 127.0.0.1, 155.35.82.247
                Listener
                    Address = *
                    Transport = HTTP
                    Port = 5985
                    Hostname
                    Enabled = true
                    URLPrefix = wsman
                    CertificateThumbprint
                    ListeningOn = 127.0.0.1, 155.35.82.247
             * */
            Boolean hyperVExists = false;
            Boolean winrmRunning = false;
            Boolean winrmAllowUnencrypted = false;
            Boolean winrmAuthBasic = false;
            String currentTransport = null;
            int currentPort = -1;
            final Hashtable<Integer, String> listeners = new Hashtable<Integer, String>();
            int parseStage = 0;
            
            if(chkResult.getReturnCode() != 0)
                /** Tolerate invalid output here since WinRM may not be running. */
                logger.debug("HyperV config script failed, further configuration needed!");
            
            String[] output = chkResult.getOutput().split("\n");
            Boolean needSkipListener = false;
            
            /**
             * Skip the first line since it's related to the chcp command
             * under European languages the output is localized, so skipping 
             * the line is more simple and robust.
             * */
            for (int i = 1; i < output.length; i++) { //(String line : output) {
                String line = output[i].trim();
                if (line.equals("HyperVExists"))
                    parseStage = 1;
                else if (line.equals("ServiceState"))
                    parseStage = 2;
                else if (line.equals("WinRMConfig"))
                    parseStage = 3;
                else if (line.equals("WinRMListeners"))
                    parseStage = 4;
//                else if(line.equalsIgnoreCase("Active code page: 437"))
//                    continue;
                else
                    switch (parseStage) {
                    case 1: {
                        if (line.toUpperCase().matches("^STATE.*"))
                            hyperVExists = true;
                        else
                            throw new Exception("Cannot parse Hyper-V existance status.\n" + chkResult.getOutput());
                    }
                        break;
                    case 2: {
                        if (line.toUpperCase().matches("^STATE.*")) {
                            if (line.toUpperCase().matches(".*RUNNING$"))
                                winrmRunning = true;
                            else
                                winrmRunning = false;
                        } else
                            throw new Exception("Cannot parse WinRM running status.\n" + chkResult.getOutput());
                    }
                        break;
                    case 3: {
                        if (line.toUpperCase().matches("^ALLOWUNENCRYPTED.*")) {
                            if (line.toUpperCase().matches(".*TRUE$"))
                                winrmAllowUnencrypted = true;
                            else
                                winrmAllowUnencrypted = false;
                        } else if (line.toUpperCase().matches("^BASIC.*")) {
                            if (line.toUpperCase().matches(".*TRUE$"))
                                winrmAuthBasic = true;
                            else
                                winrmAuthBasic = false;
                        } else
                            /** Tolerate invalid output here since WinRM may not be running. */
                            continue;
                    }
                        break;
                    case 4: {
                        if (line.toUpperCase().contains("Listener".toUpperCase()) &&
                            line.toUpperCase().contains("Source=".toUpperCase()) &&
                            line.toUpperCase().contains("Compatibility".toUpperCase()))
                            needSkipListener = true;
                        String[] kvp = line.split("=");
                        if (kvp.length != 2)
                            continue;
                        if (kvp[0].trim().equals("Transport"))
                            currentTransport = kvp[1].trim();
                        else if (kvp[0].trim().equals("Port"))
                            currentPort = Integer.parseInt(kvp[1].trim());
                        else if (kvp[0].trim().equals("Enabled")) {
                            if (kvp[1].trim().toUpperCase().equals("TRUE") && (!needSkipListener))
                                listeners.put(currentPort, currentTransport);
                            currentTransport = null;
                            currentPort = -1;
                            needSkipListener = false;
                        }
                    }
                        break;
                    default:
                        throw new Exception("Invalid check Hyper-V result:\n" + chkResult.getOutput());
                    }
            }
            
            final Boolean hyperVExistsFinal = hyperVExists;
            final Boolean winrmRunningFinal = winrmRunning;
            final Boolean winrmAllowUnencryptedFinal = winrmAllowUnencrypted;
            final Boolean winrmAuthBasicFinal = winrmAuthBasic;
            
            return new HyperVStatus() {
                @Override
                public Boolean getHaveHyperV() {
                    return hyperVExistsFinal;
                }
                
                @Override
                public Boolean getWinRMEnabled() {
                    return winrmRunningFinal;
                }
                
                @Override
                public Boolean getWinRMConfigCompatible() {
                    return (winrmAllowUnencryptedFinal && winrmAuthBasicFinal);
                }
                
                @Override
                public Map<Integer, String> getWinRMListeners() {
                    return listeners;
                }
            };
        } catch (Exception e) {
            throw new Exception("Failed to get Hyper-V information for target " + targetHost + "\n" + e.getMessage());
        }
    }
    
    public static Boolean configureWinRM(String targetHost, String adminAccount, String adminPassword) {
        try {
            String cmdUser = adminAccount;
            if(cmdUser.trim().startsWith(".")) {
                cmdUser = cmdUser.replace("./", "");
                cmdUser = cmdUser.replace(".\\", "");
            }
            logger.info("Configuring WinRM with user " + cmdUser + " " + adminAccount);
            WinCommand.Result configResult = WinCommand.On(targetHost, adminAccount, adminPassword).runCommand(
                getResource(scriptConfigWinRMContent).replace("[USERPLACEHOLDER]", cmdUser));
            if (configResult.getReturnCode() != 0)
                throw new Exception(configResult.getOutput());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static Boolean quickConfigureWinRM(String targetHost, String adminAccount, String adminPassword) {
        /** Try secure WinRM config first */
        try {
            WinCommand.Result qcResult = WinCommand.On(targetHost, adminAccount, adminPassword).runCommand(
                getResource(scriptQuickConfigSecureWinRMContent));
            if (qcResult.getReturnCode() != 0)
                throw new Exception(qcResult.getOutput());
            return true;
        } catch (Exception e) { e.printStackTrace(); }
        
        try {
            WinCommand.Result qcResult = WinCommand.On(targetHost, adminAccount, adminPassword).runCommand(
                getResource(scriptQuickConfigWinRMContent));
            if (qcResult.getReturnCode() != 0)
                throw new Exception(qcResult.getOutput());
            return true;
        } catch (Exception e) { e.printStackTrace(); }
        
        return false;
    }
    
    public static Boolean addFirewallRule(String targetHost, String adminAccount, String adminPassword, int desiredPort) {
        try {
            WinCommand.Result currRuleRes = WinCommand.On(targetHost, adminAccount, adminPassword).runCommand(
                getResource(scriptShowCurrentFirewallRule));
            if (currRuleRes.getReturnCode() == 0) {
                /** there're rule exist, check whether need add new one */
                for(String line: currRuleRes.getOutput().split("\n")) {
                    if(line.trim().startsWith("LocalPort:")) {
                        if(line.contains("" + desiredPort)) {
                            /** rule already added no need to re-add */
                            return true;
                        }                        
                    }
                }
            }
            /** we're still here, either no rule exists or existing
             *  rules are not for the desired port, add a new rule. */
            WinCommand.Result addRuleRes = WinCommand.On(targetHost, adminAccount, adminPassword).runCommand(
                getResource(scriptAddFirewallRule).replace("[PORTPLACEHOLDER]", "" + desiredPort));
            if (addRuleRes.getReturnCode() != 0)
                throw new Exception(addRuleRes.getOutput());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void main(String[] args) {
/** marked by cliicy.luo to do test
        try {
            AutoDetectHyperV.HyperVStatus status = AutoDetectHyperV.getInformation(args[0], args[1], args[2]);
            System.out.println("HyperV present  : " + status.getHaveHyperV());
            System.out.println("WinRM enabled   : " + status.getWinRMEnabled());
            System.out.println("WinRM listeners : ");
            for (Map.Entry<Integer, String> kvp : status.getWinRMListeners().entrySet()) {
                System.out.println(kvp.getValue() + ": " + kvp.getKey());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
**/
    	//KVMHypervisorImpl kvm_vm=new KVMHypervisorImpl();
 //   	IHypervisor hypervisor = HypervisorManager.getHypervisor(HypervisorType
//				.parse(jobConfig.getHypervisorType()));
    	
    }
    
}
