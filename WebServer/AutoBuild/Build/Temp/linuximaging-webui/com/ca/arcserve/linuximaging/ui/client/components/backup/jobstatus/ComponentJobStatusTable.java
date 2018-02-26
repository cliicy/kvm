package com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus;

import com.ca.arcserve.linuximaging.ui.client.common.BaseGrid;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus.i18n.JobStatusUIConstants;
import com.ca.arcserve.linuximaging.ui.client.components.backup.jobstatus.model.DataModalJobStatus;
import com.ca.arcserve.linuximaging.ui.client.model.JobType;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatus;
import com.ca.arcserve.linuximaging.ui.client.model.JobPhase;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.core.client.GWT;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.Style.SelectionMode;

public class ComponentJobStatusTable extends LayoutContainer {
	private static final JobStatusUIConstants uiConstant=GWT.create(JobStatusUIConstants.class);
	
	private ListStore<DataModalJobStatus> gridStore;
	public ColumnModel jobStatusColumnsModel;
	private Grid<DataModalJobStatus> jobStatusGrid;
	private final int COLUMN_JOBSTATUS_LENGTH=150;
	private String currentServerName;
	
	public ComponentJobStatusTable(){
		this.setLayout(new FitLayout());
		
		gridStore = new ListStore<DataModalJobStatus>();
		GridCellRenderer<DataModalJobStatus> jobType = new GridCellRenderer<DataModalJobStatus>() {

			@Override
			public Object render(DataModalJobStatus model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataModalJobStatus> store,
					Grid<DataModalJobStatus> grid) {
				return JobType.parse(model.getJobType());
			}
		};
		GridCellRenderer<DataModalJobStatus> jobStatus = new GridCellRenderer<DataModalJobStatus>() {

			@Override
			public Object render(DataModalJobStatus model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataModalJobStatus> store,
					Grid<DataModalJobStatus> grid) {
				return JobStatus.parse(model.getJobStatus());
			}
		};
		GridCellRenderer<DataModalJobStatus> jobPhase = new GridCellRenderer<DataModalJobStatus>() {

			@Override
			public Object render(DataModalJobStatus model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataModalJobStatus> store,
					Grid<DataModalJobStatus> grid) {
				return JobPhase.parse(model.getJobPhase());
			}
		};
		GridCellRenderer<DataModalJobStatus> jobProgress = new GridCellRenderer<DataModalJobStatus>() {

			@Override
			public Object render(DataModalJobStatus model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataModalJobStatus> store,
					Grid<DataModalJobStatus> grid) {
				ProgressBar bar = new ProgressBar();
				bar.setVisible(true);
				if(model.getProgress()==0){
					 bar.updateProgress(0.0026,"");
				}else{
					 bar.updateProgress(model.getProgress() * .01, model.getProgress() + "%");
				}
			   
				return bar;
			}
		};
		GridCellRenderer<DataModalJobStatus> startTime = new GridCellRenderer<DataModalJobStatus>() {

			@Override
			public Object render(DataModalJobStatus model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataModalJobStatus> store,
					Grid<DataModalJobStatus> grid) {
				String date="";
				if(model.getStartTime()>0){
					date=Utils.formatDate(new Date(model.getStartTime()));
				}
				return date;
			}
		};
		GridCellRenderer<DataModalJobStatus> elapsedTime = new GridCellRenderer<DataModalJobStatus>() {

			@Override
			public Object render(DataModalJobStatus model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataModalJobStatus> store,
					Grid<DataModalJobStatus> grid) {
				String time="";
				if(model.getElapsedTime()>0){
					time=Utils.seconds2String(model.getElapsedTime()/1000);
				}
				return time;
			}
		};
		GridCellRenderer<DataModalJobStatus> processedData = new GridCellRenderer<DataModalJobStatus>() {

			@Override
			public Object render(DataModalJobStatus model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataModalJobStatus> store,
					Grid<DataModalJobStatus> grid) {
				return Utils.bytes2String(model.getProcessedData());
			}
		};
		GridCellRenderer<DataModalJobStatus> throughput = new GridCellRenderer<DataModalJobStatus>() {

			@Override
			public Object render(DataModalJobStatus model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DataModalJobStatus> store,
					Grid<DataModalJobStatus> grid) {
				return Utils.bytes2MBString(model.getProcessedData());
			}
		};
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(createColumnConfig("jobId", uiConstant.jobID(),60,null));
	    configs.add(createColumnConfig("jobName", uiConstant.jobName(),100,null));
	    configs.add(createColumnConfig("jobType", uiConstant.jobType(),80,jobType));
	    configs.add(createColumnConfig("nodeName", uiConstant.nodeName(),100,null));
	    configs.add(createColumnConfig("jobStatus", uiConstant.jobStatus(),80,jobStatus));
	    configs.add(createColumnConfig("jobPhase", uiConstant.jobPhase(),100,jobPhase));
	    configs.add(createColumnConfig("jobProgress", uiConstant.progress(),COLUMN_JOBSTATUS_LENGTH,jobProgress));
	    configs.add(createColumnConfig("startTime", uiConstant.startTime(),150,startTime));
	    configs.add(createColumnConfig("elapsedTime", uiConstant.elapsedTime(),100,elapsedTime));
	    configs.add(createColumnConfig("processedData", uiConstant.processedData(),100,processedData));
	    configs.add(createColumnConfig("throughput", uiConstant.throughput(),100,throughput));
	    jobStatusColumnsModel = new ColumnModel(configs);
	    jobStatusGrid = new BaseGrid<DataModalJobStatus>(gridStore, jobStatusColumnsModel);
	    
//	    jobStatusColumnsModel.addListener(Events.WidthChange, new Listener<BaseEvent>(){
//
//			@Override
//			public void handleEvent(BaseEvent be) {
//				jobStatusGrid.reconfigure(gridStore, jobStatusColumnsModel);
//			}});
		
	    jobStatusGrid.setTrackMouseOver(true);
	    jobStatusGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    jobStatusGrid.setBorders(true);
	    jobStatusGrid.setHeight("100%");
	    add(jobStatusGrid);
	}
	
