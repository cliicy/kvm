package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DailyScheduleModel extends BaseModelData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3003255207733339345L;
	
	public BackupScheduleModel fullSchedule;
	public BackupScheduleModel incrementalSchedule;
	public BackupScheduleModel resyncSchedule;
	public D2DTimeModel startTime;

}
