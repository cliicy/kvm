package com.ca.arcserve.linuximaging.ui.client.common;

import java.util.Date;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.i18n.client.DateTimeFormat;

public class DateTimeSetting extends LayoutContainer {
	
	ToolTipConfig tipConfig = null;
	ToolTip tip = null;
//	LayoutContainer daylightContainer = null;

	private BaseSimpleComboBox<String> hourCombo;
	private BaseSimpleComboBox<String> minuteCombo;
	private BaseSimpleComboBox<String> amCombo;
	private DateField dateField;
	
	private LabelField dateTimeSettingsDescription;
	private boolean ready = true;
	private LayoutContainer startTimePane;
	
	@SuppressWarnings("deprecation")
	public DateTimeSetting(boolean isShowDescription)
	{
		RowLayout layout = new RowLayout(Orientation.VERTICAL);
		setLayout(layout);
		
		
		LabelField label;
		TableData tabData ;
		
		// Add sub description.
		if(isShowDescription){
			dateTimeSettingsDescription = new LabelField();
			dateTimeSettingsDescription.setText(UIContext.Constants.scheduleLabelScheduleDescription());
			add(dateTimeSettingsDescription);
		}
		startTimePane = new LayoutContainer();
		TableLayout tabLayout = new TableLayout();
		tabLayout.setWidth("90%");
		tabLayout.setCellPadding(1);
		tabLayout.setCellSpacing(1);
		tabLayout.setColumns(4);
		startTimePane.setLayout(tabLayout);
		label = new LabelField();
		label.setText(UIContext.Constants.scheduleStartDate());
		label.addStyleName("StartDateSetting");
		
		dateField = new DateField();
		dateField.ensureDebugId("C1C54199-7BDB-40d9-94C3-A519662F5C13");
		// Tool tip
		tipConfig = new ToolTipConfig(UIContext.Constants.scheduleStartDateTime());
		tip = new ToolTip(dateField, tipConfig);
		tip.setHeaderVisible(false);
		dateField.setMaxValue(Utils.maxDate);
		dateField.setMinValue(Utils.minDate);
		dateField.setEditable(false);
		dateField.getPropertyEditor().setFormat(DateTimeFormat.getShortDateFormat());
		
		tabData = new TableData();
		tabData.setWidth("15%");	
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		startTimePane.add(label, tabData);

		tabData = new TableData();
		tabData.setWidth("35%");	
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		startTimePane.add(dateField, tabData);
				
		label = new LabelField();
		label.setText(UIContext.Constants.scheduleStartTime());
		label.addStyleName("StartTimeSetting");
		
		tabData = new TableData();
		tabData.setWidth("15%");	
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		startTimePane.add(label, tabData);
		
		LayoutContainer timeContainer = getTimeContainer();
		
		tabData = new TableData();
		tabData.setWidth("35%");	
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		startTimePane.add(timeContainer, tabData);
		add(startTimePane);
	}

