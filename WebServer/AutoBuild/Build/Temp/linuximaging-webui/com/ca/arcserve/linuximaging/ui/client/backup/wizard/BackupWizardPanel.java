package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.Date;
import java.util.List;
import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.SessionLocationPanel;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.common.icon.FlashImageBundle;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.BackupTemplateModel;
import com.ca.arcserve.linuximaging.ui.client.exception.BusinessLogicException;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage.BackupStorageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage.BackupStorageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupModel;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

public class BackupWizardPanel extends Window{
	public static int WIZARD_WIDTH=840;
	public static int WIZARD_HIGHT=550;
	public static int LEFT_PANEL_WIDTH=140;
	public static int RIGHT_PANEL_WIDTH=670;
	public static int RIGHT_PANEL_HIGHT=480;
	
	private static final HomepageServiceAsync backupService = GWT.create(HomepageService.class);
	private static final BackupStorageServiceAsync storageService = GWT.create(BackupStorageService.class);
	private static final FlashImageBundle iconBundle = GWT.create(FlashImageBundle.class);
	private static final int BACKUP_SERVER_INDEX = 0;
	private static final int BACKUP_SOURCE_INDEX = 1;
	private static final int BACKUP_DESTINATION_INDEX = 2;
	private static final int BACKUP_SCHEDULE_INDEX = 3;
	private static final int BACKUP_SUMMARY_INDEX = 4;
	private static final int FIRST_PAGE_INDEX = BACKUP_SERVER_INDEX;
	private static final int LAST_PAGE_INDEX = BACKUP_SUMMARY_INDEX + 1;
	
	private int currentPageIndex = BACKUP_SERVER_INDEX;
	private VerticalPanel leftPanel;
	private LayoutContainer rightPanel;	
	private DeckPanel pageContainer;
	
	private ToggleButton tbBackupServer;
	private ToggleButton tbBackupSource;
	private ToggleButton tbBackupDestination;
	private ToggleButton tbBackupSchedule;
	private ToggleButton tbBackupSummary;
	private ToggleButton tbBackupServerBt;
	private ToggleButton tbBackupSourceBt;
	private ToggleButton tbBackupDestinationBt;
	private ToggleButton tbBackupScheduleBt;
	private ToggleButton tbBackupSummaryBt;
	private ClickHandler backupServerHandler;
	private ClickHandler backupSourceHandler;
	private ClickHandler backupDestHandler;
	private ClickHandler backupScheduleHandler;
	private ClickHandler backupSummaryHandler;
	private Button previous;
	private Button next;
	private Button cancel;
	private Button help;
	private BackupServer backupServerPage;
	private BackupSource backupSourcePage;
	private BackupDestination backupDestPage;
	private ScheduleSettings schedulePage;
	private Summary summaryPage;
	//private boolean finished = false;

	private ServiceInfoModel d2dServer = null;
	private List<NodeModel> targetList = null;
	private String excludeVolumes = null;
	private String excludeFiles = null;
	private boolean isExclude;
	private BackupTemplateModel destInfo = null;
	private String jobName = null;
	private String jobUUID = null;
	private HomepageTab homePageTab = null;
	private boolean isModify = false;
	private D2DTimeModel d2dServerTime;
	
	public BackupModel backupModel = new BackupModel();
	
	public BackupWizardPanel(ServiceInfoModel backupServer,boolean isModify,String title){
		this.setSize(WIZARD_WIDTH, WIZARD_HIGHT);
		this.setHeading(title);
		this.isModify = isModify;
		d2dServer = backupServer;
		this.add(defineMainPanel(), new RowData(1,1));
		this.setResizable(false);
	}
	
	public BackupWizardPanel(ServiceInfoModel backupServer, List<NodeModel> sourceList,boolean isModify,String title)
	{
		this.setSize(WIZARD_WIDTH, WIZARD_HIGHT);
		this.setHeading(title);
		this.isModify = isModify;
		d2dServer = backupServer;
		targetList = sourceList;
		this.add(defineMainPanel(), new RowData(1,1));
		this.setResizable(false);
	}
	
