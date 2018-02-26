package com.ca.arcserve.linuximaging.ui.client.restore;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreTargetModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class BMRTargetSummaryPanel extends FieldSet {
	public final static int MAX_FIELD_WIDTH = 300;
	
	public LabelField lblKeepSource;
	public LabelField txtKeepSource;
	
	public LabelField txtHostName;
	public LabelField txtIPAddress;
	public LabelField txtSubnetMask;
	public LabelField txtDefaultGateway;
	public LabelField txtEnableInstanceBMR;

	public LabelField txtaAutoRestoreData;
	private LabelField lblautoRestoreData;

	private LabelField txtNetwork;
	private LabelField lblIPAddress;
	private LabelField lblSubnetMask;
	private LabelField lblDefaultGateway;
	
	private LabelField lblHostName;
	private LabelField lblNetwork;
	private LabelField lblInstanceBMR; 
	

	public BMRTargetSummaryPanel() {
		setHeading(UIContext.Constants.targetMachineSettings());
		// setStyleAttribute("margin", "5,5,5,5");

		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(2);
		tlConnSettings.setCellPadding(0);
		tlConnSettings.setCellSpacing(0);
		setLayout(tlConnSettings);

		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("70%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		lblKeepSource = new LabelField(UIContext.Constants.sourceSetting());
		lblKeepSource.setAutoWidth(true);
		add(lblKeepSource, tableDataLabel);
		txtKeepSource = new LabelField();
		add(txtKeepSource, tableDataField);

		lblHostName = new LabelField(UIContext.Constants.hostName());
		lblHostName.setAutoWidth(true);
		add(lblHostName, tableDataLabel);
		txtHostName = new LabelField();
		add(txtHostName, tableDataField);

		lblNetwork = new LabelField();
		lblNetwork.setText(UIContext.Constants.network());
		lblHostName.setAutoWidth(true);
		add(lblNetwork, tableDataLabel);
		txtNetwork = new LabelField();
		add(txtNetwork, tableDataField);

		lblIPAddress = new LabelField(UIContext.Constants.ipAddress());
		add(lblIPAddress, tableDataLabel);
		txtIPAddress = new LabelField();
		add(txtIPAddress, tableDataField);

		lblSubnetMask = new LabelField(UIContext.Constants.subnetMask());
		add(lblSubnetMask, tableDataLabel);
		txtSubnetMask = new LabelField();
		add(txtSubnetMask, tableDataField);

		lblDefaultGateway = new LabelField(UIContext.Constants.defaultGateway());
		add(lblDefaultGateway, tableDataLabel);

		txtDefaultGateway = new LabelField();
		add(txtDefaultGateway, tableDataField);

	    lblInstanceBMR = new LabelField(
				UIContext.Constants.enableInstanceBMR());
		add(lblInstanceBMR, tableDataLabel);
		txtEnableInstanceBMR = new LabelField();
		add(txtEnableInstanceBMR, tableDataField);

		lblautoRestoreData = new LabelField(
				UIContext.Constants.notAutoRestoreData());
		add(lblautoRestoreData, tableDataLabel);
		txtaAutoRestoreData = new LabelField();
		add(txtaAutoRestoreData, tableDataField);
	}

	public void loadTargetModel(RestoreTargetModel model) {
		
		if(model.isKeepSourceSetting()){
			lblKeepSource.show();
			txtKeepSource.show();
			txtKeepSource.setValue(UIContext.Constants.yes());
			lblHostName.hide();
			lblNetwork.hide();
			lblInstanceBMR.hide();
			txtHostName.hide();
			txtIPAddress.hide();
			txtSubnetMask.hide();
			txtDefaultGateway.hide();
			txtEnableInstanceBMR.hide();
			txtaAutoRestoreData.hide();
			lblautoRestoreData.hide();
			txtNetwork.hide();
			lblIPAddress.hide();
			lblSubnetMask.hide();
			lblDefaultGateway.hide();
			return;		
		}else{
			lblKeepSource.hide();
			txtKeepSource.hide();
	
			lblHostName.show();
			lblNetwork.show();
			txtHostName.show();
			txtNetwork.show();	
			lblautoRestoreData.show();
			txtaAutoRestoreData.show();
			lblInstanceBMR.show();
			txtEnableInstanceBMR.show();
		}
		
		txtHostName.setValue(model.getNetwork_HostName());
		if (model.getNetwork_IsDHCP()) {
			txtNetwork.setValue(UIContext.Constants.dhcp());
			lblIPAddress.hide();
			lblSubnetMask.hide();
			lblDefaultGateway.hide();
			txtIPAddress.hide();
			txtSubnetMask.hide();
			txtDefaultGateway.hide();
		} else {
			txtNetwork.setValue(UIContext.Constants.staticIP());
			txtIPAddress.setValue(model.getNetwork_ipAddress());
			txtSubnetMask.setValue(model.getNetwork_subnetMask());
			txtDefaultGateway.setValue(model.getNetwork_gateway());
			
			lblIPAddress.show();
			lblSubnetMask.show();
			lblDefaultGateway.show();
			txtIPAddress.show();
			txtSubnetMask.show();
			txtDefaultGateway.show();	
		}
		txtaAutoRestoreData
				.setValue(model.isAutoRestoreData() ? UIContext.Constants.yes()
						: UIContext.Constants.no());
		txtEnableInstanceBMR
				.setValue(model.isEnableInstanceBMR() ? UIContext.Constants
						.yes() : UIContext.Constants.no());
		lblautoRestoreData.setVisible(model.isEnableInstanceBMR());
		txtaAutoRestoreData.setVisible(model.isEnableInstanceBMR());
	}

}