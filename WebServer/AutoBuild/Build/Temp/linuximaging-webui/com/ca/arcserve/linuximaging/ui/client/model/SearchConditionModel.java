package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class SearchConditionModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 325705277674844398L;

	public void setSearchStr(String searchStr){
		set("searchStr",searchStr);
	}
	
	public String getSearchStr(){
		return get("searchStr");
	}
	
	public void setStart(Integer start){
		set("start",start);
	}
	
	public Integer getStart(){
		return get("start");
	}
	
	public void setPageSize(Integer pageSize){
		set("pageSize",pageSize);
	}
	
	public Integer getPageSize(){
		return get("pageSize");
	}
}
