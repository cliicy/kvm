package com.ca.arcserve.linuximaging.ui.client.license;


import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
//import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
//import com.google.gwt.event.dom.client.KeyCodes;

public class LicenseManagementWindow extends Window {
	private LicenseManagementPanel licenseManagementPanel;

	public LicenseManagementWindow(){
		this.setSize(750, 550);
		this.setLayout(new FitLayout());
		this.setResizable(true);
		this.setMaximizable(true);  
		this.setClosable(true);
		this.addListener(Events.Resize, new Listener<ComponentEvent>(){

			@Override
			public void handleEvent(ComponentEvent be) {
				licenseManagementPanel.refreshLicenseMachine();
			}
			
		});
		
		this.setHeading(UIContext.Constants.licenseWindowHeader());

		setupButtons();

		licenseManagementPanel = new LicenseManagementPanel();
		licenseManagementPanel.ensureDebugId("50ab48fe-aaa0-48f3-bd22-643de5bf4e9f");
		this.add(licenseManagementPanel);
	}

	private void setupButtons() {
		Button closeButton = new Button(UIContext.Constants.close());
		closeButton.ensureDebugId("4c4a9712-a466-4cb1-92bc-9bdf0814f0f2");
		closeButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				LicenseManagementWindow.this.hide();
			}

		});
		this.addButton(closeButton);


		Button helpButton = new Button(UIContext.Constants.help());
		helpButton.ensureDebugId("2fbdf2cb-141a-4463-9466-416190bdd026");
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				Utils.showURL(UIContext.helpLink+HelpLinkItem.LICENSE);
			}

		});
		this.addButton(helpButton);
	}
	
//	@Override
//	protected void onKeyPress(WindowEvent we) {
//		super.onKeyPress(we);
//		
//		if(KeyCodes.KEY_ENTER == we.getKeyCode()) {
//			this.hide();
//		}
//	}
}