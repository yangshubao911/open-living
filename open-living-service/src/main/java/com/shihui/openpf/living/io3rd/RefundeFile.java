package com.shihui.openpf.living.io3rd;

import java.util.ArrayList;

import com.shihui.openpf.living.io3rd.RefundeItem;

public class RefundeFile {
	private int totalMoney;
	private int totalAmount;
	private ArrayList<RefundeItem> refundeList;
	//
	public RefundeFile() {
		
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
	public ArrayList<RefundeItem> getRefundeList() {
		return refundeList;
	}
	public void setRefundeList(ArrayList<RefundeItem> refundeList) {
		this.refundeList = refundeList;
	}
}
