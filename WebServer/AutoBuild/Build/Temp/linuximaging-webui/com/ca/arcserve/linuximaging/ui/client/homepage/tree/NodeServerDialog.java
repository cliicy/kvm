package com.ca.arcserve.linuximaging.ui.client.homepage.tree;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.Homepagetree;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.exception.BusinessLogicException;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;

public class NodeServerDialog extends Dialog {
	private D2DServerServiceAsync d2dServerService = GWT.create(D2DServerService.class); 
	public static final String HTTP="http";
	public static final String HTTPS="https";
	public static final String DEFAULT_PROTOCOL="http:";
	public static final int DEFAULT_PORT=8014;
	
	private Homepagetree nodeTree = null;
	private TextField<String> serverNameTextField;
	private TextField<String> usernameTextField;
	private TextField<String> passwordTextField;
	private Radio httpRadio;
	private Radio httpsRadio;
//	private BaseSimpleComboBox<String> cmbProtocolField;
	private NumberField portField;
	protected boolean isClickOK = false;
	private boolean isModify;
	private ServiceInfoModel model;
	private boolean isForce = false;
	
	public NodeServerDialog(Homepagetree tree, final boolean isModify) {
		this.isModify = isModify;
		nodeTree=tree;
		setButtons(Dialog.OKCANCEL);
		setButtonAlign(HorizontalAlignment.CENTER);
		setModal(true);
		this.setResizable(false);
		this.setWidth(350);
		this.setHideOnButtonClick(false);
		if(isModify){
			setHeading(UIContext.Constants.modifyServer());	
		}else{
			setHeading(UIContext.Constants.addServer());
		}
		
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("95%");
		tableLayout.setCellPadding(3);
		tableLayout.setCellSpacing(3);
		tableLayout.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		tableLayout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		setLayout(tableLayout);
		
		Label label = new Label(UIContext.Constants.serverName());
		add(label);
		
		serverNameTextField = new TextField<String>();
		serverNameTextField.setAllowBlank(false);
		if(isModify){
			serverNameTextField.disable();
		}
		add(serverNameTextField);
		
		label = new Label(UIContext.Constants.userName());
		add(label);
		
		usernameTextField = new TextField<String>();
		usernameTextField.setAllowBlank(false);
		add(usernameTextField);
		
		label = new Label(UIContext.Constants.password());
		add(label);
		passwordTextField = new TextField<String>();
		passwordTextField.setPassword(true);
		add(passwordTextField);
		if(isModify){	
			usernameTextField.disable();
			passwordTextField.disable();
		}
		label = new Label(UIContext.Constants.targetServerProtocol());
		//add(label);
		
		httpRadio = new Radio();
		httpRadio.setValue(true);
		httpRadio.setBoxLabel(UIContext.Constants.targetServerProtocol_Http());
		
		httpsRadio = new Radio();
		httpsRadio.setBoxLabel(UIContext.Constants.targetServerProtocol_Https());
		
		RadioGroup group = new RadioGroup();
		group.add(httpRadio);
		group.add(httpsRadio);
		//add(group);
		
		label = new Label(UIContext.Constants.targetServerPort());
		add(label);
		
		portField = new NumberField();
		portField.setValue(DEFAULT_PORT);	
		portField.setValidator(new Validator(){
			@Override
			public String validate(Field<?> field, String value) {
				int iProxyPort = portField.getValue().intValue();
				
				if(iProxyPort < 0 || iProxyPort > 65535)
				{
					portField.setValue(null);
					Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.validatePortMessage());
				}
				return null;
			}
			
		});
		
		portField.setValidateOnBlur(true);
		add(portField);
		
