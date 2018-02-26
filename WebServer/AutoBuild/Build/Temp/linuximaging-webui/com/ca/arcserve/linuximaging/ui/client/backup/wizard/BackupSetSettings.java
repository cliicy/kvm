package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.RetentionModel;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.Image;

public class BackupSetSettings extends LayoutContainer {

	private LayoutContainer setTotalContainer;

	private Radio week;
	private Radio month;
	private NumberField backupsetNumField;
	private BaseSimpleComboBox<String> dateSavedBox;
	private BaseSimpleComboBox<String> monthSavedBox;

	private HorizontalPanel notePanel;

	private static final String[] weekDays = {
			UIContext.Constants.selectFirDayOfWeek(),
			UIContext.Constants.selectSecDayOfWeek(),
			UIContext.Constants.selectThiDayOfWeek(),
			UIContext.Constants.selectFouDayOfWeek(),
			UIContext.Constants.selectFifDayOfWeek(),
			UIContext.Constants.selectSixDayOfWeek(),
			UIContext.Constants.selectSevDayOfWeek() };

	public BackupSetSettings() {

		TableLayout panelLayout = new TableLayout();
		panelLayout.setColumns(1);
		panelLayout.setWidth("97%");
		this.setLayout(panelLayout);

		LayoutContainer radioContainer = new LayoutContainer();
		radioContainer.setStyleAttribute("margin-left", "0px");

		TableLayout radioLayout = new TableLayout();
		radioLayout.setColumns(2);
		radioLayout.setCellPadding(2);
		radioLayout.setWidth("97%");
		radioContainer.setLayout(radioLayout);

		Image warningImage = UIContext.IconHundle.warning().createImage();
		notePanel = new HorizontalPanel();
		notePanel.setStyleAttribute("padding-left", "4px");
		notePanel.setWidth("97%");
		LabelField setNumNoteLabel = new LabelField();
		setNumNoteLabel.setText(UIContext.Constants.recoverySetNumNote());
		TableData tdw = new TableData();
		tdw.setStyle("padding: 2px 3px 3px 0px;");
		notePanel.add(warningImage, tdw);
		notePanel.add(setNumNoteLabel);
		this.add(notePanel);

		addBackupSetContainer();
		this.add(setTotalContainer);
	}

	private void addBackupSetContainer() {
		setTotalContainer = new LayoutContainer();
		setTotalContainer.setBorders(true);
		setTotalContainer.setStyleAttribute("margin-left", "5px");

		TableLayout secTLayout = new TableLayout();
		secTLayout.setColumns(2);
		secTLayout.setCellPadding(2);
		secTLayout.setWidth("97%");
		setTotalContainer.setLayout(secTLayout);

		// Label:A set always starts with a full backup and ends the next
		// starts.....
		LabelField backupsetNumlabel = new LabelField();
		backupsetNumlabel.setText(UIContext.Constants.settingBackupSetNumCon());
		TableData tableData = new TableData();
		tableData.setWidth("100%");
		tableData.setColspan(2);
		setTotalContainer.add(backupsetNumlabel, tableData);

		backupsetNumField = new NumberField();
		/*
		 * if(GXT.isIE) backupsetNumField.setStyleAttribute("margin-left",
		 * "8px"); else backupsetNumField.setStyleAttribute("margin-left",
		 * "15px");
		 */
		backupsetNumField.setStyleAttribute("margin-left", "5px");
		backupsetNumField.setMaxValue(UIContext.maxRPLimit);
		backupsetNumField.setMinValue(1);
		backupsetNumField.setValue(2);
		backupsetNumField.setAllowBlank(false);
		backupsetNumField.setAllowDecimals(false);
		backupsetNumField.setValidateOnBlur(true);
		backupsetNumField.setWidth(120);
		backupsetNumField.ensureDebugId("3DAC17D9-EBB7-4d54-B340-35DB7D19765B");
		backupsetNumField.getMessages().setMaxText(
				UIContext.Messages
						.settingsBackupSetCountExceedMax(UIContext.maxBSLimit));
		backupsetNumField.getMessages().setMinText(
				UIContext.Constants.settingsBackupSetCountErrorTooLow());
		Utils.addToolTip(backupsetNumField,
				UIContext.Constants.backupsetNumberTooltip());
		tableData = new TableData();
		tableData.setColspan(1);
		tableData.setWidth("40%");
		setTotalContainer.add(new LabelField(), tableData);
		tableData = new TableData();
		tableData.setColspan(1);
		tableData.setWidth("60%");
		setTotalContainer.add(backupsetNumField, tableData);

		// Set When to start a new backup set label
		LabelField setBackupsetNumInfo = new LabelField(
				UIContext.Constants.startBackupsetTooltip());
		tableData = new TableData();
		tableData.setWidth("100%");
		tableData.setColspan(2);
		setTotalContainer.add(setBackupsetNumInfo, tableData);

		addWhenToStartContainer();
	}

