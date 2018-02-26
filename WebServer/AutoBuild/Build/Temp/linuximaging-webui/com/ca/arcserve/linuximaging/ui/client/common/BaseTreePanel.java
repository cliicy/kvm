package com.ca.arcserve.linuximaging.ui.client.common;

import com.ca.arcserve.linuximaging.ui.client.model.CatalogModelType;
import com.ca.arcserve.linuximaging.ui.client.model.FileModel;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class BaseTreePanel extends TreePanel<FileModel> {
	
	public BaseTreePanel(TreeStore<FileModel> store) {
		super(store);
		view = new BaseTreePanelView();
		view.bind(this, store);
	}	
	
	public TreeNode getNode(FileModel model){
		return this.findNode(model);
	}	

	@Override
	public void refresh(FileModel model) {		
		super.refresh(model);
	}
	
	protected String getText(FileModel model) {
		String text = model.getName();
		text = text.replace(" ", "&nbsp;");
		return text;
	}
	
	@Override
	protected AbstractImagePrototype calculateIconStyle(FileModel model) {
		if(model != null)
		{
			switch(model.getType())
			{
			case CatalogModelType.File:
				return IconHelper.createStyle("file-icon");
			case CatalogModelType.Folder:
				return IconHelper.createStyle("folder-icon");
			}
		}
		return super.calculateIconStyle(model);
	};

}
