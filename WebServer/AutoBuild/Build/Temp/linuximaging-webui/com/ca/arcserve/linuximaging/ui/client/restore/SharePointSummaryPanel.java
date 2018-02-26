package com.ca.arcserve.linuximaging.ui.client.restore;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreTargetModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class SharePointSummaryPanel extends FieldSet {
	public final static int MAX_FIELD_WIDTH = 300;

	public LabelField lblService;
	public LabelField txtService;

	public LabelField lblUserName;
	public LabelField txtUserName;

	
	
	public LabelField lblNfsShareOption;
	public LabelField txtNfsShareOption;

	public SharePointSummaryPanel() {

		setHeading(UIContext.Constants.credentialSetting());
		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(2);
		tlConnSettings.setCellPadding(0);
		tlConnSettings.setCellSpacing(0);
		setLayout(tlConnSettings);

		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("70%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);

		lblUserName = new LabelField(UIContext.Constants.userName());
		lblUserName.setAutoWidth(true);
		add(lblUserName, tableDataLabel);
		txtUserName = new LabelField();
		add(txtUserName, tableDataField);

		lblNfsShareOption = new LabelField(UIContext.Constants.nfsShareOptionLabel());
		lblNfsShareOption.setAutoWidth(true);
		add(lblNfsShareOption, tableDataLabel);
		txtNfsShareOption = new LabelField();
		add(txtNfsShareOption, tableDataField);
	}

	public void loadTargetModel(RestoreTargetModel restoreTargetModel,
			String application, String nfsShareOption) {
		if (Utils.NFS.equals(application) || application.contains(Utils.NFS)) {
			txtUserName.hide();
			lblUserName.hide();
			lblNfsShareOption.show();
			txtNfsShareOption.show();
			txtNfsShareOption.setValue(nfsShareOption);
		} else {
			txtUserName.show();
			lblUserName.show();
			lblNfsShareOption.hide();
			txtNfsShareOption.hide();
			txtUserName.setValue(restoreTargetModel.getUserName());
		}
	}
}