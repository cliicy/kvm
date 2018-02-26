package com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.exception.BusinessLogicException;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupMachineModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.VersionInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class BackupMachinePanel extends LayoutContainer {

	private BackupStorageServiceAsync service = GWT
			.create(BackupStorageService.class);
	private HomepageServiceAsync homePageService = GWT
			.create(HomepageService.class);
	public static int PAGE_LIMIT = 50;
	private Grid<BackupMachineModel> grid;
	private PagingToolBar toolBar;
	private ListStore<BackupMachineModel> store;
	private HomepageTab parentTabPanel;
	private BackupLocationInfoModel locationInfo;
	private LayoutContainer linkContainer;
	private ContentPanel cp = new ContentPanel();
	private HTML html;
	private boolean existLinkContainer = true;

	public BackupMachinePanel(HomepageTab parentTabPanel) {
		this.parentTabPanel = parentTabPanel;
		setLayout(new FitLayout());
		defineBackupMachineGrid();
		getManagetServerInfo();
	}

	private void getManagetServerInfo() {
		homePageService.getVersionInfo(null,
				new BaseAsyncCallback<VersionInfoModel>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(VersionInfoModel result) {
						if (result == null) {
						} else {
							if (result.getManagedServer() != null) {
								ServerInfoModel model = result
										.getManagedServer();
								String link = model.getProtocol() + "://"
										+ model.getServerName() + ":"
										+ model.getPort();
								html = new HTML(
										"<div align=\"center\"><a href=\""
												+ link
												+ "\" target=\"_blank\"><span>"
												+ UIContext.Messages
														.rpsIsManagedByOther(model
																.getServerName())
												+ "</span></a></div>");

							} else {
								html = new HTML(
										"<div align=\"center\"><span>"
												+ UIContext.Messages
														.goToUdpConsole(UIContext.Constants
																.productNameUDP())
												+ "</span></div>");
							}
						}
					}

				});

	}

	private void defineBackupMachineGrid() {
		GridCellRenderer<BackupMachineModel> nodeName = new GridCellRenderer<BackupMachineModel>() {

			@Override
			public Object render(BackupMachineModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupMachineModel> store,
					Grid<BackupMachineModel> grid) {
				if (model.getMachineName() != null) {
					return Utils.createLabelField(model.getMachineName());
				} else {
					return Utils.createLabelField("");
				}
			}
		};

		GridCellRenderer<BackupMachineModel> recoverySetCount = new GridCellRenderer<BackupMachineModel>() {

			@Override
			public Object render(BackupMachineModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupMachineModel> store,
					Grid<BackupMachineModel> grid) {
				if (model.getMachineType() == BackupMachineModel.TYPE_HBBU_MACHINE) {
					return UIContext.Constants.NA();
				} else {
					return model.getRecoverySetCount();
				}
			}
		};

		GridCellRenderer<BackupMachineModel> recoveryPointCount = new GridCellRenderer<BackupMachineModel>() {

			@Override
			public Object render(BackupMachineModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupMachineModel> store,
					Grid<BackupMachineModel> grid) {
				return model.getRecoveryPointCount();
			}
		};

		GridCellRenderer<BackupMachineModel> recoveryPointTotalSize = new GridCellRenderer<BackupMachineModel>() {

			@Override
			public Object render(BackupMachineModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupMachineModel> store,
					Grid<BackupMachineModel> grid) {
				return Utils.bytes2GBString(model.getRecoveryPointSize());
			}
		};

		GridCellRenderer<BackupMachineModel> recoveryPointStartTime = new GridCellRenderer<BackupMachineModel>() {

			@Override
			public Object render(BackupMachineModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupMachineModel> store,
					Grid<BackupMachineModel> grid) {
				return Utils.formatDate(model.getFirstDate());
			}
		};

		GridCellRenderer<BackupMachineModel> recoveryPointLastTime = new GridCellRenderer<BackupMachineModel>() {

			@Override
			public Object render(BackupMachineModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupMachineModel> store,
					Grid<BackupMachineModel> grid) {
				return Utils.formatDate(model.getLastDate());
			}
		};

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("machineName",
				UIContext.Constants.nodeName(), 150, nodeName));
		configs.add(Utils.createColumnConfig("recoverySetCount",
				UIContext.Constants.recoverySetCount(), 150, recoverySetCount));
		configs.add(Utils.createColumnConfig("recoveryPointCount",
				UIContext.Constants.recoveryPointCount(), 150,
				recoveryPointCount));
		configs.add(Utils.createColumnConfig("recoveryPointTotalSize",
				UIContext.Constants.recoveryPointSize(), 150,
				recoveryPointTotalSize));
		configs.add(Utils.createColumnConfig("recoveryPointStartTime",
				UIContext.Constants.recoveryPointStartDate(), 200,
				recoveryPointStartTime));
		configs.add(Utils.createColumnConfig("recoveryPointLastTime",
				UIContext.Constants.recoveryPointEndDate(), 150,
				recoveryPointLastTime));

		RpcProxy<PagingLoadResult<BackupMachineModel>> proxy = new RpcProxy<PagingLoadResult<BackupMachineModel>>() {
			@Override
			protected void load(
					Object loadConfig,
					final AsyncCallback<PagingLoadResult<BackupMachineModel>> callback) {
				if (!(loadConfig instanceof PagingLoadConfig)) {
					return;
				}

				PagingLoadConfig pagingLoadConfig = (PagingLoadConfig) loadConfig;
				service.getBackupMachineList(
						parentTabPanel.currentServer,
						locationInfo,
						pagingLoadConfig,
						new BaseAsyncCallback<PagingLoadResult<BackupMachineModel>>() {

							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								grid.unmask();
								if (caught instanceof BusinessLogicException) {
									BusinessLogicException e = (BusinessLogicException) caught;
									String errorCode = e.getErrorCode();
									if (errorCode != null) {
										return;
									}
									// if (errorCode.equals("12884901892")
									// || errorCode.equals("12884901893")
									// || errorCode.equals("12884901894")
									// || errorCode.equals("12884901895")
									// || errorCode.equals("12884901896")
									// || errorCode.equals("12884901889")
									// || errorCode.equals("4294967297")) {
									// return;
									// }
								}
								parentTabPanel.changeContent(false);
							}

							@Override
							public void onSuccess(
									PagingLoadResult<BackupMachineModel> result) {
								parentTabPanel.changeContent(true);
								callback.onSuccess(result);
							}

						});
			}
		};

		// loader
		final PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(
				proxy);
		loader.setRemoteSort(true);
		toolBar = new PagingToolBar(PAGE_LIMIT) {
			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);

				ToolTipConfig removeConfig = null;

				if (!showToolTips) {
					first.setToolTip(removeConfig);
				}
				if (!showToolTips) {
					prev.setToolTip(removeConfig);
				}
				if (!showToolTips) {
					next.setToolTip(removeConfig);
				}
				if (!showToolTips) {
					last.setToolTip(removeConfig);
				}
				if (!showToolTips) {
					refresh.setToolTip(removeConfig);
				}
			}
		};
		toolBar.setAlignment(HorizontalAlignment.RIGHT);
		toolBar.setStyleAttribute("background-color", "white");
		toolBar.setShowToolTips(false);
		toolBar.bind(loader);
		toolBar.setEnabled(false);

		store = new ListStore<BackupMachineModel>(loader);
		ColumnModel cm = new ColumnModel(configs);
		grid = new BaseGrid<BackupMachineModel>(store, cm);
		// grid.setAutoExpandColumn("recoveryPointLastTime");
		grid.setAutoExpandMax(3000);
		grid.setStripeRows(true);
		grid.setLoadMask(true);
		grid.setHeight("100%");
		grid.setBorders(false);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		linkContainer = new LayoutContainer();
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		cp.setBorders(false);
		cp.setLayout(new FillLayout(Orientation.VERTICAL));
		cp.add(grid);
		cp.add(linkContainer);
		cp.setBottomComponent(toolBar);
		this.add(cp);
	}

	public void refreshData(BackupLocationInfoModel locationModel) {
		toolBar.setEnabled(true);
		store.removeAll();
		if (locationModel == null) {
			return;
		}
		if (BackupLocationInfoModel.TYPE_RPS_SERVER == locationModel.getType()) {
			cp.add(linkContainer);
			existLinkContainer = true;
			linkContainer.removeAll();
			linkContainer.add(html);
			linkContainer.layout();
			linkContainer.show();
			grid.hide();
			toolBar.hide();
			this.doLayout();
			return;
		} else {
			if (existLinkContainer) {
				cp.remove(linkContainer);
			}
			existLinkContainer = false;
			linkContainer.hide();
			grid.show();
			toolBar.show();
			this.doLayout();
		}
		this.locationInfo = locationModel;
		toolBar.first();
	}

}
