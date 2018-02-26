package com.ca.arcserve.linuximaging.ui.client.restore;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.PasswordField;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;

public class BMRAdvancedSettings extends AdvancedSettings {

	private static final int FIELD_WIDTH = 200;
	private CheckBox wakeupOnLan;
	private TextField<String> username;
	private PasswordField password;
	private PasswordField rePassword;
	private DisclosurePanel panel;
	private TextField<String> volumeDevice;
	private TextField<String> path;
	private TextArea excludeDisk;
	private LayoutContainer container;
	private Label moreLabel;
	private CheckBox reboot;

	public BMRAdvancedSettings(RestoreWindow window) {
		super(window);
		this.setHeight(RestoreWindow.RIGHT_PANEL_HIGHT + 20);
		this.setWidth(RestoreWindow.RIGHT_PANEL_WIDTH - 10);
		this.setScrollMode(Scroll.AUTOY);
	}

	private DisclosurePanel getWakeupOnLanSettings() {
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants
				.wakeupOnLanSettings());

		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		container.setLayout(layout);

		LabelField label = new LabelField(
				UIContext.Constants.wakeupOnLanDescription());
		container.add(label);

		wakeupOnLan = new CheckBox();
		wakeupOnLan.addStyleName("restoreWizardTopSpacing");
		wakeupOnLan.setBoxLabel(UIContext.Constants.enableWakeupOnLan());

