package com.ca.arcserve.linuximaging.ui.client.common;

import java.util.Date;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class TimeSettings extends LayoutContainer {
	
	private BaseSimpleComboBox<String> hourCombo;
	private BaseSimpleComboBox<String> minuteCombo;
	private BaseSimpleComboBox<String> amCombo;
	
	public TimeSettings(){
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
		hourCombo.setAllowBlank(false);
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
		minuteCombo.setEditable(false);
		minuteCombo.setTriggerAction(TriggerAction.ALL);
		minuteCombo.setAllowBlank(false);
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
			amCombo.setEditable(false);
			amCombo.setTriggerAction(TriggerAction.ALL);	
			amCombo.add(UIContext.Constants.scheduleStartTimeAM());
			amCombo.add(UIContext.Constants.scheduleStartTimePM());
			amCombo.setAllowBlank(false);
			amCombo.setWidth(64);
			//amCombo.setStyleAttribute("margin-left", "5px");
			tabData = new TableData();
			tabData.setPadding(5);
			timeContainer.add(amCombo, tabData);
		}
		this.add(timeContainer);
	}
	
	public void refresh(D2DTimeModel model){
		
	}
	
	public D2DTimeModel getTime(){
		D2DTimeModel time = new D2DTimeModel();
		
		time.setRunNow(false);
		time.setReady(false);
		time.setYear(0);
		time.setMonth(0);
		time.setDay(0);
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
	
	public void setStartDateTime(Date startDate)
	{
		setStartDateTime(startDate, -1);
	}
	
	public void setStartDateTime(Date startDate, long serverTimezoneOffset) {
		int hour, minute;
		String hourVal, minuteVal;
		boolean isAM = false;

	    Date keepTime = new Date(startDate.getTime()+5*60*1000);
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
	
	
	public void setUserStartTime(D2DTimeModel model) {
		
		int hour = model.getHourOfDay(), minute = model.getMinute();
		String hourVal, minuteVal;
		boolean isAM = false;
		
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
	
	public boolean validate(){
		if(hourCombo.validate()){
			if(minuteCombo.validate()){
				if(amCombo ==null){
					return true;
				}else{
					if(amCombo.validate()){
						return true;
					}else{
						return false;
					}
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public void clearInvalid(){
		hourCombo.clearInvalid();
		minuteCombo.clearInvalid();
		if(amCombo!=null){
			amCombo.clearInvalid();
		}
	}

}
