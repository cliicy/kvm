package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ServerCapabilityModel extends BaseModelData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6107934099665432230L;
	public void setRestoreUtilInstalled(Boolean installed) {
		set("RestoreUtilInstalled", installed);
	}
	public Boolean getRestoreUtilInstalled() {
		return get("RestoreUtilInstalled");
	}
	
	public void setRestoreUtilRunning(Boolean running) {
		set("RestoreUtilRunning", running);
	}
	public Boolean getRestoreUtilRunning() {
		return get("RestoreUtilRunning");
	}
	
	public void setPublicKey(Boolean publicKey) {
		set("publicKey", publicKey);
	}

	public Boolean getPublicKey() {
		return get("publicKey");
	}
	
	public void setEnableBackupWhenManagedByUDP(Boolean enableBackupWhenManagedByUDP) {
		set("enableBackupWhenManagedByUDP", enableBackupWhenManagedByUDP);
	}

	public Boolean getEnableBackupWhenManagedByUDP() {
		return get("enableBackupWhenManagedByUDP");
	}
}
