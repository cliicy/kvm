package com.ca.arcserve.linuximaging.ui.client.restore;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.ui.Label;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;

public class IPSettingsDialog extends Dialog {
	public TextField<String> txtIPAddress;
	public TextField<String> txtSubnetMask;
	public TextField<String> txtDefaultGateway;
	private boolean isClickOK = false;
	
	public IPSettingsDialog(String ipAddress, String subnetMask, String gateway) {
		setButtons(Dialog.OKCANCEL);
		setButtonAlign(HorizontalAlignment.CENTER);
		setModal(true);
		this.setResizable(false);
		this.setWidth(350);
//		this.setHideOnButtonClick(true);
		setHeading(UIContext.Constants.staticIPSettings());
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("95%");
		tableLayout.setCellPadding(3);
		tableLayout.setCellSpacing(0);
		tableLayout.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		tableLayout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		setLayout(tableLayout);
		
		Label lblIPAddress = new Label(UIContext.Constants.ipAddress());
		add(lblIPAddress);
		
		txtIPAddress = new TextField<String>();
		txtIPAddress.setValue(ipAddress);
//		txtIPAddress.setAllowBlank(false);		
		add(txtIPAddress);
		
		Label lblSubnetMask = new Label(UIContext.Constants.subnetMask());
		add(lblSubnetMask);
		
		txtSubnetMask = new TextField<String>();
		txtSubnetMask.setValue(subnetMask);
//		txtSubnetMask.setAllowBlank(false);
		/*usernameTextField.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value == null
						|| value.indexOf('\\') < 1
						|| (value.trim().indexOf('\\') == (value.trim()
								.length() - 1))) {
					return uiConstants.errorUserNamePattern();
				}

				return null;
			}
		});*/
		add(txtSubnetMask);
		
		Label lblDefaultGateway = new Label(UIContext.Constants.defaultGateway());
		add(lblDefaultGateway);
		
		txtDefaultGateway = new TextField<String>();
		txtDefaultGateway.setValue(gateway);
		add(txtDefaultGateway);
		
		this.getButtonById(OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if(validate()){
					isClickOK = true;
					IPSettingsDialog.this.hide();
				}
			}

			
		});

		this.getButtonById(CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				isClickOK = false;	
				IPSettingsDialog.this.hide();
			}
		});
	}

	private boolean validate() {
		String ipAddress = txtIPAddress.getValue();
		String subnetMask = txtSubnetMask.getValue();
		String gateway = txtDefaultGateway.getValue();
		if (!Utils.validateIPAddress(ipAddress)){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.ipAddressMessage());
			return false;
		}

		if (!Utils.validateIPAddress(subnetMask)){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.subnetMaskMessage());
			return false;
		}

		if (!Utils.validateIPAddress(gateway)){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.defaultGatewayMessage());
			return false;
		}
		return true;
	}

	public boolean isClickOK() {
		return isClickOK;
	}


}
