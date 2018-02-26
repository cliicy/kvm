package com.ca.arcserve.hypervisor.esx;

import java.io.File;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareVirtualInfrastructureManager;
import com.ca.arcflash.ha.vmwaremanager.Disk_Info;
import com.ca.arcflash.ha.vmwaremanager.Disk_Sector_Info;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.InvalidLoginException;
import com.ca.arcflash.ha.vmwaremanager.ResPool_Info;
import com.ca.arcflash.ha.vmwaremanager.VMNetworkConfigInfo;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareInfrastructureEntityInfo;
import com.ca.arcflash.ha.vmwaremanager.VirtualNetworkInfo;
import com.ca.arcflash.ha.vmwaremanager.VMwareServerType;
import com.ca.arcflash.ha.vmwaremanager.VMwareVMCreationException;
import com.ca.arcflash.ha.vmwaremanager.powerState;
import com.ca.arcflash.ha.vmwaremanagerIntf.BootDevice;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVMDetails;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualCDRom;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualDisk;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualMachineDefaultSetting;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualNICCard;
import com.ca.arcflash.ha.vmwaremanagerIntf.CDRomType;
import com.ca.arcflash.ha.vmwaremanagerIntf.ESXDatastore;
import com.ca.arcflash.ha.vmwaremanagerIntf.NasDatastore;
import com.ca.arcserve.hypervisor.IHypervisor;
import com.ca.arcserve.hypervisor.data.BootDeviceType;
import com.ca.arcserve.hypervisor.data.CDRomInfo;
import com.ca.arcserve.hypervisor.data.CDRom_Type;
import com.ca.arcserve.hypervisor.data.DiskInfo;
import com.ca.arcserve.hypervisor.data.HypervisorInfo;
import com.ca.arcserve.hypervisor.data.NasDatastoreInfo;
import com.ca.arcserve.hypervisor.data.NetworkInfo;
import com.ca.arcserve.hypervisor.data.PowerState;
import com.ca.arcserve.hypervisor.data.ResourcePool;
import com.ca.arcserve.hypervisor.data.SnapshotDiskInfo;
import com.ca.arcserve.hypervisor.data.SnapshotDiskSectorInfo;
import com.ca.arcserve.hypervisor.data.StorageInfo;
import com.ca.arcserve.hypervisor.data.SubHypervisorInfo;
import com.ca.arcserve.hypervisor.data.VMCreationConfiguration;
import com.ca.arcserve.hypervisor.data.VirtualMachineDefaultSetting;
import com.ca.arcserve.hypervisor.data.VirtualMachineInfo;
import com.ca.arcserve.hypervisor.exception.HypervisorErrorCode;
import com.ca.arcserve.hypervisor.exception.HypervisorException;

public class ESXHypervisorImpl implements IHypervisor {
	private CAVirtualInfrastructureManager esxManager = null;
	private Boolean isConnected() {
		if(esxManager != null) return true;
		return false;
	}
	private static final Logger logger = Logger.getLogger(ESXHypervisorImpl.class);
	@Override
	public Boolean connect(HypervisorInfo hypervisor) throws HypervisorException{
		try {
			esxManager = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(hypervisor.getName(), hypervisor.getUsername(), hypervisor.getPassword(), hypervisor.getProtocol(), true, hypervisor.getPort());
		} catch(InvalidLoginException e){
			logger.error("Failed to create connection to esx/vc(" + hypervisor.getName() + ") invalid credential");
			throw new HypervisorException(HypervisorErrorCode.ERROR_INVALID_CREDENTIAL,"Invalid credential");
		} catch (Exception e) {
			logger.error("Failed to create connection to esx/vc(" + hypervisor.getName() + ")");
			throw new HypervisorException(HypervisorErrorCode.ERROR_INVALID_HOST,"Can not connect to hypervisor");
		}
		return true;
	}
	
	@Override
	public void close() {
		if(isConnected()){
			try {
				esxManager.close();
				esxManager = null;
			} catch (Exception e) {
				logger.error("Failed to close connection to esx/vc", e);
			}
		}
	}

