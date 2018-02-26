package com.ca.arcserve.linuximaging.ui.client.model;

import com.ca.arcserve.linuximaging.ui.client.UIContext;

public enum JobType {
	UNKNOWN(0), BACKUP(1), RESTORE(2), BACKUP_FULL(3), BACKUP_INCREMENTAL(4), BACKUP_VERIFY(
			5), STANDBY(40), RESTORE_BMR(21), RESTORE_VOLUMN(22), RESTORE_FILE(
			23), RESTORE_VM(24), RESTORE_MIGRATION(26), SHARE_RECOVERY_POINT(27), ASSURE_RECOVERY(
			28), START_INSTANT_VM_JOB(60), STOP_INSTANT_VM_JOB(61);

	private Integer value;

	JobType(Integer v) {
		this.value = v;
	}

	public Integer getValue() {
		return value;
	}

	public static JobType parse(Integer v) {
		switch (v) {
		case 1:
			return BACKUP;
		case 2:
			return RESTORE;
		case 3:
			return BACKUP_FULL;
		case 4:
			return BACKUP_INCREMENTAL;
		case 5:
			return BACKUP_VERIFY;
		case 21:
			return RESTORE_BMR;
		case 22:
			return RESTORE;
		case 23:
			return RESTORE_FILE;
		case 24:
			return RESTORE_VM;
		case 25:
			return RESTORE_MIGRATION;
		case 26:
			return RESTORE_MIGRATION;
		case 27:
			return SHARE_RECOVERY_POINT;
		case 28:
			return ASSURE_RECOVERY;
		case 40:
			return STANDBY;
		case 60:
			return START_INSTANT_VM_JOB;
		case 61:
			return STOP_INSTANT_VM_JOB;
		default:
			return UNKNOWN;
		}
	}

	public static String displayMessage(Integer v, Integer jobMethod) {
		switch (v) {
		case 1:
			return UIContext.Constants.backup();
		case 2:
			return UIContext.Constants.restore();
		case 3:
			return UIContext.Constants.fullBackup();
		case 4:
			return UIContext.Constants.incrementalBackup();
		case 5:
			return UIContext.Constants.verifyBackup();
		case 21:
			if (jobMethod == JobScriptModel.JOB_METHOD_INSTANT_BMR
					|| jobMethod == JobScriptModel.JOB_METHOD_INSTANT_BMR_WITH_AUTO_RESTORE) {
				return UIContext.Constants.restoreInstantBMR();
			}
			return UIContext.Constants.restoreType_BMR();
		case 22:
			return UIContext.Constants.restore();
		case 23:
			return UIContext.Constants.restoreType_Restore_File();
		case 24:
			if (jobMethod == JobScriptModel.JOB_METHOD_INSTANT_BMR
					|| jobMethod == JobScriptModel.JOB_METHOD_INSTANT_BMR_WITH_AUTO_RESTORE) {
				return UIContext.Constants.instantVM();
			}
			return UIContext.Constants.restoreType_BMR();
		case 25:
			return UIContext.Constants.restore();
		case 26:
			return UIContext.Constants.migrationBmr();
		case 27:
			return UIContext.Constants.shareRecoveryPoint();
		case 28:
			return UIContext.Constants.assureRecovery();
		case 40:
			return UIContext.Constants.standby();
		case 60:
			return UIContext.Constants.startInstantVMJob();
		case 61:
			return UIContext.Constants.stopInstantVMJob();
		default:
			return UIContext.Constants.unknown();
		}
	}

	public static String displayMessage(Integer v) {
		return displayMessage(v, 0);
	}

	public static boolean isBackup(Integer v) {
		switch (v) {
		case 1:
			return true;
		case 3:
			return true;
		case 4:
			return true;
		case 5:
			return true;
		default:
			return false;
		}
	}
}
