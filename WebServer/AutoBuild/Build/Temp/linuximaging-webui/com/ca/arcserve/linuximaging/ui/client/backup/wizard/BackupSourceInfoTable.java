package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.BaseSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.VolumeInfoModel;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;

public class BackupSourceInfoTable extends LayoutContainer {

	private ListStore<NodeModel> gridStore;
	public ColumnModel columnsModel;
	private Grid<NodeModel> sourceInfoGrid;
	private final int COLUMN_LENGTH = 100;
	private ServiceInfoModel serviceInfo;
	private boolean isSummary = false;

	// private BackupSource parentSource;

	public BackupSourceInfoTable(BackupSource parent, int TABLE_HIGHT,
			int TABLE_WIDTH) {
		// this.parentSource = parent;
		// this.setLayout(new FitLayout());
		// this.setScrollMode(Scroll.AUTOY);
		TableLayout layout = new TableLayout();
		layout.setColumns(1);
		layout.setWidth("100%");
		// layout.setHeight("100%");
		// this.setHeight(340);
		this.setLayout(layout);
		gridStore = new ListStore<NodeModel>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("server",
				UIContext.Constants.toolBarAddNodeByHostname(),
				COLUMN_LENGTH + 40, null));
		GridCellRenderer<NodeModel> user = new GridCellRenderer<NodeModel>() {

			@Override
			public Object render(NodeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NodeModel> store, Grid<NodeModel> grid) {
				if (model != null) {
					String specialUser = model.getUserName();
					int pos = -1;
					if ((pos = specialUser.indexOf('\t')) != -1) {
						return Utils.createLabelField(specialUser.substring(0,
								pos));
					} else {
						return Utils.createLabelField(specialUser);
					}
				}
				return null;
			}
		};
		configs.add(Utils.createColumnConfig("user",
				UIContext.Constants.userName(), COLUMN_LENGTH, user));

