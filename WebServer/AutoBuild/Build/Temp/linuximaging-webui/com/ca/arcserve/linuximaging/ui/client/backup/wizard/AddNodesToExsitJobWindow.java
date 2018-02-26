package com.ca.arcserve.linuximaging.ui.client.backup.wizard;


import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.BaseComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.JobScriptModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.toolbar.ToolBarPanel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class AddNodesToExsitJobWindow extends Dialog{
	public final int LABEL_WIDTH=120;
	public final int FIELD_WIDTH=240;
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	public final int WINDOW_WIDTH=365;
	public final int WINDOW_HEIGHT=150;
	
	private ToolBarPanel toolBar;
	private BaseComboBox<JobScriptModel> cmbJobName;
	private ListStore<JobScriptModel> jobNameStore;
	private List<NodeModel> nodes;
	
	public AddNodesToExsitJobWindow(ToolBarPanel toolBar,final List<NodeModel> nodeList) {
		this.toolBar=toolBar;
		this.nodes=nodeList;
		setButtons(Dialog.YESNOCANCEL);
		setButtonAlign(HorizontalAlignment.CENTER);
		setModal(true);
		this.setResizable(false);
		this.setWidth(WINDOW_WIDTH);
		this.setHeight(WINDOW_HEIGHT);

		setHeading(UIContext.Constants.addSelectedNode2ExistJob());
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("95%");
		tableLayout.setCellPadding(5);
		tableLayout.setCellSpacing(5);
		tableLayout.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		tableLayout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		setLayout(tableLayout);
		

		LabelField lblJobName = new LabelField(UIContext.Constants.jobName());
		add(lblJobName);
		cmbJobName = new BaseComboBox<JobScriptModel>();
		cmbJobName.setWidth(FIELD_WIDTH);
		cmbJobName.setDisplayField("jobName");
		cmbJobName.setEditable(false);
		cmbJobName.setTemplate(getTemplate());
		jobNameStore = new ListStore<JobScriptModel>();
		cmbJobName.setStore(jobNameStore);
		add(cmbJobName);
		
		this.getButtonById(YES).setText(UIContext.Constants.OK());
		this.getButtonById(YES).disable();
		this.getButtonById(NO).setText(UIContext.Constants.cancel());
		this.getButtonById(CANCEL).setText(UIContext.Constants.help());
		//ok
		this.getButtonById(YES).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final JobScriptModel job = cmbJobName.getValue();
				if(job==null){
					return;
				}
				addNodeIntoJobScript(job);
			}

		});
		//cancel
		this.getButtonById(NO).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				AddNodesToExsitJobWindow.this.hide();
			}
		});
		//help
		this.getButtonById(CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Utils.showURL(UIContext.helpLink+HelpLinkItem.BACKUP_ADD_NODE_TO_JOB);
			}
		});
	}
	
	private native String getTemplate() /*-{ 
	    return  [ 
	    '<tpl for=".">', 
	    '<div class="x-combo-list-item" qtip="{jobName}" >{jobName}</div>', 
	    '</tpl>' 
	    ].join(""); 
	}-*/;
	
	private void addNodeIntoJobScript(final JobScriptModel job) {
		service.addNodeIntoJobScript(toolBar.tabPanel.currentServer, nodes, job, new BaseAsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				//Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR, caught.getMessage());
			}

			@Override
			public void onSuccess(Integer result) {
				if(result==0){
					AddNodesToExsitJobWindow.this.hide();
					toolBar.tabPanel.refreshNodeTable();
				}else{
					Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR, UIContext.Messages.failToAddNode(job.getJobName()));
				}
				
			}
			
		});
	}

	public void refresh() {
		service.getBackupJobScriptList(toolBar.tabPanel.currentServer, new BaseAsyncCallback<List<JobScriptModel>>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(List<JobScriptModel> result) {
				if(result!=null&&result.size()>0){
					jobNameStore.removeAll();
					jobNameStore.add(result);
					cmbJobName.setValue(result.get(0));
					getButtonById(YES).enable();
				}
				
			}
			
		});
		
	}
	

}
