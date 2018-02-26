package com.ca.arcserve.linuximaging.ui.client.homepage;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class AboutWindow  extends Window{
	private Window window;

	public AboutWindow(){
		this.addStyleName("aboutWindow");
		this.window = this;
		addOkButton();
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		this.setWidth(500);
		//this.setHeight(500);
		this.setResizable(false);
		this.setHeading(UIContext.Constants.about() + " " + UIContext.productName);
		Image image = new Image(UIContext.IconBundle.aboutProductLogo());
		//image.setStyleName("imageMargin");
		this.add(image);
		this.add(createContent(), new FlowData(0, 25, 0, 25));
	}
	
	private LayoutContainer createContent() {
		/*LayoutContainer container = new LayoutContainer();
		container.setWidth("100%");
		container.setHeight("100%");
		container.setStyleAttribute("background-color", "#FFFFFF");*/
		LayoutContainer panel = new LayoutContainer();
		/*panel.setStyleAttribute("margin-bottom", "13px");
		panel.setStyleAttribute("margin-right", "25px");
		panel.setStyleAttribute("margin-left", "25px");*/
		
		
		/*VerticalPanel logoPanel = new VerticalPanel();
		logoPanel.setTableWidth("100%");
		logoPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		Image image = new Image(UIContext.IconBundle.aboutProductLogo());
		image.setStyleName("imageMargin");
		logoPanel.add(image);
		
		panel.add(logoPanel);*/
		panel.add(createText(UIContext.Constants.productName()));
		
		panel.add(createText(UIContext.Messages.buildVerion(UIContext.versionInfo.getVersion(), UIContext.versionInfo.getBuildNumber())));
		
		panel.add(createText(UIContext.Constants.aboutWindowCopyRight()));
		panel.add(createLicenseAgreementWidget());
		LabelField warningLabel = createText(UIContext.Constants.aboutWindowWarning());
		warningLabel.addStyleName("warnings");
		panel.add(warningLabel);
		panel.add(createLinkWidget());
		return panel;
	}
	
	private LabelField createText(String text) {
		LabelField label =  new LabelField(text);
		label.addStyleName("aboutText");
		return label;
	}
	
	private void addOkButton(){
		Button okButton = new Button();
		okButton.setText(UIContext.Constants.OK());
		okButton.ensureDebugId("1a960f71-f05f-478d-b7de-724a655a809c");
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		});
		this.addButton(okButton);
		this.setFocusWidget(okButton);
	}
	
	private Widget createLicenseAgreementWidget() {
		LabelField license = new LabelField(UIContext.Constants.endUserLicenseAgreement());
		license.addStyleName("licenseAgreement");
		
		license.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				LicenseAgreementWindow window = new LicenseAgreementWindow();
				window.ensureDebugId("7a4ce892-7485-41d4-9912-494d1fcfa671");
				window.show();
			}
			
		});
		
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(license);
		return panel;
	}
	
	private Widget createLinkWidget() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.addStyleName("links");
		Button onlineSupportButton = new Button(UIContext.Constants.onlineSupport());
		onlineSupportButton.ensureDebugId("2c724db2-95fe-45e6-8fd7-590cea059266");
		onlineSupportButton.setBorders(true);
		onlineSupportButton.addStyleName("ca-tertiaryText");
		onlineSupportButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Utils.showURL(UIContext.helpLink+HelpLinkItem.CA_SUPPORT);
			}
			
		});
		panel.add(onlineSupportButton);
		
		Button releaseNotesButton = new Button(UIContext.Constants.releaseNotes());
		releaseNotesButton.ensureDebugId("ee6b65f0-5dd1-4df4-abb7-17ebb06cdbfe");
		releaseNotesButton.addStyleName("ca-tertiaryText");
		releaseNotesButton.setBorders(true);
		releaseNotesButton.setStyleAttribute("margin-left", "10px");
		releaseNotesButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Utils.showURL(UIContext.helpLink+HelpLinkItem.RELEASE_NOTES);
			}
			
		});
		panel.add(releaseNotesButton);
		
		return panel;
	}
	
}