	private LayoutContainer defineMainPanel(){
		this.setLayout( new FitLayout() );
				
		LayoutContainer mainPanel = new LayoutContainer();
		mainPanel.setLayout(new RowLayout( Orientation.HORIZONTAL ) );
		mainPanel.setStyleAttribute("background-color","#DFE8F6");
		
		defineToggleButtonPanel();
		defineContentPanel();
		
		mainPanel.add( leftPanel, new RowData(LEFT_PANEL_WIDTH, 1));
		mainPanel.add(rightPanel, new RowData(1,1));
		return mainPanel;
	}
	
	private void setToggleBtState(int index)
	{
		disableToggleButton();		
		switch ( index )
		{
		case BACKUP_SERVER_INDEX:
			tbBackupServerBt.setDown(true);
			tbBackupServer.setDown(true);
		break;
		case BACKUP_SOURCE_INDEX:
			tbBackupSourceBt.setDown(true);
			tbBackupSource.setDown(true);
		break;
		case BACKUP_DESTINATION_INDEX:
			tbBackupDestinationBt.setDown(true);
			tbBackupDestination.setDown(true);
		break;
		case BACKUP_SCHEDULE_INDEX:
			tbBackupScheduleBt.setDown(true);
			tbBackupSchedule.setDown(true);
		break;
		case BACKUP_SUMMARY_INDEX:
			tbBackupSummaryBt.setDown(true);
			tbBackupSummary.setDown(true);
		break;
		}
	}
	
	private void displayPage(int index)
	{
		
		if ( !showPage(index) )
		{
			setToggleBtState(currentPageIndex);
			return;
		}
		
		setToggleBtState(index);
		if(currentPageIndex-1 == BACKUP_DESTINATION_INDEX){
			schedulePage.changeScheduleType();
		}
	}
	private void disableToggleButton()
	{
		tbBackupSummaryBt.setDown(false);
		tbBackupSourceBt.setDown(false);
		tbBackupServerBt.setDown(false);
		tbBackupDestinationBt.setDown(false);
		tbBackupScheduleBt.setDown(false);
		
		tbBackupSummary.setDown(false);
		tbBackupSource.setDown(false);
		tbBackupServer.setDown(false);
		tbBackupDestination.setDown(false);
		tbBackupSchedule.setDown(false);
	}

