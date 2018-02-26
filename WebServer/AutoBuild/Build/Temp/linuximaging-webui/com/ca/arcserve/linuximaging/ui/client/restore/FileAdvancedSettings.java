package com.ca.arcserve.linuximaging.ui.client.restore;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.DisclosurePanel;

public class FileAdvancedSettings extends AdvancedSettings {
	
	private CheckBox estimateOption;
	
	public FileAdvancedSettings(RestoreWindow window){
		super(window);
		targetScriptContainer.setVisible(false);
	}
	
	private DisclosurePanel getEstimatePanel(){
		DisclosurePanel estimatePanel = Utils.getDisclosurePanel(UIContext.Constants.restoreEstimateSetting());
		
		LayoutContainer estimateContainer = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		estimateContainer.setLayout(layout);
		
		LabelField label = new LabelField(UIContext.Constants.restoreEstimateDescription());
		estimateContainer.add(label);
		
		estimateOption = new CheckBox();
		estimateOption.setValue(true);
		estimateOption.addStyleName("restoreWizardTopSpacing");
		estimateOption.setBoxLabel(UIContext.Constants.restoreEstimateFileSize());

		estimateContainer.add(estimateOption);
		estimatePanel.add(estimateContainer);
		return estimatePanel;
	}
	
	public boolean validate(){
		return true;
	}
	
	public void refreshData(){
		super.refreshData();
		RestoreModel model=restoreWindow.restoreModel;
		estimateOption.setValue(model.getEstimateFileSize());
	}
	
	public void save(){
		super.save();
		restoreWindow.restoreModel.setEstimateFileSize(estimateOption.getValue());
	}

	@Override
	protected void addOtherPanel() {
		add(getEstimatePanel());
		add(getPrePostScriptPanel());
	}

}
