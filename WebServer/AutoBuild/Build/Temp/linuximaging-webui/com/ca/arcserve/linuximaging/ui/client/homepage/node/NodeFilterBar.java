package com.ca.arcserve.linuximaging.ui.client.homepage.node;



import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.FilterListener;
import com.ca.arcserve.linuximaging.ui.client.common.TooltipSimpleComboBox;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatus;
import com.ca.arcserve.linuximaging.ui.client.model.JobStatusFilterModel;
import com.ca.arcserve.linuximaging.ui.client.model.NodeFilterModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class NodeFilterBar extends LayoutContainer {
	
	public static final int TEXT_FIELD_WIDTH = 90;
	public static final int TEXT_FIELD_WIDTH2 = 60;
	public static final int MIN_BUTTON_WIDTH = 70;
	private ToolBar filterBar;	
	
	private TextField<String> nodeName;
	//private BaseSimpleComboBox<String> cmbProtected;
	private TooltipSimpleComboBox<String> cmbLastResult;
	private TextField<String> operatingSystem;
	private Button filterBtn;
	private Button resetBtn;
	
	private NodeTable parentTable;
	private FilterListener fltLstn;
	
	public NodeFilterBar(NodeTable parent) {
		this.setLayout(new FitLayout());
		filterBar = new ToolBar();
		filterBar.setSpacing(5);
		filterBar.setBorders(true);
		
		this.parentTable = parent;
		
		//node name filter
		filterBar.add(new LabelToolItem(UIContext.Constants.nodeName()+UIContext.Constants.delimiter()));
		nodeName = new TextField<String>();
		nodeName.setWidth(TEXT_FIELD_WIDTH);
		Utils.addToolTip(nodeName, UIContext.Constants.wildcardToolTip());
		filterBar.add(nodeName);
		filterBar.add(new SeparatorToolItem());
		
		//is protected filter
		/*filterBar.add(new LabelToolItem(UIContext.Constants.nodeProtected()+UIContext.Constants.delimiter()));
		cmbProtected=new BaseSimpleComboBox<String>(); 
		cmbProtected.setWidth(TEXT_FIELD_WIDTH2);
		initComboBoxProtected();
		filterBar.add(cmbProtected);
		filterBar.add(new SeparatorToolItem());*/
		
		//last result
		filterBar.add(new LabelToolItem(UIContext.Constants.lastResult()+UIContext.Constants.delimiter()));
		cmbLastResult=new TooltipSimpleComboBox<String>(); 
		cmbLastResult.setWidth(TEXT_FIELD_WIDTH2 + 10);
		initComboBoxLastResult();
		filterBar.add(cmbLastResult);
		filterBar.add(new SeparatorToolItem());
		
		//operating system filter
		filterBar.add(new LabelToolItem(UIContext.Constants.operatingSystem()+UIContext.Constants.delimiter()));
		operatingSystem = new TextField<String>();
		operatingSystem.setWidth(TEXT_FIELD_WIDTH);
		Utils.addToolTip(operatingSystem, UIContext.Constants.wildcardToolTip());
		filterBar.add(operatingSystem);
		filterBar.add(new SeparatorToolItem());	
		
		//search button and reset button
		filterBtn = new Button(UIContext.Constants.restoreFind());
		filterBtn.setIcon(UIContext.IconHundle.search());
		filterBtn.setMinWidth(MIN_BUTTON_WIDTH);
		filterBtn.setBorders(true);
		filterBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if ( parentTable != null ) {
					parentTable.refreshTable();
					if ( isFiltered() ) {
						parentTable.getParentTabPanel().tabPanel.getSelectedItem().setIcon(UIContext.IconHundle.search());
						//parentTable.getParentTabPanel().tabPanel.getSelectedItem().setStyleAttribute("margin-top", "4px");
					} else {
						parentTable.getParentTabPanel().tabPanel.getSelectedItem().setIcon(null);
					}
				}
			}
		});
		resetBtn = new Button(UIContext.Constants.reset());
		resetBtn.setIcon(UIContext.IconHundle.reset());
		resetBtn.setMinWidth(MIN_BUTTON_WIDTH);
		resetBtn.setBorders(true);
		resetBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				resetFilter();
				if ( parentTable != null ) {
					parentTable.refreshTable();
				}
			}
		});
		filterBar.add(filterBtn);
		filterBar.add(resetBtn);
		fltLstn = new FilterListener(filterBtn);
		
		this.add(filterBar);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);		
		
		nodeName.addKeyListener(fltLstn);
		cmbLastResult.addKeyListener(fltLstn);
		operatingSystem.addKeyListener(fltLstn);		
	}
	
	public void resetFilter() {
		nodeName.setValue("");
		cmbLastResult.setSimpleValue(UIContext.Constants.all());
		operatingSystem.setValue("");
		if ( parentTable != null ) {
			parentTable.getParentTabPanel().tabPanel.getSelectedItem().setIcon(null);
		}
	}
	
	/*private void initComboBoxProtected() {
		cmbProtected.add(UIContext.Constants.all());
		cmbProtected.add(UIContext.Constants.yes());
		cmbProtected.add(UIContext.Constants.no());
		cmbProtected.setSimpleValue(UIContext.Constants.all());
		cmbProtected.setEditable(false);
		cmbProtected.setTriggerAction(TriggerAction.ALL);
	}*/

	private void initComboBoxLastResult() {
		cmbLastResult.add(UIContext.Constants.all());
		cmbLastResult.add(UIContext.Constants.jobStatus_finished());
		cmbLastResult.add(UIContext.Constants.jobStatus_cancelled());
		cmbLastResult.add(UIContext.Constants.jobStatus_failed());
		cmbLastResult.add(UIContext.Constants.NA());
		cmbLastResult.setSimpleValue(UIContext.Constants.all());
		cmbLastResult.setTriggerAction(TriggerAction.ALL);
		cmbLastResult.setEditable(false);
	}
	
	public NodeFilterModel getFilter()
	{
		if ( !isFiltered() ) {
			return null;
		}
		
		NodeFilterModel model = new NodeFilterModel();
		model.setNodeName(nodeName.getValue());
		//model.setIsProtected(getIsProtected());
		model.setLastResult(getLastResult());
		model.setOperatingSystem(operatingSystem.getValue());
		return model;
	}
	
	public boolean isFiltered() {
		if ( Utils.isEmptyOrNull(nodeName.getValue()) && Utils.isEmptyOrNull(operatingSystem.getValue())
				 && cmbLastResult.getSimpleValue().equals(UIContext.Constants.all())) {
			return false;
		}
		
		return true;
	}
	/*private int getIsProtected() {
		if(cmbProtected.getSimpleValue().equals(UIContext.Constants.no())){
			return NodeFilterModel.NODE_PROTECT_NO;
		}else if(cmbProtected.getSimpleValue().equals(UIContext.Constants.yes())){
			return NodeFilterModel.NODE_PROTECT_YES;
		}else{
			return NodeFilterModel.FILTER_ALL;
		}
	}*/
	
	private int getLastResult() {
		if(cmbLastResult.getSimpleValue().equals(UIContext.Constants.jobStatus_finished())){
			return JobStatus.FINISHED.getValue();
		}else if(cmbLastResult.getSimpleValue().equals(UIContext.Constants.jobStatus_cancelled())){
			return JobStatus.CANCELLED.getValue();
		}else if(cmbLastResult.getSimpleValue().equals(UIContext.Constants.jobStatus_failed())){
			return JobStatus.FAILED.getValue();
		}else if(cmbLastResult.getSimpleValue().equals(UIContext.Constants.NA())){
			return JobStatus.IDLE.getValue();
		}else{
			return JobStatusFilterModel.FILTER_ALL;
		}
	}
}