	private LayoutContainer getTimeContainer() {
		LabelField label;
		TableData tabData;
		
		LayoutContainer timeContainer = new LayoutContainer();
		TableLayout timeLayout = new TableLayout();
		timeLayout.setColumns(4);
		timeContainer.setLayout(timeLayout);
		
		
		hourCombo = new BaseSimpleComboBox<String>();
		hourCombo.ensureDebugId("9B2EDA22-06DE-48c6-827B-498BB53FF8B0");
		hourCombo.setEditable(false);
		hourCombo.setTriggerAction(TriggerAction.ALL);
		for (int i = Utils.minHour(); i <= Utils.maxHour(); i++) {
			String val;
			if(Utils.isHourPrefix())
				val = Utils.prefixZero( i, 2 );
			else
				val = new Integer(i).toString();
			hourCombo.add(val);
		}
		hourCombo.setWidth(44);
		hourCombo.setFieldLabel(UIContext.Constants.scheduleStartTime());
		// Tool tip
		tipConfig = new ToolTipConfig(UIContext.Constants.scheduleStartTimeTooltip1());
		tip = new ToolTip(hourCombo, tipConfig);
		tip.setHeaderVisible(false);
		
		tabData = new TableData();
		timeContainer.add(hourCombo, tabData);
		
		label = new LabelField();
		label.setText(":");
		label.setWidth(15);
		
		tabData = new TableData();
		tabData.setHorizontalAlign(HorizontalAlignment.CENTER);
		timeContainer.add(label, tabData);
		
		minuteCombo = new BaseSimpleComboBox<String>();
		minuteCombo.ensureDebugId("2170FAEC-E415-4081-A1B4-4DBA670DDE6B");
		// Tool tip
		tipConfig = new ToolTipConfig(UIContext.Constants.scheduleStartTimeTooltip2());
		tip = new ToolTip(minuteCombo, tipConfig);
		tip.setHeaderVisible(false);
		minuteCombo.setEditable(false);
		minuteCombo.setTriggerAction(TriggerAction.ALL);
		for (int i = 0; i < 60; i++) {
			String val;
			if(Utils.isMinutePrefix())
				val = Utils.prefixZero( i, 2 );
			else
				val = new Integer(i).toString();
			minuteCombo.add(val);
		}
		minuteCombo.setWidth(44);
		tabData = new TableData();
		timeContainer.add(minuteCombo, tabData);
		
		if( !Utils.is24Hours())
		{
			amCombo = new BaseSimpleComboBox<String>();
			amCombo.ensureDebugId("BAF5B09B-EF96-4f57-92DC-F312F552108A");
			tipConfig = new ToolTipConfig(UIContext.Constants.scheduleStartTimeTooltip3());
			tip = new ToolTip(amCombo, tipConfig);
			tip.setHeaderVisible(false);
			amCombo.setEditable(false);
			amCombo.setTriggerAction(TriggerAction.ALL);	
			amCombo.add(UIContext.Constants.scheduleStartTimeAM());
			amCombo.add(UIContext.Constants.scheduleStartTimePM());
			amCombo.setWidth(64);
			//amCombo.setStyleAttribute("margin-left", "5px");
			tabData = new TableData();
			tabData.setPadding(5);
			timeContainer.add(amCombo, tabData);
		}
		return timeContainer;
	}
	
