package com.ca.arcserve.linuximaging.ui.client.homepage.activitylog;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.BaseAsyncCallback;
import com.ca.arcserve.linuximaging.ui.client.exception.BusinessLogicException;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageService;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcserve.linuximaging.ui.client.homepage.HomepageTab;
import com.ca.arcserve.linuximaging.ui.client.model.ActivityLogModel;
import com.ca.arcserve.linuximaging.ui.client.model.LogFilterModel;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ActivityLogTable extends LayoutContainer {

	private static final HomepageServiceAsync service = GWT.create(HomepageService.class);
	
	private ListStore<ActivityLogModel> store;
	private Grid<ActivityLogModel> grid;
	private ColumnModel columnModel;
	private PagingToolBar toolBar;
	private HomepageTab parentTabPanel;
	private ActivityLogFilterBar filterBar;
	
	public static int PAGE_LIMIT = 50;
	
	
	public ActivityLogTable(HomepageTab parent)
	{
		parentTabPanel = parent;
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);	    
	}
	
	@Override
	protected void onRender(Element parent, int pos){
		super.onRender(parent, pos);
		RpcProxy<PagingLoadResult<ActivityLogModel>> proxy = new RpcProxy<PagingLoadResult<ActivityLogModel>>() {   
			@Override
			protected void load(Object loadConfig,
					final AsyncCallback<PagingLoadResult<ActivityLogModel>> callback) {
				if (!(loadConfig instanceof PagingLoadConfig))
				{
					return;
				}
				
				PagingLoadConfig pagingLoadConfig = (PagingLoadConfig)loadConfig;
				LogFilterModel filterModel = filterBar.getFilter();
				service.getLogList(parentTabPanel.currentServer, pagingLoadConfig, filterModel, new BaseAsyncCallback<PagingLoadResult<ActivityLogModel>>() {
					
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						grid.unmask();
						callback.onFailure(caught);
						if( caught instanceof BusinessLogicException){
							BusinessLogicException e = (BusinessLogicException)caught;
							String errorCode = e.getErrorCode();
							if(errorCode.equals(String.valueOf(0x0000000100000000L+7))){
								return;
							}
						}
						parentTabPanel.changeContent(false);
					}
					
					@Override
					public void onSuccess(PagingLoadResult<ActivityLogModel> result) {
						parentTabPanel.changeContent(true);
						callback.onSuccess(result);
					}
				});
			}   
	      };   
	    
	      // loader   
	      final PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(   
	          proxy);   
	      loader.setRemoteSort(true);
	      toolBar = new PagingToolBar(PAGE_LIMIT){
	    	  @Override
	    	  protected void onRender(Element target, int index) {
	    		  super.onRender(target, index);
	    	        
	    	        ToolTipConfig removeConfig = null;

	    		    if (!showToolTips){
	    		    	first.setToolTip(removeConfig);
	    		    }
	    		    if (!showToolTips){
	    		    	prev.setToolTip(removeConfig);
	    		    }
	    		    if (!showToolTips){
	    		    	next.setToolTip(removeConfig);
	    		    }
	    		    if (!showToolTips){
	    		    	last.setToolTip(removeConfig);
	    		    }
	    		    if (!showToolTips){
	    		    	refresh.setToolTip(removeConfig);
	    		    }
	    	  }
	    	  
	    	  public void setActivePage(int page) {
	    		  	if (totalLength == -1) {
	    		  		if (page != activePage && page > 0) {
	  	    		      doLoadRequest(--page * pageSize, pageSize);
	  	    		    } else {
	  	    		      pageText.setText(String.valueOf((int) activePage));
	  	    		    }
	    		  		return;
	    		  	}
	    		  	
	    		    if (page > pages) {
	    		      last();
	    		      return;
	    		    }
	    		    if (page != activePage && page > 0 && page <= pages) {
	    		      doLoadRequest(--page * pageSize, pageSize);
	    		    } else {
	    		      pageText.setText(String.valueOf((int) activePage));
	    		    }
	    		  }
	    	  
	    	  @Override
	    	  protected void onLoad(LoadEvent event) {
	    		    super.onLoad(event);
	    		    PagingLoadResult<?> result = event.getData();
	    		    config = (PagingLoadConfig) event.getConfig();
	    		    List<?> data = result.getData();
	    		    start = result.getOffset();
	    		    totalLength = result.getTotalLength();
	    		    activePage = (int) Math.ceil((double) (start + pageSize) / pageSize);

	    		    int lastCount = 0;
	    		    if ( totalLength == -1 && data != null && data.size() == pageSize ) {
	    		    	pages = activePage + 1;
	    		    	lastCount = data.size();
	    		    } else if ( totalLength == -1 ) {
	    		    		if ( data != null ) {
	    		    			lastCount = data.size();
	    		    		} else {
	    		    			lastCount = 0;
	    		    		}
	    		    		pages = activePage;
		    		} else {
	    		    	pages = totalLength < pageSize ? 1 : (int) Math.ceil((double) totalLength / pageSize);
	    		    }

	    		    if (activePage > pages && totalLength > 0) {
	    		      last();
	    		      return;
	    		    } else if (activePage > pages) {
	    		      start = 0;
	    		      activePage = 1;
	    		    }
	    		    
	    		    pageText.setText(String.valueOf((int) activePage));

	    		    String after = null, display = null;
	    		    if ( totalLength == -1 ) {
	    		    	  after = "";
	    		    } else {
		    		    if (msgs.getAfterPageText() != null) {
		    		      after = Format.substitute(msgs.getAfterPageText(), "" + pages);
		    		    } else {
		    		      after = GXT.MESSAGES.pagingToolBar_afterPageText(pages);
		    		    }
	    		    }

	    		    afterText.setLabel(after);

	    		    first.setEnabled(activePage != 1);
	    		    prev.setEnabled(activePage != 1);
	    		    next.setEnabled(activePage != pages);
	    		    if ( totalLength == -1 ) {	    		    	
	    		    	last.setEnabled(false);
	    		    } else {
	    		    	last.setEnabled(activePage != pages);
	    		    }

	    		    int temp = 0;
	    		    if (totalLength == -1) {
	    		    	temp = activePage == pages ? (pages-1)*pageSize+lastCount : start + pageSize;	    		    	
		    		    display = UIContext.Messages.pagingToolBar_displayMsg(start + 1, temp);
	    		    } else {
	    		    	temp = activePage == pages ? totalLength : start + pageSize;
	    		    	if (msgs.getDisplayMsg() != null) {
		    		        String[] params = new String[] {"" + (start + 1), "" + temp, "" + totalLength};
		    		        display = Format.substitute(msgs.getDisplayMsg(), (Object[]) params);
		    		    } else {
		    		        display = GXT.MESSAGES.pagingToolBar_displayMsg(start + 1, (int) temp, (int) totalLength);
		    		    }
	    		    }
	
	    		    String msg = display;
	    		    if (totalLength == 0) {
	    		      msg = msgs.getEmptyMsg();
	    		    }
	    		    displayText.setLabel(msg);
	    		  }
	      };
	      toolBar.setAlignment(HorizontalAlignment.RIGHT);
	      toolBar.setStyleAttribute("background-color","white");
	      toolBar.setShowToolTips(false);
	      toolBar.bind(loader);
	      
	      store = new ListStore<ActivityLogModel>(loader);
	    
	      List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
	      for (ActivityLogColumn column : ActivityLogColumn.values())
	      {
	    	  columns.add(column.getColumnConfig());
	      }
	      
	      columnModel = new ColumnModel(columns);
	      grid = new Grid<ActivityLogModel>(store, columnModel);   
	      grid.setStateId("pagingActivityLogTable");   
	      grid.setStateful(true);   
	      /*grid.addListener(Events.Attach, new Listener<GridEvent<ActivityLogModel>>() {   
	        public void handleEvent(GridEvent<ActivityLogModel> be) {
	          loader.load(0, PAGE_LIMIT);
	        }   
	      });   */
	      grid.setLoadMask(true);   
	      grid.setBorders(true);   
	      grid.setAutoExpandColumn("message");
	      grid.setAutoExpandMax(1000);
	      grid.setAutoExpandMin(300);
	      
	      BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH);
	      data.setSize(30);
	      filterBar = new ActivityLogFilterBar(this);
	      filterBar.hide();
	      this.add(filterBar, data);
	      
	      data = new BorderLayoutData(LayoutRegion.CENTER);
	      this.add(grid, data);
	      
	      data = new BorderLayoutData(LayoutRegion.SOUTH);
	      data.setSize(30);
	      this.add(toolBar,data);
	}
	
	public void refreshTable()
	{
		toolBar.first();
	}
	
	public void resetFilter() {
		if(filterBar != null)
			filterBar.resetFilter();
	}
	
	public void showHideFilter() {
		if ( filterBar == null ) return;
		
		if ( filterBar.isVisible() ) {
			filterBar.hide();
		} else {
			filterBar.show();
		}
	}
	
	public HomepageTab getParentTabPanel() {
		return parentTabPanel;
	}
	
	public void resetData(){
		if(grid!=null)
			grid.getStore().removeAll();
	}
	
	public void filterByNodeName(String nodeName){
		filterBar.show();
		filterBar.filterByNodename(nodeName);
	}
	
	public void filterByNodeNameAndJobName(String nodeName,String jobName){
		filterBar.show();
		filterBar.filterByNodeNameAndJobName(nodeName,jobName);
	}
	
	public void filterByJobId(String jobId){
		filterBar.show();
		filterBar.filterByJobId(jobId);
	}
}
