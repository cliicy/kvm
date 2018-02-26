package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.List;

public class PhysicalStandbyModel extends StandbyModel {
	
	public static final int START_MACHINE_METHOD_WAN_ON_LAN = 0;
	public static final int START_MACHINE_METHOD_OTHER = 1;

	/**
	 * 
	 */
	private static final long serialVersionUID = -615466971238561281L;
	
	private List<PhysicalStandbyMachineModel> standByMachineList;

	public List<PhysicalStandbyMachineModel> getStandByMachineList() {
		return standByMachineList;
	}

	public void setStandByMachineList(
			List<PhysicalStandbyMachineModel> standByMachineList) {
		this.standByMachineList = standByMachineList;
	}

}
