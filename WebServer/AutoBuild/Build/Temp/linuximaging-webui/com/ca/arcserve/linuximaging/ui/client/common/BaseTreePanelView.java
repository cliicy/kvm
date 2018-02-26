package com.ca.arcserve.linuximaging.ui.client.common;

import com.ca.arcserve.linuximaging.ui.client.model.FileModel;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelView;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.user.client.Element;

public class BaseTreePanelView extends TreePanelView<FileModel> {
	@SuppressWarnings("rawtypes")
	public void onTextChange(TreeNode node, String text) {
		Element textEl = getTextElement(node);
		if (textEl != null) {
			if (GXT.isIE) {
				text = text.replace("&nbsp;", " ");
				textEl.setInnerText(Util.isEmptyString(text) ? "&#160;" : text);
			} else {
				text = text.replace(" ", "&nbsp;");
				textEl.setInnerHTML(Util.isEmptyString(text) ? "&#160;" : text);
			}
		}
	}
}
