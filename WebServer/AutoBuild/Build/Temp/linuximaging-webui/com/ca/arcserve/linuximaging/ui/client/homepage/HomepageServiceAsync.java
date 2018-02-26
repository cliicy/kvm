package com.ca.arcserve.linuximaging.ui.client.homepage;

import java.util.Date;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.model.ActivityLogModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupMachineModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupModel;
import com.ca.arcserve.linuximaging.ui.client.model.BrowseSearchResultModel;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.ca.arcserve.linuximaging.ui.client.model.DatastoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.FileModel;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.ca.arcserve.linuximaging.ui.client.model.JobFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobScriptModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.LogFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeConnectionModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeDiscoverySettingsModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointItemModel;
import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RpsDataStoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.SearchConditionModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerCapabilityModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ShareFolderModel;
import com.ca.arcserve.linuximaging.ui.client.model.StandbyModel;
import com.ca.arcserve.linuximaging.ui.client.model.StandbyType;
import com.ca.arcserve.linuximaging.ui.client.model.UIContextModel;
import com.ca.arcserve.linuximaging.ui.client.model.VersionInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.VirtualizationServerModel;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.DashboardModel;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.JobSummaryFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.JobSummaryModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface HomepageServiceAsync {
	
	void initUIContext(AsyncCallback<UIContextModel> callback);
	void initWebServiceBehindFirewall(ServiceInfoModel serviceInfo,AsyncCallback<Integer> callback);
	
	//node
	void addNode(ServiceInfoModel serviceInfo, NodeModel model, AsyncCallback<Integer> callback);
	void deleteNodeList(ServiceInfoModel serviceInfo, List<NodeModel> nodeList, Boolean force, Boolean firstCall, AsyncCallback<String[]> callback);
	void modifyNodeList(ServiceInfoModel serviceInfo, List<NodeModel> nodeList, AsyncCallback<Integer> callback);
	void getNodeList(ServiceInfoModel serviceInfo,AsyncCallback<List<NodeModel>> callback);
	void getNodeList(ServiceInfoModel serviceInfo,PagingLoadConfig config, NodeFilterModel filter, AsyncCallback<PagingLoadResult<NodeModel>> callback);
	void getNodeProtectedState(ServiceInfoModel serviceInfo, List<NodeModel> nodeList, AsyncCallback<List<NodeModel>> callback);
	void connectNode(ServiceInfoModel serviceInfo, String server, String user, String passwd, boolean forceManage, AsyncCallback<NodeConnectionModel> callback);
	
	//job 
	void runJob(ServiceInfoModel serviceInfo, JobStatusModel model, Boolean isRunSameName, AsyncCallback<Long> callback);
	void cancelJob(ServiceInfoModel serviceInfo, JobStatusModel model, Boolean isCancelSameName, AsyncCallback<Integer> asyncCallback);
	void deleteJob(ServiceInfoModel serviceInfo, JobStatusModel model, Boolean skipRemoveDriver, Boolean isDeleteSameName,AsyncCallback<Integer> callback);
	void holdJobSchedule(ServiceInfoModel serviceInfo, JobStatusModel model,boolean ready, AsyncCallback<Integer> callback);
	
	//void getServerInfo(ServiceInfoModel serviceInfo, String server, AsyncCallback<ServerInfoModel> callback);	
	void captureServer(ServiceInfoModel serviceInfo, ServiceInfoModel server, String jobName, String templateName, String excludeFS, AsyncCallback<Long> callback);
	void getTemplateList(ServiceInfoModel serviceInfo, AsyncCallback<List<String>> callback);
	//void getJobStatus(ServiceInfoModel serviceInfo,Long jobID, AsyncCallback<JobStatusModel> callback);
	//homepage
	void getJobStatusList(ServiceInfoModel serviceInfo,AsyncCallback<List<JobStatusModel>> callback);
	void getJobStatusList(ServiceInfoModel serviceInfo, PagingLoadConfig config, JobStatusFilterModel filter, AsyncCallback<PagingLoadResult<JobStatusModel>> callback);	
	void getJobHistoryList(ServiceInfoModel serviceInfo,PagingLoadConfig config, JobFilterModel filter, AsyncCallback<PagingLoadResult<JobStatusModel>> callback);
	void deleteJobHistory(ServiceInfoModel serviceInfo, List<JobStatusModel> models, AsyncCallback<Integer> callback);
	void getLogList(ServiceInfoModel serviceInfo, PagingLoadConfig config, LogFilterModel filter, AsyncCallback<PagingLoadResult<ActivityLogModel>> callback);
	
	//backup wizard
	void getBackupJobScriptByUUID(ServiceInfoModel serviceInfo, String uuid, AsyncCallback<BackupModel> callback);
//	void runBackupJob(ServiceInfoModel serviceInfo, List<NodeModel> nodeList, JobStatusModel jobStatus, boolean runNow, AsyncCallback<Integer> callback);
	void submitBackupJob(ServiceInfoModel serviceInfo, BackupModel model, AsyncCallback<Integer> callback);
	void getBackupJobScriptList(ServiceInfoModel serviceInfo, AsyncCallback<List<JobScriptModel>> callback);	
	//restore wizard
	void getMachineList(ServiceInfoModel serviceInfo, BackupLocationInfoModel backupLocation,Boolean includeHbbu,AsyncCallback<List<BackupMachineModel>> callback);
	void getRecoveryPointList(ServiceInfoModel serviceInfo,BackupLocationInfoModel backupLocation, String machine,Date startDate,Date endDate, AsyncCallback<List<RecoveryPointModel>> callback);
	void getTargetMachineInfo(ServiceInfoModel serviceInfo, String name, AsyncCallback<NodeModel> callback);
	void submitRestoreJob(ServiceInfoModel serviceInfo, RestoreModel model, AsyncCallback<Integer> callback);
	void getRestoreJobScriptByUUID(ServiceInfoModel serviceInfo, String uuid, AsyncCallback<RestoreModel> callback);
	void getSupportedFSType(ServiceInfoModel serviceInfo, AsyncCallback<List<String>> callback);

	void getPagingGridTreeNode(ServiceInfoModel serviceInfo, BackupLocationInfoModel sessionLocation, String machine, RecoveryPointModel point, GridTreeNode parent, PagingLoadConfig pageCfg,String scriptUUID, AsyncCallback<PagingLoadResult<GridTreeNode>> callback);
	void getTreeGridChildren(ServiceInfoModel serviceInfo, BackupLocationInfoModel sessionLocation, String machine, RecoveryPointModel point, GridTreeNode loadConfig,String scriptUUID, AsyncCallback<List<GridTreeNode>> callback);
	void getFileFolderBySearch(ServiceInfoModel serviceInfo, BackupLocationInfoModel sessionLocation, String machine, RecoveryPointModel point, GridTreeNode loadConfig,SearchConditionModel searchCondition, AsyncCallback<BrowseSearchResultModel> callback);
	void startSearch(ServiceInfoModel serviceInfo, BackupLocationInfoModel sessionLocation, String machine, RecoveryPointModel point, GridTreeNode loadConfig,SearchConditionModel searchCondition, String scriptUUID,AsyncCallback<Integer> callback);
	void stopSearch(ServiceInfoModel serviceInfo, BackupLocationInfoModel sessionLocation, String machine, RecoveryPointModel point, GridTreeNode loadConfig,SearchConditionModel searchCondition, AsyncCallback<Integer> callback);
	void getCurrentD2DTimeFromServer(ServiceInfoModel serviceInfo, AsyncCallback<D2DTimeModel> callback);	
	
	void getFileItems(ServiceInfoModel serviceInfo, String inputFolder, String host, String user, String password, boolean bIncludeFiles,String scriptUUID, AsyncCallback<List<FileModel>> callback);
	void createFolder(ServiceInfoModel serviceInfo, String parentPath, String subDir, String host, String user, String password, String scriptUUID,AsyncCallback<Void> calback);
	void removeMountPoint(ServiceInfoModel serviceInfo, String machine, RecoveryPointModel point, AsyncCallback<Void> callback);
	void getDataStoreList(ServiceInfoModel serviceInfo,VirtualizationServerModel serverModel,
			AsyncCallback<List<DatastoreModel>> callback);
	void getScripts(ServiceInfoModel serviceInfo,int type,
			AsyncCallback<List<String>> callback);
	void checkRecoveryPointPasswd(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel sessionLocation, String machine, RecoveryPointModel point,String scriptUUID,
			AsyncCallback<Boolean> callback);
	void validateBackupLocation(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel backupLocationInfoModel,
			AsyncCallback<Boolean> callback);
	void addNodeByDiscovery(ServiceInfoModel serviceInfo,
			NodeDiscoverySettingsModel nodeDiscoveryModel,
			AsyncCallback<Integer> callback);
	void getNodeDiscoverySettingsModel(ServiceInfoModel serviceInfo,
			AsyncCallback<NodeDiscoverySettingsModel> callback);
	void getDashboardInformation(ServiceInfoModel serviceInfo, AsyncCallback<DashboardModel> callback);
	void getJobSummaryInformation(ServiceInfoModel serviceInfo, JobSummaryFilterModel filter, AsyncCallback<JobSummaryModel> callback);
	void addNodeIntoJobScript(ServiceInfoModel serviceInfo, List<NodeModel> nodeList, JobScriptModel job, AsyncCallback<Integer> callback);
	void getServerCapability(ServiceInfoModel serviceInfo,  AsyncCallback<ServerCapabilityModel> callback);
	void getDiskInfo(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel backupLocation, String machine,
			RecoveryPointModel recoveryPointModel,
			AsyncCallback<List<RecoveryPointItemModel>> callback);
	void getVersionInfo(ServiceInfoModel serviceInfo,AsyncCallback<VersionInfoModel> asyncCallback);
	void getBackupJobScriptByJobName(ServiceInfoModel serviceInfo,
			String jobName, AsyncCallback<BackupModel> callback);
	void getBackupJobScriptByNodeName(ServiceInfoModel serviceInfo,
			String nodeName, AsyncCallback<BackupModel> callback);
	void getLicenseText(AsyncCallback<String> callback);
	void submitStandbyJob(ServiceInfoModel serviceInfo,
			StandbyModel model, AsyncCallback<Integer> callback);
	void getStandbyModel(ServiceInfoModel serviceInfo,String jobUUID,StandbyType standbyType, AsyncCallback<StandbyModel> callback);
	void findRpsDataStoreByServerInfo(ServiceInfoModel serviceInfo,ServerInfoModel rpsDataStoreModel,
			AsyncCallback<List<RpsDataStoreModel>> callback);
	void startMigrateData(ServiceInfoModel serviceInfo, String sourceHost,JobStatusModel model,
			AsyncCallback<Integer> callback);
	void getRestoreJobScriptByNodeName(ServiceInfoModel serviceInfo,
			String nodeName, AsyncCallback<RestoreModel> callback);
	void getServerInfoFromSession(AsyncCallback<ServerInfoModel> callback);
	void getSharePathInfoFromSession(AsyncCallback<ShareFolderModel> callback);
	void validateRpsServer(ServerInfoModel serverInfoModel,
			AsyncCallback<Boolean> callback);
	void getRestoreJobScriptList(ServiceInfoModel serviceInfo,ServerInfoModel serverInfo,String uuid,
			AsyncCallback<List<JobStatusModel>> callback);
	void getRestoreModelByUUID(ServiceInfoModel serviceInfo,
			ServerInfoModel serverInfo, String jobUUID, String sourceJobUUID,
			AsyncCallback<RestoreModel> callback);
	void getRecoveryPointFromSession(ServiceInfoModel serviceInfo,BackupLocationInfoModel backupLocation,
			RecoveryPointModel recoveryPointModel, String machine,
			AsyncCallback<RecoveryPointModel> callback);
	void iscompletedMountRP(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel sessionLocation, String machine,
			RecoveryPointModel pointModel,String scriptUUID, AsyncCallback<Boolean> callback);
	void getLocalIpAndMac(AsyncCallback<List<String>> callback);
	void controlAutoRestoreData(ServiceInfoModel serviceInfo,
			JobStatusModel model, int type, AsyncCallback<Integer> callback);
	void validateSharePointUserName(ServiceInfoModel serviceInfo,String userName,String uuid,
			AsyncCallback<Boolean> callback);
	void querySshKeyInfoForCloudServer(ServiceInfoModel serviceInfo,
			ServerInfoModel serverInfo, String uuid,
			AsyncCallback<Integer> callback);
}
