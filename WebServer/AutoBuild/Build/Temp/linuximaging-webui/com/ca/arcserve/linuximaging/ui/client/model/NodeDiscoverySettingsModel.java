package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class NodeDiscoverySettingsModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8200756786199089699L;

	
	public void setScript(String script){
		set("script",script);
	}
	
	public String getScript(){
		return get("script");
	}
	
	public BackupScheduleModel schedule;
}
