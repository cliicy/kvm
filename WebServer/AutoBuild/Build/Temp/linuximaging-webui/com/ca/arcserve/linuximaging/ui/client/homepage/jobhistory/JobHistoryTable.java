package com.ca.arcserve.linuximaging.ui.client.homepage.jobhistory;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.ActivityLogModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatus;
import com.ca.arcserve.linuximaging.ui.client.model.LogFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
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
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

@SuppressWarnings("deprecation")
public class JobHistoryTable extends LayoutContainer {
	public static int PAGE_LIMIT = 50;

	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private ListStore<JobStatusModel> gridStore;
	public ColumnModel jobHistoryColumnsModel;
	public Grid<JobStatusModel> jobHistoryGrid;
	private HomepageTab parentTabPanel;
	private ServiceInfoModel currentServer;
	// private NoMaskPagingToolBar toolBar;
	private PagingToolBar toolBar;
	// private PagingLoader<PagingLoadResult<JobStatusModel>> loader;
	private ActivityLogPanel activeLog;
	private List<JobStatusModel> currentSelectedItems;
	private Point scrollPos;
	private JobHistoryFilterBar filterBar;

	public JobHistoryTable(HomepageTab homepageTab) {
		this.parentTabPanel = homepageTab;
		currentServer = homepageTab.currentServer;
		final BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);
		filterBar = new JobHistoryFilterBar(this);
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		ContentPanel northContainer = new ContentPanel();
		northContainer.setLayout(new FitLayout());
		northContainer.setHeaderVisible(false);
		northContainer.setHeight("100%");

		definePagingLoad();

