package com.ca.arcserve.linuximaging.ui.client.standby;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

import java.util.ArrayList;
import java.util.List;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.Style.SelectionMode;

public class SourceNodeGrid extends LayoutContainer {
	
	private ListStore<NodeModel> gridStore;
	public ColumnModel columnsModel;
	private Grid<NodeModel> sourceInfoGrid;
	private final int COLUMN_LENGTH=100;
	private ServiceInfoModel serviceInfo;
	
	public SourceNodeGrid(int TABLE_HIGHT,int TABLE_WIDTH){
		TableLayout layout = new TableLayout();
		layout.setColumns(1);
		layout.setWidth("100%");
		this.setLayout(layout);
		gridStore = new ListStore<NodeModel>();
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();	
	    configs.add(Utils.createColumnConfig("server", UIContext.Constants.toolBarAddNodeByHostname(),COLUMN_LENGTH+140,null));	  
	   
	    columnsModel = new ColumnModel(configs);
	    sourceInfoGrid = new BaseGrid<NodeModel>(gridStore, columnsModel);
	    sourceInfoGrid.setTrackMouseOver(true);
	    sourceInfoGrid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
	    sourceInfoGrid.setBorders(true);
	    sourceInfoGrid.setHeight(TABLE_HIGHT);
	    sourceInfoGrid.setWidth(TABLE_WIDTH);
	    add(sourceInfoGrid);
	}
	
	public void addData(List<NodeModel> list){
		if ( list != null && list.size() > 0 )
		{
			for(NodeModel info : list)
			{
				gridStore.add(info);
			}
			sourceInfoGrid.reconfigure(gridStore, columnsModel);
		}
		
	}
	
	public void addData(NodeModel info){
		if ( info != null )
		{
			for (int i = 0; i < gridStore.getCount(); ++i)
			{
				NodeModel server = gridStore.getAt(i);
				if (server != null && server.getServerName().equalsIgnoreCase(info.getServerName()))
				{
					server.setUserName(info.getUserName());
					server.setPasswd(info.getPasswd());
					server.connInfo = info.connInfo;
					gridStore.update(server);
					sourceInfoGrid.reconfigure(gridStore, columnsModel);
					return;
				}
			}
			
			gridStore.add(info);
			sourceInfoGrid.reconfigure(gridStore, columnsModel);
		}
		
	}
	
	
	
	public void removeData(List<NodeModel> serverList){
		List<NodeModel> infoList = sourceInfoGrid.getSelectionModel().getSelectedItems();
		if ( infoList != null && infoList.size() > 0 )
		{
			for ( NodeModel info : infoList )
			{
				serverList.remove(info);
				gridStore.remove(info);
			}
			sourceInfoGrid.reconfigure(gridStore, columnsModel);
		}
	}
	
	public void removeAllData(){
		gridStore.removeAll();
		sourceInfoGrid.reconfigure(gridStore, columnsModel);
	}
	
	public int getCountOfItems()
	{
		return gridStore.getCount();
	}
	
	public List<NodeModel> getData(){
		return gridStore.getModels();
	}
	
	public List<NodeModel> getSelectedItems() {
		return sourceInfoGrid.getSelectionModel().getSelectedItems();
	}

	public ServiceInfoModel getServiceInfo() {
		return serviceInfo;
	}

	public void setServiceInfo(ServiceInfoModel serviceInfo) {
		this.serviceInfo = serviceInfo;
	}
	
}
