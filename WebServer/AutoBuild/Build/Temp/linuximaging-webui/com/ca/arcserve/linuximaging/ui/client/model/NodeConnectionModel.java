package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class NodeConnectionModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8471860461722895363L;
	public List<VolumeInfoModel> volumes = null;
	
	public void setErrorCode(Integer errCode)
	{
		set("ErrorCode", errCode);
	}
	public Integer getErrorCode()
	{
		return get("ErrorCode");
	}
	
	public void setErrorCodeExt(Integer errCode)
	{
		set("ErrorCodeExt", errCode);
	}
	public Integer getErrorCodeExt()
	{
		return get("ErrorCodeExt");
	}
	
	public void setOS(String errCode)
	{
		set("OSName", errCode);
	}
	public String getOS()
	{
		return get("OSName");
	}
	
	public void setD2DServer(String server)
	{
		set("D2DServer", server);		
	}
	public String getD2DServer() {
		return get("D2DServer");
	}
	
	public void setNodeUUID(String nodeUUID)
	{
		set("nodeUUID", nodeUUID);		
	}
	
	public String getNodeUUID() {
		return get("nodeUUID");
	}

}
