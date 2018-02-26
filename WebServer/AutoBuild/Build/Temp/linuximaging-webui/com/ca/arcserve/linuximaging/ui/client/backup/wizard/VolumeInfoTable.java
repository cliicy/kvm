package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.ArrayList;
import java.util.List;
import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.VolumeInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

public class VolumeInfoTable extends Window {

	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private ListStore<VolumeInfoModel> gridStore;
	private ColumnModel columnsModel;
	private Grid<VolumeInfoModel> volumeInfoGrid;
	private ButtonBar buttonBar;
	private Button okBtn;
	private Button closeBtn;
	private final int COLUMN_LENGTH = 200;
	private static int BUTTON_MIN_WIDTH = 100;
	private List<VolumeInfoModel> excludeVolumes;
	private BackupSourceInfoTable parent;
	private HorizontalPanel notification;
	private Image warningImage;
	private LabelField note;
	private String heading = null;
	private List<String> supportFSList;
	private CheckBoxSelectionModel<VolumeInfoModel> sm;
	private boolean initialized = false;
	private NodeModel node;
	List<String> result = new ArrayList<String>();

	// *****************************
	private final Radio exclude = new Radio();
	private final Radio include = new Radio();
	private final RadioGroup volumSetting = new RadioGroup();

	public VolumeInfoTable(BackupSourceInfoTable parent) {
		this.parent = parent;
		definePanel();
	}

	public VolumeInfoTable(BackupSourceInfoTable parent, NodeModel node) {
		this.parent = parent;
		this.node = node;
		heading = UIContext.Messages.excludeVolumeSetting(node.getServerName());
		definePanel();
		if (node != null) {
			if (node.excludeVolumes != null)
				excludeVolumes = node.excludeVolumes;
			if (node.connInfo != null)
				updateVolumeInfo();
		}
	}

