package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseComboBox;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.Image;

public class BackupServer extends LayoutContainer {
	//private D2DServerServiceAsync d2dServerService = GWT.create(D2DServerService.class); 
	private BaseComboBox<ServiceInfoModel> d2dServers;
	private ListStore<ServiceInfoModel> serversStore = null;
	//private ServiceInfoModel originalServer = null;
	private BackupWizardPanel parentWindow;
	private FieldSet notificationSet;
	private FieldSet notificationSet2;
	private FieldSet notificationSet3;
		
	/**
	 * 
	 */
	public BackupServer(BackupWizardPanel parent){
		parentWindow = parent;
		/*if (parentWindow != null)
		{
			originalServer = parentWindow.getD2DServerInfo();
		}*/
		
		TableLayout layout = new TableLayout(2);
		layout.setWidth("97%");
		layout.setCellPadding(5);
		layout.setCellPadding(5);
		setLayout(layout);

		LabelField label = new LabelField(UIContext.Constants.backupServerHint());
		label.setStyleAttribute("font-weight", "bold");
		TableData layoutData = new TableData();
		layoutData.setColspan(2);
		add(label, layoutData);
		
		serversStore = new ListStore<ServiceInfoModel>();
		serversStore.add(parent.getD2DServerInfo());
//		serversStore.setDefaultSort("server", SortDir.ASC);
		d2dServers = new BaseComboBox<ServiceInfoModel>();
		d2dServers.setStore(serversStore);
		d2dServers.setDisplayField("server");
		d2dServers.setEditable(false);
		d2dServers.setEnabled(false);
		d2dServers.addSelectionChangedListener(new SelectionChangedListener<ServiceInfoModel>(){

			@Override
			public void selectionChanged(
					SelectionChangedEvent<ServiceInfoModel> se) {
				ServiceInfoModel d2dServer=se.getSelectedItem();
				parentWindow.refresh(d2dServer);
			}
			
		});
		label = new LabelField(UIContext.Constants.d2dServer());
		add(label);
		add(d2dServers);
		
		defineNotificationSet();
		add(notificationSet, layoutData);
		
		defineNotificationSet2();
		if ( notificationSet2 != null ) {
			add(notificationSet2, layoutData);
		}
		
		defineNotificationSet3();
		if ( notificationSet3 != null ) {
			add(notificationSet3, layoutData);
		}		
		initCmbBackupServer();
	}
	
	private void defineNotificationSet(){
		notificationSet = new FieldSet();
		notificationSet.setHeading(UIContext.Constants.information());
		notificationSet.setCollapsible(true);
		notificationSet.setAutoHeight(true);
		notificationSet.setStyleAttribute("margin-top", "20px");
		TableLayout warningLayout = new TableLayout();
		warningLayout.setWidth("98%");
		warningLayout.setCellSpacing(5);
		warningLayout.setColumns(2);
		notificationSet.setLayout(warningLayout);
		
		Image warningImage = new Image(UIContext.IconBundle.information());
		TableData tableData = new TableData();
		tableData.setStyle("padding: 5px 3px 3px 0px;");
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		notificationSet.add(warningImage, tableData);
		LabelField note=new LabelField(UIContext.Constants.backupServerNote());
		//note.setWidth(550);
		notificationSet.add(note);
	}
	
	private void defineNotificationSet2(){
		if(parentWindow.isModify()){
			notificationSet2 = new FieldSet();
			notificationSet2.setHeading(UIContext.Constants.information());
			notificationSet2.setCollapsible(true);
			notificationSet2.setAutoHeight(true);
			notificationSet2.setStyleAttribute("margin-top", "20px");
			TableLayout warningLayout = new TableLayout();
			warningLayout.setWidth("98%");
			warningLayout.setCellSpacing(5);
			warningLayout.setColumns(2);
			notificationSet2.setLayout(warningLayout);
			
			Image warningImage = new Image(UIContext.IconBundle.information());
			TableData tableData = new TableData();
			tableData.setStyle("padding: 5px 3px 3px 0px;");
			tableData.setVerticalAlign(VerticalAlignment.TOP);
			notificationSet2.add(warningImage, tableData);
			LabelField note=new LabelField(UIContext.Constants.modifyRunningBackupJobConfirmMessage());		
			notificationSet2.add(note);
		}
	}
	
	public void defineNotificationSet3(){
		notificationSet3 = new FieldSet();
		notificationSet3.setHeading(UIContext.Constants.notification());
		notificationSet3.setCollapsible(true);
		notificationSet3.setAutoHeight(true);
		notificationSet3.setStyleAttribute("margin-top", "20px");
		TableLayout warningLayout = new TableLayout();
		warningLayout.setWidth("98%");
		warningLayout.setCellSpacing(5);
		warningLayout.setColumns(2);
		notificationSet3.setLayout(warningLayout);
		
		Image warningImage = new Image(UIContext.IconBundle.warning());
		TableData tableData = new TableData();
		tableData.setStyle("padding: 5px 3px 3px 0px;");
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		notificationSet3.add(warningImage, tableData);
		LabelField note=new LabelField(UIContext.Constants.multipleNodeModidyWarning());
		//note.setWidth(550);
		notificationSet3.add(note);
		notificationSet3.hide();
	}
	
	public void addMultipleNodeWarning(){
		if ( notificationSet3 != null ) {
			notificationSet3.show();
		}
	}
	
	private void initCmbBackupServer() {
		d2dServers.setValue(parentWindow.getD2DServerInfo());
		/*d2dServerService.getD2DServerList(new BaseAsyncCallback<List<ServiceInfoModel>>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				
			}

			@Override
			public void onSuccess(List<ServiceInfoModel> result) {
				if (result != null && result.size() > 0)
				{
					for (ServiceInfoModel server:result)
					{
						serversStore.add(server);
					}
					
					if ( originalServer == null )
					{
						d2dServers.setValue(result.get(0));
					}
					else
					{
						d2dServers.setValue(originalServer);
					}
				}
			}});*/
	}
	
	public ServiceInfoModel GetBackupServer()
	{
		if(d2dServers == null)
			return null;
		
		return d2dServers.getValue();
	}
	
	public void refresh()
	{	if(parentWindow.isModify()){
			d2dServers.disable();
			if(parentWindow.backupModel.getTargetList().size()>1){
				this.addMultipleNodeWarning();
			}
		}
		if (parentWindow != null)
		{
			ServiceInfoModel backupServer = parentWindow.getD2DServerInfo();
			d2dServers.setValue(backupServer);
			
		}
	}

}
