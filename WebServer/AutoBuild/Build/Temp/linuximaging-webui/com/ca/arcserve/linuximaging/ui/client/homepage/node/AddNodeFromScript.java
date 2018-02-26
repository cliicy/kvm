package com.ca.arcserve.linuximaging.ui.client.homepage.node;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.backup.wizard.ScheduleSubSettings;
import com.ca.arcserve.linuximaging.ui.client.backup.wizard.ScheduleSubSettings.BkpType;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.NodeDiscoverySettingsModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class AddNodeFromScript extends Dialog {
	
	private static final HomepageServiceAsync backupService = GWT.create(HomepageService.class);
	private static final int DIALOG_WIDTH = 500;
	private SimpleComboBox<String> scriptCombo; 
	private ScheduleSubSettings scheduleSettings;
	private ServiceInfoModel serviceInfo;
	private NodeDiscoverySettingsModel settings;
	private LayoutContainer noScriptWarning;
	
	public AddNodeFromScript(String title,ServiceInfoModel svInfo){
		this.serviceInfo = svInfo;
		setButtons(Dialog.YESNOCANCEL);
		setButtonAlign(HorizontalAlignment.CENTER);
		setModal(true);
		this.setResizable(false);
		this.setWidth(DIALOG_WIDTH);

		setHeading(title);
		
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		tl.setCellPadding(3);
		tl.setCellSpacing(3);
		tl.setWidth("98%");
		setLayout(tl);
		
		Label label = new Label(UIContext.Constants.nodeDiscoveryScript());
		this.add(label);
		
		scriptCombo = new SimpleComboBox<String>();
		scriptCombo.setWidth(349);
		scriptCombo.setEditable(false);
		scriptCombo.setTriggerAction(TriggerAction.ALL);
		
		this.add(scriptCombo);
		label = new Label(UIContext.Constants.nodeDiscoverySchedule());
		
		this.add(label);
		scheduleSettings = new ScheduleSubSettings("AddNodeDiscovery",title,null,UIContext.Constants.nodeDiscoveryOnce());
		scheduleSettings.bkpType = BkpType.INC;
		this.add(scheduleSettings.Render());
		
		noScriptWarning = new LayoutContainer();
		noScriptWarning.setLayout(new TableLayout(2));
		TableData td = new TableData();
		td.setColspan(2);
		
		Image warningImage = new Image(UIContext.IconBundle.information());
		noScriptWarning.add(warningImage);
		noScriptWarning.add(new LabelField(UIContext.Constants.nodeDiscoveryNoScriptWarning()));
		this.add(noScriptWarning,td);
		noScriptWarning.setVisible(false);
		
		this.getButtonById(YES).setText(UIContext.Constants.OK());
		this.getButtonById(YES).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				addNode();
			}
		});
		this.getButtonById(NO).setText(UIContext.Constants.cancel());
		this.getButtonById(NO).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				AddNodeFromScript.this.hide();
			}
		});
		this.getButtonById(CANCEL).setText(UIContext.Constants.help());
		this.getButtonById(CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Utils.showURL(UIContext.helpLink+HelpLinkItem.NODE_DISCOVERY);
			}
		});
		refresh();
	}
	
	private void refreshPrePostScript(){
		backupService.getScripts(serviceInfo, Utils.SCRIPT_TYPE_DISCOVERY,new BaseAsyncCallback<List<String>>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				scriptCombo.removeAll();
			}

			@Override
			public void onSuccess(List<String> result) {
				scriptCombo.removeAll();
				if(result!=null && result.size()>0){
					scriptCombo.add(result);
					if(settings !=null){
						scriptCombo.setSimpleValue(settings.getScript());
					}else{
						scriptCombo.setSimpleValue(result.get(0));
					}
				}else{
					scriptCombo.setEmptyText(UIContext.Constants.nodeDiscoveryNoScript());
					noScriptWarning.setVisible(true);
				}
			}
			
		});
	}
	
	private void refresh(){
		backupService.getNodeDiscoverySettingsModel(serviceInfo, new BaseAsyncCallback<NodeDiscoverySettingsModel>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				refreshPrePostScript();
			}

			@Override
			public void onSuccess(NodeDiscoverySettingsModel result) {
				settings = result;
				if(result!=null){
					scheduleSettings.RefreshData(result.schedule);
				}
				refreshPrePostScript();
			}
			
		});
	}
	
	private void addNode(){
		if(Utils.isEmptyOrNull(scriptCombo.getSimpleValue())){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.nodeDiscoveryScriptNotEmpty());
			return;
		}
		
		if(!scheduleSettings.Validate()){
			return;
		}
		NodeDiscoverySettingsModel settingsModel = new NodeDiscoverySettingsModel();
		settingsModel.setScript(scriptCombo.getSimpleValue());
		settingsModel.schedule = scheduleSettings.Save();
		backupService.addNodeByDiscovery(serviceInfo, settingsModel, new BaseAsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				hide();
			}

			@Override
			public void onSuccess(Integer result) {
				hide();
			}
			
		});
		
	}

}
