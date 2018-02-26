package com.ca.arcserve.linuximaging.ui.client.model.license;


import com.extjs.gxt.ui.client.data.BaseModelData;

public class LicensedMachineModel extends BaseModelData{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LicensedMachineModel() {
		super();
		this.setHostname(null);
		this.setD2dserver(null);
		this.setVM(Boolean.TRUE);
		this.setSocket(0);
	}
	public String getHostname() {
		return get("hostname");
	}
	public void setHostname(String hostname) {
		set("hostname", hostname);
	}
	public String getD2dserver() {
		return get("d2dserver");
	}
	public void setD2dserver(String d2dserver) {
		set("d2dserver", d2dserver);
	}
	public Boolean getVM() {
		return get("vm");
	}
	public void setVM(Boolean vm) {
		set("vm", vm);
	}
	public Integer getSocket() {
		return get("socket");
	}
	public void setSocket(Integer socket) {
		set("socket", socket);
	}
}
