package com.ca.arcserve.linuximaging.ui.client.model;

import com.ca.arcserve.linuximaging.ui.client.UIContext;

public enum RestoreType {
	BMR(21), VOLUME(22), FILE(23), VM(24), MIGRATION_BMR(26), SHARE_RECOVERY_POINT(
			27), ASSURE_RECOVERY(28);

	private Integer value;

	RestoreType(Integer v) {
		this.value = v;
	}

	public Integer getValue() {
		return value;
	}

	public static RestoreType parse(Integer v) {
		switch (v) {
		case 21:
			return BMR;
		case 22:
			return VOLUME;
		case 23:
			return FILE;
		case 24:
			return VM;
		case 26:
			return MIGRATION_BMR;
		case 27:
			return SHARE_RECOVERY_POINT;
		case 28:
			return ASSURE_RECOVERY;
		default:
			return BMR;
		}
	}

	public static String displayMessage(Integer v) {
		switch (v) {
		case 21:
			return UIContext.Constants.bmr();
		case 22:
			return UIContext.Constants.volume();
		case 23:
			return UIContext.Constants.file();
		case 24:
			return UIContext.Constants.restoreWizardForVM();
		case 26:
			return UIContext.Constants.migrationBmr();
		case 27:
			return UIContext.Constants.shareRecoveryPoint();
		case 28:
			return UIContext.Constants.assureRecovery();
		default:
			return UIContext.Constants.bmr();
		}
	}
}
