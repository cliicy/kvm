package com.ca.arcserve.linuximaging.ui.client.homepage.dashboard;

import com.extjs.gxt.ui.client.widget.ContentPanel;

public class DashboardPanel extends ContentPanel {
	public DashboardPanel(){
//		setHeaderVisible(true);
		setCollapsible(false);
	    setBorders(true);
		setBodyBorder(false);
//		background-image: url("../images/default/panel/white-top-bottom.gif");
		this.getHeader().setStyleAttribute("background-image", "url(\"../images/background.png\")");
		this.getHeader().setStyleAttribute("border", "0");
	}

}
