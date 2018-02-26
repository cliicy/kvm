package com.ca.arcserve.linuximaging.ui.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;

import com.ca.arcserve.linuximaging.ui.client.exception.ClientException;
import com.ca.arcserve.linuximaging.ui.client.login.LoginService;
import com.ca.arcserve.linuximaging.ui.client.model.RegistrationModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.server.servlet.ContextListener;
import com.ca.arcserve.linuximaging.ui.server.servlet.SessionConstants;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceFactory;
import com.ca.arcserve.linuximaging.webservice.data.LoginInfo;
import com.ca.arcserve.linuximaging.webservice.data.Registration;

public class LoginServiceImpl extends BaseServiceImpl implements LoginService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2991961864247843015L;

	@Override
	public String getLogonUser() {
		HttpSession session = getThreadLocalRequest().getSession(true);
		String userName = (String) session
				.getAttribute(SessionConstants.SERVICE_USERNAME);
		return userName;
	}

	@Override
	public String validateUser(ServiceInfoModel serviceInfo, String username,
			String password) throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setUsername(username);
			loginInfo.setPassword(password);
			loginInfo.setClientIP(getThreadLocalRequest().getRemoteAddr());
			String uuid = client.login(loginInfo);
			if (uuid != null) {
				getThreadLocalRequest().getSession(true).setAttribute(
						SessionConstants.SERVICE_USERNAME, username);
			}
			return uuid;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public void logout() throws ClientException {
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		session.invalidate();
	}

	@Override
	public int validateByUUID(ServiceInfoModel serviceInfo)
			throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			int ret = client.validateByKey(serviceInfo.getAuthKey());
			return ret;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return -1;
	}

	private RegistrationModel convertToRegistrationModel(
			Registration registration) {
		if (registration == null)
			return null;
		RegistrationModel registrationModel = new RegistrationModel();
		registrationModel.setName(registration.getName());
		registrationModel.setEmailID(registration.getEmailID());
		registrationModel.setCompany(registration.getCompany());
		registrationModel.setContactNumber(registration.getContactNumber());
		registrationModel.setNetSuiteId(registration.getNetSuiteId());
		registrationModel.setActivate(registration.isActivate());
		return registrationModel;
	}

	private Registration convertToRegistration(
			RegistrationModel registrationModel) {
		if (registrationModel == null)
			return null;
		Registration registration = new Registration();
		registration.setName(registrationModel.getName());
		registration.setEmailID(registrationModel.getEmailID());
		registration.setContactNumber(registrationModel.getContactNumber());
		registration.setNetSuiteId(registrationModel.getNetSuiteId());
		registration.setCompany(registrationModel.getCompany());
		registration.setActivate(registrationModel.isActivate());
		return registration;
	}

	public int internalValidateByUUID(String uuid, HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		ServiceInfoModel serviceInfo = new ServiceInfoModel();
		serviceInfo.setServer("127.0.0.1");
		if (url != null && url.contains("https")) {
			serviceInfo.setProtocol("https");
		} else {
			serviceInfo.setProtocol("http");
		}
		serviceInfo.setPort(ContextListener.webServicePort);
		BaseWebServiceClientProxy proxy = new BaseWebServiceFactory()
				.getLinuxImagingWebService(serviceInfo.getProtocol(),
						serviceInfo.getServer(), serviceInfo.getPort());
		String wsdlURL = getWsdlUrl(serviceInfo.getProtocol(),
				serviceInfo.getServer(), serviceInfo.getPort());
		HttpSession session = request.getSession();
		session.setAttribute(wsdlURL, proxy);
		ILinuximagingService client = (ILinuximagingService) proxy.getService();

		int ret = client.validateByKey(uuid);
		return ret;
	}

	@Override
	public String registerEntitlementDetails(ServiceInfoModel serviceInfo,
			RegistrationModel registrationModel) throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			String result = client
					.registerEntitlementDetails(convertToRegistration(registrationModel));
			return result;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public String validateRegistrationDetails(ServiceInfoModel serviceInfo,
			RegistrationModel registrationModel) throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			String ret = client
					.validateRegistrationDetails(convertToRegistration(registrationModel));
			return ret;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}
	
	@Override
	public String cancelRegistration(ServiceInfoModel serviceInfo,
			RegistrationModel registrationModel) throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			String result = client
					.cancelRegistration(convertToRegistration(registrationModel));
			return result;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public RegistrationModel getRegistrationDetails(ServiceInfoModel serviceInfo)
			throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			Registration ret = client.getRegistrationDetails();
			return convertToRegistrationModel(ret);
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}

	@Override
	public String getNotifications(ServiceInfoModel serviceInfo)
			throws ClientException {
		try {
			ILinuximagingService client = getClient(serviceInfo);
			String ret = client.getNotifications();
			return ret;
		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return null;
	}
}
