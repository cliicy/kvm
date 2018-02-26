package com.ca.arcserve.linuximaging.ui.client.common;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class ServerInfoContainer extends LayoutContainer {

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
	private final ComboBox<ServerInfoModel> hostName = new ComboBox<ServerInfoModel>();
	private final TextField<String> userName = new TextField<String>();
	private final PasswordField password = new PasswordField(MAX_Field_WIDTH);
	private final NumberField port = new NumberField();
	private final Radio https = new Radio();
	private final Radio http = new Radio();
	private final RadioGroup protocol = new RadioGroup();

	private ListStore<ServerInfoModel> serverInfoStore = new ListStore<ServerInfoModel>();

	TableData td = new TableData();
	TableData td1 = new TableData();

	public ServerInfoContainer() {
		TableLayout form = new TableLayout();
		form.setColumns(2);
		form.setWidth("100%");
		form.setCellSpacing(6);
		setLayout(form);
		td.setWidth("20%");
		td1.setWidth("80%");
		// td2.setWidth("15%");

		hostName.setFieldLabel(UIContext.Constants.hostName());
		hostName.setWidth(MAX_Field_WIDTH);
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
	}

	public boolean isValid() {
		if (hostName.isValid() && userName.isValid() && password.isValid()
				&& port.isValid()) {
			return true;
		} else {
			return false;
		}
	}

	public ComboBox<ServerInfoModel> getHostName() {
		return hostName;
	}

	public TextField<String> getUserName() {
		return userName;
	}

	public PasswordField getPassword() {
		return password;
	}

	public RadioGroup getProtocol() {
		return protocol;
	}

	public NumberField getPort() {
		return port;
	}

	public Radio getHttps() {
		return https;
	}

	public Radio getHttp() {
		return http;
	}
}
