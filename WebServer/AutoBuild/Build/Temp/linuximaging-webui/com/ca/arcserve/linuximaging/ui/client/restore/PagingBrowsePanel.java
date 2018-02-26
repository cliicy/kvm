package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.FlashCheckBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.CatalogModelType;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointModel;
import com.ca.arcserve.linuximaging.ui.client.restore.browse.RestoreUtils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

public class PagingBrowsePanel extends LayoutContainer {

	private static final int PAGESIZE = PagingContext.DEFAULTPAGESIZE;
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private PagingContext pContext;
	private LabelField descLabel = new LabelField();
	private PagingBrowsePanel thisPanel;
	private int[] pages = null;
	private int currentPage = 0;
	private HashMap<GridTreeNode, FlashCheckBox> allOpenedMap;
	private RestoreWindow restoreWindow;

	public PagingBrowsePanel(PagingContext pageContext, RestoreWindow restoreWindow) {
		pContext = pageContext;
		this.restoreWindow=restoreWindow;
		if (pContext != null) {
			allOpenedMap = pContext.getChildrenStateMap();
			int num = pContext.calcPages(PAGESIZE);
			if (num >= 0) {
				pages = new int[num];
			}
		}
		thisPanel = this;

	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setStyleAttribute("margin", "4px");

		descLabel.setText(UIContext.Constants.browsePagingDesc());
		descLabel.setStyleAttribute("margin-bottom", "4px");

		RpcProxy<PagingLoadResult<GridTreeNode>> proxy = new RpcProxy<PagingLoadResult<GridTreeNode>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GridTreeNode>> callback) {
				final PagingLoadConfig plc = (PagingLoadConfig) loadConfig;
				final AsyncCallback<PagingLoadResult<GridTreeNode>> fcb = callback;
				AsyncCallback<PagingLoadResult<GridTreeNode>> cb = new AsyncCallback<PagingLoadResult<GridTreeNode>>() {

					@Override
					public void onFailure(Throwable caught) {
						fcb.onFailure(caught);
					}

					@Override
					public void onSuccess(PagingLoadResult<GridTreeNode> result) {
						currentPage = plc.getOffset() / PAGESIZE;
						if (pages.length > currentPage) {
							pages[currentPage]++;
						}
						fcb.onSuccess(result);
					}
				};
				BackupLocationInfoModel sessionLocation= restoreWindow.recoveryPointSettings.getBackupLocationInfoModel();
				String machine= restoreWindow.recoveryPointSettings.getMachine();
				RecoveryPointModel point=restoreWindow.recoveryPointSettings.getSelectedRecoveryPoint();
				String scriptUUID = null;
				if(restoreWindow.isModify){
					scriptUUID = restoreWindow.restoreModel.getUuid();
				}
				service.getPagingGridTreeNode(restoreWindow.currentServer,sessionLocation,machine,point, pContext.getParent(), plc,scriptUUID, cb);
			}
		};

		// loader
		final PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(
				proxy);
		loader.setRemoteSort(true);
		ListStore<GridTreeNode> store = new ListStore<GridTreeNode>(loader);
		ColumnModel cm = this.createColumnModel();
		Grid<GridTreeNode> grid = new Grid<GridTreeNode>(store, cm);
		grid.addListener(Events.Attach,
				new Listener<GridEvent<GridTreeNode>>() {
					public void handleEvent(GridEvent<GridTreeNode> be) {
						loader.load(0, PAGESIZE);
					}
				});

		grid.addListener(Events.CellDoubleClick,
				new Listener<GridEvent<GridTreeNode>>() {

					@Override
					public void handleEvent(GridEvent<GridTreeNode> be) {
						final GridTreeNode clickedTreeNode = be.getModel();
//						if (clickedTreeNode.getChildrenCount() > 0) {
							FlashCheckBox clickedNodeCheckBox = allOpenedMap
									.get(clickedTreeNode);

							PagingContext.handleClick(clickedTreeNode, pContext
									.getChildrenContextMap(), thisPanel,
									clickedNodeCheckBox, false, pContext
											.isRestoreManager(), null);
//						} else {
//
//						}
					}
				});

		grid.setHeight(400);
		grid.setLoadMask(true);
		grid.setBorders(true);
		grid.setAutoExpandColumn("displayName");
		grid.getView().setAutoFill(true);

		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.setHeight(420);
		panel.setCollapsible(false);
		panel.setScrollMode(Scroll.AUTOX);

		panel.setTopComponent(descLabel);

		panel.add(grid);

		PagingToolBar pagingBar = new PagingToolBar(PAGESIZE){	
			@Override
			protected void onRender(Element target, int index) {				
				super.onRender(target, index);
				this.pageText.setWidth("68px");
				this.pageText.addKeyPressHandler(new KeyPressHandler(){
					@Override
					public void onKeyPress(KeyPressEvent event) {
						// TODO Auto-generated method stub
					    char key = event.getCharCode();
					    if(event.isControlKeyDown() || key == KeyCodes.KEY_ENTER || key == KeyCodes.KEY_BACKSPACE || key == KeyCodes.KEY_DELETE){
					    	return;
					    }
					    if(!Character.isDigit(key)){
					    	pageText.cancelKey();
					    }
					}
					
				});
			}			
		};
		pagingBar.bind(loader);
		panel.setBottomComponent(pagingBar);

		add(panel);
	}

	private void openSubFolder(GridTreeNode clickedTreeNode) {
		if (clickedTreeNode == null)
			return;

		FlashCheckBox clickedNodeCheckBox = allOpenedMap.get(clickedTreeNode);

		PagingContext.handleClick(clickedTreeNode, pContext
				.getChildrenContextMap(), thisPanel, clickedNodeCheckBox,
				false, pContext.isRestoreManager(), null);
	}

	private ColumnModel createColumnModel() {
		ColumnConfig nameColumn = new ColumnConfig("displayName", UIContext.Constants.fileName(), 100);
		nameColumn.setMenuDisabled(true);
		nameColumn.setStyle("vertical-align:middle;");
		nameColumn.setRenderer(new GridCellRenderer<ModelData>() {
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {

				LayoutContainer lc = new LayoutContainer();
				TableLayout layout = new TableLayout();
				layout.setColumns(2);
				lc.setLayout(layout);

				final GridTreeNode node = (GridTreeNode) model;
				if (pContext.isRestoreManager()) {
					layout.setColumns(3);
					int defaultSelState = FlashCheckBox.NONE;
					if (pages[currentPage] == 1) {
						FlashCheckBox pcb = pContext.getParentCheckBox();
						if (pcb != null
								&& pcb.getSelectedState() == FlashCheckBox.FULL) {
							defaultSelState = FlashCheckBox.FULL;
						}
					}
					FlashCheckBox fcb = new FlashCheckBox();
					if (allOpenedMap.containsKey(node)) {
						FlashCheckBox tfcb = allOpenedMap.get(node);
						fcb.setSelectedState(tfcb.getSelectedState());
					}
					allOpenedMap.put(node, fcb);

					if (defaultSelState == FlashCheckBox.FULL) {
						fcb.setSelectedState(FlashCheckBox.FULL);
					}

					final FlashCheckBox ffcb = fcb;
					fcb
							.addSelectionListener(new SelectionListener<IconButtonEvent>() {
								@Override
								public void componentSelected(IconButtonEvent ce) {
									if (ffcb.isEnabled() == false)
										return;
									if (pContext != null
											&& pContext.getChildrenContextMap() != null
											&& pContext.getChildrenContextMap()
													.get(node) != null) {
										pContext.getChildrenContextMap()
												.remove(node);
									}
								}
							});

					lc.add(ffcb);
				}

				IconButton image = RestoreUtils.getNodeIcon(node);
				if(image != null)
					lc.add(image);
				if (isNotLeaf(node)) {		
					expandableNodeMap.put(image, node);
					image
							.addSelectionListener(new SelectionListener<IconButtonEvent>() {

								@Override
								public void componentSelected(IconButtonEvent ce) {
									IconButton src = ce.getIconButton();
									GridTreeNode node = expandableNodeMap.get(src);
									if (node != null) {
										openSubFolder(node);
									}

								}
							});
				}

				// ln.setText();
				if (isNotLeaf(node)) {
					Hyperlink ln = new Hyperlink(node.getDisplayName(), false,
							"");
					ln.addClickHandler(nodeclickHandler);
					ln.setStyleName("popupFolderText");
					linkMap.put(ln, node);
					lc.add(ln);
				} else {
					Label la = new Label();
					la.setStyleName("popupFileText");
					la.setText(node.getDisplayName());
					lc.add(la);
				}

				return lc;
			}

			private boolean isNotLeaf(final GridTreeNode node) {
				return node.getType() == CatalogModelType.Folder ||(CatalogModelType.allGRTExchangeTypes.contains(node.getType()) 
						&& node.getType() != CatalogModelType.OT_GRT_EXCH_MESSAGE);				 
			}
			
			ClickHandler nodeclickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Object src = event.getSource();
					if (src instanceof Hyperlink) {
						GridTreeNode node = linkMap.get(src);
						openSubFolder(node);
					}
				}
			};

			HashMap<Hyperlink, GridTreeNode> linkMap = new HashMap<Hyperlink, GridTreeNode>();
			HashMap<IconButton, GridTreeNode> expandableNodeMap = new HashMap<IconButton, GridTreeNode>();
		});

		ColumnConfig sizeColumn = new ColumnConfig("size", UIContext.Constants.size(), 100);
		sizeColumn.setMenuDisabled(true);
		sizeColumn.setStyle("vertical-align:middle;");
		sizeColumn.setAlignment(HorizontalAlignment.RIGHT);
		sizeColumn.setRenderer(new GridCellRenderer<BaseModelData>() {

			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				try {
					if (model != null
							&& (((GridTreeNode) model).getSize() != null )) {
						Long value = ((GridTreeNode) model).getSize();
						String formattedValue = "";
						if( value != null )
							formattedValue = Utils.bytes2String(value);
						return formattedValue;
					}
				} catch (Exception e) {

				}

				return "";
			}

		});

		ColumnConfig dateColumn = new ColumnConfig("date", UIContext.Constants.restoreDateModifiedColumn(), 120);
		dateColumn.setMenuDisabled(true);
		dateColumn.setStyle("vertical-align:middle;");
		dateColumn.setRenderer(new GridCellRenderer<BaseModelData>() {
			@Override
			public Object render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				try {
					if (model != null) {
						Date dateModifed = ((GridTreeNode) model).getDate();
						return Utils.formatDateToServerTime(dateModifed,
								((GridTreeNode)model).getServerTZOffset() != null?
										((GridTreeNode)model).getServerTZOffset() : 0);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error:" + e.getMessage());
				}
				return "";
			}
		});		
	
		List<ColumnConfig> lst = Arrays.asList(nameColumn, sizeColumn, dateColumn);
		for(ColumnConfig cfg : lst)	{
			cfg.setSortable(false);
		}
		ColumnModel cm = new ColumnModel(lst);
		
	
		return cm;
	}

}
