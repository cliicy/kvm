package com.ca.arcserve.linuximaging.ui.client.toolbar;


import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.google.gwt.user.client.Element;

public class ToolButtonGroup extends ButtonGroup {

	private Button refresh;
	private ToolBarPanel toolBar;
	private Button filter;

	public ToolButtonGroup(int columns, int height) {
		super(columns);
		setHeight(height);
		setHeading(UIContext.Constants.toolBar_tool());
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		refresh = new Button(UIContext.Constants.toolBar_refresh());
//		refresh.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.refresh()));
		refresh.setIcon(UIContext.IconHundle.refresh());
		refresh.setScale(ButtonScale.LARGE);  
		refresh.setIconAlign(IconAlign.TOP);
		refresh.setArrowAlign(ButtonArrowAlign.RIGHT);
		refresh.setMinWidth(50);
		refresh.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				refresh.disable();
				if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.DASHBOARD)){
//					toolBar.tabPanel.dashboardTable.mask(UIContext.Constants.loading());
					toolBar.tabPanel.refreshDashboardTable();
				}else if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.NODE)){
//					toolBar.tabPanel.nodeTable.mask(UIContext.Constants.loading());
					toolBar.tabPanel.refreshNodeTable();
				}else if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.JOB_STATUS)){
//					toolBar.tabPanel.jobStatusTable.mask(UIContext.Constants.loading());
					toolBar.tabPanel.refreshJobStatusTable(true);
				}else if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.JOB_HISTORY)){
					//toolBar.tabPanel.historyTable.mask(UIContext.Constants.loading());
					toolBar.tabPanel.refreshJobHistoryTable(false);
				}else if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.JOB_LOG)){
					toolBar.tabPanel.refreshJobLogTable();
				}else if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.BACKUP_STORAGE)){
					toolBar.tabPanel.refreshBackupStorageTable();
				}
			}
			
		});
		
		filter = new Button(UIContext.Constants.toolBar_filter());
		filter.setIcon(UIContext.IconHundle.filter());
		filter.setScale(ButtonScale.LARGE);
		filter.setIconAlign(IconAlign.TOP);
		filter.setArrowAlign(ButtonArrowAlign.RIGHT);
		filter.setMinWidth(50);
		filter.disable();
		filter.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.NODE)){
					toolBar.tabPanel.nodeTable.showHideFilter();
				}else if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.JOB_STATUS)){
					toolBar.tabPanel.jobStatusTable.showHideFilter();
				}else if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.JOB_HISTORY)){
					toolBar.tabPanel.historyTable.showHideFilter();
				}else if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.JOB_LOG)){
					toolBar.tabPanel.activityLogTable.showHideFilter();
				}
			}});
		add(refresh); 
		add(filter);
	}

	public void setToolBar(ToolBarPanel toolBarPanel) {
		this.toolBar=toolBarPanel;
		
	}

	public void enableRefresh() {
		refresh.enable();
	}
	public void enableFilter() {
		if ( filter != null ) {
			filter.enable();
		}
	}
	public void disableFilter() {
		if ( filter != null ) {
			filter.disable();
		}
	}
	
	public void setDefaultState(){
		refresh.setEnabled(true);
		filter.setEnabled(false);
	}
	
	public void setAllEnabled(boolean isEnabled){
		refresh.setEnabled(isEnabled);
		filter.setEnabled(isEnabled);
	}
	
}
