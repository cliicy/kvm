package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.Date;
import java.util.List;
import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.SessionLocationPanel;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.icons.FlashImageBundle;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage.BackupStorageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.backupstorage.BackupStorageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.toolbar.ToolBarPanel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

public class RestoreWindow extends Window {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private static final BackupStorageServiceAsync storageService = GWT
			.create(BackupStorageService.class);
	FlashImageBundle IconBundle = GWT.create(FlashImageBundle.class);
	public final int WINDOW_WIDTH = 840;
	public final int WINDOW_HEIGHT = 550;
	public final int LEFT_PANEL_WIDTH = 140;
	public final static int RIGHT_PANEL_WIDTH = 660;
	public final static int RIGHT_PANEL_HIGHT = 440;

	public final int STACK_BACKUP_SERVER = 0;
	public final int STACK_RECOVERY_POINTS = 1;
	public final int STACK_TARGET_MACHINE = 2;
	public final int STACK_ADVANCED = 3;
	public final int STACK_SUMMARY = 4;
	private int currentIndex;
	private boolean isAddedPublicKey;
	private RestoreWindow restoreWindow;
	private DeckPanel configDeckPanel;
	private VerticalPanel toggleButtonPanel;
	private ToggleButton tbBackServerLabel;
	private ToggleButton tbRecoveryPointsLabel;
	private ToggleButton tbBackServerButton;
	private ToggleButton tbRecoveryPointsButton;
	private ClickHandler chBackServerHandler;
	private ClickHandler chRecoveryPointsHandler;
	private SummarySettings summarySettings;
	private ClickHandler chTargetMachineHandler;
	private ToggleButton tbTargetMachineButton;
	private ToggleButton tbTargetMachineLabel;

	private ClickHandler advancedSettingsHandler;
	private ToggleButton tbAdvancedSettingsButton;
	private ToggleButton tbAdvancedSettingsLabel;

	private ToggleButton tbSummaryButton;
	private ToggleButton tbSummaryLabel;
	private ClickHandler chSummaryHandler;
	public BackupServerSettings backupServerSettings;
	public RecoveryPointSettings recoveryPointSettings;
	public TargetMachineSettings targetMachineSettings;
	public AdvancedSettings advancedSettings;
	public LayoutContainer rightPanel;
	private Button nextButton;
	private Button cancelButton;
	private Button helpButton;
	private Button previousButton;
	public ToolBarPanel toolBar;
	public ServiceInfoModel currentServer;
	private D2DTimeModel d2dServerTime;

	public RestoreModel restoreModel = new RestoreModel();
	public boolean isModify = false;
	private String defaultMachine;
	private String defaultLocation;
	private RestoreType restoreType;
	private BackupLocationInfoModel sessionLocation;

