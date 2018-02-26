package com.ca.arcserve.linuximaging.ui.client.components.backup.node;

import com.ca.arcserve.linuximaging.ui.client.components.backup.node.i18n.NodeUIConstants;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;

public class ComponentNodeAddServerDlg extends Dialog {
	private static final NodeUIConstants uiConstants = GWT.create(NodeUIConstants.class);

	private ComponentNode nodeTree = null;
	private TextField<String> serverNameTextField;
	private TextField<String> usernameTextField;
	private TextField<String> passwdTextField;
	protected boolean isClickOK = false;
	private String serverName = null;
	private String userName = null;
	private String passwd = null;
	
	public ComponentNodeAddServerDlg() {
		setButtons(Dialog.OKCANCEL);
		setButtonAlign(HorizontalAlignment.CENTER);
		setModal(true);
		this.setResizable(false);
		this.setWidth(350);
		this.setHideOnButtonClick(true);
		setHeading(uiConstants.addServer());
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("95%");
		tableLayout.setCellPadding(3);
		tableLayout.setCellSpacing(0);
		tableLayout.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		tableLayout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		setLayout(tableLayout);
		
		Label lblAddServer = new Label(uiConstants.lblServername_text());
		add(lblAddServer);
		
		serverNameTextField = new TextField<String>();
		serverNameTextField.setAllowBlank(false);		
		add(serverNameTextField);
		
		Label lblUserName = new Label(uiConstants.lblUserName_text());
		add(lblUserName);
		
		usernameTextField = new TextField<String>();
		usernameTextField.setAllowBlank(false);
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
		add(usernameTextField);
		
		Label lblPassword = new Label(uiConstants.lblPassword_text());
		add(lblPassword);
		
		passwdTextField = new TextField<String>();
		passwdTextField.setPassword(true);	
		add(passwdTextField);
		
		this.getButtonById(OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				isClickOK = true;
				serverName = serverNameTextField.getValue();
				userName = usernameTextField.getValue();
				passwd = passwdTextField.getValue();
				
				if (serverName == null && serverName.isEmpty())
				{
					Window.alert("Server name can't be empty!");
					return;
				}

				if (userName == null)
					userName = "";

				if (passwd == null)
					passwd = "";
				
				if (nodeTree != null)
				{
					nodeTree.AddElement(serverName, userName, passwd);
				}			
			}

		});

		this.getButtonById(CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				isClickOK = false;				
			}
		});
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public boolean isClickOK() {
		return isClickOK;
	}

	public void setClickOK(boolean isClickOK) {
		this.isClickOK = isClickOK;
	}

	public void setNodeTree(ComponentNode tree) {
		nodeTree = tree;
	}

}
