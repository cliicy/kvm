package com.ca.arcserve.linuximaging.ui.client.homepage.node;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatus;
import com.ca.arcserve.linuximaging.ui.client.model.NodeFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.restore.RestoreWindow;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

import java.util.ArrayList;
import java.util.List;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

public class NodeTable extends LayoutContainer {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private ListStore<NodeModel> gridStore;
	public ColumnModel nodeColumnsModel;
	public Grid<NodeModel> nodeGrid;
	private HomepageTab parentTabPanel;
	private PagingToolBar toolBar;
	protected List<NodeModel> currentSelectedItems;
	protected Point scrollPos;
	private NodeFilterBar filterBar;
	private Menu menu;

	public static int PAGE_LIMIT = 50;

	public NodeTable(HomepageTab homepageTab) {
		this.parentTabPanel = homepageTab;
		// this.setLayout(new FitLayout());
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		RpcProxy<PagingLoadResult<NodeModel>> proxy = new RpcProxy<PagingLoadResult<NodeModel>>() {
			@Override
			protected void load(Object loadConfig,
					final AsyncCallback<PagingLoadResult<NodeModel>> callback) {
				if (!(loadConfig instanceof PagingLoadConfig)) {
					return;
				}

				PagingLoadConfig pagingLoadConfig = (PagingLoadConfig) loadConfig;
				NodeFilterModel filterModel = filterBar.getFilter();
				service.getNodeList(parentTabPanel.currentServer,
						pagingLoadConfig, filterModel,
						new BaseAsyncCallback<PagingLoadResult<NodeModel>>() {

							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								nodeGrid.unmask();
								callback.onFailure(caught);
								parentTabPanel.changeContent(false);
							}

							@Override
							public void onSuccess(
									PagingLoadResult<NodeModel> result) {
								parentTabPanel.changeContent(true);
								callback.onSuccess(result);
							}
						});
			}
		};

		// loader
		final PagingLoader<PagingLoadResult<NodeModel>> loader = new BasePagingLoader<PagingLoadResult<NodeModel>>(
				proxy);
		loader.setRemoteSort(true);
		loader.addLoadListener(new LoadListener() {
			@Override
			public void loaderBeforeLoad(LoadEvent le) {
				currentSelectedItems = nodeGrid.getSelectionModel()
						.getSelectedItems();
				scrollPos = nodeGrid.getView().getScrollState();
			}

			public void loaderLoad(LoadEvent le) {
				if (currentSelectedItems != null) {
					List<NodeModel> selected = new ArrayList<NodeModel>();
					for (int i = 0; i < gridStore.getCount(); ++i) {
						NodeModel node = gridStore.getAt(i);
						for (NodeModel model : currentSelectedItems) {
							if (node.getServerName().equals(
									model.getServerName())) {
								selected.add(node);
							}
						}
					}
					if (selected.size() > 0) {
						nodeGrid.getSelectionModel().select(selected, true);
					}
				}
				if (scrollPos != null && scrollPos.y != 0) {
					nodeGrid.getView().getScroller().setScrollLeft(scrollPos.x);
					nodeGrid.getView().getScroller().setScrollTop(scrollPos.y);
				}
				nodeGrid.setLoadMask(true);
			}
		});
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
		// toolBar.setStyleAttribute("background-color", "white");
		toolBar.setShowToolTips(false);
		toolBar.bind(loader);

