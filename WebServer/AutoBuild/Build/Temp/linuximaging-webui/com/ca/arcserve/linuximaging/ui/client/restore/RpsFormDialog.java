package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.DatastoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.RpsDataStoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class RpsFormDialog extends Dialog {
	private final List<String> existLocation = new ArrayList<String>();
	private final Map<String, BackupLocationInfoModel> existModel = new HashMap<String, BackupLocationInfoModel>();
	RpsForm rps;
	private final ComboBox<BackupLocationInfoModel> txtBackupLocation;
	SelectionListener<ButtonEvent> saveListener = new SelectionListener<ButtonEvent>() {
		@Override
		public void componentSelected(ButtonEvent ce) {

			RpsDataStoreModel ds = rps.getDataStoreBox().getValue();
			ServerInfoModel serverInfoModel = rps.getServerInfoModel();
			if (!"".equals(ds) && ds != null) {
				BackupLocationInfoModel sessionLocation = new BackupLocationInfoModel();
				sessionLocation.setSessionLocation(ds.getSharePath());

				sessionLocation
						.setType(BackupLocationInfoModel.TYPE_RPS_SERVER);
				sessionLocation.setUser(ds.getSharePathUsername());
				sessionLocation.setPassword(ds.getSharePathPassword());
				sessionLocation.setDisplayName(ds.getHostName() + ":"
						+ ds.getStoreSharedName());
				DatastoreModel datastoreModel = new DatastoreModel();
				datastoreModel.setDisplayName(ds.getStoreSharedName());
				datastoreModel.setId(ds.getUuid());
				datastoreModel.setUuid(ds.getUuid());
				datastoreModel.setSharePath(ds.getSharePath());
				datastoreModel.setSharePathUsername(ds.getSharePathUsername());
				datastoreModel.setSharePathPassword(ds.getSharePathPassword());
				sessionLocation.setServerInfoModel(serverInfoModel);
				sessionLocation.setDatastoreModel(datastoreModel);
				sessionLocation.setEnableDedup(ds.getEnableDedup());
				if (existModel.containsKey(sessionLocation.getDisplayName())) {
					BackupLocationInfoModel server = existModel
							.get(sessionLocation.getDisplayName());
					txtBackupLocation.getStore().remove(server);
					txtBackupLocation.getStore().add(sessionLocation);
				} else {
					txtBackupLocation.getStore().add(sessionLocation);
				}
				txtBackupLocation.setValue(sessionLocation);
				existLocation.clear();
				hide();
			} else {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.INFO, UIContext.Constants.noDataStore());
			}
		}
	};

	SelectionListener<ButtonEvent> cancelListener = new SelectionListener<ButtonEvent>() {
		@Override
		public void componentSelected(ButtonEvent ce) {
			hide();
		}
	};

	SelectionListener<ButtonEvent> helpListener = new SelectionListener<ButtonEvent>() {
		@Override
		public void componentSelected(ButtonEvent ce) {
			Utils.showURL(UIContext.helpLink
					+ HelpLinkItem.BMR_RESTORE_WIZARD_TARGET);
		}
	};

	public RpsFormDialog(ComboBox<BackupLocationInfoModel> txtBackupLocation,
			ServiceInfoModel servicInfo) {
		this.txtBackupLocation = txtBackupLocation;
		TableLayout mainLayout = new TableLayout();
		mainLayout.setColumns(2);
		mainLayout.setWidth("100%");
		mainLayout.setCellSpacing(6);
		TableData td2 = new TableData();
		td2.setColspan(2);
		setLayout(mainLayout);
		setHeading(UIContext.Constants.rpsPointInformation());
		setModal(true);
		setSize(450, 300);
		setMaximizable(false);
		rps = new RpsForm(servicInfo);
		add(rps, td2);
		this.cancelText = UIContext.Constants.help();
		this.noText = UIContext.Constants.cancel();
		setButtons(YESNOCANCEL);
		getButtonById(YES).addSelectionListener(saveListener);
		getButtonById(NO).addSelectionListener(cancelListener);
		getButtonById(CANCEL).addSelectionListener(helpListener);

		List<ServerInfoModel> serverInfoList = new ArrayList<ServerInfoModel>();
		for (int i = 0; i < txtBackupLocation.getStore().getCount(); i++) {
			BackupLocationInfoModel server = txtBackupLocation.getStore()
					.getAt(i);
			existModel.put(server.getDisplayName(), server);
			if (server.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
				if (!existLocation.contains(server.getServerInfoModel()
						.getServerName())) {
					serverInfoList.add(txtBackupLocation.getStore().getAt(i)
							.getServerInfoModel());
					existLocation.add(server.getServerInfoModel()
							.getServerName());
				}
			}
		}
		rps.getHostName().getStore().add(serverInfoList);
	}
}
