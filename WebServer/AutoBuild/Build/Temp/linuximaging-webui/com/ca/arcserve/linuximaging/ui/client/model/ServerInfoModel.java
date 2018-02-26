package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ServerInfoModel extends BaseModelData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7608043316616178465L;
	
	public final static int SERVER_TYPE_D2D_SERVER = 0;
	
	public final static int SERVER_TYPE_OOLONG = 1;
	
	public ServerInfoModel(){
	}
	
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
	public void setProtocol(String protocol){
		set("protocol",protocol);
	}
	
	public String getProtocol(){
		return get("protocol");
	}
	
	public void setPort(Integer port){
		set("port",port);
	}
	
	public Integer getPort(){
		return get("port");
	}
	
	public void setId(Integer id){
		set("id",id);
	}
	
	public Integer getId(){
		return get("id");
	}
	
	public void setRestoreJobId(String restoreJobId){
		set("restoreJobId",restoreJobId);
	}
	
	public String getRestoreJobId(){
		return get("restoreJobId");
	}
	
	public void setServerType(Integer serverType){
		set("serverType",serverType);
	}
	
	public Integer getServerType(){
		return get("serverType");
	}
	
	public void setLocal(Boolean isLocal){
		set("isLocal",isLocal);
	}
	
	public Boolean getIsLocal(){
		return get("isLocal");
	}
}
