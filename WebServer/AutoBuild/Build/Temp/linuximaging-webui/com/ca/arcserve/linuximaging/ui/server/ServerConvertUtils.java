package com.ca.arcserve.linuximaging.ui.server;

import java.util.Map;

import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.DatastoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;

public class ServerConvertUtils {

	public static BackupLocationInfoModel convertToBackupLocationInfoModel(
			BackupLocationInfo info, Map<String, Integer> nameMap) {
		if (info == null)
			return null;
		BackupLocationInfoModel model = new BackupLocationInfoModel();
		model.setSessionLocation(info.getBackupDestLocation());
		model.setDisplayName(info.getBackupDestLocation());
		String displayName = "";
		if (info.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
			displayName = info.getServerInfo().getName() + ":"
					+ info.getDataStoreInfo().getName();
		} else {
			displayName = info.getBackupDestLocation();
		}
		if (nameMap != null) {
			if (nameMap.get(displayName) == null) {
				nameMap.put(displayName, 1);
			} else {
				int value = nameMap.get(displayName);
				nameMap.put(displayName, value + 1);
				displayName = displayName + "(" + value + ")";
			}
		}
		model.setDisplayName(displayName);
		ServerInfoModel serverInfoModel = new ServerInfoModel();
		if (info.getServerInfo() != null) {
			serverInfoModel.setServerName(info.getServerInfo().getName());
			serverInfoModel.setUserName(info.getServerInfo().getUser());
			serverInfoModel.setPasswd(info.getServerInfo().getPassword());
			serverInfoModel.setProtocol(info.getServerInfo().getProtocol());
			serverInfoModel.setPort(info.getServerInfo().getPort());
		}
		DatastoreModel datastoreModel = new DatastoreModel();
		if (info.getDataStoreInfo() != null) {
			datastoreModel.setDisplayName(info.getDataStoreInfo().getName());
			datastoreModel.setUuid(info.getDataStoreInfo().getUuid());
			datastoreModel.setSharePath(info.getDataStoreInfo().getSharePath());
			datastoreModel.setSharePathUsername(info.getDataStoreInfo()
					.getSharePathUsername());
			datastoreModel.setSharePathPassword(info.getDataStoreInfo()
					.getSharePathPassword());
			model.setEnableDedup(info.getDataStoreInfo().getEnableDedup());
		}
		model.setDatastoreModel(datastoreModel);
		model.setServerInfoModel(serverInfoModel);

		model.setUser(info.getBackupDestUser());
		model.setPassword(info.getBackupDestPasswd());
		model.setType(info.getType());
		model.setFreeSize(info.getFreeSize());
		model.setTotalSize(info.getTotalSize());
		model.setRunScript(info.isRunScript());
		model.setScript(info.getScript());
		model.setFreeSizeAlert(info.getFreeSizeAlert());
		model.setFreeSizeAlertUnit(info.getFreeSizeAlertUnit());
		model.setUUID(info.getUuid());
		model.setJobLimit(info.getJobLimit());
		model.setCurrentJobCount(info.getCurrentJobCount());
		model.setWaitingJobCount(info.getWaitingJobCount());
		model.setS3CifsSharePort(info.getS3CifsSharePort());
		model.setS3CifsShareUsername(info.getS3CifsShareUser());
		model.setS3CifsSharePassword(info.getS3CifsSharePassword());
		model.setEnableS3CifsShare(info.isEnableS3CifsShare());
		return model;
	}

	public static BackupLocationInfoModel convertToBackupLocationInfoModel(
			BackupLocationInfo info) {
		return convertToBackupLocationInfoModel(info, null);
	}

}
