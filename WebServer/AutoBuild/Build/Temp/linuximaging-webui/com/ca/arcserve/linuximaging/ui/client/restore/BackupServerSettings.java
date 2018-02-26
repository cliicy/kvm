package com.ca.arcserve.linuximaging.ui.client.restore;


import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.BaseComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.ServerCapabilityModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;


public class BackupServerSettings extends LayoutContainer {

	private FieldSet notificationSet;
	private FieldSet liveCDSet;
	private BaseComboBox<ServiceInfoModel> d2dServers;
	private ListStore<ServiceInfoModel> serversStore = null;
	protected RestoreWindow restoreWindow;
	private FieldSet serverCapabilitySet;
	private LabelField restoreUtilNote;
	//private RadioButton bmrRadio;
//	private Radio volumeRadio;
	//private RadioButton fileRadio;
	public final static int ROW_CELL_SPACE=5;
	public final static int MAX_FIELD_WIDTH= 300;
	
	private ServerCapabilityModel serverCapability;	
	private static final HomepageServiceAsync service = GWT.create(HomepageService.class);

	public BackupServerSettings(RestoreWindow window){
		this.restoreWindow=window;
		
		TableLayout layout = new TableLayout();
		layout.setWidth("97%");
		layout.setColumns(2);
		layout.setCellPadding(5);
		layout.setCellSpacing(5);
		setLayout(layout);
		
		LabelField head =new LabelField(UIContext.Constants.selectBackupServer());
		head.setStyleAttribute("font-weight", "bold");
		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT); 
		add(head,tdColspan);
		
		LabelField lblBackupServer =new LabelField(UIContext.Constants.backupServerLabel());
		serversStore = new ListStore<ServiceInfoModel>();
		d2dServers = new BaseComboBox<ServiceInfoModel>();
		d2dServers.setStore(serversStore);
		d2dServers.setDisplayField("server");
		d2dServers.setEditable(false);
//		Utils.addToolTip(cmbBackupServer, UIContext.Constants.Tooltip());
		d2dServers.setWidth(MAX_FIELD_WIDTH);
		d2dServers.setEnabled(false);
		d2dServers.addSelectionChangedListener(new SelectionChangedListener<ServiceInfoModel>(){

			@Override
			public void selectionChanged(SelectionChangedEvent<ServiceInfoModel> se) {
				if(!restoreWindow.isModify){
					ServiceInfoModel d2dServer=se.getSelectedItem();
					restoreWindow.refresh(d2dServer);
				}
			}
			
		});
		initCmbBackupServer();
		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("80%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		add(lblBackupServer,tdLabel);
		add(d2dServers,tdField);
		
		//defineRestoreTypeSet();
		//add(restoreTypeSet,tdColspan);
		defineNotificationSet();
		add(notificationSet,tdColspan);
		
		if(!UIContext.versionInfo.isLiveCD()&&restoreWindow.getRestoreType() == RestoreType.BMR){
			defineLiveCDSet();
			add(liveCDSet,tdColspan);
		}
		defineServerCapability();
		add(serverCapabilitySet, tdColspan);
		defineSpecialSettings();
		
	}

	private void initCmbBackupServer() {
		serversStore.add(restoreWindow.toolBar.tabPanel.currentServer);
		d2dServers.setValue(restoreWindow.toolBar.tabPanel.currentServer);
		
		/*List<ServiceInfoModel> list=restoreWindow.currentServer.toolBar.nodeTree.getBackupServerList();
		if(list==null||list.size()==0){
			return;
		}
		for(ServiceInfoModel model:list){
			serversStore.add(model);
		}
		ServiceInfoModel model=restoreWindow.toolBar.nodeTree.getSelectedItem();
		if(ServiceInfoModel.ROOT.equals(model.getType())){
			d2dServers.setValue(list.get(0));
		}else{
			d2dServers.setValue(restoreWindow.toolBar.nodeTree.getSelectedItem());
		}*/
	}

