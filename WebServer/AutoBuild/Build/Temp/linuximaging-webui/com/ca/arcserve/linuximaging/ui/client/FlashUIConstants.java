package com.ca.arcserve.linuximaging.ui.client;

import com.google.gwt.i18n.client.Constants;

public interface FlashUIConstants extends Constants {
	String productName();
	String productNameWithHtml();
	String productNameUDP();
	String login();
	String copyRight();
	String logout();
	String invalidCredential();
	String logoutConfirmMsg();
	String timeFormat();
	String timeDateFormat();
	String dateFormat();
	String backupStatusUnknown();
	String backupStatusFinished();
	String backupStatusFailed();
	String backupStatusActive();
	String backupStatusCanceled();
	String backupStatusCrashed();
	String NA();
	String yes();
	String no();
	String unknown();
	String inactiveSession();
	String cantConnectToServer();
	String finish();
	String failed();
	String successful();
	String finished();
	String incomplete();
	String other();
	String add();
	String delete();
	String modify();
	String cancel();
	String backup();
	String restore();
	String restoreVM();
	String instantVM();
	String restoreInstantBMR();
	String fullBackup();
	String incrementalBackup();
	String verifyBackup();
	String loading();
	String enableInstanceBMR();
	String notAutoRestoreData();
	String reboot();
	String feeds();
	String feedback();
	String startAutoRestore();
	String pauseAutoRestore();
	String startAutoRestoreSucceed();
	String pauseAutoRestoreSucceed();
	String startAutoRestoreFailed();
	String pauseAutoRestoreFailed();
	//homepage
	String d2dServers();
	String addServer();
	String addServerErrorMessage();
	String modifyServer();
	String serverName();
	String errorUserNamePattern();
	String lblBackupTemplate_text();
	String lblExcludeFileSystems_text();
	String nfsShare();
	String cifsShare();
	String sourceLocal();
	String targetLocal();
	String amazonS3();
	
	//archive job status
	String readyStatus();
	String scheduledStatus();
	String progressStatus();
	String finishedStatus();
	String incompleteStatus();
	String cancelledStatus();
	String notApplicable();
	String jobMonitorStatusCrashed();
	
	//tool bar
	String toolBar_D2DServer();
	String toolBar_add();
	String toolBar_delete();
	String toolBar_modify();
	String toolBar_node();
	String toolBar_BackupStorage();
	String toolBar_wizard();
	String toolBar_job();
	String toolBar_backup();
	String toolBar_menu_backup();
	String toolBar_backupForSelectedNode();
	String toolBar_addSelectedNode2ExistJob();
	String toolBar_restore();
	String toolBar_standby();
	String toolBar_standby_physical();
	String toolBar_standby_virtual();
	String toolBar_runNow();
	String toolBar_cancel();
	String toolBar_tool();
	String toolBar_refresh();
	String toolBar_schedule();
	String toolBar_hold();
	String toolBar_ready();
	String deleteConfirmMessage();
	String toolBarAddNodeByHostname();
	String toolBarAddNodeByDiscovery();
	
	//homepageTab
	String tabItemNode();
	String nodeProtected();
	String operatingSystem();
	String userName();
	String password();
	String captureServer();
	String jobID();
	String jobName();
	String jobType();
	String nodeName();
	String jobStatus();
	String result();
	String jobPhase();
	String progress();
	String startTime();
	String elapsedTime();
	String finishTime();
	String processedData();
	String dataSize();
	String throughput();
	String jobHistory();
	String activityLog();
	String status();
	String executionTime();
	String lastResult();
	String destination();
	String jobPhase_connectTarget();
	String jobPhase_connectNode();
	String jobPhase_restoreFiles();
	String jobPhase_createDisk();
	String jobPhase_wrapup();
	String jobPhase_start();
	String jobPhase_end();
	String jobPhase_installBuild();
	String jobPhase_uninstallBuild();
	String jobPhase_cancelJob();
	String jobPhase_WaitingMigration();
	String jobPhase_MigrationData();
	String jobPhase_WaitingForReboot();
	String jobPhase_BootSystem();
	String jobPhase_ReadyForUse();
	String jobPhase_Running();
	String jobStatus_ready();
	String jobStatus_idle();
	String jobStatus_finished();
	String jobStatus_cancelled();
	String jobStatus_failed();
	String jobStatus_incomplete();
	String jobStatus_active();
	String jobStatus_waiting();
	String jobStatus_crashed();
	String jobStatus_needReboot();
	String jobStatus_failed_no_license();
	String jobStatus_done();
	String jobStatus_waiting_in_jobqueue();

