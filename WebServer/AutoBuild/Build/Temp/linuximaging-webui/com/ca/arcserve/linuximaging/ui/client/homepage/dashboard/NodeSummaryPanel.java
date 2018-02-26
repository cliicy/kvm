package com.ca.arcserve.linuximaging.ui.client.homepage.dashboard;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.NodeSummaryModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;


public class NodeSummaryPanel extends DashboardPanel {
	
//	private LabelField txtTotalNodes;
//	private LabelField txtProtectedNodes;
//	private LabelField txtLastBackupFailureNodes;
	private Image protectedLegendImage;
	private Label protectedLegendText;
	private Image failureLegendImage;
	private Label failureLegendText;
	private Image totalLegendImage;
	private Label totalLegendText;
	private HTML destinationHtml;
	private LabelField noDataToshow;

	public NodeSummaryPanel(){
		setHeading(UIContext.Constants.nodeSummary());

		TableLayout layout = new TableLayout();
	    layout.setWidth("100%");
	    layout.setCellPadding(2);
		layout.setCellSpacing(2);
	    setLayout(layout);
	    
	    TableData td = new TableData();
		td.setWidth("80%");
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		
	    LayoutContainer legendChart=deinfineLegendBarPanel();
	    add(legendChart, td);
	}

	private LayoutContainer deinfineLegendBarPanel() {
		LayoutContainer pieChart=new LayoutContainer();
		RowLayout rowLayout = new RowLayout(Orientation.VERTICAL);
		pieChart.setLayout(rowLayout);
		pieChart.add(this.createBarChart(),new RowData(1, 110, new Margins(0, 0, 0, 0)));
		pieChart.add(this.createLengendChart(),new RowData(1, -1, new Margins(0, 0, 0, 0)));
		return pieChart;
	}
	private LayoutContainer createLengendChart() {
		LayoutContainer lcLegendContainer = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(6);
		lcLegendContainer.setLayout(layout);
		lcLegendContainer.setStyleAttribute("padding", "2px");
		lcLegendContainer.setStyleAttribute("padding-top", "6px");
		
		AbstractImagePrototype imagePrototype = IconHelper.create("images/legend_incremental.png", 16,16);
		
	    totalLegendImage = imagePrototype.createImage();
	    totalLegendImage.setUrl("images/legend_freeSpace.png");
	    lcLegendContainer.add(totalLegendImage);
	    
	    totalLegendText = new Label();
	    totalLegendText.setStyleName("homepage_summary_legengLabel");
	    lcLegendContainer.add(totalLegendText);
		    
		protectedLegendImage = imagePrototype.createImage();
		protectedLegendImage.setUrl("images/legend_incremental.png");
		lcLegendContainer.add(protectedLegendImage);
	    
	    protectedLegendText = new Label();
	    protectedLegendText.setStyleName("homepage_summary_legengLabel");
	    lcLegendContainer.add(protectedLegendText);
	    
	    failureLegendImage = imagePrototype.createImage();
		failureLegendImage.setUrl("images/legend_others.png");
		lcLegendContainer.add(failureLegendImage);

		failureLegendText = new Label();
		failureLegendText.setStyleName("homepage_summary_legengLabel");
		lcLegendContainer.add(failureLegendText);
		
	    return lcLegendContainer;
	}
	private LayoutContainer createBarChart() {
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
        layout.setWidth("100%");
//		layout.setColumns(6);
        layout.setCellPadding(5);
        layout.setCellSpacing(5);
		container.setLayout(layout);
//		container.setStyleAttribute("padding", "4px");
//		container.setStyleAttribute("padding-top", "10px");
		
		TableData tableData = new TableData();
		tableData.setHorizontalAlign(HorizontalAlignment.CENTER);
//		tableData.setColspan(6);
		
		destinationHtml = new HTML();
		container.add(destinationHtml,tableData);
		
		noDataToshow = new LabelField(UIContext.Constants.homepageChartNoDataDisplay());
		tableData.setVerticalAlign(VerticalAlignment.BOTTOM);
		container.add(noDataToshow, tableData);
	    
		return container;
	}

/*	private LayoutContainer deinfineSummaryPanel() {
		LayoutContainer summary=new LayoutContainer();
		TableLayout layout = new TableLayout();
	    layout.setColumns(2);
	    layout.setWidth("100%");
	    layout.setCellPadding(2);
		layout.setCellSpacing(2);
		summary.setLayout(layout);
		TableData tdLabel = new TableData();
		tdLabel.setWidth("70%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("30%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField lblTotalNodes =new LabelField(UIContext.Constants.totalNodes());
		txtTotalNodes =new LabelField();
		summary.add(lblTotalNodes, tdLabel);
		summary.add(txtTotalNodes, tdField);
		
		LabelField lblProtectedNodes =new LabelField(UIContext.Constants.protectedNodes());
		txtProtectedNodes =new LabelField();
		summary.add(lblProtectedNodes, tdLabel);
		summary.add(txtProtectedNodes, tdField);
		
		LabelField lblLastBackupFailureNodes =new LabelField(UIContext.Constants.lastBackupFailureNodes());
		txtLastBackupFailureNodes =new LabelField();
		summary.add(lblLastBackupFailureNodes, tdLabel);
		summary.add(txtLastBackupFailureNodes, tdField);
		
		return summary;
	}*/
	
