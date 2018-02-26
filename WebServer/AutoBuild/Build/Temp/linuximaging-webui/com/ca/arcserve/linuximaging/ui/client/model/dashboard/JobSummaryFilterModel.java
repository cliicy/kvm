package com.ca.arcserve.linuximaging.ui.client.model.dashboard;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class JobSummaryFilterModel extends BaseModelData {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2019176746976308887L;
	public static final int RANGE_ALL=0;
	public static final int RANGE_RECENT_WEEK=1;
	public static final int RANGE_RECENT_MONTH=2;
	
	private int timeRange;
	private int type;

	public int getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(int timeRange) {
		this.timeRange = timeRange;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
