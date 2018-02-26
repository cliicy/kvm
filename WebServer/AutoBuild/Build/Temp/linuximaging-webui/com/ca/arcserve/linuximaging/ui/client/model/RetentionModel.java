package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RetentionModel extends BaseModelData {
	
	public static final int LAST_DAY = 32;
	public static final int LAST_SUNDAY = 33;
	public static final int LAST_MONDAY = 34;
	public static final int LAST_TUESDAY = 35;
	public static final int LAST_WEDNESDAY = 36;
	public static final int LAST_THURSDAY = 37;
	public static final int LAST_FRIDAY = 38;
	public static final int LAST_SATURDAY = 39;
	
	private static final long serialVersionUID = 6780775115212302153L;

	public Integer getBackupSetCount() {
		return (Integer) get("BackupSetCount");
	}

	public void setBackupSetCount(Integer BackupSetCount) {
		set("BackupSetCount", BackupSetCount);
	}

	public Boolean isUseWeekly() {
		return (Boolean) get("useWeekly");
	}

	public void setUseWeekly(Boolean useWeekly) {
		set("useWeekly", useWeekly);
	}

	public Integer getDayOfWeek() {
		return (Integer) get("DayOfWeek");
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		set("DayOfWeek", dayOfWeek);
	}

	public Integer getDayOfMonth() {
		return (Integer) get("DayOfMonth");
	}

	public void setDayOfMonth(Integer dayOfMonth) {
		set("DayOfMonth", dayOfMonth);
	}

}
