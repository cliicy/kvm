package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.exception.BusinessLogicException;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.DatastoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.MemoryUnit;
import com.ca.arcserve.linuximaging.ui.client.model.MemoryUnitModel;
import com.ca.arcserve.linuximaging.ui.client.model.Protocol;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.VMAItemModel;
import com.ca.arcserve.linuximaging.ui.client.model.VMRestoreTargetModel;
import com.ca.arcserve.linuximaging.ui.client.model.VirtualMachineInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.VirtualizationServerModel;
import com.ca.arcserve.linuximaging.ui.client.model.VirtualizationServerType;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.RadioButton;

public class VirtualMachineSettings extends LayoutContainer {
	
	final HomepageServiceAsync service = GWT.create(HomepageService.class);
	private Radio esxRadio;
	private Radio xenRadio;
	private LayoutContainer vmwareServerInfoContainer;
	private LayoutContainer xenServerInfoContainer;
	private TextField<String> serverName;
	//private NumberField port;
	private TextField<String> userName;
	private TextField<String> password;
	//private Radio httpRadio;
	//private Radio httpsRadio;
	private Button connectButton;
	private BaseComboBox<VMAItemModel> itemList;
	private BaseComboBox<VMAItemModel> esxList; 
	
	private TextField<String> vmName;
	private BaseComboBox<DatastoreModel> datastores;
	private NumberField memory;
	private BaseComboBox<MemoryUnitModel> memoryUnit;
	
	private TextField<String> txtHostName;
	private RadioButton dhcpRadio;
	private RadioButton staticIPRadio;
	private Button configButton;
	protected IPSettingsDialog ipSettings;
	public final static int ROW_CELL_SPACE=5;
	public final static int MAX_FIELD_WIDTH= 300;
	public final static int TABLE_HIGHT = 130;
	public static int MIN_BUTTON_WIDTH = 80;
	
	private String network_ipAddress;
	private String network_subnetMask;
	private String network_gateway;
	private ServiceInfoModel currentServer;
	private CheckBox reboot;
	
	private FieldSet machineFieldSet;
	private FieldSet osFieldSet;
	
	private MemoryUnitModel gb;
	private MemoryUnitModel mb;
	private VMRestoreTargetModel targetModel;
	
	public VirtualMachineSettings(ServiceInfoModel currentServer){
		this.currentServer = currentServer;
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		this.setLayout(layout);
		
		this.add(defineTargetServerSettings());
		defineTargetMachineSettings();
		defineTargetOSSettings();
	}
	
	private FieldSet defineTargetServerSettings(){
		FieldSet targetServerFieldSet = new FieldSet();
		targetServerFieldSet.setHeading(UIContext.Constants.targetServerSettings());

		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(4);
		tlConnSettings.setCellPadding(2);
		tlConnSettings.setCellSpacing(2);
		targetServerFieldSet.setLayout(tlConnSettings);

		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("15%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		TableData tableDataField1 = new TableData();
		tableDataField1.setWidth("40%");
		tableDataField1.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		TableData tdColspan = new TableData();
		tdColspan.setColspan(3);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField label = new LabelField(UIContext.Constants.targetServerType());
		targetServerFieldSet.add(label,tableDataLabel);
		
		HorizontalPanel serverTypePanel=new HorizontalPanel();
		serverTypePanel.setSpacing(2);
		esxRadio = new Radio();
		esxRadio.setBoxLabel(UIContext.Constants.targetServerType_VMWARE());
		xenRadio = new Radio();
		xenRadio.setBoxLabel(UIContext.Constants.targetServerType_Xen());
		/*xenRadio.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				httpRadio.setEnabled(!xenRadio.getValue());
				httpsRadio.setEnabled(!xenRadio.getValue());
				port.setEnabled(!xenRadio.getValue());
			}
		});*/
		RadioGroup serverRadioGroup = new RadioGroup();
		serverRadioGroup.add(esxRadio);
		serverRadioGroup.add(xenRadio);
		serverTypePanel.add(esxRadio);
		serverTypePanel.add(xenRadio);
		targetServerFieldSet.add(serverTypePanel,tdColspan);
		
		label = new LabelField(UIContext.Constants.targetServerName());
		targetServerFieldSet.add(label,tableDataLabel);
		serverName = new TextField<String>();
		targetServerFieldSet.add(serverName,tdColspan);
		
		
		label = new LabelField(UIContext.Constants.targetServerUsername());
		targetServerFieldSet.add(label,tableDataLabel);
		userName = new TextField<String>();
		targetServerFieldSet.add(userName,tdColspan);
		
		label = new LabelField(UIContext.Constants.targetServerPassword());
		targetServerFieldSet.add(label,tableDataLabel);
		
		password = new TextField<String>();
		password.setPassword(true);
		targetServerFieldSet.add(password,tableDataField);
		
		/*label = new LabelField("");
		targetServerFieldSet.add(label,tableDataLabel);*/
		connectButton = new Button();
		connectButton.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.connect()));
		connectButton.setWidth(MIN_BUTTON_WIDTH);
		connectButton.setText(UIContext.Constants.targetServerConnect());
		connectButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				VirtualizationServerModel serverModel = getVirtualizationServerModel();
				machineFieldSet.setEnabled(true);
				osFieldSet.setEnabled(true);
				machineFieldSet.mask(UIContext.Constants.recoveryVM_validate_server());
				osFieldSet.mask(UIContext.Constants.recoveryVM_validate_server());
				service.getDataStoreList(currentServer,serverModel, new AsyncCallback<List<DatastoreModel>>(){

					@Override
					public void onFailure(Throwable caught) {
						machineFieldSet.setEnabled(false);
						osFieldSet.setEnabled(false);
						machineFieldSet.unmask();
						osFieldSet.unmask();
						if(caught instanceof BusinessLogicException){
							BusinessLogicException ex=(BusinessLogicException)caught;
							Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,ex.getDisplayMessage());
						}else{
							Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,caught.getMessage());
						}
					}

