package com.ca.arcserve.linuximaging.ui.client.restore.search.model;

import com.extjs.gxt.ui.client.data.BaseModel;

public class VolResultModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3028630399932213666L;

	public String getGuid() {
		return (String) get("guid");
	}

	public void setGuid(String guid) {
		set("guid", guid);
	}

	public String getHost() {
		return (String) get("host");
	}

	public void setHost(String host) {
		set("host", host);
	}

	public String getServer() {
		return (String) get("server");
	}

	public void setServer(String server) {
		set("server", server);
	}

	public String getSessid() {
		return (String) get("sessid");
	}

	public void setSessid(String sessid) {
		set("sessid", sessid);
	}

}
