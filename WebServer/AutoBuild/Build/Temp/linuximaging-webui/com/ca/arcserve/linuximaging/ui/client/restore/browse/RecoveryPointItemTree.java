package com.ca.arcserve.linuximaging.ui.client.restore.browse;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.CatalogModelType;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class RecoveryPointItemTree extends LayoutContainer {
	
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private ExtTreePanel tree;
	private BaseTreeLoader<GridTreeNode> loader;
	private BrowseContext browseContext;
	private BrowseRecoveryPointPanel parentPanel;
	private static final int MAX_SUB_FOLDER_COUNT = 1000;
	private boolean isFirstLoad = true;

	public RecoveryPointItemTree(BrowseRecoveryPointPanel parentContainer,
			BrowseContext browseContext) {

		this.browseContext = browseContext;
		this.parentPanel = parentContainer;
		TreeStore<GridTreeNode> treeStore = new TreeStore<GridTreeNode>(
				initTreeLoader());
		StoreSorter<GridTreeNode> sorter = RestoreUtils.initStoreSorter();
		treeStore.setStoreSorter(sorter);
		tree = new ExtTreePanel(treeStore) {
			public boolean hasChildren(GridTreeNode parent) {
				/*
				 * if(parent.getType()!=null &&
				 * parent.getType().equals(CatalogModelType.Folder) &&
				 * parent.getChildrenFolderCount()>0){ return true; }else{
				 * return false; }
				 */
				return true;
			}

			public boolean isLeaf(GridTreeNode model) {
				return model.getType() == CatalogModelType.File;
			}
		};
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tree.setDisplayProperty("displayName");
		tree.getStyle().setLeafIcon(
				AbstractImagePrototype.create(UIContext.IconBundle.folder()));
		tree.setIconProvider(new ModelIconProvider<GridTreeNode>() {

			@Override
			public AbstractImagePrototype getIcon(GridTreeNode model) {
				boolean isExpand = tree.isExpanded(model);
				if (isExpand) {
					return AbstractImagePrototype.create(UIContext.IconBundle
							.folder_open());
				} else {
					return AbstractImagePrototype.create(UIContext.IconBundle
							.folder_closed());
				}

			}

		});
		tree.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<GridTreeNode>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<GridTreeNode> se) {
						parentPanel.refresh(se.getSelectedItem());
					}

				});
		tree.mask(UIContext.Constants.loading());
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FillLayout(Orientation.HORIZONTAL));
		panel.add(tree);
		if (browseContext.getRestoreType() == RestoreType.SHARE_RECOVERY_POINT) {
			panel.setHeight(402);
		} else {
			panel.setHeight(352);
		}
		this.add(panel);
	}

	private BaseTreeLoader<GridTreeNode> initTreeLoader() {
		RpcProxy<List<GridTreeNode>> proxy = new RpcProxy<List<GridTreeNode>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<GridTreeNode>> callback) {
				GridTreeNode parent = (GridTreeNode) loadConfig;
				if (parentPanel.isNodeExist(parent)) {
					parent.setSelectState(parentPanel.getNodeState(parent));
				}
				if (parent == null) {
					parent = new GridTreeNode();
					parent.setCatalogFilePath("");
					parent.setParentID(-1L);
					parent.setSelectState(GridTreeNode.NONE);
					parent.setChildrenCount(1L);
					parent.setChildrenFolderCount(1L);
				}
				if (parent.getChildrenFolderCount() > MAX_SUB_FOLDER_COUNT) {
					Utils.showMessage(
							UIContext.Constants.productName(),
							MessageBox.INFO,
							UIContext.Messages
									.browseSubFolderExceed(MAX_SUB_FOLDER_COUNT));
				}
				final AsyncCallback<List<GridTreeNode>> oldCallBack = callback;
				AsyncCallback<List<GridTreeNode>> cb = new BaseAsyncCallback<List<GridTreeNode>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (!browseContext.isClosed()) {
							super.onFailure(caught);
							oldCallBack.onFailure(caught);
							tree.unmask();
							parentPanel.refresh(null);
							parentPanel.getWindow().hide();
						}
					}

					@Override
					public void onSuccess(List<GridTreeNode> result) {
						for (GridTreeNode node : result) {
							if (parentPanel.isNodeExist(node)) {
								node.setSelectState(parentPanel
										.getNodeState(node));
							}
						}
						oldCallBack.onSuccess(result);
						if (result.size() > 0) {
							if (isFirstLoad) {
								isFirstLoad = false;
								tree.getSelectionModel().select(false,
										tree.getStore().getRootItems().get(0));
							}
						}
						tree.unmask();
					}

				};
				service.getTreeGridChildren(browseContext.getServer(),
						browseContext.getSessionLocation(),
						browseContext.getMachine(),
						browseContext.getRecoveryPoint(), parent,
						browseContext.getScriptUUID(), cb);

				if (browseContext.getRecoveryPoint().getHbbuPath() != null) {
					service.iscompletedMountRP(browseContext.getServer(),
							browseContext.getSessionLocation(),
							browseContext.getMachine(),
							browseContext.getRecoveryPoint(),
							browseContext.getScriptUUID(), isCompleteMountRp);
				}
			}
		};
		loader = new BaseTreeLoader<GridTreeNode>(proxy);
		return loader;
	}

	AsyncCallback<Boolean> isCompleteMountRp = new BaseAsyncCallback<Boolean>() {

		@Override
		public void onFailure(Throwable caught) {

		}

		@Override
		public void onSuccess(Boolean result) {
			if (result) {
				BrowseRecoveryPointWindow window = (BrowseRecoveryPointWindow) parentPanel
						.getWindow();
				window.getWarningContainer().show();
			}
		}

	};

	public GridTreeNode getSelectedNode() {
		return tree.getSelectionModel().getSelectedItem();
	}

	public GridTreeNode getRootNode() {
		return tree.getStore().getRootItems().get(0);
	}

	public void refresh(GridTreeNode model) {
		GridTreeNode inputNode = getGridTreeNode(model);
		if (inputNode != null) {
			tree.getStore().update(inputNode);
			tree.refresh(inputNode);
			refreshParent(inputNode, true);
			refreshChildren(inputNode);
		} else {
			refreshParent(model, false);
		}
	}

	private GridTreeNode getGridTreeNode(GridTreeNode node) {
		for (GridTreeNode treeNode : tree.getStore().getAllItems()) {
			if (treeNode.getPath().equals(node.getPath())) {
				treeNode.setSelectState(node.getSelectState());
				return treeNode;
			}
		}
		return null;
	}

	private void refreshParent(GridTreeNode node, boolean exist) {
		GridTreeNode parentNodeModel = null;
		if (exist) {
			parentNodeModel = tree.getStore().getParent(node);
		} else {
			parentNodeModel = tree.getSelectionModel().getSelectedItem();
		}
		if (parentNodeModel != null) {
			if (node.getSelectState() == GridTreeNode.FULL
					|| node.getSelectState() == GridTreeNode.PARTIAL) {
				parentNodeModel.setSelectState(GridTreeNode.FULL);
			} else {
				if (parentNodeModel.getChildrenCount() == 1) {
					parentNodeModel.setSelectState(GridTreeNode.NONE);
				} else {
					int state = GridTreeNode.NONE;
					for (GridTreeNode child : tree.getStore().getChildren(
							parentNodeModel)) {
						if (child.getSelectState() == GridTreeNode.FULL
								|| child.getSelectState() == GridTreeNode.PARTIAL) {
							state = GridTreeNode.PARTIAL;
							break;
						}
					}
					parentNodeModel.setSelectState(state);
				}
			}
			tree.getStore().update(parentNodeModel);
			tree.refresh(parentNodeModel);
			refreshParent(parentNodeModel, true);
		}
	}

	private void refreshChildren(GridTreeNode node) {
		if (node != null) {
			if (tree.getStore().hasChildren(node)) {
				for (GridTreeNode child : tree.getStore().getChildren(node)) {
					if (node.getSelectState() == GridTreeNode.FULL
							|| node.getSelectState() == GridTreeNode.PARTIAL) {
						child.setSelectState(GridTreeNode.FULL);
					} else {
						child.setSelectState(GridTreeNode.NONE);
					}
					tree.getStore().update(child);
					tree.refresh(child);
					refreshChildren(child);
				}
			}
		}
	}

	/*
	 * public int getParentState(GridTreeNode node){
	 * 
	 * }
	 */

	public class ExtTreePanel extends TreePanel<GridTreeNode> {

		public ExtTreePanel(TreeStore<GridTreeNode> store) {
			super(store);
		}

		@Override
		public void refresh(GridTreeNode model) {
			super.refresh(model);
		}

	}

}
