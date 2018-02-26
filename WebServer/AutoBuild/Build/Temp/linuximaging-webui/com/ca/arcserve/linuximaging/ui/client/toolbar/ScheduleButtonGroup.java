package com.ca.arcserve.linuximaging.ui.client.toolbar;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class ScheduleButtonGroup extends ButtonGroup {
	public Button holdSchedule;
	public Button readySchedule;
	private ToolBarPanel toolBar;
	public ScheduleButtonGroup(int columns, int height) {
		super(columns);
		setHeight(height);
		setHeading(UIContext.Constants.toolBar_schedule());
		TableLayout layout = new TableLayout();
		layout.setColumns(1);
		layout.setCellPadding(1);
		layout.setCellSpacing(2);
		setLayout(layout);
	}
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		holdSchedule = new Button(UIContext.Constants.toolBar_hold()); 
//		holdSchedule.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.hold()));
		holdSchedule.setIcon(UIContext.IconHundle.hold());
		holdSchedule.disable();
		holdSchedule.addSelectionListener(new  SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				holdSchedule.disable();
				if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.JOB_STATUS)){
					toolBar.tabPanel.jobStatusTable.holdJobSchedule(false);
				}
			}
			
		});
		readySchedule = new Button(UIContext.Constants.toolBar_ready()); 
//		readySchedule.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.ready()));
		readySchedule.setIcon(UIContext.IconHundle.ready());
		readySchedule.disable();
		readySchedule.addSelectionListener(new  SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				readySchedule.disable();
				if(toolBar.tabPanel.presentTabIndex.equals(HomepageTab.JOB_STATUS)){
					toolBar.tabPanel.jobStatusTable.holdJobSchedule(true);
				}
			}
			
		});
		add(holdSchedule);
		add(readySchedule);
	}
	public void disableAll() {
		holdSchedule.disable();
		readySchedule.disable();
	}
//	public void enableAll() {
//		holdSchedule.enable();
//		readySchedule.enable();
//	}
	public void enableHold() {
		holdSchedule.enable();
		readySchedule.disable();
	}
	public void enableReady() {
		holdSchedule.disable();
		readySchedule.enable();
	}
	public void setToolBar(ToolBarPanel toolBarPanel) {
		this.toolBar=toolBarPanel;
	}
}
