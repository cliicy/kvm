package com.ca.arcserve.linuximaging.ui.client.common;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.icon.FlashImageBundle;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatus;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.ca.arcserve.linuximaging.ui.client.model.NodeConnectionModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Image;

public class Utils {
	public static final String PSEUDO_PASSWORD = "\n";
	public static final String MAC_SEPARATOR = ":";
	public static final String IP_SEPARATOR = ".";
	public static final String IP_SEPARATOR_REGEX = "[.]";
	public static final String SMB_REGEX = "\\";
	public static final String SMB_REGEX_1 = "//";
	public static final String NFS_REGEX = ":/";
	public static final String S3_PREFIX = "s3:/";
	public static final String LOCAL_REGEX = "/";
	public static final String LOCATION = "location";
	public static final String LOCATION_RESTORE = "restore";
	public static final String HOST_NAME = "hostname";
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String PROTOCOL = "protocol";
	public static final String PORT = "port";
	public static final String NFS = "nfs";
	public static final String DATASTORE_NAME = "datastorename";
	public static final NumberFormat number = NumberFormat.getFormat("0.00");
	public static final NumberFormat simpleNumberFormat = NumberFormat
			.getFormat("00");
	public static final DateTimeFormat dateFormat = DateTimeFormat
			.getFormat(UIContext.Constants.timeDateFormat());
	public static final DateTimeFormat timeFormat = DateTimeFormat
			.getFormat(UIContext.Constants.timeFormat());
	public static final DateTimeFormat dateOnlyFormat = DateTimeFormat
			.getFormat(UIContext.Constants.dateFormat());
	@SuppressWarnings("deprecation")
	public static final Date maxDate = new Date(199, 1, 1);
	@SuppressWarnings("deprecation")
	public static final Date minDate = new Date(80, 1, 1);
	// This is for convert server string to local date
	public static final DateTimeFormat serverDate = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm:ss");
	public static final int EncryptionPwdLen = 23;
	public static final int SCRIPT_TYPE_COMMON = 1;
	public static final int SCRIPT_TYPE_PREPOST = 2;
	public static final int SCRIPT_TYPE_DISCOVERY = 3;
	public static final int SCRIPT_TYPE_ALERT = 4;
	public static final int SCRIPT_TYPE_SHARE = 5;
	private static final String busyImagePath = "images/gxt/icons/grid-loading.gif";
	public static final int NUM_MILLISECONDS_IN_DAY = 24 * 60 * 60000;

	// archive jobs status
	public final static int ScheduleNotApplicable = 0;// no need to run archive
														// for this backup
														// session.
	public final static int ArchiveJobReady = 1; // archive needs run for this
													// backup session, and this
													// is is not submited to
													// scheduler.
	public final static int ScheduleScheduled = 2; // archive needs run for this
													// backup session, and this
													// is submited to scheduler
	public final static int ScheduleRunning = 3; // archive needs run for this
													// backup session, and this
													// job is currently running.
	public final static int ArchiveJobFinished = 4; // archive needs run for
													// this backup session, and
													// this job is Finished.
	public final static int ArchiveScheduleAll = 5; // archive needs run for
													// this backup session, and
													// this job is Finished.
	public final static int ArchiveJobCancelled = 6;
	public final static int ArchiveJobFailed = 7;
	public final static int ArchiveJobIncomplete = 8;
	public final static int ArchiveJobCrashed = 9;

	public final static int LEAST_RECOMMENDED_HEIGHT = 768;
	public final static int LEAST_RECOMMENDED_WIDTH = 1024;

	public static Map<String, String[]> connectionCache = null;

	public static Image createLastResultImage(JobStatus status) {
		Image image = null;
		if (status == JobStatus.FINISHED)
			image = UIContext.IconHundle.last_result_success().createImage();
		else if (status == JobStatus.FAILED
				|| status == JobStatus.FAILED_NO_LICENSE)
			image = UIContext.IconHundle.last_result_failed().createImage();
		else if (status == JobStatus.CRASHED)
			image = UIContext.IconHundle.last_result_error().createImage();
		else if (status == JobStatus.CANCELLED
				|| status == JobStatus.INCOMPLETE)
			image = UIContext.IconHundle.last_result_cancel_and_incomplete()
					.createImage();
		else
			image = UIContext.IconHundle.job_icon().createImage();

		return image;
	}

