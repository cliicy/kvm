package kvmtry;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import com.arcserve.kvm.Hypervisor;
import com.arcserve.kvm.KVMInfo;

public class KVM_app {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/***
		Hypervisor kvmhyperv = new Hypervisor();
		String domainname="centos7-linuxD2D";
		String disk_path="--disk path=./kvmtest1.qcow2";
		String location_path="/centos7_host/CentOS-7-x86_64-DVD-1511.iso";
		String format="qcow2";
		Integer vcpus=1;
		long sram=1024;
		String network="network=default";
		String graphic="--graphics spice";
		String bootOption="cdrom";
		List<SimpleEntry<String, String>> networklist=null;
		List<SimpleEntry<String, Long>> disklist = null;
	
		kvmhyperv.createVM(format,location_path, domainname, bootOption, vcpus, sram, networklist, bootOption, disklist);
		***/
		String sKVMXMLpath=args[0];
		KVMInfo kvmobj=new KVMInfo();
		kvmobj.createDomain(sKVMXMLpath);
		//kvmhyperv.createVM(vmStoragePath, vmName, bootOption, cpuCount, memorySize, nicToAddByMacAndHostNetworkName, cdPath, disks)
		//KVMHypervisorImpl kvmimpl = new KVMHypervisorImpl();
		//kvmimpl.createVM(subHost, config);
	}

}
