package com.ca.arcserve.linuximaging.ui.server.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.server.BaseServiceImpl;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.linuximaging.webservice.data.ServerInfo;
import com.ca.arcserve.linuximaging.webservice.data.restore.SshKeyInfo;

/**
 * Servlet implementation class UploadFile
 */
public class UploadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(UploadFile.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadFile() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session == null
				|| session.getAttribute(SessionConstants.SERVICE_USERNAME) == null) {
			return;
		}
		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		Writer o = response.getWriter();
		String serverName = null;
		String serviceType = null;
		String serviceName = null;
		String servicePort = null;
		String serviceProtocol = null;
		String username = null;
		String passphrase = null;
		String key = null;
		if (ServletFileUpload.isMultipartContent(request)) {
			
			request.setCharacterEncoding("utf-8");
			FileItemFactory factory = new DiskFileItemFactory();

			ServletFileUpload sfu = new ServletFileUpload(factory);
			sfu.setHeaderEncoding("utf-8");
			sfu.setFileSizeMax(204800000);
			sfu.setSizeMax(204800000);

			List<FileItem> items = null;
			try {
				items = sfu.parseRequest(request);
			} catch (SizeLimitExceededException e) {
				logger.error("[UploadFile] File size have exceed the max size: " + 20480000);
			} catch (FileUploadException e) {
				logger.error("[UploadFile] File upload failed. ", e);
			}
			Iterator<FileItem> iter = items == null ? null : items.iterator();
			while (iter != null && iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					String fieldName = item.getFieldName();
					if(fieldName != null){
						if(fieldName.equals("servername")){
							serverName = item.getString();
						}else if(fieldName.equals("servicename")){
							serviceName = item.getString();
						}else if(fieldName.equals("serviceport")){
							servicePort = item.getString();
						}else if(fieldName.equals("serviceprotocol")){
							serviceProtocol = item.getString();
						}else if(fieldName.equals("username")){
							username = item.getString();
						}else if(fieldName.equals("passphrase")){
							passphrase = item.getString();
						}else if(fieldName.equals("servicetype")){
							serviceType = item.getString();
						}
					}
				} else if (!item.isFormField()) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(item.getInputStream()));      
			        StringBuilder sb = new StringBuilder();      
			        String line = null;      
			        try {      
			           while ((line = reader.readLine()) != null) {      
			               sb.append(line + "\n");      
			           }      
			        } catch (IOException e) {      
			           e.printStackTrace();      
			        }
			        if(sb.length() == 0){
			        	o.write("1");
			        }else{
					    key = sb.toString();
			        }
				}
			}
			ServiceInfoModel serviceInfoModel = new ServiceInfoModel();
		    serviceInfoModel.setServer(serviceName);
		    serviceInfoModel.setPort(Integer.valueOf(servicePort));
		    serviceInfoModel.setProtocol(serviceProtocol);
		    serviceInfoModel.setType(serviceType);
		    ServerInfo serverInfo = new ServerInfo();
		    serverInfo.setName(serverName);
		    SshKeyInfo keyInfo = new SshKeyInfo();
		    keyInfo.setUsername(username);
		    keyInfo.setKeyPass(passphrase);
		    keyInfo.setSshKey(key);
		    
		    if(ServiceInfoModel.LOCAL_SERVER.equals(serviceInfoModel.getType())){
		    	serviceInfoModel.setServer("127.0.0.1");
			}
		    BaseServiceImpl impl = new BaseServiceImpl();
		    String wsdl = impl.getWsdlUrl(serviceProtocol, serviceInfoModel.getServer(), Integer.valueOf(servicePort));
		    BaseWebServiceClientProxy proxy = (BaseWebServiceClientProxy)session.getAttribute(wsdl);
		    int ret = ((ILinuximagingService)(proxy.getService())).uploadSshKeyForCloudServer(serverInfo, keyInfo);
		    if(ret != 0){
		    	logger.error("upload ssh key failed. "  + ret);
		    	o.write(SshKeyInfo.SSH_KEY_INVALID + "");
		    }else{
		    	o.write("0");
		    }
		} else {
			o.write(SshKeyInfo.SSH_KEY_INVALID + "");
			logger.error("[UploadFile] Form type error.");
		}
	}


}
