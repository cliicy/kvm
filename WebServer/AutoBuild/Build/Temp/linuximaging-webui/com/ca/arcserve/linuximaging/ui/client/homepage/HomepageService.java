package com.ca.arcserve.linuximaging.ui.client.homepage;

import java.util.Date;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.exception.ClientException;
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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("homepage")
public interface HomepageService extends RemoteService {
	
	UIContextModel initUIContext();
	int initWebServiceBehindFirewall(ServiceInfoModel serviceInfo);
	
	//node
	int addNode(ServiceInfoModel serviceInfo, NodeModel model) throws ClientException;
	String[] deleteNodeList(ServiceInfoModel serviceInfo, List<NodeModel> nodeList, Boolean force, Boolean firstCall) throws ClientException;
	int modifyNodeList(ServiceInfoModel serviceInfo, List<NodeModel> nodeList) throws ClientException;
	List<NodeModel> getNodeList(ServiceInfoModel serviceInfo) throws ClientException;
	PagingLoadResult<NodeModel> getNodeList(ServiceInfoModel serviceInfo,PagingLoadConfig config, NodeFilterModel filter) throws ClientException;
	List<NodeModel> getNodeProtectedState(ServiceInfoModel serviceInfo, List<NodeModel> nodeList) throws ClientException;
	NodeConnectionModel connectNode(ServiceInfoModel serviceInfo, String server, String user, String passwd, boolean forceManage) throws ClientException;
	
	//job
	long runJob(ServiceInfoModel serviceInfo, JobStatusModel model , Boolean isRunSameName) throws ClientException;
	int deleteJob(ServiceInfoModel serviceInfo, JobStatusModel model, Boolean skipRemoveDriver, Boolean isDeleteSameName) throws ClientException;
	int cancelJob(ServiceInfoModel serviceInfo, JobStatusModel model, Boolean isCancelSameName) throws ClientException;	
	int startMigrateData(ServiceInfoModel serviceInfo, String sourceHost,JobStatusModel model) throws ClientException;	
	int holdJobSchedule(ServiceInfoModel serviceInfo,JobStatusModel model, boolean ready) throws ClientException;
	
	//ServerInfoModel getServerInfo(ServiceInfoModel serviceInfo, String server);
	long captureServer(ServiceInfoModel serviceInfo, ServiceInfoModel server, String jobName, String templateName, String excludeFS);
	List<String> getTemplateList(ServiceInfoModel serviceInfo) throws ClientException;
	//JobStatusModel getJobStatus(ServiceInfoModel serviceInfo, Long jobID);
	//homepage
	List<JobStatusModel> getJobStatusList(ServiceInfoModel serviceInfo) throws ClientException;
	PagingLoadResult<JobStatusModel> getJobStatusList(ServiceInfoModel serviceInfo,PagingLoadConfig config, JobStatusFilterModel filter) throws ClientException;
	PagingLoadResult<JobStatusModel> getJobHistoryList(ServiceInfoModel serviceInfo, PagingLoadConfig config, JobFilterModel filter) throws ClientException;
	int deleteJobHistory(ServiceInfoModel serviceInfo, List<JobStatusModel> models) throws ClientException;
	PagingLoadResult<ActivityLogModel> getLogList(ServiceInfoModel serviceInfo, PagingLoadConfig config, LogFilterModel filter) throws ClientException;
	
	//backup wizard
	BackupModel getBackupJobScriptByUUID(ServiceInfoModel serviceInfo, String uuid) throws ClientException;
	BackupModel getBackupJobScriptByJobName(ServiceInfoModel serviceInfo, String jobName) throws ClientException;
	BackupModel getBackupJobScriptByNodeName(ServiceInfoModel serviceInfo, String nodeName) throws ClientException;
//	int runBackupJob(ServiceInfoModel serviceInfo, List<NodeModel> nodeList, JobStatusModel jobStatus, boolean runNow);
	int submitBackupJob(ServiceInfoModel serviceInfo, BackupModel model) throws ClientException;
	List<JobScriptModel> getBackupJobScriptList(ServiceInfoModel serviceInfo) throws ClientException;
	List<JobStatusModel> getRestoreJobScriptList(ServiceInfoModel serviceInfo,ServerInfoModel serverInfo,String uuid) throws ClientException;
	
	//restore wizard
	List<BackupMachineModel> getMachineList(ServiceInfoModel serviceInfo, BackupLocationInfoModel backupLocation,Boolean includeHbbu) throws ClientException;
	List<RecoveryPointModel> getRecoveryPointList(ServiceInfoModel serviceInfo, BackupLocationInfoModel backupLocation, String machine,Date startDate,Date endDate) throws ClientException;
	List<RecoveryPointItemModel> getDiskInfo(ServiceInfoModel serviceInfo, BackupLocationInfoModel backupLocation, String machine,RecoveryPointModel recoveryPointModel) throws ClientException;
	NodeModel getTargetMachineInfo(ServiceInfoModel serviceInfo, String name) throws ClientException;
	int submitRestoreJob(ServiceInfoModel serviceInfo, RestoreModel model) throws ClientException;
	RestoreModel getRestoreJobScriptByUUID(ServiceInfoModel serviceInfo,String uuid) throws ClientException;
	RestoreModel getRestoreModelByUUID(ServiceInfoModel serviceInfo,ServerInfoModel serverInfo,String jobUUID,String sourceJobUUID) throws ClientException;
	RestoreModel getRestoreJobScriptByNodeName(ServiceInfoModel serviceInfo,String nodeName) throws ClientException;
	List<String> getSupportedFSType(ServiceInfoModel serviceInfo) throws ClientException;
	
