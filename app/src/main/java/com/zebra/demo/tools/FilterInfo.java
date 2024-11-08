package com.zebra.demo.tools;
import java.io.Serializable;

public class FilterInfo implements Serializable {

	 private String filterNumber = "";
     private String filterData = "";
     private String filterMemoryBank = "";
     private String filterOffset = "";
     private String filterLength = "";
     private String filterAction = "";
     private String filterTarget = "";

	public String getFilterNumber() {
		return filterNumber;
	}

	public void setFilterNumber(String filterNumber) {
		this.filterNumber = filterNumber;
	}

	public String getFilterData() {
		return filterData;
	}

	public void setFilterData(String filterData) {
		this.filterData = filterData;
	}

	public String getFilterMemoryBank() {
		return filterMemoryBank;
	}

	public void setFilterMemoryBank(String filterMemoryBank) {
		this.filterMemoryBank = filterMemoryBank;
	}

	public String getFilterOffset() {
		return filterOffset;
	}

	public void setFilterOffset(String filterOffset) {
		this.filterOffset = filterOffset;
	}

	public String getFilterLength() {
		return filterLength;
	}

	public void setFilterLength(String filterLength) {
		this.filterLength = filterLength;
	}

	public String getFilterAction() {
		return filterAction;
	}

	public void setFilterAction(String filterAction) {
		this.filterAction = filterAction;
	}

	public String getFilterTarget() {
		return filterTarget;
	}

	public void setFilterTarget(String filterTarget) {
		this.filterTarget = filterTarget;
	}
}