		this.getButtonById(OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				isClickOK = true;
				if(validate()){
					if(model == null){
						model=new ServiceInfoModel();
					}
					model.setServer(serverNameTextField.getValue());
					model.setProtocol(httpRadio.getValue() == true ? HTTP:HTTPS);
					model.setPort(portField.getValue().intValue());
					model.setUserName(usernameTextField.getValue());
					model.setPasswd(passwordTextField.getValue());
					if(isModify){
						modifyD2DServer();
					}else{
						/*if(nodeTree.isDuplicate(model)){
							Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.d2dServerDuplicateMessage());
							return;
						}*/
						NodeServerDialog.this.mask(UIContext.Constants.validating());
						model.setUserName(usernameTextField.getValue());
						model.setPasswd(passwordTextField.getValue());
						addD2DServer(false);
						
					}
				}else{
					ce.cancelBubble();
				}
			}

		});

		this.getButtonById(CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				isClickOK = false;	
				NodeServerDialog.this.hide();
			}
		});
	}
	
	private void addD2DServer(final boolean isForce){
		d2dServerService.addD2DServer(model, isForce,new BaseAsyncCallback<ServiceInfoModel>(){

			@Override
			public void onFailure(Throwable caught) {
				if(!isForce){
					if( caught instanceof BusinessLogicException){
						BusinessLogicException e = (BusinessLogicException)caught;
						//Managed by other server
						if(e.getErrorCode().equals("55834574849")){
							MessageBox mb = new MessageBox();
							mb.setTitle(UIContext.productName);
							mb.setMessage(UIContext.Constants.managedByOtherServerConfirm());
							mb.setIcon(MessageBox.WARNING);
							mb.setButtons(MessageBox.YESNO);
							mb.addCallback(new Listener<MessageBoxEvent>(){
	
								@Override
								public void handleEvent(MessageBoxEvent be) {
									if(be.getButtonClicked().getItemId().equals(Dialog.YES)){
										addD2DServer(true);
									}else{
										be.cancelBubble();
										NodeServerDialog.this.unmask();
										NodeServerDialog.this.hide();
									}
								}
							});
							mb.setModal(true);
							mb.show();
						}else{
							super.onFailure(caught);
							NodeServerDialog.this.unmask();
						}
					}else{
						super.onFailure(caught);
						NodeServerDialog.this.unmask();
					}
				}else{
					super.onFailure(caught);
					NodeServerDialog.this.unmask();
				}
				
			}

			@Override
			public void onSuccess(ServiceInfoModel result) {
				NodeServerDialog.this.unmask();
				if(result != null){
					model.setId(result.getId());
					model.setProtocol(result.getProtocol());
					model.setAuthKey(result.getAuthKey());
					nodeTree.addElement(model);
					NodeServerDialog.this.hide();
				}else{
					Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Messages.connectD2DServerErrorMessage(model.getServer(),UIContext.productName));
					NodeServerDialog.this.unmask();
				}
			}
			
		});
	}
	
	private void modifyD2DServer(){
		NodeServerDialog.this.mask(UIContext.Constants.validating());
		d2dServerService.modifyD2DServer(model,isForce, new BaseAsyncCallback<ServiceInfoModel>(){

			@Override
			public void onFailure(Throwable caught) {
				if(!isForce){
					if( caught instanceof BusinessLogicException){
						BusinessLogicException e = (BusinessLogicException)caught;
						//Managed by other server
						if(e.getErrorCode().equals("55834574849")){
							MessageBox mb = new MessageBox();
							mb.setTitle(UIContext.productName);
							mb.setMessage(UIContext.Constants.mofidyManagedByOtherServerConfirm());
							mb.setIcon(MessageBox.WARNING);
							mb.setButtons(MessageBox.YESNO);
							mb.addCallback(new Listener<MessageBoxEvent>(){
	
								@Override
								public void handleEvent(MessageBoxEvent be) {
									if(be.getButtonClicked().getItemId().equals(Dialog.YES)){
										isForce = true;
										usernameTextField.enable();
										passwordTextField.enable();
										NodeServerDialog.this.unmask();
									}else{
										be.cancelBubble();
										NodeServerDialog.this.unmask();
										NodeServerDialog.this.hide();
									}
								}
							});
							mb.setModal(true);
							mb.show();
						}else{
							super.onFailure(caught);
							NodeServerDialog.this.unmask();
						}
					}else{
						super.onFailure(caught);
						NodeServerDialog.this.unmask();
					}
				}else{
					super.onFailure(caught);
					NodeServerDialog.this.unmask();
				}
			}

			@Override
			public void onSuccess(ServiceInfoModel result) {
				NodeServerDialog.this.unmask();
				if(result !=null){
					model.setProtocol(result.getProtocol());
					nodeTree.modifyElement(model);
					NodeServerDialog.this.hide();
				}else{
					Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.addServerErrorMessage());
					NodeServerDialog.this.unmask();
				}
			}
			
		});
	}
	
	public boolean validate() {
		if(serverNameTextField.getValue() == null || serverNameTextField.getValue().isEmpty()){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.validateServerMessage());
			return false;
		}
		if(!isModify || isForce){
			if(!usernameTextField.validate()){
				return false;
			}
		}
		if(portField.getValue()==null||portField.getValue().intValue()<0||portField.getValue().intValue()>65535){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.validatePortMessage());
			return false;
		}
		return true;
	}
	
	public boolean isClickOK() {
		return isClickOK;
	}

	public void setClickOK(boolean isClickOK) {
		this.isClickOK = isClickOK;
	}

	public void setNodeTree(Homepagetree tree) {
		nodeTree = tree;
	}

	public void refreshData(ServiceInfoModel model) {
		if(model!=null){
			this.model = model;
			serverNameTextField.setValue(model.getServer());
			if(model.getProtocol()!=null && model.getProtocol().equals(HTTP)){
				httpRadio.setValue(true);
			}else{
				httpsRadio.setValue(true);
			}
			portField.setValue(model.getPort());
		}
	}

}
