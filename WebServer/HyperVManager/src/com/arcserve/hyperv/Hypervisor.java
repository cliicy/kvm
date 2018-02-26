package com.arcserve.hyperv;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.arcserve.hyperv.remote.config.AutoDetectHyperV;
import com.arcserve.linux.WinCommand;

public class Hypervisor {
    private static final Logger logger = Logger.getLogger(Hypervisor.class);
    
    private Connection conn = null;
    private String target = null;
    private String user = null;
    private String passwd = null;
    

    public Boolean supportsGeneration2VM() {
        return this.conn.supportsGeneration2VM();
    }
    
    public Boolean connect(String host, String userName, String password) throws Exception {
        return this.connect(host, userName, password, null, -1);
    }
    
    private static void log_debug(String content) {
        log_debug(content, false);
    }
    private static void log_debug(String content, boolean critical) {
        if(critical || (System.getenv("DEBUG_HYPERV_CONN") != null))
            System.err.println(content);
    }
    
    private static Boolean cachedConnectionInfoExists(String host) throws Exception {
        host = host.toUpperCase();
        String d2dServerHome = System.getenv("D2DSVR_HOME");
        final String hypervConfigFolderName = "hyperv";
        if((d2dServerHome == null) || d2dServerHome.isEmpty())
            return false;
        if(!Files.isDirectory(Paths.get(d2dServerHome, "configfiles")))
            return false;
        if(!Files.isDirectory(Paths.get(d2dServerHome, "configfiles", hypervConfigFolderName))) {
            Files.createDirectory(Paths.get(d2dServerHome, "configfiles", hypervConfigFolderName));
            return false;
        }
        if(!Files.exists(Paths.get(d2dServerHome, "configfiles", hypervConfigFolderName, host+ ".cfg")))
            return false;
        return true;
    }
    private static String cachedProtocol(String host) throws Exception {
        host = host.toUpperCase();
        final String hypervConfigFolderName = "hyperv";
        if(cachedConnectionInfoExists(host)){
            String d2dServerHome = System.getenv("D2DSVR_HOME");
            for(String line: Files.readAllLines(Paths.get(d2dServerHome, "configfiles", hypervConfigFolderName, host + ".cfg"), Charset.defaultCharset()))
                if(line.matches("^protocol\\w*=.*$"))
                    return line.split("=", 2)[1];
        }
        return "";
    }    
    private static int cachedPort(String host) throws Exception {
        host = host.toUpperCase();
        final String hypervConfigFolderName = "hyperv";
        if (cachedConnectionInfoExists(host)) {
            String d2dServerHome = System.getenv("D2DSVR_HOME");
            for (String line : Files.readAllLines(Paths.get(d2dServerHome, "configfiles", hypervConfigFolderName, host + ".cfg"), Charset.defaultCharset()))
                if (line.matches("^port\\w*=.*$"))
                    return Integer.parseInt(line.split("=", 2)[1]);
        }
        return -1;
    }
    /**
     * This method may fail silently, by design
     * */
    private static void writeConnectionInfoCache(String host, String protocol, int port) {
        host = host.toUpperCase();
        String d2dServerHome = System.getenv("D2DSVR_HOME");
        final String hypervConfigFolderName = "hyperv";
        if ((d2dServerHome == null) || d2dServerHome.isEmpty())
            return;
        try {
            if (!Files.isDirectory(Paths.get(d2dServerHome, "configfiles")))
                return;
            if (!Files.isDirectory(Paths.get(d2dServerHome, "configfiles", hypervConfigFolderName))) {
                Files.createDirectory(Paths.get(d2dServerHome, "configfiles", hypervConfigFolderName));
                return;
            }
            
            Files.write(Paths.get(d2dServerHome, "configfiles", hypervConfigFolderName, host + ".cfg"),
                ("protocol=" + protocol + "\n" + "port=" + port).getBytes()
                , StandardOpenOption.CREATE);
        } catch (Exception e) {
            System.err.println(e);
        }
        log_debug("Updated auto config cache for " + host);
    }
    