	public static Image createJobLastResultImage(JobStatus status) {
		Image image = null;
		if (status == JobStatus.FINISHED) {
			image = UIContext.IconHundle.job_last_result_success()
					.createImage();
			image.setStyleName("job-icon");
		} else if (status == JobStatus.FAILED
				|| status == JobStatus.FAILED_NO_LICENSE) {
			image = UIContext.IconHundle.job_last_result_failed().createImage();
			image.setStyleName("job-icon");
		} else if (status == JobStatus.CRASHED) {
			image = UIContext.IconHundle.job_last_result_failed().createImage();
			image.setStyleName("job-icon");
		} else if (status == JobStatus.CANCELLED
				|| status == JobStatus.INCOMPLETE) {
			image = UIContext.IconHundle
					.job_last_result_cancel_and_incomplete().createImage();
			image.setStyleName("job-icon");
		} else {
			image = UIContext.IconHundle.job().createImage();
		}

		return image;
	}

	public static Image createLastResultImageForNode(JobStatus status) {
		Image image = null;
		if (status == JobStatus.FINISHED)
			image = UIContext.IconHundle.last_result_success().createImage();
		else if (status == JobStatus.FAILED
				|| status == JobStatus.FAILED_NO_LICENSE)
			image = UIContext.IconHundle.last_result_failed().createImage();
		else if (status == JobStatus.CRASHED)
			image = UIContext.IconHundle.last_result_error().createImage();
		else if (status == JobStatus.CANCELLED
				|| status == JobStatus.INCOMPLETE)
			image = UIContext.IconHundle.last_result_cancel_and_incomplete()
					.createImage();

		return image;
	}

	public static LayoutContainer createIconLabelField(Image image,
			String tooltip, String value) {
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		container.setLayout(layout);
		container.setHeight(22);
		if (tooltip != null) {
			addToolTip(container, tooltip);
		}
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		container.add(image, td);
		LabelField lbl = createLabelField(value);
		lbl.setStyleAttribute("padding-left", "5px");
		container.add(lbl);
		return container;
	}

	public static LabelField createLabelField(String value) {
		LabelField lbl = new LabelField();
		lbl.setText(new Html(
				"<pre style=\"font-family: Tahoma,Arial;font-size: 11px;\">"
						+ value + "</pre>").getHtml());
		return lbl;
	}

	public static String getMachineAddress(String url) {
		String result = null;
		if (url.startsWith(SMB_REGEX)) {
			result = url.split("\\\\\\\\")[1].split("\\\\")[0];
		} else if (url.startsWith(SMB_REGEX_1)) {
			result = url.split(SMB_REGEX_1)[1].split("/")[0];
		} else if (url.contains(NFS_REGEX)) {
			result = url.split(NFS_REGEX)[0];
		} else {
			result = "127.0.0.1";
		}
		return result;
	}

	public static boolean validateLocalAddress(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}
		if (!value.startsWith(LOCAL_REGEX)) {
			return false;
		} else if (value.startsWith(SMB_REGEX) || value.startsWith(SMB_REGEX_1)) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean validateNFSAddress(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}
		if (value.startsWith(LOCAL_REGEX) || value.startsWith(SMB_REGEX)
				|| value.startsWith(SMB_REGEX_1)) {
			return false;
		} else if (value.contains(NFS_REGEX)) {
			String machine = value.split(NFS_REGEX)[0];
			if (validateLinuxHostName(machine)) {
				return true;
			} else if (validateIPAddress(machine)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean validateMultiplePathForNFS(String value) {
		return !value.matches(".+:/.+/.+");
	}

	public static boolean validateMultiplePathForCifs(String value) {
		return !value.matches("//.+/.+/.+");
	}

	public static boolean isCIFSAddress(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}
		if(value.startsWith(S3_PREFIX)){
			return true;
		}
		if (!(value.startsWith(SMB_REGEX_1))) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isRpsAddress(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}
		if (!(value.contains(":"))) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean validateIPAddress(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}

		String[] temp = value.split(IP_SEPARATOR_REGEX);
		if (temp.length != 4) {
			return false;
		}
		for (String str : temp) {
			int num = -1;
			try {
				if (str.contains(":")) {
					str = str.split(":")[0];
				}
				num = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				return false;
			}
			if (num < 0 || num > 255) {
				return false;
			}
		}

		return true;
	}

	public static boolean validateWindowsHostName(String value) {
		String regex = "^[A-Za-z][A-Za-z0-9.-]{0,13}[A-Za-z0-9]$";
		return value.matches(regex);
	}

	public static boolean validateLinuxHostName(String value) {
		String regex = "^[A-Za-z][A-Za-z0-9.:-]{0,64}[A-Za-z0-9]$";
		return value.matches(regex);
	}

	public static boolean validateMacAddress(String value) {
		String regex = "^[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}$";
		return value.matches(regex);
	}

	public static void setButtonId(MessageBox messageBox, String buttonId,
			String id) {
		Button button = messageBox.getDialog().getButtonById(buttonId);
		if (button != null) {
			button.ensureDebugId(id);
		}
	}

	public static void showURL(String url) {
		Window.open(url, "_blank", "");
	}

	public static MessageBox showMessage(String title, String iconStyle,
			String message) {
		MessageBox msgBox = new MessageBox();
		msgBox.setIcon(iconStyle);
		msgBox.setTitle(title);
		msgBox.setModal(true);
		msgBox.setMinWidth(400);
		msgBox.setMessage(message);
		msgBox.show();
		return msgBox;
	}

	public static MessageBox showMessage(String title, String iconStyle,
			String message, SelectionListener<ButtonEvent> okHandler,
			SelectionListener<ButtonEvent> cancelHandler) {
		MessageBox msgBox = new MessageBox();
		msgBox.setIcon(iconStyle);
		msgBox.setTitle(title);
		msgBox.setModal(true);
		msgBox.setMinWidth(400);
		msgBox.setMessage(message);
		msgBox.setButtons(MessageBox.OKCANCEL);
		if (okHandler != null) {
			msgBox.getDialog().getButtonById(MessageBox.OK)
					.addSelectionListener(okHandler);
		}
		if (cancelHandler != null) {
			msgBox.getDialog().getButtonById(MessageBox.CANCEL)
					.addSelectionListener(cancelHandler);
		}
		msgBox.show();
		return msgBox;
	}

	public static Image getBusyImage() {
		return new Image(busyImagePath);
	}

	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}

