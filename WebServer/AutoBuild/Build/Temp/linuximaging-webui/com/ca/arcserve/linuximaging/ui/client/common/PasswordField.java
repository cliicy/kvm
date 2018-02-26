package com.ca.arcserve.linuximaging.ui.client.common;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class PasswordField extends TextField<String>{
	public static final String PSEUDO_PASSWORD="        \t";
	private String realPassword;
	public PasswordField(int width){
		this.setPassword(true);
		this.setWidth(width);
	}
	
	public void setPasswordValue(String value){
		this.setValue(PSEUDO_PASSWORD);
		this.realPassword=value;
	}

	public String getPasswordValue(){
		String value=this.getValue();
		if(value==null){
			return null;
		}else if(value.equals(PSEUDO_PASSWORD)){
			return realPassword;
		}else{
			return value;
		}
	}
}
