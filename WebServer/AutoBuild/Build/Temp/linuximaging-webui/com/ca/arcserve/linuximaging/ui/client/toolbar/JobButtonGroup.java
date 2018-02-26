package com.ca.arcserve.linuximaging.ui.client.toolbar;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.backup.wizard.BackupWizardPanel;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.StandbyType;
import com.ca.arcserve.linuximaging.ui.client.restore.RestoreWindow;
import com.ca.arcserve.linuximaging.ui.client.standby.StandbyWizardPanel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class JobButtonGroup extends ButtonGroup {
	public Button deleteJob;
	public Button runJob;
	public Button modifyJob;
	public Button cancelJob;
	private ToolBarPanel toolBar;

	public JobButtonGroup(int columns, int height) {
		super(columns);
		setHeight(height);
		setHeading(UIContext.Constants.toolBar_job());
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(1);
		layout.setCellSpacing(2);
		setLayout(layout);
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		runJob = new Button(UIContext.Constants.toolBar_runNow());
		// runJob.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.running()));
		runJob.setIcon(UIContext.IconHundle.running());
		runJob.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				runJob.disable();
				if (toolBar.tabPanel.presentTabIndex
						.equals(HomepageTab.JOB_STATUS)) {
					toolBar.tabPanel.jobStatusTable.runJob();
				}
			}

		});
		modifyJob = new Button(UIContext.Constants.toolBar_modify());
		// modifyJob.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.modify()));
		modifyJob.setIcon(UIContext.IconHundle.modify());
		modifyJob.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final JobStatusModel model = toolBar.tabPanel.jobStatusTable
						.getSelectedJobStatusModel();
				if (JobType.isBackup(model.getJobType())) {
					showBackupWizard(model);
				} else if (model.getJobType() == JobType.RESTORE.getValue()
						|| model.getJobType() == RestoreType.BMR.getValue()
						|| model.getJobType() == RestoreType.FILE.getValue()
						|| model.getJobType() == RestoreType.SHARE_RECOVERY_POINT
								.getValue()
						|| model.getJobType() == RestoreType.MIGRATION_BMR
								.getValue()) {
					RestoreWindow window = new RestoreWindow(toolBar,
							RestoreType.parse(model.getJobType()), null, null,
							null);
					window.refreshData(toolBar.nodeTree.getSelectedItem(),
							model.getJobUuid(), model.getJobName());
					window.show();
				} else if (model.getJobType() == StandbyType.PHYSICAL
						.getValue()
						|| model.getJobType() == StandbyType.VIRTUAL.getValue()) {
					StandbyWizardPanel window = new StandbyWizardPanel(
							toolBar.tabPanel.currentServer, null, false,
							UIContext.Constants.standbyPhysical(), StandbyType
									.parse(model.getJobType()));
					window.setHomepageTab(toolBar.tabPanel);
					window.show();
					window.refreshData(toolBar.nodeTree.getSelectedItem(),
							model.getJobUuid());
				}

				else if (model.getJobType() == RestoreType.VM.getValue()) {
					Utils.showMessage(UIContext.Constants.productName(),
							MessageBox.WARNING, UIContext.Constants
									.modifyVMRecoveryJobNotSupported());
				}

				else if (model.getJobType() == RestoreType.ASSURE_RECOVERY
						.getValue()) {
					Utils.showMessage(UIContext.Constants.productName(),
							MessageBox.WARNING, UIContext.Constants
									.modifyAssureRecoveryJobNotSupported());
				}

				setDefaultState();
				toolBar.tabPanel.jobStatusTable.jobStatusGrid
						.getSelectionModel().deselectAll();
			}

		});
		cancelJob = new Button(UIContext.Constants.toolBar_cancel());
		// cancelJob.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.cancel()));
		cancelJob.setIcon(UIContext.IconHundle.cancel());
		cancelJob.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				cancelJob.disable();
				if (toolBar.tabPanel.presentTabIndex
						.equals(HomepageTab.JOB_STATUS)) {
					toolBar.tabPanel.jobStatusTable.cancelJob();
				}
			}

		});
		deleteJob = new Button(UIContext.Constants.toolBar_delete());
		// deleteJob.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.delete()));
		deleteJob.setIcon(UIContext.IconHundle.delete());
		deleteJob.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (toolBar.tabPanel.presentTabIndex
						.equals(HomepageTab.JOB_STATUS)) {
					final JobStatusModel model = toolBar.tabPanel.jobStatusTable
							.getSelectedJobStatusModel();
					if (JobType.isBackup(model.getJobType())) {
						DeleteJobWindow deleteWindow = new DeleteJobWindow(
								toolBar.tabPanel.currentServer,
								toolBar.tabPanel, model);
						deleteWindow.setModal(true);
						deleteWindow.show();
					} else {
						deleteWithConfirm();
					}
				} else {
					deleteWithConfirm();
				}

			}

		});
		add(runJob);
		add(modifyJob);
		add(cancelJob);
		add(deleteJob);
	}

	private void deleteWithConfirm() {
		MessageBox.confirm(UIContext.Constants.delete(),
				UIContext.Constants.deleteConfirmMessage(),
				new Listener<MessageBoxEvent>() {

					@Override
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId()
								.equals(Dialog.YES)) {
							deleteJob.disable();
							if (toolBar.tabPanel.presentTabIndex
									.equals(HomepageTab.JOB_STATUS)) {
								toolBar.tabPanel.jobStatusTable.deleteJob();
							} else if (toolBar.tabPanel.presentTabIndex
									.equals(HomepageTab.JOB_HISTORY)) {
								toolBar.tabPanel.historyTable.deleteJob();
							}
						} else {
							be.cancelBubble();
						}
					}

				});
	}

	public void disableCancel() {
		runJob.enable();
		modifyJob.enable();
		cancelJob.disable();
		deleteJob.enable();
	}

	public void enableRun() {
		runJob.enable();
		modifyJob.disable();
		cancelJob.disable();
		;
		deleteJob.disable();
	}

	public void enableCancel() {
		runJob.disable();
		modifyJob.disable();
		cancelJob.enable();
		deleteJob.disable();
	}

	public void setToolBar(ToolBarPanel toolBarPanel) {
		this.toolBar = toolBarPanel;
	}

	public void enableDelete() {
		runJob.disable();
		modifyJob.disable();
		cancelJob.disable();
		deleteJob.enable();

	}

	public void enableCancelAndModify() {
		runJob.disable();
		modifyJob.enable();
		deleteJob.disable();
		cancelJob.enable();
	}

	private void showBackupWizard(JobStatusModel model) {
		BackupWizardPanel window = new BackupWizardPanel(
				toolBar.tabPanel.currentServer, true,
				UIContext.Constants.modifyBackupJob() + " ( "
						+ model.getJobName() + " )");
		window.setModal(true);
		window.show();
		window.setHomepageTab(toolBar.tabPanel);
		window.refreshData(toolBar.nodeTree.getSelectedItem(),
				model.getJobUuid());
	}

	public void setDefaultState() {
		runJob.disable();
		modifyJob.disable();
		cancelJob.disable();
		deleteJob.disable();
	}

	public void setAllEnabled(boolean isEnabled) {
		runJob.setEnabled(isEnabled);
		modifyJob.setEnabled(isEnabled);
		cancelJob.setEnabled(isEnabled);
		deleteJob.setEnabled(isEnabled);
	}
}