	private void definePanel() {
		this.setLayout(new FitLayout());
		if (heading == null) {
			this.setHeading(UIContext.Constants.excludeVolumes());
		} else {
			this.setHeading(heading);
		}
		this.setResizable(false);
		this.setScrollMode(Scroll.AUTOY);

		LayoutContainer mainPanel = new LayoutContainer();
		mainPanel.setLayout(new RowLayout(Orientation.VERTICAL));
		mainPanel.setStyleAttribute("padding", "5px");
		mainPanel.setStyleAttribute("background-color", "#DFE8F6");

		// **************
		exclude.setBoxLabel(UIContext.Constants.excludeVolumes());
		exclude.setValueAttribute("exclude");

		include.setBoxLabel(UIContext.Constants.includeVolumes());
		include.setValueAttribute("include");

		volumSetting.add(exclude);
		volumSetting.add(include);

		volumSetting.setWidth(200);
		volumSetting.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {

				if (initialized) {
					excludeVolumes = volumeInfoGrid.getSelectionModel()
							.getSelectedItems();
				}

				if (exclude.getValue()) {
					addData(node.connInfo.volumes);
					for (VolumeInfoModel info : node.connInfo.volumes) {
						if (!isSupported(info, supportFSList)) {
							sm.select(info, true);
						}
						if (needExcluded(info)) {
							sm.select(info, true);
						}
					}
					if (excludeVolumes != null && excludeVolumes.size() == 0) {
						hideWarning();
					}
				} else {
					List<VolumeInfoModel> showList = new ArrayList<VolumeInfoModel>();
					for (VolumeInfoModel info : node.connInfo.volumes) {
						if (isSupported(info, supportFSList)) {
							showList.add(info);
						}
					}
					addData(showList);
					for (VolumeInfoModel info : showList) {
						if (needExcluded(info)) {
							sm.select(info, true);
						}
					}

					if (excludeVolumes != null && excludeVolumes.size() == 0) {
						showWarning(UIContext.Messages
								.includeWholeMachineRestoreWarning(result
										.toString()));
					}
				}
				initialized = true;
			}
		});
		mainPanel.add(volumSetting, new RowData(1, 27));
		// *********************
		sm = new CheckBoxSelectionModel<VolumeInfoModel>();
		sm.setSelectionMode(SelectionMode.SIMPLE);
		gridStore = new ListStore<VolumeInfoModel>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(sm.getColumn());
		configs.add(Utils.createColumnConfig("mountPoint",
				UIContext.Constants.volumeName(), COLUMN_LENGTH, null));
		configs.add(Utils.createColumnConfig("fileSystem",
				UIContext.Constants.fileSystem(), COLUMN_LENGTH, null));
		configs.add(Utils.createColumnConfig("fsType",
				UIContext.Constants.type(), COLUMN_LENGTH / 2, null));

		columnsModel = new ColumnModel(configs);
		volumeInfoGrid = new BaseGrid<VolumeInfoModel>(gridStore, columnsModel);
		volumeInfoGrid.setAutoExpandColumn("mountPoint");
		volumeInfoGrid.setSelectionModel(sm);
		volumeInfoGrid.setBorders(true);
		volumeInfoGrid.addPlugin(sm);
		
		sm.addSelectionChangedListener(new SelectionChangedListener<VolumeInfoModel>() {
			@Override
			public void selectionChanged(
					SelectionChangedEvent<VolumeInfoModel> se) {
				List<VolumeInfoModel> volumes = se.getSelection();

				if (initialized && exclude.getValue()) {
					alwaysSelectedItems(volumes);
				}

				List<String> warningVolumes = new ArrayList<String>();
				for (VolumeInfoModel volume : volumes) {
					if (volume.getMountPoint().equals("/")) {
						warningVolumes.add("/");
					} else if (volume.getMountPoint().equals("/boot")) {
						warningVolumes.add("/boot");
					}
				}

				List<String> node = new ArrayList<String>();
				node.addAll(result);

				if (warningVolumes.size() > 0 && exclude.getValue()) {
					showWarning(UIContext.Messages
							.wholeMachineRestoreWarning(warningVolumes
									.toString()));
				} else if (warningVolumes.size() == 0 && exclude.getValue()) {
					hideWarning();
				} else if (warningVolumes.size() == result.size()
						&& !exclude.getValue()) {
					hideWarning();
				} else if (!exclude.getValue()) {
					node.removeAll(warningVolumes);
					if (node.size() != 0) {
						showWarning(UIContext.Messages
								.includeWholeMachineRestoreWarning(node
										.toString()));
					} else {
						hideWarning();
					}
				}
			}
		});
	
		mainPanel.add(volumeInfoGrid, new RowData(1, 288));
		notification = defineNotificationSet();
		notification.hide();
		mainPanel.add(notification, new RowData(1, 20));
		buttonBar = new ButtonBar();
		okBtn = new Button(UIContext.Constants.OK());
		closeBtn = new Button(UIContext.Constants.cancel());
		Button help = new Button(UIContext.Constants.help());
		okBtn.setMinWidth(BUTTON_MIN_WIDTH);
		closeBtn.setMinWidth(BUTTON_MIN_WIDTH);
		help.setMinWidth(BUTTON_MIN_WIDTH);

		buttonBar.setAlignment(HorizontalAlignment.CENTER);
		buttonBar.setStyleAttribute("background-color", "#DFE8F6");
		buttonBar.setSpacing(5);
		buttonBar.add(okBtn);
		buttonBar.add(closeBtn);
		buttonBar.add(help);
		okBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				excludeVolumes = volumeInfoGrid.getSelectionModel()
						.getSelectedItems();
				Boolean isExcluse = exclude.getValue();
				if (excludeVolumes.size() == 0 && !isExcluse) {
					Utils.showMessage(UIContext.Constants.productName(),
							MessageBox.ERROR,
							UIContext.Constants.includeAnyVolumes());
					return;
				}

				if (excludeVolumes.size() == gridStore.getCount() && isExcluse) {
					Utils.showMessage(UIContext.Constants.productName(),
							MessageBox.ERROR,
							UIContext.Constants.excludeAllVolumes());
					return;
				}
				parent.updateExcludeVolumes(excludeVolumes, isExcluse);
				VolumeInfoTable.this.hide();
			}
		});
		closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				VolumeInfoTable.this.hide();
			}
		});
		help.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Utils.showURL(UIContext.helpLink
						+ HelpLinkItem.BACKUP_WIZARD_SOURCE_EXCLUDE);
			}
		});
		
		this.add(mainPanel);
		this.setBottomComponent(buttonBar);
	}

	private boolean needExcluded(VolumeInfoModel vm) {
		if (excludeVolumes != null && vm != null) {
			for (VolumeInfoModel volume : excludeVolumes) {
				if (vm.getMountPoint().equals(volume.getMountPoint())) {
					return true;
				}
			}
		}
		return false;
	}

	private HorizontalPanel defineNotificationSet() {
		HorizontalPanel notificationSet = new HorizontalPanel();
		notificationSet.setStyleAttribute("margin-top", "5px");

		warningImage = new Image(UIContext.IconBundle.warning());
		warningImage.setVisible(false);

		notificationSet.add(warningImage);
		note = new LabelField("");
		note.setWidth("100%");
		notificationSet.add(note);

		return notificationSet;
	}

	private void showWarning(String msg) {
		note.setText(msg);
		notification.show();
	}

	private void hideWarning() {
		notification.hide();
	}

	private boolean isSupported(VolumeInfoModel volume, List<String> supportList) {
		if (supportList != null && volume != null) {
			for (String type : supportList) {
				if (volume.getType().equals(type)) {
					return true;
				}
			}
		}
		return false;
	}

	private void updateVolumeInfo() {
		if (node.connInfo.volumes != null && node.connInfo.volumes.size() > 0) {
			volumeInfoGrid.mask(UIContext.Constants.loading());
			service.getSupportedFSType(parent.getServiceInfo(),
					new BaseAsyncCallback<List<String>>() {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							supportFSList = null;
							if (node.isExclude() == null || node.isExclude()) {
								volumSetting.setValue(exclude);
							} else {
								volumSetting.setValue(include);
							}
							volumeInfoGrid.unmask();
						}

						@Override
						public void onSuccess(List<String> result) {
							supportFSList = result;
							if (node.isExclude() == null || node.isExclude()) {
								volumSetting.setValue(exclude);
							} else {
								volumSetting.setValue(include);
							}
							volumeInfoGrid.unmask();
						}
					});
		}
	}

	public void addData(List<VolumeInfoModel> list) {
		gridStore.removeAll();
		result.clear();
		if (list != null && list.size() > 0) {
			for (VolumeInfoModel info : list) {
				if (info.getMountPoint().equals("/")) {
					result.add("/");
				} else if (info.getMountPoint().equals("/boot")) {
					result.add("/boot");
				}
				gridStore.add(info);
			}
			volumeInfoGrid.reconfigure(gridStore, columnsModel);
		}
	}

	public void removeData(List<VolumeInfoModel> serverList) {
		List<VolumeInfoModel> infoList = volumeInfoGrid.getSelectionModel()
				.getSelectedItems();
		if (infoList != null && infoList.size() > 0) {
			for (VolumeInfoModel info : infoList) {
				serverList.remove(info);
				gridStore.remove(info);
			}
			volumeInfoGrid.reconfigure(gridStore, columnsModel);
		}
	}

	public void removeAllData() {
		gridStore.removeAll();
		volumeInfoGrid.reconfigure(gridStore, columnsModel);
	}

	public int getCountOfItems() {
		return gridStore.getCount();
	}

	private void alwaysSelectedItems(List<VolumeInfoModel> selectedList) {
		boolean showMessage = true;
		for (int i = 0; i < gridStore.getCount(); ++i) {
			VolumeInfoModel vm = gridStore.getAt(i);
			if (selectedList != null && selectedList.contains(vm)) {
				continue;
			}

			if (!isSupported(vm, supportFSList)) {
				sm.select(vm, true);
				if (showMessage) {
					showMessage = false;
					Utils.showMessage(
							UIContext.Constants.productName(),
							MessageBox.ERROR,
							UIContext.Messages.notSupportedFSType(
									vm.getMountPoint(), vm.getType()));
				}
			}
		}
	}
}