package com.ca.arcserve.linuximaging.ui.client.aerp;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.login.LoginService;
import com.ca.arcserve.linuximaging.ui.client.login.LoginServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.RegistrationModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.toolbar.ToolBarPanel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;

public class EntitlementRegistrationWindow extends Window {

	public EntitlementRegistrationContainer registrationContainer;
	public static Button registerButton;
	private Button cancelButton;
	private Button helpButton;
	private EntitlementRegistrationWindow thisWindow;
	private static final LoginServiceAsync service = GWT
			.create(LoginService.class);
	private ServiceInfoModel serviceServer;

	public static ToolBarPanel toolBarPanel;

	public EntitlementRegistrationWindow(ServiceInfoModel serviceModel) {

		this.setSize("700", "550");
		this.setHeading(UIContext.Constants.registrationWindowHeader());
		this.setBorders(true);
		this.setBodyStyle("background-color: white;");
		this.setModal(true);
		this.serviceServer = serviceModel;

		thisWindow = this;

		registrationContainer = new EntitlementRegistrationContainer(this);

		thisWindow.add(registrationContainer);

		helpButton = new Button(UIContext.Constants.help());
		helpButton.setTitle(UIContext.Constants.help());
		helpButton.ensureDebugId("c0c9e655-79bd-47ff-8705-61c7460556b3");
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.LINUX_UDP_REGISTRATION);
			}
		});
		this.setButtonAlign(HorizontalAlignment.LEFT);
		this.addButton(helpButton);
		this.getButtonBar().add(new FillToolItem());

		registerButton = new Button(UIContext.Constants.sendVerificationEmail());
		registerButton.setEnabled(false);
		registerButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						if (registrationContainer.emailFld.isValid()) {
							registerButton.setEnabled(false);
							validateRegistrationDetails();
						}
					}
				});
		this.getButtonBar().add(registerButton);
		cancelButton = new Button(UIContext.Constants.close());
		cancelButton.setTitle(UIContext.Constants.close());
		cancelButton.ensureDebugId("61b7ff2c-0655-4500-bdbf-8c06961c58d9");
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				EntitlementRegistrationWindow.this.hide();
			}
		});
		this.getButtonBar().add(cancelButton);

		this.setFocusWidget(registerButton);

		this.setLayoutOnChange(true);

	}

	protected void afterShow() {
		super.afterShow();
		loadRegistrationDetails();
	}

	public void registerEntitlementDetails() {
		String name = registrationContainer.nameFld.getValue();
		String company = registrationContainer.companyFld.getValue();
		String contactNumber = registrationContainer.phoneFld.getValue();
		String emailID = registrationContainer.emailFld.getValue();
		String netSuiteId = registrationContainer.fullfillFld.getValue();
		RegistrationModel registration = new RegistrationModel();
		registration.setName(name);
		registration.setCompany(company);
		registration.setContactNumber(contactNumber);
		registration.setEmailID(emailID);
		registration.setNetSuiteId(netSuiteId);
		service.registerEntitlementDetails(serviceServer, registration,
				new BaseAsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						registerButton.setEnabled(true);
						registrationContainer.resultLabel
								.setValue(UIContext.Constants
										.registrationFailed());
						thisWindow.unmask();

					}

					@Override
					public void onSuccess(String result) {
						if (toolBarPanel != null) {
							toolBarPanel.refreshToolBarTitle();
						}
						thisWindow.unmask();
						registerButton.setEnabled(true);
						String responseMsg = "";
						if ("UDP_SERVER_ENTITLEMENT_EXISTS"
								.equalsIgnoreCase(result))
							responseMsg = UIContext.Constants
									.entitlementExist();
						else if ("REGISTRATION_SUCCESS"
								.equalsIgnoreCase(result)
								|| "REGISTRATION_SUCCESS_LINK"
										.equalsIgnoreCase(result)) {
							responseMsg = UIContext.Constants.registerSucc();
							registrationContainer.resultLabel.setVisible(true);
							registrationContainer.cancelContainer.hide();
							registrationContainer.NotConfirmContainer.show();
							registrationContainer.regIntro.hide();
							registrationContainer.formContainer.show();
							registrationContainer.buttonContainer.show();
							registrationContainer.phoneFld.disable();
							registrationContainer.emailFld.disable();
							registrationContainer.companyFld.disable();
							registrationContainer.fullfillFld.disable();
							registrationContainer.nameFld.disable();
							registerButton.setEnabled(false);
						} else if ("REGISTRATION_SUCCESS_LINK_FAILED"
								.equalsIgnoreCase(result))
							responseMsg = UIContext.Constants
									.registrationSuccessLinkFailed();
						else if ("REGISTRATION_FAILED_EXCEPTION"
								.equalsIgnoreCase(result))
							responseMsg = UIContext.Constants
									.registrationFailedException();
						else if ("REGISTRATION_FAILED_INVALIDDETAILS"
								.equalsIgnoreCase(result))
							responseMsg = UIContext.Constants
									.registrationFailedInValidateDetails();
						else if ("REGISTRATION_FAILED_URI_ERROR"
								.equalsIgnoreCase(result))
							responseMsg = UIContext.Constants
									.registrationFailedUrlError();
						else if ("REGISTRATION_FAILED_INVALIDEMAIL"
								.equalsIgnoreCase(result))
							responseMsg = UIContext.Constants
									.registrationFailedInvalidEmail();
						else if ("REGISTRATION_FAILED_INVALIDCONTACTNUMBER"
								.equalsIgnoreCase(result))
							responseMsg = UIContext.Constants
									.registrationFailedDocumentNumber();
						else {
							responseMsg = UIContext.Constants
									.registrationFailed();
						}
						registrationContainer.resultLabel.setValue("<b>"
								+ responseMsg + "</b>");
					}
				});
	}

	public void validateRegistrationDetails() {
		String name = registrationContainer.nameFld.getValue();
		String company = registrationContainer.companyFld.getValue();
		String contactNumber = registrationContainer.phoneFld.getValue();
		String emailID = registrationContainer.emailFld.getValue();
		String netSuiteId = registrationContainer.fullfillFld.getValue();
		RegistrationModel registration = new RegistrationModel();
		registration.setName(name);
		registration.setCompany(company);
		registration.setContactNumber(contactNumber);
		registration.setEmailID(emailID);
		registration.setNetSuiteId(netSuiteId);
		thisWindow.mask(UIContext.Constants.loading());
		service.validateRegistrationDetails(serviceServer, registration,
				new BaseAsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						registerButton.setEnabled(true);
						thisWindow.unmask();
					}

					@Override
					public void onSuccess(String result) {

						if (result != null && !result.isEmpty()) {
							registerButton.setEnabled(true);
							String responseMsg = "";
							if (result
									.equalsIgnoreCase("REGISTRATION_FAILED_INVALIDEMAIL")) {
								responseMsg = UIContext.Messages
										.validateEmailAddress();
							} else if (result
									.equalsIgnoreCase("REGISTRATION_FAILED_INVALIDCONTACTNUMBER")) {
								responseMsg = UIContext.Messages
										.validatePhoneNumber();
							} else if (result
									.equalsIgnoreCase("REGISTRATION_FAILED_INVALIDFULFILLMENT")) {
								responseMsg = UIContext.Messages
										.validateFullfillNumber();

							} else if (result
									.equalsIgnoreCase("REGISTRATION_FAILED_INVALIDCOMPANYNAME")) {
								responseMsg = UIContext.Messages
										.validateCompany();
							} else if (result
									.equalsIgnoreCase("REGISTRATION_FAILED_INVALIDUSERNAME")) {
								responseMsg = UIContext.Messages.validateName();
							}
							thisWindow.unmask();
							Utils.showMessage(
									UIContext.Constants.productName(),
									MessageBox.ERROR, responseMsg);
						} else {
							registerEntitlementDetails();
						}
					}
				});
	}

	public void loadRegistrationDetails() {
		thisWindow.mask(UIContext.Constants.loading());
		service.getRegistrationDetails(serviceServer,
				new BaseAsyncCallback<RegistrationModel>() {
					public void onFailure(Throwable caught) {
						thisWindow.unmask();
					}

					@Override
					public void onSuccess(RegistrationModel result) {
						thisWindow.unmask();
						if (result != null) {
							registrationContainer.nameFld.setValue(result
									.getName());
							registrationContainer.companyFld.setValue(result
									.getCompany());
							registrationContainer.phoneFld.setValue(result
									.getContactNumber());
							registrationContainer.emailFld.setValue(result
									.getEmailID());
							registrationContainer.fullfillFld.setValue(result
									.getNetSuiteId());

							if (result.getEmailID() != null) {
								registerButton.setEnabled(true);
							}
						} else {
							result = new RegistrationModel();
							result.setActivate("ISACTIVATED_NOTREGISTERED");
						}
						String responseMSG = "";
						if (result.isActivate().equalsIgnoreCase(
								"ISACTIVATED_NOTREGISTERED")
								|| result.isActivate().equalsIgnoreCase(
										"USER_CANCELLED_REGISTRATION")) {
							registrationContainer.cancelContainer.show();
						} else if (result.isActivate().equalsIgnoreCase(
								"ISACTIVATED_INACTIVE")) {
							registrationContainer.NotConfirmContainer.show();
							registrationContainer.formContainer.show();
							registrationContainer.regIntro.hide();
							registrationContainer.buttonContainer.show();
							responseMSG = UIContext.Constants.registerSucc();
						} else if (result.isActivate().equalsIgnoreCase(
								"ISACTIVATED_ACTIVE")) {
							registrationContainer.NotConfirmContainer.show();
							registrationContainer.formContainer.show();
							registrationContainer.regIntro.hide();
							registrationContainer.buttonContainer.show();
							responseMSG = UIContext.Constants
									.regActiveMessage();
						}
						if (responseMSG != "") {
							registrationContainer.resultLabel.setVisible(true);
							registrationContainer.resultLabel.setValue("<b>"
									+ responseMSG + "</b>");
						}
					}
				});
	}
}
