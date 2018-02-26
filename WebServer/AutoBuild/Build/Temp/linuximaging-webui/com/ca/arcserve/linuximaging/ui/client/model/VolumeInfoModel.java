package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VolumeInfoModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8841257066293225113L;
	
	public void setMountPoint(String mountP)
	{
		set("mountPoint", mountP);
	}
	
	public String getMountPoint()
	{
		return get("mountPoint");
	}
	
	public void setFileSystem(String fs)
	{
		set("fileSystem", fs);
	}
	
	public String getFileSystem()
	{
		return get("fileSystem");
	}
	
	public void setType(String type)
	{
		set("fsType", type);
	}
	
	public String getType()
	{
		return get("fsType");
	}

}
