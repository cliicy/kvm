package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RestoreTargetModel extends BaseModelData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7608043316616178465L;
	
	public RestoreTargetModel(){
		setUseMac(false);
		setFileOption(FileOption.OVERWRITE.getValue());
		setReboot(true);
		setRestoreToOriginal(true);
		setKeepSourceSetting(false);
	}
	public void setUserName(String userName){
		set("userName",userName);
	}
	public String getUserName(){
		return get("userName");
	}
	public void setPassword(String password){
		set("password",password);
	}
	public String getPassword(){
		return get("password");
	}
	public void setDestination(String destination){
		set("destination",destination);
	}
	public String getDestination(){
		return get("destination");
	}
	public void setUseMac(Boolean useMac){
		set("useMac",useMac);
	}
	public Boolean getUseMac(){
		return get("useMac");
	}
	public void setAddress(String macAddress) {
		set("address", macAddress);
	}
	public String getAddress() {
		return get("address");
	}
	//network settings of target machine after restore 
	public void setNetwork_HostName(String hostName) {
		set("hostName", hostName);
	}
	public String getNetwork_HostName() {
		return get("hostName");
	}
	public void setNetwork_IsDHCP(Boolean isDHCP) {
		set("isDHCP", isDHCP);
	}
	public Boolean getNetwork_IsDHCP() {
		return get("isDHCP");
	}
	public void setNetwork_ipAddress(String network_ipAddress) {
		set("network_ipAddress", network_ipAddress);
	}
	public String getNetwork_ipAddress() {
		return get("network_ipAddress");
	}
	public void setNetwork_subnetMask(String network_subnetMask) {
		set("network_subnetMask", network_subnetMask);
	}
	public String getNetwork_subnetMask() {
		return get("network_subnetMask");
	}
	public void setNetwork_gateway(String network_gateway) {
		set("network_gateway", network_gateway);
	}
	public String getNetwork_gateway() {
		return get("network_gateway");
	}
	public void setNetwork_dnsServer(String network_dnsServer) {
		set("network_dnsServer", network_dnsServer);
	}
	public String getNetwork_dnsServer() {
		return get("network_dnsServer");
	}
	public void setFileOption(Integer option) {
		set("fileOption", option);
	}
	public Integer getFileOption() {
		return get("fileOption");
	}
	public void setReboot(Boolean reboot) {
		set("reboot", reboot);
	}
	public Boolean getReboot() {
		return get("reboot");
	}
	public void setRestoreToOriginal(Boolean restoreToOriginal) {
		set("restoreToOriginal", restoreToOriginal);
	}
	public Boolean getRestoreToOriginal() {
		return get("restoreToOriginal");
	}
	
	public void setCreateRootDir(Boolean createRootDir){
		set("createRootDir",createRootDir);
	}
	
	public Boolean getCreateRootDir(){
		return get("createRootDir");
	}
	
	public void setEnableInstanceBMR(Boolean enableInstanceBMR){
		set("enableInstanceBMR",enableInstanceBMR);
	}
	
	public Boolean isEnableInstanceBMR(){
		return get("enableInstanceBMR");
	}
	
	public void setAutoRestoreData(Boolean autoRestoreData){
		set("autoRestoreData",autoRestoreData);
	}
	
	public Boolean isAutoRestoreData(){
		return get("autoRestoreData");
	}
	
	public void setKeepSourceSetting(Boolean keepSourceSetting){
		set("keepSourceSetting",keepSourceSetting);
	}
	
	public Boolean isKeepSourceSetting(){
		return get("keepSourceSetting");
	}
}
