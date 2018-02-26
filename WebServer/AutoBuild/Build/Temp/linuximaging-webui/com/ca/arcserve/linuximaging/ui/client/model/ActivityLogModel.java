package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.Date;


/*@BeanModelMarker.BEAN(com.ca.arcserve.linuximaging.webservice.data.log.ActivityLog.class)
public interface ActivityLogModel extends BeanModelMarker{
	
}*/

public class ActivityLogModel extends LogFilterModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7466731157772301486L;
	
	public void setMessage(String message)
	{
		set("message", message);
	}
	public String getMessage()
	{
		return get("message");
	}
	
	public void setTime(Date date)
	{
		set("logTime", date);
	}
	public Date getTime()
	{
		return get("logTime");
	}
}
