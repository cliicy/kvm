package com.ca.arcserve.linuximaging.ui.client.model;

import com.ca.arcserve.linuximaging.ui.client.UIContext;

public enum FileOption {
	OVERWRITE(1),RENAME(2),SKIP(3);
	
	private Integer value;

	FileOption(Integer v){
		this.value=v;
	}
	public Integer getValue(){
		return value;
	}
	public static FileOption parse(Integer v){
		switch(v){
		case 1:
			return OVERWRITE;
		case 2:
			return RENAME;
		case 3:
			return SKIP;
		default:
			return OVERWRITE;
		}
	}
	public static String displayMessage(Integer v){
		switch(v){
		case 1:
			return UIContext.Constants.restoreConflictOverwrite();
		case 2:
			return UIContext.Constants.restoreConflictRename();
		case 3:
			return UIContext.Constants.restoreConflictSkip();
		default:
			return UIContext.Constants.restoreConflictOverwrite();
		}
	}

}
