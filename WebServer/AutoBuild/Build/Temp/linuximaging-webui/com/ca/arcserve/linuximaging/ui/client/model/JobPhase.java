package com.ca.arcserve.linuximaging.ui.client.model;

import com.ca.arcserve.linuximaging.ui.client.UIContext;


public enum JobPhase {
	UNKNOW(0),
	BACKUP_VOLUME(11),
	MONITORING(12),
	CONNECT_TARGET(65),
	MOUNT_SESSION(67),
	CREATE_DISK(68),
	RESTORE_VOLUME(69),
	DATA_END(71),
	WRAPUP(72),
	DIAGNOSTIC(73),
	CONNECT_NODE(97),
	RESTORE_FILE(99),
	START(209),
	JOB_END(210),
	CONVERT_JOBSCRIPT_TO_JOBHISTORY(211),
	INSTALL_BUILD(212),
	UNINSTALL_BUILD(213),
	CANCEL_JOB(214),
	WAITING_MIGRATION(80),
	MIGRATION_DATA(81),
	WAITING_FOR_REBOOT(82),
	BOOT_SYSTEM(83),
	READY_FOR_USE(84),
	RUNNING(85),
	POWER_OFF(88),
	TRYING_TO_CONNET_TARGET(89),
	PREPARE_RECOVERY_POINT(103),
	SHARE_RECOVERY_POINT(104);
	
	private Integer value;

	JobPhase(Integer v){
		this.value=v;
	}
	public Integer getValue(){
		return value;
	}
	public static JobPhase parse(Integer v){
		switch(v){
		case 11:
			return BACKUP_VOLUME;
		case 12:
			return MONITORING;
		case 65:
			return CONNECT_TARGET;
		case 67:
			return MOUNT_SESSION;
		case 68:
			return CREATE_DISK;
		case 69:
			return RESTORE_VOLUME;
		case 71:
			return DATA_END;
		case 72:
			return WRAPUP;
		case 73:
			return DIAGNOSTIC;
		case 97:
			return CONNECT_NODE;
		case 99:
			return RESTORE_FILE;
		case 209:
			return START;
		case 210:
			return JOB_END;
		case 211:
			return CONVERT_JOBSCRIPT_TO_JOBHISTORY;
		case 212:
			return INSTALL_BUILD;
		case 213:
			return UNINSTALL_BUILD;
		case 214:
			return CANCEL_JOB;
		case 80:
			return WAITING_MIGRATION;
		case 81:
			return MIGRATION_DATA;	
		case 82:
			return WAITING_FOR_REBOOT;	
		case 83:
			return BOOT_SYSTEM;	
		case 86:
		case 87:
		case 84:
			return READY_FOR_USE;	
		case 85:
			return RUNNING;	
		case 88:
			return POWER_OFF;
		case 89:
			return TRYING_TO_CONNET_TARGET;
		case 103:
			return PREPARE_RECOVERY_POINT;
		case 104:
			return SHARE_RECOVERY_POINT;
		default:
			return UNKNOW;
		}
	}
	public static String displayMessage(Integer v){
		switch(v){
		
		case 12:
			return UIContext.Constants.jobPhase_monitoring();
		case 65:
			return UIContext.Constants.jobPhase_connectTarget();
		case 68:
			return UIContext.Constants.jobPhase_createDisk();
		case 72:
			return UIContext.Constants.jobPhase_wrapup();
		case 97:
			return UIContext.Constants.jobPhase_connectNode();
		case 99:
			return UIContext.Constants.jobPhase_restoreFiles();
		case 209:
			return UIContext.Constants.jobPhase_start();
		case 212:
			return UIContext.Constants.jobPhase_installBuild();
		case 214:
			return UIContext.Constants.jobPhase_cancelJob();
		case 80:
			return UIContext.Constants.jobPhase_WaitingMigration();
		case 81:
			return UIContext.Constants.jobPhase_MigrationData();	
		case 82:
			return UIContext.Constants.jobPhase_WaitingForReboot();	
		case 83:
			return UIContext.Constants.jobPhase_BootSystem();	
		case 86:
		case 87:
		case 84:
			return UIContext.Constants.jobPhase_ReadyForUse();	
		case 85:
			return UIContext.Constants.jobPhase_Running();
		case 88:
			return UIContext.Constants.powerOff();
		case 89:
			return UIContext.Constants.tryToConnetTarget();
		case 103:
			return UIContext.Constants.prepareRecoveryPoint();
		case 104:
			return UIContext.Constants.sharingRecoveryPoint();
		default:
			return "";
		}
	}

	
}
