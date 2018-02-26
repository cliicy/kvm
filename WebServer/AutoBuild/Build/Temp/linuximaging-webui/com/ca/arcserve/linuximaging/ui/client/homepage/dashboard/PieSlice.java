package com.ca.arcserve.linuximaging.ui.client.homepage.dashboard;

public class PieSlice {
	String statusName;
	Integer count; 
	String color;
	
	public PieSlice(String statusName, Integer count, String color) {
		this.statusName = statusName;
		this.count = count;
		this.color = color;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
}
