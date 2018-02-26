package com.ca.arcserve.linuximaging.ui.client.standby;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class StandbyMachines extends LayoutContainer {
	
	protected StandbyWizardPanel parentWindow;
	
	public StandbyMachines(StandbyWizardPanel parent){
		this.parentWindow = parent;
	}

	public void save(){
		
	}
	
	public void refresh(){
		
	}
	
	public boolean validate(){
		return true;
	}
}
