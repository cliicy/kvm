package com.ca.arcserve.linuximaging.ui.client.homepage.tree;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface D2DServerServiceAsync {

	void addD2DServer(ServiceInfoModel serverInfoModel, boolean isForce,
			AsyncCallback<ServiceInfoModel> callback);

	void modifyD2DServer(ServiceInfoModel serverInfoModel,boolean isForce,
			AsyncCallback<ServiceInfoModel> callback);

	void deleteD2DServer(ServiceInfoModel serverInfoModel,boolean isForce,
			AsyncCallback<Integer> callback);

	void getD2DServerList(AsyncCallback<List<ServiceInfoModel>> callback);

}
