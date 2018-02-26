package com.ca.arcserve.linuximaging.ui.client.components.configuration;

import com.ca.arcserve.linuximaging.ui.client.components.configuration.backuptemplate.BackupTemplateSettings;
import com.ca.arcserve.linuximaging.ui.client.components.configuration.icons.FlashImageBundle;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

public class ConfigurationPanel extends LayoutContainer {
	FlashImageBundle IconBundle = GWT.create(FlashImageBundle.class);
	public final int STACK_WEB_SERVER = 0;
	public final int STACK_BACKUP_TEMPLATE = 1;
//	private TabPanel configTabPanel = null;
	private DeckPanel configDeckPanel;
	private VerticalPanel toggleButtonPanel;
	private ToggleButton tbWebServerLabel;
	private ToggleButton tbTemplateLabel;
	private ToggleButton tbWebServerButton;
	private ToggleButton tbTemplateButton;
	private ClickHandler chWebServerHandler;
	private ClickHandler chTemplateHandler;
	private BackupTemplateSettings backupTemplateSettings;
	private LayoutContainer lcbackupTemplateContainer;
	public ConfigurationPanel() {
		this.setLayout(new RowLayout(Orientation.VERTICAL));
		
		LayoutContainer contentPanel = new LayoutContainer();
		contentPanel.setLayout(new RowLayout( Orientation.HORIZONTAL ) );
		contentPanel.setStyleAttribute("background-color","#DFE8F6");
		
		defineToggleButtonPanel();
		defineDeckPanel();
		
		contentPanel.add( toggleButtonPanel, new RowData(140, 1));
		contentPanel.add( configDeckPanel, new RowData(1, 1));
		
		this.add( contentPanel, new RowData( 1, 1 ) );
		configDeckPanel.showWidget(STACK_BACKUP_TEMPLATE);
	}
	private void defineDeckPanel() {
		configDeckPanel = new DeckPanel();
		LayoutContainer advancedContainer = new LayoutContainer();
		advancedContainer.setStyleAttribute("background-color","white");
		advancedContainer.setStyleAttribute("padding", "10px");
		advancedContainer.add(new Button("Developing"));
		configDeckPanel.add(advancedContainer);
		
		backupTemplateSettings = new BackupTemplateSettings();
		lcbackupTemplateContainer = new LayoutContainer();
		lcbackupTemplateContainer.add(backupTemplateSettings);
		lcbackupTemplateContainer.setStyleAttribute("padding", "10px");
		configDeckPanel.add(lcbackupTemplateContainer);
	}
	private void defineToggleButtonPanel() {
		toggleButtonPanel = new VerticalPanel();
		toggleButtonPanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		toggleButtonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		toggleButtonPanel.setTableWidth("100%");
		
		chWebServerHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				configDeckPanel.showWidget(STACK_WEB_SERVER);
				tbTemplateButton.setDown(false);
				tbWebServerButton.setDown(true);
				
				tbTemplateLabel.setDown(false);
				tbWebServerLabel.setDown(true);
			}			
		};
		
		tbWebServerButton = new ToggleButton(new Image(IconBundle.configuration_web_server()));
		tbWebServerButton.ensureDebugId("AB330B95-5473-46b7-9A75-EEFE0762923C");
		tbWebServerButton.setStylePrimaryName("demo-ToggleButton");
		tbWebServerButton.addClickHandler(chWebServerHandler);
		toggleButtonPanel.add(tbWebServerButton);
		
		tbWebServerLabel = new ToggleButton("Web Server");
		tbWebServerLabel.ensureDebugId("EC4E00F2-6E16-4eca-ADF6-5D1014F2BA19");
		tbWebServerLabel.setStylePrimaryName("tb-settings");
		tbWebServerLabel.setDown(true);
		tbWebServerLabel.addClickHandler(chWebServerHandler);
		toggleButtonPanel.add(tbWebServerLabel);
		
		chTemplateHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				configDeckPanel.showWidget(STACK_BACKUP_TEMPLATE);
				tbTemplateButton.setDown(true);
				tbWebServerButton.setDown(false);
				
				tbTemplateLabel.setDown(true);
				tbWebServerLabel.setDown(false);
			}			
		};
		
		tbTemplateButton = new ToggleButton(new Image(IconBundle.configuration_backup_template()));
		tbTemplateButton.ensureDebugId("35963CD0-3E34-4c55-8436-B4A987703FAE");
		tbTemplateButton.setStylePrimaryName("demo-ToggleButton");
		tbTemplateButton.setDown(true);
		tbTemplateButton.addClickHandler(chTemplateHandler);
		toggleButtonPanel.add(tbTemplateButton);
		
		tbTemplateLabel = new ToggleButton("Template");
		tbTemplateLabel.ensureDebugId("3A89A05C-EC63-49c0-898C-7841AF5178B8");
		tbTemplateLabel.setStylePrimaryName("tb-settings");
		tbTemplateLabel.setDown(true);
		tbTemplateLabel.addClickHandler(chTemplateHandler);
		toggleButtonPanel.add(tbTemplateLabel);
	}

	
//	@Override
//	protected void onRender(Element e, int i)
//	{
//		super.onRender(e, i);
//		add(configTabPanel);
//	}

}
