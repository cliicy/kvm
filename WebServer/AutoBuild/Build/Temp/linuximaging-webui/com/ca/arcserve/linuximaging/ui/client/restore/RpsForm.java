package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.PasswordField;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.RpsDataStoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class RpsForm extends LayoutContainer {

	private int MAX_Field_WIDTH = 200;

	private final LabelField hostNameLabel = new LabelField(
			UIContext.Constants.rpsHostName());
	private final LabelField userNameLabel = new LabelField(
			UIContext.Constants.userName());
	private final LabelField passwordLabel = new LabelField(
			UIContext.Constants.password());
	private final LabelField portLabel = new LabelField(
			UIContext.Constants.port());
	private final LabelField protocolLabel = new LabelField(
			UIContext.Constants.protocol());
	private final LabelField datastoreLabel = new LabelField(
			UIContext.Constants.dataStore());

	private final ComboBox<ServerInfoModel> hostName = new ComboBox<ServerInfoModel>();
	private final TextField<String> userName = new TextField<String>();
	private final PasswordField password = new PasswordField(MAX_Field_WIDTH);
	private final NumberField port = new NumberField();
	private final Radio https = new Radio();
	private final Radio http = new Radio();
	private final RadioGroup protocol = new RadioGroup();
	private final ComboBox<RpsDataStoreModel> dataStoreBox = new ComboBox<RpsDataStoreModel>();
	private final ServerInfoModel serverInfoModel = new ServerInfoModel();
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	Button refresh = new Button(UIContext.Constants.load());
	private ServiceInfoModel wbService;

	private ListStore<ServerInfoModel> serverInfoStore = new ListStore<ServerInfoModel>();
	private ListStore<RpsDataStoreModel> rpsDataStore = new ListStore<RpsDataStoreModel>();

	TableData td = new TableData();
	TableData td1 = new TableData();

	private SelectionListener<ButtonEvent> refeshListener = new SelectionListener<ButtonEvent>() {
		@Override
		public void componentSelected(ButtonEvent ce) {

			if (hostName.isValid() && userName.isValid() && password.isValid()
					&& port.isValid()) {
				String hn = hostName.getRawValue();
				String un = userName.getValue();
				String pw = password.getPasswordValue();
				Integer pt = port.getValue().intValue();
				String pl = protocol.getValue().getValueAttribute();
				serverInfoModel.setServerName(hn);
				serverInfoModel.setUserName(un);
				serverInfoModel.setPasswd(pw);
				serverInfoModel.setPort(pt);
				serverInfoModel.setProtocol(pl);
				loadRpsDatastore();
			}
		}
	};

	SelectionChangedListener<ServerInfoModel> changeListener = new SelectionChangedListener<ServerInfoModel>() {

		@Override
		public void selectionChanged(SelectionChangedEvent<ServerInfoModel> se) {
			rpsDataStore.removeAll();
			dataStoreBox.clear();
			ServerInfoModel selected = se.getSelectedItem();
			hostName.setValue(selected);
			userName.setValue(selected.getUserName());
			password.setPasswordValue(selected.getPasswd());
			port.setValue(selected.getPort());
			if ("http".equalsIgnoreCase(selected.getProtocol())) {
				protocol.setValue(http);
			} else {
				protocol.setValue(https);
			}
		}
	};

	public RpsForm(ServiceInfoModel servicInfo) {
		TableLayout form = new TableLayout();
		form.setColumns(2);
		form.setWidth("100%");
		form.setCellSpacing(6);
		setLayout(form);
		td.setWidth("20%");
		td1.setWidth("80%");
		// td2.setWidth("15%");
		this.wbService = servicInfo;

		hostName.setFieldLabel(UIContext.Constants.hostName());
		hostName.setWidth(MAX_Field_WIDTH);
		hostName.setTypeAhead(true);
		hostName.setEditable(true);
		hostName.setAllowBlank(false);
		hostName.setTriggerAction(TriggerAction.ALL);
		hostName.getMessages().setBlankText(
				UIContext.Messages.validateHostName());
		hostName.setDisplayField("server");
		hostName.setStore(serverInfoStore);

		add(hostNameLabel, td);
		add(hostName, td1);

		userName.setFieldLabel(UIContext.Constants.userName());
		userName.setWidth(MAX_Field_WIDTH);
		userName.setAllowBlank(false);
		userName.getMessages().setBlankText(
				UIContext.Messages.validateUserName());

		add(userNameLabel, td);
		add(userName, td1);

		password.setFieldLabel(UIContext.Constants.password());
		password.setAllowBlank(false);
		password.setPassword(true);
		password.getMessages().setBlankText(
				UIContext.Messages.validatePassWord());

		add(passwordLabel, td);
		add(password, td1);

		port.setFieldLabel(UIContext.Constants.port());
		port.setValue(8014);
		port.setMinValue(0);
		port.setMaxValue(65535);
		port.setWidth(MAX_Field_WIDTH);
		port.setAllowBlank(false);
		port.getMessages().setBlankText(UIContext.Messages.validatePort());
		port.getMessages().setMinText(UIContext.Messages.validatePortNumber());
		port.getMessages().setMaxText(UIContext.Messages.validatePortNumber());
		add(portLabel, td);
		add(port, td1);

		https.setBoxLabel(UIContext.Constants.targetServerProtocol_Https());
		https.setValue(true);
		https.setValueAttribute("https");

		http.setBoxLabel(UIContext.Constants.targetServerProtocol_Http());
		http.setValue(true);
		http.setValueAttribute("Http");

		protocol.setFieldLabel(UIContext.Constants.protocol());
		protocol.add(http);
		protocol.add(https);
		protocol.setWidth(MAX_Field_WIDTH);

		add(protocolLabel, td);
		add(protocol, td1);
		add(datastoreLabel, td);
		add(getDataStore(), td1);
	}

	private LayoutContainer getDataStore() {
		LayoutContainer lc = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		lc.setLayout(tl);

		dataStoreBox.setFieldLabel(UIContext.Constants.dataStore());
		dataStoreBox.getMessages().setBlankText(
				UIContext.Messages.validateDataStore());
		dataStoreBox.setTypeAhead(true);
		dataStoreBox.setEditable(false);
		dataStoreBox.setTriggerAction(TriggerAction.ALL);
		dataStoreBox.setStore(rpsDataStore);
		dataStoreBox.setWidth(145);
		dataStoreBox.setDisplayField("storeSharedName");
		refresh.setIcon(AbstractImagePrototype.create(UIContext.IconBundle
				.connect()));
		lc.add(dataStoreBox, td);
		lc.add(refresh, td1);
		return lc;
	}

	private void loadRpsDatastore() {
		service.findRpsDataStoreByServerInfo(wbService, serverInfoModel,
				new BaseAsyncCallback<List<RpsDataStoreModel>>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(List<RpsDataStoreModel> result) {
						rpsDataStore.removeAll();
						dataStoreBox.clear();
						for (RpsDataStoreModel rpsDataStoreModel : result) {
							rpsDataStore.add(rpsDataStoreModel);
						}
						if (result.size() > 0) {
							dataStoreBox.setValue(result.get(0));
						}
					}
				});
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		hostName.addSelectionChangedListener(changeListener);
		refresh.addSelectionListener(refeshListener);
	}

	public ComboBox<ServerInfoModel> getHostName() {
		return hostName;
	}

	public ServerInfoModel getServerInfoModel() {
		return serverInfoModel;
	}

	public ComboBox<RpsDataStoreModel> getDataStoreBox() {
		return dataStoreBox;
	}

}
