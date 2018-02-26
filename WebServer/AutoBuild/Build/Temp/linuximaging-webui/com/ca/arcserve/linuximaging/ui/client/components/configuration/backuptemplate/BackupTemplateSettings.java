package com.ca.arcserve.linuximaging.ui.client.components.configuration.backuptemplate;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.ConfigurationService;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.ConfigurationServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.UIContext;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.BackupTemplateModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BackupTemplateSettings extends LayoutContainer {

	final ConfigurationServiceAsync service = GWT.create(ConfigurationService.class);
	private Button addButton;
	private Button deleteButton;
	private HorizontalPanel headerPanel;
	private ListStore<BackupTemplateModel> gridStore;
	public ColumnModel backupTemplateColumnsModel;
	private Grid<BackupTemplateModel> BackupTemplateGrid;
	private LayoutContainer BackupTemplateSummaryPanel;
	

	public BackupTemplateSettings() {
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(1);
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		setLayout(layout);
		
		defineHeaderPanel();
		defineBackupTemplateSummary();
		add(headerPanel);
		add(new Html("<HR>"));
		add(BackupTemplateSummaryPanel);
	}

	private void defineBackupTemplateSummary() {
		gridStore = new ListStore<BackupTemplateModel>();
		//test data
//		for(int i=0;i<10;i++){
//			BackupTemplateModel model=new BackupTemplateModel();
//			model.setName("T"+i);
//			model.setDescription("do backup job for S"+i);
//			model.setSessionLocation("//storage server"+i+"/session");
//			gridStore.add(model);
//		}
		
		
		GridCellRenderer<BackupTemplateModel> nameRenderer = new GridCellRenderer<BackupTemplateModel>() {


			@Override
			public Object render(BackupTemplateModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupTemplateModel> store,
					Grid<BackupTemplateModel> grid) {
				LabelField lblName = new LabelField();
				lblName.setText(new Html("<pre style=\"font-family: Tahoma,Arial;font-size: 11px;\">"+model.getName()+"</pre>").getHtml());
				Utils.addToolTip(lblName, model.getName());
				return lblName;
			}
		};
		
		GridCellRenderer<BackupTemplateModel> descriptionRenderer = new GridCellRenderer<BackupTemplateModel>() {

			@Override
			public Object render(BackupTemplateModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupTemplateModel> store,
					Grid<BackupTemplateModel> grid) {
				LabelField lblDescription = new LabelField();
				lblDescription.setText(new Html("<pre style=\"font-family: Tahoma,Arial;font-size: 11px;\">"+model.getDescription()+"</pre>").getHtml());
				Utils.addToolTip(lblDescription, model.getDescription());
				return lblDescription;
			}
		};
		
		GridCellRenderer<BackupTemplateModel> sessionLocation = new GridCellRenderer<BackupTemplateModel>() {

			@Override
			public Object render(BackupTemplateModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupTemplateModel> store,
					Grid<BackupTemplateModel> grid) {
				LabelField lblSessionLocation = new LabelField();
				lblSessionLocation.setText(new Html("<pre style=\"font-family: Tahoma,Arial;font-size: 11px;\">"+model.backupLocationInfoModel.getSessionLocation()+"</pre>").getHtml());
				Utils.addToolTip(lblSessionLocation, model.backupLocationInfoModel.getSessionLocation());
				return lblSessionLocation;
			}
		};
		
		List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
		columnConfigs.add(createColumnConfig("Name", UIContext.Constants.name(), 50,nameRenderer));
		columnConfigs.add(createColumnConfig("Description", UIContext.Constants.description(), 200,descriptionRenderer));
		columnConfigs.add(createColumnConfig("SessionLocation", UIContext.Constants.sessionLocation(), 300,sessionLocation));
		
		backupTemplateColumnsModel = new ColumnModel(columnConfigs);
				
		BackupTemplateGrid = new Grid<BackupTemplateModel>(gridStore, backupTemplateColumnsModel);
		
		backupTemplateColumnsModel.addListener(Events.WidthChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				BackupTemplateGrid.reconfigure(gridStore, backupTemplateColumnsModel);
			}
	    	
	    });
				
		
		BackupTemplateGrid.setLoadMask(true);
		BackupTemplateGrid.mask(UIContext.Constants.loadingBackupTemplateText());
		BackupTemplateGrid.setHeight(200);
		BackupTemplateGrid.unmask();

		BackupTemplateGrid.setTrackMouseOver(true);
		BackupTemplateGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//		BackupTemplateGrid.addListener(Events.RowClick, archiveSettingsListener);
		BackupTemplateGrid.setBorders(true);
		BackupTemplateGrid.setWidth(450);
		BackupTemplateGrid.setAutoExpandMax(3000);
		BackupTemplateGrid.getView().setForceFit(false);
		
		BackupTemplateSummaryPanel = new LayoutContainer();	  
	    BackupTemplateSummaryPanel.setLayout(new FitLayout());
		BackupTemplateSummaryPanel.setWidth(600);
	    BackupTemplateSummaryPanel.setHeight(400);
	    BackupTemplateSummaryPanel.setScrollMode(Scroll.AUTOX);
		BackupTemplateSummaryPanel.add(BackupTemplateGrid);
		refreshData();
	}
	
	private ColumnConfig createColumnConfig(String id, String header, int width, GridCellRenderer<BackupTemplateModel> renderer) {
		ColumnConfig column = new ColumnConfig();
		column.setGroupable(false);
		column.setSortable(false);
		
		column.setId(id);
		column.setHeader(header);
		column.setMenuDisabled(true);
		if (width >= 0)
			column.setWidth(width);
		if (renderer != null)
			column.setRenderer(renderer);
		return column;
	}

	private void defineHeaderPanel() {
		headerPanel = new HorizontalPanel();
//		headerPanel.setStyleAttribute("background-color","#99BBE8");
		headerPanel.setStyleAttribute("background-color","#DFE8F6");
//		fieldPanel.setSpacing(ROW_CELL_SPACE);
		TableLayout layout1 = new TableLayout();
		layout1.setWidth("100%");
		layout1.setColumns(3);
		layout1.setCellPadding(2);
		layout1.setCellSpacing(2);
		headerPanel.setLayout(layout1);
		
		TableData tdLeft = new TableData();
		tdLeft.setWidth("80%");
		tdLeft.setHorizontalAlign(HorizontalAlignment.LEFT);
		LabelField title=new LabelField("Backup Templates");
		headerPanel.add(title,tdLeft);
		
		TableData tdRight = new TableData();
		tdRight.setWidth("10%");
		tdRight.setHorizontalAlign(HorizontalAlignment.RIGHT);
		addButton=new Button("Add");
		addButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			private BackupTemplateWindow window;

			@Override
			public void componentSelected(ButtonEvent ce) {
				window=new BackupTemplateWindow();
				window.addWindowListener(new WindowListener(){
					public void windowHide(WindowEvent we) {
						if(!window.getcancelled())
						{
							BackupTemplateModel model=window.getBackupTemplateModel();
							gridStore.add(model);
							BackupTemplateGrid.reconfigure(gridStore, backupTemplateColumnsModel);
						}
					}
				});
				window.show();
			}
			
		});
		headerPanel.add(addButton,tdRight);
		deleteButton=new Button("Delete");
		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				final BackupTemplateModel model=BackupTemplateGrid.getSelectionModel().getSelectedItem();
				service.deleltBackupTemplate(new String[]{model.getName()}, new AsyncCallback<Boolean>(){

					@Override
					public void onFailure(Throwable caught) {
						Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,caught.getMessage());
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result){
							gridStore.remove(model);
							BackupTemplateGrid.reconfigure(gridStore, backupTemplateColumnsModel);
						}
					}
					
				});
				
			}
			
		});
		headerPanel.add(deleteButton,tdRight);
	}
	public void refreshData(){
		service.getBackupTemplateList(new AsyncCallback<BackupTemplateModel[]>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(BackupTemplateModel[] result) {
				for(int i=0;i<result.length;i++){
					gridStore.add(result[i]);
				}
				BackupTemplateGrid.reconfigure(gridStore, backupTemplateColumnsModel);
			}
			
		});
	}
}
