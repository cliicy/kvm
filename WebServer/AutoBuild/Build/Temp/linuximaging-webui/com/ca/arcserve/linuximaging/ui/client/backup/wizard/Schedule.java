package com.ca.arcserve.linuximaging.ui.client.backup.wizard;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class Schedule extends LayoutContainer {
	public Schedule(){
		this.setLayout(new TableLayout());
		LabelField label = new LabelField("Under developing...");
		this.add(label);
	}

}
