package com.ca.arcserve.linuximaging.ui.client.standby;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.PasswordField;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.PhysicalStandbyModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;

public class AdvancedSettings extends LayoutContainer {
	
	private static final int COMBOBOX_WIDTH = 250;
	private static final int FIELD_WIDTH = 200;
	
	private NumberField heartBeat;
	private NumberField heartBeatTime;
	
	private SimpleComboBox<String> serverScriptBeforeJob;
	private SimpleComboBox<String> serverScriptAfterJob;
	private SimpleComboBox<String> targetScriptBeforeJob;
	private SimpleComboBox<String> targetScriptAfterJob;
	protected LayoutContainer targetScriptContainer;
	
	private TextField<String> username;
	private PasswordField password;
	private PasswordField rePassword;
	private TextField<String> path;
	private LayoutContainer container;
	private Label moreLabel;
	
	private StandbyWizardPanel parentWindow;
	
	public AdvancedSettings(StandbyWizardPanel parent){
		this.parentWindow = parent;
		this.setHeight(StandbyWizardPanel.RIGHT_PANEL_HIGHT);
		this.setWidth(StandbyWizardPanel.RIGHT_PANEL_WIDTH - 10);
		this.setScrollMode(Scroll.AUTOY);
		TableLayout layout = new TableLayout();
		layout.setWidth("99%");
		layout.setColumns(1);
		layout.setCellPadding(3);
		layout.setCellSpacing(3);
		setLayout(layout);
		
		LabelField head =new LabelField(UIContext.Constants.advanced());
		head.setStyleAttribute("font-weight", "bold");
		add(head);
		
		//add(getAutoStartPanel());
		
		add(getHeartBeatPanel());
		add(getPrePostScriptPanel());
		add(getMoreSettings());
		addOtherPanel();
	}
	
	private DisclosurePanel getHeartBeatPanel(){
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.heartBeatSettings());
		
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(3);
		layout.setCellPadding(3);
		layout.setWidth("100%");
		container.setLayout(layout);
		
		TableData td = new TableData();
		td.setColspan(3);
		LabelField label = new LabelField(UIContext.Constants.heartBeatDescription());
		container.add(label,td);
		
		TableData tdLabel = new TableData();
		tdLabel.setWidth("15%");
		
		TableData tdField = new TableData();
		tdField.setWidth("15%");
		
		TableData tdLabel1 = new TableData();
		tdLabel1.setWidth("70%");
		
		label = new LabelField(UIContext.Constants.heartBeatFrequency());
		container.add(label,tdLabel);
		
		heartBeat = new NumberField();
		heartBeat.setAllowBlank(false);
		heartBeat.setWidth(80);
		heartBeat.setValue(5);
		container.add(heartBeat,tdField);
		
		label = new LabelField(UIContext.Constants.heartBeatSeconds());
		container.add(label,tdLabel1);
		
		label = new LabelField(UIContext.Constants.heartBeatTimes());
		container.add(label,tdLabel);
		
		heartBeatTime = new NumberField();
		heartBeatTime.setAllowBlank(false);
		heartBeatTime.setWidth(80);
		heartBeatTime.setValue(6);
		Utils.addToolTip(heartBeatTime, UIContext.Constants.heartBeatTimesToolTip());
		container.add(heartBeatTime,tdField);
		
		label = new LabelField("");
		container.add(label,tdLabel1);
		
		
		
		panel.add(container);
		
