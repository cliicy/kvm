package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.ArrayList;
import java.util.List;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.AddServerDialog;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.TooltipSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class BackupSource extends LayoutContainer {
	public static int LABEL_WIDTH = 220;
	public static int FIELD_WIDTH = 370;
	public static int TABLE_HIGHT = 240;
	public static int TABLE_WIDTH = 650;
	public static int MIN_BUTTON_WIDTH = 70;

	private HomepageServiceAsync service = GWT.create(HomepageService.class);

	private BackupSourceInfoTable serverInfo;
	private TextField<String> exclude;
	private TextField<String> excludeFiles;
	private List<NodeModel> serverList;
	private BackupWizardPanel parentWindow;
	private Button add;
	private Button delete;
	private Button validate;
	public static int VALIDATE_BUTTON_STATE_VALIDATE = 1;
	public static int VALIDATE_BUTTON_STATE_CACEL = 2;
	private int validateState = VALIDATE_BUTTON_STATE_VALIDATE;
	private BackupSourceValidate validator;
	private ServiceInfoModel serviceInfo;
	private Menu menu;
	private LabelField excludeLabel;
	private LabelField excludeFilesLabel;
	private TooltipSimpleComboBox<String> volumeSetting;

	public BackupSource(BackupWizardPanel parent) {
		parentWindow = parent;
		defineMainPanel();
		serviceInfo = getBackupServer();

		if (parentWindow != null) {
			getBackupServer();
			List<NodeModel> initialList = parentWindow.getBackupSourceInfo();
			if (initialList != null && initialList.size() > 0) {
				serverList = new ArrayList<NodeModel>(initialList);
				// refreshData();
			} else {
				serverList = new ArrayList<NodeModel>();
			}
		}

		validator = new BackupSourceValidate(serviceInfo, serverInfo);
		validator.setBackupSource(this);
		serverInfo.setServiceInfo(serviceInfo);
	}

	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		if (serverList.size() > 0) {
			refreshData();
		}
	}

	public HomepageServiceAsync getService() {
		return service;
	}

	public ServiceInfoModel getBackupServer() {
		if (parentWindow != null) {
			return parentWindow.getD2DServerInfo();
		}
		return null;
	}

	private void defineMainPanel() {
		TableLayout layout = new TableLayout(2);
		layout.setCellSpacing(5);
		layout.setWidth("97%");
		this.setLayout(layout);
		this.setHeight(BackupWizardPanel.RIGHT_PANEL_HIGHT - 20);
		this.setAutoHeight(true);

		TableData dataAll = new TableData();
		dataAll.setColspan(2);

		LabelField label = new LabelField();
		label.setText(UIContext.Constants.backupSourceHeader());
		label.setStyleAttribute("font-weight", "bold");
		add(label, dataAll);

		label = new LabelField();
		label.setText(UIContext.Constants.backupSourceHint());
		add(label, dataAll);

		defineButtonBar();

		serverInfo = new BackupSourceInfoTable(this, TABLE_HIGHT, TABLE_WIDTH);
		add(serverInfo, dataAll);

		excludeLabel = new LabelField();
		excludeLabel.setStyleAttribute("padding-top", "10px");
		excludeLabel.setText(UIContext.Constants.volumeFilterInfo()
				+ UIContext.Constants.delimiter());
		// excludeLabel.setWidth(LABEL_WIDTH);
		// *******
		LayoutContainer voluemeCounter = new LayoutContainer();
		TableLayout volumeLayout = new TableLayout(2);
		voluemeCounter.setLayout(volumeLayout);
		voluemeCounter.setStyleAttribute("padding-top", "10px");
		volumeSetting = new TooltipSimpleComboBox<String>();
		volumeSetting.setEditable(false);
		volumeSetting.add(UIContext.Constants.exclude());
		volumeSetting.add(UIContext.Constants.include());
		volumeSetting.setSimpleValue(UIContext.Constants.exclude());
		volumeSetting.setWidth(75);
		voluemeCounter.add(volumeSetting);
		// *************
		Utils.addToolTip(excludeLabel, UIContext.Constants.excludeInfoToolTip());
		add(excludeLabel);
		exclude = new TextField<String>();
		exclude.setWidth(295);
		// exclude.setStyleAttribute("padding-top", "10px");
		Utils.addToolTip(exclude, UIContext.Constants.excludeInfoToolTip());
		voluemeCounter.add(exclude);
		add(voluemeCounter);
		excludeFilesLabel = new LabelField();
		excludeFilesLabel.setStyleAttribute("padding-top", "10px");
		excludeFilesLabel.setText(UIContext.Constants.excludeFilesInfo()
				+ UIContext.Constants.delimiter());
		// excludeLabel.setWidth(LABEL_WIDTH);
		Utils.addToolTip(excludeFilesLabel,
				UIContext.Constants.excludeFilesInfoToolTip(), 20000);

		excludeFiles = new TextField<String>();
		excludeFiles.setWidth(FIELD_WIDTH);
		excludeFiles.setStyleAttribute("padding-top", "10px");
		Utils.addToolTip(excludeFiles,
				UIContext.Constants.excludeFilesInfoToolTip(), 20000);

		if (UIContext.selectedServerVersionInfo.isEnableExcludeFile()) {
			add(excludeFilesLabel);
			add(excludeFiles);
		}
	}

	private void defineButtonBar() {
		TableData dataAll = new TableData();
		// dataAll.setPadding(5);
		dataAll.setColspan(2);

		LabelField message = new LabelField(
				UIContext.Messages.addBackupSourceManually());
		add(message, dataAll);

		ButtonBar bar = new ButtonBar();
		bar.setAlignment(HorizontalAlignment.RIGHT);
		bar.setStyleAttribute("padding", "5px");
		// bar.setStyleAttribute("padding-right", "20px");
		validate = new Button(UIContext.Constants.validate());
		validate.setIcon(AbstractImagePrototype.create(UIContext.IconBundle
				.connect()));
		validate.setMinWidth(MIN_BUTTON_WIDTH);
		validate.setArrowAlign(ButtonArrowAlign.RIGHT);
		validate.hide();
		menu = new Menu();
		MenuItem validateAll = new MenuItem(UIContext.Constants.validateAll());
		validateAll.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				validateNodes(serverList);
			}
		});
		MenuItem validateSelected = new MenuItem(
				UIContext.Constants.validateSelected());
		validateSelected
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						validateNodes(serverInfo.getSelectedItems());
					}
				});
		menu.add(validateAll);
		menu.add(validateSelected);
		validate.setMenu(menu);

		validate.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				int state = getValidateBtState();
				if (state == BackupSource.VALIDATE_BUTTON_STATE_CACEL) {
					validator.setValidateIsCanceled(true);
					setValidateToValidate();
					validate.disable();
				}
			}
		});

		add = new Button(UIContext.Constants.add());
		add.setIcon(AbstractImagePrototype.create(UIContext.IconBundle
				.add_button()));
		add.setMinWidth(MIN_BUTTON_WIDTH);
		add.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				AddServerDialog addServer = new AddServerDialog(
						UIContext.Constants.addNode(), getBackupServer());
				addServer.setBackupSource(BackupSource.this);
				addServer.show();
			}
		});

		delete = new Button(UIContext.Constants.delete());
		delete.setMinWidth(MIN_BUTTON_WIDTH);
		delete.setIcon(AbstractImagePrototype.create(UIContext.IconBundle
				.delete_button()));
		delete.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				serverInfo.removeData(serverList);
				if (serverList.size() == 0) {
					validate.hide();
				}
			}
		});
		bar.add(validate);
		bar.add(add);
		bar.add(delete);
		add(bar, dataAll);
	}

	private void validateNodes(List<NodeModel> nodes) {
		if (nodes == null || nodes.size() == 0)
			return;

		validator.setValidateIsCanceled(false);
		validator.setNodeList(nodes);
		validator.Validate();
	}

	public void setValidateToValidate() {
		setValidateBtState(BackupSource.VALIDATE_BUTTON_STATE_VALIDATE);
		setValidateBtText(UIContext.Constants.validate());
		setValidateBtIcon(AbstractImagePrototype.create(UIContext.IconBundle
				.connect()));
		validate.hideMenu();
		if (!validate.isEnabled()) {
			validate.enable();
		}
	}

	public void setValidateToCancel() {
		setValidateBtState(BackupSource.VALIDATE_BUTTON_STATE_CACEL);
		setValidateBtText(UIContext.Constants.cancel());
		setValidateBtIcon(AbstractImagePrototype.create(UIContext.IconBundle
				.cancel()));
		validate.showMenu();
	}

	private void refreshData() {
		parentWindow.mask(UIContext.Constants.loading());
		service.getNodeProtectedState(serviceInfo, serverList,
				new BaseAsyncCallback<List<NodeModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						// Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,"Failed to call web service to get protected node list!");
						refreshSourceTable();
						parentWindow.unmask();
					}

					@Override
					public void onSuccess(List<NodeModel> result) {
						serverList = removeProtectedSources(result);
						refreshSourceTable();
						parentWindow.unmask();
					}
				});
	}

	private void refreshSourceTable() {
		if (serverList != null && serverList.size() > 0) {
			if (serverInfo.getCountOfItems() > 0) {
				serverInfo.removeAllData();
			}
			serverInfo.addData(this.serverList);
			this.validate.show();
		}
	}

	public static void showIsProtectedWarning(List<NodeModel> serverList) {
		if (serverList != null && serverList.size() > 0) {
			String delimiterStr = ", ";
			StringBuffer serverBuffer = new StringBuffer();
			// StringBuffer jobBuffer = new StringBuffer();
			for (NodeModel server : serverList) {
				serverBuffer.append(server.getServerName());
				serverBuffer.append(delimiterStr);

				// jobBuffer.append(server.getJobName());
				// jobBuffer.append(delimiterStr);
			}

			int pos = serverBuffer.lastIndexOf(delimiterStr);
			if (pos != -1) {
				serverBuffer.delete(pos, pos + delimiterStr.length());
			}

			// pos = jobBuffer.lastIndexOf(delimiterStr);
			// if ( pos != -1 )
			// {
			// jobBuffer.delete(pos, pos+delimiterStr.length());
			// }

			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.WARNING, UIContext.Messages
							.wizardBackupSourceAlreadyExisted(serverBuffer
									.toString()));
		}
	}

	private List<NodeModel> removeProtectedSources(List<NodeModel> nodeList) {
		if (nodeList != null && nodeList.size() > 0) {
			List<NodeModel> serverExistedList = new ArrayList<NodeModel>();
			List<NodeModel> freshServer = new ArrayList<NodeModel>();
			for (NodeModel server : nodeList) {
				if (server.getProtected()) {
					serverExistedList.add(server);
				} else {
					freshServer.add(server);
				}
			}

			if (serverExistedList.size() > 0) {
				showIsProtectedWarning(serverExistedList);
			}

			return freshServer;
		}

		return null;
	}

	public boolean duplicated(String server) {
		if (serverList != null && serverList.size() > 0) {
			for (NodeModel svr : serverList) {
				if (svr.getServerName().equalsIgnoreCase(server)) {
					return true;
				}
			}
		}

		return false;
	}

	public List<NodeModel> getServerInfoList() {
		return serverList;
	}

	public String getExcludeVolumns() {
		return exclude.getValue();
	}

	public String getExcludeFiles() {
		return excludeFiles.getValue();
	}

	public Boolean getvolumeSetting() {
		String type = volumeSetting.getSimpleValue();
		if (UIContext.Constants.exclude().equals(type)) {
			return true;
		} else if (UIContext.Constants.include().equals(type)) {
			return false;
		} else {
			return true;
		}
	}

	public void refresh() {
		String excludeVolumes = parentWindow.getExcludeVolumns();
		exclude.setValue(excludeVolumes);
		excludeFiles.setValue(parentWindow.getExcludeFiles());
		serverList = parentWindow.getBackupSourceInfo();
		if (parentWindow.isExclude()) {
			volumeSetting.setSimpleValue(UIContext.Constants.exclude());
		} else {
			volumeSetting.setSimpleValue(UIContext.Constants.include());
		}
		refreshSourceTable();
	}

	public void disableButtonBar() {
		this.add.disable();
		this.validate.disable();
		this.delete.disable();
	}

	public void refreshSourceTable(NodeModel model) {
		serverInfo.addData(model);
		this.validate.show();
	}

	private int getValidateBtState() {
		return validateState;
	}

	private void setValidateBtState(int state) {
		validateState = state;
	}

	private void setValidateBtText(String text) {
		if (text == null)
			return;

		validate.setText(text);
	}

	private void setValidateBtIcon(AbstractImagePrototype icon) {
		if (icon != null) {
			validate.setIcon(icon);
		}
	}

	public List<NodeModel> getServerList() {
		return serverList;
	}

	public BackupSourceInfoTable getServerInfo() {
		return serverInfo;
	}
}
