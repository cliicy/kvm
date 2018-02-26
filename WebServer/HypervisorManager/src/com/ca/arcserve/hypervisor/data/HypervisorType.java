package com.ca.arcserve.hypervisor.data;

public enum HypervisorType {
	UNKNOWN(0), OVM(1), ESX(2), XEN(3), RHEV(4), HYPERV(5), KVM(6);
	
	private Integer value;

	HypervisorType(Integer v){
		this.value=v;
	}
	public Integer getValue(){
		return value;
	}
	public static HypervisorType parse(Integer v){
		switch(v){
		case 1:
			return OVM;
		case 2:
			return ESX;
		case 3:
			return XEN;
		case 4:
			return RHEV;
		case 5:
			return HYPERV;
		case 6:
			return KVM;
		default:
			return UNKNOWN;
		}
	}
	
	public static HypervisorType parse(String s){
		if( s.equalsIgnoreCase("ovm") )
			return OVM;
		else if( s.equalsIgnoreCase("esx") )
			return ESX;
		else if( s.equalsIgnoreCase("xen") )
			return XEN;
		else if( s.equalsIgnoreCase("rhev") )
			return RHEV;
		else if( s.equalsIgnoreCase("hyperv") || s.equalsIgnoreCase("hyper-v"))
			return HYPERV;
		else if( s.equalsIgnoreCase("kvm"))
			return KVM;
		else 
			return UNKNOWN;
		
	}
	
}
