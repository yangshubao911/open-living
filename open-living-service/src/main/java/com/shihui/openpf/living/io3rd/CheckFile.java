package com.shihui.openpf.living.io3rd;

import java.util.ArrayList;

import com.shihui.openpf.living.io3rd.CheckItem;
/*
 * 对账文件
 */
public class CheckFile {
	private String bjceg;
	private int totalMoney;
	private int totalAmount;
	private int successMoney;
	private int successAmount;
	private ArrayList<CheckItem> checkList;
	//
	public CheckFile() {
		
	}
	public String getBjceg() {
		return bjceg;
	}
	public void setBjceg(String bjceg) {
		this.bjceg = bjceg;
	}
	public int getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(int totalMoney) {
		this.totalMoney = totalMoney;
	}
	public int getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	public int getSuccessMoney() {
		return successMoney;
	}
	public void setSuccessMoney(int successMoney) {
		this.successMoney = successMoney;
	}
	public int getSuccessAmount() {
		return successAmount;
	}
	public void setSuccessAmount(int successAmount) {
		this.successAmount = successAmount;
	}
	public ArrayList<CheckItem> getCheckList() {
		return checkList;
	}
	public void setCheckList(ArrayList<CheckItem> checkList) {
		this.checkList = checkList;
	}
}
