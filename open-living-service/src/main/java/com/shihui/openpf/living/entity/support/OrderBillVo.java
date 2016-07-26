package com.shihui.openpf.living.entity.support;

import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.living.entity.Bill;

public class OrderBillVo {
	
	String tempId;
	private Order order;
	private Bill bill;
	
	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public OrderBillVo() {
		order = new Order();
		bill = new Bill();
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}
	
	
}
