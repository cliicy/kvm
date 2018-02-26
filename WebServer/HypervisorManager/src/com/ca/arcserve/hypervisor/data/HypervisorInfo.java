package com.ca.arcserve.hypervisor.data;

public class HypervisorInfo {
	
	public final static int HYPERVISOR_LEVEL_TOP = 1;
	public final static int HYPERVISOR_LEVEL_SUB = 2;
	
	private String name;
	
	private String username;
	
	private String password;
	
	private String protocol;
	
	private int port;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