		gridStore = new ListStore<NodeModel>(loader);
		// gridStore = new ListStore<NodeModel>();
		GridCellRenderer<NodeModel> nodeName = new GridCellRenderer<NodeModel>() {

			@Override
			public Object render(final NodeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NodeModel> store, Grid<NodeModel> grid) {
				// return model.getServerName();
				LayoutContainer lc = Utils.createIconLabelField(
						UIContext.IconHundle.pserver().createImage(), null,
						model.getServerName());
				lc.addListener(Events.OnMouseDown, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						if (nodeGrid != null) {
							nodeGrid.getSelectionModel().select(model, false);
							rowClickHandler();
						}
					}
				});
				return lc;
			}
		};
		GridCellRenderer<NodeModel> user = new GridCellRenderer<NodeModel>() {

			@Override
			public Object render(NodeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NodeModel> store, Grid<NodeModel> grid) {
				String specialUser = model.getUserName();
				if (specialUser == null) {
					return Utils.createLabelField("");
				}
				int pos = -1;
				if ((pos = specialUser.indexOf('\t')) != -1) {
					return Utils
							.createLabelField(specialUser.substring(0, pos));
				} else {
					return Utils.createLabelField(specialUser);
				}
			}
		};
		GridCellRenderer<NodeModel> backupJob = new GridCellRenderer<NodeModel>() {

			@Override
			public Object render(NodeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NodeModel> store, Grid<NodeModel> grid) {
				if (model.getProtected()) {
					return Utils.createLabelField(model.getJobName());
				} else {
					return Utils.createLabelField("");
				}
			}
		};
		GridCellRenderer<NodeModel> recoveryPointCount = new GridCellRenderer<NodeModel>() {

			@Override
			public Object render(NodeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NodeModel> store, Grid<NodeModel> grid) {
				if (model.getBackupLocationType() != null
						&& model.getBackupLocationType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
					return Utils.createLabelField(UIContext.Constants.NA());
				}
				return Utils.createLabelField(String.valueOf(model
						.getRecoveryPointCount()));
			}
		};

		GridCellRenderer<NodeModel> lastResult = new GridCellRenderer<NodeModel>() {

			@Override
			public Object render(NodeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NodeModel> store, Grid<NodeModel> grid) {
				Image image = Utils.createLastResultImageForNode(JobStatus
						.parse(model.getLastResult()));
				if (image != null) {
					return image;
				} else {
					return Utils.createLabelField(UIContext.Constants.NA());
				}
			}
		};
		GridCellRenderer<NodeModel> operatingSystem = new GridCellRenderer<NodeModel>() {

			@Override
			public Object render(NodeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NodeModel> store, Grid<NodeModel> grid) {
				return Utils.createLabelField(model.getOperatingSystem());
			}
		};
		GridCellRenderer<NodeModel> description = new GridCellRenderer<NodeModel>() {

			@Override
			public Object render(NodeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NodeModel> store, Grid<NodeModel> grid) {
				String description = "";
				if (model.getDescription() == null) {
				} else if (model.getDescription().contains("\r\n")) {
					description = model.getDescription()
							.replaceAll("\r\n", " ");
				} else if (model.getDescription().contains("\n")) {
					description = model.getDescription().replaceAll("\n", " ");
				} else {
					description = model.getDescription();
				}
				return Utils.createLabelField(description);
			}
		};
		// GridCellRenderer<NodeModel> captureServer = new
		// GridCellRenderer<NodeModel>() {
		//
		// @Override
		// public Object render(NodeModel model, String property,
		// ColumnData config, int rowIndex, int colIndex,
		// ListStore<NodeModel> store,
		// Grid<NodeModel> grid) {
		// Button button=new Button(UIContext.Constants.captureServer());
		// final NodeModel node=model;
		// button.addSelectionListener(new SelectionListener<ButtonEvent>(){
		//
		// @Override
		// public void componentSelected(ButtonEvent ce) {
		// CaptureServerDialog captureServerDlg = new CaptureServerDialog(node);
		// captureServerDlg.show();
		// }
		//
		// });
		// return button;
		// }
		// };

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("server",
				UIContext.Constants.nodeName(), 150, nodeName));
		configs.add(Utils.createColumnConfig("user",
				UIContext.Constants.userName(), 100, user));
		String columeName = UIContext.isRestoreMode ? UIContext.Constants
				.planName() : UIContext.Constants.backupJob();
		configs.add(Utils.createColumnConfig("protected", columeName, 200,
				backupJob));
		configs.add(Utils.createColumnConfig("recoveryPointCount",
				UIContext.Constants.recoveryPointCount(), 200,
				recoveryPointCount));
		configs.add(Utils.createColumnConfig("lastResult",
				UIContext.Constants.lastResult(), 100, lastResult));
		configs.add(Utils.createColumnConfig("operatingSystem",
				UIContext.Constants.operatingSystem(), 250, operatingSystem));
		configs.add(Utils.createColumnConfig("description",
				UIContext.Constants.description(), 300, description));
		// configs.add(Utils.createColumnConfig("captureServer",
		// UIContext.Constants.captureServer(),150,captureServer));
		nodeColumnsModel = new ColumnModel(configs);

		nodeGrid = new BaseGrid<NodeModel>(gridStore, nodeColumnsModel);
		// nodeGrid.setAutoExpandColumn("server");
		// nodeGrid.setAutoExpandColumn("description");
		nodeGrid.setTrackMouseOver(true);
		nodeGrid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		nodeGrid.setBorders(true);
		nodeGrid.setHeight("100%");
		nodeGrid.addListener(Events.RowClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				rowClickHandler();
			}

		});

		// nodeGrid.setContextMenu(new NodeContextMenu(this).getContextMenu());
		// add(nodeGrid);
		nodeGrid.setLoadMask(true);
		nodeGrid.setStateId("pagingNodeTable");
		nodeGrid.setStateful(true);
		/*
		 * nodeGrid.addListener(Events.Attach, new
		 * Listener<GridEvent<NodeModel>>() { public void
		 * handleEvent(GridEvent<NodeModel> be) { loader.load(0, PAGE_LIMIT); }
		 * });
		 */

		generateMenu();
		nodeGrid.setContextMenu(menu);
		loader.addLoadListener(new LoadListener() {
			@Override
			public void loaderBeforeLoad(LoadEvent le) {
				currentSelectedItems = nodeGrid.getSelectionModel()
						.getSelectedItems();
				scrollPos = nodeGrid.getView().getScrollState();
			}

			public void loaderLoad(LoadEvent le) {
				if (currentSelectedItems != null) {
					List<NodeModel> selected = new ArrayList<NodeModel>();
					for (int i = 0; i < gridStore.getCount(); ++i) {
						NodeModel node = gridStore.getAt(i);
						for (NodeModel model : currentSelectedItems) {
							if (node.getServerName().equals(
									model.getServerName())) {
								selected.add(node);
							}
						}
					}
					if (selected.size() > 0) {
						nodeGrid.getSelectionModel().select(selected, true);
					}
				}
				if (scrollPos != null && scrollPos.y != 0) {
					nodeGrid.getView().getScroller().setScrollLeft(scrollPos.x);
					nodeGrid.getView().getScroller().setScrollTop(scrollPos.y);
				}
				nodeGrid.setLoadMask(true);
			}
		});

		BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH);
		data.setSize(30);
		filterBar = new NodeFilterBar(this);
		filterBar.hide();
		this.add(filterBar, data);

		data = new BorderLayoutData(LayoutRegion.CENTER);
		this.add(nodeGrid, data);

		data = new BorderLayoutData(LayoutRegion.SOUTH);
		data.setSize(30);
		this.add(toolBar, data);
	}

	private void generateMenu() {
		menu = new Menu();

		MenuItem searchJobStatus = new MenuItem(
				UIContext.Constants.searchJobStatus());

		Menu searchJobStatusMenu = new Menu();
		MenuItem searchJobStatusByNode = new MenuItem(
				UIContext.Constants.byNodeName());
		searchJobStatusByNode
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						NodeModel selected = nodeGrid.getSelectionModel()
								.getSelectedItem();
						if (selected != null) {
							parentTabPanel.setNeedRefresh(false);
							parentTabPanel.showTabItem(HomepageTab.JOB_STATUS);
							parentTabPanel.setNeedRefresh(true);
							parentTabPanel.jobStatusTable
									.filterByNodeName(selected.getServer());
						}
					}
				});
		searchJobStatusMenu.add(searchJobStatusByNode);
		MenuItem searchJobStatusByNodeAndJob = new MenuItem(
				UIContext.Constants.byJobName());
		searchJobStatusByNodeAndJob
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						NodeModel selected = nodeGrid.getSelectionModel()
								.getSelectedItem();
						if (selected != null) {
							parentTabPanel.setNeedRefresh(false);
							parentTabPanel.showTabItem(HomepageTab.JOB_STATUS);
							parentTabPanel.setNeedRefresh(true);
							parentTabPanel.jobStatusTable
									.filterByNodeNameAndJobName("",
											selected.getJobName());
						}
					}
				});
		searchJobStatusMenu.add(searchJobStatusByNodeAndJob);

		searchJobStatus.setSubMenu(searchJobStatusMenu);
		menu.add(searchJobStatus);

		MenuItem searchJobHistory = new MenuItem(
				UIContext.Constants.searchJobHistory());

		Menu searchJobHistoryMenu = new Menu();
		MenuItem searchJobHistoryByNode = new MenuItem(
				UIContext.Constants.byNodeName());
		searchJobHistoryByNode
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						NodeModel selected = nodeGrid.getSelectionModel()
								.getSelectedItem();
						if (selected != null) {
							parentTabPanel.setNeedRefresh(false);
							parentTabPanel.showTabItem(HomepageTab.JOB_HISTORY);
							parentTabPanel.setNeedRefresh(true);
							parentTabPanel.historyTable
									.filterByNodeName(selected.getServer());
						}
					}
				});
		searchJobHistoryMenu.add(searchJobHistoryByNode);
		MenuItem searchJobHistoryByJob = new MenuItem(
				UIContext.Constants.byJobName());
		searchJobHistoryByJob
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						NodeModel selected = nodeGrid.getSelectionModel()
								.getSelectedItem();
						if (selected != null) {
							parentTabPanel.setNeedRefresh(false);
							parentTabPanel.showTabItem(HomepageTab.JOB_HISTORY);
							parentTabPanel.setNeedRefresh(true);
							parentTabPanel.historyTable
									.filterByNodeNameAndJobName("",
											selected.getJobName());
						}
					}
				});
		searchJobHistoryMenu.add(searchJobHistoryByJob);
		searchJobHistory.setSubMenu(searchJobHistoryMenu);
		menu.add(searchJobHistory);

		MenuItem searchActivityLog = new MenuItem(
				UIContext.Constants.searchActivityLog());

		Menu searchActivityLogMenu = new Menu();
		MenuItem searchActivityLogByNode = new MenuItem(
				UIContext.Constants.byNodeName());
		searchActivityLogByNode
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						NodeModel selected = nodeGrid.getSelectionModel()
								.getSelectedItem();
						if (selected != null) {
							parentTabPanel.setNeedRefresh(false);
							parentTabPanel.showTabItem(HomepageTab.JOB_LOG);
							parentTabPanel.setNeedRefresh(true);
							parentTabPanel.activityLogTable
									.filterByNodeName(selected.getServer());
						}
					}
				});
		searchActivityLogMenu.add(searchActivityLogByNode);
		MenuItem searchActivityLogByJob = new MenuItem(
				UIContext.Constants.byJobName());
		searchActivityLogByJob
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						NodeModel selected = nodeGrid.getSelectionModel()
								.getSelectedItem();
						if (selected != null) {
							parentTabPanel.setNeedRefresh(false);
							parentTabPanel.showTabItem(HomepageTab.JOB_LOG);
							parentTabPanel.setNeedRefresh(true);
							parentTabPanel.activityLogTable
									.filterByNodeNameAndJobName("",
											selected.getJobName());
						}
					}
				});
		searchActivityLogMenu.add(searchActivityLogByJob);
		searchActivityLog.setSubMenu(searchActivityLogMenu);
		menu.add(searchActivityLog);

		final MenuItem restore = new MenuItem(UIContext.Constants.restore());

		Menu restoreMenu = new Menu();
		MenuItem restoreBMR = new MenuItem(UIContext.Constants.bmr());
		restoreBMR.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				showRestoreWindow(RestoreType.BMR);
			}
		});
		restoreMenu.add(restoreBMR);
		MenuItem restoreFile = new MenuItem(
				UIContext.Constants.restoreType_Restore_File());
		restoreFile.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				showRestoreWindow(RestoreType.FILE);
			}
		});
		restoreMenu.add(restoreFile);
		restore.setSubMenu(restoreMenu);
		menu.add(restore);

		menu.addListener(Events.BeforeShow, new Listener<MenuEvent>() {

			@Override
			public void handleEvent(MenuEvent be) {
				NodeModel selected = nodeGrid.getSelectionModel()
						.getSelectedItem();
				if (selected != null) {
					if (Utils.isEmptyOrNull(selected.getJobName())
							|| selected.getRecoveryPointCount() == 0) {
						restore.disable();
					} else {
						restore.enable();
					}
				}
			}
		});
	}

	private void showRestoreWindow(final RestoreType restoreType) {
		final NodeModel selected = nodeGrid.getSelectionModel()
				.getSelectedItem();
		if (selected != null) {
			service.getBackupJobScriptByJobName(parentTabPanel.currentServer,
					selected.getJobName(),
					new BaseAsyncCallback<BackupModel>() {
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
						}

						@Override
						public void onSuccess(BackupModel result) {
							BackupLocationInfoModel locationModel = result
									.getDestInfo().backupLocationInfoModel;
							String machine = null;
							String location = null;
							if (locationModel.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
								machine = selected.getServerName();
								location = locationModel.getDisplayName();
							} else if (locationModel.getType() != BackupLocationInfoModel.TYPE_SOURCE_LOCAL) {
								machine = selected.getServerName();
								location = locationModel.getSessionLocation();
							}
							RestoreWindow window = new RestoreWindow(
									parentTabPanel.toolBar, restoreType,
									machine, location, locationModel);
							window.show();
						}
					});
		}
	}

	private void rowClickHandler() {
		// NodeModel model=nodeGrid.getSelectionModel().getSelectedItem();

		int selectedSize = nodeGrid.getSelectionModel().getSelectedItems()
				.size();
		if (selectedSize > 1) {
			nodeGrid.setContextMenu(null);
			parentTabPanel.toolBar.node.enableDelete();
		} else if (selectedSize == 0) {
			parentTabPanel.toolBar.node.setDefaultState();
		} else {
			nodeGrid.setContextMenu(menu);
			parentTabPanel.toolBar.node.enableModifyAndDelete();
		}

	}

	public void refreshTable() {
		toolBar.first();
		toolBar.setEnabled(true);
	}

	public void resetFilter() {
		if (filterBar != null)
			filterBar.resetFilter();
	}

	public void resetData() {
		if (nodeGrid != null)
			nodeGrid.getStore().removeAll();
	}

	/*
	 * public void refreshData(ServiceInfoModel d2dServer, List<NodeModel>
	 * result) { if(d2dServer==null){ return; }else
	 * if(currentServer!=null&&d2dServer
	 * .getServer().equals(currentServer.getServer())){ for(int
	 * i=0;i<result.size();i++){ NodeModel data=result.get(i); // for(NodeModel
	 * data: result){ boolean isExist=false; for(int
	 * j=0;j<gridStore.getCount();j++){ NodeModel node=gridStore.getAt(j);
	 * if(node.getServerName().equals(data.getServerName())){ isExist=true;
	 * node.setUserName(data.getUserName()); node.setPasswd(data.getPasswd());
	 * node.setDescription(data.getDescription());
	 * node.setProtected(data.getProtected());
	 * node.setJobName(data.getJobName()); break; } } if(!isExist){ //
	 * gridStore.add(data); gridStore.insert(data,i); } } }else{
	 * gridStore.removeAll(); gridStore.add(result); currentServer=d2dServer; }
	 * 
	 * nodeGrid.reconfigure(gridStore, nodeColumnsModel); //
	 * jobStatusGrid.getView().refresh(false);
	 * if(parentTabPanel.presentTabIndex.equals(HomepageTab.NODE)){
	 * rowClickHandler(); } }
	 */

	public void deleteNode(final boolean force, final DeleteNodeDialog dialog) {
		final List<NodeModel> nodeList = nodeGrid.getSelectionModel()
				.getSelectedItems();
		if (nodeList == null || nodeList.size() == 0) {
			return;
		}

		dialog.maskAllPanel(force);
		service.deleteNodeList(parentTabPanel.currentServer, nodeList, force,
				Boolean.TRUE, new BaseAsyncCallback<String[]>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						dialog.unmaskAllPanel(force, false);
						parentTabPanel.changeContent(false);
					}

					@Override
					public void onSuccess(String[] result) {
						deleteNodeSuccessProcess(force, nodeList, result);
						dialog.unmaskAllPanel(force, true);
					}
				});
	}

	private int deleteNodeSuccessProcess(boolean force,
			final List<NodeModel> nodeList, String[] result) {
		parentTabPanel.changeContent(true);
		if (result == null) {
			return 1;
		}
		if (result[0] != null) {
			if (toolBar.getTotalPages() > 1) {
				refreshTable();
			} else {
				String[] names = result[0].split(", ");
				for (String name : names) {
					NodeModel model = getNodeModel(nodeList, name);
					gridStore.remove(model);
				}
				nodeGrid.reconfigure(gridStore, nodeColumnsModel);
			}
		}
		if (result[1] != null) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR,
					UIContext.Messages.nodeWithRunningJobMessage(result[1]));
		}
		if (result[2] != null) {
			String[] names = result[2].split(", ");
			String[] retval = result[3].split(", ");
			if (names.length != retval.length) {
				return 1;
			}
			String message = UIContext.Messages.deleteNodeFailed(result[2]);
			for (int i = 0; i < names.length; ++i) {
				int ret = Integer.parseInt(retval[i]);
				if (ret == 1) {
					message += UIContext.Messages.removeDriverFailed(names[i],
							UIContext.Messages
									.connectFailedWrongNetwork(names[i]),
							UIContext.productName);
					message += "<br>";
				} else if (ret == 2) {
					message += UIContext.Messages
							.removeDriverFailed(names[i], UIContext.Constants
									.connectFailedWrongUserAccount(),
									UIContext.productName);
					message += "<br>";
				} else if (ret > 0) {
					message += UIContext.Messages.removeDriverFailed(names[i],
							UIContext.Constants.connectFailedUndefinedReason(),
							UIContext.productName);
					message += "<br>";
				} else if (ret < 0) {
					Utils.showMessage(UIContext.Constants.productName(),
							MessageBox.ERROR, message);
					return ret;
				}
			}
			if (!force) {
				Listener<MessageBoxEvent> callback = new Listener<MessageBoxEvent>() {
					@Override
					public void handleEvent(MessageBoxEvent be) {
						String text = be.getButtonClicked().getText();
						if (text.equals(GXT.MESSAGES.messageBox_yes())) {
							deleteNodeSkipRemoveDriver(nodeList);
						}
					}
				};
				MessageBox.confirm(UIContext.Constants.productName(), message,
						callback);
			}
		}

		return 0;
	}

	public void deleteNodeSkipRemoveDriver(final List<NodeModel> nodeList) {
		if (nodeList == null || nodeList.size() == 0) {
			return;
		}

		service.deleteNodeList(parentTabPanel.currentServer, nodeList,
				Boolean.TRUE, Boolean.FALSE, new BaseAsyncCallback<String[]>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						parentTabPanel.changeContent(false);
					}

					@Override
					public void onSuccess(String[] result) {
						parentTabPanel.changeContent(true);
						if (result == null) {
							return;
						}
						if (result[0] != null) {
							String[] names = result[0].split(", ");
							for (String name : names) {
								NodeModel model = getNodeModel(nodeList, name);
								gridStore.remove(model);
							}
							nodeGrid.reconfigure(gridStore, nodeColumnsModel);
						}
						if (result[1] != null) {
							Utils.showMessage(
									UIContext.Constants.productName(),
									MessageBox.ERROR,
									UIContext.Messages
											.nodeWithRunningJobMessage(result[1]));
						}
					}
				});
	}

	protected NodeModel getNodeModel(List<NodeModel> nodeList, String name) {
		if (nodeList == null || nodeList.size() == 0) {
			return null;
		} else {
			for (NodeModel model : nodeList) {
				if (model.getServerName().equals(name)) {
					return model;
				}
			}
			return null;
		}
	}

	public List<NodeModel> getSelectedItems() {
		return nodeGrid.getSelectionModel().getSelectedItems();
	}

	public ServiceInfoModel getBackupServer() {
		return parentTabPanel.currentServer;
	}

	public HomepageTab getHomePageTab() {
		return parentTabPanel;
	}

	public void modifyNode() {
		List<NodeModel> nodeList = nodeGrid.getSelectionModel()
				.getSelectedItems();
		if (nodeList == null || nodeList.size() == 0) {
			return;
		}

		ChangeAccountDialog dialog = new ChangeAccountDialog(
				parentTabPanel.currentServer, nodeList);
		dialog.setCallback(new ChangeAccountCallback() {

			@Override
			public void processNodeList(List<NodeModel> nodeList) {
				if (nodeList == null || nodeList.size() == 0)
					return;

				service.modifyNodeList(parentTabPanel.currentServer, nodeList,
						new BaseAsyncCallback<Integer>() {

							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								parentTabPanel.changeContent(false);
							}

							@Override
							public void onSuccess(Integer result) {
								parentTabPanel.changeContent(true);
								if (result != 0)
									return;

								parentTabPanel.refreshNodeTable();
							}
						});

			}
		});
		dialog.show();
	}

	public void refreshNodeButtonGroupInToolbar() {
		NodeModel model = nodeGrid.getSelectionModel().getSelectedItem();
		if (model == null) {
			parentTabPanel.toolBar.node.setDefaultState();
		} else {
			parentTabPanel.toolBar.node.enableModifyAndDelete();
		}

	}

	public void showHideFilter() {
		if (filterBar == null)
			return;

		if (filterBar.isVisible()) {
			filterBar.hide();
		} else {
			filterBar.show();
		}
	}

	public HomepageTab getParentTabPanel() {
		return parentTabPanel;
	}
}