package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class D2DTimeModel extends BaseModelData {
	private static final long serialVersionUID = -3558640210200917626L;

	public D2DTimeModel(){
		setReady(true);
		setRunNow(false);
	}
	public Integer getYear() {
		return (Integer)get("year");
	}
	
	public void setYear(Integer year) {
		set("year", year);
	}
	
	public Integer getMonth() {
		return (Integer)get("month");
	}
	
	public void setMonth(Integer month) {
		set("month", month);
	}
	
	public Integer getDay() {
		return (Integer)get("day");
	}
	
	public void setDay(Integer day) {
		set("day", day);
	}
	
	public Integer getHour() {
		return (Integer)get("hour");
	}
	
	public void setHour(Integer hour) {
		set("hour", hour);
	}
	
	public Integer getMinute() {
		return (Integer)get("minute");
	}
	
	public void setMinute(int minute) {
		set("minute", minute);
	}
	
	public Integer getHourOfDay() {
		return (Integer)get("hourOfDay");
	}
	
	public void setHourOfDay(Integer hourofDay) {
		set("hourOfDay", hourofDay);
	}
	
	public Integer getAMPM(){
		Integer value = get("AMPM");
		if(value == null)
			return -1;
		else 
			return value;
	}
	
	public void setAMPM(Integer am) {
		set("AMPM", am);
	}
	
	@SuppressWarnings("deprecation")
	public void fromJavaDate(Date date) {
		this.setYear(date.getYear() + 1900);
		setMonth(date.getMonth());
		setDay(date.getDate());
		setHourOfDay(date.getHours());
		setMinute(date.getMinutes());
	}
	
	public Boolean isReady() {
		return (Boolean)get("ready");
	}
	
	public void setReady(Boolean ready) {
		set("ready", ready);
	}
	
	public Boolean isRunNow() {
		return (Boolean)get("runNow");
	}
	
	public void setRunNow(Boolean runNow) {
		set("runNow", runNow);
	}
}
