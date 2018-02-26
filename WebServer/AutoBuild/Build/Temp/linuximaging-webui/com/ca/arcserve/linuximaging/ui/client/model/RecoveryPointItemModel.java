package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RecoveryPointItemModel extends BaseModelData {

	private static final long serialVersionUID = 193468570951869432L;

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getDiskSignature() {
		return get("diskSignature");
	}

	public void setDiskSignature(String diskSignature) {
		set("diskSignature", diskSignature);
	}

	public Long getSize() {
		return (Long) get("size");
	}

	public void setSize(Long size) {
		set("size", size);
	}

	public String getBusType() {
		return get("busType");
	}

	public void setBusType(String busType) {
		set("busType", busType);
	}

	public String getDiskId() {
		return get("diskId");
	}

	public void setDiskId(String diskId) {
		set("diskId", diskId);
	}
}
