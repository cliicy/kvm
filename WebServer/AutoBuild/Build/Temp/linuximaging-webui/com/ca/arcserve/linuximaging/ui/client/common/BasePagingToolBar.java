package com.ca.arcserve.linuximaging.ui.client.common;

import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class BasePagingToolBar extends PagingToolBar {

	public BasePagingToolBar(int pageSize) {
		super(pageSize);
		// TODO Auto-generated constructor stub
	}


	private boolean loadMask = true;
	private boolean savedEnableState;
	
	public boolean isNoMask() {
		return loadMask;
	}

	
	public boolean isLoadMask() {
		return loadMask;
	}


	public void setLoadMask(boolean loadMask) {
		this.loadMask = loadMask;
	}


	@Override
	public void bind(PagingLoader<?> loader) {
	    if (this.loader != null) {
	      this.loader.removeLoadListener(loadListener);
	    }
	    this.loader = loader;
	    if (loader != null) {
	      loader.setLimit(pageSize);
	      if (loadListener == null) {
	        loadListener = new LoadListener() {
	          public void loaderBeforeLoad(final LoadEvent le) {
	        	 if ( loadMask ) {
		            savedEnableState = isEnabled();
		            setEnabled(false);
		            refresh.setIcon(IconHelper.createStyle("x-tbar-loading"));
	        	 }
	            DeferredCommand.addCommand(new Command() {
	              public void execute() {
	                if (le.isCancelled()) {
	                  refresh.setIcon(getImages().getRefresh());
	                  if ( loadMask ) {
	                	  setEnabled(savedEnableState);
	                  }
	                }
	              }
	            });
	          }

	          public void loaderLoad(LoadEvent le) {
	            refresh.setIcon(getImages().getRefresh());
	            if ( loadMask ) {
	            	setEnabled(savedEnableState);
	            }
	            onLoad(le);
	          }

	          public void loaderLoadException(LoadEvent le) {
	            refresh.setIcon(getImages().getRefresh());
	            if ( loadMask ) {
	            	setEnabled(savedEnableState);
	            }
	          }
	        };
	      }
	      loader.addLoadListener(loadListener);
	    }
	  }
}