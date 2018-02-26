package com.ca.arcserve.linuximaging.ui.client.common;

import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;

public class TooltipSimpleComboBox<T> extends SimpleComboBox<T> {
	public TooltipSimpleComboBox() {
		setTriggerAction(TriggerAction.ALL);
		setTemplate(getMyTemplate());
	}
	
	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		getInputEl().setSize(width - 18, height, true);
	}

	public void expand() {
		super.expand();
		getListView().setWidth(getWidth());
		((Container<?>) getListView().getParent()).setWidth(getWidth());
	}
	
	private native String getMyTemplate() /*-{ 
	    return  [ 
	    '<tpl for=".">', 
	    '<div class="x-combo-list-item" qtip="{value}" >{value}</div>', 
	    '</tpl>' 
	    ].join(""); 
	}-*/; 
}
