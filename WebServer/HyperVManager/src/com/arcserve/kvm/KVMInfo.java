package com.arcserve.kvm;
import org.libvirt.*;
import org.libvirt.jna.ConnectionPointer;


public class KVMInfo {
	//public KVMInfo[] arykvmInfo;
	private String domain_name;
	
	public String getDomain_name() {
		return domain_name;
	}

	public void setDomain_name(String kvm_name) {
		domain_name = kvm_name;
	}
	public void createDomain(String XMLfile) {
		System.out.println(XMLfile.toCharArray());
		try {
			//Domain 
			//ConnectionPointer vmp=new ConnectionPointer();
			Connect udpvm=new Connect(null);
			udpvm.domainCreateXML(XMLfile, 1);
		} catch (LibvirtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.domainCreateXML
	}
}

