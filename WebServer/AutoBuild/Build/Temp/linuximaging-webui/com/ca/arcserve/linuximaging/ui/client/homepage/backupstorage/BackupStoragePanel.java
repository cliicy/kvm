package com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.core.client.GWT;

public class BackupStoragePanel extends LayoutContainer {

	private BackupStorageServiceAsync service = GWT
			.create(BackupStorageService.class);
	public static int PAGE_LIMIT = 50;
	private HomepageTab parentTabPanel;
	private Grid<BackupLocationInfoModel> grid;
	private ListStore<BackupLocationInfoModel> store;
	// private PagingToolBar toolBar;
	private BackupMachinePanel backupMachinePanel;
	private BackupLocationInfoModel selectedModel;

	public BackupStoragePanel(HomepageTab parent) {
		this.parentTabPanel = parent;
		setLayout(new BorderLayout());
		defineBackupLocationGrid();
		defineBackupMachineGrid();
	}

	private void defineBackupLocationGrid() {

		GridCellRenderer<BackupLocationInfoModel> type = new GridCellRenderer<BackupLocationInfoModel>() {

			@Override
			public Object render(BackupLocationInfoModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<BackupLocationInfoModel> store,
					Grid<BackupLocationInfoModel> grid) {
				if (model.getType() != null
						&& model.getType().intValue() == BackupLocationInfoModel.TYPE_CIFS) {
					return Utils.createLabelField(UIContext.Constants
							.cifsShare());
				} else if (model.getType().intValue() == BackupLocationInfoModel.TYPE_SERVER_LOCAL) {
					return Utils.createLabelField(UIContext.Constants
							.serverLocal());
				} else if (model.getType().intValue() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
					return Utils.createLabelField(UIContext.Constants
							.rpsServe());
				} else if (model.getType().intValue() == BackupLocationInfoModel.TYPE_AMAZON_S3) {
					return UIContext.Constants.amazonS3();
				} else {
					return Utils.createLabelField(UIContext.Constants
							.nfsShare());
				}
			}
		};
		GridCellRenderer<BackupLocationInfoModel> totalSize = new GridCellRenderer<BackupLocationInfoModel>() {

			@Override
			public Object render(BackupLocationInfoModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<BackupLocationInfoModel> store,
					Grid<BackupLocationInfoModel> grid) {
				if (model.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER || model.getType() == BackupLocationInfoModel.TYPE_AMAZON_S3) {
					return Utils.createLabelField(UIContext.Constants.NA());
				}
				return Utils.bytes2GBString(model.getTotalSize());
			}
		};
		GridCellRenderer<BackupLocationInfoModel> freeSize = new GridCellRenderer<BackupLocationInfoModel>() {

			@Override
			public Object render(BackupLocationInfoModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<BackupLocationInfoModel> store,
					Grid<BackupLocationInfoModel> grid) {
				if (model.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER || model.getType() == BackupLocationInfoModel.TYPE_AMAZON_S3) {
					return Utils.createLabelField(UIContext.Constants.NA());
				}
				return Utils.bytes2GBString(model.getFreeSize());
			}
		};
		GridCellRenderer<BackupLocationInfoModel> currentJobCount = new GridCellRenderer<BackupLocationInfoModel>() {

			@Override
			public Object render(BackupLocationInfoModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<BackupLocationInfoModel> store,
					Grid<BackupLocationInfoModel> grid) {
				if (model.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
					return Utils.createLabelField(UIContext.Constants.NA());
				} else {
					return Utils.createLabelField(String.valueOf(model
							.getCurrentJobCount()));
				}
			}
		};
		GridCellRenderer<BackupLocationInfoModel> waitingJobCount = new GridCellRenderer<BackupLocationInfoModel>() {

			@Override
			public Object render(BackupLocationInfoModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<BackupLocationInfoModel> store,
					Grid<BackupLocationInfoModel> grid) {
				if (model.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
					return Utils.createLabelField(UIContext.Constants.NA());
				} else {
					return Utils.createLabelField(String.valueOf(model
							.getWaitingJobCount()));
				}
			}
		};

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("displayName",
				UIContext.Constants.backupDestination(), 570));
		configs.add(Utils.createColumnConfig("Type",
				UIContext.Constants.type(), 150, type));
		configs.add(Utils.createColumnConfig("TotalSize",
				UIContext.Constants.totalSize(), 150, totalSize));
		configs.add(Utils.createColumnConfig("FreeSize",
				UIContext.Constants.freeSize(), 150, freeSize));
		configs.add(Utils.createColumnConfig("CurrentJobCount",
				UIContext.Constants.runningJobCount(), 150, currentJobCount));
		configs.add(Utils.createColumnConfig("waitingJobCount",
				UIContext.Constants.waitingJobCount(), 150, waitingJobCount));