	@Override
	public List<SubHypervisorInfo> getSubHosts() {
		if(isConnected()){
			try {
				List<SubHypervisorInfo> res = new ArrayList<SubHypervisorInfo>();
				for(ESXNode n: esxManager.getESXNodeList()) {
					SubHypervisorInfo hyInfo = new SubHypervisorInfo();
					hyInfo.setName(n.getEsxName());
					hyInfo.setDcName(n.getDataCenter());
					hyInfo.setDcDisplayName(n.getDcDisplayName());
					res.add(hyInfo);
				}
				return res;
			} catch (Exception e) {
				logger.error("Failed to get sub host list", e);
				return null;
			}
		}else return null;
	}

	@Override
	public String createVM(SubHypervisorInfo subHost, VMCreationConfiguration config) throws HypervisorException{
		if(isConnected()){
			try {
				ArrayList<CAVirtualDisk> arcVirtualDisks = new ArrayList<CAVirtualDisk>();
				if(config.getDisks()!=null){
					for(DiskInfo diskInfo : config.getDisks()) {
						CAVirtualDisk disk = new CAVirtualDisk();
						disk.setCapacityinKB(diskInfo.getSize());
						disk.setThinProvisioning(true);
						disk.setDiskDataStore(diskInfo.getStorage());
						disk.setDiskController(diskInfo.getDiskController());
						disk.setUseExistingDiskFile(diskInfo.isUseExistingFile());
						disk.setDiskFile(diskInfo.getDiskFile());
						arcVirtualDisks.add(disk);
					}
				}
				ArrayList<CAVirtualNICCard> arcvirtualNics= new ArrayList<CAVirtualNICCard>();
				for(NetworkInfo network : config.getTargetNetwork()){
					CAVirtualNICCard nic = new CAVirtualNICCard();
					nic.setNetworkName(network.getNetworkName());
					nic.setadapterType(network.getAdapterType());
					nic.setLabel(network.getNetworkName());
					nic.setAddressType("manual");
					String mac = getMacAddress();
					nic.setMacAddress(mac);
					network.setMacAddress(mac);
					arcvirtualNics.add(nic);
				}
				
				CAVirtualCDRom cdRom = new CAVirtualCDRom();
				cdRom.setDatastore(config.getCdInfo().getStorage());
				cdRom.setFilename(config.getCdInfo().getFilename());
				if(config.getCdInfo().getType() == CDRom_Type.ISO){
					cdRom.setType(CDRomType.ISO);
				}else{
					cdRom.setType(CDRomType.OTHER);
				}
				BootDevice bootDevice = null;
				if(config.getBootOption() == BootDeviceType.CD){
					bootDevice = BootDevice.CD;
				}else if(config.getBootOption() == BootDeviceType.DISK){
					bootDevice = BootDevice.DISK;
				}else if(config.getBootOption() == BootDeviceType.NETWORK){
					bootDevice = BootDevice.NETWORK;
				}
				CAVMDetails detail = esxManager.createVMwareVM(
						config.getName(),
						config.getMemorySize(),
						config.getCpuCount(),
						config.getGuest(),
						config.getVmVersion(), 						// vmVersion 
						config.getTargetStorage(),
						subHost.getName(), 
						subHost.getDcName(), 						// dcName 
						arcVirtualDisks,
						arcvirtualNics, 	
						config.getResourcePoolId(), 						// resoucePool
						config.isUEFI(), 						// isUEFI
						bootDevice,				// bootDevice 
						cdRom,
						CAVMwareVirtualInfrastructureManager.NON_APPLIANCE_LINUX_VM);
				return detail.getUuid();
			} catch(VMwareVMCreationException e){
				logger.error("Failed to create vm reason:" + e.getErrorMessage());
				int errorCode = e.getErrorCode();
				if(errorCode == VMwareVMCreationException.NO_DISK_SPACE){
					throw new HypervisorException(HypervisorErrorCode.ERROR_NO_DISK_SPACE,e.getErrorMessage());
				}else if(errorCode == VMwareVMCreationException.INVALID_DATASTORE){
					throw new HypervisorException(HypervisorErrorCode.ERROR_INVALID_STORAGE,e.getErrorMessage());
				}else if(errorCode == VMwareVMCreationException.FILE_TOO_LARGE){
					throw new HypervisorException(HypervisorErrorCode.ERROR_FILE_TOO_LARGE,e.getErrorMessage());
				}else{
					throw new HypervisorException(HypervisorErrorCode.ERROR_CREATE_VM,e.getErrorMessage(),e.getErrorMessage());
				}
			} catch(Exception e) {
				logger.error("Failed to create vm reason: no reason",e);
				throw new HypervisorException(HypervisorErrorCode.ERROR_CREATE_VM,"Failed to create virtual machine." + e.getMessage(),e.getLocalizedMessage());
			}
		}
		else return null;
	}

