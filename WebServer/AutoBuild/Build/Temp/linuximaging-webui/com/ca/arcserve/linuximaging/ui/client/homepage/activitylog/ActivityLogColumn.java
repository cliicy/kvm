package com.ca.arcserve.linuximaging.ui.client.homepage.activitylog;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.ActivityLogModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public enum ActivityLogColumn {
	
	Type(1) {
		@Override
		protected void initializeConfig() {
			config = new ColumnConfig("messageType", UIContext.Constants.type(), 50);
			config.setMenuDisabled(true);
			config.setSortable(false);
			config.setRenderer(new GridCellRenderer<ActivityLogModel>() {

				@Override
				public Object render(ActivityLogModel model, String property,
						ColumnData config, int rowIndex, int colIndex,
						ListStore<ActivityLogModel> store, Grid<ActivityLogModel> grid) {
					ActivityLogModel log = model;
					IconButton image = null;
					
					switch (log.getType()) {
					case 1:
						image = new IconButton("activity_log_status_information");
						Utils.addToolTip(image, UIContext.Constants.information());
						break;
					case 2:
						image = new IconButton("activity_log_status_error");
						Utils.addToolTip(image, UIContext.Constants.error());
						break;
					case 3:
						image = new IconButton("activity_log_status_warning");
						Utils.addToolTip(image, UIContext.Constants.warning());
						break;
					default:
						break;
					}
					return image;
				}
				
			});
		}
	},
	JobID(2) {
		@Override
		protected void initializeConfig() {
			config = new ColumnConfig("jobID", UIContext.Constants.jobID(), 80);
			config.setMenuDisabled(true);
			config.setSortable(false);
			config.setAlignment(HorizontalAlignment.CENTER);
		}
	},
	JobName(3) {
		@Override
		protected void initializeConfig() {
			config = new ColumnConfig("jobName", UIContext.Constants.jobName(), 180);
			config.setMenuDisabled(true);
			config.setSortable(false);
		}
	},
	Time(4) {
		@Override
		protected void initializeConfig() {
			config = new ColumnConfig("logTime", UIContext.Constants.time(), 150);
			config.setMenuDisabled(true);
			config.setSortable(false);
			config.setRenderer(new GridCellRenderer<ActivityLogModel>() {

				@Override
				public Object render(ActivityLogModel model, String property,
						ColumnData config, int rowIndex, int colIndex,
						ListStore<ActivityLogModel> store, Grid<ActivityLogModel> grid) {
					ActivityLogModel log = model;
					return Utils.formatDateToServerTime(log.getTime());
				}
				
			});
		}
	},
	Machine(5) {
		@Override
		protected void initializeConfig() {
			config = new ColumnConfig("server", UIContext.Constants.nodeName(), 150);
			config.setMenuDisabled(true);
			config.setSortable(false);
		}
	},
	Message(6) {
		@Override
		protected void initializeConfig() {
			config = new ColumnConfig("message", UIContext.Constants.message(), 400);
			config.setMenuDisabled(true);
			config.setSortable(false);
			config.setRenderer(new GridCellRenderer<ActivityLogModel>() {

				@Override
				public Object render(ActivityLogModel model, String property,
						ColumnData config, int rowIndex, int colIndex,
						ListStore<ActivityLogModel> store, Grid<ActivityLogModel> grid) {
					ActivityLogModel log = model;
					String message = log.getMessage();
					if(message != null) {
						LabelField messageLabel = new LabelField();
						messageLabel.ensureDebugId("07cfad15-ab2c-49af-9e66-d81c4f4d00c9");
						messageLabel.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");
						messageLabel.setStyleAttribute("white-space", "normal");
						messageLabel.setValue(message);
						return messageLabel;
					}
					return "";
				}
				
			});
		}
	};
	
	protected ColumnConfig config;
	private int sortColumn;
	
	private ActivityLogColumn(int column) {
		this.sortColumn = column;
	}
	
	public ColumnConfig getColumnConfig() {
		if (config == null) {
			initializeConfig();
		}
		
		return config;
	}
	
	public int getSortColumn() {
		return this.sortColumn;
	}
	
	protected void initializeConfig() {
	}

}