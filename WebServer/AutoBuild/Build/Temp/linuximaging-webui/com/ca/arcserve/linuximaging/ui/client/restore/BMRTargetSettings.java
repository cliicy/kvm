package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.BackupMachineModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreTargetModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.ServerCapabilityModel;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RadioButton;

public class BMRTargetSettings extends TargetMachineSettings {

	protected TextField<String> txtAddress;
	protected TextField<String> txtHostName;
	// private Radio macRadio;
	// private Radio ipRadio;
	private RadioButton dhcpRadio;
	protected RadioButton staticIPRadio;
	private CheckBox enableInstanceBMR;
	private CheckBox notAutoRestoreData;
	protected LabelField lblIPAddress;
	protected TextField<String> txtIPAddress;
	protected LabelField lblSubnetMask;
	protected TextField<String> txtSubnetMask;
	protected LabelField lblDefaultGateway;
	protected TextField<String> txtDefaultGateway;
	private List<String> localHostAndMac = new ArrayList<String>();

	public BMRTargetSettings(RestoreWindow restoreWindow) {
		super(restoreWindow);
	}

	protected LayoutContainer defineTargetTable() {
		LayoutContainer bmrTargetTable = new LayoutContainer();

		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(2);
		layout.setCellPadding(10);
		layout.setCellSpacing(10);
		bmrTargetTable.setLayout(layout);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("80%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);

		LabelField lblAddress = new LabelField(
				UIContext.Constants.macOrIPAddress());
		txtAddress = new TextField<String>();
		txtAddress.setWidth(MAX_FIELD_WIDTH);
		txtAddress.setStyleAttribute("padding-left", "5px");
		Utils.addToolTip(txtAddress,
				UIContext.Constants.macOrIPAddressToolTip());
		bmrTargetTable.add(lblAddress, tdLabel);
		bmrTargetTable.add(txtAddress, tdField);
		txtAddress.disable();

		FieldSet targetMachineFieldSet = defineTargetMachineSettings();
		bmrTargetTable.add(targetMachineFieldSet, tdColspan);

		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);

		LayoutContainer container = new LayoutContainer();
		TableLayout containerLayout = new TableLayout();
		containerLayout.setWidth("100%");
		containerLayout.setCellPadding(2);
		containerLayout.setCellSpacing(2);
		container.setLayout(containerLayout);
		addInstanceBMROption(container);

		bmrTargetTable.add(container, tdColspan);
		return bmrTargetTable;
	}

	protected void addInstanceBMROption(LayoutContainer container) {
		enableInstanceBMR = new CheckBox();
		enableInstanceBMR.setBoxLabel(UIContext.Constants.enableInstanceBMR());
		enableInstanceBMR.addListener(Events.Change,
				new Listener<FieldEvent>() {
					@Override
					public void handleEvent(FieldEvent be) {
						if (enableInstanceBMR.getValue()) {
							if(restoreWindow.restoreModel.getRecoveryPoint().getVersion() != null && restoreWindow.restoreModel.getRecoveryPoint().getVersion()< 6.0){
								Utils.showMessage(UIContext.Constants.productName(),
										MessageBox.ERROR, UIContext.Constants.oldSessionNotSupportInstantBMR());
								enableInstanceBMR.setValue(false);
								return;
							}
							notAutoRestoreData.enable();
							restoreWindow.getAdvancedSettings().setSchedulerContainerVisable(false);
							restoreWindow.getAdvancedSettings().setPanelVisable(false);
						} else {
							notAutoRestoreData.disable();
							restoreWindow.getAdvancedSettings().setSchedulerContainerVisable(true);
							restoreWindow.getAdvancedSettings().setPanelVisable(true);
						}
					}

				});
		container.add(enableInstanceBMR);
		notAutoRestoreData = new CheckBox();
		notAutoRestoreData.setBoxLabel(UIContext.Constants.notAutoRestoreData());
		notAutoRestoreData.disable();
		container.add(notAutoRestoreData);
	}