	private ColumnConfig createColumnConfig(String id, String header, int width, GridCellRenderer<DataModalJobStatus> renderer) {
		ColumnConfig column = new ColumnConfig();
		column.setGroupable(false);
		column.setSortable(false);
		
		column.setId(id);
		column.setHeader(header);
		column.setMenuDisabled(true);
		if (width >= 0)
			column.setWidth(width);
		if (renderer != null)
			column.setRenderer(renderer);
		return column;
	}
	public void addData(DataModalJobStatus data){
		gridStore.add(data);
		jobStatusGrid.reconfigure(gridStore, jobStatusColumnsModel);
	}
	
	public void refreshData(String nodeServerName, List<DataModalJobStatus> list)
	{
		if(nodeServerName.equals(currentServerName)){
			for(DataModalJobStatus data: list){
				boolean isExist=false;
				for(int i=0;i<gridStore.getCount();i++){
					DataModalJobStatus status=gridStore.getAt(i);
					if(status.getJobId()==-1){// only one jobscript for one targetserver
						if(status.getNodeName().equals(data.getNodeName())){
							isExist=true;
							status.setJobName(data.getJobName());
						}
					}else {
						if(status.getJobId().equals(data.getJobId())&&status.getJobName().equals(data.getJobName())){
							isExist=true;
							status.setJobStatus(data.getJobStatus());
							status.setJobPhase(data.getJobPhase());
							status.setProgress(data.getProgress());
							status.setElapsedTime(data.getElapsedTime());
							status.setProcessedData(data.getProcessedData());
							status.setThroughput(data.getThroughput());
						}
					}
				}
				if(!isExist){
					gridStore.add(data);
				}
			}
		}else{
			gridStore.removeAll();
			gridStore.add(list);
			currentServerName=nodeServerName;
		}
		
		jobStatusGrid.reconfigure(gridStore, jobStatusColumnsModel);
//		jobStatusGrid.getView().refresh(false);
	}
}
