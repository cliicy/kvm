package com.ca.arcserve.linuximaging.ui.client.components.backup.node;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.components.backup.node.i18n.NodeUIConstants;
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

public class ComponentNodeCaptureServerDlg extends Dialog {
	private static final NodeUIConstants uiConstants = GWT.create(NodeUIConstants.class);
	static final IBackupNodeServiceAsync backupService = GWT.create(IBackupNodeService.class);
	private TextField<String> jobNameTextField;
	private TextField<String> excludeTextField;
	private ListBox templateComboBox;
	protected boolean isClickOK = false;
	private String jobName = null;
	private String excludeFileSystems = null;
	private String templateName = null;
	private ComponentNode nodeTree = null;

	public ComponentNodeCaptureServerDlg() {
		setButtons(Dialog.OKCANCEL);
		setButtonAlign(HorizontalAlignment.CENTER);
		setModal(true);
		this.setResizable(false);
		this.setWidth(350);
		this.setHideOnButtonClick(true);
		setHeading(uiConstants.captureServer());
		setLayout(new TableLayout(2));
		
		Label lblJobName = new Label(uiConstants.lblJobName_text());
		add(lblJobName);
		
		jobNameTextField = new TextField<String>();
		add(jobNameTextField);
		
		Label lblBackupTemplate = new Label(uiConstants.lblBackupTemplate_text());
		add(lblBackupTemplate);
		
		templateComboBox = new ListBox();
		add(templateComboBox);
		templateComboBox.setWidth("100%");
		
		Label lblExcludeFileSystems = new Label(uiConstants.lblExcludeFileSystems_text());
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
				
				if ( nodeTree != null )
				{
					nodeTree.CaptureServer(templateName, jobName, excludeFileSystems);
				}
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
	
	public void setNodeTree(ComponentNode tree) {
		nodeTree = tree;
	}
	
	protected void refreshData(){
		backupService.GetTemplateList(new AsyncCallback<List<String>>(){

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
