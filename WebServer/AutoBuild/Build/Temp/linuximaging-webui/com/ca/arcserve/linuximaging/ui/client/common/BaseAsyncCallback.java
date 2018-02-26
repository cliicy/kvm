package com.ca.arcserve.linuximaging.ui.client.common;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.exception.BusinessLogicException;
import com.ca.arcserve.linuximaging.ui.client.exception.ServiceConnectException;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;

public class BaseAsyncCallback<T> implements AsyncCallback<T> {

	@Override
	public void onFailure(Throwable caught) {
		
		if (caught instanceof InvocationException){
			MessageBox messageBox = new MessageBox();
			messageBox.setTitle(UIContext.Constants.productName());
			messageBox.setMessage(UIContext.Constants.cantConnectToServer());
			messageBox.setIcon(MessageBox.ERROR);
			messageBox.setModal(true);
			messageBox.setMinWidth(400);
			messageBox.addCallback(new Listener<MessageBoxEvent>(){

				@Override
				public void handleEvent(MessageBoxEvent be) {
					Window.Location.reload();
				}
				
			});
			messageBox.show();
			return;
		}
		
		if (caught instanceof ServiceConnectException){
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,((ServiceConnectException)caught).getDisplayMessage());
			return;
		}
		
		if(caught instanceof BusinessLogicException){
			BusinessLogicException ex=(BusinessLogicException)caught;
			if (ex.getErrorCode().equals(String.valueOf(0x0000000100000000L + 15))) {//data base error
				Utils.showMessage(UIContext.productName, MessageBox.ERROR, UIContext.Constants.databaseBusy());
			} else if(ex.getErrorCode().equals(String.valueOf(0x0000000100000000L + 6))){// web service session timeout
				Window.Location.reload();
			} else {
				Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,ex.getDisplayMessage());
			}
		}else{
			Utils.showMessage(UIContext.Constants.productName(),MessageBox.ERROR,caught.getMessage());
		}
	}

	@Override
	public void onSuccess(T result) {
		
	}

}
