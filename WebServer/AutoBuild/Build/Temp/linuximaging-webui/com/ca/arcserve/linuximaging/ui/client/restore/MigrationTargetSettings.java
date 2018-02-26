package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreTargetModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class MigrationTargetSettings extends BMRTargetSettings {

	protected CheckBox checkBox;
	protected HorizontalPanel networkPanel;

	public MigrationTargetSettings(RestoreWindow restoreWindow) {
		super(restoreWindow);

		checkBox.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				if (checkBox.getValue()) {
					txtHostName.disable();
					networkPanel.disable();
					enableStaticIPSettings(false);
				} else {
					txtHostName.enable();
					networkPanel.enable();
					if (staticIPRadio.getValue()) {
						enableStaticIPSettings(true);
					}
				}
			}

		});
	}

	protected void saveInstanceBMR(RestoreTargetModel model) {
		model.setEnableInstanceBMR(false);
		model.setAutoRestoreData(false);
	}

	protected void refreshInstanceBMR(RestoreTargetModel model) {

	}

	protected void addInstanceBMROption(LayoutContainer container) {

	}

	protected FieldSet defineTargetMachineSettings() {
		FieldSet hostNameFieldSet = new FieldSet();
		hostNameFieldSet
				.setHeading(UIContext.Constants.targetMachineSettings());

		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(2);
		tlConnSettings.setCellPadding(5);
		tlConnSettings.setCellSpacing(5);
		hostNameFieldSet.setLayout(tlConnSettings);

		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("20%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("80%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);

		TableData tableDataCls = new TableData();
		tableDataCls.setColspan(2);
		tableDataCls.setHorizontalAlign(HorizontalAlignment.LEFT);

		checkBox = new CheckBox();
		checkBox.setBoxLabel(UIContext.Constants.sourceSetting());
		hostNameFieldSet.add(checkBox, tableDataCls);

		LabelField lblHostName = new LabelField(UIContext.Constants.hostName());
		lblHostName.setAutoWidth(true);
		hostNameFieldSet.add(lblHostName, tableDataLabel);
		txtHostName = new TextField<String>();
		txtHostName.setWidth(MAX_FIELD_WIDTH);
		hostNameFieldSet.add(txtHostName, tableDataField);

		LabelField lblNetwork = new LabelField();
		lblNetwork.setText(UIContext.Constants.network());
		lblNetwork.setAutoWidth(true);
		hostNameFieldSet.add(lblNetwork, tableDataLabel);

		networkPanel = getNetworkPanel();
		hostNameFieldSet.add(networkPanel, tableDataField);

		lblIPAddress = new LabelField(UIContext.Constants.ipAddress());
		hostNameFieldSet.add(lblIPAddress, tableDataLabel);
		txtIPAddress = new TextField<String>();
		txtIPAddress.setWidth(MAX_FIELD_WIDTH);
		hostNameFieldSet.add(txtIPAddress, tableDataField);

		lblSubnetMask = new LabelField(UIContext.Constants.subnetMask());
		hostNameFieldSet.add(lblSubnetMask, tableDataLabel);
		txtSubnetMask = new TextField<String>();
		txtSubnetMask.setWidth(MAX_FIELD_WIDTH);
		hostNameFieldSet.add(txtSubnetMask, tableDataField);

		lblDefaultGateway = new LabelField(UIContext.Constants.defaultGateway());
		hostNameFieldSet.add(lblDefaultGateway, tableDataLabel);
		txtDefaultGateway = new TextField<String>();
		txtDefaultGateway.setWidth(MAX_FIELD_WIDTH);
		hostNameFieldSet.add(txtDefaultGateway, tableDataField);
		enableStaticIPSettings(false);
		loadHostIpAndMac();
		return hostNameFieldSet;
	}

	public void refreshNetworkSettings(RestoreModel model) {
		checkBox.setValue(true);
	}

	protected void loadSelectedTargetModel(RestoreTargetModel model) {
		txtAddress.setValue(model.getAddress());
		if (model.isKeepSourceSetting()) {
			checkBox.setValue(true);
		} else {
			refreshNetworkSetting(model);
		}
	}

	public void save() {
		List<RestoreTargetModel> list = new ArrayList<RestoreTargetModel>();
		if (checkBox.getValue()) {
			RestoreTargetModel target = new RestoreTargetModel();
			target.setAddress(txtAddress.getValue().trim());
			target.setKeepSourceSetting(true);
			target.setUseMac(isMacAddress());
			saveInstanceBMR(target);
			list.add(target);
		} else {
			RestoreTargetModel target = saveRestoreTargetModel();
			list.add(target);
		}
		restoreWindow.restoreModel.setRestoreTargetList(list);
	}

	public boolean validate() {
		if (checkBox.getValue()) {
			if (txtAddress.getValue() == null) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.macOrIPAddressEmptyMessage());
				return false;
			}
			return true;
		} else {
			return validateRestoreTargetModel();
		}
	}
}