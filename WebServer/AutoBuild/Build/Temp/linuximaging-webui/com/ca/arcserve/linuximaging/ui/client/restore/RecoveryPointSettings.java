package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.IWizard;
import com.ca.arcserve.linuximaging.ui.client.common.PasswordField;
import com.ca.arcserve.linuximaging.ui.client.common.SessionLocationPanel;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupMachineModel;
import com.ca.arcserve.linuximaging.ui.client.model.CatalogModelType;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointItemModel;
import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.restore.browse.BrowseContext;
import com.ca.arcserve.linuximaging.ui.client.restore.browse.BrowseRecoveryPointWindow;
import com.ca.arcserve.linuximaging.ui.client.restore.browse.RestoreUtils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class RecoveryPointSettings extends LayoutContainer implements IWizard {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private ComboBox<BackupMachineModel> cmbMachine;
	private RestoreWindow restoreWindow;
	private LayoutContainer currentPanel;
	private SessionLocationPanel txtBackupLocation;
	private DateField startDate;
	private DateField endDate;
	private ListStore<RecoveryPointModel> pointGridStore;
	private ColumnModel pointColumnsModel;
	public BaseGrid<RecoveryPointModel> pointGrid;
	private LayoutContainer pointItemTable;
	private ListStore<RecoveryPointItemModel> itemGridStore;
	private ColumnModel itemColumnsModel;
	private BaseGrid<RecoveryPointItemModel> itemGrid;
	private Button connectButton;
	private Button rpsButton;
	private Button filterBtn;
	public final static int ROW_CELL_SPACE = 5;
	public final static int MAX_FIELD_WIDTH = 350;
	public final static int POINT_TABLE_HIGHT = 150;
	public final static int ITEM_TABLE_HIGHT = 200;
	public final static int ITEM_TABLE_WIDTH = 608;
	public static int MIN_BUTTON_WIDTH = 80;
	public static int RECOVERY_POINTS_LIMINT = 100;
	private String sessionLocaton;
	private boolean isRefreshPointTable = true;
	private boolean isRestoreManager = true;
	private LayoutContainer fileTreeTable;
	private BaseGrid<GridTreeNode> grid;
	private ListStore<GridTreeNode> fileStore;
	private Button addButton;
	//private final Map<String, ServerInfoModel> rpsMap = new HashMap<String, ServerInfoModel>();
	private RecoveryPointModel currentPoint;
	private SelectionChangedListener<RecoveryPointModel> pointGridListener;
	private List<BackupLocationInfoModel> ignoreLocationList;
	private BackupMachineModel orignalMachine;
	private SelectionChangedListener<BackupMachineModel> machineSelectListener;
	private String machineForMigration;
	private RecoveryPointModel rpForMigration;

	private LayoutContainer fullContainer;
	private LayoutContainer browseTable;
	private LayoutContainer points;

	SelectionListener<ButtonEvent> fileListener = new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			addButton.setEnabled(false);
			browseTable.setEnabled(false);
			final BackupLocationInfoModel sessionLocation = getBackupLocationInfoModel();

			final String machine = getMachine();
			final String machineName = getMachineName();
			final RecoveryPointModel point = pointGrid.getSelectionModel()
					.getSelectedItem();
			if (point == null) {
				Utils.showMessage(UIContext.productName, MessageBox.ERROR,
						UIContext.Constants.selectRecoveryPointMessage());
				addButton.setEnabled(true);
				return;
			}
			if (cmbMachine.getValue().getMachineType() == BackupMachineModel.TYPE_HBBU_MACHINE
					&& pointGrid.getSelectionModel().getSelectedItem()
							.getVersion() < 6.0) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.validateHbbuVersion());
				addButton.setEnabled(true);
				return;
			}
			String scriptUUID = null;
			if (restoreWindow.isModify) {
				scriptUUID = restoreWindow.restoreModel.getUuid();
			}

			final BaseAsyncCallback<Boolean> callBack = new BaseAsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
					addButton.setEnabled(true);
					browseTable.setEnabled(true);
				}

				@Override
				public void onSuccess(Boolean result) {
					if (result) {
						final BrowseContext context = new BrowseContext();
						context.setMachine(machine);
						context.setMachineName(machineName);
						context.setSessionLocation(sessionLocation);
						context.setRecoveryPoint(point);
						context.setServer(restoreWindow.currentServer);
						String scriptUUID = null;
						if (restoreWindow.isModify) {
							scriptUUID = restoreWindow.restoreModel.getUuid();
						}
						context.setRestoreType(restoreWindow.getRestoreType());
						context.setScriptUUID(scriptUUID);
						BrowseRecoveryPointWindow window = new BrowseRecoveryPointWindow(
								RecoveryPointSettings.this, context);
						window.setSelectedNode(fileStore.getModels());
						window.setModal(true);
						window.show();
					}
				}
			};

			service.checkRecoveryPointPasswd(
					restoreWindow.toolBar.tabPanel.currentServer,
					sessionLocation, machine, point, scriptUUID,
					new BaseAsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							callBack.onFailure(caught);
						}

						@Override
						public void onSuccess(Boolean result) {
							addButton.setEnabled(true);
							browseTable.setEnabled(true);

							if (result) {
								if (point.getHbbuPath() != null) {
									service.getRecoveryPointFromSession(
											restoreWindow.toolBar.tabPanel.currentServer,
											getBackupLocationInfoModel(),
											pointGrid.getSelectionModel()
													.getSelectedItem(),
											getMachine(),
											new BaseAsyncCallback<RecoveryPointModel>() {
												@Override
												public void onFailure(
														Throwable caught) {
													callBack.onFailure(caught);
												}

												@Override
												public void onSuccess(
														RecoveryPointModel result) {
													point.setCompression(result
															.getCompression());
													point.items = result.items;
													callBack.onSuccess(true);
												}
											});
								} else {
									callBack.onSuccess(true);
								}
							} else {
								showEncryptionPasswordMessageBox(point,
										UIContext.Constants.invalidPassword());
							}
						}

					});

		}

	};

	public RecoveryPointSettings(RestoreWindow window) {
		this.restoreWindow = window;
		currentPanel = window.rightPanel;
		TableLayout layout = new TableLayout();
		layout.setWidth("97%");
		layout.setColumns(3);
		layout.setCellPadding(3);
		layout.setCellSpacing(3);
		setLayout(layout);

		TableData tdColspan = new TableData();
		tdColspan.setColspan(3);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField head = new LabelField(
				UIContext.Constants.selectRecoveryPoint());
		if(window.getRestoreType() == RestoreType.MIGRATION_BMR){
			head = new LabelField(
					UIContext.Constants.selectSameRecoveryPoint());
		}
		head.setStyleAttribute("font-weight", "bold");
		add(head, tdColspan);

		TableData tdLabel = new TableData();

		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();

		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdButton = new TableData();

		tdButton.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField lblBackupLocation = new LabelField(
				UIContext.Constants.backupLocationLabel());
		lblBackupLocation.setStyleAttribute("white-space", "nowrap");

		txtBackupLocation = new SessionLocationPanel(restoreWindow
				.getRestoreType().getValue(), MAX_FIELD_WIDTH, this);

		connectButton = new Button(UIContext.Constants.connect());
		connectButton.setStyleAttribute("padding-right", "2px");
		connectButton.setIcon(AbstractImagePrototype
				.create(UIContext.IconBundle.connect()));
		connectButton.setWidth(MIN_BUTTON_WIDTH);
		connectButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						connectToBackupDestination();
					}
				});
		add(lblBackupLocation, tdLabel);
		add(txtBackupLocation, tdField);
		add(connectButton, tdButton);

		LabelField lblMachine = new LabelField(UIContext.Constants.machine());
		ListStore<BackupMachineModel> machineStore = new ListStore<BackupMachineModel>();
		cmbMachine = new ComboBox<BackupMachineModel>();
		cmbMachine.setStore(machineStore);
		cmbMachine.setDisplayField("machineName");
		cmbMachine.setEditable(false);
		cmbMachine.setTriggerAction(TriggerAction.ALL);

		cmbMachine.setWidth(MAX_FIELD_WIDTH);

		cmbMachine.addListener(Events.BeforeSelect, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				orignalMachine = cmbMachine.getValue();
			}
		});

		machineSelectListener = new SelectionChangedListener<BackupMachineModel>() {

			@Override
			public void selectionChanged(
					SelectionChangedEvent<BackupMachineModel> se) {
				final BackupMachineModel machine = se.getSelectedItem();
				if (fileStore != null && fileStore.getCount() > 0) {
					MessageBox.confirm(UIContext.Constants.reset(),
							UIContext.Constants.selectionLostWarning(),
							new Listener<MessageBoxEvent>() {

								@Override
								public void handleEvent(MessageBoxEvent be) {
									if (be.getButtonClicked().getItemId()
											.equals(Dialog.YES)) {
										removeCurrentData(null);
										refreshTargetMachine();
										refreshMachineRP(machine);
									} else {
										if (orignalMachine != null) {
											if (machineSelectListener != null) {
												cmbMachine
														.removeSelectionListener(machineSelectListener);
											}
											cmbMachine.setValue(orignalMachine);
											if (machineSelectListener != null) {
												cmbMachine
														.addSelectionChangedListener(machineSelectListener);
											}
										}
									}
								}
							});
				} else {
					refreshMachineRP(machine);
					refreshTargetMachine();
				}
			}

		};
		cmbMachine.addSelectionChangedListener(machineSelectListener);
		rpsButton = new Button(UIContext.Constants.rps());
		rpsButton.hide();
		add(lblMachine, tdLabel);
		add(cmbMachine, tdField);
		add(rpsButton, tdButton);

		defineRecoveryPointFilter();
		setDateFieldServerDate();

		LayoutContainer points = definePointTable();
		add(points, tdColspan);

		fullContainer = new LayoutContainer();
		browseTable = new LayoutContainer();
		browseTable.setLayout(new TableLayout());

		fullContainer.setLayout(new FitLayout());

		defineBrowseTable();
		fullContainer.add(browseTable);

		defineFileTreeTable();
		fullContainer.add(fileTreeTable);

		definePointItemTable();
		fullContainer.add(pointItemTable);

		fullContainer.add(browseTable);
		add(fullContainer, tdColspan);
	}

	private void setDateFieldServerDate() {
		service.getCurrentD2DTimeFromServer(
				restoreWindow.toolBar.tabPanel.currentServer,
				new BaseAsyncCallback<D2DTimeModel>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(D2DTimeModel result) {
						Date ServerDate = Utils.convertD2DTimeModel(result);
						startDate.setServerDate(ServerDate);
						endDate.setServerDate(ServerDate);
					}

				});
	}

	private void refreshMachineRP(final BackupMachineModel machine) {

		if (machine != null && machine.getLastDate() != null) {
			endDate.setValue(machine.getLastDate());
			startDate.setValue(addDaysToDate(machine.getLastDate(), -14));
		} else {
			endDate.setValue(null);
			startDate.setValue(null);
		}

		if (isRefreshPointTable && machine != null && !machine.equals("")) {
			refreshPointTable(machine.getMachinePath());
		} else {
			isRefreshPointTable = true;
		}
	}

	private void refreshTargetMachine() {
		if (restoreWindow.getRestoreType() == RestoreType.FILE) {
			restoreWindow.restoreModel.setMachine(getTargetMachine());
			restoreWindow.getTargetMachineSettings().refreshPart();
		}
		if (restoreWindow.getRestoreType() == RestoreType.BMR) {
			restoreWindow.getTargetMachineSettings().refreshPart();
		}
	}

	private Date addDaysToDate(Date date, int days) {
		if (date == null)
			return null;
		Date newDate = new Date(date.getTime());
		CalendarUtil.addDaysToDate(newDate, days);
		return newDate;
	}

	private void connectToBackupDestination() {
		if (fileStore != null && fileStore.getCount() > 0) {
			MessageBox.confirm(UIContext.Constants.reset(),
					UIContext.Constants.fileSelectionLostWarning(),
					new Listener<MessageBoxEvent>() {

						@Override
						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId()
									.equals(Dialog.YES)) {
								fileStore.removeAll();
								refreshMachineList(null);
							} else {
								be.cancelBubble();
							}
						}

					});
		} else {
			refreshMachineList(null);
		}
	}

	private void refreshMachineList(final String defaultMachine) {
		if (!checkSessionLocation(SessionLocationPanel.EVENT_TYPE_CONNECT)) {
			return;
		} else if (restoreWindow.getRestoreType().getValue() == RestoreType.BMR
				.getValue()
				&& UIContext.selectedServerVersionInfo.isLiveCD()
				&& !Utils.validateIPAddress(Utils
						.getMachineAddress(txtBackupLocation.getValue()))
				&& txtBackupLocation.getLocationType() != BackupLocationInfoModel.TYPE_AMAZON_S3) {// not
																			// support
																			// hostname
																			// in
																			// current
																			// version
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR,
					UIContext.Constants.validateSessionLocationMessage());
		} else {
			connectButton.disable();
			currentPanel.mask(UIContext.Constants.loading());
			reconfigurePointGrid(txtBackupLocation.getBackupLocationInfo()
					.getType());
			service.getMachineList(
					restoreWindow.toolBar.tabPanel.currentServer,
					txtBackupLocation.getBackupLocationInfo(),
					restoreWindow.getRestoreType() == RestoreType.FILE,
					new BaseAsyncCallback<List<BackupMachineModel>>() {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							connectButton.enable();
							currentPanel.unmask();
						}

						@Override
						public void onSuccess(List<BackupMachineModel> result) {
							sessionLocaton = txtBackupLocation.getValue();
							connectButton.enable();
							currentPanel.unmask();
							refreshCmbMachine(result, defaultMachine);
						}

					});
		}
	}

	protected void refreshCmbMachine(List<BackupMachineModel> list,
			String defaultMachine) {
		cmbMachine.getStore().removeAll();
		if (list != null && list.size() > 0) {
			if (machineForMigration != null) {
				if (!(list.size() == 1 && list.get(0).getMachineName() == null)) {
					for (BackupMachineModel m : list) {
						if (m.getMachinePath().contains(machineForMigration)) {
							cmbMachine.getStore().add(m);
							cmbMachine.setValue(m);
							break;
						}
					}
				}
			} else {
				if (!(list.size() == 1 && list.get(0).getMachineName() == null)) {
					cmbMachine.getStore().add(list);
				}
				if (!Utils.isEmptyOrNull(defaultMachine)) {
					for (BackupMachineModel machine : list) {
						if (machine.getMachinePath().contains(defaultMachine)) {
							cmbMachine.setValue(machine);
							break;
						}
					}
				} else {
					cmbMachine.setValue(list.get(0));
				}
			}
		} else {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.INFO, UIContext.Constants.noMachine());
			pointGridStore.removeAll();
			pointGrid.reconfigure(pointGridStore, pointColumnsModel);
			emptyPointItems();
		}
	}

	public void checkRecoveryPointPwAndRoot(
			final BaseAsyncCallback<Boolean> callBack) {
		service.checkRecoveryPointPasswd(
				restoreWindow.toolBar.tabPanel.currentServer,
				getBackupLocationInfoModel(), getMachine(), pointGrid
						.getSelectionModel().getSelectedItem(),
				restoreWindow.restoreModel.getUuid(),
				new BaseAsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						callBack.onFailure(caught);
					}

					@Override
					public void onSuccess(Boolean result) {
						if (result) {
							RestoreType restoreType = restoreWindow
									.getRestoreType();
							if ((restoreType.getValue() == RestoreType.BMR
									.getValue()
									|| restoreType.getValue() == RestoreType.VM
											.getValue() || restoreType
									.getValue() == RestoreType.MIGRATION_BMR
									.getValue())
									&& pointGrid.getSelectionModel()
											.getSelectedItem().getHbbuPath() == null) {
								service.getRecoveryPointFromSession(
										restoreWindow.toolBar.tabPanel.currentServer,
										getBackupLocationInfoModel(),
										pointGrid.getSelectionModel()
												.getSelectedItem(),
										getMachine(),
										new BaseAsyncCallback<RecoveryPointModel>() {
											@Override
											public void onFailure(
													Throwable caught) {
												callBack.onFailure(caught);
											}

											@Override
											public void onSuccess(
													RecoveryPointModel result) {
												if (result
														.getRootVolumeBackup() == 0) {
													Utils.showMessage(
															UIContext.Constants
																	.productName(),
															MessageBox.ERROR,
															UIContext.Constants
																	.rootVolumeNotBackedUp());
												} else if (result
														.getBootVolumeExist() == 1
														&& result
																.getBootVolumeBackup() == 0) {
													Utils.showMessage(
															UIContext.Constants
																	.productName(),
															MessageBox.ERROR,
															UIContext.Constants
																	.bootVolumeNotBackedUp());
												} else {
													callBack.onSuccess(true);
												}
											}
										});
							} else {
								callBack.onSuccess(true);
							}

						} else {
							Utils.showMessage(
									UIContext.Constants.productName(),
									MessageBox.ERROR,
									UIContext.Constants.invalidPassword());
						}
					}
				});
	}

	protected void refreshPointTable(String machine) {
		currentPanel.mask(UIContext.Constants.loading());
		service.getRecoveryPointList(restoreWindow.currentServer,
				txtBackupLocation.getBackupLocationInfo(), machine,
				startDate.getValue(), addDaysToDate(endDate.getValue(), 1),
				new BaseAsyncCallback<List<RecoveryPointModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						currentPanel.unmask();
					}

					@Override
					public void onSuccess(List<RecoveryPointModel> result) {
						currentPanel.unmask();
						pointGridStore.removeAll();
						if (result != null && result.size() > 0) {
							if (result.size() > RECOVERY_POINTS_LIMINT) {
								Utils.showMessage(
										UIContext.Constants.productName(),
										MessageBox.WARNING,
										UIContext.Messages
												.recoveryPointCountExceedLimit(RECOVERY_POINTS_LIMINT));
								result = result.subList(0,
										RECOVERY_POINTS_LIMINT);
							}
							RecoveryPointModel model = null;
							if (rpForMigration != null) {
								for (RecoveryPointModel m : result) {
									if (m.getTime().longValue() == rpForMigration.getTime().longValue()) {
										pointGridStore.add(m);
										model = m;
										break;
									}
								}
							} else {
								pointGridStore.add(result);
								model = result.get(0);
							}
							pointGrid.reconfigure(pointGridStore,
									pointColumnsModel);
							pointGrid.getSelectionModel().select(model, false);
							refreshPointItems(model);
						} else {
							Utils.showMessage(
									UIContext.Constants.productName(),
									MessageBox.ERROR,
									UIContext.Constants.noRecoveryPoint());
							emptyPointItems();
						}
					}

				});
	}

	protected void emptyPointItems() {
		RestoreType restoreType = restoreWindow.getRestoreType();
		if (restoreType.getValue() == RestoreType.BMR.getValue()
				|| restoreType.getValue() == RestoreType.VM.getValue()
				|| restoreType.getValue() == RestoreType.MIGRATION_BMR
						.getValue()) {
			pointItemTable.show();
			fileTreeTable.hide();
			itemGridStore.removeAll();
		} else if (restoreType.getValue() == RestoreType.VOLUME.getValue()) {
			// TO_DO
		} else if (restoreType.getValue() == RestoreType.SHARE_RECOVERY_POINT
				.getValue()) {
			pointItemTable.hide();
			fileTreeTable.hide();
			browseTable.setEnabled(true);
			points.setHeight(210);
			pointGrid.setHeight(210);
			fileStore.removeAll();
		} else if (restoreType.getValue() == RestoreType.FILE.getValue()) {
			pointItemTable.hide();
			fileTreeTable.setEnabled(true);
			fileStore.removeAll();
		}
	}

	protected void refreshPointItems(RecoveryPointModel model) {
		currentPoint = model;
		RestoreType restoreType = restoreWindow.getRestoreType();
		if (restoreType.getValue() == RestoreType.BMR.getValue()
				|| restoreType.getValue() == RestoreType.VM.getValue()
				|| restoreType.getValue() == RestoreType.MIGRATION_BMR
						.getValue()) {
			pointItemTable.show();
			fileTreeTable.hide();
			browseTable.hide();
			itemGridStore.removeAll();
		} else if (restoreType.getValue() == RestoreType.VOLUME.getValue()) {
			// TO_DO
		} else if (restoreType.getValue() == RestoreType.SHARE_RECOVERY_POINT
				.getValue()) {
			pointItemTable.hide();
			fileTreeTable.hide();
			points.layout();
			browseTable.setEnabled(true);
		} else if (restoreType.getValue() == RestoreType.FILE.getValue()) {
			pointItemTable.hide();
			browseTable.hide();
			fileTreeTable.setEnabled(true);
			if (model != null && model.files != null && model.files.size() > 0) {
				showFileView(model.files);
			}
		}

	}

	private void refreshPointItemsGrid(RecoveryPointModel model) {
		pointItemTable.show();
		fileTreeTable.hide();
		browseTable.hide();
		itemGridStore.removeAll();
		itemGrid.mask(UIContext.Constants.loading());
		service.getDiskInfo(restoreWindow.currentServer, txtBackupLocation
				.getBackupLocationInfo(), cmbMachine.getValue()
				.getMachinePath(), model,
				new BaseAsyncCallback<List<RecoveryPointItemModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						currentPanel.unmask();
						itemGrid.unmask();
					}

					@Override
					public void onSuccess(List<RecoveryPointItemModel> result) {
						itemGridStore.removeAll();
						itemGrid.unmask();
						if (result != null) {
							itemGridStore.add(result);
							itemGrid.reconfigure(itemGridStore,
									itemColumnsModel);
							itemGrid.getView()
									.getBody()
									.setStyleAttribute("height",
											ITEM_TABLE_HIGHT - 45 + "px");
						}
					}

				});

	}

	private void showFileView(List<GridTreeNode> files) {
		fileStore.add(files);
		grid.reconfigure(fileStore, grid.getColumnModel());
	}

	private void defineRecoveryPointFilter() {
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);

		TableData td1 = new TableData();
		td1.setHorizontalAlign(HorizontalAlignment.RIGHT);

		LabelField label = new LabelField(UIContext.Constants.filter());
		add(label, td);

		LayoutContainer container = new LayoutContainer();
		container.setWidth(422);
		TableLayout dateLayout = new TableLayout(4);
		dateLayout.setWidth("83%");
		container.setLayout(dateLayout);

		label = new LabelField(UIContext.Constants.start());
		container.add(label, td);

		startDate = new DateField();
		startDate.setWidth(140);

		container.add(startDate, td);

		label = new LabelField(UIContext.Constants.end());
		container.add(label, td1);

		endDate = new DateField();
		endDate.setWidth(140);

		container.add(endDate, td1);
		add(container);
		filterBtn = new Button(UIContext.Constants.restoreFind());
		filterBtn.setIcon(UIContext.IconHundle.search());
		filterBtn.setMinWidth(MIN_BUTTON_WIDTH);
		filterBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (cmbMachine.getValue() == null) {
					Utils.showMessage(UIContext.Constants.productName(),
							MessageBox.ERROR,
							UIContext.Constants.machineMessage());
					return;
				}
				if (startDate.getValue() != null && endDate.getValue() != null) {
					if (startDate.getValue().getTime() > endDate.getValue()
							.getTime()) {
						Utils.showMessage(UIContext.Constants.productName(),
								MessageBox.ERROR,
								UIContext.Constants.endDateBeforeStartDate());
						return;
					}
				}
				if (fileStore != null && fileStore.getCount() > 0) {
					MessageBox.confirm(UIContext.Constants.reset(),
							UIContext.Constants.fileSelectionLostWarning(),
							new Listener<MessageBoxEvent>() {

								@Override
								public void handleEvent(MessageBoxEvent be) {
									if (be.getButtonClicked().getItemId()
											.equals(Dialog.YES)) {
										fileStore.removeAll();
										refreshPointTable(cmbMachine.getValue()
												.getMachinePath());
									} else {
										be.cancelBubble();
									}
								}

							});
				} else {
					refreshPointTable(cmbMachine.getValue().getMachinePath());
				}

			}
		});

		add(filterBtn, td);
	}

	private LayoutContainer definePointTable() {

		points = new LayoutContainer();
		points.setLayout(new FitLayout());
		points.setHeight(POINT_TABLE_HIGHT);

		pointGridStore = new ListStore<RecoveryPointModel>();
		GridCellRenderer<RecoveryPointModel> encryptRenderer = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {

				IconButton iconButton = null;
				if (model.getEncryptAlgoName() != null
						&& !model.getEncryptAlgoName().isEmpty()) {
					iconButton = getIncryptionImage("recoverypoint_encryption_icon");
					Utils.addToolTip(iconButton, UIContext.Constants
							.restoreBrowseRecoveryPointsToolTipEncrytion());
				} else {
					iconButton = getIncryptionImage("recoverypoint_noencryption_icon");
					Utils.addToolTip(iconButton, UIContext.Constants
							.restoreBrowseRecoveryPointsToolTipNoEncrytion());
				}

				HorizontalPanel container = new HorizontalPanel();
				container.add(iconButton);
				if (model.getRecoverySetStartFlag() == 1) {
					IconButton iconStartButton = getIncryptionImage("recoverypoint_set_start_icon");
					Utils.addToolTip(
							iconStartButton,
							UIContext.Constants
									.restoreBrowseRecoveryPointsToolTipRecoverySetStart());
					container.add(iconStartButton);
				} else {
					switch (model.getBKAdvSch()) {
					case 1:
						IconButton daily = getIncryptionImage("recoverypoint_set_start_icon");
						Utils.addToolTip(
								daily,
								UIContext.Constants
										.restoreBrowseRecoveryPointsToolTipRecoverySetDaily());
						container.add(daily);
						break;
					case 2:
						IconButton weekly = getIncryptionImage("recoverypoint_set_start_icon");
						Utils.addToolTip(
								weekly,
								UIContext.Constants
										.restoreBrowseRecoveryPointsToolTipRecoverySetWeekly());
						container.add(weekly);
						break;
					case 3:
						IconButton monthly = getIncryptionImage("recoverypoint_set_start_icon");
						Utils.addToolTip(
								monthly,
								UIContext.Constants
										.restoreBrowseRecoveryPointsToolTipRecoverySetMonthly());
						container.add(monthly);
						break;
					}
				}
				if(model.getAssruedRecoveryTestResult() != null){
					if(model.getAssruedRecoveryTestResult().getResult() == 0){
						IconButton arSucceed = getIncryptionImage("assured_recovery_test_result_succeed_icon");
						Utils.addToolTip(
								arSucceed,
								UIContext.Constants
										.restoreBrowseRecoveryPointsToolTipARTestSucceed());
						container.add(arSucceed);
					}else{
						IconButton arFailed = getIncryptionImage("assured_recovery_test_result_failed_icon");
						Utils.addToolTip(
								arFailed,
								UIContext.Constants
										.restoreBrowseRecoveryPointsToolTipARTestFailed());
						container.add(arFailed);
					}
				}else{
					IconButton arNotRun = getIncryptionImage("assured_recovery_test_result_not_run_icon");
					Utils.addToolTip(
							arNotRun,
							UIContext.Constants
									.restoreBrowseRecoveryPointsToolTipARTestNotRun());
					container.add(arNotRun);
				}
				return container;
			}

			private IconButton getIncryptionImage(String style) {
				IconButton image = new IconButton(style);
				image.setWidth(20);
				return image;
			}

		};
		GridCellRenderer<RecoveryPointModel> time = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				return Utils.createLabelField(Utils.formatDate(new Date(model
						.getTime())));
			}
		};
		GridCellRenderer<RecoveryPointModel> type = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				return Utils.createLabelField(JobType.parse(
						model.getBackupType()).toString());
			}
		};
		GridCellRenderer<RecoveryPointModel> name = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				return Utils.createLabelField(Utils.getNormalRpName(model
						.getName()));
			}
		};
		GridCellRenderer<RecoveryPointModel> encryptAlgoName = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(RecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				if (model.getEncryptAlgoName() == null
						|| model.getEncryptAlgoName().isEmpty()) {
					if (model.getEncryptionPasswordHash() == null
							|| model.getEncryptionPasswordHash().isEmpty()) {
						return Utils.createLabelField("");
					} else {
						return Utils.createLabelField(UIContext.Constants.NA());
					}
				} else {
					return Utils.createLabelField(model.getEncryptAlgoName());
				}
			}
		};
		GridCellRenderer<RecoveryPointModel> encryptPwd = new GridCellRenderer<RecoveryPointModel>() {

			@Override
			public Object render(final RecoveryPointModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<RecoveryPointModel> store,
					Grid<RecoveryPointModel> grid) {
				if ((model.getEncryptionPasswordHash() != null && !model
						.getEncryptionPasswordHash().isEmpty())
						|| (model.getEncryptAlgoName() != null && !model
								.getEncryptAlgoName().isEmpty())) {
					final PasswordField txtEncryptionPassword = new PasswordField(
							100);
					if (model.getEncryptionPassword() != null
							&& !model.getEncryptionPassword().isEmpty()) {
						txtEncryptionPassword.setPasswordValue(model
								.getEncryptionPassword());
					}
					txtEncryptionPassword.setHeight(19);
					txtEncryptionPassword.addListener(Events.Change,
							new Listener<FieldEvent>() {

								@Override
								public void handleEvent(FieldEvent be) {
									String pwd = txtEncryptionPassword
											.getPasswordValue();
									model.setEncryptionPassword(pwd);
								}

							});
					txtEncryptionPassword.addListener(Events.OnClick,
							new Listener<FieldEvent>() {

								@Override
								public void handleEvent(FieldEvent be) {
									pointGrid.getSelectionModel().select(false,
											model);
								}

							});
					txtEncryptionPassword.addListener(Events.KeyDown,
							new Listener<FieldEvent>() {

								@Override
								public void handleEvent(FieldEvent be) {
									if (be.getKeyCode() == 32) {
										be.cancelBubble();
									}
								}
							});
					return txtEncryptionPassword;
				} else {
					return Utils.createLabelField("");
				}
			}
		};

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("encryptionType", "", 65,
				encryptRenderer));
		configs.add(Utils.createColumnConfig("time",
				UIContext.Constants.time(), 135, time));
		configs.add(Utils.createColumnConfig("backupType",
				UIContext.Constants.type(), 126, type));
		configs.add(Utils.createColumnConfig("name",
				UIContext.Constants.name(), 100, name));
		configs.add(Utils.createColumnConfig("encryptAlgoName",
				UIContext.Constants.encryption(), 70, encryptAlgoName));
		configs.add(Utils.createColumnConfig("encryptPwd",
				UIContext.Constants.encryptionPassword(), 110, encryptPwd));
		pointColumnsModel = new ColumnModel(configs);
		pointGrid = new BaseGrid<RecoveryPointModel>(pointGridStore,
				pointColumnsModel);
		pointGrid.setAutoExpandColumn("encryptPwd");
		pointGrid.setTrackMouseOver(true);
		pointGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		pointGrid.setBorders(true);
		pointGrid.setHeight(POINT_TABLE_HIGHT);
		pointGridListener = new SelectionChangedListener<RecoveryPointModel>() {

			@Override
			public void selectionChanged(
					SelectionChangedEvent<RecoveryPointModel> se) {
				final RecoveryPointModel model = se.getSelectedItem();
				if (model != null) {
					if (fileStore.getCount() > 0) {
						MessageBox.confirm(UIContext.Constants.reset(),
								UIContext.Constants.selectionLostWarning(),
								new Listener<MessageBoxEvent>() {

									@Override
									public void handleEvent(MessageBoxEvent be) {
										if (be.getButtonClicked().getItemId()
												.equals(Dialog.YES)) {
											removeCurrentData(model);
										} else {
											pointGrid.getSelectionModel()
													.removeSelectionListener(
															pointGridListener);
											pointGrid
													.getSelectionModel()
													.select(currentPoint, false);
											pointGrid
													.getSelectionModel()
													.addSelectionChangedListener(
															pointGridListener);
										}
									}

								});
					}
					if (restoreWindow.getRestoreType().getValue() == RestoreType.BMR
							.getValue()
							|| restoreWindow.getRestoreType().getValue() == RestoreType.VM
									.getValue()
							|| restoreWindow.getRestoreType().getValue() == RestoreType.MIGRATION_BMR
									.getValue()) {
						refreshPointItemsGrid(model);
					}
				}
			}

		};

		pointGrid.getSelectionModel().addSelectionChangedListener(
				pointGridListener);

		points.add(pointGrid);

		return points;
	}

	private void removeCurrentData(RecoveryPointModel model) {
		this.currentPoint = model;
		RestoreType restoreType = restoreWindow.getRestoreType();
		if (restoreType.getValue() == RestoreType.BMR.getValue()
				|| restoreType.getValue() == RestoreType.VM.getValue()
				|| restoreType.getValue() == RestoreType.MIGRATION_BMR
						.getValue()) {
			itemGridStore.removeAll();
		} else if (restoreType.getValue() == RestoreType.VOLUME.getValue()) {
			// TO_DO
		} else if (restoreType.getValue() == RestoreType.FILE.getValue()) {
			fileStore.removeAll();
		}
	}

	private LayoutContainer definePointItemTable() {
		pointItemTable = new LayoutContainer();
		pointItemTable.setLayout(new FitLayout());
		pointItemTable.setHeight(ITEM_TABLE_HIGHT - 20);

		itemGridStore = new ListStore<RecoveryPointItemModel>();
		GridCellRenderer<RecoveryPointItemModel> icon = new GridCellRenderer<RecoveryPointItemModel>() {

			@Override
			public Object render(RecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointItemModel> store,
					Grid<RecoveryPointItemModel> grid) {
				return new Image(UIContext.IconBundle.volume());
			}

		};
		GridCellRenderer<RecoveryPointItemModel> name = new GridCellRenderer<RecoveryPointItemModel>() {

			@Override
			public Object render(RecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointItemModel> store,
					Grid<RecoveryPointItemModel> grid) {
				return Utils.createLabelField(model.getName());
			}
		};
		GridCellRenderer<RecoveryPointItemModel> size = new GridCellRenderer<RecoveryPointItemModel>() {

			@Override
			public Object render(RecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<RecoveryPointItemModel> store,
					Grid<RecoveryPointItemModel> grid) {
				return Utils.createLabelField(Utils.bytes2String(model
						.getSize()));
			}
		};
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("icon", "", 30, icon));
		configs.add(Utils.createColumnConfig("name",
				UIContext.Constants.diskName(), 150, name));
		configs.add(Utils.createColumnConfig("size",
				UIContext.Constants.diskSize(), 150, size));
		itemColumnsModel = new ColumnModel(configs);
		itemGrid = new BaseGrid<RecoveryPointItemModel>(itemGridStore,
				itemColumnsModel);
		itemGrid.setTrackMouseOver(true);
		itemGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		itemGrid.setBorders(true);
		itemGrid.setHeight(ITEM_TABLE_HIGHT - 45);
		itemGrid.addListener(Events.RowClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
			}

		});
		pointItemTable.add(itemGrid);
		return pointItemTable;
	}

	private LayoutContainer defineBrowseTable() {
		browseTable = new LayoutContainer();
		TableLayout tableLayout = new TableLayout();
		tableLayout.setColumns(2);
		browseTable.setLayout(tableLayout);
		TableData broseData = new TableData();

		Button browseButton = new Button(UIContext.Constants.browse());
		browseButton.setIcon(UIContext.IconHundle.search());
		browseButton.setMinWidth(MIN_BUTTON_WIDTH - 10);
		browseButton.addSelectionListener(fileListener);

		LabelField label = new LabelField(UIContext.Constants.browseLabel());
		label.setWidth(ITEM_TABLE_WIDTH - 25);

		browseTable.add(label, broseData);
		browseTable.add(browseButton, broseData);
		browseTable.setEnabled(false);
		return browseTable;
	}

	private LayoutContainer defineFileTreeTable() {
		fileTreeTable = new LayoutContainer();
		fileTreeTable.setLayout(new TableLayout());
		fileTreeTable.setHeight(ITEM_TABLE_HIGHT - 20);
		addButton = new Button(UIContext.Constants.restoreAdd());
		addButton.setIcon(AbstractImagePrototype.create(UIContext.IconBundle
				.add_button()));
		addButton.setMinWidth(MIN_BUTTON_WIDTH - 10);
		addButton.addSelectionListener(fileListener);
		Button removeButton = new Button(UIContext.Constants.restoreRemove());
		removeButton.setMinWidth(MIN_BUTTON_WIDTH - 10);
		removeButton.setIcon(AbstractImagePrototype.create(UIContext.IconBundle
				.delete_button()));
		removeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				List<GridTreeNode> nodesSelectedToRemove = grid
						.getSelectionModel().getSelectedItems();
				for (GridTreeNode node : nodesSelectedToRemove) {
					fileStore.remove(node);
				}
			}

		});

		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout(2);
		layout.setWidth("100%");
		container.setLayout(layout);
		TableData td = new TableData();
		td.setWidth("80%");

		container
				.add(new LabelField(UIContext.Constants
						.restoreFileFolderBeRestore()), td);

		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(2);
		panel.add(addButton);
		panel.add(removeButton);
		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setWidth("20%");
		container.add(panel, td);
		fileTreeTable.add(container);

		ColumnModel cm = initColumnModel();

		StoreSorter<GridTreeNode> sorter = RestoreUtils.initStoreSorter();
		fileStore = new ListStore<GridTreeNode>();
		fileStore.setStoreSorter(sorter);

		grid = new BaseGrid<GridTreeNode>(fileStore, cm);
		grid.setHeight(ITEM_TABLE_HIGHT - 50);
		grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		grid.setWidth(653);
		grid.setBorders(true);
		grid.setTrackMouseOver(true);
		fileTreeTable.add(grid);
		fileTreeTable.setEnabled(false);
		return fileTreeTable;
	}

	private void showEncryptionPasswordMessageBox(
			final RecoveryPointModel model, String message) {
		MessageBox box = Utils.showMessage(UIContext.Constants.productName(),
				MessageBox.ERROR, message);
		box.addCallback(new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				fileStore.removeAll();
			}
		});
	}

	private ColumnModel initColumnModel() {

		ColumnConfig name = new ColumnConfig("path",
				UIContext.Constants.browseFileOrFolderName(), 400);
		name.setMenuDisabled(true);
		name.setRenderer(new GridCellRenderer<BaseModelData>() {

			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				LayoutContainer lc = new LayoutContainer();

				TableLayout layout = new TableLayout();
				layout.setColumns(2);
				lc.setLayout(layout);
				final GridTreeNode node = (GridTreeNode) model;

				lc.add(RestoreUtils.getNodeIcon(node));
				lc.add(new LabelField(node.getPath()));
				lc.addListener(Events.OnClick, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						if (RecoveryPointSettings.this.grid != null) {
							RecoveryPointSettings.this.grid.getSelectionModel()
									.select(node, true);
						}
					}
				});

				return lc;
			}

		});

		ColumnConfig date = new ColumnConfig("date",
				UIContext.Constants.restoreDateModifiedColumn(), 140);
		date.setRenderer(new GridCellRenderer<BaseModelData>() {
			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				try {
					if (model != null) {
						LabelField label = new LabelField();

						label.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");
						label.setStyleAttribute("white-space", "nowrap");

						Date dateModifed = ((GridTreeNode) model).getDate();
						String strDate = Utils.formatDateToServerTime(
								dateModifed,
								((GridTreeNode) model).getServerTZOffset() != null ? ((GridTreeNode) model)
										.getServerTZOffset() : 0);

						if (strDate != null && strDate.trim().length() > 0) {
							label.setValue(strDate);
							label.setTitle(strDate);
							return label;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error:" + e.getMessage());
				}
				return "";
			}
		});
		date.setMenuDisabled(true);

		ColumnConfig size = new ColumnConfig("size",
				UIContext.Constants.size(), 80);
		size.setMenuDisabled(true);
		size.setRenderer(new GridCellRenderer<GridTreeNode>() {

			@Override
			public Object render(GridTreeNode model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GridTreeNode> store, Grid<GridTreeNode> grid) {
				try {
					if (model != null
							&& model.getType() == CatalogModelType.File
							&& model.getSize() != null) {
						Long value = model.getSize();
						String formattedValue = Utils.bytes2String(value);
						return formattedValue;
					}
				} catch (Exception e) {

				}

				return "";
			}

		});

		return new ColumnModel(Arrays.asList(name, date, size));
	}

	public List<GridTreeNode> getSelectedNodes() {
		List<GridTreeNode> nodes = new ArrayList<GridTreeNode>();
		for (int i = 0; i < fileStore.getCount(); i++) {
			nodes.add(fileStore.getAt(i));
		}
		return nodes;
	}

	public RecoveryPointModel getSelectedRecoveryPoint() {
		return pointGrid.getSelectionModel().getSelectedItem();
	}

	public RecoveryPointModel getFullRecoveryPoint() {

		RecoveryPointModel point = pointGrid.getSelectionModel()
				.getSelectedItem();
		RestoreType restoreType = restoreWindow.getRestoreType();
		if (restoreType.getValue() == RestoreType.BMR.getValue()
				|| restoreType.getValue() == RestoreType.VM.getValue()
				|| restoreType.getValue() == RestoreType.MIGRATION_BMR
						.getValue()) {
			point.items = itemGridStore.getModels();
		} else if (restoreType.getValue() == RestoreType.VOLUME.getValue()) {
			// TO_DO
		} else if (restoreType.getValue() == RestoreType.FILE.getValue()) {
			point.files = getSelectedNodes();
		}
		return point;
	}

	public BackupLocationInfoModel getBackupLocationInfoModel() {
		return txtBackupLocation.getBackupLocationInfo();
	}

	public String getMachine() {
		if (cmbMachine.getValue() == null) {
			return null;
		} else {
			return cmbMachine.getValue().getMachinePath();
		}
	}

	public String getMachineName() {
		if (cmbMachine.getValue() == null) {
			return null;
		} else {
			return cmbMachine.getValue().getMachineName();
		}
	}

	public int getMachineType() {
		if (cmbMachine.getValue() == null) {
			return 0;
		} else {
			return cmbMachine.getValue().getMachineType();
		}
	}

	public String getMachineUUID() {
		if (cmbMachine.getValue() == null) {
			return null;
		} else {
			return cmbMachine.getValue().getMachineUUID();
		}
	}

	public BackupMachineModel getMachineMode() {
		if (cmbMachine.getValue() == null) {
			return new BackupMachineModel();
		} else {
			return cmbMachine.getValue();
		}
	}

	public String getTargetMachine() {
		if (cmbMachine.getValue().getMachineType() != BackupMachineModel.TYPE_HBBU_MACHINE) {
			if (cmbMachine.getValue().getMachinePath() != null) {
				return cmbMachine.getValue().getMachinePath().split("\\[")[0];
			} else {
				return null;
			}
		} else {
			String vmHost = cmbMachine.getValue().getVmHost();
			if (vmHost != null && vmHost.toLowerCase().contains(("localhost"))) {
				return cmbMachine.getValue().getVmIp();
			} else {
				return cmbMachine.getValue().getVmHost();
			}
		}
	}

	public boolean checkSessionLocation(int eventType) {
		if (this.ignoreLocationList != null
				&& this.ignoreLocationList.size() > 0) {
			for (BackupLocationInfoModel model : this.ignoreLocationList) {
				if (model.getDisplayName().equalsIgnoreCase(
						txtBackupLocation.getValue())
						&& model.getType() == txtBackupLocation
								.getLocationType()) {
					return true;
				}
			}
		}

		return txtBackupLocation.validate(eventType);
	}

	public boolean validate(int eventType) {
		if (!checkSessionLocation(eventType)) {
			return false;
		} else if (sessionLocaton == null
				|| !txtBackupLocation.getValue().equals(sessionLocaton)) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR,
					UIContext.Constants.connectSessionLocationMessage());
			return false;
		} else if (cmbMachine.getValue() == null) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR, UIContext.Constants.machineMessage());
			return false;
		} else if (cmbMachine.getValue().getMachineType() == BackupMachineModel.TYPE_HBBU_MACHINE
				&& pointGrid.getSelectionModel().getSelectedItem().getVersion() < 3.0) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR, UIContext.Constants.validateHbbuVersion());
			return false;
		} else if (pointGrid.getSelectionModel().getSelectedItem() == null) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR,
					UIContext.Constants.selectRecoveryPointMessage());
			return false;
		} else if (pointGrid.getSelectionModel().getSelectedItem()
				.getEncryptAlgoName() != null
				&& !pointGrid.getSelectionModel().getSelectedItem()
						.getEncryptAlgoName().isEmpty()) {
			RecoveryPointModel point = pointGrid.getSelectionModel()
					.getSelectedItem();
			String password = point.getEncryptionPassword();
			if (password == null || password.isEmpty()) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.encryptionPasswordMessage());
				return false;
			}
		}

		RestoreType restoreType = restoreWindow.getRestoreType();
		if (restoreType.getValue() == RestoreType.VOLUME.getValue()) {
			// TO_DO
		} else if (restoreType.getValue() == RestoreType.FILE.getValue()) {
			List<GridTreeNode> selectedNodes = getSelectedNodes();
			if (selectedNodes.size() == 0) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.restoreMustSelectFiles());
				return false;
			}
		}
		return true;
	}

	public void save() {
		restoreWindow.restoreModel.backupLocationInfoModel = txtBackupLocation
				.getBackupLocationInfo();
		restoreWindow.restoreModel.setMachine(getMachine());
		restoreWindow.restoreModel.setMachineName(getMachineName());
		restoreWindow.restoreModel.setMachineType(getMachineType());
		restoreWindow.restoreModel.setMachineUUID(getMachineUUID());
		restoreWindow.restoreModel.setRecoveryPoint(getFullRecoveryPoint());
	}

	public void refreshData() {
		refreshData(restoreWindow.restoreModel);
	}

	private void reconfigurePointGrid(int type) {
		ColumnConfig columConfig = pointColumnsModel.getColumns().get(5);
		if (type == BackupLocationInfoModel.TYPE_RPS_SERVER) {
			columConfig.setHeader(UIContext.Constants.sessionPassword());
		} else {
			columConfig.setHeader(UIContext.Constants.encryptionPassword());
		}
		pointGrid.reconfigure(pointGridStore, pointColumnsModel);
	}

	public void refreshData(RestoreModel model) {
		showPointItemsTable(restoreWindow.getRestoreType());
		if (model.backupLocationInfoModel == null) {
			return;
		}
		txtBackupLocation.setBackupLocationInfo(model.backupLocationInfoModel);
		sessionLocaton = txtBackupLocation.getValue();

		cmbMachine.getStore().removeAll();
		if (restoreWindow.isModify
				|| restoreWindow.getRestoreType() == RestoreType.MIGRATION_BMR) {// don't
																					// need
																					// to
																					// load
																					// all
																					// recovery
																					// point
			isRefreshPointTable = false;
		}

		reconfigurePointGrid(model.backupLocationInfoModel.getType());
		BackupMachineModel machineModel = new BackupMachineModel();
		machineModel.setMachineType(model.getMachineType());
		machineModel.setMachineName(model.getMachineName());
		machineModel.setMachinePath(model.getMachine());
		machineModel.setMachineUUID(model.getMachineUUID());
		cmbMachine.getStore().add(machineModel);
		cmbMachine.setValue(machineModel);
		pointGridStore.removeAll();
		pointGridStore.add(model.getRecoveryPoint());
		pointGrid.reconfigure(pointGridStore, pointColumnsModel);
		pointGrid.getSelectionModel().select(model.getRecoveryPoint(), false);
		refreshPointItems(model.getRecoveryPoint());
	}

	public void showPointItemsTable(RestoreType restoreType) {
		if (restoreType.getValue() == RestoreType.BMR.getValue()
				|| restoreType.getValue() == RestoreType.MIGRATION_BMR
						.getValue()
				|| restoreType.getValue() == RestoreType.VM.getValue()) {
			pointItemTable.show();
			fileTreeTable.hide();
			browseTable.hide();
		} else if (restoreType.getValue() == RestoreType.VOLUME.getValue()) {
			// TO_DO
		} else if (restoreType.getValue() == RestoreType.SHARE_RECOVERY_POINT
				.getValue()) {
			pointItemTable.hide();
			fileTreeTable.hide();
			browseTable.show();
			points.setHeight(200);
			pointGrid.setHeight(200);
		} else if (restoreType.getValue() == RestoreType.FILE.getValue()) {
			pointItemTable.hide();
			browseTable.hide();
			fileTreeTable.show();
		}
	}

	public boolean isRestoreManager() {
		return isRestoreManager;
	}

	public void setRestoreManager(boolean isRestoreManager) {
		this.isRestoreManager = isRestoreManager;
	}

	public void addSelectedNode(List<GridTreeNode> selectedNodes) {
		fileStore.removeAll();
		fileStore.add(selectedNodes);
		grid.reconfigure(fileStore, grid.getColumnModel());

	}

	@Override
	public ServiceInfoModel getD2DServerInfo() {
		return restoreWindow.getD2DServerInfo();
	}

	@Override
	public void showNextSettings() {
		restoreWindow.showNextSettings();
	}

	@Override
	public void resume() {
		connectToBackupDestination();
	}

	@Override
	public void maskUI(String message) {
		restoreWindow.mask(message);
	}

	@Override
	public void unmaskUI() {
		restoreWindow.unmask();
	}

	public void refreshLocation(List<BackupLocationInfoModel> locationList,
			String defaultLocation, String defaultMachine) {
		List<BackupLocationInfoModel> listWithoutServerLocal = new ArrayList<BackupLocationInfoModel>();
		if (locationList != null) {
			for (BackupLocationInfoModel model : locationList) {
				if (restoreWindow.getRestoreType().getValue() == RestoreType.BMR
						.getValue()
						&& model.getType() == BackupLocationInfoModel.TYPE_SERVER_LOCAL) {
					continue;
				}
				listWithoutServerLocal.add(model);
			}
		}
		this.ignoreLocationList = listWithoutServerLocal;
		this.txtBackupLocation.refreshLocation(listWithoutServerLocal,
				defaultLocation);
		if (!Utils.isEmptyOrNull(defaultLocation)) {
			this.refreshMachineList(defaultMachine);
		}
	}

	public void refreshLocationForMigrationBMR(RestoreModel result) {
		showPointItemsTable(restoreWindow.getRestoreType());
		if(result.backupLocationInfoModel != null && ignoreLocationList != null){
			for(BackupLocationInfoModel backupLocation : ignoreLocationList){
				if(result.backupLocationInfoModel.getDisplayName().equals(backupLocation.getDisplayName())){
					refreshData(result);
					break;
				}
			}
		}
		this.machineForMigration = result.getMachine();
		this.rpForMigration = result.getRecoveryPoint();
		filterBtn.disable();
	}

	public void refreshPointTable() {
		if (pointGrid != null)
			pointGrid.reconfigure(pointGridStore, pointColumnsModel);
	}
}