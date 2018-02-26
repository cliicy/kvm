package com.ca.arcserve.linuximaging.ui.client.model;

public enum VirtualizationServerType {
	VMWARE(1),XEN(2);
	
	private Integer value;

	VirtualizationServerType(Integer v){
		this.value=v;
	}
	public Integer getValue(){
		return value;
	}
	public static VirtualizationServerType parse(Integer v){
		switch(v){
		case 1:
			return VMWARE;
		case 2:
			return XEN;
		default:
			return VMWARE;
		}
	}
}
