package com.ca.arcserve.hypervisor.data;

public class StorageInfo {
	
	public static final int STORAGE_TYPE_LOCAL = 1;
	public static final int STORAGE_TYPE_NETWORK_SHARE = 2;
	public static final int STORAGE_TYPE_OTHERS = 3;
	
	private String name;
	
	private String device;
	
	private int type;
	
	private long capacity;
	
	private long freeSize;
	
	private boolean accessiable;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

	public long getFreeSize() {
		return freeSize;
	}

	public void setFreeSize(long freeSize) {
		this.freeSize = freeSize;
	}

	public boolean isAccessiable() {
		return accessiable;
	}

	public void setAccessiable(boolean accessiable) {
		this.accessiable = accessiable;
	}
}
