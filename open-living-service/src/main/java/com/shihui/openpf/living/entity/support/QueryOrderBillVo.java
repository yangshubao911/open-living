package com.shihui.openpf.living.entity.support;

import com.shihui.openpf.common.model.Campaign;
import com.shihui.openpf.common.model.Group;
import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Company;
import com.shihui.openpf.living.entity.Goods;
import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.common.model.Merchant;
import com.shihui.openpf.common.model.Service;

public class QueryOrderBillVo {
	
	private String tempId;
//	private int categoryId;
//	private String companyNo;
	private String shGold;
	private Integer merchantId;
	private String ErrorCode;

	private Order order;
	private Bill bill;
	private Company company;
	private Goods goods;
	private Campaign campaign;
	private Merchant merchant;
	private Group group;// = groupManage.getGroupInfoByGid(order.getGroupId());
	private Service service;
	//

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Company getCompany() {
		return company;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public QueryOrderBillVo() {
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
	
//	public int getCategoryId() {
//		return categoryId;
//	}
//
//	public void setCategoryId(int categoryId) {
//		this.categoryId = categoryId;
//	}

//	public String getCompanyNo() {
//		return companyNo;
//	}
//
//	public void setCompanyNo(String companyNo) {
//		this.companyNo = companyNo;
//	}

	public String getShGold() {
		return shGold;
	}

	public void setShGold(String shGold) {
		this.shGold = shGold;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}

}