	public void refreshData(NodeSummaryModel model) {
		if (model == null) {
			return;
		}
//		this.txtTotalNodes.setValue("100");
//		this.txtProtectedNodes.setValue("20");
//		this.txtLastBackupFailureNodes.setValue("10");
		int total = model.getTotalNodes();
		int protect = model.getProtectedNodes();
		int failure = model.getFailureNodes();
		
		protectedLegendText.setText(UIContext.Messages.protectedNodes(protect));
		failureLegendText.setText(UIContext.Messages.lastBackupFailureNodes(failure));
		totalLegendText.setText(UIContext.Messages.totalNodes(total));
		
		protectedLegendImage.setTitle(protectedLegendText.getText());
		failureLegendImage.setTitle(failureLegendText.getText());
		totalLegendImage.setTitle(totalLegendText.getText());
		
		StringBuffer buffer = new StringBuffer();
//		buffer.append("<table width=\"85%\" height=\"15\" style=\"border:1px solid #000000; margin: 0px;\" CELLPADDING=0 CELLSPACING=0>");
//		buffer.append("<tr>");
//
//		if (total > 0) {
//			int totalPercent = ((total-protect) * 100)/total;
//			int protectPercent = (protect * 100)/total;
//			int failurePercent = (failure * 100)/total;
//			
//			if (failurePercent > 0)
//				appendTDChart(buffer, failurePercent,
//						"images/legend_others.png", failureLegendText.getText());
//			if (protectPercent > 0)
//				appendTDChart(buffer, protectPercent,
//						"images/legend_incremental.png",
//						protectedLegendText.getText());
//			appendTDChart(buffer, totalPercent, "images/legend_freeSpace.png",
//					totalLegendText.getText());
//		} else {
//			buffer.append("<td/>");
//		}
//		buffer.append("</tr></table>");

		if (total > 0) {
			int protectPercent = (protect * 100)/total;
			int failurePercent = (failure * 100)/total;
			
			appendTableChart(buffer, 100, "images/legend_freeSpace.png", totalLegendText.getText());
			appendTableChart(buffer, protectPercent, "images/legend_incremental.png", protectedLegendText.getText());
			appendTableChart(buffer, failurePercent, "images/legend_others.png", failureLegendText.getText());
			
			showChart(true);
		}else{
			showChart(false);
		}
		
		destinationHtml.setHTML(buffer.toString());
	}

	public void resetData(){
		protectedLegendText.setText("");
		failureLegendText.setText("");
		totalLegendText.setText("");
		showChart(false);
	}
	
	private void showChart(boolean flag) {
		destinationHtml.setVisible(flag);
		noDataToshow.setVisible(!flag);
	}

	private void appendTableChart(StringBuffer buffer, int percent, String image,String title){
		buffer.append("<table width=\"85%\" height=\"20\" style=\"border:0px solid #000000; margin: 10px;\" CELLPADDING=0 CELLSPACING=0>");
		buffer.append("<tr>");
		appendTDChart(buffer,percent,image,title);
		if(percent<100){
			int rest=100-percent;
			appendTDChart(buffer,rest,null,title);
		}
//		buffer.append("<td align=\"RIGHT\" width=\"40%\" font-family: Tahoma,Arial; font-size: 12px; padding: 2px 2px 2px 0;>");
//		buffer.append(title);
//		buffer.append("</td>");
		buffer.append("</tr></table>");
	}
	private void appendTDChart(StringBuffer buffer, int percent, String image,String title) {
		buffer.append("<td ");
		buffer.append(" title=\"");
		buffer.append(title);
		buffer.append("\" width=\"");
		buffer.append(percent);
		
		if(image!=null){
			buffer.append("%\" style=\"background-image: url(./");
			buffer.append(image);
			buffer.append(");\"/>");
		}else{
			buffer.append("%\" />");
		}
		
	}

}
