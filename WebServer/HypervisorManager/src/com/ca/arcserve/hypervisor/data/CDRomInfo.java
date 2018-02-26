package com.ca.arcserve.hypervisor.data;

public class CDRomInfo {
	
	private String storage;
	
	private String filename;
	
	private CDRom_Type type;

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public CDRom_Type getType() {
		return type;
	}

	public void setType(CDRom_Type type) {
		this.type = type;
	}

}
