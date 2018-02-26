package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.BackupScheduleModel;
import com.ca.arcserve.linuximaging.ui.client.model.D2DTimeModel;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.ca.arcserve.linuximaging.ui.client.model.WeeklyScheduleModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public class BackupScheduleSettings extends LayoutContainer {

	private static final int BUTTON_WIDTH = 60;
	private static final int BUTTON_HEIGHT = 25;
	private DateField dateField;
	private Button addButton;
	private Button editButton;
	private Button deleteButton;
	private Button deleteAllButton;

	private TreeStore<BackupScheduleModel> store;
	private TreeGrid<BackupScheduleModel> grid;
	private Map<Integer, BackupScheduleModel> rootItem;
	private boolean isEdit;
	private Date time;
	private ColumnModel cm;

	public BackupScheduleSettings(boolean isEdit, boolean isModify) {
		this.isEdit = isEdit;
		TableLayout layout = new TableLayout();
		layout.setColumns(1);
		layout.setWidth("100%");
		// layout.setHeight("100%");
		// this.setHeight(340);
		this.setLayout(layout);
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setWidth("100%");
		if (isEdit) {

			LayoutContainer container = new LayoutContainer();
			TableLayout tl = new TableLayout();
			tl.setColumns(3);
			tl.setWidth("100%");
			container.setLayout(tl);

			TableData tdLabel = new TableData();
			tdLabel.setWidth("13%");
			LabelField label = new LabelField();
			label.setText(UIContext.Constants.scheduleStartDate());
			label.addStyleName("StartDateSetting");
			container.add(label, tdLabel);

			tdLabel = new TableData();
			tdLabel.setWidth("37%");
			dateField = new DateField();
			dateField.ensureDebugId("C1C54199-7BDB-40d9-94C3-A519662F5C13");
			// Tool tip
			dateField.setMaxValue(Utils.maxDate);
			dateField.setMinValue(Utils.minDate);
			dateField.setEditable(false);
			dateField.getPropertyEditor().setFormat(
					DateTimeFormat.getShortDateFormat());

			container.add(dateField, tdLabel);

			tdLabel = new TableData();
			tdLabel.setWidth("50%");
			tdLabel.setHorizontalAlign(HorizontalAlignment.RIGHT);

			ButtonBar bar = new ButtonBar();
			bar.setAlignment(HorizontalAlignment.RIGHT);
			//bar.setStyleAttribute("padding", "5px");

			addButton = new Button(UIContext.Constants.add());
			addButton.setIcon(AbstractImagePrototype
					.create(UIContext.IconBundle.schedule_new()));
			addButton.setIconAlign(IconAlign.LEFT);
			addButton.setMinWidth(BUTTON_WIDTH);
			addButton.setHeight(BUTTON_HEIGHT);
			addButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {

							final BackupScheduleSubSettings setting = new BackupScheduleSubSettings(
									UIContext.Constants.addBackupSchedule(),
									false);
							setting.addListener(Events.Hide,
									new Listener<BaseEvent>() {

										@Override
										public void handleEvent(BaseEvent be) {
											if (setting.isOKClick())
												addBackupSchedule(setting
														.getBackupScheduleModelList());
										}
									});
							if (time != null) {
								setting.setStartTime(time);
							}
							if (grid.getSelectionModel().getSelectedItem() != null) {
								if (grid.getSelectionModel().getSelectedItem()
										.isRoot()) {
									int day = grid.getSelectionModel()
											.getSelectedItem().getDay();
									setting.setDefaultDay(day);
								}
							}
							setting.show();
						}
					});
			editButton = new Button(UIContext.Constants.modify());
			editButton.setIcon(AbstractImagePrototype
					.create(UIContext.IconBundle.schedule_edit()));
			editButton.setIconAlign(IconAlign.LEFT);
			editButton.setMinWidth(BUTTON_WIDTH);
			editButton.setHeight(BUTTON_HEIGHT);
			editButton.setEnabled(false);
			editButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							final BackupScheduleSubSettings setting = new BackupScheduleSubSettings(
									UIContext.Constants.modifyBackupSchedule(),
									true);
							setting.addListener(Events.Hide,
									new Listener<BaseEvent>() {

										@Override
										public void handleEvent(BaseEvent be) {
											if (setting.isOKClick())
												updateBackupSchedule(setting
														.getBackupScheduleModel());
										}
									});
							setting.refresh(grid.getSelectionModel()
									.getSelectedItem());
							setting.show();
						}
					});
			deleteButton = new Button(UIContext.Constants.delete());
			deleteButton.setIcon(AbstractImagePrototype
					.create(UIContext.IconBundle.schedule_delete()));
			deleteButton.setIconAlign(IconAlign.LEFT);
			deleteButton.setHeight(BUTTON_HEIGHT);
			deleteButton.setMinWidth(BUTTON_WIDTH);
			deleteButton.setEnabled(false);
			deleteButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							deleteBackupSchedule(grid.getSelectionModel()
									.getSelectedItems());
						}
					});

			deleteAllButton = new Button(UIContext.Constants.clear());
			deleteAllButton.setIcon(AbstractImagePrototype
					.create(UIContext.IconBundle.schedule_delete()));
			deleteAllButton.setIconAlign(IconAlign.LEFT);
			deleteAllButton.setHeight(BUTTON_HEIGHT);
			deleteAllButton.setMinWidth(BUTTON_WIDTH);
			deleteAllButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							resetSchedule();
						}
					});
			bar.add(addButton);
			bar.add(editButton);
			bar.add(deleteButton);
			bar.add(deleteAllButton);
			container.add(bar, tdLabel);

			this.add(container, td);
		}
		TreeGridCellRenderer<BackupScheduleModel> timeRenderer = new WidgetTreeGridCellRenderer<BackupScheduleModel>() {

			@Override
			public Widget getWidget(BackupScheduleModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupScheduleModel> store,
					Grid<BackupScheduleModel> grid) {
				if (model.isRoot()) {
					return new LabelField(model.getTimeDispalyMessage());
				} else {
					D2DTimeModel startTime = model.startTime;
					String message = startTime.getHour() + ":"
							+ getMinutesStr(startTime.getMinute()) + " "
							+ getAMPMStr(startTime.getAMPM());
					if (model.isEnabled()) {
						D2DTimeModel endTime = model.endTime;
						message += " " + UIContext.Constants.to() + " "
								+ endTime.getHour() + ":"
								+ getMinutesStr(endTime.getMinute()) + " "
								+ getAMPMStr(endTime.getAMPM());
					}
					return new LabelField(message);

				}
			}

		};

		ColumnConfig time = new ColumnConfig("scheduleTime",
				UIContext.Constants.time(), 320);
		time.setRenderer(timeRenderer);

		GridCellRenderer<BackupScheduleModel> methodRenderer = new GridCellRenderer<BackupScheduleModel>() {

			public Object render(BackupScheduleModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupScheduleModel> store,
					Grid<BackupScheduleModel> grid) {
				if (model.getMethod() == null) {
					return "";
				}
				return Utils.getBackupMethod(model.getMethod());
			}

		};

		ColumnConfig method = new ColumnConfig("scheduleMethod",
				UIContext.Constants.backupType(), 150);
		method.setRenderer(methodRenderer);

		GridCellRenderer<BackupScheduleModel> repeatRenderer = new GridCellRenderer<BackupScheduleModel>() {

			public Object render(BackupScheduleModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BackupScheduleModel> store,
					Grid<BackupScheduleModel> grid) {
				if (model.isRoot()) {
					return "";
				} else {
					if (model.isEnabled() == null || !model.isEnabled()) {
						return UIContext.Constants.scheduleLabelNever();
					}
					return UIContext.Constants.scheduleLabelEvery()
							+ " "
							+ model.getInterval()
							+ " "
							+ model.displayIntervalUnit(model.getIntervalUnit());
				}
			}

		};
		ColumnConfig repeat = new ColumnConfig("scheduleInterval",
				UIContext.Constants.repeat(), 150);
		repeat.setRenderer(repeatRenderer);

		cm = new ColumnModel(Arrays.asList(time, method, repeat));

		store = new TreeStore<BackupScheduleModel>();
		grid = new TreeGrid<BackupScheduleModel>(store, cm);
		// grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.setView(new TreeGridView() {

			@Override
			protected int getVisibleRowCount() {
				int nVisableRowCount = super.getVisibleRowCount();

				return nVisableRowCount;
			}

			@Override
			protected void doUpdate() {
				if (grid == null || !grid.isViewReady()
						|| !this.isBufferEnabled()) {
					return;
				}
				int count = getVisibleRowCount();
				if (count > 0) {
					ColumnModel cm = grid.getColumnModel();

					ListStore<ModelData> store = grid.getStore();
					List<ColumnData> cs = getColumnData();
					boolean stripe = grid.isStripeRows();
					int[] vr = getVisibleRows(count);
					int cc = cm.getColumnCount();
					for (int i = vr[0]; i <= vr[1]; i++) {
						// if row is NOT rendered and is visible, render it
						if (!isRowRendered(i)) {
							List<ModelData> list = new ArrayList<ModelData>();
							list.add(store.getAt(i));
							// fix 149418
							// http://www.sencha.com/forum/showthread.php?176844-GXT-2.2.4-Bug-on-GridCellRenderer-for-TreeGrid
							// widgetList.add(i, new ArrayList<Widget>());
							widgetList.set(i, new ArrayList<Widget>());
							String html = doRender(cs, list, i, cc, stripe,
									true);
							getRow(i).setInnerHTML(html);
							renderWidgets(i, i);
						}
					}
					clean();
				}
			}
		});
		((TreeGridView) grid.getView()).setRowHeight(23);
		grid.setAutoExpand(true);
		// grid.setAutoLoad(true);
		grid.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<BackupScheduleModel>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<BackupScheduleModel> se) {
						if (se.getSelectedItem() != null) {
							if (se.getSelection().size() == 1) {
								editButton.setEnabled(!se.getSelectedItem()
										.isRoot());
							} else {
								editButton.setEnabled(false);
							}
							deleteButton.setEnabled(!se.getSelectedItem()
									.isRoot());
						}
					}

				});
		grid.setTrackMouseOver(false);
		grid.setBorders(true);
		grid.setHeight(280);
		grid.setWidth(610);
		grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		// grid.setHeight("100%");
		// grid.getStyle().setLeafIcon(AbstractImagePrototype.create(UIContext.IconBundle.schedule_item()));
		grid.setIconProvider(new ModelIconProvider<BackupScheduleModel>() {

			@Override
			public AbstractImagePrototype getIcon(BackupScheduleModel model) {
				if (model.isRoot()) {
					return AbstractImagePrototype.create(UIContext.IconBundle
							.schedule_job());
				}
				return AbstractImagePrototype.create(UIContext.IconBundle
						.schedule_item());
			}

		});
		// td = new TableData();
		// td.setWidth("100%");
		/*
		 * LayoutContainer container = new LayoutContainer();
		 * container.setLayout(new FitLayout()); container.setHeight(200);
		 * container.add(grid);
		 */
		this.add(grid, td);
		getDefaultValue();
	}

	private void getDefaultValue() {
		rootItem = new HashMap<Integer, BackupScheduleModel>();

		String[] texts = new String[7];
		texts[0] = UIContext.Constants.selectFirDayOfWeek();
		texts[1] = UIContext.Constants.selectSecDayOfWeek();
		texts[2] = UIContext.Constants.selectThiDayOfWeek();
		texts[3] = UIContext.Constants.selectFouDayOfWeek();
		texts[4] = UIContext.Constants.selectFifDayOfWeek();
		texts[5] = UIContext.Constants.selectSixDayOfWeek();
		texts[6] = UIContext.Constants.selectSevDayOfWeek();

		for (int i = 0; i < 7; i++) {
			BackupScheduleModel day = new BackupScheduleModel();
			day.setTimeDisplayMessage(texts[i]);
			day.setIsRoot(true);
			day.setDay(i);
			store.add(day, false);
			rootItem.put(i + 1, day);
		}
		// if(!isModify){
		setDefaultScheduleValue();
		// }
		// grid.reconfigure(store, grid.getColumnModel());
	}

	public void refresh(WeeklyScheduleModel model) {
		if (isEdit) {
			Date keepDate = new Date(model.startTime.getYear() - 1900,
					model.startTime.getMonth(), model.startTime.getDay());
			dateField.setValue(keepDate);
		}
		resetSchedule();
		addBackupSchedule(model.scheduleList);
	}

	private void addBackupSchedule(List<BackupScheduleModel> list) {
		if (list != null) {
			for (BackupScheduleModel model : list) {
				int day = model.getDay();
				store.add(rootItem.get(day), model, true);
			}
			grid.reconfigure(store, cm);
		}
		// grid.collapseAll();
	}

	private void updateBackupSchedule(BackupScheduleModel model) {
		store.update(model);
		grid.reconfigure(store, cm);
	}

	private void deleteBackupSchedule(List<BackupScheduleModel> modelList) {
		for (BackupScheduleModel model : modelList) {
			if (!model.isRoot()) {
				store.remove(model);
			}
		}
		grid.reconfigure(store, cm);
	}

	private void resetSchedule() {
		for (BackupScheduleModel root : rootItem.values()) {
			store.removeAll(root);
		}
		grid.reconfigure(store, cm);
	}

	public WeeklyScheduleModel save() {
		WeeklyScheduleModel scheduleModel = new WeeklyScheduleModel();
		scheduleModel.scheduleList = getScheduleList();
		scheduleModel.startTime = getStartTime();
		return scheduleModel;
	}

	public List<BackupScheduleModel> getScheduleList() {
		List<BackupScheduleModel> list = new ArrayList<BackupScheduleModel>();
		for (BackupScheduleModel model : store.getAllItems()) {
			if (!model.isRoot()) {
				list.add(model);
			}
		}
		return list;
	}

	public D2DTimeModel getStartTime() {
		D2DTimeModel time = new D2DTimeModel();
		time.setRunNow(false);
		time.setReady(true);
		Date date = dateField.getValue();
		time.setYear(date.getYear() + 1900);
		time.setMonth(date.getMonth());
		time.setDay(date.getDate());
		time.setHour(0);
		time.setMinute(0);
		time.setHourOfDay(0);
		time.setAMPM(0);
		return time;
	}

	private String getMinutesStr(int minutes) {
		String val;
		if (Utils.isMinutePrefix())
			val = Utils.prefixZero(minutes, 2);
		else
			val = new Integer(minutes).toString();
		return val;
	}

	private String getAMPMStr(int amPm) {
		if (amPm == -1) {
			return "";
		} else if (amPm == 0) {
			return UIContext.Constants.scheduleStartTimeAM();
		} else {
			return UIContext.Constants.scheduleStartTimePM();
		}
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public void setStartDate(Date startDate) {
		dateField.setValue(startDate);
	}

	public void setDefaultScheduleValue() {
		D2DTimeModel startTime = new D2DTimeModel();
		if (!Utils.is24Hours()) {
			startTime.setAMPM(1);
		} else {
			startTime.setAMPM(-1);
		}
		startTime.setDay(0);
		startTime.setYear(0);
		startTime.setMonth(0);
		startTime.setHour(10);
		startTime.setHourOfDay(22);
		startTime.setMinute(0);
		startTime.setReady(false);
		startTime.setRunNow(false);

		List<BackupScheduleModel> list = new ArrayList<BackupScheduleModel>();
		for (int i = 0; i < 5; i++) {
			BackupScheduleModel monday = new BackupScheduleModel();
			monday.startTime = startTime;
			monday.setDay(i + 2);
			monday.setEnabled(false);
			monday.setInterval(0);
			monday.setIntervalUnit(0);
			if (i + 2 == 6) {
				monday.setMethod(JobType.BACKUP_FULL.getValue());
			} else {
				monday.setMethod(JobType.BACKUP_INCREMENTAL.getValue());
			}
			monday.setIsRoot(false);
			list.add(monday);
		}

		addBackupSchedule(list);
	}

	public boolean validate() {
		if (this.getScheduleList().size() == 0) {
			Utils.showMessage(UIContext.productName, MessageBox.ERROR,
					UIContext.Constants.selectSchedule());
			return false;
		} else {
			return true;
		}
	}

}
