package com.ca.arcserve.linuximaging.ui.client.homepage.jobstatus;



import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.FilterListener;
import com.ca.arcserve.linuximaging.ui.client.common.TooltipSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.JobScriptModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatus;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class JobStatusFilterBar extends LayoutContainer {
	
	public static final int TEXT_FIELD_WIDTH = 90;
	public static final int TEXT_FIELD_WIDTH2 = 60;
	public static final int MIN_BUTTON_WIDTH = 70;
	private ToolBar filterBar;	
	private TextField<String> jobName;
//	private NumberField jobID;
	private TooltipSimpleComboBox<String> cmbJobType;
	private TextField<String> nodeName;
	private TooltipSimpleComboBox<String> cmbJobStatus;
	private TooltipSimpleComboBox<String> cmbLastResult;
	private TextField<String> backupLocation;
	private Button filterBtn;
	private Button resetBtn;
	
	private JobStatusTable parentTable;
	private JobStatusFilterModel filterModel;
	private FilterListener fltLstn;
	
	public JobStatusFilterBar(JobStatusTable parent) {
		this.setLayout(new FitLayout());
		filterBar = new ToolBar();
		filterBar.setSpacing(5);
		filterBar.setBorders(true);
		
		this.parentTable = parent;
		
		//job name filter
		filterBar.add(new LabelToolItem(UIContext.Constants.jobName()+UIContext.Constants.delimiter()));
		jobName = new TextField<String>();
		jobName.setWidth(TEXT_FIELD_WIDTH);
		Utils.addToolTip(jobName, UIContext.Constants.wildcardToolTip());
		filterBar.add(jobName);
		filterBar.add(new SeparatorToolItem());	
		
		//job id filter
//		filterBar.add(new LabelToolItem(UIContext.Constants.jobID()+UIContext.Constants.delimiter()));
//		jobID = new NumberField();
//		jobID.setWidth(TEXT_FIELD_WIDTH2);
//		jobID.setAllowDecimals(false);
//		jobID.setAllowNegative(false);
//		filterBar.add(jobID);
//		filterBar.add(new SeparatorToolItem());	
		
		//job type filter
		filterBar.add(new LabelToolItem(UIContext.Constants.jobType()+UIContext.Constants.delimiter()));
		cmbJobType=new TooltipSimpleComboBox<String>(); 
		cmbJobType.setWidth(TEXT_FIELD_WIDTH);
		initComboBoxJobType();
		filterBar.add(cmbJobType);
		filterBar.add(new SeparatorToolItem());
		
		//node name filter
		filterBar.add(new LabelToolItem(UIContext.Constants.nodeName()+UIContext.Constants.delimiter()));
		nodeName = new TextField<String>();
		nodeName.setWidth(TEXT_FIELD_WIDTH);
		Utils.addToolTip(nodeName, UIContext.Constants.wildcardToolTip());
		filterBar.add(nodeName);
		filterBar.add(new SeparatorToolItem());
		
		//job status filter
		filterBar.add(new LabelToolItem(UIContext.Constants.jobStatus()+UIContext.Constants.delimiter()));
		cmbJobStatus=new TooltipSimpleComboBox<String>(); 
		cmbJobStatus.setWidth(TEXT_FIELD_WIDTH2);
		initComboBoxJobStatus();
		filterBar.add(cmbJobStatus);
		filterBar.add(new SeparatorToolItem());
		
		//last result filter
		filterBar.add(new LabelToolItem(UIContext.Constants.lastResult()+UIContext.Constants.delimiter()));
		cmbLastResult=new TooltipSimpleComboBox<String>(); 
		cmbLastResult.setWidth(TEXT_FIELD_WIDTH2);
		initComboBoxLastResult();
		filterBar.add(cmbLastResult);
		filterBar.add(new SeparatorToolItem());
		
		//backup destination
		filterBar.add(new LabelToolItem(UIContext.Constants.backupDestination()+UIContext.Constants.delimiter()) );
		backupLocation = new TextField<String>();
		backupLocation.setWidth(TEXT_FIELD_WIDTH);
		Utils.addToolTip(backupLocation, UIContext.Constants.wildcardToolTip());
		filterBar.add(backupLocation);
		filterBar.add(new SeparatorToolItem());
		
		//search button and reset button
		filterBtn = new Button(UIContext.Constants.restoreFind());
		filterBtn.setIcon(UIContext.IconHundle.search());
		filterBtn.setMinWidth(MIN_BUTTON_WIDTH);
		filterBtn.setBorders(true);
		filterBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if ( parentTable != null ) {
					filterModel = getFilterModel();
					parentTable.refreshTable();
					if ( isFiltered() ) {
						parentTable.getParentTabPanel().tabPanel.getSelectedItem().setIcon(UIContext.IconHundle.search());
						//parentTable.getParentTabPanel().tabPanel.getSelectedItem().setStyleAttribute("margin-top", "4px");
					} else {
						parentTable.getParentTabPanel().tabPanel.getSelectedItem().setIcon(null);
					}
				}
			}
		});
		resetBtn = new Button(UIContext.Constants.reset());
		resetBtn.setIcon(UIContext.IconHundle.reset());
		resetBtn.setMinWidth(MIN_BUTTON_WIDTH);
		resetBtn.setBorders(true);
		resetBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				resetFilter();
				if ( parentTable != null ) {
					parentTable.refreshTable();
				}
			}
		});
		filterBar.add(filterBtn);
		filterBar.add(resetBtn);
		fltLstn = new FilterListener(filterBtn);
		
		this.add(filterBar);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);		
		jobName.addKeyListener(fltLstn);
		nodeName.addKeyListener(fltLstn);
		cmbJobType.addKeyListener(fltLstn);
		cmbJobStatus.addKeyListener(fltLstn);
		backupLocation.addKeyListener(fltLstn);
		cmbLastResult.addKeyListener(fltLstn);
	}
	
	public void resetFilter() {
		nodeName.setValue("");
		cmbJobStatus.setSimpleValue(UIContext.Constants.all());
		cmbJobType.setSimpleValue(UIContext.Constants.all());
		cmbLastResult.setSimpleValue(UIContext.Constants.all());
		jobName.setValue("");
		backupLocation.setValue("");
		filterModel = null;
		if ( parentTable != null ) {
			parentTable.getParentTabPanel().tabPanel.getSelectedItem().setIcon(null);
		}
	}
	
	private void initComboBoxLastResult() {
		cmbLastResult.add(UIContext.Constants.all());
		cmbLastResult.add(UIContext.Constants.jobStatus_finished());
		cmbLastResult.add(UIContext.Constants.jobStatus_cancelled());
		cmbLastResult.add(UIContext.Constants.jobStatus_failed());
		cmbLastResult.add(UIContext.Constants.jobStatus_incomplete());
		cmbLastResult.setSimpleValue(UIContext.Constants.all());
		cmbLastResult.setTriggerAction(TriggerAction.ALL);
		cmbLastResult.setEditable(false);
	}

	private void initComboBoxJobStatus() {
		cmbJobStatus.add(UIContext.Constants.all());
		cmbJobStatus.add(UIContext.Constants.jobStatus_active());
		cmbJobStatus.add(UIContext.Constants.jobStatus_inactive());
		cmbJobStatus.add(UIContext.Constants.jobStatus_waiting_in_jobqueue());
		cmbJobStatus.setSimpleValue(UIContext.Constants.all());
		cmbJobStatus.setTriggerAction(TriggerAction.ALL);
		cmbJobStatus.setEditable(false);
	}

	private void initComboBoxJobType() {
		cmbJobType.add(UIContext.Constants.all());
		cmbJobType.add(UIContext.Constants.backup());
		cmbJobType.add(UIContext.Constants.restore());
		cmbJobType.add(UIContext.Constants.restoreType_BMR());
		cmbJobType.add(UIContext.Constants.instantVM());
		cmbJobType.add(UIContext.Constants.migrationBmr());
		cmbJobType.add(UIContext.Constants.restoreType_Restore_File());
		cmbJobType.add(UIContext.Constants.shareRecoveryPoint());
		cmbJobType.add(UIContext.Constants.assureRecovery());
		cmbJobType.setSimpleValue(UIContext.Constants.all());
		cmbJobType.setTriggerAction(TriggerAction.ALL);
		cmbJobType.setEditable(false);
	}

	public JobStatusFilterModel getFilter() {
		return filterModel;
	}
	
	public JobStatusFilterModel getFilterModel()
	{
		if ( !isFiltered() ) {
			return null;
		}
		
		JobStatusFilterModel model = new JobStatusFilterModel();
		model.setJobName(jobName.getValue());
//		model.setJobId(jobID.getValue()==null?-1:jobID.getValue().intValue());
		model.setJobType(getJobType());
		model.setNodeName(nodeName.getValue());
		model.setStatus(getJobStatus());
		model.setLastResult(getLastResult());
		model.setBackupLocation(backupLocation.getValue());
		return model;
	}
	
	public boolean isFiltered() {
		if ( Utils.isEmptyOrNull(nodeName.getValue()) && Utils.isEmptyOrNull(backupLocation.getValue()) && Utils.isEmptyOrNull(jobName.getValue())
				 && cmbJobStatus.getSimpleValue().equals(UIContext.Constants.all())
				 && cmbJobType.getSimpleValue().equals(UIContext.Constants.all())
				 && cmbLastResult.getSimpleValue().equals(UIContext.Constants.all())) {
			return false;
		}
		
		return true;
	}
	
	private int getLastResult() {
		if(cmbLastResult.getSimpleValue().equals(UIContext.Constants.jobStatus_finished())){
			return JobStatus.FINISHED.getValue();
		}else if(cmbLastResult.getSimpleValue().equals(UIContext.Constants.jobStatus_cancelled())){
			return JobStatus.CANCELLED.getValue();
		}else if(cmbLastResult.getSimpleValue().equals(UIContext.Constants.jobStatus_failed())){
			return JobStatus.FAILED.getValue();
		}else if(cmbLastResult.getSimpleValue().equals(UIContext.Constants.jobStatus_incomplete())){
			return JobStatus.INCOMPLETE.getValue();
		}else{
			return JobStatusFilterModel.FILTER_ALL;
		}
	}

	private int getJobStatus() {
		if(cmbJobStatus.getSimpleValue().equals(UIContext.Constants.jobStatus_inactive())){
			return JobStatusFilterModel.JOB_STATUS_INACTIVE;
		}else if(cmbJobStatus.getSimpleValue().equals(UIContext.Constants.jobStatus_active())){
			return JobStatusFilterModel.JOB_STATUS_ACTIVE;
		}else if(cmbJobStatus.getSimpleValue().equals(UIContext.Constants.jobStatus_waiting_in_jobqueue())){
			return JobStatusFilterModel.JOB_STATUS_WAITING;
		}else{
			return JobStatusFilterModel.FILTER_ALL;
		}
	}

	private int getJobType() {
		if(cmbJobType.getSimpleValue().equals(UIContext.Constants.backup())){
			return JobType.BACKUP.getValue();
		}else if(cmbJobType.getSimpleValue().equals(UIContext.Constants.restore())){
			return JobType.RESTORE.getValue();
		}else if(cmbJobType.getSimpleValue().equals(UIContext.Constants.restoreType_BMR())){
			return JobType.RESTORE_BMR.getValue();
		}else if(cmbJobType.getSimpleValue().equals(UIContext.Constants.instantVM())){
			return JobType.RESTORE_VM.getValue();
		}else if(cmbJobType.getSimpleValue().equals(UIContext.Constants.migrationBmr())){
			return JobType.RESTORE_MIGRATION.getValue();
		}else if(cmbJobType.getSimpleValue().equals(UIContext.Constants.restoreType_Restore_File())){
			return JobType.RESTORE_FILE.getValue();
		}else if(cmbJobType.getSimpleValue().equals(UIContext.Constants.shareRecoveryPoint())){
			return JobType.SHARE_RECOVERY_POINT.getValue();
		}else if(cmbJobType.getSimpleValue().equals(UIContext.Constants.assureRecovery())){
			return JobType.ASSURE_RECOVERY.getValue();
		}else{
			return JobStatusFilterModel.FILTER_ALL;
		}
	}

	public void filterByNodeNameAndJobName(String nName,String jName){
		resetFilter();
		nodeName.setValue(nName);
		jobName.setValue(jName);
		filterBtn.fireEvent(Events.Select);
	}
	
	public void filterByNodename(String name){
		resetFilter();
		nodeName.setValue(name);
		filterBtn.fireEvent(Events.Select);
	}
	
	public void filterByBackupDestination(String destination){
		resetFilter();
		backupLocation.setValue(destination);
		filterBtn.fireEvent(Events.Select);
	}
	
	public void filterByJobType(int jobType,boolean needRefresh){
		resetFilter();
		if(jobType == JobStatusFilterModel.JOB_TYPE_BACKUP){
			cmbJobType.setSimpleValue(UIContext.Constants.backup());
		}else if(jobType == JobStatusFilterModel.JOB_TYPE_RESTORE){
			cmbJobType.setSimpleValue(UIContext.Constants.restore());
		}else{
			cmbJobType.setSimpleValue(UIContext.Constants.all());
		}
		if(needRefresh){
			filterBtn.fireEvent(Events.Select);
		}else{
			filterModel = getFilterModel();
			parentTable.getParentTabPanel().getJobStatusTabItem().setIcon(UIContext.IconHundle.search());
		}
	}
	
}
