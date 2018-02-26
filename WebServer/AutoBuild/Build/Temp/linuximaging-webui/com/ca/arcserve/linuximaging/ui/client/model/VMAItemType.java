package com.ca.arcserve.linuximaging.ui.client.model;

public enum VMAItemType {
	VCENTER(1),ESXSERVER(2);
	
	private Integer value;

	VMAItemType(Integer v){
		this.value=v;
	}
	public Integer getValue(){
		return value;
	}
	public static VMAItemType parse(Integer v){
		switch(v){
		case 1:
			return VCENTER;
		case 2:
			return ESXSERVER;
		default:
			return ESXSERVER;
		}
	}
}
