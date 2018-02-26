package com.ca.arcserve.linuximaging.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VMRestoreTargetModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -38982953155742922L;
	
	private VirtualizationServerModel virtualizationServerModel;
	
	private VirtualMachineInfoModel vmModel;
	
	public VirtualizationServerModel getVirtualizationServerModel() {
		return virtualizationServerModel;
	}

	public void setVirtualizationServerModel(
			VirtualizationServerModel virtualizationServerModel) {
		this.virtualizationServerModel = virtualizationServerModel;
	}

	public VirtualMachineInfoModel getVmModel() {
		return vmModel;
	}

	public void setVmModel(VirtualMachineInfoModel vmModel) {
		this.vmModel = vmModel;
	}
	
	

}
