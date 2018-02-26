package com.ca.arcserve.linuximaging.ui.client.homepage.jobhistory;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.FilterListener;
import com.ca.arcserve.linuximaging.ui.client.common.TooltipSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.JobFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatus;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class JobHistoryFilterBar extends LayoutContainer {
	
	
	public static final int TEXT_FIELD_WIDTH = 90;
	public static final int TEXT_FIELD_WIDTH2 = 60;
	public static final int MIN_BUTTON_WIDTH = 70;
	
	public static Map<JobStatus, String> JOB_RESULT_RESOURCE;
	public static Map<JobType, String> JOB_TYPE_RESOURCE;
	
	
	private ToolBar filterBar;
	private JobHistoryTable parentTable;
	private TooltipSimpleComboBox<String> jobResult;
	private JobStatus defaultJobResult;
	private TextField<String> jobID;
	private TextField<String> jobName;
	private DateField timeEnd;	
	private DateField timeStart;
	private Date end;
	private Date start;
	private TextField<String> nodeName;
	private TooltipSimpleComboBox<String> jobType;
	private JobType defaultJobType;
	private TextField<String> sessLocation;
	private Button filterBtn;
	private Button resetBtn;
	private JobFilterModel filterModel;
	private FilterListener fltLstn;
	private boolean needShow;
		
	static {
		JOB_RESULT_RESOURCE = new EnumMap<JobStatus, String>(JobStatus.class);
		JOB_RESULT_RESOURCE.put(JobStatus.READY, UIContext.Constants.all());
		JOB_RESULT_RESOURCE.put(JobStatus.CANCELLED, UIContext.Constants.jobStatus_cancelled());
		//JOB_RESULT_RESOURCE.put(JobStatus.CRASHED, UIContext.Constants.jobStatus_crashed());
		JOB_RESULT_RESOURCE.put(JobStatus.FAILED, UIContext.Constants.jobStatus_failed());
		JOB_RESULT_RESOURCE.put(JobStatus.FINISHED, UIContext.Constants.jobStatus_finished());
		JOB_RESULT_RESOURCE.put(JobStatus.INCOMPLETE, UIContext.Constants.jobStatus_incomplete());		
		
		JOB_TYPE_RESOURCE = new EnumMap<JobType, String>(JobType.class);
		JOB_TYPE_RESOURCE.put(JobType.UNKNOWN, UIContext.Constants.all());
		JOB_TYPE_RESOURCE.put(JobType.BACKUP, UIContext.Constants.backup());
		JOB_TYPE_RESOURCE.put(JobType.RESTORE, UIContext.Constants.restore());
		JOB_TYPE_RESOURCE.put(JobType.BACKUP_FULL, UIContext.Constants.fullBackup());
		JOB_TYPE_RESOURCE.put(JobType.BACKUP_INCREMENTAL, UIContext.Constants.incrementalBackup());
		JOB_TYPE_RESOURCE.put(JobType.BACKUP_VERIFY, UIContext.Constants.verifyBackup());
		JOB_TYPE_RESOURCE.put(JobType.RESTORE_BMR, UIContext.Constants.restoreType_BMR());
		JOB_TYPE_RESOURCE.put(JobType.RESTORE_VM, UIContext.Constants.instantVM());
		JOB_TYPE_RESOURCE.put(JobType.RESTORE_MIGRATION, UIContext.Constants.migrationBmr());
		JOB_TYPE_RESOURCE.put(JobType.SHARE_RECOVERY_POINT, UIContext.Constants.shareRecoveryPoint());
		JOB_TYPE_RESOURCE.put(JobType.ASSURE_RECOVERY, UIContext.Constants.assureRecovery());
	}
	
	public JobHistoryFilterBar(JobHistoryTable parent) {
		parentTable = parent;
		
		this.setLayout(new FitLayout());
		filterBar = new ToolBar();
		filterBar.setSpacing(5);
		filterBar.setBorders(true);
		filterBar.setEnableOverflow(false);

		ButtonGroup group  = new ButtonGroup(14);
		group.setHeaderVisible(false);
		group.setBodyBorder(false);
		group.setSize(950, 60);
				
		TableData labelData = new TableData();
		labelData.setWidth("5%");
		labelData.setPadding(2);
		TableData fieldData = new TableData();
		fieldData.setWidth("15%");
		fieldData.setPadding(2);
		
		TableData column2Span = new TableData();
		column2Span.setColspan(2);
		column2Span.setWidth("5%");
		column2Span.setPadding(2);
		
		TableData column2SpanRight = new TableData();
		column2SpanRight.setColspan(2);
		column2SpanRight.setWidth("5%");
		column2SpanRight.setPadding(2);
		column2SpanRight.setHorizontalAlign(HorizontalAlignment.RIGHT);
		
		//job result filter
		group.add(new LabelToolItem(UIContext.Constants.result()+UIContext.Constants.delimiter()), labelData);
		createJobResult();
		group.add(jobResult, fieldData);
		group.add(new SeparatorToolItem());
				
		//job id filter
		group.add(new LabelToolItem(UIContext.Constants.jobID()+UIContext.Constants.delimiter()), column2Span);
		jobID = new TextField<String>();
		jobID.setWidth(TEXT_FIELD_WIDTH);
		group.add(jobID, fieldData);
		group.add(new SeparatorToolItem());
		
		//job name filter
		group.add(new LabelToolItem(UIContext.Constants.jobName()+UIContext.Constants.delimiter()), labelData);
		jobName = new TextField<String>();
		jobName.setWidth(TEXT_FIELD_WIDTH);
		Utils.addToolTip(jobName, UIContext.Constants.wildcardToolTip());
		group.add(jobName, fieldData);
		group.add(new SeparatorToolItem());	
		
		//job type filter 
		group.add(new LabelToolItem(UIContext.Constants.jobType()+UIContext.Constants.delimiter()), labelData);
		createJobType();
		group.add(jobType, fieldData);
		group.add(new SeparatorToolItem());	
		
		//search button 
		filterBtn = new Button(UIContext.Constants.restoreFind());
		filterBtn.setIcon(UIContext.IconHundle.search());
		filterBtn.setMinWidth(MIN_BUTTON_WIDTH);
		filterBtn.setBorders(true);
		filterBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				needShow = false;
				if ( checkFilterFields() ) {
					filterModel = getFilterModel();
					if ( parentTable != null ) {
						parentTable.searchData();
						if ( isFiltered() ) {
							parentTable.getParentTabPanel().tabPanel.getSelectedItem().setIcon(UIContext.IconHundle.search());
							//parentTable.getParentTabPanel().tabPanel.getSelectedItem().setStyleAttribute("margin-top", "4px");
						} else {
							parentTable.getParentTabPanel().tabPanel.getSelectedItem().setIcon(null);
						}
					}					
				} else {
					if ( parentTable != null ) {
						parentTable.getParentTabPanel().tabPanel.getSelectedItem().setIcon(null);
					}
				}
			}});
		group.add(filterBtn, fieldData);
		
		//node name filter
		group.add(new LabelToolItem(UIContext.Constants.nodeName()+UIContext.Constants.delimiter()), labelData);
		nodeName = new TextField<String>();
		nodeName.setWidth(TEXT_FIELD_WIDTH);
		Utils.addToolTip(nodeName, UIContext.Constants.wildcardToolTip());
		group.add(nodeName, fieldData);
		group.add(new SeparatorToolItem());
		
		//start time filter		
		group.add(new LabelToolItem(UIContext.Constants.startTime()+UIContext.Constants.delimiter()), labelData);
		group.add(new LabelToolItem(""), labelData);
		timeStart = new DateField();
		timeStart.setWidth(TEXT_FIELD_WIDTH);
		group.add(timeStart, fieldData);
		group.add(new LabelToolItem(UIContext.Constants.endTime()), column2SpanRight);
		timeEnd = new DateField();
		timeEnd.setWidth(TEXT_FIELD_WIDTH);
		group.add(timeEnd, fieldData);		
		group.add(new SeparatorToolItem());
		
		//session location filter
		group.add(new LabelToolItem(UIContext.Constants.backupDestination()+UIContext.Constants.delimiter()), labelData);
		sessLocation = new TextField<String>();
		sessLocation.setWidth(TEXT_FIELD_WIDTH);
		Utils.addToolTip(sessLocation, UIContext.Constants.wildcardToolTip());
		group.add(sessLocation, fieldData);
		group.add(new SeparatorToolItem());
		
		
		
		//reset button
		resetBtn = new Button(UIContext.Constants.reset());
		resetBtn.setIcon(UIContext.IconHundle.reset());
		resetBtn.setMinWidth(MIN_BUTTON_WIDTH);
		resetBtn.setBorders(true);
		resetBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				needShow = false;
				resetFilter();
				if ( parentTable != null ) {
					parentTable.searchData();
				}
			}});
		
		group.add(resetBtn, fieldData);
		filterBar.add(group);
		fltLstn = new FilterListener(filterBtn);
		
		this.add(filterBar);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		timeStart.addKeyListener(fltLstn);
		timeEnd.addKeyListener(fltLstn);
		jobID.addKeyListener(fltLstn);
		jobName.addKeyListener(fltLstn);
		nodeName.addKeyListener(fltLstn);
		sessLocation.addKeyListener(fltLstn);
		jobResult.addKeyListener(fltLstn);
		jobType.addKeyListener(fltLstn);
	}
	
	public void resetFilter() {
		resetJobResult();
		resetJobType();
		jobID.setValue("");
		jobName.setValue("");
		timeEnd.setValue(null);
		timeStart.setValue(null);
		nodeName.setValue("");
		sessLocation.setValue("");
		filterModel = null;
		if ( parentTable != null ) {
			parentTable.getParentTabPanel().tabPanel.getSelectedItem().setIcon(null);
		}
	}
	
	private void createJobResult() {
		jobResult = new TooltipSimpleComboBox<String>();
		jobResult.setWidth(TEXT_FIELD_WIDTH);
		defaultJobResult = JobStatus.READY;

		for (Entry<JobStatus, String> result : JOB_RESULT_RESOURCE.entrySet()) {
			jobResult.add(result.getValue());
		}
			
		jobResult.setTriggerAction(ComboBox.TriggerAction.ALL);
		jobResult.setEditable(false);
		jobResult.setSimpleValue(JOB_RESULT_RESOURCE.get(defaultJobResult));
	}
	
	private void resetJobResult() {
		if ( jobResult == null ) return;
		jobResult.setSimpleValue(JOB_RESULT_RESOURCE.get(defaultJobResult));
	}
	
	private void createJobType() {
		jobType = new TooltipSimpleComboBox<String>();
		jobType.setWidth(TEXT_FIELD_WIDTH);
		defaultJobType = JobType.UNKNOWN;
		
		for (Entry<JobType, String> result : JOB_TYPE_RESOURCE.entrySet()) {
			jobType.add(result.getValue());
		}
		
		jobType.setTriggerAction(ComboBox.TriggerAction.ALL);
		jobType.setEditable(false);
		jobType.setSimpleValue(JOB_TYPE_RESOURCE.get(defaultJobType));
	}
	
	private void resetJobType() {
		if ( jobType == null ) return;
		jobType.setSimpleValue(JOB_TYPE_RESOURCE.get(defaultJobType));
	}
	
	private JobStatus getJobResult() {
		if ( jobResult == null ) {
			return null;
		}
		
		int index = jobResult.getSelectedIndex();
		int tmpIndex = 0;
		for (JobStatus result : JOB_RESULT_RESOURCE.keySet()) {
			if ( tmpIndex++ == index )
				return result;
		}
		return null;
	}
	
	private JobType getJobType() {
		if ( jobType == null ) {
			return null;
		}
		
		int index = jobType.getSelectedIndex();
		int tmpIndex = 0;
		for (JobType type : JOB_TYPE_RESOURCE.keySet()) {
			if ( tmpIndex++ == index )
				return type;
		}
		return null;
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
	
	public boolean isFiltered() {
		boolean ret = false;
		if ( !Utils.isEmptyOrNull(jobID.getValue()) ) {
			ret = true;
		} else if ( !Utils.isEmptyOrNull(jobName.getValue()) ) {
			ret = true;
		} else if ( !Utils.isEmptyOrNull(nodeName.getValue()) ) {
			ret = true;
		} else if ( !Utils.isEmptyOrNull(sessLocation.getValue()) ) {
			ret = true;
		} else if ( start != null || end != null ) {
			ret = true;
		} else {
			JobStatus result = this.getJobResult();
			JobType type = this.getJobType();
			if ( result != null && result != JobStatus.READY ) {
				ret = true;
			}
			
			if ( !ret && type != null && type != JobType.UNKNOWN ) {
				ret = true;
			}
		}
		return ret;
	}
	
	public JobFilterModel getFilter() {
		return filterModel;
	}
	
	private JobFilterModel getFilterModel() { // this is called locally due to JobHistory is refreshed periodicly.
		if ( !isFiltered() ) {
			return null;
		}
		
		JobFilterModel model = new JobFilterModel();
		String tmpStr = jobID.getValue();
		if ( !Utils.isEmptyOrNull(tmpStr) ) {
			model.setJobID(Long.parseLong(tmpStr));
		}
		
		tmpStr = jobName.getValue();
		if ( !Utils.isEmptyOrNull(tmpStr) ) {
			model.setJobName(tmpStr);
		}
		
		tmpStr = nodeName.getValue();
		if ( !Utils.isEmptyOrNull(tmpStr) ) {
			model.setNodeName(tmpStr);
		}
		
		tmpStr = sessLocation.getValue();
		if ( !Utils.isEmptyOrNull(tmpStr) ) {
			model.setSessionLoc(tmpStr);
		}
		
		if ( getJobResult() != null ) {
			model.setJobResult(getJobResult().getValue());
		}
		if ( getJobType() != null ) {
			model.setJobType(getJobType().getValue());
		}
		model.setStartTime(start);
		model.setEndTime(end);
		return model;
	}
	
	public void filterByNodeNameAndJobName(String nName,String jName){
		resetFilter();
		nodeName.setValue(Utils.getNodeNameWithoutPort(nName));
		jobName.setValue(jName);
		filterBtn.fireEvent(Events.Select);
	}
	
	public void filterByJobName(String name){
		resetFilter();
		jobName.setValue(name);
		filterBtn.fireEvent(Events.Select);
	}
	
	public void filterByNodeName(String name){
		resetFilter();
		nodeName.setValue(Utils.getNodeNameWithoutPort(name));
		filterBtn.fireEvent(Events.Select);
	}

	public void filterByDestination(String destination){
		resetFilter();
		sessLocation.setValue(destination);
		filterBtn.fireEvent(Events.Select);
	}
	
	public void filterByJobType(JobType searcgJobType,boolean needRefresh){
		resetFilter();
		jobType.setSimpleValue(JOB_TYPE_RESOURCE.get(searcgJobType));
		if(needRefresh){
			filterBtn.fireEvent(Events.Select);
		}else{
			needShow = true;
			filterModel = getFilterModel();
			parentTable.getParentTabPanel().getJobHistoryTabItem().setIcon(UIContext.IconHundle.search());
		}
	}
	
	public boolean isNeedShow(){
		return needShow;
	}
	
	public void setNeedShow(boolean needShow){
		this.needShow = needShow;
	}
}
