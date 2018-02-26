package com.ca.arcserve.linuximaging.ui.client.model;

import com.ca.arcserve.linuximaging.ui.client.UIContext;

public enum JobStatus {
//	READY(0),RUNNING(1),FINISH(2),UNKNOW(3);
	READY(-1),
	IDLE(0), 
	FINISHED(1),
	CANCELLED(2),
	FAILED(3),
	INCOMPLETE(4),
	ACTIVE(5),
	WAITING(6),
	CRASHED(7),
	NEEDREBOOT(8),
	FAILED_NO_LICENSE(9),
	WAITING_IN_JOBQUEUE(10),
	DONE(100);
	
	private Integer value;

	JobStatus(Integer v){
		this.value=v;
	}
	public Integer getValue(){
		return value;
	}
	public static JobStatus parse(Integer v){
		switch(v){
		case -1:
			return READY;
		case 0:
			return IDLE;
		case 1:
			return FINISHED;
		case 2:
			return CANCELLED;
		case 3:
			return FAILED;
		case 4:
			return INCOMPLETE;
		case 5:
			return ACTIVE;
		case 6:
			return WAITING;
		case 7:
			return CRASHED;
		case 8:
			return NEEDREBOOT;
		case 9:
			return FAILED_NO_LICENSE;
		case 10:
			return WAITING_IN_JOBQUEUE;
		case 100:
			return DONE;
		default:
			return DONE;
		}
	}
	public static String displayMessage(Integer v){
		switch(v){
		case -1:
			return UIContext.Constants.jobStatus_ready();
		case 0:
			return UIContext.Constants.jobStatus_idle();
		case 1:
			return UIContext.Constants.jobStatus_finished();
		case 2:
			return UIContext.Constants.jobStatus_cancelled();
		case 3:
			return UIContext.Constants.jobStatus_failed();
		case 4:
			return UIContext.Constants.jobStatus_incomplete();
		case 5:
			return UIContext.Constants.jobStatus_active();
		case 6:
			return UIContext.Constants.jobStatus_waiting();
		case 7:
			return UIContext.Constants.jobStatus_crashed();
		case 8:
			return UIContext.Constants.jobStatus_needReboot();
		case 9:
			return UIContext.Constants.jobStatus_failed_no_license();
		case 10:
			return UIContext.Constants.jobStatus_waiting_in_jobqueue();
		case 100:
			return UIContext.Constants.jobStatus_done();
		default:
			return "";
		}
	}

}
