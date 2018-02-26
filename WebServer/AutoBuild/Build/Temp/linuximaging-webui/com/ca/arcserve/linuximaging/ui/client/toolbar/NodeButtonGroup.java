package com.ca.arcserve.linuximaging.ui.client.toolbar;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.AddServerDialog;
import com.ca.arcserve.linuximaging.ui.client.homepage.node.AddNodeFromScript;
import com.ca.arcserve.linuximaging.ui.client.homepage.node.DeleteNodeDialog;
import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.google.gwt.user.client.Element;

public class NodeButtonGroup extends ButtonGroup {

	private Button addNode;
	private Button deleteNode;
	private Button modifyNode;
	private ToolBarPanel toolBar;
	public NodeButtonGroup(int columns,int height) {
		super(columns);
		setHeight(height);
		setHeading(UIContext.Constants.toolBar_node());
	}
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		TableData data = new TableData();  
	    data.setRowspan(2);
		addNode = new Button(UIContext.Constants.toolBar_add());
//		addNode.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.create()));
		addNode.setIcon(UIContext.IconHundle.create());
		addNode.setScale(ButtonScale.LARGE);  
	    addNode.setIconAlign(IconAlign.TOP);
	    addNode.setArrowAlign(ButtonArrowAlign.RIGHT);
	    addNode.disable();
	    addNode.setMinWidth(50);
	    

	    Menu menu = new Menu();
	    MenuItem addByIp = new MenuItem(UIContext.Constants.toolBarAddNodeByHostname());
	    addByIp.addSelectionListener(new  SelectionListener<MenuEvent>(){

			@Override
			public void componentSelected(MenuEvent ce) {
				AddServerDialog addNodeDlg = new AddServerDialog(UIContext.Constants.addNode(), toolBar.tabPanel.currentServer);
				addNodeDlg.setToolBar(toolBar);
				addNodeDlg.show();
			}
			
		});
	    MenuItem addByDiscovery = new MenuItem(UIContext.Constants.toolBarAddNodeByDiscovery());
	    addByDiscovery.addSelectionListener(new  SelectionListener<MenuEvent>(){

			@Override
			public void componentSelected(MenuEvent ce) {
				AddNodeFromScript addNodeDlg = new AddNodeFromScript(UIContext.Constants.nodeDiscoveryTitle(), toolBar.tabPanel.currentServer);
				addNodeDlg.show();
			}
			
		});
	    menu.add(addByIp);
	    menu.add(addByDiscovery);
	    addNode.setMenu(menu);
	    
	    modifyNode = new Button(UIContext.Constants.toolBar_modify());  
//		modifyNode.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.modify()));
		modifyNode.setIcon(UIContext.IconHundle.modify());
		modifyNode.disable();
		modifyNode.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				toolBar.tabPanel.nodeTable.modifyNode();
			}});
		deleteNode = new Button(UIContext.Constants.toolBar_delete()); 
//		deleteNode.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.delete()));
		deleteNode.setIcon(UIContext.IconHundle.delete());
		deleteNode.disable();
		deleteNode.addSelectionListener(new  SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {				
				DeleteNodeDialog dl = new DeleteNodeDialog(toolBar.tabPanel.nodeTable);
				dl.show();
			}
			
		});
		add(addNode,data);
		add(modifyNode);
		add(deleteNode);
	}
	public void enableModifyAndDelete() {
		if(!UIContext.isRestoreMode){
			modifyNode.enable();
			deleteNode.enable();
		}
	}
	
	public void enableDelete(){
		if(!UIContext.isRestoreMode){
			modifyNode.disable();
			deleteNode.enable();
		}
	}
	
	public void setToolBar(ToolBarPanel toolBarPanel) {
		this.toolBar=toolBarPanel;
	}
	
	public void setDefaultState(){
		if(!UIContext.isRestoreMode){
			addNode.enable();
			modifyNode.disable();
			deleteNode.disable();
		}
	}
	
	public void setAllEnabled(boolean isEnabled){
		addNode.setEnabled(isEnabled);
		modifyNode.setEnabled(isEnabled);
		deleteNode.setEnabled(isEnabled);
	}
}