		GridCellRenderer<NodeModel> status = new GridCellRenderer<NodeModel>() {

			@Override
			public Object render(NodeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NodeModel> store, Grid<NodeModel> grid) {
				if (model == null) {
					return null;
				}

				final NodeModel node = model;
				int type = Utils.getNodeConnectionMessageType(model.connInfo);
				IconButton warningImage = null;
				if (type == 0) {
					warningImage = new IconButton("backup_source_add_success");
				} else if (type == 1) {
					warningImage = new IconButton("activity_log_status_error");
				} else if (type == 2) {
					warningImage = new IconButton("activity_log_status_warning");
				} else if (!isSummary) {
					Anchor link = new Anchor(
							UIContext.Constants.clickToGetMoreInfo());
					link.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							BackupSourceValidate validator = new BackupSourceValidate(
									serviceInfo, BackupSourceInfoTable.this);
							validator.Validate(node);
						}
					});
					return link;
				}

				if (warningImage != null) {
					Utils.addToolTip(warningImage, Utils
							.getNodeConnectionMessage(model.connInfo, model
									.getServerName(), serviceInfo == null ? ""
									: serviceInfo.getServer(), false));
				}
				return warningImage;
			}
		};
		ColumnConfig statusClm = Utils.createColumnConfig("status",
				UIContext.Constants.status(), COLUMN_LENGTH + 80, status);
		configs.add(statusClm);

		GridCellRenderer<NodeModel> excludeVolumes = new GridCellRenderer<NodeModel>() {

			@Override
			public Object render(final NodeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NodeModel> store, Grid<NodeModel> grid) {
				if (model == null)
					return null;

				if (isSummary) {
					if (model.excludeVolumes == null
							|| model.excludeVolumes.size() == 0) {
						return null;
					}
					String excludes = "";
					if (model.isExclude()) {
						excludes = UIContext.Constants.exclude();
					} else {
						excludes = UIContext.Constants.include();
					}
					for (VolumeInfoModel vm : model.excludeVolumes) {
						if (!excludes.isEmpty()) {
							excludes += " : ";
						}
						excludes += vm.getMountPoint();
					}
					return Utils.createLabelField(excludes);
				} else {
					if (model.connInfo == null)
						return null;
					Button browseVolume = new Button();
					browseVolume
							.addSelectionListener(new SelectionListener<ButtonEvent>() {

								@Override
								public void componentSelected(ButtonEvent ce) {
									sourceInfoGrid.getSelectionModel().select(
											model, false);
									NodeModel node = model;
									if (node.connInfo == null)
										return;
									VolumeInfoTable volumeInfo = new VolumeInfoTable(
											BackupSourceInfoTable.this, node);
									volumeInfo.setSize(510, 400);
									volumeInfo.setModal(true);
									volumeInfo.show();
								}
							});

					if (model.excludeVolumes == null
							|| model.excludeVolumes.size() == 0) {
						browseVolume.setIcon(UIContext.IconHundle.volume());
						Utils.addToolTip(browseVolume,
								UIContext.Messages.noExcludeVolumesForNode());
					} else {
						browseVolume.setIcon(UIContext.IconHundle
								.volume_filter());
						String excludes = "";
						for (VolumeInfoModel vm : model.excludeVolumes) {
							excludes += vm.getMountPoint();
							excludes += "<br>";
						}
						Utils.addToolTip(browseVolume, UIContext.Messages
								.excludeVolumesForNode(excludes));
					}
					return browseVolume;
				}
			}
		};
		ColumnConfig excludVlmClm = Utils.createColumnConfig("exclude",
				UIContext.Constants.volumeFilter(), COLUMN_LENGTH + 30,
				excludeVolumes);
		configs.add(excludVlmClm);

		GridCellRenderer<NodeModel> priority = new GridCellRenderer<NodeModel>() {

			@Override
			public Object render(final NodeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<NodeModel> store, Grid<NodeModel> grid) {
				if (model == null)
					return null;
				if (isSummary) {
					return Utils.createLabelField(getPriorityText(model
							.getPriority() == null ? 0 : model.getPriority()));
				} else {
					final BaseSimpleComboBox<String> p = new BaseSimpleComboBox<String>();
					p.add(UIContext.Constants.priority_high());
					p.add(UIContext.Constants.priority_medium());
					p.add(UIContext.Constants.priority_low());
					if (model.getPriority() != null && model.getPriority() >= 0) {
						p.setSimpleValue(getPriorityText(model.getPriority()));
					} else {
						p.setSimpleValue(UIContext.Constants.priority_high());
					}
					p.setWidth(70);
					p.setEditable(false);
					p.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

						@Override
						public void selectionChanged(
								SelectionChangedEvent<SimpleComboValue<String>> se) {
							model.setPriority(getPriorityValue(se
									.getSelectedItem().getValue()));
						}

					});
					p.addListener(Events.KeyDown, new Listener<FieldEvent>() {

						@Override
						public void handleEvent(FieldEvent be) {
							if (be.getKeyCode() == 32) {
								be.cancelBubble();
							}
						}
					});
					return p;
				}
			}
		};
		ColumnConfig priorityClm = Utils.createColumnConfig("priority",
				UIContext.Constants.priority(), COLUMN_LENGTH, priority);
		configs.add(priorityClm);
		columnsModel = new ColumnModel(configs);
		sourceInfoGrid = new BaseGrid<NodeModel>(gridStore, columnsModel);
		sourceInfoGrid.setTrackMouseOver(true);
		sourceInfoGrid.getSelectionModel()
				.setSelectionMode(SelectionMode.MULTI);
		sourceInfoGrid.setBorders(true);
		sourceInfoGrid.setHeight(TABLE_HIGHT);
		sourceInfoGrid.setWidth(TABLE_WIDTH);
		// sourceInfoGrid.setAutoExpandColumn("server");
		add(sourceInfoGrid);
	}

	public void addData(List<NodeModel> list) {
		if (list != null && list.size() > 0) {
			for (NodeModel info : list) {
				gridStore.add(info);
			}
			sourceInfoGrid.reconfigure(gridStore, columnsModel);
		}

	}

	public void addData(NodeModel info) {
		if (info != null) {
			for (int i = 0; i < gridStore.getCount(); ++i) {
				NodeModel server = gridStore.getAt(i);
				if (server != null
						&& server.getServerName().equalsIgnoreCase(
								info.getServerName())) {
					server.setUserName(info.getUserName());
					server.setPasswd(info.getPasswd());
					server.connInfo = info.connInfo;
					gridStore.update(server);
					sourceInfoGrid.reconfigure(gridStore, columnsModel);
					return;
				}
			}

			gridStore.add(info);
			sourceInfoGrid.reconfigure(gridStore, columnsModel);
		}

	}

	public void updateExcludeVolumes(List<VolumeInfoModel> volumes,
			Boolean isExclude) {
		NodeModel node = sourceInfoGrid.getSelectionModel().getSelectedItem();
		if (node != null) {
			node.excludeVolumes = volumes;
			node.setExclude(isExclude);
			gridStore.update(node);
			sourceInfoGrid.reconfigure(gridStore, columnsModel);
		}
	}

	public void removeData(List<NodeModel> serverList) {
		List<NodeModel> infoList = sourceInfoGrid.getSelectionModel()
				.getSelectedItems();
		if (infoList != null && infoList.size() > 0) {
			for (NodeModel info : infoList) {
				serverList.remove(info);
				gridStore.remove(info);
			}
			sourceInfoGrid.reconfigure(gridStore, columnsModel);
		}
	}

	public void removeAllData() {
		gridStore.removeAll();
		sourceInfoGrid.reconfigure(gridStore, columnsModel);
	}

	public int getCountOfItems() {
		return gridStore.getCount();
	}

	public List<NodeModel> getSelectedItems() {
		return sourceInfoGrid.getSelectionModel().getSelectedItems();
	}

	public ServiceInfoModel getServiceInfo() {
		return serviceInfo;
	}

	public void setServiceInfo(ServiceInfoModel serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	public void setSummary(boolean isSummary) {
		this.isSummary = isSummary;
	}

	private int getPriorityValue(String priorityText) {
		if (UIContext.Constants.priority_high().equals(priorityText)) {
			return 0;
		} else if (UIContext.Constants.priority_medium().equals(priorityText)) {
			return 1;
		} else {
			return 2;
		}
	}

	private String getPriorityText(int priority) {
		if (priority == 0) {
			return UIContext.Constants.priority_high();
		} else if (priority == 1) {
			return UIContext.Constants.priority_medium();
		} else {
			return UIContext.Constants.priority_low();
		}
	}
}
