package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.BaseSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.IWizard;
import com.ca.arcserve.linuximaging.ui.client.common.PasswordField;
import com.ca.arcserve.linuximaging.ui.client.common.SessionLocationPanel;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.BackupTemplateModel;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.EncryptionAlgModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.CompressType;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.DisclosurePanel;

public class BackupDestination extends LayoutContainer implements IWizard{
	
	public static int LABEL_WIDTH = 150;
	public static int MAX_FIELD_WIDTH= 300;	
	public SessionLocationPanel destination;
	private BaseSimpleComboBox<String> compressionOption;
	private BaseComboBox<EncryptionAlgModel> encryptionAlgOption;
	private LabelField encryptKeyLabel;
	private PasswordField encryptionKeyTextField;
	private LabelField reTypeEncryptKeyLabel;
	private PasswordField retypeEncryptionKeyTextField;
	private ListStore<EncryptionAlgModel> encryptAlgStore;
	private Integer selectedAlgortith;
	private BackupWizardPanel parentWindow;
	private List<BackupLocationInfoModel> ignoreLocationList;
	
	public BackupDestination(BackupWizardPanel parent)
	{
		parentWindow = parent;
		TableLayout layout = new TableLayout(2);
		layout.setWidth("97%");
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		this.setLayout(layout);
		
		defineContent();
	}
	
	private void defineContent()
	{
		LabelField header = new LabelField(UIContext.Constants.backupDestHeader());
		header.setStyleAttribute("font-weight", "bold");
		TableData data = new TableData();
		data.setColspan(2);
		add(header, data);
		
		
		DisclosurePanel disPanel = Utils.getDisclosurePanel(UIContext.Constants.backupDestination());
		
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setCellPadding(3);
		layout.setCellSpacing(3);
		container.setLayout(layout);
		
		destination = new SessionLocationPanel(1,350,this);
		container.add(destination);
		
		disPanel.add(container);
		add(disPanel,data);
		
		LabelField hint = new LabelField(UIContext.Constants.backupDestHint());
		hint.setStyleAttribute("font-weight", "bold");
		add(hint, data);
		
		defineCompressionSection();
		defineEncryptionSection();
	}
	
