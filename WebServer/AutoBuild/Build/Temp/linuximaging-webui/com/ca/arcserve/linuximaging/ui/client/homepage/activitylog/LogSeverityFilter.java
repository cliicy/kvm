package com.ca.arcserve.linuximaging.ui.client.homepage.activitylog;

import java.util.EnumMap;
import java.util.Map;

import com.ca.arcserve.linuximaging.ui.client.UIContext;
import com.ca.arcserve.linuximaging.ui.client.common.TooltipSimpleComboBox;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class LogSeverityFilter extends Composite {
	
	public static final Map<Severity, String> SEVERITY_RESOURCES;
	
	static {
		SEVERITY_RESOURCES = new EnumMap<Severity, String>(Severity.class);
		SEVERITY_RESOURCES.put(Severity.All, UIContext.Constants.all());
		SEVERITY_RESOURCES.put(Severity.Information, UIContext.Constants.information());
		SEVERITY_RESOURCES.put(Severity.Error, UIContext.Constants.error());
		SEVERITY_RESOURCES.put(Severity.Warning, UIContext.Constants.warning());
		SEVERITY_RESOURCES.put(Severity.ErrorAndWarning, UIContext.Constants.errorsAndWarnings());
	}
	
	private TooltipSimpleComboBox<String> severityBox;
	
	public LogSeverityFilter() {
		this(Severity.All);
	}
	
	public LogSeverityFilter(Severity defaultSeverity) {
		severityBox = new TooltipSimpleComboBox<String>();		
		severityBox.ensureDebugId("aca720c7-7167-4e96-b987-3dc54def7721");
		for (Severity severity : Severity.values()) {
			if (SEVERITY_RESOURCES.containsKey(severity)) {
				severityBox.add(SEVERITY_RESOURCES.get(severity));
			}
		}
		
		severityBox.setTriggerAction(ComboBox.TriggerAction.ALL);
		severityBox.setEditable(false);
		severityBox.setSimpleValue(SEVERITY_RESOURCES.get(defaultSeverity));
				
		this.initComponent(severityBox);
	}
	
	public Integer getType() {
		int index = severityBox.getSelectedIndex();
		Severity selectedSeverity = Severity.values()[index];
		return selectedSeverity.getValue();
	}
	
	public void reSetType() {
		severityBox.setSimpleValue(SEVERITY_RESOURCES.get(Severity.All));
	}
	
	public void addKeyListener(KeyListener lstn) {
		if ( severityBox != null && lstn != null ) {
			severityBox.addKeyListener(lstn);
		}
	}
}