	String jobStatus_inactive();
	String jobPhase_monitoring();
	//restore
	String restoreWizard();
	String restoreWizardForBMR();
	String restoreWizardForMigration();
	String restoreWizardForFile();
	String restoreWizardForVM();
	String backupServer();
	String recoveryPoints();
	String recoveryPoint();
	String targetMachine();
	String advanced();
	String summary();
	String notification();
	String selectBackupServer();
	String selectBackupServerNotification();
	String backupServerLabel();
	String next();
	String help();
	String connect();
	String previous();
	String configuration();
	String submit();
	String selectRecoveryPoint();
	String selectSameRecoveryPoint();
	String backupLocationLabel();
	String backupLocationMessage();
	String backupLocationMultiplePathNotSupported();
	String validateSessionLocationMessage();
	String connectSessionLocationMessage();
	String machine();
	String machineMessage();
	String time();
	String type();
	String size();
	String selectRecoveryPointMessage();
	String encryptionPasswordMessage();
	String volumeName();
	String volumeSize();
	String selectTargetMachine();
	String selectTargetMachineForFileRestore();
	String macAddress();
	String ip();
	String macOrIPAddress();
	String macOrIPAddressToolTip();
	String macOrIPAddressEmptyMessage();
	String macOrIPAddressDuplicateMessage();
	String macOrIPAddressMessage();
	String dhcp();
	String staticIP();
	String staticIPSettings();
	String hostName();
	String hostNameOrIP();
	String ipAddress();
	String network();
	String targetMachineSettings();
	String subnetMask();
	String defaultGateway();
	String ipAddressMessage();
	String subnetMaskMessage();
	String defaultGatewayMessage();
	String preventRestoreServerSelf();
	String configurationButtonMessage();
	String selectTargetMachineMessage();
	String restoreBrowseRecoveryPointsToolTipNoEncrytion();
	String restoreBrowseRecoveryPointsToolTipEncrytion();
	String restoreBrowseRecoveryPointsToolTipRecoverySetStart();
	String restoreBrowseRecoveryPointsToolTipRecoverySetDaily();
	String restoreBrowseRecoveryPointsToolTipRecoverySetWeekly();
	String restoreBrowseRecoveryPointsToolTipRecoverySetMonthly();
	String restoreBrowseRecoveryPointsToolTipARTestSucceed();
	String restoreBrowseRecoveryPointsToolTipARTestFailed();
	String restoreBrowseRecoveryPointsToolTipARTestNotRun();
	String targetServerSettings();
	String targetServerType();
	String targetServerType_EsxServer();
	String targetServerType_VMWARE();
	String targetServerType_Xen();
	String targetServerName();
	String targetServerUsername();
	String targetServerPassword();
	String targetServerPort();
	String targetServerProtocol();
	String targetServerProtocol_Http();
	String targetServerProtocol_Https();
	String targetServerConnect();
	String targetVirtualMachineSettings();
	String targetVirtualMachineName();
	String targetVirtualMachineDataStore();
	String targetVirtualMachineMemoryUnit_GB();
	String targetVirtualMachineMemoryUnit_MB();
	String targetVirtualMachineMemory();
	String targetVirtualMachineOSSettings();
	String restoreType_BMR();
	String restoreType_Restore_File();
	String restoreType_Recovery_VM();
	String recoveryVM_validate_server();
	String recoveryVM_fail_to_connect_server();
	String recoveryVM_vCenter_or_ESX();
	String vaildateBackupLocationFailed();
	String wakeupOnLanSettings();
	String wakeupOnLanDescription();
	String enableWakeupOnLan();
	String debugOptionSettings();
	String credentialSettings();
	String credentialSDescription();
	String passwordNotSame();
	String sessionLocationSettings();
	String sessionLocationDescription();
	String device();
	String excludeDiskSettings();
	String excludeDiskDescription();
	String disk();
	String resetForUser();
	String excludeTargetDisk();
	
