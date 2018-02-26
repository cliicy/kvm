package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RpsDataStoreModel extends BaseModelData {

	private static final long serialVersionUID = 2222L;

	public RpsDataStoreModel() {
	};

	public RpsDataStoreModel(String uuid) {
		set("uuid", uuid);
	}

	public String getUuid() {
		return get("uuid");
	}

	public String getStoreSharedName() {
		return get("storeSharedName");
	}

	public void setStoreSharedName(String storeSharedName) {
		set("storeSharedName", storeSharedName);
	}

	public int getEnableDedup() {
		return get("enableDedupe");
	}

	public void setEnableDedup(int enableDedup) {
		set("enableDedupe", enableDedup);
	}
	
	public String getHostName() {
		return get("hostName");
	}

	public void setHostName(String hostName) {
		set("hostName", hostName);
	}

	public String getSharePath() {
		return get("sharePath");
	}

	public void setSharePath(String sharePath) {
		set("sharePath", sharePath);
	}

	public String getSharePathUsername() {
		return get("sharePathUsername");
	}

	public void setSharePathUsername(String sharePathUsername) {
		set("sharePathUsername", sharePathUsername);
	}

	public String getSharePathPassword() {
		return get("sharePathPassword");
	}

	public void setSharePathPassword(String sharePathPassword) {
		set("sharePathPassword", sharePathPassword);
	}

}
