package com.ca.arcserve.linuximaging.ui.client.restore.search;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointItemModel;
import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.restore.search.model.SearchResultModel;
import com.ca.arcserve.linuximaging.ui.client.restore.search.model.VolResultModel;
import com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint;
import com.ca.arcserve.linuximaging.webservice.data.RecoveryPointItem;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RestoreBySearchPanel extends LayoutContainer {
	final SearchServiceAsync service = GWT.create(SearchService.class);
	public final static int CELL_PADDING = 1;
	public final static int CELL_SPACING = 1;
	public final static int MAX_FIELD_WIDTH = 300;
	public static int MIN_BUTTON_WIDTH = 80;
	public final static int DEFAULT_PAGE_SIZE = 10;

	private BaseSimpleComboBox<String> cmbMachine;
	private TextField<String> fileFolderTxtField;
	private TextField<String> searchPathTxtField;
	private CheckBox ckbSubDir;
	private GroupingStore<SearchResultModel> store;
	private RestoreBySearchPanel instance;

	private BasePagingLoader<PagingLoadResult<ModelData>> loader;
	private GroupingView view;
	
	private List<SearchResultModel> selectedFile = new ArrayList<SearchResultModel>();
	CheckBoxSelectionModel<SearchResultModel> sm;
	
	public RestoreBySearchPanel() {
		CreateSearchRestorePanel();
	}
	
	public boolean validate() {
		updateSelectedList();
		return !selectedFile.isEmpty();
	}
	public String getBackupLocation() {
		return "192.168.199.147:" + selectedFile.get(0).getBackupLocation();
	}
	
	public String getMachine() {
		return selectedFile.get(0).getMachine();
	}
	
	public RecoveryPointModel getRecoveryPointModel() {
		RecoveryPointModel model = new RecoveryPointModel();
		RecoveryPointModel point = selectedFile.get(0).getPoint();
		model.setBackupType(point.getBackupType());
		model.setName(point.getName());
		model.setCompression(point.getCompression());
		model.setEncryptAlgoName(point.getEncryptAlgoName());
		model.setEncryptionPassword("a");
		model.setBootVolumeExist(point.getBootVolumeExist());
		model.setBootVolumeBackup(point.getBootVolumeBackup());
		model.setRootVolumeBackup(point.getRootVolumeBackup());
		model.setTime(point.getTime());
		model.items = new ArrayList<RecoveryPointItemModel>();
		RecoveryPointItemModel rpModel = new RecoveryPointItemModel();
		rpModel.setName(point.items.get(0).getName());
		rpModel.setSize(point.items.get(0).getSize());
		model.items.add(rpModel);

		model.files = new ArrayList<GridTreeNode>();
		GridTreeNode node = new GridTreeNode();
		node.setCatalogFilePath(selectedFile.get(0).getName());
		model.files.add(node);

		return model;
	}

	private void CreateSearchRestorePanel() {
		instance = this;
		TableLayout layout = new TableLayout();
		layout.setWidth("97%");
		layout.setColumns(3);
		layout.setCellPadding(CELL_PADDING);
		layout.setCellSpacing(CELL_SPACING);
		setLayout(layout);

		TableData tdColspan = new TableData();
		tdColspan.setColspan(3);
		tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField head = new LabelField(UIContext.Constants.search());
		head.setStyleAttribute("font-weight", "bold");
		add(head, tdColspan);

		TableLayout fieldSetLayout = new TableLayout();
		fieldSetLayout.setWidth("97%");
		fieldSetLayout.setColumns(3);
		fieldSetLayout.setCellPadding(CELL_PADDING);
		fieldSetLayout.setCellSpacing(CELL_SPACING);

		TableData tdLabel = new TableData();
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdButton = new TableData();
		tdButton.setHorizontalAlign(HorizontalAlignment.LEFT);

		LabelField machineLocation = new LabelField(
				UIContext.Constants.backupMachineLocation());
		machineLocation.setWidth(MAX_FIELD_WIDTH / 2);

		cmbMachine = new BaseSimpleComboBox<String>();
		cmbMachine.setEditable(true);
		// Utils.addToolTip(cmbBackupServer, UIContext.Constants.Tooltip());
		cmbMachine.setWidth(MAX_FIELD_WIDTH);
		loadMachines();

		Button refreshButton = new Button(
				UIContext.Constants.refreshBackupMachine());
		refreshButton.setWidth(MIN_BUTTON_WIDTH);
		refreshButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						loadMachines();
					}

				});

		TableLayout whatToSearchLayout = new TableLayout();
		whatToSearchLayout.setWidth("97%");
		whatToSearchLayout.setColumns(3);
		whatToSearchLayout.setCellPadding(CELL_PADDING);
		whatToSearchLayout.setCellSpacing(CELL_SPACING);

		FieldSet whatToSearchFieldSet = new FieldSet();
		whatToSearchFieldSet.setHeading(UIContext.Constants.whatToSearch());
		whatToSearchFieldSet.setAutoHeight(true);
		whatToSearchFieldSet.setLayout(whatToSearchLayout);

		whatToSearchFieldSet.add(machineLocation, tdLabel);
		whatToSearchFieldSet.add(cmbMachine, tdField);
		whatToSearchFieldSet.add(refreshButton, tdButton);

		LabelField lbfSearchPath = new LabelField(
				UIContext.Constants.searchPath());
		lbfSearchPath.setWidth(MAX_FIELD_WIDTH / 2);
		whatToSearchFieldSet.add(lbfSearchPath, tdLabel);

		searchPathTxtField = new TextField<String>();
		searchPathTxtField.setWidth(MAX_FIELD_WIDTH);
		whatToSearchFieldSet.add(searchPathTxtField, tdField);

		LabelField lblHide = new LabelField();
		lblHide.setVisible(false);
		whatToSearchFieldSet.add(lblHide);

		LabelField fileFolderName = new LabelField(
				UIContext.Constants.fileFolderName());
		fileFolderName.setWidth(MAX_FIELD_WIDTH / 2);
		whatToSearchFieldSet.add(fileFolderName, tdLabel);

		fileFolderTxtField = new TextField<String>();
		fileFolderTxtField.setWidth(MAX_FIELD_WIDTH);
		whatToSearchFieldSet.add(fileFolderTxtField, tdField);

		Button findButton = new Button(UIContext.Constants.find());
		findButton.setWidth(MIN_BUTTON_WIDTH);
		findButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				loader.load(0, DEFAULT_PAGE_SIZE);
			}

		});
		whatToSearchFieldSet.add(findButton, tdButton);

		ckbSubDir = new CheckBox();
		ckbSubDir.setBoxLabel(UIContext.Constants.includeSubDir());
		ckbSubDir.setValue(true);
		whatToSearchFieldSet.add(ckbSubDir, tdLabel);

		add(whatToSearchFieldSet, tdColspan);

		LabelField lbfSelectVersion = new LabelField(
				UIContext.Constants.selectVersion());
		lbfSelectVersion.setStyleAttribute("font-weight", "bold");
		add(lbfSelectVersion, tdColspan);

		add(createResultPanel(), tdColspan);
	}

	private void loadMachines() {
		instance.mask();
		cmbMachine.removeAll();

		cmbMachine.add(UIContext.Constants.allMachines());
		cmbMachine.setValue(cmbMachine.getStore().getAt(0));
		service.getMachineList(new AsyncCallback<List<VolResultModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				instance.unmask();
			}

			@Override
			public void onSuccess(List<VolResultModel> result) {
				if (result != null && !result.isEmpty()) {
					for (VolResultModel v : result) {
						cmbMachine.add(v.getHost());
					}
				}
				instance.unmask();
			}

		});

	}

	private void search(PagingLoadConfig config,
			AsyncCallback<PagingLoadResult<SearchResultModel>> callback) {

		String machine;
		machine = cmbMachine.getSelectedText();

		String fileKeyWords = fileFolderTxtField.getValue();
		String pathKeyWords = searchPathTxtField.getValue();

		service.search(config, machine, pathKeyWords, fileKeyWords,
				ckbSubDir.getValue(), callback);
	}

	private ContentPanel createResultPanel() {

		RpcProxy<PagingLoadResult<SearchResultModel>> proxy = new RpcProxy<PagingLoadResult<SearchResultModel>>() {

			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<SearchResultModel>> callback) {
				PagingLoadConfig config = (PagingLoadConfig) loadConfig;
				config.setLimit(DEFAULT_PAGE_SIZE);
				if (config.getOffset() == 0)
					search(config, callback);
				else
					service.getNextPage(config, callback);
			}
		};

		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);

		store = new GroupingStore<SearchResultModel>(loader);
		store.groupBy("name");
		store.addStoreListener(new StoreListener<SearchResultModel>() {
			@Override
			public void storeBeforeDataChanged(StoreEvent<SearchResultModel> se) {
				onBeforeLoad(se);
			}

			@Override
			public void storeDataChanged(StoreEvent<SearchResultModel> se) {
				onLoad(se);
			}
		});
		/* addTestData(store); */

		ColumnConfig name = new ColumnConfig("name",
				UIContext.Constants.restoreGridName(), 200);
		ColumnConfig sessionid = new ColumnConfig("sessionid",
				UIContext.Constants.restoreGridSessionid(), 80);
		ColumnConfig size = new ColumnConfig("size",
				UIContext.Constants.restoreGridSize(), 80);
		ColumnConfig date = new ColumnConfig("modifyDate",
				UIContext.Constants.restoreGridDate(), 120);

		view = new GroupingView() {
			private String checkedStyle = "x-grid3-group-check";
			private String uncheckedStyle = "x-grid3-group-uncheck";

			@Override
			protected void onMouseDown(GridEvent<ModelData> ge) {
				El hd = ge.getTarget(".x-grid-group-hd", 10);
				El target = ge.getTargetEl();
				if (hd != null && target.hasStyleName(uncheckedStyle)
						|| target.hasStyleName(checkedStyle)) {
					boolean checked = !ge.getTargetEl().hasStyleName(
							uncheckedStyle);
					checked = !checked;
					if (checked) {
						ge.getTargetEl().replaceStyleName(uncheckedStyle,
								checkedStyle);
					} else {
						ge.getTargetEl().replaceStyleName(checkedStyle,
								uncheckedStyle);
					}

					Element group = (Element) findGroup(ge.getTarget());
					if (group != null) {
						NodeList<Element> rows = El.fly(group).select(
								".x-grid3-row");
						List<ModelData> temp = new ArrayList<ModelData>();
						for (int i = 0; i < rows.getLength(); i++) {
							Element r = rows.getItem(i);
							int idx = findRowIndex(r);
							ModelData m = grid.getStore().getAt(idx);
							temp.add(m);
						}
						if (checked) {
							grid.getSelectionModel().select(temp, true);
						} else {
							grid.getSelectionModel().deselect(temp);
						}
					}
					return;
				}
				super.onMouseDown(ge);
			}
		};
		sm = new CheckBoxSelectionModel<SearchResultModel>() {

			@Override
			protected void doSelect(List<SearchResultModel> models,
					boolean keepExisting, boolean supressEvent) {
				NodeList<com.google.gwt.dom.client.Element> groups = view
						.getGroups();

				search: for (int i = 0; i < groups.getLength(); i++) {
					com.google.gwt.dom.client.Element group = groups.getItem(i);
					NodeList<Element> rows = El.fly(group).select(
							".x-grid3-row");
					for (int j = 0, len = rows.getLength(); j < len; j++) {
						Element r = rows.getItem(j);
						int idx = grid.getView().findRowIndex(r);
						SearchResultModel m = grid.getStore().getAt(idx);

						if (!isInSameGroup(models, m))
							continue search;

						if (isSelected(m)) {
							deselect(m);
						}
					}
				}
				super.doSelect(models, keepExisting, supressEvent);
			}

			private boolean isInSameGroup(List<SearchResultModel> models,
					SearchResultModel m) {
				for (SearchResultModel model : models) {
					if (model.getName().equals(m.getName()))
						return true;
				}
				return false;
			}
		};
		sm.setSelectionMode(SelectionMode.SIMPLE);

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(sm.getColumn());
		configs.add(name);
		configs.add(sessionid);
		configs.add(size);
		configs.add(date);

		final ColumnModel cm = new ColumnModel(configs);

		Grid<SearchResultModel> grid = new Grid<SearchResultModel>(store, cm);
		grid.setView(view);
		grid.setBorders(true);
		grid.setAutoExpandColumn("name");
		grid.setAutoExpandMax(3000);
		grid.addPlugin(sm);
		grid.setSelectionModel(sm);

		final PagingToolBar toolBar = new PagingToolBar(DEFAULT_PAGE_SIZE);
		toolBar.bind(loader);
		toolBar.disable();
		ContentPanel panel = new ContentPanel();
		panel.setFrame(false);
		panel.setCollapsible(true);
		panel.setAnimCollapse(false);
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		panel.setLayout(new FitLayout());
		panel.add(grid);
		panel.setHeight(200);
		panel.setBottomComponent(toolBar);
		panel.setCollapsible(false);
		panel.setHeaderVisible(false);
		panel.setBorders(false);

		return panel;
	}
	
	protected void onBeforeLoad(StoreEvent<SearchResultModel> se) {
		updateSelectedList();
	}
	
	private void updateSelectedList() {
		List<SearchResultModel> models = store.getModels();
		if (models != null && !models.isEmpty()) {
			for (SearchResultModel m : models) {
				if (sm.isSelected(m)) {
					if (!selectedFile.contains(m))
						selectedFile.add(m);
				} else {
					int nIndex = selectedFile.indexOf(m);
					if (nIndex != -1)
						selectedFile.remove(nIndex);
				}
			}
		}
	}

	protected void onLoad(StoreEvent<SearchResultModel> se) {
		List<SearchResultModel> models = store.getModels();

		if (selectedFile == null || selectedFile.isEmpty() || models == null)
			return;

		for (SearchResultModel m : models) {
			for (SearchResultModel mSelected : selectedFile) {
				if (mSelected.getName().equals(m.getName())
						&& mSelected.getSessionid().equals(m.getSessionid()))
					sm.select(true, m);
			}
		}
	}
}
