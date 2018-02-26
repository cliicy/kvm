package com.ca.arcserve.linuximaging.ui.client.restore.browse;

import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;

public class BrowseContext {

	private BackupLocationInfoModel sessionLocation;
	private String machine;
	private String machineName;
	private RecoveryPointModel recoveryPoint;
	private ServiceInfoModel server;
	private GridTreeNode parent;
	private String scriptUUID;
	private boolean isClosed = false;
    private RestoreType restoreType;
    
	public BackupLocationInfoModel getSessionLocation() {
		return sessionLocation;
	}

	public void setSessionLocation(BackupLocationInfoModel sessionLocation) {
		this.sessionLocation = sessionLocation;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public RecoveryPointModel getRecoveryPoint() {
		return recoveryPoint;
	}

	public void setRecoveryPoint(RecoveryPointModel recoveryPoint) {
		this.recoveryPoint = recoveryPoint;
	}

	public ServiceInfoModel getServer() {
		return server;
	}

	public void setServer(ServiceInfoModel server) {
		this.server = server;
	}

	public GridTreeNode getParent() {
		return parent;
	}

	public void setParent(GridTreeNode parent) {
		this.parent = parent;
	}

	public String getScriptUUID() {
		return scriptUUID;
	}

	public void setScriptUUID(String scriptUUID) {
		this.scriptUUID = scriptUUID;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public RestoreType getRestoreType() {
		return restoreType;
	}

	public void setRestoreType(RestoreType restoreType) {
		this.restoreType = restoreType;
	}
}
