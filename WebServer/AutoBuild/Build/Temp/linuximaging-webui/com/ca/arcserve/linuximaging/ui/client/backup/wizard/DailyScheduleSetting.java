package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.Date;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.backup.wizard.ScheduleSubSettings.BkpType;
import com.ca.arcserve.linuximaging.ui.client.common.DateTimeSetting;
import com.ca.arcserve.linuximaging.ui.client.model.BackupScheduleModel;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.ca.arcserve.linuximaging.ui.client.model.DailyScheduleModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class DailyScheduleSetting extends LayoutContainer {
	
	private DateTimeSetting startTimeContainer;
	private ScheduleSubSettings incrementalSchedule;
	private ScheduleSubSettings fullSchedule;
	private ScheduleSubSettings resyncSchedule;
	
	public DailyScheduleSetting(){
		TableLayout layout = new TableLayout();
		layout.setColumns(1);
		layout.setWidth("100%");
		
		FieldSet fieldSet = new FieldSet();
		fieldSet.setCollapsible(true);
		fieldSet.setHeading(UIContext.Constants.scheduleStartDateTime());
		
		startTimeContainer = new DateTimeSetting(true);
		fieldSet.add(startTimeContainer);
		this.add(fieldSet);
		
		String incrementalScheduleDescription = "";
		incrementalScheduleDescription = UIContext.Messages.scheduleLabelIncrementalDescription();
		incrementalSchedule = new ScheduleSubSettings("IncrementalBackupRadioID",
				UIContext.Constants.scheduleLabelIncrementalBackup(), 
				incrementalScheduleDescription,false);
		incrementalSchedule.setDebugID("35AA09BA-0955-4134-9585-39FA70237C83", 
				"867FFD53-562B-452c-B0B0-5BF9A4EA962A", "2EDE819F-ACE6-456e-8F95-41B710CC10FB", 
				"E3F4B1C8-A350-492e-A33E-CF56BD720655");
		
		incrementalSchedule.bkpType = BkpType.INC;
		
		String fullScheduleDescription = "";
		fullScheduleDescription = UIContext.Messages.scheduleLabelFullDescription();
		fullSchedule = new ScheduleSubSettings("FullBackupRadioID",
				UIContext.Constants.scheduleLabelFullBackup(), 
				fullScheduleDescription);
		fullSchedule.setDebugID("57AA1406-366A-46f5-9E79-A8DA771EFC55", 
				"4A136C81-BF77-4584-8B08-B777B265B077", "26D8AB29-0D45-4e15-81C2-85A720DE538A", 
				"869FAA77-04DE-4c85-BAC9-384C41F06CBA");
		fullSchedule.bkpType = BkpType.FULL;

		String resyncDescription = "";
		resyncDescription = UIContext.Messages.scheduleLabelResyncDescription();
		resyncSchedule = new ScheduleSubSettings("ResyncBackupRadioID",
				UIContext.Constants.scheduleLabelResyncBackup(), 
				resyncDescription);			
		resyncSchedule.setDebugID("67CA00B8-AF6C-4f31-B484-6C11DBE7593B", 
				"3A343452-5C7B-4094-8DE1-315C05D65836", "9ECA712A-FACD-4796-BF50-DB02689AF765", 
				"518ED6CB-BECB-4efe-9DDE-6D663CB4A907");
		
		fieldSet = new FieldSet();
		fieldSet.setCollapsible(true);
		fieldSet.setHeading(UIContext.Constants.scheduleLabelIncrementalBackup());
		fieldSet.add(incrementalSchedule.Render());
		this.add(fieldSet);

		fieldSet = new FieldSet();
		fieldSet.setCollapsible(true);
		fieldSet.setHeading(UIContext.Constants.scheduleLabelFullBackup());
		fieldSet.add(fullSchedule.Render());
		this.add(fieldSet);
		
		fieldSet = new FieldSet();
		fieldSet.setCollapsible(true);
		fieldSet.setHeading(UIContext.Constants.scheduleLabelResyncBackup());
		fieldSet.add(resyncSchedule.Render());
		this.add(fieldSet);
		
		incrementalSchedule.setDefaultValue();
		fullSchedule.setDefaultValue();
		resyncSchedule.setDefaultValue();
	}
	
	public DailyScheduleModel save(){
		BackupScheduleModel fullModel = fullSchedule.Save();
		BackupScheduleModel incModel = incrementalSchedule.Save();
		BackupScheduleModel resyncModel = resyncSchedule.Save();
		
		D2DTimeModel startTime = startTimeContainer.getUserSetTime();
		
		DailyScheduleModel model = new DailyScheduleModel();
		model.fullSchedule = fullModel;
		model.incrementalSchedule = incModel;
		model.resyncSchedule = resyncModel;
		model.startTime = startTime;
		
		return model;
	}
	
	public void refresh(DailyScheduleModel model){
		startTimeContainer.setUserStartTime(model.startTime);
		fullSchedule.RefreshData(model.fullSchedule);
		incrementalSchedule.RefreshData(model.incrementalSchedule);
		resyncSchedule.RefreshData(model.resyncSchedule);
		
	}
	
	public void setStartDateTime(Date time){
		startTimeContainer.setStartDateTime(time);
	}
	
	public D2DTimeModel getStartDateTime(){
		return startTimeContainer.getUserSetTime();
	}

	public boolean validate(){
		return incrementalSchedule.Validate() && fullSchedule.Validate() && resyncSchedule.Validate();
	}
	
}
