package com.ca.arcserve.linuximaging.ui.client.standby;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class SourceNode extends LayoutContainer {
	public static int LABEL_WIDTH = 220;
	public static int FIELD_WIDTH = 370;
	public static int TABLE_HIGHT = 240;
	public static int TABLE_WIDTH = 650;
	public static int MIN_BUTTON_WIDTH = 70;
	
	private HomepageServiceAsync service = GWT.create(HomepageService.class);

	private List<NodeModel> defaultNodeList;
	private StandbyWizardPanel parentWindow;
	private ServiceInfoModel serviceInfo;
	private SourceNodeGrid sourceNodeGrid;
	
	public SourceNode(StandbyWizardPanel parent,List<NodeModel> defaultNodeList)
	{
		parentWindow = parent;
		defineMainPanel();
		serviceInfo = getBackupServer();
		this.defaultNodeList = defaultNodeList;
		if (parentWindow != null )
		{
			getBackupServer();
		}
	}
	
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		if ( defaultNodeList!=null && defaultNodeList.size() > 0 ) {
			refreshData();
		}
	}
	
	public HomepageServiceAsync getService() {
		return service;
	}
	
	public ServiceInfoModel getBackupServer()
	{
		if (parentWindow != null )
		{
			return parentWindow.getD2DServerInfo();
		}
		return null;
	}
	
	private void defineMainPanel()
	{
		TableLayout layout = new TableLayout(1);
		layout.setCellSpacing(5);
		layout.setWidth("97%");
		this.setLayout(layout);
		this.setHeight(StandbyWizardPanel.RIGHT_PANEL_HIGHT-20);
		this.setAutoHeight(true);
		

		LabelField label = new LabelField();
		label.setText(UIContext.Constants.sourceNodesHeader());
		label.setStyleAttribute("font-weight", "bold");
		add(label);
		
		label = new LabelField();
		label.setText(UIContext.Constants.sourceNodesHint());
		add(label);
		
		Button removeButton = new Button(UIContext.Constants.restoreRemove());
		removeButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(sourceNodeGrid.getSelectedItems().size() == sourceNodeGrid.getCountOfItems()){
					MessageBox.confirm(UIContext.Constants.productName(), UIContext.Constants.lastNodeWarning(), new Listener<MessageBoxEvent>(){

						@Override
						public void handleEvent(MessageBoxEvent be) {
							if(be.getButtonClicked().getItemId().equals(Dialog.YES)){
								parentWindow.hide();
							}else{
								be.cancelBubble();
							}
						}
					});
					return;
				}
				sourceNodeGrid.removeData(defaultNodeList);
			}
			
		});
		
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		add(removeButton,td);
		sourceNodeGrid = new SourceNodeGrid(TABLE_HIGHT,TABLE_WIDTH);
		add(sourceNodeGrid);
	}
		
	private void refreshData()
	{
		parentWindow.mask(UIContext.Constants.loading());
		service.getNodeProtectedState(serviceInfo, defaultNodeList, new BaseAsyncCallback<List<NodeModel>>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				//Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,"Failed to call web service to get protected node list!");
				refreshSourceTable();
				parentWindow.unmask();
			}

			@Override
			public void onSuccess(List<NodeModel> result) {
				refreshSourceTable();
				parentWindow.unmask();
			}});
	}
	
	private void refreshSourceTable()
	{
		if ( defaultNodeList != null && defaultNodeList.size() > 0 )
		{
			if ( sourceNodeGrid.getCountOfItems() > 0 )
			{
				sourceNodeGrid.removeAllData();
			}
			sourceNodeGrid.addData(this.defaultNodeList);
		}
	}
	
	public void refresh()
	{
		sourceNodeGrid.addData(parentWindow.standbyModel.getSourceNodeList());
	}
	
	public boolean validate(){
		if(sourceNodeGrid.getData().size() == 0){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.specifyAtLeastOneNode());
			return false;
		}
		return true;
	}
	
	public void save(){
		parentWindow.standbyModel.setSourceNodeList(sourceNodeGrid.getData());
	}

}