	//config
	String webServer();
	String backupTemplate();
	String backupTemplates();
	String name();
	String description();
	String sessionLocation();
	String loadingBackupTemplateText();
	String backupTemplateSettings();
	String general();
	String templateName();
	String location();
	String compression();
	String settingsCompressionNone();
	String settingsCompreesionStandard();
	String settingsCompressionMax();
	String settingsLabelCompressionNoneTooltip();
	String settingsLabelCompressionStandardTooltip();
	String settingsLabelCompressionMaxTooltip();
	String noEncryption();
	String encryption();
	String encryptionPassword();
	String retypeEncryptionPassword();
	String scheduledExportToolTipPassword();
	String scheduledExportToolTipRePassword();
	String save();
	String validating();
	
	//backup wizard
	String backupWizard();
	String backupSource();
	String backupDestination();
	String schedule();
	String scriptRunBeforeSnapshot();
	String scriptRunAfterSnapshot();
	String loginUsernameExample();
	String recoverySetSettings();
	String selectFirDayOfWeek();
	String selectSecDayOfWeek();
	String selectThiDayOfWeek();
	String selectFouDayOfWeek();
	String selectFifDayOfWeek();
	String selectSixDayOfWeek();
	String selectSevDayOfWeek();
	String selectLastDayOfMonth();
	String recoverySetNumNote();
	String settingBackupSetNumCon();
	String startBackupsetTooltip();
	String settingBackupDate();
	String selectDayofWeekTooltip();
	String backupsetNumberTooltip();
	String settingBackupDateofMonth();
	String selectDayofMonthTooltip();
	String settingsBackupSetCountErrorTooLow();
	String summaryBackupSetNumber();
	String whenToBackup();
	String now();
	String runOnce();
	String runManually();
	String repeat();
	String daily();
	String weekly();
	String runNowDescription();
	String runManuallyDescription();
	String addBackupSchedule();
	String modifyBackupSchedule();
	String repeatEvery();
	String applyTo();
	String applyToOneDay();
	String allDays();
	String workingDays();
	String weekEndDays();
	String selectSchedule();
	String clear();
	String runOnceDescription();
	String showMoreSettings();
	String hideMoreSettings();
	String backupLocationLocalDescription();
	String endTimeBeforeStartTime();
	
	//Backup Server
	String backupServerHint();
	String d2dServer();
	String backupServerNote();
	String backupServerInputValidServer();
	
	//Bakcup Source
	String delimiter();
	String backupSourceHeader();
	String backupSourceHint();
	String hostNameIP();
	String excludeInfo();
	String includeInfo();
	String volumeFilterInfo();
	String exclude();
	String include();
	String includeVolumes();
	String excludeVolumes();
	String excludeInfoToolTip();
	String excludeFilesInfo();
	String excludeFilesInfoToolTip();
	String addNode();
	String clickToGetMoreInfo();
	String backupSourceNotEmpty();

	//Backup destination
	String backupDestHeader();
	String backupDestHint();
	String compressInfo();
	
	//Activity log
	String message();
	String information();
	String warning();
	String error();
	String none();
	