	private void defineToggleButtonPanel() {
		leftPanel = new VerticalPanel();
		leftPanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		leftPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		leftPanel.setTableWidth("100%");
		
		backupServerHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				displayPage(BACKUP_SERVER_INDEX);
			}			
		};
		
		tbBackupServerBt = new ToggleButton(new Image(iconBundle.wizard_backup_server()));
		tbBackupServerBt.setStylePrimaryName("linux-ToggleButton");
		tbBackupServerBt.setDown(true);
		tbBackupServerBt.addClickHandler(backupServerHandler);
		leftPanel.add(tbBackupServerBt);
		
		tbBackupServer = new ToggleButton(UIContext.Constants.backupServer());
		tbBackupServer.setStylePrimaryName("tb-settings");
		tbBackupServer.setDown(true);
		tbBackupServer.addClickHandler(backupServerHandler);
		leftPanel.add(tbBackupServer);
		
		backupSourceHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				displayPage(BACKUP_SOURCE_INDEX);
			}			
		};
		
		tbBackupSourceBt = new ToggleButton(new Image(iconBundle.wizard_backup_source()));
		tbBackupSourceBt.setStylePrimaryName("linux-ToggleButton");
		tbBackupSourceBt.addClickHandler(backupSourceHandler);
		leftPanel.add(tbBackupSourceBt);
		
		tbBackupSource = new ToggleButton(UIContext.Constants.backupSource());
		tbBackupSource.setStylePrimaryName("tb-settings");
		tbBackupSource.addClickHandler(backupSourceHandler);
		leftPanel.add(tbBackupSource);
		
		backupDestHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				displayPage(BACKUP_DESTINATION_INDEX);
			}			
		};
		
		tbBackupDestinationBt = new ToggleButton(new Image(iconBundle.wizard_backup_destination()));
		tbBackupDestinationBt.setStylePrimaryName("linux-ToggleButton");
		tbBackupDestinationBt.addClickHandler(backupDestHandler);
		leftPanel.add(tbBackupDestinationBt);
		
		tbBackupDestination = new ToggleButton(UIContext.Constants.backupDestination());
		tbBackupDestination.setStylePrimaryName("tb-settings");
		tbBackupDestination.addClickHandler(backupDestHandler);
		leftPanel.add(tbBackupDestination);
		
		backupScheduleHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				displayPage(BACKUP_SCHEDULE_INDEX);
			}			
		};
		
		tbBackupScheduleBt = new ToggleButton(new Image(iconBundle.schedule()));
		tbBackupScheduleBt.setStylePrimaryName("linux-ToggleButton");
		tbBackupScheduleBt.addClickHandler(backupScheduleHandler);
		leftPanel.add(tbBackupScheduleBt);
		
		tbBackupSchedule = new ToggleButton(UIContext.Constants.advanced());
		tbBackupSchedule.setStylePrimaryName("tb-settings");
		tbBackupSchedule.addClickHandler(backupScheduleHandler);
		leftPanel.add(tbBackupSchedule);
		
		backupSummaryHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				displayPage(BACKUP_SUMMARY_INDEX);
			}			
		};
		
		tbBackupSummaryBt = new ToggleButton(new Image(iconBundle.summary()));
		tbBackupSummaryBt.setStylePrimaryName("linux-ToggleButton");
		tbBackupSummaryBt.addClickHandler(backupSummaryHandler);
		leftPanel.add(tbBackupSummaryBt);
		
		tbBackupSummary = new ToggleButton(UIContext.Constants.summary());
		tbBackupSummary.setStylePrimaryName("tb-settings");
		tbBackupSummary.addClickHandler(backupSummaryHandler);
		leftPanel.add(tbBackupSummary);	
	}
	
	private void definePagePanel()
	{
		pageContainer = new DeckPanel();
		
		backupServerPage = new BackupServer(this);
		LayoutContainer advancedContainer = new LayoutContainer();
		advancedContainer.setStyleAttribute("background-color","white");
		advancedContainer.setStyleAttribute("padding", "10px");
		advancedContainer.add(backupServerPage);
		pageContainer.add(advancedContainer);
		
		backupSourcePage = new BackupSource(this);
		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("background-color","white");
		container.setStyleAttribute("padding", "10px");
		container.add(backupSourcePage);
		pageContainer.add(container);
		
		backupDestPage = new BackupDestination(this);
		container = new LayoutContainer();
		container.add(backupDestPage);
		container.setStyleAttribute("background-color","white");
		container.setStyleAttribute("padding", "10px");
		pageContainer.add(container);
		
		schedulePage = new ScheduleSettings(this);
		container = new LayoutContainer();
		container.add(schedulePage);
		container.setStyleAttribute("background-color","white");
		container.setStyleAttribute("padding", "10px");
		pageContainer.add(container);
		
		summaryPage = new Summary(this);
		container = new LayoutContainer();
		container.add(summaryPage);
		container.setStyleAttribute("background-color","white");
		container.setStyleAttribute("padding", "10px");
		pageContainer.add(container);
		
		pageContainer.showWidget(currentPageIndex);
	}
	
	private void defineContentPanel()
	{
//		rightPanel = new LayoutContainer();
//		rightPanel.setStyleAttribute("background-color","white");
//		rightPanel.setLayout(new RowLayout(Orientation.VERTICAL));

		definePagePanel();
		
		ButtonBar buttonPanel = new ButtonBar();
		buttonPanel.setStyleAttribute("background-color","white");
		buttonPanel.setSpacing(5);
		//buttonPanel.setStyleAttribute("padding", "2px 3px 3px 0px");
		buttonPanel.setAlignment(HorizontalAlignment.RIGHT);
		buttonPanel.setMinButtonWidth(UIContext.BUTTON_MINWIDTH);
		
		previous = new Button(UIContext.Constants.previous());
		//previous.disable();
		previous.hide();
		previous.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				getPreviousPage();
			}});
		buttonPanel.add(previous);
		
		next = new Button(UIContext.Constants.next());
		next.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