		return panel;
	}
	
	private LayoutContainer getMoreSettings(){
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
				if(container.isVisible()){
					moreLabel.setText(UIContext.Constants.showMoreSettings());
					container.hide();
				}else{
					moreLabel.setText(UIContext.Constants.hideMoreSettings());
					container.show();
				}
			}
		};
		moreLabel.addClickHandler(handler);
		moreSettings.add(moreLabel,td);
		return moreSettings;
	}
	
	private void addOtherPanel(){
		container = new LayoutContainer();
		container.add(getResetCrentialSettings());
		if(!UIContext.selectedServerVersionInfo.isLiveCD()){
			container.add(getLocationSettings());
		}
		container.hide();
		this.add(container);
	}
	
	private DisclosurePanel getPrePostScriptPanel(){
		DisclosurePanel prePostSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.restorePrePostSettings());
		LayoutContainer scriptPanel = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		scriptPanel.setLayout(layout);
		
		LabelField label = new LabelField(UIContext.Constants.scriptRunOnD2DServer());
		label.setStyleAttribute("font-weight", "bold");
		scriptPanel.add(label);
		
		label = new LabelField(UIContext.Constants.scriptRunBeforeJob());
		serverScriptBeforeJob = new SimpleComboBox<String>();
		serverScriptBeforeJob.setWidth(COMBOBOX_WIDTH);
		serverScriptBeforeJob.setEditable(false);
		serverScriptBeforeJob.setTriggerAction(TriggerAction.ALL);
		scriptPanel.add(getScriptContianer(label,serverScriptBeforeJob));
		
		label = new LabelField(UIContext.Constants.scriptRunAfterJob());
		serverScriptAfterJob = new SimpleComboBox<String>();
		serverScriptAfterJob.setWidth(COMBOBOX_WIDTH);
		serverScriptAfterJob.setEditable(false);
		serverScriptAfterJob.setTriggerAction(TriggerAction.ALL);
		scriptPanel.add(getScriptContianer(label,serverScriptAfterJob));
		
		
		targetScriptContainer = new LayoutContainer();
		TableLayout layoutForTarget = new TableLayout(1);
		layoutForTarget.setWidth("100%");
		targetScriptContainer.setLayout(layoutForTarget);
		
		label = new LabelField(UIContext.Constants.scriptRunOnTargetMachine());
		label.setStyleAttribute("font-weight", "bold");
		targetScriptContainer.add(label);
		
		label = new LabelField(UIContext.Constants.scriptRunBeforeJob());
		targetScriptBeforeJob = new SimpleComboBox<String>();
		targetScriptBeforeJob.setWidth(COMBOBOX_WIDTH);
		targetScriptBeforeJob.setEditable(false);
		targetScriptBeforeJob.setTriggerAction(TriggerAction.ALL);
		targetScriptContainer.add(getScriptContianer(label,targetScriptBeforeJob));
		
		label = new LabelField(UIContext.Constants.scriptRunAfterJob());
		targetScriptAfterJob = new SimpleComboBox<String>();
		targetScriptAfterJob.setWidth(COMBOBOX_WIDTH);
		targetScriptAfterJob.setEditable(false);
		targetScriptAfterJob.setTriggerAction(TriggerAction.ALL);
		targetScriptContainer.add(getScriptContianer(label,targetScriptAfterJob));
		scriptPanel.add(targetScriptContainer);
		
		prePostSettingsPanel.add(scriptPanel);
		return prePostSettingsPanel;
	}
	
	private LayoutContainer getScriptContianer(LabelField label, SimpleComboBox<String> scirptCombox){
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		container.setLayout(tableLayout);
		container.addStyleName("restoreWizardLeftSpacing");
		
		TableData td = new TableData();
		td.setWidth("25%");
		container.add(label,td);
		
		td = new TableData();
		td.setWidth("75%");
		container.add(scirptCombox);
		return container;
	}
	
	private DisclosurePanel getResetCrentialSettings(){
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.credentialSettings());
		
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(2);
		layout.setCellPadding(3);
		container.setLayout(layout);
		
		TableData td = new TableData();
		td.setColspan(2);
		LabelField label = new LabelField(UIContext.Constants.credentialSDescription());
		container.add(label,td);
		
		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");
		
		TableData tdField = new TableData();
		tdField.setWidth("80%");
		
		label = new LabelField(UIContext.Constants.userName() + UIContext.Constants.delimiter());
		container.add(label,tdLabel);
		
		username = new TextField<String>();
		username.setWidth(FIELD_WIDTH);
		container.add(username,tdField);
		
		label = new LabelField(UIContext.Constants.password() + UIContext.Constants.delimiter());
		container.add(label,tdLabel);
		
		password = new PasswordField(FIELD_WIDTH);
		container.add(password,tdField);
		
		label = new LabelField(UIContext.Constants.retypeEncryptionPassword() + UIContext.Constants.delimiter());
		container.add(label,tdLabel);
		
		rePassword = new PasswordField(FIELD_WIDTH);
		container.add(rePassword,tdField); 
		
		panel.add(container);
		return panel;
	}
	
	private DisclosurePanel getLocationSettings(){
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.sessionLocationSettings());
		
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(3);
		layout.setWidth("100%");
		container.setLayout(layout);
		
		TableData td = new TableData();
		td.setColspan(2);
		LabelField label = new LabelField(UIContext.Constants.sessionLocationDescription());
		container.add(label,td);
		
		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");
		
		TableData tdField = new TableData();
		tdField.setWidth("80%");
		
		label = new LabelField(UIContext.Constants.path() + UIContext.Constants.delimiter());
		container.add(label,tdLabel);
		
		path = new TextField<String>();
		path.setWidth(FIELD_WIDTH);
		container.add(path,tdField);
		
		panel.add(container);
		
		return panel;
	}
	
	public void save(){
		if(heartBeat.getValue() !=null){
			parentWindow.standbyModel.setHeartBeatFrequency(heartBeat.getValue().intValue());
		}
		if(heartBeatTime.getValue() !=null){
			parentWindow.standbyModel.setHeartBeatTime(heartBeatTime.getValue().intValue());
		}
		if(serverScriptAfterJob.getSimpleValue().equals(UIContext.Constants.none())){
			parentWindow.standbyModel.setServerScriptAfterJob(null);
		}else{
			parentWindow.standbyModel.setServerScriptAfterJob(serverScriptAfterJob.getSimpleValue());
		}
		if(serverScriptBeforeJob.getSimpleValue().equals(UIContext.Constants.none())){
			parentWindow.standbyModel.setServerScriptBeforeJob(null);
		}else{
			parentWindow.standbyModel.setServerScriptBeforeJob(serverScriptBeforeJob.getSimpleValue());
		}
		if(targetScriptAfterJob.getSimpleValue().equals(UIContext.Constants.none())){
			parentWindow.standbyModel.setTargetScriptAfterJob(null);
		}else{
			parentWindow.standbyModel.setTargetScriptAfterJob(targetScriptAfterJob.getSimpleValue());
		}
		if(targetScriptBeforeJob.getSimpleValue().equals(UIContext.Constants.none())){
			parentWindow.standbyModel.setTargetScriptBeforeJob(null);
		}else{
			parentWindow.standbyModel.setTargetScriptBeforeJob(targetScriptBeforeJob.getSimpleValue());
		}
		
		parentWindow.standbyModel.setNewUsername(username.getValue());
		parentWindow.standbyModel.setNewPassword(password.getPasswordValue());
		parentWindow.standbyModel.setLocalPath(path.getValue());
	}
	
	public void refresh(){
		PhysicalStandbyModel model = (PhysicalStandbyModel)parentWindow.standbyModel;
		heartBeatTime.setValue(model.getHeartBeatTime());
		heartBeat.setValue(model.getHeartBeatFrequency());
		if(model.getServerScriptAfterJob()!=null){
			serverScriptAfterJob.setSimpleValue(model.getServerScriptAfterJob());
		}else{
			serverScriptAfterJob.setSimpleValue(UIContext.Constants.none());
		}
		if(model.getServerScriptBeforeJob()!=null){
			serverScriptBeforeJob.setSimpleValue(model.getServerScriptBeforeJob());
		}else{
			serverScriptBeforeJob.setSimpleValue(UIContext.Constants.none());
		}
		if(model.getTargetScriptAfterJob()!=null){
			targetScriptAfterJob.setSimpleValue(model.getTargetScriptAfterJob());
		}else{
			targetScriptAfterJob.setSimpleValue(UIContext.Constants.none());
		}
		if(model.getTargetScriptBeforeJob()!=null){
			targetScriptBeforeJob.setSimpleValue(model.getTargetScriptBeforeJob());
		}else{
			targetScriptBeforeJob.setSimpleValue(UIContext.Constants.none());
		}
		if(model.getNewUsername() != null){
			username.setValue(model.getNewUsername());
		}
		if(model.getNewPassword() != null){
			password.setPasswordValue(model.getNewPassword());
			rePassword.setPasswordValue(model.getNewPassword());
		}
		if(!UIContext.selectedServerVersionInfo.isLiveCD() && model.getLocalPath()!=null){
			path.setValue(model.getLocalPath());
		}
	}
	
	public void refreshPrePostScripts(List<String> scriptList){
		serverScriptAfterJob.removeAll();
		serverScriptBeforeJob.removeAll();
		targetScriptAfterJob.removeAll();
		targetScriptBeforeJob.removeAll();
		if(scriptList == null){
			return;
		}
		scriptList.add(0, UIContext.Constants.none());
		serverScriptAfterJob.add(scriptList);
		serverScriptAfterJob.setSimpleValue(UIContext.Constants.none());
		serverScriptBeforeJob.add(scriptList);
		serverScriptBeforeJob.setSimpleValue(UIContext.Constants.none());
		targetScriptAfterJob.add(scriptList);
		targetScriptAfterJob.setSimpleValue(UIContext.Constants.none());
		targetScriptBeforeJob.add(scriptList);
		targetScriptBeforeJob.setSimpleValue(UIContext.Constants.none());
	}
	
	public boolean validate(){
		if(!heartBeatTime.validate()){
			return false;
		}
		if(!heartBeat.validate()){
			return false;
		}
		if(password.getPasswordValue()!=null && !password.getPasswordValue().equals(rePassword.getPasswordValue())){
			Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Constants.passwordNotSame());
			return false;
		}
		return true;
	}
}