	protected FieldSet defineTargetMachineSettings() {
		FieldSet hostNameFieldSet = new FieldSet();
		hostNameFieldSet
				.setHeading(UIContext.Constants.targetMachineSettings());
		// hostNameFieldSet.setStyleAttribute("margin", "5,5,5,5");

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

		HorizontalPanel networkPanel = getNetworkPanel();
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

	protected void enableStaticIPSettings(boolean enable) {
		if (enable) {
			lblIPAddress.enable();
			txtIPAddress.enable();
			lblSubnetMask.enable();
			txtSubnetMask.enable();
			lblDefaultGateway.enable();
			txtDefaultGateway.enable();
		} else {
			lblIPAddress.disable();
			txtIPAddress.disable();
			lblSubnetMask.disable();
			txtSubnetMask.disable();
			lblDefaultGateway.disable();
			txtDefaultGateway.disable();
		}
	}

	protected HorizontalPanel getNetworkPanel() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(2);
		dhcpRadio = new RadioButton("ipOption", UIContext.Constants.dhcp());
		dhcpRadio.setStyleName("x-form-field");
		dhcpRadio.setValue(true);
		panel.add(dhcpRadio);

		LabelField space = new LabelField();
		space.setWidth(85);
		panel.add(space);

		// dhcp.addListener(Events.Change,
		// ArchiveDestinationSettingsChangeHandler);
		staticIPRadio = new RadioButton("ipOption",
				UIContext.Constants.staticIP());
		staticIPRadio.setStyleName("x-form-field");
		// staticIPRadio.setStyleAttribute("padding-left", "40px");

		ClickHandler handler = new ClickHandler() {
			public void onClick(ClickEvent e) {
				if (e.getSource() == dhcpRadio) {
					enableStaticIPSettings(false);
				} else {
					enableStaticIPSettings(true);
				}
			}
		};
		dhcpRadio.addClickHandler(handler);
		staticIPRadio.addClickHandler(handler);

		panel.add(staticIPRadio);
		return panel;
	}

	protected void loadSelectedTargetModel(RestoreTargetModel model) {
		txtAddress.setValue(model.getAddress());
		refreshNetworkSetting(model);
		refreshInstanceBMR(model);
	}

	protected void refreshNetworkSetting(RestoreTargetModel model) {
		txtHostName.setValue(model.getNetwork_HostName());
		dhcpRadio.setValue(model.getNetwork_IsDHCP());
		staticIPRadio.setValue(!model.getNetwork_IsDHCP());
		if (model.getNetwork_IsDHCP()) {
			enableStaticIPSettings(false);
		} else {
			enableStaticIPSettings(true);
			txtIPAddress.setValue(model.getNetwork_ipAddress());
			txtSubnetMask.setValue(model.getNetwork_subnetMask());
			txtDefaultGateway.setValue(model.getNetwork_gateway());
		}
	}

	protected void refreshInstanceBMR(RestoreTargetModel model) {
		enableInstanceBMR.setValue(model.isEnableInstanceBMR());
		if (model.isEnableInstanceBMR()) {
			notAutoRestoreData.setValue(!model.isAutoRestoreData());
		}
	}

	protected RestoreTargetModel saveRestoreTargetModel() {
		RestoreTargetModel model = new RestoreTargetModel();
		model.setAddress(txtAddress.getValue().trim());
		model.setUseMac(isMacAddress());
		model.setNetwork_HostName(txtHostName.getValue());
		saveInstanceBMR(model);
		if (dhcpRadio.getValue()) {
			model.setNetwork_IsDHCP(true);
		} else if (staticIPRadio.getValue()) {
			model.setNetwork_IsDHCP(false);
			model.setNetwork_ipAddress(txtIPAddress.getValue());
			model.setNetwork_subnetMask(txtSubnetMask.getValue());
			model.setNetwork_gateway(txtDefaultGateway.getValue());
		}
		return model;
	}

	protected void saveInstanceBMR(RestoreTargetModel model) {
		model.setEnableInstanceBMR(enableInstanceBMR.getValue());
		if (enableInstanceBMR.getValue()) {
			model.setAutoRestoreData(!notAutoRestoreData.getValue());
		} else {
			model.setAutoRestoreData(false);
		}
	}

