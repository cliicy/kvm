package com.ca.arcserve.hypervisor.data;

public enum PowerState {
	
	PoweredOn(1), PoweredOff(2), Suspended(3), ErrorFault(4);
	
	private Integer value;
	
	PowerState(Integer v){
		this.value = v;
	}
	
	public Integer getValue(){
		return value;
	}

}
