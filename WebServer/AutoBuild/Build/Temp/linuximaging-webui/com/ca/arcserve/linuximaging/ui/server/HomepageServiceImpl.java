package com.ca.arcserve.linuximaging.ui.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;

import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.BackupTemplateModel;
import com.ca.arcserve.linuximaging.ui.client.exception.ClientException;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.model.ActivityLogModel;
import com.ca.arcserve.linuximaging.ui.client.model.AssuredRecoveryTestResultModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupMachineModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupScheduleModel;
import com.ca.arcserve.linuximaging.ui.client.model.BrowseSearchResultModel;
import com.ca.arcserve.linuximaging.ui.client.model.CatalogModelType;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.ca.arcserve.linuximaging.ui.client.model.DailyScheduleModel;
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
import com.ca.arcserve.linuximaging.ui.client.model.PhysicalStandbyMachineModel;
import com.ca.arcserve.linuximaging.ui.client.model.PhysicalStandbyModel;
import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointItemModel;
import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreTargetModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.RetentionModel;
import com.ca.arcserve.linuximaging.ui.client.model.RpsDataStoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.SearchConditionModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerCapabilityModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ShareFolderModel;
import com.ca.arcserve.linuximaging.ui.client.model.StandbyModel;
import com.ca.arcserve.linuximaging.ui.client.model.StandbyType;
import com.ca.arcserve.linuximaging.ui.client.model.UIContextModel;
import com.ca.arcserve.linuximaging.ui.client.model.VMRestoreTargetModel;
import com.ca.arcserve.linuximaging.ui.client.model.VersionInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.VirtualMachineInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.VirtualizationServerModel;
import com.ca.arcserve.linuximaging.ui.client.model.VolumeInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.WeeklyScheduleModel;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.D2DServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.DashboardModel;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.JobSummaryFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.JobSummaryModel;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.NodeSummaryModel;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.ResourceUsageModel;
import com.ca.arcserve.linuximaging.ui.server.servlet.SessionConstants;
import com.ca.arcserve.linuximaging.util.CommonUtil;
import com.ca.arcserve.linuximaging.util.StringUtil;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;
import com.ca.arcserve.linuximaging.webservice.data.BackupScript;
import com.ca.arcserve.linuximaging.webservice.data.D2DTime;
import com.ca.arcserve.linuximaging.webservice.data.JobScript;
import com.ca.arcserve.linuximaging.webservice.data.JobStatus;
import com.ca.arcserve.linuximaging.webservice.data.NodeConnectionInfo;
import com.ca.arcserve.linuximaging.webservice.data.NodeDiscoverySettings;
import com.ca.arcserve.linuximaging.webservice.data.PagingConfig;
import com.ca.arcserve.linuximaging.webservice.data.PagingResult;
import com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint;
import com.ca.arcserve.linuximaging.webservice.data.RecoveryPointItem;
import com.ca.arcserve.linuximaging.webservice.data.RestoreScript;
import com.ca.arcserve.linuximaging.webservice.data.Retention;
import com.ca.arcserve.linuximaging.webservice.data.ServerCapability;
import com.ca.arcserve.linuximaging.webservice.data.ServerInfo;
import com.ca.arcserve.linuximaging.webservice.data.ServiceInfo;
import com.ca.arcserve.linuximaging.webservice.data.TargetMachineInfo;
import com.ca.arcserve.linuximaging.webservice.data.VersionInfo;
import com.ca.arcserve.linuximaging.webservice.data.VolumeInfo;
import com.ca.arcserve.linuximaging.webservice.data.backup.BackupConfiguration;
import com.ca.arcserve.linuximaging.webservice.data.backup.BackupSchedule;
import com.ca.arcserve.linuximaging.webservice.data.backup.BackupTarget;
import com.ca.arcserve.linuximaging.webservice.data.backup.DailySchedule;
import com.ca.arcserve.linuximaging.webservice.data.backup.DataStoreInfo;
import com.ca.arcserve.linuximaging.webservice.data.backup.WeeklySchedule;
import com.ca.arcserve.linuximaging.webservice.data.browse.CatalogItem;
import com.ca.arcserve.linuximaging.webservice.data.browse.File;
import com.ca.arcserve.linuximaging.webservice.data.browse.FileFolderItem;
import com.ca.arcserve.linuximaging.webservice.data.browse.Folder;
import com.ca.arcserve.linuximaging.webservice.data.browse.PagedCatalogItem;
import com.ca.arcserve.linuximaging.webservice.data.browse.SearchCondition;
import com.ca.arcserve.linuximaging.webservice.data.browse.SearchResult;
import com.ca.arcserve.linuximaging.webservice.data.dashboard.D2DServerInfo;
import com.ca.arcserve.linuximaging.webservice.data.dashboard.Dashboard;
import com.ca.arcserve.linuximaging.webservice.data.dashboard.JobSummary;
import com.ca.arcserve.linuximaging.webservice.data.dashboard.TargetSummary;
import com.ca.arcserve.linuximaging.webservice.data.dashboard.ResourceUsage;
import com.ca.arcserve.linuximaging.webservice.data.filter.JobStatusFilter;
import com.ca.arcserve.linuximaging.webservice.data.filter.JobSummaryFilter;
import com.ca.arcserve.linuximaging.webservice.data.filter.TargetMachineFilter;
import com.ca.arcserve.linuximaging.webservice.data.jobhistory.JobFilter;
import com.ca.arcserve.linuximaging.webservice.data.jobhistory.JobPagingConfig;
import com.ca.arcserve.linuximaging.webservice.data.jobhistory.JobPagingResult;
import com.ca.arcserve.linuximaging.webservice.data.log.ActivityLog;
import com.ca.arcserve.linuximaging.webservice.data.log.LogFilter;
import com.ca.arcserve.linuximaging.webservice.data.log.LogPagingConfig;
import com.ca.arcserve.linuximaging.webservice.data.log.LogPagingResult;
import com.ca.arcserve.linuximaging.webservice.data.log.Severity;
import com.ca.arcserve.linuximaging.webservice.data.restore.BackupMachine;
import com.ca.arcserve.linuximaging.webservice.data.restore.DataStore;
import com.ca.arcserve.linuximaging.webservice.data.restore.RestoreConfiguration;
import com.ca.arcserve.linuximaging.webservice.data.restore.RestoreTarget;
import com.ca.arcserve.linuximaging.webservice.data.restore.VirtualMachineRestoreTarget;
import com.ca.arcserve.linuximaging.webservice.data.restore.VirtualMachineSetting;
import com.ca.arcserve.linuximaging.webservice.data.restore.VirtualNetworkInfo;
import com.ca.arcserve.linuximaging.webservice.data.restore.VirtualizationServer;
import com.ca.arcserve.linuximaging.webservice.data.standby.PhysicalStandByJobScript;
import com.ca.arcserve.linuximaging.webservice.data.standby.PhysicalStandbyMachine;
import com.ca.arcserve.linuximaging.webservice.data.standby.StandByJobScript;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class HomepageServiceImpl extends BaseServiceImpl implements
		HomepageService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 545970491741502493L;

	@Override
	public VersionInfoModel getVersionInfo(ServiceInfoModel serviceInfo) {
		VersionInfoModel model = null;
		try {
			VersionInfo versionInfo = getClient(serviceInfo).getVersionInfo();
			model = convertToVersionInfoModel(versionInfo);
			return model;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
		}
		return null;
	}

	@Override
	public UIContextModel initUIContext() {
		UIContextModel model = new UIContextModel();
		model.setFileSeparator(java.io.File.separator);
		model.setHelpLink(getHelpLink());
		model.setVersion("CA ARCserve<small><sup>&reg;</sup></small> D2D for Linux r16.5");

		try {
			VersionInfo versionInfo = getClient(null).getVersionInfo();
			model.setVersionInfoModel(convertToVersionInfoModel(versionInfo));
		} catch (Exception ex) {
			logger.error("Failed to get version info");
		}

		return model;
	}

	private VersionInfoModel convertToVersionInfoModel(VersionInfo versionInfo) {
		if (versionInfo == null)
			return null;
		VersionInfoModel model = new VersionInfoModel();
		model.setLiveCD(versionInfo.isLiveCD());
		model.setSupportWakeOnLan(versionInfo.isSupportWakeOnLan());
		model.setLicensed(versionInfo.isLicensed());
		model.setVersion(versionInfo.getVersion());
		model.setBuildNumber(versionInfo.getBuildNumber());
		model.setTimeZoneOffset(versionInfo.getTimeZoneOffset());
		model.setManagedServer(convertToServerInfoModel(versionInfo
				.getManagedServer()));
		model.setEnableNonRootUser(versionInfo.isEnableNonRootUser());
		model.setLiveCDIsoExist(versionInfo.isLiveCDIsoExist());
		model.setShowDefaultUserWhenLogin(versionInfo
				.isShowDefaultUserWhenLogin());
		model.setDefaultUser(versionInfo.getDefaultUser());
		model.setUiLogoutTime(versionInfo.getUiLogoutTime());
		model.setEnableExcludeFile(versionInfo.isEnableExcludeFile());
		String enableVM = System.getenv("ENABLE_RESTORE_VM");
		if (enableVM != null && Integer.parseInt(enableVM) != 0) {
			model.setEnableVM(true);
		} else {
			model.setEnableVM(false);
		}
		model.setServerCapabilityModel(convertServerCapability(versionInfo
				.getServerCapability()));
		return model;
	}

	private ServerInfo convertToServerInfo(ServerInfoModel serverInfoModel) {
		if (serverInfoModel == null) {
			return null;
		}
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.setUuid(serverInfoModel.getRestoreJobId());
		serverInfo.setName(serverInfoModel.getServerName());
		serverInfo.setUser(serverInfoModel.getUserName());
		serverInfo.setPassword(serverInfoModel.getPasswd());
		serverInfo.setProtocol(serverInfoModel.getProtocol());
		serverInfo.setPort(serverInfoModel.getPort());
		serverInfo.setLocal(serverInfoModel.getIsLocal() == null ? false
				: serverInfoModel.getIsLocal());
		return serverInfo;
	}

	private ServerInfoModel convertToServerInfoModel(ServerInfo serverInfo) {
		if (serverInfo == null)
			return null;
		ServerInfoModel serverModel = new ServerInfoModel();
		serverModel.setServerName(serverInfo.getName());
		serverModel.setUserName(serverInfo.getUser());
		serverModel.setPasswd(serverInfo.getPassword());
		serverModel.setProtocol(serverInfo.getProtocol());
		serverModel.setPort(serverInfo.getPort());
		serverModel.setServerType(serverInfo.getServerType());
		serverModel.setLocal(serverInfo.isLocal());
		return serverModel;
	}

	private String getHelpLink() {
		String helpLink = "http://www.arcservedocs.com/arcserveudp/6.5/agentlinux/redirect.php?";
		/*
		 * String linkFile = ContextListener.PATH_CONFIGURATION +
		 * java.io.File.separator + "Links.properties"; Properties properties =
		 * new Properties(); FileInputStream fis = null; try {
		 * logger.debug("load link file: "+linkFile); fis = new
		 * FileInputStream(linkFile); properties.load(fis); helpLink =
		 * properties.getProperty("helpLink"); } catch (IOException e) {
		 * logger.error(e); } finally { try { if(fis != null) fis.close();
		 * }catch(Throwable t) {} }
		 */
		String locale = CommonUtil.getLocaleStr();
		helpLink = helpLink + "lang=" + locale + "&item=";
		logger.debug("helpLink: " + helpLink);
		return helpLink;
	}

	@Override
	public long captureServer(ServiceInfoModel serviceInfo,
			ServiceInfoModel server, String jobName, String templateName,
			String excludeFS) {
		if (server == null) {
			return -1;
		}

		// long result = getClient(serviceInfo.getServer(),
		// serviceInfo.getProtocol(),
		// serviceInfo.getPort()).captureServerByTemplate(jobName, templateName,
		// server.getServerName(),
		// server.getUserName(), server.getPasswd(), excludeFS);
		return 0;

	}

	@Override
	public List<String> getTemplateList(ServiceInfoModel serviceInfo)
			throws ClientException {
		// BaseWebServiceClientProxy clientProxy = this.getServiceClient();
		// return
		// ((ILinuximagingService)(clientProxy.getService())).getTemplateNameList();
		return null;
	}

	@Override
	public List<JobStatusModel> getJobStatusList(ServiceInfoModel serviceInfo)
			throws ClientException {
		try {
			List<JobStatusModel> list = new ArrayList<JobStatusModel>();
			JobStatus[] statusArray = getClient(serviceInfo).getJobStatusList();
			if (statusArray != null) {
				for (JobStatus status : statusArray) {
					if (status != null) {
						JobStatusModel model = convertJobStatus(status);
						list.add(model);
					}
				}
			}
			return list;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public PagingLoadResult<JobStatusModel> getJobStatusList(
			ServiceInfoModel serviceInfo, PagingLoadConfig configModel,
			JobStatusFilterModel filter) throws ClientException {
		try {
			PagingConfig config = new PagingConfig();
			config.setStartIndex(configModel.getOffset());
			config.setCount(configModel.getLimit());
			PagingResult<JobStatus> result = getClient(serviceInfo)
					.getJobStatusPagingList(config,
							convertJobStatusFilterModel(filter));
			List<JobStatusModel> modelList = new ArrayList<JobStatusModel>();
			if (result != null && result.getData() != null
					&& result.getData().size() > 0) {
				for (JobStatus status : result.getData()) {
					JobStatusModel model = convertJobStatus(status);
					if (model != null) {
						modelList.add(model);
					}
				}
				return new BasePagingLoadResult<JobStatusModel>(modelList,
						result.getStartIndex(), result.getTotalCount());
			}

			return new BasePagingLoadResult<JobStatusModel>(modelList, 0, 0);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private JobStatusFilter convertJobStatusFilterModel(
			JobStatusFilterModel model) {
		if (model == null) {
			return null;
		} else {
			JobStatusFilter filter = new JobStatusFilter();
			filter.setJobName(model.getJobName());
			filter.setJobType(model.getJobType());
			filter.setTargetName(model.getNodeName());
			filter.setStatus(model.getStatus());
			filter.setLastResult(model.getLastResult());
			filter.setBackupLocation(model.getBackupLocation());
			return filter;
		}
	}

	private JobFilter convertJobFilterModel(JobFilterModel model) {
		if (model == null)
			return null;

		JobFilter filter = new JobFilter();
		if (model.getJobID() != null) {
			filter.setJobId(model.getJobID());
		}
		filter.setJobName(model.getJobName());
		if (model.getJobResult() != null) {
			filter.setJobResult(model.getJobResult());
		}

		if (model.getType() != null) {
			filter.setJobType(model.getType());
		}

		filter.setNodeName(model.getNodeName());
		filter.setSessionLocation(model.getSessionLoc());
		filter.setStart(model.getStartTime());
		filter.setEnd(model.getEndTime());
		return filter;
	}

	@Override
	public PagingLoadResult<JobStatusModel> getJobHistoryList(
			ServiceInfoModel serviceInfo, PagingLoadConfig config,
			JobFilterModel filter) throws ClientException {
		try {
			JobPagingConfig jobCfg = new JobPagingConfig();
			jobCfg.setStartIndex(config.getOffset());
			jobCfg.setCount(config.getLimit());
			JobFilter jobFilter = convertJobFilterModel(filter);
			JobPagingResult result = getClient(serviceInfo).getJobHistoryList(
					jobCfg, jobFilter);

			List<JobStatusModel> list = new ArrayList<JobStatusModel>();
			if (result != null && result.getData() != null
					&& result.getData().size() > 0) {
				for (JobStatus status : result.getData()) {
					JobStatusModel model = convertJobStatus(status);
					list.add(model);
				}

				return new BasePagingLoadResult<JobStatusModel>(list,
						result.getStartIndex(), result.getTotalCount());
			} else if (result != null) {
				return new BasePagingLoadResult<JobStatusModel>(list,
						result.getStartIndex(), result.getTotalCount());
			}
			return new BasePagingLoadResult<JobStatusModel>(list, 0, 0);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public int addNode(ServiceInfoModel serviceInfo, NodeModel model)
			throws ClientException {
		try {
			TargetMachineInfo target = convertNodeModel(model);
			return getClient(serviceInfo).addTargetMachine(target);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public List<NodeModel> getNodeList(ServiceInfoModel serviceInfo)
			throws ClientException {
		try {
			List<NodeModel> list = new ArrayList<NodeModel>();
			List<TargetMachineInfo> servers = getClient(serviceInfo)
					.getTargetMachineList();
			for (TargetMachineInfo target : servers) {
				NodeModel model = convertTargetMachine(target);
				list.add(model);
			}
			return list;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public PagingLoadResult<NodeModel> getNodeList(
			ServiceInfoModel serviceInfo, PagingLoadConfig configModel,
			NodeFilterModel filter) throws ClientException {
		try {
			PagingConfig config = new PagingConfig();
			config.setStartIndex(configModel.getOffset());
			config.setCount(configModel.getLimit());
			PagingResult<TargetMachineInfo> result = getClient(serviceInfo)
					.getTargetMachinePagingList(config,
							convertNodeFilterModel(filter));
			List<NodeModel> modelList = new ArrayList<NodeModel>();
			if (result != null && result.getData() != null
					&& result.getData().size() > 0) {
				for (TargetMachineInfo target : result.getData()) {
					NodeModel model = convertTargetMachine(target);
					if (model != null) {
						modelList.add(model);
					}
				}
				return new BasePagingLoadResult<NodeModel>(modelList,
						result.getStartIndex(), result.getTotalCount());
			}

			return new BasePagingLoadResult<NodeModel>(modelList, 0, 0);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private TargetMachineFilter convertNodeFilterModel(NodeFilterModel model) {
		if (model == null) {
			return null;
		} else {
			TargetMachineFilter filter = new TargetMachineFilter();
			filter.setTargetName(model.getNodeName());
			// filter.setIsProtected(model.getIsProtected());
			filter.setOperatingSystem(model.getOperatingSystem());
			filter.setLastResult(model.getLastResult());
			return filter;
		}
	}

	private NodeModel convertTargetMachine(TargetMachineInfo target) {
		if (target == null)
			return null;
		NodeModel model = new NodeModel();
		model.setUUID(target.getUUID());
		model.setServerName(target.getName());
		model.setUserName(target.getUser());
		model.setPasswd(target.getPassword());
		model.setProtected(target.isProtected());
		model.setJobName(target.getJobName());
		model.setOperatingSystem(target.getOperatingSystem());
		model.setDescription(target.getDescription());
		model.setLastResult(target.getLastResult());
		model.setRecoveryPointCount(target.getRecoveryPointCount());
		model.setRecoverySetCount(target.getRecoverySetCount());
		model.setBackupLocationType(target.getBackupLocationType());
		model.setExclude(true);
		return model;
	}

	public BackupTarget convertNodeModel(NodeModel node) {
		if (node == null)
			return null;
		BackupTarget target = new BackupTarget();
		target.setUUID(node.getUUID());
		target.setName(node.getServerName());
		target.setUser(node.getUserName());
		target.setPassword(node.getPasswd());
		target.setDescription(node.getDescription());
		target.setProtected(node.getProtected());
		target.setJobName(node.getJobName());
		target.setOperatingSystem(node.getOperatingSystem());
		if (node.isExclude() == null) {
			target.setExclude(true);
		} else {
			target.setExclude(node.isExclude());
		}
		if (node.excludeVolumes != null && node.excludeVolumes.size() > 0) {
			List<VolumeInfo> volumes = new ArrayList<VolumeInfo>();
			for (int i = 0; i < node.excludeVolumes.size(); ++i) {
				volumes.add(convertVolumeInfoModel(node.excludeVolumes.get(i)));
			}
			target.setExcludeVolumes(volumes);
		}
		if (node.getPriority() != null)
			target.setPriority(node.getPriority());
		return target;
	}

	public VolumeInfo convertVolumeInfoModel(VolumeInfoModel volume) {
		VolumeInfo volumeInfo = new VolumeInfo();
		if (volume != null) {
			volumeInfo.setFileSystem(volume.getFileSystem());
			volumeInfo.setMountOn(volume.getMountPoint());
			volumeInfo.setType(volume.getType());
		}
		return volumeInfo;
	}

	public static ServiceInfoModel convertServerInfo(ServiceInfo server) {
		if (server == null)
			return null;

		ServiceInfoModel model = new ServiceInfoModel();
		model.setServer(server.getServer());
		model.setProtocol(server.getProtocol());
		model.setPort(server.getPort());
		if (server.isLocal()) {
			model.setType(ServiceInfoModel.LOCAL_SERVER);
		} else {
			model.setType(ServiceInfoModel.D2D_SERVER);
		}
		return model;
	}

	@Override
	public int submitBackupJob(ServiceInfoModel serviceInfo, BackupModel model)
			throws ClientException {
		if (model.getTargetList() == null || model.getTargetList().size() == 0) {
			// no server need to do backup
			return 0;
		}
		BackupScript script = convertBackupModel(model);
		ILinuximagingService service = getClient(serviceInfo);
		try {
			int result = service.submitBackupJob(script);
			return result;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	private BackupScript convertBackupModel(BackupModel model) {
		BackupScript script = new BackupScript();
		script.setUuid(model.getUuid());
		script.setTemplateID(model.getTemplateID());
		script.setJobName(model.getJobName());
		List<BackupTarget> settingList = new ArrayList<BackupTarget>();
		for (NodeModel server : model.getTargetList()) {
			BackupTarget serverInfo = convertNodeModel(server);
			if (serverInfo != null) {
				settingList.add(serverInfo);
			}
		}
		script.setSettings(settingList);
		script.setExclude(model.isExclude());
		script.setExcludeVolumes(model.getExcludeVolumes());
		script.setExcludeFiles(model.getExcludeFiles());
		// script.setTemplateName(model.getDestInfo().getName());
		script.setBackupLocationInfo(convertToBackupLocationInfo(model
				.getDestInfo().backupLocationInfoModel));
		script.setCompressLevel(model.getDestInfo().getCompression());
		String encName = model.getDestInfo().getEncryptionName();
		if (encName != null && encName.indexOf('-') > 0) {
			encName = encName.replace("-", "");
		}
		script.setEncryptAlgoName(encName);
		if (model.getDestInfo().getEncryptionType() != null)
			script.setEncryptAlgoType(model.getDestInfo().getEncryptionType());
		// result.setEncryptAlgoName(model.getEncryptionName());
		script.setEncryptPasswd(model.getDestInfo().getEncryptionPassword());
		script.setThrottle(model.getThrottle());
		// script.setFullSchedule(convertBackupScheduleModel(model.fullSchedule));
		// script.setIncrementalSchedule(convertBackupScheduleModel(model.incrementalSchedule));
		// script.setResyncSchedule(convertBackupScheduleModel(model.resyncSchedule));
		script.setScheduleType(model.getScheduleType());
		int scheduleType = model.getScheduleType();
		if (scheduleType == BackupModel.SCHEDULE_TYPE_ONCE) {
			script.setDisable(false);
			script.setStartTime(convertD2DTimeModel(model.startTime));
		} else if (scheduleType == BackupModel.SCHEDULE_TYPE_REPEAT_DAILY) {
			script.setDisable(false);
			DailySchedule dailySchedule = new DailySchedule();
			dailySchedule.setFullSchedule(convertBackupScheduleModel(
					model.dailySchedule.fullSchedule, scheduleType));
			dailySchedule.setIncrementalSchedule(convertBackupScheduleModel(
					model.dailySchedule.incrementalSchedule, scheduleType));
			dailySchedule.setResyncSchedule(convertBackupScheduleModel(
					model.dailySchedule.resyncSchedule, scheduleType));
			dailySchedule
					.setStartTime(convertD2DTimeModel(model.dailySchedule.startTime));
			script.setDailySchedule(dailySchedule);
		} else if (scheduleType == BackupModel.SCHEDULE_TYPE_REPEAT_WEEKLY) {
			script.setDisable(false);
			WeeklySchedule weeklySchedule = new WeeklySchedule();
			List<BackupSchedule> scheduleList = new ArrayList<BackupSchedule>();
			for (BackupScheduleModel schedule : model.weeklySchedule.scheduleList) {
				scheduleList.add(convertBackupScheduleModel(schedule,
						scheduleType));
			}
			weeklySchedule.setScheduleList(scheduleList);
			weeklySchedule
					.setStartTime(convertD2DTimeModel(model.weeklySchedule.startTime));
			script.setWeeklySchedule(weeklySchedule);
		} else {
			script.setDisable(true);
		}
		script.setRetention(convertToRetention(model.retentionModel));
		if (!StringUtil.isEmptyOrNull(model.getServerScriptBeforeJob())) {
			script.setServerScriptBeforeJob(model.getServerScriptBeforeJob());
		}
		if (!StringUtil.isEmptyOrNull(model.getServerScriptAfterJob())) {
			script.setServerScriptAfterJob(model.getServerScriptAfterJob());
		}
		if (!StringUtil.isEmptyOrNull(model.getTargetScriptBeforeJob())) {
			script.setTargetScriptBeforeJob(model.getTargetScriptBeforeJob());
		}
		if (!StringUtil.isEmptyOrNull(model.getTargetScriptAfterJob())) {
			script.setTargetScriptAfterJob(model.getTargetScriptAfterJob());
		}
		if (!StringUtil.isEmptyOrNull(model.getTargetScriptBeforeSnapshot())) {
			script.setTargetScriptBeforeSnapshot(model
					.getTargetScriptBeforeSnapshot());
		}
		if (!StringUtil.isEmptyOrNull(model.getTargetScriptAfterSnapshot())) {
			script.setTargetScriptAfterSnapshot(model
					.getTargetScriptAfterSnapshot());
		}
		return script;
	}

	private Retention convertToRetention(RetentionModel model) {
		Retention retention = new Retention();
		retention.setBackupSetCount(model.getBackupSetCount());
		retention.setUseWeekly(model.isUseWeekly());
		retention.setDayOfMonth(model.getDayOfMonth());
		retention.setDayOfWeek(model.getDayOfWeek());
		return retention;
	}

	private RetentionModel convertToRetentionModel(Retention retention) {
		if (retention == null) {
			return null;
		}
		RetentionModel model = new RetentionModel();
		model.setBackupSetCount(retention.getBackupSetCount());
		model.setUseWeekly(retention.isUseWeekly());
		model.setDayOfWeek(retention.getDayOfWeek());
		model.setDayOfMonth(retention.getDayOfMonth());
		return model;
	}

	private D2DTime convertD2DTimeModel(D2DTimeModel model) {
		D2DTime time = new D2DTime();
		if (model == null) {
			return null;
		} else if (model.isRunNow()) {
			time.setRunNow(true);
			time.setReady(model.isReady());
		} else {
			time.setYear(model.getYear());
			time.setMonth(model.getMonth());
			time.setDay(model.getDay());
			time.setHour(model.getHour());
			time.setMinute(model.getMinute());
			time.setAmPM(model.getAMPM());
			time.setHourOfday(model.getHourOfDay());
			time.setReady(model.isReady());
		}
		return time;
	}

	private BackupSchedule convertBackupScheduleModel(
			BackupScheduleModel model, int scheduleType) {
		if (model == null) {
			return null;
		}
		BackupSchedule schedule = new BackupSchedule();

		schedule.setEnabled(model.isEnabled());
		if (model.isEnabled()) {
			schedule.setInterval(model.getInterval());
			schedule.setIntervalUnit(model.getIntervalUnit());
		}
		if (scheduleType == BackupModel.SCHEDULE_TYPE_REPEAT_WEEKLY) {
			schedule.setDay(model.getDay());
			schedule.setStartTime(convertD2DTimeModel(model.startTime));
			schedule.setJobType(model.getMethod());
			schedule.setEndTime(convertD2DTimeModel(model.endTime));
		}

		return schedule;
	}

	@Override
	public long runJob(ServiceInfoModel serviceInfo, JobStatusModel model,
			Boolean isRunSameName) throws ClientException {
		logger.debug("rerun job - start");
		try {
			ILinuximagingService client = getClient(serviceInfo);
			if (client != null) {
				JobStatus status = convertJobStatusModel(model);
				if (isRunSameName) {
					return getClient(serviceInfo).runJobByName(
							status.getJobName(), status.getJobType());
				}
				return getClient(serviceInfo).runJob(status.getUuid(),
						status.getJobType());
			}

		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public int cancelJob(ServiceInfoModel serviceInfo, JobStatusModel model,
			Boolean isCancelSameName) throws ClientException {
		try {
			if (isCancelSameName) {
				return getClient(serviceInfo).cancelJobByJobName(
						model.getJobName());
			} else {
				return getClient(serviceInfo).cancelJob(model.getJobUuid());
			}
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public int deleteJob(ServiceInfoModel serviceInfo, JobStatusModel model,
			Boolean skipRemoveDriver, Boolean isDeleteSameName)
			throws ClientException {
		try {
			// JobStatus status=convertJobStatusModel(model);
			if (isDeleteSameName) {
				return getClient(serviceInfo).deleteJobByJobName(
						model.getJobName());
			} else {
				return getClient(serviceInfo).deleteJob(model.getJobUuid(),
						skipRemoveDriver);
			}
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public int deleteJobHistory(ServiceInfoModel serviceInfo,
			List<JobStatusModel> models) throws ClientException {
		try {
			if (models == null || models.size() == 0) {
				return 0;
			}
			long[] jobIDs = new long[models.size()];
			for (int i = 0; i < jobIDs.length; i++) {
				jobIDs[i] = models.get(i).getJobID();
			}
			return getClient(serviceInfo).deleteJobHistory(jobIDs);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	private JobStatus convertJobStatusModel(JobStatusModel model) {
		JobStatus status = new JobStatus();
		status.setUuid(model.getJobUuid());
		status.setJobID(model.getJobID());
		status.setJobName(model.getJobName());
		status.setJobType(model.getJobType());
		status.setTargetServer(model.getNodeName());
		status.setTargetServerUser(model.getNodeUser());
		status.setTargetServerPwd(model.getNodePassword());
		status.setBackupLocationInfo(convertToBackupLocationInfo(model.backupLocationInfoMode));

		if (model.getExcludeVolumes() == null) {
			status.setVolumesFilter(model.getExcludeVolumes());
			status.setExclude(true);
		} else {
			status.setVolumesFilter(model.getIncludeVolumes());
			status.setExclude(false);
		}
		status.setCompressLevel(model.getCompression());
		status.setEncryptAlgoName(model.getEncryptionName());
		status.setEncryptPasswd(model.getEncryptionPassword());

		status.setStatus(model.getJobStatus());
		status.setJobPhase(model.getJobPhase());
		status.setProgress(model.getProgress());
		status.setExecuteTime(model.getExecuteTime());
		status.setElapsedTime(model.getElapsedTime());
		status.setFinishTime(model.getFinishTime());
		status.setProcessedData(model.getProcessedData());
		status.setThroughput(model.getThroughput());
		status.setLastResult(model.getLastResult());
		status.setStartTime(convertD2DTimeModel(model.startTime));
		return status;
	}

	private JobStatusModel convertJobStatus(JobStatus status) {
		JobStatusModel model = new JobStatusModel();
		model.setJobUuid(status.getUuid());
		model.setJobID(status.getJobID());
		model.setJobName(status.getJobName());
		model.setJobType(status.getJobType());
		model.setNodeName(status.getTargetServer());
		model.setNodeUser(status.getTargetServerUser());
		model.setNodePassword(status.getTargetServerPwd());
		model.backupLocationInfoMode = ServerConvertUtils
				.convertToBackupLocationInfoModel(status
						.getBackupLocationInfo());
		model.setCompression(status.getCompressLevel());
		model.setEncryptionName(status.getEncryptAlgoName());
		model.setEncryptionPassword(status.getEncryptPasswd());
		model.setCleanIncompleteSession(status.getCleanIncompleteSession());
		if (status.isExclude()) {
			model.setExcludeVolumes(status.getVolumesFilter());
		} else {
			model.setIncludeVolumes(status.getVolumesFilter());
		}
		model.setJobStatus(status.getStatus());
		model.setJobPhase(status.getJobPhase());
		model.setProgress(status.getProgress());
		model.setVolume(status.getVolume());
		model.setExecuteTime(status.getExecuteTime() * 1000);
		model.setElapsedTime(status.getElapsedTime() * 1000);
		model.setFinishTime(status.getFinishTime() * 1000);
		model.setProcessedData(status.getProcessedData() * 1024);
		model.setThroughput(status.getThroughput() * 1024);
		model.setWriteData(status.getWriteData() * 1024);
		model.setWriteThroughput(status.getWriteThroughput() * 1024);
		model.setLastResult(status.getLastResult());
		model.startTime = convertD2DTime(status.getStartTime());
		model.setJobMethod(status.getJobMethod());
		return model;
	}

	@Override
	public List<BackupMachineModel> getMachineList(
			ServiceInfoModel serviceInfo,
			BackupLocationInfoModel backupLocation, Boolean includeHbbu)
			throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			List<BackupMachine> machines = client
					.getMachineList(convertToBackupLocationInfo(backupLocation));
			List<BackupMachineModel> machineList = new ArrayList<BackupMachineModel>();
			Map<String, Integer> machineNameMap = new HashMap<String, Integer>();
			boolean includeHbbuInSystemEnv = "true".equalsIgnoreCase(System
					.getenv("include_hbbu_machine"));
			for (BackupMachine machine : machines) {
				if (!includeHbbuInSystemEnv
						&& !includeHbbu
						&& (machine.getMachineType() == BackupMachine.TYPE_HBBU_MACHINE)) {
					continue;
				}
				BackupMachineModel backupMachine = convertToBackupMachineModel(machine);
				Integer machineCount = machineNameMap.get(backupMachine
						.getMachineName());
				if (machineCount == null) {
					machineNameMap.put(backupMachine.getMachineName(), 0);
				} else {
					StringBuffer name = new StringBuffer();
					name.append(backupMachine.getMachineName());
					name.append("[");
					name.append(machineCount += 1);
					machineNameMap.put(backupMachine.getMachineName(),
							machineCount);
					name.append("]");
					backupMachine.setMachineName(name.toString());
				}
				machineList.add(backupMachine);
			}
			return machineList;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private BackupMachineModel convertToBackupMachineModel(BackupMachine machine) {
		if (machine == null)
			return null;
		BackupMachineModel model = new BackupMachineModel();
		if (machine.getMachineName() != null) {

			String machineName = CommonUtil.findMachineName(machine
					.getMachineName());
			if (BackupMachine.TYPE_TAPE_MACHINE == machine.getMachineType()) {
				model.setMachineName(machineName);
			} else {
				model.setMachineName(machineName);
				model.setMachinePath(machine.getMachineName());
			}
		}
		model.setFirstDate(machine.getFirstDate());
		model.setLastDate(machine.getLastDate());
		model.setRecoveryPointCount(machine.getRecoveryPointCount());
		model.setRecoveryPointSize(machine.getRecoveryPointSize());
		model.setRecoverySetCount(machine.getRecoverySetCount());
		model.setMachineType(machine.getMachineType());
		model.setVmHost(machine.getVmHost());
		model.setVmIp(machine.getVmIp());
		model.setMachineUUID(machine.getUuid());
		return model;
	}

	@Override
	public List<RecoveryPointModel> getRecoveryPointList(
			ServiceInfoModel serviceInfo,
			BackupLocationInfoModel backupLocation, String machine,
			Date startDate, Date endDate) throws ClientException {
		List<RecoveryPointModel> result = new ArrayList<RecoveryPointModel>();
		try {
			List<RecoveryPoint> list = getClient(serviceInfo)
					.getRecoveryPointList(
							convertToBackupLocationInfo(backupLocation),
							machine, startDate, endDate);

			if (list == null || list.size() == 0) {
				return null;
			}
			for (RecoveryPoint point : list) {
				RecoveryPointModel model = convertRecoveryPoint(point,
						JobScript.RESTORE_BMR, machine);
				result.add(model);
			}

			return result;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return result;
	}

	@Override
	public List<RecoveryPointItemModel> getDiskInfo(
			ServiceInfoModel serviceInfo,
			BackupLocationInfoModel backupLocation, String machine,
			RecoveryPointModel recoveryPointModel) throws ClientException {
		logger.info("get disk info:" + machine + ":"
				+ backupLocation.getDisplayName());
		try {
			List<RecoveryPointItemModel> result = new ArrayList<RecoveryPointItemModel>();
			RecoveryPoint rp = new RecoveryPoint();
			rp.setName(recoveryPointModel.getName());
			rp.setHbbuPath(recoveryPointModel.getHbbuPath());
			List<RecoveryPointItem> list = getClient(serviceInfo).getDiskInfo(
					convertToBackupLocationInfo(backupLocation), machine, rp);
			if (list == null || list.size() == 0) {
				return null;
			}
			for (RecoveryPointItem item : list) {
				RecoveryPointItemModel model = convertRecoveryPointItem(item);
				result.add(model);
			}
			return result;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public NodeModel getTargetMachineInfo(ServiceInfoModel serviceInfo,
			String name) throws ClientException {
		try {
			ILinuximagingService service = getClient(serviceInfo);
			TargetMachineInfo info = service.getTargetMachineByName(name);
			return convertTargetMachine(info);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public int submitRestoreJob(ServiceInfoModel serviceInfo, RestoreModel model)
			throws ClientException {

		RestoreScript script = new RestoreScript();
		script.setServerInfo(convertToServerInfo(model.getServerInfoModel()));
		script.setRestoreType(model.getRestoreType().getValue());
		script.setTemplateID(model.getTemplateID());
		script.setUuid(model.getUuid());
		script.setJobName(model.getJobName());
		script.setLinuxD2DServer(convertToServerInfo(model.getLinuxD2DServer()));
		script.setAttachedRestoreJobUUID(model.getAttachedRestoreJobUUID());
		script.setAttachedRestoreJobType(model.getAttachedRestoreType() == null ? RestoreType.BMR
				.getValue() : model.getAttachedRestoreType().getValue());
		// script.setEncryptionPassword(model.getEncryptionPassword());
		script.setBackupLocationInfo(convertToBackupLocationInfo(model.backupLocationInfoModel));
		script.setMachine(model.getMachine());
		script.setMachineName(model.getMachineName());
		script.setMachineType(model.getMachineType());
		script.setMachineUUID(model.getMachineUUID());
		script.setRecoveryPoint(convertRecoveryPointModel(
				model.getRecoveryPoint(), model.getRestoreType(),
				model.getMachine()));

		if (model.getRestoreTargetList() != null) {
			List<RestoreTarget> restoreTargetList = new ArrayList<RestoreTarget>();
			List<RestoreTargetModel> modelList = model.getRestoreTargetList();
			for (RestoreTargetModel targetModel : modelList) {
				RestoreTarget target = convertRestoreTargetModel(targetModel,
						model.getRestoreType());
				restoreTargetList.add(target);
			}
			script.setRestoreTargetList(restoreTargetList);
		}
		if (model.getVmRestoreTargetList() != null) {
			List<VirtualMachineRestoreTarget> vmTargetList = new ArrayList<VirtualMachineRestoreTarget>();
			List<VMRestoreTargetModel> vmModelList = model
					.getVmRestoreTargetList();
			for (VMRestoreTargetModel vmModel : vmModelList) {
				VirtualMachineRestoreTarget target = convertToVirtualMachineRestoreTarget(vmModel);
				vmTargetList.add(target);
			}
			script.setVmRestoreTargetList(vmTargetList);
		}
		script.setStartTime(convertD2DTimeModel(model.startTime));
		if (!StringUtil.isEmptyOrNull(model.getServerScriptBeforeJob())) {
			script.setServerScriptBeforeJob(model.getServerScriptBeforeJob());
		}
		if (!StringUtil.isEmptyOrNull(model.getServerScriptAfterJob())) {
			script.setServerScriptAfterJob(model.getServerScriptAfterJob());
		}
		if (!StringUtil.isEmptyOrNull(model.getTargetScriptBeforeJob())) {
			script.setTargetScriptBeforeJob(model.getTargetScriptBeforeJob());
		}
		if (!StringUtil.isEmptyOrNull(model.getTargetScriptAfterJob())) {
			script.setTargetScriptAfterJob(model.getTargetScriptAfterJob());
		}
		if (!StringUtil.isEmptyOrNull(model.getTargetScriptReadyForUseJob())) {
			script.setTargetScriptReadyForUseJob(model
					.getTargetScriptReadyForUseJob());
		}
		script.setEstimateFileSize(model.getEstimateFileSize());
		script.setEnableWakeOnLan(model.getEnableWakeOnLan());
		script.setLengthOftime(model.getLengthOftime());
		script.setApplication(model.getApplication());
		script.setNfsShareOption(model.getNfsShareOption());
		script.setNewUsername(model.getNewUsername());
		script.setNewPassword(model.getNewPassword());
		script.setLocalDevice(model.getLocalDevice());
		script.setLocalPath(model.getLocalPath());
		script.setExcludeTargetDisks(model.getExcludeTargetDisks());
		try {
			ILinuximagingService service = getClient(serviceInfo);
			return service.submitRestoreJob(script);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -200;
	}

	private RestoreTarget convertRestoreTargetModel(RestoreTargetModel model,
			RestoreType restoreType) {
		RestoreTarget target = new RestoreTarget();
		target.setAddress(model.getAddress());
		if (restoreType.getValue() == RestoreType.SHARE_RECOVERY_POINT
				.getValue()) {
			target.setUserName(model.getUserName());
			target.setPassword(model.getPassword());
		}
		if (restoreType.getValue() == RestoreType.BMR.getValue()
				|| restoreType.getValue() == RestoreType.MIGRATION_BMR
						.getValue()) {
			target.setUserName("root");
			target.setPassword("cad2d");
			target.setUseMac(model.getUseMac());
			target.setKeepSourceSetting(model.isKeepSourceSetting());
			if (!model.isKeepSourceSetting()) {
				target.setNetwork_hostName(model.getNetwork_HostName());
				target.setNetwork_isDHCP(model.getNetwork_IsDHCP());
				target.setNetwork_ipAddress(model.getNetwork_ipAddress());
				target.setNetwork_subnetMask(model.getNetwork_subnetMask());
				target.setNetwork_gateway(model.getNetwork_gateway());

			}
			target.setNetwork_dnsServer(model.getNetwork_dnsServer());
			target.setReboot(model.getReboot());
			target.setEnableInstanceBMR(model.isEnableInstanceBMR());
			target.setAutoRestoreData(model.isAutoRestoreData());
		} else if (restoreType.getValue() == RestoreType.VOLUME.getValue()) {
			// TO_DO
		} else if (restoreType.getValue() == RestoreType.FILE.getValue()) {
			target.setUserName(model.getUserName());
			target.setPassword(model.getPassword());
			target.setDestination(model.getDestination());
			target.setFileOption(model.getFileOption());
			target.setRestoreToOriginal(model.getRestoreToOriginal());
			target.setCreateRootDir(model.getCreateRootDir());
		}

		return target;
	}

	private VirtualMachineRestoreTarget convertToVirtualMachineRestoreTarget(
			VMRestoreTargetModel model) {
		VirtualMachineRestoreTarget target = new VirtualMachineRestoreTarget();

		VirtualizationServer server = new VirtualizationServer();
		server.setServerName(model.getVirtualizationServerModel()
				.getVirtualizationServerName());
		server.setUserName(model.getVirtualizationServerModel()
				.getVirtualizationServerUsername());
		server.setPassword(model.getVirtualizationServerModel()
				.getVirtualizationServerPassword());
		server.setPort(model.getVirtualizationServerModel().getPort());
		server.setProtocol(model.getVirtualizationServerModel().getProtocol());
		server.setServerType(model.getVirtualizationServerModel()
				.getServerType());

		VirtualMachineSetting vmSetting = new VirtualMachineSetting();
		vmSetting.setNetwork_hostName(model.getVmModel()
				.getVirtualMachineHostName());
		List<VirtualNetworkInfo> networkList = new ArrayList<VirtualNetworkInfo>();
		VirtualNetworkInfo networkInfo = new VirtualNetworkInfo();
		networkInfo.setDHCP(model.getVmModel().getNetwork_IsDHCP());
		networkInfo.setIpAddress(model.getVmModel().getNetwork_ipAddress());
		networkInfo.setNetmask(model.getVmModel().getNetwork_subnetMask());
		networkInfo.setGateway(model.getVmModel().getNetwork_gateway());
		networkInfo.setDns(model.getVmModel().getNetwork_dnsServer());
		networkList.add(networkInfo);
		vmSetting.setNetworkList(networkList);
		vmSetting.setReboot(model.getVmModel().getReboot());
		vmSetting.setDsId(model.getVmModel().getDataStoreId());
		vmSetting.setDsName(model.getVmModel().getDataStoreName());
		vmSetting.setMemory(model.getVmModel().getVirtualMachineMemory());
		vmSetting.setMemoryUnit(model.getVmModel()
				.getVirtualMachineMemoryUnit());
		vmSetting.setVmName(model.getVmModel().getVirtualMachineName());
		vmSetting.setUserName("root");
		vmSetting.setPassword("cad2d");

		target.setServerInfo(server);
		target.setVmSetting(vmSetting);
		return target;
	}

	private RecoveryPoint convertRecoveryPointModel(RecoveryPointModel model,
			RestoreType restoreType, String machine) {
		RecoveryPoint point = new RecoveryPoint();
		point.setTime(model.getTime());
		point.setBackupType(model.getBackupType());
		point.setName(model.getName());
		point.setVersion(model.getVersion());
		point.setHbbuPath(model.getHbbuPath());
		point.setCompressLevel(model.getCompression());
		point.setOsType(model.getOsType());
		point.setEncryptAlgoName(model.getEncryptAlgoName());
		point.setEncryptionPasswordHash(model.getEncryptionPasswordHash());
		point.setEncryptionPassword(model.getEncryptionPassword());
		point.setRecoverySetStartFlag(model.getRecoverySetStartFlag() == null ? 0
				: model.getRecoverySetStartFlag());
		point.setUEFI(model.isUEFI() == null ? false : model.isUEFI());
		if (model.getHbbuPath() != null) {
			List<RecoveryPointItem> items = new ArrayList<RecoveryPointItem>();
			for (RecoveryPointItemModel itemModel : model.items) {
				RecoveryPointItem item = convertRecoveryPointItemModel(itemModel);
				items.add(item);
			}
			point.setItems(items);
		}
		if (restoreType == null) {

		} else if (restoreType.getValue() == RestoreType.BMR.getValue()
				|| restoreType.getValue() == RestoreType.MIGRATION_BMR
						.getValue()
				|| restoreType.getValue() == RestoreType.VM.getValue()) {
			point.setBootVolumeExistFlag(model.getBootVolumeExist());
			point.setBootVolumeBackupFlag(model.getBootVolumeBackup());
			point.setRootVolumeBackupFlag(model.getRootVolumeBackup());
			List<RecoveryPointItem> items = new ArrayList<RecoveryPointItem>();
			for (RecoveryPointItemModel itemModel : model.items) {
				RecoveryPointItem item = convertRecoveryPointItemModel(itemModel);
				items.add(item);
			}
			point.setItems(items);
		} else if (restoreType.getValue() == RestoreType.VOLUME.getValue()) {
			// TO_DO
		} else if (restoreType.getValue() == RestoreType.FILE.getValue()) {
			List<CatalogItem> files = new ArrayList<CatalogItem>();
			for (GridTreeNode node : model.files) {
				files.add(convertToCatalogItem(node));
			}
			point.setFileList(files);
		}

		return point;
	}

	private CatalogItem convertToCatalogItem(GridTreeNode node) {
		CatalogItem item = new CatalogItem();
		item.setDate(node.getDate());
		item.setName(node.getDisplayName());
		item.setPath(node.getCatalogFilePath());
		item.setSize(node.getSize());
		item.setType(node.getType());
		return item;
	}

	private RecoveryPointItem convertRecoveryPointItemModel(
			RecoveryPointItemModel itemModel) {
		RecoveryPointItem item = new RecoveryPointItem();
		item.setName(itemModel.getName());
		item.setSize(itemModel.getSize());
		item.setBusType(itemModel.getBusType());
		item.setDiskSignature(itemModel.getDiskSignature());
		item.setDiskId(itemModel.getDiskId());
		return item;
	}

	public static RecoveryPointModel convertRecoveryPoint(RecoveryPoint point,
			int restoreType, String machine) {
		RecoveryPointModel model = new RecoveryPointModel();
		model.setTime(point.getTime());
		model.setBackupType(point.getBackupType());
		model.setName(point.getName());
		model.setOsType(point.getOsType());
		model.setCompression(point.getCompressLevel());
		model.setEncryptAlgoName(point.getEncryptAlgoName());
		model.setHbbuPath(point.getHbbuPath());
		model.setEncryptionPasswordHash(point.getEncryptionPasswordHash());
		model.setCompression(point.getCompressLevel());
		model.setEncryptAlgoName(point.getEncryptAlgoName());
		model.setRecoverySetStartFlag(point.getRecoverySetStartFlag());
		model.setBKAdvSch(point.getBkAdvSch());
		model.setVersion(point.getVersion());
		model.setUEFI(point.isUEFI());
		if (model.getHbbuPath() != null) {
			List<RecoveryPointItemModel> items = new ArrayList<RecoveryPointItemModel>();
			for (RecoveryPointItem item : point.getItems()) {
				RecoveryPointItemModel itemModel = convertRecoveryPointItem(item);
				items.add(itemModel);
			}
			model.items = items;

		}
		if (restoreType == JobScript.RESTORE_BMR
				|| restoreType == JobScript.MIGRATION_BMR
				|| restoreType == JobScript.RESTORE_VM
				|| restoreType == JobScript.ASSURED_RECOVERY) {
			model.setBootVolumeExist(point.getBootVolumeExistFlag());
			model.setBootVolumeBackup(point.getBootVolumeBackupFlag());
			model.setRootVolumeBackup(point.getRootVolumeBackupFlag());
			List<RecoveryPointItemModel> items = new ArrayList<RecoveryPointItemModel>();
			for (RecoveryPointItem item : point.getItems()) {
				RecoveryPointItemModel itemModel = convertRecoveryPointItem(item);
				items.add(itemModel);
			}
			model.items = items;
		} else if (restoreType == JobScript.RESTORE_VOLUME) {

		} else if (restoreType == JobScript.RESTORE_FILE) {
			// String mountPoint=getMountPoint(point.getName(), machine);
			List<CatalogItem> list = point.getFileList();
			List<GridTreeNode> nodes = new ArrayList<GridTreeNode>();
			for (CatalogItem file : list) {
				nodes.add(convertCatalogItem(file, "", true, 0));
			}
			model.files = nodes;
		}
		if(point.getAssuredRecoveryTestResult() != null){
			AssuredRecoveryTestResultModel testResult = new AssuredRecoveryTestResultModel();
			testResult.setStartTime(point.getAssuredRecoveryTestResult().getStartTime());
			testResult.setEndTime(point.getAssuredRecoveryTestResult().getEndTime());
			testResult.setResult(point.getAssuredRecoveryTestResult().getResult());
			model.setAssruedRecoveryTestResult(testResult);
		}
		return model;
	}

	private static RecoveryPointItemModel convertRecoveryPointItem(
			RecoveryPointItem item) {
		RecoveryPointItemModel model = new RecoveryPointItemModel();
		model.setDiskSignature(item.getDiskSignature());
		model.setName(item.getName());
		model.setSize(item.getSize());
		model.setBusType(item.getBusType());
		model.setDiskId(item.getDiskId());
		return model;
	}

	@Override
	public List<NodeModel> getNodeProtectedState(ServiceInfoModel serviceInfo,
			List<NodeModel> nodeList) throws ClientException {
		if (nodeList == null || nodeList.size() == 0) {
			return null;
		}

		try {
			List<TargetMachineInfo> targetList = new ArrayList<TargetMachineInfo>();
			for (NodeModel node : nodeList) {
				TargetMachineInfo target = convertNodeModel(node);
				if (target != null) {
					targetList.add(target);
				}
			}

			if (targetList.size() != nodeList.size()) {
				return null;
			}

			List<TargetMachineInfo> result = getClient(serviceInfo)
					.getTargetMachineProtectedState(targetList);
			for (int index = 0; index < result.size(); ++index) {
				TargetMachineInfo target = result.get(index);
				NodeModel node = nodeList.get(index);
				node.setProtected(target.isProtected());
				node.setJobName(target.getJobName());
			}
			return nodeList;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public String[] deleteNodeList(ServiceInfoModel serviceInfo,
			List<NodeModel> nodeList, Boolean force, Boolean firstCall)
			throws ClientException {
		try {
			if (nodeList == null || nodeList.size() == 0) {
				return null;
			}
			List<TargetMachineInfo> targetList = new ArrayList<TargetMachineInfo>();
			for (NodeModel node : nodeList) {
				TargetMachineInfo target = convertNodeModel(node);
				if (target != null) {
					targetList.add(target);
				}
			}

			return getClient(serviceInfo).deleteTargetMachineList(targetList,
					force, firstCall);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public PagingLoadResult<ActivityLogModel> getLogList(
			ServiceInfoModel serviceInfo, PagingLoadConfig config,
			LogFilterModel filter) throws ClientException {
		try {
			LogPagingConfig configLog = new LogPagingConfig();
			configLog.setStartIndex(config.getOffset());
			configLog.setCount(config.getLimit());
			// configLog.setAsc(false);
			LogPagingResult result = getClient(serviceInfo).getLogList(
					configLog, convertLogFilterModel(filter));
			List<ActivityLogModel> modelList = new ArrayList<ActivityLogModel>();
			if (result != null && result.getData() != null
					&& result.getData().size() > 0) {
				for (ActivityLog log : result.getData()) {
					ActivityLogModel model = convertActivityLog(log);
					if (model != null) {
						modelList.add(model);
					}
				}
				return new BasePagingLoadResult<ActivityLogModel>(modelList,
						result.getStartIndex(), result.getTotalCount());
			} else if (result != null) {
				return new BasePagingLoadResult<ActivityLogModel>(modelList,
						result.getStartIndex(), result.getTotalCount());
			}

			return new BasePagingLoadResult<ActivityLogModel>(modelList, 0, 0);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private LogFilter convertLogFilterModel(LogFilterModel model) {
		if (model == null)
			return null;

		LogFilter filter = new LogFilter();
		filter.setJobId(model.getJobID() == null ? 0 : model.getJobID());
		filter.setNodeName(model.getServer());
		if (model.getType() != null) {
			filter.setSeverity(Severity.parse(model.getType()));
		}
		filter.setJobName(model.getJobName());
		filter.setStart(model.getStartTime());
		filter.setEnd(model.getEndTime());
		return filter;
	}

	private ActivityLogModel convertActivityLog(ActivityLog log) {
		if (log != null) {
			ActivityLogModel model = new ActivityLogModel();
			model.setType((int) (log.getType()));
			model.setServer(log.getTargetName());
			model.setTime(new Date(log.getTime() * 1000));
			model.setJobID((log.getJobID()));
			model.setJobName(log.getJobName());
			model.setMessage(log.getMessage());
			return model;
		}
		return null;
	}

	@Override
	public int modifyNodeList(ServiceInfoModel serviceInfo,
			List<NodeModel> nodeList) throws ClientException {
		try {
			if (nodeList == null || nodeList.size() == 0)
				return 0;

			List<TargetMachineInfo> machineInfoList = new ArrayList<TargetMachineInfo>();
			for (NodeModel node : nodeList) {
				TargetMachineInfo machine = convertNodeModel(node);
				machineInfoList.add(machine);
			}
			return getClient(serviceInfo).modifyTargetMachineList(
					machineInfoList);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public NodeConnectionModel connectNode(ServiceInfoModel serviceInfo,
			String server, String user, String passwd, boolean forceManage)
			throws ClientException {
		if (server == null || server.isEmpty()) {
			return null;
		}
		try {
			NodeConnectionInfo result = getClient(serviceInfo).loginNode(
					server, user, passwd, forceManage);
			return convertNodeConnectionInfo(result);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	public static NodeConnectionModel convertNodeConnectionInfo(
			NodeConnectionInfo connInfo) {
		if (connInfo == null)
			return null;

		NodeConnectionModel connModel = new NodeConnectionModel();
		connModel.setNodeUUID(connInfo.getNodeUUID());
		connModel.setErrorCode(connInfo.getErrCode1());
		connModel.setErrorCodeExt(connInfo.getErrCode2());
		connModel.setOS(connInfo.getOsName());
		List<VolumeInfo> volumes = connInfo.getVolumes();
		if (volumes != null && volumes.size() > 0) {
			connModel.volumes = new ArrayList<VolumeInfoModel>();
			for (VolumeInfo volume : volumes) {
				connModel.volumes.add(convertVolumeInfo(volume));
			}
		} else {
			connModel.volumes = null;
		}

		if (connInfo.getD2dServer() != null) {
			connModel.setD2DServer(connInfo.getD2dServer().getServerName());
		}
		return connModel;
	}

	public static VolumeInfoModel convertVolumeInfo(VolumeInfo volume) {
		if (volume == null)
			return null;

		VolumeInfoModel volumeModel = new VolumeInfoModel();
		volumeModel.setMountPoint(volume.getMountOn());
		volumeModel.setFileSystem(volume.getFileSystem());
		volumeModel.setType(volume.getType());
		return volumeModel;
	}

	@Override
	public BackupModel getBackupJobScriptByUUID(ServiceInfoModel serviceInfo,
			String uuid) throws ClientException {
		if (uuid == null || uuid.isEmpty()) {
			return null;
		}
		try {
			BackupConfiguration script = getClient(serviceInfo)
					.getBackupJobScriptByUUID(uuid);
			if (script != null) {
				return convertBackupJobScript(script);
			}
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public BackupModel getBackupJobScriptByJobName(
			ServiceInfoModel serviceInfo, String jobName)
			throws ClientException {
		if (jobName == null || jobName.isEmpty()) {
			return null;
		}
		try {
			BackupConfiguration script = getClient(serviceInfo)
					.getBackupJobScriptByJobName(jobName);
			if (script != null) {
				return convertBackupJobScript(script);
			}
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private BackupModel convertBackupJobScript(BackupConfiguration script) {
		BackupModel model = new BackupModel();
		model.setUuid(script.getUuid());
		model.setTemplateID(script.getTemplateID());
		model.setJobName(script.getJobName());
		model.setDisable(script.isDisable());
		List<NodeModel> targetList = new ArrayList<NodeModel>();
		targetList.add(convertBackupTarget(script.getTarget()));
		List<BackupTarget> targets = script.getOtherTargets();
		if (targets != null && targets.size() > 0) {
			for (BackupTarget target : targets) {
				targetList.add(convertBackupTarget(target));
			}
		}
		model.setTargetList(targetList);
		model.setExclude(script.isExclude());
		model.setExcludeVolumes(script.getVolumesFilter());
		model.setExcludeFiles(script.getExcludeFiles());
		model.setDestInfo(convertDataBackupTemplate(script));
		model.setThrottle(script.getThrottle());
		model.setScheduleType(script.getScheduleType());
		int scheduleType = script.getScheduleType();
		if (scheduleType == BackupModel.SCHEDULE_TYPE_ONCE) {
			model.startTime = convertD2DTime(script.getStartTime());
		} else if (scheduleType == BackupModel.SCHEDULE_TYPE_REPEAT_DAILY) {
			DailyScheduleModel dailyModel = new DailyScheduleModel();
			dailyModel.fullSchedule = convertBackupSchedule(script
					.getDailySchedule().getFullSchedule());
			dailyModel.incrementalSchedule = convertBackupSchedule(script
					.getDailySchedule().getIncrementalSchedule());
			dailyModel.resyncSchedule = convertBackupSchedule(script
					.getDailySchedule().getResyncSchedule());
			dailyModel.startTime = convertD2DTime(script.getDailySchedule()
					.getStartTime());
			model.dailySchedule = dailyModel;
		} else if (scheduleType == BackupModel.SCHEDULE_TYPE_REPEAT_WEEKLY) {
			WeeklyScheduleModel weeklyModel = new WeeklyScheduleModel();
			weeklyModel.startTime = convertD2DTime(script.getWeeklySchedule()
					.getStartTime());
			List<BackupScheduleModel> scheduleList = new ArrayList<BackupScheduleModel>();
			if (script.getWeeklySchedule().getScheduleList() != null) {
				for (BackupSchedule schedule : script.getWeeklySchedule()
						.getScheduleList()) {
					scheduleList.add(convertBackupSchedule(schedule));
				}
			}
			weeklyModel.scheduleList = scheduleList;
			model.weeklySchedule = weeklyModel;
		}

		model.retentionModel = convertToRetentionModel(script.getRetention());
		model.setServerScriptAfterJob(script.getServerScriptAfterJob());
		model.setServerScriptBeforeJob(script.getServerScriptBeforeJob());
		model.setTargetScriptAfterJob(script.getTargetScriptAfterJob());
		model.setTargetScriptBeforeJob(script.getTargetScriptBeforeJob());
		model.setTargetScriptBeforeSnapshot(script
				.getTargetScriptBeforeSnapshot());
		model.setTargetScriptAfterSnapshot(script
				.getTargetScriptAfterSnapshot());

		return model;
	}

	private NodeModel convertBackupTarget(BackupTarget target) {
		if (target == null) {
			return null;
		}
		NodeModel node = this.convertTargetMachine(target);
		node.setPriority(target.getPriority());
		if (target.getExcludeVolumes() == null) {
			return node;
		}
		node.setExclude(target.isExclude());
		List<VolumeInfoModel> excludeVolumes = new ArrayList<VolumeInfoModel>(
				target.getExcludeVolumes().size());
		for (VolumeInfo info : target.getExcludeVolumes()) {
			VolumeInfoModel model = convertVolumeInfo(info);
			excludeVolumes.add(model);
		}
		node.excludeVolumes = excludeVolumes;

		return node;
	}

	private BackupScheduleModel convertBackupSchedule(BackupSchedule schedule) {
		if (schedule == null) {
			return null;
		}
		BackupScheduleModel model = new BackupScheduleModel();

		model.setEnabled(schedule.isEnabled());
		model.setMethod(schedule.getJobType());
		model.startTime = convertD2DTime(schedule.getStartTime());
		model.setDay(schedule.getDay());
		model.setIsRoot(false);
		if (schedule.isEnabled()) {
			model.setInterval(schedule.getInterval());
			model.setIntervalUnit(schedule.getIntervalUnit());
			model.endTime = convertD2DTime(schedule.getEndTime());
		}
		return model;
	}

	private D2DTimeModel convertD2DTime(D2DTime time) {
		D2DTimeModel model = new D2DTimeModel();
		if (time == null) {
			return null;
		} else if (time.isRunNow()) {
			model.setRunNow(true);
			model.setReady(time.isReady());
			return model;
		} else {
			model.setYear(time.getYear());
			model.setMonth(time.getMonth());
			model.setDay(time.getDay());
			model.setHour(time.getHour());
			model.setMinute(time.getMinute());
			model.setAMPM(time.getAmPM());
			model.setHourOfDay(time.getHourOfday());
			model.setReady(time.isReady());
			return model;
		}
	}

	@Override
	public RestoreModel getRestoreJobScriptByUUID(ServiceInfoModel serviceInfo,
			String uuid) throws ClientException {
		if (uuid == null || uuid.isEmpty()) {
			return null;
		}
		try {
			RestoreConfiguration script = getClient(serviceInfo)
					.getRestoreJobScriptByUUID(uuid);
			if (script != null) {
				RestoreModel restoreModel = convertRestoreJobScript(script);
				if (restoreModel.getServerInfoModel() != null) {
					restoreModel.getServerInfoModel().setRestoreJobId(uuid);
				}
				return restoreModel;
			} else {
				return null;
			}
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private RestoreModel convertRestoreJobScript(RestoreConfiguration script) {
		RestoreModel model = new RestoreModel();
		model.setRestoreType(RestoreType.parse(script.getJobType()));
		model.setTemplateID(script.getTemplateID());
		model.setUuid(script.getUuid());
		model.setJobName(script.getJobName());
		model.setAttachedRestoreJobUUID(script.getAttachedRestoreJobUUID());
		model.setLinuxD2DServer(convertToServerInfoModel(script
				.getLinuxD2DServer()));
		model.setAttachedRestoreType(RestoreType.parse(script
				.getAttachedRestoreJobType()));
		// script.setEncryptionPassword(model.getEncryptionPassword());
		model.backupLocationInfoModel = ServerConvertUtils
				.convertToBackupLocationInfoModel(script
						.getBackupLocationInfo());
		model.setMachine(script.getMachine());
		model.setMachineName(script.getMachineName());
		model.setMachineType(script.getMachineType());
		model.setMachineUUID(script.getMachineUUID());
		model.setServerInfoModel(convertToServerInfoModel(script
				.getServerInfo()));
		model.setRecoveryPoint(convertRecoveryPoint(script.getRecoveryPoint(),
				script.getJobType(), script.getMachine()));
		model.getRecoveryPoint().setEncryptionPassword(
				script.getEncryptPasswd());
		model.setEstimateFileSize(script.isEstimateFileSize());
		model.setServerScriptAfterJob(script.getServerScriptAfterJob());
		model.setServerScriptBeforeJob(script.getServerScriptBeforeJob());
		model.setTargetScriptAfterJob(script.getTargetScriptAfterJob());
		model.setTargetScriptBeforeJob(script.getTargetScriptBeforeJob());
		model.setTargetScriptReadyForUseJob(script
				.getTargetScriptReadyForUseJob());
		model.setEnableWakeOnLan(script.isEnableWakeOnLan());
		model.setNewUsername(script.getNewUsername());
		model.setNewPassword(script.getNewPassword());
		model.setLocalDevice(script.getLocalDevice());
		model.setLocalPath(script.getLocalPath());
		model.setLengthOftime(script.getLengthOftime());
		model.setApplication(script.getApplication());
		model.setNfsShareOption(script.getNfsShareOption());
		model.setExcludeTargetDisks(script.getExcludeTargetDisks());
		List<RestoreTargetModel> targetList = new ArrayList<RestoreTargetModel>();
		if (script.getRestoreTarget() != null) {
			RestoreTargetModel targetModel = convertRestoreTarget(
					script.getRestoreTarget(), script.getJobType());
			targetList.add(targetModel);
		}
		if (script.getOtherTargets() != null) {
			for (RestoreTarget target : script.getOtherTargets()) {
				RestoreTargetModel targetModel = convertRestoreTarget(target,
						script.getJobType());
				targetList.add(targetModel);
			}
		}
		model.setRestoreTargetList(targetList);

		if (script.getVmRestoreTarget() != null) {
			VMRestoreTargetModel vmTargetModel = convertVMRetoreTarget(script
					.getVmRestoreTarget());
			List<VMRestoreTargetModel> vmTargetList = new ArrayList<VMRestoreTargetModel>();
			vmTargetList.add(vmTargetModel);
			model.setVmRestoreTargetList(vmTargetList);
		}
		model.startTime = convertD2DTime(script.getStartTime());
		return model;
	}

	private RestoreTargetModel convertRestoreTarget(RestoreTarget target,
			int restoreType) {
		RestoreTargetModel model = new RestoreTargetModel();
		model.setAddress(target.getAddress());
		if (restoreType == JobScript.RESTORE_BMR
				|| restoreType == JobScript.MIGRATION_BMR) {
			model.setUseMac(target.isUseMac());
			model.setKeepSourceSetting(target.isKeepSourceSetting());
			if (!target.isKeepSourceSetting()) {
				model.setNetwork_HostName(target.getNetwork_hostName());
				model.setNetwork_IsDHCP(target.isNetwork_isDHCP());
				model.setNetwork_ipAddress(target.getNetwork_ipAddress());
				model.setNetwork_subnetMask(target.getNetwork_subnetMask());
				model.setNetwork_gateway(target.getNetwork_gateway());
			}
			model.setNetwork_dnsServer(target.getNetwork_dnsServer());
			model.setReboot(target.isReboot());
			model.setEnableInstanceBMR(target.isEnableInstanceBMR());
			model.setAutoRestoreData(target.isAutoRestoreData());
		} else if (restoreType == JobScript.RESTORE_VOLUME) {

		} else if (restoreType == JobScript.SHARE_RECOVERY_POINT) {
			model.setUserName(target.getUserName());
			model.setPassword(target.getPassword());
		} else if (restoreType == JobScript.RESTORE_FILE) {
			model.setUserName(target.getUserName());
			model.setPassword(target.getPassword());
			model.setDestination(target.getDestination());
			model.setFileOption(target.getFileOption());
			model.setRestoreToOriginal(target.isRestoreToOriginal());
			model.setCreateRootDir(target.isCreateRootDir());
		}
		return model;
	}

	private VMRestoreTargetModel convertVMRetoreTarget(
			VirtualMachineRestoreTarget vmTarget) {
		VMRestoreTargetModel model = new VMRestoreTargetModel();

		VirtualizationServerModel serverModel = new VirtualizationServerModel();
		serverModel.setPort(vmTarget.getServerInfo().getPort());
		serverModel.setProtocol(vmTarget.getServerInfo().getProtocol());
		serverModel.setServerType(vmTarget.getServerInfo().getServerType());
		serverModel.setVirtualizationServerName(vmTarget.getServerInfo()
				.getServerName());
		serverModel.setVirtualizationServerUsername(vmTarget.getServerInfo()
				.getUserName());
		serverModel.setVirtualizationServerPassword(vmTarget.getServerInfo()
				.getPassword());
		model.setVirtualizationServerModel(serverModel);

		VirtualMachineInfoModel vmModel = new VirtualMachineInfoModel();
		vmModel.setVirtualMachineHostName(vmTarget.getVmSetting()
				.getNetwork_hostName());
		VirtualNetworkInfo virtualNetwork = vmTarget.getVmSetting()
				.getNetworkList().get(0);
		vmModel.setNetwork_IsDHCP(virtualNetwork.isDHCP());
		vmModel.setNetwork_ipAddress(virtualNetwork.getIpAddress());
		vmModel.setNetwork_subnetMask(virtualNetwork.getNetmask());
		vmModel.setNetwork_gateway(virtualNetwork.getGateway());
		vmModel.setNetwork_dnsServer(virtualNetwork.getDns());
		vmModel.setReboot(vmTarget.getVmSetting().isReboot());
		vmModel.setDataStoreId(vmTarget.getVmSetting().getDsId());
		vmModel.setDataStoreName(vmTarget.getVmSetting().getDsName());
		vmModel.setVirtualMachineMemory(vmTarget.getVmSetting().getMemory());
		vmModel.setVirtualMachineMemoryUnit(vmTarget.getVmSetting()
				.getMemoryUnit());
		vmModel.setVirtualMachineName(vmTarget.getVmSetting().getVmName());
		model.setVmModel(vmModel);

		return model;

	}

	private BackupTemplateModel convertDataBackupTemplate(
			BackupConfiguration data) {
		BackupTemplateModel model = new BackupTemplateModel();
		model.setName(data.getJobName());
		model.backupLocationInfoModel = ServerConvertUtils
				.convertToBackupLocationInfoModel(data.getBackupLocationInfo());
		model.setCompression(data.getCompressLevel());
		model.setEncryptionName(data.getEncryptAlgoName());
		model.setEncryptionType(data.getEncryptAlgoType());
		model.setEncryptionPassword(data.getEncryptPasswd());
		return model;
	}

	@Override
	public int holdJobSchedule(ServiceInfoModel serviceInfo,
			JobStatusModel model, boolean ready) throws ClientException {
		try {
			JobScript script = convertJobStatusModel(model);
			return getClient(serviceInfo).holeJobScheule(script, ready);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public int initWebServiceBehindFirewall(ServiceInfoModel serviceInfo) {
		return initWebServiceBehindFirewall(serviceInfo.getProtocol(),
				serviceInfo.getServer(), serviceInfo.getPort());
	}

	@Override
	public List<String> getSupportedFSType(ServiceInfoModel serviceInfo)
			throws ClientException {
		try {
			return getClient(serviceInfo).getSupportedFSType();
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public PagingLoadResult<GridTreeNode> getPagingGridTreeNode(
			ServiceInfoModel serviceInfo,
			BackupLocationInfoModel sessionLocation, String machine,
			RecoveryPointModel pointModel, GridTreeNode parent,
			PagingLoadConfig pageCfg, String scriptUUID) throws ClientException {
		logger.debug("getPagingGridTreeNode(GridTreeNode, PagingLoadConfig) - start");
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		logger.debug("seesionid is: " + session.getId());
		try {
			if (parent == null) {
				parent = convertToGridTreeNode(pointModel);
			}

			int start = pageCfg.getOffset();
			int size = pageCfg.getLimit();

			String catPath = parent.getCatalogFilePath();
			long parentID = parent.getParentID();
			// long subSessionID = parent.getSubSessionID();

			logger.debug("start:" + start);
			logger.debug("size:" + size);
			logger.debug("catPath:" + catPath);
			logger.debug("parentID:" + parentID);
			// logger.debug("subSessionID:" + subSessionID);

			ILinuximagingService client = getClient(serviceInfo);
			if (client != null) {
				RecoveryPoint point = convertRecoveryPointModel(pointModel,
						null, machine);
				PagedCatalogItem items = client.getPagedCatalogItems(
						convertToBackupLocationInfo(sessionLocation), machine,
						point, catPath, parentID, start, size, scriptUUID);
				CatalogItem[] catalogs = items.getCaltalogItems();

				ArrayList<GridTreeNode> retlst = new ArrayList<GridTreeNode>();

				if (catalogs != null) {
					boolean selectable = true;

					for (int i = 0; i < catalogs.length; i++) {
						retlst.add(convertCatalogItem(catalogs[i], catPath,
								selectable, parent.getSelectState()));
					}
					sortNodeList(retlst);
					logger.debug("CatalogItemModel length = " + catalogs.length);
				}

				int totalLen = (int) items.getTotal();

				return new BasePagingLoadResult<GridTreeNode>(retlst, start,
						totalLen);
			}

		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}

		logger.debug("getPagingGridTreeNode(GridTreeNode, PagingLoadConfig) - end");

		return null;
	}

	private static GridTreeNode convertCatalogItem(CatalogItem item,
			String catPath, boolean selectable, int parentState) {
		GridTreeNode node = new GridTreeNode();
		node.setName(item.getName());
		if (item.getType() == CatalogModelType.File) {
			node.setSize(item.getSize());
		} else {
			node.setSize(0L);
		}
		// node.setDate(model.getDate().toString());
		node.setParentID(item.getId());
		// node.setCatalogFilePath(catPath);
		node.setCatalogFilePath(item.getPath());
		node.setType(item.getType());
		node.setPath(item.getPath());
		node.setSelectable(selectable);
		node.setDisplayName(item.getName());
		node.setChildrenCount(item.getChildrenCount());
		node.setChildrenFolderCount(item.getChildrenFolderCount());
		node.setChecked(false);
		node.setDate(item.getDate());
		node.setServerTZOffset(item.getServerTimeZoneOffset());
		if (parentState == GridTreeNode.FULL)
			node.setSelectState(GridTreeNode.FULL);
		else
			node.setSelectState(GridTreeNode.NONE);
		// node.setId(node.toId().hashCode());
		return node;
	}

	private void sortNodeList(List<GridTreeNode> nodeList) {
		Collections.sort(nodeList, new Comparator<GridTreeNode>() {

			@Override
			public int compare(GridTreeNode m1, GridTreeNode m2) {
				if (m1.getType() != null && m2.getType() != null
						&& !m1.getType().equals(m2.getType())) {
					return m1.getType().compareTo(m2.getType());
				} else {
					return fileNameCompare(m1, m2);
				}
			}

			private int fileNameCompare(GridTreeNode m1, GridTreeNode m2) {
				int r = 0;
				if (m1.getDisplayName() == null) {
					if (m2.getDisplayName() == null)
						return 0;
					else
						return -1;
				} else {
					if (m2.getDisplayName() == null)
						return 1;
					else {
						r = m1.getDisplayName().compareToIgnoreCase(
								m2.getDisplayName());
						if (r == 0)
							r = m1.getDisplayName().compareTo(
									m2.getDisplayName());
						return r;
					}
				}
			}

		});
	}

	@Override
	public List<GridTreeNode> getTreeGridChildren(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel sessionLocation, String machine,
			RecoveryPointModel pointModel, GridTreeNode loadConfig,
			String scriptUUID) throws ClientException {
		logger.debug("getTreeGridChildren - start");
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		logger.debug("seesionid is: " + session.getId());
		try {
			/*
			 * if(loadConfig == null){ List<GridTreeNode> retList = new
			 * ArrayList<GridTreeNode>();
			 * retList.add(convertToGridTreeNode(pointModel)); return retList; }
			 */
			String catPath = loadConfig.getCatalogFilePath();
			long parentID = loadConfig.getParentID();
			RecoveryPoint point = convertRecoveryPointModel(pointModel, null,
					machine);
			CatalogItem[] items = getClient(serviceInfo).getCatalogItems(
					convertToBackupLocationInfo(sessionLocation), machine,
					point, catPath, parentID, false, scriptUUID);
			List<GridTreeNode> list = new ArrayList<GridTreeNode>();
			if (items == null) {
				return list;
			}
			for (CatalogItem item : items) {
				GridTreeNode node = convertCatalogItem(item, catPath, true,
						loadConfig.getSelectState());
				list.add(node);
			}
			// for(int i=0;i<10;i++){
			// GridTreeNode node = new GridTreeNode();
			// node.setType(6);
			// node.setDate(null);
			// node.setSize(111111L);
			// node.setName("test"+i);
			// node.setDisplayName("test"+i);
			// node.setCatalofFilePath("/test"+i);
			// node.setChildrenCount(10L);
			// // First Level use -1
			// node.setParentID(-2l);
			// node.setChecked(false);
			// node.setSubSessionID(1);
			// list.add(node);
			// }
			logger.debug("getTreeGridChildren - return: " + list.size());
			return list;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}

		logger.debug("getTreeGridChildren - end");

		return null;
	}

	private GridTreeNode convertToGridTreeNode(RecoveryPointModel model) {
		GridTreeNode node = new GridTreeNode();
		node.setType(6);
		node.setDate(null);
		node.setSize(111111L);
		node.setName(java.io.File.separator);
		node.setDisplayName(java.io.File.separator);
		node.setCatalogFilePath(java.io.File.separator);
		node.setPath(java.io.File.separator);

		node.setChildrenCount(10L);
		// First Level use -1
		node.setParentID(-1l);
		node.setChecked(false);
		// node.setSubSessionID(1);
		node.setSelectState(GridTreeNode.NONE);
		return node;
	}

	@Override
	public D2DTimeModel getCurrentD2DTimeFromServer(ServiceInfoModel serviceInfo)
			throws ClientException {
		try {
			D2DTime time = getClient(serviceInfo).getCurrentD2DTime();
			return convertD2DTime(time);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public List<FileModel> getFileItems(ServiceInfoModel serviceInfo,
			String inputFolder, String host, String user, String password,
			boolean bIncludeFiles, String scriptUUID) throws ClientException {
		try {
			FileFolderItem item = getClient(serviceInfo)
					.getFileFolderWithCredentials(inputFolder, host, user,
							password, scriptUUID);
			List<FileModel> modelList = ConvertToFileModelList(item,
					bIncludeFiles);
			return modelList;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private List<FileModel> ConvertToFileModelList(FileFolderItem item,
			boolean includeFiles) {

		List<FileModel> modelList = new ArrayList<FileModel>();

		if (item.getFolders() != null) {
			for (int i = 0; i < item.getFolders().length; i++) {
				modelList.add(ConvertToModel(item.getFolders()[i]));
			}
		}
		if (includeFiles && item.getFiles() != null) {
			for (int i = 0; i < item.getFiles().length; i++) {
				modelList.add(ConvertToModel(item.getFiles()[i]));
			}
		}
		return modelList;
	}

	private FileModel ConvertToModel(File file) {
		FileModel m = new FileModel();
		m.setName(file.getName());
		m.setPath(file.getPath());
		m.setType(CatalogModelType.File);

		return m;
	}

	private FileModel ConvertToModel(Folder folder) {
		FileModel m = new FileModel();
		m.setName(folder.getName());
		m.setPath(folder.getPath());
		if (folder.getPath().equals("")) {
			m.setType(CatalogModelType.File);
		} else {
			m.setType(CatalogModelType.Folder);
		}
		return m;
	}

	@Override
	public void createFolder(ServiceInfoModel serviceInfo, String parentPath,
			String subDir, String host, String user, String password,
			String scriptUUID) throws ClientException {
		logger.debug("createFolder - start: " + subDir);
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		logger.debug("seesionid is: " + session.getId());
		try {
			getClient(serviceInfo).createFolder(parentPath, subDir, host, user,
					password, scriptUUID);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
	}

	@Override
	public void removeMountPoint(ServiceInfoModel serviceInfo, String machine,
			RecoveryPointModel model) throws ClientException {
		logger.debug("removeMountPoint - start: " + machine + " | "
				+ model.getName());
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		logger.debug("seesionid is: " + session.getId());
		if (model == null || machine == null) {
			return;
		}
		try {
			RecoveryPoint point = convertRecoveryPointModel(model, null,
					machine);
			getClient(serviceInfo).removeMountPoint(machine, point);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
	}

	@Override
	public List<DatastoreModel> getDataStoreList(ServiceInfoModel serviceInfo,
			VirtualizationServerModel serverModel) throws ClientException {
		try {
			ILinuximagingService service = getClient(serviceInfo);
			List<DataStore> dsList = service
					.getDataStore(convertToVirualizationServer(serverModel));
			if (dsList != null) {
				List<DatastoreModel> dsModelList = new ArrayList<DatastoreModel>();
				for (DataStore ds : dsList) {
					DatastoreModel dsModel = new DatastoreModel();
					dsModel.setDisplayName(ds.getName());
					dsModel.setId(ds.getId());
					dsModelList.add(dsModel);
				}
				return dsModelList;
			}
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}

		return null;
	}

	private VirtualizationServer convertToVirualizationServer(
			VirtualizationServerModel serverModel) {
		VirtualizationServer server = new VirtualizationServer();
		server.setServerName(serverModel.getVirtualizationServerName());
		server.setServerType(serverModel.getServerType());
		server.setUserName(serverModel.getVirtualizationServerUsername());
		server.setPassword(serverModel.getVirtualizationServerPassword());
		server.setProtocol(serverModel.getProtocol());
		server.setPort(serverModel.getPort());
		return server;
	}

	@Override
	public BrowseSearchResultModel getFileFolderBySearch(
			ServiceInfoModel serviceInfo,
			BackupLocationInfoModel sessionLocation, String machine,
			RecoveryPointModel pointModel, GridTreeNode loadConfig,
			SearchConditionModel searchCondition) throws ClientException {
		logger.debug("getFileFolderBySearch - start");
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		logger.debug("seesionid is: " + session.getId());
		try {
			if (loadConfig == null)
				loadConfig = convertToGridTreeNode(pointModel);
			String catPath = loadConfig.getCatalogFilePath();
			long parentID = loadConfig.getParentID();
			RecoveryPoint point = convertRecoveryPointModel(pointModel, null,
					machine);
			SearchResult result = getClient(serviceInfo).getFileFolderBySearch(
					convertToBackupLocationInfo(sessionLocation), machine,
					point, catPath, parentID,
					convertToSearchCondition(searchCondition));
			List<GridTreeNode> list = new ArrayList<GridTreeNode>();
			if (result == null) {
				return null;
			}
			if (result.getResultList() != null) {
				for (CatalogItem item : result.getResultList()) {
					GridTreeNode node = convertCatalogItem(item, catPath, true,
							0);
					list.add(node);
				}
				sortNodeList(list);
			}
			BrowseSearchResultModel model = new BrowseSearchResultModel();
			model.setResultList(list);
			model.setSearchEnd(result.isSearchEnd());
			logger.debug("getFileFolderBySearch - return: " + list.size());
			return model;
		} catch (WebServiceException ex) {
			logger.error("getFileFolderBySearch error occurred:", ex);
			proccessAxisFaultException(ex);
		}

		logger.debug("getFileFolderBySearch - end");

		return null;
	}

	@Override
	public int startSearch(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel sessionLocation, String machine,
			RecoveryPointModel pointModel, GridTreeNode loadConfig,
			SearchConditionModel searchCondition, String scriptUUID)
			throws ClientException {
		logger.debug("startSearch - start");
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		logger.debug("seesionid is: " + session.getId());
		try {
			if (loadConfig == null)
				loadConfig = convertToGridTreeNode(pointModel);
			String catPath = loadConfig.getCatalogFilePath();
			long parentID = loadConfig.getParentID();
			RecoveryPoint point = convertRecoveryPointModel(pointModel, null,
					machine);
			int ret = getClient(serviceInfo).startSearch(
					convertToBackupLocationInfo(sessionLocation), machine,
					point, catPath, parentID,
					convertToSearchCondition(searchCondition), scriptUUID);
			return ret;
		} catch (WebServiceException ex) {
			logger.error("startSearch error occurred:", ex);
			proccessAxisFaultException(ex);
		}

		logger.debug("startSearch - end");

		return -1;
	}

	@Override
	public int stopSearch(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel sessionLocation, String machine,
			RecoveryPointModel pointModel, GridTreeNode loadConfig,
			SearchConditionModel searchCondition) throws ClientException {

		logger.debug("stopSearch - start");
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		logger.debug("seesionid is: " + session.getId());
		try {
			if (loadConfig == null)
				loadConfig = convertToGridTreeNode(pointModel);
			String catPath = loadConfig.getCatalogFilePath();
			long parentID = loadConfig.getParentID();
			RecoveryPoint point = convertRecoveryPointModel(pointModel, null,
					machine);
			int ret = getClient(serviceInfo).stopSearch(
					convertToBackupLocationInfo(sessionLocation), machine,
					point, catPath, parentID,
					convertToSearchCondition(searchCondition));
			return ret;
		} catch (WebServiceException ex) {
			logger.error("stopSearch error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		logger.debug("stopSearch - end");

		return -1;
	}

	private SearchCondition convertToSearchCondition(SearchConditionModel model) {
		SearchCondition condition = new SearchCondition();
		condition.setPageSize(model.getPageSize());
		condition.setStart(model.getStart());
		condition.setSearchStr(model.getSearchStr());
		return condition;
	}

	@Override
	public List<String> getScripts(ServiceInfoModel serviceInfo, int type)
			throws ClientException {
		try {
			logger.debug("getPrePostScripts start");
			ILinuximagingService client = getClient(serviceInfo);
			List<String> machines = client.getScripts(type);
			logger.debug("getPrePostScripts end");
			return machines;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;

	}

	@Override
	public boolean checkRecoveryPointPasswd(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel sessionLocation, String machine,
			RecoveryPointModel pointModel, String scriptUUID)
			throws ClientException {
		try {
			logger.debug("checkRecoveryPointPasswd start");
			ILinuximagingService client = getClient(serviceInfo);
			RecoveryPoint point = convertRecoveryPointModel(pointModel, null,
					machine);
			boolean ret = client.checkRecoveryPointPasswd(
					convertToBackupLocationInfo(sessionLocation), machine,
					point, scriptUUID);
			logger.debug("checkRecoveryPointPasswd end" + ret);
			return ret;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return false;
	}

	@Override
	public boolean validateBackupLocation(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel backupLocationInfoModel)
			throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			return client
					.validateBackupLocation(convertToBackupLocationInfo(backupLocationInfoModel));
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return false;
	}

	private BackupLocationInfo convertToBackupLocationInfo(
			BackupLocationInfoModel backupLocationInfoModel) {
		if (backupLocationInfoModel == null) {
			return new BackupLocationInfo();
		}
		BackupLocationInfo info = new BackupLocationInfo();
		info.setUuid(backupLocationInfoModel.getUUID());
		info.setBackupDestLocation(backupLocationInfoModel.getSessionLocation());
		info.setBackupDestUser(backupLocationInfoModel.getUser());
		info.setBackupDestPasswd(backupLocationInfoModel.getPassword());
		if (backupLocationInfoModel.getType() != null) {
			info.setType(backupLocationInfoModel.getType());
		}
		DataStoreInfo dataStore = new DataStoreInfo();
		ServerInfo serverInfo = new ServerInfo();
		if (backupLocationInfoModel.getDatastoreModel() != null) {
			dataStore.setEnableDedup(backupLocationInfoModel.getEnableDedup());
			dataStore.setSharePath(backupLocationInfoModel.getDatastoreModel()
					.getSharePath());
			dataStore.setName(backupLocationInfoModel.getDatastoreModel()
					.getDisplayName());
			dataStore.setSharePathUsername((backupLocationInfoModel
					.getDatastoreModel().getSharePathUsername()));
			dataStore.setSharePathPassword(backupLocationInfoModel
					.getDatastoreModel().getSharePathPassword());
			dataStore.setUuid(backupLocationInfoModel.getDatastoreModel()
					.getUuid());
		}

		if (backupLocationInfoModel.getServerInfoModel() != null) {
			serverInfo.setName(backupLocationInfoModel.getServerInfoModel()
					.getServerName());
			serverInfo.setUser(backupLocationInfoModel.getServerInfoModel()
					.getUserName());
			serverInfo.setPassword(backupLocationInfoModel.getServerInfoModel()
					.getPasswd());
			serverInfo.setPort(backupLocationInfoModel.getServerInfoModel()
					.getPort());
			serverInfo.setProtocol(backupLocationInfoModel.getServerInfoModel()
					.getProtocol());
		}
		info.setDataStoreInfo(dataStore);
		info.setServerInfo(serverInfo);
		return info;
	}

	@Override
	public int addNodeByDiscovery(ServiceInfoModel serviceInfo,
			NodeDiscoverySettingsModel nodeDiscoveryModel)
			throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			return client
					.addNodeByDiscovery(convertToNodeDiscoverySettings(nodeDiscoveryModel));
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	private NodeDiscoverySettings convertToNodeDiscoverySettings(
			NodeDiscoverySettingsModel nodeDiscoveryModel) {
		NodeDiscoverySettings settings = new NodeDiscoverySettings();
		settings.setScript(nodeDiscoveryModel.getScript());
		settings.setSchedule(convertBackupScheduleModel(
				nodeDiscoveryModel.schedule,
				BackupModel.SCHEDULE_TYPE_REPEAT_DAILY));
		return settings;

	}

	@Override
	public NodeDiscoverySettingsModel getNodeDiscoverySettingsModel(
			ServiceInfoModel serviceInfo) throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			NodeDiscoverySettings settings = client.getNodeDiscoverySettings();

			return convertToNodeDiscoverySettingsModel(settings);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private NodeDiscoverySettingsModel convertToNodeDiscoverySettingsModel(
			NodeDiscoverySettings settings) {
		if (settings == null) {
			return null;
		}
		NodeDiscoverySettingsModel model = new NodeDiscoverySettingsModel();
		model.setScript(settings.getScript());
		model.schedule = convertBackupSchedule(settings.getSchedule());
		return model;
	}

	@Override
	public DashboardModel getDashboardInformation(ServiceInfoModel serviceInfo)
			throws ClientException {

		try {
			ILinuximagingService client = getClient(serviceInfo);
			Dashboard dashboard = client.getDashboardInformation();
			return convertToDashboardModel(dashboard);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private DashboardModel convertToDashboardModel(Dashboard dashboard) {
		if (dashboard == null) {
			return null;
		}
		DashboardModel model = new DashboardModel();
		model.setServerInformation(convertToD2DServerInfoModel(dashboard
				.getServerInformation()));
		model.setResourceUsage(convertToResourceUsageModel(dashboard
				.getResourceUsage()));
		model.setBackupStorages(convertToBackupLocationModels(dashboard
				.getBackupStorages()));
		model.setNodeSummary(convertToNodeSummaryModel(dashboard
				.getNodeSummary()));
		model.setJobSummary(convertToJobSummaryModel(dashboard.getJobSummary()));
		return model;
	}

	private JobSummaryModel convertToJobSummaryModel(JobSummary jobSummary) {
		if (jobSummary == null) {
			return null;
		}
		JobSummaryModel model = new JobSummaryModel();
		model.setTotalJobs(jobSummary.getTotalJobs());
		model.setSuccess(jobSummary.getSuccess());
		model.setFailure(jobSummary.getFailure());
		model.setIncomplete(jobSummary.getIncomplete());
		return model;
	}

	private NodeSummaryModel convertToNodeSummaryModel(TargetSummary nodeSummary) {
		if (nodeSummary == null) {
			return null;
		}
		NodeSummaryModel model = new NodeSummaryModel();
		model.setTotalNodes(nodeSummary.getTotalNodes());
		model.setProtectedNodes(nodeSummary.getProtectedNodes());
		model.setFailureNodes(nodeSummary.getFailureNodes());
		return model;
	}

	private BackupLocationInfoModel[] convertToBackupLocationModels(
			BackupLocationInfo[] backupStorages) {
		if (backupStorages == null) {
			return null;
		}
		BackupLocationInfoModel[] models = new BackupLocationInfoModel[backupStorages.length];
		Map<String, Integer> nameMap = new HashMap<String, Integer>();
		for (int i = 0; i < backupStorages.length; i++) {
			models[i] = ServerConvertUtils.convertToBackupLocationInfoModel(
					backupStorages[i], nameMap);
		}
		return models;
	}

	private ResourceUsageModel convertToResourceUsageModel(
			ResourceUsage resourceUsage) {
		if (resourceUsage == null) {
			return null;
		}
		ResourceUsageModel model = new ResourceUsageModel();
		model.setCpuUsage(resourceUsage.getCpuUsage());
		model.setPhysicalMemoryTotal(resourceUsage.getPhysicalMemoryTotal());
		model.setPhysicalMemoryFree(resourceUsage.getPhysicalMemoryFree());
		model.setSwapSizeTotal(resourceUsage.getSwapSizeTotal());
		model.setSwapSizeFree(resourceUsage.getSwapSizeFree());
		model.setInstallationVolumeTotal(resourceUsage
				.getInstallationVolumeTotal());
		model.setInstallationVolumeFree(resourceUsage
				.getInstallationVolumeFree());
		return model;
	}

	private D2DServerInfoModel convertToD2DServerInfoModel(D2DServerInfo info) {
		if (info == null) {
			return null;
		}
		D2DServerInfoModel model = new D2DServerInfoModel();
		// model.setManufacture(info.getManufacture());
		// model.setModel(info.getModel());
		// model.setCpuType(info.getCpuType());
		model.setOsVersion(info.getOsVersion());
		model.setUpTime(info.getUpTime());
		model.setRunningJobs(info.getRunningJobs());
		model.setRestoreUtility(info.isRestoreUtility());
		model.setLicense(info.getLicense().getStatus());
		model.setLicenseCount(info.getLicense().getCount());
		return model;
	}

	@Override
	public JobSummaryModel getJobSummaryInformation(
			ServiceInfoModel serviceInfo, JobSummaryFilterModel filter)
			throws ClientException {
		ILinuximagingService client = getClient(serviceInfo);
		try {

			JobSummary summary = client
					.getJobSummaryInformation(convertToJobSummaryFilter(filter));
			return convertToJobSummaryModel(summary);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private JobSummaryFilter convertToJobSummaryFilter(
			JobSummaryFilterModel model) {
		JobSummaryFilter filter = new JobSummaryFilter();
		filter.setTimeRange(model.getTimeRange());
		filter.setType(model.getType());
		return filter;
	}

	@Override
	public List<JobScriptModel> getBackupJobScriptList(
			ServiceInfoModel serviceInfo) throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			List<JobScript> list = client.getJobScriptList();

			if (list == null) {
				return null;
			}
			List<JobScriptModel> result = new ArrayList<JobScriptModel>();
			for (JobScript script : list) {
				if (script.getJobType() == JobScript.BACKUP) {
					JobScriptModel model = new JobScriptModel();
					model.setJobName(script.getJobName());
					model.setTemplateID(script.getTemplateID());
					model.setJobType(script.getJobType());
					model.setRepeat(script.isRepeat());
					model.setBackupLocation(script.getBackupLocation());
					result.add(model);
				}
			}
			return result;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public int addNodeIntoJobScript(ServiceInfoModel serviceInfo,
			List<NodeModel> nodeList, JobScriptModel job)
			throws ClientException {

		List<BackupTarget> list = new ArrayList<BackupTarget>(nodeList.size());
		for (NodeModel node : nodeList) {
			BackupTarget target = this.convertNodeModel(node);
			list.add(target);
		}
		JobScript script = convertJobScriptModel(job);
		ILinuximagingService client = getClient(serviceInfo);
		try {
			return client.addBackupTargetIntoJobScript(list, script);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	private JobScript convertJobScriptModel(JobScriptModel job) {
		if (job == null) {
			return null;
		}
		JobScript script = new JobScript();
		script.setJobName(job.getJobName());
		script.setTemplateID(job.getTemplateID());
		script.setJobType(job.getJobType());
		script.setRepeat(job.isRepeat());
		script.setBackupLocation(job.getBackupLocation());
		return script;
	}

	public static ServerCapabilityModel convertServerCapability(
			ServerCapability sc) {
		if (sc == null)
			return null;

		ServerCapabilityModel model = new ServerCapabilityModel();
		for (String key : sc.getCapability().keySet()) {
			if (key.equalsIgnoreCase(ServerCapability.KEY_RESTORE_UTIL)) {
				model.setRestoreUtilInstalled(Boolean.parseBoolean(sc
						.getCapability().get(key)));
			} else if (key
					.equalsIgnoreCase(ServerCapability.KEY_RESOTRE_UTIL_RUNNING)) {
				model.setRestoreUtilRunning(Boolean.parseBoolean(sc
						.getCapability().get(key)));
			} else if (key.equalsIgnoreCase(ServerCapability.KEY_D2DSVR_PUBKEY)) {
				model.setPublicKey(Boolean.parseBoolean(sc.getCapability().get(
						key)));
			} else if (key
					.equalsIgnoreCase(ServerCapability.KEY_ENABLE_BACKUP_WHEN_MANAGED_BY_UDP)) {
				model.setEnableBackupWhenManagedByUDP(Boolean.parseBoolean(sc
						.getCapability().get(key)));
			}
		}
		return model;
	}

	@Override
	public ServerCapabilityModel getServerCapability(
			ServiceInfoModel serviceInfo) throws ClientException {
		ILinuximagingService client = getClient(serviceInfo);
		try {
			ServerCapability sc = client.getServerCapability();
			return convertServerCapability(sc);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}

		return null;
	}

	@Override
	public BackupModel getBackupJobScriptByNodeName(
			ServiceInfoModel serviceInfo, String nodeName)
			throws ClientException {
		if (nodeName == null || nodeName.isEmpty()) {
			return null;
		}
		try {
			BackupConfiguration script = getClient(serviceInfo)
					.getBackupJobScriptByNodeName(nodeName);
			if (script != null) {
				BackupTarget target = new BackupTarget();
				target.setName(nodeName);
				script.setTarget(target);
				return convertBackupJobScript(script);
			}
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public String getLicenseText() {
		try {
			return com.ca.arcflash.common.CommonUtil.getLicenseText();
		} catch (Exception e) {
			logger.error("Failed to get license context", e);
		}

		return null;
	}

	@Override
	public int submitStandbyJob(ServiceInfoModel serviceInfo, StandbyModel model)
			throws ClientException {
		try {
			if (model.getStandbyType() == StandbyType.PHYSICAL) {
				return getClient(serviceInfo).submitPhysicalStandbyJob(
						covertToPhysicalStandByJobScript(model));
			} else {
			}
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return 0;
	}

	private PhysicalStandByJobScript covertToPhysicalStandByJobScript(
			StandbyModel model) {
		PhysicalStandByJobScript standByJob = new PhysicalStandByJobScript();
		convertToStandByJobScript(standByJob, model);
		convertPhysicalStandbyList(standByJob, (PhysicalStandbyModel) model);
		return standByJob;
	}

	private void convertToStandByJobScript(PhysicalStandByJobScript standByJob,
			StandbyModel model) {
		standByJob.setAutoStartMachine(model.isAutoStartMachine());
		standByJob.setHeartBeatFrequency(model.getHeartBeatFrequency());
		standByJob.setHeartBeatTime(model.getHeartBeatTime());
		standByJob.setJobName(model.getJobName());
		standByJob.setLocalPath(model.getLocalPath());
		standByJob.setNewPassword(model.getNewPassword());
		standByJob.setNewUsername(model.getNewUsername());
		standByJob.setServerScriptAfterJob(model.getServerScriptAfterJob());
		standByJob.setServerScriptBeforeJob(model.getServerScriptBeforeJob());
		standByJob.setStandbyType(model.getStandbyType().getValue());
		standByJob.setStartMachineMethod(model.getStartMachineMethod());
		standByJob.setTargetScriptAfterJob(model.getTargetScriptAfterJob());
		standByJob.setTargetScriptBeforeJob(model.getTargetScriptBeforeJob());
		standByJob.setTemplateID(model.getTemplateID());
		List<TargetMachineInfo> sourceNodeList = new ArrayList<TargetMachineInfo>();
		for (NodeModel node : model.getSourceNodeList()) {
			sourceNodeList.add(convertToTargetMachineInfo(node));
		}
		standByJob.setSourceNodeList(sourceNodeList);
	}

	private void convertPhysicalStandbyList(
			PhysicalStandByJobScript standbyJob, PhysicalStandbyModel model) {
		List<PhysicalStandbyMachine> standByMachineList = new ArrayList<PhysicalStandbyMachine>();
		for (PhysicalStandbyMachineModel machine : model
				.getStandByMachineList()) {
			standByMachineList.add(covertToPhysicalStandbyMachine(machine));
		}
		standbyJob.setStandByMachineList(standByMachineList);
	}

	private TargetMachineInfo convertToTargetMachineInfo(NodeModel node) {
		TargetMachineInfo machine = new TargetMachineInfo();
		machine.setName(node.getServerName());
		return machine;
	}

	private PhysicalStandbyMachine covertToPhysicalStandbyMachine(
			PhysicalStandbyMachineModel machine) {
		PhysicalStandbyMachine standbyMachine = new PhysicalStandbyMachine();
		standbyMachine.setMacAddress(machine.getMacAddress());
		return standbyMachine;
	}

	@Override
	public StandbyModel getStandbyModel(ServiceInfoModel serviceInfo,
			String jobUUID, StandbyType standbyType) throws ClientException {
		try {
			StandByJobScript standbyJobScript = getClient(serviceInfo)
					.getPhysicalStandbyJob(jobUUID);
			return covertToStandbyModel(standbyJobScript);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private StandbyModel covertToStandbyModel(StandByJobScript standbyJob) {
		StandbyModel standbyModel = null;
		if (standbyJob.getStandbyType() == JobScript.STANDBY_PHYSICAL) {
			standbyModel = new PhysicalStandbyModel();
		} else {

		}
		standbyModel.setAutoStartMachine(standbyJob.isAutoStartMachine());
		standbyModel.setHeartBeatFrequency(standbyJob.getHeartBeatFrequency());
		standbyModel.setHeartBeatTime(standbyJob.getHeartBeatTime());
		standbyModel.setJobName(standbyJob.getJobName());
		standbyModel.setLocalPath(standbyJob.getLocalPath());
		standbyModel.setNewPassword(standbyJob.getNewPassword());
		standbyModel.setNewUsername(standbyJob.getNewUsername());
		standbyModel.setServerScriptAfterJob(standbyJob
				.getServerScriptAfterJob());
		standbyModel.setServerScriptBeforeJob(standbyJob
				.getServerScriptBeforeJob());
		standbyModel.setStandbyType(StandbyType.parse(standbyJob
				.getStandbyType()));
		standbyModel.setStartMachineMethod(standbyJob.getStartMachineMethod());
		standbyModel.setTargetScriptAfterJob(standbyJob
				.getTargetScriptAfterJob());
		standbyModel.setTargetScriptBeforeJob(standbyJob
				.getTargetScriptBeforeJob());
		standbyModel.setTemplateID(standbyJob.getTemplateID());
		List<NodeModel> sourceNodeList = new ArrayList<NodeModel>();
		for (TargetMachineInfo node : standbyJob.getSourceNodeList()) {
			sourceNodeList.add(covertToNodeModel(node));
		}
		standbyModel.setSourceNodeList(sourceNodeList);

		if (standbyJob.getStandbyType() == JobScript.STANDBY_PHYSICAL) {
			List<PhysicalStandbyMachineModel> machineList = new ArrayList<PhysicalStandbyMachineModel>();
			for (PhysicalStandbyMachine machine : ((PhysicalStandByJobScript) standbyJob)
					.getStandByMachineList()) {
				machineList.add(covertToPhysicalStandbyMachineModel(machine));
			}
			((PhysicalStandbyModel) standbyModel)
					.setStandByMachineList(machineList);
		} else {

		}

		return standbyModel;
	}

	private NodeModel covertToNodeModel(TargetMachineInfo node) {
		NodeModel model = new NodeModel();
		model.setServer(node.getName());
		return model;
	}

	private PhysicalStandbyMachineModel covertToPhysicalStandbyMachineModel(
			PhysicalStandbyMachine machine) {
		PhysicalStandbyMachineModel model = new PhysicalStandbyMachineModel();
		model.setMacAddress(machine.getMacAddress());
		return model;
	}

	@Override
	public List<RpsDataStoreModel> findRpsDataStoreByServerInfo(
			ServiceInfoModel serviceInfo, ServerInfoModel serverInfoModel)
			throws ClientException {
		List<RpsDataStoreModel> rpsDsList = new ArrayList<RpsDataStoreModel>();
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.setUser(serverInfoModel.getUserName());
		serverInfo.setPassword(serverInfoModel.getPasswd());
		if (serverInfoModel.getPort() != null) {
			serverInfo.setPort(serverInfoModel.getPort());
		}
		serverInfo.setProtocol(serverInfoModel.getProtocol());
		serverInfo.setName(serverInfoModel.getServerName());
		try {
			List<DataStoreInfo> result = getClient(serviceInfo)
					.findRpsDataStoreByServerInfo(serverInfo);
			for (DataStoreInfo dataIf : result) {
				RpsDataStoreModel rpsds = new RpsDataStoreModel(
						dataIf.getUuid());
				rpsds.setStoreSharedName(dataIf.getName());
				rpsds.setHostName(serverInfoModel.getServerName());
				rpsds.setSharePath(dataIf.getSharePath());
				rpsds.setSharePathUsername(dataIf.getSharePathUsername());
				rpsds.setSharePathPassword(dataIf.getSharePathPassword());
				rpsds.setEnableDedup(dataIf.getEnableDedup());
				if (rpsds.getSharePathUsername() == null
						|| "".equals(rpsds.getSharePathUsername())) {
					rpsds.setSharePathUsername(serverInfoModel.getUserName());
					rpsds.setSharePathPassword(serverInfoModel.getPasswd());
				}
				rpsDsList.add(rpsds);
			}
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			ex.printStackTrace();
			proccessAxisFaultException(ex);
		}
		return rpsDsList;
	}

	@Override
	public int startMigrateData(ServiceInfoModel serviceInfo,
			String sourceHost, JobStatusModel model) throws ClientException {
		try {
			return getClient(serviceInfo).startMigrateData(sourceHost,
					model.getJobUuid());
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public RestoreModel getRestoreJobScriptByNodeName(
			ServiceInfoModel serviceInfo, String nodeName)
			throws ClientException {
		if (nodeName == null || nodeName.isEmpty()) {
			return null;
		}
		try {
			RestoreConfiguration script = getClient(serviceInfo)
					.getRestoreJobScriptByNodeName(nodeName);
			if (script != null) {
				return convertRestoreJobScript(script);
			} else {
				return null;
			}
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public ServerInfoModel getServerInfoFromSession() {
		HttpSession session = this.getThreadLocalRequest().getSession();
		return (ServerInfoModel) session
				.getAttribute(SessionConstants.SERVICE_SERVERINFO);
	}

	@Override
	public ShareFolderModel getSharePathInfoFromSession() {
		HttpSession session = this.getThreadLocalRequest().getSession();
		return (ShareFolderModel) session
				.getAttribute(SessionConstants.SERVICE_SHAREPATH);
	}

	@Override
	public boolean validateRpsServer(ServerInfoModel serverInfoModel)
			throws ClientException {
		try {
			return getClient(null).validateRpsServer(
					convertToServerInfo(serverInfoModel));
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return false;
	}

	@Override
	public List<JobStatusModel> getRestoreJobScriptList(
			ServiceInfoModel serviceInfo, ServerInfoModel serverInfo,
			String uuid) throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			List<RestoreConfiguration> list = client.getRestoreJobList(
					convertToServerInfo(serverInfo), uuid);

			if (list == null) {
				return null;
			}
			List<JobStatusModel> result = new ArrayList<JobStatusModel>();
			for (RestoreConfiguration script : list) {
				if (script.getMachineType() != BackupMachine.TYPE_HBBU_MACHINE
						&& ((script.getJobType() == JobScript.RESTORE_BMR) || (script
								.getJobType() == JobScript.RESTORE_VM))
						&& isInstant(script) && script.isSupportMigration()) {
					JobStatusModel model = new JobStatusModel();
					model.setJobName(script.getJobName());
					model.setJobUuid(script.getUuid());
					model.setJobType(script.getJobType());
					model.setJobMethod(script.getJobMethod());
					model.setNodeName(script.getTargetServer());
					result.add(model);
				}
			}
			return result;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private boolean isInstant(RestoreConfiguration config) {
		boolean instantEnabled = false;
		if (config.getJobType() == JobScript.RESTORE_VM) {
			if (config.getVmRestoreTarget() != null) {
				instantEnabled = config.getVmRestoreTarget().isInstant();
			}
		} else {
			if (config.getRestoreTarget() != null) {
				instantEnabled = config.getRestoreTarget()
						.isEnableInstanceBMR();
			}
		}
		return instantEnabled;
	}

	@Override
	public RestoreModel getRestoreModelByUUID(ServiceInfoModel serviceInfo,
			ServerInfoModel serverInfo, String jobUUID, String sourceJobUUID)
			throws ClientException {
		if (jobUUID == null || jobUUID.isEmpty()) {
			return null;
		}
		try {
			RestoreConfiguration script = getClient(serviceInfo)
					.getRestoreConfigurationByUUID(
							convertToServerInfo(serverInfo), jobUUID,
							sourceJobUUID);
			if (script != null) {
				RestoreModel restoreModel = convertRestoreJobScript(script);
				if (restoreModel.getServerInfoModel() != null) {
					restoreModel.getServerInfoModel().setRestoreJobId(jobUUID);
				}
				return restoreModel;
			} else {
				return null;
			}
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public RecoveryPointModel getRecoveryPointFromSession(
			ServiceInfoModel serviceInfo,
			BackupLocationInfoModel backupLocation,
			RecoveryPointModel recoveryPointModel, String machine)
			throws ClientException {
		try {

			RecoveryPoint recoveryPoint = getClient(serviceInfo)
					.getRecoveryPointFromSession(
							convertToBackupLocationInfo(backupLocation),
							convertRecoveryPointModel(recoveryPointModel, null,
									machine), machine);
			RecoveryPointModel pointModel = convertRecoveryPoint(recoveryPoint,
					JobScript.RESTORE_BMR, machine);
			return pointModel;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			ex.printStackTrace();
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public boolean iscompletedMountRP(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel sessionLocation, String machine,
			RecoveryPointModel pointModel, String scriptUUID)
			throws ClientException {
		try {
			RecoveryPoint point = convertRecoveryPointModel(pointModel, null,
					machine);
			return getClient(serviceInfo).iscompletedMountRP(
					convertToBackupLocationInfo(sessionLocation), machine,
					point, scriptUUID);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			ex.printStackTrace();
			proccessAxisFaultException(ex);
		}
		return false;
	}

	@Override
	public List<String> getLocalIpAndMac() throws ClientException {
		List<String> localIpAndMac = new ArrayList<String>();
		Map<String, List<String>> ipAndMack = com.ca.arcflash.common.CommonUtil
				.getLocalMachineIP();
		for (String key : ipAndMack.keySet()) {
			localIpAndMac.add(key.toUpperCase());
			localIpAndMac.addAll(ipAndMack.get(key));
		}
		return localIpAndMac;
	}

	@Override
	public int controlAutoRestoreData(ServiceInfoModel serviceInfo,
			JobStatusModel model, int type) throws ClientException {
		try {
			return getClient(serviceInfo).controlAutoRestoreData(
					model.getJobUuid(), type);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public boolean validateSharePointUserName(ServiceInfoModel serviceInfo,
			String userName, String uuid) throws ClientException {
		
		try {
			return getClient(serviceInfo).validateSharePointUserName(userName,
					uuid);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return false;
	}

	@Override
	public int querySshKeyInfoForCloudServer(ServiceInfoModel serviceInfo,
			ServerInfoModel serverInfo, String uuid) throws ClientException {
		try {
			return getClient(serviceInfo).querySshKeyInfoForCloudServer(convertToServerInfo(serverInfo), uuid);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return 0;
	}
}
