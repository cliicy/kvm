package com.ca.arcserve.linuximaging.ui.client.homepage.jobstatus;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class MigrationSourceHostWindow extends Window {
	
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private int MIN_BUTTON_WIDTH = 90;
	private ServiceInfoModel d2dServer;
	private JobStatusModel model;
	private TextField<String> sourceIP;
	
	public MigrationSourceHostWindow(JobStatusModel model,ServiceInfoModel d2dServerModel){
		this.d2dServer = d2dServerModel;
		this.model = model;
		this.setResizable(false);
		this.setWidth(380);
		this.setHeading(UIContext.productName);
		
		TableLayout layout = new TableLayout();
		layout.setWidth("95%");
		layout.setColumns(2);
		layout.setCellPadding(4);
		layout.setCellSpacing(0);
		this.setLayout(layout);
		
		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("80%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField label = new LabelField(UIContext.Constants.sourceIPMigrationDescription());
		TableData td = new TableData();
		td.setColspan(2);
		add(label,td);
		
		label = new LabelField(UIContext.Constants.migrationBmrSource());
		sourceIP = new TextField<String>();
		sourceIP.setWidth(200);
		sourceIP.setAllowBlank(false);
		add(label,tdLabel);
		add(sourceIP,tdField);
		
		Button okButton = new Button();
		okButton.ensureDebugId("a8b8a97c-5aaf-4af5-9bdf-307b5f35e036");
		okButton.setText(UIContext.Constants.OK());
		okButton.setMinWidth(MIN_BUTTON_WIDTH);

		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(d2dServer !=null){
					if(sourceIP.validate()){
						triggerMigration();
					}
				}else{
					MigrationSourceHostWindow.this.hide();
				}
			}

		});
		this.addButton(okButton);
		Button cancelButton = new Button();
		cancelButton.ensureDebugId("ea145e35-6ed5-4202-bd89-a7c88a0f03e7");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.setMinWidth(MIN_BUTTON_WIDTH);
		
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				MigrationSourceHostWindow.this.hide();
			}

		});
		this.addButton(cancelButton);
		this.setButtonAlign(HorizontalAlignment.CENTER);
	}
	
	private void triggerMigration(){
		service.startMigrateData(d2dServer,sourceIP.getValue(),model, new BaseAsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				MigrationSourceHostWindow.this.hide();
			}

			@Override
			public void onSuccess(Integer result) {
				if(result == 0){
					Utils.showMessage(UIContext.productName, MessageBox.INFO, UIContext.Constants.startMigrationSuccessfully());
				}else{
					Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Constants.startMigrationFailed());
				}
				MigrationSourceHostWindow.this.hide();
			}
		});
	}

}
