package com.ca.arcserve.linuximaging.ui.client.homepage.node;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;


public class DeleteNodeDialog extends Dialog {
	private CheckBox forceDelete;
	private NodeTable parentTable;
	
	public DeleteNodeDialog(NodeTable table) {
		parentTable = table;

		this.setHeading(UIContext.Constants.deleteNode());	   
	    this.setButtons(Dialog.YESNOCANCEL);
	    this.setFocusWidget(this.getButtonById(Dialog.NO));
	    this.setModal(true);
	    this.setButtonAlign(HorizontalAlignment.CENTER);
	    this.setSize(350, 220);
	    this.setResizable(false);
	    	
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setCellPadding(3);
		tableLayout.setCellSpacing(3);
		this.setLayout(tableLayout);
		
		LabelField label = new LabelField(UIContext.Constants.deleteConfirmMessage());
		forceDelete = new CheckBox();
		forceDelete.setValue(true);
		forceDelete.setBoxLabel(UIContext.Constants.forceDeleteNode());
		
		LabelField info = new LabelField(UIContext.Constants.forceDeleteNodeToolTip());
		this.add(label);
		this.add(forceDelete);
		this.add(info);
		
		this.getButtonById(Dialog.YES).addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if ( parentTable != null ) {
					parentTable.deleteNode(isForcibly(), DeleteNodeDialog.this);
					if ( isForcibly() ) {
						DeleteNodeDialog.this.hide();
					}
				}
			}});
		this.getButtonById(Dialog.NO).addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				DeleteNodeDialog.this.hide();
			}});
		this.getButtonById(Dialog.CANCEL).setText(UIContext.Constants.help());
		this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				Utils.showURL(UIContext.helpLink+HelpLinkItem.NODE_DELETE_HELP);
			}});
	}
	
	public boolean isForcibly() {
		if ( forceDelete != null ) {
			return forceDelete.getValue();
		}
		return false;
	}
	
	public void maskAllPanel(boolean force) {
		//if ( !force ) {
			this.mask(UIContext.Constants.deleting());
		//}
	}
	
	public void unmaskAllPanel(boolean force, boolean close) {
		if ( !force ) {
			this.unmask();
		}
		if ( close ) {
			this.hide();
		}
	}
}
