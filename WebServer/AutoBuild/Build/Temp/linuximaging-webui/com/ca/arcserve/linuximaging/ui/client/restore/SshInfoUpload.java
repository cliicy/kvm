package com.ca.arcserve.linuximaging.ui.client.restore;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;

public class SshInfoUpload extends Dialog {
	private static final String UPLOAD_URL = "UploadFile";
	private FormPanel uploadPanel;
	private TextField<String> username;
	private FileUploadField externalFileField;
	private TextField<String> passphrase;
	private String serverName;
	private ServiceInfoModel serviceInfo;
	private boolean isCancelled;
	
	public SshInfoUpload(String serverName,ServiceInfoModel serviceInfo){
		this.serverName = serverName;
		this.serviceInfo = serviceInfo;
		setModal(true);
		setWidth(500);
		setHeight(230);
		setClosable(false);
		setHeading(UIContext.Constants.productName());
		setButtons(Dialog.OKCANCEL);
		this.setButtonAlign(HorizontalAlignment.CENTER);
		getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				uploadPanel.submit();
			}
			
		});
		getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				isCancelled = true;
				SshInfoUpload.this.hide();
			}
			
		});
		addMainPanel();
	}
	
	private void addMainPanel(){

		LabelField label = new LabelField(UIContext.Constants.serverInCloudNeedSsh());
		label.setInputStyleAttribute("padding-top", "5px");
		label.setInputStyleAttribute("padding-bottom", "5px");
		label.setInputStyleAttribute("padding-left", "2px");
		label.setInputStyleAttribute("padding-right", "2px");
		add(label);
		
		uploadPanel = new FormPanel();
		uploadPanel.setEncoding(Encoding.MULTIPART);
		uploadPanel.setMethod(Method.POST);
		uploadPanel.setAction(UPLOAD_URL);
		uploadPanel.setWidth(500);
		uploadPanel.setHeight(150);
		uploadPanel.setHeaderVisible(false);
		uploadPanel.setBodyBorder(false);
		uploadPanel.setBorders(false);
		uploadPanel.addListener(Events.Submit,new Listener<FormEvent>(){
		            @Override
		    public void handleEvent(FormEvent be){        
		        String result = be.getResultHtml();
		        if(result == null){
		        	Utils.showMessage(UIContext.Constants.productName(), MessageBox.ERROR, UIContext.Constants.failedToUploadFile());
		        }else if(result.contains("0")){
		        	SshInfoUpload.this.hide();
		        }else if(result.contains("2")){
		        	Utils.showMessage(UIContext.Constants.productName(), MessageBox.ERROR, UIContext.Constants.invalidKeyFile());
		        }
		    }            
		});        
		uploadPanel.setPadding(5);
		uploadPanel.setFieldWidth(250);
		uploadPanel.setLabelWidth(100);
		HiddenField<String> serverNameField = new HiddenField<String>();
		serverNameField.setName("servername");
		serverNameField.setValue(serverName);
		HiddenField<String> serviceNameField = new HiddenField<String>();
		serviceNameField.setName("servicename");
		serviceNameField.setValue(serviceInfo.getServer());
		HiddenField<Integer> servicePortField = new HiddenField<Integer>();
		servicePortField.setName("serviceport");
		servicePortField.setValue(serviceInfo.getPort());
		HiddenField<String> serviceProtocolField = new HiddenField<String>();
		serviceProtocolField.setName("serviceprotocol");
		serviceProtocolField.setValue(serviceInfo.getProtocol());
		HiddenField<String> serviceTypeField = new HiddenField<String>();
		serviceTypeField.setName("servicetype");
		serviceTypeField.setValue(serviceInfo.getType());
		
		uploadPanel.add(serverNameField);
		uploadPanel.add(serviceNameField);
		uploadPanel.add(servicePortField);
		uploadPanel.add(serviceProtocolField);
		uploadPanel.add(serviceTypeField);
		username = new TextField<String>();
		username.setFieldLabel(UIContext.Constants.userName());
		username.setName("username");
		username.setAllowBlank(false);
		username.setWidth(200);
		uploadPanel.add(username);
		externalFileField = new FileUploadField();
		externalFileField.setName("importNodeFile");
		externalFileField.setAllowBlank(false);
		externalFileField.setFieldLabel(UIContext.Constants.sshKeyFile());
		externalFileField.setWidth(250);
		uploadPanel.add(externalFileField);
		
		passphrase = new TextField<String>();
		passphrase.setPassword(true);
		passphrase.setFieldLabel(UIContext.Constants.passphrase());
		passphrase.setName("passphrase");
		passphrase.setWidth(200);
		uploadPanel.add(passphrase);
		
		
		this.add(uploadPanel);
	}

	public boolean isCancelled(){
		return this.isCancelled;
	}
	
}
