package com.ca.arcserve.linuximaging.ui.client.homepage.dashboard;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

public class PieChartPanel extends LayoutContainer{
	private static String flashDownloadUrl = "http://get.adobe.com/flashplayer";
	native public static String getAdobeFlashMajorVersion()/*-{
	
	try { 
		try { 
  			var axo = new ActiveXObject('ShockwaveFlash.ShockwaveFlash.6'); 
  			try{ 
      			axo.AllowScriptAccess = 'always'; 
  			}catch(e) {
      			return '6,0,0'; 
  			} 
		} catch(e) {} 
		return new ActiveXObject('ShockwaveFlash.ShockwaveFlash').GetVariable('$version').replace(/\D+/g, ',').match(/^,?(.+),?$/)[1]; 
		} catch(e) { 
		try { 
  			if(navigator.mimeTypes["application/x-shockwave-flash"].enabledPlugin){ 
    			return (navigator.plugins["Shockwave Flash 2.0"] || navigator.plugins["Shockwave Flash"]).description.replace(/\D+/g, ",").match(/^,?(.+),?$/)[1]; 
  			}
  		} catch(e) {} 
		} 
		
		return '0,0,0';
}-*/;
	private static final String chartXMLDataBegin = "<graph  showNames='0' decimalPrecision='0' showValues='0' formatNumberScale='0' pieRadius='90'> ";
	private static final String chartXMLDataEnd = "</graph>";
	private static final String chartXMLDataSet = "<set name='SETNAME' value='COUNT' color='COLOR' />";
	private boolean isAdobeFlashInstalled = false;
//	private int chartWidth = 380;
//	private int chartHighth = 170; 
	private int chartWidth = 300;
	private int chartHighth = 148;

	private String chartHTML = new StringBuilder()
	.append("<html>")
	.append("  <body bgcolor=\"#ffffff\" >")
	.append("    <OBJECT classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" width=\"").append(chartWidth).append("\" height=\"").append(chartHighth).append("\" id=\"Pie3D\">")
	.append("      <param name=\"movie\" value=\"FusionCharts/FCF_Pie3D.swf\" />")
	.append("      <param name=\"FlashVars\" ")
	.append("             value=\"&dataXML=ChartXMLData&chartWidth=").append(chartWidth).append("&chartHeight=").append(chartHighth).append("\">")
	.append("      <param name=wmode value=transparent>")
	.append("      <param name=\"quality\" value=\"high\" />")
	.append("      <embed src=\"../FusionCharts/FCF_Pie3D.swf\" ")
	.append("             flashVars=\"&dataXML=ChartXMLData&chartWidth=").append(chartWidth).append("&chartHeight=").append(chartHighth).append("\"")
	.append("             quality=\"high\" wmode=transparent width=\"").append(chartWidth).append("\" height=\"").append(chartHighth).append("\" name=\"Pie3D\" ")
	.append("             type=\"application/x-shockwave-flash\" />")
	.append("    </OBJECT>")
	.append("  </body>")
	.append("</html>").toString();

	private HTML pieChart = new HTML("<html><body bgcolor=\"#ffffff\"></body></html>", true);
	private LabelField noDataToshow;
	
	public PieChartPanel(){
		TableLayout layout = new TableLayout();
	    layout.setWidth("100%");
//	    layout.setHeight("100%");
//	    layout.setCellPadding(2);
//		layout.setCellSpacing(2);
	    setLayout(layout);
	    
	    TableData tableData = new TableData();
		tableData.setHorizontalAlign(HorizontalAlignment.CENTER);
		
		add(pieChart,tableData);
		
		noDataToshow = new LabelField(UIContext.Constants.homepageChartNoDataDisplay());
		
		tableData.setVerticalAlign(VerticalAlignment.BOTTOM);
		add(noDataToshow, tableData);
		showChart(false);
	    
	    HTML text = getFlashInstallReminder();
		if (text != null) {
			text.setStyleName("FlashInstaller");
			add(text);
		}
	}

	@Override
	protected void onRender(Element parent, int pos){
		super.onRender(parent, pos);
	}
	
	public PieChartPanel(int chartWidth, int chartHighth) {
		super();
		this.chartWidth = chartWidth;
		this.chartHighth = chartHighth;
	}
	
	public void showChart(boolean flag) {
		pieChart.setVisible(flag);
		noDataToshow.setVisible(!flag);
	}
	
	public int getChartWidth() {
		return chartWidth;
	}
	public void setChartWidth(int chartWidth) {
		this.chartWidth = chartWidth;
	}
	public int getChartHighth() {
		return chartHighth;
	}
	public void setChartHighth(int chartHighth) {
		this.chartHighth = chartHighth;
	}
	public static HTML getFlashInstallReminder() {
		String flashMajorVersionString = getAdobeFlashMajorVersion();
		int majorVersion = Integer.parseInt(flashMajorVersionString.split(",")[0]);
		HTML text = null;
		if (majorVersion<10){
			String posiURL = "<a href=\"" + flashDownloadUrl + "\" target=\"_blank\" style=\"home_chart_description\">" + UIContext.Constants.homepageStatusPieChartAdobeFlashHere() + "</a>";
			text = new HTML(UIContext.Constants.homepageStatusPieChartAdobeFlashNotInstalled() + "&nbsp; " + UIContext.Messages.homepagePieChartInstallFlash(posiURL));
			text.setStyleName("home_chart_description");
		}
		return text;
	}
	private String getXMLDataSet(String statusName,Integer count, String color) {
		return chartXMLDataSet.replace("SETNAME", statusName)
                              .replace("COUNT", count.toString())
                              .replace("COLOR", color);
	}
	public void setSlices(PieSlice[] slices){
		if(slices==null||slices.length==0){
			return;
		}
		
		StringBuilder concretData = new StringBuilder(chartXMLDataBegin);
		for(PieSlice slice : slices){
			concretData.append(getXMLDataSet(slice.getStatusName(),slice.getCount(),slice.getColor()));
		}
		concretData.append(chartXMLDataEnd);
		String chartHTMLString = chartHTML.replaceAll("ChartXMLData", concretData.toString());
		pieChart.setHTML(chartHTMLString);
	}
}
