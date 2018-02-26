package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RegistrationModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getName() {
		return (String) get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getCompany() {
		return (String) get("company");
	}

	public void setCompany(String company) {
		set("company", company);
	}

	public String getContactNumber() {
		return (String) get("contactNumber");
	}

	public void setContactNumber(String contactNumber) {
		set("contactNumber", contactNumber);
	}

	public String getEmailID() {
		return (String) get("emailID");
	}

	public void setEmailID(String emailID) {
		set("emailID", emailID);
	}

	public String getNetSuiteId() {
		return (String) get("netSuiteId");
	}

	public void setNetSuiteId(String netSuiteId) {
		set("netSuiteId", netSuiteId);
	}

	public String isActivate() {
		return (String) get("activate");
	}

	public void setActivate(String activate) {
		set("activate", activate);
	}
}
