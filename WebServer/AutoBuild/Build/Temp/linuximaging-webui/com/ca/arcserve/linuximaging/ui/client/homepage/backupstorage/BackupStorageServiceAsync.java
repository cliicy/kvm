package com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupMachineModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BackupStorageServiceAsync {

	void addBackupStorage(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel locationInfoModel,
			AsyncCallback<Integer> callback);

	void updateBackupStorage(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel locationInfoModel,
			AsyncCallback<Integer> callback);

	void deteleBackupStorage(ServiceInfoModel serviceInfo, String uuid,
			AsyncCallback<Integer> callback);

	void getBackupStorageList(ServiceInfoModel serviceInfo,
			PagingLoadConfig config,
			AsyncCallback<PagingLoadResult<BackupLocationInfoModel>> callback);

	void getAllBackupLocation(ServiceInfoModel serviceInfo,
			AsyncCallback<List<BackupLocationInfoModel>> callback);

	void getBackupMachineList(ServiceInfoModel serviceInfo,
			BackupLocationInfoModel locationInfoModel, PagingLoadConfig config,
			AsyncCallback<PagingLoadResult<BackupMachineModel>> callback);

}
