package com.ca.arcserve.linuximaging.ui.client.homepage.jobstatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.BasePagingToolBar;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.exception.BusinessLogicException;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobPhase;
import com.ca.arcserve.linuximaging.ui.client.model.JobScriptModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatus;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.toolbar.CancelJobWindow;
import com.ca.arcserve.linuximaging.ui.client.toolbar.RunNowWindow;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class JobStatusTable extends LayoutContainer {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private ListStore<JobStatusModel> gridStore;
	public ColumnModel jobStatusColumnsModel;
	public Grid<JobStatusModel> jobStatusGrid;
	private HomepageTab parentTabPanel;
	protected List<JobStatusModel> currentSelectedItems;
	protected Point scrollPos;
	private BasePagingToolBar toolBar;
	private JobStatusFilterBar filterBar;
	public static int PAGE_LIMIT = 50;

	public JobStatusTable(HomepageTab homepageTab) {
		this.parentTabPanel = homepageTab;
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		RpcProxy<PagingLoadResult<JobStatusModel>> proxy = new RpcProxy<PagingLoadResult<JobStatusModel>>() {
			@Override
			protected void load(
					Object loadConfig,
					final AsyncCallback<PagingLoadResult<JobStatusModel>> callback) {
				if (!(loadConfig instanceof PagingLoadConfig)) {
					return;
				}

				PagingLoadConfig pagingLoadConfig = (PagingLoadConfig) loadConfig;
				JobStatusFilterModel filterModel = filterBar.getFilter();
				service.getJobStatusList(
						parentTabPanel.currentServer,
						pagingLoadConfig,
						filterModel,
						new BaseAsyncCallback<PagingLoadResult<JobStatusModel>>() {

							@Override
							public void onFailure(Throwable caught) {
								if (parentTabPanel.currentServer.getType()
										.equals(ServiceInfoModel.LOCAL_SERVER)) {
									if (caught instanceof BusinessLogicException) {
										BusinessLogicException e = (BusinessLogicException) caught;
										String errorCode = e.getErrorCode();
										if (errorCode.equals("4294967303")) {
											return;
										}
									}
									super.onFailure(caught);
									jobStatusGrid.unmask();
									callback.onFailure(caught);
									// parentTabPanel.changeContent(false);
								} else {
									jobStatusGrid.unmask();
								}
							}

							@Override
							public void onSuccess(
									PagingLoadResult<JobStatusModel> result) {
								parentTabPanel.changeContent(true);
								callback.onSuccess(result);
							}
						});
			}
		};

		// loader
		final PagingLoader<PagingLoadResult<JobStatusModel>> loader = new BasePagingLoader<PagingLoadResult<JobStatusModel>>(
				proxy);
		loader.setRemoteSort(true);

		toolBar = new BasePagingToolBar(PAGE_LIMIT) {
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

		defineJobStatusGrid(loader);

		loader.addLoadListener(new LoadListener() {
			@Override
			public void loaderBeforeLoad(LoadEvent le) {
				if (jobStatusGrid != null) {
					currentSelectedItems = jobStatusGrid.getSelectionModel()
							.getSelectedItems();
					if (jobStatusGrid.getView() != null)
						scrollPos = jobStatusGrid.getView().getScrollState();
				}
			}

			public void loaderLoad(LoadEvent le) {
				if (currentSelectedItems != null) {
					List<JobStatusModel> selected = new ArrayList<JobStatusModel>();
					for (int i = 0; i < gridStore.getCount(); ++i) {
						JobStatusModel status = gridStore.getAt(i);
						for (JobStatusModel model : currentSelectedItems) {
							if (status.getJobUuid().equals(model.getJobUuid())) {
								selected.add(status);
							}
						}
					}
					if (selected.size() > 0) {
						jobStatusGrid.getSelectionModel()
								.select(selected, true);
					}
				}
				if (scrollPos != null && scrollPos.y != 0) {
					jobStatusGrid.getView().getScroller()
							.setScrollLeft(scrollPos.x);
					jobStatusGrid.getView().getScroller()
							.setScrollTop(scrollPos.y);
				}
			}
		});

		BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH);
		data.setSize(30);
		filterBar = new JobStatusFilterBar(this);
		filterBar.hide();
		this.add(filterBar, data);

		data = new BorderLayoutData(LayoutRegion.CENTER);
		this.add(jobStatusGrid, data);

		data = new BorderLayoutData(LayoutRegion.SOUTH);
		data.setSize(30);
		this.add(toolBar, data);
	}

	private void defineJobStatusGrid(
			PagingLoader<PagingLoadResult<JobStatusModel>> loader) {
		gridStore = new ListStore<JobStatusModel>(loader) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			protected void onLoad(LoadEvent le) {
				this.config = (ListLoadConfig) le.getConfig();

				Object data = le.getData();
				// removeAll();
				for (JobStatusModel m : all) {
					unregisterModel(m);
				}
				all.clear();
				modified.clear();
				recordMap.clear();
				if (snapshot != null) {
					snapshot.clear();
				}
				if (jobStatusGrid != null)
					jobStatusGrid.getSelectionModel().deselectAll();

				if (data == null) {
					all = new ArrayList();
				} else if (data instanceof List) {
					List<JobStatusModel> list = (List) data;
					all = new ArrayList(list);
				} else if (data instanceof ListLoadResult) {
					all = new ArrayList(((ListLoadResult) data).getData());
				}

				for (JobStatusModel m : all) {
					registerModel(m);
				}

				if (config != null
						&& config.getSortInfo() != null
						&& !Util.isEmptyString(config.getSortInfo()
								.getSortField())) {
					sortInfo = config.getSortInfo();
				} else {
					sortInfo = new SortInfo();
				}

				if (filtersEnabled) {
					filtersEnabled = false;
					applyFilters(filterProperty);
				}

				if (storeSorter != null) {
					applySort(true);
				}
				fireEvent(DataChanged, createStoreEvent());
			}
		};

		GridCellRenderer<JobStatusModel> jobID = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				if (model.getJobID() == JobStatusModel.JOBSCRIPT_ID) {
					return Utils.createLabelField("");
				} else {
					return Utils.createLabelField(model.getJobID().toString());
				}
			}
		};
		GridCellRenderer<JobStatusModel> jobName = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(final JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				Image image = Utils.createJobLastResultImage(JobStatus
						.parse(model.getLastResult()));
				LayoutContainer lc = Utils.createIconLabelField(image, null,
						model.getJobName());
				lc.addListener(Events.OnMouseDown, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						if (jobStatusGrid != null) {
							jobStatusGrid.getSelectionModel().select(model,
									false);
							rowClickHandler();
						}
					}

				});
				return lc;
			}
		};
		GridCellRenderer<JobStatusModel> jobType = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				return Utils.createLabelField(JobType.displayMessage(JobType
						.parse(model.getJobType()).getValue(), model
						.getJobMethod()));
			}
		};
		GridCellRenderer<JobStatusModel> nodeName = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				return Utils.createLabelField(model.getNodeName());
			}
		};
		// GridCellRenderer<JobStatusModel> jobStatus = new
		// GridCellRenderer<JobStatusModel>() {
		//
		// @Override
		// public Object render(JobStatusModel model, String property,
		// ColumnData config, int rowIndex, int colIndex,
		// ListStore<JobStatusModel> store,
		// Grid<JobStatusModel> grid) {
		// return
		// Utils.createLabelField(JobStatus.parse(model.getJobStatus()).toString());
		// }
		// };
		GridCellRenderer<JobStatusModel> jobPhase = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				final JobStatusModel jsModel = model;
				int phase = model.getJobPhase();
				if (phase == JobPhase.BACKUP_VOLUME.getValue()) {
					return Utils.createLabelField(UIContext.Messages
							.jobPhase_backupVolume(model.getVolume()));
				} else if (phase == JobPhase.RESTORE_VOLUME.getValue()) {
					return Utils.createLabelField(UIContext.Messages
							.jobPhase_restoreVolume(model.getVolume()));
				} else if (phase == JobPhase.INSTALL_BUILD.getValue()) {
					if (JobType.parse(model.getJobType()).equals(
							JobType.RESTORE)) {
						return Utils.createLabelField(JobPhase
								.displayMessage(JobPhase.CONNECT_TARGET
										.getValue()));
					} else {
						return Utils.createLabelField(JobPhase
								.displayMessage(JobPhase.START.getValue()));
					}
				} else if (phase == JobPhase.WAITING_MIGRATION.getValue()) {
					Label link = new Label(
							JobPhase.displayMessage(JobPhase.WAITING_MIGRATION
									.getValue()));
					link.setStyleName("jobstatus_migrate_data");
					ClickHandler handler = new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							triggerMigration(jsModel);
						}
					};
					link.addClickHandler(handler);
					return link;
				} else {
					return Utils.createLabelField(JobPhase
							.displayMessage(phase));
				}
			}
		};
		GridCellRenderer<JobStatusModel> jobProgress = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				// if(model.getProcessedData()>0&&model.getProgress()<100){
				// ProgressBar bar = new ProgressBar();
				// bar.setVisible(true);
				// bar.updateProgress(model.getProgress() * .01,
				// model.getProgress() + "%");
				// return bar;
				// }else if(model.getProgress()>0&& model.getProgress()<100){
				// ProgressBar bar = new ProgressBar();
				// bar.setVisible(true);
				// bar.updateProgress(model.getProgress() * .01,
				// model.getProgress() + "%");
				// return bar;
				// }else {
				// return
				// Utils.createLabelField(JobStatus.displayMessage(model.getJobStatus()));
				// }
				if (model.getProgress() <= 0 || model.getProgress() >= 100) {
					return Utils.createLabelField(JobStatus
							.displayMessage(model.getJobStatus()));
				} else {
					if (model.getJobType() == JobType.RESTORE_BMR.getValue()
							&& model.getJobMethod() == JobScriptModel.JOB_METHOD_INSTANT_BMR) {
						return Utils.createLabelField(JobStatus
								.displayMessage(JobStatus.ACTIVE.getValue()));
					} else {
						ProgressBar bar = new ProgressBar();
						bar.setVisible(true);
						bar.updateProgress(model.getProgress() * .01,
								model.getProgress() + "%");
						return bar;
					}
				}
			}
		};
		GridCellRenderer<JobStatusModel> startTime = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				String date = "";
				if (model.getExecuteTime() > 0) {
					date = Utils.formatDateToServerTime(new Date(model
							.getExecuteTime()));
				}
				return Utils.createLabelField(date);
			}
		};
		GridCellRenderer<JobStatusModel> elapsedTime = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				if (model.getJobID() == -1) {
					return Utils.createLabelField("");
				}
				String time = "";
				if (model.getElapsedTime() > 0) {
					time = Utils.seconds2String(model.getElapsedTime() / 1000);
				}
				return Utils.createLabelField(time);
			}
		};
		GridCellRenderer<JobStatusModel> processedData = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				if (model.getJobID() == -1 || model.getProcessedData() == 0) {
					return Utils.createLabelField("");
				} else {
					return Utils.createLabelField(Utils.bytes2String(model
							.getProcessedData()));
				}
			}
		};
		GridCellRenderer<JobStatusModel> readThroughput = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				if (model.getJobID() == -1) {
					return Utils.createLabelField("");
				} else {
					JobType jobType = JobType.parse(model.getJobType());
					if (jobType.equals(JobType.BACKUP_FULL)
							|| jobType.equals(JobType.BACKUP_INCREMENTAL)
							|| jobType.equals(JobType.BACKUP_VERIFY)
							|| jobType.equals(JobType.BACKUP)) {
						if (model.getThroughput() == 0) {
							return Utils.createLabelField("");
						} else {
							return Utils.createLabelField(NumberFormat
									.getFormat("0.00").format(
											((double) model.getThroughput())
													/ (1024 * 1024)));
						}
					} else {
						return Utils.createLabelField(UIContext.Constants.NA());
					}
				}
			}
		};

		GridCellRenderer<JobStatusModel> writeThroughput = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				if (model.getJobID() == -1) {
					return Utils.createLabelField("");
				} else {
					JobType jobType = JobType.parse(model.getJobType());
					if ((model.backupLocationInfoMode != null && model.backupLocationInfoMode
							.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER)
							&& (jobType.equals(JobType.BACKUP_FULL)
									|| jobType
											.equals(JobType.BACKUP_INCREMENTAL)
									|| jobType.equals(JobType.BACKUP_VERIFY) || jobType
										.equals(JobType.BACKUP))) {
						return Utils.createLabelField(UIContext.Constants.NA());
					} else {
						if (model.getWriteThroughput() == 0) {
							return Utils.createLabelField("");
						} else {
							return Utils.createLabelField(NumberFormat
									.getFormat("0.00").format(
											((double) model
													.getWriteThroughput())
													/ (1024 * 1024)));
						}
					}
				}
			}
		};
		GridCellRenderer<JobStatusModel> path = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(final JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				if (model.backupLocationInfoMode == null) {
					return Utils.createLabelField("");
				} else {
					return Utils.createLabelField(model.backupLocationInfoMode
							.getDisplayName());
				}
			}
		};
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("jobName",
				UIContext.Constants.jobName(), 200, jobName));
		configs.add(Utils.createColumnConfig("jobId",
				UIContext.Constants.jobID(), 50, jobID));
		// configs.add(Utils.createColumnConfig("jobName",
		// UIContext.Constants.jobName(),100,jobName));
		configs.add(Utils.createColumnConfig("jobType",
				UIContext.Constants.jobType(), 70, jobType));
		configs.add(Utils.createColumnConfig("nodeName",
				UIContext.Constants.nodeName(), 150, nodeName));
		// configs.add(Utils.createColumnConfig("jobStatus",
		// UIContext.Constants.jobStatus(),70,jobStatus));
		configs.add(Utils.createColumnConfig("jobPhase",
				UIContext.Constants.jobPhase(), 150, jobPhase));
		configs.add(Utils.createColumnConfig("jobProgress",
				UIContext.Constants.status(), 100, jobProgress));
		configs.add(Utils.createColumnConfig("startTime",
				UIContext.Constants.executionTime(), 150, startTime));
		configs.add(Utils.createColumnConfig("elapsedTime",
				UIContext.Constants.elapsedTime(), 100, elapsedTime));
		configs.add(Utils.createColumnConfig("processedData",
				UIContext.Constants.processedData(), 130, processedData));
		configs.add(Utils.createColumnConfig("readThroughput",
				UIContext.Constants.readThroughput(), 150, readThroughput));
		configs.add(Utils.createColumnConfig("writeThroughput",
				UIContext.Constants.writeThroughput(), 150, writeThroughput));
		configs.add(Utils.createColumnConfig("lastResult",
				UIContext.Constants.backupDestination(), 200, path));
		jobStatusColumnsModel = new ColumnModel(configs);

		jobStatusGrid = new BaseGrid<JobStatusModel>(gridStore,
				jobStatusColumnsModel);
		jobStatusGrid.setTrackMouseOver(true);
		jobStatusGrid.getSelectionModel()
				.setSelectionMode(SelectionMode.SINGLE);
		jobStatusGrid.setBorders(true);
		jobStatusGrid.setHeight("100%");
		jobStatusGrid.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<JobStatusModel>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<JobStatusModel> se) {
						rowClickHandler();
					}

				});
		jobStatusGrid.setLoadMask(true);
		jobStatusGrid.setStateId("pagingJobStatusTable");
		jobStatusGrid.setStateful(true);

		jobStatusGrid.setContextMenu(getMenu());
	}

	private Menu getMenu() {
		Menu menu = new Menu();

		final MenuItem autoRestoreItem = new MenuItem(
				UIContext.Constants.startAutoRestore());
		autoRestoreItem
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						JobStatusModel selected = jobStatusGrid
								.getSelectionModel().getSelectedItem();
						final int jobMethod = selected.getJobMethod();
						int type = 0;
						if (jobMethod == JobScriptModel.JOB_METHOD_INSTANT_BMR) {
							type = 1;
						}
						service.controlAutoRestoreData(
								parentTabPanel.currentServer, selected, type,
								new BaseAsyncCallback<Integer>() {
									@Override
									public void onFailure(Throwable caught) {
										super.onFailure(caught);
									}

									@Override
									public void onSuccess(Integer result) {
										if (result == 0) {
											if (jobMethod == JobScriptModel.JOB_METHOD_INSTANT_BMR) {
												Utils.showMessage(
														UIContext.Constants
																.productName(),
														MessageBox.INFO,
														UIContext.Constants
																.startAutoRestoreSucceed());
											} else {
												Utils.showMessage(
														UIContext.Constants
																.productName(),
														MessageBox.INFO,
														UIContext.Constants
																.pauseAutoRestoreSucceed());
											}
										} else {
											if (jobMethod == JobScriptModel.JOB_METHOD_INSTANT_BMR) {
												Utils.showMessage(
														UIContext.Constants
																.productName(),
														MessageBox.ERROR,
														UIContext.Constants
																.startAutoRestoreFailed());
											} else {
												Utils.showMessage(
														UIContext.Constants
																.productName(),
														MessageBox.ERROR,
														UIContext.Constants
																.pauseAutoRestoreFailed());
											}
										}
									}
								});
					}
				});
		menu.add(autoRestoreItem);
		autoRestoreItem.setVisible(false);

		MenuItem searchJobStatus = new MenuItem(
				UIContext.Constants.searchJobStatus());

		Menu searchJobStatusMenu = new Menu();

		MenuItem searchJobStatusByNodeAndJob = new MenuItem(
				UIContext.Constants.byJobName());
		searchJobStatusByNodeAndJob
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						JobStatusModel selected = jobStatusGrid
								.getSelectionModel().getSelectedItem();
						if (selected != null) {
							parentTabPanel.jobStatusTable
									.filterByNodeNameAndJobName("",
											selected.getJobName());
						}
					}
				});
		searchJobStatusMenu.add(searchJobStatusByNodeAndJob);

		MenuItem searchJobStatusByDestination = new MenuItem(
				UIContext.Constants.byBackupDestination());
		searchJobStatusByDestination
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						JobStatusModel selected = jobStatusGrid
								.getSelectionModel().getSelectedItem();
						if (selected != null
								&& selected.backupLocationInfoMode != null) {
							parentTabPanel.jobStatusTable
									.filterByBackupDestination(selected.backupLocationInfoMode
											.getSessionLocation());
						}
					}
				});
		searchJobStatusMenu.add(searchJobStatusByDestination);

		searchJobStatus.setSubMenu(searchJobStatusMenu);
		menu.add(searchJobStatus);

		MenuItem searchJobHistory = new MenuItem(
				UIContext.Constants.searchJobHistory());

		Menu searchJobHistoryMenu = new Menu();

		MenuItem searchByNodeName = new MenuItem(
				UIContext.Constants.byNodeName());
		searchByNodeName
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						JobStatusModel selected = jobStatusGrid
								.getSelectionModel().getSelectedItem();
						if (selected != null) {
							parentTabPanel.setNeedRefresh(false);
							parentTabPanel.showTabItem(HomepageTab.JOB_HISTORY);
							parentTabPanel.setNeedRefresh(true);
							parentTabPanel.historyTable
									.filterByNodeName(selected.getNodeName());
						}
					}
				});
		searchJobHistoryMenu.add(searchByNodeName);

		MenuItem searchByJobName = new MenuItem(UIContext.Constants.byJobName());
		searchByJobName
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						JobStatusModel selected = jobStatusGrid
								.getSelectionModel().getSelectedItem();
						if (selected != null) {
							parentTabPanel.setNeedRefresh(false);
							parentTabPanel.showTabItem(HomepageTab.JOB_HISTORY);
							parentTabPanel.setNeedRefresh(true);
							parentTabPanel.historyTable
									.filterByJobName(selected.getJobName());
						}
					}
				});
		searchJobHistoryMenu.add(searchByJobName);
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
						JobStatusModel selected = jobStatusGrid
								.getSelectionModel().getSelectedItem();
						if (selected != null) {
							parentTabPanel.setNeedRefresh(false);
							parentTabPanel.showTabItem(HomepageTab.JOB_LOG);
							parentTabPanel.setNeedRefresh(true);
							parentTabPanel.activityLogTable
									.filterByNodeName(selected.getNodeName());
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
						JobStatusModel selected = jobStatusGrid
								.getSelectionModel().getSelectedItem();
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

		final MenuItem searchActivityLogByJobId = new MenuItem(
				UIContext.Constants.byJobId());
		searchActivityLogByJobId
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						JobStatusModel selected = jobStatusGrid
								.getSelectionModel().getSelectedItem();
						if (selected != null && selected.getJobID() != -1) {
							parentTabPanel.setNeedRefresh(false);
							parentTabPanel.showTabItem(HomepageTab.JOB_LOG);
							parentTabPanel.setNeedRefresh(true);
							parentTabPanel.activityLogTable
									.filterByJobId(String.valueOf(selected
											.getJobID()));
						}
					}
				});
		searchActivityLogMenu.add(searchActivityLogByJobId);

		searchActivityLog.setSubMenu(searchActivityLogMenu);
		menu.add(searchActivityLog);

		menu.addListener(Events.BeforeShow, new Listener<MenuEvent>() {

			@Override
			public void handleEvent(MenuEvent be) {
				JobStatusModel selected = jobStatusGrid.getSelectionModel()
						.getSelectedItem();
				if (selected != null) {
					if (selected.getJobID() == null || selected.getJobID() < 0) {
						autoRestoreItem.setVisible(false);
						searchActivityLogByJobId.disable();
					} else {
						JobType jobType = JobType.parse(selected.getJobType());
						if (jobType == JobType.RESTORE_BMR
								|| jobType == JobType.RESTORE_VM) {
							if (selected.getJobMethod() == JobScriptModel.JOB_METHOD_INSTANT_BMR
									&& JobPhase.parse(selected.getJobPhase()) == JobPhase.READY_FOR_USE) {
								autoRestoreItem.setText(UIContext.Constants
										.startAutoRestore());
								autoRestoreItem.setVisible(true);
							} else if (selected.getJobMethod() == JobScriptModel.JOB_METHOD_INSTANT_BMR_WITH_AUTO_RESTORE
									&& selected.getJobPhase() == JobPhase.READY_FOR_USE
											.getValue()) {
								autoRestoreItem.setText(UIContext.Constants
										.pauseAutoRestore());
								autoRestoreItem.setVisible(true);
							} else {
								autoRestoreItem.setVisible(false);
							}
						} else {
							autoRestoreItem.setVisible(false);
						}

						searchActivityLogByJobId.enable();
					}
				}
			}
		});
		return menu;
	}

	private void rowClickHandler() {
		refreshJobButtonGroupInToolbar();
		refreshScheduleButtonGroupInToolbar();
	}

	public void refreshScheduleButtonGroupInToolbar() {
		JobStatusModel model = jobStatusGrid.getSelectionModel()
				.getSelectedItem();
		if (model == null || model.startTime == null) {
			parentTabPanel.toolBar.schedule.disableAll();
		} else if (model.getJobID() == JobStatus.READY.getValue().longValue()) {
			if (model.startTime.isReady()) {
				parentTabPanel.toolBar.schedule.enableHold();
			} else {
				parentTabPanel.toolBar.schedule.enableReady();
			}
		} else {
			parentTabPanel.toolBar.job.setAllEnabled(false);
		}
	}

	public void refreshJobButtonGroupInToolbar() {
		JobStatusModel model = jobStatusGrid.getSelectionModel()
				.getSelectedItem();
		if (model == null) {
			parentTabPanel.toolBar.job.setAllEnabled(false);
		} else if (model.getJobID() == JobStatusModel.JOBSCRIPT_ID) {
			if (UIContext.isRestoreMode && JobType.isBackup(model.getJobType())) {
				if (UIContext.selectedServerVersionInfo
						.getServerCapabilityModel() == null
						|| !UIContext.selectedServerVersionInfo
								.getServerCapabilityModel()
								.getEnableBackupWhenManagedByUDP()) {
					parentTabPanel.toolBar.job.setAllEnabled(false);
				} else {
					parentTabPanel.toolBar.job.enableRun();
				}
			} else {
				parentTabPanel.toolBar.job.disableCancel();
			}
		} else if (JobType.isBackup(model.getJobType())) {
			if (UIContext.isRestoreMode) {
				if (UIContext.selectedServerVersionInfo
						.getServerCapabilityModel() == null
						|| !UIContext.selectedServerVersionInfo
								.getServerCapabilityModel()
								.getEnableBackupWhenManagedByUDP()) {
					parentTabPanel.toolBar.job.setAllEnabled(false);
				} else {
					parentTabPanel.toolBar.job.enableCancel();
				}
			} else {
				parentTabPanel.toolBar.job.enableCancelAndModify();
			}
		} else {
			parentTabPanel.toolBar.job.enableCancel();
		}
	}

	/*
	 * public void addData(JobStatusModel data){ gridStore.add(data);
	 * jobStatusGrid.reconfigure(gridStore, jobStatusColumnsModel); }
	 * 
	 * public void refreshData(ServiceInfoModel d2dServer, List<JobStatusModel>
	 * list) { if(d2dServer==null){ return; }else
	 * if(currentServer!=null&&d2dServer
	 * .getServer().equals(currentServer.getServer())){ for(int i=0;
	 * i<list.size();i++){ JobStatusModel data=list.get(i); boolean
	 * isExist=false; for(int j=0;j<gridStore.getCount();j++){ JobStatusModel
	 * status=gridStore.getAt(j);
	 * if(status.getJobUuid().equals(data.getJobUuid())){ isExist=true;
	 * status.setJobID(data.getJobID()); status.setJobName(data.getJobName());
	 * status.setJobStatus(data.getJobStatus());
	 * status.setJobPhase(data.getJobPhase());
	 * status.setProgress(data.getProgress());
	 * status.setJobType(data.getJobType());
	 * status.setExecuteTime(data.getExecuteTime());
	 * status.setElapsedTime(data.getElapsedTime());
	 * status.setProcessedData(data.getProcessedData());
	 * status.setThroughput(data.getThroughput());
	 * status.setLastResult(data.getLastResult());
	 * status.setVolume(data.getVolume()); status.backupLocationInfoMode =
	 * data.backupLocationInfoMode; status.setNodeName(data.getNodeName());
	 * status.setNodePassword(data.getNodePassword());
	 * status.startTime=data.startTime; } } if(!isExist){ //
	 * gridStore.add(data); gridStore.insert(data,i); } } //delete restore job
	 * status for(int i=0;i<gridStore.getCount();i++){ JobStatusModel
	 * status=gridStore.getAt(i); boolean isExist=false; for(JobStatusModel
	 * data: list){ if(status.getJobUuid().equals(data.getJobUuid())){
	 * isExist=true; break; } } if(!isExist){ gridStore.remove(status); } }
	 * }else{ gridStore.removeAll(); gridStore.add(list);
	 * currentServer=d2dServer; }
	 * 
	 * jobStatusGrid.reconfigure(gridStore, jobStatusColumnsModel); //
	 * jobStatusGrid.getView().refresh(false);
	 * if(parentTabPanel.presentTabIndex.equals(HomepageTab.JOB_STATUS)){
	 * refreshJobButtonGroupInToolbar(); refreshScheduleButtonGroupInToolbar();
	 * } }
	 */

	public JobStatusModel getSelectedJobStatusModel() {
		return jobStatusGrid.getSelectionModel().getSelectedItem();
	}

	public void runJob() {
		final JobStatusModel model = jobStatusGrid.getSelectionModel()
				.getSelectedItem();
		if (model.getJobType() == JobType.BACKUP.getValue()
				|| model.getJobType() == JobType.BACKUP_FULL.getValue()
				|| model.getJobType() == JobType.BACKUP_INCREMENTAL.getValue()
				|| model.getJobType() == JobType.BACKUP_VERIFY.getValue()) {

			RunNowWindow runNowWindow = new RunNowWindow(
					parentTabPanel.currentServer, parentTabPanel, model);
			runNowWindow.setModal(true);
			runNowWindow.show();
			// runNowWindow.changeSettings(isCompressionLevelChagned);
		} else {
			service.runJob(parentTabPanel.currentServer, model, false,
					new BaseAsyncCallback<Long>() {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							parentTabPanel.toolBar.job.runJob.enable();

						}

						@Override
						public void onSuccess(Long result) {
							parentTabPanel.toolBar.job.enableCancel();
							if (result == 0) {
								parentTabPanel.refreshJobStatusTable(true);
								// Utils.showMessage(UIContext.Constants.productName(),MessageBox.INFO,
								// UIContext.Constants.runNowWindowSubmitSuccessful());
							} else {
								Utils.showMessage(UIContext.Constants
										.productName(), MessageBox.ERROR,
										UIContext.Constants
												.runNowWindowSubmitFailed());
							}
						}
					});
		}
	}

	public void deleteJob() {
		final JobStatusModel model = jobStatusGrid.getSelectionModel()
				.getSelectedItem();
		service.deleteJob(parentTabPanel.currentServer, model, Boolean.FALSE,
				false, new BaseAsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						parentTabPanel.toolBar.job.deleteJob.enable();
					}

					@Override
					public void onSuccess(Integer result) {
						parentTabPanel.toolBar.job.setAllEnabled(false);
						if (result == 0) {
							gridStore.remove(model);
							jobStatusGrid.reconfigure(gridStore,
									jobStatusColumnsModel);
							Info.display(UIContext.Constants.productName(),
									UIContext.Constants
											.deleteJobSubmitSuccessfully());
						} else {
							String message = UIContext.Messages
									.deleteJobFailed(model.getJobName());
							if (result == 1) {
								message += UIContext.Messages.removeDriverFailed(
										model.getNodeName(),
										UIContext.Messages
												.connectFailedWrongNetwork(model
														.getNodeName()),
										UIContext.productName);
							} else if (result == 2) {
								message += UIContext.Messages.removeDriverFailed(
										model.getNodeName(),
										UIContext.Constants
												.connectFailedWrongUserAccount(),
										UIContext.productName);
							} else if (result > 0) {
								message += UIContext.Messages.removeDriverFailed(
										model.getNodeName(),
										UIContext.Constants
												.connectFailedUndefinedReason(),
										UIContext.productName);
							} else if (result < 0) {
								Utils.showMessage(
										UIContext.Constants.productName(),
										MessageBox.ERROR, message);
								return;
							}

							Listener<MessageBoxEvent> callback = new Listener<MessageBoxEvent>() {
								@Override
								public void handleEvent(MessageBoxEvent be) {
									String text = be.getButtonClicked()
											.getText();
									if (text.equals(GXT.MESSAGES
											.messageBox_yes())) {
										deleteJobBySkipRemoveDriver(model);
									}
								}
							};
							MessageBox.confirm(
									UIContext.Constants.productName(), message,
									callback);
						}
					}
				});

	}

	private void deleteJobBySkipRemoveDriver(final JobStatusModel model) {
		service.deleteJob(parentTabPanel.currentServer, model, Boolean.TRUE,
				false, new BaseAsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						parentTabPanel.toolBar.job.deleteJob.enable();
					}

					@Override
					public void onSuccess(Integer result) {
						parentTabPanel.toolBar.job.setAllEnabled(false);
						if (result == 0) {
							gridStore.remove(model);
							jobStatusGrid.reconfigure(gridStore,
									jobStatusColumnsModel);
							Info.display(UIContext.Constants.productName(),
									UIContext.Constants
											.deleteJobSubmitSuccessfully());
						} else {
							Utils.showMessage(
									UIContext.Constants.productName(),
									MessageBox.ERROR,
									UIContext.Messages.deleteJobFailed(model
											.getJobName()));
						}
					}
				});
	}

	public void cancelJob() {
		final JobStatusModel model = jobStatusGrid.getSelectionModel()
				.getSelectedItem();
		if (JobType.isBackup(model.getJobType())) {
			CancelJobWindow cancelWindow = new CancelJobWindow(
					parentTabPanel.currentServer, parentTabPanel, model);
			cancelWindow.setModal(true);
			cancelWindow.show();
		} else {
			MessageBox.confirm(UIContext.Constants.cancel(),
					UIContext.Constants.cancelJobConfirm(),
					new Listener<MessageBoxEvent>() {

						@Override
						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId()
									.equals(Dialog.YES)) {
								service.cancelJob(parentTabPanel.currentServer,
										model, false,
										new BaseAsyncCallback<Integer>() {

											@Override
											public void onFailure(
													Throwable caught) {
												super.onFailure(caught);
												// parentTabPanel.toolBar.job.cancelJob.enable();
											}

											@Override
											public void onSuccess(Integer result) {
												// parentTabPanel.toolBar.job.disableAll();
												if (result == 0) {
													parentTabPanel
															.refreshJobStatusTable(true);
													Info.display(
															UIContext.Constants
																	.productName(),
															UIContext.Constants
																	.cancelJobSubmitSuccessfully());
												} else {
													Utils.showMessage(
															UIContext.Constants
																	.productName(),
															MessageBox.ERROR,
															UIContext.Constants
																	.failToCancelJob());
												}
											}

										});
							} else {
								be.cancelBubble();
							}
						}

					});

		}
	}

	public void holdJobSchedule(boolean ready) {
		final JobStatusModel model = jobStatusGrid.getSelectionModel()
				.getSelectedItem();
		service.holdJobSchedule(parentTabPanel.currentServer, model, ready,
				new BaseAsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(Integer result) {
						// parentTabPanel.toolBar.job.disableAll();
						if (result == 0) {
							parentTabPanel.refreshJobStatusTable(true);
						} else {
							Utils.showMessage(
									UIContext.Constants.productName(),
									MessageBox.ERROR,
									"failed to hold job schedule!");
						}
					}

				});

	}

	private void triggerMigration(final JobStatusModel model) {
		service.getRestoreJobScriptByUUID(parentTabPanel.currentServer,
				model.getJobUuid(), new BaseAsyncCallback<RestoreModel>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(RestoreModel result) {
						if (result != null) {
							if (result.getAttachedRestoreType() == RestoreType.VM) {
								showConfirmDialog(model);
							} else {
								MigrationSourceHostWindow sourceHostWindow = new MigrationSourceHostWindow(
										model, parentTabPanel.currentServer);
								sourceHostWindow.setModal(true);
								sourceHostWindow.show();
							}
						} else {
							Utils.showMessage(
									UIContext.Constants.productName(),
									MessageBox.ERROR, UIContext.Constants
											.sourceRestoreJobNotExist());
						}
					}

				});
	}

	private void showConfirmDialog(final JobStatusModel model) {
		final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId()
						.equals(com.extjs.gxt.ui.client.widget.Dialog.YES)) {
					runMigration(model);
				}
			}
		};

		MessageBox mb = new MessageBox();
		mb.setIcon(MessageBox.WARNING);
		mb.setButtons(MessageBox.YESNO);
		mb.setTitle(UIContext.productName);
		mb.setMessage(UIContext.Constants.migrationDataConfirm());
		mb.setMinWidth(350);
		mb.addCallback(l);
		Utils.setMessageBoxDebugId(mb);
		mb.show();

	}

	private void runMigration(JobStatusModel model) {
		service.startMigrateData(parentTabPanel.currentServer, null, model,
				new BaseAsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(Integer result) {
						if (result == 0) {
							Utils.showMessage(UIContext.productName,
									MessageBox.INFO, UIContext.Constants
											.startMigrationSuccessfully());
						} else {
							Utils.showMessage(UIContext.productName,
									MessageBox.ERROR,
									UIContext.Constants.startMigrationFailed());
						}
					}
				});
	}

	public void refreshTable() {
		toolBar.setLoadMask(true);
		jobStatusGrid.setLoadMask(true);
		toolBar.refresh();
	}

	public void resetFilter() {
		if (filterBar != null)
			filterBar.resetFilter();
	}

	public void refreshTableWithoutMask() {
		toolBar.setLoadMask(false);
		jobStatusGrid.setLoadMask(false);
		toolBar.refresh();
		toolBar.setLoadMask(true);
		jobStatusGrid.setLoadMask(true);
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

	public void filterByNodeName(String nodeName) {
		filterBar.show();
		filterBar.filterByNodename(nodeName);
	}

	public void filterByNodeNameAndJobName(String nodeName, String jobName) {
		filterBar.show();
		filterBar.filterByNodeNameAndJobName(nodeName, jobName);
	}

	public void filterByBackupDestination(String destination) {
		filterBar.show();
		filterBar.filterByBackupDestination(destination);
	}

	public void filterByJobType(int jobType, boolean needRefresh) {
		filterBar.show();
		filterBar.filterByJobType(jobType, needRefresh);
	}

	public void resetData() {
		if (jobStatusGrid != null)
			jobStatusGrid.getStore().removeAll();
	}
}
