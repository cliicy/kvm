package com.ca.arcserve.linuximaging.ui.client.license;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.model.license.LicenseStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.license.LicensedMachineModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LicenseManageServiceAsync {

	void getComponentStatusList(AsyncCallback<List<LicenseStatusModel>> callback);

	void addLicenseKey(String key, AsyncCallback<Void> callback);

	void unBindLicenses(String componentid, List<LicensedMachineModel> machines, AsyncCallback<LicenseStatusModel> callback);

	void getMachineList(PagingLoadConfig config, String componentid, AsyncCallback<PagingLoadResult<LicensedMachineModel>> callback);

}
