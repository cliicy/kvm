package com.ca.arcserve.linuximaging.ui.client.standby;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.PhysicalStandbyModel;
import com.ca.arcserve.linuximaging.ui.client.model.StandbyModel;
import com.ca.arcserve.linuximaging.ui.client.model.StandbyType;
import com.ca.arcserve.linuximaging.ui.client.standby.physical.PhysicalStandbyMachinesGrid;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class Summary extends LayoutContainer {
	public static int SOURCE_TABLE_HIGHT = 120;
	public static int SOURCE_TABLE_WIDTH = 610;
	public static int SUMMARY_HIGHT = 365;
	public final static int MAX_FIELD_WIDTH= 300;
	public static int JOB_NAME_MAX_LENGTH = 128;
	private StandbyWizardPanel parentWindow;
	private LabelField txtD2DServer;
	private LabelField txtAutoStartMachine;
	private LabelField txtStartMachineMethod;
	private LabelField txtHeartBeatFrequency;
	private LabelField txtHeartBeatTimeout;
	private LabelField txtNewUsername;
	private LabelField txtSessionLocationLocal;
	private LabelField txtServerScriptBeforeJob;
	private LabelField txtServerScriptAfterJob;
	private LabelField txtTargetScriptBeforeJob;
	private LabelField txtTargetScriptAfterJob;
	private LabelField lblServerScriptBeforeJob;
	private LabelField lblServerScriptAfterJob;
	private LabelField lblTargetScriptBeforeJob;
	private LabelField lblTargetScriptAfterJob;
	private SourceNodeGrid sourceNodeGrid;
	private PhysicalStandbyMachinesGrid physicalStandbyMachinesGird;
	private TextField<String> txtJobName;
	
	public Summary(StandbyWizardPanel parent){
		this.parentWindow = parent;
		TableLayout layout = new TableLayout();
		layout.setWidth("97%");
		layout.setCellPadding(5);
		layout.setCellSpacing(5);
		setLayout(layout);
		
		LabelField head =new LabelField(UIContext.Constants.summary());
		head.setStyleAttribute("font-weight", "bold");
		add(head);
		
		LayoutContainer general=defineGeneralSummary();
		add(general);
		
		LayoutContainer jobName=defineJobNameField();
		add(jobName);
	}
	
	private LayoutContainer defineGeneralSummary(){
		LayoutContainer container=new LayoutContainer();
		container.setStyleAttribute("border", "1px solid");
		container.setStyleAttribute("border-color", "#B5B8C8");
		container.setHeight(SUMMARY_HIGHT);
		container.setScrollMode(Scroll.AUTOY);
		
		LayoutContainer generalFieldSet=new LayoutContainer();

		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(2);
		tlConnSettings.setCellPadding(1);
		tlConnSettings.setCellSpacing(5);
		generalFieldSet.setLayout(tlConnSettings);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("70%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("30%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField lblD2DServer =new LabelField(UIContext.Constants.backupServerLabel()+UIContext.Constants.delimiter());
		lblD2DServer.setAutoWidth(true);
		generalFieldSet.add(lblD2DServer, tdLabel);
		txtD2DServer =new LabelField();
		generalFieldSet.add(txtD2DServer, tdField);
		defineSourceNode(generalFieldSet);
		defineHeartBeat(generalFieldSet);
		StandbyType standByType = parentWindow.getStandbyType();
		if(standByType == StandbyType.PHYSICAL){
			definePhysicalStandbyMachine(generalFieldSet);
		}else if(standByType == StandbyType.VIRTUAL){
			
		}
		defineServerScript(generalFieldSet);
		defineTargetScript(generalFieldSet);
		defineAdvancedSetting(generalFieldSet);
		container.add(generalFieldSet);
		return container;
		
	}
	
	private void defineSourceNode(LayoutContainer container){
		sourceNodeGrid = new SourceNodeGrid(SOURCE_TABLE_HIGHT,SOURCE_TABLE_WIDTH);
		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT); 
		container.add(sourceNodeGrid, tdColspan);
		
		LabelField label = new LabelField(UIContext.Constants.automaticallyStartMachine()+UIContext.Constants.delimiter());
		container.add(label);
		txtAutoStartMachine = new LabelField();
		container.add(txtAutoStartMachine);
		
		label = new LabelField(UIContext.Constants.howToStartMachine()+UIContext.Constants.delimiter());
		container.add(label);	
		txtStartMachineMethod = new LabelField();
		container.add(txtStartMachineMethod);
	}
	
	private void defineHeartBeat(LayoutContainer container){
		LabelField label = new LabelField(UIContext.Constants.heartBeatFrequencySummary()+UIContext.Constants.delimiter());
		container.add(label);	
		txtHeartBeatFrequency = new LabelField();
		container.add(txtHeartBeatFrequency);
		
		label = new LabelField(UIContext.Constants.heartBeatTimesSummary()+UIContext.Constants.delimiter());
		container.add(label);
		txtHeartBeatTimeout = new LabelField();
		container.add(txtHeartBeatTimeout);
		
	}
	
	private void definePhysicalStandbyMachine(LayoutContainer container){
		physicalStandbyMachinesGird = new PhysicalStandbyMachinesGrid(SOURCE_TABLE_HIGHT,SOURCE_TABLE_WIDTH);
		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT); 
		container.add(physicalStandbyMachinesGird, tdColspan);
	}
	
	private void defineServerScript(LayoutContainer container){
		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("70%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		lblServerScriptBeforeJob = new LabelField(UIContext.Constants.summaryServerScriptBeforeJob()+UIContext.Constants.delimiter());
		lblServerScriptBeforeJob.setAutoWidth(true);
		container.add(lblServerScriptBeforeJob, tableDataLabel);
		txtServerScriptBeforeJob = new LabelField();
		container.add(txtServerScriptBeforeJob, tableDataField);
		
		lblServerScriptAfterJob = new LabelField(UIContext.Constants.summaryServerScriptAfterJob()+UIContext.Constants.delimiter());
		lblServerScriptAfterJob.setAutoWidth(true);
		container.add(lblServerScriptAfterJob, tableDataLabel);
		txtServerScriptAfterJob = new LabelField();
		container.add(txtServerScriptAfterJob, tableDataField);
	}
	
	private void defineTargetScript(LayoutContainer container){
		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("70%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		lblTargetScriptBeforeJob = new LabelField(UIContext.Constants.summaryTargetScriptBeforeJob()+UIContext.Constants.delimiter());
		lblTargetScriptBeforeJob.setAutoWidth(true);
		container.add(lblTargetScriptBeforeJob, tableDataLabel);
		txtTargetScriptBeforeJob = new LabelField();
		container.add(txtTargetScriptBeforeJob, tableDataField);
		
		lblTargetScriptAfterJob = new LabelField(UIContext.Constants.summaryTargetScriptAfterJob()+UIContext.Constants.delimiter());
		lblTargetScriptAfterJob.setAutoWidth(true);
		container.add(lblTargetScriptAfterJob, tableDataLabel);
		txtTargetScriptAfterJob = new LabelField();
		container.add(txtTargetScriptAfterJob, tableDataField);
	}
	
	private void defineAdvancedSetting(LayoutContainer container){
		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("70%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		
		LabelField label = new LabelField(UIContext.Constants.resetForUser() + UIContext.Constants.delimiter());
		container.add(label,tableDataLabel);
		txtNewUsername = new LabelField();
		container.add(txtNewUsername,tableDataField);
		
		label = new LabelField(UIContext.Constants.sessionLocationSettings() + UIContext.Constants.delimiter());
		container.add(label, tableDataLabel);
		txtSessionLocationLocal = new LabelField();
		container.add(txtSessionLocationLocal, tableDataField);
	}
	
	private LayoutContainer defineJobNameField()
	{
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		container.setLayout(layout);
		
		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("80%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField lblJobName =new LabelField(UIContext.Constants.jobName());
		txtJobName=new TextField<String>();		
		txtJobName.setWidth(MAX_FIELD_WIDTH);
		StandbyType standByType = parentWindow.getStandbyType();
		if(standByType == StandbyType.PHYSICAL){
			txtJobName.setValue(UIContext.Constants.standbyPhysical());
		}else{
			txtJobName.setValue(UIContext.Constants.standbyVirtual());
		}
		container.add(lblJobName,tdLabel);
		container.add(txtJobName,tdField);
		return container;
	}
	
	public void refresh(){
		StandbyModel model = parentWindow.standbyModel;
		if(model.getJobName()!=null)
			txtJobName.setValue(model.getJobName());
		txtD2DServer.setValue(parentWindow.getD2DServerInfo().getServer());
		sourceNodeGrid.removeAllData();
		sourceNodeGrid.addData(model.getSourceNodeList());
		txtHeartBeatFrequency.setValue(model.getHeartBeatFrequency() + " " + UIContext.Constants.heartBeatSeconds());
		txtHeartBeatTimeout.setValue(model.getHeartBeatTime());
		txtAutoStartMachine.setValue(model.isAutoStartMachine() == true ? UIContext.Constants.yes():UIContext.Constants.no());
		txtStartMachineMethod.setValue(model.getStartMachineMethod() == PhysicalStandbyModel.START_MACHINE_METHOD_WAN_ON_LAN ? UIContext.Constants.wakeOnLan():UIContext.Constants.other());
		StandbyType standByType = parentWindow.getStandbyType();
		if(standByType == StandbyType.PHYSICAL){
			PhysicalStandbyModel physicalModel = (PhysicalStandbyModel)parentWindow.standbyModel;
			physicalStandbyMachinesGird.removeAllData();
			physicalStandbyMachinesGird.addData(physicalModel.getStandByMachineList());
		}else{
			
		}
		
		if(!Utils.isEmptyOrNull(model.getServerScriptAfterJob())){
			txtServerScriptAfterJob.setValue(model.getServerScriptAfterJob());
		}else{
			txtServerScriptAfterJob.setValue(UIContext.Constants.none());
		}
		
		if(!Utils.isEmptyOrNull(model.getServerScriptBeforeJob())){
			txtServerScriptBeforeJob.setValue(model.getServerScriptBeforeJob());
		}else{
			txtServerScriptBeforeJob.setValue(UIContext.Constants.none());
		}
		
		if(!Utils.isEmptyOrNull(model.getTargetScriptAfterJob())){
			txtTargetScriptAfterJob.setValue(model.getTargetScriptAfterJob());
		}else{
			txtTargetScriptAfterJob.setValue(UIContext.Constants.none());
		}
		
		if(!Utils.isEmptyOrNull(model.getTargetScriptBeforeJob())){
			txtTargetScriptBeforeJob.setValue(model.getTargetScriptBeforeJob());
		}else{
			txtTargetScriptBeforeJob.setValue(UIContext.Constants.none());
		}
		
		if(!Utils.isEmptyOrNull(model.getNewUsername())){
			txtNewUsername.setValue(model.getNewUsername());
		}else{
			txtNewUsername.setValue(UIContext.Constants.NA());
		}
		
		if ( Utils.isEmptyOrNull(model.getLocalPath()) ) {
			txtSessionLocationLocal.setValue(UIContext.Constants.NA());
		} else {
			txtSessionLocationLocal.setValue(model.getLocalPath());
		}
	}
	
	public String getJobName(){
		return txtJobName.getValue();
	}
	public void setJobName(String jobName){
		txtJobName.setValue(jobName);
	}
	
}
