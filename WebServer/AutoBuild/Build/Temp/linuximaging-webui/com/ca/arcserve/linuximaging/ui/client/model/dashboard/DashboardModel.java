package com.ca.arcserve.linuximaging.ui.client.model.dashboard;

import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class DashboardModel extends BaseModelData{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7595809758954005864L;
	private D2DServerInfoModel serverInformation;
	private ResourceUsageModel resourceUsage;
	private BackupLocationInfoModel[] backupStorages;
	private NodeSummaryModel nodeSummary;
	private JobSummaryModel jobSummary;
	
	public D2DServerInfoModel getServerInformation() {
		return serverInformation;
	}
	public void setServerInformation(D2DServerInfoModel serverInformation) {
		this.serverInformation = serverInformation;
	}
	public ResourceUsageModel getResourceUsage() {
		return resourceUsage;
	}
	public void setResourceUsage(ResourceUsageModel resourceUsage) {
		this.resourceUsage = resourceUsage;
	}
	public BackupLocationInfoModel[] getBackupStorages() {
		return backupStorages;
	}
	public void setBackupStorages(BackupLocationInfoModel[] backupStorages) {
		this.backupStorages = backupStorages;
	}
	public NodeSummaryModel getNodeSummary() {
		return nodeSummary;
	}
	public void setNodeSummary(NodeSummaryModel nodeSummary) {
		this.nodeSummary = nodeSummary;
	}
	public JobSummaryModel getJobSummary() {
		return jobSummary;
	}
	public void setJobSummary(JobSummaryModel jobSummary) {
		this.jobSummary = jobSummary;
	}
	
}
