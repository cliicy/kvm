package com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.BaseSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.PasswordField;
import com.ca.arcserve.linuximaging.ui.client.common.TooltipSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.DatastoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RpsDataStoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

public class AddBackupStorageWindow extends Dialog {

	private static final HomepageServiceAsync backupService = GWT
			.create(HomepageService.class);
	private static final BackupStorageServiceAsync storageService = GWT
			.create(BackupStorageService.class);
	public static final int TYPE_ADD = 1;
	public static final int TYPE_MODIFY = 2;
	private int MAX_Field_WIDTH = 200;
	private FlexTable table;
	private CheckBox cb;
	private SimpleComboBox<String> scriptCombo;
	private NumberField freeSpace;
	private SimpleComboBox<String> unitCombo;
	private TextField<String> txtBackupLocation = new TextField<String>();
	private LabelField labelUsername;
	private LabelField labelPassword;
	private TextField<String> nameTextField;
	private PasswordField passwordTextField;
	private Radio noLimit;
	private Radio limitTo;
	private NumberField jobLimit;
	private int MAX_COMBOBOX_WIDTH = 100;
	private TooltipSimpleComboBox<String> cmbLocationType;
	private int type;
	private BackupLocationInfoModel model;
	private HomepageTab homePageTab = null;
	private LayoutContainer noScriptWarning;
	private TextField<String> hostName = new TextField<String>();
	private  TextField<String> userName = new TextField<String>();
	private  PasswordField password = new PasswordField(200);
	private  NumberField port = new NumberField();
	private  Radio https = new Radio();
	private  Radio http = new Radio();
	private  RadioGroup protocol = new RadioGroup();
	private final ComboBox<RpsDataStoreModel> dataStoreBox = new ComboBox<RpsDataStoreModel>();
	
	private final ServerInfoModel serverInfoModel = new ServerInfoModel();
	
	private ListStore<RpsDataStoreModel> rpsDataStore = new ListStore<RpsDataStoreModel>();;
	private Button refresh = new Button(UIContext.Constants.load());
	private FieldSet s3CifsShareSettings;
	//private  NumberField s3CifsSharePort = new NumberField();
	private  TextField<String> s3CifsShareUserName = new TextField<String>();
	private  PasswordField s3CifsSharePassword = new PasswordField(200);
	private HomepageServiceAsync service = GWT.create(HomepageService.class);

	private SelectionListener<ButtonEvent> refeshListener = new SelectionListener<ButtonEvent>() {
		@Override
		public void componentSelected(ButtonEvent ce) {
			if (hostName.isValid() && userName.isValid() && password.isValid()
					&& port.isValid()) {
				String hn = hostName.getRawValue();
				String un = userName.getValue();
				String pw = password.getPasswordValue();
				Integer pt = port.getValue().intValue();
				String pl = protocol.getValue().getValueAttribute();
				serverInfoModel.setServerName(hn);
				serverInfoModel.setUserName(un);
				serverInfoModel.setPasswd(pw);
				serverInfoModel.setPort(pt);
				serverInfoModel.setProtocol(pl);
				if (type == TYPE_ADD) {
					model = new BackupLocationInfoModel();
					model.setType(getType());
					model.setServerInfoModel(serverInfoModel);
				} else {
					model.setServerInfoModel(serverInfoModel);
				}
				loadRpsDatastore();
			}
		}
	};

