package com.ca.arcserve.linuximaging.ui.client.toolbar;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.TooltipSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;

public class DeleteJobWindow extends Window {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	//private Radio radioSelectedNode;
	//private Radio radioSameJobName;
	private SimpleComboBox<String> runType;
	private Button okButton;
	private BackupNowKeyListener keyListener =  new BackupNowKeyListener();	
	private HomepageTab parentTabPanel;;
	private ServiceInfoModel webServer;
	private JobStatusModel jobModel;
	private Button cancelButton;
	
	
	public DeleteJobWindow(ServiceInfoModel server, HomepageTab tabPanel, JobStatusModel model){
		this.setWidth(400);
		this.setResizable(false);
		this.setHeading(UIContext.Constants.deleteJob());

		webServer = server;
		parentTabPanel = tabPanel;
		jobModel = model;
	
		TableLayout layout = new TableLayout(2);
		layout.setWidth("95%");
		layout.setCellPadding(4);
		layout.setCellSpacing(4);
		this.setLayout(layout);
		
		TableData td = new TableData();
		td.setColspan(2);
		
		LabelField label = new LabelField(UIContext.Constants.deleteJobDescription());
		this.add(label,td);
		TableData labelTD = new TableData();
		labelTD.setWidth("25%");
		label = new LabelField(UIContext.Constants.deleteFor() + UIContext.Constants.delimiter());
		this.add(label,labelTD);
		
		/*radioSelectedNode = new Radio();
		radioSelectedNode.addKeyListener(keyListener);
		this.add(getRadioContainer(radioSelectedNode,UIContext.Messages.runJobForNode(model.getNodeName())));

		this.add(new LabelField(""));
		
		radioSameJobName = new Radio();
		radioSameJobName.addKeyListener(keyListener);
		this.add(getRadioContainer(radioSameJobName,UIContext.Messages.runJobForNodeProtectedBy(model.getJobName())));
		
		RadioGroup rg = new RadioGroup();
		rg.add(radioSelectedNode);
		rg.add(radioSameJobName);
		rg.setValue(radioSelectedNode);*/
		
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
					DeleteJobWindow.this.hide();
				}
		});
		
		okButton = new Button();
		okButton.setText(UIContext.Constants.OK());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {					
					processBefore();
					
					service.deleteJob(webServer, jobModel,Boolean.TRUE,isRunSameJob(), new BaseAsyncCallback<Integer>(){
						
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							processAfter(false);
						}
			
						@Override
						public void onSuccess(Integer result) {
							if(result==0){
								parentTabPanel.refreshJobStatusTable(true);
								String text = UIContext.Constants.deleteJobSubmitSuccessfully();
								InfoConfig infoConfig = new InfoConfig(UIContext.Constants.productName(), text);
								if(isRunSameJob()){
									infoConfig.text = UIContext.Constants.deleteJobSubmitSuccessfullyWithWarning();
									infoConfig.width = 300;
									infoConfig.height = 100;
								}
								Info.display(infoConfig);
							}else{
								Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Messages.deleteJobFailed(jobModel.getJobName()));
							}
							processAfter(true);
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
				Utils.showURL(UIContext.helpLink+HelpLinkItem.DELETE_WINDOW);
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
	
	private void processBefore() {
		okButton.setEnabled(false);
		cancelButton.setEnabled(false);
		this.mask();
	}
	
	private void processAfter(boolean close) {
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