	public static String formatTime(Date date) {
		return timeFormat.format(date);
	}

	public static String formatDateToServerTime(Date date) {
		if (date == null)
			return "";

		TimeZone serverTimeZone = TimeZone
				.createTimeZone(getServerTimeZoneOffset());
		return dateFormat.format(date, serverTimeZone);
	}

	public static String formatDateToServerTime(Date date,
			long serverTimezoneOffset) {
		if (date == null)
			return "";
		TimeZone serverTimeZone = TimeZone
				.createTimeZone((int) serverTimezoneOffset / (-1000 * 60));
		return dateFormat.format(date, serverTimeZone);
	}

	public static String formatTimeToServerTime(Date date) {
		if (date == null)
			return "";
		TimeZone serverTimeZone = TimeZone
				.createTimeZone(getServerTimeZoneOffset());
		return timeFormat.format(date, serverTimeZone);
	}

	public static int getServerTimeZoneOffset() {
		// return
		// Calendar.getInstance().getTimeZone().getOffset(System.currentTimeMillis())/(-1000*60);
		return UIContext.selectedServerVersionInfo.getTimeZoneOffset()
				/ (-1000 * 60);
	}

	/**
	 * Converts the local time to server time. Note: This method is only used to
	 * set a server time in the component DateField, which makes the DateField
	 * look like that it is showing the server time. You can not set a time got
	 * from server directly to DateField to make it show time server because
	 * date represent UTC time and is transformed to local time automatically.
	 * That is this method only makes the transformed time likes like the server
	 * time in textual representation "yyyy-MM-dd HH:mm:ss".
	 * 
	 * @param localDate
	 *            localDate which shows the same textual representation with the
	 *            server time.
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date localTimeToServerTime(Date localDate) {
		// long timeDiffLocalAndServer = localDate.getTimezoneOffset() * 60 *
		// 1000 + UIContext.serverVersionInfo.getTimeZoneOffset();
		long timeDiffLocalAndServer = localDate.getTimezoneOffset() * 60 * 1000
				+ getServerTimeZoneOffset();
		// make the date in client look like server time in text.
		Date serverDate = new Date(localDate.getTime() + timeDiffLocalAndServer);
		return serverDate;
	}

	@SuppressWarnings("deprecation")
	public static Date localTimeToServerTime(Date localDate,
			long serverTimeZoneOffset) {
		if (serverTimeZoneOffset == 0 || serverTimeZoneOffset == -1) {
			return localTimeToServerTime(localDate);
		}

		long timeDiffLocalAndServer = localDate.getTimezoneOffset() * 60 * 1000
				+ serverTimeZoneOffset;
		// make the date in client look like server time in text.
		Date serverDate = new Date(localDate.getTime() + timeDiffLocalAndServer);
		return serverDate;
	}

	/**
	 * Converts the server time to local time. This method can only be used in
	 * pair with <code>localTimeToServerTime</code>.
	 * 
	 * @see localTimeToServerTime
	 * 
	 * @param serverDate
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date serverTimeToLocalTime(Date serverDate) {
		DateWrapper wrapper = new DateWrapper(serverDate);
		// long timeDiffLocalAndServer = (- new
		// Date().getTimezoneOffset()*60*1000 -
		// UIContext.serverVersionInfo.getTimeZoneOffset() );
		long timeDiffLocalAndServer = (-new Date().getTimezoneOffset() * 60 * 1000 - getServerTimeZoneOffset());
		wrapper = wrapper.addMillis((int) timeDiffLocalAndServer);
		return wrapper.asDate();
	}

	@SuppressWarnings("deprecation")
	public static Date serverTimeToLocalTime(Date serverDate,
			long serverTimeZoneOffset) {
		if (serverTimeZoneOffset == 0 || serverTimeZoneOffset == -1) {
			return serverTimeToLocalTime(serverDate);
		}

		DateWrapper wrapper = new DateWrapper(serverDate);
		long timeDiffLocalAndServer = (-new Date().getTimezoneOffset() * 60 * 1000 - serverTimeZoneOffset);
		wrapper = wrapper.addMillis((int) timeDiffLocalAndServer);
		return wrapper.asDate();
	}

	public static Date serverString2LocalDate(String serverTime) {
		return serverDate.parse(serverTime);
	}

	public static String localDate2LocalString(Date localDate) {
		return dateFormat.format(localDate);
	}

	public static String seconds2DayAndTime(long seconds) {
		long days = seconds / (3600 * 24);
		seconds = seconds - (days * 24 * 3600);
		long hours = seconds / 3600;
		seconds = seconds - (hours * 3600);
		long minutes = seconds / 60;
		return UIContext.Messages.dayAndTime(days,
				simpleNumberFormat.format(hours),
				simpleNumberFormat.format(minutes));
	}

	public static String seconds2String(long seconds) {
		long hours = seconds / 3600;
		long minutes = (seconds - (hours * 3600)) / 60;
		seconds = seconds % 60;
		return simpleNumberFormat.format(hours) + ":"
				+ simpleNumberFormat.format(minutes) + ":"
				+ simpleNumberFormat.format(seconds);
	}

	public static String milseconds2String(long value) {
		long seconds = value / 1000;
		return seconds2String(seconds);
	}

	public static long MBPerMin2BytePerSec(long mbPerMin) {
		return (mbPerMin << 20) / 60;
	}

	public static String fileCopyStatus2String(long backupStatus) {
		if (backupStatus == ArchiveJobCancelled)
			return UIContext.Constants.backupStatusCanceled();
		else if (backupStatus == ArchiveJobCrashed)
			return UIContext.Constants.backupStatusCrashed();
		else if (backupStatus == ArchiveJobFailed)
			return UIContext.Constants.backupStatusFailed();
		else if (backupStatus == ArchiveJobFinished)
			return UIContext.Constants.backupStatusFinished();
		else
			return UIContext.Constants.backupStatusUnknown();
	}

	public static String bytes2String(long bytes) {
		if (bytes < 1024)
			return UIContext.Messages.bytes(bytes + "");
		else if (bytes < (1024 * 1024)) {
			String kb = number.format(((double) bytes) / 1024);
			if (kb.startsWith("1024"))
				return UIContext.Messages.MB("1");

			return UIContext.Messages.KB(kb);
		} else if (bytes < (1024 * 1024 * 1024)) {
			String mb = number.format(((double) bytes) / (1024 * 1024));
			if (mb.startsWith("1024"))
				return UIContext.Messages.GB("1");

			return UIContext.Messages.MB(mb);
		} else
			return UIContext.Messages.GB(number.format(((double) bytes)
					/ (1024 * 1024 * 1024)));
	}

	public static String bytes2MBString(long bytes) {
		String mb = number.format(((double) bytes) / (1024 * 1024));
		if (mb.startsWith("1024"))
			return UIContext.Messages.jobMonitorThroughoutGPerMin("1");

		return UIContext.Messages.jobMonitorThroughout(mb);
	}

	public static String bytes2GBString(long bytes) {
		return UIContext.Messages.GB(number.format(((double) bytes)
				/ (1024 * 1024 * 1024)));
	}

	// public static String scheduleIntervalUnitModel2String(int type){
	// if (type == BackupScheduleIntervalUnitModel.Minute)
	// return UIContext.Constants.minutes();
	// else if (type == BackupScheduleIntervalUnitModel.Hour)
	// return UIContext.Constants.hours();
	// else if (type == BackupScheduleIntervalUnitModel.Day)
	// return UIContext.Constants.days();
	// else if (type == BackupScheduleIntervalUnitModel.Backups)
	// return UIContext.Constants.ArchiveScheduleUnit();
	// return "";
	// }

	public static ColumnConfig createColumnConfig(String id, String header,
			int width) {
		return createColumnConfig(id, header, width, null);
	}

	public static ColumnConfig createColumnConfig(String id, String header,
			int width, GridCellRenderer<?> renderer) {
		ColumnConfig column = new ColumnConfig();
		column.setGroupable(false);
		column.setSortable(false);

		column.setId(id);
		column.setHeader(header);
		column.setMenuDisabled(true);
		if (width >= 0)
			column.setWidth(width);
		if (renderer != null)
			column.setRenderer(renderer);
		return column;
	}

	public static ColumnConfig createColumnConfigForRecentPanel(String id,
			String header, int width, GridCellRenderer<?> renderer) {
		ColumnConfig column = new ColumnConfig();
		column.setGroupable(false);
		column.setSortable(true);

		column.setId(id);
		column.setHeader(header);
		column.setMenuDisabled(true);
		if (width >= 0)
			column.setWidth(width);
		if (renderer != null)
			column.setRenderer(renderer);
		return column;
	}

	public static boolean isValidRemotePath(String path) {

		String serverNameReg = "\\\\\\\\[^`~!@#\\$\\^&\\*\\(\\)=\\+\\[\\]{}\\\\\\|;:'\",<>/\\?]+";
		String directroy = "[^\\\\/:\\*\\?\"<>\\|]+";
		String absoluteDirReg = "(\\\\" + directroy + ")*(\\\\)?$";

		if (path != null) {
			return path.trim().matches(serverNameReg + absoluteDirReg);
		}
		return false;
	}

	/**
	 * This function will replace all of "/" to "\" in the path.
	 * 
	 * @return
	 */
	public static String getNormalizedPath(String path) {
		if (path == null)
			return path;

		return path.replaceAll("/", "\\\\").trim();
	}