	protected void defineSpecialSettings(){
		
	}
	
	private void defineNotificationSet(){
		notificationSet = new FieldSet();
		notificationSet.setHeading(UIContext.Constants.information());
		notificationSet.setCollapsible(true);
		notificationSet.setAutoHeight(true);
		notificationSet.setStyleAttribute("margin-top", "10px");
		TableLayout warningLayout = new TableLayout();
		warningLayout.setWidth("80%");
		warningLayout.setCellSpacing(1);
		warningLayout.setColumns(2);
		notificationSet.setLayout(warningLayout);
		
		Image warningImage = new Image(UIContext.IconBundle.information());
		TableData tableData = new TableData();
		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		notificationSet.add(warningImage, tableData);
		LabelField note=new LabelField(UIContext.Constants.selectBackupServerNotification());
		note.setWidth(550);
		notificationSet.add(note);
	}
	
	private void defineLiveCDSet(){
		liveCDSet = new FieldSet();
		liveCDSet.setHeading(UIContext.Constants.information());
		liveCDSet.setCollapsible(true);
		liveCDSet.setAutoHeight(true);
		liveCDSet.setStyleAttribute("margin-top", "10px");
		TableLayout warningLayout = new TableLayout();
		warningLayout.setWidth("80%");
		warningLayout.setCellSpacing(1);
		warningLayout.setColumns(2);
		liveCDSet.setLayout(warningLayout);
		
		if(UIContext.versionInfo.isLiveCDIsoExist()){
			Image warningImage = new Image(UIContext.IconBundle.information());
			TableData tableData = new TableData();
			tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
			tableData.setVerticalAlign(VerticalAlignment.TOP);
			liveCDSet.add(warningImage, tableData);
			/*LabelField note=new LabelField(UIContext.Constants.clickHereToDownloadLiveCD());
			note.addStyleName("click-download");
			note.addListener(Events.OnClick, new Listener<FieldEvent>(){
	
				@Override
				public void handleEvent(FieldEvent be) {
					String link = Window.Location.getProtocol() +"//"+ Window.Location.getHost() + "/" + "LinuxD2D-LiveCD.iso";
					Utils.showURL(link);
				}
				
			});
			note.setWidth(550);*/
			String link = Window.Location.getProtocol() +"//"+ Window.Location.getHost() + "/" + "UDP_Agent_Linux-LiveCD.iso";
			HTML html = new HTML("<a href=\""+link+"\" target=\"_blank\"><span class=\"click-download\">"+UIContext.Constants.clickHereToDownloadLiveCD()+"</span></a>");
			//html.setStyleName("click-download");
			html.setWidth("550px");
			liveCDSet.add(html);
		}else{
			Image warningImage = new Image(UIContext.IconBundle.information());
			TableData tableData = new TableData();
			tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
			tableData.setVerticalAlign(VerticalAlignment.TOP);
			liveCDSet.add(warningImage, tableData);
			LabelField note=new LabelField(UIContext.Constants.noLiveCDWarning());
			note.setWidth(550);
			liveCDSet.add(note);
		}
	}
	
	private void defineServerCapability() {
		serverCapabilitySet = new FieldSet();
		serverCapabilitySet.setHeading(UIContext.Constants.serverCapability());
		serverCapabilitySet.setCollapsible(true);
		serverCapabilitySet.setAutoHeight(true);
		serverCapabilitySet.setStyleAttribute("margin-top", "10px");
		TableLayout warningLayout = new TableLayout();
		warningLayout.setWidth("80%");
		warningLayout.setCellSpacing(1);
		warningLayout.setColumns(2);
		serverCapabilitySet.setLayout(warningLayout);
		
		Image warningImage = null;		
		restoreUtilNote = null;
		if ( restoreWindow.getRestoreType() == RestoreType.BMR || restoreWindow.getRestoreType() == RestoreType.MIGRATION_BMR) {
			warningImage = new Image(UIContext.IconBundle.warning());
			restoreUtilNote=new LabelField(UIContext.Constants.noRestoreUtilWarnningBMR());
		} else if ( restoreWindow.getRestoreType() == RestoreType.FILE || restoreWindow.getRestoreType() == RestoreType.SHARE_RECOVERY_POINT) {
			warningImage = new Image(UIContext.IconBundle.error());
			restoreUtilNote=new LabelField(UIContext.Constants.noRestoreUtilErrorFile());
		}
		
		if ( warningImage != null ) {
			TableData tableData = new TableData();
			tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
			tableData.setVerticalAlign(VerticalAlignment.TOP);
			serverCapabilitySet.add(warningImage, tableData);
		}
		if ( restoreUtilNote != null ) {
			restoreUtilNote.setWidth(550);
			serverCapabilitySet.add(restoreUtilNote);
		}
		serverCapabilitySet.hide();
	}
	
