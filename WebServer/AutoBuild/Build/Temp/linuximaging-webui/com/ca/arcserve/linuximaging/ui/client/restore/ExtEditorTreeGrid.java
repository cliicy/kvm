package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.common.FlashCheckBox;
import com.ca.arcserve.linuximaging.ui.client.model.CatalogModelType;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridSelectionModel;
import com.google.gwt.user.client.Element;

public class ExtEditorTreeGrid<M extends ModelData> extends EditorTreeGrid<M> {

	@SuppressWarnings("unchecked")
	public ExtEditorTreeGrid(TreeStore store, ColumnModel cm,
			HashMap<GridTreeNode, FlashCheckBox> table, boolean isRestManager, boolean isExchangeGRTPanel) {
		super(store, cm);
		
		// set a normal selection model, otherwise the it will be CellTreeGridSelectionModel
		if (isExchangeGRTPanel)
		{
			setSelectionModel(new TreeGridSelectionModel<M>());
		}
		
		this.table = table;
		this.isRestManager = isRestManager;
		this.isExchangeGRTPanel = isExchangeGRTPanel;
	}

	private HashMap<GridTreeNode, FlashCheckBox> table;
	private boolean isRestManager;

	private HashMap<GridTreeNode, PagingContext> nodeContextMap = new HashMap<GridTreeNode, PagingContext>();
	private boolean isExchangeGRTPanel = false;
	private RestoreWindow restoreWindow;	

	public void clean(GridTreeNode node) {
		nodeContextMap.remove(node);
	}

	@Override
	protected void onClick(GridEvent<M> e) {
		M m = e.getModel();
		
		if (m != null) {
			TreeNode node = findNode(m);
			if (node != null)
			{
				Element jointEl = treeGridView.getJointElement(node);
				if (jointEl != null && e.within(jointEl))
				{
					if (m instanceof GridTreeNode)
					{
						final GridTreeNode treeNode = (GridTreeNode) m;
						
						// show Mailbox Explorer here
						if (isExchGRTRoot(treeNode))
						{
							if (isExchangeGRTPanel)
							{
								//showMailboxExplorer(treeNode);
							}							
						}
						else
						{
							if (isPagingRequired(treeNode))
							{
							handlePaging(treeNode);
							}
							else
							{
							toggle(m);
						}
						}

					}
					else
					{
						toggle(m);
					}
				}
				else
				{					
					super.onClick(e);
				}
			}
		}
	}

	private boolean isExchGRTRoot(GridTreeNode treeNode) {
		boolean bRoot = false;
		
		if (treeNode != null && treeNode.getType() != null) 
		{
			bRoot = CatalogModelType.rootGRTExchangeTypes.contains(treeNode.getType().intValue());
		}
		
		return bRoot;
	}
	
	private void showMailboxExplorer(GridTreeNode treeNode)
	{
		FlashCheckBox clickedNodeCheckBox = table.get(treeNode);
		if (clickedNodeCheckBox == null) {
			clickedNodeCheckBox = new FlashCheckBox();
			table.put(treeNode, clickedNodeCheckBox);
		}
		
		// get the backup destination and session id from parent node (cannot access the RecoveryPointsPanel here)
		GridTreeNode parentNode = (GridTreeNode) this.getTreeStore().getParent((M) treeNode);
		if (parentNode != null)
		{
			treeNode.setBackupDestination(parentNode.getBackupDestination());
			treeNode.setSessionID(parentNode.getSessionID());
		}		
	}

	private boolean isPagingRequired(final GridTreeNode treeNode) {
		return (isFileSystem(treeNode) || isExchGRT(treeNode))
				&& treeNode.getChildrenCount() != null
				&& treeNode.getChildrenCount() > PagingContext.PAGETHRESHOLD;
	}	

	private boolean isExchGRT(GridTreeNode treeNode) {
		if (treeNode != null && treeNode.getType() != null) {
			return CatalogModelType.allGRTExchangeTypes.contains(treeNode
					.getType());
		}
		return false;
	}

