package com.ca.arcserve.linuximaging.ui.client.components.configuration;

import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.BackupTemplateModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>BackupConfigurationService</code>.
 */
public interface ConfigurationServiceAsync {
	void getBackupTemplateList(AsyncCallback<BackupTemplateModel[]> callback);

	void addBackupTemplate(BackupTemplateModel model, AsyncCallback<Boolean> callback);

	void deleltBackupTemplate(String[] names, AsyncCallback<Boolean> callback);
}