	protected void afterRender() {
		super.afterRender();
		this.mask(UIContext.Constants.loading());
		service.getServerCapability(restoreWindow.toolBar.tabPanel.currentServer, new BaseAsyncCallback<ServerCapabilityModel>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				BackupServerSettings.this.unmask();
			}

			@Override
			public void onSuccess(ServerCapabilityModel result) {
				if ( result != null ) {
					serverCapability = result;
					restoreWindow.setAddedPublicKey(result.getPublicKey());
					BackupServerSettings.this.unmask();
					
					if ( !result.getRestoreUtilInstalled() ) {
						if ( restoreWindow.getRestoreType() == RestoreType.BMR || restoreWindow.getRestoreType() == RestoreType.MIGRATION_BMR) {
							restoreUtilNote.setText(UIContext.Constants.noRestoreUtilWarnningBMR());
						} else if ( restoreWindow.getRestoreType() == RestoreType.FILE || restoreWindow.getRestoreType() == RestoreType.SHARE_RECOVERY_POINT ) {
							restoreUtilNote.setText(UIContext.Constants.noRestoreUtilErrorFile());
						}
					    serverCapabilitySet.show();
						return;
					} else if ( !result.getRestoreUtilRunning() ) {
						if ( restoreWindow.getRestoreType() == RestoreType.BMR || restoreWindow.getRestoreType() == RestoreType.MIGRATION_BMR) {
							restoreUtilNote.setText(UIContext.Constants.noRestoreUtilRunningWarnningBMR());
							serverCapabilitySet.show();
							return;
						}
					}
				}
				
				serverCapabilitySet.hide();
			}
		});
	}
	
	public void validateAsync(final BaseAsyncCallback<Boolean> callBack){
		callBack.onSuccess(true);
	}
	
	public boolean validate(){
		if(d2dServers.getValue()==null||d2dServers.getValue()==null){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,"please select a valid D2D server first!");
			return false;
		}
		
		if ( restoreWindow.getRestoreType() == RestoreType.FILE && serverCapability != null && !serverCapability.getRestoreUtilInstalled() )
		{
			Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Messages.noRestoreUtilError(restoreWindow.currentServer.getServer()));
			return false;
		}
		return true;
	}
	public void save(){
		restoreWindow.currentServer=d2dServers.getValue();
		//restoreWindow.setTargetMachineSettings();
	}
	public ServiceInfoModel getD2DServer() {
		return d2dServers.getValue();
	}

	public void refreshData() {
		d2dServers.setValue(restoreWindow.currentServer);
		//RestoreType type=restoreWindow.restoreModel.getRestoreType();
		/*if(type.getValue()==RestoreType.BMR.getValue()){
			bmrRadio.setValue(true);
		}else if(type.getValue()==RestoreType.FILE.getValue()){
			fileRadio.setValue(true);
		}*/
		if(restoreWindow.isModify){
			d2dServers.disable();
//			restoreTypeSet.disable();
			//bmrRadio.setEnabled(false);
			//fileRadio.setEnabled(false);
		}
	}

	public ServerCapabilityModel getServerCapability() {
		return serverCapability;
	}
}
