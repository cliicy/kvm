package com.ca.arcserve.linuximaging.ui.client.restore.browse;

import java.util.List;
import com.ca.arcserve.linuximaging.ui.client.HelpLinkItem;
import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.ca.arcserve.linuximaging.ui.client.model.RecoveryPointModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.ca.arcserve.linuximaging.ui.client.model.ServiceInfoModel;
import com.ca.arcserve.linuximaging.ui.client.restore.RecoveryPointSettings;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

public class BrowseRecoveryPointWindow extends Window {
	private HomepageServiceAsync service = GWT.create(HomepageService.class);
	public static final int WINDOW_WIDTH = 920;
	public static final int WINDOW_HEIGHT = 600;

	private Window window;
	private BrowseRecoveryPointPanel browseRecoveryPointPanel;
	private RecoveryPointSettings recoveryPointSettings;
	private LayoutContainer warningContainer = new LayoutContainer();

	public BrowseRecoveryPointWindow(RecoveryPointSettings settings,
			final BrowseContext browseContext) {
		this.window = this;
		this.recoveryPointSettings = settings;
		this.setHeading(UIContext.Constants.restoreBrowse()
				+ "-"
				+ browseContext.getMachineName()
				+ "-"
				+ Utils.getNormalRpName(browseContext.getRecoveryPoint()
						.getName()));
		this.setResizable(false);
		this.setClosable(true);
		if (browseContext.getRestoreType() == RestoreType.SHARE_RECOVERY_POINT) {
			this.setHeight(WINDOW_HEIGHT - 102);
		} else {
			this.setHeight(WINDOW_HEIGHT);
		}
		this.setWidth(WINDOW_WIDTH);
		this.setLayout(new FitLayout());

		Button okBtn = new Button(UIContext.Constants.OK());
		okBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				recoveryPointSettings.addSelectedNode(getSelectedNodes());
				window.hide();
			}
		});
		this.getButtonBar().add(addWarningContainer());
		this.addButton(okBtn);

		Button cancelBtn = new Button(UIContext.Constants.cancel());
		cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		});
		this.addButton(cancelBtn);
		browseRecoveryPointPanel = new BrowseRecoveryPointPanel(browseContext);
		browseRecoveryPointPanel.setWindow(this);
		this.add(browseRecoveryPointPanel);
		warningContainer.hide();
		this.addWindowListener(new WindowListener() {
			public void windowHide(WindowEvent we) {
				browseContext.setClosed(true);
				removeMountPoint(browseContext.getServer(),
						browseContext.getMachine(),
						browseContext.getRecoveryPoint());
			}
		});
	}

	public void setSelectedNode(List<GridTreeNode> selectedList) {
		browseRecoveryPointPanel.addSelectedNode(selectedList);
	}

	public void removeMountPoint(ServiceInfoModel server, String machine,
			RecoveryPointModel point) {
		if (server == null || machine == null || point == null) {
			return;
		}
		service.removeMountPoint(server, machine, point,
				new BaseAsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(Void result) {
					}

				});
	}

	public List<GridTreeNode> getSelectedNodes() {
		return browseRecoveryPointPanel.getSelectedNodes();
	}

	private LayoutContainer addWarningContainer() {
		Image warningImag = UIContext.IconHundle
				.last_result_cancel_and_incomplete().createImage();
		LabelField warningLabel = new LabelField(
				"<span style='color:#FF9900;'>"
						+ UIContext.Messages.browseWarning(UIContext.helpLink
								+ HelpLinkItem.LINUX_BROWSE_WARNING)
						+ "</span>");
		TableLayout layout = new TableLayout();
		layout.setColumns(3);
		layout.setCellSpacing(5);
		warningContainer.setLayout(layout);
		warningContainer.add(warningImag);
		warningContainer.add(warningLabel);
		warningContainer.setWidth("730PX");
		return warningContainer;
	}

	public LayoutContainer getWarningContainer() {
		return warningContainer;
	}

}