	//Modify node
	String changeAccount();
	String nodeList();
	String backupDefaultJobName();

	
	//add node
	String close();
	String addMore();
	String addClose();
	String validate();
	String scheduleLabelRepeat();
	String scheduleLabelNever();
	String scheduleLabelEvery();
	String scheduleLabelDays();
	String scheduleLabelHours();
	String scheduleLabelMinutes();
	String backupSettingsSchedule();
	String scheduleStartDateTime();
	String scheduleLabelIncrementalBackup();
	String scheduleLabelFullBackup();
	String scheduleLabelResyncBackup();
	String scheduleLabelScheduleDescription();
	String scheduleStartDate();
	String scheduleStartTime();
	String scheduleStartTimeTooltip1();
	String scheduleStartTimeTooltip2();
	String scheduleStartTimeTooltip3();
	String scheduleStartTimeAM();
	String scheduleStartTimePM();
	String getRestoreJobScriptFailed();
	String nodeDiscoveryScript();
	String nodeDiscoverySchedule();
	String nodeDiscoveryScriptNotEmpty();
	String nodeDiscoveryNoScript();
	String nodeDiscoveryOnce();
	String nodeDiscoveryNoScriptWarning();
	String nodeDiscoveryTitle();
	
	//volume info
	String fileSystem();
	String validateAll();
	String validateSelected();
	String volumeFilter();
	String excludeVolumesToolTipInfo();
	String OK();
	
	String protocol();
	String port();
	String validateServerMessage();
	String validatePortMessage();
	String d2dServerDuplicateMessage();
	
	String restoreIntroductionLabel();
	String bmr();
	String volume();
	String file();
	String search();
	String bmrMessage();
	String volumeMessage();
	String fileMessage();
	String searchMessage();
	String browsePagingDesc();
	String restoreDateModifiedColumn();
	String restoreMustSelectFiles();
	String migrationBmr();
	String migrationBmrSource();
	String migrationBmrSourceDescription();
	String instantVMRecoveryJob();
	
	String userNameMessage();
	String passwordMessage();
	String destinationMessage();
	String restoreTypeLabel();
	String nodeNameMessage();
	String submitRestoreJobFailMessage();
	String startTimeOutdatedMessage();
	
	String browse();
	String browseSelectFolder();
	String browseSelectFile();
	String browseFolderName();
	String browseFileName();
	String browseFailed();
	String browseFileOrFolderName();
	
	String browseEmptyFolder();
	String browseWindowUpTooltip();
	String browseWindowNewTooltip();
	String browseFailedToCreateFolder();
	String restoreLoading();
	String selectDestinationPath();
	
	String restoreResolvingConflicts();
	String restoreConflictOverwrite();
	String restoreConflictOverwriteTooltip();
	String restoreConflictRename();
	String restoreConflictRenameTooltip();
	String restoreConflictSkip();
	String restoreConflictSkipTooltip();
	String runNow();
	String reset();
	String resetFileSelection();
	String fileSelectionLostWarning();
	String selectionLostWarning();
	String setSpecialTime();
	String serverLocal();
	
	//run now window
	String runNowWindowHeading();
	String runNowWindowCompressionChanged();
	String runNowWindowSubmitSuccessful();
	String runNowWindowSubmitFailed();

