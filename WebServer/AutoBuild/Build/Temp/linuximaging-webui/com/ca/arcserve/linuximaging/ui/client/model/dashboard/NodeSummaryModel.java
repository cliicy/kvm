package com.ca.arcserve.linuximaging.ui.client.model.dashboard;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class NodeSummaryModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1442653952398161387L;
	
	private Integer totalNodes;
	private Integer protectedNodes;
	private Integer failureNodes;
	
	public Integer getTotalNodes() {
		return totalNodes;
	}
	public void setTotalNodes(Integer totalNodes) {
		this.totalNodes = totalNodes;
	}
	public Integer getProtectedNodes() {
		return protectedNodes;
	}
	public void setProtectedNodes(Integer protectedNodes) {
		this.protectedNodes = protectedNodes;
	}
	public Integer getFailureNodes() {
		return failureNodes;
	}
	public void setFailureNodes(Integer failureNodes) {
		this.failureNodes = failureNodes;
	}

}
