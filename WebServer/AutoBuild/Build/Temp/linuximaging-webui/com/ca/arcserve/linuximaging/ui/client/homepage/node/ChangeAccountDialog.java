package com.ca.arcserve.linuximaging.ui.client.homepage.node;


import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.PasswordField;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.NodeConnectionModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;

public class ChangeAccountDialog extends Dialog {
	private static final HomepageServiceAsync service = GWT.create(HomepageService.class);
	public final int FIELD_WIDTH=200;
	public final int DIALOG_WIDTH=350;
	public final int FIELD_SET_WIDTH=324;
	public final int NODE_LIST_HIGHT=80;
	
	private ListField<NodeModel> nodeList;
	private ListStore<NodeModel> nodeStore;
	private TextField<String> nodeNameTextField;
	private FieldSet userSettings;
	private TextField<String> usernameTextField;
	private PasswordField passwdTextField;
	private TextArea descriptionTextField = null;
	private Label nodeNameLbl;

	private TextField<String> rootUsernameTextField;
	private PasswordField rootPasswdTextField;
	private FieldSet rootAccountField;

	private ChangeAccountCallback callback;
	private List<NodeModel> oriNodeList;
	private ServiceInfoModel currentServer;
	
	public ChangeAccountDialog(ServiceInfoModel server, List<NodeModel> serverlist) {
		currentServer = server;
		oriNodeList = serverlist;
		
		setButtons(Dialog.OKCANCEL);
		setButtonAlign(HorizontalAlignment.CENTER);
		setModal(true);
		this.setResizable(false);
		this.setWidth(DIALOG_WIDTH);
		
		setHeading(UIContext.Constants.changeAccount());
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		tableLayout.setCellPadding(3);
		tableLayout.setCellSpacing(3);
		setLayout(tableLayout);
		
		if (serverlist == null || serverlist.size() == 0)
		{
			add(new LabelField(UIContext.Messages.noSelectedNodes()));
			return;
		}

		TableData labelData = new TableData();
		labelData.setWidth("25%");
		if ( serverlist.size() == 1 )
		{
			nodeNameLbl = new Label(UIContext.Constants.toolBarAddNodeByHostname());
			add(nodeNameLbl, labelData);
			nodeNameTextField = new TextField<String>();
			nodeNameTextField.setAllowBlank(false);
			nodeNameTextField.setWidth(FIELD_WIDTH);
			nodeNameTextField.disable();
			nodeNameTextField.setValue(serverlist.get(0).getServerName());
			add(nodeNameTextField);
		}
		else
		{
			nodeNameLbl = new Label(UIContext.Constants.nodeList());
			add(nodeNameLbl, labelData);
			nodeList = new ListField<NodeModel>();
			nodeList.setDisplayField("server");
			nodeStore = new ListStore<NodeModel>();
			nodeStore.setStoreSorter(new StoreSorter<NodeModel>());
			nodeStore.add(serverlist);
			nodeList.setStore(nodeStore);
			nodeList.setWidth(FIELD_WIDTH);
			nodeList.setHeight(NODE_LIST_HIGHT);
			nodeList.disable();
			add(nodeList);
		}
		
		Label lblUserName = new Label(UIContext.Constants.userName());
		add(lblUserName,labelData);
		
		usernameTextField = new TextField<String>();
		//usernameTextField.setAllowBlank(false);
		usernameTextField.setWidth(FIELD_WIDTH);
		add(usernameTextField);
		
		Label lblPassword = new Label(UIContext.Constants.password());
		add(lblPassword);		
		passwdTextField = new PasswordField(FIELD_WIDTH);
		add(passwdTextField);
		
		TableData spanData = new TableData();
		spanData.setColspan(2);		
		//defineUserSettings();
		//add(userSettings, spanData);

		if ( UIContext.selectedServerVersionInfo.isEnableNonRootUser() ) {
			defineRootAccountField();
			add(rootAccountField, spanData);
		}
		
		if ( serverlist.size() == 1 )
		{
			String specialUser = serverlist.get(0).getUserName();
			int pos = specialUser.indexOf('\t');
			if ( pos != -1 && UIContext.selectedServerVersionInfo.isEnableNonRootUser()) {
				rootAccountField.setExpanded(true);
				usernameTextField.setValue(specialUser.substring(0, pos));
				rootUsernameTextField.setValue(specialUser.substring(pos+1));
				rootPasswdTextField.setPasswordValue(serverlist.get(0).getPasswd());
			} else {
				if (  UIContext.selectedServerVersionInfo.isEnableNonRootUser() ) {
					rootAccountField.setExpanded(false);
				}
				usernameTextField.setValue(specialUser);
			}
			
			passwdTextField.setPasswordValue(serverlist.get(0).getPasswd());
			
			Label lblDescription = new Label(UIContext.Constants.description());
			add(lblDescription, labelData);
			descriptionTextField = new TextArea();
			descriptionTextField.setWidth(FIELD_WIDTH);
			add(descriptionTextField);
			String desc = serverlist.get(0).getDescription();
			if (!Utils.isEmptyOrNull(desc))
			{
				descriptionTextField.setValue(desc);
			}
		}

		this.getButtonById(OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				processNode(false);
			}
		});
		
		this.getButtonById(CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				ChangeAccountDialog.this.hide();
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
		usernameTextField.setAllowBlank(false);
		usernameTextField.setWidth(FIELD_WIDTH);
		userSettings.add(usernameTextField);
		
		Label lblPassword = new Label(UIContext.Constants.password());
		userSettings.add(lblPassword, labelData);		
		passwdTextField = new PasswordField(FIELD_WIDTH);
		userSettings.add(passwdTextField);
		return userSettings;
	}
	
	private FieldSet defineRootAccountField() {
		TableData spanData = new TableData();
		spanData.setColspan(2);
		Label notes = new Label(UIContext.Constants.rootUserNotes());
		//notes.setStyleAttribute("font-weight", "bold");
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
		
		rootPasswdTextField = new PasswordField(FIELD_WIDTH);
		rootAccountField.add(rootPasswdTextField);
		rootAccountField.addListener(Events.Collapse, new Listener<FieldSetEvent>() {

			public void handleEvent(FieldSetEvent be) {				
					ChangeAccountDialog.this.doLayout(true);
				}
			});

		return rootAccountField;
	}
	
	public List<NodeModel> getNodeModelList()
	{
		List<NodeModel> nodeMap = new ArrayList<NodeModel>();

		String userName = usernameTextField.getValue();
		String password = passwdTextField.getPasswordValue();
		String rootUserName = null;
		String rootPassword = null;
		if ( UIContext.selectedServerVersionInfo.isEnableNonRootUser() ) {
			rootUserName = rootAccountField.isExpanded() ? rootUsernameTextField.getValue() : null;
			rootPassword = rootAccountField.isExpanded() ? rootPasswdTextField.getPasswordValue() : null;
			if (rootPassword.equals("\n")) {
				rootPassword = null;
			}
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
		
		for (NodeModel node : oriNodeList)
		{
			NodeModel tmpNode = new NodeModel();
			tmpNode.setServerName(node.getServerName());
			tmpNode.setUserName(specialUser);
			tmpNode.setPasswd(specialPwd);
						
			if ( descriptionTextField != null )
			{
				tmpNode.setDescription(descriptionTextField.getValue()==null?"":descriptionTextField.getValue());
			} else {
				tmpNode.setDescription(null);
			}
			nodeMap.add(tmpNode);
		}
		
		return nodeMap;
	}
	
	public List<NodeModel> replaceUserAccountInfo(NodeModel newModel)
	{
		for (NodeModel node : oriNodeList)
		{
			node.setUserName(newModel.getUserName());
			node.setPasswd(newModel.getPasswd());
			node.setUUID(newModel.getUUID());			
			if ( newModel.getDescription() != null )
			{
				node.setDescription(newModel.getDescription());
			}
		}
		
		return oriNodeList;
	}
	
	public void setCallback(ChangeAccountCallback callback)
	{
		this.callback = callback;
	}

	public void processNode(boolean forceManage)
	{
		final List<NodeModel> nodesMap = getNodeModelList();
		if ( nodesMap == null || nodesMap.size() == 0 ){
			return;
		}
		
		//currently, we only support validate for one node
		final NodeModel node = nodesMap.get(0);
		this.mask(UIContext.Constants.validating());
		service.connectNode(currentServer, node.getServerName(), node.getUserName(), node.getPasswd(), forceManage, new BaseAsyncCallback<NodeConnectionModel>(){

			@Override
			public void onFailure(Throwable caught) {
				//Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,caught.getMessage());
				super.onFailure(caught);
				clearWork(false);
			}

			@Override
			public void onSuccess(NodeConnectionModel result) {
				int connStatus = Utils.getNodeConnectionMessageType(result);
				if ( connStatus == 1 )//connect node failed
				{
					String mesg = Utils.getNodeConnectionMessage(result, node.getServerName(), currentServer.getServer(), true);
					if ( result.getErrorCode() != null && result.getErrorCode() == 30 ) {
						SelectionListener<ButtonEvent> handler = new SelectionListener<ButtonEvent>(){
							@Override
							public void componentSelected(ButtonEvent ce) {
								clearWork(false);
								processNode(true);
							}};
						SelectionListener<ButtonEvent> chandler = new SelectionListener<ButtonEvent>(){
								@Override
								public void componentSelected(ButtonEvent ce) {									
									clearWork(false);
								}};
						Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR, mesg, handler, chandler);
						clearWork(false);
					} else {
						Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,mesg);
						clearWork(false);
					}					
					return;
				} else if (connStatus == 2) {
					String mesg = Utils.getNodeConnectionMessage(result, node.getServerName(), currentServer.getServer(), true);
					Utils.showMessage(UIContext.Constants.productName(),MessageBox.WARNING, mesg);
				}
				
				//connect node succeed
				if ( callback != null )
				{   
					node.setUUID(result.getNodeUUID());
					List<NodeModel> nodeList = replaceUserAccountInfo(node);
					callback.processNodeList(nodeList);
					clearWork(true);
				} else {
					clearWork(true);
				}
			}});
	}
	
	private void clearWork(boolean isClose)
	{
		unmask();
		if ( isClose ) {
			this.hide();
		}
	}
}
