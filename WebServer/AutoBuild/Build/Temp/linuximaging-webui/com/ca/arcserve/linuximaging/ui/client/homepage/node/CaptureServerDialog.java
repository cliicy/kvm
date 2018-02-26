package com.ca.arcserve.linuximaging.ui.client.homepage.node;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class CaptureServerDialog extends Dialog {
	static final HomepageServiceAsync backupService = GWT.create(HomepageService.class);
	private TextField<String> jobNameTextField;
	private TextField<String> excludeTextField;
	private ListBox templateComboBox;
	protected boolean isClickOK = false;
	private String jobName = null;
	private String excludeFileSystems = null;
	private String templateName = null;
//	private ComponentNode nodeTree = null;

	public CaptureServerDialog(ServiceInfoModel model) {
		final ServiceInfoModel node=model;
		setButtons(Dialog.OKCANCEL);
		setButtonAlign(HorizontalAlignment.CENTER);
		setModal(true);
		this.setResizable(false);
		this.setWidth(350);
		this.setHideOnButtonClick(true);
		setHeading(UIContext.Constants.captureServer());
		setLayout(new TableLayout(2));
		
		Label lblJobName = new Label(UIContext.Constants.jobName());
		add(lblJobName);
		
		jobNameTextField = new TextField<String>();
		add(jobNameTextField);
		
		Label lblBackupTemplate = new Label(UIContext.Constants.lblBackupTemplate_text());
		add(lblBackupTemplate);
		
		templateComboBox = new ListBox();
		add(templateComboBox);
		templateComboBox.setWidth("100%");
		
		Label lblExcludeFileSystems = new Label(UIContext.Constants.lblExcludeFileSystems_text());
		add(lblExcludeFileSystems);
		
		excludeTextField = new TextField<String>();
		add(excludeTextField);
		
		this.getButtonById(OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				isClickOK = true;
				jobName = jobNameTextField.getValue();
				excludeFileSystems = excludeTextField.getValue();
				templateName = templateComboBox.getValue(templateComboBox.getSelectedIndex());
				
				if (jobName == null)
					jobName = "";

				if (excludeFileSystems == null)
					excludeFileSystems = "";

				if (templateName == null)
					templateName = "";
				
				backupService.captureServer(Utils.getServiceInfo(null, null, null), node, jobName, templateName, excludeFileSystems, new AsyncCallback<Long>(){

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to capture server :" + caught.getMessage());
					}
		
					@Override
					public void onSuccess(Long result) {
						if(result>=0){
							Window.alert("Success to capture server!");
		//					componentJobStatus.jobStatusTable.addData(result);
						}else{
							Window.alert("Failed to capture server!");
						}
					}
				});
//				if ( nodeTree != null )
//				{
//					nodeTree.CaptureServer(templateName, jobName, excludeFileSystems);
//				}
			}


		});

		this.getButtonById(CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				isClickOK = false;				
			}
		});
		
		refreshData();
		
	}
	
	public boolean isClickOK() {
		return isClickOK;
	}

	public void setClickOK(boolean isClickOK) {
		this.isClickOK = isClickOK;
	}
	
	protected void refreshData(){
		backupService.getTemplateList(Utils.getServiceInfo(null, null, null), new AsyncCallback<List<String>>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("GetTemplateList failed!");
			}

			@Override
			public void onSuccess(List<String> result) {
				
				if (result != null && result.size() > 0)
				{
					for (String name : result)
					{
						templateComboBox.addItem(name);
					}
				}
			}});
	}
}
