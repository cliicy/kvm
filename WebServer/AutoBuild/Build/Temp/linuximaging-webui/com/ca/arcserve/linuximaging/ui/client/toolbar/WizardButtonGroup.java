package com.ca.arcserve.linuximaging.ui.client.toolbar;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.backup.wizard.AddNodesToExsitJobWindow;
import com.ca.arcserve.linuximaging.ui.client.backup.wizard.BackupSource;
import com.ca.arcserve.linuximaging.ui.client.backup.wizard.BackupWizardPanel;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.node.NodeTable;
import com.ca.arcserve.linuximaging.ui.client.model.NodeModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.StandbyType;
import com.ca.arcserve.linuximaging.ui.client.restore.RestoreWindow;
import com.ca.arcserve.linuximaging.ui.client.standby.StandbyWizardPanel;
import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;

public class WizardButtonGroup extends ButtonGroup {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	private Button backup;
	private Button recovery;
	private Button standby;
	private ToolBarPanel toolBar;

	public WizardButtonGroup(int columns, int height) {
		super(columns);
		setHeight(height);
		setHeading(UIContext.Constants.toolBar_wizard());
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		backup = new Button(UIContext.Constants.toolBar_backup());
		// backup.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.backup()));
		backup.setIcon(UIContext.IconHundle.backup());
		backup.setScale(ButtonScale.LARGE);
		backup.setIconAlign(IconAlign.TOP);
		backup.setArrowAlign(ButtonArrowAlign.RIGHT);
		backup.setMinWidth(50);
		if (UIContext.isRestoreMode) {
			backup.disable();
		}
		Menu menu = new Menu();
		MenuItem backupMi = new MenuItem(
				UIContext.Constants.toolBar_menu_backup());
		backupMi.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				BackupWizardPanel window = new BackupWizardPanel(
						toolBar.tabPanel.currentServer, false,
						UIContext.Constants.backupWizard());
				window.setHomepageTab(toolBar.tabPanel);
				window.setModal(true);
				window.show();
			}
		});
		menu.add(backupMi);

		MenuItem backupSelected = new MenuItem(
				UIContext.Constants.toolBar_backupForSelectedNode());
		backupSelected.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				if (toolBar != null) {
					NodeTable table = toolBar.tabPanel.nodeTable;
					;
					if (table != null) {
						List<NodeModel> serverList = table.getSelectedItems();
						// ServiceInfoModel backupServer =
						// table.getBackupServer();
						if (serverList != null && serverList.size() > 0) {
							BackupWizardPanel window = new BackupWizardPanel(
									toolBar.tabPanel.currentServer, serverList,
									false, UIContext.Constants.backupWizard());
							window.setHomepageTab(toolBar.tabPanel);
							window.setModal(true);
							window.show();
							return;
						}
					}
				}

				Utils.showMessage(UIContext.Constants.productName(),
						MessageBox.ERROR, UIContext.Messages.noSelectedNodes());
			}
		});
		menu.add(backupSelected);

		MenuItem addNodes = new MenuItem(
				UIContext.Constants.toolBar_addSelectedNode2ExistJob());
		addNodes.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				List<NodeModel> serverList = toolBar.tabPanel.nodeTable
						.getSelectedItems();
				if (serverList == null || serverList.size() == 0) {
					Utils.showMessage(UIContext.Constants.productName(),
							MessageBox.ERROR,
							UIContext.Messages.noSelectedNodes());
					return;
				}
				service.getNodeProtectedState(toolBar.tabPanel.currentServer,
						serverList, new BaseAsyncCallback<List<NodeModel>>() {

							@Override
							public void onFailure(Throwable caught) {
								// Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,caught.getMessage());
								super.onFailure(caught);
							}

							@Override
							public void onSuccess(List<NodeModel> result) {
								List<NodeModel> protectedNodes = new ArrayList<NodeModel>();
								for (NodeModel node : result) {
									if (node.getProtected()) {
										protectedNodes.add(node);
									}
								}
								if (protectedNodes.size() > 0) {
									BackupSource
											.showIsProtectedWarning(protectedNodes);
								} else {
									AddNodesToExsitJobWindow window = new AddNodesToExsitJobWindow(
											toolBar, result);
									window.refresh();
									window.show();
								}
							}
						});

			}
		});
		menu.add(addNodes);

		backup.setMenu(menu);

		recovery = new Button(UIContext.Constants.toolBar_restore());
		// recovery.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.restore()));
		recovery.setIcon(UIContext.IconHundle.restore());
		recovery.setScale(ButtonScale.LARGE);
		recovery.setIconAlign(IconAlign.TOP);
		recovery.setArrowAlign(ButtonArrowAlign.RIGHT);
		recovery.setMinWidth(50);
		/*
		 * recovery.addSelectionListener(new SelectionListener<ButtonEvent>(){
		 * 
		 * @Override public void componentSelected(ButtonEvent ce) {
		 * RestoreWindow window = new RestoreWindow(toolBar); window.show(); }
		 * 
		 * });
		 */
		Menu recoveryMenu = new Menu();
		MenuItem recoveryPhysicalMachine = new MenuItem(
				UIContext.Constants.bmr());
		recoveryPhysicalMachine
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						RestoreWindow window = new RestoreWindow(toolBar,
								RestoreType.BMR, null, null, null);
						window.show();
					}
				});
		recoveryMenu.add(recoveryPhysicalMachine);

		MenuItem migration = new MenuItem(UIContext.Constants.migrationBmr());
		migration.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				RestoreWindow window = new RestoreWindow(toolBar,
						RestoreType.MIGRATION_BMR, null, null, null);
				window.show();
			}
		});
		recoveryMenu.add(migration);

		MenuItem restoreFile = new MenuItem(
				UIContext.Constants.restoreType_Restore_File());
		restoreFile.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				RestoreWindow window = new RestoreWindow(toolBar,
						RestoreType.FILE, null, null, null);
				window.show();
			}
		});
		recoveryMenu.add(restoreFile);

		MenuItem shareRecoveryPoint = new MenuItem(
				UIContext.Constants.shareRecoveryPoint());
		shareRecoveryPoint
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						RestoreWindow window = new RestoreWindow(toolBar,
								RestoreType.SHARE_RECOVERY_POINT, null, null,
								null);
						window.show();
					}
				});
		recoveryMenu.add(shareRecoveryPoint);

		if (UIContext.versionInfo.isEnableVM()) {
			MenuItem recoveryVirtualMachine = new MenuItem(
					UIContext.Constants.restoreType_Recovery_VM());
			recoveryVirtualMachine
					.addSelectionListener(new SelectionListener<MenuEvent>() {

						@Override
						public void componentSelected(MenuEvent ce) {
							RestoreWindow window = new RestoreWindow(toolBar,
									RestoreType.VM, null, null, null);
							window.show();
						}
					});
			recoveryMenu.add(recoveryVirtualMachine);
		}

		recovery.setMenu(recoveryMenu);

		standby = new Button(UIContext.Constants.toolBar_standby());
		// recovery.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.restore()));
		standby.setIcon(UIContext.IconHundle.restore());
		standby.setScale(ButtonScale.LARGE);
		standby.setIconAlign(IconAlign.TOP);
		standby.setArrowAlign(ButtonArrowAlign.RIGHT);
		standby.setMinWidth(50);
		Menu standbyMenu = new Menu();
		MenuItem standByPhysicalMachine = new MenuItem(
				UIContext.Constants.toolBar_standby_physical());
		standByPhysicalMachine
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						NodeTable table = toolBar.tabPanel.nodeTable;
						;
						if (table != null) {
							List<NodeModel> serverList = table
									.getSelectedItems();
							// ServiceInfoModel backupServer =
							// table.getBackupServer();
							if (serverList != null && serverList.size() > 0) {
								StandbyWizardPanel window = new StandbyWizardPanel(
										toolBar.tabPanel.currentServer,
										serverList, false, UIContext.Constants
												.standbyPhysical(),
										StandbyType.PHYSICAL);
								window.setHomepageTab(toolBar.tabPanel);
								window.show();
							}
						}
					}
				});
		standbyMenu.add(standByPhysicalMachine);

		MenuItem standByVirtualMachine = new MenuItem(
				UIContext.Constants.toolBar_standby_virtual());
		standByVirtualMachine
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						NodeTable table = toolBar.tabPanel.nodeTable;
						;
						if (table != null) {
							List<NodeModel> serverList = table
									.getSelectedItems();
							// ServiceInfoModel backupServer =
							// table.getBackupServer();
							if (serverList != null && serverList.size() > 0) {
								StandbyWizardPanel window = new StandbyWizardPanel(
										toolBar.tabPanel.currentServer,
										serverList, false, UIContext.Constants
												.standbyVirtual(),
										StandbyType.VIRTUAL);
								window.setHomepageTab(toolBar.tabPanel);
								window.show();
							}
						}
					}
				});
		// standbyMenu.add(standByVirtualMachine);
		standby.setMenu(standbyMenu);

		add(backup);
		add(recovery);
		// add(standby);
	}

	public void setToolBar(ToolBarPanel toolBarPanel) {
		this.toolBar = toolBarPanel;

	}

	public void setDefaultState() {
		if (!UIContext.isRestoreMode) {
			backup.setEnabled(true);
		}
		recovery.setEnabled(true);

	}

	public void setAllEnabled(boolean isEnabled) {
		backup.setEnabled(isEnabled);
		recovery.setEnabled(isEnabled);
	}
}