	public void setStartDateTime(Date startDate, long serverTimezoneOffset) {
		//make the date in client look like server time in text. 
		int hour, minute;
		String hourVal, minuteVal;
		boolean isAM = false;
		
		Date keepDate = startDate;
	    Date keepTime = new Date(startDate.getTime()+5*60*1000);
		dateField.setValue(keepDate);
		DateWrapper wrapper = new DateWrapper(keepTime);
		
		hour = wrapper.getHours();
		minute = wrapper.getMinutes();
		if( !Utils.is24Hours() ){							//for 12 hours
			if( Utils.minHour() == 0 ){					//	for 0-11 clock
				if( hour < 12 ){					//		for am
					isAM = true;
				}
				else{								//		for pm
					isAM = false;
					if( hour == 12 )				//			translate 12:30 to 0:30 pm
						hour = 0;
					else
						hour = hour - 12;			//			translate 18:30 to 6:30 pm
				}
			}
			else{									//	for 1-12 clock
				if( hour < 12 ){					//		for am
					isAM = true;
					if( hour == 0 )					//			translate 0:30 to 12:30 am
						hour = 12;
				}
				else{								//		for pm
					isAM = false;
					if( hour != 12 )				//			translate 12:30 to 12:30 pm
						hour -= 12;					//			translate 18:30 to 6:30 pm
				}
			}
		}
		else{										//for 24 hours
			if( Utils.minHour() == 1)						//	for 1-24 clock
			{
				if( hour == 0 )						//		translate 0:30 to 24:30
					hour = 24;
			}
		}
		
		if( Utils.isHourPrefix() )
			hourVal = Utils.prefixZero( hour, 2 );
		else
			hourVal = Integer.toString(hour);
		if( Utils.isMinutePrefix() )
			minuteVal = Utils.prefixZero( minute, 2 );
		else
			minuteVal =Integer.toString(minute);
		
		hourCombo.setSimpleValue(hourVal);
		minuteCombo.setSimpleValue(minuteVal);
		if( !Utils.is24Hours() )
			if( isAM )
				amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimeAM());
			else
				amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimePM());
	}
	
	public void setStartDateTime(Date startDate)
	{
		setStartDateTime(startDate, -1);
	}
	
	public Date getServerStartTime() {
		Date newDate = dateField.getValue();
		DateWrapper wrapper = new DateWrapper(newDate);
		int hour = gethour();
		int minute = getMinute();
		DateWrapper newWrapper = wrapper.addHours(hour)
	       .addMinutes(minute);
		return newWrapper.asDate();
	}
	
	public int gethour() {
		String strHour = hourCombo.getValue() == null ? "" :hourCombo.getValue().getValue();
		int hour = Integer.valueOf(strHour).intValue();
		boolean isAM = false;
		if( amCombo != null && amCombo.getValue() != null )
			if( amCombo.getValue().getValue() == UIContext.Constants.scheduleStartTimeAM() )
				isAM = true;
			else
				isAM = false;

		if( !Utils.is24Hours() ){							//for 12 hours
			if( Utils.minHour() == 0 ){					//	for 0-11 clock
				if( !isAM ){						//		for pm
					if( hour == 0 )					//			translate 0:30 pm to 12:30
						hour = 12;
					else						
						hour += 12;					//			translate 6:30 pm to 18:30
				}
			}
			else{									//	for 1-12 clock
				if( isAM ){							//		for am
					if( hour == 12 )				//			translate 12:30 am to 0:30
						hour = 0;
				}
				else{								//		for pm
					if( hour != 12 )				//			translate 12:30 pm to 12:30
						hour += 12;					//			translate 6:30 pm to 18:30
				}
			}
		}
		else{										//for 24 hours
			if( Utils.minHour() == 1 ){					//	for 1-24 clock
				if( hour == 24 )					//		translate 24:30 to 0:30
					hour = 0;						
			}
		}
		return hour;
	}
	
	public int getMinute() {
		String strMinute = minuteCombo.getValue() == null ? "" : minuteCombo
				.getValue().getValue();
		return Integer.valueOf(strMinute).intValue();
	}
	
	public Date getStartDateTime()
	{
		return getStartDateTime(-1);
	}
		
	public Date getStartDateTime(long serverTimeZoneOffset) {
		Date date = this.getServerStartTime();
		return Utils.serverTimeToLocalTime(date, serverTimeZoneOffset);
	}

	public LabelField getDateTimeSettingsDescription() {
		return dateTimeSettingsDescription;
	}

	public void setDateTimeSettingsDescription(
			LabelField dateTimeSettingsDescription) {
		this.dateTimeSettingsDescription = dateTimeSettingsDescription;
	}

	public Date getStartDate() {
		return dateField.getValue();
	}
	
	public void setStartDate(Date value) {
		dateField.setValue(value);
	}
	
	public void setHour(int hour) {
		boolean isAM = false;
		String hourVal = "";
		if( !Utils.is24Hours() ){							//for 12 hours
			if( Utils.minHour() == 0 ){					//	for 0-11 clock
				if( hour < 12 ){					//		for am
					isAM = true;
				}
				else{								//		for pm
					isAM = false;
					if( hour == 12 )				//			translate 12:30 to 0:30 pm
						hour = 0;
					else
						hour = hour - 12;			//			translate 18:30 to 6:30 pm
				}
			}
			else{									//	for 1-12 clock
				if( hour < 12 ){					//		for am
					isAM = true;
					if( hour == 0 )					//			translate 0:30 to 12:30 am
						hour = 12;
				}
				else{								//		for pm
					isAM = false;
					if( hour != 12 )				//			translate 12:30 to 12:30 pm
						hour -= 12;					//			translate 18:30 to 6:30 pm
				}
			}
		}
		else{										//for 24 hours
			if( Utils.minHour() == 1)						//	for 1-24 clock
			{
				if( hour == 0 )						//		translate 0:30 to 24:30
					hour = 24;
			}
		}
		
		if( Utils.isHourPrefix() )
			hourVal = Utils.prefixZero( hour, 2 );
		else
			hourVal = Integer.toString(hour);
		
		
		hourCombo.setSimpleValue(hourVal);
		
		if( !Utils.is24Hours() )
			if( isAM )
				amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimeAM());
			else
				amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimePM());
	}
	
	public void setMinute(int minute) {
		String minuteVal = "";
		if( Utils.isMinutePrefix() )
			minuteVal = Utils.prefixZero( minute, 2 );
		else
			minuteVal =Integer.toString(minute);
		minuteCombo.setSimpleValue(minuteVal);
	}
	
	public void setUserStartTime(D2DTimeModel model) {
		this.ready=model.isReady();
		int hour = model.getHourOfDay(), minute = model.getMinute();
		String hourVal, minuteVal;
		boolean isAM = false;
		Date keepDate = new Date(model.getYear() - 1900, model.getMonth(), model.getDay());
		
		dateField.setValue(keepDate);
		
		if( !Utils.is24Hours() ){							//for 12 hours
			if( Utils.minHour() == 0 ){					//	for 0-11 clock
				if( hour < 12 ){					//		for am
					isAM = true;
				}
				else{								//		for pm
					isAM = false;
					if( hour == 12 )				//			translate 12:30 to 0:30 pm
						hour = 0;
					else
						hour = hour - 12;			//			translate 18:30 to 6:30 pm
				}
			}
			else{									//	for 1-12 clock
				if( hour < 12 ){					//		for am
					isAM = true;
					if( hour == 0 )					//			translate 0:30 to 12:30 am
						hour = 12;
				}
				else{								//		for pm
					isAM = false;
					if( hour != 12 )				//			translate 12:30 to 12:30 pm
						hour -= 12;					//			translate 18:30 to 6:30 pm
				}
			}
		}
		else{										//for 24 hours
			if( Utils.minHour() == 1)						//	for 1-24 clock
			{
				if( hour == 0 )						//		translate 0:30 to 24:30
					hour = 24;
			}
		}
		
		if( Utils.isHourPrefix() )
			hourVal = Utils.prefixZero( hour, 2 );
		else
			hourVal = Integer.toString(hour);
		if( Utils.isMinutePrefix() )
			minuteVal = Utils.prefixZero( minute, 2 );
		else
			minuteVal =Integer.toString(minute);
		
		hourCombo.setSimpleValue(hourVal);
		minuteCombo.setSimpleValue(minuteVal);
		if( !Utils.is24Hours() )
			if( isAM )
				amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimeAM());
			else
				amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimePM());
	}
	
	public D2DTimeModel getUserSetTime() {
		D2DTimeModel time = new D2DTimeModel();
		time.setRunNow(false);
		time.setReady(ready);
		Date date = this.getStartDate();
		time.setYear(date.getYear() + 1900);
		time.setMonth(date.getMonth());
		time.setDay(date.getDate());
		time.setHour(Integer.valueOf(hourCombo.getSimpleValue()).intValue());
		time.setMinute(getMinute());
		time.setHourOfDay(gethour());
		
		boolean isAM = false;
		if( amCombo != null && amCombo.getValue() != null )
			if( amCombo.getValue().getValue() == UIContext.Constants.scheduleStartTimeAM() )
				isAM = true;
			else
				isAM = false;
		if(!Utils.is24Hours()) {
			if(isAM)
				time.setAMPM(0);
			else
				time.setAMPM(1);
		}else
			time.setAMPM(-1);
		
		return time;
	}

	public void setEditable(boolean isEditable) {
		hourCombo.setEnabled(isEditable);
		minuteCombo.setEnabled(isEditable);
		amCombo.setEnabled(isEditable);
		dateField.setEnabled(isEditable);
	}

}
