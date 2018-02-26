package com.ca.arcserve.linuximaging.ui.client.homepage;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.common.icon.FlashImageBundle;
import com.ca.arcserve.linuximaging.ui.client.exception.BusinessLogicException;
import com.ca.arcserve.linuximaging.ui.client.homepage.tree.D2DServerService;
import com.ca.arcserve.linuximaging.ui.client.homepage.tree.D2DServerServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.tree.NodePopupMenu;
import com.ca.arcserve.linuximaging.ui.client.login.LoginService;
import com.ca.arcserve.linuximaging.ui.client.login.LoginServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ShareFolderModel;
import com.ca.arcserve.linuximaging.ui.client.model.VersionInfoModel;
import com.ca.arcserve.linuximaging.ui.client.restore.RestoreSelectionWindow;
import com.ca.arcserve.linuximaging.ui.client.toolbar.ToolBarPanel;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class Homepagetree extends LayoutContainer {
	public static final String NODENAME = "nodename";
	public static final String JOBNAME = "jobname";

	FlashImageBundle IconBundle = GWT.create(FlashImageBundle.class);
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private D2DServerServiceAsync d2dServerService = GWT
			.create(D2DServerService.class);
	private LoginServiceAsync loginService = GWT.create(LoginService.class);
	private TreePanel<ServiceInfoModel> homepageTree = null;
	private HomepageTab homepageTab;
	private ToolBarPanel toolBar;
	public ServiceInfoModel currentServer;
	private BaseTreeLoader<ServiceInfoModel> loader;
	private RestoreSelectionWindow restoreWindow;

	protected HomepageServiceAsync getService() {
		return service;
	}

	protected D2DServerServiceAsync getD2DServerService() {
		return d2dServerService;
	}

	public Homepagetree() {

		// this.setLayout(new FitLayout());
		this.setLayout(new FillLayout(Orientation.HORIZONTAL));
		TreeStore<ServiceInfoModel> nodeTreeStore = new TreeStore<ServiceInfoModel>(
				initTreeLoader());

		ServiceInfoModel m = new ServiceInfoModel();
		m.setServer(UIContext.Constants.d2dServers());
		m.setType(ServiceInfoModel.ROOT);
		nodeTreeStore.add(m, true);

		homepageTree = new TreePanel<ServiceInfoModel>(nodeTreeStore) {
			public boolean hasChildren(ServiceInfoModel parent) {
				if (parent.getType() != null
						&& parent.getType().equals(ServiceInfoModel.ROOT)) {
					return true;
				} else {
					return false;
				}
			}
		};

		homepageTree.setDisplayProperty("server");
		// homepageTree.getSelectionModel().select(nodeTreeStore.getRootItems(),
		// true);
		homepageTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		homepageTree.setBorders(true);

		// homepageTree.getStyle().setLeafIcon(AbstractImagePrototype.create(IconBundle.production_server()));
		homepageTree.setIconProvider(new ModelIconProvider<ServiceInfoModel>() {

			@Override
			public AbstractImagePrototype getIcon(ServiceInfoModel model) {
				if (model == null || model.getType() == null) {
					// return
					// AbstractImagePrototype.create(IconBundle.d2d_server());
					return UIContext.IconHundle.d2d_server();
				} else if (model.getType().equals(ServiceInfoModel.ROOT)) {
					// return
					// AbstractImagePrototype.create(IconBundle.all_servers());
					return UIContext.IconHundle.all_servers();
				} else {
					// return
					// AbstractImagePrototype.create(IconBundle.d2d_server());
					return UIContext.IconHundle.d2d_server();
				}
			}

		});

		NodePopupMenu contextMenu = new NodePopupMenu();
		contextMenu.setNodeTree(this);
		// homepageTree.setContextMenu(contextMenu.getContextMenu());

		homepageTree.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<ServiceInfoModel>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<ServiceInfoModel> se) {
						final ServiceInfoModel model = se.getSelectedItem();
						if (model == null) {
							return;
						} else if (ServiceInfoModel.ROOT.equals(model.getType())) {
							// toolBar.d2dServer.disableAll();
							toolBar.d2dServer.disableModifyAndDelete();
							se.setCancelled(true);
							if (currentServer != null) {
								homepageTree.getSelectionModel().select(
										currentServer, true);
							}
							// showPresentPanel(null);
						} else if (ServiceInfoModel.LOCAL_SERVER.equals(model
								.getType())) {
							// toolBar.d2dServer.disableAll();
							toolBar.d2dServer.disableModifyAndDelete();
							showPresentPanel(model);
							currentServer = model;
						} else if (model.getServer().startsWith("@")) {
							homepageTree.mask("initializing connection...");
							service.initWebServiceBehindFirewall(model,
									new AsyncCallback<Integer>() {

										@Override
										public void onFailure(Throwable caught) {
											homepageTree.unmask();
										}

										@Override
										public void onSuccess(Integer result) {
											homepageTree.unmask();
											if (result == 0) {
												// toolBar.d2dServer.disableAll();
												toolBar.d2dServer
														.enableModifyAndDelete();
												showPresentPanel(model);
											} else if (result == -100) {
												Utils.showMessage(
														UIContext.Constants
																.productName(),
														MessageBox.ERROR,
														"The version of "
																+ model.getServer()
																+ " is not consistent with current version");
											} else {
												Utils.showMessage(
														UIContext.Constants
																.productName(),
														MessageBox.ERROR,
														"Failed to create connection, error code is: "
																+ result);
											}
										}

									});
							currentServer = model;
						} else {
							// toolBar.d2dServer.disableAll();
							toolBar.d2dServer.enableModifyAndDelete();
							showPresentPanel(model);
							currentServer = model;
						}

					}

				});

		loader.addLoadListener(new LoadListener() {

			public void loaderLoadException(LoadEvent le) {
				super.loaderLoadException(le);
			}

			@Override
			public void loaderBeforeLoad(LoadEvent le) {
				homepageTree.getSelectionModel().select(
						homepageTree.getStore().getRootItems().get(0), false);
			}
		});
	}

	private BaseTreeLoader<ServiceInfoModel> initTreeLoader() {
		RpcProxy<List<ServiceInfoModel>> proxy = new RpcProxy<List<ServiceInfoModel>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<ServiceInfoModel>> callback) {
				getD2DServerService().getD2DServerList(callback);
			}
		};

		loader = new BaseTreeLoader<ServiceInfoModel>(proxy);
		loader.addLoadListener(new LoadListener() {

			public void loaderLoadException(LoadEvent le) {
				super.loaderLoadException(le);
			}

			@Override
			public void loaderLoad(LoadEvent le) {
				List<ServiceInfoModel> servers = le.getData();
				if (servers != null && servers.size() > 0) {
					// nodeTree.getStore().insert(list, 0, false);
					// homepageTree.getStore().add(homepageTree.getStore().getRootItems().get(0),
					// servers, false);
					// homepageTree.getStore().insert(homepageTree.getStore().getRootItems().get(0),
					// servers, 0, true);

					homepageTree.getSelectionModel().select(servers.get(0),
							false);
					// homepageTree.getSelectionModel().setSelection(servers);
					// homepageTree.getSelectionModel().select(homepageTree.getStore().getRootItems().get(0),
					// false);
				}
			}
		});
		return loader;
	}

	protected void showPresentPanel(final ServiceInfoModel d2dServer) {
		homepageTree.mask(UIContext.Constants.connecting());
		homepageTab.mask(UIContext.Constants.connecting());
		homepageTab.changeToolBar(false);
		homepageTab.resetFilter(d2dServer);
		loginService.validateByUUID(d2dServer,
				new BaseAsyncCallback<Integer>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						homepageTree.unmask();
						homepageTab.unmask();
						if (caught instanceof BusinessLogicException) {
							homepageTab
									.changeContent(((BusinessLogicException) caught)
											.getDisplayMessage());
						} else {
							homepageTab.changeContent(false);
						}
						homepageTab.changeToolBar(false);
						homepageTab.setCurrentServer(d2dServer);
					}

					@Override
					public void onSuccess(Integer result) {

						if (result == 0) {
							service.getVersionInfo(d2dServer,
									new BaseAsyncCallback<VersionInfoModel>() {
										@Override
										public void onFailure(Throwable caught) {
											super.onFailure(caught);
											homepageTree.unmask();
											homepageTab.unmask();
											homepageTab.changeContent(false);
											homepageTab.changeToolBar(false);
											homepageTab
													.setCurrentServer(d2dServer);
										}

										@Override
										public void onSuccess(
												VersionInfoModel result) {
											homepageTree.unmask();
											homepageTab.unmask();
											if (result != null) {
												UIContext.selectedServerVersionInfo = result;
											}
											homepageTab.changeContent(true);
											homepageTab.changeToolBar(true);
											homepageTab.refreshData(d2dServer);
											checkLocation();
										}
									});

						} else {
							homepageTree.unmask();
							homepageTab.unmask();
							homepageTab.changeContent(false);
							homepageTab.changeToolBar(false);
							homepageTab.setCurrentServer(d2dServer);
							homepageTab.setErrorLabel(UIContext.Constants
									.d2dServerManagedByOther());
						}
					}
				});

	}

	private void checkLocation() {
		if (restoreWindow == null) {
			if (UIContext.isRestoreMode) {
				final String nodeName = Location.getParameter(NODENAME);
				service.getServerInfoFromSession(new BaseAsyncCallback<ServerInfoModel>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(final ServerInfoModel serverInfoModel) {
						service.getSharePathInfoFromSession(new BaseAsyncCallback<ShareFolderModel>() {
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
							}

							@Override
							public void onSuccess(
									ShareFolderModel shareFolderModel) {
								setDefaultFilter();

								String datastoreName = Location
										.getParameter(Utils.DATASTORE_NAME);
								if (nodeName != null && nodeName.contains("@")) {
									restoreWindow = new RestoreSelectionWindow(
											homepageTab, serverInfoModel,
											shareFolderModel, nodeName,
											datastoreName);
									restoreWindow.setRestoreFileWindow();
								} else {
									restoreWindow = new RestoreSelectionWindow(
											homepageTab, serverInfoModel,
											shareFolderModel, nodeName,
											datastoreName);
									restoreWindow.show();
								}

							}
						});
					}
				});
			}
		}
	}

	private void setDefaultFilter() {
		homepageTab.jobStatusTable.filterByJobType(
				JobStatusFilterModel.JOB_TYPE_RESTORE, false);
		homepageTab.historyTable.filterByJobType(JobType.RESTORE, false);
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		setLayout(new FitLayout());
		add(homepageTree);
	}

	public void addElement(ServiceInfoModel model) {
		homepageTree.getStore().add(
				homepageTree.getStore().getRootItems().get(0), model, false);
		homepageTree.getSelectionModel().select(false, model);
	}

	public boolean isDuplicate(ServiceInfoModel model) {
		List<ServiceInfoModel> list = homepageTree.getStore().getChildren(
				homepageTree.getStore().getRootItems().get(0));
		if (list == null || list.size() == 0) {
			return false;
		} else {
			for (ServiceInfoModel server : list) {
				if (server.getServer().equalsIgnoreCase(model.getServer())) {
					return true;
				}
			}
		}
		return false;
	}

	public void modifyElement(ServiceInfoModel model) {

		ServiceInfoModel d2dserver = homepageTree.getSelectionModel()
				.getSelectedItem();
		d2dserver.setPort(model.getPort());
		d2dserver.setProtocol(model.getProtocol());
		showPresentPanel(model);
		// homepageTab.refreshData(model);
	}

	public void deleteElement() {

		//
		// List<ServerInfoModel> list=homepageTree.getStore().getRootItems();
		// for(int i=0;i<list.size();i++){
		// String name=list.get(i).getServerName();
		// }
		// List<ServerInfoModel> list2=homepageTree.getStore().getAllItems();
		// for(int i=0;i<list2.size();i++){
		// String name=list2.get(i).getServerName();
		// }
		// homepageTree.getStore().getChild(1).getServerName();
		final ServiceInfoModel model = homepageTree.getSelectionModel()
				.getSelectedItem();
		if (model == null) {
			return;
		} else if (isSelectedRootItem()) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR, "You can't delete root node.");
		} else if (model.getType().equalsIgnoreCase(
				ServiceInfoModel.LOCAL_SERVER)) {
			Utils.showMessage(UIContext.Constants.productName(),
					MessageBox.ERROR, "You can't delete local node.");
		} else {
			deleteD2DServer(model, false);
		}
	}

	private void deleteD2DServer(final ServiceInfoModel model,
			final boolean isForce) {
		getD2DServerService().deleteD2DServer(model, isForce,
				new BaseAsyncCallback<Integer>() {
					@Override
					public void onFailure(Throwable caught) {
						if (!isForce) {
							if (caught instanceof BusinessLogicException) {
								BusinessLogicException e = (BusinessLogicException) caught;
								// Managed by other server or cannot connect
								if (e.getErrorCode().equals("55834574849")
										|| e.getErrorCode().equals(
												"55834574850")) {
									String message = "";
									if (e.getErrorCode().equals("55834574849")) {
										message = UIContext.Constants
												.deleteManagedByOtherServerConfirm();
									} else {
										message = UIContext.Constants
												.deleteCannotConnectConfirm();
									}
									MessageBox mb = new MessageBox();
									mb.setTitle(UIContext.productName);
									mb.setMessage(message);
									mb.setIcon(MessageBox.WARNING);
									mb.setButtons(MessageBox.YESNO);
									mb.addCallback(new Listener<MessageBoxEvent>() {

										@Override
										public void handleEvent(
												MessageBoxEvent be) {
											if (be.getButtonClicked()
													.getItemId()
													.equals(Dialog.YES)) {
												deleteD2DServer(model, true);
											} else {
												be.cancelBubble();
											}
										}
									});
									mb.setModal(true);
									mb.show();
								} else {
									super.onFailure(caught);
								}
							} else {
								super.onFailure(caught);
							}
						} else {
							super.onFailure(caught);
						}
					}

					@Override
					public void onSuccess(Integer result) {
						homepageTree.getStore().remove(model);
						homepageTree.getSelectionModel().select(
								false,
								homepageTree.getStore().getChild(
										homepageTree.getStore().getRootItems()
												.get(0), 0));
					}
				});
	}

	public boolean isSelectedRootItem() {
		ServiceInfoModel model = homepageTree.getSelectionModel()
				.getSelectedItem();
		// return model.getType().equals(ServerInfoModel.ROOT);
		return model.equals(homepageTree.getStore().getRootItems().get(0));
	}

	public ServiceInfoModel getSelectedItem() {
		return homepageTree.getSelectionModel().getSelectedItem();
	}

	public List<ServiceInfoModel> getBackupServerList() {
		// ServiceInfoModel root=homepageTree.getStore().getRootItems().get(0);
		ServiceInfoModel root = homepageTree.getStore().getChild(0);
		List<ServiceInfoModel> result = homepageTree.getStore().getChildren(
				root);
		return result;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		homepageTree.expandAll();
		// homepageTree.setExpanded(homepageTree.getStore().getRootItems().get(0),
		// true);
		// homepageTree.getSelectionModel().select(getBackupServerList(),
		// false);
	}

	public void setHomepageTab(HomepageTab homepageTab) {
		this.homepageTab = homepageTab;

	}

	public void setToolBarPanel(ToolBarPanel toolBarPanel) {
		this.toolBar = toolBarPanel;

	}

	public void selectElement(ServiceInfoModel server) {
		homepageTree.getSelectionModel().select(server, false);
	}
}