	@Override
	public boolean deleteVM(String uuid) throws HypervisorException{
		if(isConnected())
			try {
				return esxManager.deleteVM(null, uuid);
			} catch (Exception e) {
				logger.error("Failed to delete vm", e);
				throw new HypervisorException(HypervisorErrorCode.ERROR_DELETE_VM,"Failed to delete vm",e.getLocalizedMessage());
			}
		else return false;
	}

	@Override
	public boolean startVM(String uuid) throws HypervisorException{
		if(isConnected())
			try {
				return esxManager.powerOnVM(null, uuid);
			} catch (Exception e) {
				logger.error("Failed to power on vm", e);
				throw new HypervisorException(HypervisorErrorCode.ERROR_START_VM,"Failed to start vm",e.getLocalizedMessage());
			}
		else return false;
	}

	@Override
	public boolean stopVM(String uuid) throws HypervisorException{
		if(isConnected())
			try {
				esxManager.powerOffVM(null, uuid,true);
				return true;
			} catch (Exception e) {
				logger.error("Failed to power off vm", e);
				throw new HypervisorException(HypervisorErrorCode.ERROR_STOP_VM,"Failed to stop vm",e.getLocalizedMessage());
			}
		else return false;
	}

	@Override
	public String createHypervisorNetwork(String subHost, String networkName) throws HypervisorException{
		if(isConnected())
			try {
				VMNetworkConfigInfo network = esxManager.createHostNetwork(subHost, networkName);
				return network.getDeviceName();
			} catch (Exception e) {
				logger.error("Failed to create network");
				throw new HypervisorException(HypervisorErrorCode.ERROR_CREATE_NETWORK,"Failed to create network",e.getLocalizedMessage());
			}
		else return null;
	}
	
	@Override
	public boolean removeHypervisorNetwork(String subHost, String networkName) throws HypervisorException{
		if(isConnected())
			try {
				return esxManager.removeHostNetwork(subHost, networkName);
			} catch (Exception e) {
				logger.error("Failed to create network");
				throw new HypervisorException(HypervisorErrorCode.ERROR_CREATE_NETWORK,"Failed to remove network",e.getLocalizedMessage());
			}
		else return false;
	}

	@Override
	public VirtualMachineInfo getVirtualMachineInfo(String uuid) throws HypervisorException{
		if(isConnected()){
			try{
				VM_Info vmInfo =  esxManager.getVMInfo(null, uuid);
				VirtualMachineInfo info = new VirtualMachineInfo();
				List<NetworkInfo> networkAdapters = new ArrayList<NetworkInfo>();
				for(CAVirtualNICCard card : vmInfo.getNetworkAdapters()){
					NetworkInfo adapter = new NetworkInfo();
					adapter.setAdapterType(card.getadapterType());
					adapter.setMacAddress(card.getMacAddress());
					adapter.setNetworkName(card.getNetworkName());
					networkAdapters.add(adapter);
				}
				info.setNetworkAdapters(networkAdapters);
				info.setVmName(vmInfo.getVMName());
				return info;
			} catch (Exception e){
				logger.error("Failed to get vm information",e);
				throw new HypervisorException(HypervisorErrorCode.ERROR_GET_VM_INFO,"Failed to get virtual machine info",e.getLocalizedMessage());
			}
		}
		return null;
	}