    public class AutoConfigResult {
        private String protocol = "";
        private int port = -1;
        private boolean success = true;
        private Exception exception = null;
        public AutoConfigResult() {}
        /**
         * @return the protocol
         */
        public String getProtocol() {
            return protocol;
        }
        /**
         * @param protocol the protocol to set
         */
        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }
        /**
         * @return the success
         */
        public boolean isSuccess() {
            return success;
        }
        /**
         * @return the port
         */
        public int getPort() {
            return port;
        }
        /**
         * @param port the port to set
         */
        public void setPort(int port) {
            this.port = port;
        }
        /**
         * @return the exception
         */
        public Exception getException() {
            return exception;
        }
        /**
         * @param exception the exception to set
         */
        public void setException(Exception exception) {
            this.exception = exception;
            if(this.exception == null)
                this.success = true;
            else this.success = false;
        }
    }
    private static AutoConfigResult autoConfigHyperV(Hypervisor hv, String host, String userName, String password, String expectedProtocol, int expectedPort) {
        AutoConfigResult result = hv.new AutoConfigResult();
        AutoDetectHyperV.HyperVStatus status = null;

        try {
            status = AutoDetectHyperV.getInformation(host, userName, password);
        } catch(Exception e) {
            String errMessage = e.getMessage();
            if(errMessage.contains("Current OS doesn't have `net' command"))
                result.setException(new HyperVException(HyperVException.ERROR_NET_UTIL_NOT_FOUND, e.getMessage()));
            else if(errMessage.toUpperCase().contains("NT_STATUS") || errMessage.toUpperCase().contains("IO_TIMEOUT"))
                result.setException( new HyperVException(HyperVException.ERROR_NET_UTIL_FAILED, e.getMessage()));
            else result.setException( e);
            return result;
        }
        try {
            if (!status.getHaveHyperV()) {
                result.setException( new Exception("Hyper-V is not installed on target " + host));
                return result;
            }
            if (!status.getWinRMConfigCompatible()) {
                if (AutoDetectHyperV.configureWinRM(host, userName, password))
                    status = AutoDetectHyperV.getInformation(host, userName, password);
                else {
                    result.setException( new Exception("Failed to configure WinRM service on target " + host));
                    return result;
                }
            }
            if (!(status.getWinRMEnabled()) || (status.getWinRMListeners().entrySet().size() < 1)) {
                if (AutoDetectHyperV.quickConfigureWinRM(host, userName, password))
                    status = AutoDetectHyperV.getInformation(host, userName, password);
                else{
                    result.setException( new Exception("Failed to configure WinRM service on target " + host));
                    return result;
                }
            }
            
            if (status.getWinRMListeners().entrySet().size() < 1) {
                result.setException(new Exception("WinRM is not properly configured on the target " + host));
                return result;
            }
        } catch(Exception e) {
            result.setException(new HyperVException(HyperVException.ERROR_CONFIG_HV_SERVER, e.getMessage()));
            return result;
        }
        /* Just get one listener */
        for (Entry<Integer, String> l : status.getWinRMListeners().entrySet()) {
            if ((expectedProtocol == null) || (expectedPort == -1)) {
                /** Here we try to find a HTTPS listener if possible */
                if((result.getProtocol() != null) && !result.getProtocol().isEmpty()) {
                    if(l.getValue().trim().equalsIgnoreCase("https")) {
                        result.setPort(l.getKey());
                        result.setProtocol(l.getValue());
                    }                        
                } else {
                    result.setPort(l.getKey());
                    result.setProtocol(l.getValue());
                }
            } else if ((l.getValue().toUpperCase().equals(expectedProtocol.toUpperCase())) &&
                (expectedPort == l.getKey())) {
                result.setPort(l.getKey());
                result.setProtocol(l.getValue());
            } else {
                // Ignoring not matched port and protocol
            }
        }
        if ((expectedProtocol != null) && (expectedPort != -1)) {
            if ((!expectedProtocol.equals(result.getPort())) && (expectedPort != result.getPort())) {
                result.setException(new Exception("Specified protocol '" + expectedProtocol + ": " + expectedPort +
                    "' is not found on target."));
                return result;
            }
        } else if ((result.getProtocol() == null) || (result.getPort() == -1)) {
            result.setException(new Exception("WinRM is not properly configured on the target " + host));
            return result;
        }
        
        /** finally try add firewall rule for winrm */
        if(!AutoDetectHyperV.addFirewallRule(host, userName, password, result.getPort())) {
            result.setException(new Exception("Failed to open port " + result.getPort() + " for WinRM on target " + host));
            return result;
        }
        
        return result;
    }
    
    public Boolean connect(String host, String userName, String password, String expectedProtocol, int expectedPort) 
        throws Exception {
        this.target = host;
        this.user = userName;
        this.passwd = password;
        
        Integer port = -1;
        String protocol = null;
        
        String autoConfigureHyperV = System.getenv("auto_configure_hyperv");
        if(autoConfigureHyperV != null && "false".equalsIgnoreCase(autoConfigureHyperV)){
            log_debug("auto_configure_hyperv is set to false");
            protocol = expectedProtocol;
            port = expectedPort;
        } else {
            if(cachedConnectionInfoExists(host)) {
                log_debug("Trying cached conn info");
                /**
                 * Try get cached information first
                 * */
                try {
                    protocol = cachedProtocol(host);
                    port = cachedPort(host);
                } catch(Exception e) {
                    log_debug("Failed to get cached conn info");
                    log_debug(e.toString());
                    /** 
                     * Try auto config here 
                     * */
                    AutoConfigResult res = autoConfigHyperV(this, host, userName, password, expectedProtocol, expectedPort);
                    if(!res.success) throw res.getException();
                    protocol = res.getProtocol();
                    port = res.getPort();
                    this.conn = new Connection(
                        host, userName, password, port,
                        protocol.toLowerCase().equals("http"));
                    writeConnectionInfoCache(host, protocol, port);
                    return true;
                }
                try {
                    this.conn = new Connection(
                        host, userName, password, port,
                        protocol.toLowerCase().equals("http"));
                } catch(Exception e) {
                    /**
                     * Failed to connect using the cached info
                     * Try auto config again
                     * */
                    log_debug("Failed to connect using cached conn info " + protocol + " " + port);
                    log_debug(e.toString());
                    AutoConfigResult res = autoConfigHyperV(this, host, userName, password, expectedProtocol, expectedPort);
                    if(!res.success) throw res.getException();
                    protocol = res.getProtocol();
                    port = res.getPort();
                    this.conn = new Connection(
                        host, userName, password, port,
                        protocol.toLowerCase().equals("http"));
                    writeConnectionInfoCache(host, protocol, port);
                    return true;
                }
                /**
                 * Connect using cached info success, no more action required
                 * */
                return true;
            } else {
                /**
                 * No cache, auto config directly
                 * */
                AutoConfigResult res = autoConfigHyperV(this, host, userName, password, expectedProtocol, expectedPort);
                if(!res.success) throw res.getException();
                protocol = res.getProtocol();
                port = res.getPort();
                this.conn = new Connection(
                    host, userName, password, port,
                    protocol.toLowerCase().equals("http"));
                writeConnectionInfoCache(host, protocol, port);
                return true;
            }
        }

        /**
         * no auto config and no cached conn info
         * */
        this.conn = new Connection(
            host, userName, password, port,
            protocol.toLowerCase().equals("http"));
        
        return true;
    }
    
    public void close() {
        conn.close();
        return;
    }
    
    /**
     * disks each entry example: <[String]"{controller}\n{storage}", [Long]size>
     * @throws Exception 
     * */
    public String createVM(String vmStoragePath, String vmName, String bootOption, Integer cpuCount,
        Long memorySize, List<SimpleEntry<String, String>> nicToAddByMacAndHostNetworkName, String cdPath, List<SimpleEntry<String, Long>> disks)
        throws Exception {
        return createVM(1, vmStoragePath, vmName, bootOption, cpuCount, memorySize, nicToAddByMacAndHostNetworkName, cdPath, disks);
    }
    public String createVM(int generation, String vmStoragePath, String vmName, String bootOption, Integer cpuCount,
        Long memorySize, List<SimpleEntry<String, String>> nicToAddByMacAndHostNetworkName, String cdPath, List<SimpleEntry<String, Long>> disks) throws Exception {
        logger.info(String.format("Creating generation %d vm: [%s], [%s], [%s], [%d], [%d], [%s].", generation, vmStoragePath, vmName, bootOption, cpuCount, memorySize, cdPath));
        return this.conn.getTargetAPI().createVM(generation, vmStoragePath, vmName, bootOption, cpuCount, memorySize,
            nicToAddByMacAndHostNetworkName, cdPath, disks);
    }
    
    public void changeBootOrder(String uuid, String bootOption) throws Exception  {
        this.conn.getTargetAPI().changeBootOrder(uuid, bootOption);
    }
    
    public boolean deleteVM(String uuid) throws Exception {
        try {
            for (String vd : this.conn.getTargetAPI().getVirtualDisksList(uuid))
                this.deleteFile(null, vd);
            //System.out.println("Remove virtual hard disk " + vd + " : " + this.deleteFile(null, vd));
        } catch (Exception e) {
            logger.error("Failed to enum/remove assosiated virtual hard disks.", e);
        }
        return this.conn.getTargetAPI().deleteVM(uuid);
    }
    
    public boolean startVM(String uuid) throws Exception {
        return this.conn.getTargetAPI().startVM(uuid);
    }
    
    public boolean stopVM(String uuid) throws Exception {
        return this.conn.getTargetAPI().stopVM(uuid);
    }
    
    public Map<String, List<String>> getMACListForVMs() throws Exception {
        return this.conn.getTargetAPI().getMACListForVMs();
    }
    
    public List<String> getHypervisorNetworkList() throws Exception {
        return this.conn.getTargetAPI().getHypervisorNetworkList();
    }
    
    public List<SimpleEntry<String, String>> getVirtualMachineMACs(String uuid) throws Exception {
        return this.conn.getTargetAPI().getVirtualMachineMACs(uuid);
    }
    
    public String createHypervisorNetwork(String networkName) throws Exception {
        return this.conn.getTargetAPI().createHypervisorNetwork(networkName);
    }
    
    public boolean removeHypervisorNetwork(String networkName) throws Exception {
        return this.conn.getTargetAPI().removeHypervisorNetwork(networkName);
    }
    
    /** We'll always remove things and re-add with new CDRomInfo */
    public boolean changeCDRom(String uuid, String imageFilePath) throws Exception {
        return this.conn.getTargetAPI().changeCDRom(uuid, imageFilePath);
    }
    
    public String addNicCard(String vmuuid, String adapterType, String mac, String networkName) throws Exception {
        return this.conn.getTargetAPI().addNicCard(vmuuid, adapterType, mac, networkName);
    }
    
    /** Removes all NICs related to the specified network */
    public boolean removeNicCard(String vmuuid, String networkName) throws Exception {
        return this.conn.getTargetAPI().removeNicCard(vmuuid, networkName);
    }
    
    public String createSnapshot(String vmuuid, String snapshotName, String description) throws Exception {
        return this.conn.getTargetAPI().createSnapshot(vmuuid, snapshotName, description);
    }
    
    public boolean removeSnapshot(String vmuuid, String snapshotId) throws Exception {
        return this.conn.getTargetAPI().removeSnapshot(vmuuid, snapshotId);
    }
    
    /** datastore is used as parent folder, ie. full file path is always [`datastore' + '\' +] `filename' */
    public int putFile(String targetHost, String userName, String password, String datastore, String filename,
        String filePath) throws Exception {
        return this.conn.getTargetAPI().putFile(targetHost, userName, password, datastore, filename, filePath);
    }
    
    /** datastore is used as parent folder, ie. full file path is always [`datastore' + '\' +] `filename' */
    public int deleteFile(String datastore, String filename) throws Exception {
        return this.conn.getTargetAPI().deleteFile(datastore, filename);
    }
    
    public int getVMPowerStatus(String vmuuid) throws Exception {
        /**
            Value               Meaning
            Unknown:0           The state of the VM could not be determined.
            Enabled:2           The VM is running.
            Disabled:3          The VM is turned off.
            Paused:32768        The VM is paused.
            Suspended:32769     The VM is in a saved state.
            Starting:32770      The VM is starting. This is a transitional state between 3 (Disabled) or 32769 (Suspended) and 2 (Enabled) initiated by a call to the RequestStateChange method with a RequestedState parameter of 2 (Enabled).
            Snapshotting:32771  Starting with Windows Server 2008 R2 this value is not supported. If the VM is performing a snapshot operation, the element at index 1 of the OperationalStatus property array will contain 32768 (Creating Snapshot), 32769 (Applying Snapshot), or 32770 (Deleting Snapshot).
                                Windows Server 2008:  
                                This value is supported and indicates the VM is performing a snapshot operation.
            Saving:32773        The VM is saving its state. This is a transitional state between 2 (Enabled) and 32769 (Suspended) initiated by a call to the RequestStateChange method with a RequestedState parameter of 32769 (Suspended).
            Stopping:32774      The VM is turning off. This is a transitional state between 2 (Enabled) and 3 (Disabled) initiated by a call to the RequestStateChange method with a RequestedState parameter of 3 (Disabled) or a guest operating system initiated power off.
            Pausing:32776       The VM is pausing. This is a transitional state between 2 (Enabled) and 32768 (Paused) initiated by a call to the RequestStateChange method with a RequestedState parameter of 32768 (Paused).
            Resuming:32777      The VM is resuming from a paused state. This is a transitional state between 32768 (Paused) and 2 (Enabled).
         */
        return Integer.parseInt(this.conn.getTargetAPI().getVMPowerState(vmuuid));
    }
    
    public String getVirtualMachineName(String uuid) throws Exception {
        return this.conn.getTargetAPI().getVirtualMachineName(uuid);
    }
    
    public boolean getDirectoryExists(String path) throws Exception {
        // try create the dir first, ignore any error
        WinCommand.On(this.target, this.user, this.passwd).runCommand("SETLOCAL ENABLEEXTENSIONS\nMD \"" + path + "\"\n");
        return this.conn.getTargetAPI().getDirectoryExists(path);            
    }

    public boolean setVirtualMachineProperty(String uuid, String key, String val)throws Exception{
        return this.conn.getTargetAPI().setVirtualMachineProperty(uuid, key, val);
    }
    
    public String getVirtualMachineProperty(String uuid, String prop) throws Exception{
        return this.conn.getTargetAPI().getVirtualMachineProperty(uuid, prop);
    }
}
