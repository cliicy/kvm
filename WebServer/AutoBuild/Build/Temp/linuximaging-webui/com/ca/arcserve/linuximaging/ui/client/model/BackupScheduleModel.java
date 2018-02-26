package com.ca.arcserve.linuximaging.ui.client.model;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class BackupScheduleModel extends BaseTreeModel{
	public static final int Minute_Unit 	= 0;
	public static final int Hour_Unit 		= 1;
	public static final int Day_Unit	 	= 2;

	private static final long serialVersionUID = -6901964354365970528L;
	
	public Integer getInterval() {
		return (Integer)get("interval");
	}

	public void setInterval(Integer interval) {
		set("interval", interval);
	}

	public Integer getIntervalUnit() {
		return (Integer)get("intervalUnit");
	}

	public void setIntervalUnit(Integer intervalUnit) {
		set("intervalUnit", intervalUnit);
	}

	public Boolean isEnabled() {
		return (Boolean)get("enabled");
	}

	public void setEnabled(Boolean enabled) {
		set("enabled", enabled);
	}
	
	public Integer getMethod(){
		return get("method");
	}
	
	public void setMethod(Integer method){
		set("method",method);
	}
	
	public D2DTimeModel startTime;
	public D2DTimeModel endTime;
	
	public String getTimeDispalyMessage(){
		return get("timeDisplay");
	}
	
	public void setTimeDisplayMessage(String message){
		set("timeDisplay",message);
	}
	
	public String displayIntervalUnit(int unit){
		switch (unit) {
		case BackupScheduleModel.Day_Unit:
			return UIContext.Constants.scheduleLabelDays();
		case BackupScheduleModel.Hour_Unit:
			return UIContext.Constants.scheduleLabelHours();
		case BackupScheduleModel.Minute_Unit:
			return UIContext.Constants.scheduleLabelMinutes();
		default:
			return "";
		}
	}
	public String displayMessage(){
		if(this.isEnabled()!=null&&this.isEnabled()){
			return UIContext.Messages.repeatEvery(this.getInterval(),displayIntervalUnit(this.getIntervalUnit()));
		}else{
			return UIContext.Constants.scheduleLabelNever();
		}
		
	}
	
	public Integer getDay(){
		return get("day");
	}
	
	public void setDay(Integer day){
		set("day",day);
	}
	
	public Boolean isRoot(){
		return get("isRoot");
	}
	public void setIsRoot(Boolean isRoot){
		set("isRoot",isRoot);
	}
}
