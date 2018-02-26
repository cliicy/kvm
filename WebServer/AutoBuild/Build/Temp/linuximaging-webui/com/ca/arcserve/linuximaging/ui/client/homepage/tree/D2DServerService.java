package com.ca.arcserve.linuximaging.ui.client.homepage.tree;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.exception.ClientException;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("d2dserver")
public interface D2DServerService extends RemoteService {
	
	ServiceInfoModel addD2DServer(ServiceInfoModel serverInfoModel,boolean isForce) throws ClientException;
	
	ServiceInfoModel modifyD2DServer(ServiceInfoModel serverInfoModel,boolean isForce) throws ClientException;
	
	int deleteD2DServer(ServiceInfoModel serverInfoModel,boolean isForce) throws ClientException;
	
	List<ServiceInfoModel> getD2DServerList() throws ClientException;
	
}
