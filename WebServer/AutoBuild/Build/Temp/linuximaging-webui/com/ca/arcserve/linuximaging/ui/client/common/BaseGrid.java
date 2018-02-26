package com.ca.arcserve.linuximaging.ui.client.common;

/**
 * prevent scroll to top on refresh
 */

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public class BaseGrid<M extends ModelData> extends Grid<M> {


  /**
   * Creates a new grid.
   * 
   * @param store the data store
   * @param cm the column model
   */
  public BaseGrid(ListStore<M> store, ColumnModel cm) {
    super(store, cm);
    view = new BaseGridView();
  }

}
