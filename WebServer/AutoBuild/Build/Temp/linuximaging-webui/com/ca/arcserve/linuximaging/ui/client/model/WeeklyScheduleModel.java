package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class WeeklyScheduleModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6639640140240285181L;
	
	public D2DTimeModel startTime;
	public List<BackupScheduleModel> scheduleList;
	

}
