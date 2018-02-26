package com.ca.arcserve.linuximaging.ui.client.common;

import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.grid.GridView;

public class BaseGridView extends GridView {
	
	public BaseGridView(){
		setPreventScrollToTopOnRefresh(true);
	}

	public boolean isPreventScrollToTopOnRefresh() {
		return preventScrollToTopOnRefresh;
	}

	public void setPreventScrollToTopOnRefresh(boolean preventScrollToTopOnRefresh) {
		this.preventScrollToTopOnRefresh = preventScrollToTopOnRefresh;
	}
	
	public void refresh(boolean headerToo) {
		Point p=getScrollState();
		super.refresh(headerToo);
		restoreScroll(p);
	}
}
