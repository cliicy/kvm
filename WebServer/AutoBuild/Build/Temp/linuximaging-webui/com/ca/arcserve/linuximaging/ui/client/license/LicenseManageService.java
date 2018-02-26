package com.ca.arcserve.linuximaging.ui.client.license;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.exception.ClientException;
import com.ca.arcserve.linuximaging.ui.client.model.license.LicenseStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.license.LicensedMachineModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("license")
public interface LicenseManageService extends RemoteService {

	List<LicenseStatusModel> getComponentStatusList() throws ClientException;

/**
 * @param key String like OOOOO-SSSSS-AAAAA-EEEEE-PPPPP
 * @throws ClientException
 */
	void addLicenseKey(String key) throws ClientException;

	LicenseStatusModel unBindLicenses(String componentid, List<LicensedMachineModel> machines)  throws ClientException ;
	
	PagingLoadResult<LicensedMachineModel> getMachineList(PagingLoadConfig config, String componentid) throws ClientException;
}
