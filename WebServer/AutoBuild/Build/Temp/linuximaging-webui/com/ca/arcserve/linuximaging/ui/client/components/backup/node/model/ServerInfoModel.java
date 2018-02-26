package com.ca.arcserve.linuximaging.ui.client.components.backup.node.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ServerInfoModel extends BaseModelData {
	public final static String ROOT="root";
	public final static String LEAF="leaf";
	/**
	 * 
	 */
	private static final long serialVersionUID = -7608043316616178465L;
	
	public void setServerName(String serverName) {
		set("server", serverName);
	}
	public String getServerName() {
		return get("server");
	}
	public void setUserName(String userName) {
		set("user", userName);
	}
	public String getUserName() {
		return get("user");
	}
	public void setPasswd(String passwd) {
		set("passwd", passwd);
	}
	public String getPasswd() {
		return get("passwd");
	}
	public void setType(String type){
		set("type",type);
	}
	public String getType(){
		return get("type");
	}
	

}
