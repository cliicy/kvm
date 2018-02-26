package com.ca.arcserve.linuximaging.ui.client.restore;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;

public class IPSettingsPanel extends LayoutContainer {
	public TextField<String> txtIPAddress;
	public TextField<String> txtSubnetMask;
	public TextField<String> txtDefaultGateway;
	
	public IPSettingsPanel() {
		this.setWidth(350);
//		this.setHideOnButtonClick(true);
//		setHeading(UIContext.Constants.staticIPSettings());
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("95%");
		tableLayout.setCellPadding(3);
		tableLayout.setCellSpacing(0);
		tableLayout.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		tableLayout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		setLayout(tableLayout);
		
		LabelField lblIPAddress = new LabelField(UIContext.Constants.ipAddress());
		add(lblIPAddress);
		
		txtIPAddress = new TextField<String>();
//		txtIPAddress.setAllowBlank(false);		
		add(txtIPAddress);
		
		LabelField lblSubnetMask = new LabelField(UIContext.Constants.subnetMask());
		add(lblSubnetMask);
		
		txtSubnetMask = new TextField<String>();
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
		
		LabelField lblDefaultGateway = new LabelField(UIContext.Constants.defaultGateway());
		add(lblDefaultGateway);
		
		txtDefaultGateway = new TextField<String>();
		add(txtDefaultGateway);

	}

	public boolean validate() {
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

}
