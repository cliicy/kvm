package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.NodeConnectionModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.google.gwt.core.client.GWT;

public class BackupSourceValidate {
	private List<NodeModel> nodeList = null;
	private int curIndex = 0;
	private ServiceInfoModel serviceInfo = null;
	private BackupSourceInfoTable serverInfo = null;
	private BackupSource backupSource = null;
	private boolean validateIsCanceled = false;
	private HomepageServiceAsync service = GWT.create(HomepageService.class);

	
	public BackupSourceValidate(ServiceInfoModel service, BackupSourceInfoTable table) {
		if (table == null) {
			return;
		}
		
		nodeList = table.getSelectedItems();
		serverInfo = table;
		serviceInfo = service;
	}
	
	private void maskTable() {
		serverInfo.mask(UIContext.Constants.validating());
		if (backupSource != null) {
			backupSource.setValidateToCancel();
		}
	}
	
	private void unmaskTable() {
		serverInfo.unmask();
		if (backupSource != null) {
			backupSource.setValidateToValidate();
		}
		
	}
	
	public void Validate() {
		maskTable();
		validateNode(getFirst());
	}
	
	public void Validate(NodeModel node) {
		if (node == null) return;
		final NodeModel nodeF = node;
		maskTable();
		service.connectNode(serviceInfo, 
				node.getServerName(), node.getUserName(), node.getPasswd(), false, new BaseAsyncCallback<NodeConnectionModel>(){

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						//Utils.showMessage(UIContext.Constants.productName(), MessageBox.ERROR, caught.getMessage());
						unmaskTable();
					}

					@Override
					public void onSuccess(NodeConnectionModel result) {
						NodeModel newNode = new NodeModel();
						newNode.setServerName(nodeF.getServerName());
						newNode.setUserName(nodeF.getUserName());
						newNode.setPasswd(nodeF.getPasswd());
						newNode.setUUID(result.getNodeUUID());
						newNode.connInfo = result;
						serverInfo.addData(newNode);
						unmaskTable();
					}
		});
	}
	
	private NodeModel getFirst()
	{
		if ( nodeList == null || nodeList.size() == 0 )
		{
			return null;
		}
		
		curIndex = 0;
		return nodeList.get(curIndex++);
	}
	private NodeModel getNext()
	{
		if (nodeList == null || curIndex >= nodeList.size())
			return null;
		
		if ( isValidateIsCanceled() )
			return null;
		
		return nodeList.get(curIndex++);
	}
	
	private void validateNode(NodeModel node)
	{
		if ( node == null )
		{
			unmaskTable();
			return;
		}

		final NodeModel nodeF = node;
		service.connectNode(serviceInfo, 
				node.getServerName(), node.getUserName(), node.getPasswd(), false, new BaseAsyncCallback<NodeConnectionModel>(){

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						//Utils.showMessage(UIContext.Constants.productName(), MessageBox.ERROR, caught.getMessage());
						unmaskTable();
					}

					@Override
					public void onSuccess(NodeConnectionModel result) {
						NodeModel newNode = new NodeModel();
						newNode.setServerName(nodeF.getServerName());
						newNode.setUserName(nodeF.getUserName());
						newNode.setPasswd(nodeF.getPasswd());
						newNode.setUUID(result.getNodeUUID());
						newNode.connInfo = result;
						serverInfo.addData(newNode);
						validateNode(getNext());
					}
			
		});
	}

	public void setValidateIsCanceled(boolean validateIsCanceled) {
		this.validateIsCanceled = validateIsCanceled;
	}

	public boolean isValidateIsCanceled() {
		return validateIsCanceled;
	}
	
	public void setNodeList(List<NodeModel> nodes) {
		nodeList = nodes;
	}

	public void setBackupSource(BackupSource backupSource) {
		this.backupSource = backupSource;
	}

	public BackupSource getBackupSource() {
		return backupSource;
	}
}
