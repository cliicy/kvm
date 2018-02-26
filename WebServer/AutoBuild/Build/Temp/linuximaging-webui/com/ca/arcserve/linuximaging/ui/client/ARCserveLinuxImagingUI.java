package com.ca.arcserve.linuximaging.ui.client;

import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepagePanel;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.login.LoginPanel;
import com.ca.arcserve.linuximaging.ui.client.login.LoginService;
import com.ca.arcserve.linuximaging.ui.client.login.LoginServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.login.LoginWindow;
import com.ca.arcserve.linuximaging.ui.client.model.UIContextModel;
import com.ca.arcserve.linuximaging.ui.client.model.VersionInfoModel;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ARCserveLinuxImagingUI implements EntryPoint {
	final HomepageServiceAsync service = GWT.create(HomepageService.class);
	final LoginServiceAsync loginService = GWT.create(LoginService.class);
	private Timer detectInactiveTimer;
	private HomepagePanel mainPanel;
	private LoginPanel loginPanel;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		service.getVersionInfo(null, new BaseAsyncCallback<VersionInfoModel>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				getLogonUser();
			}

			@Override
			public void onSuccess(VersionInfoModel result) {
				if(result == null){
					getLogonUser();
				}else{
					UIContext.versionInfo = result;
					UIContext.selectedServerVersionInfo = result;
					String location = Location.getParameter(Utils.LOCATION);
					if(location!=null && Utils.LOCATION_RESTORE.equals(location)){
						UIContext.isRestoreMode = true;
					}
					if(result.getManagedServer() != null){
						UIContext.isRestoreMode = true;
						/*final ServerInfoModel model = result.getManagedServer();
						MessageBox mb = new MessageBox();
						mb.setTitle(UIContext.productName);
						mb.setMessage(UIContext.Messages.serverIsManagedByOther(model.getServerName(), UIContext.productName));
						mb.setIcon(MessageBox.WARNING);
						mb.setButtons(MessageBox.OK);
						mb.setMinWidth(350);
						mb.addCallback(new Listener<MessageBoxEvent>(){
	
							@Override
							public void handleEvent(MessageBoxEvent be) {
								if(be.getButtonClicked().getItemId().equals(Dialog.OK)){
									String serverURL = model.getProtocol()+ "://" + model.getServerName()+":"+model.getPort();
									Window.Location.replace(serverURL);
								}
							}
						});
						mb.setModal(true);
						mb.show();*/
						getLogonUser();
					}else if(result.isLiveCD()){
						loginService.validateUser(null, "root", "cad2d", new BaseAsyncCallback<String>(){
							@Override
							public void onFailure(Throwable caught) {
								getLogonUser();
							}
							
							@Override
							public void onSuccess(String result) {
								if(result != null){
									addHomepage();
								}else{
									getLogonUser();
								}
							}
						});
					}else{
						getLogonUser();
					}
				}
			}
			
		});
		
	}
	
	private void getLogonUser(){
		loginService.getLogonUser(new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				addLoginPanel();
			}

			@Override
			public void onSuccess(String result) {
				if(result == null || result.equals("")){
					addLoginPanel();
				}
				else{
					addHomepage();
				}
			}
			
		});
	}
	
	private void addLoginPanel(){
		Window.setTitle(UIContext.productName);
		/*final Viewport topPanel = new Viewport();
		topPanel.ensureDebugId("9b27f647-d039-465f-8a8c-c814dfc8df42");
		topPanel.setLayout(new CenterLayout());
		topPanel.addStyleName("loginBackgroundColor");*/
		loginPanel = new LoginPanel();	
		RootPanel.get().addStyleName("login-background-color");
		RootPanel.get().add(loginPanel);
		
		LoginWindow loginWindow = loginPanel.getLoginWindow();
		loginWindow.addListener(Events.Hide, new Listener<BaseEvent>()
		{

			@Override
			public void handleEvent(BaseEvent be) {												
				RootPanel.get().remove(loginPanel);	
				addHomepage();
			}
			
		});
		
		//topPanel.add(loginPanel);
		//Window.setTitle(UIContext.productName);
		//mainPanel = new HomepagePanel();
		//RootPanel.get().setLayoutData(new FillLayout(Orientation.HORIZONTAL));
		//RootPanel.get().add(topPanel);
	}
	
	private void addHomepage(){
		service.initUIContext(new AsyncCallback<UIContextModel>(){

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(UIContextModel result) {
//				UIContext.productName=result.getProductName();
				UIContext.FILE_SEPATATOR=result.getFileSeparator();
				/*if(result.getVersion()!=null){
					UIContext.version=result.getVersion();
				}*/
				if(result.getHelpLink()!=null){
					UIContext.helpLink=result.getHelpLink();
				}
				if(result.getVersionInfoModel()!=null){
					UIContext.versionInfo = result.getVersionInfoModel();
					UIContext.INTERVAL_ACTIVE_TIMEOUT = result.getVersionInfoModel().getUiLogoutTime()*60*1000;
				}else{
					UIContext.versionInfo = new VersionInfoModel();
				}
				
				Window.setTitle(UIContext.productName);
				mainPanel = new HomepagePanel();
				RootPanel.get().setLayoutData(new FillLayout(Orientation.HORIZONTAL));
				RootPanel.get().add(mainPanel);
				addTimerAndEventHandler();
				
			}
			
		});
	}
	
	private void addTimerAndEventHandler() {
		detectInactiveTimer = new Timer() {
			public void run() {
				loginService.logout(new AsyncCallback<Void>(){

					@Override
					public void onFailure(Throwable caught) {
						mainPanel.cancelTimer();
					}

					@Override
					public void onSuccess(Void result) {
						mainPanel.cancelTimer();
						//showMessageBox();
						Window.Location.reload();
					}
					
				});
			}
		};
		detectInactiveTimer.schedule(UIContext.INTERVAL_ACTIVE_TIMEOUT);
		
		Event.addNativePreviewHandler(new NativePreviewHandler(){

			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONKEYPRESS
						|| event.getTypeInt() == Event.ONMOUSEMOVE){
					detectInactiveTimer.schedule(UIContext.INTERVAL_ACTIVE_TIMEOUT);
				}
			}
			
		});
	}
	
}