	public AddBackupStorageWindow(HomepageTab homePageTab, int type) {

		this.homePageTab = homePageTab;
		this.type = type;
		setButtons(Dialog.YESNOCANCEL);
		setButtonAlign(HorizontalAlignment.CENTER);
		setModal(true);
		this.setResizable(false);
		this.setWidth(550);
		table = new FlexTable();
		table.setCellSpacing(5);
		table.getColumnFormatter().setWidth(0, "25%");
		table.getColumnFormatter().setWidth(1, "75%");
		table.setWidth("100%");
		if (type == TYPE_ADD) {
			this.setHeading(UIContext.Constants.addBackupStorage());
		} else {
			this.setHeading(UIContext.Constants.modifyBackupStorage());
		}
		this.addRows();
		this.add(table);
		this.getButtonById(YES).setText(UIContext.Constants.OK());
		this.getButtonById(YES).addSelectionListener(
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						if (validate())
							saveOrUpdate();
					}
				});
		this.getButtonById(NO).setText(UIContext.Constants.cancel());
		this.getButtonById(NO).addSelectionListener(
				new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						AddBackupStorageWindow.this.hide();
					}
				});
		this.getButtonById(CANCEL).setText(UIContext.Constants.help());
		this.getButtonById(CANCEL).addSelectionListener(
				new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						Utils.showURL(UIContext.helpLink
								+ HelpLinkItem.BACKUPSTORAGE);
					}
				});
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		refresh.addSelectionListener(refeshListener);
	}

	private void addRows(){
		LabelField labelType = new LabelField(UIContext.Constants.type());
		table.setWidget(0, 0, labelType);
		table.setWidget(0, 1, getBackupLocation());
		
		LabelField labelPath = new LabelField(UIContext.Constants.path());
		table.setWidget(1, 0, labelPath);
		
		txtBackupLocation.setAllowBlank(false);
		txtBackupLocation.setWidth(242);
		table.setWidget(1, 1, txtBackupLocation);
		
		labelUsername = new LabelField(UIContext.Constants.userName());
		table.setWidget(2, 0, labelUsername);
		nameTextField = new TextField<String>();
		nameTextField.setAllowBlank(false);
		nameTextField.setWidth(200);
		table.setWidget(2, 1, nameTextField);

		labelPassword = new LabelField(UIContext.Constants.password());
		table.setWidget(3, 0, labelPassword);
		passwordTextField = new PasswordField(200);
		passwordTextField.setAllowBlank(false);
		table.setWidget(3, 1, passwordTextField);

		LabelField labelConcurrent = new LabelField(UIContext.Constants.concurrentBackupJob());
		table.setWidget(4, 0, labelConcurrent);
		noLimit = new Radio();
		noLimit.setBoxLabel(UIContext.Constants.noLimit());
		table.setWidget(4, 1, noLimit);
	
		LabelField labelEmpty = new LabelField("");
		table.setWidget(5, 0, labelEmpty);

		LayoutContainer container = new LayoutContainer();
		container.setLayout(new TableLayout(2));
		limitTo = new Radio();
		limitTo.setBoxLabel(UIContext.Constants.limitTo());
		container.add(limitTo);

		jobLimit = new NumberField();
		jobLimit.setWidth(50);
		jobLimit.setEnabled(false);
		jobLimit.setAllowBlank(false);
		jobLimit.setAllowNegative(false);
		jobLimit.setMinValue(1);
		container.add(jobLimit);
		RadioGroup rg = new RadioGroup();
		rg.add(noLimit);
		rg.add(limitTo);
		rg.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				jobLimit.setEnabled(!noLimit.getValue());
			}

		});
		noLimit.setValue(true);
		table.setWidget(5, 1, container);
		table.getFlexCellFormatter().setColSpan(6, 0, 2);
		table.setWidget(6, 0, getDiskFreeSpaceAlert());
		generateNoScriptWarning();
		table.getFlexCellFormatter().setColSpan(7, 0, 2);
		table.setWidget(7, 0, noScriptWarning);
		
		addRpsRows();
		addS3CifsShare();
		cmbLocationType.setSimpleValue(UIContext.Constants.nfsShare());
	}
	
	private void addRpsRows() {
		LabelField hostNameLabel = new LabelField(
				UIContext.Constants.rpsHostName());
		hostName.setWidth(MAX_Field_WIDTH);
		hostName.setAllowBlank(false);
		hostName.getMessages().setBlankText(
				UIContext.Messages.validateHostName());
		table.setWidget(8, 0, hostNameLabel);
		table.setWidget(8, 1, hostName);
		
		LabelField userNameLabel = new LabelField(
				UIContext.Constants.userName());
		userName.setWidth(MAX_Field_WIDTH);
		userName.setAllowBlank(false);
		userName.getMessages().setBlankText(
				UIContext.Messages.validateUserName());
		table.setWidget(9, 0, userNameLabel);
		table.setWidget(9, 1, userName);

		LabelField passwordLabel = new LabelField(
				UIContext.Constants.password());
		password.setAllowBlank(false);
		password.setPassword(true);
		password.getMessages().setBlankText(
				UIContext.Messages.validatePassWord());
		table.setWidget(10, 0, passwordLabel);
		table.setWidget(10, 1, password);

		LabelField portLabel = new LabelField(
				UIContext.Constants.port());
		port.setFieldLabel(UIContext.Constants.port());
		port.setValue(8014);
		port.setMinValue(0);
		port.setMaxValue(65535);
		port.setWidth(MAX_Field_WIDTH);
		port.setAllowBlank(false);
		port.getMessages().setBlankText(UIContext.Messages.validatePort());
		port.getMessages().setMinText(UIContext.Messages.validatePortNumber());
		port.getMessages().setMaxText(UIContext.Messages.validatePortNumber());
		table.setWidget(11, 0, portLabel);
		table.setWidget(11, 1, port);

		LabelField protocolLabel = new LabelField(
				UIContext.Constants.protocol());
		https.setBoxLabel(UIContext.Constants.targetServerProtocol_Https());
		https.setValue(true);
		https.setValueAttribute("https");

		http.setBoxLabel(UIContext.Constants.targetServerProtocol_Http());
		http.setValue(true);
		http.setValueAttribute("Http");

		protocol.setFieldLabel(UIContext.Constants.protocol());
		protocol.setWidth(MAX_Field_WIDTH);
		protocol.add(http);
		protocol.add(https);
		table.setWidget(12, 0, protocolLabel);
		table.setWidget(12, 1, protocol);

		LabelField datastoreLabel = new LabelField(
				UIContext.Constants.dataStore());
		table.setWidget(13, 0, datastoreLabel);
		table.setWidget(13, 1, getDataStore());
	}

	private void addS3CifsShare(){
		s3CifsShareSettings = new FieldSet();
		s3CifsShareSettings.setHeading(UIContext.Constants.enableS3CifsShare());
		s3CifsShareSettings.setCheckboxToggle(true);
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		tableLayout.setCellPadding(3);
		tableLayout.setCellSpacing(2);
		s3CifsShareSettings.setLayout(tableLayout);
		
		TableData labelData = new TableData();
		labelData.setWidth("25%");
		
		TableData td = new TableData();
		td.setColspan(2);
		
		LabelField s3CifsDescriptionLabel = new LabelField(UIContext.Constants.s3CifsDescription());
		s3CifsShareSettings.add(s3CifsDescriptionLabel,td);
		
		LabelField s3CifsSettingsLabel = new LabelField(UIContext.Constants.s3Settings());
		s3CifsShareSettings.add(s3CifsSettingsLabel,td);
		
		LabelField userNameLabel = new LabelField(
				UIContext.Constants.userName());
		s3CifsShareUserName = new TextField<String>();
		s3CifsShareUserName.setReadOnly(true);
		s3CifsShareUserName.setEnabled(false);
		s3CifsShareUserName.setValue("root");
		s3CifsShareUserName.setWidth(MAX_Field_WIDTH);
		s3CifsShareSettings.add(userNameLabel,labelData);
		s3CifsShareSettings.add(s3CifsShareUserName);
		//table.setWidget(15, 0, userNameLabel);
		//table.setWidget(15, 1, s3CifsShareUserName);
		
		LabelField passwordLabel = new LabelField(
				UIContext.Constants.password());
		s3CifsSharePassword = new PasswordField(200);
		s3CifsSharePassword.setAllowBlank(false);
		s3CifsSharePassword.setPassword(true);
		//table.setWidget(16, 0, passwordLabel);
		//table.setWidget(16, 1, s3CifsSharePassword);
		s3CifsShareSettings.add(passwordLabel,labelData);
		s3CifsShareSettings.add(s3CifsSharePassword);
		
		/*LabelField portLabel = new LabelField(
				UIContext.Constants.port());
		s3CifsSharePort = new NumberField();
		s3CifsSharePort.setValue(8017);
		s3CifsSharePort.setMinValue(0);
		s3CifsSharePort.setMaxValue(65535);
		s3CifsSharePort.setWidth(MAX_Field_WIDTH);
		s3CifsSharePort.setAllowBlank(false);
		table.setWidget(17, 0, portLabel);
		table.setWidget(17, 1, s3CifsSharePort);*/

		
		LabelField s3CifsShareNote1 = new LabelField(
				UIContext.Constants.s3CifsNote());
		//table.getFlexCellFormatter().setColSpan(18, 0, 2);
		//table.setWidget(18, 0, s3CifsShareNote);
		s3CifsShareSettings.add(s3CifsShareNote1,td);
		
		LabelField s3CifsShareNote2 = new LabelField(
				UIContext.Constants.s3CifsNote2());
		//table.getFlexCellFormatter().setColSpan(18, 0, 2);
		//table.setWidget(18, 0, s3CifsShareNote);
		s3CifsShareSettings.add(s3CifsShareNote2,td);
		s3CifsShareSettings.addListener(Events.Collapse, new Listener<FieldSetEvent>() {

			public void handleEvent(FieldSetEvent be) {				
					s3CifsSharePassword.clearInvalid();	
					AddBackupStorageWindow.this.doLayout(true);
				}
		});
		s3CifsShareSettings.addListener(Events.Expand, new Listener<FieldSetEvent>() {

			public void handleEvent(FieldSetEvent be) {				
				s3CifsSharePassword.clearInvalid();	
			}
		});
		s3CifsShareSettings.setExpanded(false);
		table.getFlexCellFormatter().setColSpan(14, 0, 2);
		table.setWidget(14, 0, s3CifsShareSettings);
	}
	
	private LayoutContainer getDataStore() {
		LayoutContainer lc = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		lc.setLayout(tl);

		dataStoreBox.setStore(rpsDataStore);
		dataStoreBox.setFieldLabel(UIContext.Constants.dataStore());
		dataStoreBox.getMessages().setBlankText(
				UIContext.Messages.validateDataStore());
		dataStoreBox.setTypeAhead(true);
		dataStoreBox.setEditable(false);
		dataStoreBox.setTriggerAction(TriggerAction.ALL);
		dataStoreBox.setStore(rpsDataStore);
		dataStoreBox.setDisplayField("storeSharedName");
		lc.add(dataStoreBox);
		refresh.setIcon(AbstractImagePrototype.create(UIContext.IconBundle
				.connect()));
		lc.add(refresh);
		return lc;
	}

	private LayoutContainer getBackupLocation() {
		LayoutContainer lc = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		lc.setLayout(tl);

		cmbLocationType = new TooltipSimpleComboBox<String>();
		cmbLocationType.setWidth(MAX_COMBOBOX_WIDTH);
		cmbLocationType.setTriggerAction(TriggerAction.ALL);
		cmbLocationType.setEditable(false);
		cmbLocationType.add(UIContext.Constants.nfsShare());
		cmbLocationType.add(UIContext.Constants.cifsShare());
		cmbLocationType.add(UIContext.Constants.serverLocal());
		cmbLocationType.add(UIContext.Constants.rpsServe());
		cmbLocationType.add(UIContext.Constants.amazonS3());
		// cmbLocationType.setSimpleValue(UIContext.Constants.nfsShare());
		cmbLocationType
				.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<SimpleComboValue<String>> se) {
						String type = se.getSelectedItem().getValue();
						setUsernamePwdLabel();
						if (type.equals(UIContext.Constants.nfsShare())
								|| type.equals(UIContext.Constants
										.serverLocal())) {
							showNFSAndSourceLocal();
						} else if (type.equals(UIContext.Constants.rpsServe())) {
							showRPS();
						} else if(type.equals(UIContext.Constants.amazonS3())){
							showAmazonS3();
						}else {
							showCifs();
						}
						AddBackupStorageWindow.this.doLayout(true);
					}

				});
		lc.add(cmbLocationType);
		if (type == TYPE_MODIFY) {
			cmbLocationType.setEnabled(false);
			txtBackupLocation.setEnabled(false);
		}

		return lc;
	}

	private void showNFSAndSourceLocal(){
		setCredentialsEnable(false);
		nameTextField.clearInvalid();
		passwordTextField.clearInvalid();
		txtBackupLocation.removeToolTip();
		table.getRowFormatter().setVisible(1, true);
		table.getRowFormatter().setVisible(2, true);
		table.getRowFormatter().setVisible(3, true);
		table.getRowFormatter().setVisible(4, true);
		table.getRowFormatter().setVisible(5, true);
		table.getRowFormatter().setVisible(6, true);
		table.getRowFormatter().setVisible(7, true);
		table.getRowFormatter().setVisible(8, false);
		table.getRowFormatter().setVisible(9, false);
		table.getRowFormatter().setVisible(10, false);
		table.getRowFormatter().setVisible(11, false);
		table.getRowFormatter().setVisible(12, false);
		table.getRowFormatter().setVisible(13, false);
		table.getRowFormatter().setVisible(14, false);
	}
	
	private void showCifs(){
		setCredentialsEnable(true);
		txtBackupLocation.removeToolTip();
		txtBackupLocation.setToolTip(UIContext.Constants
				.tooltipForCifs());
		txtBackupLocation.getToolTip().setHeaderVisible(
				false);
		table.getRowFormatter().setVisible(1, true);
		table.getRowFormatter().setVisible(2, true);
		table.getRowFormatter().setVisible(3, true);
		table.getRowFormatter().setVisible(4, true);
		table.getRowFormatter().setVisible(5, true);
		table.getRowFormatter().setVisible(6, true);
		table.getRowFormatter().setVisible(7, true);
		table.getRowFormatter().setVisible(8, false);
		table.getRowFormatter().setVisible(9, false);
		table.getRowFormatter().setVisible(10, false);
		table.getRowFormatter().setVisible(11, false);
		table.getRowFormatter().setVisible(12, false);
		table.getRowFormatter().setVisible(13, false);
		table.getRowFormatter().setVisible(14, false);
	}
	
	private void showAmazonS3(){
		setCredentialsEnable(true);
		txtBackupLocation.removeToolTip();
		txtBackupLocation.setToolTip(UIContext.Messages.tooltipForS3("China"));
		txtBackupLocation.getToolTip().setHeaderVisible(
				false);
		table.getRowFormatter().setVisible(1, true);
		table.getRowFormatter().setVisible(2, true);
		table.getRowFormatter().setVisible(3, true);
		table.getRowFormatter().setVisible(4, true);
		table.getRowFormatter().setVisible(5, true);
		table.getRowFormatter().setVisible(6, false);
		table.getRowFormatter().setVisible(7, false);
		table.getRowFormatter().setVisible(8, false);
		table.getRowFormatter().setVisible(9, false);
		table.getRowFormatter().setVisible(10, false);
		table.getRowFormatter().setVisible(11, false);
		table.getRowFormatter().setVisible(12, false);
		table.getRowFormatter().setVisible(13, false);
		table.getRowFormatter().setVisible(14, true);
	}
	
	private void showRPS(){
		table.getRowFormatter().setVisible(1, false);
		table.getRowFormatter().setVisible(2, false);
		table.getRowFormatter().setVisible(3, false);
		table.getRowFormatter().setVisible(4, false);
		table.getRowFormatter().setVisible(5, false);
		table.getRowFormatter().setVisible(6, false);
		table.getRowFormatter().setVisible(7, false);
		table.getRowFormatter().setVisible(8, true);
		table.getRowFormatter().setVisible(9, true);
		table.getRowFormatter().setVisible(10, true);
		table.getRowFormatter().setVisible(11, true);
		table.getRowFormatter().setVisible(12, true);
		table.getRowFormatter().setVisible(13, true);
		table.getRowFormatter().setVisible(14, false);
	}
	
	private void setUsernamePwdLabel(){
		if(getType() == BackupLocationInfoModel.TYPE_AMAZON_S3){
			labelUsername.setText(UIContext.Constants.accessId());
			labelPassword.setText(UIContext.Constants.accessKey());
		}else{
			labelUsername.setText(UIContext.Constants.userName());
			labelPassword.setText(UIContext.Constants.password());
		}
	}
	
	private void loadRpsDatastore() {
		service.findRpsDataStoreByServerInfo(homePageTab.currentServer,
				serverInfoModel,
				new BaseAsyncCallback<List<RpsDataStoreModel>>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(List<RpsDataStoreModel> result) {
						rpsDataStore.removeAll();
						dataStoreBox.clear();
						for (RpsDataStoreModel rpsDataStoreModel : result) {
							rpsDataStore.add(rpsDataStoreModel);
						}
						if (result.size() > 0) {
							dataStoreBox.setValue(result.get(0));
						}
					}
				});
	}

	private void setCredentialsEnable(boolean isEnable) {
		nameTextField.setEnabled(isEnable);
		passwordTextField.setEnabled(isEnable);
	}

	private LayoutContainer getDiskFreeSpaceAlert() {
		LayoutContainer lc = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setCellSpacing(5);
		tl.setWidth("100%");
		tl.setColumns(4);
		lc.setLayout(tl);

		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setWidth("25%");
		cb = new CheckBox();
		cb.setStyleAttribute("float", "right");
		cb.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				setCifsEnable(cb.getValue());
			}

		});
		lc.add(cb, td);

		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setWidth("50%");

		LabelField label = new LabelField(
				UIContext.Constants.backupStorageFreeSpaceLessThan());
		lc.add(label, td);

		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setWidth("14%");
		freeSpace = new NumberField();
		// freeSpace.setMinValue(1);
		// freeSpace.setAllowNegative(false);
		// freeSpace.setAllowBlank(false);
		freeSpace.setWidth(60);
		lc.add(freeSpace, td);

		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		unitCombo = new BaseSimpleComboBox<String>();
		unitCombo.setWidth(50);
		unitCombo.setTriggerAction(TriggerAction.ALL);
		unitCombo.setForceSelection(true);
		unitCombo.add("%");
		unitCombo.add(UIContext.Constants.MB());
		/*
		 * unitCombo.addSelectionChangedListener(new
		 * SelectionChangedListener<SimpleComboValue<String>>(){
		 * 
		 * @Override public void selectionChanged(
		 * SelectionChangedEvent<SimpleComboValue<String>> se) {
		 * if(se.getSelectedItem().getValue().equals("%")){
		 * freeSpace.setMaxValue(99); }else{
		 * freeSpace.setMaxValue(Integer.MAX_VALUE); } }
		 * 
		 * });
		 */
		unitCombo.setSimpleValue("%");
		lc.add(unitCombo, td);

		return lc;
	}

	private LayoutContainer getScriptContainer() {
		LayoutContainer lc = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		lc.setLayout(tl);

		LabelField label = new LabelField(UIContext.Constants.runScript());
		lc.add(label);

		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		scriptCombo = new BaseSimpleComboBox<String>();
		scriptCombo.setWidth(245);
		lc.add(scriptCombo, td);
		getScript();
		return lc;
	}

	private void generateNoScriptWarning() {
		noScriptWarning = new LayoutContainer();
		noScriptWarning.setLayout(new TableLayout(2));

		Image warningImage = new Image(UIContext.IconBundle.information());
		noScriptWarning.add(warningImage);
		noScriptWarning.add(new LabelField(UIContext.Constants
				.backupStorageAlertNoScriptWarning()));
		// noScriptWarning.setVisible(false);
	}

	private void setCifsEnable(boolean isEnabled) {
		freeSpace.setEnabled(isEnabled);
		unitCombo.setEnabled(isEnabled);
	}

	public void refresh(BackupLocationInfoModel model) {
		this.model = model;
		if (model != null) {
			if (model.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
				cmbLocationType.setSimpleValue(UIContext.Constants.rpsServe());
				hostName.setValue(model.getServerInfoModel().getServerName());
				userName.setValue(model.getServerInfoModel().getUserName());
				password.setPasswordValue(model.getServerInfoModel()
						.getPasswd());
				port.setValue(model.getServerInfoModel().getPort());
				if ("http".equalsIgnoreCase(model.getServerInfoModel()
						.getProtocol())) {
					protocol.setValue(http);
				} else {
					protocol.setValue(https);
				}
			} else {
				if (model.getType() == BackupLocationInfoModel.TYPE_NFS) {
					cmbLocationType.setSimpleValue(UIContext.Constants
							.nfsShare());
				} else  if(model.getType() == BackupLocationInfoModel.TYPE_CIFS){
					cmbLocationType.setSimpleValue(UIContext.Constants.cifsShare());
				}else if(model.getType() == BackupLocationInfoModel.TYPE_AMAZON_S3){
					cmbLocationType.setSimpleValue(UIContext.Constants.amazonS3());
					if(model.isEnableS3CifsShare()){
						s3CifsShareSettings.setExpanded(true);
						/*if(model.getS3CifsSharePort() != null){
							s3CifsSharePort.setValue(model.getS3CifsSharePort());
						}*/
					}
					if(model.getS3CifsShareUsername() !=null){
						s3CifsShareUserName.setValue(model.getS3CifsShareUsername());
					}
					if(model.getS3CifsSharePassword() != null){
						s3CifsSharePassword.setPasswordValue(model.getS3CifsSharePassword());
					}
				}else{
					cmbLocationType.setSimpleValue(UIContext.Constants
							.serverLocal());
				}
				txtBackupLocation.setValue(model.getSessionLocation());
				if(model.getUser() !=null){
					nameTextField.setValue(model.getUser());
				}
				if(model.getPassword() !=null){
					passwordTextField.setPasswordValue(model.getPassword());
				}
				if (model.getJobLimit() == 0) {
					noLimit.setValue(true);
				} else {
					limitTo.setValue(true);
					jobLimit.setValue(model.getJobLimit());
				}
				if (model.isRunScript()) {
					cb.setValue(true);
					// scriptCombo.setSimpleValue(model.getScript());
					freeSpace.setValue(model.getFreeSizeAlert());
					unitCombo
							.setSimpleValue(model.getFreeSizeAlertUnit() == BackupLocationInfoModel.UNIT_TYPE_MB ? UIContext.Constants
									.MB() : "%");
				}
				
			}

		}
	}

	private void getScript() {
		backupService.getScripts(homePageTab.currentServer,
				Utils.SCRIPT_TYPE_ALERT, new AsyncCallback<List<String>>() {

					@Override
					public void onFailure(Throwable caught) {
						scriptCombo.removeAll();
						noScriptWarning.setVisible(true);
					}

					@Override
					public void onSuccess(List<String> result) {
						scriptCombo.removeAll();
						if (result != null && result.size() > 0) {
							scriptCombo.add(result);
							if (model != null && model.isRunScript()) {
								scriptCombo.setSimpleValue(model.getScript());
							} else {
								scriptCombo.setSimpleValue(result.get(0));
							}
							noScriptWarning.setVisible(false);
						} else {
							scriptCombo.setEmptyText(UIContext.Constants
									.nodeDiscoveryNoScript());
							noScriptWarning.setVisible(true);
						}
					}

				});
	}

	private boolean validate() {
		if (cmbLocationType.getSimpleValue() == null
				|| cmbLocationType.getSimpleValue().isEmpty()) {
			return false;
		}
		if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.rpsServe())) {
			return true;
		} else { 
			if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.cifsShare()) || cmbLocationType.getSimpleValue().equals(
						UIContext.Constants.amazonS3())) {
				if (!txtBackupLocation.validate()) {
					return false;
				}
				boolean isCIFS = Utils.isCIFSAddress(txtBackupLocation.getValue());
				if (isCIFS) {
					if (nameTextField.validate() && passwordTextField.validate()) {
					} else {
						return false;
					}
	
				} else {
					Utils.showMessage(UIContext.Constants.productName(),
							MessageBox.ERROR, UIContext.Messages
									.invalidCifsPathMessage(Utils.SMB_REGEX_1));
					return false;
				}
				if(cmbLocationType.getSimpleValue().equals(
						UIContext.Constants.amazonS3())){
					if(s3CifsShareSettings.isExpanded()){
						if(!s3CifsShareUserName.isValid()){
							return false;
						}
						if(!s3CifsSharePassword.isValid()){
							return false;
						}
					}
					/*if(!s3CifsSharePort.isValid()){
						return false;
					}*/
				}
			}	else if (cmbLocationType.getSimpleValue().equals(
					UIContext.Constants.nfsShare())) {
				if (Utils.validateNFSAddress(txtBackupLocation.getValue())) {
				} else {
					Utils.showMessage(UIContext.Constants.productName(),
							MessageBox.ERROR,
							UIContext.Constants.backupLocationMessage());
					return false;
				}
			}
			if (limitTo.getValue()) {
				if (!jobLimit.isValid()) {
					return false;
				}
			}
			if (cb.getValue()) {
				if (freeSpace.getValue() == null) {
					Utils.showMessage(UIContext.Constants.productName(),
							MessageBox.ERROR,
							UIContext.Constants.freeSizeEmpty());
					return false;
				}
				if (unitCombo.getSimpleValue().equals("%")) {
					if (freeSpace.getValue().intValue() <= 0
							|| freeSpace.getValue().intValue() > 99) {
						Utils.showMessage(UIContext.Constants.productName(),
								MessageBox.ERROR, UIContext.Constants
										.freeSizePercentFormatWrong());
						return false;
					} else {
						return true;
					}
				} else {
					if (freeSpace.getValue().intValue() <= 0) {
						Utils.showMessage(UIContext.Constants.productName(),
								MessageBox.ERROR,
								UIContext.Constants.freeSizeFormatWrong());
						return false;
					} else {
						return true;
					}
				}
			}

		} 
		return true;
	}

	private int getType() {
		String typeStr = cmbLocationType.getSimpleValue();
		int type = BackupLocationInfoModel.TYPE_NFS;
		if (typeStr.equals(UIContext.Constants.nfsShare())) {
			type = BackupLocationInfoModel.TYPE_NFS;
		} else if (typeStr.equals(UIContext.Constants.cifsShare())) {
			type = BackupLocationInfoModel.TYPE_CIFS;
		} else if (typeStr.equals(UIContext.Constants.serverLocal())) {
			type = BackupLocationInfoModel.TYPE_SERVER_LOCAL;
		} else if (typeStr.equals(UIContext.Constants.rpsServe())) {
			type = BackupLocationInfoModel.TYPE_RPS_SERVER;
		} else if(typeStr.equals(UIContext.Constants.amazonS3())){
			type = BackupLocationInfoModel.TYPE_AMAZON_S3;
		}
		return type;
	}

	private void saveOrUpdate() {
		if (model == null) {
			model = new BackupLocationInfoModel();
			ServerInfoModel serverInfoModel = new ServerInfoModel();
			DatastoreModel datastoreModel = new DatastoreModel();
			model.setServerInfoModel(serverInfoModel);
			model.setDatastoreModel(datastoreModel);
		}
		if (getType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
			DatastoreModel datastoreModel = new DatastoreModel();
			model.setDatastoreModel(datastoreModel);
			model.setType(BackupLocationInfoModel.TYPE_RPS_SERVER);
			RpsDataStoreModel ds = dataStoreBox.getValue();
			model.setUser(ds.getUuid());
			model.setEnableDedup(ds.getEnableDedup());
			model.setSessionLocation(ds.getSharePath());
			model.setUser(ds.getSharePathUsername());
			model.setPassword(ds.getSharePathPassword());
			model.getDatastoreModel().setUuid(ds.getUuid());
			model.getDatastoreModel().setDisplayName(ds.getStoreSharedName());
			model.setJobLimit(0);
			model.setRunScript(false);
			model.setScript(null);
			model.setFreeSizeAlert(0L);
			model.setFreeSizeAlertUnit(0);
			model.setServerInfoModel(serverInfoModel);
		} else {
			model.setSessionLocation(txtBackupLocation.getValue());
			model.setType(getType());
			model.setUser(nameTextField.getValue());
			model.setPassword(passwordTextField.getPasswordValue());
			if(getType() == BackupLocationInfoModel.TYPE_AMAZON_S3){
				model.setEnableS3CifsShare(s3CifsShareSettings.isExpanded());
				if(s3CifsShareSettings.isExpanded()){
					model.setS3CifsShareUsername(s3CifsShareUserName.getValue());
					model.setS3CifsSharePassword(s3CifsSharePassword.getPasswordValue());
				}
				//model.setS3CifsSharePort(s3CifsSharePort.getValue().intValue());
			}
			if (noLimit.getValue()) {
				model.setJobLimit(0);
			} else {
				model.setJobLimit(jobLimit.getValue().intValue());
			}
			if (cb.getValue()) {
				model.setRunScript(true);
				model.setFreeSizeAlert(freeSpace.getValue().longValue());
				// model.setScript(scriptCombo.getSimpleValue());
				model.setFreeSizeAlertUnit(unitCombo.getSimpleValue().equals(
						"%") == true ? BackupLocationInfoModel.UNIT_TYPE_PERCENT
						: BackupLocationInfoModel.UNIT_TYPE_MB);
			} else {
				model.setRunScript(false);
				model.setScript(null);
				model.setFreeSizeAlert(0L);
				model.setFreeSizeAlertUnit(0);
			}
		} 
		if (type == TYPE_ADD) {
			this.mask(UIContext.Constants.adding());
		} else {
			this.mask(UIContext.Constants.modifying());
		}
		if (model.getUUID() == null) {
			storageService.addBackupStorage(homePageTab.currentServer, model,
					new BaseAsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							unmask();
						}

						@Override
						public void onSuccess(Integer result) {
							if (result == 0) {
								homePageTab.refreshBackupStorageTable();
								hide();
							} else {
								Utils.showMessage(UIContext.Constants
										.productName(), MessageBox.ERROR,
										UIContext.Constants
												.backupLocationMessage());
								unmask();
							}
						}

					});
		} else {
			storageService.updateBackupStorage(homePageTab.currentServer,
					model, new BaseAsyncCallback<Integer>() {
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							unmask();
						}

						@Override
						public void onSuccess(Integer result) {
							if (result == 0) {
								homePageTab.refreshBackupStorageTable();
								hide();
							} else {
								Utils.showMessage(UIContext.Constants
										.productName(), MessageBox.ERROR,
										UIContext.Constants
												.backupLocationMessage());
								unmask();
							}

						}
					});
		}

	}

}
