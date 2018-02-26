package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.PasswordField;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.FileOption;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreTargetModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.HTML;

public class FileTargetSettings extends TargetMachineSettings {

	public final static int ROW_CELL_SPACE = 5;
	public final static int MAX_FIELD_WIDTH = 300;
	public final static int TABLE_HIGHT = 130;
	public static int MIN_BUTTON_WIDTH = 60;

	private TextField<String> txtNodeName;
	private TextField<String> txtUserName;
	private PasswordField txtPassword;
	private TextField<String> txtDestination;
	private Radio overwrite;
	private Radio rename;
	private Radio skip;
	private Radio originalLocation;
	private Radio alternateLocation;
	private HorizontalPanel destination;
	private LabelField lblDestination;
	private CheckBox baseFolder;
	private VerticalPanel structurePanel;
	private String alterHost;
	private String originalHost;

	public FileTargetSettings(RestoreWindow restoreWindow) {
		super(restoreWindow);
	}

	protected LayoutContainer defineTargetTable() {
		LayoutContainer fileTargetTable = new LayoutContainer();
		HorizontalPanel locationGroup = getLocationGroupPanel();
		fileTargetTable.add(locationGroup);

		FieldSet targetFieldSet = new FieldSet();
		targetFieldSet.setHeading(UIContext.Constants.targetMachineSettings());
		// hostNameFieldSet.setStyleAttribute("margin", "5,5,5,5");

		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("100%");
		tlConnSettings.setColumns(2);
		tlConnSettings.setCellPadding(0);
		tlConnSettings.setCellSpacing(2);
		targetFieldSet.setLayout(tlConnSettings);

		TableData tableDataLabel = new TableData();
		tableDataLabel.setWidth("20%");
		tableDataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tableDataField = new TableData();
		tableDataField.setWidth("80%");
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField lblNodeName = new LabelField(
				UIContext.Constants.hostNameOrIP());
		lblNodeName.setAutoWidth(true);
		targetFieldSet.add(lblNodeName, tableDataLabel);
		txtNodeName = new TextField<String>();
		txtNodeName.setWidth(MAX_FIELD_WIDTH);
		targetFieldSet.add(txtNodeName, tableDataField);

		LabelField lblUserName = new LabelField(UIContext.Constants.userName());
		lblUserName.setAutoWidth(true);
		targetFieldSet.add(lblUserName, tableDataLabel);
		txtUserName = new TextField<String>();
		txtUserName.setWidth(MAX_FIELD_WIDTH);
		targetFieldSet.add(txtUserName, tableDataField);

		LabelField lblPassword = new LabelField(UIContext.Constants.password());
		targetFieldSet.add(lblPassword, tableDataLabel);
		txtPassword = new PasswordField(MAX_FIELD_WIDTH);
		targetFieldSet.add(txtPassword, tableDataField);

		lblDestination = new LabelField(UIContext.Constants.destination());
		lblDestination.setAutoWidth(true);
		targetFieldSet.add(lblDestination, tableDataLabel);
		destination = getDestinationPanel();
		targetFieldSet.add(destination, tableDataField);
		lblDestination.hide();
		destination.hide();

		fileTargetTable.add(targetFieldSet);

		fileTargetTable.add(new HTML("<HR>"));

		fileTargetTable.add(getResolveConflictContainer());
		fileTargetTable.add(new HTML("<HR>"));

		generateRootDir();
		fileTargetTable.add(structurePanel);
		setOriginalLocation();
		return fileTargetTable;
	}

	private LayoutContainer getResolveConflictContainer() {
		VerticalPanel conflictPanel = new VerticalPanel();

		LabelField label = new LabelField(
				UIContext.Constants.restoreResolvingConflicts());
		label.addStyleName("restoreWizardSubItem");
		conflictPanel.add(label);

		LabelField lblDescription = new LabelField(
				UIContext.Messages
						.restoreResolvingConflictsDescription(UIContext.productName));
		conflictPanel.add(lblDescription);

		overwrite = new Radio();
		overwrite.setBoxLabel(UIContext.Constants.restoreConflictOverwrite());
		overwrite.ensureDebugId("52401560-2395-4cd7-99A1-337739B4ED83");
		overwrite.setValue(true);
		Utils.addToolTip(overwrite,
				UIContext.Constants.restoreConflictOverwriteTooltip());
		overwrite.addStyleName("restoreWizardLeftSpacing");
		conflictPanel.add(overwrite);

		rename = new Radio();
		rename.setBoxLabel(UIContext.Constants.restoreConflictRename());
		rename.ensureDebugId("F2791EAD-D404-43dc-92C5-63ACAAE445AE");
		Utils.addToolTip(rename,
				UIContext.Constants.restoreConflictRenameTooltip());
		rename.addStyleName("restoreWizardLeftSpacing");
		conflictPanel.add(rename);

		skip = new Radio();
		skip.setBoxLabel(UIContext.Constants.restoreConflictSkip());
		skip.ensureDebugId("4A837B0F-3143-402e-932C-81F38C0A1B10");
		Utils.addToolTip(skip, UIContext.Constants.restoreConflictSkipTooltip());
		skip.addStyleName("restoreWizardLeftSpacing");

		conflictPanel.add(skip);
		RadioGroup radioGroup = new RadioGroup();
		radioGroup.add(overwrite);
		radioGroup.add(rename);
		radioGroup.add(skip);

		return conflictPanel;
	}

