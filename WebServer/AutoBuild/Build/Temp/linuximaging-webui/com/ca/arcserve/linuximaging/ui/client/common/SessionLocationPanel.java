package com.ca.arcserve.linuximaging.ui.client.common;

import java.util.ArrayList;
import java.util.List;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.restore.RpsFormDialog;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class SessionLocationPanel extends LayoutContainer {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);

	public final static int BACKUP = 1;
	public final static int RESTORE = 2;
	public final static int EVENT_TYPE_NEXT = 1;
	public final static int EVENT_TYPE_CONNECT = 2;
	public final static int EVENT_TYPE_VALIDATE = 3;
	public static int MIN_BUTTON_WIDTH = 50;
	private ComboBox<BackupLocationInfoModel> txtBackupLocation;
	private int MAX_COMBOBOX_WIDTH = 115;
	private TooltipSimpleComboBox<String> cmbLocationType;
	private Button validateButton;
	private int type;
	private BackupLocationInfoModel lastBackupLocation;
	private IWizard parentWindow;
	private LayoutContainer localContainer;
	private Button addButton = new Button();
	private List<BackupLocationInfoModel> nfsLocationList = new ArrayList<BackupLocationInfoModel>();
	private List<BackupLocationInfoModel> cifsLocationList = new ArrayList<BackupLocationInfoModel>();
	private List<BackupLocationInfoModel> rpsLocationList = new ArrayList<BackupLocationInfoModel>();
	private List<BackupLocationInfoModel> localLocationList = new ArrayList<BackupLocationInfoModel>();
	private List<BackupLocationInfoModel> sourceLocationList = new ArrayList<BackupLocationInfoModel>();
	private List<BackupLocationInfoModel> s3LocationList = new ArrayList<BackupLocationInfoModel>();

	private Listener<BaseEvent> pathListener = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			if (!cmbLocationType.getSimpleValue().equals(
					UIContext.Constants.sourceLocal())
					&& !Utils.isEmptyOrNull(txtBackupLocation.getRawValue()
							.trim())) {
				validateButton.setEnabled(true);
			} else {
				validateButton.setEnabled(false);
			}
		}
	};

	private SelectionListener<ButtonEvent> rpsForm = new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {

			RpsFormDialog rpsFormDialog = new RpsFormDialog(txtBackupLocation,
					getServiceInfoModel());
			rpsFormDialog.show();
		}
	};

	public SessionLocationPanel(final int type, int width) {
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		this.setLayout(layout);
		this.setWidth("100%");
		this.type = type;
		HorizontalPanel panel = new HorizontalPanel();
		panel.setWidth(width);
		cmbLocationType = new TooltipSimpleComboBox<String>();
		cmbLocationType
				.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<SimpleComboValue<String>> se) {
						if (type == BACKUP
								|| type == RestoreType.FILE.getValue()
								|| type == RestoreType.BMR.getValue()
								|| type == RestoreType.SHARE_RECOVERY_POINT
										.getValue()
								|| type == RestoreType.MIGRATION_BMR.getValue()
								|| type == RestoreType.VM.getValue()) {
							if (!se.getSelectedItem().getValue()
									.equals(UIContext.Constants.sourceLocal())) {
								validateButton.setVisible(true);
								if (txtBackupLocation.getRawValue() != null) {
									validateButton.setEnabled(true);
								} else {
									validateButton.setEnabled(false);
								}
								localContainer.setVisible(false);
							} else {
								validateButton.setVisible(false);
								localContainer.setVisible(true);
							}
						}
						if (se.getSelectedItem().getValue()
								.equals(UIContext.Constants.cifsShare())) {
							txtBackupLocation.setEditable(true);
							addButton.hide();
							refresComboBox(cifsLocationList,
									BackupLocationInfoModel.TYPE_CIFS);
							txtBackupLocation.removeToolTip();
							txtBackupLocation.setToolTip(UIContext.Constants
									.tooltipForCifs());
							txtBackupLocation.getToolTip().setHeaderVisible(
									false);
						} else if (se.getSelectedItem().getValue()
								.equals(UIContext.Constants.rpsServe())) {
							txtBackupLocation.setEditable(false);
							addButton.show();
							txtBackupLocation.removeToolTip();
							txtBackupLocation.setToolTip(UIContext.Constants
									.addRpsNotice());
							txtBackupLocation.getToolTip().setHeaderVisible(
									false);
							refresComboBox(rpsLocationList,
									BackupLocationInfoModel.TYPE_RPS_SERVER);
						} else if (se.getSelectedItem().getValue()
								.equals(UIContext.Constants.serverLocal())) {
							txtBackupLocation.setEditable(true);
							addButton.hide();
							txtBackupLocation.removeToolTip();
							refresComboBox(localLocationList,
									BackupLocationInfoModel.TYPE_SERVER_LOCAL);
						} else if (se.getSelectedItem().getValue()
								.equals(UIContext.Constants.sourceLocal())) {
							txtBackupLocation.setEditable(true);
							addButton.hide();
							txtBackupLocation.removeToolTip();
							refresComboBox(sourceLocationList,
									BackupLocationInfoModel.TYPE_SOURCE_LOCAL);
						} else if (se.getSelectedItem().getValue()
								.equals(UIContext.Constants.amazonS3())) {
							txtBackupLocation.setEditable(true);
							addButton.hide();
							txtBackupLocation.removeToolTip();
							txtBackupLocation.setToolTip(UIContext.Messages.tooltipForS3("China"));
							txtBackupLocation.getToolTip().setHeaderVisible(
									false);
							refresComboBox(s3LocationList,
									BackupLocationInfoModel.TYPE_AMAZON_S3);
						} else {
							txtBackupLocation.setEditable(true);
							txtBackupLocation.removeToolTip();
							addButton.hide();
							refresComboBox(nfsLocationList,
									BackupLocationInfoModel.TYPE_NFS);
						}
					}
				});
		cmbLocationType.setWidth(MAX_COMBOBOX_WIDTH);
		cmbLocationType.setEditable(false);
		panel.add(cmbLocationType);
		ListStore<BackupLocationInfoModel> store = new ListStore<BackupLocationInfoModel>();
		txtBackupLocation = new ComboBox<BackupLocationInfoModel>();
		txtBackupLocation.setStore(store);
		txtBackupLocation.setWidth(width - MAX_COMBOBOX_WIDTH);
		txtBackupLocation.setEditable(true);
		txtBackupLocation.setTypeAhead(true);
		txtBackupLocation.setTriggerAction(TriggerAction.ALL);
		txtBackupLocation.setDisplayField("displayName");
		txtBackupLocation.setTemplate(getTemplate());
		txtBackupLocation
				.addSelectionChangedListener(new SelectionChangedListener<BackupLocationInfoModel>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<BackupLocationInfoModel> se) {
						BackupLocationInfoModel model = se.getSelectedItem();
						if (model == null) {
							return;
						}
						if (model.getType() == BackupLocationInfoModel.TYPE_CIFS) {
							cmbLocationType.setSimpleValue(UIContext.Constants
									.cifsShare());
						} else if (model.getType() == BackupLocationInfoModel.TYPE_SERVER_LOCAL) {
							cmbLocationType.setSimpleValue(UIContext.Constants
									.serverLocal());
						} else if (model.getType() == BackupLocationInfoModel.TYPE_SOURCE_LOCAL) {
							cmbLocationType.setSimpleValue(UIContext.Constants
									.sourceLocal());
						} else if (model.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
							cmbLocationType.setSimpleValue(UIContext.Constants
									.rpsServe());
						} else if (model.getType() == BackupLocationInfoModel.TYPE_AMAZON_S3) {
							cmbLocationType.setSimpleValue(UIContext.Constants.amazonS3());
						} else {
							cmbLocationType.setSimpleValue(UIContext.Constants
									.nfsShare());
						}

					}
				});
		panel.add(txtBackupLocation);
		if (type == BACKUP || type == RestoreType.FILE.getValue()
				|| type == RestoreType.BMR.getValue()
				|| type == RestoreType.SHARE_RECOVERY_POINT.getValue()
				|| type == RestoreType.MIGRATION_BMR.getValue()
				|| type == RestoreType.VM.getValue()) {
			txtBackupLocation.addListener(Events.Change, pathListener);
			txtBackupLocation.addListener(Events.KeyUp, pathListener);
			validateButton = new Button();
			validateButton.setIcon(AbstractImagePrototype
					.create(UIContext.IconBundle.rightarrow()));
			validateButton.setVisible(false);
			validateButton.setEnabled(false);
			validateButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {
						@Override
						public void componentSelected(ButtonEvent ce) {
							validate(EVENT_TYPE_VALIDATE);
						}
					});
			validateButton.setWidth(25);
			panel.add(validateButton);
			addButton.setMinWidth(MIN_BUTTON_WIDTH);
			addButton.setText(UIContext.Constants.add());
			addButton.setIcon(AbstractImagePrototype
					.create(UIContext.IconBundle.add_button()));
			addButton.addSelectionListener(rpsForm);
			panel.add(addButton);
			addButton.hide();
		}
		this.add(panel);

		localContainer = new LayoutContainer();
		localContainer.setStyleAttribute("padding-top", "5px");
		TableLayout localLayout = new TableLayout(2);
		localLayout.setCellSpacing(5);
		localContainer.setLayout(localLayout);

		Image warningImage = new Image(UIContext.IconBundle.information());
		localContainer.add(warningImage);

		LabelField localLabel = new LabelField(
				UIContext.Constants.backupLocationLocalDescription());
		localContainer.add(localLabel);
		localContainer.setVisible(false);
		this.add(localContainer);
		initComboBox(type);
	}

	public SessionLocationPanel(int type, int width, IWizard parent) {
		this(type, width);
		this.parentWindow = parent;
	}

	private void initComboBox(int type) {
		if (type == BACKUP) {
			cmbLocationType.add(UIContext.Constants.nfsShare());
			cmbLocationType.add(UIContext.Constants.cifsShare());
			cmbLocationType.add(UIContext.Constants.sourceLocal());
			cmbLocationType.add(UIContext.Constants.amazonS3());
			// cmbLocationType.add(UIContext.Constants.rpsServe());
		} else if (type == RestoreType.BMR.getValue()
				|| type == RestoreType.FILE.getValue()
				|| type == RestoreType.SHARE_RECOVERY_POINT.getValue()
				|| type == RestoreType.MIGRATION_BMR.getValue()
				|| type == RestoreType.VM.getValue()) {
			cmbLocationType.add(UIContext.Constants.nfsShare());
			cmbLocationType.add(UIContext.Constants.cifsShare());
			cmbLocationType.add(UIContext.Constants.rpsServe());
			if ((type == RestoreType.BMR.getValue() && UIContext.selectedServerVersionInfo
					.isLiveCD())
					|| type == RestoreType.FILE.getValue()
					|| type == RestoreType.SHARE_RECOVERY_POINT.getValue()) {
				cmbLocationType.add(UIContext.Constants.serverLocal());
			}
			cmbLocationType.add(UIContext.Constants.amazonS3());
		}
	}

	public boolean validate(int eventType) {
		boolean ret = true;
		String message = UIContext.Constants.backupLocationMessage();
		if (cmbLocationType.getSimpleValue() == null
				|| cmbLocationType.getSimpleValue().isEmpty()) {
			ret = false;
		} else if (txtBackupLocation.getRawValue() == null
				|| txtBackupLocation.getRawValue().trim().isEmpty()) {
			ret = false;
		} else if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.nfsShare())) {
			ret = Utils.validateNFSAddress(txtBackupLocation.getRawValue());
			if (type == RestoreType.BMR.getValue()
					&& UIContext.selectedServerVersionInfo.isLiveCD()
					&& !Utils
							.validateIPAddress(Utils
									.getMachineAddress(txtBackupLocation
											.getRawValue()))) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.validateSessionLocationMessage());
				return false;
			}
			if (ret == true
					&& (lastBackupLocation == null || !txtBackupLocation
							.getRawValue().trim()
							.equals(lastBackupLocation.getDisplayName()))) {
				validateBackupLocation(eventType);
				return false;
			}
		} else if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.rpsServe())) {
			ret = Utils.isRpsAddress(txtBackupLocation.getRawValue());
			if (type == RestoreType.BMR.getValue()
					&& UIContext.selectedServerVersionInfo.isLiveCD()
					&& !Utils
							.validateIPAddress(Utils
									.getMachineAddress(txtBackupLocation
											.getRawValue()))) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.validateSessionLocationMessage());
				return false;
			}
			if (ret == true
					&& (lastBackupLocation == null || !txtBackupLocation
							.getRawValue().trim()
							.equals(lastBackupLocation.getDisplayName()))) {
				validateBackupLocation(eventType);
				return false;
			}

		} else if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.cifsShare())) {
			boolean isCIFS = Utils.isCIFSAddress(txtBackupLocation
					.getRawValue().trim());
			if (type == RestoreType.BMR.getValue()
					&& UIContext.selectedServerVersionInfo.isLiveCD()
					&& !Utils
							.validateIPAddress(Utils
									.getMachineAddress(txtBackupLocation
											.getRawValue()))) {
				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR,
						UIContext.Constants.validateSessionLocationMessage());
				ret = false;
			}
			if (isCIFS) {
				if (eventType == EVENT_TYPE_VALIDATE) {
					showCrentialWindow(eventType);
				} else {
					if (lastBackupLocation == null
							|| !txtBackupLocation
									.getRawValue()
									.trim()
									.equals(lastBackupLocation.getDisplayName())) {
						showCrentialWindow(eventType);
						return false;
					}
				}
			} else {
				message = UIContext.Messages
						.invalidCifsPathMessage(Utils.SMB_REGEX_1);
				ret = false;
			}
		} else if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.amazonS3())) {
			boolean isCIFS = Utils.isCIFSAddress(txtBackupLocation
					.getRawValue().trim());
			if (isCIFS) {
				if (eventType == EVENT_TYPE_VALIDATE) {
					showCrentialWindow(eventType);
				} else {
					if (lastBackupLocation == null
							|| !txtBackupLocation
									.getRawValue()
									.trim()
									.equals(lastBackupLocation.getDisplayName())) {
						showCrentialWindow(eventType);
						return false;
					}
				}
			} else {
				message = UIContext.Messages
						.invalidCifsPathMessage(Utils.SMB_REGEX_1);
				ret = false;
			}
		} else {
			ret = Utils.validateLocalAddress(txtBackupLocation.getRawValue());
		}
		if (ret == false) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR, message);
		}
		return ret;
	}

	private void validateBackupLocation(final int eventType) {
		parentWindow.maskUI(UIContext.Constants.validating());
		BackupLocationInfoModel infoModel = new BackupLocationInfoModel();
		infoModel.setSessionLocation(txtBackupLocation.getRawValue());
		if (getLocationType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
			infoModel = txtBackupLocation.getValue();
		}
		service.validateBackupLocation(getServiceInfoModel(), infoModel,
				new BaseAsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						parentWindow.unmaskUI();
					}

					@Override
					public void onSuccess(Boolean result) {
						if (result == true) {
							if (getLocationType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
								lastBackupLocation = txtBackupLocation
										.getValue();
							} else {
								lastBackupLocation = new BackupLocationInfoModel();
								lastBackupLocation.setType(getLocationType());
								lastBackupLocation
										.setSessionLocation(txtBackupLocation
												.getRawValue().trim());
								lastBackupLocation
										.setDisplayName(txtBackupLocation
												.getRawValue().trim());
							}
							if (eventType == EVENT_TYPE_NEXT) {
								parentWindow.showNextSettings();
							} else if (eventType == EVENT_TYPE_CONNECT) {
								parentWindow.resume();
							}
						} else {
							Utils.showMessage(
									UIContext.Constants.productName(),
									MessageBox.ERROR,
									UIContext.Constants.backupLocationMessage());
						}
						parentWindow.unmaskUI();
					}

				});
	}

	public void showCrentialWindow(final int eventType) {
		BackupLocationInfoModel model = getExistLocation(txtBackupLocation
				.getRawValue().trim());
		String username = "";
		String password = "";
		if (model != null) {
			username = model.getUser();
			password = model.getPassword();
		}
		final UserPasswordWindow window = new UserPasswordWindow(
				txtBackupLocation.getRawValue().trim(), username, password,getLocationType(),
				getServiceInfoModel());
		window.setModal(true);
		window.addWindowListener(new WindowListener() {
			public void windowHide(WindowEvent we) {
				if (window.getCancelled() == false) {
					lastBackupLocation = new BackupLocationInfoModel();
					lastBackupLocation.setSessionLocation(txtBackupLocation
							.getRawValue().trim());
					lastBackupLocation.setDisplayName(txtBackupLocation
							.getRawValue().trim());
					lastBackupLocation.setType(getLocationType());
					lastBackupLocation.setUser(window.getUsername());
					lastBackupLocation.setPassword(window.getPassword());
					if (parentWindow != null) {
						if (eventType == EVENT_TYPE_NEXT)
							parentWindow.showNextSettings();
						else if (eventType == EVENT_TYPE_CONNECT)
							parentWindow.resume();
					}
				}
			}
		});
		window.show();
	}

	public int getType() {
		return type;
	}

	public String getValue() {
		return txtBackupLocation.getRawValue();
	}

	public BackupLocationInfoModel getBackupLocationInfo() {
		if (lastBackupLocation == null) {
			BackupLocationInfoModel model = getExistLocation(txtBackupLocation
					.getRawValue().trim());
			if (model != null) {
				return model;
			} else {
				model = new BackupLocationInfoModel();
				model.setType(getLocationType());
				model.setSessionLocation(txtBackupLocation.getRawValue().trim());
				model.setDisplayName(txtBackupLocation.getRawValue().trim());
				return model;
			}
		} else {
			BackupLocationInfoModel model = getExistLocation(txtBackupLocation
					.getRawValue().trim());
			if (model != null) {
				return model;
			} else if (lastBackupLocation.getDisplayName().equals(
					txtBackupLocation.getRawValue().trim())) {
				return lastBackupLocation;
			} else {
				model = new BackupLocationInfoModel();
				model.setType(getLocationType());
				model.setSessionLocation(txtBackupLocation.getRawValue().trim());
				model.setDisplayName(txtBackupLocation.getRawValue().trim());
				return model;
			}
		}
	}

	public int getLocationType() {
		int type = BackupLocationInfoModel.TYPE_NFS;
		if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.nfsShare())) {
			type = BackupLocationInfoModel.TYPE_NFS;
		} else if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.cifsShare())) {
			type = BackupLocationInfoModel.TYPE_CIFS;
		} else if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.sourceLocal())) {
			type = BackupLocationInfoModel.TYPE_SOURCE_LOCAL;
		} else if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.serverLocal())) {
			type = BackupLocationInfoModel.TYPE_SERVER_LOCAL;
		} else if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.rpsServe())) {
			type = BackupLocationInfoModel.TYPE_RPS_SERVER;
		} else if (cmbLocationType.getSimpleValue().equals(
				UIContext.Constants.amazonS3())) {
			type = BackupLocationInfoModel.TYPE_AMAZON_S3;
		} 
		return type;
	}

	public void setBackupLocationInfo(
			BackupLocationInfoModel backupLocationModel) {
		this.lastBackupLocation = backupLocationModel;
		if (backupLocationModel.getType() == BackupLocationInfoModel.TYPE_SOURCE_LOCAL) {
			sourceLocationList.add(backupLocationModel);
		}
		txtBackupLocation.setValue(backupLocationModel);
		setValue(backupLocationModel);
	}

	public void setValue(BackupLocationInfoModel backupLocationModel) {
		String value = backupLocationModel.getSessionLocation();
		if (value == null || value.isEmpty()) {
			return;
		} else if (type == BACKUP) {
			if(backupLocationModel.getType() == BackupLocationInfoModel.TYPE_AMAZON_S3){
				cmbLocationType.setSimpleValue(UIContext.Constants.amazonS3());
			}else if (Utils.validateLocalAddress(value)) {
				cmbLocationType.setSimpleValue(UIContext.Constants
						.sourceLocal());
			} else if (Utils.validateNFSAddress(value)) {
				cmbLocationType.setSimpleValue(UIContext.Constants.nfsShare());
			} else if (Utils.isCIFSAddress(value)) {
				cmbLocationType.setSimpleValue(UIContext.Constants.cifsShare());
			}
		} else if (type == RestoreType.BMR.getValue()
				|| type == RestoreType.MIGRATION_BMR.getValue()
				|| type == RestoreType.FILE.getValue()
				|| type == RestoreType.SHARE_RECOVERY_POINT.getValue()) {
			if(backupLocationModel.getType() == BackupLocationInfoModel.TYPE_AMAZON_S3){
				cmbLocationType.setSimpleValue(UIContext.Constants.amazonS3());
			}else if (Utils.isCIFSAddress(value)) {
				cmbLocationType.setSimpleValue(UIContext.Constants.cifsShare());
			} else if (backupLocationModel.getType() == BackupLocationInfoModel.TYPE_SERVER_LOCAL) {
				cmbLocationType.setSimpleValue(UIContext.Constants
						.serverLocal());
			} else if (backupLocationModel.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
				cmbLocationType.setSimpleValue(UIContext.Constants.rpsServe());
			} else {
				cmbLocationType.setSimpleValue(UIContext.Constants.nfsShare());
			}
		}
	}

	private ServiceInfoModel getServiceInfoModel() {
		return parentWindow == null ? null : parentWindow.getD2DServerInfo();
	}

	public void refreshLocation(List<BackupLocationInfoModel> locationList,
			String defaultLocation) {
		cifsLocationList.clear();
		localLocationList.clear();
		rpsLocationList.clear();
		nfsLocationList.clear();
		sourceLocationList.clear();
		s3LocationList.clear();
		for (BackupLocationInfoModel model : locationList) {
			if (model.getType() == BackupLocationInfoModel.TYPE_CIFS) {
				cifsLocationList.add(model);
			} else if (model.getType() == BackupLocationInfoModel.TYPE_SERVER_LOCAL) {
				localLocationList.add(model);
			} else if (model.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
				rpsLocationList.add(model);
			} else if(model.getType() == BackupLocationInfoModel.TYPE_AMAZON_S3){
				s3LocationList.add(model);
			}else {
				nfsLocationList.add(model);
			}
		}

		for (BackupLocationInfoModel model : locationList) {
			if (!Utils.isEmptyOrNull(defaultLocation)) {
				if (defaultLocation.equals(model.getDisplayName())) {
					txtBackupLocation.setValue(model);
				}
			}
			if (lastBackupLocation != null) {
				if (lastBackupLocation.getSessionLocation().equals(
						model.getSessionLocation())) {
					txtBackupLocation.setValue(model);
				}
			}
		}
		if (lastBackupLocation == null && txtBackupLocation.getValue() == null) {

			if (locationList.size() > 0 && type != BACKUP) {
				txtBackupLocation.setValue(locationList.get(0));
			} else if (cifsLocationList.size() > 0) {
				txtBackupLocation.setValue(cifsLocationList.get(0));
			} else if (sourceLocationList.size() > 0 && type == BACKUP) {
				txtBackupLocation.setValue(sourceLocationList.get(0));
			} else {
				cmbLocationType.setSimpleValue(UIContext.Constants.nfsShare());
			}

		}
	}

	public void refresComboBox(List<BackupLocationInfoModel> locationList,
			int type) {
		if (locationList != null) {
			txtBackupLocation.getStore().removeAll();
			for (BackupLocationInfoModel model : locationList) {
				txtBackupLocation.getStore().add(model);
			}
			BackupLocationInfoModel backupLocationInfoModel = txtBackupLocation
					.getValue();
			if (locationList.size() > 0) {
				if (backupLocationInfoModel == null) {
					txtBackupLocation.setValue(locationList.get(0));
					return;
				}
				if (type != backupLocationInfoModel.getType()) {
					txtBackupLocation.setValue(locationList.get(0));
				}
			} else {
				txtBackupLocation.clear();
			}
		} else {
			txtBackupLocation.getStore().removeAll();
		}
	}

	public List<BackupLocationInfoModel> getRpsLocationList() {
		return rpsLocationList;
	}

	private BackupLocationInfoModel getExistLocation(String location) {
		BackupLocationInfoModel model = null;
		int locationType = getLocationType();
		if (txtBackupLocation.getStore() != null) {
			for (int i = 0; i < txtBackupLocation.getStore().getCount(); i++) {
				BackupLocationInfoModel infoModel = txtBackupLocation
						.getStore().getAt(i);
				if (location.equals(infoModel.getDisplayName())
						&& locationType == infoModel.getType()) {
					model = txtBackupLocation.getStore().getAt(i);
					break;
				}
			}
		}
		return model;
	}

	private native String getTemplate() /*-{
		return [
				'<tpl for=".">',
				'<div class="x-combo-list-item" qtip="{displayName}">{displayName}</div>',
				'</tpl>' ].join("");
	}-*/;
}