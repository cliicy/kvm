package com.ca.arcserve.linuximaging.ui.client.standby.physical;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.PhysicalStandbyMachineModel;
import com.ca.arcserve.linuximaging.ui.client.model.PhysicalStandbyModel;
import com.ca.arcserve.linuximaging.ui.client.standby.StandbyMachines;
import com.ca.arcserve.linuximaging.ui.client.standby.StandbyWizardPanel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class PhysicalStandbyMachines extends StandbyMachines {
	
	public static int TABLE_HIGHT = 240;
	public static int TABLE_WIDTH = 650;
	private PhysicalStandbyMachinesGrid standbyMachineGrid;
	private TextField<String> macField;
	private LayoutContainer startMethodContainer;
	private CheckBox autoStart;
	private Radio wakeupOnLan;
	
	public PhysicalStandbyMachines(StandbyWizardPanel parent) {
		super(parent);
		defineMainPanel();
	}

	public void defineMainPanel(){
		TableLayout layout = new TableLayout(1);
		layout.setCellSpacing(5);
		layout.setWidth("97%");
		this.setLayout(layout);
		this.setHeight(StandbyWizardPanel.RIGHT_PANEL_HIGHT-20);
		this.setAutoHeight(true);

		LabelField label = new LabelField();
		label.setText(UIContext.Constants.physicalStandbyHeader());
		label.setStyleAttribute("font-weight", "bold");
		add(label);
		
		addMacPanel();
		standbyMachineGrid = new PhysicalStandbyMachinesGrid(TABLE_HIGHT,TABLE_WIDTH);
		add(standbyMachineGrid);
		add(getAutoStartPanel());
	}
	
	private LayoutContainer getAutoStartPanel(){
		
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(1);
		layout.setCellPadding(3);
		layout.setWidth("100%");
		container.setLayout(layout);
		
		autoStart = new CheckBox();
		autoStart.addListener(Events.Change, new Listener<FieldEvent>(){
				@Override
				public void handleEvent(FieldEvent be) {
					startMethodContainer.setEnabled(autoStart.getValue());
				}

		});
		autoStart.setBoxLabel(UIContext.Constants.automaticallyStartMachine());
		container.add(autoStart);
		
		startMethodContainer = new LayoutContainer();
		startMethodContainer.setStyleAttribute("padding-left", "15px");
		LabelField label = new LabelField(UIContext.Constants.howToStartMachine());
		startMethodContainer.add(label);
		
		wakeupOnLan = new Radio();
		wakeupOnLan.setValue(true);
		wakeupOnLan.setBoxLabel(UIContext.Constants.wakeOnLan());
		startMethodContainer.add(wakeupOnLan);
		startMethodContainer.setEnabled(false);
		
		container.add(startMethodContainer);
		
		return container;
	}
	
	private void addMacPanel(){
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(2);
		
		LabelField label = new LabelField(UIContext.Constants.macAddress());
		panel.add(label);
		
		macField = new TextField<String>();
		macField.setWidth(200);
		panel.add(macField);
		
		Button addButton = new Button(UIContext.Constants.add());
		addButton.setMinWidth(60);
		addButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				String macAddress = macField.getValue();
				if(Utils.validateMacAddress(macAddress)){
					if(isExist(macAddress)){
						Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.macAddressExist());
					}else{
						PhysicalStandbyMachineModel model = new PhysicalStandbyMachineModel();
						model.setMacAddress(macAddress);
						standbyMachineGrid.addData(model);
						macField.setValue("");
					}
				}else{
					Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.invalidMacAddress());
				}
			}
			
		});
		
		panel.add(addButton);
		
		Button removeButton = new Button(UIContext.Constants.restoreRemove());
		removeButton.setMinWidth(60);
		removeButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				standbyMachineGrid.removeSelectedData();
			}
			
		});
		panel.add(removeButton);
		add(panel);
	}
	
	public void save(){
		PhysicalStandbyModel model = (PhysicalStandbyModel)parentWindow.standbyModel;
		model.setStandByMachineList(standbyMachineGrid.getData());
		model.setAutoStartMachine(autoStart.getValue());
		model.setStartMachineMethod(wakeupOnLan.getValue() == true ? PhysicalStandbyModel.START_MACHINE_METHOD_WAN_ON_LAN : PhysicalStandbyModel.START_MACHINE_METHOD_OTHER);
	}
	
	public boolean validate(){
		if(standbyMachineGrid.getData().size() == 0){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.noPhysicalMachine());
			return false;
		}
		return true;
	}
	
	private boolean isExist(String mac){
		if(standbyMachineGrid.getData().size() == 0){
			return false;
		}else{
			for(PhysicalStandbyMachineModel model : standbyMachineGrid.getData()){
				if(model.getMacAddress().equalsIgnoreCase(mac)){
					return true;
				}
			}
			return false;
		}
	}
	
	public void refresh(){
		PhysicalStandbyModel model = (PhysicalStandbyModel)parentWindow.standbyModel;
		standbyMachineGrid.addData(model.getStandByMachineList());
		autoStart.setValue(model.isAutoStartMachine());
	}
}
