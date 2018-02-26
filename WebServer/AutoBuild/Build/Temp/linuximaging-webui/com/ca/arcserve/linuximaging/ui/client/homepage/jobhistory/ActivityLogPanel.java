package com.ca.arcserve.linuximaging.ui.client.homepage.jobhistory;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.homepage.activitylog.ActivityLogColumn;
import com.ca.arcserve.linuximaging.ui.client.model.ActivityLogModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class ActivityLogPanel extends LayoutContainer {
	private ListStore<ActivityLogModel> store;
	private Grid<ActivityLogModel> grid;
	private ColumnModel columnModel;
	private ContentPanel content;
	
	public ActivityLogPanel(){
		//this.setHeading(UIContext.Constants.jobLog());
		this.setLayout(new FitLayout());

		content = new ContentPanel();
		content.setHeaderVisible(false);
		content.setLayout(new FitLayout());
		//content.setHeading(UIContext.Constants.jobLog());
		//content.setCollapsible(true);
		store = new ListStore<ActivityLogModel>();

		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(ActivityLogColumn.Type.getColumnConfig());
		columns.add(ActivityLogColumn.Time.getColumnConfig());
		columns.add(ActivityLogColumn.Message.getColumnConfig());

		columnModel = new ColumnModel(columns);
		grid = new Grid<ActivityLogModel>(store, columnModel);
		grid.setAutoExpandColumn("message");
		grid.setAutoExpandMax(1000);
		grid.setAutoExpandMin(300);
		content.add(grid);
		this.add(content);
	}
	
	public void addData(List<ActivityLogModel> logs) {
		if ( logs == null ) return;
		store.removeAll();
		store.add(logs);
		grid.reconfigure(store, columnModel);
	}
	
	public void clearData(){
		store.removeAll();
		grid.reconfigure(store, columnModel);
	}
}
