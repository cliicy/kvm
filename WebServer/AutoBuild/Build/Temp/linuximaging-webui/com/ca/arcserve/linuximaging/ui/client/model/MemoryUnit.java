package com.ca.arcserve.linuximaging.ui.client.model;

import com.ca.arcserve.linuximaging.ui.client.UIContext;

public enum MemoryUnit {
	
	GB(1), MB(2);
	
	private Integer value;

	MemoryUnit(Integer v){
		this.value=v;
	}
	public Integer getValue(){
		return value;
	}
	public static MemoryUnit parse(Integer v){
		switch(v){
		case 1:
			return GB;
		case 2:
			return MB;
		default:
			return GB;
		}
	}
	
	public static String displayMessage(Integer v){
		switch(v){
		case 1:
			return UIContext.Constants.targetVirtualMachineMemoryUnit_GB();
		case 2:
			return UIContext.Constants.targetVirtualMachineMemoryUnit_MB();
		default:
			return UIContext.Constants.targetVirtualMachineMemoryUnit_GB();
		}
	}

}
