package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.Date;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.BackupModel;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DisclosurePanel;

public class ScheduleSettings extends LayoutContainer{
	
	private static Integer allowMaxThrottleValue = new Integer(99999);
	private static Integer allowMinThrottleValue = new Integer(1);
	private static final int COMBOBOX_WIDTH = 250;
	private AdvancedScheduleSettings advancedSchedule;
	private BackupWizardPanel backupWindow;
	private BackupSetSettings backupSetSettings;
	
	private CheckBox throttleCheckBox = new CheckBox();
	private NumberField throttleField = new ErrorAlignedNumberField();
	
	private SimpleComboBox<String> serverScriptBeforeJob;
	private SimpleComboBox<String> serverScriptAfterJob;
	private SimpleComboBox<String> targetScriptBeforeSnapshot;
	private SimpleComboBox<String> targetScriptAfterSnapshot;	
	private SimpleComboBox<String> targetScriptBeforeJob;
	private SimpleComboBox<String> targetScriptAfterJob;
	private BackupModel model;
	
	public ScheduleSettings(BackupWizardPanel w) {
		backupWindow = w;
		this.setHeight(BackupWizardPanel.RIGHT_PANEL_HIGHT-10);
		this.setWidth(BackupWizardPanel.RIGHT_PANEL_WIDTH-10);
		this.setScrollMode(Scroll.AUTOY);
//		RowLayout layout = new RowLayout();		
//		container.setLayout(layout);
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("97%");
//		tl.setHeight("95%");
		this.setLayout(tl);
		
		LabelField label = new LabelField();
		label.setText(UIContext.Constants.backupSettingsSchedule());
//		label.addStyleName("restoreWizardTitle");
		label.setStyleAttribute("font-weight", "bold");
		//this.add(label);
		
		DisclosurePanel disSettingsPanel; 
		disSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.schedule());
		advancedSchedule = new AdvancedScheduleSettings(w.isModify());
		disSettingsPanel.add(advancedSchedule);
		this.add(disSettingsPanel);
		
		label = new LabelField();
		label.setText(UIContext.Constants.recoverySetSettings());
//		label.addStyleName("restoreWizardTitle");
		label.setStyleAttribute("font-weight", "bold");
		//this.add(label);
		
		this.add(getRecoverySetPanel());
		
		this.add(getThroughputPanel());
		
		label = new LabelField();
		label.setText(UIContext.Constants.restorePrePostSettings());
//		label.addStyleName("restoreWizardTitle");
		label.setStyleAttribute("font-weight", "bold");
		//this.add(label);
		
		this.add(getPrePostScriptPanel());
	}
	
	
	private DisclosurePanel getRecoverySetPanel(){
		DisclosurePanel recoverySetSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.recoverySetSettings());
		LayoutContainer scriptPanel = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		scriptPanel.setLayout(layout);
		
		backupSetSettings = new BackupSetSettings();
		recoverySetSettingsPanel.add(backupSetSettings);
		return recoverySetSettingsPanel;
	}
	
	private DisclosurePanel getThroughputPanel(){
		DisclosurePanel throughputSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.throughputTitle());
		LayoutContainer throughPanel = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(3);
		layout.setCellPadding(2);
		throughPanel.setLayout(layout);
		
		throttleCheckBox.ensureDebugId("5648D570-34E8-4cfe-A80B-2C1FA0586F65");
		throttleCheckBox.setBoxLabel(UIContext.Constants.throughputDescription());
		throttleCheckBox.addListener(Events.Change, new Listener<FieldEvent>()
				{
					@Override
					public void handleEvent(FieldEvent be) {
						throttleField.setEnabled(throttleCheckBox.getValue());
						if (!throttleCheckBox.getValue())
							throttleField.clear();
					}

				});
		throughPanel.add(throttleCheckBox);

		throttleField.ensureDebugId("CD0AF3A7-5525-4dac-BF49-288767104BCF");
		throttleField.setWidth(100);
		throttleField.setAllowBlank(false);
		throttleField.setMinValue(1);
		throttleField.setEnabled(false);
		throttleField.setAllowDecimals(false);
		throttleField.setAllowNegative(false);
		throttleField.setMaxValue(allowMaxThrottleValue);
		throttleField.setMinValue(allowMinThrottleValue);
