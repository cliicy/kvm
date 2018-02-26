package com.ca.arcserve.linuximaging.ui.client.restore.browse;

import java.util.List;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.LoadingStatus;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.BrowseSearchResultModel;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.SearchConditionModel;
import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BrowseRecoveryPointPanel extends LayoutContainer {
	
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	public static final int REFRESH_INTERVAL = 3 * 1000;
	private TextField<String> selectedPath;
	private TextField<String> searchFile;
	private BrowseContext browseContext;
	private RecoveryPointItemGridPanel itemGridPanel;
	private RecoveryPointItemTree itemTree;
	private RecoveryPointItemSelectedPanel selectedPanel;
	private static final int RESULT_LIMIT = 100;
	private static final int PAGE_SIZE = 25;
	private int currentResultSize;
	private Button findButton;
	private boolean searchCancel = false;
	private LoadingStatus searching;
	private Window window;
	private Button actionButton;
	private SelectionListener<ButtonEvent> selectionListenerFind = new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			currentResultSize = 0;
			SearchConditionModel model = new SearchConditionModel();
			model.setSearchStr(searchFile.getValue());
			model.setStart(0);
			model.setPageSize(PAGE_SIZE);
			GridTreeNode parent = getSelectedNodeFromTree();
			if (parent == null) {
				parent = getRootNode();
			}
			searchCancel = false;
			changeFindButton(false);
			service.startSearch(browseContext.getServer(),
					browseContext.getSessionLocation(),
					browseContext.getMachine(),
					browseContext.getRecoveryPoint(), parent, model,
					browseContext.getScriptUUID(),
					new AsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable caught) {

						}

						@Override
						public void onSuccess(Integer result) {
							if (result == 0) {
								itemGridPanel.removeAllItems();
								getSearchResult(0);
							}
						}

					});
		}

	};

	private SelectionListener<ButtonEvent> selectionListenerCancel = new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			searchCancel = true;
			stopSearch();
		}

	};

	public BrowseRecoveryPointPanel(BrowseContext browseContext) {
		this.browseContext = browseContext;

		this.setLayout(new BorderLayout());

		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH,
				27);
		northData.setMargins(new Margins(0));

		this.add(generateNorth(), northData);

		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 250);
		westData.setMargins(new Margins(0, 0, 0, 0));
		itemTree = getRecoveryPointItemTree();
		this.add(itemTree, westData);

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER,
				450);
		centerData.setMargins(new Margins(0));

		itemGridPanel = getRecoveryPointItemGridPanel();
		this.add(itemGridPanel, centerData);

		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,
				150);
		southData.setMargins(new Margins(0));

		selectedPanel = getRecoveryPointItemSelectedPanel();
		this.add(selectedPanel, southData);

		if (browseContext.getRestoreType() != RestoreType.SHARE_RECOVERY_POINT) {
			this.add(selectedPanel, southData);
		} else {
			selectedPanel.hide();
			actionButton.hide();
		}

	}

	private RecoveryPointItemTree getRecoveryPointItemTree() {
		return new RecoveryPointItemTree(this, browseContext);
	}

	private RecoveryPointItemGridPanel getRecoveryPointItemGridPanel() {
		return new RecoveryPointItemGridPanel(this, browseContext);
	}

	private RecoveryPointItemSelectedPanel getRecoveryPointItemSelectedPanel() {
		return new RecoveryPointItemSelectedPanel(this);
	}

	private LayoutContainer generateNorth() {
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(6);
		layout.setCellSpacing(2);
		layout.setWidth("100%");
		container.setLayout(layout);

		TableData td = new TableData();
		td.setWidth("17%");
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		LabelField label = new LabelField(
				UIContext.Constants.restoreCurrentLocation());
		container.add(label, td);

		td = new TableData();
		td.setWidth("40%");
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		selectedPath = new TextField<String>();
		selectedPath.setWidth(420);
		selectedPath.setEnabled(false);
		container.add(selectedPath, td);

		td = new TableData();
		td.setWidth("8%");
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		actionButton = new Button(UIContext.Constants.restoreAction());
		actionButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		Menu actionMenu = new Menu();
		MenuItem selectAll = new MenuItem(
				UIContext.Constants.restoreSelectAll());
		selectAll.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				if (selectedPanel.isSelected(selectedPath.getValue())) {
					Utils.showMessage(UIContext.productName,
							MessageBox.WARNING,
							UIContext.Constants.cannotSelect());
					return;
				}
				itemGridPanel.changeSelectionState(true);
				addSelectedNode(getSelectedNodeFromItemGrid());
			}
		});
		MenuItem deselectAll = new MenuItem(
				UIContext.Constants.restoreDeSelectAll());
		deselectAll.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				if (selectedPanel.isSelected(selectedPath.getValue())) {
					Utils.showMessage(UIContext.productName,
							MessageBox.WARNING,
							UIContext.Constants.cannotDeselect());
					return;
				}
				selectedPanel.removeSelectedList(getSelectedNodeFromItemGrid());
				itemGridPanel.changeSelectionState(false);
			}
		});
		actionMenu.add(selectAll);
		actionMenu.add(deselectAll);
		actionButton.setMenu(actionMenu);
		container.add(actionButton, td);

		td = new TableData();
		td.setWidth("14%");
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		searching = new LoadingStatus();
		searching.setLoadingMsg(UIContext.Constants.restoreSearching());
		searching.hideIndicator();
		container.add(searching, td);

		td = new TableData();
		td.setWidth("18%");
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		searchFile = new TextField<String>();
		Utils.addToolTip(searchFile,
				UIContext.Constants.restoreNameFileNameTooltip());
		container.add(searchFile, td);

		td = new TableData();
		td.setWidth("3%");
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		findButton = new Button(UIContext.Constants.restoreFind());
		findButton.setMinWidth(60);
		findButton.setIcon(UIContext.IconHundle.search());
		findButton.addSelectionListener(selectionListenerFind);
		container.add(findButton, td);
		return container;
	}

	private void changeFindButton(boolean isFind) {
		if (isFind) {
			findButton.setText(UIContext.Constants.restoreFind());
			findButton.removeSelectionListener(selectionListenerCancel);
			findButton.addSelectionListener(selectionListenerFind);
			searching.hideIndicator();
		} else {
			findButton.setText(UIContext.Constants.restoreCancel());
			findButton.removeSelectionListener(selectionListenerFind);
			findButton.addSelectionListener(selectionListenerCancel);
			searching.showIndicator();
		}

	}

	private void getSearchResult(int start) {
		SearchConditionModel model = new SearchConditionModel();
		model.setSearchStr(searchFile.getValue());
		model.setStart(start + 1);
		model.setPageSize(PAGE_SIZE);
		if (!searchCancel) {
			service.getFileFolderBySearch(browseContext.getServer(),
					browseContext.getSessionLocation(),
					browseContext.getMachine(),
					browseContext.getRecoveryPoint(),
					getSelectedNodeFromTree(), model,
					new AsyncCallback<BrowseSearchResultModel>() {

						@Override
						public void onFailure(Throwable caught) {
							searching.hideIndicator();
						}

						@Override
						public void onSuccess(BrowseSearchResultModel result) {
							if (result != null) {
								currentResultSize += result.getResultList()
										.size();
								if (result.isSearchEnd()) {
									itemGridPanel.refreshSearchResult(result
											.getResultList());
									stopSearch();
								} else if (currentResultSize > RESULT_LIMIT) {
									Utils.showMessage(
											UIContext.Constants.productName(),
											MessageBox.INFO,
											UIContext.Messages
													.restoreSearchResultExceed(RESULT_LIMIT));
									stopSearch();
								} else {
									itemGridPanel.refreshSearchResult(result
											.getResultList());
									getSearchResult(currentResultSize);
								}
							}
						}

					});
		}
	}

	private void stopSearch() {
		changeFindButton(true);
		SearchConditionModel model = new SearchConditionModel();
		model.setSearchStr(searchFile.getValue());
		model.setStart(1);
		model.setPageSize(PAGE_SIZE);
		service.stopSearch(browseContext.getServer(),
				browseContext.getSessionLocation(), browseContext.getMachine(),
				browseContext.getRecoveryPoint(), getSelectedNodeFromTree(),
				model, new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Integer result) {

					}

				});
	}

	public void refresh(GridTreeNode parent) {
		if (parent == null) {
			itemGridPanel.refresh(null);
		} else {
			selectedPath.setValue(parent.getCatalogFilePath());
			itemGridPanel.refresh(parent);
		}
	}

	public void setSelectedPath(GridTreeNode parent) {
		selectedPath.setValue(parent.getCatalogFilePath());
	}

	public void refreshTreeState(GridTreeNode node) {
		itemTree.refresh(node);
	}

	public List<GridTreeNode> getSelectedNodes() {
		return selectedPanel.getSelectedNodes();
	}

	private GridTreeNode getSelectedNodeFromTree() {
		return itemTree.getSelectedNode();
	}

	private GridTreeNode getRootNode() {
		return itemTree.getRootNode();
	}

	public int getNodeState(GridTreeNode node) {
		return itemGridPanel.getNodeState(node);
	}

	public boolean isNodeExist(GridTreeNode node) {
		return itemGridPanel.isNodeExist(node);
	}

	public void deselectItemGridNode(GridTreeNode node) {
		itemGridPanel.deselectItem(node);
	}

	public List<GridTreeNode> getSelectedNodeFromItemGrid() {
		return itemGridPanel.getSelectedNodes();
	}

	public void refreshSelectedPanel(GridTreeNode node) {
		selectedPanel.refresh(node);
	}

	public boolean isParentSelected(GridTreeNode node) {
		return selectedPanel.isParentSelected(node);
	}

	public void addSelectedNode(List<GridTreeNode> selectedList) {
		selectedPanel.addSelectedList(selectedList);
	}

	public boolean isSelected(GridTreeNode node) {
		return selectedPanel.isSelected(node);
	}

	public Window getWindow() {
		return window;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

}