	/**
	 * This function check if the agent is IE6.
	 * 
	 * @param agent
	 * @return
	 */
	public static boolean checkIE6(String agent) {
		if (agent == null || agent.equals("")) {
			return false;
		}
		if (agent.indexOf("msie 6.0") > -1) {
			return true;
		}
		return false;
	}

	public static String convert2UILabel(String value) {
		if (value == null || value.length() == 0)
			return UIContext.Constants.NA();
		return value;
	}

	public static String getCatalogStatusString() {
		return null;
	}

	public static String getGRTCatalogStatusString() {
		return null;
	}

	public static String ConvertArchiveJobStatusToString(
			Integer archiveJobStatus) {
		switch (archiveJobStatus) {
		case ArchiveJobReady:
			return UIContext.Constants.readyStatus();
		case ScheduleScheduled:
			return UIContext.Constants.scheduledStatus();
		case ScheduleRunning:
			return UIContext.Constants.progressStatus();
		case ArchiveJobFinished:
			return UIContext.Constants.finishedStatus();
		case ArchiveJobIncomplete:
			return UIContext.Constants.incompleteStatus();
		case ArchiveJobCancelled:
			return UIContext.Constants.cancelledStatus();
		case ArchiveJobFailed:
			return UIContext.Constants.failed();
		case ArchiveJobCrashed:
			return UIContext.Constants.jobMonitorStatusCrashed();
		default:
			return UIContext.Constants.notApplicable();
		}
	}

