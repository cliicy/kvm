package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.ArrayList;
import java.util.List;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.backup.wizard.Summary;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.ConfigurationService;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.ConfigurationServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.FileOption;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreTargetModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.VMRestoreTargetModel;
import com.ca.arcserve.linuximaging.ui.client.model.VirtualizationServerType;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;

public class SummarySettings extends LayoutContainer {
	public static int SUMMARY_HIGHT = 365;
	public final static int ITEM_TABLE_WIDTH = 565;
	public final static int ITEM_TABLE_HIGHT = 100;
	final ConfigurationServiceAsync service = GWT
			.create(ConfigurationService.class);
	private RestoreWindow restoreWindow;
	private BaseGrid<VMRestoreTargetModel> vmTargetGrid;
	private ListStore<VMRestoreTargetModel> vmTargetGridStore;
	private ColumnModel vmTargetColumnsModel;
	private LabelField txtD2DServer;
	private LabelField txtBackupLocation;
	private LabelField txtMachine;
	private LabelField txtRecoveryPoint;
	private TextField<String> txtJobName;
	private LabelField txtRestoreType;
	private LayoutContainer fileTargetTable;
	private LayoutContainer vmTargetTable;
	private LabelField txtUserName;
	private LabelField txtDestination;
	private LabelField txtHostName;
	private LabelField txtFileOption;
	private LabelField txtCreateRootDir;
	private LabelField txtStartTime;
	private LabelField txtEstimateFileSize;
	private LabelField txtReboot;
	private LabelField txtEnableWakeOnLan;
	private LabelField txtNewUsername;
	private LabelField txtSessionLocationLocal;
	private LabelField txtExcludeDisk;
	private LabelField txtServerScriptBeforeJob;
	private LabelField txtServerScriptAfterJob;
	private LabelField txtTargetScriptBeforeJob;
	private LabelField txtTargetScriptAfterJob;
	private LabelField txtTargetScriptReadyForUseJob;

	private LabelField txtRestoreLocation;
	private LabelField lblDestination;
	private HTMLPanel filesPanel;
	private LabelField lblHostName;
	private LabelField lblUserName;
	private LabelField lblFileOption;
	private LabelField lblCreateRootDir;
	private LabelField lblEstimateFileSize;
	private LabelField lblServerScriptBeforeJob;
	private LabelField lblServerScriptAfterJob;
	private LabelField lblTargetScriptBeforeJob;
	private LabelField lblTargetScriptAfterJob;
	private LabelField lblTargetScriptReadyForUseJob;
	private BMRTargetSummaryPanel bmrTarget;
	private LabelField txtBmrTargetName;
	public final static int TABLE_HIGHT = 135;
	public final static int MAX_FIELD_WIDTH = 300;

	private SharePointSummaryPanel shareFie;
	private LabelField lengthOftime;
	private LabelField application;

	public SummarySettings(RestoreWindow restoreWindow) {
		this.restoreWindow = restoreWindow;
		TableLayout layout = new TableLayout();
		layout.setWidth("97%");
		layout.setCellPadding(5);
		layout.setCellSpacing(5);
		setLayout(layout);

		LabelField head = new LabelField(UIContext.Constants.summary());
		head.setStyleAttribute("font-weight", "bold");
		add(head);

		LayoutContainer general = defineGeneralSummary();
		add(general);

		LayoutContainer jobName = defineJobNameField();
		add(jobName);
	}

