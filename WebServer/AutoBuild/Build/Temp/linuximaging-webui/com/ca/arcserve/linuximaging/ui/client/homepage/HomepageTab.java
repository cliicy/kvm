package com.ca.arcserve.linuximaging.ui.client.homepage;


import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.homepage.activitylog.ActivityLogTable;
import com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage.BackupStoragePanel;
import com.ca.arcserve.linuximaging.ui.client.homepage.dashboard.DashboardTable;
import com.ca.arcserve.linuximaging.ui.client.homepage.jobhistory.JobHistoryTable;
import com.ca.arcserve.linuximaging.ui.client.homepage.jobstatus.JobStatusTable;
import com.ca.arcserve.linuximaging.ui.client.homepage.node.NodeTable;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.DashboardModel;
import com.ca.arcserve.linuximaging.ui.client.toolbar.ToolBarPanel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;


public class HomepageTab extends LayoutContainer {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	public static final int REFRESH_INTERVAL = 5000;//3000;
	private static final String tabIndex = "tabIndex";
	
	public static final String DASHBOARD="0";
	public static final String NODE="1";
	public static final String JOB_STATUS="2";
	public static final String JOB_HISTORY="3";
	public static final String JOB_LOG="4";
	public static final String BACKUP_STORAGE = "5";
	
	public TabPanel tabPanel = null;
	public JobStatusTable jobStatusTable = null;
	public String presentTabIndex=NODE;
	public ServiceInfoModel currentServer;
	private Timer timer;
	private boolean isRun = false;
	public ToolBarPanel toolBar;
	public NodeTable nodeTable;
	public BackupStoragePanel storagePanel;
	public JobHistoryTable historyTable;
	public ActivityLogTable activityLogTable = null;
	private TabItem tbtmNode;
	private TabItem tbtmJobStatus;
	private TabItem tbtmJobHistory;
	private TabItem tbtmJobLog;
	private TabItem tbtmStorage;
	
	private TabItem tbtmDashboard;
	private DashboardTable dashboardTable;
	private boolean isServerChanged;
	private LayoutContainer serverNotReachableContainer;
	private LabelField errorLabel;
	private boolean needRefresh = true;
	
	public HomepageTab() {
		this.setStyleAttribute("background-color", "white");
		this.setLayout(new FitLayout());
		tabPanel = new TabPanel();
		tabPanel.addListener(Events.Select, new Listener<TabPanelEvent>(){
        	
			public void handleEvent(TabPanelEvent be) {        		        		
        		presentTabIndex = be.getItem().getData(tabIndex);
        		refreshTabItemAndToolBar();
        	} });
		
		tbtmDashboard = new TabItem(UIContext.Constants.dashboard());
		tbtmDashboard.setLayout(new FillLayout(Orientation.HORIZONTAL));
		dashboardTable=new DashboardTable(this);
		dashboardTable.setHeight("100%");
		tbtmDashboard.setData(tabIndex,DASHBOARD);
		tbtmDashboard.add(dashboardTable);
		tabPanel.add(tbtmDashboard);
		
		tbtmNode = new TabItem(UIContext.Constants.tabItemNode());
		tbtmNode.setLayout(new FillLayout(Orientation.HORIZONTAL));
		nodeTable=new NodeTable(this);
		nodeTable.setHeight("100%");
		tbtmNode.setData(tabIndex,NODE);
		tbtmNode.add(nodeTable);
		tabPanel.add(tbtmNode);
		
		tbtmJobStatus = new TabItem(UIContext.Constants.jobStatus());
		tbtmJobStatus.setLayout(new FillLayout(Orientation.HORIZONTAL));
		jobStatusTable = new JobStatusTable(this);
		jobStatusTable.setHeight("100%");
		tbtmJobStatus.setData(tabIndex,JOB_STATUS);
		tbtmJobStatus.add(jobStatusTable);
		tabPanel.add(tbtmJobStatus);
		
		tbtmJobHistory = new TabItem(UIContext.Constants.jobHistory());
		tbtmJobHistory.setLayout(new FillLayout(Orientation.HORIZONTAL));
		historyTable=new JobHistoryTable(this);
		historyTable.setHeight("100%");
		tbtmJobHistory.setData(tabIndex,JOB_HISTORY);
		tbtmJobHistory.add(historyTable);
		tabPanel.add(tbtmJobHistory);
		
		tbtmJobLog = new TabItem(UIContext.Constants.activityLog());
		tbtmJobLog.setLayout(new FillLayout(Orientation.HORIZONTAL));
		activityLogTable = new ActivityLogTable(this);
		activityLogTable.setHeight("100%");
		tbtmJobLog.setData(tabIndex,JOB_LOG);
		tbtmJobLog.add(activityLogTable);
		tabPanel.add(tbtmJobLog);
		
		tbtmStorage = new TabItem(UIContext.Constants.backupStorage());
		tbtmStorage.setLayout(new FillLayout(Orientation.HORIZONTAL));
		storagePanel = new BackupStoragePanel(this);
		storagePanel.setHeight("100%");
		tbtmStorage.setData(tabIndex,BACKUP_STORAGE);
		tbtmStorage.add(storagePanel);
		tabPanel.add(tbtmStorage);
		
		add(tabPanel);
		
		addErrorContainer();
		defineTimer();
	}
	