	@Override
	public boolean changeCDRom(String uuid, CDRomInfo cdInfo) throws HypervisorException{
		if(isConnected()){
			try{
				CAVirtualCDRom cdRom = new CAVirtualCDRom();
				cdRom.setDatastore(cdInfo.getStorage());
				cdRom.setFilename(cdInfo.getFilename());
				if(cdInfo.getType() == CDRom_Type.ISO){
					cdRom.setType(CDRomType.ISO);
				}else{
					cdRom.setType(CDRomType.OTHER);
				}
				esxManager.reconfigVMCDRom(null, uuid, cdRom);
				return true;
			} catch (Exception e){
				logger.error("Failed to changeCDRom",e);
				throw new HypervisorException(HypervisorErrorCode.ERROR_CHANGE_CD_ROM,"Failed to change cd rom",e.getLocalizedMessage());
			}
		}
		return false;
	}

	@Override
	public String addNicCard(String vmuuid,String subHost,NetworkInfo networkInfo) throws HypervisorException{
		if(isConnected()){
			try{
				CAVirtualNICCard nicCard = new CAVirtualNICCard();
				nicCard.setadapterType(networkInfo.getAdapterType());
				nicCard.setLabel(networkInfo.getNetworkName());
				nicCard.setNetworkName(networkInfo.getNetworkName());
				nicCard.setAddressType("manual");
				String mac = getMacAddress();
				nicCard.setMacAddress(mac);
				esxManager.addNicCard(null, vmuuid, subHost, nicCard);
				return mac;
			} catch (Exception e){
				logger.error("Failed to addNicCard",e);
			}
		}
		return null;
	}

	@Override
	public List<VirtualMachineInfo> getVMList(SubHypervisorInfo subHost) {
		if(isConnected()){
			try {
				List<VM_Info> vmList = esxManager.getVMNames(getESXNode(subHost), true);
				if(vmList != null && vmList.size()>0){
					List<VirtualMachineInfo> vmInfoList = new ArrayList<VirtualMachineInfo>();
					for(VM_Info vm : vmList){
						VirtualMachineInfo vmInfo = new VirtualMachineInfo();
						List<NetworkInfo> networkAdapters = new ArrayList<NetworkInfo>();
						for(CAVirtualNICCard card : vm.getNetworkAdapters()){
							NetworkInfo adapter = new NetworkInfo();
							adapter.setAdapterType(card.getadapterType());
							adapter.setMacAddress(card.getMacAddress());
							adapter.setNetworkName(card.getNetworkName());
							networkAdapters.add(adapter);
						}
						vmInfo.setNetworkAdapters(networkAdapters);
						vmInfo.setUuid(vm.getVMvmInstanceUUID());
						vmInfoList.add(vmInfo);
					}
					return vmInfoList;
				}
			} catch (Exception e) {
				logger.error("Failed to getVMList",e);
			}
		}
		return null;
	}

	@Override
	public String createSnapshot(String vmuuid, String snapshotName,
			String description) {
		if(isConnected()){
			try{
				return esxManager.createSnapshot(null, vmuuid, snapshotName, description);
			}catch(Exception e){
				logger.error("create snapshot failed",e);
			}
		}
		return null;
	}

	@Override
	public int enableChangeTracking(String vmuuid, boolean isEnable) {
		if(isConnected()){
			try{
				if(isEnable){
					return esxManager.enableChangeTracking(null, vmuuid);
				}else{
					return esxManager.disableChangeTracking(null, vmuuid) == true ? 0 : -1;
				}
			}catch(Exception e){
				logger.error("create snapshot failed",e);
			}
		}
		return -1;
	}

	@Override
	public boolean removeSnapshot(String vmuuid, String snapshotId) {
		if(isConnected()){
			try{
				if(snapshotId == null){
					return esxManager.removeAllSnapshots(null, vmuuid); 
				}
				return esxManager.removeSnapshot(null, vmuuid, snapshotId);
			}catch(Exception e){
				logger.error("remove snapshot failed",e);
			}
		}
		return false;
	}

	@Override
	public List<SnapshotDiskInfo> getSnapshotDiskInfo(String vmuuid,
			String snapshotId) {
		if(isConnected()){
			try{
				List<Disk_Info> diskList = esxManager.getsnapDiskInfo(null, vmuuid, snapshotId);
				if(diskList != null){
					List<SnapshotDiskInfo> snapshotDiskList = new ArrayList<SnapshotDiskInfo>();
					for(Disk_Info diskInfo : diskList){
						SnapshotDiskInfo sDisk = new SnapshotDiskInfo();
						sDisk.setChangeID(diskInfo.getchangeID());
						sDisk.setDeviceKey(diskInfo.getDiskDeviceKey());
						sDisk.setDiskURL(diskInfo.getdiskURL());
						snapshotDiskList.add(sDisk);
					}
					return snapshotDiskList;
				}
			}catch(Exception e){
				logger.error("getSnapshotDiskInfo failed",e);
			}
		}
		return null;
	}

