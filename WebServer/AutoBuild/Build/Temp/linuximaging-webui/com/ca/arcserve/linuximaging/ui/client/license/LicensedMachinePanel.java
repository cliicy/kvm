package com.ca.arcserve.linuximaging.ui.client.license;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.BasePagingToolBar;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.license.LicenseStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.license.LicensedMachineModel;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;

import java.util.ArrayList;   
import java.util.List;   
  
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.El;   
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;   
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.GridEvent;   
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;   
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;   
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;   
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;   
import com.extjs.gxt.ui.client.widget.grid.Grid;   
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;   
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;   
import com.extjs.gxt.ui.client.widget.grid.GroupingView;   
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;   
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NodeList;   
import com.google.gwt.user.client.Element; 
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LicensedMachinePanel extends ContentPanel {

	public static int PAGE_LIMIT = 50;
	private LicenseManageServiceAsync service = GWT.create(LicenseManageService.class);
	private GroupingView view;
	private String checkedStyle = "x-grid3-group-check";
	private String uncheckedStyle = "x-grid3-group-uncheck";
	private GroupingStore<LicensedMachineModel> store;
	CheckBoxSelectionModel<LicensedMachineModel> checkBoxSelectionModel;
//	private String componentID;
	private Grid<LicensedMachineModel> grid;
	private BasePagingToolBar toolBar;
	private Button deleteButton;
	private ColumnModel cm;
	private LicenseManagementPanel parent;
	private LicenseStatusModel currentLicense;
	
	public LicensedMachinePanel(LicenseManagementPanel licenseManagementPanel){
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		this.parent=licenseManagementPanel;
		PagingLoader<PagingLoadResult<LicensedMachineModel>> loader=definePagingLoad();
		loader.addLoadListener(new LoadListener(){
			@Override
			public void loaderLoad(LoadEvent le) {
//				PagingLoadResult<LicensedMachineModel> result=le.getData();
				parent.updateLicense(currentLicense);
			}
		});
		definePagingToolBar(loader);
		definePagingGrid(loader);
//		this.add(grid);
//		this.setBottomComponent(toolBar);
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
		this.add(grid, data);

		data = new BorderLayoutData(LayoutRegion.SOUTH);
		data.setSize(30);
		this.add(toolBar, data);
	}
	
/*	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
//		setLayout(new FitLayout());
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		PagingLoader<PagingLoadResult<LicensedMachineModel>> loader=definePagingLoad();
		definePagingToolBar(loader);
		definePagingGrid(loader);
		
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
		this.add(grid, data);

		data = new BorderLayoutData(LayoutRegion.SOUTH);
		data.setSize(30);
		this.add(toolBar, data);
//		this.add(grid);
//		this.setBottomComponent(toolBar);
	}*/

	private void definePagingGrid(PagingLoader<PagingLoadResult<LicensedMachineModel>> loader) {
		store = new GroupingStore<LicensedMachineModel>(loader);
//		store = new ListStore<LicensedMachineModel>(loader);

		store.setMonitorChanges(true);
//		store.groupBy("d2dserver");

		checkBoxSelectionModel = new CheckBoxSelectionModel<LicensedMachineModel>() {
			@Override
			public void deselectAll() {
				super.deselectAll();
				NodeList<com.google.gwt.dom.client.Element> groups = view.getGroups();
				for (int i = 0; i < groups.getLength(); i++) {
					com.google.gwt.dom.client.Element group = groups.getItem(i).getFirstChildElement();
					setGroupChecked((Element) group, false);
				}
			}

			@Override
			public void selectAll() {
				super.selectAll();
				NodeList<com.google.gwt.dom.client.Element> groups = view.getGroups();
				for (int i = 0; i < groups.getLength(); i++) {
					com.google.gwt.dom.client.Element group = groups.getItem(i)
							.getFirstChildElement();
					setGroupChecked((Element) group, true);
				}
			}

			@Override
			protected void doDeselect(List<LicensedMachineModel> models,boolean supressEvent) {
				super.doDeselect(models, supressEvent);
				NodeList<com.google.gwt.dom.client.Element> groups = view.getGroups();
				search: for (int i = 0; i < groups.getLength(); i++) {
					com.google.gwt.dom.client.Element group = groups.getItem(i);
					NodeList<Element> rows = El.fly(group).select(
							".x-grid3-row");
					for (int j = 0, len = rows.getLength(); j < len; j++) {
						Element r = rows.getItem(j);
						int idx = grid.getView().findRowIndex(r);
						LicensedMachineModel m = grid.getStore().getAt(idx);
						if (!isSelected(m)) {
							setGroupChecked((Element) group, false);
							continue search;
						}
					}
				}

			}

			@Override
			protected void doSelect(List<LicensedMachineModel> models,
					boolean keepExisting, boolean supressEvent) {
				super.doSelect(models, keepExisting, supressEvent);
				NodeList<com.google.gwt.dom.client.Element> groups = view
						.getGroups();
				search: for (int i = 0; i < groups.getLength(); i++) {
					com.google.gwt.dom.client.Element group = groups.getItem(i);
					NodeList<Element> rows = El.fly(group).select(".x-grid3-row");
					for (int j = 0, len = rows.getLength(); j < len; j++) {
						Element r = rows.getItem(j);
						int idx = grid.getView().findRowIndex(r);
						LicensedMachineModel m = grid.getStore().getAt(idx);
						if (!isSelected(m)) {
							continue search;
						}
					}
					setGroupChecked((Element) group, true);

				}
			}
		};
		checkBoxSelectionModel.setSelectionMode(SelectionMode.MULTI);
		
		GridCellRenderer<LicensedMachineModel> vm = new GridCellRenderer<LicensedMachineModel>() {

			@Override
			public Object render(LicensedMachineModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LicensedMachineModel> store,
					Grid<LicensedMachineModel> grid) {
				if(model.getVM()){
					return UIContext.Constants.yes();
				}else{
					return UIContext.Constants.no();
				}
			}
		};
		
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		config.add(checkBoxSelectionModel.getColumn());
//		ColumnConfig column_d2dserver = new ColumnConfig("d2dserver", UIContext.Constants.backupServer(), 300);
//		config.add(column_d2dserver);
		config.add(Utils.createColumnConfig("d2dserver", UIContext.Constants.backupServer(),250,null));
//		ColumnConfig column_machine = new ColumnConfig("hostname", UIContext.Constants.licenseMachine(), 300);
//		column_machine.setGroupable(false);
//		config.add(column_machine);
		config.add(Utils.createColumnConfig("hostname", UIContext.Constants.licenseMachine(),250,null));
		config.add(Utils.createColumnConfig("vm", UIContext.Constants.licenseVM(),100,vm));
		config.add(Utils.createColumnConfig("socket", UIContext.Constants.licenseSocket(),100,null));
		cm = new ColumnModel(config);

		view = new GroupingView() {

			@Override
			protected void onMouseDown(GridEvent<ModelData> ge) {
				El hd = ge.getTarget(".x-grid-group-hd", 10);
				El target = ge.getTargetEl();
				if (hd != null && target.hasStyleName(uncheckedStyle)
						|| target.hasStyleName(checkedStyle)) {
					boolean checked = !ge.getTargetEl().hasStyleName(
							uncheckedStyle);
					checked = !checked;
					if (checked) {
						ge.getTargetEl().replaceStyleName(uncheckedStyle,
								checkedStyle);
					} else {
						ge.getTargetEl().replaceStyleName(checkedStyle,
								uncheckedStyle);
					}

					Element group = (Element) findGroup(ge.getTarget());
					if (group != null) {
						NodeList<Element> rows = El.fly(group).select(
								".x-grid3-row");
						List<ModelData> temp = new ArrayList<ModelData>();
						for (int i = 0; i < rows.getLength(); i++) {
							Element r = rows.getItem(i);
							int idx = findRowIndex(r);
							ModelData m = grid.getStore().getAt(idx);
							temp.add(m);
						}
						if (checked) {
							grid.getSelectionModel().select(temp, true);
						} else {
							grid.getSelectionModel().deselect(temp);
						}
					}
					return;
				}
				super.onMouseDown(ge);
			}

		};
		view.setStartCollapsed(true);
		view.setShowGroupedColumn(false);
//		view.setForceFit(true);
		view.setGroupRenderer(new GridGroupRenderer() {
			public String render(GroupColumnData data) {
				String f = cm.getColumnById(data.field).getHeader();
				String l = data.models.size() == 1 ? UIContext.Constants.item() : UIContext.Constants.items();
				return "<div class='x-grid3-group-checker'><div class='"
						+ uncheckedStyle + "'> </div></div> " + f + UIContext.Constants.delimiter()+" "
						+ data.group + " (" + data.models.size() + " " + l
						+ ")";
			}
		});

		grid = new Grid<LicensedMachineModel>(store, cm);
//		grid.setAutoExpandColumn("hostname");
		grid.setHeight("100%");
		grid.setView(view);
		grid.setBorders(true);
		grid.addPlugin(checkBoxSelectionModel);
		grid.setSelectionModel(checkBoxSelectionModel);
	}

	private void definePagingToolBar(PagingLoader<PagingLoadResult<LicensedMachineModel>> loader) {
		toolBar = new BasePagingToolBar(PAGE_LIMIT){
	    	  @Override
	    	  protected void onRender(Element target, int index) {
	    		  super.onRender(target, index);
	    	        
	    	        ToolTipConfig removeConfig = null;

	    		    if (!showToolTips){
	    		    	first.setToolTip(removeConfig);
	    		    }
	    		    if (!showToolTips){
	    		    	prev.setToolTip(removeConfig);
	    		    }
	    		    if (!showToolTips){
	    		    	next.setToolTip(removeConfig);
	    		    }
	    		    if (!showToolTips){
	    		    	last.setToolTip(removeConfig);
	    		    }
	    		    if (!showToolTips){
	    		    	refresh.setToolTip(removeConfig);
	    		    }
	    	  }
	      };
		toolBar.setAlignment(HorizontalAlignment.RIGHT);
//		toolBar.setStyleAttribute("background-color", "white");
		toolBar.setShowToolTips(false);
		toolBar.bind(loader);
		
		SeparatorToolItem item = new SeparatorToolItem();
		item.ensureDebugId("2a939141-3cef-422e-b000-7facd8f1dfb8");
		item.setStyleAttribute("margin-left", "4px");
		item.setStyleAttribute("margin-right", "4px");
		toolBar.insert(item, 11);
		
		deleteButton = new Button();
		deleteButton.ensureDebugId("33bc8f15-428c-4fd0-8ce3-cde00d43b774");
		deleteButton.setIcon(IconHelper.create("images/delete.gif"));
		deleteButton.setText(UIContext.Constants.release());
		Utils.addToolTip(deleteButton, UIContext.Constants.releaseLicense());
//		toolBar.add(deleteButton);
		toolBar.insert(deleteButton, 12);
		
		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				List<LicensedMachineModel> machines=checkBoxSelectionModel.getSelectedItems();
//				List<LicensedMachineModel> machines=grid.getSelectionModel().getSelectedItems();
				service.unBindLicenses(currentLicense.getComponentID(), machines, new BaseAsyncCallback<LicenseStatusModel>(){

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						toolBar.first();
					}

					@Override
					public void onSuccess(LicenseStatusModel result) {
						if(result!=null){
							currentLicense=result;
						}
						toolBar.first();
					}

				});
			}
		});
	}

	private PagingLoader<PagingLoadResult<LicensedMachineModel>> definePagingLoad() {
		RpcProxy<PagingLoadResult<LicensedMachineModel>> proxy = new RpcProxy<PagingLoadResult<LicensedMachineModel>>() {
			@Override
			protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<LicensedMachineModel>> callback) {
				if (!(loadConfig instanceof PagingLoadConfig)||currentLicense==null||currentLicense.getComponentID()==null) {
					BasePagingLoadResult<LicensedMachineModel> result=new BasePagingLoadResult<LicensedMachineModel>(new ArrayList<LicensedMachineModel>(), 0, 0);
					callback.onSuccess(result);
					return;
				}

				PagingLoadConfig pagingLoadConfig = (PagingLoadConfig) loadConfig;
				service.getMachineList(pagingLoadConfig, currentLicense.getComponentID(), 
						new BaseAsyncCallback<PagingLoadResult<LicensedMachineModel>>() {

							@Override
							public void onFailure(Throwable caught) {
								grid.unmask();
							}

							@Override
							public void onSuccess(PagingLoadResult<LicensedMachineModel> result) {
								callback.onSuccess(result);
							}
						});
			}
		};

		// loader
		final PagingLoader<PagingLoadResult<LicensedMachineModel>> loader = new BasePagingLoader<PagingLoadResult<LicensedMachineModel>>(proxy);
		loader.setRemoteSort(true);
		return loader;
	}

	private El findCheck(Element group) {
		return El.fly(group).selectNode(".x-grid3-group-checker").firstChild();
	}

	private void setGroupChecked(Element group, boolean checked) {
		findCheck(group).replaceStyleName(checked ? uncheckedStyle : checkedStyle, checked ? checkedStyle : uncheckedStyle);
	}

	public void refreshData(LicenseStatusModel license) {
		if(license==null||license.getComponentID()==null){
			return;
		}
		this.currentLicense=license;
//		this.componentID=license.getCompoentID();
		toolBar.first();
		if(license.getComponentGroup()==LicenseStatusModel.COMPONENT_GROUP_SOCKET){
			cm.setHidden(3, false);
			cm.setHidden(4, false);
		}else if(license.getComponentGroup()==LicenseStatusModel.COMPONENT_GROUP_PER_HOST){
			cm.setHidden(3, false);
			cm.setHidden(4, true);
		}else{
			cm.setHidden(3, true);
			cm.setHidden(4, true);
		}
		/*String componentCode=license.getComponentID();
		if(componentCode.equals(Component.Base.getValue())
				||componentCode.equals(Component.Encryption.getValue())){
			cm.setHidden(2, true);
//			column_d2dserver.setHidden(true);
//			view.getHeader().getHead(1).hide();
//			view.getHeader().getHead(1).getParent().setVisible(false);
		}else{
			cm.setHidden(2, false);
//			column_d2dserver.setHidden(false);
//			view.getHeader().getHead(1).show();
//			view.getHeader().getHead(1).getParent().setVisible(true);
		}*/
	}

	public void refresh() {
//		toolBar.refresh();
		grid.reconfigure(store, cm);
	}

}