	public static String getCatalogEclipsedTime(String value) {
		// long millseconds = new Date().getTime() -
		// Utils.serverTimeToLocalTime(new Date(Long.parseLong(value) *
		// 1000)).getTime();
		long millseconds = new Date().getTime()
				- new Date(Long.parseLong(value) * 1000).getTime();
		return Utils.milseconds2String(millseconds);
	}

	public static Template getTooltipWrapTemplate(String tipText) {

		String modTip = "<div class='tooltip-item'><pre>" + tipText
				+ "</pre></div>";

		if (isFirefoxBrowser())
			modTip = "<div class='tooltip-item'>" + tipText + "</div>";

		Template template = new Template(modTip);
		return template;
	};

	public static DisclosurePanel getDisclosurePanel(String title) {
		DisclosurePanel disclosurePanel;
		FlashImageBundle iconBundle = GWT.create(FlashImageBundle.class);
		disclosurePanel = new DisclosurePanel(iconBundle.disclosurePanelOpen(),
				iconBundle.disclosurePanelClosed(), title);
		disclosurePanel.setOpen(true);
		disclosurePanel.setWidth("100%");
		disclosurePanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		disclosurePanel.setOpen(true);
		return disclosurePanel;
	}

	public static native int getScreenWidth() /*-{
		return $wnd.screen.width;
	}-*/;

	public static native int getScreenHeight() /*-{
		return $wnd.screen.height;
	}-*/;

	/**
	 * Gets the name of the used browser.
	 */
	public static native String getBrowserName() /*-{
		return navigator.userAgent.toLowerCase();
	}-*/;

	/**
	 * Returns true if the current browser is Firefox.
	 */
	public static boolean isFirefoxBrowser() {
		return getBrowserName().toLowerCase().contains("firefox");
	}

