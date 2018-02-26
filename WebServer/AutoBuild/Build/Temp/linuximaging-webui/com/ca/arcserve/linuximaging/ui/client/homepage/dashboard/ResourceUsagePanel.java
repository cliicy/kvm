package com.ca.arcserve.linuximaging.ui.client.homepage.dashboard;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.Utils;
import com.ca.arcserve.linuximaging.ui.client.model.dashboard.ResourceUsageModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;


public class ResourceUsagePanel extends DashboardPanel {
	
	private LabelField txtCpuUsage;
	private LabelField txtPhysicalMemory;
	private LabelField txtSwapSize;
	private LabelField txtInstallationVolume;

	public ResourceUsagePanel(){
		setHeading(UIContext.Constants.resourceUsage());
		
		TableLayout layout = new TableLayout();
	    layout.setColumns(2);
	    layout.setWidth("100%");
	    layout.setCellPadding(2);
		layout.setCellSpacing(2);
	    setLayout(layout);
	    
	    TableData tdLabel = new TableData();
		tdLabel.setWidth("50%");
		tdLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdField = new TableData();
		tdField.setWidth("50%");
		tdField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField lblCpuUsage =new LabelField(UIContext.Constants.cpuUsage());
		txtCpuUsage =new LabelField();
		this.add(lblCpuUsage, tdLabel);
		this.add(txtCpuUsage, tdField);
		
		LabelField lblPhysicalMemory =new LabelField(UIContext.Constants.physicalMemory());
		txtPhysicalMemory =new LabelField();
		this.add(lblPhysicalMemory, tdLabel);
		this.add(txtPhysicalMemory, tdField);
		
		LabelField lblSwapSize =new LabelField(UIContext.Constants.swapSize());
		txtSwapSize =new LabelField();
		this.add(lblSwapSize, tdLabel);
		this.add(txtSwapSize, tdField);
		
		LabelField lblInstallationVolume =new LabelField(UIContext.Constants.installationVolume());
		txtInstallationVolume =new LabelField();
		this.add(lblInstallationVolume, tdLabel);
		this.add(txtInstallationVolume, tdField);
	}
	
	public void refreshData(ResourceUsageModel model){
		if(model==null){
			return;
		}
		this.txtCpuUsage.setValue(model.getCpuUsage().intValue()+"%");
		this.txtPhysicalMemory.setValue(displayPercentText(model.getPhysicalMemoryFree(),model.getPhysicalMemoryTotal()));
		this.txtSwapSize.setValue(displayPercentText(model.getSwapSizeFree(),model.getSwapSizeTotal()));
		this.txtInstallationVolume.setValue(displayPercentText(model.getInstallationVolumeFree(),model.getInstallationVolumeTotal()));
	}
	
	public  void resetData(){
		this.txtCpuUsage.setValue("");
		this.txtPhysicalMemory.setValue("");
		this.txtSwapSize.setValue("");
		this.txtInstallationVolume.setValue("");
	}
	
	private String displayPercentText(Long free, Long total){
		if(total==0){
			return "0/0 (0%)";
		}
		long percent= (free*100)/total;
		return Utils.bytes2GBString(free*1024)+"/"+Utils.bytes2GBString(total*1024)+" ("+percent+"%)";
	}

}