//				if ( finished )
//				{
//					if ( getPageInfo(BACKUP_SUMMARY_INDEX) < 0 )
//					{
//						return;
//					}
//					captureServer();
//					BackupWizardPanel.this.hide();
//				}
//				else
				{
//					getNextPage();
					//changed by liuyu07
					showNextSettings();
				}
			}});
		
		buttonPanel.add(next);
		
		cancel = new Button(UIContext.Constants.cancel());
		cancel.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				MessageBox.confirm(UIContext.Constants.productName(), UIContext.Constants.closeBackupWizardConfirm(), new Listener<MessageBoxEvent>(){

					@Override
					public void handleEvent(MessageBoxEvent be) {
						if(be.getButtonClicked().getItemId().equals(Dialog.YES)){
							BackupWizardPanel.this.hide();
						}else{
							be.cancelBubble();
						}
					}
				});
				
				
			}});
		buttonPanel.add(cancel);

		help = new Button(UIContext.Constants.help());
		buttonPanel.add(help);
		help.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				switch(currentPageIndex){
				case BACKUP_SERVER_INDEX:
					Utils.showURL(UIContext.helpLink+HelpLinkItem.BACKUP_WIZARD_SERVER);
					break;
				case BACKUP_SOURCE_INDEX:
					Utils.showURL(UIContext.helpLink+HelpLinkItem.BACKUP_WIZARD_SOURCE);
					break;
				case BACKUP_DESTINATION_INDEX:
					Utils.showURL(UIContext.helpLink+HelpLinkItem.BACKUP_WIZARD_DESTINATION);
					break;
				case BACKUP_SCHEDULE_INDEX:
					Utils.showURL(UIContext.helpLink+HelpLinkItem.BACKUP_WIZARD_SCHEDULE);
					break;	
				case BACKUP_SUMMARY_INDEX:
					Utils.showURL(UIContext.helpLink+HelpLinkItem.BACKUP_WIZARD_SUMMARY);
					break;
				}
			}
			
		});

