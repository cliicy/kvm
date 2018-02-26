package com.ca.arcserve.linuximaging.ui.client.homepage.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.JobSummaryFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.JobSummaryModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;


public class JobSummaryPanel extends DashboardPanel {
	
	private static final String COLOR_INCOMPLETE="97a4ab";
	private static final String COLOR_SUCCESS="1d72b6";
	private static final String COLOR_FAILURE="f6ad40";
	private static final String COLOR_OTHER="fcfc00";
	
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private LabelField txtTotalJobs;
	private LabelField txtSuccessJobs;
	private LabelField txtFailureJobs;
	private LabelField txtIncompleteJobs;
	private PieChartPanel pieChart;
	private LabelField txtOtherJobs;
	private DashboardTable parentTable;
	private Label viewLink;
	
	public JobSummaryPanel(DashboardTable parent){
		this.parentTable = parent;
		setHeading(UIContext.Constants.jobSummary());

		TableLayout layout = new TableLayout();
	    layout.setColumns(2);
	    layout.setWidth("100%");
	    layout.setCellPadding(2);
		layout.setCellSpacing(2);
	    setLayout(layout);
	    
	    TableData tdLabel = new TableData();
		tdLabel.setWidth("25%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("75%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
	    LayoutContainer summary=deinfineSummaryPanel();
//	    LayoutContainer pieChart=deinfinePieChartPanel();
	    pieChart=new PieChartPanel();
	    add(summary, tdLabel);
	    add(pieChart, tdField);
	}
	private Label defineViewLink() {
	    final Menu menu = createViewMenu();
		viewLink = new Label(){
			@Override
			protected void onAttach() {
				super.onAttach();
				sinkEvents(Event.ONMOUSEUP);
			}

			public void onBrowserEvent(Event event) {

	            switch (DOM.eventGetType(event)) {
	                    case Event.ONMOUSEUP: {
	                    	menu.showAt(getAbsoluteLeft(), getAbsoluteTop()+this.getHeight());
	                    	break;
	                    }
	            }

			}

		};

		viewLink.setStyleName("homepage_header_helplink_label");
		viewLink.setText(UIContext.Constants.viewAll());
		viewLink.ensureDebugId("a9e16c55-a9c1-4c4c-9b76-c765587cd4d2");	
	    
		return viewLink;
	}
	private Menu createViewMenu() {
		Menu menu = new Menu();
	    MenuItem viewAll = new MenuItem(UIContext.Constants.all());
	    viewAll.addSelectionListener(new SelectionListener<MenuEvent>(){

			@Override
			public void componentSelected(MenuEvent ce) {
				viewLink.setText(UIContext.Constants.viewAll());
				refresh(JobSummaryFilterModel.RANGE_ALL);
			}
	    });
	    MenuItem viewWeek = new MenuItem(UIContext.Constants.viewThisWeek());
	    viewWeek.addSelectionListener(new SelectionListener<MenuEvent>(){

			@Override
			public void componentSelected(MenuEvent ce) {
				viewLink.setText(UIContext.Constants.viewThisWeek());
				refresh(JobSummaryFilterModel.RANGE_RECENT_WEEK);
			}
	    });
	    MenuItem viewMonth = new MenuItem(UIContext.Constants.viewThisMonth());
	    viewMonth.addSelectionListener(new SelectionListener<MenuEvent>(){

			@Override
			public void componentSelected(MenuEvent ce) {
				viewLink.setText(UIContext.Constants.viewThisMonth());
				refresh(JobSummaryFilterModel.RANGE_RECENT_MONTH);
			}
	    });
	    menu.add(viewAll);
	    menu.add(viewWeek);
	    menu.add(viewMonth);
		return menu;
	}
	
	/*private LayoutContainer defineViewButton() {
		LayoutContainer container=new LayoutContainer();
		TableLayout layout = new TableLayout();
//	    layout.setColumns(2);
	    layout.setWidth("100%");
	    layout.setCellPadding(2);
		layout.setCellSpacing(2);
	    container.setLayout(layout);
	    
		viewButton = new Button(UIContext.Constants.viewAll());
	    viewButton.setWidth(80);
//	    viewButton.setStyleAttribute("margin-top", "5px");
//	    viewButton.setStyleAttribute("margin-right", "350px");
	    viewButton.setArrowAlign(ButtonArrowAlign.RIGHT);
	    
	    Menu menu = createViewMenu();
	    viewButton.setMenu(menu);
	    TableData tdField = new TableData();
		tdField.setWidth("25%");
		tdField.setHorizontalAlign(HorizontalAlignment.RIGHT);
//		tdField.setVerticalAlign(VerticalAlignment.TOP);
		
		container.add(viewButton,tdField);
		return container;
	}

	private LayoutContainer deinfinePieChartPanel() {
		LayoutContainer chart=new LayoutContainer();
		RowLayout rowLayout = new RowLayout(Orientation.VERTICAL);
		chart.setLayout(rowLayout);
		pieChart=new PieChartPanel();
		chart.add(defineViewButton(),new RowData(1, -1, new Margins(0, 0, 0, 0)));
		chart.add(pieChart,new RowData(1, -1, new Margins(0, 0, 0, 0)));
		return chart;
	}*/


	private LayoutContainer deinfineSummaryPanel() {
		LayoutContainer summary=new LayoutContainer();
		TableLayout layout = new TableLayout();
	    layout.setColumns(3);
	    layout.setWidth("100%");
	    layout.setCellPadding(2);
		layout.setCellSpacing(2);
		summary.setLayout(layout);
		
		TableData tdImage = new TableData();
		tdImage.setWidth("15%");
		tdImage.setHorizontalAlign(HorizontalAlignment.RIGHT);
		tdImage.setVerticalAlign(VerticalAlignment.BOTTOM);
		TableData tdLabel = new TableData();
		tdLabel.setWidth("40%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("45%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		Html boxTotal = new Html("<div class=\"box\" ></div>");
		summary.add(boxTotal, tdImage);
		LabelField lblTotalJobs = new LabelField(UIContext.Constants.totalJobs()+UIContext.Constants.delimiter());
		lblTotalJobs.setStyleAttribute("white-space", "nowrap");
		txtTotalJobs = new LabelField();
		summary.add(lblTotalJobs, tdLabel);
		summary.add(txtTotalJobs, tdField);
		
//		Html boxSuccess = new Html("<div class=\"box\" style=\"background-color:#"+COLOR_SUCCESS+";\"></div>");
//		summary.add(boxSuccess, tdImage);
		Image imageSuccess= IconHelper.create("images/job_completed.jpg", 15,15).createImage();
		summary.add(imageSuccess, tdImage);
		LabelField lblSuccessJobs = new LabelField(UIContext.Constants.successJobs()+UIContext.Constants.delimiter());
		txtSuccessJobs = new LabelField();
		summary.add(lblSuccessJobs, tdLabel);
		summary.add(txtSuccessJobs, tdField);
		
//		Html boxFailure = new Html("<div class=\"box\" style=\"background-color:#"+COLOR_FAILURE+";\"></div>");
//		summary.add(boxFailure, tdImage);
		Image imageFailure= IconHelper.create("images/job_failed.jpg", 15,15).createImage();
	    summary.add(imageFailure, tdImage);
		LabelField lblFailureJobs = new LabelField(UIContext.Constants.failureJobs()+UIContext.Constants.delimiter());
		txtFailureJobs = new LabelField();
		summary.add(lblFailureJobs, tdLabel);
		summary.add(txtFailureJobs, tdField);
		
//		Html boxIncomplete = new Html("<div class=\"box\" style=\"background-color:#"+COLOR_INCOMPLETE+";\"></div>");
//		summary.add(boxIncomplete, tdImage);
		Image imageIncomplete= IconHelper.create("images/job_incomplete.jpg", 15,15).createImage();
	    summary.add(imageIncomplete, tdImage);
		LabelField lblIncomplete = new LabelField(UIContext.Constants.incompleteJobs()+UIContext.Constants.delimiter());
		txtIncompleteJobs = new LabelField();
		summary.add(lblIncomplete, tdLabel);
		summary.add(txtIncompleteJobs, tdField);
		
//		Html boxOther = new Html("<div class=\"box\" style=\"background-color:#"+COLOR_OTHER+";\"></div>");
//		summary.add(boxOther, tdImage);
		Image imageOther= IconHelper.create("images/job_other.jpg", 15,15).createImage();
	    summary.add(imageOther, tdImage);
		LabelField lblOther = new LabelField(UIContext.Constants.cancelledStatus()+UIContext.Constants.delimiter());
		txtOtherJobs = new LabelField();
		summary.add(lblOther, tdLabel);
		summary.add(txtOtherJobs, tdField);

		Html boxViewFor = new Html("<div class=\"box\" ></div>");
		summary.add(boxViewFor, tdImage);
		LabelField lblView = new LabelField(UIContext.Constants.viewFor()+UIContext.Constants.delimiter());
		Label view=this.defineViewLink();
		summary.add(lblView, tdLabel);
		summary.add(view, tdField);
		
		return summary;
	}
	
	public void resetViewText(){
		viewLink.setText(UIContext.Constants.viewAll());
	}
	
	public void refreshData(JobSummaryModel model){
		if(model==null){
			return;
		}
		int other=model.getTotalJobs()-model.getSuccess()-model.getFailure()-model.getIncomplete();
		this.txtTotalJobs.setValue(model.getTotalJobs());
		this.txtSuccessJobs.setValue(model.getSuccess());
		this.txtFailureJobs.setValue(model.getFailure());
		this.txtIncompleteJobs.setValue(model.getIncomplete());
		this.txtOtherJobs.setValue(other);
		if(model.getTotalJobs()>0){
			List<PieSlice> list=new ArrayList<PieSlice>(4);
			
			if(model.getFailure()>0){
				list.add(new PieSlice(UIContext.Constants.failureJobs(),model.getFailure(),COLOR_FAILURE));
			}
			if(model.getSuccess()>0){
				list.add(new PieSlice(UIContext.Constants.successJobs(),model.getSuccess(),COLOR_SUCCESS));
			}
			if(model.getIncomplete()>0){
				list.add(new PieSlice(UIContext.Constants.incompleteJobs(),model.getIncomplete(),COLOR_INCOMPLETE));
			}
			if(other>0){
				list.add(new PieSlice(UIContext.Constants.cancelledStatus(),other,COLOR_OTHER));
			}
			pieChart.setSlices(list.toArray(new PieSlice[0]));
			pieChart.showChart(true);
		}else{
			pieChart.showChart(false);
		}
		
	}
	
	public void resetData(){
		this.txtTotalJobs.setValue("");
		this.txtSuccessJobs.setValue("");
		this.txtFailureJobs.setValue("");
		this.txtIncompleteJobs.setValue("");
		this.txtOtherJobs.setValue("");
		pieChart.showChart(false);
	}
	
	private void refresh(int type){
		JobSummaryFilterModel filter = new JobSummaryFilterModel();
		filter.setTimeRange(type);
		service.getJobSummaryInformation(parentTable.parentTabPanel.currentServer, filter, new AsyncCallback<JobSummaryModel>(){

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(JobSummaryModel result) {
				refreshData(result);
			}
			
		});
	}
}
