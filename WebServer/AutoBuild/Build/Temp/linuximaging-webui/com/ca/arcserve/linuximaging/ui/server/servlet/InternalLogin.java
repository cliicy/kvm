package com.ca.arcserve.linuximaging.ui.server.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ShareFolderModel;
import com.ca.arcserve.linuximaging.ui.server.LoginServiceImpl;
import com.ca.arcserve.linuximaging.util.StringUtil;

public class InternalLogin extends HttpServlet {

	private static final Logger logger = Logger.getLogger(InternalLogin.class);
	public static final String LOCATION = "location";
	public static final String UUID = "uuid";
	public static final String NODENAME = "nodename";
	public static final String HOSTNAME = "hostname";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "key";
	public static final String PROTOCOL = "protocol";
	public static final String PORT = "port";
	public static final String DATASTORENAME = "datastorename";
	public static final String SHARE_PATH = "shareFoldPath";
	public static final String SHARE_USER = "shareFoldUser";
	public static final String SHARE_KEY = "shareFoldkey";
	public static final String IS_NFS = "isNfs";
	public static final String LOCATION_VALUE_RESTORE = "restore";
	public static final String LOCAL_SERVER = "localserver";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1667717170200709390L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InternalLogin() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		login(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		login(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	private void login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String location = request.getParameter(LOCATION);
		String redirect = "/ARCserveLinuxImagingUI.html";
		if (StringUtil.isEmptyOrNull(location)
				|| !LOCATION_VALUE_RESTORE.equals(location)) {
			redirect(response, redirect);
		}

		String uuid = request.getParameter(UUID);
		String nodeName = request.getParameter(NODENAME);
		String datastoreName = request.getParameter(DATASTORENAME);
		String hostName = request.getParameter(HOSTNAME);
		String userName = request.getParameter(USERNAME);
		String password = request.getParameter(PASSWORD);
		String protocol = request.getParameter(PROTOCOL);
		String port = request.getParameter(PORT);
		String sharePath = request.getParameter(SHARE_PATH);
		String shareUser = request.getParameter(SHARE_USER);
		String shareKey = request.getParameter(SHARE_KEY);
		String isNfs = request.getParameter(IS_NFS);
		if (!StringUtil.isEmptyOrNull(uuid)) {
			LoginServiceImpl login = new LoginServiceImpl();
			int ret = login.internalValidateByUUID(uuid, request);
			if (ret == 0) {
				if (!StringUtil.isEmptyOrNull(nodeName)) {
					if (hostName != null && userName != null
							&& password != null && protocol != null
							&& port != null) {
						ServerInfoModel serverInfoModel = new ServerInfoModel();
						serverInfoModel.setServerName(hostName);
						serverInfoModel.setUserName(userName);
						serverInfoModel.setPasswd(password);
						serverInfoModel.setProtocol(protocol);
						serverInfoModel.setPort(Integer.valueOf(port));
						request.getSession().setAttribute(
								SessionConstants.SERVICE_SHAREPATH, null);
						request.getSession().setAttribute(
								SessionConstants.SERVICE_SERVERINFO,
								serverInfoModel);
					} else if (sharePath != null) {
						ShareFolderModel shareFold = new ShareFolderModel();
						shareFold.setPath(sharePath.replaceAll("\\\\", "/")
								.trim());
						shareFold.setUserName(shareUser);
						shareFold.setPasswd(shareKey);
						shareFold.setNfs(isNfs);
						request.getSession().setAttribute(
								SessionConstants.SERVICE_SERVERINFO, null);
						request.getSession().setAttribute(
								SessionConstants.SERVICE_SHAREPATH, shareFold);
					} else {
						request.getSession().setAttribute(
								SessionConstants.SERVICE_SERVERINFO, null);
						request.getSession().setAttribute(
								SessionConstants.SERVICE_SHAREPATH, null);
					}
					request.getSession().setAttribute(
							SessionConstants.SERVICE_USERNAME, nodeName);
					redirect += "?" + LOCATION + "=" + location + "&nodename="
							+ nodeName;
					if (datastoreName != null) {
						redirect += "&datastorename=" + datastoreName;
					}
				} else {
					request.getSession().setAttribute(
							SessionConstants.SERVICE_USERNAME, LOCAL_SERVER);
					redirect += "?" + LOCATION + "=" + location;
				}
				logger.info("internal login redirect to " + redirect);
				redirect(response, redirect);
			} else {
				redirect(response, redirect);
			}
		} else {
			redirect(response, redirect);
		}

	}

	private void redirect(HttpServletResponse response, String address) {
		try {
			response.sendRedirect(address);
		} catch (Exception e) {
			logger.error("Failed to send redirect to " + address, e);
		}
	}
}
