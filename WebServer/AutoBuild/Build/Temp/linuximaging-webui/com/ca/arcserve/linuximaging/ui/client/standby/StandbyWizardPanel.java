package com.ca.arcserve.linuximaging.ui.client.standby;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.common.icon.FlashImageBundle;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.BackupModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.PhysicalStandbyModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.StandbyModel;
import com.ca.arcserve.linuximaging.ui.client.model.StandbyType;
import com.ca.arcserve.linuximaging.ui.client.model.VirtualStandbyModel;
import com.ca.arcserve.linuximaging.ui.client.standby.physical.PhysicalStandbyMachines;
import com.ca.arcserve.linuximaging.ui.client.standby.virtual.VirtualStandbyMachines;
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

public class StandbyWizardPanel extends Window{
	public static int WIZARD_WIDTH=840;
	public static int WIZARD_HIGHT=550;
	public static int LEFT_PANEL_WIDTH=140;
	public static int RIGHT_PANEL_WIDTH=670;
	public static int RIGHT_PANEL_HIGHT=480;
	private static final HomepageServiceAsync backupService = GWT.create(HomepageService.class);
	private static final FlashImageBundle iconBundle = GWT.create(FlashImageBundle.class);
	private static final int BACKUP_SERVER_INDEX = 0;
	private static final int SOURCE_NODE_INDEX = 1;
	private static final int STANDBY_MACHINES_INDEX = 2;
	private static final int ADVANCED_SETTINGS_INDEX = 3;
	private static final int SUMMARY_INDEX = 4;
	private static final int FIRST_PAGE_INDEX = BACKUP_SERVER_INDEX;
	private static final int LAST_PAGE_INDEX = SUMMARY_INDEX + 1;
	
	private int currentPageIndex = BACKUP_SERVER_INDEX;
	private VerticalPanel leftPanel;
	private LayoutContainer rightPanel;	
	private DeckPanel pageContainer;
	
	private ToggleButton tbBackupServer;
	private ToggleButton tbSourceNode;
	private ToggleButton tbStandByMachines;
	private ToggleButton tbAdvancedSettings;
	private ToggleButton tbSummary;
	private ToggleButton tbBackupServerBt;
	private ToggleButton tbSourceNodeBt;
	private ToggleButton tbStandByMachinesBt;
	private ToggleButton tbAdvancedSettingsBt;
	private ToggleButton tbSummaryBt;
	private ClickHandler backupServerHandler;
	private ClickHandler sourceNodeHandler;
	private ClickHandler standByMachinesHandler;
	private ClickHandler advancedSettingsHandler;
	private ClickHandler summaryHandler;
	private Button previous;
	private Button next;
	private Button cancel;
	private Button help;
	private BackupServer backupServerPage;
	private SourceNode sourceNodePage;
	private StandbyMachines standbyMachinePage;
	private AdvancedSettings advancedPage;
	private Summary summaryPage;
	//private boolean finished = false;

	private ServiceInfoModel d2dServer = null;
	private List<NodeModel> targetList = null;
	private String jobName = null;
	private HomepageTab homePageTab = null;
	private boolean isModify = false;
	private StandbyType standbyType;
	
	public StandbyModel standbyModel;
	
