package com.ca.arcserve.linuximaging.ui.client.homepage.dashboard;

import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.DashboardModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class DashboardTable extends LayoutContainer {
	protected HomepageTab parentTabPanel;
	private ServerInformationPanel serverInformation;
	private ResourceUsagePanel resourceUsage;
	private BackupStoragePanel backupStorage;
	private NodeSummaryPanel nodeSummary;
	private JobSummaryPanel jobSummary;
	private int CELL_SPACE=2;

	public DashboardTable(HomepageTab homepageTab){
		this.parentTabPanel=homepageTab;
		this.setScrollMode(Scroll.AUTOY);
		RowLayout rowLayout = new RowLayout(Orientation.VERTICAL);
		rowLayout.setAdjustForScroll(true);
		this.setLayout(rowLayout);		
		LayoutContainer serverSummary=defineServerSummaryPanel();
		this.add(serverSummary, new RowData(1, -1, new Margins(0, 0, 0, 3)));
		
	    backupStorage=new BackupStoragePanel();
	    this.add(backupStorage, new RowData(1, -1, new Margins(4, 2, 0, 5)));
	    
	    LayoutContainer chartSummary=defineChartSummaryPanel();
	    this.add(chartSummary, new RowData(1, -1, new Margins(4, 0, 0, 3)));
//		nodeSummary=new NodeSummaryPanel();
//		this.add(nodeSummary, new RowData(1, -1, new Margins(4, 0, 0, 0)));		
//		jobSummary=new JobSummaryPanel();
//		this.add(jobSummary, new RowData(1, -1, new Margins(4, 0, 0, 0)));	
		
	}
	private LayoutContainer CreateSummaryContainer() {
		LayoutContainer summary=new LayoutContainer();
		TableLayout layout = new TableLayout();
	    layout.setColumns(2);
	    layout.setCellSpacing(CELL_SPACE);
	    layout.setWidth("100%");
	    summary.setLayout(layout);
		return summary;
	}
	
	private LayoutContainer defineChartSummaryPanel() {
		LayoutContainer summary = CreateSummaryContainer();
	    
	    TableData tdLeft = new TableData();
		tdLeft.setWidth("49.1%");
		tdLeft.setHorizontalAlign(HorizontalAlignment.LEFT);
		tdLeft.setVerticalAlign(VerticalAlignment.TOP);
		TableData tdRight = new TableData();
		tdRight.setWidth("50.9%");
		tdRight.setHorizontalAlign(HorizontalAlignment.LEFT);
		tdRight.setVerticalAlign(VerticalAlignment.TOP);
		
		int height = 210;
		nodeSummary=new NodeSummaryPanel();
		nodeSummary.setHeight(height);
	    summary.add(nodeSummary, tdLeft);
	    jobSummary=new JobSummaryPanel(this);
	    jobSummary.setHeight(height);
	    summary.add(jobSummary, tdRight);
		return summary;
	}
	
	private LayoutContainer defineServerSummaryPanel() {
		LayoutContainer summary = CreateSummaryContainer();
		
	    TableData tdLeft = new TableData();
		tdLeft.setWidth("50%");
		tdLeft.setHorizontalAlign(HorizontalAlignment.LEFT);
		tdLeft.setVerticalAlign(VerticalAlignment.TOP);
		TableData tdRight = new TableData();
		tdRight.setWidth("50%");
		tdRight.setHorizontalAlign(HorizontalAlignment.LEFT);
		tdRight.setVerticalAlign(VerticalAlignment.TOP);
		
	    int height=170;
		serverInformation=new ServerInformationPanel();
	    serverInformation.setHeight(height);
	    summary.add(serverInformation, tdLeft);
	    resourceUsage=new ResourceUsagePanel();
	    resourceUsage.setHeight(height);
	    summary.add(resourceUsage, tdRight);
		return summary;
	}

	public void refreshTable() {
//		serverInformation.refreshData(null);
//		resourceUsage.refreshData(null);
//		backupStorage.refreshData(null);
//		nodeSummary.refreshData(null);
//		jobSummary.refreshData(null);
		
	}

	public void refreshData(DashboardModel dashboard) {
		if(dashboard==null){
			return;
		}
		serverInformation.refreshData(dashboard.getServerInformation());
		resourceUsage.refreshData(dashboard.getResourceUsage());
		backupStorage.refreshData(dashboard.getBackupStorages());
		nodeSummary.refreshData(dashboard.getNodeSummary());
		jobSummary.resetViewText();
		jobSummary.refreshData(dashboard.getJobSummary());
		
	}
	
	public void resetData(){
		serverInformation.resetData();
		resourceUsage.resetData();
		backupStorage.resetData();
		nodeSummary.resetData();
		jobSummary.resetData();
	}

}
