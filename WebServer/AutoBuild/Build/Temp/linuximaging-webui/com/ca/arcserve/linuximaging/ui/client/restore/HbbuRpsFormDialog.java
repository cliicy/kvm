package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.ServerInfoContainer;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class HbbuRpsFormDialog extends Dialog {
	private final List<String> existLocation = new ArrayList<String>();
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private final Map<String, ServerInfoModel> rpsMap;
	private ServerInfoModel serverInfoModel = new ServerInfoModel();
	// private int MAX_Field_WIDTH = 200;
	TableData td = new TableData();
	TableData td1 = new TableData();
	ServerInfoContainer rpsContainer = new ServerInfoContainer();
	List<BackupLocationInfoModel> backupList = null;
	SelectionChangedListener<ServerInfoModel> changeListener = new SelectionChangedListener<ServerInfoModel>() {

		@Override
		public void selectionChanged(SelectionChangedEvent<ServerInfoModel> se) {
			ServerInfoModel selected = se.getSelectedItem();
			rpsContainer.getHostName().setValue(selected);
			rpsContainer.getUserName().setValue(selected.getUserName());
			rpsContainer.getPassword().setPasswordValue(selected.getPasswd());
			rpsContainer.getPort().setValue(selected.getPort());
			if ("http".equalsIgnoreCase(selected.getProtocol())) {
				rpsContainer.getProtocol().setValue(rpsContainer.getHttp());
			} else {
				rpsContainer.getProtocol().setValue(rpsContainer.getHttps());
			}
		}
	};

	SelectionListener<ButtonEvent> saveListener = new SelectionListener<ButtonEvent>() {
		@Override
		public void componentSelected(ButtonEvent ce) {
			if (rpsContainer.isValid()) {
				String hn = rpsContainer.getHostName().getRawValue();
				String un = rpsContainer.getUserName().getValue();
				String pw = rpsContainer.getPassword().getPasswordValue();
				Integer pt = rpsContainer.getPort().getValue().intValue();
				String pl = rpsContainer.getProtocol().getValue()
						.getValueAttribute();
				if (!hn.equalsIgnoreCase(serverInfoModel.getServerName())
						|| !un.equalsIgnoreCase(serverInfoModel.getUserName())) {
					serverInfoModel.setRestoreJobId(null);
				}
				serverInfoModel.setServerName(hn);
				serverInfoModel.setUserName(un);
				serverInfoModel.setPasswd(pw);
				serverInfoModel.setProtocol(pl);
				serverInfoModel.setPort(pt);
				service.validateRpsServer(serverInfoModel,
						new BaseAsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
							}

							@Override
							public void onSuccess(Boolean result) {
								if (result) {
									rpsMap.put(null, serverInfoModel);
									hide();
								} else {
									Utils.showMessage(UIContext.Constants
											.productName(), MessageBox.INFO,
											UIContext.Constants
													.validateServerMessage());
								}
							}
						});
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

	public HbbuRpsFormDialog(Map<String, ServerInfoModel> rpsMap,
			List<BackupLocationInfoModel> backupList) {
		this.rpsMap = rpsMap;
		this.backupList = backupList;
		td.setWidth("25%");
		td1.setWidth("75%");
		TableLayout mainLayout = new TableLayout();
		mainLayout.setColumns(2);
		mainLayout.setWidth("100%");
		mainLayout.setCellSpacing(6);
		TableData td2 = new TableData();
		td2.setColspan(2);
		setLayout(mainLayout);
		setHeading(UIContext.Constants.rpsPointInformation());
		setModal(true);
		setSize(350, 250);
		setMaximizable(false);
		add(rpsContainer, td2);
		this.cancelText = UIContext.Constants.help();
		this.noText = UIContext.Constants.cancel();
		setButtons(YESNOCANCEL);
		getButtonById(YES).addSelectionListener(saveListener);
		getButtonById(NO).addSelectionListener(cancelListener);
		getButtonById(CANCEL).addSelectionListener(helpListener);
		rpsContainer.getHostName().addSelectionChangedListener(changeListener);
		refreshRpsForm();
	}

	private void refreshRpsForm() {
		List<ServerInfoModel> serverInfoList = new ArrayList<ServerInfoModel>();
		for (int i = 0; i < backupList.size(); i++) {
			BackupLocationInfoModel server = backupList.get(i);
			if (!existLocation.contains(server.getServerInfoModel()
					.getServerName())) {
				serverInfoList.add(server.getServerInfoModel());
				existLocation.add(server.getServerInfoModel().getServerName());
			}

		}
		rpsContainer.getHostName().getStore().add(serverInfoList);
		ServerInfoModel serverInfo = rpsMap.get(null);
		if (serverInfo != null) {
			serverInfoModel.setRestoreJobId(serverInfo.getRestoreJobId());
			serverInfoModel.setServerName(serverInfo.getServerName());
			serverInfoModel.setUserName(serverInfo.getUserName());
			rpsContainer.getHostName().setValue(serverInfo);
		}
	}
}
