package com.ca.arcserve.linuximaging.ui.server.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.xml.sax.InputSource;

import com.ca.arcflash.common.SecurityUtil;
import com.ca.arcserve.linuximaging.util.CommonUtil;

public class ContextListener implements ServletContextListener {
	
	public static final String D2D_SERVER_HOME="D2DSVR_HOME";
	public static final String WS_PORT_XPATH                =   "/Server/Service[@name='Catalina']/Connector[@protocol='HTTP/1.1']/@port";
	public static String PATH_CONFIGURATION;
//	public static String PATH_CUSTOMIZATION;
//	public static String ProductNameD2D;
	public static int webServicePort = 8014;
	private static final Logger logger = Logger.getLogger(ContextListener.class);
	public static String PATH_D2D_SERVER_HOME = com.ca.arcflash.common.CommonUtil.PATH_D2D_SERVER_HOME;

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		prepareEnvironment(sce);
		configLog4J(sce);
		logger.info("Preparing environment for SSL");
		try {
			SecurityUtil.prepareTrustAllSSLEnv();
		} catch (Exception e) {
			logger.error("prepare environment for SSL occur exception: ",e);
		} 
		logger.info("D2D_WEBSVR_TIMEOUT " + System.getenv("D2D_WEBSVR_TIMEOUT"));
		CommonUtil.initTimeoutValue();
		logger.info("webservice timeout value : " + CommonUtil.TIME_OUT_VALUE);
	}
	private void prepareEnvironment(ServletContextEvent sce) {
		InputStream inputForServer = null;
	    try {
			String xmlPath = null;
			PATH_CONFIGURATION = PATH_D2D_SERVER_HOME + File.separator + "configfiles";
			xmlPath = PATH_D2D_SERVER_HOME + File.separator + "TOMCAT" + File.separator + "conf" + File.separator + "server.xml";
			inputForServer = new FileInputStream(new File(xmlPath));
			XPath xpath = XPathFactory.newInstance().newXPath();
			String port = xpath.evaluate(WS_PORT_XPATH,new InputSource(inputForServer));
			if(port != null && !port.isEmpty())
				webServicePort = Integer.parseInt(port);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(inputForServer != null) inputForServer.close();
			}catch(Throwable t) {}
		}
		
	}
	private void configLog4J(ServletContextEvent sce) {
		InputStream input = null;
	    try {
			String logFilePath = (PATH_D2D_SERVER_HOME+File.separator+"logs"+File.separator+"webui.log");
			String log4jFile = PATH_CONFIGURATION + File.separator + "log4j-webui.properties";
				
		    input = new FileInputStream(log4jFile);
			java.util.Properties props = new java.util.Properties();
			props.load(input);
				
			props.setProperty("log4j.appender.logout.File", logFilePath);
			PropertyConfigurator.configure(props);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(input != null) input.close();
			}catch(Throwable t) {}
		}
	}
	
}