	String restoreToOriginalLocation();
	String restoreToOriginalLocationTooltip();
	String restoreTo();
	String restoreToTooltip();
	String fileName();
	String diskName();
	String diskSize();
	String excludeAllVolumes();
	String includeAnyVolumes();
	String backupSettingsErrorDaysTooLarge();
	String backupSettingsErrorHoursTooLarge();
	String backupSettingsErrorMinutesTooLarge();
	String backupSettingsErrorScheduleTooSmall();
	String cannotConnectLocation();
	String noMachine();
	String noRecoveryPoint();
	String fileList();
	String failToCancelJob();
	String rootVolumeNotBackedUp();
	String bootVolumeNotBackedUp();
	String restoreTargetMachines();
	String restoreTargetMachine();
	String restoreCurrentLocation();
	String restoreFind();
	String restoreBrowse();
	String restoreAdd();
	String restoreRemove();
	String restoreFileFolderBeRestore();
	String restoreCancel();
	String restoreAction();
	String restoreSelectAll();
	String restoreDeSelectAll();
	String restoreSearching();
	String restorePrePostSettings();
	String restoreDirectoryStructure();
	String restoreDirectoryStructureDescription();
	String restoreConflictBaseFolderWillBeCreated();
	String restoreConflictBaseFolderWillNotBeCreatedTooltip();
	String scriptRunOnD2DServer();
	String scriptRunOnTargetMachine();
	String scriptRunBeforeJob();
	String scriptRunAfterJob();
	String scriptRunAfterReadyForUseJob();
	String restoreEstimateSetting();
	String restoreEstimateFileSize();
	String restoreEstimateDescription();
	String summaryServerScriptBeforeJob();
	String summaryServerScriptAfterJob();
	String summaryTargetScriptBeforeJob();
	String summaryTargetScriptAfterJob();
	String summaryTargetScriptReadyForUseJob();
	String summaryTargetScriptBeforeSnapshot();
	String summaryTargetScriptAfterSnapshot();
	String invalidPassword();
	String restoreNameFileNameTooltip();

	String whereToSearch();
	String whatToSearch();
	String backupMachineLocation();
	String refreshBackupMachine();
	String allMachines();
	String fileFolderName();
	String searchPath();
	String find();
	String includeSubDir();
	String restoreGridName();
	String restoreGridSessionid();
	String restoreGridSize();
	String restoreGridDate();
	String selectVersion();
	String index();
	String restoreBySearchValidateError();
	String invalidCharacter();
	String all();
	String errorsAndWarnings();
	String endTime();
	String invalidJobID();
	String toolBar_filter();
	String between();
	String and();
	String wildcardToolTip();

	//dashboard
	String dashboard();
	String serverInformation();
	String osVersion();
	String upTime();
	String runningJobs();
	String installed();
	String notInstalled();
	String restoreUtility();
	String license();
	String resourceUsage();
	String cpuUsage();
	String physicalMemory();
	String swapSize();
	String installationVolume();
	String addBackupStorage();
	String modifyBackupStorage();
	String backupStorage();
	String backupStorageFreeSpaceLessThan();
	String concurrentBackupJob();
	String noLimit();
	String limitTo();
	String MB();
	String runScript();
	String path();
	String adding();
	String modifying();
	String totalSize();
	String freeSize();
	String nodeSummary();
	String jobSummary();
	String totalJobs();
	String successJobs();
	String failureJobs();
	String incompleteJobs();
	String otherJobs();
	String homepageStatusPieChartAdobeFlashNotInstalled();
	String homepageStatusPieChartAdobeFlashHere();
	String homepageChartNoDataDisplay();
	String viewFor();
	String viewAll();
	String viewThisWeek();
	String viewThisMonth();
	
	String managedByOtherServerConfirm();
	String deleteManagedByOtherServerConfirm();
	String deleteCannotConnectConfirm();
	String mofidyManagedByOtherServerConfirm();
	String connecting();
	String d2dServerNotReachable();
	String d2dServerManagedByOther();
	String d2dServerNeedUpgrade();
	
	String selectOneBackupStorage();
	
	String connectFailedNoModules();
	String connectWarnningNoModules();
	String connectFailedNotRootUser();
	String connectFailedNoNfs();
	String connectFailedNoPerl();
	String connectFailedNoParted();
	String connectFailedNoCifs();
	String connectFailedWrongUserAccount();
	String connectFailedSSHKey();
	String connectFailedNotSupportedPlatform();
	String connectFailedNotSupportedArch();
	String connectFailedNotSupportedLinuxPlatform();
	String connectFailedNotSupportedOSVersion();
	String connectFailedUndefinedReason();
	String deleting();
	String deleteNode();
	String forceDeleteNode();
	String forceDeleteNodeToolTip();
	String modifyRunningBackupJobConfirmMessage();
	String multipleNodeModidyWarning();
	
