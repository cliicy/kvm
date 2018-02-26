package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class NodeFilterModel extends BaseModelData {
	public static final int FILTER_ALL=-1;
	public static final int NODE_PROTECT_YES=0;
	public static final int NODE_PROTECT_NO=1;

	private static final long serialVersionUID = 6092911383000280629L;
	private String nodeName;
	private int isProtected; //0 no, 1 yes, 2 all
	private String operatingSystem;
	private int lastResult;
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public int getIsProtected() {
		return isProtected;
	}
	public void setIsProtected(int isProtected) {
		this.isProtected = isProtected;
	}
	public String getOperatingSystem() {
		return operatingSystem;
	}
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	public int getLastResult() {
		return lastResult;
	}
	public void setLastResult(int lastResult) {
		this.lastResult = lastResult;
	}

}
