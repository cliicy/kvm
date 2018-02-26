package com.ca.arcserve.linuximaging.ui.client.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.backup.wizard.BackupSource;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.NodeConnectionModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.toolbar.ToolBarPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;

public class AddServerDialog extends Dialog {
	public final int FIELD_WIDTH=200;
	public final int DIALOG_WIDTH=350;
	public final int FIELD_SET_WIDTH=324;
	
	private static final HomepageServiceAsync service = GWT.create(HomepageService.class);
	public TextField<String> nodeNameTextField;
	
	private FieldSet userSettings;
	public TextField<String> usernameTextField;
	public TextField<String> passwdTextField;
	
	private FieldSet rootAccountField;
	public TextField<String> rootUsernameTextField;
	public TextField<String> rootPasswdTextField;
	public TextArea descriptionTextField;
	private Label lblAddServer;
	private LabelField status;
	private ServiceInfoModel serviceInfo = Utils.getServiceInfo(null, null, null);

	private HashMap<AddServerType, AddServerCallback> callbackMap = new HashMap<AddServerType, AddServerCallback>();
	private AddServerCallback callback = null;
	private BackupSource bSource = null;
	private ToolBarPanel toolBar = null;
	private AddServerType addType = null;
	private boolean closeFlag = false;
	
	enum AddServerType{
		AddNode(1),
		AddBackupSource(2);
		
		private int value;
	
		private AddServerType(){};
		
		private AddServerType(int value)
		{
			this.value = value;
		}
		
		int getValue()
		{
			return value;
		}
	}
	
