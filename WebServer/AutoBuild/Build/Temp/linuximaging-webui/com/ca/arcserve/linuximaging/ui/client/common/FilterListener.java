package com.ca.arcserve.linuximaging.ui.client.common;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.KeyCodes;

public class FilterListener extends KeyListener {
	private Button filterBt;
	public FilterListener(Button bt) {
		filterBt = bt;
	}
	
	@Override
	public void componentKeyPress(ComponentEvent event) {
		if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
			if ( filterBt != null ) 
				filterBt.fireEvent(Events.Select);
		}
	}
}
