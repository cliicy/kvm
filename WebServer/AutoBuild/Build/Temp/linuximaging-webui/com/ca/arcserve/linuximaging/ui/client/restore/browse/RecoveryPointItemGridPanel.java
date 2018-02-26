package com.ca.arcserve.linuximaging.ui.client.restore.browse;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.FlashCheckBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.CatalogModelType;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

public class RecoveryPointItemGridPanel extends LayoutContainer {

	private static final int COUNT_EVERY_PAGE = 25;
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private Grid<GridTreeNode> grid;
	private BrowseContext browseContext;
	private GridTreeNode parent;
	private PagingLoader<PagingLoadResult<ModelData>> loader;
	private ListStore<GridTreeNode> store;
	private HashMap<GridTreeNode, FlashCheckBox> table = new HashMap<GridTreeNode, FlashCheckBox>();
	private ExtPagingToolBar toolBar;
	private ToolBar resultBar;
	private LabelField resultCount;
	private boolean isSearchMode = false;
	private BrowseRecoveryPointPanel parentPanel;
	private ContentPanel panel;

	public RecoveryPointItemGridPanel(BrowseRecoveryPointPanel parentPanel,
			BrowseContext context) {
		this.parentPanel = parentPanel;
		this.browseContext = context;

		RpcProxy<PagingLoadResult<GridTreeNode>> proxy = new RpcProxy<PagingLoadResult<GridTreeNode>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GridTreeNode>> callback) {
				final PagingLoadConfig plc = (PagingLoadConfig) loadConfig;
				service.getPagingGridTreeNode(browseContext.getServer(),
						browseContext.getSessionLocation(),
						browseContext.getMachine(),
						browseContext.getRecoveryPoint(), parent, plc,
						browseContext.getScriptUUID(), callback);
			}

		};

		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
		store = new ListStore<GridTreeNode>(loader);
		store.setStoreSorter(RestoreUtils.initStoreSorter());
		store.setSortField("displayName");
		toolBar = new ExtPagingToolBar(COUNT_EVERY_PAGE) {
			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				this.pageText.setWidth("68px");
				this.pageText.addKeyPressHandler(new KeyPressHandler() {
					@Override
					public void onKeyPress(KeyPressEvent event) {
						int event_key = event.getNativeEvent().getKeyCode();
						if (event.isControlKeyDown()
								|| event_key == KeyCodes.KEY_ENTER
								|| event_key == KeyCodes.KEY_BACKSPACE
								|| event_key == KeyCodes.KEY_DELETE
								|| event_key == KeyCodes.KEY_LEFT
								|| event_key == KeyCodes.KEY_RIGHT
								|| event_key == KeyCodes.KEY_HOME
								|| event_key == KeyCodes.KEY_END) {
							return;
						}
						char key = event.getCharCode();
						if (!Character.isDigit(key)) {
							pageText.cancelKey();
						}
					}

				});
			}

		};
		toolBar.bind(loader);
		toolBar.setWidth(656);
		resultBar = new ToolBar();
		resultBar.setAlignment(HorizontalAlignment.RIGHT);
		resultBar.setSize(656, 29);
		resultBar.setBorders(true);
		LabelField results = new LabelField(UIContext.Constants.results());
		resultCount = new LabelField();
		resultCount.setStyleAttribute("padding-right", "5px");
		resultBar.add(resultCount);
		resultBar.add(results);
		resultBar.setVisible(false);

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSize(656, 28);
		vPanel.add(toolBar);
		vPanel.add(resultBar);
		ColumnModel cm = initColumnModel();
		grid = new Grid<GridTreeNode>(store, cm);
		grid.setHeight(352);
		grid.setLoadMask(true);
		grid.setAutoExpandColumn("displayName");
		grid.setAutoExpandMax(5000);
		grid.mask(UIContext.Constants.loading());

		panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FillLayout(Orientation.HORIZONTAL));
		panel.add(grid);
		panel.setBottomComponent(vPanel);
		if (browseContext.getRestoreType() == RestoreType.SHARE_RECOVERY_POINT) {
			panel.setHeight(402);
		} else {
			panel.setHeight(352);
		}
		add(panel);
	}

	private ColumnModel initColumnModel() {

		ColumnConfig name = new ColumnConfig("displayName",
				UIContext.Constants.browseFileOrFolderName(), 340);
		name.setMenuDisabled(true);
		name.setRenderer(new GridCellRenderer<BaseModelData>() {

			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				LayoutContainer lc = new LayoutContainer();

				TableLayout layout = new TableLayout();
				layout.setColumns(3);
				lc.setLayout(layout);

				final GridTreeNode node = (GridTreeNode) model;
				final FlashCheckBox fcb = new FlashCheckBox();
				FlashCheckBox temp = table.get(node);
				if (temp == null) {
					if (browseContext.getRestoreType() != RestoreType.SHARE_RECOVERY_POINT) {
						table.put(node, fcb);
					}
					if (isSelected(node) || isParentSelected(node)) {
						fcb.setSelectedState(GridTreeNode.FULL);
						node.setSelectState(FlashCheckBox.FULL);
					}
					fcb.addSelectionListener(new SelectionListener<IconButtonEvent>() {
						@Override
						public void componentSelected(IconButtonEvent ce) {
							node.setSelectState(fcb.getSelectedState());
							if (isParentSelected(node)) {
								if (fcb.getSelectedState() == FlashCheckBox.FULL) {
									Utils.showMessage(UIContext.productName,
											MessageBox.WARNING,
											UIContext.Constants.cannotSelect());
									fcb.setSelectedState(GridTreeNode.NONE);
									node.setSelectState(FlashCheckBox.NONE);
								} else {
									Utils.showMessage(UIContext.productName,
											MessageBox.WARNING,
											UIContext.Constants
													.cannotDeselect());
									fcb.setSelectedState(GridTreeNode.FULL);
									node.setSelectState(FlashCheckBox.FULL);
								}
							} else {
								refreshSelectedPanel(node);
								refreshChildrenNode(node);
							}
						}
					});
					if (browseContext.getRestoreType() != RestoreType.SHARE_RECOVERY_POINT) {
						lc.add(fcb);
					}
				} else {
					if (isParentSelected(node)) {
						temp.setSelectedState(GridTreeNode.FULL);
						node.setSelectState(FlashCheckBox.FULL);
					}
					lc.add(temp);
				}

				lc.add(getNodeIcon(node));
				Label name = new Label(getDisplayName(node));
				if (node.getType() == CatalogModelType.Folder) {
					name.setStyleName("folder_link_label");
					ClickHandler handler = new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							refresh(node);
							updateCurrentLocation(node);
						}
					};
					name.addClickHandler(handler);
				}
				lc.add(name);
				lc.addListener(Events.OnClick, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						if (RecoveryPointItemGridPanel.this.grid != null) {
							RecoveryPointItemGridPanel.this.grid
									.getSelectionModel().select(node, false);
						}
					}
				});
				return lc;
			}

		});

		ColumnConfig date = new ColumnConfig("date",
				UIContext.Constants.restoreDateModifiedColumn(), 200);
		date.setRenderer(new GridCellRenderer<BaseModelData>() {
			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				try {
					if (model != null) {
						LabelField label = new LabelField();

						label.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");
						label.setStyleAttribute("white-space", "nowrap");

						Date dateModifed = ((GridTreeNode) model).getDate();
						String strDate = Utils.formatDateToServerTime(
								dateModifed,
								((GridTreeNode) model).getServerTZOffset() != null ? ((GridTreeNode) model)
										.getServerTZOffset() : 0);

						if (strDate != null && strDate.trim().length() > 0) {
							label.setValue(strDate);
							label.setTitle(strDate);
							return label;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error:" + e.getMessage());
				}
				return "";
			}
		});
		date.setMenuDisabled(true);

		ColumnConfig size = new ColumnConfig("size",
				UIContext.Constants.size(), 100);
		size.setMenuDisabled(true);
		size.setRenderer(new GridCellRenderer<GridTreeNode>() {

			@Override
			public Object render(GridTreeNode model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GridTreeNode> store, Grid<GridTreeNode> grid) {
				try {
					if (model != null
							&& model.getType() == CatalogModelType.File
							&& model.getSize() != null) {
						Long value = model.getSize();
						String formattedValue = Utils.bytes2String(value);
						return formattedValue;
					}
				} catch (Exception e) {

				}

				return "";
			}

		});

		return new ColumnModel(Arrays.asList(name, date, size));
	}

	public String getDisplayName(GridTreeNode node) {
		if (isSearchMode)
			return node.getCatalogFilePath();
		else
			return node.getDisplayName();
	}

	public static IconButton getNodeIcon(GridTreeNode node) {

		if (node == null)
			return null;

		IconButton image = null;
		int nodeType = node.getType();
		switch (nodeType) {
		case CatalogModelType.Folder:
			image = new IconButton("folder-icon");
			break;
		case CatalogModelType.File:
			image = new IconButton("file-icon");
			break;
		case CatalogModelType.Link:
			image = new IconButton("exchange_node_icon");
			break;
		default:
			break;
		}
		if (image != null) {
			image.setWidth(20);
			image.setStyleAttribute("font-size", "0");
		}

		return image;

	}

	public int getParentState(GridTreeNode parentNode) {
		int childrenSelectedCount = 0;
		long childrenCount = parentNode.getChildrenCount();
		for (GridTreeNode node : table.keySet()) {
			String fullPath = parentNode.getPath();
			if (parentNode.getPath().equals(UIContext.FILE_SEPATATOR)) {
				fullPath += node.getDisplayName();
			} else {
				fullPath += UIContext.FILE_SEPATATOR + node.getDisplayName();
			}
			if (fullPath.equals(node.getPath())) {
				FlashCheckBox fcb = table.get(node);
				if (fcb.getSelectedState() == GridTreeNode.FULL) {
					childrenSelectedCount++;
				}
			}
		}
		if (childrenSelectedCount == 0) {
			return GridTreeNode.NONE;
		} else if (childrenSelectedCount == childrenCount) {
			return GridTreeNode.FULL;
		} else {
			return GridTreeNode.PARTIAL;
		}
	}

	public void refreshTreeState(GridTreeNode currentNode) {
		parentPanel.refreshTreeState(currentNode);
	}

	public void refreshSelectedPanel(GridTreeNode currentNode) {
		parentPanel.refreshSelectedPanel(currentNode);
	}

	public void refreshChildrenNode(GridTreeNode currentNode) {
		if (currentNode.getSelectState() == FlashCheckBox.NONE) {
			for (GridTreeNode node : table.keySet()) {
				if (node.getCatalogFilePath().contains(
						currentNode.getCatalogFilePath()
								+ UIContext.FILE_SEPATATOR)) {
					node.setSelectState(FlashCheckBox.NONE);
					table.get(node).setSelectedState(FlashCheckBox.NONE);
				}
			}
		}
	}

	public boolean isParentSelected(GridTreeNode currentNode) {
		return parentPanel.isParentSelected(currentNode);
	}

	public boolean isSelected(GridTreeNode currentNode) {
		return parentPanel.isSelected(currentNode);
	}

	public void deselectItem(GridTreeNode node) {
		FlashCheckBox fcb = table.get(node);
		if (fcb != null) {
			for (int i = 0; i < store.getCount(); i++) {
				GridTreeNode tmp = store.getAt(i);
				if (tmp.getCatalogFilePath()
						.contains(node.getCatalogFilePath())) {
					FlashCheckBox fcbTmp = table.get(tmp);
					if (fcbTmp != null) {
						fcbTmp.setSelectedState(FlashCheckBox.NONE);
						tmp.setSelectState(FlashCheckBox.NONE);
					}
				}
			}
			fcb.setSelectedState(FlashCheckBox.NONE);
			node.setSelectState(FlashCheckBox.NONE);
		}
	}

	public int getNodeState(GridTreeNode inputNode) {
		FlashCheckBox fcb = table.get(inputNode);
		if (fcb == null) {
			return GridTreeNode.NONE;
		} else {
			return fcb.getSelectedState();
		}
	}

	public boolean isNodeExist(GridTreeNode inputNode) {
		if (table.get(inputNode) == null) {
			return false;
		} else {
			return true;
		}
	}

	public void refresh(GridTreeNode parent) {
		if (parent == null) {
			grid.unmask();
			return;
		}
		this.parent = parent;
		this.isSearchMode = false;
		toolBar.show();
		resultBar.hide();
		loader.load(0, COUNT_EVERY_PAGE);
	}

	public List<GridTreeNode> getSelectedNodes() {
		return store.getModels();
	}

	public void updateCurrentLocation(GridTreeNode node) {
		parentPanel.setSelectedPath(node);
	}

	public void refreshSearchResult(List<GridTreeNode> searchResult) {
		toolBar.hide();
		resultBar.show();
		if (!isSearchMode)
			store.removeAll();
		this.isSearchMode = true;
		store.add(searchResult);
		resultCount.setText(store.getCount() + "");

	}

	public void removeAllItems() {
		store.removeAll();
	}

	public void changeSelectionState(boolean selectAll) {
		for (GridTreeNode node : store.getModels()) {
			FlashCheckBox temp = table.get(node);
			if (temp != null) {
				if (selectAll)
					temp.setSelectedState(FlashCheckBox.FULL);
				else
					temp.setSelectedState(FlashCheckBox.NONE);
			}
		}
	}

	public class ExtPagingToolBar extends PagingToolBar {

		public ExtPagingToolBar(int pageSize) {
			super(pageSize);
		}

		public int getTotalLength() {
			return totalLength;
		}
	}
}
