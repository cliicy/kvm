package com.ca.arcserve.linuximaging.ui.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import com.ca.arcserve.linuximaging.ui.client.exception.ClientException;
import com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage.BackupStorageService;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupMachineModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;
import com.ca.arcserve.linuximaging.webservice.data.PagingConfig;
import com.ca.arcserve.linuximaging.webservice.data.PagingResult;
import com.ca.arcserve.linuximaging.webservice.data.ServerInfo;
import com.ca.arcserve.linuximaging.webservice.data.backup.DataStoreInfo;
import com.ca.arcserve.linuximaging.webservice.data.restore.BackupMachine;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class BackupStorageServiceImpl extends BaseServiceImpl implements
		BackupStorageService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1469553602196946158L;

	@Override
	public int addBackupStorage(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel locationInfoModel) throws ClientException {
		try {
			return getClient(serviceInfo).addBackupLocation(
					convertToBackupLocationInfo(locationInfoModel));
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public int updateBackupStorage(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel locationInfoModel) throws ClientException {
		try {
			return getClient(serviceInfo).updateBackupLocation(
					convertToBackupLocationInfo(locationInfoModel));
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public int deteleBackupStorage(ServiceInfoModel serviceInfo, String uuid)
			throws ClientException {
		try {
			return getClient(serviceInfo).deleteBackupLocation(uuid);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	@Override
	public PagingLoadResult<BackupLocationInfoModel> getBackupStorageList(
			ServiceInfoModel serviceInfo, PagingLoadConfig configModel)
			throws ClientException {
		try {
			PagingConfig config = new PagingConfig();
			config.setStartIndex(configModel.getOffset());
			config.setCount(configModel.getLimit());
			PagingResult<BackupLocationInfo> result = getClient(serviceInfo)
					.getBackupLocationList(config);
			List<BackupLocationInfoModel> list = new ArrayList<BackupLocationInfoModel>();
			if (result != null && result.getData() != null
					&& result.getData().size() > 0) {
				Map<String,Integer> nameMap = new HashMap<String,Integer>();
				for (BackupLocationInfo model : result.getData()) {
					list.add(ServerConvertUtils.convertToBackupLocationInfoModel(model,nameMap));
				}
				return new BasePagingLoadResult<BackupLocationInfoModel>(list,
						result.getStartIndex(), result.getTotalCount());
			}
			return new BasePagingLoadResult<BackupLocationInfoModel>(list, 0, 0);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private BackupLocationInfo convertToBackupLocationInfo(
			BackupLocationInfoModel model) {
		if (model == null)
			return null;

		BackupLocationInfo info = new BackupLocationInfo();
		DataStoreInfo dataStoreInfo = new DataStoreInfo();
		ServerInfo serverInfo = new ServerInfo();
		info.setBackupDestLocation(model.getSessionLocation());
		info.setBackupDestUser(model.getUser());
		info.setBackupDestPasswd(model.getPassword());
		info.setUuid(model.getUUID());
		info.setJobLimit(model.getJobLimit());
		info.setDataStoreInfo(dataStoreInfo);
		info.setServerInfo(serverInfo);
		if (model.getType() != null)
			info.setType(model.getType());
		if (model.isRunScript()) {
			info.setRunScript(model.isRunScript());
			info.setScript(model.getScript());
			info.setFreeSizeAlert(model.getFreeSizeAlert());
			info.setFreeSizeAlertUnit(model.getFreeSizeAlertUnit());
		}
		serverInfo.setName(model.getServerInfoModel().getServerName());
		serverInfo.setUser(model.getServerInfoModel().getUserName());
		serverInfo.setPassword(model.getServerInfoModel().getPasswd());
		if (model.getServerInfoModel().getPort() != null) {
			serverInfo.setPort((model.getServerInfoModel().getPort()));
		}
		serverInfo.setProtocol(model.getServerInfoModel().getProtocol());
		dataStoreInfo.setUuid(model.getDatastoreModel().getUuid());
		dataStoreInfo.setName(model.getDatastoreModel().getDisplayName());
		dataStoreInfo.setSharePath(info.getBackupDestLocation());
		dataStoreInfo.setSharePathUsername(info.getBackupDestUser());
		dataStoreInfo.setSharePathPassword(info.getBackupDestPasswd());
		if (model.getEnableDedup() != null) {
			dataStoreInfo.setEnableDedup(model.getEnableDedup());
		}
		info.setS3CifsShareUser(model.getS3CifsShareUsername());
		info.setS3CifsSharePassword(model.getS3CifsSharePassword());
		if(model.getS3CifsSharePort() != null){
			info.setS3CifsSharePort(model.getS3CifsSharePort());
		}
		if(model.isEnableS3CifsShare() != null){
			info.setEnableS3CifsShare(model.isEnableS3CifsShare());
		}
		return info;
	}

	

	@Override
	public List<BackupLocationInfoModel> getAllBackupLocation(
			ServiceInfoModel serviceInfo) throws ClientException {
		try {
			List<BackupLocationInfo> locationList = getClient(serviceInfo)
					.getAllBackupLocation();
			if (locationList != null) {
				List<BackupLocationInfoModel> modelList = new ArrayList<BackupLocationInfoModel>();
				Map<String,Integer> nameMap = new HashMap<String,Integer>();
				for (BackupLocationInfo info : locationList) {
					modelList.add(ServerConvertUtils.convertToBackupLocationInfoModel(info,nameMap));
				}
				return modelList;
			}
			return null;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public PagingLoadResult<BackupMachineModel> getBackupMachineList(
			ServiceInfoModel serviceInfo,
			BackupLocationInfoModel locationInfoModel,
			PagingLoadConfig configModel) throws ClientException {
		try {
			PagingConfig config = new PagingConfig();
			config.setStartIndex(configModel.getOffset());
			config.setCount(configModel.getLimit());
			BackupLocationInfo location = convertToBackupLocationInfo(locationInfoModel);
			PagingResult<BackupMachine> result = getClient(serviceInfo)
					.getBackupMachineList(location, config);
			List<BackupMachineModel> list = new ArrayList<BackupMachineModel>();
			if (result != null && result.getData() != null
					&& result.getData().size() > 0) {
				Map<String, Integer> machineNameMap = new HashMap<String, Integer>();
				for (BackupMachine model : result.getData()) {
					BackupMachineModel backupMachine = convertToBackupMachineModel(model);
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
					list.add(backupMachine);
				}
				return new BasePagingLoadResult<BackupMachineModel>(list,
						result.getStartIndex(), result.getTotalCount());
			}
			return new BasePagingLoadResult<BackupMachineModel>(list, 0, 0);
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
		String[] machineName = machine.getMachineName().split("\\[");
		model.setMachineName(machineName[0]);
		model.setMachinePath(machine.getMachineName());
		model.setMachineType(machine.getMachineType());
		model.setFirstDate(machine.getFirstDate());
		model.setLastDate(machine.getLastDate());
		model.setRecoveryPointCount(machine.getRecoveryPointCount());
		model.setRecoveryPointSize(machine.getRecoveryPointSize());
		model.setRecoverySetCount(machine.getRecoverySetCount());
		return model;
	}

}
