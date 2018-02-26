package com.ca.arcserve.linuximaging.ui.client.toolbar;

import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.aerp.EntitlementRegistrationWindow;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.AboutWindow;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.homepage.Homepagetree;
import com.ca.arcserve.linuximaging.ui.client.license.LicenseManagementWindow;
import com.ca.arcserve.linuximaging.ui.client.login.LoginService;
import com.ca.arcserve.linuximaging.ui.client.login.LoginServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.RegistrationModel;
import com.ca.arcserve.linuximaging.ui.client.model.ServerInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ToolBarPanel extends LayoutContainer {

	final LoginServiceAsync loginService = GWT.create(LoginService.class);
	public static final int HEIGHT = 85;
	public static final int HEAD_HEIGHT = 25;
	private ToolBar toolBar;
	public D2DServerButtonGroup d2dServer;
	public NodeButtonGroup node;
	public BackupStorageButtonGroup storage;
	public WizardButtonGroup wizard;
	public JobButtonGroup job;
	public ScheduleButtonGroup schedule;
	public Homepagetree nodeTree;
	public HomepageTab tabPanel;
	public ToolButtonGroup tool;
	private LayoutContainer messages = null;
	private LayoutContainer messagesLabel = null;

	private LabelField messageHtml = new LabelField(
			UIContext.Constants.noticeRegisterMessage());

	public ToolBarPanel() {
		// this.setLayout(new FlowLayout());

		TableLayout tableLayout = new TableLayout(1);
		tableLayout.setWidth("100%");
		this.setLayout(tableLayout);
		// this.set.setAlign(HorizontalAlignment.CENTER);

		LayoutContainer head = getHeadPanel();
		this.add(head);

		toolBar = new ToolBar();

		// TableData data = new TableData();
		// data.setRowspan(2);

		d2dServer = new D2DServerButtonGroup(2, HEIGHT);
		d2dServer.setToolBar(this);
		toolBar.add(d2dServer);

		node = new NodeButtonGroup(2, HEIGHT);
		node.setToolBar(this);
		toolBar.add(node);

		wizard = new WizardButtonGroup(3, HEIGHT);
		wizard.setToolBar(this);
		toolBar.add(wizard);

		job = new JobButtonGroup(2, HEIGHT);
		job.setToolBar(this);
		toolBar.add(job);

		schedule = new ScheduleButtonGroup(1, HEIGHT);
		schedule.setToolBar(this);
		toolBar.add(schedule);
		schedule.hide();

		storage = new BackupStorageButtonGroup(2, HEIGHT);
		storage.setToolBar(this);
		toolBar.add(storage);

		tool = new ToolButtonGroup(2, HEIGHT);
		tool.setToolBar(this);
		toolBar.add(tool);

		this.add(toolBar);
		EntitlementRegistrationWindow.toolBarPanel = this;

		// this.add(toolBar, left);

		// VerticalPanel logoPanel = new VerticalPanel();
		// logoPanel.setStyleName("x-toolbar");
		// // logoPanel.setWidth(200);
		// logoPanel.setHeight(HEIGHT+5);
		// logoPanel.setHorizontalAlign(HorizontalAlignment.RIGHT);
		// logoPanel.setVerticalAlign(VerticalAlignment.TOP);
		//
		//
		// LabelField lbl = new LabelField();
		// // String value="Linux D2D Alpha2 1276.1";
		// // lbl.setText(new
		// Html("<pre style=\"font-family: Tahoma,Arial;font-size: 15px; font-weight: bold; color: #15428B;\">"+value+"</pre>").getHtml());
		// lbl.setText(new
		// Html("<pre style=\"font-family: Tahoma,Arial;font-size: 12px; color: #15428B;\">"+UIContext.version+"</pre>").getHtml());
		// logoPanel.add(lbl);
		// logoPanel.add(getHelpLabel());
		// this.add(logoPanel, right);
	}

	private LayoutContainer getHeadPanel() {
		LayoutContainer head = new LayoutContainer();
		TableLayout layout = new TableLayout(3);
		layout.setCellSpacing(1);
		layout.setWidth("100%");
		head.setLayout(layout);

		head.setStyleName("branding-contents");
		head.setHeight(HEAD_HEIGHT);

		TableData left = new TableData();
		left.setHorizontalAlign(HorizontalAlignment.LEFT);
		left.setWidth("40%");
		TableData right = new TableData();
		right.setWidth("15%");
		right.setHorizontalAlign(HorizontalAlignment.RIGHT);
		// TableData tdColspan = new TableData();
		// tdColspan.setColspan(3);
		// tdColspan.setHorizontalAlign(HorizontalAlignment.LEFT);

		HorizontalPanel logo = getLogoPanel();
		head.add(logo, left);
		LabelField hide = new LabelField();
		hide.setWidth("45%");
		head.add(hide);
		head.add(getFeedsAndHelpLabel(), right);
		return head;
	}

	private HorizontalPanel getLogoPanel() {
		HorizontalPanel logo = new HorizontalPanel();
		logo.add(new Image(UIContext.IconBundle.ca_logo()));
		LabelField lbl = new LabelField();
		lbl.setText(new Html(
				"<pre style=\"font-family: Verdana;font-size: 13px; color: #38304C;font-weight: bold;padding-left: 8px;\">"
						+ UIContext.version + "</pre>").getHtml());
		lbl.setHeight(25);
		TableData td = new TableData();
		// td.setVerticalAlign(VerticalAlignment.MIDDLE);
		// logo.add(lbl, td);
		return logo;
	}

	private LayoutContainer getFeedsAndHelpLabel() {
		HorizontalPanel panel = new HorizontalPanel();
		// LayoutContainer
		// feeds=getLinkPanel(UIContext.IconHundle.feedback().createImage(),UIContext.Constants.feedback(),UIContext.feedsLink);
		LayoutContainer logout = getLogoutPanel(
				AbstractImagePrototype.create(
						UIContext.IconBundle.homepage_logout_icon())
						.createImage(), UIContext.Constants.logout());
		// LayoutContainer
		// help=getLinkPanel(UIContext.IconHundle.homepage_help_icon().createImage(),UIContext.Constants.help(),UIContext.helpLink+HelpLinkItem.HOMEPAGE);
		LayoutContainer help = getHelpPanel(UIContext.IconHundle
				.homepage_help_icon().createImage(),
				UIContext.Constants.help(), UIContext.helpLink
						+ HelpLinkItem.HOMEPAGE);

		// panel.add(feeds);
		messages = getMessagesContainer();
		messagesLabel = getLabel();
		messagesLabel.hide();
		if (UIContext.versionInfo.getManagedServer() == null) {
			panel.add(messages);
			panel.add(messagesLabel);
		} else {
			panel.add(getManagedByPanel());
		}
		panel.add(logout);
		panel.add(getLabel());
		panel.add(help);
		return panel;
	}

	private LayoutContainer getMessagesContainer() {

		LayoutContainer container = new LayoutContainer();

		HorizontalPanel messagesPanel = new HorizontalPanel();
		Image warningImage = new Image(UIContext.IconBundle.information());
		messagesPanel.add(warningImage);

		final Label messagesLink = new Label();
		messagesLink.setText(UIContext.Messages.messages(1));
		messagesLink.setStyleName("homepage_header_link_label_linux");

		TableLayout tableLayout = new TableLayout(1);
		tableLayout.setCellPadding(4);
		container.setLayout(tableLayout);

		final Menu menu = createMessagesMenu();

		ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				menu.showAt(
						messagesLink.getAbsoluteLeft() - 20,
						messagesLink.getAbsoluteTop()
								+ messagesLink.getOffsetHeight());
			}
		};

		addClickHandler(messagesLink, null, handler);
		messagesPanel.add(messagesLink);
		container.add(messagesPanel);
		container.hide();
		return container;
	}

	private Menu createMessagesMenu() {
		Menu menu = new Menu();
		MenuItem register = new MenuItem();
		register.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				EntitlementRegistrationWindow window = new EntitlementRegistrationWindow(
						tabPanel.currentServer);
				window.ensureDebugId("38a835a2-89f0-4d47-828b-093c8df81665");
				window.setModal(true);
				window.show();

			}
		});
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(messageHtml);
		register.setWidget(horizontalPanel);
		menu.add(register);
		return menu;
	}

	private LayoutContainer getHelpPanel(Image icon, String text,
			final String url) {
		/*
		 * LayoutContainer container = new LayoutContainer();
		 * 
		 * TableLayout tableLayout = new TableLayout(1);
		 * tableLayout.setCellPadding(4); container.setLayout(tableLayout);
		 * //container.add(icon);
		 * 
		 * container.add(createHelpLink());
		 */

		return createHelpLink();
	}

	private LayoutContainer getLabel() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(1);
		tableLayout.setCellPadding(5);
		container.setLayout(tableLayout);
		Label label = new Label("|");
		label.setStyleName("label_white");
		container.add(label);
		return container;
	}

	private LayoutContainer createHelpLink() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(1);
		tableLayout.setCellPadding(4);
		container.setLayout(tableLayout);
		final Menu menu = createHelpMenu();
		final Label link = new Label(UIContext.Constants.help());

		link.setStyleName("homepage_header_link_label_linux");
		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				menu.showAt(link.getAbsoluteLeft(), link.getAbsoluteTop()
						+ link.getOffsetHeight());
			}
		};
		addClickHandler(link, null, handler);
		container.add(link);
		// container.add(arrowImage);
		return container;
	}

	private Menu createHelpMenu() {
		Menu menu = new Menu();

		MenuItem knowledgeCenter = new MenuItem(
				UIContext.Constants.knowledgeCenter());
		knowledgeCenter
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						Utils.showURL(UIContext.helpLink
								+ HelpLinkItem.KNOWNLEDGE_CENTER);
					}
				});
		menu.add(knowledgeCenter);

		MenuItem caSupportItem = new MenuItem(
				UIContext.Constants.onlineSupport());
		caSupportItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				Utils.showURL(UIContext.helpLink + HelpLinkItem.CA_SUPPORT);
			}
		});
		menu.add(caSupportItem);

		MenuItem solutionsGuideItem = new MenuItem(
				UIContext.Constants.solutionsGuide());
		solutionsGuideItem
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						Utils.showURL(UIContext.helpLink
								+ HelpLinkItem.SOLUTIONS_GUIDE);
					}
				});
		menu.add(solutionsGuideItem);

		MenuItem userGuide = new MenuItem(UIContext.Constants.agentUserGuide());
		userGuide.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				Utils.showURL(UIContext.helpLink + HelpLinkItem.HOMEPAGE);
			}
		});
		menu.add(userGuide);

		MenuItem liveChartItem = new MenuItem(UIContext.Constants.askSupport());
		liveChartItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				Utils.showURL(UIContext.helpLink + HelpLinkItem.LIVE_CHART);
			}
		});
		menu.add(liveChartItem);

		MenuItem feedbackupItem = new MenuItem(
				UIContext.Constants.sendFeedback());
		feedbackupItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				Utils.showURL(UIContext.helpLink + HelpLinkItem.FEEDBACK);
			}
		});
		menu.add(feedbackupItem);

		// MenuItem supportAndCommunityItem = new
		// MenuItem(UIContext.Constants.supportAndCommunity());

		// Menu supportSubMenu = new Menu();

		MenuItem videoItem = new MenuItem(UIContext.Constants.video());
		videoItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				Utils.showURL(UIContext.helpLink + HelpLinkItem.VIDEO);
			}
		});
		menu.add(videoItem);

		if (!UIContext.versionInfo.isLiveCD()) {
			if (!UIContext.isRestoreMode) {
				menu.add(new SeparatorMenuItem());
				MenuItem license = new MenuItem(
						UIContext.Constants.manageLicense());
				license.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						LicenseManagementWindow window = new LicenseManagementWindow();
						window.ensureDebugId("99b5f641-40d4-4dd4-8c81-c57c7f37d5c3");
						window.setModal(true);
						window.show();
					}
				});
				menu.add(license);
			}
		}

		if (UIContext.versionInfo.getManagedServer() == null) {
			menu.add(new SeparatorMenuItem());
			MenuItem registerItem = new MenuItem();
			registerItem.ensureDebugId("ea7dbba9-cc2d-4d52-8a60-a15fc75741c0");
			registerItem.setText(UIContext.Constants.d2dRegistration());
			registerItem
					.addSelectionListener(new SelectionListener<MenuEvent>() {
						public void componentSelected(MenuEvent ce) {
							EntitlementRegistrationWindow window = new EntitlementRegistrationWindow(
									tabPanel.currentServer);
							window.ensureDebugId("38a835a2-89f0-4d47-828b-093c8df81665");
							window.setModal(true);
							window.show();
						}
					});
			menu.add(registerItem);
		}

		menu.add(new SeparatorMenuItem());
		MenuItem aboutItem = new MenuItem();
		aboutItem.ensureDebugId("ea7dbba9-cc2d-4d52-8a60-a15fc75741c0");
		aboutItem.setText(UIContext.Constants.about());
		// aboutItem.setIcon(UIContext.IconBundle.homepage_help_menu_about());
		aboutItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				AboutWindow window = new AboutWindow();
				window.setModal(true);
				window.show();
			}
		});

		menu.add(aboutItem);
		return menu;
	}

	// private LayoutContainer getFeedsLabel() {
	// return
	// getLinkPanel(UIContext.IconHundle.feedIcon().createImage(),UIContext.Constants.feeds(),UIContext.feedsLink);
	// }
	// private LayoutContainer getHelpLabel() {
	// return
	// getLinkPanel(UIContext.IconHundle.homepage_help_icon().createImage(),UIContext.Constants.help(),UIContext.helpLink+HelpLinkItem.HOMEPAGE);
	// }
	private LayoutContainer getLinkPanel(Image icon, String text,
			final String url) {
		LayoutContainer container = new LayoutContainer();

		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setCellPadding(4);
		container.setLayout(tableLayout);
		container.add(icon);
		Label link = new Label(text);
		link.setStyleName("homepage_header_allfeeds_link_label");
		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Utils.showURL(url);
			}
		};
		addClickHandler(link, icon, handler);
		container.add(link);

		return container;
	}

	private LayoutContainer getLogoutPanel(Image icon, String text) {
		LayoutContainer container = new LayoutContainer();

		TableLayout tableLayout = new TableLayout(1);
		tableLayout.setCellPadding(4);
		container.setLayout(tableLayout);
		// container.add(icon);
		Label link = new Label(text);
		link.setStyleName("homepage_header_link_label_linux");
		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked()
								.getItemId()
								.equals(com.extjs.gxt.ui.client.widget.Dialog.YES)) {
							callLogout();
						}
					}
				};

				MessageBox mb = new MessageBox();
				mb.setIcon(MessageBox.WARNING);
				mb.setButtons(MessageBox.YESNO);
				mb.setTitle(UIContext.productName);
				mb.setMessage(UIContext.Constants.logoutConfirmMsg());
				mb.setMinWidth(350);
				mb.addCallback(l);
				Utils.setMessageBoxDebugId(mb);
				mb.show();
			}
		};
		addClickHandler(link, icon, handler);
		container.add(link);

		return container;
	}

	private LayoutContainer getManagedByPanel() {
		LayoutContainer container = new LayoutContainer();

		TableLayout tableLayout = new TableLayout(1);
		tableLayout.setColumns(2);
		tableLayout.setCellPadding(4);
		container.setLayout(tableLayout);
		// container.add(icon);
		Label managedBy = new Label(UIContext.Constants.managedBy());
		managedBy.setStyleName("homepage_header_label_linux");
		container.add(managedBy);
		Label link = new Label(UIContext.versionInfo.getManagedServer()
				.getServerName());
		link.setStyleName("homepage_header_link_label_linux");
		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ServerInfoModel serverInfo = UIContext.versionInfo
						.getManagedServer();
				String serverURL = serverInfo.getProtocol() + "://"
						+ serverInfo.getServerName() + ":"
						+ serverInfo.getPort();
				Window.open(serverURL, "_blank", "");
			}
		};
		addClickHandler(link, null, handler);
		container.add(link);

		return container;
	}

	private void callLogout() {
		loginService.logout(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.Location.reload();
			}

			@Override
			public void onSuccess(Void result) {
				Window.Location.reload();
			}

		});
	}

	private void addClickHandler(Label label, Image icon, ClickHandler handler) {
		if (handler == null)
			return;

		if (label != null)
			label.addClickHandler(handler);
		if (icon != null)
			icon.addClickHandler(handler);
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	public void initComponent(Homepagetree treePanel,
			HomepageTab componentJobStatus) {
		this.nodeTree = treePanel;
		this.tabPanel = componentJobStatus;
		refreshToolBarTitle();
	}

	public void setAllEnabled(boolean isEnabled) {
		if (!isEnabled) {
			node.setAllEnabled(false);
			wizard.setAllEnabled(false);
			job.setAllEnabled(false);
			storage.setAllEnabled(false);
			tool.setAllEnabled(false);
		} else {
			node.setDefaultState();
			wizard.setDefaultState();
			job.setDefaultState();
			storage.setDefaultState();
			tool.setDefaultState();
		}
	}

	public void refreshToolBarTitle() {
		loginService.getNotifications(tabPanel.currentServer,
				new BaseAsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {

					}

					@Override
					public void onSuccess(String result) {
						if ("USER_CANCELLED_REGISTRATION"
								.equalsIgnoreCase(result)) {
							messages.hide();
							messagesLabel.hide();
						} else if ("ISACTIVATED_NOTREGISTERED"
								.equalsIgnoreCase(result)) {
							messages.show();
							messagesLabel.show();
						} else if ("ISACTIVATED_INACTIVE"
								.equalsIgnoreCase(result)) {
							loginService.getRegistrationDetails(
									tabPanel.currentServer,
									new BaseAsyncCallback<RegistrationModel>() {
										@Override
										public void onFailure(Throwable caught) {

										}

										@Override
										public void onSuccess(
												RegistrationModel result) {
											if (result != null) {
												messageHtml
														.setValue(UIContext.Messages
																.getInActiveMessage(result
																		.getEmailID()));
												messages.show();
												messagesLabel.show();
											}
										}

									});

						}
					}
				});

	}
}
