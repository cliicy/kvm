package com.ca.arcserve.linuximaging.ui.client.login;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;

public class LoginWindow extends LayoutContainer {

	final LoginServiceAsync loginService = GWT.create(LoginService.class);
	private TextField<String> nameTextField;
	private TextField<String> passwordTextField;
	private LabelField buildLabel;
	private Button button;
	private LoginWindow thisWindow;
	private LoginKeyListener keyListener =  new LoginKeyListener();
	
	public LoginWindow(){
		this.thisWindow = this;
		//this.setWidth(370);
		/*LayoutContainer container = new LayoutContainer();
		
		TableLayout layout0 = new TableLayout();
		layout0.setWidth("98%");
		layout0.setColumns(1);
		layout0.setCellPadding(0);
		layout0.setCellSpacing(0);
		container.setLayout(layout0);*/

		//LabelField label = new LabelField();
		//label.setText("");
		//container.add(label);
		
		LayoutContainer loginContainer = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(3);
		layout.setCellSpacing(0);
		loginContainer.setLayout(layout);
		this.add(loginContainer);
		
		TableData tableData = new TableData();
		tableData.setColspan(2);
		
		//label = new LabelField();
		//label.setText(UIContext.Constants.userName());
		//label.setWidth(100);		
		//label.setStyleAttribute("margin", "25px 0px 0px 0px;");		
		//loginContainer.add(label,tableData);
		
		nameTextField = new TextField<String>();
		nameTextField.ensureDebugId("92be14c0-b588-413e-a347-c472f3bb76c9");
		//nameTextField.setWidth("100%");
		nameTextField.setWidth(247);
		nameTextField.setAllowBlank(false);
		nameTextField.addKeyListener(keyListener);
		nameTextField.setStyleAttribute("margin", "25px 0px 0px 0px;");
		nameTextField.setStyleName("login-input");
		loginContainer.add(nameTextField, tableData);
		
		//label = new LabelField();
		//label.setText(UIContext.Constants.password());
		//label.setWidth(100);		
		//loginContainer.add(label,tableData);
		
		passwordTextField = new TextField<String>();
		passwordTextField.ensureDebugId("350083ef-768b-4a00-bc1f-e5f4eb510cf1");
		passwordTextField.setPassword(true);
		//passwordTextField.setWidth("100%");
		passwordTextField.setWidth(247);
		passwordTextField.setValue("");
		passwordTextField.addKeyListener(keyListener);
		passwordTextField.setStyleName("login-input");
		loginContainer.add(passwordTextField, tableData);
		
		//loginContainer.add(new Html(""));
		
		button = new Button();
		button.ensureDebugId("7ba45dca-14ca-4936-abfc-363ab6e4e88a");
		//button.setStyleAttribute("margin", "25px 3px 0px 0px;");
		button.setText(UIContext.Constants.login());
		button.setMinWidth(50);
		buildLabel = new LabelField();
		buildLabel.setStyleName("buildtext");
		buildLabel.setText(UIContext.Messages.buildVerionForLogin(UIContext.versionInfo.getVersion(), UIContext.versionInfo.getBuildNumber()));
		//buildLabel.setVisible(false);
		loginContainer.add(buildLabel);
		loginContainer.add(button, new TableData(Style.HorizontalAlignment.RIGHT, Style.VerticalAlignment.MIDDLE));
		
		button.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				setComponentEnabled(false);
				validateUser();
			}

		});
		
	}
	
	@Override
	  protected void onRender(Element target, int index) {
		  super.onRender(target, index);
		  //this.setFocusWidget(passwordTextField);
		  passwordTextField.focus();
	  }	
	
	class LoginKeyListener extends KeyListener{

		@Override
		public void componentKeyPress(ComponentEvent event) {
			if (event.getKeyCode() == KeyCodes.KEY_ENTER)
				button.fireEvent(Events.Select);
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();	
		if(UIContext.versionInfo.isShowDefaultUserWhenLogin()){
			nameTextField.setValue(UIContext.versionInfo.getDefaultUser());
		}
		passwordTextField.focus();
	}
	
	private void validateUser(){
		loginService.validateUser(null,nameTextField.getValue(), passwordTextField.getValue(), new BaseAsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				setComponentEnabled(true);
			}

			@Override
			public void onSuccess(String result) {
				if(result != null){
					thisWindow.hide();
				}else{
					setComponentEnabled(true);
					Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Constants.invalidCredential());
				}
			}
			
		});
	}
	
	private void setComponentEnabled(boolean isEnable){
		button.setEnabled(isEnable);
		nameTextField.setEnabled(isEnable);
		passwordTextField.setEnabled(isEnable);
	}
}
