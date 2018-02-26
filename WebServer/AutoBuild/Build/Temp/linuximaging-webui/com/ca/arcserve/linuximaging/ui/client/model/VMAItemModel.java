package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VMAItemModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1792041058799862895L;
	
	public void setItemName(String itemName){
		set("itemName",itemName);
	}
	
	public String getItemName(){
		return get("itemName");
	}
	
	public void setItemType(Integer itemType){
		set("itemType",itemType);
	}
	
	public Integer getItemType(){
		return get("itemType");
	}

}
