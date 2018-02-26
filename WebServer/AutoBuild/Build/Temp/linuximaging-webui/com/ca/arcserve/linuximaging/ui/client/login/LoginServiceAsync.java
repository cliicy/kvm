package com.ca.arcserve.linuximaging.ui.client.login;

import com.ca.arcserve.linuximaging.ui.client.model.RegistrationModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {

	void getLogonUser(AsyncCallback<String> callback);

	void validateUser(ServiceInfoModel serviceInfo, String username,
			String password, AsyncCallback<String> callback);

	void logout(AsyncCallback<Void> callback);

	void validateByUUID(ServiceInfoModel serviceInfo,
			AsyncCallback<Integer> callback);

	void registerEntitlementDetails(ServiceInfoModel serviceInfo,
			RegistrationModel registrationModel, AsyncCallback<String> callback);

	void validateRegistrationDetails(ServiceInfoModel serviceInfo,
			RegistrationModel registrationModel, AsyncCallback<String> callback);

	void getRegistrationDetails(ServiceInfoModel serviceInfo,
			AsyncCallback<RegistrationModel> callback);

	void getNotifications(ServiceInfoModel serviceInfo,
			AsyncCallback<String> callback);

	void cancelRegistration(ServiceInfoModel serviceInfo,
			RegistrationModel registrationModel, AsyncCallback<String> callback);

}
