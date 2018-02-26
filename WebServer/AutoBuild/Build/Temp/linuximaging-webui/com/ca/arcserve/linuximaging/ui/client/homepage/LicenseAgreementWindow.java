package com.ca.arcserve.linuximaging.ui.client.homepage;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class LicenseAgreementWindow extends Window {
	private final HomepageServiceAsync homepageService = GWT.create(HomepageService.class);
	protected String agreementText;
	
	protected ContentPanel agreementPanel;
	
	public LicenseAgreementWindow() {
		this(null);
	}
	
	public LicenseAgreementWindow(String agreementText) {
		this.agreementText = agreementText;
		this.addButton(createCloseButton());
		this.addButton(createPrintButton());
		
	}
	
	protected Button createPrintButton() {
		Button button = new Button(UIContext.Constants.licensePrint());
		button.addListener(Events.Select, createPrintListener());
		this.setFocusWidget(button);
		return button;
	}
	
	protected <E extends BaseEvent> Listener<E> createPrintListener() {
		return new Listener<E>() {

			@Override
			public void handleEvent(E be) {
				print(agreementText);
			}
			
		};
	}
	
	protected Button createCloseButton() {
		Button button = new Button(UIContext.Constants.OK());
		
		button.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				LicenseAgreementWindow.this.hide();
			}
			
		});
		
		this.setFocusWidget(button);
		
		return button;
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		this.setWidth(500);
		this.setHeight(500);
		this.setResizable(false);
		this.setModal(true);
		this.setHeading(UIContext.Constants.licenseAgreement());
		
		LabelField note = new LabelField();
		note.ensureDebugId("c4bb01ae-5475-4956-9f0f-dba1f512cc95");
		note.setText("<body>" + UIContext.Constants.licenseImportantContent() + "</body>");
		this.add(note, new FlowData(13,25,13,25));
		
		agreementPanel = new ContentPanel();
		agreementPanel.ensureDebugId("a3a037b3-f8f6-48ad-aa06-851c068b4047");
		agreementPanel.setHeaderVisible(false);
		agreementPanel.setHeight("335px");
		agreementPanel.setScrollMode(Scroll.AUTOY);
		agreementPanel.add(createAgreementHtml());
		this.add(agreementPanel, new FlowData(10));
	}
	
	private Html createAgreementHtml() {
		final Html agreementHtml = new Html();
		agreementHtml.ensureDebugId("fc42a235-f9a6-4d65-803b-339fcf203d07");
		
		if (agreementText != null && !agreementText.isEmpty()) {
			agreementHtml.setHtml(agreementText);
		} else {
			agreementHtml.addListener(Events.Attach, new Listener<BaseEvent>() {
				
				@Override
				public void handleEvent(BaseEvent be) {
					agreementHtml.mask();
					homepageService.getLicenseText(new BaseAsyncCallback<String>() {
						
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							agreementHtml.unmask();
						}
						
						@Override
						public void onSuccess(String result) {
							agreementText = result;
							agreementHtml.setHtml(result);
							agreementHtml.unmask();
						}
						
					});
				}
				
			});
		}
		
		return agreementHtml;
	}
	
	
	public static native void print(String html)/*-{
		var frame = $doc.getElementById("__gwt_historyFrame");
		if (!frame){
			return;
		}
		
		frame = frame.contentWindow;
		var doc = frame.document;
		doc.open();
		doc.write(html);
		doc.close();
		frame.focus();
		frame.print();
	}-*/;
	
}