	private void addErrorContainer(){
		serverNotReachableContainer = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		serverNotReachableContainer.setLayout(layout);
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		td.setVerticalAlign(VerticalAlignment.MIDDLE);
		errorLabel = new LabelField(UIContext.Constants.d2dServerNotReachable());
		errorLabel.setStyleAttribute("color", "red");
		serverNotReachableContainer.add(errorLabel,td);
		serverNotReachableContainer.hide();
		add(serverNotReachableContainer);
	}
	
	public void showTabItem(String tabIndex){
		if(tabIndex.equals(DASHBOARD)){
			tabPanel.setSelection(tbtmDashboard);
		}else if(tabIndex.equals(NODE)){
			tabPanel.setSelection(tbtmNode);
		}else if(tabIndex.equals(JOB_STATUS)){
			tabPanel.setSelection(tbtmJobStatus);
		}else if(tabIndex.equals(JOB_HISTORY)){
			tabPanel.setSelection(tbtmJobHistory);
		}else if(tabIndex.equals(JOB_LOG)){
			tabPanel.setSelection(tbtmJobLog);
		}else if(tabIndex.equals(BACKUP_STORAGE)){
			tabPanel.setSelection(tbtmStorage);
		}
	}
	
	public TabItem getJobStatusTabItem(){
		return tbtmJobStatus;
	}
	
	public TabItem getJobHistoryTabItem(){
		return tbtmJobHistory;
	}
	
	protected void refreshTabItemAndToolBar() {
		if(presentTabIndex.equals(DASHBOARD)){
			toolBar.node.setDefaultState();
			toolBar.job.setDefaultState();
			toolBar.schedule.disableAll();
			toolBar.tool.disableFilter();
			toolBar.storage.setModifyAndDeleteEnable(false);
			refreshDashboardTable();
		}else if(presentTabIndex.equals(NODE)){
			nodeTable.refreshNodeButtonGroupInToolbar();
			toolBar.job.setDefaultState();
			toolBar.schedule.disableAll();
			toolBar.tool.enableFilter();
			toolBar.storage.setModifyAndDeleteEnable(false);
			refreshNodeTable();
		}else if(presentTabIndex.equals(JOB_STATUS)){
			jobStatusTable.refreshJobButtonGroupInToolbar();
			toolBar.node.setDefaultState();
			toolBar.tool.enableFilter();
			toolBar.storage.setModifyAndDeleteEnable(false);
			if(needRefresh){
				refreshJobStatusTable(true);
			}
		}else if(presentTabIndex.equals(JOB_HISTORY)){
			toolBar.node.setDefaultState();
			toolBar.schedule.disableAll();
			toolBar.tool.enableFilter();
			historyTable.refreshJobButtonGroupInToolbar();
			toolBar.storage.setModifyAndDeleteEnable(false);
			if(needRefresh){
				refreshJobHistoryTable(false);
			}
		}else if(presentTabIndex.equals(JOB_LOG)){
			toolBar.node.setDefaultState();
			toolBar.job.setDefaultState();
			toolBar.schedule.disableAll();
			toolBar.tool.enableFilter();
			toolBar.storage.setModifyAndDeleteEnable(false);
			if(needRefresh){
				refreshJobLogTable();
			}
		}else if(presentTabIndex.equals(BACKUP_STORAGE)){
			storagePanel.refreshJobButtonGroupInToolbar();
			toolBar.node.setDefaultState();
			toolBar.job.setDefaultState();
			toolBar.schedule.disableAll();
			toolBar.tool.disableFilter();
			refreshBackupStorageTable();
		}
		
	}

