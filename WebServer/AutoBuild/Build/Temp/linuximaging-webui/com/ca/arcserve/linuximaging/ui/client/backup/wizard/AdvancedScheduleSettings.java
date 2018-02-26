package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.Date;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.TooltipSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.model.BackupModel;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class AdvancedScheduleSettings extends LayoutContainer {
	
	
	//private Radio runNow;
	//private Radio runOnce;
	//private Radio runManually;
	//private Radio repeat;
	//private RadioGroup rg;
	private SimpleComboBox<String> repeatMethod;
	//private LayoutContainer runNowDescription;
	//private DateTimeSetting startTimeContainer;
	private LayoutContainer runManuallyDescription;
	private DailyScheduleSetting dailyContainer;
	private BackupScheduleSettings scheduleSettings;
	private Date time;
	private boolean isModify;
	private BackupModel model;
	private int scheduleSelectedType = BackupModel.SCHEDULE_TYPE_REPEAT_DAILY;
	
	public AdvancedScheduleSettings(boolean isModify){
		this.isModify = isModify;
		TableLayout layout = new TableLayout();
		layout.setColumns(1);
		layout.setWidth("100%");
		
		
		addRunContainer();
		//addRunNowDescription();
		//addStartDateAndTime();
		addRunManuallyDescription();
		addDaily();
		addWeekly();
		repeatMethod.setSimpleValue(UIContext.Constants.custom());
	}
	
	private void addRunContainer(){
		/*runNow = new Radio();
		runNow.setValue(true);
		runNow.setBoxLabel(UIContext.Constants.now());
		runOnce = new Radio();
		runOnce.setBoxLabel(UIContext.Constants.runOnce());
		runManually = new Radio();
		runManually.setBoxLabel(UIContext.Constants.runManually());
		
		repeat = new Radio();
		repeat.setBoxLabel(UIContext.Constants.repeat());
		
		rg = new RadioGroup();
		rg.setSpacing(5);
		rg.add(runNow);
		rg.add(runOnce);
		rg.add(runManually);
		rg.add(repeat);
		rg.addListener(Events.Change, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				if(rg.getValue() == runNow){
					runNowDescription.setVisible(true);
					startTimeContainer.setVisible(false);
					runManuallyDescription.setVisible(false);
					dailyContainer.setVisible(false);
					scheduleSettings.setVisible(false);
					repeatMethod.setEnabled(false);
					AdvancedScheduleSettings.this.scheduleSelectedType = BackupModel.SCHEDULE_TYPE_NOW;
				}else if(rg.getValue() == runOnce){
					runNowDescription.setVisible(false);
					startTimeContainer.setVisible(true);
					runManuallyDescription.setVisible(false);
					dailyContainer.setVisible(false);
					scheduleSettings.setVisible(false);
					repeatMethod.setEnabled(false);
					AdvancedScheduleSettings.this.scheduleSelectedType = BackupModel.SCHEDULE_TYPE_ONCE;
				}else if(rg.getValue() == runManually){
					runNowDescription.setVisible(false);
					startTimeContainer.setVisible(false);
					runManuallyDescription.setVisible(true);
					dailyContainer.setVisible(false);
					scheduleSettings.setVisible(false);
					repeatMethod.setEnabled(false);
					AdvancedScheduleSettings.this.scheduleSelectedType = BackupModel.SCHEDULE_TYPE_MANUALLY;
				}else if(rg.getValue() == repeat){
					runNowDescription.setVisible(false);
					startTimeContainer.setVisible(false);
					runManuallyDescription.setVisible(false);
					repeatMethod.setEnabled(true);
					changeSchedule(repeatMethod.getSimpleValue());
				}
			}
			
		});*/
		LabelField label = new LabelField(UIContext.Constants.scheduleType());
		
		
		repeatMethod = new TooltipSimpleComboBox<String>();
		repeatMethod.setWidth(150);
		if(isModify){
			repeatMethod.add(UIContext.Constants.simple());
		}
		repeatMethod.add(UIContext.Constants.custom());
		repeatMethod.add(UIContext.Constants.none());
		repeatMethod.setTriggerAction(TriggerAction.ALL);
		repeatMethod.setEditable(false);
		repeatMethod.setStyleAttribute("padding-left", "5px");
		repeatMethod.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>(){

			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				//if(repeat.getValue()){
					changeSchedule(se.getSelectedItem().getValue());
				//}
			}
		});
		
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout(2);
		//layout.setCellPadding(5);
		//layout.setCellSpacing(3);
		container.setLayout(layout);
		//container.add(runNow);
		//container.add(runOnce);
		//container.add(runManually);
		//container.add(repeat);
		container.add(label);
		container.add(repeatMethod);
		this.add(container);
	}
	
	private void changeSchedule(String value){
		if(value.equals(UIContext.Constants.simple())){
			scheduleSettings.setVisible(false);
			runManuallyDescription.setVisible(false);
			dailyContainer.setVisible(true);
			AdvancedScheduleSettings.this.scheduleSelectedType = BackupModel.SCHEDULE_TYPE_REPEAT_DAILY;
		}else if(value.equals(UIContext.Constants.custom())){
			dailyContainer.setVisible(false);
			runManuallyDescription.setVisible(false);
			scheduleSettings.setVisible(true);
			AdvancedScheduleSettings.this.scheduleSelectedType = BackupModel.SCHEDULE_TYPE_REPEAT_WEEKLY;
		}else{
			dailyContainer.setVisible(false);
			scheduleSettings.setVisible(false);
			runManuallyDescription.setVisible(true);
			AdvancedScheduleSettings.this.scheduleSelectedType = BackupModel.SCHEDULE_TYPE_MANUALLY;
		}
	}
	
	/*private void addStartDateAndTime(){
		startTimeContainer = new DateTimeSetting(true);
		startTimeContainer.setStartDateTime(new Date());
		startTimeContainer.getDateTimeSettingsDescription().setValue(UIContext.Constants.runOnceDescription());
		startTimeContainer.setVisible(false);
		this.add(startTimeContainer);
	}*/
	
	/*private void addRunNowDescription(){
		runNowDescription = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setHeight("40px");
		runNowDescription.setLayout(layout);
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.MIDDLE);
		LabelField label = new LabelField(UIContext.Constants.runNowDescription());
		runNowDescription.add(label,td);
		this.add(runNowDescription);
	}*/
	
	private void addRunManuallyDescription(){
		runManuallyDescription = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setHeight("40px");
		runManuallyDescription.setLayout(layout);
		runManuallyDescription.setVisible(false);
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.MIDDLE);
		LabelField label = new LabelField(UIContext.Constants.runManuallyDescription());
		runManuallyDescription.add(label,td);
		this.add(runManuallyDescription);
	}
	
	private void addDaily(){
		dailyContainer = new DailyScheduleSetting();
		dailyContainer.setVisible(false);
		dailyContainer.setStyleAttribute("padding-top", "10px");
		this.add(dailyContainer);
		
	}
	private void addWeekly(){
		scheduleSettings = new BackupScheduleSettings(true,isModify);
		scheduleSettings.setVisible(false);
		scheduleSettings.setStyleAttribute("padding-top", "10px");
		this.add(scheduleSettings);
	}
	
	public void save(BackupModel model){
		int scheduleType = BackupModel.SCHEDULE_TYPE_MANUALLY;
		/*if(runNow.getValue()){
			scheduleType = BackupModel.SCHEDULE_TYPE_NOW;
		}else if(runOnce.getValue()){
			model.startTime = startTimeContainer.getUserSetTime();
			scheduleType = BackupModel.SCHEDULE_TYPE_ONCE;
		}else if(runManually.getValue()){
			scheduleType = BackupModel.SCHEDULE_TYPE_MANUALLY;
		}else if(repeat.getValue()){
			if(repeatMethod.getSimpleValue().equals(UIContext.Constants.daily())){
				model.dailySchedule = dailyContainer.save();
				scheduleType = BackupModel.SCHEDULE_TYPE_REPEAT_DAILY;
			}else{
				model.weeklySchedule = scheduleSettings.save();
				scheduleType = BackupModel.SCHEDULE_TYPE_REPEAT_WEEKLY;
			}
		}*/
		String value = repeatMethod.getSimpleValue();
		if(value.equals(UIContext.Constants.simple())){
			model.dailySchedule = dailyContainer.save();
			scheduleType = BackupModel.SCHEDULE_TYPE_REPEAT_DAILY;
		}else if(value.equals(UIContext.Constants.custom())){
			model.weeklySchedule = scheduleSettings.save();
			scheduleType = BackupModel.SCHEDULE_TYPE_REPEAT_WEEKLY;
		}else{
			scheduleType = BackupModel.SCHEDULE_TYPE_MANUALLY;
		}
		model.setScheduleType(scheduleType);
	}
	
	
	public void refresh(BackupModel model){
		this.model = model;
		int scheduleType = model.getScheduleType();
		this.scheduleSelectedType = model.getScheduleType();
		if(scheduleType == BackupModel.SCHEDULE_TYPE_NOW){
			//runNow.setValue(true);
		}else if(scheduleType == BackupModel.SCHEDULE_TYPE_ONCE){
			/*if (model.startTime != null) {
				startTimeContainer.setUserStartTime(model.startTime);
			}
			runOnce.setValue(true);*/
		}else if(scheduleType == BackupModel.SCHEDULE_TYPE_MANUALLY || model.isDisable()){
			//runManually.setValue(true);
			repeatMethod.setSimpleValue(UIContext.Constants.none());
		}else if(scheduleType == BackupModel.SCHEDULE_TYPE_REPEAT_DAILY){
			//repeat.setValue(true);
			repeatMethod.setSimpleValue(UIContext.Constants.simple());
			dailyContainer.refresh(model.dailySchedule);
		}else if(scheduleType == BackupModel.SCHEDULE_TYPE_REPEAT_WEEKLY){
			//repeat.setValue(true);
			repeatMethod.setSimpleValue(UIContext.Constants.custom());
			scheduleSettings.refresh(model.weeklySchedule);
		}
		
		if(scheduleType != BackupModel.SCHEDULE_TYPE_REPEAT_DAILY){
			repeatMethod.remove(UIContext.Constants.simple());
		}
	}
	
	public void changeScheduleType(){
		if(scheduleSelectedType == BackupModel.SCHEDULE_TYPE_NOW){
			//runNow.setValue(true);
		}else if(scheduleSelectedType == BackupModel.SCHEDULE_TYPE_ONCE){
			//runOnce.setValue(true);
		}else if(scheduleSelectedType == BackupModel.SCHEDULE_TYPE_MANUALLY){
			//runManually.setValue(true);
			repeatMethod.setSimpleValue(UIContext.Constants.none());
		}else if(scheduleSelectedType == BackupModel.SCHEDULE_TYPE_REPEAT_DAILY){
			//repeat.setValue(true);
			repeatMethod.setSimpleValue(UIContext.Constants.simple());
			if(model!=null)
				dailyContainer.refresh(model.dailySchedule);
		}else if(scheduleSelectedType == BackupModel.SCHEDULE_TYPE_REPEAT_WEEKLY){
			//runNow.setValue(true);
			//repeat.setValue(true);
			repeatMethod.setSimpleValue(UIContext.Constants.none());
			repeatMethod.setSimpleValue(UIContext.Constants.custom());
			if(model!=null)
				scheduleSettings.refresh(model.weeklySchedule);
		}
	}
	
	public boolean validate(){
		/*if(repeat.getValue()){
			if(repeatMethod.getSimpleValue().equals(UIContext.Constants.daily())){
				return dailyContainer.validate();
			}else{
				return scheduleSettings.validate();
			}
		}else{
			return true;
		}*/
		if(repeatMethod.getSimpleValue().equals(UIContext.Constants.simple())){
			return dailyContainer.validate();
		}else if(repeatMethod.getSimpleValue().equals(UIContext.Constants.custom())){
			return scheduleSettings.validate();
		}else{
			return true;
		}
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
		if(!isModify){
			//startTimeContainer.setStartDateTime(time);
			dailyContainer.setStartDateTime(time);
			scheduleSettings.setTime(time);
			scheduleSettings.setStartDate(time);
		}else {
			int scheduleType = model.getScheduleType();
			if(scheduleType == BackupModel.SCHEDULE_TYPE_NOW){
				//startTimeContainer.setStartDateTime(time);
				dailyContainer.setStartDateTime(time);
				scheduleSettings.setTime(time);
				scheduleSettings.setStartDate(time);
			}else if(scheduleType == BackupModel.SCHEDULE_TYPE_ONCE){
				dailyContainer.setStartDateTime(time);
				scheduleSettings.setTime(time);
				scheduleSettings.setStartDate(time);
			}else if(scheduleType == BackupModel.SCHEDULE_TYPE_MANUALLY){
				//startTimeContainer.setStartDateTime(time);
				dailyContainer.setStartDateTime(time);
				scheduleSettings.setTime(time);
				scheduleSettings.setStartDate(time);
			}else if(scheduleType == BackupModel.SCHEDULE_TYPE_REPEAT_DAILY){
				//startTimeContainer.setStartDateTime(time);
				scheduleSettings.setTime(time);
				scheduleSettings.setStartDate(time);
			}else if(scheduleType == BackupModel.SCHEDULE_TYPE_REPEAT_WEEKLY){
				//startTimeContainer.setStartDateTime(time);
				dailyContainer.setStartDateTime(time);
				scheduleSettings.setTime(time);
			}
		}
	}
	
	public D2DTimeModel getUserSetTime() {
		/*if(runNow.getValue()){
			return null;
		}else if(runOnce.getValue()){
			return startTimeContainer.getUserSetTime();
		}else if(runManually.getValue()){
			return null;
		}else if(repeat.getValue()){
			if(repeatMethod.getSimpleValue().equals(UIContext.Constants.daily())){
				return dailyContainer.getStartDateTime();
			}else{
				return null;
			}
		}
		return null;*/
		
		if(repeatMethod.getSimpleValue().equals(UIContext.Constants.simple())){
			return dailyContainer.getStartDateTime();
		}else{
			return null;
		}
	}
	
}
