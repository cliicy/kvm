package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreTargetModel;
import com.ca.arcserve.linuximaging.ui.client.model.VMRestoreTargetModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class VMTargetSettings extends TargetMachineSettings {

	private ListStore<RestoreTargetModel> targetGridStore;
	private ColumnModel targetColumnsModel;
	private BaseGrid<RestoreTargetModel> targetGrid;
	//private Button addButton;
	//private Button deleteButton;
	
	private VirtualMachineSettings virtualMachineSettings;

	public VMTargetSettings(RestoreWindow restoreWindow){
		super(restoreWindow);
	}
	protected LayoutContainer defineTargetTable() {
		LayoutContainer bmrTargetTable= new LayoutContainer();
		
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(2);
		layout.setCellPadding(0);
		layout.setCellSpacing(2);
		bmrTargetTable.setLayout(layout);
		
		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("80%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		virtualMachineSettings = new VirtualMachineSettings(restoreWindow.currentServer);
		bmrTargetTable.add(virtualMachineSettings,tdColspan);
		
		//ButtonBar targetBar=defineTargetButtonBarPanel();
		//bmrTargetTable.add(targetBar,tdColspan);
		
		//LayoutContainer targets=defineTargetMachineTable();
		//bmrTargetTable.add(targets,tdColspan);
		return bmrTargetTable;
	}
	
	public LayoutContainer defineTargetMachineTable() {
		LayoutContainer targets=new LayoutContainer();
		targets.setLayout(new FitLayout());
		targets.setHeight(TABLE_HIGHT);
		
		targetGridStore = new ListStore<RestoreTargetModel>();
		GridCellRenderer<RestoreTargetModel> network = new GridCellRenderer<RestoreTargetModel>() {

			@Override
			public Object render(RestoreTargetModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RestoreTargetModel> store,
					Grid<RestoreTargetModel> grid) {
				if(model.getNetwork_IsDHCP()){
					return UIContext.Constants.dhcp();
				}else{
					LabelField staticIP=new LabelField(UIContext.Constants.staticIP());
					Utils.addToolTip(staticIP, UIContext.Messages.networkToolTip(model.getNetwork_ipAddress(), model.getNetwork_subnetMask(), model.getNetwork_gateway()));
					return staticIP;
				}
			}
		};
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
	    configs.add(Utils.createColumnConfig("serverName", "Server Name",150,null));
	    configs.add(Utils.createColumnConfig("vmName", "Virtual Machine Name",150,null));
	    configs.add(Utils.createColumnConfig("hostName", UIContext.Constants.hostName(),150,null));
	    configs.add(Utils.createColumnConfig("network", UIContext.Constants.network(),150,network));
//	    configs.add(Utils.createColumnConfig("mapping", UIContext.Constants.network(),150,mapping));
	    targetColumnsModel = new ColumnModel(configs);
	    targetGrid = new BaseGrid<RestoreTargetModel>(targetGridStore, targetColumnsModel);
	    targetGrid.setLoadMask(true);
//		targetGrid.mask(UIContext.Constants.loadingIndicatorText());
		targetGrid.setHeight(TABLE_HIGHT);
		targetGrid.unmask();
	    targetGrid.setTrackMouseOver(true);
	    targetGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    targetGrid.setBorders(true);
//	    targetGrid.setAutoExpandMax(3000);
	    targetGrid.getView().setForceFit(false);
		targetGrid.addListener(Events.RowClick,  new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				//RestoreTargetModel model=targetGrid.getSelectionModel().getSelectedItem();
				//loadSelectedTargetModel(model);
			}

		});
		targets.add(targetGrid);
		return targets;
	}
	/*private void loadSelectedTargetModel(RestoreTargetModel model) {
		txtAddress.setValue(model.getAddress());
		txtHostName.setValue(model.getNetwork_HostName());
		dhcpRadio.setValue(model.getNetwork_IsDHCP());
		staticIPRadio.setValue(!model.getNetwork_IsDHCP());
		if(model.getNetwork_IsDHCP()){
			configButton.disable();
		}else{
			configButton.enable();
		}
		network_ipAddress=model.getNetwork_ipAddress();
		network_subnetMask=model.getNetwork_subnetMask();
		network_gateway=model.getNetwork_gateway();
		reboot.setValue(model.getReboot());
	}*/
	/*private ButtonBar defineTargetButtonBarPanel() {
		ButtonBar bar = new ButtonBar();
		bar.setAlignment(HorizontalAlignment.RIGHT);
		addButton=new Button(UIContext.Constants.save());
		addButton.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.add_button()));
		addButton.setWidth(MIN_BUTTON_WIDTH);
		addButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				saveRestoreTargetModel();
				restoreWindow.restoreModel.setRestoreTargetList(getRestoreTargetList());
			}
			
		});
		deleteButton=new Button(UIContext.Constants.delete());
		deleteButton.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.delete_button()));
		deleteButton.setWidth(MIN_BUTTON_WIDTH);
		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				List<RestoreTargetModel> models=targetGrid.getSelectionModel().getSelectedItems();
				for(RestoreTargetModel model : models){
					targetGridStore.remove(model);
				}
			}
			
		});
		bar.add(addButton);
		bar.add(deleteButton);
		return bar;
	}*/
	private void saveRestoreTargetModel() {
		if(!validateRestoreTargetModel()){
			return;
		}
		/*RestoreTargetModel model=new RestoreTargetModel();				
		model.setAddress(txtAddress.getValue().trim());
		model.setUseMac(isMacAddress());
		model.setNetwork_HostName(txtHostName.getValue());
		model.setReboot(reboot.getValue());
		if(dhcpRadio.getValue()){
			model.setNetwork_IsDHCP(true);
		}else if(staticIPRadio.getValue()){
			model.setNetwork_IsDHCP(false);
			model.setNetwork_ipAddress(network_ipAddress);
			model.setNetwork_subnetMask(network_subnetMask);
			model.setNetwork_gateway(network_gateway);
		}
		updateTargetGridStore(model);
		if(!restoreWindow.isModify){
			network_ipAddress=null;
			network_subnetMask=null;
			network_gateway=null;
			txtAddress.setValue(null);
			txtHostName.setValue(null);
		}*/
	}
	protected void updateTargetGridStore(RestoreTargetModel model) {
		if(restoreWindow.isModify){ //modify a restore job script
			targetGridStore.removeAll();
		}else{
			for(RestoreTargetModel original : targetGridStore.getModels()){
				if(original.getAddress().equalsIgnoreCase(model.getAddress())){
					targetGridStore.remove(original);
					break;
				}
			}
		}
		
		targetGridStore.add(model);
		targetGrid.reconfigure(targetGridStore, targetColumnsModel);
		targetGrid.getSelectionModel().deselectAll();
	}
	
	protected boolean validateRestoreTargetModel() {
		if(!virtualMachineSettings.validate()){
			return false;
		}
		return true;
	}
	
	public List<VMRestoreTargetModel> getRestoreTargetList(){
		List<VMRestoreTargetModel> vmTargetList = new ArrayList<VMRestoreTargetModel>();
		VMRestoreTargetModel model = virtualMachineSettings.getVMRestoreTargetModel();
		vmTargetList.add(model);
		return 	vmTargetList;
	}
	
	public boolean validate(){
		if(restoreWindow.isModify&&!validateRestoreTargetModel()){
			return false;
		}
		/*if(targetGridStore.getModels().size()>0){
			return true;
		}else{
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.selectTargetMachineMessage());
			return false;
		}*/
		return true;
	}
	public void save() {
		if(restoreWindow.isModify){
			saveRestoreTargetModel();
		}
		restoreWindow.restoreModel.setVmRestoreTargetList(getRestoreTargetList());
	}
	public void refreshData() {
		RestoreModel model=restoreWindow.restoreModel;
		
		if(restoreWindow.isModify){
			virtualMachineSettings.refreshData(model.getVmRestoreTargetList().get(0));
		}
		/*targetGridStore.removeAll();
		if(restoreWindow.isModify){
			targetGridStore.add(model.getRestoreTargetList().get(0));
			//loadSelectedTargetModel(model.getRestoreTargetList().get(0));
		}else{
			targetGridStore.add(model.getRestoreTargetList());
		}
		targetGrid.reconfigure(targetGridStore, targetColumnsModel);*/
		
	}
	@Override
	public void refreshPart() {
		// TODO Auto-generated method stub
		
	}

}