	private void defineTimer() {
		timer = new Timer() {   
	          @Override  
	          public void run() { 
	        	  if(presentTabIndex.equals(DASHBOARD)){
	        		  refreshDashboard_resourceUsagePanel();
	        	  }else if(presentTabIndex.equals(JOB_STATUS)){
	        		  refreshJobStatusTable(false);
	        	  }else if(presentTabIndex.equals(JOB_HISTORY)){
	        		  //refreshJobHistoryTable(true);
	        	  }
	          }
	        };   
	   
		
	}
	
	public void refreshData(ServiceInfoModel serverName){
		this.currentServer = serverName;
		if(!isRun){
			 timer.schedule(REFRESH_INTERVAL);
			 timer.scheduleRepeating(REFRESH_INTERVAL);
			 isRun=true;
		}
		if(presentTabIndex.equals(DASHBOARD)){
			toolBar.node.setDefaultState();
			toolBar.job.setDefaultState();
			toolBar.tool.disableFilter();
			toolBar.schedule.disableAll();
			refreshDashboardTable();
		}else if(presentTabIndex.equals(NODE)){
			toolBar.node.setDefaultState();
			toolBar.job.setDefaultState();
			toolBar.tool.enableFilter();
			toolBar.schedule.disableAll();
			refreshNodeTable();
		}else if(presentTabIndex.equals(JOB_STATUS)){
			toolBar.node.setDefaultState();
			toolBar.job.setDefaultState();
			toolBar.tool.enableFilter();
			refreshJobStatusTable(true);
		}else if(presentTabIndex.equals(JOB_HISTORY)){
			toolBar.node.setDefaultState();
			toolBar.schedule.disableAll();
			toolBar.tool.enableFilter();
			refreshJobHistoryTable(false);
			historyTable.clearActivitlog();
		}else if(presentTabIndex.equals(JOB_LOG)){
			toolBar.node.setDefaultState();
			toolBar.job.setDefaultState();
			toolBar.schedule.disableAll();
			toolBar.tool.enableFilter();
			refreshJobLogTable();
		}else if(presentTabIndex.equals(BACKUP_STORAGE)){
			toolBar.node.setDefaultState();
			toolBar.job.setDefaultState();
			toolBar.schedule.disableAll();
			toolBar.tool.disableFilter();
			refreshBackupStorageTable();
		}

	}
	
