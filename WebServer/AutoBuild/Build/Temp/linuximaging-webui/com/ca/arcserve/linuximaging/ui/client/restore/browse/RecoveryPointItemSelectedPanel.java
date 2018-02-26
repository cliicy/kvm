package com.ca.arcserve.linuximaging.ui.client.restore.browse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.FlashCheckBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.CatalogModelType;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class RecoveryPointItemSelectedPanel extends LayoutContainer {
	
	public final static int ITEM_TABLE_HIGHT = 200;
	private ListStore<GridTreeNode> fileStore;
	private BaseGrid<GridTreeNode> grid;
	private BrowseRecoveryPointPanel parentPanel; 
	
	public RecoveryPointItemSelectedPanel(BrowseRecoveryPointPanel parentPanel){
		this.parentPanel = parentPanel;
		ColumnModel cm = initColumnModel();

		StoreSorter<GridTreeNode> sorter = RestoreUtils.initStoreSorter();
		fileStore = new ListStore<GridTreeNode>();
		fileStore.setStoreSorter(sorter);

		grid = new BaseGrid<GridTreeNode>(fileStore, cm);
		grid.setHeight(ITEM_TABLE_HIGHT - 75);
		grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		grid.setBorders(true);
		grid.setTrackMouseOver(true);
		grid.setAutoExpandColumn("path");
		grid.setAutoExpandMax(5000);
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
	    panel.setBodyBorder(false);
	    panel.setBorders(false);
	    panel.setLayout(new FillLayout());
	    panel.add(grid);
	    panel.setHeight(ITEM_TABLE_HIGHT - 50);
	    
	    LayoutContainer container = new LayoutContainer();
	    container.setWidth("100%");
	    TableLayout layout = new TableLayout(2); 
	    layout.setWidth("100%");
	    container.setLayout(layout);
	    
	    TableData tdLeft = new TableData();
	    tdLeft.setHorizontalAlign(HorizontalAlignment.LEFT);
	    tdLeft.setWidth("50%");
	    
	    TableData tdRight = new TableData();
	    tdRight.setHorizontalAlign(HorizontalAlignment.RIGHT);
	    tdRight.setWidth("50%");
	    
	    container.add(new LabelField(UIContext.Constants.restoreFileFolderBeRestore()),tdLeft);
	    Button removeButton = new Button(UIContext.Constants.restoreRemove());
	    removeButton.setMinWidth(70);
	    removeButton.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.delete_button()));
		removeButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				List<GridTreeNode> nodesSelectedToRemove = grid.getSelectionModel().getSelectedItems();
				for(GridTreeNode node : nodesSelectedToRemove){
					fileStore.remove(node);
					deselectItemGrid(node);
				}
			}
			
		});
	    container.add(removeButton,tdRight);
	    panel.setTopComponent(container);
	    add(panel);
		
	}
	
	public void deselectItemGrid(GridTreeNode node){
		parentPanel.deselectItemGridNode(node);
	}
	
	public void refresh(GridTreeNode node){
		if(node.getSelectState() == FlashCheckBox.FULL){
			//deselect all the children
			List<GridTreeNode> children = new ArrayList<GridTreeNode>();
			for(int i = 0 ;i < fileStore.getCount();i++){
				GridTreeNode tmp = fileStore.getAt(i);
				if(tmp.getCatalogFilePath().equals(node.getCatalogFilePath()) || tmp.getCatalogFilePath().startsWith(node.getCatalogFilePath()+ UIContext.FILE_SEPATATOR)){
					children.add(tmp);
				}
			}
			for(GridTreeNode child : children){
				fileStore.remove(child);
			}
			fileStore.add(node);
		}else{
			fileStore.remove(node);
		}
		
	}
	
	public void addSelectedList(List<GridTreeNode> selectedList){
		for(GridTreeNode node : selectedList){
			if(!fileStore.contains(node)){
				fileStore.add(node);
			}
		}
		//fileStore.add(selectedList);
	}
	
	public void removeSelectedList(List<GridTreeNode> selectedList){
		for(GridTreeNode node : selectedList){
			fileStore.remove(node);
		}
	}
	
	public boolean isParentSelected(GridTreeNode node){
		boolean exist = false;
		for(int i = 0 ;i < fileStore.getCount();i++){
			GridTreeNode tmp = fileStore.getAt(i);
			String path = tmp.getCatalogFilePath()+UIContext.FILE_SEPATATOR;
			if(!node.getCatalogFilePath().equals(tmp.getCatalogFilePath()) && node.getCatalogFilePath().startsWith(path)){
				exist = true;
				break;
			}
		}
		return exist;
	}
	
	public boolean isSelected(GridTreeNode node){
		return fileStore.contains(node);
	}
	
	public boolean isSelected(String path){
		for(GridTreeNode node :fileStore.getModels()){
			if(node.getCatalogFilePath().equals(path)){
				return true;
			}
		}
		return false;
	}
	
	private ColumnModel initColumnModel() {

		ColumnConfig name = new ColumnConfig("path", UIContext.Constants.browseFileOrFolderName(), 430);
		name.setMenuDisabled(true);
		name.setRenderer(new GridCellRenderer<BaseModelData>() {

			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex, 
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				LayoutContainer lc = new LayoutContainer();
				
				TableLayout layout = new TableLayout();
				layout.setColumns(2);
				lc.setLayout(layout);
				final GridTreeNode node = (GridTreeNode) model;
				
				lc.add(RestoreUtils.getNodeIcon(node));
				lc.add(new LabelField(node.getCatalogFilePath()));
				lc.addListener(Events.OnClick, new Listener<BaseEvent>(){

					@Override
					public void handleEvent(BaseEvent be) {
						if ( RecoveryPointItemSelectedPanel.this.grid != null ) {
							RecoveryPointItemSelectedPanel.this.grid.getSelectionModel().select(node, true);
						}
				}});
				
				return lc;
			}

		});
		
		ColumnConfig date = new ColumnConfig("date", UIContext.Constants.restoreDateModifiedColumn(), 140);
		date.setRenderer(new GridCellRenderer<BaseModelData>() {
			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex, 
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				try
				{
					if (model != null)
					{
						LabelField label = new LabelField();

						label.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");
						label.setStyleAttribute("white-space", "nowrap");

						Date dateModifed = ((GridTreeNode) model).getDate();
						String strDate = Utils.formatDateToServerTime(dateModifed, 
								((GridTreeNode)model).getServerTZOffset() != null ? 
										((GridTreeNode)model).getServerTZOffset() : 0);

						if (strDate != null && strDate.trim().length() > 0)
						{
							label.setValue(strDate);
							label.setTitle(strDate);
							return label;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
                      System.out.println("Error:" + e.getMessage());
				}
				return "";
			}
		});
		date.setMenuDisabled(true);
		
		ColumnConfig size = new ColumnConfig("size", UIContext.Constants.size(), 80);
		size.setMenuDisabled(true);
		size.setRenderer(new GridCellRenderer<GridTreeNode>() {

			@Override
			public Object render(GridTreeNode model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GridTreeNode> store, Grid<GridTreeNode> grid) {
				try {
					if (model!=null&&model.getType()==CatalogModelType.File &&model.getSize()!=null) {
						Long value = model.getSize();
						String formattedValue = Utils.bytes2String(value);
						return formattedValue;
					}
				} catch (Exception e) {

				}

				return "";
			}

		});

		return new ColumnModel(Arrays.asList(name,
				date, size));
	}
	
	public List<GridTreeNode> getSelectedNodes(){
		return fileStore.getModels();
	}

}
