package com.ca.arcserve.linuximaging.ui.client.restore.search;

import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.restore.search.model.SearchResultModel;
import com.ca.arcserve.linuximaging.ui.client.restore.search.model.VolResultModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchServiceAsync {

	void search(PagingLoadConfig config, String machine, String path,
			String name, boolean bIncludeSub,
			AsyncCallback<PagingLoadResult<SearchResultModel>> callback);

	void getNextPage(PagingLoadConfig config,
			AsyncCallback<PagingLoadResult<SearchResultModel>> callback);

	void getMachineList(AsyncCallback<List<VolResultModel>> callback);
}