	private void generateRootDir() {
		structurePanel = new VerticalPanel();

		LabelField label = new LabelField(
				UIContext.Constants.restoreDirectoryStructure());
		label.addStyleName("restoreWizardSubItem");
		structurePanel.add(label);

		label = new LabelField(
				UIContext.Constants.restoreDirectoryStructureDescription());
		label.addStyleName("restoreWizardSubItemDescription");
		label.addStyleName("restoreWizardTopSpacing");
		structurePanel.add(label);

		baseFolder = new CheckBox();
		baseFolder.ensureDebugId("96D0E4F1-D1DE-4532-A146-6F84BE8CE23E");
		baseFolder.setBoxLabel(UIContext.Constants
				.restoreConflictBaseFolderWillBeCreated());
		Utils.addToolTip(baseFolder, UIContext.Constants
				.restoreConflictBaseFolderWillNotBeCreatedTooltip());
		baseFolder.addStyleName("restoreWizardLeftSpacing");
		structurePanel.add(baseFolder);
	}

	private HorizontalPanel getLocationGroupPanel() {
		HorizontalPanel locationGroup = new HorizontalPanel();
		locationGroup.setSpacing(5);
		RadioGroup radioGroup = new RadioGroup();

		originalLocation = new Radio();
		originalLocation.setBoxLabel(UIContext.Constants
				.restoreToOriginalLocation());
		originalLocation.setValue(true);

		alternateLocation = new Radio();
		alternateLocation.setBoxLabel(UIContext.Constants.restoreTo());
		radioGroup.add(originalLocation);
		radioGroup.add(alternateLocation);
		radioGroup.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				if (originalLocation.getValue()) {
					setOriginalLocation();
				} else {
					setAlternateLocation();
				}
			}

		});

		locationGroup.add(originalLocation);
		locationGroup.add(alternateLocation);
		return locationGroup;
	}

	private void setAlternateLocation() {
		// txtNodeName.setValue("");
		if (alterHost != null)
			txtNodeName.setValue(alterHost);
		txtNodeName.enable();
		lblDestination.show();
		destination.show();
		structurePanel.enable();
	}

	private void setOriginalLocation() {
		alterHost = txtNodeName.getValue();
		txtNodeName.setValue(originalHost);
		// txtNodeName.disable();
		lblDestination.hide();
		destination.hide();
		structurePanel.disable();
	}

	private HorizontalPanel getDestinationPanel() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(0);

		txtDestination = new TextField<String>();
		txtDestination.setWidth(MAX_FIELD_WIDTH);
		panel.add(txtDestination);
		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("padding-left", "10px");
		Button browseButton = new Button(UIContext.Constants.browse());
		browseButton.setMinWidth(UIContext.BUTTON_MINWIDTH);
		container.add(browseButton);
		panel.add(container);

		browseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (validateFileTargetTable(false)) {
					BrowseWindow browseDlg = defineBrowseWindow();
					browseDlg.show();
				}
			}

		});

		return panel;
	}

	private BrowseWindow defineBrowseWindow() {
		final BrowseWindow browseDlg = new BrowseWindow(
				restoreWindow.currentServer, false,
				UIContext.Constants.selectDestinationPath());
		browseDlg.setDebugID("FADD20FE-AABB-4209-A600-3D823BEBD27D",
				"62BE7BDE-0136-47e8-9A9E-9AC0486C0981");
		browseDlg.setResizable(false);
		browseDlg.setHost(txtNodeName.getValue());
		browseDlg.setUser(txtUserName.getValue());
		browseDlg.setPassword(txtPassword.getPasswordValue());
		String scriptUUID = null;
		if (restoreWindow.isModify) {
			scriptUUID = restoreWindow.restoreModel.getUuid();
		}
		browseDlg.setScriptUUID(scriptUUID);

		String inputFolder = txtDestination.getValue();
		if (inputFolder != null) {
			inputFolder = inputFolder.trim();
		}
		browseDlg.setInputFolder(inputFolder);

		browseDlg.setModal(true);
		browseDlg.addWindowListener(new WindowListener() {
			public void windowHide(WindowEvent we) {
				if (browseDlg.getLastClicked() == Dialog.CANCEL) {

				} else {
					String newDest = browseDlg.getDestination() == null ? ""
							: browseDlg.getDestination();
					txtDestination.setValue(newDest);
				}
			}

		});
		return browseDlg;
	}

	private boolean validateFileTargetTable(boolean checkDestination) {
		if (txtNodeName.getValue() == null || txtNodeName.getValue().isEmpty()) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR, UIContext.Constants.nodeNameMessage());
			return false;
		} else {
			if (!Utils.validateLinuxHostName(txtNodeName.getValue().trim())
					&& !Utils.validateIPAddress(txtNodeName.getValue().trim())) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR, UIContext.Constants.nodeNameMessage());
				return false;
			}
		}
		/*
		 * if(txtUserName.getValue()==null){
		 * Utils.showMessage(UIContext.Constants
		 * .productName(),MessageBox.ERROR,UIContext
		 * .Constants.userNameMessage()); return false; }
		 */
		// if(txtPassword.getValue()==null){
		// Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.passwordMessage());
		// return false;
		// }
		if (alternateLocation.getValue() && checkDestination
				&& !Utils.validateLocalAddress(txtDestination.getValue())) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR, UIContext.Constants.destinationMessage());
			return false;
		}

		if (!restoreWindow.isAddedPublicKey()) {
			if (txtUserName.getValue() == null) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR, UIContext.Messages.userNameNotEmpty());
				return false;
			}
		}
		return true;
	}

	public List<RestoreTargetModel> getRestoreTargetList() {
		List<RestoreTargetModel> result = new ArrayList<RestoreTargetModel>();
		RestoreTargetModel target = new RestoreTargetModel();
		target.setAddress(txtNodeName.getValue());
		target.setUserName(txtUserName.getValue());
		target.setPassword(txtPassword.getPasswordValue());
		if (originalLocation.getValue()) {
			target.setRestoreToOriginal(true);
			target.setDestination(UIContext.FILE_SEPATATOR);
		} else {
			target.setRestoreToOriginal(false);
			target.setDestination(txtDestination.getValue());
		}
		if (overwrite.getValue()) {
			target.setFileOption(FileOption.OVERWRITE.getValue());
		} else if (rename.getValue()) {
			target.setFileOption(FileOption.RENAME.getValue());
		} else if (skip.getValue()) {
			target.setFileOption(FileOption.SKIP.getValue());
		}
		target.setCreateRootDir(baseFolder.getValue());
		result.add(target);
		return result;
	}

	public boolean validate() {
		return validateFileTargetTable(true);
	}

	public void save() {
		restoreWindow.restoreModel.setRestoreTargetList(getRestoreTargetList());
	}

	public void refreshData() {
		RestoreModel model = restoreWindow.restoreModel;

		RestoreTargetModel target = model.getRestoreTargetList().get(0);
		if (target.getRestoreToOriginal()) {
			originalLocation.setValue(true);
			setOriginalLocation();
		} else {
			alternateLocation.setValue(true);
			setAlternateLocation();
		}
		alterHost = target.getAddress();
		txtNodeName.setValue(target.getAddress());
		txtUserName.setValue(target.getUserName());
		txtPassword.setPasswordValue(target.getPassword());
		txtDestination.setValue(target.getDestination());
		if (FileOption.SKIP.getValue().equals(target.getFileOption())) {
			skip.setValue(true);
		} else if (FileOption.RENAME.getValue().equals(target.getFileOption())) {
			rename.setValue(true);
		} else {
			overwrite.setValue(true);
		}
		if (target.getCreateRootDir() != null) {
			baseFolder.setValue(target.getCreateRootDir());
		}

		/*
		 * if(model.getRestoreTargetList()==null||model.getRestoreTargetList().get
		 * (0)==null){ originalLocation.setValue(true); setOriginalLocation(); }
		 */
	}

	public void refreshPart() {
		if (originalLocation.getValue()) {
			originalHost = restoreWindow.restoreModel.getMachine();
			txtNodeName.setValue(originalHost);
		}
	}
}
