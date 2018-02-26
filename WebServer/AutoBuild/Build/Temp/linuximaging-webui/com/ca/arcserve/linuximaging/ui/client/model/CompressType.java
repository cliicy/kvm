package com.ca.arcserve.linuximaging.ui.client.model;

import com.ca.arcserve.linuximaging.ui.client.UIContext;

public enum CompressType {
	NONE(0),STANDARD(1),MAX(2);
	
	private Integer value;

	CompressType(Integer v){
		this.value=v;
	}
	public Integer getValue(){
		return value;
	}
	public static CompressType parse(Integer v){
		switch(v){
		case 0:
			return NONE;
		case 1:
			return STANDARD;
		case 2:
			return MAX;
		default:
			return NONE;
		}
	}
	
	public static int parseMessage(String compression) {
		if ( compression == null )
			return NONE.getValue();
		
		if(UIContext.Constants.settingsCompressionNone().equalsIgnoreCase(compression)){
			return NONE.getValue();
		}else if(UIContext.Constants.settingsCompreesionStandard().equalsIgnoreCase(compression)){
			return STANDARD.getValue();
		}else if(UIContext.Constants.settingsCompressionMax().equalsIgnoreCase(compression)){
			return MAX.getValue();
		}else{
			return NONE.getValue();
		}
	}
	
	public static String displayMessage(Integer v){
		switch(v){
		case 0:
			return UIContext.Constants.settingsCompressionNone();
		case 1:
			return UIContext.Constants.settingsCompreesionStandard();
		case 2:
			return UIContext.Constants.settingsCompressionMax();
		default:
			return UIContext.Constants.settingsCompressionNone();
		}
	}
}
