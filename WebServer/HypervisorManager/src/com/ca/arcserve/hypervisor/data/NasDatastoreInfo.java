package com.ca.arcserve.hypervisor.data;

public class NasDatastoreInfo {

	public static final int TYPE_CIFS = 1;
	public static final int TYPE_NFS = 0;
	
	public static final int ACCESS_MODE_READ = 0;
	public static final int ACCESS_MODE_READWRITE = 1;
	
	private String dataStoreName;
	
	private int type;
	
	private String remoteHost;
	
	private String remotePath;
	
	private String username;
	
	private String password;
	
	private int accessMode;

	public String getDataStoreName() {
		return dataStoreName;
	}

	public void setDataStoreName(String dataStoreName) {
		this.dataStoreName = dataStoreName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(int accessMode) {
		this.accessMode = accessMode;
	}
}
