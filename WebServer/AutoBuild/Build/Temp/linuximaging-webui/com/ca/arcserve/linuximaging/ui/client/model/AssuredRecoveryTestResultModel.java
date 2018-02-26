package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class AssuredRecoveryTestResultModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2860807517170623420L;
	
	public Long getStartTime(){
		return get("startTime");
	}

	public void setStartTime(Long startTime){
		set("startTime",startTime);
	}
	
	public Long getEndTime(){
		return get("endTime");
	}

	public void setEndTime(Long endTime){
		set("endTime",endTime);
	}
	
	public Integer getResult(){
		return get("result");
	}
	
	public void setResult(Integer result){
		set("result",result);
	}
	
}
