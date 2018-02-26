package com.ca.arcserve.linuximaging.ui.server;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import com.ca.arcserve.linuximaging.ui.client.exception.ClientException;
import com.ca.arcserve.linuximaging.ui.client.homepage.tree.D2DServerService;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.ServerInfo;

public class D2DServerServiceImpl extends BaseServiceImpl implements
		D2DServerService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5137032611598143308L;

	@Override
	public ServiceInfoModel addD2DServer(ServiceInfoModel serverInfoModel,boolean isForce) throws ClientException{
		
		try{
			ILinuximagingService client = getClient(null);
			ServerInfo server = client.addD2DServer(convertToServerInfo(serverInfoModel),isForce);
			return convertToServiceInfoModel(server);
		}catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public ServiceInfoModel modifyD2DServer(ServiceInfoModel serverInfoModel,boolean isForce) throws ClientException{
		try{
			ILinuximagingService client = getClient(null);
			ServerInfo serverInfo = client.modifyD2DServer(convertToServerInfo(serverInfoModel),isForce);
			return convertToServiceInfoModel(serverInfo);
		}catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public int deleteD2DServer(ServiceInfoModel serverInfoModel,boolean isForce) throws ClientException{
		try{
			ILinuximagingService client = getClient(null);
			return client.deleteD2DServer(serverInfoModel.getId(),isForce);
		}catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public List<ServiceInfoModel> getD2DServerList() throws ClientException{
		try{
			ILinuximagingService client = getClient(null);
			List<ServerInfo> d2dServerList = client.getD2DServerInfoList();
			List<ServiceInfoModel> serviceList = new ArrayList<ServiceInfoModel>();
			if(d2dServerList!=null){
				for(ServerInfo serverInfo : d2dServerList){
					serviceList.add(convertToServiceInfoModel(serverInfo));
				}
			}
			return serviceList;
		}catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}
	
	private ServiceInfoModel convertToServiceInfoModel(ServerInfo serverInfo){
		if(serverInfo == null)
			return null;
		ServiceInfoModel serviceInfo = new ServiceInfoModel();
		serviceInfo.setId(serverInfo.getId());
		serviceInfo.setServer(serverInfo.getName());
		serviceInfo.setProtocol(serverInfo.getProtocol());
		serviceInfo.setPort(serverInfo.getPort());
		serviceInfo.setType(serverInfo.isLocal() == true ? ServiceInfoModel.LOCAL_SERVER : ServiceInfoModel.D2D_SERVER);
		serviceInfo.setAuthKey(serverInfo.getAuthKey());
		return serviceInfo;
	}
	
	private ServerInfo convertToServerInfo(ServiceInfoModel serviceInfo){
		if(serviceInfo == null)
			return null;
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.setName(serviceInfo.getServer());
		serverInfo.setUser(serviceInfo.getUserName());
		serverInfo.setPassword(serviceInfo.getPasswd());
		serverInfo.setProtocol(serviceInfo.getProtocol());
		serverInfo.setPort(serviceInfo.getPort());
		if(serviceInfo.getId()!=null)
			serverInfo.setId(serviceInfo.getId());
		return serverInfo;
	}


}
