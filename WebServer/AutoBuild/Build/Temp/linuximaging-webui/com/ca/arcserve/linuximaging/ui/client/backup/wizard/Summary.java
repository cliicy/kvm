package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.model.BackupTemplateModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupModel;
import com.ca.arcserve.linuximaging.ui.client.model.BackupScheduleModel;
import com.ca.arcserve.linuximaging.ui.client.model.CompressType;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class Summary extends LayoutContainer {
	public static int SUMMARY_HIGHT = 390;
	public static int SOURCE_TABLE_HIGHT = 120;
	public static int SOURCE_TABLE_WIDTH = 610;
	public static int JOB_LABLE_WIDTH = 80;
	public static int JOB_FIELD_WIDTH = 300;
	public static int JOB_NAME_MAX_LENGTH = 128;
	private BackupWizardPanel parentPanel;

	LabelField txtD2DServer;
	BackupSourceInfoTable table;
	LabelField lblExclude;
	LabelField txtExclude;
	LabelField lblInclude;
	LabelField txtInclude;
	LabelField lblExcludeFiles;
	LabelField txtExcludeFiles;
	LabelField txtDestination;
	LabelField txtCompression;
	LabelField txtEncryption;
	private TextField<String> jobName;
	private LabelField txtStartTime;
	private LabelField txtFullSchedule;
	private LabelField txtIncrementalSchedule;
	private LabelField txtResyncSchedule;
	private LabelField txtRecoverySetNumber;
	private LabelField txtThroughput;
	private LabelField txtServerScriptBeforeJob;
	private LabelField txtServerScriptAfterJob;
	private LabelField txtTargetScriptBeforeJob;
	private LabelField txtTargetScriptAfterJob;
	private LabelField txtTargetScriptBeforeSnapshot;
	private LabelField txtTargetScriptAfterSnapshot;
	private LayoutContainer incrementalContainer;
	private LayoutContainer fullContainer;
	private LayoutContainer resyncContainer;
	private BackupScheduleSettings scheduleSettings;

	public Summary(BackupWizardPanel parent) {
		parentPanel = parent;

		TableLayout layout = new TableLayout();
		layout.setWidth("97%");
		layout.setCellPadding(2);
		layout.setCellSpacing(2);

		this.setLayout(layout);
		// this.setHeight(BackupWizardPanel.RIGHT_PANEL_HIGHT-20);
		// this.setWidth(BackupWizardPanel.RIGHT_PANEL_WIDTH);
		// this.setScrollMode(Scroll.AUTOY);

		LabelField head = new LabelField(UIContext.Constants.summary());
		head.setStyleAttribute("font-weight", "bold");
		add(head);

		LayoutContainer general = defineGeneralSummary();
		add(general);

		LayoutContainer jobName = defineJobNameField();
		add(jobName);
		refresh();
	}

	private LayoutContainer defineGeneralSummary() {
		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("border", "1px solid");
		container.setStyleAttribute("border-color", "#B5B8C8");
		container.setHeight(SUMMARY_HIGHT);
		container.setScrollMode(Scroll.AUTOY);

		// FieldSet summaryField = new FieldSet();
		LayoutContainer summaryField = new LayoutContainer();
		// summaryField.setStyleAttribute("border", "1px solid");
		// summaryField.setStyleAttribute("border-color", "#B5B8C8");
		// summaryField.setHeight(SUMMARY_HIGHT);
		// summaryField.setScrollMode(Scroll.AUTOY);
		// summaryField.setHeading(UIContext.Constants.general());
		// summaryField.setStyleAttribute("margin", "5,5,5,5");

		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setColumns(2);
		tableLayout.setCellPadding(1);
		tableLayout.setCellSpacing(5);
		summaryField.setLayout(tableLayout);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("70%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("30%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField lblD2DServer = new LabelField(
				UIContext.Constants.d2dServer());
		lblD2DServer.setAutoWidth(true);
		summaryField.add(lblD2DServer, tdLabel);

		txtD2DServer = new LabelField();
		summaryField.add(txtD2DServer, tdField);

		LabelField lblTableHead = new LabelField(UIContext.Constants.nodeList()
				+ UIContext.Constants.delimiter());
		lblTableHead.setAutoWidth(true);
		summaryField.add(lblTableHead, tdColspan);

		table = new BackupSourceInfoTable(null, SOURCE_TABLE_HIGHT,
				SOURCE_TABLE_WIDTH);
		table.setSummary(true);
		summaryField.add(table, tdColspan);

		lblExclude = new LabelField(UIContext.Constants.excludeInfo()
				+ UIContext.Constants.delimiter());
		lblExclude.setAutoWidth(true);
		summaryField.add(lblExclude, tdLabel);
		txtExclude = new LabelField();
		summaryField.add(txtExclude, tdField);

		lblInclude = new LabelField(UIContext.Constants.includeInfo()
				+ UIContext.Constants.delimiter());
		lblInclude.setAutoWidth(true);
		summaryField.add(lblInclude, tdLabel);
		txtInclude = new LabelField();
		summaryField.add(txtInclude, tdField);

		lblExcludeFiles = new LabelField(UIContext.Constants.excludeFilesInfo()
				+ UIContext.Constants.delimiter());
		lblExcludeFiles.setAutoWidth(true);
		summaryField.add(lblExcludeFiles, tdLabel);
		txtExcludeFiles = new LabelField();
		summaryField.add(txtExcludeFiles, tdField);

		LabelField lblDestination = new LabelField(
				UIContext.Constants.backupDestination()
						+ UIContext.Constants.delimiter());
		lblDestination.setAutoWidth(true);
		summaryField.add(lblDestination, tdLabel);
		txtDestination = new LabelField();
		summaryField.add(txtDestination, tdField);

		LabelField lblCompression = new LabelField(
				UIContext.Constants.compression()
						+ UIContext.Constants.delimiter());
		lblCompression.setAutoWidth(true);
		summaryField.add(lblCompression, tdLabel);
		txtCompression = new LabelField();
		summaryField.add(txtCompression, tdField);

		LabelField lblEncryption = new LabelField(
				UIContext.Constants.encryption()
						+ UIContext.Constants.delimiter());
		lblEncryption.setAutoWidth(true);
		summaryField.add(lblEncryption, tdLabel);
		txtEncryption = new LabelField();
		summaryField.add(txtEncryption, tdField);

		LabelField lblStartTime = new LabelField(
				UIContext.Constants.startTime()
						+ UIContext.Constants.delimiter());
		lblStartTime.setAutoWidth(true);
		summaryField.add(lblStartTime, tdLabel);
		txtStartTime = new LabelField();
		summaryField.add(txtStartTime, tdField);

		scheduleSettings = new BackupScheduleSettings(false, false);
		scheduleSettings.setVisible(false);
		summaryField.add(scheduleSettings, tdColspan);

		fullContainer = new LayoutContainer();
		fullContainer.setLayout(getScheduleLayout());

		LabelField lblFullSchedule = new LabelField(
				UIContext.Constants.scheduleLabelFullBackup()
						+ UIContext.Constants.delimiter());
		lblFullSchedule.setAutoWidth(true);
		fullContainer.add(lblFullSchedule, tdLabel);
		txtFullSchedule = new LabelField();
		fullContainer.add(txtFullSchedule, tdField);
		summaryField.add(fullContainer, tdColspan);

		incrementalContainer = new LayoutContainer();
		incrementalContainer.setLayout(getScheduleLayout());

		LabelField lblIncrementalSchedule = new LabelField(
				UIContext.Constants.scheduleLabelIncrementalBackup()
						+ UIContext.Constants.delimiter());
		lblIncrementalSchedule.setAutoWidth(true);
		incrementalContainer.add(lblIncrementalSchedule, tdLabel);
		txtIncrementalSchedule = new LabelField();
		incrementalContainer.add(txtIncrementalSchedule, tdField);
		summaryField.add(incrementalContainer, tdColspan);

		resyncContainer = new LayoutContainer();
		resyncContainer.setLayout(getScheduleLayout());

		LabelField lblResyncSchedule = new LabelField(
				UIContext.Constants.scheduleLabelResyncBackup()
						+ UIContext.Constants.delimiter());
		lblResyncSchedule.setAutoWidth(true);
		resyncContainer.add(lblResyncSchedule, tdLabel);
		txtResyncSchedule = new LabelField();
		resyncContainer.add(txtResyncSchedule, tdField);
		summaryField.add(resyncContainer, tdColspan);

		LabelField lblRecoverySetNumber = new LabelField(
				UIContext.Constants.summaryBackupSetNumber()
						+ UIContext.Constants.delimiter());
		lblResyncSchedule.setAutoWidth(true);
		summaryField.add(lblRecoverySetNumber, tdLabel);
		txtRecoverySetNumber = new LabelField();
		summaryField.add(txtRecoverySetNumber, tdField);

		LabelField lblThroughput = new LabelField(
				UIContext.Constants.throughputDescription()
						+ UIContext.Constants.delimiter());
		lblThroughput.setAutoWidth(true);
		summaryField.add(lblThroughput, tdLabel);
		txtThroughput = new LabelField();
		summaryField.add(txtThroughput, tdField);

		LabelField lblServerScriptBeforeJob = new LabelField(
				UIContext.Constants.summaryServerScriptBeforeJob()
						+ UIContext.Constants.delimiter());
		lblServerScriptBeforeJob.setAutoWidth(true);
		summaryField.add(lblServerScriptBeforeJob, tdLabel);
		txtServerScriptBeforeJob = new LabelField();
		summaryField.add(txtServerScriptBeforeJob, tdField);

		LabelField lblServerScriptAfterJob = new LabelField(
				UIContext.Constants.summaryServerScriptAfterJob()
						+ UIContext.Constants.delimiter());
		lblServerScriptAfterJob.setAutoWidth(true);
		summaryField.add(lblServerScriptAfterJob, tdLabel);
		txtServerScriptAfterJob = new LabelField();
		summaryField.add(txtServerScriptAfterJob, tdField);

		LabelField lblTargetScriptBeforeJob = new LabelField(
				UIContext.Constants.summaryTargetScriptBeforeJob()
						+ UIContext.Constants.delimiter());
		lblTargetScriptBeforeJob.setAutoWidth(true);
		summaryField.add(lblTargetScriptBeforeJob, tdLabel);
		txtTargetScriptBeforeJob = new LabelField();
		summaryField.add(txtTargetScriptBeforeJob, tdField);

		LabelField lblTargetScriptAfterJob = new LabelField(
				UIContext.Constants.summaryTargetScriptAfterJob()
						+ UIContext.Constants.delimiter());
		lblTargetScriptAfterJob.setAutoWidth(true);
		summaryField.add(lblTargetScriptAfterJob, tdLabel);
		txtTargetScriptAfterJob = new LabelField();
		summaryField.add(txtTargetScriptAfterJob, tdField);

		LabelField lblTargetScriptBeforeSnapshot = new LabelField(
				UIContext.Constants.summaryTargetScriptBeforeSnapshot()
						+ UIContext.Constants.delimiter());
		lblTargetScriptBeforeSnapshot.setAutoWidth(true);
		summaryField.add(lblTargetScriptBeforeSnapshot, tdLabel);
		txtTargetScriptBeforeSnapshot = new LabelField();
		summaryField.add(txtTargetScriptBeforeSnapshot, tdField);

		LabelField lblTargetScriptAfterSnapshot = new LabelField(
				UIContext.Constants.summaryTargetScriptAfterSnapshot()
						+ UIContext.Constants.delimiter());
		lblTargetScriptAfterSnapshot.setAutoWidth(true);
		summaryField.add(lblTargetScriptAfterSnapshot, tdLabel);
		txtTargetScriptAfterSnapshot = new LabelField();
		summaryField.add(txtTargetScriptAfterSnapshot, tdField);

		// return summaryField;
		container.add(summaryField);
		return container;
	}

	private TableLayout getScheduleLayout() {
		TableLayout scheduleLayout = new TableLayout();
		scheduleLayout.setWidth("100%");
		scheduleLayout.setColumns(2);
		return scheduleLayout;
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

		LabelField jobNameLbl = new LabelField(UIContext.Constants.jobName());
		jobNameLbl.setWidth(JOB_LABLE_WIDTH);
		jobName = new TextField<String>();
		jobName.setWidth(JOB_FIELD_WIDTH);
		jobName.setValue(UIContext.Constants.backupDefaultJobName());
		jobName.setAllowBlank(false);
		jobName.setAutoValidate(true);
		jobName.setMaxLength(JOB_NAME_MAX_LENGTH);
		container.add(jobNameLbl, tdLabel);
		container.add(jobName, tdField);
		return container;
	}

	public String getJobName() {
		if (jobName != null) {
			return jobName.getValue();
		}
		return null;
	}

	public void save() {
		if (jobName != null) {
			parentPanel.backupModel.setJobName(jobName.getValue());
		}
	}

	public void refresh() {
		BackupModel model = parentPanel.backupModel;
		int scheduleType = model.getScheduleType();
		if (scheduleType == BackupModel.SCHEDULE_TYPE_NOW) {
			txtStartTime.setValue(UIContext.Constants.runNow());
			setScheduleContainerVisable(false);
			scheduleSettings.setVisible(false);
		} else if (scheduleType == BackupModel.SCHEDULE_TYPE_ONCE) {
			txtStartTime.setValue(Utils.formatD2DTime(model.startTime));
			setScheduleContainerVisable(false);
			scheduleSettings.setVisible(false);
		} else if (scheduleType == BackupModel.SCHEDULE_TYPE_MANUALLY) {
			txtStartTime.setValue(UIContext.Constants.runManually());
			setScheduleContainerVisable(false);
			scheduleSettings.setVisible(false);
		} else if (scheduleType == BackupModel.SCHEDULE_TYPE_REPEAT_DAILY) {
			txtStartTime.setValue(Utils
					.formatD2DTime(model.dailySchedule.startTime));
			setScheduleContainerVisable(true);
			refreshSchedule(txtFullSchedule, model.dailySchedule.fullSchedule);
			refreshSchedule(txtIncrementalSchedule,
					model.dailySchedule.incrementalSchedule);
			refreshSchedule(txtResyncSchedule,
					model.dailySchedule.resyncSchedule);
			scheduleSettings.setVisible(false);
		} else if (scheduleType == BackupModel.SCHEDULE_TYPE_REPEAT_WEEKLY) {
			txtStartTime.setValue(Utils
					.formatD2DDate(model.weeklySchedule.startTime));
			setScheduleContainerVisable(false);
			scheduleSettings.refresh(model.weeklySchedule);
			scheduleSettings.setVisible(true);
		}

		String volumeFilter = parentPanel.getExcludeVolumns();
		if (parentPanel.isExclude()) {
			txtInclude.setVisible(false);
			lblInclude.setVisible(false);
			txtExclude.setVisible(true);
			lblExclude.setVisible(true);
			if (Utils.isEmptyOrNull(volumeFilter)) {
				volumeFilter = UIContext.Constants.none();
			}
			txtExclude.setText(volumeFilter);
		} else {
			txtInclude.setVisible(true);
			lblInclude.setVisible(true);
			txtExclude.setVisible(false);
			lblExclude.setVisible(false);
			if (Utils.isEmptyOrNull(volumeFilter)) {
				volumeFilter = UIContext.Constants.none();
			}
			txtInclude.setText(volumeFilter);
		}

		ServiceInfoModel d2dServer = parentPanel.getD2DServerInfo();
		txtD2DServer.setText(d2dServer == null ? UIContext.Constants.none()
				: d2dServer.getServer());

		List<NodeModel> backupSourceInfo = parentPanel.getBackupSourceInfo();
		table.removeAllData();
		table.setServiceInfo(d2dServer);
		table.addData(backupSourceInfo);

		String exclude = parentPanel.getExcludeVolumns();
		if (Utils.isEmptyOrNull(exclude)) {
			exclude = UIContext.Constants.none();
		}
		txtExclude.setText(exclude);

		String excludeFiles = parentPanel.getExcludeFiles();
		if (Utils.isEmptyOrNull(excludeFiles)) {
			excludeFiles = UIContext.Constants.none();
		}
		txtExcludeFiles.setText(excludeFiles);

		BackupTemplateModel backupDest = parentPanel.getBackupDestinationInfo();
		if (backupDest != null) {
			String sessLoc = backupDest.backupLocationInfoModel
					.getSessionLocation();
			if (Utils.isEmptyOrNull(sessLoc)) {
				sessLoc = UIContext.Constants.none();
			}
			txtDestination.setText(sessLoc);

			String compress = CompressType.displayMessage(backupDest
					.getCompression());
			if (Utils.isEmptyOrNull(compress)) {
				compress = UIContext.Constants.none();
			}
			txtCompression.setText(compress);

			String encrypt = backupDest.getEncryptionName();
			if (Utils.isEmptyOrNull(encrypt)) {
				encrypt = UIContext.Constants.none();
			}
			txtEncryption.setText(encrypt);
		}

		/*
		 * String job = parentPanel.getJobName(); if (
		 * !(Utils.isEmptyOrNull(job)) ) { jobName.setValue(job); } else {
		 * String curTimeStr =
		 * Utils.formatD2DTime(parentPanel.getD2dServerTime());
		 * jobName.setValue(UIContext.Constants.backupDefaultJobName() + " - " +
		 * curTimeStr); }
		 */

		if (model.retentionModel != null) {
			txtRecoverySetNumber.setValue(model.retentionModel
					.getBackupSetCount());
		}
		String throughput = UIContext.Constants.throughputNoLimit();
		if (model.getThrottle() != 0) {
			throughput = model.getThrottle() + " "
					+ UIContext.Constants.throughputUnit();
		}
		txtThroughput.setValue(throughput);

		if (!Utils.isEmptyOrNull(model.getServerScriptBeforeJob())) {
			txtServerScriptBeforeJob.setValue(model.getServerScriptBeforeJob());
		} else {
			txtServerScriptBeforeJob.setValue(UIContext.Constants.none());
		}

		if (!Utils.isEmptyOrNull(model.getServerScriptAfterJob())) {
			txtServerScriptAfterJob.setValue(model.getServerScriptAfterJob());
		} else {
			txtServerScriptAfterJob.setValue(UIContext.Constants.none());
		}

		if (!Utils.isEmptyOrNull(model.getTargetScriptBeforeJob())) {
			txtTargetScriptBeforeJob.setValue(model.getTargetScriptBeforeJob());
		} else {
			txtTargetScriptBeforeJob.setValue(UIContext.Constants.none());
		}

		if (!Utils.isEmptyOrNull(model.getTargetScriptAfterJob())) {
			txtTargetScriptAfterJob.setValue(model.getTargetScriptAfterJob());
		} else {
			txtTargetScriptAfterJob.setValue(UIContext.Constants.none());
		}

		if (!Utils.isEmptyOrNull(model.getTargetScriptBeforeSnapshot())) {
			txtTargetScriptBeforeSnapshot.setValue(model
					.getTargetScriptBeforeSnapshot());
		} else {
			txtTargetScriptBeforeSnapshot.setValue(UIContext.Constants.none());
		}

		if (!Utils.isEmptyOrNull(model.getTargetScriptAfterSnapshot())) {
			txtTargetScriptAfterSnapshot.setValue(model
					.getTargetScriptAfterSnapshot());
		} else {
			txtTargetScriptAfterSnapshot.setValue(UIContext.Constants.none());
		}

	}

	public void setJobName(String name) {
		if (jobName != null)
			jobName.setValue(name);
	}

	private void refreshSchedule(LabelField txtLabel,
			BackupScheduleModel schedule) {
		if (schedule != null) {
			txtLabel.setValue(schedule.displayMessage());
		} else {
			txtLabel.setValue(UIContext.Constants.NA());
		}
	}

	private void setScheduleContainerVisable(boolean isVisable) {
		incrementalContainer.setVisible(isVisable);
		fullContainer.setVisible(isVisable);
		resyncContainer.setVisible(isVisable);
	}

}