	public StandbyWizardPanel(ServiceInfoModel backupServer, List<NodeModel> sourceList,boolean isModify,String title,StandbyType standbyType)
	{
		this.setSize(WIZARD_WIDTH, WIZARD_HIGHT);
		this.setHeading(title);
		this.isModify = isModify;
		this.standbyType = standbyType;
		if(standbyType == StandbyType.PHYSICAL){
			standbyModel = new PhysicalStandbyModel();
		}else{
			standbyModel = new VirtualStandbyModel();
		}
		standbyModel.setStandbyType(standbyType);
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
		case SOURCE_NODE_INDEX:
			tbSourceNodeBt.setDown(true);
			tbSourceNode.setDown(true);
		break;
		case STANDBY_MACHINES_INDEX:
			tbStandByMachinesBt.setDown(true);
			tbStandByMachines.setDown(true);
		break;
		case ADVANCED_SETTINGS_INDEX:
			tbAdvancedSettingsBt.setDown(true);
			tbAdvancedSettings.setDown(true);
		break;
		case SUMMARY_INDEX:
			tbSummaryBt.setDown(true);
			tbSummary.setDown(true);
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
	}
	private void disableToggleButton()
	{
		tbSummaryBt.setDown(false);
		tbSourceNodeBt.setDown(false);
		tbBackupServerBt.setDown(false);
		tbStandByMachinesBt.setDown(false);
		tbAdvancedSettingsBt.setDown(false);
		
		
		tbSummary.setDown(false);
		tbSourceNode.setDown(false);
		tbBackupServer.setDown(false);
		tbStandByMachines.setDown(false);
		tbAdvancedSettings.setDown(false);
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
		
		sourceNodeHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				displayPage(SOURCE_NODE_INDEX);
			}			
		};
		
		tbSourceNodeBt = new ToggleButton(new Image(iconBundle.wizard_backup_source()));
		tbSourceNodeBt.setStylePrimaryName("linux-ToggleButton");
		tbSourceNodeBt.addClickHandler(sourceNodeHandler);
		leftPanel.add(tbSourceNodeBt);
		
		tbSourceNode = new ToggleButton(UIContext.Constants.standbySourceNodes());
		tbSourceNode.setStylePrimaryName("tb-settings");
		tbSourceNode.addClickHandler(sourceNodeHandler);
		leftPanel.add(tbSourceNode);
		
		standByMachinesHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				displayPage(STANDBY_MACHINES_INDEX);
			}			
		};
		
		tbStandByMachinesBt = new ToggleButton(new Image(iconBundle.wizard_backup_destination()));
		tbStandByMachinesBt.setStylePrimaryName("linux-ToggleButton");
		tbStandByMachinesBt.addClickHandler(standByMachinesHandler);
		leftPanel.add(tbStandByMachinesBt);
		
		tbStandByMachines = new ToggleButton(UIContext.Constants.standbyStandbyMachine());
		tbStandByMachines.setStylePrimaryName("tb-settings");
		tbStandByMachines.addClickHandler(standByMachinesHandler);
		leftPanel.add(tbStandByMachines);
		
		advancedSettingsHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				displayPage(ADVANCED_SETTINGS_INDEX);
			}			
		};
		
		tbAdvancedSettingsBt = new ToggleButton(new Image(iconBundle.schedule()));
		tbAdvancedSettingsBt.setStylePrimaryName("linux-ToggleButton");
		tbAdvancedSettingsBt.addClickHandler(advancedSettingsHandler);
		leftPanel.add(tbAdvancedSettingsBt);
		
		tbAdvancedSettings = new ToggleButton(UIContext.Constants.advanced());
		tbAdvancedSettings.setStylePrimaryName("tb-settings");
		tbAdvancedSettings.addClickHandler(advancedSettingsHandler);
		leftPanel.add(tbAdvancedSettings);
		
		summaryHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				displayPage(SUMMARY_INDEX);
			}			
		};
		
		tbSummaryBt = new ToggleButton(new Image(iconBundle.summary()));
		tbSummaryBt.setStylePrimaryName("linux-ToggleButton");
		tbSummaryBt.addClickHandler(summaryHandler);
		leftPanel.add(tbSummaryBt);
		
		tbSummary = new ToggleButton(UIContext.Constants.summary());
		tbSummary.setStylePrimaryName("tb-settings");
		tbSummary.addClickHandler(summaryHandler);
		leftPanel.add(tbSummary);	
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
		
		sourceNodePage = new SourceNode(this,targetList);
		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("background-color","white");
		container.setStyleAttribute("padding", "10px");
		container.add(sourceNodePage);
		pageContainer.add(container);
		
		standbyMachinePage = getStandbyMachines();
		container = new LayoutContainer();
		container.add(standbyMachinePage);
		container.setStyleAttribute("background-color","white");
		container.setStyleAttribute("padding", "10px");
		pageContainer.add(container);
		
		advancedPage = new AdvancedSettings(this);
		container = new LayoutContainer();
		container.add(advancedPage);
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
	
	private StandbyMachines getStandbyMachines(){
		if(standbyType == StandbyType.PHYSICAL){
			return new PhysicalStandbyMachines(this);
		}else{
			return new VirtualStandbyMachines(this);
		}
	}
	
	private void defineContentPanel()
	{
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

					showNextSettings();
				}
			});
		
		buttonPanel.add(next);
		
		cancel = new Button(UIContext.Constants.cancel());
		cancel.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				MessageBox.confirm(UIContext.Constants.productName(), UIContext.Constants.closeStandbyWizardConfirm(), new Listener<MessageBoxEvent>(){

					@Override
					public void handleEvent(MessageBoxEvent be) {
						if(be.getButtonClicked().getItemId().equals(Dialog.YES)){
							StandbyWizardPanel.this.hide();
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
					Utils.showURL(UIContext.helpLink+HelpLinkItem.STANDBY_WIZARD_SERVER);
					break;
				case SOURCE_NODE_INDEX:
					Utils.showURL(UIContext.helpLink+HelpLinkItem.STANDBY_WIZARD_SOURCE);
					break;
				case STANDBY_MACHINES_INDEX:
					Utils.showURL(UIContext.helpLink+HelpLinkItem.STANDBY_WIZARD_STANDBY_MACHINE);
					break;
				case ADVANCED_SETTINGS_INDEX:
					Utils.showURL(UIContext.helpLink+HelpLinkItem.STANDBY_WIZARD_ADVANCED);
					break;	
				case SUMMARY_INDEX:
					Utils.showURL(UIContext.helpLink+HelpLinkItem.STANDBY_WIZARD_SUMMARY);
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

		if( index == SUMMARY_INDEX )
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
		getNextPage();
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
		
		if (index <= SOURCE_NODE_INDEX)
			return true;
		
		if (index <= STANDBY_MACHINES_INDEX){
			
			return true;
		}
		
		if (index <= ADVANCED_SETTINGS_INDEX){
			if(!sourceNodePage.validate()){
				showPage(SOURCE_NODE_INDEX);
				return false;
			}
			if(!standbyMachinePage.validate()){
				showPage(STANDBY_MACHINES_INDEX);
				return false;
			}
			return true;
		}
		
		if (index <= SUMMARY_INDEX){
			if(!sourceNodePage.validate()){
				showPage(SOURCE_NODE_INDEX);
				return false;
			}
			if(!standbyMachinePage.validate()){
				showPage(STANDBY_MACHINES_INDEX);
				return false;
			}
			if(!advancedPage.validate()){
				showPage(ADVANCED_SETTINGS_INDEX);
				return false;
			}
			return true;
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
			d2dServer = backupServerPage.GetBackupServer();
		}
		else if( index == SOURCE_NODE_INDEX )
		{
			sourceNodePage.save();
		}
		else if( index == STANDBY_MACHINES_INDEX )
		{
			standbyMachinePage.save();
		}
		else if( index == ADVANCED_SETTINGS_INDEX )
		{
			advancedPage.save();
		}
		else if( index == SUMMARY_INDEX )
		{
			jobName = summaryPage.getJobName();
		}
		
		return true;
	}
	
	private void captureServer()
	{
		
		mask(UIContext.Constants.validating());
		standbyModel.setJobName(jobName);
		
		backupService.submitStandbyJob(d2dServer, standbyModel, new BaseAsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable caught) {
				unmask();
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(Integer result) {
				if(homePageTab.presentTabIndex.equals(HomepageTab.JOB_STATUS)){
					homePageTab.refreshJobStatusTable(true);
				}else{
					homePageTab.showTabItem(HomepageTab.JOB_STATUS);
				}
				
				StandbyWizardPanel.this.hide();
				if(result==1){
					Utils.showMessage(UIContext.Constants.productName(),MessageBox.WARNING,UIContext.Constants.notDeleteRunningNodes());
			}
		}});
		
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
		backupService.getStandbyModel(server, jobUUID,standbyType, new BaseAsyncCallback<StandbyModel>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				StandbyWizardPanel.this.hide();
			}

			@Override
			public void onSuccess(StandbyModel result) {
				if ( result == null ) {
					Utils.showMessage(UIContext.Constants.productName(), MessageBox.ERROR, UIContext.Messages.getBackupJobScriptFailed());
					StandbyWizardPanel.this.hide();
					return;
				}
				refreshEachWidow(server, result);
				StandbyWizardPanel.this.unmask();
			}});
	}
	
	private void refreshEachWidow(ServiceInfoModel backupServer, StandbyModel model)
	{
		if ( model == null )
			return;
		this.standbyModel=model;
		//backup server
		this.d2dServer = backupServer;
		this.backupServerPage.refresh();
		this.sourceNodePage.refresh();
		this.standbyMachinePage.refresh();
		
		this.advancedPage.refresh();
		
		//backup summary
		this.summaryPage.refresh();
		this.summaryPage.setJobName(null);

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
		refreshPrePostScript(server);
	}
	
	
	
	public void refreshPrePostScript(ServiceInfoModel server){
		backupService.getScripts(server, Utils.SCRIPT_TYPE_PREPOST,new BaseAsyncCallback<List<String>>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				advancedPage.refreshPrePostScripts(null);
			}

			@Override
			public void onSuccess(List<String> result) {
				advancedPage.refreshPrePostScripts(result);
			}
			
		});
	}

	public StandbyType getStandbyType(){
		return this.standbyType;
	}
	
	public boolean isModify() {
		return isModify;
	}

	public ServiceInfoModel getD2DServerInfo() {
		return d2dServer;
	}
	
}
