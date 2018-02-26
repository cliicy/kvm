package com.ca.arcserve.linuximaging.ui.client.model;

import com.ca.arcserve.linuximaging.ui.client.UIContext;

public enum StandbyType {
	PHYSICAL(30),VIRTUAL(31);
	
	private Integer value;

	StandbyType(Integer v){
		this.value=v;
	}
	public Integer getValue(){
		return value;
	}
	public static StandbyType parse(Integer v){
		switch(v){
		case 30:
			return PHYSICAL;
		case 31:
			return VIRTUAL;
		default:
			return PHYSICAL;
		}
	}
	
	public static String displayMessage(Integer v){
		switch(v){
		case 21:
			return UIContext.Constants.bmr();
		case 22:
			return UIContext.Constants.volume();
		case 23:
			return UIContext.Constants.file();
		case 24:
			return UIContext.Constants.restoreWizardForVM();
		default:
			return UIContext.Constants.bmr();
		}
	}
}
