package com.ca.arcserve.linuximaging.ui.client.standby.physical;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.PhysicalStandbyMachineModel;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class PhysicalStandbyMachinesGrid extends LayoutContainer {
	
	private Grid<PhysicalStandbyMachineModel> standbyMachineGrid;
	public ColumnModel columnsModel;
	private ListStore<PhysicalStandbyMachineModel> gridStore;

	public PhysicalStandbyMachinesGrid(int TABLE_HIGHT,int TABLE_WIDTH){
		TableLayout layout = new TableLayout();
		layout.setColumns(1);
		layout.setWidth("100%");
		this.setLayout(layout);
		gridStore = new ListStore<PhysicalStandbyMachineModel>();
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();	
	    
	    GridCellRenderer<PhysicalStandbyMachineModel> mac = new GridCellRenderer<PhysicalStandbyMachineModel>(){

			@Override
			public Object render(final PhysicalStandbyMachineModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<PhysicalStandbyMachineModel> store, Grid<PhysicalStandbyMachineModel> grid) {
				if (model == null) return null;
				return model.getMacAddress();
				
			}};
		ColumnConfig macConfig = Utils.createColumnConfig("mac", UIContext.Constants.macAddress(),200,mac);
	    configs.add(macConfig);
	    
	    columnsModel = new ColumnModel(configs);
	    standbyMachineGrid = new BaseGrid<PhysicalStandbyMachineModel>(gridStore, columnsModel);
	    standbyMachineGrid.setTrackMouseOver(true);
	    standbyMachineGrid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
	    standbyMachineGrid.setBorders(true);
	    standbyMachineGrid.setHeight(TABLE_HIGHT);
	    standbyMachineGrid.setWidth(TABLE_WIDTH);
	    add(standbyMachineGrid);
	}
	
	public void addData(PhysicalStandbyMachineModel machineModel){
		gridStore.add(machineModel);
		standbyMachineGrid.reconfigure(gridStore, columnsModel);
	}
	
	public void addData(List<PhysicalStandbyMachineModel> machineList){
		if(machineList != null){
			gridStore.add(machineList);
			standbyMachineGrid.reconfigure(gridStore, columnsModel);
		}
	}
	
	public List<PhysicalStandbyMachineModel> getData(){
		return gridStore.getModels();
	}
	
	public void removeSelectedData(){
		List<PhysicalStandbyMachineModel> infoList = standbyMachineGrid.getSelectionModel().getSelectedItems();
		if ( infoList != null && infoList.size() > 0 )
		{
			for ( PhysicalStandbyMachineModel info : infoList )
			{
				gridStore.remove(info);
			}
			standbyMachineGrid.reconfigure(gridStore, columnsModel);
		}
	}
	
	public void removeAllData(){
		gridStore.removeAll();
		standbyMachineGrid.reconfigure(gridStore, columnsModel);
	}
}