	private void handlePaging(GridTreeNode treeNode) {
		FlashCheckBox clickedNodeCheckBox = table.get(treeNode);
		if (clickedNodeCheckBox == null) {
			clickedNodeCheckBox = new FlashCheckBox();
			table.put(treeNode, clickedNodeCheckBox);
		}

//		GridTreeNode edbNode = RestoreUtil.findAncestorEDBItem((TreeStore<GridTreeNode>)treeStore, treeNode);
//		GridTreeNode mbNode =  RestoreUtil.findAncestorMailboxLevelItem((TreeStore<GridTreeNode>)treeStore, treeNode);
//		if(edbNode != null){
//			treeNode.getReferNode().add(edbNode);
//		}
//		if(mbNode != null){
//			treeNode.getReferNode().add(mbNode);
//		}
		PagingContext.handleClick(treeNode, nodeContextMap, null,
				clickedNodeCheckBox, true, isRestManager, this);		
	}

	public HashMap<GridTreeNode, FlashCheckBox> getTable() {
		return table;
	}

	public void setTable(HashMap<GridTreeNode, FlashCheckBox> table) {
		this.table = table;
	}

	public HashMap<GridTreeNode, PagingContext> getNodeContextMap() {
		return nodeContextMap;
	}

	public void setNodeContextMap(
			HashMap<GridTreeNode, PagingContext> nodeContextMap) {
		this.nodeContextMap = nodeContextMap;
	}

	public List<GridTreeNode> getPagedSelectedNodes() {
		List<GridTreeNode> pagedSelected = new ArrayList<GridTreeNode>();
		Iterator<GridTreeNode> contIt = nodeContextMap.keySet().iterator();
		while (contIt.hasNext()) {
			GridTreeNode contNode = contIt.next();
			PagingContext childCont = nodeContextMap.get(contNode);
			if (childCont != null) {
				List<GridTreeNode> lst = childCont.getSelectedNodes();
				pagedSelected.addAll(lst);
			}
		}
		return pagedSelected;
	}

	private boolean isFileSystem(GridTreeNode node) {
		return true;
//		List<GridTreeNode> selectedNodes = new ArrayList<GridTreeNode>();
//		selectedNodes.add(node);
//		RestoreJobType type = RestoreUtil.getJobType(selectedNodes);
//		if (type != null && type == RestoreJobType.FileSystem) {
//			return true;
//		}
//		return false;
	}

	@Override
	protected void onDoubleClick(GridEvent<M> e) 
    {
		if (editSupport.onDoubleClick(e))
		{
			return;
		}
		M m = e.getModel();

		boolean isOpenPaging = false;
		boolean isOpenMailboxExplorer = false;
		if (m instanceof GridTreeNode)
		{
			final GridTreeNode treeNode = (GridTreeNode) m;

			// show Mailbox Explorer here
			if (isExchGRTRoot(treeNode))
			{
				if (isExchangeGRTPanel)
				{
					isOpenMailboxExplorer = true;					
				}				
			}
			else
			{
				if (isPagingRequired(treeNode))
				{
				isOpenPaging = true;
			}
		}
		}

		if (isOpenPaging)
		{
			handlePaging((GridTreeNode) m);
		}
		else if (isOpenMailboxExplorer)
		{
			//showMailboxExplorer((GridTreeNode) m);
		}
		else
		{
			super.onDoubleClick(e);
		}
	}
	
	public void selectTreeNodeParent(M node) {
		M parent = this.getTreeStore().getParent(node);
		int parentState = FlashCheckBox.NONE;
		if (parent != null) {
			int fullCount = 0;
			int partialCount = 0;
			int emptyCount = 0;
			int nullCount = 0;

			List<M> childNodes = this.getTreeStore().getChildren(
					parent);
			// For each call select Children
			for (int i = 0; i < childNodes.size(); i++) {
				FlashCheckBox fcb = table.get(childNodes.get(i));
				if (fcb != null) {
					switch (fcb.getSelectedState()) {
					case FlashCheckBox.FULL:
						fullCount++;
						break;
					case FlashCheckBox.PARTIAL:
						partialCount++;
						break;
					case FlashCheckBox.NONE:
					default:
						emptyCount++;
						break;
					}
				} else {
					nullCount++;
				}
			}

			if (emptyCount + nullCount == childNodes.size()) {
				parentState = FlashCheckBox.NONE;
			} else {
				parentState = FlashCheckBox.PARTIAL;
			}

			FlashCheckBox fcb = table.get(parent);
			if (fcb != null) {
				fcb.setSelectedState(parentState);
				// Parent changed, change the parent's parent
				selectTreeNodeParent(parent);
			}
		}
	}

	public void setRestoreWindow(RestoreWindow restoreWindow) {
		this.restoreWindow=restoreWindow;
	}

	public RestoreWindow getRestoreWindow() {
		return restoreWindow;
	}

	
}
