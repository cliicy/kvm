package com.ca.arcserve.linuximaging.ui.client.components.configuration;

import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.BackupTemplateModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("configuration")
public interface ConfigurationService extends RemoteService {
	public BackupTemplateModel[] getBackupTemplateList();

	public Boolean addBackupTemplate(BackupTemplateModel model);

	public Boolean deleltBackupTemplate(String[] names);
	
}
