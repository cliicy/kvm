package com.ca.arcserve.linuximaging.ui.client.restore;

import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class SPTargetSettings extends TargetMachineSettings {

	private SPLocationSettings sPLocationSettings;

	public SPTargetSettings(RestoreWindow restoreWindow) {
		super(restoreWindow);
	}

	@Override
	protected LayoutContainer defineTargetTable() {
		LayoutContainer SPTargetTable = new LayoutContainer();

		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(2);
		SPTargetTable.setLayout(layout);
		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("80%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);

		sPLocationSettings = new SPLocationSettings(restoreWindow.currentServer);
		SPTargetTable.add(sPLocationSettings, tdColspan);
		return SPTargetTable;
	}

	@Override
	public boolean validate() {
		return sPLocationSettings.validate();
	}

	@Override
	public void save() {
		sPLocationSettings.save(restoreWindow.restoreModel);
	}

	@Override
	public void refreshData() {
		sPLocationSettings.refreshData(restoreWindow.restoreModel);
	}

	@Override
	public void refreshPart() {

	}
	
	public void validatePassword(final BaseAsyncCallback<Boolean> callBack) {
		sPLocationSettings.validatePassword(callBack);
	}
	
}
