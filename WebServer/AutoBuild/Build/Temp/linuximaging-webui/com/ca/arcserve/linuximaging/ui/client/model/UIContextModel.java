package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class UIContextModel extends BaseModelData{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String companyName="CA";
	private String productName="CA ARCserve D2D for Linux";
	private String fileSeparator="/";
	private String helpLink="http://www.arcservedocs.com/arcserved2d/r16.5/redirect_lnx.php?lang=en&item=";//pattern
	private String version="Linux D2D";
	private VersionInfoModel versionInfoModel;
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getFileSeparator() {
		return fileSeparator;
	}
	public void setFileSeparator(String fileSeparator) {
		this.fileSeparator = fileSeparator;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getHelpLink() {
		return helpLink;
	}
	public void setHelpLink(String helpLink) {
		this.helpLink = helpLink;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public VersionInfoModel getVersionInfoModel() {
		return versionInfoModel;
	}
	public void setVersionInfoModel(VersionInfoModel versionInfoModel) {
		this.versionInfoModel = versionInfoModel;
	}
}
