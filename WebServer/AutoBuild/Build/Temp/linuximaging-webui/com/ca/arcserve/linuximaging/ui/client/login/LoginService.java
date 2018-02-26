package com.ca.arcserve.linuximaging.ui.client.login;

import com.ca.arcserve.linuximaging.ui.client.exception.ClientException;
import com.ca.arcserve.linuximaging.ui.client.model.RegistrationModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {

	public String getLogonUser();

	public String validateUser(ServiceInfoModel serviceInfo, String username,
			String password) throws ClientException;

	public int validateByUUID(ServiceInfoModel serviceInfo)
			throws ClientException;

	public void logout() throws ClientException;

	public String registerEntitlementDetails(ServiceInfoModel serviceInfo,
			RegistrationModel registrationModel) throws ClientException;

	public String getNotifications(ServiceInfoModel serviceInfo)
			throws ClientException;

	public RegistrationModel getRegistrationDetails(ServiceInfoModel serviceInfo)
			throws ClientException;

	public String validateRegistrationDetails(ServiceInfoModel serviceInfo,
			RegistrationModel registrationModel) throws ClientException;
	
	public String cancelRegistration(ServiceInfoModel serviceInfo,
			RegistrationModel registrationModel) throws ClientException;
	
}
