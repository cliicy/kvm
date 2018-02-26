package com.ca.arcserve.linuximaging.ui.client.components.backup.node;

import com.ca.arcserve.linuximaging.ui.client.components.backup.node.i18n.NodeUIConstants;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;

public class ComponentNodePopupMenu{

	private static final NodeUIConstants uiConstants = GWT.create(NodeUIConstants.class);
	private Menu contextMenu = null;
	private ComponentNode nodeTree = null;
	
	public ComponentNode getNodeTree() {
		return nodeTree;
	}

	public void setNodeTree(ComponentNode nodeTree) {
		this.nodeTree = nodeTree;
	}
	
	public ComponentNodePopupMenu() {
		contextMenu = new Menu();   
		contextMenu.setWidth(140);   

		MenuItem addServer = new MenuItem();
		addServer.setText(uiConstants.addServer());
		contextMenu.add(addServer);
		addServer.addSelectionListener(new SelectionListener<MenuEvent>() {   
			public void componentSelected(MenuEvent ce) { 
				ComponentNodeAddServerDlg addServerDlg = new ComponentNodeAddServerDlg();
				addServerDlg.setNodeTree(nodeTree);
				addServerDlg.show();
			}   
		});  
		
		MenuItem caputerServer = new MenuItem();   
		caputerServer.setText(uiConstants.captureServer());
		caputerServer.addSelectionListener(new SelectionListener<MenuEvent>() {   
			public void componentSelected(MenuEvent ce) {   
				ComponentNodeCaptureServerDlg captureServerDlg = new ComponentNodeCaptureServerDlg();
				captureServerDlg.setNodeTree(nodeTree);
				captureServerDlg.show();
			}   
		});  
		//caputerServer.setIcon(xxx);   
		contextMenu.add(caputerServer);   

		MenuItem delete = new MenuItem();   
		delete.setText(uiConstants.delete());
		delete.addSelectionListener(new SelectionListener<MenuEvent>() {   
			public void componentSelected(MenuEvent ce) {   
				nodeTree.DeleteElement();
			}   
		});  
		contextMenu.add(delete);
	}

	public Menu getContextMenu()
	{
		return contextMenu;
	}
	
	
}
