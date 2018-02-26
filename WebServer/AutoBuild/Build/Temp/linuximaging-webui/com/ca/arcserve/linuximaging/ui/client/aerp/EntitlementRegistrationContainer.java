package com.ca.arcserve.linuximaging.ui.client.aerp;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.login.LoginService;
import com.ca.arcserve.linuximaging.ui.client.login.LoginServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.RegistrationModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

public class EntitlementRegistrationContainer extends LayoutContainer {

	private int MAX_Field_WIDTH = 200;
	public LabelField resultLabel = new LabelField();

	public CheckBox radio = new CheckBox();
	public TextField<String> nameFld = new TextField<String>();
	public TextField<String> companyFld = new TextField<String>();
	public TextField<String> phoneFld = new TextField<String>();
	public TextField<String> emailFld = new TextField<String>();
	public TextField<String> fullfillFld = new TextField<String>();
	public Button button = new Button(
			UIContext.Constants.cancelParticipathion());

	public LayoutContainer cancelContainer = new LayoutContainer();

	public LayoutContainer formContainer = new LayoutContainer();

	public LayoutContainer NotConfirmContainer = new LayoutContainer();

	public LayoutContainer buttonContainer = new LayoutContainer();

	public LabelField regIntro;

	private EntitlementRegistrationWindow parentWindow;

	private static final LoginServiceAsync service = GWT
			.create(LoginService.class);

	private SelectionListener<ButtonEvent> refeshListener = new SelectionListener<ButtonEvent>() {
		@Override
		public void componentSelected(ButtonEvent ce) {
			button.setEnabled(false);
			cancelRegistration();

		}
	};

	LabelField policy = new LabelField(UIContext.Messages.PrivacyAndEUmodel(
			UIContext.helpLink + HelpLinkItem.UDP_PRIVACY_POLICY,
			UIContext.helpLink + HelpLinkItem.UDP_EUModel_CLAUSE));

	public EntitlementRegistrationContainer(
			EntitlementRegistrationWindow parentWindow) {
		this.parentWindow = parentWindow;
		TableLayout form = new TableLayout();
		form.setWidth("100%");
		form.setCellSpacing(0);
		form.setCellPadding(4);
		setLayout(form);
		buildFormContainerLaoyout();
		buildCancelContainerLayout();
		buildNotConfirmContainerLayout();
		buildButtonContainerLayout();

		add(resultLabel);

		cancelContainer.hide();
		NotConfirmContainer.hide();
		formContainer.hide();
		buttonContainer.hide();
		add(cancelContainer);
		add(NotConfirmContainer);
		add(formContainer);
		add(buttonContainer);

		this.doLayout();
	}