	@Override
	public SnapshotDiskSectorInfo generateBitMapForDisk(String vmuuid,
			String snapshotId, String diskChangeId, Integer diskDeviceKey,
			String filePath, Integer chunkSize, Integer sectorSize) {
		if(isConnected()){
			try{
				Disk_Sector_Info diskSectorInfo = esxManager.getUsedDiskBlocks(null, vmuuid, snapshotId, diskChangeId, diskDeviceKey, filePath, chunkSize, sectorSize);
				if(diskSectorInfo != null){
					SnapshotDiskSectorInfo sDiskSectorInfo = new SnapshotDiskSectorInfo();
					sDiskSectorInfo.setDiskTotalNumSectors(diskSectorInfo.getDiskTotalNumSectors());
					sDiskSectorInfo.setDiskUsedNumSectors(diskSectorInfo.getDiskUsedNumSectors());
					return sDiskSectorInfo;
				}
			}catch(Exception e){
				logger.error("generateBitMapForDisk failed",e);
			}
		}
		return null;
	}

	@Override
	public List<ResourcePool> getResourcePoolList(SubHypervisorInfo subHost) {
		if(isConnected()){
			try{
				List<ResPool_Info> resPoolList = esxManager.getresPool(getESXNode(subHost));
				if(resPoolList != null){
					List<ResourcePool> poolList = new ArrayList<ResourcePool>();
					for(ResPool_Info res : resPoolList){
						ResourcePool rp = new ResourcePool();
						rp.setName(res.getpoolName());
						rp.setId(res.getpoolMoref());
						poolList.add(rp);
					}
					return poolList;
				}
			}catch(Exception e){
				logger.error("getResourcePoolList failed",e);
			}
		}
		return null;
	}

	private String getDcPath(SubHypervisorInfo subHypervisorInfo){
		String dcPath = subHypervisorInfo.getDcDisplayName();
		try {
			VMwareServerType serverType = esxManager.getVMwareServerType();
			if(serverType == VMwareServerType.virtualCenter){
				dcPath = esxManager.getDcPath(getESXNode(subHypervisorInfo));
			}
		} catch (Exception e) {
			logger.error("Failed to get dc path . ",e);
		}
		return URLEncoder.encode(dcPath);
	}
	
