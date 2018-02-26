package com.ca.arcserve.linuximaging.ui.client.homepage.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class BackupStoragePanel extends DashboardPanel {

	private Grid<BackupLocationInfoModel> grid;
	private ColumnModel cm;
	private ListStore<BackupLocationInfoModel> store;

	public BackupStoragePanel() {
		setHeading(UIContext.Constants.backupStorage());
		setAutoHeight(true);
		setLayout(new FitLayout());
		defineBackupLocationGrid();
		add(grid);
	}

	private void defineBackupLocationGrid() {
		store = new ListStore<BackupLocationInfoModel>();

		GridCellRenderer<BackupLocationInfoModel> type = new GridCellRenderer<BackupLocationInfoModel>() {

			@Override
			public Object render(BackupLocationInfoModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<BackupLocationInfoModel> store,
					Grid<BackupLocationInfoModel> grid) {
				if (model.getType() != null
						&& model.getType().intValue() == BackupLocationInfoModel.TYPE_CIFS) {
					return UIContext.Constants.cifsShare();
				} else if (model.getType().intValue() == BackupLocationInfoModel.TYPE_SERVER_LOCAL) {
					return UIContext.Constants.serverLocal();
				} else if (model.getType().intValue() == BackupLocationInfoModel.TYPE_RPS_SERVER) {
					return UIContext.Constants.rpsServe();
				} else if (model.getType().intValue() == BackupLocationInfoModel.TYPE_AMAZON_S3) {
					return UIContext.Constants.amazonS3();
				} else {
					return UIContext.Constants.nfsShare();
				}
			}
		};
		GridCellRenderer<BackupLocationInfoModel> totalSize = new GridCellRenderer<BackupLocationInfoModel>() {

			@Override
			public Object render(BackupLocationInfoModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<BackupLocationInfoModel> store,
					Grid<BackupLocationInfoModel> grid) {
				if (model.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER || model.getType() == BackupLocationInfoModel.TYPE_AMAZON_S3) {
					return Utils.createLabelField(UIContext.Constants.NA());
				}
				return Utils.bytes2GBString(model.getTotalSize());
			}
		};
		GridCellRenderer<BackupLocationInfoModel> freeSize = new GridCellRenderer<BackupLocationInfoModel>() {

			@Override
			public Object render(BackupLocationInfoModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<BackupLocationInfoModel> store,
					Grid<BackupLocationInfoModel> grid) {
				if (model.getType() == BackupLocationInfoModel.TYPE_RPS_SERVER || model.getType() == BackupLocationInfoModel.TYPE_AMAZON_S3) {
					return Utils.createLabelField(UIContext.Constants.NA());
				}
				return Utils.bytes2GBString(model.getFreeSize());
			}
		};

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("displayName",
				UIContext.Constants.backupDestination(), 570));
		configs.add(Utils.createColumnConfig("Type",
				UIContext.Constants.type(), 150, type));
		configs.add(Utils.createColumnConfig("TotalSize",
				UIContext.Constants.totalSize(), 150, totalSize));
		configs.add(Utils.createColumnConfig("FreeSize",
				UIContext.Constants.freeSize(), 150, freeSize));

		cm = new ColumnModel(configs);
		// the following listener is added to solve the issue that the grid only
		// shows two rows
		// with a vertical scroll bar on IE8 browser
		// cm.addListener(Events.WidthChange, new Listener<BaseEvent>() {
		//
		// @Override
		// public void handleEvent(BaseEvent be) {
		// int width = 0;
		// for (int i = 0; i < cm.getColumnCount(); i++) {
		// width += cm.getColumnWidth(i);
		// }
		//
		// if(width >= grid.getWidth()) {
		// this.setHeight(130);
		// }
		// else {
		// this.setHeight(113);
		// }
		//
		// this.layout(true);
		// }
		//
		// });
		grid = new Grid<BackupLocationInfoModel>(store, cm);
		grid.setAutoExpandColumn("displayName");
		grid.setAutoExpandMax(3000);
		grid.setStripeRows(true);
		// grid.setLoadMask(true);
		grid.setAutoHeight(true);
	}

	public void refreshData(BackupLocationInfoModel[] models) {
		if (models == null) {
			if (store != null)
				store.removeAll();
			return;
		}
		store.removeAll();
		for (BackupLocationInfoModel model : models) {
			store.add(model);
		}
		grid.reconfigure(store, cm);
	}

	public void resetData() {
		grid.getStore().removeAll();
	}

}
