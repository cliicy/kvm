package com.ca.arcserve.linuximaging.ui.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcserve.linuximaging.common.properties.ResourcesReader;
import com.ca.arcserve.linuximaging.ui.client.exception.BusinessLogicException;
import com.ca.arcserve.linuximaging.ui.client.exception.ServiceConnectException;
import com.ca.arcserve.linuximaging.ui.client.exception.ServiceInternalException;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.server.servlet.ContextListener;
import com.ca.arcserve.linuximaging.util.CommonUtil;
import com.ca.arcserve.linuximaging.webservice.FlashServiceErrorCode;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class BaseServiceImpl extends RemoteServiceServlet {

	public static final String PREFIX_INTERNET_D2DSERVER="@";
	private static final long serialVersionUID = -8976248413988600354L;
	public static final Logger logger = Logger.getLogger(BaseServiceImpl.class);
	public static HashMap<String,BaseWebServiceClientProxy> proxyMap=new HashMap<String,BaseWebServiceClientProxy>();
	public BaseWebServiceClientProxy clientProxy = null;
	
	public String getWsdlUrl(String protocol,String host, int port){
		String wsdlURL = protocol + "//" + host + ":" + port+ "/WebServiceImpl/services/LinuximagingServiceImpl?wsdl";
		return wsdlURL;
	}
	
	protected BaseWebServiceClientProxy getServiceClient(String protocol,String host, int port){
		String wsdlURL=getWsdlUrl(protocol, host, port);
		HttpSession session = this.getThreadLocalRequest().getSession();
		return (BaseWebServiceClientProxy)session.getAttribute(wsdlURL);
	}
	
	protected void setServiceClient(String protocol,String host, int port, BaseWebServiceClientProxy client) {
		String wsdlURL=getWsdlUrl(protocol, host, port);
		HttpSession session = this.getThreadLocalRequest().getSession();
		session.setAttribute(wsdlURL, client);
	}
	
	protected void setServiceClient(BaseWebServiceClientProxy client) {
		//HttpSession session = this.getThreadLocalRequest().getSession();
		//session.setAttribute(SessionConstants.SERVICE_CLIENT, client);
		clientProxy = client;
	}
	
	protected BaseWebServiceClientProxy getServiceClient(){
		return clientProxy;
		//HttpSession session = this.getThreadLocalRequest().getSession();
		//return (BaseWebServiceClientProxy)session.getAttribute(SessionConstants.SERVICE_CLIENT);
	}
	
	public ILinuximagingService getClient(ServiceInfoModel serviceInfo){
		if ( serviceInfo == null ) {
			serviceInfo = new ServiceInfoModel();
			serviceInfo.setServer("127.0.0.1");
			String url = getThreadLocalRequest().getRequestURL().toString();
			if(url!=null && url.contains("https")){
				serviceInfo.setProtocol("https");
			}else{
				serviceInfo.setProtocol("http");
			}
			serviceInfo.setPort(ContextListener.webServicePort);
		}else{
			if(ServiceInfoModel.LOCAL_SERVER.equals(serviceInfo.getType())){
				serviceInfo.setServer("127.0.0.1");
			}
		}

		BaseWebServiceClientProxy proxy = getBaseWebServiceClientProxy(serviceInfo.getServer(), 
				serviceInfo.getProtocol(), serviceInfo.getPort());
		return (ILinuximagingService)(proxy.getService());
	}
	
	protected BaseWebServiceClientProxy getBaseWebServiceClientProxy(String server, String protocol, int port) {
		BaseWebServiceClientProxy proxy = this.getServiceClient(protocol, server, port);
		if(proxy==null){
			if(server.startsWith(PREFIX_INTERNET_D2DSERVER)){
				proxy=getWebServiceBehindFirewall(protocol, server, port);
			}else{
				BaseWebServiceFactory webServiceFactory = new BaseWebServiceFactory();
				proxy = webServiceFactory.getLinuxImagingWebService(protocol, server, port);
			}
			this.setServiceClient(protocol, server, port, proxy); 
		}
		return proxy;
	}
	
	//@NonSecured
	public HttpSession getSession(){
		//return  this.getThreadLocalRequest().getSession();
		return null;
	}
	
	public int initWebServiceBehindFirewall(String protocol, String server, int port) {
		String wsdlURL=getWsdlUrl(protocol, server, port);
		BaseWebServiceClientProxy proxy=proxyMap.get(wsdlURL);
		if(proxy!=null){
			return 0;
		}
		BufferedReader br=null;    
        try {
        	logger.info("cmd: pconn open "+server.substring(1,server.length())+" "+port);
            Process proc=Runtime.getRuntime().exec("pconn open "+server.substring(1,server.length())+" "+port);  
            int result=proc.waitFor();
            if(result==0){
            	 br=new BufferedReader(new InputStreamReader(proc.getInputStream()));    
                 String line=null;  
                 while((line=br.readLine())!=null){    
                     logger.info("pconn return: "+line); 
                     break;
                 } 
                 int newPort = Integer.parseInt(line);
                 BaseWebServiceFactory webServiceFactory = new BaseWebServiceFactory();
                 proxy = webServiceFactory.getLinuxImagingWebService(protocol, "127.0.0.1", newPort);
                 proxyMap.put(wsdlURL, proxy);
                 logger.info("create WebService behind firewall : "+proxy); 
            }else{
            	logger.error("pconn open failed"); 
            }
            return result;
        } catch (Exception e) {    
        	logger.error(e);
        	return -100;
        }finally{    
            if(br!=null){    
                try {    
                    br.close();    
                } catch (Exception e) {    
                }    
            }    
        } 
	}
	
	public BaseWebServiceClientProxy getWebServiceBehindFirewall(String protocol, String server, int port) {
		String wsdlURL=getWsdlUrl(protocol, server, port);
		BaseWebServiceClientProxy proxy=proxyMap.get(wsdlURL);
		if(proxy==null){
			logger.error("proxy of "+server+" is not initialized");
		}
		return proxy;
	}
	
	protected void proccessAxisFaultException(WebServiceException exception) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		proccessAxisFaultException(exception, true);
	}
	protected void proccessAxisFaultException(WebServiceException exception, boolean clearSession)throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		if (exception.getCause()!=null &&
				(
				exception.getCause() instanceof ConnectException
				|| exception.getCause() instanceof SocketException
				|| exception.getCause() instanceof UnknownHostException
				)
			)
		{
			/*HttpServletRequest request = this.getThreadLocalRequest();
			HttpSession session = request.getSession();
			if (clearSession) {
				session.invalidate();
			}
			logger.info("session.invalidate() in Thread:"
					+ Thread.currentThread().getId() + "||"
					+ Thread.currentThread().getName());*/
			throw new ServiceConnectException(FlashServiceErrorCode.Common_CantConnectService,ResourcesReader.getResource(FlashServiceErrorCode.Common_CantConnectService,CommonUtil.getLocaleStr()));
		}else if (exception.getCause()!=null &&  exception.getCause() instanceof SocketTimeoutException) {
			logger.debug("SocketTimeoutException");
			throw generateException(FlashServiceErrorCode.Common_ServiceRequestTimeout);
		}else if (exception instanceof SOAPFaultException) {
			SOAPFaultException e = (SOAPFaultException) exception;
			if(e.getFault() != null){
				String errorCode = e.getFault().getFaultCodeAsQName().getLocalPart();
				String errorMsg = e.getFault().getFaultString();
				//service session timeout
				if(String.valueOf(0x0000000100000000L + 6).equals(errorCode)){
					this.getThreadLocalRequest().getSession().invalidate();
				}
				if(errorCode != null && errorCode.equalsIgnoreCase("Client") 
					&& errorMsg != null && errorMsg.contains("StAX")){
					//server will throw this exception when request timeout					
					logger.error(exception);
					for(StackTraceElement element : exception.getStackTrace()){
						//print stack trace to know which method always time out
						logger.error("     at " + element);
					}
					throw generateException(FlashServiceErrorCode.Common_ServiceRequestTimeout);
				}
			}	
			if (e.getFault() != null) {
				logger.debug(e.getFault().getFaultCode()+" | "+e.getFault().getFaultString());
//				BusinessLogicException ex = new BusinessLogicException(e.getFault().getFaultCodeAsQName().getLocalPart(),e.getFault().getFaultString());
				
				BusinessLogicException ex = new BusinessLogicException(e.getFault().getFaultCode(),e.getFault().getFaultString());
				throw ex;
			}
		}else if(exception instanceof WebServiceException){
			if(exception.getMessage()!= null && exception.getMessage().contains("no corresponding wsdl operation")){
				logger.error("Version is not matched.");
				throw generateException(FlashServiceErrorCode.Common_Error_Version_Low);
			}
		}
	}
	
	protected BusinessLogicException generateException(String errorCode) {
		logger.debug("generateException(String) enter  errorCode:" + errorCode);
		String errorMessage = null;
		try{
			errorMessage = ResourcesReader.getResource(errorCode,CommonUtil.getLocaleStr());
		}catch(Exception e){
			logger.error(e);
		}
		BusinessLogicException ex = new BusinessLogicException(errorCode, errorMessage);
		logger.debug("generateException(String) exit BusinessLogicException:",ex);
		return ex;
	}
	
}
