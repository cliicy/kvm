package com.ca.arcserve.linuximaging.ui.client.common;


import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.BackupLocationInfoModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

public class UserPasswordWindow extends Window {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private UserPasswordWindow thisWindow;
	private TextField<String> nameTextField;
	private TextField<String> passwordTextField;
	private Button okButton;
	private Button cancelButton;
	private String path;
	private int locationType = BackupLocationInfoModel.TYPE_CIFS;
	private boolean isClickOK = false;
	private CredKeyListener keyListener = new CredKeyListener();
	
	public static final String ICON_LOADING = "images/gxt/icons/grid-loading.gif";
	private Image image = new Image(ICON_LOADING);// IconHelper.create(ICON_LOADING,
													// 16, 16).createImage();
	private int MIN_BUTTON_WIDTH = 90;
	private ServiceInfoModel d2dServer;

	public UserPasswordWindow(String sharePath, String username, String password,int locationType,ServiceInfoModel d2dServerModel) {
		this.thisWindow = this;
		this.path = sharePath;
		this.d2dServer = d2dServerModel;
		this.locationType = locationType;
		this.setResizable(false);
		this.setWidth(380);
		// this.setHeading();
		this.setHeading("");

		TableLayout layout = new TableLayout();
		layout.setWidth("95%");
		layout.setColumns(3);
		layout.setCellPadding(4);
		layout.setCellSpacing(0);
		this.setLayout(layout);

		TableData tableData = new TableData();
		tableData.setColspan(3);

		// Table section just for the loading icon and
		LayoutContainer lc = new LayoutContainer();
		TableLayout subTable = new TableLayout();
		subTable.setWidth("100%");
		subTable.setColumns(2);
		lc.setLayout(subTable);
		TableData td = new TableData();
		td.setWidth("0%");
		image.setVisible(false);
		lc.add(image, td);

		td = new TableData();
		td.setWidth("100%");

		LabelField label = new LabelField();
		label.setText(UIContext.Messages.connectToNetworkPath(path));
		label.addStyleName("restoreWizardSubItem");
		label.setStyleAttribute("word-wrap", "break-word");
		label.setStyleAttribute("word-break", "break-all");
		//label.setWidth(320);
		lc.add(label, td);

		this.add(lc, tableData);

		tableData = new TableData();
		tableData.setWidth("30%");

		label = new LabelField();
		if(locationType == BackupLocationInfoModel.TYPE_AMAZON_S3){
			label.setText(UIContext.Constants.accessId());
		}else{
			label.setText(UIContext.Constants.userName());
		}
		label.addStyleName("connectDialogSpacing");
		this.add(label, tableData);

		tableData = new TableData();
		tableData.setColspan(2);
		tableData.setWidth("70%");
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);

		nameTextField = new TextField<String>();
		nameTextField.setWidth("100%");
		nameTextField.ensureDebugId("328c02b0-6c3b-4ef7-a5ea-67c95656aaa1");
		nameTextField.setValue(username);
		nameTextField.addKeyListener(keyListener);
		this.add(nameTextField, tableData);

		label = new LabelField();
		if(locationType == BackupLocationInfoModel.TYPE_AMAZON_S3){
			label.setText(UIContext.Constants.accessKey());
		}else{
			label.setText(UIContext.Constants.password());
		}
		label.addStyleName("connectDialogSpacing");
		tableData = new TableData();
		tableData.setWidth("30%");
		this.add(label, tableData);

		passwordTextField = new TextField<String>();
		passwordTextField.ensureDebugId("aec00e43-ad32-42a6-96c5-9695282453cd");
		passwordTextField.setPassword(true);
		passwordTextField.setValue(password);
		passwordTextField.addKeyListener(keyListener);
		passwordTextField.setWidth("100%");
		tableData = new TableData();
		tableData.setColspan(2);
		tableData.setWidth("70%");
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		this.add(passwordTextField, tableData);

		tableData = new TableData();
		tableData.setColspan(3);
		label = new LabelField();
		label.setText(UIContext.Constants.loginUsernameExample());
		label.addStyleName("connectDialogSpacing");
		if(locationType != BackupLocationInfoModel.TYPE_AMAZON_S3){
			this.add(label, tableData);
		}

		//this.add(new Html(""));
		
		okButton = new Button();
		okButton.ensureDebugId("a8b8a97c-5aaf-4af5-9bdf-307b5f35e036");
		okButton.setText(UIContext.Constants.OK());
		okButton.setMinWidth(MIN_BUTTON_WIDTH);
		//this.add(okButton, new TableData(Style.HorizontalAlignment.RIGHT,
				//Style.VerticalAlignment.MIDDLE));

		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(d2dServer !=null){
					thisWindow.mask(UIContext.Constants.validating());
					isClickOK = true;
					BackupLocationInfoModel infoModel = new BackupLocationInfoModel();
					infoModel.setSessionLocation(path);
					infoModel.setUser(nameTextField.getValue());
					infoModel.setPassword(passwordTextField.getValue());
					infoModel.setType(UserPasswordWindow.this.locationType);
					service.validateBackupLocation(d2dServer, infoModel, new BaseAsyncCallback<Boolean>(){
	
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							thisWindow.unmask();
						}
	
						@Override
						public void onSuccess(Boolean result) {
							if(result == true){
								thisWindow.hide();
							}else{
								Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR, UIContext.Constants.vaildateBackupLocationFailed());
								thisWindow.unmask();
							}
						}
						
					});
				}else{
					thisWindow.hide();
				}
			}

		});
		this.addButton(okButton);
		cancelButton = new Button();
		cancelButton.ensureDebugId("ea145e35-6ed5-4202-bd89-a7c88a0f03e7");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.setMinWidth(MIN_BUTTON_WIDTH);
		//this.add(cancelButton, new TableData(Style.HorizontalAlignment.RIGHT,
				//Style.VerticalAlignment.MIDDLE));

		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				isClickOK = false;
				thisWindow.hide();
			}

		});
		this.addButton(cancelButton);
		this.setButtonAlign(HorizontalAlignment.CENTER);
	}

	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		this.setFocusWidget(nameTextField);
		passwordTextField.focus();
	}

	public String getUsername() {
		return nameTextField.getValue();
	}

	public String getPassword() {
		return passwordTextField.getValue();
	}

	public boolean getCancelled() {
		return !isClickOK;
	}

	private class CredKeyListener extends KeyListener {
		@Override
		public void componentKeyPress(ComponentEvent event) {
			if (event.getKeyCode() == KeyCodes.KEY_ENTER)
				okButton.fireEvent(Events.Select);
		}
	}

	@Override
	public void hide(Button buttonPressed) {
		super.hide(buttonPressed);
		if(isClickOK){
			Utils.cacheConnectionInfo(path, getUsername(), getPassword());
		}
	}	
}