	// Add tool tip without showing header
	public static ToolTip addToolTip(Component widget, String toolTip) {
		ToolTip tp = widget.getToolTip();
		if (tp == null) {
			ToolTipConfig tpConfig = new ToolTipConfig(toolTip);
			tp = new ToolTip(widget, tpConfig);
			tp.setHeaderVisible(false);
		} else {
			ToolTipConfig tpConfig = tp.getToolTipConfig();
			tpConfig.setText(toolTip);
			tp.update(tpConfig);
		}

		return tp;
	}

	// Add tool tip without showing header with dismiss delay
	public static ToolTip addToolTip(Component widget, String toolTip,
			int dismissDelay) {
		ToolTip tp = widget.getToolTip();
		if (tp == null) {
			ToolTipConfig tpConfig = new ToolTipConfig(toolTip);
			tpConfig.setDismissDelay(dismissDelay);
			tp = new ToolTip(widget, tpConfig);
			tp.setHeaderVisible(false);
		} else {
			ToolTipConfig tpConfig = tp.getToolTipConfig();
			tpConfig.setDismissDelay(dismissDelay);
			tpConfig.setText(toolTip);
			tp.update(tpConfig);
		}
		return tp;
	}

	public static void updateToolTip(ToolTip tip, String msg) {
		if (tip != null) {
			ToolTipConfig tpConfig = tip.getToolTipConfig();
			tpConfig.setText(msg);
			tip.update(tpConfig);
		}
	}

	public static String getMachineName(String remotePath) {
		if (remotePath == null)
			return null;
		if (remotePath.startsWith("\\\\") && remotePath.length() > 2) {
			int indexBackSlash = remotePath.indexOf("\\", 3);
			int indexSlash = remotePath.indexOf("/", 3);
			int index = indexBackSlash == -1 || indexSlash > 0
					&& indexBackSlash > indexSlash ? indexSlash
					: indexBackSlash;
			if (index < 0)
				index = remotePath.length();

			return remotePath.substring(2, index);
		}

		return null;
	}

	public static void cacheConnectionInfo(String dest, String userName,
			String password) {
		if (connectionCache != null) {
			if (!isEmptyOrNull(dest) && !isEmptyOrNull(password)
					&& !isEmptyOrNull(userName)) {
				String[] info = new String[] { "", userName, password };
				connectionCache.put(Utils.getMachineName(dest), info);
			}
		}

	}

	public static boolean isEmptyOrNull(String target) {
		if (target == null || target.equals("") || target.trim().equals(""))
			return true;
		return false;
	}

	public static String[] getConnectionInfo(String path) {
		if (connectionCache != null) {
			return connectionCache.get(getMachineName(path));
		}

		return null;
	}

	public static void clearConnectionCache() {
		if (connectionCache != null) {
			connectionCache.clear();
			connectionCache = null;
		}
	}

	public static void setMessageBoxDebugId(MessageBox messageBox) {
		if (messageBox == null)
			return;

		messageBox.getDialog().ensureDebugId(
				"60b952c8-a510-4b91-bf47-1469d8cb71f0");
		Button OKButton = messageBox.getDialog().getButtonById(Dialog.OK);
		if (OKButton != null) {
			OKButton.ensureDebugId("44cb254a-6123-44bf-82e3-34e695b33890");
		}
		Button CancelButton = messageBox.getDialog().getButtonById(
				Dialog.CANCEL);
		if (CancelButton != null) {
			CancelButton.ensureDebugId("d2a6708a-8f3a-40a7-9452-3c41c3c71737");
		}
		Button YesButton = messageBox.getDialog().getButtonById(Dialog.YES);
		if (YesButton != null) {
			YesButton.ensureDebugId("06fcf1d7-fe34-499c-90ba-b16b1f57affa");
		}
		Button NoButton = messageBox.getDialog().getButtonById(Dialog.NO);
		if (NoButton != null) {
			NoButton.ensureDebugId("167cb576-6616-4044-aa4e-f8d20df77125");
		}
		Button CloseButon = messageBox.getDialog().getButtonById(Dialog.CLOSE);
		if (CloseButon != null) {
			CloseButon.ensureDebugId("2f25848f-08bd-48e7-9590-da57507263fc");
		}
	}

	public static String GetServerDateString(DateWrapper beginDateWrap,
			boolean isKeepHoursMinSec) {
		String strSvrBeginDate = "";
		strSvrBeginDate += beginDateWrap.getFullYear();
		strSvrBeginDate += "-" + (1 + beginDateWrap.getMonth());
		strSvrBeginDate += "-" + beginDateWrap.getDate();

		if (!isKeepHoursMinSec) {
			strSvrBeginDate += " 00:00:00";
		} else {
			strSvrBeginDate += " " + beginDateWrap.getHours();
			strSvrBeginDate += ":" + beginDateWrap.getMinutes();
			strSvrBeginDate += ":" + beginDateWrap.getSeconds();
		}
		return strSvrBeginDate;
	}

