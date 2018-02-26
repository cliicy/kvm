package com.arcserve.kvm;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openwsman.Client;
import org.openwsman.ClientOptions;
import org.openwsman.OpenWSManConstants;
import org.openwsman.XmlDoc;

import com.arcserve.winrm.Classes;
import com.arcserve.winrm.Classes.ClassInstance;
import com.arcserve.winrm.ClassesV1;
import com.arcserve.winrm.ClassesV2;
import com.arcserve.winrm.JDomUtils;


public class Connection {
    private static final Logger logger = Logger.getLogger(Connection.class);
    
    private static final String d2dServerUrlProp = "com.arcserve.hyperv.d2dserverurl";
    
    private Classes     classes;
    private Client      targetServer;
    private APIContract targetAPI;
    private String      rawVer;
    private boolean     supportsGeneration2 = false;
    
    public void close() { 
        this.targetServer.delete();
        this.targetServer = null;
        this.targetAPI = null;
        this.classes = null;
        this.rawVer = null;
        this.supportsGeneration2 = false;
    }
    
    public APIContract getTargetAPI() {
        return this.targetAPI;
    }

    public Boolean supportsGeneration2VM() {
        return this.supportsGeneration2;
    }
    
    public Connection(String host, String user, String passwd, int port, Boolean isHttp) throws Exception {
        String targetUrl = "";
        String[] d2dServerInfo = getD2DServerInfo();
        
        if(d2dServerInfo == null) {        
            /** cannot determine delegate information */
            targetUrl = String.format("%s://%s:%d/wsman", isHttp ? "http" : "https", host, port);
            logger.info("Using direct connection mode cause no delegate server info is found");
        } else {
            String d2dProtocol = d2dServerInfo[0];
            String d2dHost = d2dServerInfo[1]; 
            int d2dsvrPort = Integer.parseInt(d2dServerInfo[2]);
            /** 
             * targetInfo should finally be converted into a <encrypted string>
             * before encryotion the string should be like
             * <protocol>/<target>/<port>/<prefix>/<username>/<password>
             * the <username> field mush NOT contain forward slash(/), 
             * all forward slash should be replaced with backward slash(\)
             */
            String targetInfo = String.format("%s/%s/%s/%s/%s/%s", isHttp ? "http" : "https", host, port, "wsman", user.replaceAll("/", "\\"), passwd);
            targetInfo = CommonUtil.encryptUsingD2DCmdUtil(targetInfo);
            
            // servlet: https://luyu-dev:8014/WebServiceImpl/WinRMDelegate
            targetUrl = String.format("%s://%s:%d/WebServiceImpl/WinRMDelegate/%s", d2dProtocol, d2dHost, d2dsvrPort, targetInfo);
            logger.info("Using delegate server for winrm requests");
        }
        /** System.out.println("targetServerection url: " + url); */
        this.targetServer = new Client(targetUrl);
        this.targetServer.transport().set_agent("Arcserve Instant VM Client for HyperV");
        this.targetServer.transport().set_auth_method(OpenWSManConstants.BASIC_AUTH_STR);
        this.targetServer.transport().set_username(user);
        this.targetServer.transport().set_password(passwd);
        this.targetServer.transport().set_verify_host(0);
        this.targetServer.transport().set_verify_peer(0);
        
        this.identify();
    }
    
