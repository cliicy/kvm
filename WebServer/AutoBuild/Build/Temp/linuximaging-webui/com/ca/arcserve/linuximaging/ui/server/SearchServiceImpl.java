package com.ca.arcserve.linuximaging.ui.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.linuximaging.search.ARCserveSearchResult;
import com.ca.arcserve.linuximaging.search.FileInfoDetail;
import com.ca.arcserve.linuximaging.search.VolInfo;
import com.ca.arcserve.linuximaging.ui.client.restore.search.SearchService;
import com.ca.arcserve.linuximaging.ui.client.restore.search.model.SearchResultModel;
import com.ca.arcserve.linuximaging.ui.client.restore.search.model.VolResultModel;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceFactory;
import com.ca.arcserve.linuximaging.webservice.data.JobScript;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class SearchServiceImpl extends BaseServiceImpl implements SearchService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3789135950829293122L;
	private static BaseWebServiceClientProxy proxyClient = null;
	private static final int cachePages = 10;

	@Override
	public PagingLoadResult<SearchResultModel> search(PagingLoadConfig config,
			String machine, String path, String name, boolean bIncludeSub) {
		try {

			ARCserveSearchResult files = getClient().getSearchResult(name,
					path, machine, config.getLimit(), cachePages, bIncludeSub);
			PagingLoadResult<SearchResultModel> result = new BasePagingLoadResult<SearchResultModel>(
					getSearchModelResult(files.getResult()), 0,
					files.getTotalCount());
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<VolResultModel> getMachineList() {
		List<VolInfo> infos = getClient().getSearchVolList(null);
		return convertVolInfo(infos);
	}

	private List<VolResultModel> convertVolInfo(List<VolInfo> infos) {
		List<VolResultModel> volModelList = new ArrayList<VolResultModel>();

		if (infos != null) {
			for (VolInfo info : infos) {
				VolResultModel volModel = new VolResultModel();
				volModel.setHost(info.getHost());
				volModel.setGuid(info.getGuid());
				volModel.setServer(info.getRpsHost());
				volModel.setSessid(info.getSessid());
				volModelList.add(volModel);
			}
		}
		return volModelList;
	}

	private List<SearchResultModel> getSearchModelResult(
			List<FileInfoDetail> result) {
		List<SearchResultModel> searchModelList = new ArrayList<SearchResultModel>();
		for (FileInfoDetail fInfo : result) {
			SearchResultModel e = new SearchResultModel();
			e.setBackupLocation(fInfo.getBackupLocation());
			e.setMachine(fInfo.getRpsHost());
			e.setIsDir(fInfo.getIsDir());
			e.setSize(fInfo.getSize());
			// e.setFullName(getFullName(fInfo.getPath(), fInfo.getName()));
			e.setName(fInfo.getFullName());
			e.setSessionid(fInfo.getGUID());
			e.setModifyDate(new Date(fInfo.getDate()));
			e.setPoint(HomepageServiceImpl.convertRecoveryPoint(
					fInfo.getPoint(), JobScript.RESTORE_BMR,
					fInfo.getRpsHost()));
			searchModelList.add(e);
		}
		return searchModelList;
	}

	/*
	 * private String getFullName(String path, String name) { String fullName =
	 * path;
	 * 
	 * if (!fullName.endsWith(File.separator)) fullName += File.separator;
	 * 
	 * fullName += name; return fullName; }
	 */

	private ILinuximagingService getClient() {

		if (SearchServiceImpl.proxyClient == null) {
			synchronized (SearchServiceImpl.class) {
				if (SearchServiceImpl.proxyClient == null)
					SearchServiceImpl.proxyClient = new BaseWebServiceFactory()
							.getLinuxImagingWebService("http", "localhost",
									8014);
			}
		}

		BaseWebServiceClientProxy proxy1 = SearchServiceImpl.proxyClient;
		ILinuximagingService client = (ILinuximagingService) proxy1
				.getService();
		return client;
	}

	@Override
	public PagingLoadResult<SearchResultModel> getNextPage(
			PagingLoadConfig config) throws Exception {
		ILinuximagingService service = getClient();

		ARCserveSearchResult result = service.getNextPage(config.getOffset()
				/ config.getLimit());
		return new BasePagingLoadResult<SearchResultModel>(
				getSearchModelResult(result.getResult()), config.getOffset(),
				result.getTotalCount());
	}

}
