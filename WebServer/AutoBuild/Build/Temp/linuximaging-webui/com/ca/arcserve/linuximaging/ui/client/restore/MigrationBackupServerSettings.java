package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.BaseComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.PasswordField;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.tree.NodeServerDialog;
import com.ca.arcserve.linuximaging.ui.client.model.JobScriptModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MigrationBackupServerSettings extends BackupServerSettings {

	private static final int FIELD_WIDTH = 200;
	private Radio localServer;
	private Radio remoteServer;
	private TextField<String> hostname;
	private TextField<String> username;
	private PasswordField password;
	private Radio http;
	private Radio https;
	private RadioGroup radioGroup;
	private RadioGroup rg;
	private NumberField port;
	private BaseComboBox<JobStatusModel> cmbJobName;
	private Button refreshButton;
	private ListStore<JobStatusModel> jobNameStore;
	private LayoutContainer container;
	private String confirmJobUUID;
	private static final HomepageServiceAsync service = GWT
			.create(HomepageService.class);
	private String jobLoaded = null;

	public MigrationBackupServerSettings(RestoreWindow window) {
		super(window);
		this.setHeight(RestoreWindow.RIGHT_PANEL_HIGHT + 5);
		this.setWidth(RestoreWindow.RIGHT_PANEL_WIDTH - 20);
		this.setScrollMode(Scroll.AUTOY);
	}

	protected void defineSpecialSettings() {
		LabelField head = new LabelField(
				UIContext.Constants.migrationBmrSourceDescription());
		head.setStyleAttribute("font-weight", "bold");

		TableData tdColspan = new TableData();
		tdColspan.setColspan(2);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);
		add(head, tdColspan);

		TableData tdLabel = new TableData();
		tdLabel.setWidth("20%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("80%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField label = new LabelField(UIContext.Constants.serverType());
		localServer = new Radio();
		localServer.setValue(true);
		localServer.setBoxLabel(UIContext.Constants.localServer());

		remoteServer = new Radio();
		remoteServer.setValue(true);
		remoteServer.setBoxLabel(UIContext.Constants.remoteServer());

		radioGroup = new RadioGroup();
		radioGroup.add(localServer);
		radioGroup.add(remoteServer);
		radioGroup.setValue(localServer);
		radioGroup.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				if (radioGroup.getValue() == localServer) {
					container.setVisible(false);
				} else {
					container.setVisible(true);
				}
				hostname.clearInvalid();
				username.clearInvalid();
				password.clearInvalid();
				cmbJobName.getStore().removeAll();
				cmbJobName.setValue(null);
				cmbJobName.clearInvalid();
			}

		});
		add(label, tdLabel);
		add(radioGroup, tdField);

		container = new LayoutContainer();
		TableLayout layout1 = new TableLayout();
		layout1.setWidth("100%");
		layout1.setColumns(2);
		container.setLayout(layout1);

		label = new LabelField(UIContext.Constants.hostName());
		label.setStyleAttribute("padding", "0px 5px 5px 0px");
		hostname = new TextField<String>();
		hostname.setWidth(FIELD_WIDTH);
		hostname.setAllowBlank(false);
		hostname.setStyleAttribute("padding", "0px 5px 5px 5px");
		container.add(label, tdLabel);
		container.add(hostname, tdField);

		label = new LabelField(UIContext.Constants.userName());
		label.setStyleAttribute("padding", "5px 5px 5px 0px");
		username = new TextField<String>();
		username.setWidth(FIELD_WIDTH);
		username.setAllowBlank(false);
		username.setStyleAttribute("padding", "5px");
		container.add(label, tdLabel);
		container.add(username, tdField);

		label = new LabelField(UIContext.Constants.password());
		label.setStyleAttribute("padding", "5px 5px 5px 0px");
		password = new PasswordField(FIELD_WIDTH);
		password.setAllowBlank(false);
		password.setStyleAttribute("padding", "5px");
		container.add(label, tdLabel);
		container.add(password, tdField);

		label = new LabelField(UIContext.Constants.protocol());
		label.setStyleAttribute("padding", "5px 5px 5px 0px");
		http = new Radio();
		http.setBoxLabel(UIContext.Constants.targetServerProtocol_Http());
		https = new Radio();
		https.setBoxLabel(UIContext.Constants.targetServerProtocol_Https());
		rg = new RadioGroup();
		rg.add(http);
		rg.add(https);
		rg.setValue(https);
		rg.setStyleAttribute("padding", "5px");
		container.add(label, tdLabel);
		container.add(rg, tdField);
		add(container, tdColspan);
		container.hide();
		label = new LabelField(UIContext.Constants.port());
		label.setStyleAttribute("padding", "5px 5px 5px 0px");
		port = new NumberField();
		port.setAllowBlank(false);
		port.setValue(8014);
		port.setWidth(50);
		port.setStyleAttribute("padding", "5px");
		container.add(label, tdLabel);
		container.add(port, tdField);

		label = new LabelField(UIContext.Constants.instantVMRecoveryJob());
		cmbJobName = new BaseComboBox<JobStatusModel>();
		cmbJobName.setWidth(FIELD_WIDTH);
		cmbJobName.setDisplayField("jobName");
		cmbJobName.setEditable(false);
		cmbJobName.setTemplate(getTemplate());
		cmbJobName.setAllowBlank(false);
		jobNameStore = new ListStore<JobStatusModel>();
		cmbJobName.setStore(jobNameStore);

		HorizontalPanel panel = new HorizontalPanel();
		panel.add(cmbJobName);

		refreshButton = new Button(UIContext.Constants.refreshBackupMachine());
		refreshButton.setMinWidth(50);
		refreshButton.setStyleAttribute("padding-left", "20px");
		refreshButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						if (validateServerInfo())
							loadRestoreJob();
					}

				});

		panel.add(refreshButton);
		add(label, tdLabel);
		add(panel, tdField);
		if (!restoreWindow.isModify) {
			loadRestoreJob();
		}
	}

	private void setRemoteServerVisable(boolean visable) {

	}

	private native String getTemplate() /*-{
		return [
				'<tpl for=".">',
				'<div class="x-combo-list-item" qtip="{jobName}" >{jobName}</div>',
				'</tpl>' ].join("");
	}-*/;

	public boolean validate() {
		boolean isValid = true;
		isValid = validateServerInfo();
		if (isValid) {
			isValid = cmbJobName.validate();
		}
		if (isValid) {
			isValid = super.validate();
		}
		if(cmbJobName.getValue().getJobMethod() == JobScriptModel.JOB_METHOD_INSTANT_BMR_WITH_AUTO_RESTORE){
			if(!cmbJobName.getValue().getJobUuid().equals(confirmJobUUID)){
				SelectionListener<ButtonEvent> handler = new SelectionListener<ButtonEvent>(){
					@Override
					public void componentSelected(ButtonEvent ce) {
						confirmJobUUID = cmbJobName.getValue().getJobUuid();
						restoreWindow.showNextSettings(false);
					}};
				SelectionListener<ButtonEvent> chandler = new SelectionListener<ButtonEvent>(){
						@Override
						public void componentSelected(ButtonEvent ce) {
							ce.cancelBubble();
						}};
				Utils.showMessage(UIContext.Constants.productName(),MessageBox.WARNING, UIContext.Constants.migrationWarning(), handler, chandler);
				isValid = false;
			}
		}
		return isValid;
	}

	public void validateAsync(final BaseAsyncCallback<Boolean> callBack){
		/*String jobUUID = restoreWindow.isModify == true ? restoreWindow.restoreModel
				.getUuid() : null;
		service.querySshKeyInfoForCloudServer(restoreWindow.currentServer,getServerInfoModel(), jobUUID, new BaseAsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(Integer result) {
				if (result != null) {
					if(result == 0){
						callBack.onSuccess(true);
					}else{
						final SshInfoUpload sshInfoUpload = new SshInfoUpload(hostname.getValue(),restoreWindow.currentServer);
						sshInfoUpload.addWindowListener(new WindowListener(){
							public void windowHide(WindowEvent we) {
								if (sshInfoUpload.isCancelled() == false) {
									callBack.onSuccess(true);
								}else{
									callBack.onSuccess(false);
								}
							}
						});
						sshInfoUpload.show();
					}
				}
			}
		});*/
		
		callBack.onSuccess(true);
	}
	
	private boolean validateServerInfo() {
		if (!localServer.getValue()) {
			return hostname.validate() && username.validate()
					&& password.validate() && port.validate();
		} else {
			return true;
		}
	}

	public void save() {
		super.save();
		restoreWindow.restoreModel.setLinuxD2DServer(getServerInfoModel());
		restoreWindow.restoreModel.setAttachedRestoreJobUUID(cmbJobName
				.getValue().getJobUuid());
		restoreWindow.restoreModel.setAttachedRestoreType(RestoreType
				.parse(cmbJobName.getValue().getJobType()));
		if (jobLoaded == null
				|| !cmbJobName.getValue().getJobUuid().equals(jobLoaded)) {
			loadSourceNodeJobInfo();
		}
	}

	public void refreshData() {
		super.refreshData();
		if (restoreWindow.isModify) {
			if (!restoreWindow.restoreModel.getLinuxD2DServer().getIsLocal()) {
				radioGroup.setValue(remoteServer);
				hostname.setValue(restoreWindow.restoreModel
						.getLinuxD2DServer().getServerName());
				username.setValue(restoreWindow.restoreModel
						.getLinuxD2DServer().getUserName());
				password.setPasswordValue(restoreWindow.restoreModel
						.getLinuxD2DServer().getPasswd());
				port.setValue(restoreWindow.restoreModel.getLinuxD2DServer()
						.getPort());
				if (restoreWindow.restoreModel.getLinuxD2DServer()
						.getProtocol().equals(NodeServerDialog.HTTP)) {
					rg.setValue(http);
				} else {
					rg.setValue(https);
				}
			}
			jobLoaded = restoreWindow.restoreModel.getAttachedRestoreJobUUID();
			loadRestoreJob();
		}

	}

	private void loadRestoreJob() {
		mask(UIContext.Constants.loading());
		String jobUUID = restoreWindow.isModify == true ? restoreWindow.restoreModel
				.getUuid() : null;
		service.getRestoreJobScriptList(restoreWindow.currentServer,
				getServerInfoModel(), jobUUID,
				new BaseAsyncCallback<List<JobStatusModel>>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						cmbJobName.getStore().removeAll();
						cmbJobName.setValue(null);
						cmbJobName.clearInvalid();
						unmask();
					}

					@Override
					public void onSuccess(List<JobStatusModel> result) {
						if (result != null) {
							jobNameStore.removeAll();
							jobNameStore.add(result);
							if (restoreWindow.isModify) {
								for (JobStatusModel job : result) {
									if (job.getJobUuid()
											.equals(restoreWindow.restoreModel
													.getAttachedRestoreJobUUID())) {
										cmbJobName.setValue(job);
										break;
									}
								}
								if (cmbJobName.getValue() == null) {
									if (result.size() > 0) {
										cmbJobName.setValue(result.get(0));
									}
								}
							} else {
								if (result.size() > 0) {
									cmbJobName.setValue(result.get(0));
								}
							}
						}
						unmask();
					}
				});
	}

	private ServerInfoModel getServerInfoModel() {
		ServerInfoModel serviceInfo = new ServerInfoModel();
		serviceInfo.setServerName(hostname.getValue());
		serviceInfo.setUserName(username.getValue());
		serviceInfo.setPasswd(password.getPasswordValue());
		serviceInfo.setPort(port.getValue().intValue());
		serviceInfo.setProtocol(rg.getValue() == http ? NodeServerDialog.HTTP
				: NodeServerDialog.HTTPS);
		serviceInfo.setLocal(radioGroup.getValue() == localServer);
		return serviceInfo;
	}

	private void loadSourceNodeJobInfo() {
		if (cmbJobName.getValue() != null) {
			String jobUUID = restoreWindow.isModify == true ? restoreWindow.restoreModel
					.getUuid() : null;
			final ServerInfoModel serviceInfo = getServerInfoModel();
			service.getRestoreModelByUUID(restoreWindow.currentServer,
					serviceInfo, cmbJobName.getValue().getJobUuid(),
					jobUUID, new BaseAsyncCallback<RestoreModel>() {
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
						}

						@Override
						public void onSuccess(RestoreModel result) {
							if (result != null) {
								result.getRecoveryPoint()
										.setEncryptionPassword("");
								restoreWindow.recoveryPointSettings
										.refreshLocationForMigrationBMR(result);
								restoreWindow.targetMachineSettings
										.refreshNetworkSettings(result);
								jobLoaded = result.getUuid();
							}
						}
					});
		}
	}
}
