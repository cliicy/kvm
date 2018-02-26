package com.ca.arcserve.linuximaging.ui.client.main;


import com.ca.arcserve.linuximaging.ui.client.components.configuration.ConfigurationPanel;
import com.ca.arcserve.linuximaging.ui.client.homepage.Homepagetree;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.main.i18n.MainUIConstants;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.Style.LayoutRegion;

public class MainPanel extends LayoutContainer {
	public MainPanel() {
	}

	private static final MainUIConstants mainUIConstants = GWT.create(MainUIConstants.class);
	private TabPanel mainPanel = null;
	private Homepagetree tree = null;
	/*public MainPanel()*/
	@Override
	protected void onRender(Element parent, int pos){
		super.onRender(parent, pos);
		setLayout(new FitLayout());
		
		mainPanel = new TabPanel();
		mainPanel.setHeight(600);
		
		TabItem tbtmBackup = new TabItem(mainUIConstants.Backup());
		tbtmBackup.setBorders(true);
		tbtmBackup.setTabIndex(0);
		tbtmBackup.setLayout(new BorderLayout());		
		tree = new Homepagetree();
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.WEST, 125.0f);
		data.setSplit(true);
		tbtmBackup.add(tree, data);
		
		HomepageTab componentJobStatus = new HomepageTab();
		tbtmBackup.add(componentJobStatus, new BorderLayoutData(LayoutRegion.CENTER, 330.0f));
		mainPanel.add(tbtmBackup);
		tree.setHomepageTab(componentJobStatus);
		
		TabItem tbtmRestore = new TabItem(mainUIConstants.Restore());
		tbtmRestore.setBorders(true);
		tbtmRestore.setTabIndex(1);
		mainPanel.add(tbtmRestore);
		
		TabItem tbtmConfiguration = new TabItem(mainUIConstants.Configuration());
		tbtmRestore.setBorders(true);
		tbtmConfiguration.setTabIndex(2);
		tbtmConfiguration.setLayout(new FitLayout());
		ConfigurationPanel config=new ConfigurationPanel();
		tbtmConfiguration.add(config);
		mainPanel.add(tbtmConfiguration);
		
		
		add(mainPanel, new RowData(Style.DEFAULT, Style.DEFAULT, new Margins()));
	}
	
}
