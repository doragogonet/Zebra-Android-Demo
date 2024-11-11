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
	private int filterMemoryBankSelection;
	private int filterActionSelection;
	private int filterTargetSelection;

	public int getFilterMemoryBankSelection() {
		return filterMemoryBankSelection;
	}

	public void setFilterMemoryBankSelection(int filterMemoryBankSelection) {
		this.filterMemoryBankSelection = filterMemoryBankSelection;
	}

	public int getFilterActionSelection() {
		return filterActionSelection;
	}

	public void setFilterActionSelection(int filterActionSelection) {
		this.filterActionSelection = filterActionSelection;
	}

	public int getFilterTargetSelection() {
		return filterTargetSelection;
	}

	public void setFilterTargetSelection(int filterTargetSelection) {
		this.filterTargetSelection = filterTargetSelection;
	}

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
