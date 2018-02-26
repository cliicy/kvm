package com.ca.arcserve.hypervisor;

import com.ca.arcserve.hypervisor.data.HypervisorType;
import com.ca.arcserve.hypervisor.esx.ESXHypervisorImpl;
import com.ca.arcserve.hypervisor.hyperv.HyperVHypervisorImpl;
import com.ca.arcserve.hypervisor.kvm.KVMHypervisorImpl;

public abstract class HypervisorManager {
	public static IHypervisor getHypervisor(HypervisorType type) {
		switch (type) {
		case ESX:
			return new ESXHypervisorImpl();
		case XEN:
		case OVM:
		case RHEV:
		case HYPERV:
			return new HyperVHypervisorImpl();
		case KVM:
			return new KVMHypervisorImpl();
		default:
			return null;
		}
	}
}
