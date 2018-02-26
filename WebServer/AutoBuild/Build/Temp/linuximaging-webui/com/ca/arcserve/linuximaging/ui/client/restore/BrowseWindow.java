package com.ca.arcserve.linuximaging.ui.client.restore;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseTreePanel;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.CatalogModelType;
import com.ca.arcserve.linuximaging.ui.client.model.FileModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

public class BrowseWindow extends Dialog {

	private BrowseWindow thisWindow;
	private boolean showFiles = false;
	public TreeStore<FileModel> store;
	private BaseTreePanel tree;
	private TreeLoader<FileModel> loader;
	private TextField<String> folderField;	
	private FieldSet notificationSet;
	private String lastClicked;
	private LabelField treeStatusLabel;
	public LayoutContainer treeContainer;
	final HomepageServiceAsync service = GWT.create(HomepageService.class);

	private String inputFolder;
	private String host;
	private String user;
	private String password;
	private boolean isTreeCreated = false;
	private String scriptUUID;
	
	private LayoutContainer buttonContainer;
	String parentDir;
	
	private static String actualPath;
	private String cancelID = "7BCCEC1E-7A1C-42e8-9247-82E1DF27D3A1";
	private String okID = "01155BB0-71B7-4f35-A550-0038F662FB9C"; 
	
	private ServiceInfoModel serviceInfo;

	public BrowseWindow(ServiceInfoModel serviceInfo, boolean showFiles, String title) {
		super();
		this.serviceInfo=serviceInfo;
		this.showFiles = showFiles;
		this.thisWindow = this;
		this.setHeading(title);
		this.setButtonAlign(HorizontalAlignment.CENTER);
	}
	
	public String getInputFolder() {
		return inputFolder;
	}

