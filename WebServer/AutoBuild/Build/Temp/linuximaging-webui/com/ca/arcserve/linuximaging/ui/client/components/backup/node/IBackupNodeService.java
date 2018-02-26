package com.ca.arcserve.linuximaging.ui.client.components.backup.node;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus.model.DataModalJobStatus;
import com.ca.arcserve.linuximaging.ui.client.components.backup.node.model.ServerInfoModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("backup")
public interface IBackupNodeService extends RemoteService {
	int AddNewServer(ServerInfoModel model);
	List<ServerInfoModel> GetServerList();
	int RemoveServer(String serverName);
	ServerInfoModel GetServerInfo(String server);
	long captureServer(ServerInfoModel server, String jobName, String templateName, String excludeFS);
	List<String> GetTemplateList();
	DataModalJobStatus GetJobStatus(Long jobID);
	List<DataModalJobStatus> GetJobStatusList(String serverName);

}
