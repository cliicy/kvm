package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VirtualizationServerModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7900926343439710780L;
	
	public void setVirtualizationServerName(String serverName){
		set("serverName",serverName);
	}
	
	public String getVirtualizationServerName(){
		return get("serverName");
	}
	
	public void setVirtualizationServerUsername(String username){
		set("username",username);
	}
	
	public String getVirtualizationServerUsername(){
		return get("username");
	}
	
	public void setVirtualizationServerPassword(String password){
		set("password",password);
	}
	
	public String getVirtualizationServerPassword(){
		return get("password");
	} 
	
	public Integer getPort(){
		return get("port");
	}
	
	public void setPort(Integer port){
		set("port",port);
	}
	
	public Integer getProtocol(){
		return get("protocol");
	}
	
	public void setProtocol(Integer protocol){
		set("protocol",protocol);
	}
	
	public Integer getServerType(){
		return get("serverType");
	}
	
	public void setServerType(Integer serverType){
		set("serverType",serverType);
	}

}