	public RestoreWindow(ToolBarPanel toolBar, RestoreType restoreType,
			String defaultMachine, String defaultLocation,
			BackupLocationInfoModel sessionLocation) {
		this.defaultMachine = Utils.getNodeNameWithoutPort(defaultMachine);
		this.defaultLocation = defaultLocation;
		this.sessionLocation = sessionLocation;
		this.toolBar = toolBar;
		restoreWindow = this;
		this.restoreType = restoreType;
		this.restoreModel.setRestoreType(restoreType);
		this.setLayout(new FitLayout());

		if (restoreType == RestoreType.SHARE_RECOVERY_POINT) {
			this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT - 80);
		} else {
			this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		}
		this.setModal(true);
		this.setResizable(false);
		if (restoreType == RestoreType.BMR) {
			restoreWindow.setHeading(UIContext.Constants.restoreWizardForBMR());
		} else if (restoreType == RestoreType.FILE) {
			restoreWindow
					.setHeading(UIContext.Constants.restoreWizardForFile());
		} else if (restoreType == RestoreType.VM) {
			restoreWindow.setHeading(UIContext.Constants.restoreWizardForVM());
		} else if (restoreType == RestoreType.MIGRATION_BMR) {
			restoreWindow.setHeading(UIContext.Constants
					.restoreWizardForMigration());
		} else if (restoreType == RestoreType.SHARE_RECOVERY_POINT) {
			restoreWindow.setHeading(UIContext.Constants
					.restoreWizardForShareRecoveryPoint());
		} else {
			restoreWindow.setHeading(UIContext.Constants.restoreWizardForBMR());
		}
		LayoutContainer containerPanel = new LayoutContainer();
		containerPanel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		containerPanel.setStyleAttribute("background-color", "white");
		defineToggleButtonPanel();
		defineRightPanel();
		containerPanel.add(toggleButtonPanel, new RowData(LEFT_PANEL_WIDTH, 1));
		containerPanel.add(rightPanel, new RowData(1, 1));
		this.add(containerPanel, new RowData(1, 1));
		showSettings(STACK_BACKUP_SERVER);
	}

	private void defineRightPanel() {

		rightPanel = new LayoutContainer();
		rightPanel.setLayout(new BorderLayout());
		defineDeckPanel();
		ButtonBar buttonPanel = defineButtonsPanel();

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0));
		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,
				30);
		southData.setMargins(new Margins(0));
		rightPanel.add(configDeckPanel, centerData);
		rightPanel.add(buttonPanel, southData);

	}

	private ButtonBar defineButtonsPanel() {
		ButtonBar bar = new ButtonBar();
		bar.setAlignment(HorizontalAlignment.RIGHT);
		bar.setMinButtonWidth(UIContext.BUTTON_MINWIDTH);
		bar.setStyleAttribute("background-color", "white");

		previousButton = new Button(UIContext.Constants.previous());
		previousButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						showPreviousSettings();
					}

				});

		nextButton = new Button(UIContext.Constants.next());
		nextButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				showNextSettings();
			}

		});
		cancelButton = new Button(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				MessageBox.confirm(UIContext.Constants.productName(),
						UIContext.Constants.closeRestoreWizardConfirm(),
						new Listener<MessageBoxEvent>() {

							@Override
							public void handleEvent(MessageBoxEvent be) {
								if (be.getButtonClicked().getItemId()
										.equals(Dialog.YES)) {
									restoreWindow.hide();
								} else {
									be.cancelBubble();
								}
							}
						});

			}

		});
		helpButton = new Button(UIContext.Constants.help());
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				showHelpURL();
			}

		});
		bar.add(previousButton);
		bar.add(nextButton);
		bar.add(cancelButton);
		bar.add(helpButton);
		return bar;
	}

	private void defineDeckPanel() {
		configDeckPanel = new DeckPanel();
		backupServerSettings = getBackupServerSettings();
		backupServerSettings.setStyleAttribute("background-color", "white");
		backupServerSettings.setStyleAttribute("padding", "10px");
		configDeckPanel.add(backupServerSettings);

		recoveryPointSettings = new RecoveryPointSettings(this);
		recoveryPointSettings.setStyleAttribute("background-color", "white");
		recoveryPointSettings.setStyleAttribute("padding", "10px");
		configDeckPanel.add(recoveryPointSettings);

		targetMachineSettings = getTargetMachineSettings();
		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("background-color", "white");
		container.setStyleAttribute("padding", "10px");
		container.add(targetMachineSettings);
		configDeckPanel.add(container);

		advancedSettings = getAdvancedSettings();
		container = new LayoutContainer();
		container.setStyleAttribute("background-color", "white");
		container.setStyleAttribute("padding", "10px");
		container.add(advancedSettings);
		configDeckPanel.add(container);

		summarySettings = new SummarySettings(this);
		summarySettings.setStyleAttribute("background-color", "white");
		summarySettings.setStyleAttribute("padding", "10px");
		configDeckPanel.add(summarySettings);
	}

	private void defineToggleButtonPanel() {
		toggleButtonPanel = new VerticalPanel();
		toggleButtonPanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		toggleButtonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		toggleButtonPanel.setTableWidth("100%");
		toggleButtonPanel.setStyleAttribute("background-color", "#DFE8F6");

		chBackServerHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSettings(STACK_BACKUP_SERVER);
			}
		};

		tbBackServerButton = new ToggleButton(new Image(
				UIContext.IconBundle.backup_server()));
		tbBackServerButton
				.ensureDebugId("AB330B95-5473-46b7-9A75-EEFE0762923C");
		tbBackServerButton.setStylePrimaryName("linux-ToggleButton");
		tbBackServerButton.addClickHandler(chBackServerHandler);
		toggleButtonPanel.add(tbBackServerButton);

		tbBackServerLabel = new ToggleButton(UIContext.Constants.backupServer());
		tbBackServerLabel.ensureDebugId("EC4E00F2-6E16-4eca-ADF6-5D1014F2BA19");
		tbBackServerLabel.setStylePrimaryName("tb-settings");
		tbBackServerLabel.setDown(true);
		tbBackServerLabel.addClickHandler(chBackServerHandler);
		toggleButtonPanel.add(tbBackServerLabel);

		chRecoveryPointsHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSettings(STACK_RECOVERY_POINTS);
			}
		};

		tbRecoveryPointsButton = new ToggleButton(new Image(
				UIContext.IconBundle.recovery_points()));
		tbRecoveryPointsButton
				.ensureDebugId("35963CD0-3E34-4c55-8436-B4A987703FAE");
		tbRecoveryPointsButton.setStylePrimaryName("linux-ToggleButton");
		tbRecoveryPointsButton.setDown(true);
		tbRecoveryPointsButton.addClickHandler(chRecoveryPointsHandler);
		toggleButtonPanel.add(tbRecoveryPointsButton);

		tbRecoveryPointsLabel = new ToggleButton(
				UIContext.Constants.recoveryPoints());
		tbRecoveryPointsLabel
				.ensureDebugId("3A89A05C-EC63-49c0-898C-7841AF5178B8");
		tbRecoveryPointsLabel.setStylePrimaryName("tb-settings");
		tbRecoveryPointsLabel.setDown(true);
		tbRecoveryPointsLabel.addClickHandler(chRecoveryPointsHandler);
		toggleButtonPanel.add(tbRecoveryPointsLabel);

		chTargetMachineHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSettings(STACK_TARGET_MACHINE);
			}
		};

		tbTargetMachineButton = new ToggleButton(new Image(
				UIContext.IconBundle.target_machine()));
		tbTargetMachineButton
				.ensureDebugId("35963CD0-3E34-4c55-8436-B4A987703FAE");
		tbTargetMachineButton.setStylePrimaryName("linux-ToggleButton");
		tbTargetMachineButton.setDown(true);
		tbTargetMachineButton.addClickHandler(chTargetMachineHandler);
		toggleButtonPanel.add(tbTargetMachineButton);

		if (restoreType == RestoreType.SHARE_RECOVERY_POINT) {
			tbTargetMachineLabel = new ToggleButton(
					UIContext.Constants.settings());
		} else {
			tbTargetMachineLabel = new ToggleButton(
					UIContext.Constants.targetMachine());
		}

		tbTargetMachineLabel
				.ensureDebugId("3A89A05C-EC63-49c0-898C-7841AF5178B8");
		tbTargetMachineLabel.setStylePrimaryName("tb-settings");
		tbTargetMachineLabel.setDown(true);
		tbTargetMachineLabel.addClickHandler(chTargetMachineHandler);
		toggleButtonPanel.add(tbTargetMachineLabel);

		advancedSettingsHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSettings(STACK_ADVANCED);
			}
		};

		tbAdvancedSettingsButton = new ToggleButton(new Image(
				UIContext.IconBundle.advanced_settings()));
		tbAdvancedSettingsButton
				.ensureDebugId("35963CD0-3E34-4c55-8436-B4A987703FAE");
		tbAdvancedSettingsButton.setStylePrimaryName("linux-ToggleButton");
		tbAdvancedSettingsButton.setDown(true);
		tbAdvancedSettingsButton.addClickHandler(advancedSettingsHandler);
		tbAdvancedSettingsLabel = new ToggleButton(
				UIContext.Constants.advanced());
		tbAdvancedSettingsLabel
				.ensureDebugId("3A89A05C-EC63-49c0-898C-7841AF5178B8");
		tbAdvancedSettingsLabel.setStylePrimaryName("tb-settings");
		tbAdvancedSettingsLabel.setDown(true);
		tbAdvancedSettingsLabel.addClickHandler(advancedSettingsHandler);

		if (restoreType != RestoreType.SHARE_RECOVERY_POINT) {
			toggleButtonPanel.add(tbAdvancedSettingsButton);
			toggleButtonPanel.add(tbAdvancedSettingsLabel);
		}
		chSummaryHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSettings(STACK_SUMMARY);
			}
		};

		tbSummaryButton = new ToggleButton(new Image(
				UIContext.IconBundle.summary()));
		tbSummaryButton.ensureDebugId("35963CD0-3E34-4c55-8436-B4A987703FAE");
		tbSummaryButton.setStylePrimaryName("linux-ToggleButton");
		tbSummaryButton.setDown(true);
		tbSummaryButton.addClickHandler(chSummaryHandler);
		toggleButtonPanel.add(tbSummaryButton);

		tbSummaryLabel = new ToggleButton(UIContext.Constants.summary());
		tbSummaryLabel.ensureDebugId("3A89A05C-EC63-49c0-898C-7841AF5178B8");
		tbSummaryLabel.setStylePrimaryName("tb-settings");
		tbSummaryLabel.setDown(true);
		tbSummaryLabel.addClickHandler(chSummaryHandler);
		toggleButtonPanel.add(tbSummaryLabel);
	}

	public void showPreviousSettings() {
		if (restoreType == RestoreType.SHARE_RECOVERY_POINT
				&& currentIndex == STACK_SUMMARY) {
			showSettings(currentIndex - 2);
		} else {
			showSettings(currentIndex - 1);
		}
	}

	public void showNextSettings(boolean needValidate) {
		if (currentIndex == STACK_SUMMARY) {
			nextButton.disable();
			if (!summarySettings.validate()) {
				nextButton.enable();
				return;
			}
			restoreModel.setJobName(summarySettings.getJobName());
			if (advancedSettings.getUserSetTime().isRunNow()) {
				submitRestoreJob();
				return;
			}
			mask(UIContext.Constants.validating());
			service.getCurrentD2DTimeFromServer(currentServer,
					new BaseAsyncCallback<D2DTimeModel>() {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							unmask();
						}

						@Override
						public void onSuccess(D2DTimeModel result) {
							D2DTimeModel time = advancedSettings
									.getUserSetTime();
							if (Utils.convertD2DTimeModel(time).getTime() <= Utils
									.convertD2DTimeModel(result).getTime()) {
								nextButton.enable();
								Utils.showMessage(UIContext.Constants
										.productName(), MessageBox.ERROR,
										UIContext.Constants
												.startTimeOutdatedMessage());
								unmask();
							} else {
								submitRestoreJob();
							}
						}

					});
		} else if (currentIndex == STACK_ADVANCED) {
			if (advancedSettings.getUserSetTime().isRunNow()) {
				showSettings(currentIndex + 1);
				return;
			}
			service.getCurrentD2DTimeFromServer(currentServer,
					new BaseAsyncCallback<D2DTimeModel>() {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
						}

						@Override
						public void onSuccess(D2DTimeModel result) {
							D2DTimeModel time = advancedSettings
									.getUserSetTime();
							if (Utils.convertD2DTimeModel(time).getTime() <= Utils
									.convertD2DTimeModel(result).getTime()) {
								Utils.showMessage(UIContext.Constants
										.productName(), MessageBox.ERROR,
										UIContext.Constants
												.startTimeOutdatedMessage());
							} else {
								showSettings(currentIndex + 1);
							}
						}

					});
		} else {
			if (restoreType == RestoreType.SHARE_RECOVERY_POINT
					&& currentIndex == STACK_TARGET_MACHINE) {
				showSettings(currentIndex + 2, needValidate);
			} else {
				showSettings(currentIndex + 1, needValidate);
			}
		}
	}

	public void showNextSettings() {
		showNextSettings(true);
	}

	private void submitRestoreJob() {
		mask(UIContext.Constants.validating());
		service.submitRestoreJob(currentServer, restoreModel,
				new BaseAsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						nextButton.enable();
						unmask();
					}

					@Override
					public void onSuccess(Integer result) {
						if (result == 0) {
							restoreWindow.hide();
							if (toolBar.tabPanel.presentTabIndex
									.equals(HomepageTab.JOB_STATUS)) {
								if (UIContext.isRestoreMode) {
									toolBar.tabPanel.jobStatusTable
											.filterByJobType(
													JobStatusFilterModel.JOB_TYPE_RESTORE,
													true);
								} else {
									toolBar.tabPanel
											.refreshJobStatusTable(true);
								}
							} else {
								if (UIContext.isRestoreMode) {
									toolBar.tabPanel.setNeedRefresh(false);
									toolBar.tabPanel
											.showTabItem(HomepageTab.JOB_STATUS);
									toolBar.tabPanel.setNeedRefresh(true);
									toolBar.tabPanel.jobStatusTable
											.filterByJobType(
													JobStatusFilterModel.JOB_TYPE_RESTORE,
													true);
								} else {
									toolBar.tabPanel
											.showTabItem(HomepageTab.JOB_STATUS);
								}
							}
						} else {
							nextButton.enable();
							unmask();
							Utils.showMessage(
									UIContext.Constants.productName(),
									MessageBox.ERROR, UIContext.Constants
											.submitRestoreJobFailMessage());

						}

					}

				});
	}

	public void showSettings(int i) {
		showSettings(i, true);
	}

	public void showSettings(int i, boolean needValidate) {
		disableToggleButton();
		switch (i) {
		case STACK_BACKUP_SERVER:
			currentIndex = STACK_BACKUP_SERVER;
			configDeckPanel.showWidget(STACK_BACKUP_SERVER);
			setToggleBtState(currentIndex);
			previousButton.hide();
			nextButton.setText(UIContext.Constants.next());
			break;
		case STACK_RECOVERY_POINTS:
			if (needValidate) {
				if (!backupServerSettings.validate()) {
					setToggleBtState(currentIndex);
					return;
				}else{
					RestoreWindow.this.mask(UIContext.Constants.validating());
					BaseAsyncCallback<Boolean> callBack = new BaseAsyncCallback<Boolean>() {
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							RestoreWindow.this.unmask();
						}

						@Override
						public void onSuccess(Boolean result) {
							if (result) {
								currentIndex = STACK_RECOVERY_POINTS;
								backupServerSettings.save();
								recoveryPointSettings.showPointItemsTable(getRestoreType());
								configDeckPanel.showWidget(STACK_RECOVERY_POINTS);
								setToggleBtState(currentIndex);
								previousButton.show();
								nextButton.setText(UIContext.Constants.next());
								if (defaultLocation != null)
									recoveryPointSettings.refreshPointTable();
							}
							RestoreWindow.this.unmask();
						}
					};
					backupServerSettings.validateAsync(callBack);
					return;
				}
			}
			currentIndex = STACK_RECOVERY_POINTS;
			backupServerSettings.save();
			recoveryPointSettings.showPointItemsTable(getRestoreType());
			configDeckPanel.showWidget(STACK_RECOVERY_POINTS);
			setToggleBtState(currentIndex);
			previousButton.show();
			nextButton.setText(UIContext.Constants.next());
			if (defaultLocation != null)
				recoveryPointSettings.refreshPointTable();
			break;
		case STACK_TARGET_MACHINE:
			if (!backupServerSettings.validate()
					|| !recoveryPointSettings
							.validate(SessionLocationPanel.EVENT_TYPE_NEXT)) {
				setToggleBtState(currentIndex);
				return;
			}
			backupServerSettings.save();
			recoveryPointSettings.save();
			BaseAsyncCallback<Boolean> callBack = new BaseAsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
				}

				@Override
				public void onSuccess(Boolean result) {
					if (result) {
						targetMachineSettings.refreshWhenShowing();
						currentIndex = STACK_TARGET_MACHINE;
						configDeckPanel.showWidget(STACK_TARGET_MACHINE);
						setToggleBtState(currentIndex);
						previousButton.show();
						nextButton.setText(UIContext.Constants.next());
					}
				}
			};
			recoveryPointSettings.checkRecoveryPointPwAndRoot(callBack);
			break;
		case STACK_ADVANCED:
			if (!backupServerSettings.validate()
					|| !recoveryPointSettings
							.validate(SessionLocationPanel.EVENT_TYPE_NEXT)
					|| !targetMachineSettings.validate()) {
				setToggleBtState(currentIndex);
				return;
			}
			backupServerSettings.save();
			recoveryPointSettings.save();
			targetMachineSettings.save();
			currentIndex = STACK_ADVANCED;
			configDeckPanel.showWidget(STACK_ADVANCED);
			setToggleBtState(currentIndex);
			previousButton.show();
			nextButton.setText(UIContext.Constants.next());
			break;
		case STACK_SUMMARY:
			if (!backupServerSettings.validate()
					|| !recoveryPointSettings
							.validate(SessionLocationPanel.EVENT_TYPE_NEXT)
					|| !targetMachineSettings.validate()
					|| !advancedSettings.validate()) {
				setToggleBtState(currentIndex);
				return;
			}
			BaseAsyncCallback<Boolean> callback = new BaseAsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
				}

				@Override
				public void onSuccess(Boolean result) {
					if (result) {
						backupServerSettings.save();
						recoveryPointSettings.save();
						targetMachineSettings.save();
						advancedSettings.save();
						if (currentIndex != STACK_SUMMARY)
							summarySettings.refreshData();
						currentIndex = STACK_SUMMARY;
						configDeckPanel.showWidget(STACK_SUMMARY);
						setToggleBtState(currentIndex);
						previousButton.show();
						nextButton.setText(UIContext.Constants.submit());
					}
				}
			};
			if (targetMachineSettings instanceof SPTargetSettings) {
				SPTargetSettings spTargetSettings = (SPTargetSettings) targetMachineSettings;
				spTargetSettings.validatePassword(callback);
			} else {
				callback.onSuccess(true);
			}
			break;
		}
	}

	private void setToggleBtState(int index) {
		disableToggleButton();
		switch (index) {
		case STACK_BACKUP_SERVER:
			tbBackServerLabel.setDown(true);
			tbBackServerButton.setDown(true);
			break;
		case STACK_RECOVERY_POINTS:
			tbRecoveryPointsLabel.setDown(true);
			tbRecoveryPointsButton.setDown(true);
			break;
		case STACK_TARGET_MACHINE:
			tbTargetMachineLabel.setDown(true);
			tbTargetMachineButton.setDown(true);
			break;
		case STACK_ADVANCED:
			tbAdvancedSettingsButton.setDown(true);
			tbAdvancedSettingsLabel.setDown(true);
			break;
		case STACK_SUMMARY:
			tbSummaryLabel.setDown(true);
			tbSummaryButton.setDown(true);
			break;
		}
	}

	private void disableToggleButton() {
		tbBackServerLabel.setDown(false);
		tbBackServerButton.setDown(false);
		tbRecoveryPointsLabel.setDown(false);
		tbRecoveryPointsButton.setDown(false);
		tbTargetMachineLabel.setDown(false);
		tbTargetMachineButton.setDown(false);
		tbAdvancedSettingsButton.setDown(false);
		tbAdvancedSettingsLabel.setDown(false);
		tbSummaryLabel.setDown(false);
		tbSummaryButton.setDown(false);
	}

	protected void showHelpURL() {
		if (restoreType == RestoreType.BMR) {
			switch (currentIndex) {
			case STACK_BACKUP_SERVER:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.BMR_RESTORE_WIZARD_SERVER);
				break;
			case STACK_RECOVERY_POINTS:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.BMR_RESTORE_WIZARD_RECOVERY_POINTS);
				break;
			case STACK_TARGET_MACHINE:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.BMR_RESTORE_WIZARD_TARGET);
				break;
			case STACK_ADVANCED:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.BMR_RESTORE_WIZARD_ADVANCED);
				break;
			case STACK_SUMMARY:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.BMR_RESTORE_WIZARD_SUMMARY);
				break;
			}
		} else if (restoreType == RestoreType.FILE) {
			switch (currentIndex) {
			case STACK_BACKUP_SERVER:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.FILE_RESTORE_WIZARD_SERVER);
				break;
			case STACK_RECOVERY_POINTS:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.FILE_RESTORE_WIZARD_RECOVERY_POINTS);
				break;
			case STACK_TARGET_MACHINE:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.FILE_RESTORE_WIZARD_TARGET);
				break;
			case STACK_ADVANCED:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.FILE_RESTORE_WIZARD_ADVANCED);
				break;
			case STACK_SUMMARY:
				Utils.showURL(UIContext.helpLink + HelpLinkItem.FILE_RESTORE_WIZARD_SUMMARY);
				break;
			}
		} else if (restoreType == RestoreType.SHARE_RECOVERY_POINT) {
			switch (currentIndex) {
			case STACK_BACKUP_SERVER:
				Utils.showURL(UIContext.helpLink + HelpLinkItem.MOUNT_POINT_SERVER);
				break;
			case STACK_RECOVERY_POINTS:
				Utils.showURL(UIContext.helpLink + HelpLinkItem.MOUNT_POINT_RECOVERY_POINTS);
				break;
			case STACK_TARGET_MACHINE:
				Utils.showURL(UIContext.helpLink + HelpLinkItem.MOUNT_POINT_SETTINGS);
				break;
			case STACK_SUMMARY:
				Utils.showURL(UIContext.helpLink + HelpLinkItem.MOUNT_POINT_SUMMARY);
				break;
			}
		} else if (restoreType == RestoreType.SHARE_RECOVERY_POINT) {
			switch (currentIndex) {
			case STACK_BACKUP_SERVER:
				Utils.showURL(UIContext.helpLink + HelpLinkItem.MOUNT_POINT_SERVER);
				break;
			case STACK_RECOVERY_POINTS:
				Utils.showURL(UIContext.helpLink + HelpLinkItem.MOUNT_POINT_RECOVERY_POINTS);
				break;
			case STACK_TARGET_MACHINE:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.MOUNT_POINT_SETTINGS);
				break;
			case STACK_SUMMARY:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.MOUNT_POINT_SUMMARY);
				break;
			} }
		else if (restoreType == RestoreType.MIGRATION_BMR) {
			switch (currentIndex) {
			case STACK_BACKUP_SERVER:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.MBMR_RESTORE_WIZARD_SERVER);
				break;
			case STACK_RECOVERY_POINTS:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.MBMR_RESTORE_WIZARD_RECOVERY_POINTS);
				break;
			case STACK_TARGET_MACHINE:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.MBMR_RESTORE_WIZARD_TARGET);
				break;
			case STACK_ADVANCED:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.MBMR_RESTORE_WIZARD_ADVANCED);
				break;
			case STACK_SUMMARY:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.MBMR_RESTORE_WIZARD_SUMMARY);
				break;
			}
		} else {
			switch (currentIndex) {
			case STACK_BACKUP_SERVER:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.BMR_RESTORE_WIZARD_SERVER);
				break;
			case STACK_RECOVERY_POINTS:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.BMR_RESTORE_WIZARD_RECOVERY_POINTS);
				break;
			case STACK_TARGET_MACHINE:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.BMR_RESTORE_WIZARD_TARGET);
				break;
			case STACK_SUMMARY:
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.BMR_RESTORE_WIZARD_SUMMARY);
				break;
			}
		}
	}

	public void refreshData(ServiceInfoModel server, String uuid, String jobName) {
		if (Utils.isEmptyOrNull(uuid))
			return;

		String heading = "";
		if (restoreType == RestoreType.BMR) {
			heading = UIContext.Constants.modifyBMRJob();
		} else if (restoreType == RestoreType.FILE) {
			heading = UIContext.Constants.modifyFileRestoreJob();
		} else if (restoreType == RestoreType.MIGRATION_BMR) {
			heading = UIContext.Constants.modifyMigrationJob();
		} else if (restoreType == RestoreType.SHARE_RECOVERY_POINT) {
			heading = UIContext.Constants.shareRecoveryPoint();
		} else {
			heading = UIContext.Constants.modifyBMRJob();
		}
		heading += " ( " + jobName + " )";
		restoreWindow.setHeading(heading);
		this.currentServer = server;
		toggleButtonPanel.mask();
		rightPanel.mask(UIContext.Constants.loading());
		service.getRestoreJobScriptByUUID(server, uuid,
				new BaseAsyncCallback<RestoreModel>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						restoreWindow.hide();
					}

					@Override
					public void onSuccess(RestoreModel result) {
						if (result == null) {
							Utils.showMessage(
									UIContext.Constants.productName(),
									MessageBox.ERROR, UIContext.Constants
											.getRestoreJobScriptFailed());
							restoreWindow.hide();
						}
						isModify = true;
						restoreModel = result;
						backupServerSettings.refreshData();
						recoveryPointSettings.refreshData();
						targetMachineSettings.refreshData();
						advancedSettings.refreshData();
						summarySettings.refreshData();
						summarySettings.setJobName(result.getJobName());
						toggleButtonPanel.unmask();
						rightPanel.unmask();
					}

				});
	}

	public void refresh(ServiceInfoModel server) {
		this.currentServer = server;
		setServerTime(server);
		if (restoreType == RestoreType.FILE || restoreType == RestoreType.BMR
				|| restoreType == RestoreType.MIGRATION_BMR
				|| restoreType == RestoreType.SHARE_RECOVERY_POINT
				|| restoreType == RestoreType.VM) {
			refreshPrePostScripts(server);
			refreshLocation(server);
		}
	}

	public void setServerTime(ServiceInfoModel server) {
		service.getCurrentD2DTimeFromServer(server,
				new BaseAsyncCallback<D2DTimeModel>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(D2DTimeModel result) {
						if (result != null) {
							d2dServerTime = result;
							Date time = Utils.convertD2DTimeModel(result);
							getAdvancedSettings().setStartDateTime(time);
							if (!isModify) {
								if (restoreType == RestoreType.FILE) {
									String curTimeStr = Utils
											.formatD2DTime(result);
									summarySettings
											.setJobName(UIContext.Constants
													.restore()
													+ " - "
													+ curTimeStr);
								}
							}
						}

					}

				});

	}

	public void refreshPrePostScripts(ServiceInfoModel server) {
		service.getScripts(server, Utils.SCRIPT_TYPE_PREPOST,
				new BaseAsyncCallback<List<String>>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						getAdvancedSettings().refreshPrePostScripts(null);
					}

					@Override
					public void onSuccess(List<String> result) {
						getAdvancedSettings().refreshPrePostScripts(result);
					}

				});
	}

	public void refreshLocation(ServiceInfoModel server) {
		storageService.getAllBackupLocation(server,
				new BaseAsyncCallback<List<BackupLocationInfoModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						recoveryPointSettings.refreshLocation(null, null, null);
					}

					@Override
					public void onSuccess(List<BackupLocationInfoModel> result) {
						if (sessionLocation != null) {
							BackupLocationInfoModel existBackupInfoModel = null;
							for (BackupLocationInfoModel backupInfoModel : result) {
								if (backupInfoModel.getDisplayName().equals(
										sessionLocation.getDisplayName())) {
									existBackupInfoModel = backupInfoModel;
								}
							}
							result.remove(existBackupInfoModel);
							result.add(sessionLocation);
						}
						recoveryPointSettings.refreshLocation(result,
								defaultLocation, defaultMachine);
					}

				});
	}

	public RestoreType getRestoreType() {
		return this.restoreType;
	}

	public BackupServerSettings getBackupServerSettings() {
		if (backupServerSettings == null) {
			if (restoreType != null && restoreType == RestoreType.MIGRATION_BMR) {
				backupServerSettings = new MigrationBackupServerSettings(this);
			} else {
				backupServerSettings = new BackupServerSettings(this);
			}
		}
		return backupServerSettings;
	}

	public TargetMachineSettings getTargetMachineSettings() {
		if (targetMachineSettings == null) {
			if (restoreModel == null || restoreType == null) {
				targetMachineSettings = new BMRTargetSettings(this);
			} else if (restoreType == RestoreType.MIGRATION_BMR) {
				targetMachineSettings = new MigrationTargetSettings(this);
			} else if (restoreType == RestoreType.FILE) {
				targetMachineSettings = new FileTargetSettings(this);
			} else if (restoreType == RestoreType.VM) {
				targetMachineSettings = new VMTargetSettings(this);
			} else if (restoreType == RestoreType.SHARE_RECOVERY_POINT) {
				targetMachineSettings = new SPTargetSettings(this);
			} else {
				targetMachineSettings = new BMRTargetSettings(this);
			}
		}
		return targetMachineSettings;
	}

	public AdvancedSettings getAdvancedSettings() {
		if (advancedSettings == null) {
			if (restoreType == RestoreType.FILE) {
				advancedSettings = new FileAdvancedSettings(this);
			} else {
				advancedSettings = new BMRAdvancedSettings(this);
			}
		}
		return advancedSettings;
	}

	public ServiceInfoModel getD2DServerInfo() {
		return currentServer;
	}

	public D2DTimeModel getD2dServerTime() {
		return d2dServerTime;
	}

	public boolean isAddedPublicKey() {
		return isAddedPublicKey;
	}

	public void setAddedPublicKey(boolean isAddedPublicKey) {
		this.isAddedPublicKey = isAddedPublicKey;
	}
}