					@Override
					public void onSuccess(List<DatastoreModel> result) {
						datastores.getStore().removeAll();
						if(result!=null){
							datastores.getStore().add(result);
							if(targetModel == null){
								datastores.setValue(result.get(0));
							}else{
								for(DatastoreModel ds : result){
									if(ds.getId().equalsIgnoreCase(targetModel.getVmModel().getDataStoreId())){
										datastores.setValue(ds);
										break;
									}
								}
							}
								
						}
						machineFieldSet.unmask();
						osFieldSet.unmask();
					}
					
				});
			}
		});
		targetServerFieldSet.add(connectButton,tableDataField1);
		label = new LabelField("");
		targetServerFieldSet.add(label,tableDataLabel);
		
		label = new LabelField(UIContext.Constants.recoveryVM_vCenter_or_ESX());
		targetServerFieldSet.add(label,tableDataLabel);
		
		itemList = new BaseComboBox<VMAItemModel>();
		itemList.setDisplayField("itemName");
		itemList.setAllowBlank(false);
		itemList.setValidateOnBlur(false);
		itemList.setEditable(false);
		itemList.setTriggerAction(TriggerAction.ALL);
		itemList.setStore(new ListStore<VMAItemModel>());
		targetServerFieldSet.add(itemList,tdColspan);
		
		esxRadio.setValue(true);
		return targetServerFieldSet;
	}
	
	private void defineTargetMachineSettings(){
		machineFieldSet = new FieldSet();
		machineFieldSet.setHeading(UIContext.Constants.targetVirtualMachineSettings());
		
		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(4);
		tlConnSettings.setCellPadding(2);
		tlConnSettings.setCellSpacing(2);
		machineFieldSet.setLayout(tlConnSettings);

		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("15%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		TableData tableDataField1 = new TableData();
		tableDataField1.setWidth("40%");
		tableDataField1.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		TableData tdColspan = new TableData();
		tdColspan.setColspan(3);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField label = new LabelField(UIContext.Constants.targetVirtualMachineDataStore());
		machineFieldSet.add(label,tableDataLabel);
		datastores = new BaseComboBox<DatastoreModel>();
		datastores.setDisplayField("displayName");
		datastores.setAllowBlank(false);
		datastores.setValidateOnBlur(false);
		datastores.setEditable(false);
		datastores.setTriggerAction(TriggerAction.ALL);
		datastores.setStore(new ListStore<DatastoreModel>());
		machineFieldSet.add(datastores,tdColspan);
		
		label = new LabelField(UIContext.Constants.targetVirtualMachineName());
		machineFieldSet.add(label,tableDataLabel);
		vmName = new TextField<String>();
		machineFieldSet.add(vmName,tdColspan);
		
		label = new LabelField();
		label.setText(UIContext.Constants.targetVirtualMachineMemory());
		machineFieldSet.add(label, tableDataLabel);
		
		HorizontalPanel memoryPanel = new HorizontalPanel();
		memory = new NumberField();
		memory.setWidth(50);
		memoryPanel.add(memory);
		
		memoryUnit = new BaseComboBox<MemoryUnitModel>();
		memoryUnit.setDisplayField("displayName");
		memoryUnit.setAllowBlank(false);
		memoryUnit.setValidateOnBlur(false);
		memoryUnit.setEditable(false);
		memoryUnit.setTriggerAction(TriggerAction.ALL);
		memoryUnit.setStore(getMemoryUnitList());
		memoryUnit.setWidth(50);
		memoryUnit.setValue(gb);
		memoryPanel.add(memoryUnit);
		machineFieldSet.add(memoryPanel,tdColspan);
		
		machineFieldSet.setEnabled(false);
		this.add(machineFieldSet);
	}
	
	private ListStore<MemoryUnitModel> getMemoryUnitList(){
		ListStore<MemoryUnitModel> list = new ListStore<MemoryUnitModel>();
		gb = new MemoryUnitModel();
		gb.setDisplayName(UIContext.Constants.targetVirtualMachineMemoryUnit_GB());
		gb.setValue(MemoryUnit.GB.getValue());
		list.add(gb);
		
		mb = new MemoryUnitModel();
		mb.setDisplayName(UIContext.Constants.targetVirtualMachineMemoryUnit_MB());
		mb.setValue(MemoryUnit.MB.getValue());
		list.add(mb);
		
		return list;
	}
	
	private void defineTargetOSSettings(){
		osFieldSet=new FieldSet();
		osFieldSet.setHeading(UIContext.Constants.targetVirtualMachineOSSettings());

		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(4);
		tlConnSettings.setCellPadding(2);
		tlConnSettings.setCellSpacing(2);
		osFieldSet.setLayout(tlConnSettings);

		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("15%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		TableData tableDataField1 = new TableData();
		tableDataField1.setWidth("40%");
		tableDataField1.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField label = new LabelField(UIContext.Constants.hostName());
		label.setAutoWidth(true);
		osFieldSet.add(label, tableDataLabel);
		txtHostName = new TextField<String>();
		//txtHostName.setWidth(MAX_FIELD_WIDTH);
		osFieldSet.add(txtHostName,tableDataField);
		
		reboot=new CheckBox();
		reboot.setBoxLabel(UIContext.Constants.reboot());
		reboot.setValue(true);
		osFieldSet.add(reboot,tdColspan);
		
		label = new LabelField();
		label.setText(UIContext.Constants.network());
		label.setAutoWidth(true);
		osFieldSet.add(label, tableDataLabel);

		TableData tdColspan3 = new TableData();
		tdColspan3.setColspan(3);
		tdColspan3.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		HorizontalPanel networkPanel = getNetworkPanel();
		osFieldSet.add(networkPanel, tdColspan3);
		
		osFieldSet.setEnabled(false);
		this.add(osFieldSet);
	}
	private HorizontalPanel getNetworkPanel() {
		HorizontalPanel panel=new HorizontalPanel();
		panel.setSpacing(3);
		dhcpRadio=new RadioButton("ipOption",UIContext.Constants.dhcp());
		dhcpRadio.setStyleName("x-form-field");	
		dhcpRadio.setValue(true);
		panel.add(dhcpRadio);
		
		/*LabelField space=new LabelField();
		space.setWidth(85);
		panel.add(space);*/
		
//		dhcp.addListener(Events.Change, ArchiveDestinationSettingsChangeHandler);	
		staticIPRadio=new RadioButton("ipOption",UIContext.Constants.staticIP());
		staticIPRadio.setStyleName("x-form-field");
//		staticIPRadio.setStyleAttribute("padding-left", "40px");
		configButton=new Button(UIContext.Constants.configuration());
		configButton.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.network_configuration()));
		configButton.setWidth(UIContext.BUTTON_MINWIDTH);
		configButton.disable();
		configButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				ipSettings=new IPSettingsDialog(network_ipAddress, network_subnetMask, network_gateway);
				ipSettings.addWindowListener(new WindowListener(){

					public void windowHide(WindowEvent we) {
						if(ipSettings.isClickOK()){
							network_ipAddress = ipSettings.txtIPAddress.getValue();
							network_subnetMask = ipSettings.txtSubnetMask.getValue();
							network_gateway = ipSettings.txtDefaultGateway.getValue();
						}
					}
				});
				ipSettings.show();
			}
			
		});
		
		ClickHandler handler = new ClickHandler() {
		    public void onClick(ClickEvent e) {
		    	if(e.getSource() == dhcpRadio){
		    		configButton.disable();
				}else{
					configButton.enable();
				}
		    }
		};
		dhcpRadio.addClickHandler(handler);
		staticIPRadio.addClickHandler(handler);
		
		panel.add(staticIPRadio);
		panel.add(configButton);
		return panel;
	}
	
	public boolean validate(){
		if(staticIPRadio.getValue()){
			if(network_ipAddress==null){
				Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.configurationButtonMessage());
				return false;
			}else if(network_subnetMask==null){
				Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.configurationButtonMessage());
				return false;
			}else if(network_gateway==null){
				Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.configurationButtonMessage());
				return false;
			}
		}
		return true;
	}
	
	public VMRestoreTargetModel getVMRestoreTargetModel(){
		VirtualizationServerModel serverModel = getVirtualizationServerModel();
		
		VirtualMachineInfoModel vmModel = new VirtualMachineInfoModel();
		vmModel.setVirtualMachineHostName(txtHostName.getValue());
		vmModel.setVirtualMachineName(vmName.getValue());
		vmModel.setDataStoreId(datastores.getValue().getId());
		vmModel.setDataStoreName(datastores.getValue().getDisplayName());
		vmModel.setReboot(reboot.getValue());
		vmModel.setVirtualMachineMemory(memory.getValue().intValue());
		vmModel.setVirtualMachineMemoryUnit(memoryUnit.getValue().getValue());
		if(dhcpRadio.getValue()){
			vmModel.setNetwork_IsDHCP(true);
		}else if(staticIPRadio.getValue()){
			vmModel.setNetwork_IsDHCP(false);
			vmModel.setNetwork_ipAddress(network_ipAddress);
			vmModel.setNetwork_subnetMask(network_subnetMask);
			vmModel.setNetwork_gateway(network_gateway);
		}
		VMRestoreTargetModel targetModel = new VMRestoreTargetModel();
		targetModel.setVirtualizationServerModel(serverModel);
		targetModel.setVmModel(vmModel);
		return targetModel;
	}
	
	private VirtualizationServerModel getVirtualizationServerModel(){
		VirtualizationServerModel serverModel = new VirtualizationServerModel();
		serverModel.setVirtualizationServerName(serverName.getValue());
		serverModel.setVirtualizationServerUsername(userName.getValue());
		serverModel.setVirtualizationServerPassword(password.getValue());
		serverModel.setServerType(esxRadio.getValue() == true ? VirtualizationServerType.VMWARE.getValue():VirtualizationServerType.XEN.getValue());
		//serverModel.setPort(port.getValue().intValue());
		//serverModel.setProtocol(httpRadio.getValue() == true ? Protocol.HTTP.getValue():Protocol.HTTPS.getValue());
		serverModel.setPort(443);
		serverModel.setProtocol(Protocol.HTTPS.getValue());
		
		
		return serverModel;
	}
	
	public void refreshData(VMRestoreTargetModel model){
		if(model != null){
			targetModel = model;
			if(model.getVirtualizationServerModel().getServerType() == VirtualizationServerType.VMWARE.getValue()){
				esxRadio.setValue(true);
				/*port.setValue(model.getVirtualizationServerModel().getPort());
				if(model.getVirtualizationServerModel().getProtocol() == Protocol.HTTP.getValue()){
					httpRadio.setValue(true);
				}else{
					httpsRadio.setValue(true);
				}*/
			}else{
				xenRadio.setValue(true);
			}
			serverName.setValue(model.getVirtualizationServerModel().getVirtualizationServerName());
			userName.setValue(model.getVirtualizationServerModel().getVirtualizationServerUsername());
			password.setValue(model.getVirtualizationServerModel().getVirtualizationServerPassword());
			
			connectButton.fireEvent(Events.Select);
			
			txtHostName.setValue(model.getVmModel().getVirtualMachineHostName());
			vmName.setValue(model.getVmModel().getVirtualMachineName());
			if(model.getVmModel().getReboot()){
				reboot.setValue(true);
			}else{
				reboot.setValue(false);
			}
			memory.setValue(model.getVmModel().getVirtualMachineMemory());
			if(model.getVmModel().getVirtualMachineMemoryUnit() == MemoryUnit.GB.getValue().intValue()){
				memoryUnit.setValue(gb);
			}else{
				memoryUnit.setValue(mb);
			}
			
			if(model.getVmModel().getNetwork_IsDHCP()){
				dhcpRadio.setValue(true);
			}else{
				staticIPRadio.setValue(true);
				network_ipAddress = model.getVmModel().getNetwork_ipAddress();
				network_subnetMask = model.getVmModel().getNetwork_subnetMask();
				network_gateway = model.getVmModel().getNetwork_gateway();
			}
		}
	}

}
