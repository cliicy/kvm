package com.ca.arcserve.linuximaging.ui.client.restore;


import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreModel;
import com.ca.arcserve.linuximaging.ui.client.model.RestoreType;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public abstract class TargetMachineSettings extends LayoutContainer {

	protected RestoreWindow restoreWindow;
	public final static int ROW_CELL_SPACE=5;
	public final static int MAX_FIELD_WIDTH= 300;
	public final static int TABLE_HIGHT = 130;
	public static int MIN_BUTTON_WIDTH = 60;
	public static int HEIGHT = 480;
	public static int WIDTH = 670;
	protected HomepageServiceAsync service = GWT.create(HomepageService.class);

	public TargetMachineSettings(RestoreWindow restoreWindow){
		this.restoreWindow=restoreWindow;
		this.setHeight(HEIGHT);
		this.setWidth(WIDTH);
		this.setScrollMode(Scroll.AUTOY);
		TableLayout layout = new TableLayout();
		layout.setWidth("97%");
		layout.setColumns(1);
		layout.setCellPadding(3);
		layout.setCellSpacing(3);
		setLayout(layout);
		String selectTargetMachineMessage = UIContext.Constants.selectTargetMachine();
		if(restoreWindow.getRestoreType() == RestoreType.BMR){
			selectTargetMachineMessage = UIContext.Constants.selectTargetMachine();
		}else if (restoreWindow.getRestoreType() == RestoreType.FILE){
			selectTargetMachineMessage = UIContext.Constants.selectTargetMachineForFileRestore();
		}else if (restoreWindow.getRestoreType() == RestoreType.SHARE_RECOVERY_POINT){
			selectTargetMachineMessage = UIContext.Constants.selectLoctionForShareRecoveryPoint();
		}
		LabelField head =new LabelField(selectTargetMachineMessage);
		head.setStyleAttribute("font-weight", "bold");
		add(head);
		
		LayoutContainer targetTable=defineTargetTable();
		add(targetTable);
		
	}
	protected abstract LayoutContainer defineTargetTable();

	public abstract boolean validate();
	public abstract void save();
	public abstract void refreshData();
	public abstract void refreshPart();
	public void refreshWhenShowing(){};
	public void refreshNetworkSettings(RestoreModel model){};
}