		/*
		 * RpcProxy<PagingLoadResult<BackupLocationInfoModel>> proxy = new
		 * RpcProxy<PagingLoadResult<BackupLocationInfoModel>>() {
		 * 
		 * @Override protected void load( Object loadConfig, final
		 * AsyncCallback<PagingLoadResult<BackupLocationInfoModel>> callback) {
		 * if (!(loadConfig instanceof PagingLoadConfig)) { return; }
		 * 
		 * PagingLoadConfig pagingLoadConfig = (PagingLoadConfig) loadConfig;
		 * service.getBackupStorageList(parentTabPanel.currentServer,
		 * pagingLoadConfig, new
		 * BaseAsyncCallback<PagingLoadResult<BackupLocationInfoModel>>(){
		 * 
		 * @Override public void onFailure(Throwable caught) {
		 * super.onFailure(caught); grid.unmask();
		 * parentTabPanel.changeContent(false); }
		 * 
		 * @Override public void onSuccess(
		 * PagingLoadResult<BackupLocationInfoModel> result) {
		 * parentTabPanel.changeContent(true); callback.onSuccess(result); }
		 * 
		 * }); } };
		 * 
		 * // loader final PagingLoader<PagingLoadResult<ModelData>> loader =
		 * new BasePagingLoader<PagingLoadResult<ModelData>>( proxy);
		 * loader.setRemoteSort(true); toolBar = new PagingToolBar(PAGE_LIMIT) {
		 * 
		 * @Override protected void onRender(Element target, int index) {
		 * super.onRender(target, index);
		 * 
		 * ToolTipConfig removeConfig = null;
		 * 
		 * if (!showToolTips) { first.setToolTip(removeConfig); } if
		 * (!showToolTips) { prev.setToolTip(removeConfig); } if (!showToolTips)
		 * { next.setToolTip(removeConfig); } if (!showToolTips) {
		 * last.setToolTip(removeConfig); } if (!showToolTips) {
		 * refresh.setToolTip(removeConfig); } } };
		 * toolBar.setAlignment(HorizontalAlignment.RIGHT);
		 * toolBar.setStyleAttribute("background-color", "white");
		 * toolBar.setShowToolTips(false); toolBar.bind(loader);
		 */

		// store = new ListStore<BackupLocationInfoModel>(loader);
		store = new ListStore<BackupLocationInfoModel>();
		ColumnModel cm = new ColumnModel(configs);
		grid = new BaseGrid<BackupLocationInfoModel>(store, cm);
		// grid.setAutoExpandColumn("FreeSize");
		grid.setAutoExpandMax(3000);
		grid.setStripeRows(true);
		grid.setLoadMask(true);
		grid.setHeight("100%");
		grid.setBorders(false);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<BackupLocationInfoModel>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<BackupLocationInfoModel> se) {
						backupMachinePanel.refreshData(grid.getSelectionModel()
								.getSelectedItem());
						if (se.getSelectedItem() != null)
							selectedModel = se.getSelectedItem();
					}

				});
		grid.addListener(Events.RowClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				parentTabPanel.toolBar.storage.setModifyAndDeleteEnable(true);

			}

		});

		ContentPanel cp = new ContentPanel();
		cp.setBodyBorder(false);
		cp.setHeaderVisible(false);
		cp.setBorders(true);
		cp.setLayout(new FillLayout(Orientation.HORIZONTAL));
		cp.add(grid);
		// cp.setBottomComponent(toolBar);

		BorderLayoutData center = new BorderLayoutData(LayoutRegion.CENTER);
		center.setCollapsible(true);
		center.setFloatable(true);
		center.setSplit(true);
		this.add(cp, center);
	}

	private void defineBackupMachineGrid() {
		backupMachinePanel = new BackupMachinePanel(parentTabPanel);
		BorderLayoutData south = new BorderLayoutData(LayoutRegion.SOUTH, 250,
				50, 600);
		south.setCollapsible(true);
		south.setSplit(true);
		south.setFloatable(true);
		this.add(backupMachinePanel, south);
	}

	public void refreshData() {
		// toolBar.first();
		service.getAllBackupLocation(parentTabPanel.currentServer,
				new BaseAsyncCallback<List<BackupLocationInfoModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						store.removeAll();
						grid.unmask();
						parentTabPanel.changeContent(false);
					}

					@Override
					public void onSuccess(List<BackupLocationInfoModel> result) {
						store.removeAll();
						if (result != null) {
							store.add(result);
							if (selectedModel != null) {
								for (BackupLocationInfoModel model : result) {
									if (selectedModel.getUUID().equals(
											model.getUUID())) {
										grid.getSelectionModel().select(model,
												false);
										break;
									}
								}
							}
						}
					}

				});
	}

	public BackupLocationInfoModel getSelectionModel() {
		return grid.getSelectionModel().getSelectedItem();
	}

	public void deleteBackupLocation() {
		BackupLocationInfoModel model = getSelectionModel();
		if (model == null) {
			Utils.showMessage(UIContext.productName, MessageBox.ERROR,
					UIContext.Constants.selectOneBackupStorage());
			return;
		}
		service.deteleBackupStorage(parentTabPanel.currentServer,
				model.getUUID(), new BaseAsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(Integer result) {
						if (result == 0) {
							parentTabPanel.refreshBackupStorageTable();
						}
					}
				});
	}

	public void refreshJobButtonGroupInToolbar() {
		BackupLocationInfoModel model = grid.getSelectionModel()
				.getSelectedItem();
		if (model == null) {
			parentTabPanel.toolBar.storage.setDefaultState();
		} else {
			parentTabPanel.toolBar.storage.setAllEnabled(true);
		}
	}

	public void resetData() {
		if (grid != null) {
			grid.getStore().removeAll();
			backupMachinePanel.refreshData(null);
		}
	}

}