	public AddServerDialog(String title, ServiceInfoModel svInfo) {
		if ( svInfo != null )
		{
			serviceInfo = svInfo;
		}
		
		setButtons(Dialog.YESNOCANCEL);
		setButtonAlign(HorizontalAlignment.CENTER);
		setModal(true);
		this.setResizable(false);
		this.setWidth(DIALOG_WIDTH);

		setHeading(title);
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		tableLayout.setCellPadding(3);
		tableLayout.setCellSpacing(3);
		tableLayout.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		tableLayout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		setLayout(tableLayout);
		
		TableData labelData = new TableData();
		labelData.setWidth("25%");
		TableData spanData = new TableData();
		spanData.setColspan(2);
		
		lblAddServer = new Label(UIContext.Constants.toolBarAddNodeByHostname());
		add(lblAddServer, labelData);
		
		nodeNameTextField = new TextField<String>();
		nodeNameTextField.setAllowBlank(false);
		nodeNameTextField.setWidth(FIELD_WIDTH);
		add(nodeNameTextField);
		
		Label lblUserName = new Label(UIContext.Constants.userName());
		add(lblUserName, labelData);
		
		usernameTextField = new TextField<String>();
		usernameTextField.setWidth(FIELD_WIDTH);
		add(usernameTextField);
		
		Label lblPassword = new Label(UIContext.Constants.password());
		add(lblPassword, labelData);
		
		passwdTextField = new TextField<String>();
		passwdTextField.setPassword(true);	
		passwdTextField.setWidth(FIELD_WIDTH);
		add(passwdTextField);
		//defineUserSettings();
		//add(userSettings, spanData);
		
		if ( UIContext.selectedServerVersionInfo.isEnableNonRootUser() ) {
			defineRootAccountField();		
			add(rootAccountField, spanData);
		}
		
		Label lblDescription = new Label(UIContext.Constants.description());
		add(lblDescription, labelData);
		descriptionTextField = new TextArea();
		descriptionTextField.setWidth(FIELD_WIDTH);
		add(descriptionTextField);
		
		status = new LabelField();
		status.hide();
		add(status, spanData);
		
		this.getButtonById(YES).setText(UIContext.Constants.addMore());
		this.getButtonById(NO).setText(UIContext.Constants.addClose());
		this.getButtonById(CANCEL).setText(UIContext.Constants.close());
		this.getButtonById(CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				AddServerDialog.this.hide();
			}
		});

		this.getButtonById(YES).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				closeFlag = false;
				processNode(false);
			}
		});
		
		this.getButtonById(NO).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				closeFlag = true;
				processNode(false);
			}
		});
	}

	private FieldSet defineUserSettings() {
		userSettings = new FieldSet();
		userSettings.setHeading(UIContext.Constants.userSettings());
		userSettings.setWidth(FIELD_SET_WIDTH);
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		tableLayout.setCellPadding(3);
		tableLayout.setCellSpacing(2);
		userSettings.setLayout(tableLayout);
		
		TableData labelData = new TableData();
		labelData.setWidth("24%");
		Label lblUserName = new Label(UIContext.Constants.userName());
		userSettings.add(lblUserName, labelData);
		
		usernameTextField = new TextField<String>();
		usernameTextField.setWidth(FIELD_WIDTH);
		userSettings.add(usernameTextField);
		
		Label lblPassword = new Label(UIContext.Constants.password());
		userSettings.add(lblPassword, labelData);
		
		passwdTextField = new TextField<String>();
		passwdTextField.setPassword(true);	
		passwdTextField.setWidth(FIELD_WIDTH);
		userSettings.add(passwdTextField);
		return userSettings;
	}

	private FieldSet defineRootAccountField() {
		TableData spanData = new TableData();
		spanData.setColspan(2);
		Label notes = new Label(UIContext.Constants.rootUserNotes());
		add(notes, spanData);
		
		rootAccountField = new FieldSet();
		rootAccountField.setHeading(UIContext.Constants.enableRootUser());
		rootAccountField.setCheckboxToggle(true);
		rootAccountField.setWidth(FIELD_SET_WIDTH);
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		tableLayout.setCellPadding(3);
		tableLayout.setCellSpacing(2);
		rootAccountField.setLayout(tableLayout);
		
		TableData labelData = new TableData();
		labelData.setWidth("23%");
		
		Label lblRootUserName = new Label(UIContext.Constants.userName());
		rootAccountField.add(lblRootUserName, labelData);
		
		rootUsernameTextField = new TextField<String>();
		rootUsernameTextField.setWidth(FIELD_WIDTH);		
		rootAccountField.add(rootUsernameTextField);
		
		Label lblRootPassword = new Label(UIContext.Constants.password());
		rootAccountField.add(lblRootPassword, labelData);
		
		rootPasswdTextField = new TextField<String>();
		rootPasswdTextField.setPassword(true);	
		rootPasswdTextField.setWidth(FIELD_WIDTH);
		rootAccountField.add(rootPasswdTextField);
		rootAccountField.addListener(Events.Collapse, new Listener<FieldSetEvent>() {

			public void handleEvent(FieldSetEvent be) {				
					AddServerDialog.this.doLayout(true);
				}
			});
		return rootAccountField;
	}
		
	public String getNodeName() {
		return nodeNameTextField.getValue();
	}

	public void setNodeName(String serverName) {
		nodeNameTextField.setValue(serverName);
	}
	
	public void setBackupSource(BackupSource source)
	{
		addType = AddServerType.AddBackupSource;
		bSource = source;
	}
	
	public void setToolBar(ToolBarPanel tool)
	{
		addType = AddServerType.AddNode;
		toolBar = tool;
	}
	
	//this is a temp code for "Add D2D Server", should be removed later
	public void setCallback(AddServerCallback callb)
	{
		callback = callb;
	}
	
	public void processNode(boolean forceManage)
	{
		this.mask(UIContext.Constants.validating());
		final String nodeName = nodeNameTextField.getValue() == null ? "" : nodeNameTextField.getValue().trim();
		String userName = usernameTextField.getValue();
		String password = passwdTextField.getValue();
		String rootUserName = null;
		String rootPassword = null;
		if ( UIContext.selectedServerVersionInfo.isEnableNonRootUser() ) {
			rootUserName = rootAccountField.isExpanded() ? rootUsernameTextField.getValue() : null;
			rootPassword = rootAccountField.isExpanded() ? rootPasswdTextField.getValue() : null;
		}
		
		if (nodeName == null || nodeName.isEmpty())
		{
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Messages.nodeNameNotEmpty());
			clearWork();
			return;
		}

		if (userName == null)
			userName = "";

		if (password == null)
			password = "";
		
		String specialUser = userName.trim();
		String specialPwd = password;
		if ( rootUserName != null ) {
			specialUser += "\t"+rootUserName;
		}
		if ( rootPassword != null ) {
			specialPwd += "\n" + rootPassword;
		}
		final String user = specialUser;
		final String passwd = specialPwd;
		final String desc = descriptionTextField.getValue();
		
		//this is a temp code for "Add D2D Server", should be removed later
		if ( callback != null )
		{
			NodeModel model=new NodeModel(nodeName, user, passwd, desc, null);
			callback.processNode(model);
			clearWork();
			return;
		}
		
		setStatus("", MessageBox.INFO);
		service.connectNode(serviceInfo, nodeName, user, passwd, forceManage, new BaseAsyncCallback<NodeConnectionModel>(){

			@Override
			public void onFailure(Throwable caught) {
				//Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,"Failed to call web service to validate the node!");
				super.onFailure(caught);
				clearWork();
			}

			@Override
			public void onSuccess(NodeConnectionModel result) {
				if (result == null)
				{
					setStatus(UIContext.Constants.failed(), MessageBox.ERROR);
					if ( closeFlag ) {
						closeFlag = false;
					}
					clearWork();
					return;
				}
				
				int connStatus = Utils.getNodeConnectionMessageType(result);
				String mesg = Utils.getNodeConnectionMessage(result, nodeName, serviceInfo.getServer(), true);
				if ( connStatus == 1 )//connect node failed
				{
					if (result.getErrorCode() != null && result.getErrorCode() == 30) {
						SelectionListener<ButtonEvent> handler = new SelectionListener<ButtonEvent>(){
							@Override
							public void componentSelected(ButtonEvent ce) {
								AddServerDialog.this.unmask();
								processNode(true);
							}};
						SelectionListener<ButtonEvent> chandler = new SelectionListener<ButtonEvent>(){
								@Override
								public void componentSelected(ButtonEvent ce) {
									//if ( closeFlag ) {
									//	closeFlag = false;
									//}
									clearWork();
								}};
						Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR, mesg, handler, chandler);
					} else {
						setStatus(mesg, MessageBox.ERROR);
						if ( closeFlag ) {
							closeFlag = false;
						}
						clearWork();
					}
					return;
				} else if (connStatus == 2) {
					Utils.showMessage(UIContext.Constants.productName(),MessageBox.WARNING, mesg);
				}
				
				//connect node succeed
				setStatus(mesg, MessageBox.INFO);
				
				AddServerCallback callbackSub = getCallback(addType);
				if ( callbackSub != null )
				{
					NodeModel model=new NodeModel(nodeName, user, passwd, desc, result.getOS());
					model.setUUID(result.getNodeUUID());
					model.connInfo = result;
					callbackSub.processNode(model);
				}
				else
				{
					clearWork();
				}
			}});
	}
	
