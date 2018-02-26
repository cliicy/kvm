package com.ca.arcserve.linuximaging.ui.client.restore.search.model;

import java.util.Date;

import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointModel;
import com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint;
import com.extjs.gxt.ui.client.data.BaseModel;

public class SearchResultModel extends BaseModel {

	private static final long serialVersionUID = -7144600947180117627L;

	private RecoveryPointModel point;
	
	public String getName() {
		return (String) get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public Long getSize() {
		return (Long) get("size");
	}

	public void setSize(Long size) {
		set("size", size);
	}

	public Date getModifyDate() {
		return (Date) get("modifyDate");
	}

	public void setModifyDate(Date modifyDate) {
		set("modifyDate", modifyDate);
	}

	public Boolean getIsDir() {
		return (Boolean) get("type");
	}

	public void setIsDir(Boolean type) {
		set("type", type);
	}

	public String getFullName() {
		return (String) get("fullName");
	}

	public void setFullName(String fullName) {
		set("fullName", fullName);
	}
	
	public String getSessionid() {
		return (String) get("sessionid");
	}

	public void setSessionid(String sessionid) {
		set("sessionid", sessionid);
	}

	public RecoveryPointModel getPoint() {
		return point;
	}

	public void setPoint(RecoveryPointModel point) {
		this.point = point;
	}
	
	public void setMachine(String machine) {
		set("machine", machine);
	}

	public String getMachine() {
		return get("machine");
	}

	public void setBackupLocation(String backupLocation) {
		set("backupLocation", backupLocation);
	}

	public String getBackupLocation() {
		return get("backupLocation");
	}
}