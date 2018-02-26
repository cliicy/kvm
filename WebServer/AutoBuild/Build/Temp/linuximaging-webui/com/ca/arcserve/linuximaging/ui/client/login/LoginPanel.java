package com.ca.arcserve.linuximaging.ui.client.login;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

public class LoginPanel extends LayoutContainer {
	
	LoginWindow window = new LoginWindow();
	
	public void render(Element target, int index) {
		super.render(target, index);
		this.setStyleAttribute("padding", "0px");
		this.setStyleName("login_container");
		
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		this.setLayout(layout);
		
		TableData td = new TableData();
		td.setWidth("100%");
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		
		LayoutContainer container = new LayoutContainer();
		AbsoluteLayout absLayout = new AbsoluteLayout();
		container.setLayout(absLayout);
		container.setHeight(750);
		container.setWidth(937);

		
		Image image = new Image(UIContext.IconBundle.agentLoginHeader());
		image.setStyleName("login_productName_container");
		container.add(image);
		
		window.setStyleName("login_form_container");
		container.add(window);
		this.add(container, td);
		
		//
		/*StringBuffer htmlString = new StringBuffer();
		//htmlString.append("<TABLE WIDTH=1000><TR><TD><HR></TD></TR>");
//		htmlString.append("<TR><TD class=\"copyright\">");
		htmlString.append("<span class=\"copyright\">"+UIContext.Constants.aboutWindowCopyRight()+UIContext.Constants.aboutWindowCopyRight2()+"</span>");
//		htmlString.append("</TD></TR></TABLE>");
		Html htmlControl = new Html(htmlString.toString());			
		
//		td = new TableData();
//		td.setWidth("100%");		
//		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		
		//this.add(htmlControl, td);
		container.add(htmlControl);
		htmlControl.setStyleAttribute("position", "absolute");
		htmlControl.setStyleAttribute("left", "183px");
		htmlControl.setStyleAttribute("top", "533px");
		htmlControl.setStyleAttribute("top", "533px");		*/
		
	}
	
	public LoginWindow getLoginWindow()
	{
		return window;				
	}
	
}