	private void addWhenToStartContainer() {
		// Selected day of week
		// Selected day of month
		// week radio
		RadioGroup rg = new RadioGroup();
		week = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				dateSavedBox.setEnabled(true);
				monthSavedBox.setEnabled(false);
			}
		};
		week.ensureDebugId("E2B40D43-0488-40e2-94ED-4B066ABE80EA");
		week.setBoxLabel(UIContext.Constants.settingBackupDate());
		week.setStyleAttribute("margin-left", "15px");
		Utils.addToolTip(week, UIContext.Constants.selectDayofWeekTooltip());
		week.setValue(true);
		rg.add(week);
		TableData td1 = new TableData();
		td1.setWidth("40%");
		// td1.setStyle("margin-left:15px");
		setTotalContainer.add(week, td1);

		// week combobox
		dateSavedBox = new BaseSimpleComboBox<String>();
		dateSavedBox.ensureDebugId("47C7A99A-084D-4572-966F-BB47CE69D2AD");
		dateSavedBox.setEditable(false);
		for (int i = 0; i < 7; i += 1) {
			dateSavedBox.add(weekDays[i]);
		}
		dateSavedBox.setSimpleValue(weekDays[5]);
		Utils.addToolTip(dateSavedBox,
				UIContext.Constants.selectDayofWeekTooltip());
		dateSavedBox.setWidth(120);
		dateSavedBox.setStyleAttribute("margin-left", "5px");
		dateSavedBox.setEnabled(true);
		TableData td2 = new TableData();
		td2.setWidth("60%");
		setTotalContainer.add(dateSavedBox, td2);

		// month radio
		month = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				monthSavedBox.setEnabled(true);
				dateSavedBox.setEnabled(false);
				BackupSetSettings.this.repaint();
			}
		};
		month.ensureDebugId("E08FDBE5-E85C-4bc7-9CDA-E78981C93F13");
		month.setBoxLabel(UIContext.Constants.settingBackupDateofMonth());
		month.setStyleAttribute("margin-left", "15px");
		Utils.addToolTip(month, UIContext.Constants.selectDayofMonthTooltip());
		rg.add(month);
		TableData td3 = new TableData();
		td3.setWidth("40%");
		setTotalContainer.add(month, td3);

		// month ComboBox
		monthSavedBox = new BaseSimpleComboBox<String>();
		monthSavedBox.ensureDebugId("D21A414D-31FB-4025-92DD-8594E0CC3B64");
		monthSavedBox.setEditable(false);
		for (int i = 1; i < 33; i += 1) {
			if (i == 32)
				monthSavedBox.add(UIContext.Constants.selectLastDayOfMonth());
			else
				monthSavedBox.add(i + "");
		}
		monthSavedBox.add(UIContext.Constants.lastSunday());
		monthSavedBox.add(UIContext.Constants.lastMonday());
		monthSavedBox.add(UIContext.Constants.lastTuesday());
		monthSavedBox.add(UIContext.Constants.lastWednesday());
		monthSavedBox.add(UIContext.Constants.lastThursday());
		monthSavedBox.add(UIContext.Constants.lastFriday());
		monthSavedBox.add(UIContext.Constants.lastSaturday());
		monthSavedBox.setSimpleValue(1 + "");
		Utils.addToolTip(monthSavedBox,
				UIContext.Constants.selectDayofMonthTooltip());
		monthSavedBox.setWidth(120);
		monthSavedBox.setStyleAttribute("margin-left", "5px");
		monthSavedBox.setEnabled(false);
		// tableContainer.add(monthRadioContainer);
		TableData td4 = new TableData();
		td4.setWidth("60%");
		setTotalContainer.add(monthSavedBox, td4);
		// setTotalContainer.add(tableContainer);

	}

	public void RefreshData(RetentionModel retentionPolicy) {
		if (retentionPolicy == null)
			return;

		if (retentionPolicy.getBackupSetCount() != null
				&& retentionPolicy.getBackupSetCount() > 0)
			backupsetNumField.setValue(retentionPolicy.getBackupSetCount());
		if (retentionPolicy.isUseWeekly() != null
				&& retentionPolicy.isUseWeekly()) {
			week.setValue(true);
			dateSavedBox.setEnabled(true);
			month.setValue(false);
			monthSavedBox.setEnabled(false);

			if (retentionPolicy.getDayOfWeek() != null)
				dateSavedBox.setSimpleValue(weekDays[retentionPolicy
						.getDayOfWeek() - 1]);
		} else if (retentionPolicy.isUseWeekly() != null
				&& !retentionPolicy.isUseWeekly()) {
			week.setValue(false);
			dateSavedBox.setEnabled(false);
			month.setValue(true);
			monthSavedBox.setEnabled(true);
			if (retentionPolicy.getDayOfMonth() != null) {
				int day = retentionPolicy.getDayOfMonth();
				if (day == RetentionModel.LAST_DAY){
					monthSavedBox.setSimpleValue(UIContext.Constants
							.selectLastDayOfMonth());
				}else if(day == RetentionModel.LAST_SUNDAY){
					monthSavedBox.setSimpleValue(UIContext.Constants
							.lastSunday());
				}else if(day == RetentionModel.LAST_MONDAY){
					monthSavedBox.setSimpleValue(UIContext.Constants
							.lastMonday());
				}else if(day == RetentionModel.LAST_TUESDAY){
					monthSavedBox.setSimpleValue(UIContext.Constants
							.lastTuesday());
				}else if(day == RetentionModel.LAST_WEDNESDAY){
					monthSavedBox.setSimpleValue(UIContext.Constants
							.lastWednesday());
				}else if(day == RetentionModel.LAST_THURSDAY){
					monthSavedBox.setSimpleValue(UIContext.Constants
							.lastThursday());
				}else if(day == RetentionModel.LAST_FRIDAY){
					monthSavedBox.setSimpleValue(UIContext.Constants
							.lastFriday());
				}else if(day == RetentionModel.LAST_SATURDAY){
					monthSavedBox.setSimpleValue(UIContext.Constants
							.lastSaturday());
				}else{
					monthSavedBox.setSimpleValue(retentionPolicy
							.getDayOfMonth() + "");
				}
			}
		}

	}

	public RetentionModel Save() {
		if (this == null || !this.isRendered())
			return null;
		RetentionModel retentionPolicy = new RetentionModel();
		if(backupsetNumField.getValue()!=null)
			retentionPolicy.setBackupSetCount(backupsetNumField.getValue().intValue());
		retentionPolicy.setUseWeekly(week.getValue());
		retentionPolicy.setDayOfWeek(dateSavedBox.getSelectedIndex() + 1);
		retentionPolicy.setDayOfMonth(monthSavedBox.getSelectedIndex() + 1);

		return retentionPolicy;
	}

	public boolean Validate() {
		if (this == null || !this.isRendered())
			return true;

		return backupSetNumValidate();

	}

	private boolean backupSetNumValidate() {
		Number n = backupsetNumField.getValue();
		if (n == null || n.intValue() == 0) {
			// Protection Settings
			String title = UIContext.Constants.productName();
			// Minimum backup set number.
			String msgStr = UIContext.Constants
					.settingsBackupSetCountErrorTooLow();
			backupsetNumField.setValue(1);
			this.popupMessage(title, msgStr, MessageBox.ERROR, null, null);
			return false;
		} else if (n.intValue() > UIContext.maxBSLimit) {
			String title = UIContext.Constants.productName();
			// Maximum backup set number
			String msgStr = UIContext.Messages
					.settingsBackupSetCountExceedMax(UIContext.maxBSLimit);
			backupsetNumField.setValue(UIContext.maxBSLimit);
			backupsetNumField.fireEvent(Events.Change);
			this.popupMessage(title, msgStr, MessageBox.ERROR, null, null);
			return false;
		}
		return true;
	}

	private void popupMessage(String title, String message, String icon,
			String buttons, Listener<MessageBoxEvent> callback) {
		MessageBox msg = new MessageBox();
		msg.setIcon(icon);
		msg.setTitle(title);
		msg.setMessage(message);
		if (buttons != null && !buttons.isEmpty()) {
			msg.setButtons(buttons);
		}
		if (callback != null)
			msg.addCallback(callback);
		msg.setModal(true);
		Utils.setMessageBoxDebugId(msg);
		msg.show();
	}
}
