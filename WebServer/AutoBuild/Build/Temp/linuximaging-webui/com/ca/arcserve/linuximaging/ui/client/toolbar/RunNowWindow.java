package com.ca.arcserve.linuximaging.ui.client.toolbar;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.TooltipSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;

public class RunNowWindow extends Window {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	//private SimpleComboBox<String> jobType;
	private Radio radioFull;
	private Radio radioIncremental;
	private Radio radioVerify;
	//private Radio radioRunSelectedNode;
	//private Radio radioRunSameJobName;
	private SimpleComboBox<String> runType;
	private Button okButton;
	private LabelField warningLabel;
	private BackupNowKeyListener keyListener =  new BackupNowKeyListener();	
	private HomepageTab parentTabPanel;;
	private ServiceInfoModel webServer;
	private JobStatusModel jobModel;
	private Button cancelButton;
	
	
	public RunNowWindow(ServiceInfoModel server, HomepageTab tabPanel, JobStatusModel model){
		this.setWidth(400);
		this.setResizable(false);
		this.setHeading(UIContext.Constants.runNowWindowHeading());

		webServer = server;
		parentTabPanel = tabPanel;
		jobModel = model;
	
		TableLayout layout = new TableLayout(2);
		layout.setWidth("98%");
		layout.setCellPadding(4);
		layout.setCellSpacing(4);
		this.setLayout(layout);
		
		warningLabel = new LabelField();
		warningLabel.setVisible(false);
		warningLabel.setText(UIContext.Constants.runNowWindowCompressionChanged());
		//this.add(warningLabel);
		TableData labelTD = new TableData();
		labelTD.setWidth("25%");
		LabelField label = new LabelField(UIContext.Constants.backupType() + UIContext.Constants.delimiter());
		this.add(label,labelTD);
		
		
		/*jobType = new TooltipSimpleComboBox<String>();
		jobType.setWidth(250);
		jobType.setTriggerAction(TriggerAction.ALL);
		jobType.setEditable(false);
		jobType.add(UIContext.Constants.incrementalBackup());
		jobType.add(UIContext.Constants.fullBackup());
		jobType.add(UIContext.Constants.verifyBackup());
		jobType.setSimpleValue(UIContext.Constants.incrementalBackup());
		this.add(jobType);*/
		
		RadioGroup radioGroup = new RadioGroup();
		
		radioIncremental = new Radio(){
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
			}
			
		};
		radioIncremental.setId("RunNow_Radio_Incremental");
		radioIncremental.setBoxLabel(UIContext.Constants.incrementalBackup());
		radioIncremental.addKeyListener(keyListener);
		this.add(radioIncremental);
		radioGroup.add(radioIncremental);
		