	public static String getDriveLetter(String path) {
		String driveLetter = "";
		if (path != null) {
			if (path.length() >= 3) {
				driveLetter = path.substring(0, 3);
			} else if (path.length() == 2) {
				driveLetter = path.substring(0, 2) + "\\";
			}

		}
		return driveLetter;
	}

	public static ServiceInfoModel getServiceInfo(String server,
			String protocol, Integer port) {
		ServiceInfoModel model = new ServiceInfoModel();
		if (Utils.isEmptyOrNull(server)) {
			model.setServer("localhost");
		} else {
			model.setServer(server);
		}

		if (Utils.isEmptyOrNull(protocol)) {
			model.setProtocol("http");
		} else {
			model.setProtocol(protocol);
		}

		if (port == null || port.intValue() == 0) {
			model.setPort(8014);
		} else {
			model.setPort(port);
		}

		return model;
	}

	/*
	 * return: 0 - info 1 - error 2 - Warning 3 - N/A
	 */
	public static int getNodeConnectionMessageType(NodeConnectionModel conn) {
		if (conn != null && conn.getErrorCode() != null) {
			int error1 = conn.getErrorCode().intValue();
			if (error1 == 0) {
				return 0;
			} else if (error1 == 20) {
				int error2 = conn.getErrorCodeExt();
				if ((error2 & 0x2) != 0 || (error2 & 0x4) != 0) {// no perl or
																	// no parted
					return 1;
				}

				return 2;// no nfs or cifs
			} else {
				return 1;
			}
		}
		return 3;
	}

	public static String getNodeConnectionMessage(NodeConnectionModel conn,
			String node, String d2dServer, boolean popup) {
		String mesg = "";
		if (conn != null && conn.getErrorCode() != null) {
			int errorCode = conn.getErrorCode().intValue();

			switch (errorCode) {
			case 0:
				mesg = UIContext.Messages.validateSuccessfully(node);
				break;
			case 1:
			case 3:
			case 9:
				mesg = UIContext.Messages.connectFailedWrongNetwork(node);
				break;
			case 2:
				mesg = UIContext.Constants.connectFailedWrongUserAccount();
				break;
			case 22:
				mesg = UIContext.Constants.connectFailedSSHKey();
				break;
			case 4:
				mesg = UIContext.Messages
						.connectFailedExecuteCheckMachine(node);
				break;
			case 5:
				mesg = UIContext.Constants.connectFailedNotSupportedPlatform();
				break;
			case 6:
				mesg = UIContext.Constants
						.connectFailedNotSupportedLinuxPlatform();
				break;
			case 7:
				mesg = UIContext.Constants.connectFailedNotSupportedArch();
				break;
			case 8:
				mesg = UIContext.Constants.connectFailedNotSupportedOSVersion();
				break;
			case 20: {
				String extErrMsg = "";
				int extWarnMsg = 0;
				if (conn.getErrorCodeExt() != null) {
					int errorCodeExt = conn.getErrorCodeExt().intValue();

					if ((errorCodeExt & 0x01) != 0) {
						extWarnMsg += 1;
					}
					if ((errorCodeExt & 0x02) != 0) {
						if (!extErrMsg.isEmpty()) {
							extErrMsg += ", ";
						}
						extErrMsg += UIContext.Constants.connectFailedNoPerl();
					}
					if ((errorCodeExt & 0x04) != 0) {
						if (!extErrMsg.isEmpty()) {
							extErrMsg += ", ";
						}
						extErrMsg += UIContext.Constants
								.connectFailedNoParted();
					}
					if ((errorCodeExt & 0x08) != 0) {

						extWarnMsg += 2;
					}
				}
				if (!extErrMsg.isEmpty()) {
					mesg = UIContext.Constants.connectFailedNoModules()
							+ extErrMsg;
				} else if (extWarnMsg == 3) {
					mesg = UIContext.Constants.connectWarnningNoModules();
				} else if (extWarnMsg == 1) {
					mesg = UIContext.Messages
							.connectWarnningNoCifsOrNfs(UIContext.Constants
									.connectFailedNoNfs());
				} else if (extWarnMsg == 2) {
					mesg = UIContext.Messages
							.connectWarnningNoCifsOrNfs(UIContext.Constants
									.connectFailedNoCifs());
				}
				break;
			}
			case 30:
				if (popup) {
					mesg = UIContext.Messages.nodeIsManagedByOther(
							conn.getD2DServer(), d2dServer);
				} else {
					mesg = UIContext.Messages.nodeIsManagedByOtherInfo(conn
							.getD2DServer());
				}
				break;
			case 31:
				mesg = UIContext.Messages
						.nodeManageFailedByRunningJob(d2dServer);
				break;
			case 40:
				mesg = UIContext.Constants.connectFailedNotRootUser();
				break;
			case 41:
				mesg = UIContext.Constants.invalidRootPasswd();
				break;
			case 42:
				mesg = UIContext.Messages.connectFailedExecutePwdExpired(node);
				break;
			case 100:
				mesg = UIContext.Constants.connectFailedUndefinedReason();
				break;
			}
		}
		return mesg;
	}