	private LayoutContainer defineJobNameField() {
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		container.setLayout(layout);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("80%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField lblJobName = new LabelField(UIContext.Constants.jobName());
		txtJobName = new TextField<String>();
		txtJobName.setWidth(MAX_FIELD_WIDTH);
		container.add(lblJobName, tdLabel);
		container.add(txtJobName, tdField);
		return container;
	}

	private LayoutContainer defineGeneralSummary() {
		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("border", "1px solid");
		container.setStyleAttribute("border-color", "#B5B8C8");
		container.setHeight(SUMMARY_HIGHT);
		container.setScrollMode(Scroll.AUTOY);

		LayoutContainer generalFieldSet = new LayoutContainer();
		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(2);
		tlConnSettings.setCellPadding(1);
		tlConnSettings.setCellSpacing(5);
		generalFieldSet.setLayout(tlConnSettings);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("70%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("30%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField lblD2DServer = new LabelField(
				UIContext.Constants.backupServerLabel()
						+ UIContext.Constants.delimiter());
		lblD2DServer.setAutoWidth(true);
		generalFieldSet.add(lblD2DServer, tdLabel);
		txtD2DServer = new LabelField();
		generalFieldSet.add(txtD2DServer, tdField);

		LabelField lblRestoreType = new LabelField(
				UIContext.Constants.restoreTypeLabel()
						+ UIContext.Constants.delimiter());
		lblRestoreType.setAutoWidth(true);
		generalFieldSet.add(lblRestoreType, tdLabel);
		txtRestoreType = new LabelField();
		generalFieldSet.add(txtRestoreType, tdField);

		LabelField lblBackupLocation = new LabelField(
				UIContext.Constants.backupLocationLabel()
						+ UIContext.Constants.delimiter());
		lblBackupLocation.setAutoWidth(true);
		generalFieldSet.add(lblBackupLocation, tdLabel);
		txtBackupLocation = new LabelField();
		generalFieldSet.add(txtBackupLocation, tdField);

		LabelField lblMachine = new LabelField(UIContext.Constants.machine()
				+ UIContext.Constants.delimiter());
		lblMachine.setAutoWidth(true);
		generalFieldSet.add(lblMachine, tdLabel);
		txtMachine = new LabelField();
		generalFieldSet.add(txtMachine, tdLabel);

		LabelField lblRecoveryPoint = new LabelField(
				UIContext.Constants.recoveryPoint()
						+ UIContext.Constants.delimiter());
		lblRecoveryPoint.setAutoWidth(true);
		generalFieldSet.add(lblRecoveryPoint, tdLabel);
		txtRecoveryPoint = new LabelField();
		generalFieldSet.add(txtRecoveryPoint, tdLabel);

		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);
		RestoreType restoreType = restoreWindow.getRestoreType();
		if (restoreType.getValue() == RestoreType.BMR.getValue()
				|| restoreType.getValue() == RestoreType.MIGRATION_BMR
						.getValue()) {
			defineBMRTargetTable(generalFieldSet, tdLabel, tdField);
			defineServerScript(generalFieldSet);
			defineTargetScript(generalFieldSet);
			defineAdvancedSetting(generalFieldSet);
		} else if (restoreType.getValue() == RestoreType.VM.getValue()) {
			defineVMTargetTable();
			generalFieldSet.add(vmTargetTable, tdColspan);
		} else if (restoreType.getValue() == RestoreType.FILE.getValue()) {
			defineFileTargetTable(generalFieldSet);
		} else if (restoreType.getValue() == RestoreType.SHARE_RECOVERY_POINT
				.getValue()) {
			container.setHeight(SUMMARY_HIGHT - 65);
			defineShareTable(generalFieldSet, tdLabel, tdField);
		}

		LabelField lblStartTime = new LabelField(
				UIContext.Constants.startTime()
						+ UIContext.Constants.delimiter());
		lblStartTime.setAutoWidth(true);
		generalFieldSet.add(lblStartTime, tdLabel);
		txtStartTime = new LabelField();
		generalFieldSet.add(txtStartTime, tdField);

		container.add(generalFieldSet);
		return container;
	}

	public LayoutContainer defineShareTable(LayoutContainer generalFieldSet,
			TableData tdLabel, TableData tdField) {
		LabelField lblService = new LabelField(
				UIContext.Constants.application()
						+ UIContext.Constants.delimiter());
		generalFieldSet.add(lblService, tdLabel);

		application = new LabelField();
		generalFieldSet.add(application, tdField);

		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		shareFie = new SharePointSummaryPanel();
		generalFieldSet.add(shareFie, tdColspan);

		LabelField lblBmrTargetName = new LabelField(
				UIContext.Constants.lengthOftime()
						+ UIContext.Constants.delimiter());
		generalFieldSet.add(lblBmrTargetName, tdLabel);
		lengthOftime = new LabelField();
		LabelField shareHours = new LabelField(UIContext.Constants.shareHours());
		HorizontalPanel length = new HorizontalPanel();
		length.add(lengthOftime);
		length.add(shareHours);
		generalFieldSet.add(length, tdField);

		return shareFie;
	}

	private void defineFileTargetTable(LayoutContainer generalFieldSet) {
		fileTargetTable = new LayoutContainer();
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setColumns(2);
		tableLayout.setCellPadding(0);
		tableLayout.setCellSpacing(0);
		fileTargetTable.setLayout(tableLayout);

		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("70%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);

		FieldSet fileList = new FieldSet();
		fileList.setHeading(UIContext.Constants.fileList()
				+ UIContext.Constants.delimiter());
		filesPanel = new HTMLPanel("");
		fileList.add(filesPanel);
		fileTargetTable.add(fileList, tdColspan);

		txtRestoreLocation = new LabelField();
		txtRestoreLocation.setAutoWidth(true);
		fileTargetTable.add(txtRestoreLocation, tdColspan);
		generalFieldSet.add(fileTargetTable, tdColspan);

		lblHostName = new LabelField(UIContext.Constants.hostName()
				+ UIContext.Constants.delimiter());
		lblHostName.setAutoWidth(true);
		generalFieldSet.add(lblHostName, tableDataLabel);
		txtHostName = new LabelField();
		generalFieldSet.add(txtHostName, tableDataField);

		lblUserName = new LabelField(UIContext.Constants.userName()
				+ UIContext.Constants.delimiter());
		lblUserName.setAutoWidth(true);
		generalFieldSet.add(lblUserName, tableDataLabel);
		txtUserName = new LabelField();
		generalFieldSet.add(txtUserName, tableDataField);

		lblDestination = new LabelField(UIContext.Constants.destination()
				+ UIContext.Constants.delimiter());
		lblDestination.setAutoWidth(true);
		generalFieldSet.add(lblDestination, tableDataLabel);
		txtDestination = new LabelField();
		generalFieldSet.add(txtDestination, tableDataField);

		lblFileOption = new LabelField(
				UIContext.Constants.restoreResolvingConflicts()
						+ UIContext.Constants.delimiter());
		lblFileOption.setAutoWidth(true);
		generalFieldSet.add(lblFileOption, tableDataLabel);
		txtFileOption = new LabelField();
		generalFieldSet.add(txtFileOption, tableDataField);

		lblCreateRootDir = new LabelField(
				UIContext.Constants.restoreConflictBaseFolderWillBeCreated()
						+ UIContext.Constants.delimiter());
		lblCreateRootDir.setAutoWidth(true);
		generalFieldSet.add(lblCreateRootDir, tableDataLabel);
		txtCreateRootDir = new LabelField();
		generalFieldSet.add(txtCreateRootDir, tableDataField);

		lblEstimateFileSize = new LabelField(
				UIContext.Constants.restoreEstimateFileSize()
						+ UIContext.Constants.delimiter());
		lblEstimateFileSize.setAutoWidth(true);
		generalFieldSet.add(lblEstimateFileSize, tableDataLabel);
		txtEstimateFileSize = new LabelField();
		generalFieldSet.add(txtEstimateFileSize, tableDataField);
		fileTargetTable.add(generalFieldSet, tdColspan);

		defineServerScript(generalFieldSet);

	}

	private void defineServerScript(LayoutContainer container) {
		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("70%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		lblServerScriptBeforeJob = new LabelField(
				UIContext.Constants.summaryServerScriptBeforeJob()
						+ UIContext.Constants.delimiter());
		lblServerScriptBeforeJob.setAutoWidth(true);
		container.add(lblServerScriptBeforeJob, tableDataLabel);
		txtServerScriptBeforeJob = new LabelField();
		container.add(txtServerScriptBeforeJob, tableDataField);

		lblServerScriptAfterJob = new LabelField(
				UIContext.Constants.summaryServerScriptAfterJob()
						+ UIContext.Constants.delimiter());
		lblServerScriptAfterJob.setAutoWidth(true);
		container.add(lblServerScriptAfterJob, tableDataLabel);
		txtServerScriptAfterJob = new LabelField();
		container.add(txtServerScriptAfterJob, tableDataField);
	}

	private void defineTargetScript(LayoutContainer container) {
		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("70%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		lblTargetScriptBeforeJob = new LabelField(
				UIContext.Constants.summaryTargetScriptBeforeJob()
						+ UIContext.Constants.delimiter());
		lblTargetScriptBeforeJob.setAutoWidth(true);
		container.add(lblTargetScriptBeforeJob, tableDataLabel);
		txtTargetScriptBeforeJob = new LabelField();
		container.add(txtTargetScriptBeforeJob, tableDataField);

		lblTargetScriptReadyForUseJob = new LabelField(
				UIContext.Constants.summaryTargetScriptReadyForUseJob()
						+ UIContext.Constants.delimiter());
		lblTargetScriptReadyForUseJob.setAutoWidth(true);
		container.add(lblTargetScriptReadyForUseJob, tableDataLabel);
		txtTargetScriptReadyForUseJob = new LabelField();
		container.add(txtTargetScriptReadyForUseJob, tableDataField);

		lblTargetScriptAfterJob = new LabelField(
				UIContext.Constants.summaryTargetScriptAfterJob()
						+ UIContext.Constants.delimiter());
		lblTargetScriptAfterJob.setAutoWidth(true);
		container.add(lblTargetScriptAfterJob, tableDataLabel);
		txtTargetScriptAfterJob = new LabelField();
		container.add(txtTargetScriptAfterJob, tableDataField);
	}

	private void defineAdvancedSetting(LayoutContainer container) {
		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("70%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("30%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField label = new LabelField(
				UIContext.Constants.enableWakeupOnLan()
						+ UIContext.Constants.delimiter());
		container.add(label, tableDataLabel);

		txtEnableWakeOnLan = new LabelField();
		container.add(txtEnableWakeOnLan, tableDataField);

		label = new LabelField(UIContext.Constants.resetForUser()
				+ UIContext.Constants.delimiter());
		container.add(label, tableDataLabel);
		txtNewUsername = new LabelField();
		container.add(txtNewUsername, tableDataField);

		label = new LabelField(UIContext.Constants.sessionLocationSettings()
				+ UIContext.Constants.delimiter());
		container.add(label, tableDataLabel);
		txtSessionLocationLocal = new LabelField();
		container.add(txtSessionLocationLocal, tableDataField);

		label = new LabelField(UIContext.Constants.excludeTargetDisk()
				+ UIContext.Constants.delimiter());
		container.add(label, tableDataLabel);
		txtExcludeDisk = new LabelField();
		container.add(txtExcludeDisk, tableDataField);

		label = new LabelField(UIContext.Constants.reboot()
				+ UIContext.Constants.delimiter());
		container.add(label, tableDataLabel);

		txtReboot = new LabelField();
		container.add(txtReboot, tableDataField);

	}

	public LayoutContainer defineBMRTargetTable(
			LayoutContainer generalFieldSet, TableData tdLabel,
			TableData tdField) {
		LabelField lblBmrTargetName = new LabelField(
				UIContext.Constants.restoreTargetMachine()
						+ UIContext.Constants.delimiter());
		generalFieldSet.add(lblBmrTargetName, tdLabel);

		txtBmrTargetName = new LabelField();
		generalFieldSet.add(txtBmrTargetName, tdField);

		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		bmrTarget = new BMRTargetSummaryPanel();
		generalFieldSet.add(bmrTarget, tdColspan);
		return bmrTarget;
	}

	public LayoutContainer defineVMTargetTable() {
		vmTargetTable = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(1);
		layout.setCellPadding(0);
		layout.setCellSpacing(1);
		vmTargetTable.setLayout(layout);

		LabelField head = new LabelField(
				UIContext.Constants.restoreTargetMachines()
						+ UIContext.Constants.delimiter());
		vmTargetTable.add(head);

		LabelField space = new LabelField("");
		vmTargetTable.add(space);
		space.hide();

		LayoutContainer gridContainer = new LayoutContainer();
		gridContainer.setLayout(new FitLayout());
		vmTargetGridStore = new ListStore<VMRestoreTargetModel>();
		GridCellRenderer<VMRestoreTargetModel> serverName = new GridCellRenderer<VMRestoreTargetModel>() {

			@Override
			public Object render(VMRestoreTargetModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VMRestoreTargetModel> store,
					Grid<VMRestoreTargetModel> grid) {
				return model.getVirtualizationServerModel()
						.getVirtualizationServerName();
			}
		};
		GridCellRenderer<VMRestoreTargetModel> serverType = new GridCellRenderer<VMRestoreTargetModel>() {

			@Override
			public Object render(VMRestoreTargetModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VMRestoreTargetModel> store,
					Grid<VMRestoreTargetModel> grid) {
				String serverType = "";
				if (model.getVirtualizationServerModel().getServerType() == VirtualizationServerType.VMWARE
						.getValue()) {
					serverType = UIContext.Constants
							.targetServerType_EsxServer();
				} else if (model.getVirtualizationServerModel().getServerType() == VirtualizationServerType.XEN
						.getValue()) {
					serverType = UIContext.Constants.targetServerType_Xen();
				}
				return serverType;
			}
		};
		GridCellRenderer<VMRestoreTargetModel> hostName = new GridCellRenderer<VMRestoreTargetModel>() {

			@Override
			public Object render(VMRestoreTargetModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VMRestoreTargetModel> store,
					Grid<VMRestoreTargetModel> grid) {
				return model.getVmModel().getVirtualMachineHostName();
			}
		};
		GridCellRenderer<VMRestoreTargetModel> network = new GridCellRenderer<VMRestoreTargetModel>() {

			@Override
			public Object render(VMRestoreTargetModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VMRestoreTargetModel> store,
					Grid<VMRestoreTargetModel> grid) {
				if (model.getVmModel().getNetwork_IsDHCP()) {
					return UIContext.Constants.dhcp();
				} else {
					LabelField staticIP = new LabelField(
							UIContext.Constants.staticIP());
					Utils.addToolTip(staticIP, UIContext.Messages
							.networkToolTip(model.getVmModel()
									.getNetwork_ipAddress(), model.getVmModel()
									.getNetwork_subnetMask(), model
									.getVmModel().getNetwork_gateway()));
					return staticIP;
				}
			}
		};

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("serverName",
				UIContext.Constants.targetServerName(), 150, serverName));
		configs.add(Utils.createColumnConfig("serverType",
				UIContext.Constants.targetServerType(), 100, serverType));
		configs.add(Utils.createColumnConfig("hostName",
				UIContext.Constants.hostName(), 150, hostName));
		configs.add(Utils.createColumnConfig("network",
				UIContext.Constants.network(), 150, network));
		vmTargetColumnsModel = new ColumnModel(configs);
		vmTargetGrid = new BaseGrid<VMRestoreTargetModel>(vmTargetGridStore,
				vmTargetColumnsModel);
		vmTargetGrid.setLoadMask(true);
		vmTargetGrid.setHeight(TABLE_HIGHT);
		vmTargetGrid.unmask();
		vmTargetGrid.setTrackMouseOver(true);
		vmTargetGrid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		vmTargetGrid.setBorders(true);
		vmTargetGrid.setAutoExpandMax(3000);
		vmTargetGrid.getView().setForceFit(false);
		vmTargetGrid.addListener(Events.RowClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

			}

		});
		gridContainer.add(vmTargetGrid);
		vmTargetTable.add(gridContainer);
		return vmTargetTable;
	}

