package com.ca.arcserve.linuximaging.ui.client;


import com.ca.arcserve.linuximaging.ui.client.common.icon.FlashImageBundle;
import com.ca.arcserve.linuximaging.ui.client.common.icon.FlashImageHandle;
import com.ca.arcserve.linuximaging.ui.client.model.VersionInfoModel;
import com.google.gwt.core.client.GWT;

public class UIContext {
	public static FlashUIConstants Constants = GWT.create(FlashUIConstants.class);
	public static FlashUIMessages Messages = GWT.create(FlashUIMessages.class);
	public static final FlashImageBundle IconBundle = GWT.create(FlashImageBundle.class);
	public static final FlashImageHandle IconHundle = GWT.create(FlashImageHandle.class);
	
	public static String productName = UIContext.Constants.productName();
	public static String companyName = "CA";
//	public static VersionInfoModel serverVersionInfo;
	public static int INTERVAL_ACTIVE_TIMEOUT =  60*60*1000;
	public static final long maxRPLimitDEFAULT = 1344;
	public static final long maxBSDEFAULT = 100;
	public static long maxRPLimit = maxRPLimitDEFAULT;	
	public static long maxBSLimit = maxBSDEFAULT;	
	public static long maxBackupsForArchiveJob = 700;
	public static long minBackupsForArchiveJob = 1;
	

	public static long minFileVersions = 1;
	public static long maxFileVersions = 100;

	public static long minThrottleInKB = 1;
	public static long maxThrottleInKB = 1706650;
	public static long minThrottleInMB = 1;
	public static long maxThrottleInMB = 99999;
	public static long minRetentionTime = 1;
	public static final int BUTTON_MINWIDTH = 100;
	
	//public static int timeZoneOffset = 28800000;
	
	public static String FILE_SEPATATOR="/";
	public static String helpLink="http://www.arcservedocs.com/arcserveudp/6.5/agentlinux/redirect.php?lang=en&item=";
	public static String version=UIContext.Constants.productNameWithHtml();
	
	public static VersionInfoModel versionInfo;
	public static VersionInfoModel selectedServerVersionInfo;
	public static boolean isRestoreMode = false;
	
	private UIContext() {
	}

	
}
