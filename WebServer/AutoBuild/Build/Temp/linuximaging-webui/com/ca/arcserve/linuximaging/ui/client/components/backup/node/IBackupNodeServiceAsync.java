package com.ca.arcserve.linuximaging.ui.client.components.backup.node;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus.model.DataModalJobStatus;
import com.ca.arcserve.linuximaging.ui.client.components.backup.node.model.ServerInfoModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IBackupNodeServiceAsync {
	void AddNewServer(ServerInfoModel model, AsyncCallback<Integer> callback);
	void GetServerList(AsyncCallback<List<ServerInfoModel>> callback);
	void RemoveServer(String serverName, AsyncCallback<Integer> callback);
	void GetServerInfo(String server, AsyncCallback<ServerInfoModel> callback);	
	void captureServer(ServerInfoModel server, String jobName, String templateName, String excludeFS, AsyncCallback<Long> callback);
	void GetTemplateList(AsyncCallback<List<String>> callback);
	void GetJobStatus(Long jobID,AsyncCallback<DataModalJobStatus> callback);
	void GetJobStatusList(String serverName,AsyncCallback<List<DataModalJobStatus>> callback);
}