	protected Boolean isMacAddress() {
		if (txtAddress.getValue().contains(Utils.MAC_SEPARATOR)) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean validateRestoreTargetModel() {
		if (txtAddress.getValue() == null) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR,
					UIContext.Constants.macOrIPAddressEmptyMessage());
			return false;
		} else if (txtAddress.getValue().contains(Utils.MAC_SEPARATOR)) {
			if (!Utils.validateMacAddress(txtAddress.getValue().trim())) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.macOrIPAddressMessage());
				return false;
			}

			ServerCapabilityModel model = restoreWindow.backupServerSettings
					.getServerCapability();
			if (model != null && !model.getRestoreUtilInstalled()
					&& restoreWindow.getRestoreType() == RestoreType.BMR) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.noRestoreUtilWarnningBMR());
				return false;
			}
			if (model != null && !model.getRestoreUtilRunning()
					&& restoreWindow.getRestoreType() == RestoreType.BMR) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.noRestoreUtilRunningWarnningBMR());
				return false;
			}
		} else if (txtAddress.getValue().contains(Utils.IP_SEPARATOR)) {
			if (!Utils.validateIPAddress(txtAddress.getValue().trim())) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.macOrIPAddressMessage());
				return false;
			}
		} else {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR,
					UIContext.Constants.macOrIPAddressMessage());
			return false;
		}
		// if(targetGridStore.getModels().size()>0){
		// for(RestoreTargetModel model : targetGridStore.getModels()){
		// if(model.getAddress().equalsIgnoreCase(txtAddress.getValue())){
		// Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.macOrIPAddressDuplicateMessage());
		// return false;
		// }
		// }
		// }
		if (txtHostName.getValue() == null) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR, UIContext.Constants.nodeNameMessage());
			return false;
		} else {
			if (!Utils.validateLinuxHostName(txtHostName.getValue().trim())) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR, UIContext.Constants.nodeNameMessage());
				return false;
			}
		}
		if (staticIPRadio.getValue()) {
			String ipAddress = txtIPAddress.getValue();
			String subnetMask = txtSubnetMask.getValue();
			String gateway = txtDefaultGateway.getValue();
			if (!Utils.validateIPAddress(ipAddress)) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.ipAddressMessage());
				return false;
			}

			if (!Utils.validateIPAddress(subnetMask)) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.subnetMaskMessage());
				return false;
			}

			if (!Utils.validateIPAddress(gateway)) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.defaultGatewayMessage());
				return false;
			}
		}

		if (enableInstanceBMR != null && enableInstanceBMR.getValue()) {
			if (localHostAndMac.contains(txtAddress.getValue().toUpperCase())) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.preventRestoreServerSelf());
				return false;
			}
		}

		return true;
	}

	public boolean validate() {
		return validateRestoreTargetModel();
	}

	public void save() {
		List<RestoreTargetModel> list = new ArrayList<RestoreTargetModel>();
		RestoreTargetModel target = saveRestoreTargetModel();
		list.add(target);
		restoreWindow.restoreModel.setRestoreTargetList(list);
	}

	public void refreshData() {
		RestoreModel model = restoreWindow.restoreModel;

		if (model.getRestoreTargetList() != null
				&& model.getRestoreTargetList().size() > 0) {
			loadSelectedTargetModel(model.getRestoreTargetList().get(0));
		}
	}

	public void refreshNetworkSettings(RestoreModel model) {
		if (model.getRestoreTargetList() != null
				&& model.getRestoreTargetList().size() > 0) {
			refreshNetworkSetting(model.getRestoreTargetList().get(0));
		}
	}

	@Override
	public void refreshPart() {
		if (restoreWindow.recoveryPointSettings.getMachineMode()
				.getMachineType() == BackupMachineModel.TYPE_HBBU_MACHINE) {
			enableInstanceBMR.setValue(true);
			enableInstanceBMR.disable();
		} else {
			enableInstanceBMR.enable();
		}
	}

	protected void loadHostIpAndMac() {
		service.getLocalIpAndMac(new BaseAsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(List<String> result) {
				txtAddress.enable();
				localHostAndMac.addAll(result);
			}
		});
	}
	
	public void refreshWhenShowing(){
		if(restoreWindow.restoreModel.getRecoveryPoint().getVersion() != null && restoreWindow.restoreModel.getRecoveryPoint().getVersion()< 6.0){
			enableInstanceBMR.setValue(false);
		}
	}
}
