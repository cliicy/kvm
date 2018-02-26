package com.ca.arcserve.linuximaging.ui.client.model;

public enum Protocol {
	HTTP(0), HTTPS(1);
	
	private Integer value;

	Protocol(Integer v){
		this.value=v;
	}
	public Integer getValue(){
		return value;
	}
	public static Protocol parse(Integer v){
		switch(v){
		case 0:
			return HTTP;
		case 1:
			return HTTPS;
		default:
			return HTTPS;
		}
	}
}
