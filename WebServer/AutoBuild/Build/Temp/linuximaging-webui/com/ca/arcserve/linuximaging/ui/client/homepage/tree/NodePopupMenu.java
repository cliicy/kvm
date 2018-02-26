package com.ca.arcserve.linuximaging.ui.client.homepage.tree;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.homepage.Homepagetree;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class NodePopupMenu{

	private Menu contextMenu = null;
	private Homepagetree nodeTree = null;
	
	public Homepagetree getNodeTree() {
		return nodeTree;
	}

	public void setNodeTree(Homepagetree nodeTree) {
		this.nodeTree = nodeTree;
	}
	
	public NodePopupMenu() {
		contextMenu = new Menu();   
		contextMenu.setWidth(140);   

		MenuItem addServer = new MenuItem();
		addServer.setText(UIContext.Constants.addServer());
		contextMenu.add(addServer);
		addServer.addSelectionListener(new SelectionListener<MenuEvent>() {   
			public void componentSelected(MenuEvent ce) { 
				NodeServerDialog addServerDlg = new NodeServerDialog(nodeTree, false);
				addServerDlg.show();
			}   
		});  
		
//		MenuItem caputerServer = new MenuItem();   
//		caputerServer.setText(uiConstants.captureServer());
//		caputerServer.addSelectionListener(new SelectionListener<MenuEvent>() {   
//			public void componentSelected(MenuEvent ce) {   
//				ComponentNodeCaptureServerDlg captureServerDlg = new ComponentNodeCaptureServerDlg();
//				captureServerDlg.setNodeTree(nodeTree);
//				captureServerDlg.show();
//			}   
//		});  
//		//caputerServer.setIcon(xxx);   
//		contextMenu.add(caputerServer);   

		MenuItem delete = new MenuItem();   
		delete.setText(UIContext.Constants.delete());
		delete.addSelectionListener(new SelectionListener<MenuEvent>() {   
			public void componentSelected(MenuEvent ce) {   
				nodeTree.deleteElement();
			}   
		});  
		contextMenu.add(delete);
	}

	public Menu getContextMenu()
	{
		return contextMenu;
	}
	
}
