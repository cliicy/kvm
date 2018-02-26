package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.PasswordField;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreTargetModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

public class SPLocationSettings extends LayoutContainer {

	private TextField<String> userName;
	private PasswordField password;
	private PasswordField confirmPassword;
	private SimpleComboBox<String> application;
	private NumberField lengthOftime;
	private LabelField userNameLabel;
	private LabelField passwordLabel;
	private LabelField confirmPasswordLabel;
	private LabelField nfsShareOptionLable;
	private TextField<String> nfsShareOption;
	private LabelField header;
	private ServiceInfoModel currentServer;
	private int MAX_Field_WIDTH = 150;
	private Image image;
	private String uuid;

	private static final HomepageServiceAsync services = GWT.create(HomepageService.class);

	SelectionChangedListener<SimpleComboValue<String>> changeListener = new SelectionChangedListener<SimpleComboValue<String>>() {

		@Override
		public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
			String application = se.getSelectedItem().getValue().toLowerCase();
			if (Utils.NFS.equals(application) || application.contains(Utils.NFS)) {
				nfsShareOptionLable.show();
				nfsShareOption.show();
				header.hide();
				userNameLabel.hide();
				passwordLabel.hide();
				confirmPasswordLabel.hide();
				userName.hide();
				password.hide();
				confirmPassword.hide();
				image.setVisible(true);
				image.setTitle(UIContext.Constants.shareInLocal());
			} else {
				nfsShareOptionLable.hide();
				nfsShareOption.hide();
				header.show();
				userNameLabel.show();
				passwordLabel.show();
				confirmPasswordLabel.show();
				userName.show();
				password.show();
				confirmPassword.show();
				if ("WebDav".equalsIgnoreCase(application)) {
					image.setVisible(true);
					image.setTitle(UIContext.Constants.shareInInternet());
				} else {
					image.setVisible(false);
				}
			}
		}
	};

	public SPLocationSettings(ServiceInfoModel currentServer) {
		this.currentServer = currentServer;
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setCellPadding(2);
		this.setLayout(layout);
		defineShareScriptSettings();
		this.add(defineTargetServerSettings());
		defineAdvanceSettings();
		initLocationInfo();
	}

	private void defineShareScriptSettings() {

		LayoutContainer container = new LayoutContainer();
		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(2);
		tlConnSettings.setCellPadding(2);
		tlConnSettings.setCellSpacing(5);
		container.setLayout(tlConnSettings);
		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("15%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("28%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		LabelField label = new LabelField(UIContext.Constants.application());
		container.add(label, tableDataLabel);
		application = new SimpleComboBox<String>();
		application.addSelectionChangedListener(changeListener);
		application.setWidth(130);
		application.setTypeAhead(true);
		application.setEditable(false);
		application.setForceSelection(false);
		application.setTriggerAction(TriggerAction.ALL);
		application.setAllowBlank(false);

		HorizontalPanel cogo = new HorizontalPanel();
		image = new Image();
		image.setResource(UIContext.IconBundle.download());
		cogo.add(application);
		LabelField widthLabel = new LabelField("");
		widthLabel.setWidth(20);
		cogo.add(widthLabel);
		cogo.add(image);

		container.add(cogo, tableDataField);
		this.add(container);
	}

	private FieldSet defineTargetServerSettings() {
		FieldSet targetServerFieldSet = new FieldSet();
		targetServerFieldSet.setHeading(UIContext.Constants.credentialSetting());
		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(2);
		tlConnSettings.setCellPadding(2);
		tlConnSettings.setCellSpacing(2);
		targetServerFieldSet.setLayout(tlConnSettings);

		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("15%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);

		TableData tableDataField = new TableData();
		tableDataField.setWidth("28%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);

		TableData tableHeader = new TableData();
		tableHeader.setColspan(2);

		header = new LabelField(UIContext.Constants.shareHeader());

		targetServerFieldSet.add(header, tableHeader);

		nfsShareOptionLable = new LabelField(UIContext.Constants.nfsShareOptionLabel());
		targetServerFieldSet.add(nfsShareOptionLable, tableDataLabel);
		nfsShareOption = new TextField<String>();
		nfsShareOption.setToolTip(UIContext.Constants.nfsShareOptionToolTip());
		targetServerFieldSet.add(nfsShareOption, tableDataField);

		userNameLabel = new LabelField(UIContext.Constants.userName());
		targetServerFieldSet.add(userNameLabel, tableDataLabel);
		userName = new TextField<String>();
		userName.setAllowBlank(false);
		userName.getMessages().setBlankText(UIContext.Messages.validateUserName());
		targetServerFieldSet.add(userName, tableDataField);

		passwordLabel = new LabelField(UIContext.Constants.password());
		targetServerFieldSet.add(passwordLabel, tableDataLabel);
		password = new PasswordField(MAX_Field_WIDTH);
		password.setAllowBlank(false);
		password.setPassword(true);

		password.getMessages().setBlankText(UIContext.Messages.validatePassWord());
		targetServerFieldSet.add(password, tableDataField);
		confirmPasswordLabel = new LabelField(UIContext.Constants.confirmPassword());
		targetServerFieldSet.add(confirmPasswordLabel, tableDataLabel);
		confirmPassword = new PasswordField(MAX_Field_WIDTH);
		confirmPassword.setAllowBlank(false);
		confirmPassword.setPassword(true);
		confirmPassword.addListener(Events.Blur, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {

				if (password.isValid() && !password.getValue().equals(confirmPassword.getValue())) {
					confirmPassword.markInvalid(UIContext.Constants.notMatchPassword());
				}
			}

		});

		password.addListener(Events.Blur, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {

				if (password.isValid() && Utils.containSpecialCharacter(password.getRawValue())) {
					password.markInvalid(UIContext.Constants.canNotContainSC());
				}
			}

		});

		userName.addListener(Events.Blur, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {

				if (userName.isValid() && Utils.containSpecialCharacter(userName.getRawValue())) {
					userName.markInvalid(UIContext.Constants.canNotContainSC());
				}
			}

		});
		targetServerFieldSet.add(confirmPassword, tableDataField);

		return targetServerFieldSet;
	}

	private void defineAdvanceSettings() {
		FieldSet advancedFieldSet = new FieldSet();
		advancedFieldSet.setHeading(UIContext.Constants.advancedSetting());
		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(2);
		tlConnSettings.setCellPadding(2);
		tlConnSettings.setCellSpacing(2);
		advancedFieldSet.setLayout(tlConnSettings);

		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("15%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);

		TableData tableDataField = new TableData();
		tableDataField.setWidth("28%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField label = new LabelField(UIContext.Constants.lengthOftime());
		advancedFieldSet.add(label, tableDataLabel);
		lengthOftime = new NumberField();
		lengthOftime.setWidth(70);
		lengthOftime.setAllowDecimals(false);
		lengthOftime.setAllowNegative(false);
		lengthOftime.setAllowBlank(false);
		lengthOftime.getMessages().setBlankText(UIContext.Constants.blackMessage());
		lengthOftime.setToolTip(UIContext.Constants.zeroisforever());

		HorizontalPanel logo = new HorizontalPanel();
		logo.add(lengthOftime);
		LabelField c = new LabelField("");
		c.setWidth(20);
		logo.add(c);
		LabelField hour = new LabelField(UIContext.Constants.shareHours());
		logo.add(hour);
		advancedFieldSet.add(logo, tableDataField);
		this.add(advancedFieldSet);
	}

	protected void initLocationInfo() {
		services.getScripts(currentServer, Utils.SCRIPT_TYPE_SHARE, new BaseAsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(List<String> result) {
				if (result.size() > 0) {
					application.add(result);
					application.setSimpleValue(result.get(0));
				}
			}
		});
	}

	public boolean validate() {

		if (!application.isValid()) {
			return false;
		}

		String type = application.getSimpleValue().toLowerCase();
		if (Utils.NFS.equals(type) || type.contains(Utils.NFS)) {
			if (lengthOftime.isValid()) {
				return true;
			} else {
				return false;
			}
		}

		if (userName.isValid() && Utils.containSpecialCharacter(userName.getValue())) {
			userName.markInvalid(UIContext.Constants.canNotContainSC());
			return false;
		}

		if (password.isValid() && Utils.containSpecialCharacter(password.getValue())) {
			password.markInvalid(UIContext.Constants.canNotContainSC());
			return false;
		}

		if (password.isValid() && !password.getValue().equals(confirmPassword.getValue())) {
			confirmPassword.markInvalid(UIContext.Constants.notMatchPassword());
			return false;
		}
		if (userName.isValid() && password.isValid() && lengthOftime.isValid()) {
			return true;
		} else {
			return false;
		}
	}

	public void save(RestoreModel restrorModel) {
		restrorModel.setLengthOftime(lengthOftime.getValue().intValue());
		restrorModel.setApplication(application.getRawValue());
		restrorModel.setNfsShareOption(nfsShareOption.getRawValue());
		List<RestoreTargetModel> result = new ArrayList<RestoreTargetModel>();
		RestoreTargetModel target = new RestoreTargetModel();
		target.setUserName(userName.getValue());
		target.setPassword(password.getPasswordValue());
		target.setAddress(restrorModel.getMachineName());
		result.add(target);
		if (restrorModel.getJobName() == null) {
			String curTimeStr = Utils.formatDate(new Date());
			restrorModel.setJobName(UIContext.Constants.shareRecoveryPoint() + "-" + curTimeStr);
		}
		restrorModel.setRestoreTargetList(result);
	}

	public void refreshData(RestoreModel restrorModel) {
		uuid = restrorModel.getUuid();
		lengthOftime.setValue(restrorModel.getLengthOftime());
		application.setSimpleValue(restrorModel.getApplication());
		String type = restrorModel.getApplication().toLowerCase();
		if (Utils.NFS.equals(type) || type.contains(Utils.NFS)) {
			nfsShareOption.setValue(restrorModel.getNfsShareOption());
		} else {
			userName.setValue(restrorModel.getRestoreTargetList().get(0).getUserName());
			password.setPasswordValue(restrorModel.getRestoreTargetList().get(0).getPassword());
			confirmPassword.setPasswordValue(restrorModel.getRestoreTargetList().get(0).getPassword());
		}
	}

	public void validatePassword(final BaseAsyncCallback<Boolean> callBack) {
		services.validateSharePointUserName(currentServer, userName.getRawValue(), uuid,
				new BaseAsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						callBack.onFailure(caught);
					}

					@Override
					public void onSuccess(Boolean result) {
						if (!result) {
							userName.markInvalid(UIContext.Constants.userNameExist());
						}
						callBack.onSuccess(result);
					}
				});
	}

}
