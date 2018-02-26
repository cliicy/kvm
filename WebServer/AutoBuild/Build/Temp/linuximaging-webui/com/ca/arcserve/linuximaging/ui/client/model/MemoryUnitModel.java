package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class MemoryUnitModel extends BaseModelData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7987296347218421487L;

	public void setDisplayName(String displayName){
		set("displayName",displayName);
	}

	public String getDisplayName(){
		return get("displayName");
	}
	
	public void setValue(Integer value){
		set("value",value);
	}
	
	public Integer getValue(){
		return get("value");
	}
	
	public boolean equals(MemoryUnitModel model){
		return model.getValue().intValue() == this.getValue().intValue();
	}
}
