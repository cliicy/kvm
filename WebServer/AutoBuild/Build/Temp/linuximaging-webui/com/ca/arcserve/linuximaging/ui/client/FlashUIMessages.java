package com.ca.arcserve.linuximaging.ui.client;


import com.google.gwt.i18n.client.Messages;

public interface FlashUIMessages extends Messages {
	String bytes(String num);
	String KB(String num);
	String MB(String num);
	String GB(String num);
	String percentage(String num);
	String jobMonitorThroughout(String throughput);
	String jobMonitorThroughoutKPerSec(String throughput);
	String jobMonitorThroughoutGPerMin(String throughput);
	String wizardBackupSourceAlreadyExisted(String machineList);
	String wizardBackupSourceDuplicated(String machine);
	String nodeProtected(String jobName);
	String networkToolTip(String ip, String subnet, String gateway);
	String noSelectedNodes();
	String addBackupSourceManually();
	String nodeAlreadyBeenProtected(String node, String jobname);
	String nodeWithRunningJobMessage(String nodes);
	String settingsBackupSetCountExceedMax(long number);
	
	String jobPhase_backupVolume(String volume);
	String jobPhase_restoreVolume(String volume);

	String connectFailedWrongNetwork(String node);
	String connectFailedExecuteCheckMachine(String node);
	String connectFailedExecutePwdExpired(String node);
	String validateSuccessfully(String node);
	String addingNode(String node);
	String addNodeSuccessfully(String node);
	String addNodeFailed(String node);
	String nodeIsProtectedOrNot(String node);
	String nodeNameNotEmpty();
	String passwordNotMatch();
	String passworkNotEmpty();
	String backupDestinationNotEmpty();
	
	String getBackupJobScriptFailed();
	String scheduleLabelRepeatTooltip(String title);
	String scheduleLabelNeverTooltip(String title);
	String scheduleLabelIncrementalDescription();
	String scheduleLabelFullDescription();
	String scheduleLabelResyncDescription();
	
	String wholeMachineRestoreWarning(String volumes);
	String includeWholeMachineRestoreWarning(String volumes);
	String noExcludeVolumesForNode();
	String excludeVolumesForNode(String volumes);
	String notSupportedFSType(String mountP, String fsType);

	String browse(String folderName);
	String browseWindowCreateAFolderUnder(String parentDir);
	String backupSettingsNodifications(int i);
	String restoreResolvingConflictsDescription(String productName);
	
	String jobNameEmpty();
	String exceedMaxJobNameLength(Integer maxLen);
	String encPasswordMaxLength(Integer maxLen);
	String backupSettingsErrorMinutesTooSmallForNoLic(int i);
	String repeatEvery(Integer interval, String displayIntervalUnit);
	String deleteJobFailed(String jobName);
	String deleteNodeFailed(String nodeName);
	String removeDriverFailed(String node, String reason, String productName);
	String connectD2DServerErrorMessage(String server, String productName);
	String excludeVolumeSetting(String node);
	
	String restoreSearchResultExceed(int maxCount);
	String browseSubFolderExceed(int maxCount);
	String connectToNetworkPath(String path);
	
	String invalidTimeRange(String start, String end);
	
	//dashboard
	String dayAndTime(long days, String hours, String minutes);
	String totalNodes(int num);
	String protectedNodes(int num);
	String lastBackupFailureNodes(int num);
	String homepagePieChartInstallFlash(String posi);
	
	String serverIsManagedByOther(String serverName, String productName);
	String rpsIsManagedByOther(String serverName);
	String nodeIsManagedByOther(String oldServer, String newServer);
	String nodeIsManagedByOtherInfo(String oldServer);
	String nodeManageFailedByRunningJob(String newServer);
	
	String failToAddNode(String jobName);
	String noRestoreUtilError(String server);
	
	String invalidCifsPathMessage(String format);
	String runJobForNode(String nodeName);
	String runJobForNodeProtectedBy(String jobName);
	String recoveryPointCountExceedLimit(int num);
	String pagingToolBar_displayMsg(int start, int end);
	String buildVerion(String version, String build);
	String buildVerionForLogin(String version,String build);
	
	String validateHostName();
    String validateUserName();
    String validatePassWord();
    String validatePort();
    String validateDataStore();
    String validatePortNumber();
    String goToUdpConsole(String udpName);
    String connectWarnningNoCifsOrNfs(String clientType);
    String messages(int num);
    String validateName();
    String validateCompany();
    String validateFullfillNumber();
    String validatePhoneNumber();
    String validateEmailAddress();
    String isActivateResult();
    String isInactivateResult(String Email);
    String userNameNotEmpty();
    String browseWarning(String URL);
    
    String PrivacyAndEUmodel(String privacy,String eumodel);
    String getRegisterPolicyLabel(String privacy);
    String getRegAgreementLabel(String eumodel);
    String getEUModuleClauseLabel(String eumodel);
    String getInActiveMessage(String email);
    
    String tooltipForS3(String region);

}