	public static boolean is24Hours() {
		String fmt = UIContext.Constants.timeDateFormat();
		if (fmt.indexOf('H') > -1 || fmt.indexOf('k') > -1)
			return true;
		return false;
	}

	public static int minHour() {
		String fmt = UIContext.Constants.timeDateFormat();
		if (fmt.indexOf('H') > -1 || fmt.indexOf('K') > -1)
			return 0;
		else
			return 1;
	}

	public static int maxHour() {
		String fmt = UIContext.Constants.timeDateFormat();
		if (is24Hours()) {
			if (fmt.indexOf('H') > -1)
				return 23;
			else
				return 24;
		} else {
			if (fmt.indexOf('h') > -1)
				return 12;
			else
				return 11;
		}
	}

	public static boolean isHourPrefix() {
		String fmt = UIContext.Constants.timeDateFormat();
		if (fmt.indexOf("HH") > -1 || fmt.indexOf("hh") > -1
				|| fmt.indexOf("KK") > -1 || fmt.indexOf("kk") > -1)
			return true;
		return false;
	}

	public static boolean isMinutePrefix() {
		String fmt = UIContext.Constants.timeDateFormat();
		if (fmt.indexOf("mm") > -1)
			return true;
		return false;
	}

	public static String prefixZero(int val, int digit) {
		String str = Integer.toString(val);
		int pre = digit - str.length();
		for (int i = 0; i < pre; i++)
			str = '0' + str;
		return str;
	}

	@SuppressWarnings("deprecation")
	public static Date convertD2DTimeModel(D2DTimeModel time) {
		if (time != null && time.getYear() > 1900) {
			return new Date(time.getYear() - 1900, time.getMonth(),
					time.getDay(), time.getHourOfDay(), time.getMinute());
		} else {
			return null;
		}
	}

	public static String formatD2DTime(D2DTimeModel time) {
		Date date = convertD2DTimeModel(time);
		if (date != null) {
			return dateFormat.format(date);
		} else {
			return "";
		}

	}

	public static String formatD2DDate(D2DTimeModel time) {
		Date date = convertD2DTimeModel(time);
		if (date != null) {
			return dateOnlyFormat.format(date);
		} else {
			return "";
		}

	}

	public static String getBackupMethod(int method) {
		if (method == JobType.BACKUP_FULL.getValue()) {
			return UIContext.Constants.fullBackup();
		} else if (method == JobType.BACKUP_INCREMENTAL.getValue()) {
			return UIContext.Constants.incrementalBackup();
		} else {
			return UIContext.Constants.verifyBackup();
		}
	}

	public static boolean isBackupJob(int jobType) {
		if (jobType == JobType.BACKUP.getValue()
				|| jobType == JobType.BACKUP_FULL.getValue()
				|| jobType == JobType.BACKUP_INCREMENTAL.getValue()
				|| jobType == JobType.BACKUP_VERIFY.getValue()) {
			return true;
		} else {
			return false;
		}
	}

	public static String getNodeNameWithoutPort(String nodeName) {
		if (nodeName != null && validateMacAddress(nodeName)) {
			return nodeName;
		}
		if (nodeName != null && nodeName.contains(":")) {
			return nodeName.split(":")[0];
		}
		return nodeName;
	}

	public static String getNormalRpName(String rpName) {
		String result = "";
		if (rpName != null) {
			String[] name = rpName.split("/");
			if (name.length == 2) {
				result = name[1];
			} else {
				result = rpName;
			}
		}
		return result;
	}

	public static String getRandomString(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static boolean containSpecialCharacter(String charSeq) {
		
		if (charSeq.contains("\\*") || charSeq.contains("?")
				|| charSeq.contains("|") || charSeq.contains("&")
				|| charSeq.contains(";") || charSeq.contains("]")
				|| charSeq.contains("[") || charSeq.contains("{")
				|| charSeq.contains("}") || charSeq.contains("(")
				|| charSeq.contains("\\)") || charSeq.contains("<")
				|| charSeq.contains(">") || charSeq.contains("\"")
				|| charSeq.contains("`") || charSeq.contains("$")) {
			return true;
		} else {
			return false;
		}
	}
}