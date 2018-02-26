package com.ca.arcserve.linuximaging.ui.client.main;

import com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus.ComponentJobStatus;
import com.ca.arcserve.linuximaging.ui.client.components.backup.node.ComponentNode;
import com.ca.arcserve.linuximaging.ui.client.toolbar.ToolBarPanel;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.Element;

public class HomepagePanel extends LayoutContainer {

	@Override
	protected void onRender(Element parent, int pos){
		super.onRender(parent, pos);
		setLayout(new BorderLayout());
		
		ToolBarPanel toolBarPanel = new ToolBarPanel();  
//	    ContentPanel west = new ContentPanel();  
//	    ContentPanel center = new ContentPanel(); 
		LayoutContainer D2DHomePageTab=defineD2DHomePageTab();
//		D2DHomePageTab.setToolBar(toolBarPanel.getToolBar());
		
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 85);  
	    northData.setCollapsible(true);  
	    northData.setFloatable(true);  
	    northData.setHideCollapseTool(true);  
	    northData.setSplit(true);  
	    northData.setMargins(new Margins(0)); 
	    
	    BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(0));
	   
	    
		
	    add(toolBarPanel, northData); 
//	    add(west, westData);  
//	    add(center, centerData);
	    add(D2DHomePageTab, centerData); 

	}

	private LayoutContainer defineD2DHomePageTab() {
		LayoutContainer D2DHomePageTab =new LayoutContainer();
		D2DHomePageTab.setLayout(new BorderLayout());
		D2DHomePageTab.setHeight(500);
		ComponentNode tree = new ComponentNode();
		ComponentJobStatus componentJobStatus = new ComponentJobStatus();
		tree.setComponentJobStatus(componentJobStatus);
		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 150);  
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(0,0,0,0)); 
	    
	    BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(0));
	    
	    D2DHomePageTab.add(tree, westData);
	    D2DHomePageTab.add(componentJobStatus,centerData);
		return D2DHomePageTab;
	}
}
