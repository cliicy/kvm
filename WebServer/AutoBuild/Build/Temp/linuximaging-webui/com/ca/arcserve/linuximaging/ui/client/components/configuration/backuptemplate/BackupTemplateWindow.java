package com.ca.arcserve.linuximaging.ui.client.components.configuration.backuptemplate;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.common.BaseComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.BaseSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.ConfigurationService;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.ConfigurationServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.UIContext;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.BackupTemplateModel;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.EncryptionAlgModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.CompressType;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BackupTemplateWindow extends Window {
	final ConfigurationServiceAsync service = GWT.create(ConfigurationService.class);
	private final static int MAX_WIDTH = 550;
	private final static int MAX_HEIGHT = 540;
	private final static int MAX_FIELD_WIDTH= 250;
	private final static int MIN_WIDTH = 90;
	
	private boolean bCancelled = false;
	private BackupTemplateWindow thisWindow;
	private Button btSave;
	private Button btCancel;
	
	private FieldSet generalFieldSet;
	private TextField<String> txtTemplateName;
//	private TextField<String> txtDescription;
	private FieldSet backupDestinationFieldSet;
	private TextField<String> txtLocation;
	private TextField<String> txtUserName;
	private TextField<String> txtPassword;
	private FieldSet compressionFieldSet;
	private BaseSimpleComboBox<String> compressionOption;
	private FieldSet encryptionFieldSet;
	private BaseComboBox<EncryptionAlgModel> encryptionAlgOption;
	private LabelField encryptKeyLabel;
	private TextField<String> encryptionKeyTextField;
	private LabelField reTypeEncryptKeyLabel;
	private TextField<String> retypeEncryptionKeyTextField;
	private ListStore<EncryptionAlgModel> encryptAlgStore;
	private Integer selectedAlgortith;
	private LabelField lblUserName;
	private LabelField lblPassword;
	

	
	
	public BackupTemplateWindow(){
		thisWindow=this;
		setScrollMode(Scroll.AUTO);
		setResizable(false);
		setHeading(UIContext.Constants.backupTemplateSettings());
		setWidth(MAX_WIDTH);
		setHeight(MAX_HEIGHT);
		
		LayoutContainer container = new LayoutContainer();
		TableLayout tLayout = new TableLayout();
		tLayout.setWidth("100%");
		tLayout.setColumns(1);
		tLayout.setCellPadding(2);
		tLayout.setCellSpacing(0);
		container.setLayout(tLayout);
		
		defineGeneralSection();
		defineBackupDestinationSection();
		defineCompressionSection();
		defineEncryptionSection();
		container.add(generalFieldSet);
		container.add(backupDestinationFieldSet);
		container.add(compressionFieldSet);
		container.add(encryptionFieldSet);
		thisWindow.add(container);
		
		defineButtons();
	}
	
	private void defineButtons() {
		btSave = new Button()
		{
			@Override
			protected void onDisable() {
				addStyleName("item-disabled");
				super.onDisable();		   
			}

			@Override
			protected void onEnable() {
				removeStyleName("item-disabled");
				super.onEnable();
			}
		};
		btSave.setText(UIContext.Constants.save());
		btSave.setMinWidth(MIN_WIDTH);		
		btSave.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(validate()){
					String maskMsg =  UIContext.Constants.validating();					
					thisWindow.mask(maskMsg);
					service.addBackupTemplate(getBackupTemplateModel(), new AsyncCallback<Boolean>(){

						@Override
						public void onSuccess(Boolean result) {
							if(result){
								thisWindow.hide(btSave);
							}
							thisWindow.unmask();
						}

						@Override
						public void onFailure(Throwable caught) {
							thisWindow.unmask();
							
						}
						
					});
					
					
				}
				return;
			}
		});		
		thisWindow.addButton(btSave);

		btCancel = new Button()
		{
			@Override
			protected void onDisable() {
				addStyleName("item-disabled");
				super.onDisable();		   
			}

			@Override
			protected void onEnable() {
				removeStyleName("item-disabled");
				super.onEnable();
			}
		};
		btCancel.setText(UIContext.Constants.cancel());
		btCancel.setMinWidth(MIN_WIDTH);
		btCancel.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				bCancelled = true;
				thisWindow.hide();

			}});		
		thisWindow.addButton(btCancel);	
		
	}

	protected boolean validate() {
		// TODO Auto-generated method stub
		return true;
	}

	private void defineGeneralSection(){
		generalFieldSet=new FieldSet();
		generalFieldSet.setHeading(UIContext.Constants.general());
		generalFieldSet.setStyleAttribute("margin", "5,5,5,5");

		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setColumns(2);
		tableLayout.setCellPadding(0);
		tableLayout.setCellSpacing(5);
		generalFieldSet.setLayout(tableLayout);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("15%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("85%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField lblTemplateName = new LabelField(UIContext.Constants.templateName());
		lblTemplateName.setAutoWidth(true);
		generalFieldSet.add(lblTemplateName, tdLabel);
		
		txtTemplateName = new TextField<String>();
		txtTemplateName.setWidth(MAX_FIELD_WIDTH);
		txtTemplateName.setAllowBlank(false);
		generalFieldSet.add(txtTemplateName, tdField);

//		LabelField lblDescription = new LabelField();
//		lblDescription.setText(UIContext.Constants.description());
//		lblDescription.setAutoWidth(true);
//		generalFieldSet.add(lblDescription, tdLabel);
//
//		txtDescription = new TextField<String>();
//		txtDescription.setWidth(MAX_FIELD_WIDTH);
//		txtDescription.setAllowBlank(false);
//		generalFieldSet.add(txtDescription, tdField);
	}
	
	private void defineBackupDestinationSection(){
		backupDestinationFieldSet=new FieldSet();
		backupDestinationFieldSet.setHeading(UIContext.Constants.backupTemplateSettings());
		backupDestinationFieldSet.setStyleAttribute("margin", "5,5,5,5");

		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setColumns(2);
		tableLayout.setCellPadding(0);
		tableLayout.setCellSpacing(5);
		backupDestinationFieldSet.setLayout(tableLayout);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("15%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("85%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField lblLocation = new LabelField(UIContext.Constants.location());
		lblLocation.setAutoWidth(true);
		backupDestinationFieldSet.add(lblLocation, tdLabel);
		
		txtLocation = new TextField<String>();
		txtLocation.setWidth(MAX_FIELD_WIDTH);
		txtLocation.setAllowBlank(false);
		txtLocation.addListener(Events.Change, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				if(txtLocation.getValue()==null){
					enableCredentialsPanel(false);
				}else if(txtLocation.getValue().startsWith("//")){
					enableCredentialsPanel(true);
				}else if(txtLocation.getValue().contains(":/")){
					enableCredentialsPanel(true);
				}else{
					enableCredentialsPanel(false);
				}
			}

			
		});
		
		backupDestinationFieldSet.add(txtLocation, tdField);

		lblUserName = new LabelField();
		lblUserName.setText(UIContext.Constants.userName());
		lblUserName.setAutoWidth(true);
		backupDestinationFieldSet.add(lblUserName, tdLabel);

		txtUserName = new TextField<String>();
		txtUserName.setWidth(MAX_FIELD_WIDTH);
		txtUserName.setAllowBlank(false);
		backupDestinationFieldSet.add(txtUserName, tdField);
		
		lblPassword = new LabelField();
		lblPassword.setText(UIContext.Constants.password());
		lblPassword.setAutoWidth(true);
		backupDestinationFieldSet.add(lblPassword, tdLabel);

		txtPassword = new TextField<String>();
		txtPassword.setPassword(true);
		txtPassword.setWidth(MAX_FIELD_WIDTH);
		txtPassword.setAllowBlank(false);
		backupDestinationFieldSet.add(txtPassword, tdField);
	}
	
	public void enableCredentialsPanel(boolean flag) {
		lblUserName.setEnabled(flag);
		txtUserName.setEnabled(flag);
		lblPassword.setEnabled(flag);
		txtPassword.setEnabled(flag);
	}
	
	private void defineCompressionSection(){
		compressionFieldSet=new FieldSet();
		compressionFieldSet.setHeading(UIContext.Constants.compression());
		compressionFieldSet.setStyleAttribute("margin", "5,5,5,5");

		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setColumns(2);
		tableLayout.setCellPadding(0);
		tableLayout.setCellSpacing(5);
		compressionFieldSet.setLayout(tableLayout);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("15%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("85%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField lblCompression = new LabelField(UIContext.Constants.compression());
		lblCompression.setAutoWidth(true);
		compressionFieldSet.add(lblCompression, tdLabel);
		
		compressionOption = new BaseSimpleComboBox<String>();
		compressionOption.ensureDebugId("75D99A0E-1366-41af-9003-F7C719B86037");
		compressionOption.setEditable(false);
		compressionOption.add(UIContext.Constants.settingsCompressionNone());
		compressionOption.add(UIContext.Constants.settingsCompreesionStandard());
		compressionOption.add(UIContext.Constants.settingsCompressionMax());
		compressionOption.setSimpleValue(UIContext.Constants.settingsCompressionNone());
		Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
		compressionOption.setWidth(MAX_FIELD_WIDTH);
		
		compressionOption.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>(){

			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				String selString = compressionOption.getSimpleValue();
				if (selString.compareTo(UIContext.Constants.settingsCompressionNone()) == 0)
				{
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionNoneTooltip());
				}
				else if (selString.compareTo(UIContext.Constants.settingsCompreesionStandard()) == 0)
				{
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
				}
				else if (selString.compareTo(UIContext.Constants.settingsCompressionMax()) == 0)
				{
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionMaxTooltip());
				}
			}
			
		});
		
		compressionFieldSet.add(compressionOption, tdField);
	}
	
	private void defineEncryptionSection(){
		encryptionFieldSet=new FieldSet();
		encryptionFieldSet.setHeading(UIContext.Constants.encryption());
		encryptionFieldSet.setStyleAttribute("margin", "5,5,5,5");

		TableLayout tlGeneral = new TableLayout();
		tlGeneral.setWidth("100%");
		tlGeneral.setColumns(2);
		tlGeneral.setCellPadding(0);
		tlGeneral.setCellSpacing(5);
		encryptionFieldSet.setLayout(tlGeneral);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("15%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("85%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField lblEncryption = new LabelField(UIContext.Constants.encryption());
		lblEncryption.setAutoWidth(true);
		encryptionFieldSet.add(lblEncryption, tdLabel);
		
		encryptionAlgOption = new BaseComboBox<EncryptionAlgModel>();
		encryptionAlgOption.setDisplayField("name");
		encryptionAlgOption.setEditable(false);
		encryptionAlgOption.setWidth(200);
		encryptAlgStore = new ListStore<EncryptionAlgModel>();
		encryptionAlgOption.setStore(encryptAlgStore);
		encryptionAlgOption.addSelectionChangedListener(new SelectionChangedListener<EncryptionAlgModel>(){
				@Override
				public void selectionChanged(
						SelectionChangedEvent<EncryptionAlgModel> se) {
					setEncryptionPasswordControlsEnabled();
				}
			}
		);
		encryptionFieldSet.add(encryptionAlgOption, tdField);

		encryptKeyLabel = new LabelField();
		encryptKeyLabel.setText(UIContext.Constants.encryptionPassword());
		encryptionFieldSet.add(encryptKeyLabel, tdLabel);
		
		encryptionKeyTextField = new TextField<String>();
		encryptionKeyTextField.setWidth(MAX_FIELD_WIDTH);
		encryptionKeyTextField.setPassword(true);
		encryptionKeyTextField.setValue("");
		encryptionKeyTextField.setMaxLength(Utils.EncryptionPwdLen);
//		encryptionKeyTextField.setAllowBlank(false);
		encryptionFieldSet.add(encryptionKeyTextField, tdField);

		reTypeEncryptKeyLabel = new LabelField();
		reTypeEncryptKeyLabel.addStyleName("backupDestinationEncrypt");
		reTypeEncryptKeyLabel.setText(UIContext.Constants.retypeEncryptionPassword());
		reTypeEncryptKeyLabel.setAutoWidth(true);
		encryptionFieldSet.add(reTypeEncryptKeyLabel, tdLabel);

		retypeEncryptionKeyTextField = new TextField<String>();
		retypeEncryptionKeyTextField.setPassword(true);
		retypeEncryptionKeyTextField.setWidth(MAX_FIELD_WIDTH);
		retypeEncryptionKeyTextField.setValue("");
		retypeEncryptionKeyTextField.setMaxLength(Utils.EncryptionPwdLen);
//		retypeEncryptionKeyTextField.setAllowBlank(false);
		encryptionFieldSet.add(retypeEncryptionKeyTextField, tdField);
		
		initEncryptionAlgModels();
	}

	private void initEncryptionAlgModels() {
		EncryptionAlgModel[] encryptAlgModels = new EncryptionAlgModel[3];
		encryptAlgModels[0] = new EncryptionAlgModel();
		encryptAlgModels[0].setName("AES-128");
		encryptAlgModels[0].setAlgType(1);
		
		encryptAlgModels[1] = new EncryptionAlgModel();
		encryptAlgModels[1].setName("AES-192");
		encryptAlgModels[1].setAlgType(2);
		
		encryptAlgModels[2] = new EncryptionAlgModel();
		encryptAlgModels[2].setName("AES-256");
		encryptAlgModels[2].setAlgType(3);
		List<EncryptionAlgModel> list = new ArrayList<EncryptionAlgModel>();
		EncryptionAlgModel noEncryptModel = new EncryptionAlgModel();
		noEncryptModel.setName(UIContext.Constants.noEncryption());
		noEncryptModel.setAlgType(0);
		list.add(noEncryptModel);
		int libType = 1;
		if(encryptAlgModels != null) {
			for (int i = 0; i < encryptAlgModels.length; i++) {
				encryptAlgModels[i].setAlgType((libType << 16) | encryptAlgModels[i].getAlgType());
				list.add(encryptAlgModels[i]);
			}
		}
		
		encryptAlgStore.add(list);
		refreshEncryptionContainer();
	}
	
	private void refreshEncryptionContainer() {
		if(encryptAlgStore.getCount() == 0)
			return;
		
		if(selectedAlgortith != null)
			encryptionAlgOption.setValue(getEncryptName(selectedAlgortith));
		else
			encryptionAlgOption.setValue(getEncryptName(0));
			
		if (encryptionAlgOption != null){
			SelectionChangedEvent<EncryptionAlgModel> se = new SelectionChangedEvent<EncryptionAlgModel>(encryptionAlgOption, encryptionAlgOption.getSelection());
			encryptionAlgOption.fireEvent(Events.SelectionChange, se);
		}
	}
	
	public void setEncryptAlogrithm(Integer alg) {
		selectedAlgortith = alg;
		refreshEncryptionContainer();
	}
	
	public void setEncryptPassword(String password) {
		encryptionKeyTextField.setValue(password);
		retypeEncryptionKeyTextField.setValue(password);
	}
	
	private EncryptionAlgModel getEncryptName(Integer algrithm) {
		if(algrithm == null)
			return encryptAlgStore.getAt(0);
		
		for(int i = 0, count = encryptAlgStore.getCount(); i < count; i++) {
			EncryptionAlgModel alg = encryptAlgStore.getAt(i);
			if(algrithm.intValue() == alg.getAlgType())
				return alg;
		}
		
		return encryptAlgStore.getAt(0);
	}
	
	private void setEncryptionPasswordControlsEnabled() {
		Integer algType = encryptionAlgOption.getValue().getAlgType();
		if(algType == null || algType == 0) {
			enableEncryptionPasswordControls(false);
		}
		else {
			enableEncryptionPasswordControls(true);
		}
	}
	
	private void enableEncryptionPasswordControls(boolean enable) {
		encryptKeyLabel.setEnabled(enable);
		encryptionKeyTextField.setEnabled(enable);
		reTypeEncryptKeyLabel.setEnabled(enable);
		retypeEncryptionKeyTextField.setEnabled(enable);
		Utils.addToolTip(encryptionKeyTextField, UIContext.Constants.scheduledExportToolTipPassword());
		Utils.addToolTip(retypeEncryptionKeyTextField, UIContext.Constants.scheduledExportToolTipRePassword());
		if(!enable) {
			encryptionKeyTextField.setValue("");
			retypeEncryptionKeyTextField.setValue("");
		}
	}
	
	public boolean getcancelled() {
		return bCancelled;
	}
	public BackupTemplateModel getBackupTemplateModel(){
		BackupTemplateModel model=new BackupTemplateModel();
		model.setName(txtTemplateName.getValue());
		model.setDescription("description");
		model.backupLocationInfoModel = new BackupLocationInfoModel();
		model.backupLocationInfoModel.setSessionLocation(txtLocation.getValue());
		model.backupLocationInfoModel.setUser(txtUserName.getValue());
		model.backupLocationInfoModel.setPassword(txtPassword.getValue());
		model.setCompression(CompressType.parseMessage(compressionOption.getSimpleValue()));
		model.setEncryptionName(encryptionAlgOption.getValue()==null?null:encryptionAlgOption.getValue().getName());
		model.setEncryptionPassword(encryptionKeyTextField.getValue());
		
		return model;
	}
}
