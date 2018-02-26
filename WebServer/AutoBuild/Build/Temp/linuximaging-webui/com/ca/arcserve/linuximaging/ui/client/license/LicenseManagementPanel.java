package com.ca.arcserve.linuximaging.ui.client.license;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.license.LicenseStatusModel;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class LicenseManagementPanel extends LayoutContainer {
	public static int MARGIN = 2;
	private ListStore<LicenseStatusModel> licenseStore;
	private ColumnModel licenseColumnModel;
	private Grid<LicenseStatusModel> licenseGrid;
	private LicenseManageServiceAsync service = GWT.create(LicenseManageService.class);
	private LicenseStatusModel selectedStatus = null;
	//private LicenseKeyInputFields licenseKeyInputFields;
	private  TextField<String> licenseTextField ;
	private LicensedMachinePanel machinePanel;
	public LicenseManagementPanel(){
		BorderLayout layout = new BorderLayout();
        setLayout(layout);

        BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 200);   
        northData.setMargins(new Margins(0, MARGIN, 0, MARGIN));   
      
        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);   
        centerData.setMargins(new Margins(MARGIN));   
      
        BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 40);   
        southData.setMargins(new Margins(0, MARGIN, MARGIN, MARGIN));  
        
        add(setupLicenseGrid(), northData);
        add(setupLicensedMachinePanel(), centerData);
        add(setupLicenseKeyInputPanel(), southData);
	}

	private Widget setupLicensedMachinePanel() {
		machinePanel = new LicensedMachinePanel(this);
		machinePanel.ensureDebugId("187369d7-37e0-435d-a5c6-2ece1d5e3e96");
		machinePanel.setHeading(UIContext.Constants.licenseMachines());
		return machinePanel;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		this.mask(UIContext.Constants.loading());
		refresh();
	}

	private void refresh() {

		service.getComponentStatusList(new BaseAsyncCallback<List<LicenseStatusModel>>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				LicenseManagementPanel.this.unmask();
			}

			@Override
			public void onSuccess(List<LicenseStatusModel> result) {
				licenseStore.removeAll();
				if (result!=null && result.size()>0){
					for (LicenseStatusModel node : result){
						licenseStore.add(node);
					}

					licenseGrid.reconfigure(licenseStore, licenseColumnModel);
				}

				LicenseManagementPanel.this.unmask();

				if (licenseStore.getCount()>0)
					licenseGrid.getSelectionModel().select(0, false);
			}

		});
	}

	private Widget setupLicenseKeyInputPanel() {

		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.ensureDebugId("4e028be7-fcdb-4964-b91f-2bf853d8f119");
		layoutContainer.setHeight(40);

        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(MARGIN));
        layout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
        layoutContainer.setLayout(layout);
		
		Label label = new Label();
		label.ensureDebugId("8f5cd0d8-6a62-4294-8fbe-9109411e4d1c");
	    label.setText(UIContext.Constants.licenseKey());
	   
	    layoutContainer.add(label, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
      //  flexTable.getFlexCellFormatter().setWidth(0, 0, "455px");
	    LayoutContainer inputContainer = new LayoutContainer();
	    inputContainer.ensureDebugId("7fa6def5-8da5-4328-9d9c-d44d4aaf2311");
	    inputContainer.setWidth(380);
	   
	    {
	    
	    //	BorderLayout inputlayout = new BorderLayout();
	    	
	    	inputContainer.setLayout(new RowLayout(Orientation.VERTICAL));
	         
		    licenseTextField = new TextField<String>();
		    licenseTextField.ensureDebugId("adc3f3a5-a1e4-4fdf-912b-df5415dfdd10");
			
		    licenseTextField.setAllowBlank(false);
		    licenseTextField.setMaxLength(29);
		   
		    inputContainer.add(licenseTextField,new RowData(1,-1,new Margins(0,20,0,0)));//with more space on right side for error icon
		    Label tiplabel = new Label();
		    tiplabel.ensureDebugId("0c90e116-30cf-4382-9bbf-0f9f3fc1ba38");
		   
		    tiplabel.setText(UIContext.Constants.licenseKeyFormat());
		    inputContainer.add(tiplabel,new RowData(1,-1,new Margins(0,5,0,0)));
		   
	    }
	    layoutContainer.add(inputContainer, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
        final Button addLicenseButton = new Button(UIContext.Constants.add());
        addLicenseButton.ensureDebugId("f67fb9c8-8cc2-4c2a-9d99-ba8b93d7fb1f");
        addLicenseButton.setWidth(UIContext.BUTTON_MINWIDTH);
        addLicenseButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (licenseTextField.validate()){
					String key = licenseTextField.getValue();
					if(key!=null) key = key.trim();
					LicenseManagementPanel.this.mask(UIContext.Constants.loading());
					service.addLicenseKey(key, new BaseAsyncCallback<Void>(){

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							refresh();
						}

						@Override
						public void onSuccess(Void result) {
							licenseTextField.clear();
							refresh();
							
						}

					});

				}
			}

        });
	    
        layoutContainer.add(addLicenseButton, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
 
		return layoutContainer;
	}

	private Widget setupLicenseGrid() {
		LayoutContainer container= new LayoutContainer();
		VBoxLayout layout = new VBoxLayout();
//		layout.setPadding(new Padding(5));
		layout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
		container.setLayout(layout);
		
		Label label = new Label();
	    label.ensureDebugId("ec61caef-0f6a-4356-aab0-b3f2063479c6");
	    label.setText(UIContext.Constants.releaseLicense());
	    container.add(label, new VBoxLayoutData(new Margins(5, 0, 5, 0)));
	    
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		GridCellRenderer<LicenseStatusModel> componentNameRenderer = new GridCellRenderer<LicenseStatusModel>(){

			@Override
			public Object render(LicenseStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LicenseStatusModel> store, Grid<LicenseStatusModel> grid) {
//				return model.getDisplayName();
				return model.getComponentName();
			}

		};
		GridCellRenderer<LicenseStatusModel> active = new GridCellRenderer<LicenseStatusModel>(){

			@Override
			public Object render(LicenseStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LicenseStatusModel> store, Grid<LicenseStatusModel> grid) {
				if(model.getComponentGroup()==LicenseStatusModel.COMPONENT_GROUP_CAPACITY){
					return UIContext.Constants.license_CapacityEdition();
				}else{
					return model.getActiveLicenseCount();
				}
			}

		};
		GridCellRenderer<LicenseStatusModel> available = new GridCellRenderer<LicenseStatusModel>(){

			@Override
			public Object render(LicenseStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LicenseStatusModel> store, Grid<LicenseStatusModel> grid) {
				if(model.getComponentGroup()==LicenseStatusModel.COMPONENT_GROUP_CAPACITY){
					return UIContext.Constants.license_CapacityEdition();
				}else{
					return model.getAvailableLicenseCount();
				}
			}

		};
		GridCellRenderer<LicenseStatusModel> total = new GridCellRenderer<LicenseStatusModel>(){

			@Override
			public Object render(LicenseStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LicenseStatusModel> store, Grid<LicenseStatusModel> grid) {
				if(model.getComponentGroup()==LicenseStatusModel.COMPONENT_GROUP_CAPACITY){
					return UIContext.Constants.license_CapacityEdition();
				}else{
					return model.getTotalLicenseCount();
				}
			}

		};
//		GridCellRenderer<LicenseStatusModel> needed = new GridCellRenderer<LicenseStatusModel>(){
//
//			@Override
//			public Object render(LicenseStatusModel model, String property,
//					ColumnData config, int rowIndex, int colIndex,
//					ListStore<LicenseStatusModel> store, Grid<LicenseStatusModel> grid) {
//				String componentCode=model.getComponentID();
//				if(componentCode.equals(Component.RPO_Managed_Capacity.getValue())
//						||componentCode.equals(Component.RPO_RTO_Managed_Capacity.getValue())){
//					return UIContext.Constants.license_CapacityEdition();
//				}else{
//					return model.getNeededLicenseCount();
//				}
//			}
//
//		};
		configs.add(createCenterColumn("componentName", UIContext.Constants.columnNameComponent(), 100,componentNameRenderer));
		configs.add(createCenterColumn("version", UIContext.Constants.columnNameVersion(), 80,null));
//		configs.add(Utils.createColumnConfig("activeLicenseCount", UIContext.Constants.columnNameActive(), 70, active));
//		configs.add(Utils.createColumnConfig("availableLicenseCount", UIContext.Constants.columnNameAvailable(), 70, available));
//		configs.add(Utils.createColumnConfig("totalLicenseCount", UIContext.Constants.columnNameTotal(), 70, total));
//		configs.add(Utils.createColumnConfig("neededLicenseCount", UIContext.Constants.columnNameNeeded(), 150, needed));
		configs.add(Utils.createColumnConfig("activeLicenseCount", UIContext.Constants.columnNameActive(), 120, active));
		configs.add(Utils.createColumnConfig("availableLicenseCount", UIContext.Constants.columnNameAvailable(), 120, available));
		configs.add(Utils.createColumnConfig("totalLicenseCount", UIContext.Constants.columnNameTotal(), 120, total));

	    licenseColumnModel = new ColumnModel(configs);
//	    licenseColumnModel.addHeaderGroup(0, 2, new HeaderGroupConfig(UIContext.Constants.licenseHeader(), 1, 4));
	    licenseColumnModel.addHeaderGroup(0, 2, new HeaderGroupConfig(UIContext.Constants.licenseHeader(), 1, 3));

	    licenseStore = new ListStore<LicenseStatusModel>();

	    licenseGrid = new BaseGrid<LicenseStatusModel>(licenseStore, licenseColumnModel);
	    licenseGrid.ensureDebugId("1f15fe01-d11e-4cba-86ed-0a61b4632e87");
		licenseGrid.setTrackMouseOver(false);
	    licenseGrid.setAutoExpandColumn("componentName");
	    licenseGrid.setBorders(false);
	    licenseGrid.setAutoWidth(false);
	    licenseGrid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<LicenseStatusModel>(){

			@Override
			public void selectionChanged(SelectionChangedEvent<LicenseStatusModel> se) {
				if(se.getSelectedItem() == null) return ;
				selectedStatus = se.getSelectedItem();
				machinePanel.refreshData(selectedStatus);
			}

	    });

	    ContentPanel availablePanel = new ContentPanel();
	    availablePanel.ensureDebugId("4d887019-b5f2-4b42-8627-55ec01d28861");
		availablePanel.setHeading(UIContext.Constants.licenseStatus());
		availablePanel.setHeight(175);
		availablePanel.setLayout(new FitLayout());
		availablePanel.add(licenseGrid);

		container.add(availablePanel, new VBoxLayoutData(new Margins(0, 0, 0, 0)));
		return container;
	}

	private ColumnConfig createCenterColumn(String name, String header, int size,@SuppressWarnings("rawtypes") GridCellRenderer renderer){
//		FlexTable tbl = new FlexTable();
//		tbl.ensureDebugId("26aa01a3-c572-4dff-b339-90704c0a089d");
//	    tbl.setWidth("100%");
//	    tbl.setHTML(0, 0, " ");
//	    tbl.setWidget(0, 1, new Label(header));
//	    tbl.setHTML(0, 2, "");
//	    tbl.getCellFormatter().setWidth(0, 0, "5%");
//	    tbl.getCellFormatter().setWidth(0, 2, "5%");

	    ColumnConfig createColumnConfig = Utils.createColumnConfig(name, header, size, renderer);
//		createColumnConfig.setWidget(tbl, name);
//	    createColumnConfig.setAlignment(HorizontalAlignment.CENTER);   

		return createColumnConfig;
	}

	public void updateLicense(LicenseStatusModel license) {
		if (license!=null&&license.getComponentID()!=null){
			for (LicenseStatusModel status : licenseStore.getModels()){
				if(status.getComponentID().equalsIgnoreCase(license.getComponentID())){
					status.setActiveLicenseCount(license.getActiveLicenseCount());
					status.setAvailableLicenseCount(license.getAvailableLicenseCount());
					status.setNeededLicenseCount(license.getNeededLicenseCount());
				}
			}
			licenseGrid.reconfigure(licenseStore, licenseColumnModel);
		}
		
	}
	
	public void refreshLicenseMachine(){
//		machinePanel.refreshData(selectedStatus);
		machinePanel.refresh();
	}

}