//	private void clearUI()
//	{
//		nodeNameTextField.setValue("");
//		usernameTextField.setValue("");
//		passwdTextField.setValue("");
//		descriptionTextField.setValue("");
//		
//	}
	
	public void setServerLabelName(String name)
	{
		if (!(Utils.isEmptyOrNull(name)))
		{
			lblAddServer.setText(name);
		}
	}
	
	public void setServiceInfo(String server)
	{
		serviceInfo = Utils.getServiceInfo(server, null, null);
	}
	
	public void setServiceInfo(ServiceInfoModel model)
	{
		serviceInfo = model;
	}
	
	private AddServerCallback getCallback(AddServerType type)
	{
		if ( callbackMap.containsKey(type) && callbackMap.get(type) != null )
		{
			return callbackMap.get(type);
		}
		
		AddServerCallback callback = null;
		switch (type)
		{
		case AddNode:
		{
			callback = new AddServerCallback(){
				@Override
				public void processNode(NodeModel model) {
					final String nodeName = model.getServerName();
					setStatus(UIContext.Messages.addingNode(nodeName), MessageBox.INFO);
					service.addNode(serviceInfo, model, new BaseAsyncCallback<Integer>(){

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							setStatus(UIContext.Messages.addNodeFailed(nodeName), MessageBox.ERROR);
							clearWork();
						}

						@Override
						public void onSuccess(Integer result) {
							if(result==0){
								if ( toolBar != null )
								{									
									if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.NODE)){
										toolBar.tabPanel.refreshNodeTable();
									}else{
										toolBar.tabPanel.showTabItem(HomepageTab.NODE);
									}
								}
								setStatus(UIContext.Messages.addNodeSuccessfully(nodeName), MessageBox.INFO);
							}else if(result==1){
								setStatus(UIContext.Messages.wizardBackupSourceDuplicated(nodeName) ,MessageBox.ERROR);
							}else{
								setStatus(UIContext.Messages.addNodeFailed(nodeName), MessageBox.ERROR);
							}
							clearWork();
						}
					});				
				}};
				
				break;
		}
		case AddBackupSource:
		{
			if ( bSource == null )
			{
				return null;
			}
			
			callback = new AddServerCallback(){

				@Override
				public void processNode(NodeModel model) {
					final String nodeName = model.getServerName();
					if ( bSource.duplicated(nodeName) )
					{
						setStatus(UIContext.Messages.wizardBackupSourceDuplicated(nodeName), MessageBox.WARNING);
						clearWork();
						return;
					}

					setStatus(UIContext.Messages.nodeIsProtectedOrNot(nodeName), MessageBox.INFO);
					List<NodeModel> nodeList = new ArrayList<NodeModel>();
					nodeList.add(model);
					
					final NodeModel node = model;
					service.getNodeProtectedState(serviceInfo, nodeList, new BaseAsyncCallback<List<NodeModel>>(){
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							clearWork();
						}

						@Override
						public void onSuccess(List<NodeModel> result) {

							if ( result == null || result.size() == 0 )
							{
								setStatus(UIContext.Constants.failed(), MessageBox.ERROR);
								clearWork();
								return;
							}
							
							NodeModel nodeInfo = result.get(0);
							Boolean protect = nodeInfo.getProtected();
							if (protect!=null && protect.booleanValue())
							{
								setStatus(UIContext.Messages.nodeAlreadyBeenProtected(nodeName, nodeInfo.getJobName()), MessageBox.WARNING);
								clearWork();
							}
							else
							{
								final String nodeName = node.getServerName();
								setStatus(UIContext.Messages.addingNode(nodeName), MessageBox.INFO);
								service.addNode(serviceInfo, node, new BaseAsyncCallback<Integer>(){

									@Override
									public void onFailure(Throwable caught) {
										super.onFailure(caught);
										setStatus(UIContext.Messages.addNodeFailed(nodeName), MessageBox.ERROR);
										clearWork();
									}

									@Override
									public void onSuccess(Integer result) {
										if(result==0 || result == 1){
											bSource.getServerList().add(node);
											bSource.getServerInfo().addData(node);
											setStatus(UIContext.Messages.addNodeSuccessfully(nodeName), MessageBox.INFO);
										}else{
											setStatus(UIContext.Messages.addNodeFailed(nodeName), MessageBox.ERROR);
										}
										clearWork();
									}
								});
							}
						}});
				}};
				
				break;
		}
		}
		
		callbackMap.put(type, callback);
		return callback;
	}
	
	private void setStatus(String text, String type)
	{
		if (Utils.isEmptyOrNull(text) || type == null)
		{
			status.hide();
			return;
		}
		
		if ( type.equals(MessageBox.INFO) )
		{
			status.setText(text);
		}
		else
		{
			status.setText(text);
			Utils.showMessage(UIContext.Constants.productName(), type, text);
		}
		status.show();
	}
	
	private void clearWork()
	{
		unmask();
		if ( closeFlag )
		{
			hide();
		}
	}
}