    private Boolean identify() throws Exception {
        XmlDoc res = this.targetServer.identify(new ClientOptions());
        
        if (res == null)
            throw new Exception("Invalid host or credential");
        else if (res.isFault())
            throw new Exception("Faile to targetServerect to target server: " + res.fault().toString());
        else {
            Element response = JDomUtils.buildElementFromString(res.encode("UTF-8"));
            JDomUtils.removeNamespaces(response);
            
            /** System.out.println("identify: " + JDomUtils.getPrettyXml(response)); */
            
            this.rawVer = response.getChild("Body").getChild("IdentifyResponse").getChildText("ProductVersion");
            String[] items = this.rawVer.replaceAll("^ *OS: *", "").replaceAll(" *SP *", " ").replaceAll(" *Stack: *",
                " ").split(" ");
            String[] ver = items[0].split("\\.");

            if (ver[0].equals("6")) {
                /** Windows 2012 support both v1 and v2 api. 
                 *  We're using the v1 here assuming that 
                 *  v2 is newly introduced and v1 is more trust worth */
                if (Integer.parseInt(ver[1]) <= 2) {
                    /** This is probably 2008 series and 2012 */
                    this.classes = new ClassesV1();
                    this.targetAPI = APIv1.createAgainstTarget(this.classes, targetServer, this.getServerName(), this.getServerSystemRoot());
                } else {
                    this.supportsGeneration2 = true;
                    /** This is probably 2012 R2 and later */
                    this.classes = new ClassesV2();
                    this.targetAPI = APIv2.createAgainstTarget(this.classes, targetServer, this.getServerName(), this.getServerSystemRoot());  
                }
                return true;
            } else if(ver[0].equals("10")) {
                this.supportsGeneration2 = true;
                /** This is probably Windows 10 series */
                /** WARNING: EXPERIMENTAL*/
                this.classes = new ClassesV2();
                this.targetAPI = APIv2.createAgainstTarget(this.classes, targetServer, this.getServerName(), this.getServerSystemRoot());  
                return true;
            } else throw new Exception("Unsupported target OS version:\n" + this.rawVer + "\n");
        }
    }
    
    private String getServerName() throws Exception {
        for (ClassInstance inst : this.classes.createReference("Win32_OperatingSystem").enumerateInstances(this.targetServer))
            return inst.getProperty("CSName").getSimpleValue();
        throw new Exception("Cannot determin server name!");
    }
    
    private String getServerSystemRoot() throws Exception {
        for (ClassInstance inst : this.classes.createReference("Win32_OperatingSystem").enumerateInstances(this.targetServer))
            return inst.getProperty("WindowsDirectory").getSimpleValue();
        throw new Exception("Cannot determin system root!");
    }
    
    /**
     * returns null if no d2d server infor is found, meaning we should not use the new proxy servlet
     * */
    private String[] getD2DServerInfo() {
        String env = System.getProperty(d2dServerUrlProp);
        if(env == null || env.isEmpty()) {
            logger.info(String.format("Not running with usable D2D Server, please use %s prop to specify d2d server. Falling back to non proxy mode.", d2dServerUrlProp));
            return null;
        }  
        
        String[] result = new String[3];        
        try {
            // should be like <proto>://<server addr>:<server port>/xxxxxxxx
            if(env.toLowerCase().startsWith("https"))
                result[0] = "https";
            else if(env.toLowerCase().startsWith("http"))
                result[0] = "http";
            else return result = null;            
            env = env.substring(result[0].length()).trim();
            
            if(!env.startsWith("://"))
                return result = null;            
            env = env.substring("://".length());
            
            String[] remaining = env.split(":", 2);
            if(remaining.length != 2)
                return result = null;            
            result[1] = remaining[0];
            
            env = remaining[1];
            if(env.indexOf('/') >= 0)
                env = env.substring(0, env.indexOf('/'));            
            if(env.isEmpty()) 
                return result = null;  
            for(int i = 0; i < env.length(); i ++)
                if(!Character.isDigit(env.charAt(i)))
                    return result = null;
            result[2] = env;
            
            return result;
        } finally {
            if(result == null) 
                logger.error(String.format("Failed to extract D2D Server info from prop %s(%s, process stopped at: %s)", d2dServerUrlProp, System.getProperty(d2dServerUrlProp), env));
            else
                logger.debug(String.format("D2D Server info proto:[%s] addr:[%s] port:[%s]", result[0], result[1], result[2]));
        }
    }
}
