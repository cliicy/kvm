package com.ca.arcserve.linuximaging.ui.client.components.configuration.model;

import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupTemplateModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4657078281790508045L;

	public String getName() {
		return get("Name");
	}

	public void setName(String name) {
		set("Name", name);
	}

	public String getDescription() {
		return get("Description");
	}

	public void setDescription(String description) {
		set("Description", description);
	}

	public BackupLocationInfoModel backupLocationInfoModel;
	
	public Integer getCompression(){
		return get("Compression");
	}
	
	public void setCompression(Integer compression){
		set("Compression",compression);
	}
	
	public String getEncryptionName(){
		return get("EncryptionName");
	}
	
	public void setEncryptionName(String encryptionName){
		set("EncryptionName", encryptionName);
	}
	
	public String getEncryptionPassword(){
		return get("EncryptionPassword");
	}
	
	public void setEncryptionPassword(String encryptionPassword){
		set("EncryptionPassword",encryptionPassword);
	}
	
	public void setEncryptionType(Integer encryptionType){
		set("EncryptionType",encryptionType);
	}
	public Integer getEncryptionType(){
		return get("EncryptionType");
	}
}
