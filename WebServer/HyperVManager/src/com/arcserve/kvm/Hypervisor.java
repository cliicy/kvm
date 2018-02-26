package com.arcserve.kvm;

import java.io.File;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Hypervisor {
    private static final Logger logger = Logger.getLogger(Hypervisor.class);
    
    private Connection conn = null;
    private String target = null;
    private String user = null;
    private String passwd = null;
    public KVMInfo kvmmac=null;
   
    
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
    public String createVM(String format,String vmStoragePath, String vmName, String bootOption, Integer cpuCount,
        Long memorySize, List<SimpleEntry<String, String>> nicToAddByMacAndHostNetworkName, String cdPath, List<SimpleEntry<String, Long>> disks)
        throws Exception {
        return createVM(format,1, vmStoragePath, vmName, bootOption, cpuCount, memorySize, nicToAddByMacAndHostNetworkName, cdPath, disks);
    }
    public String createVM(String format,int generation, String vmStoragePath, String vmName, String bootOption, Integer cpuCount,
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
    
    public KVMInfo getkvmXMLinfos(String strkvmXmlFilePath){
	       	return null;
	    }
    }
 /**
    	kvmmac=new KVMInfo();
		File file = new File(strkvmXmlFilePath);
		if (!file.exists())
		{
			System.out.println("module xml file doesn't exist");
			return null;
		}
		try 
		{
		    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		    Document doc = docBuilder.parse (new File(strkvmXmlFilePath));

		    // normalize text representation
		    doc.getDocumentElement().normalize();
		    //System.out.println ("Root element of the Status.xml is " + doc.getDocumentElement().getNodeName());
		    NodeList listsVMInfo = null;
		    int iPnode = 0 , itotalPkgs = 0;
		    String sDomainFlag;
		    //get the total number of Packagexx. like Package0 Package1 Package2
		    do {
		    	sDomainFlag = "domain"+ Integer.toString(iPnode);;
		    	listsVMInfo = doc.getElementsByTagName(sDomainFlag);
		    	itotalPkgs = listsVMInfo.getLength();
		    	if ( itotalPkgs == 0 ) break;
		    	iPnode++;
		    } while (true);
		    //get the total number of Packagexx. like Package0 Package1 Package2
		    kvmmac.arykvmInfo = new KVMInfo[iPnode] ;
		    iPnode = 0;
		    
		    do {
		    	sDomainFlag = "Package" + Integer.toString(iPnode);
		    	listOfPkgs = doc.getElementsByTagName(sPackageFlag);
		    	itotalPkgs = listOfPkgs.getLength();    		
		    	if ( itotalPkgs == 0 ) break;   	
		    	
				for (int i = 0; i < itotalPkgs ; i++) {
					if ( i >= 1 ) break;//make sure the Package name is not the same, is different. 
					Node firstPatchNode = listOfPkgs.item(i);
					if (firstPatchNode.getNodeType() == Node.ELEMENT_NODE) {
						i = iPnode;
						biPInfo.aryPatchInfo[i] = new PatchInfo();
						// ------- get update ID name
						Element firstElement = (Element) firstPatchNode;
						String sPID = firstElement.getAttribute("Id");
						biPInfo.aryPatchInfo[i].setPackageID(sPID);
	
						// ------- get update PublishedDate
						String sPdate = firstElement.getAttribute("PublishedDate");
						biPInfo.aryPatchInfo[i].setPublishedDate(sPdate.toString());
	
						// System.out.println("Id : " + sPID);
						// System.out.println("PublishedDate : " + sPdate);
	
						// ------- get update patch name
						NodeList firstNameList = firstElement.getElementsByTagName("Update");
						Element firstNameElement = (Element) firstNameList.item(0);
						NodeList textFNList = firstNameElement.getChildNodes();
						String pckname = ((Node) textFNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setPackageUpdateName(pckname);
						// System.out.println("Update Patch Name : " + pckname);
	
						// ------- get Dependency update patch name
						NodeList dyNameList = firstElement.getElementsByTagName("Dependency");
						Element dyNameElement = (Element) dyNameList.item(0);
						NodeList dyNList = dyNameElement.getChildNodes();
						String dyname = ((Node) dyNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setPackageDepy(dyname);
						
						// ------- get Size
						NodeList secNameList = firstElement.getElementsByTagName("Size");
						Element secNameElement = (Element) secNameList.item(0);
						NodeList textSNList = secNameElement.getChildNodes();
						String ssize = ((Node) textSNList.item(0)).getNodeValue().trim();
						int nsize = Integer.parseInt(ssize.toString());
						biPInfo.aryPatchInfo[i].setSize(nsize);
						// System.out.println("Size of Patch : " + ssize);
	
						// ------- get Checksum
						NodeList chsNameList = firstElement
								.getElementsByTagName("Checksum");
						Element chsNameElement = (Element) chsNameList.item(0);
						NodeList chsNList = chsNameElement.getChildNodes();
						String schs = ((Node) chsNList.item(0)).getNodeValue().trim().toString();		
						// System.out.println("Checksum of Patch : " + schs);
	
						// ------- get Downloadedlocation
						NodeList dlocNameList = firstElement.getElementsByTagName("Downloadedlocation");
						Element dlocNameElement = (Element) dlocNameList.item(0);
	
						NodeList dlocNList = dlocNameElement.getChildNodes();
						String sloc = ((Node) dlocNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setPatchDownloadLocation(sloc.toString());
						System.out.println("Downloadedlocation of Patch : " +sloc);
	
						// ------- get DownloadStatus
						NodeList dlsNameList = firstElement.getElementsByTagName("DownloadStatus");
						Element dlsNameElement = (Element) dlsNameList.item(0);
	
						NodeList dlsNList = dlsNameElement.getChildNodes();
						String sls = ((Node) dlsNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setDownloadStatus(Integer.parseInt(sls.toString()));
						biPInfo.setDownloadStatus(Integer.parseInt(sls.toString()));
	
						if (biPInfo.aryPatchInfo[i].getDownloadStatus() == 1) {
							File downloadFile = new File(sloc.toString());
							if (!downloadFile.exists()) {
								biPInfo.aryPatchInfo[i].setDownloadStatus(0);
								biPInfo.setDownloadStatus(0);
							} else {
								biPInfo.aryPatchInfo[i].setDownloadStatus(1);
								biPInfo.setDownloadStatus(1);
							}
						}
						// System.out.println("DownloadStatus of Patch : " + sls);
	
						// ------- get AvailableStatus
						NodeList avsNameList = firstElement
								.getElementsByTagName("AvailableStatus");
						Element avsNameElement = (Element) avsNameList.item(0);
	
						NodeList avsNList = avsNameElement.getChildNodes();
						String savs = ((Node) avsNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setAvailableStatus(Integer.parseInt(savs.toString()));
						biPInfo.setAvailableStatus(Integer.parseInt(savs.toString()));
						// System.out.println("AvailableStatus of Patch : " + savs);
	
						// ------- get UpdateBuild
						NodeList upbNameList = firstElement.getElementsByTagName("UpdateBuild");
						Element upbNameElement = (Element) upbNameList.item(0);
	
						NodeList upbNList = upbNameElement.getChildNodes();
						String supb = ((Node) upbNList.item(0)).getNodeValue().trim();
						String[] supbx = supb.toString().split("\\.");
						biPInfo.aryPatchInfo[i].setBuildNumber(Integer.parseInt(supbx[0]));
						biPInfo.setBuildNumber(Integer.parseInt(supbx[0]));
						// System.out.println("UpdateBuild of Patch : " + supb);
	
						// ------- get UpdateVersionNumber
						NodeList upvNameList = firstElement
								.getElementsByTagName("UpdateVersionNumber");
						Element upvNameElement = (Element) upvNameList.item(0);
	
						NodeList upvNList = upvNameElement.getChildNodes();
						String supv = ((Node) upvNList.item(0)).getNodeValue()
								.trim();
						biPInfo.aryPatchInfo[i].setPatchVersionNumber(Integer
								.parseInt(supv.toString()));
						// System.out.println("UpdateVersionNumber of Patch : " +
						// supv);
	
						// ------- get RebootRequired
						NodeList rbNameList = firstElement
								.getElementsByTagName("RebootRequired");
						Element rbNameElement = (Element) rbNameList.item(0);
	
						NodeList rbNList = rbNameElement.getChildNodes();
						String srb = ((Node) rbNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setRebootRequired(Integer
								.parseInt(srb.toString()));
						// System.out.println("RebootRequired of Patch : " + srb);
	
						// ------- get LastRebootableUpdateVersion
						NodeList lrbNameList = firstElement
								.getElementsByTagName("LastRebootableUpdateVersion");
						Element lrbNameElement = (Element) lrbNameList.item(0);
	
						NodeList lrbNList = lrbNameElement.getChildNodes();
						// System.out.println("LastRebootableUpdateVersion of Patch : "
						// + ((Node)lrbNList.item(0)).getNodeValue().trim());
	
						// ------- get RequiredVersionOfAutoUpdate
						NodeList vaupNameList = firstElement
								.getElementsByTagName("RequiredVersionOfAutoUpdate");
						Element vaupNameElement = (Element) vaupNameList.item(0);
	
						NodeList vaupNList = vaupNameElement.getChildNodes();
						// System.out.println("RequiredVersionOfAutoUpdate of Patch : "
						// + ((Node)vaupNList.item(0)).getNodeValue().trim());
	
						// ------- get InstallStatus
						NodeList itsNameList = firstElement
								.getElementsByTagName("InstallStatus");
						Element itsNameElement = (Element) itsNameList.item(0);
	
						NodeList itsNList = itsNameElement.getChildNodes();
						String sits = ((Node) itsNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setInstallStatus(Integer.parseInt(sits.toString()));
						biPInfo.setInstallStatus(Integer.parseInt(sits.toString()));
						// System.out.println("InstallStatus of Patch : " + sits);
	
						// ------- get Desc
						String sdec = "Desc" + getSuffix4Language();
						NodeList desNameList = firstElement.getElementsByTagName(sdec);
						Element desNameElement = (Element) desNameList.item(0);
	
						NodeList desNList = desNameElement.getChildNodes();
						String sdes = ((Node) desNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setDescription(sdes.toString());
						// System.out.println("Desc of Patch : " + sdes);
						//
						// ------- get UpdateURL
						String surl = "UpdateURL" + getSuffix4Language();
						NodeList urlNameList = firstElement.getElementsByTagName(surl);
						Element urlNameElement = (Element) urlNameList.item(0);
	
						NodeList urlNList = urlNameElement.getChildNodes();
						String surlV = ((Node) urlNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setPatchURL(surlV.toString());
						// System.out.println("UpdateURL of Patch : " + surlV);
	
						// Error Message
						NodeList errorMNameList = firstElement.getElementsByTagName("ErrorMessage");
						biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_SUCCESS);
						if (errorMNameList.item(0) != null) {
							Element errorMElement = (Element) errorMNameList.item(0);
	
							NodeList errorMNList = errorMElement.getChildNodes();
							String serrorV = ((Node) errorMNList.item(0)).getNodeValue().trim();
							biPInfo.aryPatchInfo[i].setErrorMessage(serrorV.toString());
							biPInfo.setErrorMessage(serrorV.toString());
	
							if (serrorV.toString().length() > 0) {
								biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
							} else {
								biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_SUCCESS);
							}
						}
					}
				}//end of for loop with s var
				iPnode++;
		    }while ( true );
		    return biPInfo;
		} catch (Exception err) {
			err.printStackTrace ();
			biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
		}
		biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
    }
    **/
 
