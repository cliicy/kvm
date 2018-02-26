package com.ca.arcserve.linuximaging.ui.server.dao;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.xml.XMLDAO;
import com.ca.arcserve.linuximaging.ui.server.servlet.ContextListener;
import com.ca.arcserve.linuximaging.webservice.data.ServiceInfo;

public class ServerXMLDAO extends XMLDAO {
	
	public static String HTTP = "http";
	public static String HTTPS = "https";
	
	private static final Logger logger = Logger.getLogger(ServerXMLDAO.class);
	private String D2DServerFile=ContextListener.PATH_CONFIGURATION+File.separator+"ServerList.xml";
	private int DEFAULT_PORT=ContextListener.webServicePort;
	private Document doc;
	private static final ServerXMLDAO instance=new ServerXMLDAO();
	public static ServerXMLDAO getInstance(){
		return instance;
	}
	private ServerXMLDAO(){
		logger.debug("D2DServerFile is "+D2DServerFile);
		createDefaultD2DServer();
	}
	private void createDefaultD2DServer() {
		try {
			if(!new File(D2DServerFile).exists()){
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.newDocument();
				Element RootElement = doc.createElement("ServerList");
				doc.appendChild(RootElement);
			}else{
				doc=this.loadXmlFile(D2DServerFile);
			}
			Node local=this.selectNode(doc, "ServerList/D2DServer[@local='true']");
			if(local!=null){
				Node name=local.getAttributes().getNamedItem("name");
				name.setNodeValue(InetAddress.getLocalHost().getHostName());
			}else{
				Element targetE=doc.createElement("D2DServer");
				targetE.setAttribute("name", InetAddress.getLocalHost().getHostName());
				targetE.setAttribute("local", "true");
				
				Element userE=doc.createElement("Protocol");
				userE.setTextContent(HTTP);
				targetE.appendChild(userE);
				
				Element passwordE=doc.createElement("Port");
				passwordE.setTextContent(String.valueOf(DEFAULT_PORT));
				targetE.appendChild(passwordE);
				
				Element root=doc.getDocumentElement();
				root.appendChild(targetE);
			}
			this.doc2XmlFile(doc, D2DServerFile);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	public List<ServiceInfo> getServerList() {
		NodeList list=this.selectNodeList(doc, "ServerList/D2DServer");
		List<ServiceInfo> targets=new ArrayList<ServiceInfo>();
		if(list==null){
			return targets;
		}
		Node local=this.selectNode(doc, "ServerList/D2DServer[@local='true']");
		Node name=local.getAttributes().getNamedItem("name");
		String localhost=name.getNodeValue();
		for (int i = 0; i < list.getLength(); i++) {
			Node node=list.item(i);
			try {
//				targets.add(convertNodeToServerInfo(node));
				ServiceInfo server=convertNodeToServerInfo(node);
				if(server.getServer().equalsIgnoreCase(localhost)){
					server.setLocal(true);
					targets.add(0, server);
				}else{
					targets.add(server);
				}
			} catch (Exception e) {
				logger.error(i+" | "+node.getAttributes().getNamedItem("name").getNodeValue() ,e);
			}
		}
		return targets;
	}
	private ServiceInfo convertNodeToServerInfo(Node node) throws Exception{
		if (node==null){
			return null;
		}
		ServiceInfo result=new ServiceInfo();
		Node name=node.getAttributes().getNamedItem("name");
		result.setServer(name.getNodeValue());
		
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if(child.getNodeName().equals("Protocol")){
				result.setProtocol(child.getTextContent());
			}else if(child.getNodeName().equals("Port")){
				result.setPort(Integer.parseInt(child.getTextContent()));
			}
		}
		return result;
	}
	public void addServer(ServiceInfo target) throws Exception {
		this.removeNode(doc, "ServerList/D2DServer[@name='"+target.getServer()+"']");
		Element targetE=doc.createElement("D2DServer");
		targetE.setAttribute("name", target.getServer());
		
		Element protocolE=doc.createElement("Protocol");
		protocolE.setTextContent(target.getProtocol());
		targetE.appendChild(protocolE);
		
		Element portE=doc.createElement("Port");
		portE.setTextContent(String.valueOf(target.getPort()));
		targetE.appendChild(portE);
		
		Element root=doc.getDocumentElement();
		root.appendChild(targetE);
		
		this.doc2XmlFile(doc, D2DServerFile);
	}
	public void removeServer(String[] names) throws Exception {
		for(int i=0; i<names.length; i++){
			this.removeNode(doc, "ServerList/D2DServer[@name='"+names[i]+"']");
		}
		this.doc2XmlFile(doc, D2DServerFile);
	}
	public void removeServer(String name) throws Exception {
		this.removeNode(doc, "ServerList/D2DServer[@name='"+name+"']");
		this.doc2XmlFile(doc, D2DServerFile);
		logger.debug("end to remove server: "+name);
	}
	public ServiceInfo getD2DServerByName(String name) {
		Node node=this.selectNode(doc, "ServerList/D2DServer[@name='"+name+"']");
		try {
			return convertNodeToServerInfo(node);
		} catch (Exception e) {
			logger.error(name ,e);
			return null;
		}
	}
	
	

}
