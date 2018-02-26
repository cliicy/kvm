package com.ca.arcserve.linuximaging.ui.client.model.license;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class LicenseStatusModel extends BaseModelData {

	private static final long serialVersionUID = 1L;

	public final static int COMPONENT_GROUP_COUNT_NODE=0;
	public final static int COMPONENT_GROUP_CAPACITY=1;
	public final static int COMPONENT_GROUP_PER_HOST=2;
	public final static int COMPONENT_GROUP_SOCKET=3;
	
	private int componentGroup;
	
	private List<LicensedMachineModel> machines = new ArrayList<LicensedMachineModel>();

	public List<LicensedMachineModel> getMachines() {
		return machines;
	}
	public void setMachines(List<LicensedMachineModel> machines) {
		this.machines = machines;
	}
	public String getComponentName() {
		return get("componentName");
	}
	public void setComponentName(String componentName) {
		set("componentName", componentName);
	}
	public String getVersion() {
		return get("version");
	}
	public void setVersion(String version) {
		set("version", version);
	}
	public Integer getActiveLicenseCount() {
		return get("activeLicenseCount");
	}
	public void setActiveLicenseCount(Integer activeLicenseCount) {
		set("activeLicenseCount", activeLicenseCount);
	}
	public Integer getAvailableLicenseCount() {
		return get("availableLicenseCount");
	}
	public void setAvailableLicenseCount(Integer availableLicenseCount) {
		set("availableLicenseCount", availableLicenseCount);
	}
	public Integer getTotalLicenseCount() {
		return get("totalLicenseCount");
	}
	public void setTotalLicenseCount(Integer totalLicenseCount) {
		set("totalLicenseCount", totalLicenseCount);
	}
	public Integer getNeededLicenseCount() {
		return get("neededLicenseCount");
	}
	public void setNeededLicenseCount(Integer neededLicenseCount) {
		set("neededLicenseCount", neededLicenseCount);
	}
	public String getComponentID() {
		return get("componentID");
	}
	public void setComponentID(String compoentID) {
		set("componentID", compoentID);
	}
	public int getComponentGroup() {
		return componentGroup;
	}
	public void setComponentGroup(int componentGroup) {
		this.componentGroup = componentGroup;
	}
	
}
