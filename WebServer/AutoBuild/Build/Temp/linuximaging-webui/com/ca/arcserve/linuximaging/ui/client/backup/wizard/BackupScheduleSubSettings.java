package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.TimeSettings;
import com.ca.arcserve.linuximaging.ui.client.common.TooltipSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.BackupScheduleModel;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.HTML;

public class BackupScheduleSubSettings extends Dialog {
	
	private SimpleComboBox<String> jobType;
	private TimeSettings startTime;
	private NumberField repeat;
	private SimpleComboBox<String> repeatUnit;
	private TimeSettings endTime;
	private LayoutContainer repeatFieldSet;
	private CheckBox[] daysCb;
	private boolean isOKClick;
	private boolean isModify;
	private BackupScheduleModel model;
	private CheckBox selectAllDay;
	private CheckBox enableRepeat;
	
	
	public BackupScheduleSubSettings(String title,boolean isModify){
		this.isModify = isModify;
		setButtons(Dialog.YESNO);
		setButtonAlign(HorizontalAlignment.CENTER);
		setModal(true);
		this.setResizable(false);
		this.setWidth(450);
		this.setAutoHeight(true);
		
		setHeading(title);
		
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(5);
		layout.setWidth("100%");
		setLayout(layout);
		
		TableData tdLabel = new TableData();
		tdLabel.setWidth("28%");
		
		TableData tdField = new TableData();
		tdField.setWidth("72%");
		
		LabelField label = new LabelField(UIContext.Constants.backupType());
		this.add(label,tdLabel);
		
		jobType = new TooltipSimpleComboBox<String>();
		jobType.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>(){

			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				if(se.getSelectedItem().getValue().equals(UIContext.Constants.incrementalBackup())){
					enableRepeat.setEnabled(true);
					enableRepeat.setValue(true);
					repeatFieldSet.setEnabled(true);
				}else{
					enableRepeat.setEnabled(false);
					enableRepeat.setValue(false);
					repeatFieldSet.setEnabled(false);
				}
			}
			
		});
		jobType.setWidth(172);
		jobType.setTriggerAction(TriggerAction.ALL);
		jobType.setEditable(false);
		jobType.add(UIContext.Constants.incrementalBackup());
		jobType.add(UIContext.Constants.fullBackup());
		jobType.add(UIContext.Constants.verifyBackup());
		this.add(jobType,tdField);
		
		label = new LabelField(UIContext.Constants.startTime());
		
		this.add(label,tdLabel);
		
		startTime = new TimeSettings();
		this.add(startTime,tdField);
		
		TableData td = new TableData();
		td.setColspan(2);
		addEnableRepeat();
		
		this.add(getRepeatFieldSet(),td);
		
		addDaysCheckbox();
		
		this.getButtonById(YES).setText(UIContext.Constants.OK());
		this.getButtonById(YES).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(validate()){
					isOKClick = true;
					BackupScheduleSubSettings.this.hide();
				}
			}
		});
		this.getButtonById(NO).setText(UIContext.Constants.cancel());
		this.getButtonById(NO).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				BackupScheduleSubSettings.this.hide();
			}
		});
		setDefaultValue();
	}
	
	public void setDefaultDay(int defaultDay){
		daysCb[defaultDay].setValue(true);
	}
	
	private void setDefaultValue(){
		jobType.setSimpleValue(UIContext.Constants.incrementalBackup());
		repeatUnit.setSimpleValue(UIContext.Constants.scheduleLabelMinutes());
	}
	
	private void addEnableRepeat(){
		TableData td = new TableData();
		td.setColspan(2);
		
		enableRepeat = new CheckBox();
		enableRepeat.setBoxLabel(UIContext.Constants.repeat());
		enableRepeat.addListener(Events.Change, new Listener<FieldEvent>(){

			@Override
			public void handleEvent(FieldEvent be) {
				repeatFieldSet.setEnabled(enableRepeat.getValue());
				if(!enableRepeat.getValue()){
					repeat.clearInvalid();
					endTime.clearInvalid();
				}else{
					repeat.validate();
				}
			}
			
		});
		add(enableRepeat,td);
	}
	
	private LayoutContainer getRepeatFieldSet(){
		
		repeatFieldSet = new LayoutContainer();
		repeatFieldSet.setBorders(true);
		repeatFieldSet.setStyleAttribute("margin-left", "15px");
		repeatFieldSet.ensureDebugId("e3c04b53-652f-4043-94e1-7a032b720d78");
		
		TableLayout tl = new TableLayout(3);
		tl.setWidth("100%");
		tl.setCellPadding(3);
		
		repeatFieldSet.setLayout(tl);
		
		TableData tdField = new TableData();
		tdField.setWidth("15%");
		LabelField label = new LabelField(UIContext.Constants.repeatEvery());
		repeatFieldSet.add(label,tdField);
		
		tdField = new TableData();
		tdField.setWidth("17%");
		repeat = new NumberField();
		repeat.setAllowBlank(false);
		repeat.setAllowNegative(false);
		repeat.setAllowDecimals(false);
		repeat.setWidth(50);
		repeatFieldSet.add(repeat,tdField);
		
		repeatUnit = new SimpleComboBox<String>();
		repeatUnit.setTriggerAction(TriggerAction.ALL);
		repeatUnit.setEditable(false);
		repeatUnit.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>(){

			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				if(se.getSelectedItem().getValue().equals(UIContext.Constants.scheduleLabelHours())){
					repeat.setMinValue(1);
					repeat.setMaxValue(24);
				}else{
					repeat.setMinValue(15);
					repeat.setMaxValue(60);
				}
				repeat.clearInvalid();
				repeat.validate();
			}
			
		});
		
		tdField = new TableData();
		tdField.setWidth("68%");
		repeatUnit.add(UIContext.Constants.scheduleLabelHours());
		repeatUnit.add(UIContext.Constants.scheduleLabelMinutes());
		repeatUnit.setWidth(101);
		repeatFieldSet.add(repeatUnit,tdField);
		
		label = new LabelField(UIContext.Constants.endTime());
		repeatFieldSet.add(label);
		
		TableData td = new TableData();
		td.setColspan(2);
		
		endTime = new TimeSettings();
		repeatFieldSet.add(endTime,td);
		
		return repeatFieldSet;
	}
	
	private void addDaysCheckbox(){
		if(!isModify){
			TableData td = new TableData();
			td.setVerticalAlign(VerticalAlignment.TOP);
			LabelField label = new LabelField(UIContext.Constants.applyTo());
			this.add(label,td);
			
			selectAllDay = new CheckBox();
			selectAllDay.setBoxLabel(UIContext.Constants.allDays());
			selectAllDay.addListener(Events.OnClick, new Listener<BaseEvent>(){

				@Override
				public void handleEvent(BaseEvent be) {
					for(CheckBox cb : daysCb){
						cb.setValue(selectAllDay.getValue());
					}
				}
				
			});
			
			this.add(selectAllDay);
			
			TableData td2 = new TableData();
			td2.setColspan(2);
			
			this.add(new HTML("<hr>"),td2);
			
			this.add(new LabelField());
			
			LayoutContainer panel = new LayoutContainer();
			panel.setLayout(new TableLayout(4));
			String[] texts = new String[7];
			texts[0] = UIContext.Constants.selectFirDayOfWeek();
			texts[1] = UIContext.Constants.selectSecDayOfWeek();
			texts[2] = UIContext.Constants.selectThiDayOfWeek();
			texts[3] = UIContext.Constants.selectFouDayOfWeek();
			texts[4] = UIContext.Constants.selectFifDayOfWeek();
			texts[5] = UIContext.Constants.selectSixDayOfWeek();
			texts[6] = UIContext.Constants.selectSevDayOfWeek();
			daysCb = new CheckBox[7];
			for(int i = 0 ; i < 7 ; i++){
				daysCb[i] = new CheckBox();
				daysCb[i].addListener(Events.OnClick, new Listener<BaseEvent>(){

					@Override
					public void handleEvent(BaseEvent be) {
						CheckBox cb = (CheckBox)be.getSource();
						if(!cb.getValue()){
							selectAllDay.setValue(false);
						}else{
							boolean allBoxSelected = true;
							for(CheckBox box : daysCb){
								if(!box.getValue()){
									allBoxSelected = false;
									break;
								}
							}
							if(allBoxSelected){
								selectAllDay.setValue(true);
							}else{
								selectAllDay.setValue(false);
							}
						}
					}
					
				});
				daysCb[i].setBoxLabel(texts[i]);
				panel.add(daysCb[i]);
			}
			this.add(panel);
		}
	}
	
	
	public void setStartTime(Date model){
		startTime.setStartDateTime(model);
	}
	
	private int getJobType(){
		if(jobType.getSimpleValue().equals(UIContext.Constants.incrementalBackup())){
			return JobType.BACKUP_INCREMENTAL.getValue();
		}else if (jobType.getSimpleValue().equals(UIContext.Constants.fullBackup())){
			return JobType.BACKUP_FULL.getValue();
		}else{
			return JobType.BACKUP_VERIFY.getValue();
		}
	}
	
	private int getRepeatUnit(){
		if(repeatUnit.getSimpleValue().equals(UIContext.Constants.scheduleLabelHours())){
			return BackupScheduleModel.Hour_Unit;
		}else{
			return BackupScheduleModel.Minute_Unit;
		}
	}
	
	
	public List<BackupScheduleModel> getBackupScheduleModelList(){
		List<BackupScheduleModel> list = new ArrayList<BackupScheduleModel>();
		D2DTimeModel start = startTime.getTime();
		boolean isEnableSchedule = enableRepeat.getValue();
		D2DTimeModel end = null;
		if(isEnableSchedule){
			 end = endTime.getTime();
		}
		for(int i = 0 ; i < daysCb.length; i++){
			CheckBox cb = daysCb[i];
			if(cb.getValue()){
				BackupScheduleModel schedule = new BackupScheduleModel();
				schedule.startTime = start;
				schedule.setMethod(getJobType());
				schedule.setDay(i+1);
				schedule.setEnabled(isEnableSchedule);
				schedule.setIsRoot(false);
				if(isEnableSchedule){
					schedule.endTime = end;
					schedule.setInterval(repeat.getValue().intValue());
					schedule.setIntervalUnit(getRepeatUnit());
				}
				list.add(schedule);
			}
		}
		return list;
	}

	public boolean isOKClick(){
		return this.isOKClick;
	}
	
	public BackupScheduleModel getBackupScheduleModel(){
		D2DTimeModel start = startTime.getTime();
		boolean isEnableSchedule = enableRepeat.getValue();
		if(isEnableSchedule){
			D2DTimeModel end = endTime.getTime();
			model.endTime = end;
			model.setInterval(repeat.getValue().intValue());
			model.setIntervalUnit(getRepeatUnit());
		}
		model.startTime = start;
		model.setMethod(getJobType());
		model.setEnabled(isEnableSchedule);
		return model;
	}
	
	public void refresh(BackupScheduleModel model){
		if(model!=null){
			this.model = model;
			int method = model.getMethod();
			if(method == JobType.BACKUP_FULL.getValue()){
				jobType.setSimpleValue(UIContext.Constants.fullBackup());
			}else if(method == JobType.BACKUP_INCREMENTAL.getValue()){
				jobType.setSimpleValue(UIContext.Constants.incrementalBackup());
			}else{
				jobType.setSimpleValue(UIContext.Constants.verifyBackup());
			}
			startTime.setUserStartTime(model.startTime);
			if(model.isEnabled()){
				enableRepeat.setValue(true);
				repeat.setValue(model.getInterval());
				if(model.getIntervalUnit() == BackupScheduleModel.Hour_Unit){
					repeatUnit.setSimpleValue(UIContext.Constants.scheduleLabelHours());
				}else{
					repeatUnit.setSimpleValue(UIContext.Constants.scheduleLabelMinutes());
				}
				endTime.setUserStartTime(model.endTime);
			}else{
				enableRepeat.setValue(false);
				repeatFieldSet.setEnabled(false);
			}
			//daysCb[model.getDay()-1].setValue(true);
			//daysCb[model.getDay()-1].setEnabled(false);
		}else{
			startTime.setStartDateTime(new Date());
		}
	}
	
	private boolean validate(){
		
		boolean ret = false;
		if(enableRepeat.getValue()){
			D2DTimeModel start = startTime.getTime();
			ret = repeat.validate();
			if(ret){
				if(!endTime.validate()){
					return false;
				}else{
					D2DTimeModel end = endTime.getTime();
					if(start.getHourOfDay() > end.getHourOfDay()){
						Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Constants.endTimeBeforeStartTime());
						return false;
					}else if(start.getHourOfDay() == end.getHourOfDay()){
						if(start.getMinute()>=end.getMinute()){
							Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Constants.endTimeBeforeStartTime());
							return false;
						}else{
							ret = true;
						}
					}else{
						ret = true;
					}
					
				}
			}else{
				return false;
			}
		}else{
			ret = true;
		}
		if(!isModify){
			if(ret == true){
				boolean isCheck = false;
				for(int i = 0 ; i < daysCb.length; i++){
					CheckBox cb = daysCb[i];
					if(cb.getValue() == true){
						isCheck = true;
						break;
					}
				}
				ret = isCheck;
			}
			if(ret == false){
				Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Constants.applyToOneDay());
			}
		}
		return ret;
	}
	
}