	public void refreshData() {
		RestoreModel model = restoreWindow.restoreModel;
		txtD2DServer.setValue(restoreWindow.currentServer.getServer());
		txtBackupLocation.setValue(model.backupLocationInfoModel
				.getDisplayName());
		txtMachine.setValue(model.getMachineName());
		txtRecoveryPoint.setValue(Utils.getNormalRpName(model
				.getRecoveryPoint().getName()));

		RestoreType restoreType = restoreWindow.getRestoreType();
		txtRestoreType.setValue(RestoreType.displayMessage(restoreType
				.getValue()));
		if (restoreType.getValue() == RestoreType.BMR.getValue()
				|| restoreType.getValue() == RestoreType.MIGRATION_BMR
						.getValue()) {
			this.txtBmrTargetName.setValue(model.getRestoreTargetList().get(0)
					.getAddress());
			bmrTarget.loadTargetModel(model.getRestoreTargetList().get(0));
			if (Utils.isEmptyOrNull(txtJobName.getValue())) {
				if (restoreType.getValue() == RestoreType.BMR.getValue()) {
					txtJobName.setValue(UIContext.Constants.restoreType_BMR()
							+ " - "
							+ model.getRestoreTargetList().get(0)
									.getNetwork_HostName().toLowerCase());
				} else {
					txtJobName.setValue(UIContext.Constants.migrationBmr()
							+ " - "
							+ model.getRestoreTargetList().get(0).getAddress()
									.toLowerCase());
				}
			}
			if (!Utils.isEmptyOrNull(model.getServerScriptAfterJob())) {
				txtServerScriptAfterJob.setValue(model
						.getServerScriptAfterJob());
			} else {
				txtServerScriptAfterJob.setValue(UIContext.Constants.none());
			}

			if (!Utils.isEmptyOrNull(model.getServerScriptBeforeJob())) {
				txtServerScriptBeforeJob.setValue(model
						.getServerScriptBeforeJob());
			} else {
				txtServerScriptBeforeJob.setValue(UIContext.Constants.none());
			}

			if (!Utils.isEmptyOrNull(model.getTargetScriptAfterJob())) {
				txtTargetScriptAfterJob.setValue(model
						.getTargetScriptAfterJob());
			} else {
				txtTargetScriptAfterJob.setValue(UIContext.Constants.none());
			}

			if (!Utils.isEmptyOrNull(model.getTargetScriptReadyForUseJob())) {
				txtTargetScriptReadyForUseJob.setValue(model
						.getTargetScriptReadyForUseJob());
			} else {
				txtTargetScriptReadyForUseJob.setValue(UIContext.Constants
						.none());
			}

			if (!Utils.isEmptyOrNull(model.getTargetScriptBeforeJob())) {
				txtTargetScriptBeforeJob.setValue(model
						.getTargetScriptBeforeJob());
			} else {
				txtTargetScriptBeforeJob.setValue(UIContext.Constants.none());
			}

			if (model.getRestoreTargetList() != null
					&& model.getRestoreTargetList().size() > 0) {
				if (model.getRestoreTargetList().get(0).getReboot()) {
					txtReboot.setValue(UIContext.Constants.no());
				} else {
					txtReboot.setValue(UIContext.Constants.yes());
				}
			}

			if (model.getEnableWakeOnLan()) {
				txtEnableWakeOnLan.setValue(UIContext.Constants.yes());
			} else {
				txtEnableWakeOnLan.setValue(UIContext.Constants.no());
			}

			if (!Utils.isEmptyOrNull(model.getNewUsername())) {
				txtNewUsername.setValue(model.getNewUsername());
			} else {
				txtNewUsername.setValue(UIContext.Constants.NA());
			}

			if (Utils.isEmptyOrNull(model.getLocalPath())) {
				txtSessionLocationLocal.setValue(UIContext.Constants.NA());
			} else {
				txtSessionLocationLocal.setValue(model.getLocalPath());
			}

			if (!Utils.isEmptyOrNull(model.getExcludeTargetDisks())) {
				String[] disks = model.getExcludeTargetDisks().split("\n");
				String diskStr = "";
				for (String disk : disks) {
					diskStr += disk + ";";
				}
				txtExcludeDisk.setValue(diskStr.substring(0,
						diskStr.length() - 1));
			} else {
				txtExcludeDisk.setValue(UIContext.Constants.NA());
			}
		} else if (restoreType.getValue() == RestoreType.VOLUME.getValue()) {
			// TO_DO
		} else if (restoreType.getValue() == RestoreType.FILE.getValue()) {

			filesPanel.clear();
			List<GridTreeNode> files = model.getRecoveryPoint().files;
			if (files != null && files.size() > 0) {
				for (GridTreeNode node : files) {
					filesPanel.add(new Html(
							"<pre style=\"font-family: Tahoma,Arial;font-size: 12px;\">"
									+ node.getCatalogFilePath() + "</pre>"));
				}
			}
			RestoreTargetModel target = model.getRestoreTargetList().get(0);
			if (target.getRestoreToOriginal()) {
				txtRestoreLocation.setValue(UIContext.Constants
						.restoreToOriginalLocation());
				lblDestination.hide();
				txtDestination.hide();
				lblCreateRootDir.hide();
				txtCreateRootDir.hide();
			} else {
				txtRestoreLocation.setValue(UIContext.Constants.restoreTo());
				lblDestination.show();
				txtDestination.show();
				txtDestination.setValue(target.getDestination());
				lblCreateRootDir.show();
				txtCreateRootDir
						.setValue(target.getCreateRootDir() == true ? UIContext.Constants
								.yes() : UIContext.Constants.no());
			}
			txtHostName.setValue(target.getAddress());
			txtUserName.setValue(target.getUserName());
			txtFileOption.setValue(FileOption.displayMessage(target
					.getFileOption()));
			txtEstimateFileSize
					.setValue(model.getEstimateFileSize() == true ? UIContext.Constants
							.yes() : UIContext.Constants.no());

			if (!Utils.isEmptyOrNull(model.getServerScriptAfterJob())) {
				txtServerScriptAfterJob.setValue(model
						.getServerScriptAfterJob());
			} else {
				txtServerScriptAfterJob.setValue(UIContext.Constants.none());
			}

			if (!Utils.isEmptyOrNull(model.getServerScriptBeforeJob())) {
				txtServerScriptBeforeJob.setValue(model
						.getServerScriptBeforeJob());
			} else {
				txtServerScriptBeforeJob.setValue(UIContext.Constants.none());
			}

		} else if (restoreType.getValue() == RestoreType.VM.getValue()) {
			vmTargetGridStore.removeAll();
			vmTargetGridStore.add(model.getVmRestoreTargetList());
			vmTargetGrid.reconfigure(vmTargetGridStore, vmTargetColumnsModel);
		} else if (restoreType.getValue() == RestoreType.SHARE_RECOVERY_POINT
				.getValue()) {
			application.setValue(model.getApplication());
			model.setJobName(model.getJobName());
			shareFie.loadTargetModel(model.getRestoreTargetList().get(0),
					model.getApplication().toLowerCase(),model.getNfsShareOption());
			lengthOftime.setValue(model.getLengthOftime());
		}

		if (model.startTime != null && !model.startTime.isRunNow()) {
			txtStartTime.setValue(Utils.formatD2DTime(model.startTime));
		} else {
			txtStartTime.setValue(UIContext.Constants.runNow());
		}

		if (model.getJobName() != null) {
			txtJobName.setValue(model.getJobName());
		}
	}

	public boolean validate() {
		if (txtJobName.getValue() == null || txtJobName.getValue().equals("")) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR, UIContext.Messages.jobNameEmpty());
			return false;
		}

		if (txtJobName.getValue().trim().isEmpty()) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR, UIContext.Constants.jobNameContainSpace());
			return false;
		}
		if (txtJobName.getValue().length() > Summary.JOB_NAME_MAX_LENGTH) {
			Utils.showMessage(
					UIContext.Constants.productName(),
					MessageBox.ERROR,
					UIContext.Messages
							.exceedMaxJobNameLength(Summary.JOB_NAME_MAX_LENGTH));
			return false;
		}
		return true;
	}

	public String getJobName() {
		return txtJobName.getValue();
	}

	public void setJobName(String jobName) {
		txtJobName.setValue(jobName);
	}
}
