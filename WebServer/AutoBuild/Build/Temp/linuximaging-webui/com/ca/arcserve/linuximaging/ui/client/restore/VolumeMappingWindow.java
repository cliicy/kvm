package com.ca.arcserve.linuximaging.ui.client.restore;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class VolumeMappingWindow extends Window {
	public final int WINDOW_WIDTH=800;
	public final int WINDOW_HEIGHT=500;
	private VolumeMappingWindow mappingWindow;
	private boolean bCancelled = false;
	
	
	
	public VolumeMappingWindow(){
		mappingWindow=this;
		this.setLayout( new FitLayout() );
		this.setHeading(UIContext.Constants.restoreWizard());
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setModal(true);
//		this.setMinWidth(WINDOW_WIDTH);
//		this.setMinHeight(WINDOW_HEIGHT);
		this.setResizable(false);
	}

	public boolean getcancelled() {
		return bCancelled;
	}

}
