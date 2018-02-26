package com.ca.arcserve.linuximaging.ui.client.restore.search;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.restore.search.model.SearchResultModel;
import com.ca.arcserve.linuximaging.ui.client.restore.search.model.VolResultModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService {

	PagingLoadResult<SearchResultModel> search(PagingLoadConfig config, String machine, String path, String name,
			boolean bIncludeSub);

	PagingLoadResult<SearchResultModel> getNextPage(PagingLoadConfig config) throws Exception;

	List<VolResultModel> getMachineList();

}
