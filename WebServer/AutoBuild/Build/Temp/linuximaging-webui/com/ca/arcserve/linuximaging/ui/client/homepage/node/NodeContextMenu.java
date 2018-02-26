package com.ca.arcserve.linuximaging.ui.client.homepage.node;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.backup.wizard.BackupWizardPanel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class NodeContextMenu{
	
	private static int MENU_WIDTH = 140;
	private Menu contextMenu = null;
	private NodeTable nodeTable;

	
	public NodeContextMenu(NodeTable node) {
		nodeTable = node;
		
		contextMenu = new Menu();   
		contextMenu.setWidth(MENU_WIDTH);   

		MenuItem backup = new MenuItem();
		backup.setText(UIContext.Constants.backup());
		contextMenu.add(backup);
		backup.addSelectionListener(new SelectionListener<MenuEvent>() {   
			public void componentSelected(MenuEvent ce) {
				BackupWizardPanel backupWindow;
				if ( nodeTable != null )
				{
					ServiceInfoModel backupServer = nodeTable.getBackupServer();
					List<NodeModel> sourceList = nodeTable.getSelectedItems();
					backupWindow = new BackupWizardPanel(backupServer, sourceList,false,UIContext.Constants.backupWizard());					
				}
				else
				{
					backupWindow = new BackupWizardPanel(null,false,UIContext.Constants.backupWizard());
				}
				backupWindow.setHomepageTab(nodeTable.getHomePageTab());
				backupWindow.setModal(true);
				backupWindow.show();
			}   
		});  
		

		MenuItem delete = new MenuItem();   
		delete.setText(UIContext.Constants.delete());
		delete.addSelectionListener(new SelectionListener<MenuEvent>() {   
			public void componentSelected(MenuEvent ce) {   
				//nodeTable.deleteNode();
				DeleteNodeDialog dl = new DeleteNodeDialog(nodeTable);
				dl.show();
			}   
		});  
		contextMenu.add(delete);
	}

	public Menu getContextMenu()
	{
		return contextMenu;
	}
	
	
}
