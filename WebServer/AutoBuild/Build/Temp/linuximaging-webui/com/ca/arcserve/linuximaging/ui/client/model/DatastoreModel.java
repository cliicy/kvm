package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DatastoreModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6106902258207120941L;
	
	public void setDisplayName(String displayName){
		set("displayName",displayName);
	}
	
	public String getDisplayName(){
		return get("displayName");
	}
	
	public void setId(String id){
		set("id",id);
	}
	
	public String getId(){
		return get("id");
	}
	
	public void setUuid(String uuid) {
		set("uuid", uuid);
	}

	public String getUuid() {
		return get("uuid");
	}

	public void setSharePath(String sharePath) {
		set("sharePath", sharePath);
	}

	public String getSharePath() {
		return get("sharePath");
	}

	public void setSharePathUsername(String sharePathUsername) {
		set("sharePathUsername", sharePathUsername);
	}

	public String getSharePathUsername() {
		return get("sharePathUsername");
	}

	public void setSharePathPassword(String sharePathPassword) {
		set("sharePathPassword", sharePathPassword);
	}

	public String getSharePathPassword() {
		return get("sharePathPassword");
	}

}
