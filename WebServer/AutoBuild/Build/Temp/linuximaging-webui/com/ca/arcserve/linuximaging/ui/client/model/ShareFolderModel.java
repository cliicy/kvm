package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ShareFolderModel extends BaseModelData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7608043316616178465L;

	public final static int SERVER_TYPE_D2D_SERVER = 0;

	public final static int SERVER_TYPE_OOLONG = 1;

	public ShareFolderModel() {
	}

	public void setPath(String path) {
		set("path", path);
	}

	public String getPath() {
		return get("path");
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

	public void setNfs(String isNfs) {
		set("isNfs", isNfs);
	}

	public String isNfs() {
		return get("isNfs");
	}
}
