package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ServiceInfoModel extends BaseModelData {
	public final static String ROOT="root";
	public final static String LOCAL_SERVER="localServer";
	public final static String D2D_SERVER="d2dServer";
	/**
	 * 
	 */
	private static final long serialVersionUID = -7608043316616178465L;
	
	public ServiceInfoModel() {
		setType(D2D_SERVER);
		setProtocol(null);
		setServer(null);
		setPort(null);
	}
	
	public ServiceInfoModel(String protocol, String server, Integer port) {
		setType(D2D_SERVER);
		setProtocol(protocol);
		setServer(server);
		setPort(port);
	}
	public void setType(String type){
		set("type",type);
	}
	public String getType(){
		return get("type");
	}
	public void setProtocol(String protocol)
	{
		set("protocol", protocol);
	}
	public String getProtocol()
	{
		return get("protocol");
	}	
	public void setServer(String server)
	{
		set("server", server);
	}
	public String getServer()
	{
		return get("server");
	}
	public void setPort(Integer port)
	{
		set("port", port);
	}
	public Integer getPort()
	{
		return get("port");
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
	
	public void setId(Integer id){
		set("id",id);
	}
	
	public Integer getId(){
		return get("id");
	}
	public void setAuthKey(String authKey){
		set("authKey",authKey);
	}
	public String getAuthKey(){
		return get("authKey");
	}
}
