package com.ca.arcserve.linuximaging.ui.client.restore;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class PagingBrowseWindow extends Window {
	public static final int WINDOW_WIDTH = 750;
	public static final int WINDOW_HEIGHT = 500;

	private Window window;
	private PagingContext pContext;
	private PagingBrowsePanel pagingPanel;
	private boolean isCancelled = false;

	public PagingBrowseWindow(PagingContext context, RestoreWindow restoreWindow) {
		pContext = context;
		this.window = this;
		String fullPath = "";
		if (pContext != null && pContext.getParent() != null) {
			fullPath = pContext.getParent().getFullPath();
			if (fullPath == null || fullPath.trim().length() == 0) {
				fullPath = pContext.getParent().getDisplayName();
			} else {
				if (!fullPath.endsWith("\\")) {
					fullPath += "\\";
				}
				fullPath += pContext.getParent().getDisplayName();
			}
		}
		this.setHeading(UIContext.Messages.browse(fullPath));

		this.setResizable(false);
		this.setClosable(true);
		this.setHeight(WINDOW_HEIGHT);
		this.setWidth(WINDOW_WIDTH);

		LayoutContainer con = new LayoutContainer();
		con.setStyleAttribute("padding", "6px");
		con.setLayout(new FitLayout());

//		pagingPanel = new PagingBrowsePanel(pContext);
		pagingPanel = new PagingBrowsePanel(pContext,restoreWindow);

		con.add(pagingPanel);

		Button okBtn = new Button(UIContext.Constants.OK());
		okBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		});

		this.addButton(okBtn);

		Button cancelBtn = new Button(UIContext.Constants.cancel());
		cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				setCancelled(true);
				window.hide();
			}
		});
		this.addButton(cancelBtn);

		this.add(con);
	}

	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

}