		GridCellRenderer<JobStatusModel> jobID = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				return model.getJobID();
			}
		};
		GridCellRenderer<JobStatusModel> jobType = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				return JobType.displayMessage(model.getJobType(),
						model.getJobMethod());
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
		// return JobStatus.displayMessage(model.getJobStatus());
		// }
		// };
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
				return date;
			}
		};
		GridCellRenderer<JobStatusModel> finishTime = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				String date = "";
				if (model.getFinishTime() > 0) {
					date = Utils.formatDateToServerTime(new Date(model
							.getFinishTime()));
				}
				return date;
			}
		};
		GridCellRenderer<JobStatusModel> processedData = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				// return Utils.bytes2String(model.getProcessedData());
				if (model.getProcessedData() == 0) {
					return Utils.createLabelField("");
				} else {
					return Utils.createLabelField(Utils.bytes2String(model
							.getWriteData()));
				}
			}
		};
		GridCellRenderer<JobStatusModel> readThroughput = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				// return
				// NumberFormat.getFormat("0.00").format(((double)model.getThroughput())/(1024*1024));
				// return Utils.bytes2MBString(model.getProcessedData());
				if (model.getThroughput() == 0) {
					return Utils.createLabelField("");
				} else {
					JobType jobType = JobType.parse(model.getJobType());
					if (jobType.equals(JobType.BACKUP_FULL)
							|| jobType.equals(JobType.BACKUP_INCREMENTAL)
							|| jobType.equals(JobType.BACKUP_VERIFY)) {
						return Utils.createLabelField(NumberFormat.getFormat(
								"0.00").format(
								((double) model.getThroughput())
										/ (1024 * 1024)));
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
				if (model.getWriteThroughput() == 0) {
					return Utils.createLabelField("");
				} else {
					JobType jobType = JobType.parse(model.getJobType());
					if ((model.backupLocationInfoMode != null
							&& model.backupLocationInfoMode
									.getServerInfoModel().getServerName() != null && !model.backupLocationInfoMode
							.getServerInfoModel().getServerName().isEmpty())
							&& (jobType.equals(JobType.BACKUP_FULL)
									|| jobType
											.equals(JobType.BACKUP_INCREMENTAL) || jobType
										.equals(JobType.BACKUP_VERIFY))) {
						return Utils.createLabelField(UIContext.Constants.NA());
					} else {
						return Utils.createLabelField(NumberFormat.getFormat(
								"0.00").format(
								((double) model.getWriteThroughput())
										/ (1024 * 1024)));
					}
				}

			}
		};
		GridCellRenderer<JobStatusModel> jobName = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(final JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				Image image = Utils.createLastResultImage(JobStatus.parse(model
						.getJobStatus()));
				LayoutContainer lc = Utils.createIconLabelField(image,
						JobStatus.displayMessage(model.getJobStatus()),
						model.getJobName());
				lc.addListener(Events.OnMouseDown, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						if (jobHistoryGrid != null) {
							jobHistoryGrid.getSelectionModel().select(model,
									false);
							rowClickHandler();
						}
					}
				});
				return lc;
			}
		};

		GridCellRenderer<JobStatusModel> backupDestination = new GridCellRenderer<JobStatusModel>() {

			@Override
			public Object render(final JobStatusModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<JobStatusModel> store, Grid<JobStatusModel> grid) {
				if (model.backupLocationInfoMode != null) {
					return Utils.createLabelField(model.backupLocationInfoMode
							.getSessionLocation());
				} else {
					return Utils.createLabelField("");
				}
			}
		};

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("jobName",
				UIContext.Constants.jobName(), 200, jobName));
		configs.add(Utils.createColumnConfig("jobId",
				UIContext.Constants.jobID(), 50, jobID));
		configs.add(Utils.createColumnConfig("jobType",
				UIContext.Constants.jobType(), 150, jobType));
		configs.add(Utils.createColumnConfig("nodeName",
				UIContext.Constants.nodeName(), 150, null));
		// configs.add(Utils.createColumnConfig("jobStatus",
		// UIContext.Constants.result(),70,jobStatus));
		// configs.add(Utils.createColumnConfig("jobPhase",
		// UIContext.Constants.jobPhase(),100,jobPhase));
		configs.add(Utils.createColumnConfig("startTime",
				UIContext.Constants.startTime(), 140, startTime));
		configs.add(Utils.createColumnConfig("finishTime",
				UIContext.Constants.finishTime(), 140, finishTime));
		configs.add(Utils.createColumnConfig("processedData",
				UIContext.Constants.dataSize(), 100, processedData));
		configs.add(Utils.createColumnConfig("readThroughput",
				UIContext.Constants.readThroughput(), 150, readThroughput));
		configs.add(Utils.createColumnConfig("writeThroughput",
				UIContext.Constants.writeThroughput(), 150, writeThroughput));
		configs.add(Utils
				.createColumnConfig("backupDestination",
						UIContext.Constants.backupDestination(), 200,
						backupDestination));
		jobHistoryColumnsModel = new ColumnModel(configs);
		jobHistoryGrid = new BaseGrid<JobStatusModel>(gridStore,
				jobHistoryColumnsModel);

		jobHistoryGrid.setTrackMouseOver(true);
		jobHistoryGrid.getSelectionModel()
				.setSelectionMode(SelectionMode.MULTI);
		jobHistoryGrid.setBorders(true);
		jobHistoryGrid.setHeight("100%");
		jobHistoryGrid.setStateId("jobHistoryTable");
		jobHistoryGrid.setStateful(true);
		jobHistoryGrid.setLoadMask(true);
		/*
		 * jobHistoryGrid.addListener(Events.Attach, new
		 * Listener<GridEvent<JobStatusModel>>(){
		 * 
		 * @Override public void handleEvent(GridEvent<JobStatusModel> be) { if
		 * ( loader != null ) { loader.load(0, JobHistoryTable.PAGE_LIMIT); }
		 * }});
		 */
		jobHistoryGrid.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<JobStatusModel>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<JobStatusModel> se) {
						rowClickHandler();
					}

				});
		/*
		 * jobHistoryGrid.addListener(Events.RowClick, new Listener<BaseEvent>()
		 * {
		 * 
		 * @Override public void handleEvent(BaseEvent be) { rowClickHandler();
		 * }
		 * 
		 * });
		 */
		jobHistoryGrid.setContextMenu(getMenu());
		northContainer.add(jobHistoryGrid);
		northContainer.setBottomComponent(toolBar);

		filterBar.hide();
		BorderLayoutData north = new BorderLayoutData(LayoutRegion.NORTH);
		north.setSize(73);
		this.add(filterBar, north);

		BorderLayoutData center = new BorderLayoutData(LayoutRegion.CENTER);
		center.setCollapsible(true);
		center.setFloatable(true);
		center.setSplit(true);
		this.add(northContainer, center);

		activeLog = new ActivityLogPanel();
		BorderLayoutData south = new BorderLayoutData(LayoutRegion.SOUTH, 250,
				50, 600);
		south.setCollapsible(true);
		south.setSplit(true);
		south.setFloatable(true);
		this.add(activeLog, south);
	}

	private Menu getMenu() {
		Menu menu = new Menu();

		MenuItem searchJobHistory = new MenuItem(
				UIContext.Constants.searchJobHistory());

		Menu searchJobHistoryMenu = new Menu();

		MenuItem searchByNodeName = new MenuItem(
				UIContext.Constants.byNodeName());
		searchByNodeName
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						JobStatusModel selected = jobHistoryGrid
								.getSelectionModel().getSelectedItem();
						if (selected != null) {
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
						JobStatusModel selected = jobHistoryGrid
								.getSelectionModel().getSelectedItem();
						if (selected != null) {
							parentTabPanel.historyTable
									.filterByJobName(selected.getJobName());
						}
					}
				});
		searchJobHistoryMenu.add(searchByJobName);

		MenuItem searchByDestination = new MenuItem(
				UIContext.Constants.byBackupDestination());
		searchByDestination
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						JobStatusModel selected = jobHistoryGrid
								.getSelectionModel().getSelectedItem();
						if (selected != null
								&& selected.backupLocationInfoMode != null) {
							parentTabPanel.historyTable
									.filterByDestination(selected.backupLocationInfoMode
											.getSessionLocation());
						}
					}
				});
		searchJobHistoryMenu.add(searchByDestination);

		searchJobHistory.setSubMenu(searchJobHistoryMenu);
		menu.add(searchJobHistory);

		return menu;
	}

	private void rowClickHandler() {
		if (jobHistoryGrid.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		parentTabPanel.toolBar.job.enableDelete();
		PagingLoadConfig pagingLoadConfig = new BasePagingLoadConfig();
		pagingLoadConfig.setOffset(0);
		pagingLoadConfig.setLimit(200);
		LogFilterModel filter = new LogFilterModel();
		filter.setJobID(jobHistoryGrid.getSelectionModel().getSelectedItem()
				.getJobID());
		service.getLogList(parentTabPanel.currentServer, pagingLoadConfig,
				filter,
				new BaseAsyncCallback<PagingLoadResult<ActivityLogModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						parentTabPanel.changeContent(false);
					}

					@Override
					public void onSuccess(
							PagingLoadResult<ActivityLogModel> result) {
						parentTabPanel.changeContent(true);
						if (result != null) {
							activeLog.addData(result.getData());
						}
					}
				});
	}

	private void definePagingLoad() {
		RpcProxy<PagingLoadResult<JobStatusModel>> proxy = new RpcProxy<PagingLoadResult<JobStatusModel>>() {

			@Override
			protected void load(
					Object loadConfig,
					final AsyncCallback<PagingLoadResult<JobStatusModel>> callback) {
				if (!(loadConfig instanceof PagingLoadConfig))
					return;

				PagingLoadConfig config = (PagingLoadConfig) loadConfig;
				JobFilterModel filter = filterBar.getFilter();
				service.getJobHistoryList(
						getCurrentServer(),
						config,
						filter,
						new BaseAsyncCallback<PagingLoadResult<JobStatusModel>>() {
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								callback.onFailure(caught);
								parentTabPanel.changeContent(false);
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

		final PagingLoader<PagingLoadResult<JobStatusModel>> loader = new BasePagingLoader<PagingLoadResult<JobStatusModel>>(
				proxy);
		loader.setRemoteSort(true);

		toolBar = new NoMaskPagingToolBar(PAGE_LIMIT) {
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

			public void setActivePage(int page) {
				if (totalLength == -1) {
					if (page != activePage && page > 0) {
						doLoadRequest(--page * pageSize, pageSize);
					} else {
						pageText.setText(String.valueOf((int) activePage));
					}
					return;
				}

				if (page > pages) {
					last();
					return;
				}
				if (page != activePage && page > 0 && page <= pages) {
					doLoadRequest(--page * pageSize, pageSize);
				} else {
					pageText.setText(String.valueOf((int) activePage));
				}
			}

			@Override
			protected void onLoad(LoadEvent event) {
				super.onLoad(event);
				PagingLoadResult<?> result = event.getData();
				config = (PagingLoadConfig) event.getConfig();
				List<?> data = result.getData();
				start = result.getOffset();
				totalLength = result.getTotalLength();
				activePage = (int) Math.ceil((double) (start + pageSize)
						/ pageSize);

				int lastCount = 0;
				if (totalLength == -1 && data != null
						&& data.size() == pageSize) {
					pages = activePage + 1;
					lastCount = data.size();
				} else if (totalLength == -1) {
					if (data != null) {
						lastCount = data.size();
					} else {
						lastCount = 0;
					}
					pages = activePage;
				} else {
					pages = totalLength < pageSize ? 1 : (int) Math
							.ceil((double) totalLength / pageSize);
				}

				if (activePage > pages && totalLength > 0) {
					last();
					return;
				} else if (activePage > pages) {
					start = 0;
					activePage = 1;
				}

				pageText.setText(String.valueOf((int) activePage));

				String after = null, display = null;
				if (totalLength == -1) {
					after = "";
				} else {
					if (msgs.getAfterPageText() != null) {
						after = Format.substitute(msgs.getAfterPageText(), ""
								+ pages);
					} else {
						after = GXT.MESSAGES.pagingToolBar_afterPageText(pages);
					}
				}

				afterText.setLabel(after);

				first.setEnabled(activePage != 1);
				prev.setEnabled(activePage != 1);
				next.setEnabled(activePage != pages);
				if (totalLength == -1) {
					last.setEnabled(false);
				} else {
					last.setEnabled(activePage != pages);
				}

				int temp = 0;
				if (totalLength == -1) {
					temp = activePage == pages ? (pages - 1) * pageSize
							+ lastCount : start + pageSize;
					display = UIContext.Messages.pagingToolBar_displayMsg(
							start + 1, temp);
				} else {
					temp = activePage == pages ? totalLength : start + pageSize;
					if (msgs.getDisplayMsg() != null) {
						String[] params = new String[] { "" + (start + 1),
								"" + temp, "" + totalLength };
						display = Format.substitute(msgs.getDisplayMsg(),
								(Object[]) params);
					} else {
						display = GXT.MESSAGES.pagingToolBar_displayMsg(
								start + 1, (int) temp, (int) totalLength);
					}
				}

				String msg = display;
				if (totalLength == 0) {
					msg = msgs.getEmptyMsg();
				}
				displayText.setLabel(msg);
			}
		};
		toolBar.setAlignment(HorizontalAlignment.RIGHT);
		toolBar.setStyleAttribute("background-color", "white");
		toolBar.setShowToolTips(false);
		toolBar.bind(loader);
		toolBar.setHeight(25);

		gridStore = new ListStore<JobStatusModel>(loader);
		loader.addLoadListener(new LoadListener() {
			@Override
			public void loaderBeforeLoad(LoadEvent le) {
				if (jobHistoryGrid != null) {
					currentSelectedItems = jobHistoryGrid.getSelectionModel()
							.getSelectedItems();
					if (jobHistoryGrid.getView() != null) {
						scrollPos = jobHistoryGrid.getView().getScrollState();
					}
				}
			}

			public void loaderLoad(LoadEvent le) {
				if (currentSelectedItems != null) {
					List<JobStatusModel> selected = new ArrayList<JobStatusModel>();
					for (int i = 0; i < gridStore.getCount(); ++i) {
						JobStatusModel jobS = gridStore.getAt(i);
						for (JobStatusModel model : currentSelectedItems) {
							if (jobS.getJobID() == model.getJobID()) {
								selected.add(jobS);
							}
						}
					}
					if (selected.size() > 0) {
						jobHistoryGrid.getSelectionModel().select(selected,
								true);
					} else {
						activeLog.clearData();
					}
				} else {
					activeLog.clearData();
				}
				if (scrollPos != null && scrollPos.y != 0) {
					jobHistoryGrid.getView().getScroller()
							.setScrollLeft(scrollPos.x);
					jobHistoryGrid.getView().getScroller()
							.setScrollTop(scrollPos.y);
				}
				// toolBar.setNoMask(false);
				// jobHistoryGrid.setLoadMask(true);
			}
		});
	}

	public void refreshData(boolean noMask) {
		// toolBar.first();
		if (noMask) {
			int currentPage = toolBar.getActivePage();
			if (currentPage != 1) {
				return;
			}

			// toolBar.setNoMask(noMask);
			// jobHistoryGrid.setLoadMask(!noMask);
			toolBar.refresh();
		} else {
			toolBar.first();
		}
	}

	public void clearActivitlog() {
		activeLog.clearData();
	}

	public void searchData() {
		toolBar.first();
	}

	public void resetFilter() {
		if (filterBar != null)
			filterBar.resetFilter();
	}

	/*
	 * public void refreshData(ServiceInfoModel d2dServer, List<JobStatusModel>
	 * list){ // gridStore.removeAll(); // gridStore.add(list); //
	 * jobHistoryGrid.reconfigure(gridStore, jobHistoryColumnsModel);
	 * if(d2dServer==null){ return; }else
	 * if(currentServer!=null&&d2dServer.getServer
	 * ().equals(currentServer.getServer())){ for(int i=0; i<list.size();i++){
	 * JobStatusModel data=list.get(i); boolean isExist=false; for(int
	 * j=0;j<gridStore.getCount();j++){ JobStatusModel
	 * status=gridStore.getAt(j);
	 * if(status.getJobUuid().equals(data.getJobUuid()
	 * )&&status.getJobID().equals(data.getJobID())){ isExist=true;
	 * status.setJobName(data.getJobName());
	 * status.setJobType(data.getJobType());
	 * status.setNodeName(data.getNodeName());
	 * status.setNodePassword(data.getNodePassword());
	 * status.setJobStatus(data.getJobStatus());
	 * status.setJobPhase(data.getJobPhase());
	 * status.setProgress(data.getProgress());
	 * status.setExecuteTime(data.getExecuteTime());
	 * status.setElapsedTime(data.getElapsedTime());
	 * status.setProcessedData(data.getProcessedData());
	 * status.setThroughput(data.getThroughput());
	 * status.setLastResult(data.getLastResult());
	 * status.setBackupDestination(data.getBackupDestination()); break; } }
	 * if(!isExist){ // gridStore.add(data); gridStore.insert(data,i); } }
	 * //delete restore job status for(int i=0;i<gridStore.getCount();i++){
	 * JobStatusModel status=gridStore.getAt(i); boolean isExist=false;
	 * for(JobStatusModel data: list){
	 * if(status.getJobUuid().equals(data.getJobUuid())){ isExist=true; break; }
	 * } if(!isExist){ gridStore.remove(status); } } }else{
	 * gridStore.removeAll(); gridStore.add(list); currentServer=d2dServer; }
	 * 
	 * jobHistoryGrid.reconfigure(gridStore, jobHistoryColumnsModel); }
	 */

	public void deleteJob() {
		final List<JobStatusModel> models = jobHistoryGrid.getSelectionModel()
				.getSelectedItems();
		if (models == null || models.size() == 0) {
			return;
		}
		service.deleteJobHistory(getCurrentServer(), models,
				new BaseAsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						// parentTabPanel.toolBar.job.deleteJob.enable();
						super.onFailure(caught);
						parentTabPanel.changeContent(false);
					}

					@Override
					public void onSuccess(Integer result) {
						// parentTabPanel.toolBar.job.deleteJob.enable();
						parentTabPanel.changeContent(true);
						if (result == 0) {
							int size = toolBar.getTotalPages();
							if (size > 1) {
								refreshData(false);
							} else {
								for (JobStatusModel jobS : models) {
									gridStore.remove(jobS);
									if (currentSelectedItems != null) {
										List<JobStatusModel> selected = new ArrayList<JobStatusModel>();
										for (JobStatusModel model2 : currentSelectedItems) {
											if (jobS.getJobID() == model2
													.getJobID()) {
												selected.add(jobS);
											}
										}
										if (selected.size() > 0) {
											jobHistoryGrid.getSelectionModel()
													.select(selected, true);
										} else {
											activeLog.clearData();
										}
									} else {
										activeLog.clearData();
									}
								}
								jobHistoryGrid.reconfigure(gridStore,
										jobHistoryColumnsModel);
							}
						} else {
							Utils.showMessage(
									UIContext.Constants.productName(),
									MessageBox.ERROR,
									"failed to delete job history!");
						}

					}

				});
	}

	public void refreshJobButtonGroupInToolbar() {
		JobStatusModel model = jobHistoryGrid.getSelectionModel()
				.getSelectedItem();
		if (model == null) {
			parentTabPanel.toolBar.job.setAllEnabled(false);
		} else {
			parentTabPanel.toolBar.job.enableDelete();
		}
		if (filterBar.isNeedShow()) {
			filterBar.show();
			filterBar.setNeedShow(false);
		}
	}

	private ServiceInfoModel getCurrentServer() {
		currentServer = this.parentTabPanel.currentServer;
		return currentServer;
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

	public class NoMaskPagingToolBar extends PagingToolBar {

		private boolean noMask = false;
		private boolean savedEnableState;

		public boolean isNoMask() {
			return noMask;
		}

		public void setNoMask(boolean noMask) {
			this.noMask = noMask;
		}

		public NoMaskPagingToolBar(int pageSize) {
			super(pageSize);
		}

		@Override
		public void bind(PagingLoader<?> loader) {
			if (this.loader != null) {
				this.loader.removeLoadListener(loadListener);
			}
			this.loader = loader;
			if (loader != null) {
				loader.setLimit(pageSize);
				if (loadListener == null) {
					loadListener = new LoadListener() {
						public void loaderBeforeLoad(final LoadEvent le) {
							if (!isNoMask()) {
								savedEnableState = isEnabled();
								setEnabled(false);
								refresh.setIcon(IconHelper
										.createStyle("x-tbar-loading"));
							}
							DeferredCommand.addCommand(new Command() {
								public void execute() {
									if (le.isCancelled()) {
										refresh.setIcon(getImages()
												.getRefresh());
										if (!isNoMask()) {
											setEnabled(savedEnableState);
										}
									}
								}
							});
						}

						public void loaderLoad(LoadEvent le) {
							refresh.setIcon(getImages().getRefresh());
							if (!isNoMask()) {
								setEnabled(savedEnableState);
							}
							onLoad(le);
						}

						public void loaderLoadException(LoadEvent le) {
							refresh.setIcon(getImages().getRefresh());
							if (!isNoMask()) {
								setEnabled(savedEnableState);
							}
						}
					};
				}
				loader.addLoadListener(loadListener);
			}
		}
	}

	public void filterByJobName(String jobName) {
		filterBar.show();
		filterBar.filterByJobName(jobName);
	}

	public void filterByNodeName(String nodeName) {
		filterBar.show();
		filterBar.filterByNodeName(nodeName);
	}

	public void filterByNodeNameAndJobName(String nodeName, String jobName) {
		filterBar.show();
		filterBar.filterByNodeNameAndJobName(nodeName, jobName);
	}

	public void filterByDestination(String destination) {
		filterBar.show();
		filterBar.filterByDestination(destination);
	}

	public void filterByJobType(JobType jobType, boolean needRefresh) {
		filterBar.show();
		filterBar.filterByJobType(jobType, needRefresh);
	}

	public void resetData() {
		if (jobHistoryGrid != null) {
			jobHistoryGrid.getStore().removeAll();
			activeLog.clearData();
		}
	}

}