	PagingLoadResult<GridTreeNode> getPagingGridTreeNode(ServiceInfoModel serviceInfo, BackupLocationInfoModel sessionLocation, String machine, RecoveryPointModel point, GridTreeNode parent, PagingLoadConfig pageCfg,String scriptUUID) throws ClientException;
	List<GridTreeNode> getTreeGridChildren(ServiceInfoModel serviceInfo, BackupLocationInfoModel sessionLocation, String machine, RecoveryPointModel point, GridTreeNode loadConfig,String scriptUUID) throws ClientException;
	D2DTimeModel getCurrentD2DTimeFromServer(ServiceInfoModel serviceInfo) throws ClientException;
	List<FileModel> getFileItems(ServiceInfoModel serviceInfo, String inputFolder, String host, String user, String password, boolean bIncludeFiles,String scriptUUID) throws ClientException;
	void createFolder(ServiceInfoModel serviceInfo, String parentPath, String subDir, String host, String user, String password,String scriptUUID) throws ClientException;
	void removeMountPoint(ServiceInfoModel serviceInfo, String machine, RecoveryPointModel point) throws ClientException;
	List<DatastoreModel> getDataStoreList(ServiceInfoModel serviceInfo,VirtualizationServerModel serverModel) throws ClientException;
	BrowseSearchResultModel getFileFolderBySearch(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel sessionLocation, String machine, RecoveryPointModel point,
			GridTreeNode loadConfig, SearchConditionModel searchCondition) throws ClientException;
	int startSearch(ServiceInfoModel serviceInfo, BackupLocationInfoModel sessionLocation,
			String machine, RecoveryPointModel point, GridTreeNode loadConfig,
			SearchConditionModel searchCondition,String scriptUUID) throws ClientException;
	int stopSearch(ServiceInfoModel serviceInfo, BackupLocationInfoModel sessionLocation,
			String machine, RecoveryPointModel point, GridTreeNode loadConfig,
			SearchConditionModel searchCondition) throws ClientException;
	List<String> getScripts(ServiceInfoModel serviceInfo,int type) throws ClientException;
	boolean checkRecoveryPointPasswd(ServiceInfoModel serviceInfo,BackupLocationInfoModel sessionLocation,String machine,RecoveryPointModel point,String scriptUUID) throws ClientException;
	boolean validateBackupLocation(ServiceInfoModel serviceInfo,BackupLocationInfoModel backupLocationInfoModel) throws ClientException;
	int addNodeByDiscovery(ServiceInfoModel serviceInfo,NodeDiscoverySettingsModel nodeDiscoverySettingsModel) throws ClientException;
	boolean validateRpsServer(ServerInfoModel serverInfoModel) throws ClientException;
	NodeDiscoverySettingsModel getNodeDiscoverySettingsModel(ServiceInfoModel serviceInfo) throws ClientException;
    DashboardModel getDashboardInformation(ServiceInfoModel serviceInfo) throws ClientException;
    JobSummaryModel getJobSummaryInformation(ServiceInfoModel serviceInfo,JobSummaryFilterModel filter) throws ClientException;
    int addNodeIntoJobScript(ServiceInfoModel serviceInfo, List<NodeModel> nodeList, JobScriptModel job) throws ClientException;
    ServerCapabilityModel getServerCapability(ServiceInfoModel serviceInfo) throws ClientException;
	VersionInfoModel getVersionInfo(ServiceInfoModel serviceInfo);
	String getLicenseText();
	int submitStandbyJob(ServiceInfoModel serviceInfo, StandbyModel model) throws ClientException;
	StandbyModel getStandbyModel(ServiceInfoModel serviceInfo,String jobUUID,StandbyType standbyType) throws ClientException;
	List<RpsDataStoreModel> findRpsDataStoreByServerInfo(ServiceInfoModel serviceInfo,ServerInfoModel rpsDataStoreModel) throws ClientException;
	ServerInfoModel getServerInfoFromSession() throws ClientException;
	ShareFolderModel getSharePathInfoFromSession() throws ClientException;
	RecoveryPointModel getRecoveryPointFromSession (ServiceInfoModel serviceInfo,BackupLocationInfoModel backupLocation, RecoveryPointModel recoveryPointModel, String machine) throws ClientException;
	boolean iscompletedMountRP(ServiceInfoModel serviceInfo,BackupLocationInfoModel sessionLocation, String machine,RecoveryPointModel pointModel,String scriptUUID) throws ClientException;
	List<String> getLocalIpAndMac() throws ClientException;
	int controlAutoRestoreData(ServiceInfoModel serviceInfo, JobStatusModel model,int type) throws ClientException;
	boolean validateSharePointUserName(ServiceInfoModel serviceInfo,String userName, String uuid) throws ClientException;
	int querySshKeyInfoForCloudServer(ServiceInfoModel serviceInfo,ServerInfoModel serverInfo,String uuid) throws ClientException;
}