	public void setInputFolder(String inputFolder) {
		if(inputFolder!=null) {
			inputFolder = inputFolder.trim();
		}
		this.inputFolder = inputFolder;
		actualPath = inputFolder;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	RpcProxy<List<FileModel>> proxy = new RpcProxy<List<FileModel>>() {
		@Override
		protected void load(Object loadConfig,
				final AsyncCallback<List<FileModel>> callback) {
			GWT.log("Proxy Load", null);
			FileModel fileModel = (FileModel) loadConfig;
			String fullPath = fileModel != null ? fileModel.getPath() : null;
			
//			String userName = fileModel.getUserName();
//			if (userName == null || userName.length() == 0) {
//				userName = user;
//			}
//			if (userName == null)
//				userName = "";
//
//			String passwd = fileModel.getPassword();
//			if (passwd == null || passwd.length() == 0) {
//				passwd = password;
//			}
//
//			if (passwd == null)
//				passwd = "";

			if (fullPath == null)
				fullPath = UIContext.FILE_SEPATATOR;

			service.getFileItems(serviceInfo, fullPath, host, user, password, showFiles,scriptUUID, callback);
				
		}
	};
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setButtons(Dialog.OKCANCEL);

		LayoutContainer container = new LayoutContainer();
		RowLayout rl = new RowLayout();
		container.setLayout(rl);

		RowData rd = new RowData();
		Margins margins = new Margins();
		margins.left = 5;
		margins.right = 5;
		rd.setMargins(margins);
		
		LayoutContainer topContainer = new LayoutContainer();
		topContainer.setStyleAttribute("margin-right", "4px");
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setColumns(2);
		topContainer.setLayout(tableLayout);

		LabelField label = new LabelField();
		if(!showFiles)
			label.setText(UIContext.Constants.browseSelectFolder());
		else 
			label.setText(UIContext.Constants.browseSelectFile());
		topContainer.add(label);
		
		TableData tableData = new TableData();
		tableData.setHorizontalAlign(HorizontalAlignment.RIGHT);
		
		buttonContainer = new LayoutContainer();
		TableLayout tblLayout = new TableLayout();
		tblLayout.setColumns(2);
		buttonContainer.setLayout(tblLayout);
		buttonContainer.setWidth(32);
		
		IconButton upImage = new IconButton("browse_arrow-up");
		upImage.ensureDebugId("0E276D4E-B443-4380-BB19-E9C9EC6DBF3B");
		Utils.addToolTip(upImage, UIContext.Constants.browseWindowUpTooltip());
		upImage.setStyleName("homepage_task_icon", true);
		upImage.addSelectionListener(new SelectionListener<IconButtonEvent>(){

			@Override
			public void componentSelected(IconButtonEvent ce) {				
				if (store!=null)
					store.removeAll();
				String parentFolder = getParentFolder();
				inputFolder = parentFolder;
				FileModel fileModel = new FileModel();
				fileModel.setName("");
				fileModel.setPath(inputFolder);
				folderField.setValue(inputFolder);
				
				if( parentFolder == null)
					isTreeCreated = false;
				
				if (isTreeCreated == false)
					createTree();
				else
					loader.load(fileModel);
			}
			
		});
	
		buttonContainer.add(upImage);
		
		//Create Dir
		IconButton createImage = new IconButton("browse_new_folder");
		createImage.ensureDebugId("65DB88CC-73EF-4417-8CEA-3D402C2F76CB");
		Utils.addToolTip(createImage, UIContext.Constants.browseWindowNewTooltip());
		createImage.setStyleName("homepage_task_icon", true);
		createImage.addSelectionListener(new SelectionListener<IconButtonEvent>(){

			@Override
			public void componentSelected(IconButtonEvent ce) {
				//Popup a dialog, create the folder from here
				if (thisWindow.folderField.getValue() != null && thisWindow.folderField.getValue().length() > 0)
					parentDir = thisWindow.folderField.getValue();
				else
				{
					List<FileModel> list = thisWindow.store.getRootItems();
					parentDir = list.get(0).getPath();
				}
				
				String message = "<div style=\"word-wrap: break-word; word-break: break-all\"> " 
					+ UIContext.Messages.browseWindowCreateAFolderUnder(parentDir)
					+ "</div>";
				
				MessageBox.prompt(UIContext.Constants.browseWindowNewTooltip(), 
						message, 
						new Listener<MessageBoxEvent>(){

					@Override
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId()
								.equals(
										com.extjs.gxt.ui.client.widget.Dialog.OK))
						{	
							String subDir = be.getValue();
							final String path;
							if (parentDir.endsWith(UIContext.FILE_SEPATATOR))
								path = parentDir + subDir; 
							else
								path = parentDir + UIContext.FILE_SEPATATOR + subDir;
							if(parentDir == null || parentDir.length() == 0 || subDir == null || subDir.length() == 0)
								return;
							
							service.createFolder(serviceInfo, parentDir, subDir, host, user, password,scriptUUID, createFolderHandler(path));
						}
					}
				});
			}
		});
			
		
		buttonContainer.add(createImage);		
		buttonContainer.hide();
		topContainer.add(buttonContainer, tableData);
		container.add(topContainer);

		treeContainer = new LayoutContainer();
		treeContainer.setLayout(new CenterLayout());
		treeContainer.setWidth(370);
		treeContainer.setHeight(210);
		container.add(treeContainer, rd);

		treeStatusLabel = new LabelField();
		treeStatusLabel.addStyleName("browseLoading");
		treeStatusLabel.setText(UIContext.Constants.restoreLoading());
		treeStatusLabel.setWidth(190);
		treeContainer.add(treeStatusLabel);

		label = new LabelField();
		if(!showFiles)
			label.setText(UIContext.Constants.browseFolderName());
		else
			label.setText(UIContext.Constants.browseFileName());
		container.add(label, rd);

		folderField = new TextField<String>();
		folderField.ensureDebugId("6FDFA845-BBAB-46a8-8CC4-1A4CF852E8E1");
		folderField.setValue(this.getInputFolder());
		folderField.setWidth(370);
		folderField.disable();
		container.add(folderField, rd);
		
		notificationSet = new FieldSet();
		notificationSet.ensureDebugId("3C490EA4-3A30-4621-BA55-D127C049D9E5");
		notificationSet.setHeading(UIContext.Messages.backupSettingsNodifications(1));
		notificationSet.setCollapsible(true);
		TableLayout warningLayout = new TableLayout();
		warningLayout.setWidth("100%");
		warningLayout.setCellSpacing(1);
		warningLayout.setColumns(2);
		notificationSet.setLayout(warningLayout);
		notificationSet.setVisible(false);
		TableData data = new TableData();
		data.setWidth("100%");
		container.add(notificationSet,data);
	
		
		createTree();
		
		this.getButtonById(Dialog.CANCEL).setWidth(80);
		this.getButtonById(Dialog.CANCEL).setStyleAttribute("margin-left", "10px");
		this.getButtonById(Dialog.CANCEL).ensureDebugId(cancelID);
		this.getButtonById(Dialog.CANCEL).addSelectionListener(
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						lastClicked = Dialog.CANCEL;
						thisWindow.hide();
					}
				});
		
		this.getButtonById(Dialog.OK).setWidth(80);
		this.getButtonById(Dialog.OK).ensureDebugId(okID);
		this.getButtonById(Dialog.OK).addSelectionListener(
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						lastClicked = Dialog.OK;
						thisWindow.hide();
					}
				});
		
		this.add(container);
		this.setSize(400, 355);
	}

	private AsyncCallback<Void> createFolderHandler(final String path){
		return new AsyncCallback<Void>(){

			@Override
			public void onFailure(Throwable caught) {
				Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,UIContext.Constants.browseFailedToCreateFolder());
			}

			@Override
			public void onSuccess(Void result) {
				thisWindow.folderField.setValue(path);
				if (thisWindow.tree != null && thisWindow.tree.getSelectionModel().getSelectedItem() != null)
				{
					FileModel file = thisWindow.tree.getSelectionModel().getSelectedItem();
					store.removeAll(file);
					file.setCreateFolder(path);
					thisWindow.loader.loadChildren(file);	
					
				}
				else
				{
					if (inputFolder.compareTo(parentDir) == 0)
					{
						if (store!=null)
							store.removeAll();
						FileModel fileModel = new FileModel();
						fileModel.setName("");
						fileModel.setPath(inputFolder);
						if(loader != null)
						  loader.load(fileModel);
					}
				}
			}
			
		};
	}
	public String getLastClicked() {
		return lastClicked;
	}

	public String getDestination() {
		if (folderField != null) {
			return folderField.getValue();
		}
		return "";
	}

	public boolean isListRoots() {
		return inputFolder == null || inputFolder.trim().length() == 0;
	}

	public void createTree() {
		try {
			if (user == null)
				user = "";

			if (password == null)
				password = "";

//			if(inputFolder==null){
//				List<FileModel> rootList=new ArrayList<FileModel>();
//				FileModel root = new FileModel();
//				root.setName(UIContext.FILE_SEPATATOR);
//				root.setPath(UIContext.FILE_SEPATATOR);
//				root.setType(CatalogModelType.Folder);
//				rootList.add(root);
//				callback.onSuccess(rootList);
//			}else if(showFiles){
//				int iIndex = inputFolder.lastIndexOf(UIContext.FILE_SEPATATOR);
//				inputFolder = actualPath.substring(0, iIndex);
//				fileName = actualPath.substring(iIndex+1,actualPath.length());
//			}
			service.getFileItems(serviceInfo, inputFolder, this.host, this.user, this.password, showFiles,scriptUUID, callback);
		} catch (Exception e) {
			treeContainer.setLayoutOnChange(true);
//			treeStatusLabel.setText(UIContext.Constants.restoreUnableToFindVolumes());
			treeStatusLabel.setText(UIContext.Constants.browseFailed());
		}
	}
	
	private AsyncCallback<List<FileModel>> callback = new AsyncCallback<List<FileModel>>() {

		@Override
		public void onFailure(Throwable caught) {
			buttonContainer.show();
			treeContainer.setLayoutOnChange(true);
			treeStatusLabel.setText(UIContext.Constants.browseFailed());
		}

		@Override
		public void onSuccess(List<FileModel> result) {
			
			if (result == null || result.size() == 0) {
				// No volumes found
				treeContainer.setLayoutOnChange(true);
				treeStatusLabel.setText(UIContext.Constants.browseEmptyFolder());
			} else {
				treeStatusLabel.setVisible(false);
				// Create the Loader
				createTreeLoader();
				// Create the treestore
				store = new TreeStore<FileModel>(loader);
				for(FileModel model: result){
					store.add(model, false);
				}
				createNewTree();
				treeContainer.setLayoutOnChange(true);
				treeContainer.setLayout(new CenterLayout());
				treeContainer.add(tree);
				isTreeCreated = true;
			}
			//After dir tree is created, show topContainer
			buttonContainer.show();
		}

		private void createNewTree() {
			tree = new BaseTreePanel(store);
			
			tree.setBorders(true);
			tree.setStateful(true);
			tree.setDisplayProperty("name");
			tree.setWidth(370);
			tree.setHeight(210);

			// selection listener
			tree.getSelectionModel().addSelectionChangedListener(
					new SelectionChangedListener<FileModel>() {
						@Override
						public void selectionChanged(
								SelectionChangedEvent<FileModel> se) {
							if(se.getSelectedItem() != null)
							{
								String dest = se.getSelectedItem().getPath();
								if((dest!=null)&&dest.isEmpty())
									return;
								thisWindow.folderField.setValue(dest);
							}
						}

					});
		}

		private void createTreeLoader() {
			loader = new BaseTreeLoader<FileModel>(proxy) {
				@Override
				public boolean hasChildren(FileModel parent) {
					if(parent != null)
					{
						Integer type = parent.getType();
						if (type != null && type == CatalogModelType.File) {
							return false;
						} else {
							return true;
						}
					}
					else
					{
						return false;
					}
				}
			};
			loader.addLoadListener(new LoadListener() {

				@Override
				public void loaderLoad(LoadEvent le) {
					//super.loaderLoad(le);
					Object config = le.getConfig();
					if(config instanceof FileModel) {
						FileModel fileModel = (FileModel)config;
						String createFolderPath = fileModel.getCreateFolderPath();
						if((createFolderPath!=null)&&(createFolderPath.length()>0)) {
							
							tree.setExpanded(fileModel, true);
							List<FileModel> listFileModels = (List<FileModel>)le.getData();
							for (FileModel childFileModel : listFileModels) {
								String childDir = childFileModel.getPath()+UIContext.FILE_SEPATATOR+childFileModel.getName();
								if(childDir.equalsIgnoreCase(createFolderPath)) {
									
									fileModel.setCreateFolder("");
									TreePanelSelectionModel<FileModel> selectionModel = tree.getSelectionModel();
									//selectionModel.getSelectedItems().clear();
									//selectionModel.getSelectedItems().add(childFileModel);
									selectionModel.select(childFileModel, false);
									tree.setSelectionModel(selectionModel);
									break;
								}
							}

						}
					}
				}

				@Override
				public void loaderLoadException(LoadEvent le) {
					Object config = le.getConfig();
					
					if (config instanceof FileModel) {
						Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,le.exception.getMessage());
					} 
//					boolean showErrDialog = true;
//						if (showErrDialog && le.exception != null) {
//							if (config instanceof FileModel){
//								tree.refresh((FileModel) config);
//							}
//							new AsyncCallback().onFailure(le.exception);
//						}

				}
			});
		}
	};
	
	public void updateNotificationSet(String message)
	{
		removeNotificationSet();		
		addWaringIcon();	
		notificationSet.add(new LabelField(message));
		notificationSet.setVisible(true);
		notificationSet.expand();	
		notificationSet.layout(true);							

	}
	
	
	public void removeNotificationSet()
	{
		notificationSet.removeAll();
		notificationSet.setVisible(false);	
	}
	
	private void addWaringIcon() {
		Image warningImage = getWaringIcon();
		TableData tableData = new TableData();
		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		notificationSet.add(warningImage, tableData);
	}
	
	private Image getWaringIcon() {
		Image warningImage = new Image(UIContext.IconBundle.warning());
		return warningImage;
	}
	
	private String getParentFolder(){
		if (inputFolder==null || inputFolder.length() == 0)
			return null;
		
		if (inputFolder.charAt(inputFolder.length()-1) == UIContext.FILE_SEPATATOR.charAt(0))
			inputFolder = inputFolder.substring(0,inputFolder.length()-1);
		
		int index = inputFolder.lastIndexOf(UIContext.FILE_SEPATATOR);
		if (index >= 0) {
			return inputFolder.substring(0, index);
		} else
			return null;
	}
	
	public void setDebugID(String cancelID, String okID) {
		this.cancelID = cancelID;
		this.okID = okID;
	}

	public String getScriptUUID() {
		return scriptUUID;
	}

	public void setScriptUUID(String scriptUUID) {
		this.scriptUUID = scriptUUID;
	}

}


