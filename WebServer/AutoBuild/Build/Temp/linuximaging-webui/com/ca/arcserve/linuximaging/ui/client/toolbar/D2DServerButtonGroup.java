package com.ca.arcserve.linuximaging.ui.client.toolbar;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.homepage.tree.NodeServerDialog;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
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

public class D2DServerButtonGroup extends ButtonGroup {

	private Button addServer;
	private Button deleteServer;
	private Button modifyServer;
	private ToolBarPanel toolBar;
	public D2DServerButtonGroup(int columns,int height) {
		super(columns);
		setHeight(height);
		setHeading(UIContext.Constants.toolBar_D2DServer());
	}
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		TableData data = new TableData();  
	    data.setRowspan(2);
		addServer = new Button(UIContext.Constants.toolBar_add());
		addServer.setStyleAttribute("font-weight", "bold");
//		addServer.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.create()));
		addServer.setIcon(UIContext.IconHundle.create());
		addServer.setScale(ButtonScale.LARGE);  
	    addServer.setIconAlign(IconAlign.TOP);
	    addServer.setArrowAlign(ButtonArrowAlign.RIGHT);
	    addServer.setMinWidth(50);
		addServer.addSelectionListener(new  SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				NodeServerDialog addServerDlg = new NodeServerDialog(toolBar.nodeTree, false);
				addServerDlg.show();
			}
			
		});
		if(UIContext.isRestoreMode){
			addServer.disable();
		}
	    modifyServer = new Button(UIContext.Constants.toolBar_modify());  
//		modifyServer.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.modify()));
	    modifyServer.setIcon(UIContext.IconHundle.modify());
		modifyServer.disable();
		modifyServer.addSelectionListener(new  SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				NodeServerDialog addServerDlg = new NodeServerDialog(toolBar.nodeTree, true);
				ServiceInfoModel model=toolBar.nodeTree.getSelectedItem();
				addServerDlg.refreshData(model);
				addServerDlg.show();
			}
			
		});
		deleteServer = new Button(UIContext.Constants.toolBar_delete()); 
//		deleteServer.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.delete()));
		deleteServer.setIcon(UIContext.IconHundle.delete());
		deleteServer.disable();
		deleteServer.addSelectionListener(new  SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				MessageBox.confirm(UIContext.Constants.delete(), UIContext.Constants.deleteConfirmMessage(), new Listener<MessageBoxEvent>(){

					@Override
					public void handleEvent(MessageBoxEvent be) {
						if(be.getButtonClicked().getItemId().equals(Dialog.YES)){
							toolBar.nodeTree.deleteElement();
						}else{
							be.cancelBubble();
						}
					}
					
				}); 
			}
			
		});
		add(addServer,data);
		add(modifyServer);
		add(deleteServer);
	}
	public void disableDelete() {
//		modifyServer.enable();
		deleteServer.disable();
	}
	public void disableModifyAndDelete() {
		modifyServer.disable();
		deleteServer.disable();
	}
	public void enableModifyAndDelete() {
		if(!UIContext.isRestoreMode){
			modifyServer.enable();
			deleteServer.enable();
		}
	}
	public void disableAll() {
		addServer.disable();
		modifyServer.disable();
		deleteServer.disable();
	}
	public void enableAll() {
		if(!UIContext.isRestoreMode){
			addServer.enable();
			modifyServer.enable();
			deleteServer.enable();
		}
	}
	public void setToolBar(ToolBarPanel toolBarPanel) {
		this.toolBar=toolBarPanel;
	}

}
