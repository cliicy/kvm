package com.ca.arcserve.linuximaging.ui.client.toolbar;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage.AddBackupStorageWindow;
import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.user.client.Element;

public class BackupStorageButtonGroup extends ButtonGroup {
	
	private Button addStorage;
	private Button deleteStorage;
	private Button modifyStorage;
	private ToolBarPanel toolBar;

	public BackupStorageButtonGroup(int columns,int height) {
		super(columns);
		setHeight(height);
		setHeading(UIContext.Constants.toolBar_BackupStorage());
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		TableData data = new TableData();  
	    data.setRowspan(2);
	    addStorage = new Button(UIContext.Constants.toolBar_add());
	    addStorage.setStyleAttribute("font-weight", "bold");
//		addServer.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.create()));
	    addStorage.setIcon(UIContext.IconHundle.create());
	    addStorage.setScale(ButtonScale.LARGE);  
	    addStorage.setIconAlign(IconAlign.TOP);
	    addStorage.setArrowAlign(ButtonArrowAlign.RIGHT);
	    addStorage.setMinWidth(50);
	    addStorage.addSelectionListener(new  SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				AddBackupStorageWindow window = new AddBackupStorageWindow(toolBar.tabPanel,AddBackupStorageWindow.TYPE_ADD);
				window.setModal(true);
				window.show();
			}
			
		});
	    /*if(UIContext.isRestoreMode){
	    	addStorage.disable();
	    }*/
	    modifyStorage = new Button(UIContext.Constants.toolBar_modify());  
//		modifyServer.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.modify()));
	    modifyStorage.setIcon(UIContext.IconHundle.modify());
	    modifyStorage.disable();
	    modifyStorage.addSelectionListener(new  SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				AddBackupStorageWindow window = new AddBackupStorageWindow(toolBar.tabPanel,AddBackupStorageWindow.TYPE_MODIFY);
				window.refresh(toolBar.tabPanel.storagePanel.getSelectionModel());
				window.setModal(true);
				window.show();
			}
			
		});
	    deleteStorage = new Button(UIContext.Constants.toolBar_delete()); 
//		deleteServer.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.delete()));
	    deleteStorage.setIcon(UIContext.IconHundle.delete());
	    deleteStorage.disable();
	    deleteStorage.addSelectionListener(new  SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(toolBar.tabPanel.storagePanel.getSelectionModel() == null){
					Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Constants.selectOneBackupStorage());
				}else{
					MessageBox.confirm(UIContext.Constants.delete(), UIContext.Constants.deleteConfirmMessage(), new Listener<MessageBoxEvent>(){
	
						@Override
						public void handleEvent(MessageBoxEvent be) {
							if(be.getButtonClicked().getItemId().equals(Dialog.YES)){
								toolBar.tabPanel.storagePanel.deleteBackupLocation();
							}else{
								be.cancelBubble();
							}
						}
						
					}); 
				}
			}
			
		});
		add(addStorage,data);
		add(modifyStorage);
		add(deleteStorage);
	}

	public void setToolBar(ToolBarPanel toolBarPanel) {
		this.toolBar=toolBarPanel;
	}
	
	public void setModifyAndDeleteEnable(boolean isEnable){
		//if(!UIContext.isRestoreMode){
		modifyStorage.setEnabled(isEnable);
		//}
		deleteStorage.setEnabled(isEnable);
	}
	
	public void setDefaultState(){
		//if(!UIContext.isRestoreMode){
		addStorage.setEnabled(true);
		//}
		modifyStorage.setEnabled(false);
		deleteStorage.setEnabled(false);
	}
	
	public void setAllEnabled(boolean isEnabled){
		if(isEnabled == false || !UIContext.isRestoreMode){
			addStorage.setEnabled(isEnabled);
			modifyStorage.setEnabled(isEnabled);
			deleteStorage.setEnabled(isEnabled);
		}
		
	}
	
}
