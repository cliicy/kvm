package com.ca.arcserve.linuximaging.ui.client.homepage;

import com.ca.arcserve.linuximaging.ui.client.toolbar.ToolBarPanel;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.Element;

public class HomepagePanel extends Viewport {

	private Homepagetree homepageTree;
	private HomepageTab homepageTab;
	private ToolBarPanel toolBarPanel;

	@Override
	protected void onRender(Element parent, int pos){
		super.onRender(parent, pos);
		setLayout(new BorderLayout());
		
		toolBarPanel = new ToolBarPanel();  
//	    ContentPanel west = new ContentPanel();  
//	    ContentPanel center = new ContentPanel(); 
		LayoutContainer D2DHomePageTab=defineD2DHomePageTab();
//		D2DHomePageTab.setToolBar(toolBarPanel.getToolBar());
		
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 114);  
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
	    toolBarPanel.initComponent(homepageTree,homepageTab);
	}


	private LayoutContainer defineD2DHomePageTab() {
		LayoutContainer D2DHomePageTab =new LayoutContainer();
		D2DHomePageTab.setLayout(new BorderLayout());
		D2DHomePageTab.setHeight(500);
		homepageTree = new Homepagetree();
		homepageTree.setToolBarPanel(toolBarPanel);
		homepageTab = new HomepageTab();
		homepageTab.setToolBarPanel(toolBarPanel);
		
		homepageTree.setHomepageTab(homepageTab);
//		componentJobStatus.setTreePanel(treePanel);
		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 150);  
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(0,0,0,0)); 
	    
	    BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(0));
	    
	    D2DHomePageTab.add(homepageTree, westData);
	    D2DHomePageTab.add(homepageTab,centerData);
		return D2DHomePageTab;
	}


	public void cancelTimer() {
		homepageTab.cancelTimer();
	}
}
