package com.shihui.openpf.living.entity.support;

import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Company;
import com.shihui.openpf.living.entity.Order;

/*
 * 订单内容的ＶＯ，用于下单与缴费
 */
public class OrderBillVo {
	
	private Order order;
	private Bill bill;
	private Company company;
	//

	public OrderBillVo() {
//		order = new Order();
//		bill = new Bill();
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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