	@Override
	public int putFile(HypervisorInfo hypervisor, String subHost,
			String datastore, String filename, String filePath) throws HypervisorException {
		if(isConnected()){
			List<SubHypervisorInfo> subList = getSubHosts();
			SubHypervisorInfo subHypervisorInfo = null;
			for (SubHypervisorInfo info : subList) {
				if (info.getName().equals(subHost)) {
					subHypervisorInfo = info;
					break;
				}
			}
			try {
				String url = hypervisor.getProtocol() + "://"
						+ hypervisor.getName() + "/folder/" + filename
						+ "?dcPath=" + getDcPath(subHypervisorInfo) + "&dsName="
						+ URLEncoder.encode(datastore);
				HttpPut put = new HttpPut(url);
				FileEntity entity = new FileEntity(new File(filePath));
				entity.setChunked(false);
				put.setEntity(entity);
				// StringEntity input = new StringEntity("test");
				put.getParams().setBooleanParameter(
						"http.protocol.expect-continue", true);
				put.setEntity(entity);
				TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
					@Override
					public boolean isTrusted(X509Certificate[] certificate,
							String authType) {
						return true;
					}
				};
				SSLContext sslcontext = SSLContexts.custom()
						.loadTrustMaterial(null, acceptingTrustStrategy).build();
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						sslcontext, null, null,
						SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(new AuthScope(hypervisor.getName(),
						hypervisor.getPort()), new UsernamePasswordCredentials(
								hypervisor.getUsername(), hypervisor.getPassword()));
				CloseableHttpClient httpclient = HttpClients.custom()
						.setDefaultCredentialsProvider(credsProvider)
						.setSSLSocketFactory(sslsf).build();
				HttpResponse response = httpclient.execute(put);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 201 || statusCode == 200 || statusCode == 204) {
					logger.debug("Successfully put file. status code : " + statusCode);
				}else{
					logger.error("Failed to put file error:"
							+ response.getStatusLine().getStatusCode() + " file name : " + filename);
					throw new HypervisorException(
							HypervisorErrorCode.ERROR_PUT_FILE, "Put file error");
				}
				return 0;
			} catch (Exception ex) {
				logger.error("Failed to put file to datastore", ex);
				throw new HypervisorException(HypervisorErrorCode.ERROR_PUT_FILE,
						"Put file error",ex.getLocalizedMessage());
			}
		}
		return -1;
	}

	@Override
	public int deleteFile(HypervisorInfo hypervisor, String subHost,
			String datastore, String filename) throws HypervisorException{
		if(isConnected()){
			List<SubHypervisorInfo> subList = getSubHosts();
			SubHypervisorInfo subHypervisorInfo = null;
			for (SubHypervisorInfo info : subList) {
				if (info.getName().equals(subHost)) {
					subHypervisorInfo = info;
					break;
				}
			}
			try {
				String url = hypervisor.getProtocol() + "://"
						+ hypervisor.getName() + "/folder/" + filename
						+ "?dcPath=" + getDcPath(subHypervisorInfo) + "&dsName="
						+ URLEncoder.encode(datastore);
				HttpDelete put = new HttpDelete(url);
				TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
					@Override
					public boolean isTrusted(X509Certificate[] certificate,
							String authType) {
						return true;
					}
				};
				SSLContext sslcontext = SSLContexts.custom()
						.loadTrustMaterial(null, acceptingTrustStrategy).build();
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						sslcontext, null, null,
						SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(new AuthScope(hypervisor.getName(),
						hypervisor.getPort()),
						new UsernamePasswordCredentials(hypervisor.getUsername(),
								hypervisor.getPassword()));
				CloseableHttpClient httpclient = HttpClients.custom()
						.setDefaultCredentialsProvider(credsProvider)
						.setSSLSocketFactory(sslsf).build();
				HttpResponse response = httpclient.execute(put);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 204 || statusCode == 200) {
					logger.debug("Successully delete file " + filename);
				}else{
					logger.error("Failed to delete file error:"
							+ response.getStatusLine().getStatusCode() + " file name : " + filename);
					throw new HypervisorException(
							HypervisorErrorCode.ERROR_DELETE_FILE,
							"Delete file error");
				}
				return 0;
			} catch (Exception ex) {
				logger.error("Failed to delete file on datastore", ex);
				throw new HypervisorException(
						HypervisorErrorCode.ERROR_DELETE_FILE, "Delete file error",ex.getLocalizedMessage());
			}
		}
		return -1;
	}

	@Override
	public List<StorageInfo> getStorageList(SubHypervisorInfo subHost) {
		if(isConnected()){
			try {
				List<ESXDatastore> dsList = esxManager.getESXHostDataStoreListWithDetail(getESXNode(subHost));
				List<StorageInfo> storageList = new ArrayList<StorageInfo>();
				if(dsList != null){
					for(ESXDatastore ds : dsList){
						StorageInfo storage = new StorageInfo();
						storage.setAccessiable(ds.isAccessiable());
						storage.setName(ds.getName());
						storage.setCapacity(ds.getCapacity());
						storage.setFreeSize(ds.getFreeSize());
						storage.setDevice(ds.getDevice());
						int type = StorageInfo.STORAGE_TYPE_OTHERS;
						if(ESXDatastore.TYPE_LOCAL.equals(ds.getType())){
							type = StorageInfo.STORAGE_TYPE_LOCAL;
						}else if(ESXDatastore.TYPE_NFS.equals(ds.getType())){
							type = StorageInfo.STORAGE_TYPE_NETWORK_SHARE;
						}
						storage.setType(type);
						storageList.add(storage);
					}
				}
				return storageList;
			} catch (Exception e) {
				logger.error("Failed to getStorageList",e);
			}
		}
		return null;
	}

	@Override
	public VirtualMachineDefaultSetting getVirtualMachineDefaultSetting(String subHost,
			String guestOS) {
		if(isConnected()){
			List<SubHypervisorInfo> subList = getSubHosts();
			SubHypervisorInfo subHypervisorInfo = null;
			for (SubHypervisorInfo info : subList) {
				if (info.getName().equals(subHost)) {
					subHypervisorInfo = info;
					break;
				}
			}
			try {
				CAVirtualMachineDefaultSetting setting = esxManager.getVMDefaultSetting(getESXNode(subHypervisorInfo),guestOS);
				VirtualMachineDefaultSetting defaultSetting = null;
				if(setting != null){
					defaultSetting = new VirtualMachineDefaultSetting();
					defaultSetting.setNetworkAdapterType(setting.getNetworkAdapterType().replace("Virtual", ""));
					defaultSetting.setScsiController(setting.getScsiController());
					defaultSetting.setGuestOsID(setting.getGuestOsID());
					defaultSetting.setSupportedMaxVmVersion(setting.getSupportedMaxVMVersion());
				}
				return defaultSetting;
			} catch (Exception e) {
				logger.error("Failed to getVirtualMachineDefaultSetting",e);
			}
		}
		return null;
	}

	@Override
	public boolean removeNicCard(String vmuuid, String subHost,
			String networkName) {
		if(isConnected()){
			try {
				esxManager.removeNicCardByNetworkName(null, vmuuid, subHost, networkName);
				return true;
			} catch (Exception e) {
				logger.error("Failed to getVirtualMachineDefaultSetting",e);
			}
		}
		return false;
	}

	@Override
	public List<String> getNetworkList(SubHypervisorInfo subHost) {
		if(isConnected()){
			try {
				List<String> result = new ArrayList<String>();
				ArrayList<VirtualNetworkInfo> networkInfos = esxManager.getVirtualNetworkList(getESXNode(subHost));
				for (int i = 0; i < networkInfos.size(); i++) {
					result.add(networkInfos.get(i).getVirtualName());
				}
				return result;
			} catch (Exception e) {
				logger.error("Failed to getStorageList",e);
			}
		}
		return null;
	}

	@Override
	public boolean addSerialPort(String vmuuid, String subHost,
			String datastore, String fileName) throws HypervisorException {
		if(isConnected()){
			try{
				return esxManager.addSerialPort(null, vmuuid, subHost, datastore,fileName);
			} catch (Exception e){
				logger.error("Failed to addSerialPort",e);
			}
		}
		return false;
	}

	@Override
	public boolean addConfigurationParameter(String vmuuid, String subHost,
			Map<String, String> parameters) throws HypervisorException {
		if(isConnected()){
			try{
				return esxManager.addConfigurationParameter(null, vmuuid, subHost, parameters);
			} catch (Exception e){
				logger.error("Failed to addConfigurationParameter",e);
			}
		}
		return false;
	}

	@Override
	public boolean changeBootOrder(String vmuuid, BootDeviceType bootDeviceType)
			throws HypervisorException {
		if(isConnected()){
			try{
				BootDevice bootDevice = null;
				if(bootDeviceType == BootDeviceType.CD){
					bootDevice = BootDevice.CD;
				}else if(bootDeviceType == BootDeviceType.DISK){
					bootDevice = BootDevice.DISK;
				}else if(bootDeviceType == BootDeviceType.NETWORK){
					bootDevice = BootDevice.NETWORK;
				}
				return esxManager.reconfigBootOrder(null, vmuuid, bootDevice);
			} catch (Exception e){
				logger.error("Failed to changeBootOrder",e);
			}
		}
		return false;
	}

	@Override
	public String createNasDatastore(SubHypervisorInfo subHost,
			NasDatastoreInfo datastore) throws HypervisorException {
		if(isConnected()){
			try{
				NasDatastore ds = new NasDatastore();
				ds.setAccessMode(datastore.getAccessMode());
				ds.setDataStoreName(datastore.getDataStoreName());
				ds.setPassword(datastore.getPassword());
				ds.setRemoteHost(datastore.getRemoteHost());
				ds.setRemotePath(datastore.getRemotePath());
				ds.setType(datastore.getType());
				ds.setUsername(datastore.getUsername());
				String result = esxManager.createNasDatastore(getESXNode(subHost), ds);
				return result;
			}catch(Exception e){
				logger.error("Failed to create datastore: " , e);
			}
		}
		return null;
	}

	@Override
	public boolean removeDatastore(SubHypervisorInfo subHost,
			String datastoreName) throws HypervisorException {
		if(isConnected()){
			try {
				return esxManager.removeDatastore(getESXNode(subHost), datastoreName);
			} catch (Exception e) {
				logger.error("Failed to remove datastore: ", e);
			}
		}
		return false;
	}
	
	@Override
	public void refreshDatastore(SubHypervisorInfo subHost,
			String datastoreName) {
		if(isConnected()){
			try {
				esxManager.refreshDatastore(getESXNode(subHost), datastoreName);
			} catch (Exception e) {
				logger.error("Failed to refresh datastore: ", e);
			}
		}
	}
	
	private ESXNode getESXNode(SubHypervisorInfo subHost){
		ESXNode node = new ESXNode();
		node.setEsxName(subHost.getName());
		node.setDataCenter(subHost.getDcName());
		return node;
	}

	@Override
	public int getHypervisorLevel() {
		if(isConnected()){
			try {
				VMwareServerType serverType = esxManager.getVMwareServerType();
				if(serverType == VMwareServerType.virtualCenter){
					return HypervisorInfo.HYPERVISOR_LEVEL_TOP;
				}else{
					return HypervisorInfo.HYPERVISOR_LEVEL_SUB;
				}
			} catch (Exception e) {
				logger.error("Failed to get hypervisor level." , e);
			}
		}
		return HypervisorInfo.HYPERVISOR_LEVEL_SUB;
	}

	@Override
	public PowerState getVMPowerStatus(String vmuuid) throws HypervisorException{
		PowerState status = PowerState.ErrorFault;
		if(isConnected()){
			try {
				powerState state = esxManager.getVMPowerstate(null, vmuuid);
				if(state == powerState.errorFault){
					status = PowerState.ErrorFault;
				}else if(state == powerState.poweredOff){
					status = PowerState.PoweredOff;
				}else if(state == powerState.poweredOn){
					status = PowerState.PoweredOn;
				}else if(state == powerState.suspended){
					status = PowerState.Suspended;
				}
			} catch (Exception e) {
				logger.error("Failed to get vm power status",e);
				throw new HypervisorException(HypervisorErrorCode.ERROR_GET_VM_POWER_STATUS,"Failed to get virtual machine power status.",e.getLocalizedMessage());
			}
		}
		return status;
	}
	
	private String getMacAddress(){
		String mac = "00:50:56";
		mac += ":" + getRamdonStr(4) + getRamdonStr(16);
		for (int i = 0; i < 2; i++){
            mac += ":" + getRamdonStr(16) + getRamdonStr(16);
		}
		return mac;
	}
	
	private String getRamdonStr(int number){
		return String.format("%1$X", (byte) (Math.random() * number));
	}

	@Override
	public boolean uploadVMNvRamFile(String vmuuid, String file) {
		if(isConnected()){
			try{
				return esxManager.setVMNVRAMFile(null, vmuuid, file);
			} catch (Exception e) {
				logger.error("Failed to uploadVMNvRamFile: " , e);
			}
		}
		return false;
	}

	@Override
	public boolean isSupportedVM(int requireVmVersion) {
		return true;
	}

	@Override
	public String checkStorageExist(SubHypervisorInfo subHost,String storageName) {
		List<StorageInfo> datastoreList = getStorageList(subHost);
		String dsName = null;
		for(StorageInfo datastore : datastoreList){
			if(datastore.getName().equalsIgnoreCase(storageName)){
				dsName = datastore.getName();
				break;
			}
		}
		return dsName;
	}

	@Override
	public boolean shutdownOS(String uuid) throws HypervisorException {
		if(isConnected())
			try {
				esxManager.shutdownVM(null, uuid);
				return true;
			} catch (Exception e) {
				logger.error("Failed to shutdown vm", e);
				throw new HypervisorException(HypervisorErrorCode.ERROR_STOP_VM,"Failed to shutdown vm",e.getLocalizedMessage());
			}
		else return false;
	}
}
