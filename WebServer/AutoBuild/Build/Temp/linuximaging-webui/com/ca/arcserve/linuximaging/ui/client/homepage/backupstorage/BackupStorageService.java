package com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.exception.ClientException;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupMachineModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("backupstorage")
public interface BackupStorageService extends RemoteService {
	
	int addBackupStorage(ServiceInfoModel serviceInfo,BackupLocationInfoModel locationInfoModel) throws ClientException;
	
	int updateBackupStorage(ServiceInfoModel serviceInfo, BackupLocationInfoModel locationInfoModel) throws ClientException;
	
	int deteleBackupStorage(ServiceInfoModel serviceInfo, String uuid) throws ClientException;
	
	PagingLoadResult<BackupLocationInfoModel> getBackupStorageList(ServiceInfoModel serviceInfo, PagingLoadConfig config) throws ClientException;
	
	List<BackupLocationInfoModel> getAllBackupLocation(ServiceInfoModel serviceInfo) throws ClientException;
	
	PagingLoadResult<BackupMachineModel> getBackupMachineList(ServiceInfoModel serviceInfo,BackupLocationInfoModel locationInfoModel, PagingLoadConfig config) throws ClientException;

}
