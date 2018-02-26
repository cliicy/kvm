package com.ca.arcserve.linuximaging.ui.client.homepage.dashboard;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.D2DServerInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.Image;


public class ServerInformationPanel extends DashboardPanel {
	
//	private LabelField txtManufacture;
//	private LabelField txtModel;
//	private LabelField txtCpuType;
	private LabelField txtOsVersion;
	private LabelField txtUpTime;
	private LabelField txtRunningJobs;
	private LabelField txtRestoreUtility;
	private LabelField txtLicense;
	private Image image_pass;
	private Image image_warning;
	private Image image_error;
	private LabelField lblLicense;
	private LayoutContainer license;

	public ServerInformationPanel(){
		setHeading(UIContext.Constants.serverInformation());

		TableLayout layout = new TableLayout();
	    layout.setColumns(2);
	    layout.setWidth("100%");
	   
	    layout.setCellPadding(2);
		layout.setCellSpacing(2);
	    setLayout(layout);
	    
	    TableData tdLabel = new TableData();
		tdLabel.setWidth("30%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("70%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
//		LabelField lblManufacture =new LabelField(UIContext.Constants.manufacture());
//		txtManufacture =new LabelField();
//		this.add(lblManufacture, tdLabel);
//		this.add(txtManufacture, tdField);
//		
//		LabelField lblModel =new LabelField(UIContext.Constants.model());
//		txtModel =new LabelField();
//		this.add(lblModel, tdLabel);
//		this.add(txtModel, tdField);
//		
//		LabelField lblCpuType =new LabelField(UIContext.Constants.cpuType());
//		txtCpuType =new LabelField();
//		this.add(lblCpuType, tdLabel);
//		this.add(txtCpuType, tdField);
		
		LabelField lblOsVersion =new LabelField(UIContext.Constants.osVersion());
		lblOsVersion.setStyleAttribute("white-space", "nowrap");
		txtOsVersion =new LabelField();
		this.add(lblOsVersion, tdLabel);
		this.add(txtOsVersion, tdField);
		
		LabelField lblUpTime =new LabelField(UIContext.Constants.upTime());
		txtUpTime =new LabelField();
		this.add(lblUpTime, tdLabel);
		this.add(txtUpTime, tdField);
		
		LabelField lblRunningJobs =new LabelField(UIContext.Constants.runningJobs());
		txtRunningJobs =new LabelField();
		this.add(lblRunningJobs, tdLabel);
		this.add(txtRunningJobs, tdField);
		
		LabelField lblRestoreUtility =new LabelField(UIContext.Constants.restoreUtility());
		txtRestoreUtility =new LabelField();
		this.add(lblRestoreUtility, tdLabel);
		this.add(txtRestoreUtility, tdField);
		
		lblLicense = new LabelField(UIContext.Constants.license());
		license = defineLicenseContainer();
		this.add(lblLicense, tdLabel);
		this.add(license, tdField);
		lblLicense.hide();
		license.hide();
	}
	
	private LayoutContainer defineLicenseContainer() {
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(4);
		container.setLayout(layout);
		image_pass = UIContext.IconHundle.finish().createImage();
		image_warning = UIContext.IconHundle.warning().createImage();
		image_error = UIContext.IconHundle.cancel().createImage();
		txtLicense =new LabelField();
		txtLicense.setStyleAttribute("padding-left", "5px");
		
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		container.add(image_pass, td);
		container.add(image_warning, td);
		container.add(image_error, td);
		container.add(txtLicense);
		hideAllImage();
		return container;
	}

	private void hideAllImage() {
		image_pass.setVisible(false);
		image_warning.setVisible(false);
		image_error.setVisible(false);
	}

	public void refreshData(D2DServerInfoModel model){
		if(model==null){
			return;
		}
//		this.txtManufacture.setValue(model.getManufacture());
//		this.txtModel.setValue(model.getModel());
//		this.txtCpuType.setValue(model.getCpuType());
		this.txtOsVersion.setValue(model.getOsVersion());
		this.txtUpTime.setValue(Utils.seconds2DayAndTime(model.getUpTime()));
		this.txtRunningJobs.setValue(model.getRunningJobs());
		this.txtRestoreUtility.setValue(model.isRestoreUtility()?UIContext.Constants.installed():UIContext.Constants.notInstalled());
//		this.refreshLicenseLabelField(model.getLicense());
		this.refreshLicense(model.getLicense(), model.getLicenseCount());
	}
	
	private void refreshLicense(int licenseStatus, int count) {
		switch(licenseStatus){
		case D2DServerInfoModel.GLIC_NOT_INSTALL:
		case D2DServerInfoModel.ERROR:
		case D2DServerInfoModel.TERMINATE:
		case D2DServerInfoModel.WG_COUNT:
		case D2DServerInfoModel.TRIAL:
		case D2DServerInfoModel.WILL_EXPIRE:
		case D2DServerInfoModel.EXPIRED:
			this.refreshLicenseLabelField(licenseStatus, count);
			lblLicense.show();
			license.show();
			break;
		case D2DServerInfoModel.VALID:
			lblLicense.hide();
			license.hide();
			break;
		default:
			lblLicense.hide();
			license.hide();
		}
	}

	public void resetData(){
		this.txtOsVersion.setValue("");
		this.txtUpTime.setValue("");
		this.txtRunningJobs.setValue("");
		this.txtRestoreUtility.setValue("");
		hideAllImage() ;
		this.txtLicense.setValue("");
	}
	
	public void refreshLicenseLabelField(int licenseStatus, int count){
		switch(licenseStatus){
		case D2DServerInfoModel.GLIC_NOT_INSTALL:
		case D2DServerInfoModel.ERROR:
		case D2DServerInfoModel.TERMINATE:
		case D2DServerInfoModel.WG_COUNT:
//		case D2DServerInfoModel.BETA_EXPIRED:
			hideAllImage() ;
			image_error.setVisible(true);
			break;
		case D2DServerInfoModel.VALID:
			hideAllImage(); 
			image_pass.setVisible(true);
			break;
		case D2DServerInfoModel.TRIAL:
		case D2DServerInfoModel.WILL_EXPIRE:
		case D2DServerInfoModel.EXPIRED:
			hideAllImage(); 
			image_warning.setVisible(true);
			break;
		default:
			hideAllImage(); 
			image_pass.setVisible(true);
//			txtLicense.setStyleAttribute("padding-left", "0px");
		}
		this.txtLicense.setValue(getDisplayLicenseMessage(licenseStatus, count));
	}

	private String getDisplayLicenseMessage(int licenseStatus, int count) {
		switch(licenseStatus){
		case D2DServerInfoModel.GLIC_NOT_INSTALL:
			return UIContext.Constants.glicNotInstall();
		case D2DServerInfoModel.ERROR:
		case D2DServerInfoModel.TERMINATE:
			return UIContext.Constants.licenseFailure()+ " ("+count+")";
		case D2DServerInfoModel.EXPIRED:
			return UIContext.Constants.licenseExpired();
		case D2DServerInfoModel.WG_COUNT:
			return UIContext.Constants.licenseWGCount();
		case D2DServerInfoModel.VALID:
//		case D2DServerInfoModel.BETA_EXPIRED:
//			return UIContext.Constants.licensed();
			return "";
		case D2DServerInfoModel.TRIAL:
			return UIContext.Constants.licenseTrial();
		case D2DServerInfoModel.WILL_EXPIRE:
			return UIContext.Constants.licenseWillExpire();
		default:
			return UIContext.Constants.NA();
		}
	}

}