		container.add(wakeupOnLan);
		panel.add(container);
		if (!UIContext.selectedServerVersionInfo.isSupportWakeOnLan()) {
			container.setEnabled(false);
		}
		return panel;
	}

	private DisclosurePanel getResetCrentialSettings() {
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants
				.credentialSettings());

		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(2);
		layout.setCellPadding(3);
		container.setLayout(layout);

		TableData td = new TableData();
		td.setColspan(2);
		LabelField label = new LabelField(
				UIContext.Constants.credentialSDescription());
		container.add(label, td);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");

		TableData tdField = new TableData();
		tdField.setWidth("80%");

		label = new LabelField(UIContext.Constants.userName()
				+ UIContext.Constants.delimiter());
		container.add(label, tdLabel);

		username = new TextField<String>();
		username.setWidth(FIELD_WIDTH);
		container.add(username, tdField);

		label = new LabelField(UIContext.Constants.password()
				+ UIContext.Constants.delimiter());
		container.add(label, tdLabel);

		password = new PasswordField(FIELD_WIDTH);
		container.add(password, tdField);

		label = new LabelField(UIContext.Constants.retypeEncryptionPassword()
				+ UIContext.Constants.delimiter());
		container.add(label, tdLabel);

		rePassword = new PasswordField(FIELD_WIDTH);
		container.add(rePassword, tdField);

		panel.add(container);
		return panel;
	}

	private DisclosurePanel getRebootOptionSettings() {
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants
				.debugOptionSettings());

		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(2);
		layout.setCellPadding(3);
		container.setLayout(layout);

		TableData td = new TableData();
		td.setColspan(2);
		reboot = new CheckBox();
		reboot.setBoxLabel(UIContext.Constants.reboot());
		reboot.setValue(false);
		container.add(reboot);
		panel.add(container);
		return panel;
	}

	private DisclosurePanel getLocationSettings() {
		panel = Utils.getDisclosurePanel(UIContext.Constants
				.sessionLocationSettings());

		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(3);
		layout.setWidth("100%");
		container.setLayout(layout);

		TableData td = new TableData();
		td.setColspan(2);
		LabelField label = new LabelField(
				UIContext.Constants.sessionLocationDescription());
		container.add(label, td);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");

		TableData tdField = new TableData();
		tdField.setWidth("80%");

		label = new LabelField(UIContext.Constants.device()
				+ UIContext.Constants.delimiter());
		// container.add(label,tdLabel);

		volumeDevice = new TextField<String>();
		volumeDevice.setWidth(FIELD_WIDTH);
		// container.add(volumeDevice,tdField);

		label = new LabelField(UIContext.Constants.path()
				+ UIContext.Constants.delimiter());
		container.add(label, tdLabel);

		path = new TextField<String>();
		path.setWidth(FIELD_WIDTH);
		container.add(path, tdField);

		panel.add(container);

		return panel;
	}

	private DisclosurePanel getExcludeSettings() {
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants
				.excludeDiskSettings());

		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(3);
		layout.setWidth("100%");
		container.setLayout(layout);

		TableData td = new TableData();
		td.setColspan(2);
		LabelField label = new LabelField(
				UIContext.Constants.excludeDiskDescription());
		container.add(label, td);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");
		tdLabel.setVerticalAlign(VerticalAlignment.TOP);
		TableData tdField = new TableData();
		tdField.setWidth("80%");

		label = new LabelField(UIContext.Constants.disk()
				+ UIContext.Constants.delimiter());
		container.add(label, tdLabel);

		excludeDisk = new TextArea();
		excludeDisk.setWidth(200);
		container.add(excludeDisk, tdField);

		panel.add(container);

		return panel;
	}

	private LayoutContainer getMoreSettings() {
		LayoutContainer moreSettings = new LayoutContainer();
		TableLayout moreLayout = new TableLayout();
		moreLayout.setWidth("100%");
		moreSettings.setLayout(moreLayout);
		TableData td = new TableData();
		td.setWidth("100%");
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		moreLabel = new Label(UIContext.Constants.showMoreSettings());
		moreLabel.setStyleName("homepage_header_allfeeds_link_label");
		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (container.isVisible()) {
					moreLabel.setText(UIContext.Constants.showMoreSettings());
					container.hide();
				} else {
					moreLabel.setText(UIContext.Constants.hideMoreSettings());
					container.show();
				}
			}
		};
		moreLabel.addClickHandler(handler);
		moreSettings.add(moreLabel, td);
		return moreSettings;
	}

	public boolean validate() {

		if (password.getPasswordValue() != null
				&& !password.getPasswordValue().equals(
						rePassword.getPasswordValue())) {
			Utils.showMessage(UIContext.productName, MessageBox.ERROR,
					UIContext.Constants.passwordNotSame());
			return false;
			// }else if(restoreWindow.isModify && password.getPasswordValue() ==
			// null){
			// if (PasswordField.PSEUDO_PASSWORD.equals(password.getValue())
			// && PasswordField.PSEUDO_PASSWORD.equals(rePassword.getValue())) {
			// return true;
			// }else
			// if(PasswordField.PSEUDO_PASSWORD.equals(password.getValue())
			// || PasswordField.PSEUDO_PASSWORD.equals(rePassword.getValue())){
			// Utils.showMessage(UIContext.productName, MessageBox.ERROR,
			// UIContext.Constants.passwordNotSame());
			// return false;
			// }
		}
		return true;
	}

	public void refreshData() {
		super.refreshData();
		RestoreModel model = restoreWindow.restoreModel;
		if (model.getEnableWakeOnLan() != null && model.getEnableWakeOnLan()) {
			wakeupOnLan.setValue(true);
		}
		if (model.getNewUsername() != null) {
			username.setValue(model.getNewUsername());
		}
		if (model.getNewPassword() != null) {
			password.setPasswordValue(model.getNewPassword());
			rePassword.setPasswordValue(model.getNewPassword());
		}
		if (!UIContext.selectedServerVersionInfo.isLiveCD()
				&& model.getLocalDevice() != null) {
			volumeDevice.setValue(model.getLocalDevice());
		}
		if (!UIContext.selectedServerVersionInfo.isLiveCD()
				&& model.getLocalPath() != null) {
			path.setValue(model.getLocalPath());
		}
		if (model.getExcludeTargetDisks() != null) {
			excludeDisk.setValue(model.getExcludeTargetDisks());
		}
		if (model.getRestoreTargetList() != null
				&& model.getRestoreTargetList().size() > 0) {
			reboot.setValue(!model.getRestoreTargetList().get(0).getReboot());
		}
	}

	public void save() {
		super.save();
		restoreWindow.restoreModel.setEnableWakeOnLan(wakeupOnLan.getValue());
		restoreWindow.restoreModel.setNewUsername(username.getValue());
		restoreWindow.restoreModel.setNewPassword(password.getPasswordValue());

		if (!UIContext.selectedServerVersionInfo.isLiveCD()) {
			restoreWindow.restoreModel.setLocalDevice(volumeDevice.getValue());
			restoreWindow.restoreModel.setLocalPath(path.getValue());
		}
		restoreWindow.restoreModel
				.setExcludeTargetDisks(excludeDisk.getValue());
		if (restoreWindow.restoreModel.getRestoreTargetList() != null
				&& restoreWindow.restoreModel.getRestoreTargetList().size() > 0) {
			restoreWindow.restoreModel.getRestoreTargetList().get(0)
					.setReboot(!reboot.getValue());
		}
	}

	@Override
	protected void addOtherPanel() {
		add(getPrePostScriptPanel());
		add(getMoreSettings());
		container = new LayoutContainer();
		container.add(getResetCrentialSettings());
		getLocationSettings();
		if (!UIContext.selectedServerVersionInfo.isLiveCD()) {
			container.add(panel);
		}
		container.add(getExcludeSettings());
		container.add(getWakeupOnLanSettings());
		container.add(getRebootOptionSettings());
		container.hide();
		this.add(container);
	}
	
	protected void setPanelVisable(boolean isVisable){
		panel.setVisible(isVisable);
	}
}