//		rightPanel.add(pageContainer, new RowData(1, RIGHT_PANEL_HIGHT));
//		rightPanel.add(buttonPanel, new RowData(1, 1));
		
		rightPanel = new LayoutContainer();
		rightPanel.setLayout(new BorderLayout());
		
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(0));
	    BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,30);  
	    southData.setMargins(new Margins(0));
		rightPanel.add(pageContainer, centerData);
		rightPanel.add(buttonPanel, southData);
	}
	
	private boolean showPage(int index)
	{
		if ( index < FIRST_PAGE_INDEX || index > LAST_PAGE_INDEX  || currentPageIndex == index )
		{
			return true;
		}
		if ( !getPageInfo(currentPageIndex) )
		{
			return false;
		}
		//click previous will not check the page information
		if ( currentPageIndex < index )
		{
			if ( !checkPageInfo(index) )
			{
				return false;
			}
		}
		
		if ( index == LAST_PAGE_INDEX )
		{
			captureServer();
			return true;
		}
		
		if( index == FIRST_PAGE_INDEX )
		{
			previous.hide();
		}
		else
		{
			previous.show();
		}

		if( index == BACKUP_SUMMARY_INDEX )
		{
			summaryPage.refresh();
			next.setText(UIContext.Constants.finish());
//		}else if(index == BACKUP_SCHEDULE_INDEX){
//			//schedulePage.refreshData();
		}
		else
		{
			next.setText(UIContext.Constants.next());
		}
		
		currentPageIndex = index;
		pageContainer.showWidget(currentPageIndex);
		return true;
	}

	private void getPreviousPage()
	{
		displayPage(currentPageIndex-1);
	}
	public void showNextSettings(){
		if(currentPageIndex==BACKUP_SCHEDULE_INDEX||currentPageIndex==BACKUP_SUMMARY_INDEX){
			schedulePage.Save(0);
			if(isModify||backupModel.getScheduleType() == BackupModel.SCHEDULE_TYPE_NOW || backupModel.getScheduleType() == BackupModel.SCHEDULE_TYPE_MANUALLY){
				getNextPage();
			}else{
				final D2DTimeModel time = schedulePage.getUserSetTime();
				if(time != null){
					mask(UIContext.Constants.validating());
					backupService.getCurrentD2DTimeFromServer(d2dServer, new BaseAsyncCallback<D2DTimeModel>(){
	
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							//getNextPage();
							unmask();
						}
	
						@Override
						public void onSuccess(D2DTimeModel result) {
							unmask();
							if(Utils.convertD2DTimeModel(time).getTime()<=Utils.convertD2DTimeModel(result).getTime()){
								Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.startTimeOutdatedMessage());
							}else{
								getNextPage();
							}
						}
						
					});
				}else{
					getNextPage();
				}
			}
		}else{
			getNextPage();
		}
	}
	private void getNextPage()
	{
		displayPage(currentPageIndex+1);
	}
	
	private boolean checkPageInfo(int index)
	{
		if (index <= BACKUP_SERVER_INDEX)
			return true;
		
		if ( BACKUP_SERVER_INDEX <= index && d2dServer == null )
		{
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.backupServerInputValidServer());
			return false;
		}
		
		if (index <= BACKUP_SOURCE_INDEX)
			return true;
		
		if ( targetList == null || targetList.size() == 0 )
		{
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.backupSourceNotEmpty());
			return false;
		}
		
		if (index <= BACKUP_DESTINATION_INDEX)
			return true;
		
		if(destInfo == null){
			destInfo = backupDestPage.getDestInfo();
		}
		
		if ( destInfo == null || destInfo.backupLocationInfoModel.getSessionLocation() == null || destInfo.backupLocationInfoModel.getSessionLocation().isEmpty() )
		{
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Messages.backupDestinationNotEmpty());
			return false;
		}
		
		if (!backupDestPage.checkSessionLocation())
		{
			//Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.backupLocationMessage());
			return false;
		}
		
		if (!backupDestPage.checkEncPass())
		{
			return false;
		}
		
		if (index <= BACKUP_SCHEDULE_INDEX)
			return true;
		
		if(!schedulePage.Validate()){
			return false;
		}
		
		if (index <= BACKUP_SUMMARY_INDEX)
			return true;
		
		if ( jobName == null||jobName.isEmpty() )
		{
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR, UIContext.Messages.jobNameEmpty());
			return false;
		}
		
		if(jobName.trim().isEmpty()){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR, UIContext.Constants.jobNameContainSpace());
			return false;
		}
		
		if ( jobName.length() > Summary.JOB_NAME_MAX_LENGTH ) {
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR, UIContext.Messages.exceedMaxJobNameLength(Summary.JOB_NAME_MAX_LENGTH));
			return false;
		}

		return true;
	}
	
	private boolean getPageInfo(int index)
	{   
		if ( index < FIRST_PAGE_INDEX || index > LAST_PAGE_INDEX )
		{
			return true;
		}
		if( index == BACKUP_SERVER_INDEX )
		{   
			targetList = backupSourcePage.getServerInfoList();	
			d2dServer = backupServerPage.GetBackupServer();
		}
		else if( index == BACKUP_SOURCE_INDEX )
		{
			targetList = backupSourcePage.getServerInfoList();
			excludeVolumes = backupSourcePage.getExcludeVolumns();
			excludeFiles = backupSourcePage.getExcludeFiles();
			isExclude=backupSourcePage.getvolumeSetting();
		}
		else if( index == BACKUP_DESTINATION_INDEX )
		{    
			destInfo = backupDestPage.getDestInfo();
		}
		else if( index == BACKUP_SCHEDULE_INDEX )
		{
			//TO_DO get timeZoneOffset from d2d server
			int timeZoneOffset=0;
			schedulePage.Save(timeZoneOffset);
		}
		else if( index == BACKUP_SUMMARY_INDEX )
		{
			jobName = summaryPage.getJobName();
		}
		
		return true;
	}
	
	private void captureServer()
	{
		backupModel.setJobName(jobName);
		backupModel.setExcludeVolumes(excludeVolumes);
		backupModel.setExclude(isExclude);
		backupModel.setExcludeFiles(excludeFiles);
		backupModel.setDestInfo(destInfo);
		backupModel.setUuid(jobUUID);
		backupModel.setTargetList(targetList);
		mask(UIContext.Constants.validating());
		backupService.submitBackupJob(d2dServer, backupModel, new BaseAsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable caught) {
				unmask();
				if( caught instanceof BusinessLogicException){
					BusinessLogicException e = (BusinessLogicException)caught;
					//Managed by other server
					if(e.getErrorCode().equals("12884901889")){
						displayPage(BACKUP_DESTINATION_INDEX);
						if(backupModel.getDestInfo().backupLocationInfoModel.getType()==BackupLocationInfoModel.TYPE_CIFS){
							backupDestPage.destination.showCrentialWindow(SessionLocationPanel.EVENT_TYPE_VALIDATE);
						}else{
							super.onFailure(caught);
						}
					}else{
						super.onFailure(caught);
					}
				}else{
					super.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(Integer result) {
				if(homePageTab.presentTabIndex.equals(HomepageTab.JOB_STATUS)){
					homePageTab.refreshJobStatusTable(true);
				}else{
					homePageTab.showTabItem(HomepageTab.JOB_STATUS);
				}
				
				BackupWizardPanel.this.hide();
				if(result==1){
					Utils.showMessage(UIContext.Constants.productName(),MessageBox.WARNING,UIContext.Constants.notDeleteRunningNodes());
				}
			}});
	}
	
	public List<NodeModel> getBackupSourceInfo()
	{
		return targetList;
	}
	
	public String getExcludeVolumns()
	{
		return excludeVolumes;
	}
	
	public String getExcludeFiles(){
		return excludeFiles;
	}
	
	public BackupTemplateModel getBackupDestinationInfo()
	{
		return destInfo;
	}
	
	public String getJobName()
	{
		return jobName;
	}

	public void refreshData(final ServiceInfoModel server, final String jobUUID) 
	{
		if ( Utils.isEmptyOrNull(jobUUID) )
			return;
		
		this.isModify = true;
		this.mask(UIContext.Constants.loading());
		backupService.getBackupJobScriptByUUID(server, jobUUID, new BaseAsyncCallback<BackupModel>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				BackupWizardPanel.this.hide();
			}

			@Override
			public void onSuccess(BackupModel result) {
				if ( result == null ) {
					Utils.showMessage(UIContext.Constants.productName(), MessageBox.ERROR, UIContext.Messages.getBackupJobScriptFailed());
					BackupWizardPanel.this.hide();
					return;
				}
				setServerTime(server);
//				BackupWizardPanel.this.backupSourcePage.disableButtonBar();
				refreshEachWidow(server, result);
				BackupWizardPanel.this.unmask();
			}});
	}
	
	private void refreshEachWidow(ServiceInfoModel backupServer, BackupModel model)
	{
		if ( model == null )
			return;
		this.backupModel=model;
		//backup server
		this.d2dServer = backupServer;
		this.backupServerPage.refresh();
		
		//backup source
//		NodeModel serverInfo = new NodeModel();
//		serverInfo.setServerName(model.getNodeName());
//		serverInfo.setUserName(model.getNodeUser());
//		serverInfo.setPasswd(model.getNodePassword());
//
//		if ( this.targetList == null )
//		{
//			this.targetList = new ArrayList<NodeModel>();
//		}
//		this.targetList.add(serverInfo);
		this.targetList=model.getTargetList();
		this.excludeVolumes = model.getExcludeVolumes();
		this.excludeFiles = model.getExcludeFiles();
		this.isExclude=model.isExclude();
		this.backupSourcePage.refresh();
		
		//backup destination
//		BackupTemplateModel template = new BackupTemplateModel();
//		template.setSessionLocation(model.getBackupDestination());
//		template.setCompression(model.getCompression());
//		template.setEncryptionName(model.getEncryptionName());
//		template.setEncryptionPassword(model.getEncryptionPassword());
//		this.destInfo = template;
		this.destInfo=model.getDestInfo();
		this.backupDestPage.refresh();
		
		this.schedulePage.refreshData();
		
		//backup summary
		this.jobUUID = model.getUuid();
		this.jobName = model.getJobName();
		this.summaryPage.refresh();
		this.summaryPage.setJobName(model.getJobName());

	}
	
	public void setHomepageTab(HomepageTab tab)
	{
		homePageTab = tab;
	}
	
	public void refreshNodeTable()
	{
		if (homePageTab != null)
		{
			homePageTab.refreshNodeTable();
		}
	}

	public void refresh(ServiceInfoModel server){
		this.d2dServer = server;
		if(!this.isModify)
			setServerTime(server);
		refreshPrePostScript(server);
		refreshLocation(server);
	}
	
	public void setServerTime(ServiceInfoModel server) {
		backupService.getCurrentD2DTimeFromServer(server, new BaseAsyncCallback<D2DTimeModel>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(D2DTimeModel result) {
				if(result!=null){
					Date time=Utils.convertD2DTimeModel(result);
					d2dServerTime = result;
					schedulePage.setStartDateTime(time);
					if(!isModify){
						String curTimeStr = Utils.formatD2DTime(result);
						summaryPage.setJobName(UIContext.Constants.backupDefaultJobName() + " - " + curTimeStr);
					}
						
				}
			}
			
		});
	}
	
	public void refreshPrePostScript(ServiceInfoModel server){
		backupService.getScripts(server, Utils.SCRIPT_TYPE_PREPOST,new BaseAsyncCallback<List<String>>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				schedulePage.refreshPrePostScripts(null);
			}

			@Override
			public void onSuccess(List<String> result) {
				schedulePage.refreshPrePostScripts(result);
			}
			
		});
	}

	public void refreshLocation(ServiceInfoModel server){
		storageService.getAllBackupLocation(server, new BaseAsyncCallback<List<BackupLocationInfoModel>>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				backupDestPage.refreshLocation(null);
			}

			@Override
			public void onSuccess(List<BackupLocationInfoModel> result) {
				backupDestPage.refreshLocation(result);
			}
			
		});
	}
	
	public boolean isModify() {
		return isModify;
	}

	public ServiceInfoModel getD2DServerInfo() {
		return d2dServer;
	}

	public D2DTimeModel getD2dServerTime() {
		return d2dServerTime;
	}

	public boolean isExclude() {
		return isExclude;
	}

	public void setExclude(boolean isExclude) {
		this.isExclude = isExclude;
	}
		
}
