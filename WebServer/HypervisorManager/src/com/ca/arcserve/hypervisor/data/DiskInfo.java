package com.ca.arcserve.hypervisor.data;

public class DiskInfo {
	
	private long size;
	
	private String storage;
	
	private String diskController;
	
	private boolean useExistingFile;
	
	private String diskFile;

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public String getDiskController() {
		return diskController;
	}

	public void setDiskController(String diskController) {
		this.diskController = diskController;
	}

	public boolean isUseExistingFile() {
		return useExistingFile;
	}

	public void setUseExistingFile(boolean useExistingFile) {
		this.useExistingFile = useExistingFile;
	}

	public String getDiskFile() {
		return diskFile;
	}

	public void setDiskFile(String diskFile) {
		this.diskFile = diskFile;
	}
	
}
