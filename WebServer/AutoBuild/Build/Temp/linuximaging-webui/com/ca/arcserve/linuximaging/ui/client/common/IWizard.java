package com.ca.arcserve.linuximaging.ui.client.common;

import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;

public interface IWizard {
	
	public ServiceInfoModel getD2DServerInfo();
	
	public void showNextSettings();
	
	public void resume();
	
	public void maskUI(String message);
	
	public void unmaskUI();

}
