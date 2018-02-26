package com.ca.arcserve.linuximaging.ui.server;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import com.ca.arcserve.linuximaging.ui.client.exception.ClientException;
import com.ca.arcserve.linuximaging.ui.client.license.LicenseManageService;
import com.ca.arcserve.linuximaging.ui.client.model.license.LicenseStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.license.LicensedMachineModel;
import com.ca.arcserve.linuximaging.webservice.data.PagingConfig;
import com.ca.arcserve.linuximaging.webservice.data.PagingResult;
import com.ca.arcserve.linuximaging.webservice.data.license.LicenseResult.LicenseComponent;
import com.ca.arcserve.linuximaging.webservice.data.license.LicenseStatusInformation;
import com.ca.arcserve.linuximaging.webservice.data.license.LicenseUtil;
import com.ca.arcserve.linuximaging.webservice.data.license.LicensedMachine;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;


public class LicenseManageServiceImpl extends BaseServiceImpl implements LicenseManageService {
	private static final long serialVersionUID = 1577800332426475257L;

	@Override
	public List<LicenseStatusModel> getComponentStatusList()  throws ClientException {
		try {
			List<LicenseStatusModel> result = new ArrayList<LicenseStatusModel>();
			List<LicenseStatusInformation> list = getClient(null).getComponentStatusList();
			if (list != null) {
				for(LicenseStatusInformation info : list){
					LicenseStatusModel model=convertLicenseStatusInformation(info);
					result.add(model);
				}
			}
			return result;
		}catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	private LicenseStatusModel convertLicenseStatusInformation(LicenseStatusInformation info) {
		if(info==null){
			return null;
		}
		LicenseStatusModel model = new LicenseStatusModel();
		model.setComponentID(info.getComponentID());
		model.setComponentName(info.getComponentName());
//		for(LicensedMachine machine: info.getMachines()){
//			model.getMachines().add(convertLicensedMachine(machine));
//		}
		model.setActiveLicenseCount(info.getActiveLicenseCount());
		model.setAvailableLicenseCount(info.getAvailableLicenseCount());
		model.setNeededLicenseCount(info.getNeededLicenseCount());
		model.setTotalLicenseCount(info.getTotalLicenseCount());
		model.setVersion(info.getVersion());
		
		model.setComponentGroup(LicenseUtil.getComponentGroup(info.getComponentID()));
		return model;
	}
	
	public boolean isManageCapacity(LicenseStatusInformation info) {
		/*String componentID = info.getComponentID();
		if(componentID==null){
			return false;
		}else if (componentID.equals(LicenseComponent.Managed_Capacity_Standard.getCode())
				|| componentID.equals(LicenseComponent.Managed_Capacity_Advanced.getCode())
				|| componentID.equals(LicenseComponent.Managed_Capacity_Premium.getCode())
				|| componentID.equals(LicenseComponent.Managed_Capacity_Premium_Plus.getCode())) {
			return true;
		} else {*/
			return false;
		//}
	}

	private LicensedMachineModel convertLicensedMachine(LicensedMachine machine) {
		if(machine==null){
			return null;
		}
		LicensedMachineModel model = new LicensedMachineModel();
		model.setHostname(machine.getHostname());
		model.setD2dserver(machine.getD2dserver());
		model.setVM((machine.getMachineType()&LicensedMachine.TYPE_PLATFORM_MASK)==LicensedMachine.TYPE_VM);
		model.setSocket(machine.getSocketNumber());
		return model;
	}

	@Override
	public void addLicenseKey(String key) throws ClientException {
		try {
			getClient(null).addLicenseKey(key);
		}catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
	}

	@Override
	public LicenseStatusModel unBindLicenses(String componentid, List<LicensedMachineModel> machines)  throws ClientException {
		try {
			if(componentid==null||machines==null)
				return null;
			List<LicensedMachine> list = new ArrayList<LicensedMachine>(machines.size());
			for(LicensedMachineModel machine : machines){
				list.add(convertLicensedMachineModel(machine));
			}
			LicenseStatusInformation license = getClient(null).unBindLicenses(componentid, list);
			return convertLicenseStatusInformation(license);
		}catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;

	}

//	private LicenseStatusInformation convertLicenseStatusModel(LicenseStatusModel model) {
//		if(model==null){
//			return null;
//		}
//		LicenseStatusInformation info = new LicenseStatusInformation();
//		info.setCompoentID(model.getCompoentID());
//		info.setComponentName(model.getComponentName());
//		for(LicensedMachineModel machine: model.getMachines()){
//			info.getMachines().add(convertLicensedMachineModel(machine));
//		}
//		info.setActiveLicenseCount(model.getActiveLicenseCount());
//		info.setAvailableLicenseCount(model.getAvailableLicenseCount());
//		info.setNeededLicenseCount(model.getNeededLicenseCount());
//		info.setTotalLicenseCount(model.getTotalLicenseCount());
//		info.setVersion(model.getVersion());
//		return info;
//	}

	private LicensedMachine convertLicensedMachineModel(LicensedMachineModel model) {
		if(model==null){
			return null;
		}
		LicensedMachine machine = new LicensedMachine();
		machine.setHostname(model.getHostname());
		machine.setD2dserver(model.getD2dserver());
		return machine;
	}

	@Override
	public PagingLoadResult<LicensedMachineModel> getMachineList(PagingLoadConfig configModel, String componentid) throws ClientException {
		try{
			PagingConfig config = new PagingConfig();
			config.setStartIndex(configModel.getOffset());
			config.setCount(configModel.getLimit());
			PagingResult<LicensedMachine> result = getClient(null).getLicensedMachinePagingList(config, componentid);
			List<LicensedMachineModel> modelList = new ArrayList<LicensedMachineModel>();
			if ( result != null && result.getData() != null && result.getData().size() > 0 )
			{
				for (LicensedMachine target : result.getData())
				{
					LicensedMachineModel model = convertLicensedMachine(target);
					if (model != null)
					{
						modelList.add(model);
					}
				}
				return new BasePagingLoadResult<LicensedMachineModel>(modelList, result.getStartIndex(), result.getTotalCount());
			}
			
			return new BasePagingLoadResult<LicensedMachineModel>(modelList, 0, 0);
		}catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

}
