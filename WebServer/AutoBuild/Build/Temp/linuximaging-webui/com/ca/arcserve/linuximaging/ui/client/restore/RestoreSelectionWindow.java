package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupModel;
import com.ca.arcserve.linuximaging.ui.client.model.DatastoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.RpsDataStoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ShareFolderModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class RestoreSelectionWindow extends Window {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private HomepageTab homePageTab;
	private String nodeName;
	private String datastoreName;
	private ServerInfoModel serverInfoModel;
	private ShareFolderModel shareFolderModel;
	private BackupLocationInfoModel sessionLocation;
	private Radio bmrRadio;
	private Radio fileRadio;
	private Button okButton;
	private Button cancelButton;
	private RestoreSelectionWindow thisWindow;
	private RestoreType restoreType;

	public RestoreSelectionWindow(HomepageTab tabPanel,
			ServerInfoModel serverInfoModel, ShareFolderModel shareFolder,
			String nodename, String datastorename) {
		this.thisWindow = this;
		this.serverInfoModel = serverInfoModel;
		this.shareFolderModel = shareFolder;
		this.datastoreName = datastorename;
		this.nodeName = nodename;
		this.homePageTab = tabPanel;
		this.setWidth(300);
		this.setResizable(false);
		this.setHeading(UIContext.Constants.selectRestoreType());
		VerticalPanel vp = new VerticalPanel();
		bmrRadio = new Radio();
		bmrRadio.setValue(true);
		bmrRadio.setBoxLabel(UIContext.Constants.restoreType_BMR());

		fileRadio = new Radio();
		fileRadio.setBoxLabel(UIContext.Constants.restoreType_Restore_File());

		RadioGroup rg = new RadioGroup();
		rg.add(bmrRadio);
		rg.add(fileRadio);

		vp.add(bmrRadio);
		vp.add(fileRadio);
		this.add(vp);

		okButton = new Button();
		okButton.setText(UIContext.Constants.OK());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				restoreType = RestoreType.BMR;
				if (!bmrRadio.getValue()) {
					restoreType = RestoreType.FILE;
				}
				getRpdDatastoreByServerInfoModel();
				thisWindow.hide();

			}
		});

		cancelButton = new Button();
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
			}
		});
		this.setButtonAlign(HorizontalAlignment.CENTER);
		this.addButton(okButton);
		this.addButton(cancelButton);
	}

	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
	}

	public void setRestoreFileWindow() {
		restoreType = RestoreType.FILE;
		getRpdDatastoreByServerInfoModel();
	}

	private void getRpdDatastoreByServerInfoModel() {
		if (serverInfoModel != null) {
			service.findRpsDataStoreByServerInfo(
					homePageTab.toolBar.tabPanel.currentServer,
					serverInfoModel,
					new BaseAsyncCallback<List<RpsDataStoreModel>>() {
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
						}

						@Override
						public void onSuccess(List<RpsDataStoreModel> result) {
							for (RpsDataStoreModel ds : result) {
								if (datastoreName.equalsIgnoreCase(ds
										.getStoreSharedName())) {
									sessionLocation = new BackupLocationInfoModel();
									sessionLocation.setSessionLocation(ds
											.getSharePath());

									sessionLocation
											.setType(BackupLocationInfoModel.TYPE_RPS_SERVER);
									sessionLocation.setUser(ds
											.getSharePathUsername());
									sessionLocation.setPassword(ds
											.getSharePathPassword());
									sessionLocation.setDisplayName(ds
											.getHostName()
											+ ":"
											+ ds.getStoreSharedName());
									DatastoreModel datastoreModel = new DatastoreModel();
									datastoreModel.setDisplayName(ds
											.getStoreSharedName());
									datastoreModel.setId(ds.getUuid());
									datastoreModel.setUuid(ds.getUuid());
									datastoreModel.setSharePath(ds
											.getSharePath());
									datastoreModel.setSharePathUsername(ds
											.getSharePathUsername());
									datastoreModel.setSharePathPassword(ds
											.getSharePathPassword());
									sessionLocation
											.setServerInfoModel(serverInfoModel);
									sessionLocation
											.setDatastoreModel(datastoreModel);
									sessionLocation.setEnableDedup(ds
											.getEnableDedup());
									datastoreName = sessionLocation
											.getDisplayName();
								}
							}
							showRestoreWindow(restoreType);
						}
					});
		} else if (shareFolderModel != null) {
			sessionLocation = new BackupLocationInfoModel();
			sessionLocation.setSessionLocation(shareFolderModel.getPath());
			sessionLocation.setDisplayName(shareFolderModel.getPath());
			if ("1".equalsIgnoreCase(shareFolderModel.isNfs())) {
				sessionLocation.setType(BackupLocationInfoModel.TYPE_NFS);
			} else {
				if(shareFolderModel.getPath().startsWith(Utils.S3_PREFIX)){
					sessionLocation.setType(BackupLocationInfoModel.TYPE_AMAZON_S3);
				}else{
					sessionLocation.setType(BackupLocationInfoModel.TYPE_CIFS);
				}
			}
			sessionLocation.setUser(shareFolderModel.getUserName());
			sessionLocation.setPassword(shareFolderModel.getPasswd());
			sessionLocation.setDisplayName(shareFolderModel.getPath());
			datastoreName = sessionLocation.getDisplayName();
			showRestoreWindow(restoreType);
		} else {
			showRestoreWindow(restoreType);
		}

	}

	private void showRestoreWindow(final RestoreType restoreType) {
		if (nodeName != null) {
			if (serverInfoModel != null || shareFolderModel != null) {
				RestoreWindow window = new RestoreWindow(homePageTab.toolBar,
						restoreType, nodeName, datastoreName, sessionLocation);
				window.show();
			} else {
				this.mask(UIContext.Constants.loading());
				service.getBackupJobScriptByNodeName(homePageTab.currentServer,
						nodeName, new BaseAsyncCallback<BackupModel>() {
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								unmask();
							}

							@Override
							public void onSuccess(BackupModel result) {
								unmask();
								String node = nodeName;
								String location = datastoreName;
								if (result != null) {

									BackupLocationInfoModel locationModel = result
											.getDestInfo().backupLocationInfoModel;
									if (locationModel.getType() != BackupLocationInfoModel.TYPE_SOURCE_LOCAL) {
										location = locationModel
												.getDisplayName();
									}
									node = nodeName;
									sessionLocation = locationModel;
								}
								RestoreWindow window = new RestoreWindow(
										homePageTab.toolBar, restoreType, node,
										location, sessionLocation);
								window.show();
							}
						});
			}

		} else {
			RestoreWindow window = new RestoreWindow(homePageTab.toolBar,
					restoreType, null, null, null);
			window.show();
		}
	}

	public void setRestoreType(RestoreType restoreType) {
		this.restoreType = restoreType;
	}
}