		radioVerify = new Radio(){
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
			}
			
		};
		radioVerify.setId("RunNow_Radio_Veriry");
		radioVerify.setBoxLabel(UIContext.Constants.verifyBackup());
		radioVerify.addKeyListener(keyListener);
		this.add(new LabelField(""));
		this.add(radioVerify);
		radioGroup.add(radioVerify);
		
		radioFull = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
			}
			
		};
		radioFull.setId("RunNow_Radio_Full");
		radioFull.setBoxLabel(UIContext.Constants.fullBackup());
		radioFull.addKeyListener(keyListener);
		this.add(new LabelField(""));
		this.add(radioFull);
		radioGroup.add(radioFull);
		radioGroup.setValue(radioIncremental);
		
		label = new LabelField(UIContext.Constants.runFor() + UIContext.Constants.delimiter());
		this.add(label);
		/*radioRunSelectedNode = new Radio();
		radioRunSelectedNode.addKeyListener(keyListener);
		this.add(getRadioContainer(radioRunSelectedNode,UIContext.Messages.runJobForNode(model.getNodeName())));
		this.add(new LabelField(""));
		radioRunSameJobName = new Radio();
		radioRunSameJobName.addKeyListener(keyListener);
		this.add(getRadioContainer(radioRunSameJobName,UIContext.Messages.runJobForNodeProtectedBy(model.getJobName())));
		
		RadioGroup rg = new RadioGroup();
		rg.add(radioRunSelectedNode);
		rg.add(radioRunSameJobName);
		rg.setValue(radioRunSelectedNode);*/
		
		runType = new TooltipSimpleComboBox<String>();
		runType.setWidth(250);
		runType.setTriggerAction(TriggerAction.ALL);
		runType.setEditable(false);
		runType.add(UIContext.Constants.selectedNode());
		runType.add(UIContext.Constants.nodesProtectedBySelectedJob());
		runType.setSimpleValue(UIContext.Constants.selectedNode());
		this.add(runType);
		
		LayoutContainer container = new LayoutContainer();
		TableLayout rowLayout = new TableLayout();
		rowLayout.setColumns(2);
		rowLayout.setWidth("100%");
		container.setLayout(rowLayout);
		
		this.add(container);
		cancelButton = new Button();
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					RunNowWindow.this.hide();
				}
			});
		
		okButton = new Button();
		okButton.setText(UIContext.Constants.OK());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {					
					processBeforeRunJob();
					
					int backupType = JobType.BACKUP_FULL.getValue();
					
					if (radioIncremental.getValue())
						backupType = JobType.BACKUP_INCREMENTAL.getValue();
					else if (radioVerify.getValue())
						backupType = JobType.BACKUP_VERIFY.getValue();
					
					jobModel.setJobType(backupType);
					service.runJob(webServer, jobModel,isRunSameJob(), new BaseAsyncCallback<Long>(){
						
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							parentTabPanel.toolBar.job.runJob.enable();
							/*if(caught instanceof ClientException){
								Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,((ClientException)caught).getDisplayMessage());
							}else{
								Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,caught.getMessage());
							}*/
							processAfterRunJob(false);
						}
			
						@Override
						public void onSuccess(Long result) {
							parentTabPanel.toolBar.job.enableCancel();
							if(result==0){
								parentTabPanel.refreshJobStatusTable(true);
								Info.display(UIContext.Constants.productName(), UIContext.Constants.runNowWindowSubmitSuccessful());
							}else if(result == 4){
								Utils.showMessage(UIContext.Constants.productName(),MessageBox.WARNING, UIContext.Constants.backupNowJobWaitingInJobQueue());
							}else{
								Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR, UIContext.Constants.runNowWindowSubmitFailed());
							}
							processAfterRunJob(true);
						}
					});
				}
			});
		this.setButtonAlign(HorizontalAlignment.CENTER);
		this.addButton(okButton);
		this.addButton(cancelButton);
		Button helpButton = new Button(UIContext.Constants.help());
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				Utils.showURL(UIContext.helpLink+HelpLinkItem.RUNNOW_WINDOW);
			}
			
		});
		this.addButton(helpButton);
	}
	
	private boolean isRunSameJob(){
		if(runType.getSimpleValue().equals(UIContext.Constants.selectedNode())){
			return false;
		}else{
			return true;
		}
	}
	
	/*private int getJobType(){
		if(jobType.getSimpleValue().equals(UIContext.Constants.incrementalBackup())){
			return JobType.BACKUP_INCREMENTAL.getValue();
		}else if (jobType.getSimpleValue().equals(UIContext.Constants.fullBackup())){
			return JobType.BACKUP_FULL.getValue();
		}else{
			return JobType.BACKUP_VERIFY.getValue();
		}
	}*/
	
	/*private LayoutContainer getRadioContainer(final Radio radio,String label){
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(radio);
		LabelField labelField = new LabelField(label);
		labelField.setStyleAttribute("padding-left", "4px");
		labelField.addListener(Events.OnClick, new Listener<FieldEvent>(){

			@Override
			public void handleEvent(FieldEvent be) {
				radio.setValue(true);
			}
			
		});
		panel.add(labelField);
		return panel;
	}*/


	@Override
	protected void onRender(Element target, int index) {
		  super.onRender(target, index);
	}	
	
	public void setKeyListener(BackupNowKeyListener keyListener) {
		this.keyListener = keyListener;
	}

	public BackupNowKeyListener getKeyListener() {
		return keyListener;
	}
	
	private void processBeforeRunJob() {
		okButton.setEnabled(false);
		cancelButton.setEnabled(false);
		this.mask();
	}
	
	private void processAfterRunJob(boolean close) {
		okButton.setEnabled(true);
		cancelButton.setEnabled(true);
		this.unmask();
		if ( close ) {
			this.hide();
		}
	}

	class BackupNowKeyListener extends KeyListener{

		@Override
		public void componentKeyPress(ComponentEvent event) {
			if (event.getKeyCode() == KeyCodes.KEY_ENTER)
				okButton.fireEvent(Events.Select);
		}
		
	}
}
