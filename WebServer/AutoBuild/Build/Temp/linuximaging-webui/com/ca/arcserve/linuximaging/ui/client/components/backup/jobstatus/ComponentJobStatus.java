package com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus.i18n.JobStatusUIConstants;
import com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus.model.DataModalJobStatus;
import com.ca.arcserve.linuximaging.ui.client.components.backup.node.IBackupNodeService;
import com.ca.arcserve.linuximaging.ui.client.components.backup.node.IBackupNodeServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.UIContext;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ComponentJobStatus extends LayoutContainer {
	private IBackupNodeServiceAsync backupService = GWT.create(IBackupNodeService.class);
	public static final int REFRESH_INTERVAL = 5000;//3000;
	private static final JobStatusUIConstants jobStatusUIConstants = GWT.create(JobStatusUIConstants.class);
	private static final String tabIndex = "tabIndex";
	
	public static final String NODE="1";
	public static final String JOB_STATUS="2";
	public static final String JOB_HISTORY="3";
	public static final String JOB_LOG="4";
	
	public TabPanel JobStatusPanel = null;
	public ComponentJobStatusTable jobStatusTable = null;
	public String presentTabIndex=JOB_STATUS;
	private String nodeServerName;
	private Timer timer;
	private boolean isRun = false;
	
	public ComponentJobStatus() {
		this.setLayout(new FitLayout());
		JobStatusPanel = new TabPanel();
		JobStatusPanel.setHeight(260);
		JobStatusPanel.addListener(Events.Select, new Listener<TabPanelEvent>(){
        	
			public void handleEvent(TabPanelEvent be) {        		        		
        		presentTabIndex = be.getItem().getData(tabIndex);
        		
        	} });
		TabItem tbtmNode = new TabItem("Node");
		tbtmNode.setData(tabIndex,NODE);
		JobStatusPanel.add(tbtmNode);
		
		TabItem tbtmJobStatus = new TabItem(jobStatusUIConstants.jobStatus());
		tbtmJobStatus.setLayout(new FillLayout(Orientation.HORIZONTAL));
		jobStatusTable = new ComponentJobStatusTable();
		jobStatusTable.setHeight("100%");
		tbtmJobStatus.setData(tabIndex,JOB_STATUS);
		tbtmJobStatus.add(jobStatusTable);
		JobStatusPanel.add(tbtmJobStatus);
		
		TabItem tbtmJobHistory = new TabItem(jobStatusUIConstants.jobHistory());
		tbtmJobHistory.setData(tabIndex,JOB_HISTORY);
		JobStatusPanel.add(tbtmJobHistory);
		
		TabItem tbtmJobLog = new TabItem(jobStatusUIConstants.jobLog());
		tbtmJobLog.setData(tabIndex,JOB_LOG);
		JobStatusPanel.add(tbtmJobLog);
		add(JobStatusPanel);
		defineTimer();
	}
	
	private void defineTimer() {
		timer = new Timer() {   
	          @Override  
	          public void run() {   
	        	  refreshJobStatusTable();
	          }   
	        };   
	   
		
	}
	
	public void refreshData(String serverName){
		this.nodeServerName = serverName;
		if(!isRun){
			 timer.schedule(REFRESH_INTERVAL);
			 timer.scheduleRepeating(REFRESH_INTERVAL);
			 isRun=true;
		}
		if(presentTabIndex.equals(JOB_STATUS)){
			refreshJobStatusTable();
		}else if(presentTabIndex.equals(JOB_HISTORY)){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.INFO,"developing...");
		}else if(presentTabIndex.equals(JOB_LOG)){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.INFO,"developing...");
		}else{
			refreshJobStatusTable();
		}

	}
	
	public void refreshJobStatusTable() {
		backupService.GetJobStatusList(nodeServerName, new AsyncCallback<List<DataModalJobStatus>>(){

			@Override
			public void onFailure(Throwable caught) {
//				Utils.showErrorMessage(UIContext.Constants.productName(),MessageBox.INFO,caught.getMessage());				
			}

			@Override
			public void onSuccess(List<DataModalJobStatus> result) {
				if(result!=null){
					jobStatusTable.refreshData(nodeServerName,result);
				}
			}
			
		});
	}
}
