package com.ca.arcserve.linuximaging.ui.server;

import com.ca.arcserve.linuximaging.ui.client.components.configuration.ConfigurationService;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.BackupTemplateModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.server.BaseServiceImpl;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceFactory;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;
import com.ca.arcserve.linuximaging.webservice.data.JobScript;

public class ConfigurationServiceImpl extends BaseServiceImpl implements ConfigurationService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2198703288677375627L;

	@Override
	public BackupTemplateModel[] getBackupTemplateList() {
		ILinuximagingService client = getClient();
		JobScript[] templates=null;
//		JobScript[] templates=client.getBackupTemplateList();
		BackupTemplateModel[] models=new BackupTemplateModel[templates.length];
		for(int i=0;i<templates.length;i++){
			models[i]=convertJobScript(templates[i]);
		}
		return models;
//		BackupTemplateModel[] models=new BackupTemplateModel[10];
//		for(int i=0;i<10;i++){
//			BackupTemplateModel model=new BackupTemplateModel();
//			model.setName("T"+i);
//			model.setDescription("do backup job for S"+i);
//			model.setSessionLocation("//storage server"+i+"/session");
//			models[i]=model;
//		}
//		return models;
	}

	public static BackupTemplateModel convertJobScript(
			JobScript data) {
		BackupTemplateModel model=new BackupTemplateModel();
		model.setName(data.getJobName());
		BackupLocationInfoModel backupLocationInfoModel = new BackupLocationInfoModel();
		backupLocationInfoModel.setSessionLocation(data.getBackupLocationInfo().getBackupDestLocation());
		backupLocationInfoModel.setUser(data.getBackupLocationInfo().getBackupDestUser());
		backupLocationInfoModel.setPassword(data.getBackupLocationInfo().getBackupDestPasswd());
		model.backupLocationInfoModel = backupLocationInfoModel;
		model.setEncryptionName(data.getEncryptAlgoName());
		model.setDescription("description");
		return model;
	}
	public static JobScript convertBackupTemplateModel(BackupTemplateModel model){
		JobScript result=new JobScript();
		result.setJobName(model.getName());
		BackupLocationInfo backupLocationInfo = new BackupLocationInfo();
		backupLocationInfo.setBackupDestLocation(model.backupLocationInfoModel.getSessionLocation());
		backupLocationInfo.setBackupDestUser(model.backupLocationInfoModel.getUser());
		backupLocationInfo.setBackupDestPasswd(model.backupLocationInfoModel.getPassword());
		result.setBackupLocationInfo(backupLocationInfo);
//		int compressLevel=convertCompress(model.getCompression());
		result.setCompressLevel(model.getCompression());
		result.setEncryptAlgoName(model.getEncryptionName());
		result.setEncryptPasswd(model.getEncryptionPassword());
		return result;
	}

	@Override
	public Boolean addBackupTemplate(BackupTemplateModel model) {
		
		JobScript data=convertBackupTemplateModel(model);
		ILinuximagingService client = getClient();
//		return client.addBackupTemplate(data);
		return true;
	}

	private ILinuximagingService getClient() {
		BaseWebServiceClientProxy proxy= new BaseWebServiceFactory().getLinuxImagingWebService("http", "localhost", 8014);
		ILinuximagingService client =(ILinuximagingService)proxy.getService();
		return client;
	}

	@Override
	public Boolean deleltBackupTemplate(String[] names) {
		ILinuximagingService client = getClient();
//		return client.deleltBackupTemplate(names);
		return true;
	}
	
	
	private static int convertCompress(String compression) {
		if("No Compression".equalsIgnoreCase(compression)){
			return 0;
		}else if("Standard Compression".equalsIgnoreCase(compression)){
			return 1;
		}else if("Maximum Compression".equalsIgnoreCase(compression)){
			return 2;
		}
		return 0;
	}

}
