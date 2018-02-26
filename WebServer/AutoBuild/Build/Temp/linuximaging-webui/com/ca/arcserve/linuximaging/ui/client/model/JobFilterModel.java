package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class JobFilterModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1514830199517427878L;
	
	public JobFilterModel() {
		setJobID(0L);
		setJobName(null);
		setJobType(null);
		setNodeName(null);
		setSessionLoc(null);
		setJobResult(null);
		setStartTime(null);
		setEndTime(null);
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
	public void setJobType(Integer type)
	{
		set("jobType", type);
	}
	public Integer getType()
	{
		return get("jobType");
	}
	
	public void setNodeName(String server)
	{
		set("nodeName", server);
	}
	public String getNodeName()
	{
		return get("nodeName");
	}
	
	public void setSessionLoc(String sessLoc)
	{
		set("sessionLocation", sessLoc);
	}
	public String getSessionLoc()
	{
		return get("sessionLocation");
	}
	
	public void setJobResult(Integer result) {
		set("result", result);
	}
	public Integer getJobResult() {
		return get("result");
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
	
}