//		throttleField.setStyleAttribute("margin-right", "20px");
		throughPanel.add(throttleField);
		
		LabelField label = new LabelField();
		label.setStyleAttribute("margin-left", "5px");
		label.setText(UIContext.Constants.throughputUnit());
		throughPanel.add(label);
		throughputSettingsPanel.add(throughPanel);
		return throughputSettingsPanel;
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
		
		
		label = new LabelField(UIContext.Constants.scriptRunOnTargetMachine());
		label.setStyleAttribute("font-weight", "bold");
		scriptPanel.add(label);
		
		label = new LabelField(UIContext.Constants.scriptRunBeforeJob());
		targetScriptBeforeJob = new SimpleComboBox<String>();
		targetScriptBeforeJob.setWidth(COMBOBOX_WIDTH);
		targetScriptBeforeJob.setEditable(false);
		targetScriptBeforeJob.setTriggerAction(TriggerAction.ALL);
		scriptPanel.add(getScriptContianer(label,targetScriptBeforeJob));
		
		label = new LabelField(UIContext.Constants.scriptRunAfterJob());
		targetScriptAfterJob = new SimpleComboBox<String>();
		targetScriptAfterJob.setWidth(COMBOBOX_WIDTH);
		targetScriptAfterJob.setEditable(false);
		targetScriptAfterJob.setTriggerAction(TriggerAction.ALL);
		scriptPanel.add(getScriptContianer(label,targetScriptAfterJob));

		label = new LabelField(UIContext.Constants.scriptRunBeforeSnapshot());
		targetScriptBeforeSnapshot = new SimpleComboBox<String>();
		targetScriptBeforeSnapshot.setWidth(COMBOBOX_WIDTH);
		targetScriptBeforeSnapshot.setEditable(false);
		targetScriptBeforeSnapshot.setTriggerAction(TriggerAction.ALL);
		scriptPanel.add(getScriptContianer(label,targetScriptBeforeSnapshot));
		
		
		label = new LabelField(UIContext.Constants.scriptRunAfterSnapshot());
		targetScriptAfterSnapshot = new SimpleComboBox<String>();
		targetScriptAfterSnapshot.setWidth(COMBOBOX_WIDTH);
		targetScriptAfterSnapshot.setEditable(false);
		targetScriptAfterSnapshot.setTriggerAction(TriggerAction.ALL);
		scriptPanel.add(getScriptContianer(label,targetScriptAfterSnapshot));
		
		
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
	
	public void refreshData() {
		model = backupWindow.backupModel;
		advancedSchedule.refresh(model);
		backupSetSettings.RefreshData(model.retentionModel);
		if(model.getThrottle()!=0){
			throttleCheckBox.setValue(true);
			throttleField.setValue(model.getThrottle());
		}else{
			throttleCheckBox.setValue(false);
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
		
		if(model.getTargetScriptAfterSnapshot()!=null){
			targetScriptAfterSnapshot.setSimpleValue(model.getTargetScriptAfterSnapshot());
		}else{
			targetScriptAfterSnapshot.setSimpleValue(UIContext.Constants.none());
		}
		if(model.getTargetScriptBeforeSnapshot()!=null){
			targetScriptBeforeSnapshot.setSimpleValue(model.getTargetScriptBeforeSnapshot());
		}else{
			targetScriptBeforeSnapshot.setSimpleValue(UIContext.Constants.none());
		}
	}
	public void Save(long timeZoneOffset)
	{
		if (this == null || !this.isRendered())
			return;
		
		advancedSchedule.save(backupWindow.backupModel);
		backupWindow.backupModel.retentionModel = backupSetSettings.Save();
		if(throttleCheckBox.getValue() && throttleField.getValue()!=null){
			backupWindow.backupModel.setThrottle(throttleField.getValue().longValue());
		}else{
			backupWindow.backupModel.setThrottle(0L);
		}
		if(serverScriptAfterJob.getSimpleValue().equals(UIContext.Constants.none())){
			backupWindow.backupModel.setServerScriptAfterJob(null);
		}else{
			backupWindow.backupModel.setServerScriptAfterJob(serverScriptAfterJob.getSimpleValue());
		}
		if(serverScriptBeforeJob.getSimpleValue().equals(UIContext.Constants.none())){
			backupWindow.backupModel.setServerScriptBeforeJob(null);
		}else{
			backupWindow.backupModel.setServerScriptBeforeJob(serverScriptBeforeJob.getSimpleValue());
		}
		if(targetScriptAfterJob.getSimpleValue().equals(UIContext.Constants.none())){
			backupWindow.backupModel.setTargetScriptAfterJob(null);
		}else{
			backupWindow.backupModel.setTargetScriptAfterJob(targetScriptAfterJob.getSimpleValue());
		}
		if(targetScriptBeforeJob.getSimpleValue().equals(UIContext.Constants.none())){
			backupWindow.backupModel.setTargetScriptBeforeJob(null);
		}else{
			backupWindow.backupModel.setTargetScriptBeforeJob(targetScriptBeforeJob.getSimpleValue());
		}
		if(targetScriptAfterSnapshot.getSimpleValue().equals(UIContext.Constants.none())){
			backupWindow.backupModel.setTargetScriptAfterSnapshot(null);
		}else{
			backupWindow.backupModel.setTargetScriptAfterSnapshot(targetScriptAfterSnapshot.getSimpleValue());
		}
		if(targetScriptBeforeSnapshot.getSimpleValue().equals(UIContext.Constants.none())){
			backupWindow.backupModel.setTargetScriptBeforeSnapshot(null);
		}else{
			backupWindow.backupModel.setTargetScriptBeforeSnapshot(targetScriptBeforeSnapshot.getSimpleValue());
		}
	}
	public boolean Validate()
	{
		if (this == null || !this.isRendered())
			return true;
		if(throttleCheckBox.getValue()){
			if(!throttleField.validate()){
				return false;
			}
		}
		return advancedSchedule.validate() && backupSetSettings.Validate();
		//return startTimeContainer.getStartDateTime() != null && (incrementalSchedule.Validate() && fullSchedule.Validate() && resyncSchedule.Validate() && backupSetSettings.Validate());
	}
	
	public void setEditable(boolean isEditable){
		/*startTimeContainer.setEnabled(isEditable);
		incrementalSchedule.setEditable(isEditable);
		fullSchedule.setEditable(isEditable);
		resyncSchedule.setEditable(isEditable);*/
	}
	public D2DTimeModel getUserSetTime() {
		return advancedSchedule.getUserSetTime();
	}
	public void setUserStartTime(D2DTimeModel time) {
		//startTimeContainer.setUserStartTime(time);
	}
	/*public Date getServerStartDate() {
		return startTimeContainer.getServerStartTime();
	}*/
	public void setStartDateTime(Date time) {
		//startTimeContainer.setStartDateTime(time);
		advancedSchedule.setTime(time);
	}
	
	public void refreshPrePostScripts(List<String> scriptList){
		serverScriptAfterJob.removeAll();
		serverScriptBeforeJob.removeAll();
		targetScriptAfterJob.removeAll();
		targetScriptBeforeJob.removeAll();
		targetScriptAfterSnapshot.removeAll();
		targetScriptBeforeSnapshot.removeAll();
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
		targetScriptAfterSnapshot.add(scriptList);
		targetScriptAfterSnapshot.setSimpleValue(UIContext.Constants.none());
		targetScriptBeforeSnapshot.add(scriptList);
		targetScriptBeforeSnapshot.setSimpleValue(UIContext.Constants.none());
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
			
			if(model.getTargetScriptAfterSnapshot()!=null){
				targetScriptAfterSnapshot.setSimpleValue(model.getTargetScriptAfterSnapshot());
			}else{
				targetScriptAfterSnapshot.setSimpleValue(UIContext.Constants.none());
			}
			if(model.getTargetScriptBeforeSnapshot()!=null){
				targetScriptBeforeSnapshot.setSimpleValue(model.getTargetScriptBeforeSnapshot());
			}else{
				targetScriptBeforeSnapshot.setSimpleValue(UIContext.Constants.none());
			}
		}
	}
	
	public void changeScheduleType(){
		advancedSchedule.changeScheduleType();
	}
	
	public class ErrorAlignedNumberField extends NumberField {
		@Override
		protected void alignErrorIcon() {
			final Element	alignElement = (Element) this.getElement().getParentElement().getNextSiblingElement();
			
			 DeferredCommand.addCommand(new Command() {
			      public void execute() {
			        errorIcon.el().alignTo(alignElement,
			        		"tl-tr", new int[] {0, 3});
			      }
			 });
		}
	}
}
