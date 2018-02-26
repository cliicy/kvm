package com.ca.arcserve.linuximaging.ui.client.components.backup.node;


import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.common.icon.FlashImageBundle;
import com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus.ComponentJobStatus;
import com.ca.arcserve.linuximaging.ui.client.components.backup.node.i18n.NodeUIConstants;
import com.ca.arcserve.linuximaging.ui.client.components.backup.node.model.ServerInfoModel;
import com.extjs.gxt.ui.client.data.BaseLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ComponentNode extends LayoutContainer{
	FlashImageBundle IconBundle = GWT.create(FlashImageBundle.class);
	private static final NodeUIConstants uiConstants = GWT.create(NodeUIConstants.class);
	private IBackupNodeServiceAsync backupService = GWT.create(IBackupNodeService.class);
	private TreePanel<ServerInfoModel> nodeTree = null;
	private ComponentJobStatus componentJobStatus;
	
	protected IBackupNodeServiceAsync getService()
	{
		return backupService;
	}

	public ComponentNode(){
		this.setLayout(new FitLayout());
		TreeStore<ServerInfoModel> nodeTreeStore = new TreeStore<ServerInfoModel>(); 
	    
		ServerInfoModel m = new ServerInfoModel();
		m.setServerName(uiConstants.allNodes());
		m.setType(ServerInfoModel.ROOT);
	    nodeTreeStore.add(m, true);
	    
	    nodeTree = new TreePanel<ServerInfoModel>(nodeTreeStore){   
		      @Override  
		      protected boolean hasChildren(ServerInfoModel m) {   
		        return true;   
		      }   
		    };      

	    nodeTree.setDisplayProperty("server");
	    nodeTree.getSelectionModel().select(nodeTreeStore.getRootItems(), true); 
		nodeTree.setBorders(true);
		nodeTree.getStyle().setLeafIcon(AbstractImagePrototype.create(IconBundle.d2d_server()));
		
		ComponentNodePopupMenu contextMenu = new ComponentNodePopupMenu();
		contextMenu.setNodeTree(this);
		nodeTree.setContextMenu(contextMenu.getContextMenu());
		
		
		
		nodeTree.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ServerInfoModel>(){

			@Override
			public void selectionChanged(SelectionChangedEvent<ServerInfoModel> se) {
				ServerInfoModel model = se.getSelectedItem();
				if(ServerInfoModel.ROOT.equals(model.getType())){
					//root node ,no action
//					Utils.showErrorMessage(UIContext.Constants.productName(),MessageBox.INFO,"test");
				}else{
					String serverName=se.getSelectedItem().getServerName();
					showPresentPanel(serverName);
				}
				
			}
			
		});
	}
	
	protected void showPresentPanel(String serverName) {
		componentJobStatus.refreshData(serverName);
	}

	@Override
	protected void onRender(Element parent, int pos){
		super.onRender(parent, pos);
		setLayout(new FitLayout());
		add(nodeTree);
	}
	
	public void AddElement(String serverName, String userName, String passwd)
	{
		final ServerInfoModel model = new ServerInfoModel();   
	    model.setServerName(serverName);
	    model.setUserName(userName);
	    model.setPasswd(passwd);
		getService().AddNewServer(model, new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to add new server to the server!");
			}

			@Override
			public void onSuccess(Integer result) {
				nodeTree.getStore().add(nodeTree.getStore().getRootItems().get(0), model, false);
			}
		});
		//nodeTree.setLeaf(m, true);
	}
	
	public void DeleteElement()
	{
		List<ServerInfoModel> servers = nodeTree.getSelectionModel().getSelectedItems();
		for(ServerInfoModel model : servers)
		{
			if (model != null && !model.equals(nodeTree.getStore().getRootItems().get(0))) {
				nodeTree.getStore().remove(model);
				getService().RemoveServer((String)(model.get("name")), new AsyncCallback<Integer>(){

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to delete server from the server!");
					}

					@Override
					public void onSuccess(Integer result) {
						
					}});
			}
		}
	}
	
	public void CaptureServer(String templateName, String jobName, String excludeFS)
	{
		ServerInfoModel server = nodeTree.getSelectionModel().getSelectedItem();
		getService().captureServer(server, jobName, templateName, excludeFS, new AsyncCallback<Long>(){

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to capture server :" + caught.getMessage());
			}

			@Override
			public void onSuccess(Long result) {
				if(result>=0){
					Window.alert("Success to capture server!");
//					componentJobStatus.jobStatusTable.addData(result);
				}else{
					Window.alert("Failed to capture server!");
				}
			}
			});
	}
	
	public static NodeUIConstants getUIConsts() {
		return uiConstants;
	}
	
	@Override
	protected void onLoad()
	{
		super.onLoad();
		
		RpcProxy<List<ServerInfoModel>> proxy = new RpcProxy<List<ServerInfoModel>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<ServerInfoModel>> callback) {
				getService().GetServerList(callback);
			}
		};

		final BaseLoader<List<ServerInfoModel>> loader = new BaseLoader<List<ServerInfoModel>>(proxy);
		loader.addLoadListener(new LoadListener() {

			public void loaderLoadException(LoadEvent le) {
				super.loaderLoadException(le);				
			}
			
			@Override
			public void loaderLoad(LoadEvent le) {
				List<ServerInfoModel> servers = le.getData();
				if(servers!=null && servers.size()>0)
				{
					//nodeTree.getStore().insert(list, 0, false);
					nodeTree.getStore().insert(nodeTree.getStore().getRootItems().get(0), servers, 0, true);
				}
			}
		});

		loader.load();
	}

	public void setComponentJobStatus(ComponentJobStatus componentJobStatus) {
		this.componentJobStatus=componentJobStatus;
		
	}

}