	//this methods must called before refreshData if it need to call
	public void resetFilter(ServiceInfoModel serverName) {
		checkServerChanged(serverName);
		if ( isServerChanged() ) {
			this.nodeTable.resetFilter();
			this.jobStatusTable.resetFilter();
			this.historyTable.resetFilter();
			this.activityLogTable.resetFilter();
			if(tabPanel != null){
				for(TabItem item : tabPanel.getItems()){
					item.setIcon(null);
				}
			}
			this.dashboardTable.resetData();
			this.nodeTable.resetData();
			this.jobStatusTable.resetData();
			this.historyTable.resetData();
			this.activityLogTable.resetData();
			this.storagePanel.resetData();
			/*if(presentTabIndex.equals(NODE)){
				this.nodeTable.resetFilter();
			}else if(presentTabIndex.equals(JOB_STATUS)){
				this.jobStatusTable.resetFilter();
			}else if(presentTabIndex.equals(JOB_HISTORY)){
				this.historyTable.resetFilter();
			}else if(presentTabIndex.equals(JOB_LOG)){
				this.activityLogTable.resetFilter();
			}*/
		}
	}
	public void refreshDashboard_resourceUsagePanel() {
		
	}
	public void refreshDashboardTable() {
		dashboardTable.mask(UIContext.Constants.loading());
		service.getDashboardInformation(currentServer, new BaseAsyncCallback<DashboardModel>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				dashboardTable.unmask();
				toolBar.tool.enableRefresh();
				changeContent(false);
			}

			@Override
			public void onSuccess(DashboardModel result) {
				dashboardTable.unmask();
				dashboardTable.refreshData(result);
				toolBar.tool.enableRefresh();
				changeContent(true);
			}
			
		});
		dashboardTable.refreshTable();
		
	}
	
	public void refreshJobLogTable() {
		showTabItem(HomepageTab.JOB_LOG);
		activityLogTable.refreshTable();
		toolBar.tool.enableRefresh();
	}
	
	public void refreshBackupStorageTable(){
		showTabItem(HomepageTab.BACKUP_STORAGE);
		storagePanel.refreshData();
		toolBar.tool.enableRefresh();
	}

	public void refreshJobHistoryTable(boolean noMask) {
		/*service.getJobHistoryList(currentServer, new AsyncCallback<List<JobStatusModel>>(){

			@Override
			public void onFailure(Throwable caught) {
				historyTable.unmask();
				toolBar.tool.enableRefresh();
			}

			@Override
			public void onSuccess(List<JobStatusModel> result) {
				historyTable.unmask();
				toolBar.tool.enableRefresh();
				if(result!=null){
					historyTable.refreshData(currentServer,result);
				}
			}
		});*/
		historyTable.refreshData(noMask);
		toolBar.tool.enableRefresh();
	}

	public void refreshNodeTable() {
		nodeTable.unmask();
		nodeTable.refreshTable();
		toolBar.tool.enableRefresh();
		/*service.getNodeList(currentServer, new AsyncCallback<List<NodeModel>>(){

			@Override
			public void onFailure(Throwable caught) {
				nodeTable.unmask();
				toolBar.tool.enableRefresh();
			}

			@Override
			public void onSuccess(List<NodeModel> result) {
				nodeTable.unmask();
				toolBar.tool.enableRefresh();
				if(result!=null){
					nodeTable.refreshData(currentServer,result);
				}
			}
			
		});*/
	}

	public void refreshJobStatusTable(boolean mask) {
		if(mask){
			jobStatusTable.refreshTable();
		}else{
			jobStatusTable.refreshTableWithoutMask();
		}
		
		toolBar.tool.enableRefresh();
		/*service.getJobStatusList(currentServer, new AsyncCallback<List<JobStatusModel>>(){

			@Override
			public void onFailure(Throwable caught) {
				jobStatusTable.unmask();
				toolBar.tool.enableRefresh();
				errorCount++;
				if(errorCount>=5){
					cancelTimer();
					showMessageBox();
				}
			}

			@Override
			public void onSuccess(List<JobStatusModel> result) {
				jobStatusTable.unmask();
				toolBar.tool.enableRefresh();
				errorCount=0;
				if(result!=null){
					jobStatusTable.refreshData(currentServer,result);
				}
			}
			
		});*/
	}
	/*private void showMessageBox(){
		MessageBox messageBox =Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.cantConnectToServer());
		messageBox.addCallback(new Listener<MessageBoxEvent>(){

			@Override
			public void handleEvent(MessageBoxEvent be) {
				Window.Location.reload();
			}
			
		});
	}*/


	public void setToolBarPanel(ToolBarPanel toolBarPanel) {
		this.toolBar=toolBarPanel;
	}

	public void cancelTimer() {
		timer.cancel();
	}

	public boolean checkServerChanged(ServiceInfoModel model) {
		if ( model == null && currentServer == null ) {
			isServerChanged = false;
		} else if ( currentServer != null && model != null && currentServer.getServer().equalsIgnoreCase(model.getServer()) ) {
			isServerChanged = false;
		} else {
			isServerChanged = true;
		}
		
		return isServerChanged;
	}
	
	public boolean isServerChanged() {
		return isServerChanged;
	}
	
	public void changeContent(boolean isReachable){
		if(isReachable){
			if(!isRun){
				 timer.schedule(REFRESH_INTERVAL);
				 timer.scheduleRepeating(REFRESH_INTERVAL);
				 isRun=true;
			}
			tabPanel.show();
			serverNotReachableContainer.hide();
		}else{
			timer.cancel();
			isRun = false;
			tabPanel.hide();
			errorLabel.setValue(UIContext.Constants.d2dServerNotReachable());
			serverNotReachableContainer.show();
		}
	}
	
	public void changeContent(String message){
		timer.cancel();
		isRun = false;
		tabPanel.hide();
		errorLabel.setValue(message);
		serverNotReachableContainer.show();
	}
	
	public void changeToolBar(boolean isReachable){
		if(isReachable){
			toolBar.setAllEnabled(true);
		}else{
			toolBar.setAllEnabled(false);
		}
	}
	
	public void setErrorLabel(String errorContent){
		errorLabel.setValue(errorContent);
		tabPanel.hide();
		serverNotReachableContainer.show();
	}
	
	public void setCurrentServer(ServiceInfoModel model){
		this.currentServer = model;
	}

	public void setNeedRefresh(boolean needRefresh) {
		this.needRefresh = needRefresh;
	}
	
}
