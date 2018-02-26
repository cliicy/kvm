package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BrowseSearchResultModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5650775479167623141L;
	
	public void setResultList(List<GridTreeNode> resultList){
		set("resultList",resultList);
	}
	
	public List<GridTreeNode> getResultList(){
		return get("resultList");
	}
	
	public void setSearchEnd(Boolean isSearchEnd){
		set("isSearchEnd",isSearchEnd);
	}
	
	public Boolean isSearchEnd(){
		return get("isSearchEnd");
	}

}
