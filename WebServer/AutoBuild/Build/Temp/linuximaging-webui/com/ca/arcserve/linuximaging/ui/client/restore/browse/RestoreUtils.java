package com.ca.arcserve.linuximaging.ui.client.restore.browse;

import com.ca.arcserve.linuximaging.ui.client.model.CatalogModelType;
import com.ca.arcserve.linuximaging.ui.client.model.GridTreeNode;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.button.IconButton;

public class RestoreUtils {
	
	public static StoreSorter<GridTreeNode> initStoreSorter() {
		StoreSorter<GridTreeNode> sorter = new StoreSorter<GridTreeNode>(){
			private int fileNameCompare( GridTreeNode m1, GridTreeNode m2 )
			{
				int r = 0;
				if( m1.getDisplayName() == null )
				{
					if( m2.getDisplayName() == null )
						return 0;
					else
						return -1;
				}
				else
				{
					if( m2.getDisplayName() == null )
						return 1;
					else
					{
						r = m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName());
						if( r == 0 )
							r = m1.getDisplayName().compareTo(m2.getDisplayName());
						return r;
					}
				}
			}
			public int compare(Store<GridTreeNode> store, GridTreeNode m1, GridTreeNode m2, String property) {
				if(m1.getType() != null && m2.getType() != null && !m1.getType().equals(m2.getType()))
				{
					return m1.getType().compareTo( m2.getType());
				}
				else if( property == null ){
					return m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName());	
				}
				else if( property == "displayName" ){
					return fileNameCompare(m1, m2);
				}
				else if( property == "date" )
				{
					if( m1.getDate() == null)
					{
						if( m2.getDate() == null )
							return fileNameCompare(m1,m2);
						else
							return -1;
					}
					else
					{
						if( m2.getDate() == null )
							return 1;
						else
							return m1.getDate().compareTo(m2.getDate());
					}
				}
				else if( property == "size" ){
					if( m1.getSize() == null )
					{
						if( m2.getSize() == null )
							return fileNameCompare(m1,m2);
						else
							return -1;
					}
					else
					{
						if(m2.getSize() == null)
							return 1;
						else
						{
							if( m1.getSize() == m2.getSize() )
							{
								return fileNameCompare(m1, m2);
							}
							else if( m1.getSize() < m2.getSize() )
								return -1;
							else
								return 1;
						}
					}
				}
				else
					return super.compare(store, m1, m2, property);
			}
		};
		return sorter;
	}
	
	public static IconButton getNodeIcon(GridTreeNode node){
		
		if(node == null)
			return null;
		
		IconButton image = null;
		int nodeType = node.getType();
		switch (nodeType) {
			case CatalogModelType.Folder:
				image = new IconButton("folder-icon");
				break;
			case CatalogModelType.File:
				image = new IconButton("file-icon");
				break;
			case CatalogModelType.Link:
				image = new IconButton("exchange_node_icon");
				break;
			default:
				break;
		}
		if(image != null){
			image.setWidth(20);
			image.setStyleAttribute("font-size", "0");
		}
		
		return image;
	
	}

}