	public void cancelRegistration() {
		String name = nameFld.getValue();
		String company = companyFld.getValue();
		String contactNumber = phoneFld.getValue();
		String emailID = emailFld.getValue();
		String netSuiteId = fullfillFld.getValue();
		RegistrationModel registration = new RegistrationModel();
		registration.setName(name);
		registration.setCompany(company);
		registration.setContactNumber(contactNumber);
		registration.setEmailID(emailID);
		registration.setNetSuiteId(netSuiteId);
		parentWindow.mask(UIContext.Constants.jobPhase_cancelJob());
		service.cancelRegistration(null, registration,
				new BaseAsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						button.setEnabled(true);
						parentWindow.unmask();
					}

					@Override
					public void onSuccess(String result) {
						parentWindow.unmask();
						if ("CANCELREGISTRATION_SUCCESS"
								.equalsIgnoreCase(result)) {
							resultLabel.setValue("<B>"
									+ UIContext.Constants.getCancelRegSuccess()
									+ "</B>");
							NotConfirmContainer.hide();
							formContainer.hide();
							NotConfirmContainer.hide();
							buttonContainer.hide();
							cancelContainer.show();
							regIntro.show();
							radio.setValue(false);
							button.setEnabled(true);
							nameFld.clear();
							companyFld.clear();
							phoneFld.clear();
							emailFld.clear();
							fullfillFld.clear();
							if (parentWindow.toolBarPanel != null) {
								parentWindow.toolBarPanel.refreshToolBarTitle();
							}
						} else if ("CANCELREGISTRATION_INVALID_USER"
								.equalsIgnoreCase(result)
								|| "CANCELREGISTRATION_FAILED_EXCEPTION"
										.equalsIgnoreCase(result)) {
							resultLabel.setValue("<B>"
									+ UIContext.Constants.getCancelRegFailure()
									+ "</B>");
						}

					}
				});
	}

	public void buildFormContainerLaoyout() {

		regIntro = new LabelField(UIContext.Constants.regIntroduction());
		LabelField warningLabel = new LabelField(
				UIContext.Constants.warningLabel());
		LabelField nameLabel = new LabelField(UIContext.Constants.name());
		LabelField companyLabel = new LabelField(UIContext.Constants.company());
		LabelField fullfillLable = new LabelField(
				UIContext.Constants.fullfillNumber());
		LabelField phoneLabel = new LabelField(
				UIContext.Constants.phoneNumber());
		LabelField emailLabel = new LabelField(
				UIContext.Constants.emailAddress());

		TableLayout form = new TableLayout();
		form.setColumns(3);
		form.setWidth("100%");
		form.setCellSpacing(2);
		form.setCellPadding(4);
		formContainer.setLayout(form);
		formContainer.setStyleAttribute("padding-left", "20px");
		TableData td = new TableData();
		TableData td1 = new TableData();
		TableData td3 = new TableData();
		TableData td4 = new TableData();
		td4.setColspan(3);
		td4.setPadding(4);
		td3.setWidth("5%");
		td3.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setWidth("20%");
		td1.setWidth("75%");

		nameFld.setWidth(MAX_Field_WIDTH);
		formContainer.add(regIntro, td4);
		formContainer.add(warningLabel, td4);

		formContainer.add(new LabelField(""), td3);
		formContainer.add(nameLabel, td);
		formContainer.add(nameFld, td1);

		companyFld.setWidth(MAX_Field_WIDTH);

		formContainer.add(new LabelField(""), td3);
		formContainer.add(companyLabel, td);
		formContainer.add(companyFld, td1);

		phoneFld.setWidth(MAX_Field_WIDTH);

		formContainer.add(new LabelField(""), td3);
		formContainer.add(phoneLabel, td);
		formContainer.add(phoneFld, td1);

		emailFld.setWidth(MAX_Field_WIDTH);
		emailFld.setAllowBlank(false);
		emailFld.getMessages().setBlankText(
				UIContext.Messages.validateEmailAddress());
		emailFld.addKeyListener(new KeyListener() {
			@Override
			public void componentKeyUp(ComponentEvent event) {
				if (emailFld.getValue() == null) {
					EntitlementRegistrationWindow.registerButton
							.setEnabled(false);
				} else {
					EntitlementRegistrationWindow.registerButton
							.setEnabled(true);
				}
			}
		});
		formContainer.add(new LabelField("*"), td3);
		formContainer.add(emailLabel, td);
		formContainer.add(emailFld, td1);

		fullfillFld.setWidth(MAX_Field_WIDTH);

		Image image = new Image();
		image.setResource(UIContext.IconBundle.download());
		image.setTitle(UIContext.Constants.getFulFillmentToolTip());
		formContainer.add(image, td3);
		HorizontalPanel fullfill = new HorizontalPanel();
		// fullfill.add(image);
		fullfill.add(fullfillLable);
		formContainer.add(fullfill, td);
		formContainer.add(fullfillFld, td1);
	}

	public void buildCancelContainerLayout() {
		LabelField headerLabel = new LabelField(
				UIContext.Constants.getRegistrationHeader());
		LabelField messageOne = new LabelField(
				UIContext.Messages.getRegisterPolicyLabel(UIContext.helpLink
						+ HelpLinkItem.UDP_PRIVACY_POLICY));
		messageOne.setStyleAttribute("padding-left", "20px");
		LabelField messageTwo = new LabelField(
				UIContext.Messages.getRegAgreementLabel(UIContext.helpLink
						+ HelpLinkItem.UDP_EUModel_CLAUSE));
		messageTwo.setStyleAttribute("padding-left", "20px");
		LabelField messageThree = new LabelField(
				UIContext.Constants.getEUModuleClauseLabel());
		messageThree.setStyleAttribute("padding-left", "20px");
		TableLayout cancelTable = new TableLayout();
		cancelTable.setWidth("100%");
		cancelTable.setCellSpacing(1);
		cancelTable.setCellPadding(4);
		cancelContainer.setLayout(cancelTable);
		cancelContainer.add(headerLabel);
		cancelContainer.add(messageOne);
		cancelContainer.add(messageTwo);
		cancelContainer.add(messageThree);
		radio.setBoxLabel(UIContext.Constants.getParticipateRegLabel());
		cancelContainer.add(radio);
		radio.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				if (radio.getValue()) {
					resultLabel.setVisible(false);
					formContainer.show();
				} else {
					formContainer.hide();
				}
			}

		});
	}

	public void buildNotConfirmContainerLayout() {

		LabelField confirmLabel = new LabelField(
				UIContext.Constants.usageStatistics());
		LabelField confirmLabe2 = new LabelField(
				UIContext.Constants.cancelRegist());
		TableLayout cancelTable = new TableLayout();
		cancelTable.setWidth("100%");
		cancelTable.setCellSpacing(1);
		cancelTable.setCellPadding(4);
		NotConfirmContainer.setLayout(cancelTable);
		NotConfirmContainer.add(confirmLabel);
		NotConfirmContainer.add(confirmLabe2);

	}

	public void buildButtonContainerLayout() {
		TableLayout cancelTable = new TableLayout();
		cancelTable.setWidth("100%");
		cancelTable.setCellSpacing(1);
		cancelTable.setCellPadding(4);
		button.addSelectionListener(refeshListener);
		buttonContainer.setLayout(cancelTable);
		buttonContainer.add(policy);
		buttonContainer.add(button);
	}
}