package com.ca.arcserve.linuximaging.ui.client.model.dashboard;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class JobSummaryModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4579451748879226016L;
	
	private Integer totalJobs;
	private Integer success;
	private Integer failure;
	private Integer incomplete;
	
	public Integer getTotalJobs() {
		return totalJobs;
	}
	public void setTotalJobs(Integer totalJobs) {
		this.totalJobs = totalJobs;
	}
	public Integer getSuccess() {
		return success;
	}
	public void setSuccess(Integer success) {
		this.success = success;
	}
	public Integer getFailure() {
		return failure;
	}
	public void setFailure(Integer failure) {
		this.failure = failure;
	}
	public Integer getIncomplete() {
		return incomplete;
	}
	public void setIncomplete(Integer incomplete) {
		this.incomplete = incomplete;
	}
}
