package com.ca.arcserve.linuximaging.ui.client.homepage.activitylog;


import java.util.Date;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.FilterListener;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.LogFilterModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class ActivityLogFilterBar extends LayoutContainer {
	
	public static final int TEXT_FIELD_WIDTH = 90;
	public static final int TEXT_FIELD_WIDTH2 = 60;
	public static final int MIN_BUTTON_WIDTH = 70;
	private ToolBar filterBar;	
	private LogSeverityFilter typeFilter;
	private TextField<String> jobID;
	private TextField<String> jobName;
	private DateField timeEnd;	
	private DateField timeStart;
	private Date end;
	private Date start;
	private TextField<String> nodeName;
	private Button filterBtn;
	private Button resetBtn;
	private FilterListener fltLstn;
	
	private ActivityLogTable altparent;
	
	public ActivityLogFilterBar(ActivityLogTable parent) {
		this.setLayout(new FitLayout());
		filterBar = new ToolBar();
		filterBar.setSpacing(5);
		filterBar.setBorders(true);
		
		this.altparent = parent;
		
		//type filter
		filterBar.add(new LabelToolItem(UIContext.Constants.type()+UIContext.Constants.delimiter()));
		typeFilter = new LogSeverityFilter();
		typeFilter.setWidth(TEXT_FIELD_WIDTH);
		filterBar.add(typeFilter);
		filterBar.add(new SeparatorToolItem());
		
		//job id filter
		filterBar.add(new LabelToolItem(UIContext.Constants.jobID()+UIContext.Constants.delimiter()));
		jobID = new TextField<String>();
		jobID.setWidth(TEXT_FIELD_WIDTH2);
		filterBar.add(jobID);
		filterBar.add(new SeparatorToolItem());
		
		//job name filter
		filterBar.add(new LabelToolItem(UIContext.Constants.jobName()+UIContext.Constants.delimiter()));
		jobName = new TextField<String>();
		jobName.setWidth(TEXT_FIELD_WIDTH);
		Utils.addToolTip(jobName, UIContext.Constants.wildcardToolTip());
		filterBar.add(jobName);
		filterBar.add(new SeparatorToolItem());	
		
		//time filter		
		filterBar.add(new LabelToolItem(UIContext.Constants.time()+UIContext.Constants.delimiter()));
		filterBar.add(new LabelToolItem(UIContext.Constants.between()));
		timeStart = new DateField();
		timeStart.setWidth(TEXT_FIELD_WIDTH);
		filterBar.add(timeStart);
		filterBar.add(new LabelToolItem(UIContext.Constants.and()));
		timeEnd = new DateField();
		timeEnd.setWidth(TEXT_FIELD_WIDTH);
		filterBar.add(timeEnd);		
		filterBar.add(new SeparatorToolItem());
		
		//node name filter
		filterBar.add(new LabelToolItem(UIContext.Constants.nodeName()+UIContext.Constants.delimiter()));
		nodeName = new TextField<String>();
		nodeName.setWidth(TEXT_FIELD_WIDTH);
		Utils.addToolTip(nodeName, UIContext.Constants.wildcardToolTip());
		filterBar.add(nodeName);
		filterBar.add(new SeparatorToolItem());
		
		//search button and reset button
		filterBtn = new Button(UIContext.Constants.restoreFind());
		filterBtn.setIcon(UIContext.IconHundle.search());
		filterBtn.setMinWidth(MIN_BUTTON_WIDTH);
		filterBtn.setBorders(true);
		filterBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if ( checkFilterFields() ) {
					if ( altparent != null ) {
						altparent.refreshTable();
						if ( isFiltered() ) {
							altparent.getParentTabPanel().tabPanel.getSelectedItem().setIcon(UIContext.IconHundle.search());
							//altparent.getParentTabPanel().tabPanel.getSelectedItem().setStyleAttribute("margin-top", "4px");
						} else {
							altparent.getParentTabPanel().tabPanel.getSelectedItem().setIcon(null);
						}
					}
				} else {
					if ( altparent != null ) {
						altparent.getParentTabPanel().tabPanel.getSelectedItem().setIcon(null);
					}
				}
			}});
		resetBtn = new Button(UIContext.Constants.reset());
		resetBtn.setIcon(UIContext.IconHundle.reset());
		resetBtn.setMinWidth(MIN_BUTTON_WIDTH);
		resetBtn.setBorders(true);
		resetBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				resetFilter();
				if ( altparent != null ) {
					altparent.refreshTable();
				}
			}});
		filterBar.add(filterBtn);
		filterBar.add(resetBtn);
		
		fltLstn = new FilterListener(filterBtn);
		
		this.add(filterBar);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		typeFilter.addKeyListener(fltLstn);
		timeStart.addKeyListener(fltLstn);
		timeEnd.addKeyListener(fltLstn);
		jobID.addKeyListener(fltLstn);
		jobName.addKeyListener(fltLstn);
		nodeName.addKeyListener(fltLstn);
	}
	
	public void resetFilter() {
		typeFilter.reSetType();
		jobID.setValue("");
		jobName.setValue("");
		timeEnd.setValue(null);
		timeStart.setValue(null);
		nodeName.setValue("");
		if ( altparent != null ) {
			altparent.getParentTabPanel().tabPanel.getSelectedItem().setIcon(null);
		}
	}
	
	private boolean checkFilterFields() {
		//check job ID
		try {
			String jobid = jobID.getValue();
			if ( !Utils.isEmptyOrNull(jobid) ) {
				Long.parseLong(jobID.getValue());
			}
		} catch (NumberFormatException e) {
			Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Constants.invalidJobID());
			return false;
		}
		
		//check time range
		start = timeStart.getValue();
		end = timeEnd.getValue();
		
		if ( end != null && start != null ) {
			if ( end.before(start) ) {
				Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Messages.invalidTimeRange(Utils.formatDate(start), Utils.formatDate(end)));
				return false;
			}
		}
		
		return true;
	}
	public LogFilterModel getFilter()
	{
		if ( !isFiltered() ) {
			return null;
		}
		
		LogFilterModel model = new LogFilterModel();
		model.setType(typeFilter.getType());

		if ( !Utils.isEmptyOrNull( jobID.getValue()) ) {
			Long jobid = Long.parseLong(jobID.getValue());
			model.setJobID( jobid );
		}else{
			model.setJobID(-1L);
		}
		
		if ( !Utils.isEmptyOrNull(jobName.getValue()) ) {
			model.setJobName( jobName.getValue() );
		}
		
		model.setEndTime(end);
		model.setStartTime(start);
		
		if ( !Utils.isEmptyOrNull(nodeName.getValue()) ) {
			model.setServer( nodeName.getValue() );
		}
		return model;
	}
	
	public boolean isFiltered() {
		if ( (typeFilter.getType() == null||typeFilter.getType()==Severity.All.getValue()) && Utils.isEmptyOrNull(jobID.getValue()) && Utils.isEmptyOrNull(jobName.getValue())
				&& !isTimeFiltered() && Utils.isEmptyOrNull(nodeName.getValue()) ) {
			return false;
		}
		return true;
	}
	
	public boolean isTimeFiltered() {
		if (timeEnd.getValue() != null || timeStart.getValue() != null)
			return true;
		
		return false;
	}
	
	public void filterByNodeNameAndJobName(String nName,String jName){
		resetFilter();
		nodeName.setValue(Utils.getNodeNameWithoutPort(nName));
		jobName.setValue(jName);
		filterBtn.fireEvent(Events.Select);
	}
	
	public void filterByNodename(String name){
		resetFilter();
		nodeName.setValue(Utils.getNodeNameWithoutPort(name));
		filterBtn.fireEvent(Events.Select);
	}
	
	public void filterByJobId(String jobId){
		resetFilter();
		jobID.setValue(jobId);
		filterBtn.fireEvent(Events.Select);
	}
	
}
