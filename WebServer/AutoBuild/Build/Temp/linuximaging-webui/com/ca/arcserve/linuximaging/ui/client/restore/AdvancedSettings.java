package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.Date;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.StartDateTimeSetting;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.DisclosurePanel;

public class AdvancedSettings extends LayoutContainer {
	
	private static final int COMBOBOX_WIDTH = 250;
	
	private SimpleComboBox<String> serverScriptBeforeJob;
	private SimpleComboBox<String> serverScriptAfterJob;
	private SimpleComboBox<String> targetScriptBeforeJob;
	private SimpleComboBox<String> targetScriptAfterJob;
	private SimpleComboBox<String> targetScriptReadyForUseJob;
	
	protected LayoutContainer targetScriptContainer;
	protected DisclosurePanel disSettingsPanel;
	protected StartDateTimeSetting startTimeContainer;
	protected RestoreWindow restoreWindow;
	private RestoreModel model;
	
	public AdvancedSettings(RestoreWindow window){
		this.restoreWindow=window;
		TableLayout layout = new TableLayout();
		layout.setWidth("99%");
		layout.setColumns(1);
		layout.setCellPadding(3);
		layout.setCellSpacing(3);
		setLayout(layout);
		
		LabelField head =new LabelField(UIContext.Constants.advanced());
		head.setStyleAttribute("font-weight", "bold");
		add(head);
		
		add(getDateTimePanel());
		addOtherPanel();
		
		if(restoreWindow.getRestoreType() == RestoreType.MIGRATION_BMR){
			disSettingsPanel.setVisible(false);
		}else{
			disSettingsPanel.setVisible(true);
		}
	}
	
	private DisclosurePanel getDateTimePanel(){
		disSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.scheduleStartDateTime());
		startTimeContainer = new StartDateTimeSetting();
		startTimeContainer.setStartDateTime(new Date());
		startTimeContainer.getDateTimeSettingsHeader().hide();
		startTimeContainer.getDateTimeSettingsDescription().hide();
		disSettingsPanel.add(startTimeContainer);
		
		return disSettingsPanel;
	}
	
	protected void addOtherPanel(){
	}
	
	protected DisclosurePanel getPrePostScriptPanel(){
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
		
		label = new LabelField(UIContext.Constants.scriptRunAfterReadyForUseJob());
		targetScriptReadyForUseJob = new SimpleComboBox<String>();
		targetScriptReadyForUseJob.setWidth(COMBOBOX_WIDTH);
		targetScriptReadyForUseJob.setEditable(false);
		targetScriptReadyForUseJob.setTriggerAction(TriggerAction.ALL);
		targetScriptContainer.add(getScriptContianer(label,targetScriptReadyForUseJob));
		
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
	
	public boolean validate(){
		return true;
	}
	public void save(){
		restoreWindow.restoreModel.startTime=getUserSetTime();
		if(serverScriptAfterJob.getSimpleValue().equals(UIContext.Constants.none())){
			restoreWindow.restoreModel.setServerScriptAfterJob(null);
		}else{
			restoreWindow.restoreModel.setServerScriptAfterJob(serverScriptAfterJob.getSimpleValue());
		}
		if(serverScriptBeforeJob.getSimpleValue().equals(UIContext.Constants.none())){
			restoreWindow.restoreModel.setServerScriptBeforeJob(null);
		}else{
			restoreWindow.restoreModel.setServerScriptBeforeJob(serverScriptBeforeJob.getSimpleValue());
		}
		if(targetScriptAfterJob.getSimpleValue().equals(UIContext.Constants.none())){
			restoreWindow.restoreModel.setTargetScriptAfterJob(null);
		}else{
			restoreWindow.restoreModel.setTargetScriptAfterJob(targetScriptAfterJob.getSimpleValue());
		}
		if(targetScriptBeforeJob.getSimpleValue().equals(UIContext.Constants.none())){
			restoreWindow.restoreModel.setTargetScriptBeforeJob(null);
		}else{
			restoreWindow.restoreModel.setTargetScriptBeforeJob(targetScriptBeforeJob.getSimpleValue());
		}
		if(targetScriptReadyForUseJob.getSimpleValue().equals(UIContext.Constants.none())){
			restoreWindow.restoreModel.setTargetScriptReadyForUseJob(null);
		}else{
			restoreWindow.restoreModel.setTargetScriptReadyForUseJob(targetScriptReadyForUseJob.getSimpleValue());
		}
	}
	public void refreshData(){
		model=restoreWindow.restoreModel;
		
		if( model.startTime != null) {
			startTimeContainer.setUserStartTime(model.startTime);
		}
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
		if(model.getTargetScriptReadyForUseJob()!=null){
			targetScriptReadyForUseJob.setSimpleValue(model.getTargetScriptReadyForUseJob());
		}else{
			targetScriptReadyForUseJob.setSimpleValue(UIContext.Constants.none());
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
		targetScriptReadyForUseJob.add(scriptList);
		targetScriptReadyForUseJob.setSimpleValue(UIContext.Constants.none());
		
		
		if(model!=null){
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
			if(model.getTargetScriptReadyForUseJob()!=null){
				targetScriptReadyForUseJob.setSimpleValue(model.getTargetScriptReadyForUseJob());
			}else{
				targetScriptReadyForUseJob.setSimpleValue(UIContext.Constants.none());
			}
		}
	}
	
	public D2DTimeModel getUserSetTime() {
		return startTimeContainer.getUserSetTime();
	}
	public void setUserStartTime(D2DTimeModel time) {
		startTimeContainer.setUserStartTime(time);
	}
	public void setStartDateTime(Date time) {
		startTimeContainer.setStartDateTime(time);
	}

	public void setSchedulerContainerVisable(boolean isVisable){
		disSettingsPanel.setVisible(isVisable);
	}
	
	protected void setPanelVisable(boolean isVisable){
		
	}
}
