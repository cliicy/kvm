package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

/*@BeanModelMarker.BEAN(com.ca.arcserve.linuximaging.webservice.data.log.LogFilter.class)
public interface LogFilterModel extends BeanModelMarker{
	
}*/
public class LogFilterModel extends BaseModelData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6646330685835133238L;
	
	/*type:
	 * 	All(-1),
		Information(1),
		Error(4),
		Warning(2),
		ErrorAndWarning(6);
	 * 
	 * */
	public LogFilterModel() {
		this.setType(null);
		this.setJobID(null);
		this.setJobName(null);
		this.setServer(null);
		this.setEndTime(null);
		this.setStartTime(null);
	}
	public void setType(Integer type)
	{
		set("messageType", type);
	}
	public Integer getType()
	{
		return get("messageType");
	}
	
	public void setServer(String server)
	{
		set("server", server);
	}
	public String getServer()
	{
		return get("server");
	}
	
	public void setEndTime(Date date)
	{
		set("endTime", date);
	}
	public Date getEndTime()
	{
		return get("endTime");
	}
	public void setStartTime(Date date)
	{
		set("startTime", date);
	}
	public Date getStartTime()
	{
		return get("startTime");
	}
	public void setJobID(Long id)
	{
		set("jobID", id);
	}
	public Long getJobID()
	{
		return get("jobID");
	}
	
	public void setJobName(String name)
	{
		set("jobName", name);
	}
	public String getJobName()
	{
		return get("jobName");
	}
}