	private void defineCompressionSection(){
		
		DisclosurePanel disPanel = Utils.getDisclosurePanel(UIContext.Constants.compression());
	
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setCellPadding(3);
		layout.setCellSpacing(3);
		container.setLayout(layout);
		
		LabelField lblCompression = new LabelField(UIContext.Constants.compressInfo());
		lblCompression.setAutoWidth(true);
		container.add(lblCompression);
		
		compressionOption = new BaseSimpleComboBox<String>();
		compressionOption.setEditable(false);
		//compressionOption.add(UIContext.Constants.settingsCompressionNone());
		compressionOption.add(UIContext.Constants.settingsCompreesionStandard());
		compressionOption.add(UIContext.Constants.settingsCompressionMax());
		compressionOption.setSimpleValue(UIContext.Constants.settingsCompreesionStandard());
		Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
		compressionOption.setWidth(MAX_FIELD_WIDTH);
		
		compressionOption.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>(){

			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				String selString = compressionOption.getSimpleValue();
				//if (selString.compareTo(UIContext.Constants.settingsCompressionNone()) == 0)
				//{
				//	Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionNoneTooltip());
				//}
				/*else */if (selString.compareTo(UIContext.Constants.settingsCompreesionStandard()) == 0)
				{
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
				}
				else if (selString.compareTo(UIContext.Constants.settingsCompressionMax()) == 0)
				{
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionMaxTooltip());
				}
			}
			
		});
		
		container.add(compressionOption);
		disPanel.add(container);
		
		TableData data = new TableData();
		data.setColspan(2);
		add(disPanel, data);
	}
	
	private void defineEncryptionSection(){
		DisclosurePanel disPanel = Utils.getDisclosurePanel(UIContext.Constants.encryption());
		
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout(2);
		layout.setCellPadding(3);
		layout.setCellSpacing(3);
		container.setLayout(layout);
		
		TableData tdLabel = new TableData();
		tdLabel.setWidth("30%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("70%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField lblEncryption = new LabelField(UIContext.Constants.encryption());
		//lblEncryption.setAutoWidth(true);
		container.add(lblEncryption, tdLabel);
		
		encryptionAlgOption = new BaseComboBox<EncryptionAlgModel>();
		encryptionAlgOption.setDisplayField("name");
		encryptionAlgOption.setEditable(false);
		encryptionAlgOption.setWidth(MAX_FIELD_WIDTH);
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
		container.add(encryptionAlgOption, tdField);

		encryptKeyLabel = new LabelField();
		encryptKeyLabel.setText(UIContext.Constants.encryptionPassword());
		container.add(encryptKeyLabel, tdLabel);
		
		encryptionKeyTextField = new PasswordField(MAX_FIELD_WIDTH);
		encryptionKeyTextField.setMaxLength(Utils.EncryptionPwdLen);
		encryptionKeyTextField.setAllowBlank(false);
		
		container.add(encryptionKeyTextField, tdField);

		reTypeEncryptKeyLabel = new LabelField();
		reTypeEncryptKeyLabel.addStyleName("backupDestinationEncrypt");
		reTypeEncryptKeyLabel.setText(UIContext.Constants.retypeEncryptionPassword());
		reTypeEncryptKeyLabel.setAutoWidth(true);
		container.add(reTypeEncryptKeyLabel, tdLabel);

		retypeEncryptionKeyTextField = new PasswordField(MAX_FIELD_WIDTH);
		retypeEncryptionKeyTextField.setMaxLength(Utils.EncryptionPwdLen);
		retypeEncryptionKeyTextField.setAllowBlank(false);
		
		container.add(retypeEncryptionKeyTextField, tdField);
		disPanel.add(container);
		
		TableData data = new TableData();
		data.setColspan(2);
		add(disPanel, data);
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
			encryptionKeyTextField.clear();
			encryptionKeyTextField.clearInvalid();
			retypeEncryptionKeyTextField.clear();
			retypeEncryptionKeyTextField.clearInvalid();
		}
	}
	
	public BackupTemplateModel getDestInfo()
	{
		BackupTemplateModel destInfo = new BackupTemplateModel();
		destInfo.backupLocationInfoModel = destination.getBackupLocationInfo();
		destInfo.setCompression(CompressType.parseMessage(compressionOption.getValue().getValue()));
		
		destInfo.setEncryptionName(encryptionAlgOption.getValue().getName());
		Integer algType = encryptionAlgOption.getValue().getAlgType();
		destInfo.setEncryptionType(algType);
		if ( algType != null && algType != 0 )
		{
			destInfo.setEncryptionPassword(encryptionKeyTextField.getPasswordValue());
		}
				
		return destInfo;
	}
	
	public boolean checkSessionLocation() {
		if(this.ignoreLocationList!=null&&this.ignoreLocationList.size()>0){
			for(BackupLocationInfoModel model : this.ignoreLocationList){
				if(model.getSessionLocation().equalsIgnoreCase(destination.getValue())&&model.getType()==destination.getLocationType()){
					return true;
				}
			}
		}
		
		return destination.validate(SessionLocationPanel.EVENT_TYPE_NEXT);
	}
	
	public boolean checkEncPass()
	{
		Integer algType = encryptionAlgOption.getValue().getAlgType();
		if ( algType != null && algType != 0 )
		{
			if (PasswordField.PSEUDO_PASSWORD.equals(encryptionKeyTextField.getValue())
					&& PasswordField.PSEUDO_PASSWORD.equals(retypeEncryptionKeyTextField.getValue())) {
				return true;
			}else if(PasswordField.PSEUDO_PASSWORD.equals(encryptionKeyTextField.getValue())
					|| PasswordField.PSEUDO_PASSWORD.equals(retypeEncryptionKeyTextField.getValue())){
				Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Messages.passwordNotMatch());
				return false;
			}
			
			String encPass = encryptionKeyTextField.getPasswordValue();
			
			if ( encPass == null || encPass.equals("") )
			{
				Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Messages.passworkNotEmpty());
				return false;
			}
			
			String retypeEncPass = retypeEncryptionKeyTextField.getPasswordValue();
			if (retypeEncPass == null || retypeEncPass.equals("") || !retypeEncPass.equals(encPass))
			{
				Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Messages.passwordNotMatch());
				return false;
			}
			
			if ( encPass.length() > Utils.EncryptionPwdLen ) {
				Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Messages.encPasswordMaxLength(Utils.EncryptionPwdLen));
				return false;
			}
		}
		
		return true;
	}

	public EncryptionAlgModel getEncAlg(String name)
	{
		if (name == null || name.isEmpty())
		{
			return encryptAlgStore.getAt(0);
		}
		
		String encName = name;
		if (!name.equalsIgnoreCase(UIContext.Constants.noEncryption()) && name.indexOf("-") == -1)
		{
			encName = new String(name.substring(0, 3)+"-"+name.substring(3));
		}
		
		for (int i = 0; i < encryptAlgStore.getCount(); ++i)
		{
			EncryptionAlgModel enc = encryptAlgStore.getAt(i);
			if (enc != null && enc.getName().equalsIgnoreCase(encName))
			{
				return enc;
			}
		}
		
		return encryptAlgStore.getAt(0);
	}
	public void refresh()
	{
		BackupTemplateModel template = parentWindow.getBackupDestinationInfo();
		if ( template == null )
		{
			return;
		}
		
		this.destination.setBackupLocationInfo(template.backupLocationInfoModel);
		this.compressionOption.setSimpleValue(CompressType.displayMessage(template.getCompression()));
		
		EncryptionAlgModel encModel = getEncAlg(template.getEncryptionName());
		this.encryptionAlgOption.setValue(encModel);
		
		Integer type = encModel.getAlgType();
		if ( type != null && type != 0 )
		{
			encryptionKeyTextField.setPasswordValue(template.getEncryptionPassword());
			retypeEncryptionKeyTextField.setPasswordValue(template.getEncryptionPassword());
			enableEncryptionPasswordControls(true);
		}
		else
		{
			enableEncryptionPasswordControls(false);
		}
	}
	
	public void refreshLocation(List<BackupLocationInfoModel> locationList){
		List<BackupLocationInfoModel> listWithoutServerLocal = new ArrayList<BackupLocationInfoModel>();
		if(locationList!=null){
			for(BackupLocationInfoModel model : locationList){
				if(model.getType() != BackupLocationInfoModel.TYPE_SERVER_LOCAL){
					listWithoutServerLocal.add(model);
				}
			}
		}
		this.ignoreLocationList=listWithoutServerLocal;
		this.destination.refreshLocation(listWithoutServerLocal,null);
	}

	@Override
	public ServiceInfoModel getD2DServerInfo() {
		return parentWindow.getD2DServerInfo();
	}

	@Override
	public void showNextSettings() {
		parentWindow.showNextSettings();		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void maskUI(String message) {
		parentWindow.mask(message);
	}

	@Override
	public void unmaskUI() {
		parentWindow.unmask();
	}
}