	String serverCapability();
	String noRestoreUtilWarnningBMR();
	String noRestoreUtilRunningWarnningBMR();
	String noRestoreUtilErrorFile();
	String addSelectedNode2ExistJob();
	String tooltipForCifs();
	
	String endDateBeforeStartDate();
	String filter();
	String to();
	String start();
	String end();
	String throughputTitle();
	String throughputDescription();
	String throughputUnit();
	String throughputNoLimit();
	String readThroughput();
	String writeThroughput();
	String databaseBusy();

	String manageLicense();
	String releaseLicense();
	String release();
	String licenseMachines();
	String licenseMachine();
	String licenseVM();
	String licenseSocket();
	String item();
	String items();
	String columnNameComponent();
	String columnNameVersion();
	String columnNameActive();
	String columnNameAvailable();
	String columnNameTotal();
	String columnNameNeeded();
	String licenseHeader();
	String licenseStatus();
	String licenseWindowHeader();
	String licenseKey();
	String licenseKeyFormat();
	
	String licensed();
	String licenseFailure();
	String licenseExpired();
	String licenseWillExpire();
	String licenseWGCount();
	String licenseTrial();
	
	String recoverySetCount();
	String recoveryPointCount();
	String recoveryPointSize();
	String recoveryPointStartDate();
	String recoveryPointEndDate();
	
	String about();
	String aboutWindowLicense();
	String aboutWindowCopyRight();
	String aboutWindowNotLicense();
	String aboutWindowCopyRight2();
	String copyDestValidateSucc();
	String releaseNotes();
	String aboutWindowWarning();
	String licenseAgreement();
	String onlineSupport();
	String endUserLicenseAgreement();
	String licensePrint();
	String licenseImportant();
	String licenseImportantContent();
	
	String cannotSelect();
	String cannotDeselect();
	String backupStorageAlertNoScriptWarning();
	String results();
	String searchJobStatus();
	String searchJobHistory();
	String searchActivityLog();
	String byNodeName();
	String byJobName();
	String byJobId();
	String byBackupDestination();
	String freeSizeFormatWrong();
	String freeSizeEmpty();
	String freeSizePercentFormatWrong();
	String jobNameContainSpace();
	String closeBackupWizardConfirm();
	String closeRestoreWizardConfirm();
	String runFor();
	String cancelJobSubmitSuccessfully();
	String cancelJob();
	String deleteJob();
	String cancelFor();
	String deleteFor();
	String deleteJobSubmitSuccessfully();
	String deleteJobSubmitSuccessfullyWithWarning();
	String clickHereToDownloadLiveCD();
	String backupJob();
	String selectedNode();
	String nodesProtectedBySelectedJob();
	String cancelJobDescription();
	String deleteJobDescription();
	String cancelJobConfirm();
	
	String rootUserNotes();
	String enableRootUser();
	String userSettings();
	String invalidRootPasswd();
	String noLiveCDWarning();
	
	String license_CapacityEdition();
	String notDeleteRunningNodes();
	String backupType();
	String CASupport();
	String userCommunityDiscussions();
	String expertAdviceCenter();
	String social();
	String RssLink();
	String simple();
	String scheduleType();
	String custom();
	String video();
	String liveChat();
	String supportAndCommunity();
	String userGuide();
	String knowledgeCenter();
    String glicNotInstall();
    
    String modifyBackupJob();
    String modifyFileRestoreJob();
    String modifyBMRJob();
    String modifyMigrationJob();
    String modifyVMRecoveryJobNotSupported();
    String selectRestoreType();
    String aboutHelp();
    String solutionsGuide();
    String agentUserGuide();
    String askSupport();
    String sendFeedback();
    
