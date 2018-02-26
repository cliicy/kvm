package com.ca.arcserve.linuximaging.ui.client.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GridTreeNode extends BaseModelData
{

	private static final long serialVersionUID = -3037512242125232582L;
	public static final int NONE = 0;
	public static final int PARTIAL = 1;
	public static final int FULL = 2;
    /**
     * to save guid for system reserved volume Robin
     * Fix issue 18906917
     * @return
     */
	
    public String getGuid(){
          return get("GUID");
    }
    public void setGuid(String guid){
           set("GUID",guid);
    }

	public Boolean getChecked()
	{
		return (Boolean) get("checked");
	}
	public void setChecked(Boolean check)
	{
		set("checked", check);
	}
	
	public String getName()
	{
		return (String)get("name");
	}
	public void setName(String name)
	{
		set("name", name);
	}
	
	public Long getSize()
	{
		return (Long)get("size");
	}
	
	public void setSize(Long size)
	{
		set("size", size);
	}
	
	public Date getDate()
	{
		return (Date)get("date");			
	}
	public void setDate(Date date)
	{
		set("date", date);
	}
	
	public Long getServerTZOffset() {
		return (Long)get("ServerTZOffset");
	}
	
	public void setServerTZOffset(Long offset) {
		set("ServerTZOffset", offset);
	}
	
	//
	public String getCatalogFilePath()
	{
		return (String)get("catalogFilePath");		
	}
	public void setCatalogFilePath(String path)
	{
		set("catalogFilePath", path);
	}
	
	public Long getParentID()
	{
		return (Long)get("parentID");		
	}
	public void setParentID(Long parentID)
	{
		set("parentID", parentID);
	}
	public Integer getType() {
		return (Integer) get("type");
	}

	public void setType(Integer type) {
		set("type", type);
	}
	
	
	//
	public Integer getSubSessionID() {
		return (Integer)get("subSessionID");
	}
	public void setSubSessionID(Integer subSessionID) {
		set("subSessionID", subSessionID);
	}
	
	public String getPath()
	{
		return (String) get("path");
	}
	public void setPath(String path)
	{
		set("path", path);
	}
	
	public String getFullPath()
	{
		return (String) get("fullPath");
	}
	public void setFullPath(String fullPath)
	{
		set("fullPath", fullPath);
	}
	
	public Boolean getPackage()
	{
		return (Boolean) get("package");
	}
	public void setPackage(Boolean b)
	{
		set("package", b);
	}
	
	public Boolean getSelectable()
	{
		return (Boolean) get("selectable");
	}
	public void setSelectable(Boolean selectable)
	{
		set("selectable", selectable);
	}
	
	public String getComponentName()
	{
		return (String)get("name");
	}
	public void setComponentName(String name)
	{
		set("name", name);
	}
	public void setDisplayName(String componentName) {
		set("displayName", componentName);		
	}
	public String getDisplayName()
	{
		return get("displayName");
	}

	public void setUserChecked(Boolean value) {
		set("userChecked", value);
	}

	public Boolean isUserChecked() {
		return get("userChecked");
	}

	public Long getChildrenCount() {
		return (Long) get("childrenCount");
	}

	public void setChildrenCount(Long childrenCount) {
		set("childrenCount", childrenCount);
	}
	
	public Long getChildrenFolderCount() {
		return (Long) get("childrenFolderCount");
	}

	public void setChildrenFolderCount(Long childrenFolderCount) {
		set("childrenFolderCount", childrenFolderCount);
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (obj instanceof GridTreeNode) {
			GridTreeNode another = (GridTreeNode) obj;
			boolean isEqual = this.toId().equalsIgnoreCase(another.toId());

			return isEqual;
		} else {
			return false;
		}
	}

	public String toId() {
		String id = "";
		if (this.getParentID() != null) {
			id += "@" + this.getParentID();
		}
		if (this.getSubSessionID() != null) {
			id += "@" + this.getSubSessionID();
		} else {
			id += "@" + 0;
		}
		if (this.getGuid() != null) {
			id += "@" + this.getGuid();
		}

		if (this.getCatalogFilePath() != null) {
			id += "@" + this.getCatalogFilePath();
		}

		return id.toString();
	}

	private Integer id = null;

	public void setId(Integer id) {
		this.id = id;
	}

	private int getId() {
		if (id == null) {
			id = this.toId().hashCode();
		}
		return id;
	}

	private String grtCatalogFile; // alternative catalog file path for GRT
	private Long sessionID;
	private String backupDestination;

	public String getGrtCatalogFile() {
		return grtCatalogFile;
	}

	public void setGrtCatalogFile(String grtCatalogFile) {
		this.grtCatalogFile = grtCatalogFile;
	}

	public Long getSessionID() {
		return sessionID;
	}

	public void setSessionID(Long sessionID) {
		this.sessionID = sessionID;
	}

	public String getBackupDestination() {
		return backupDestination;
	}

	public void setBackupDestination(String backupDestination) {
		this.backupDestination = backupDestination;
	}

	private List<GridTreeNode> referNode = new LinkedList<GridTreeNode>();

	public List<GridTreeNode> getReferNode() {
		return referNode;
	}
	public void setReferNode(List<GridTreeNode> referNode) {
		this.referNode = referNode;
	}
	
	// display path is the readable path instead of the internal GUID
	public String getDisplayPath()
	{
		return (String) get("displayPath");
	}
	public void setDisplayPath(String displayPath)
	{
		set("displayPath", displayPath);
	}
	
	public Integer getSelectState(){
		return get("selectState");
	}
	
	public void setSelectState(Integer selectState){
		set("selectState",selectState);
	}
}