    String standbyPhysical();
    String standbyVirtual();
    String standbySourceNodes();
    String standbyStandbyMachine();
    String closeStandbyWizardConfirm();
    String sourceNodesHeader();
    String sourceNodesHint();
    String automaticallyStartMachine();
    String howToStartMachine();
    String wakeOnLan();
    String specifyAtLeastOneNode();
    String lastNodeWarning();
    String physicalStandbyHeader();
    String macAddressExist();
    String invalidMacAddress();
    String noPhysicalMachine();
    String heartBeatSettings();
    String heartBeatDescription();
    String heartBeatFrequency();
    String heartBeatTimesToolTip();
    String heartBeatTimes();
    String heartBeatSeconds();
    String heartBeatFrequencySummary();
    String heartBeatTimesSummary();
    String others();
    String standby();
    
    String priority();
    String backupNowJobWaitingInJobQueue();
    String runningJobCount();
    String waitingJobCount();
    
    String priority_high();
    String priority_medium();
    String priority_low();
    String rpsHostName();
    String rpsServe();
    String rpsPointInformation();
    String noDataStore();
    String validateFormFailed();
    String dataStore();
    String startMigrationSuccessfully();
    String startMigrationFailed();
    String migrationDataConfirm();
    String load();
    String addRpsNotice();
    String hbbuRpsMessage();
    String validateHbbuVersion();
    String oldSessionNotSupportInstantBMR();
    String rps();
    
    String serverType();
    String localServer();
    String remoteServer();
    String sourceRestoreJobNotExist();
    String sourceIPMigrationDescription();
    
    String startInstantVMJob();
    String stopInstantVMJob();
    String connectWarnningNoCifsAndNfs();
    
    String planName();
    String noticeRegisterMessage();
    String company();
    String fullfillNumber();
    String phoneNumber();
    String emailAddress();
    String registrationWindowHeader();
    String sendVerificationEmail();
    String registrationFailed();
    String headerLabel();
    String msgLabel();
    String warningLabel();
   
    String d2dRegistration();
    String sessionPassword();
    String entitlementExist();
    String registrationSuccessLinkFailed();
    String registrationFailedException();
    String registrationFailedInValidateDetails();
    String registrationFailedUrlError();
    String registrationFailedInvalidEmail();
    String registrationFailedDocumentNumber();
    String cancelParticipathion();
    String getCancelRegSuccess();
    String getCancelRegFailure();
    String getFulFillmentToolTip();
    String getRegistrationHeader();
    String getParticipateRegLabel();
    String usageStatistics();
    String cancelRegist();
    String registerSucc();
    String regActiveMessage();
    String regIntroduction();
    String getEUModuleClauseLabel();
    String powerOff();
    String managedBy();
    String sourceSetting();
    String migrationWarning();
    
    String shareHeader();
    String shareHours();
    String shareRecoveryPoint();
    String lengthOftime();
    String application();
    String restoreWizardForShareRecoveryPoint();
    String credentialSetting();
    String advancedSetting();
    String blackMessage();
    String confirmPassword();
    String notMatchPassword();
    String settings();
    String selectLoctionForShareRecoveryPoint();
    String userNameExist();
    String prepareRecoveryPoint();
    String sharingRecoveryPoint();
    String tryToConnetTarget();
    String assureRecovery();
    String modifyAssureRecoveryJobNotSupported();
    String shareInLocal();
    String shareInInternet();
    String canNotContainSC();
    String browseLabel();
    String nfsShareOptionToolTip();
    String nfsShareOptionLabel();
    String zeroisforever();
    
    String serverInCloudNeedSsh();
    String sshKeyFile();
    String passphrase();
    String failedToUploadFile();
    String invalidKeyFile();
   
    String s3CifsDescription();
    String s3CifsNote();
    String s3CifsNote2();
    String accessId();
    String accessKey();
    String enableS3CifsShare();
    String s3Tooltip();
    String s3Settings();
    
    String lastSunday();
    String lastMonday();
    String lastTuesday();
    String lastWednesday();
    String lastThursday();
    String lastFriday();
    String lastSaturday();
}
