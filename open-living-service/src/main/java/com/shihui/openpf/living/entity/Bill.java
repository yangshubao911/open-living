package com.shihui.openpf.living.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhoutc on 2016/2/2.
 */
@Entity(name="bill")
public class Bill implements Serializable {

    private static final long serialVersionUID = -1L;

    @Id
    private Long orderId;
    private Integer serviceId;
	private Integer categoryId;
	private String feeName;
	private Integer cityId;
	private String cityName;
	private String userNo;
	private String billKeyType;
	private String billKey;
	private Integer companyId;
	private String userAddress;
	private String billDate;
	private Integer feeType;
	private Integer billStatus;
	private Date updateTime;

	//---
	@Column(name = "item1") private String item1;
	@Column(name = "item2") private String item2;
	@Column(name = "item3") private String item3;
	@Column(name = "item4") private String item4;
	@Column(name = "item5") private String item5;
	@Column(name = "item6") private String item6;
	@Column(name = "item7") private String item7;
//
	private String contractNo;
	private String userName;
	private String balance;
	private String payment;
//
	private String startTime;
	private String endTime;
//
	@Column(name = "field1") private String field1;
	@Column(name = "field2") private String field2;
	@Column(name = "field3") private String field3;
	@Column(name = "field4") private String field4;
	@Column(name = "field5") private String field5;	
//---
	private String serialNo;
	private String payTime;
//
	@Column(name = "bb_field1") private String bbField1;
	@Column(name = "bb_field2") private String bbField2;
	@Column(name = "bb_field3") private String bbField3;
	@Column(name = "bb_field4") private String bbField4;
//**
	private String bankBillNo;
	private String receiptNo;
//
	private String bankAcctDate;
	//

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

    public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getBillKeyType() {
		return billKeyType;
	}

	public void setBillKeyType(String billKeyType) {
		this.billKeyType = billKeyType;
	}

	public String getBillKey() {
		return billKey;
	}

	public void setBillKey(String billKey) {
		this.billKey = billKey;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}

	public Integer getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(Integer billStatus) {
		this.billStatus = billStatus;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getItem1() {
		return item1;
	}

	public void setItem1(String item1) {
		this.item1 = item1;
	}

	public String getItem2() {
		return item2;
	}

	public void setItem2(String item2) {
		this.item2 = item2;
	}

	public String getItem3() {
		return item3;
	}

	public void setItem3(String item3) {
		this.item3 = item3;
	}

	public String getItem4() {
		return item4;
	}

	public void setItem4(String item4) {
		this.item4 = item4;
	}

	public String getItem5() {
		return item5;
	}

	public void setItem5(String item5) {
		this.item5 = item5;
	}

	public String getItem6() {
		return item6;
	}

	public void setItem6(String item6) {
		this.item6 = item6;
	}

	public String getItem7() {
		return item7;
	}

	public void setItem7(String item7) {
		this.item7 = item7;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getBbField1() {
		return bbField1;
	}

	public void setBbField1(String bbField1) {
		this.bbField1 = bbField1;
	}

	public String getBbField2() {
		return bbField2;
	}

	public void setBbField2(String bbField2) {
		this.bbField2 = bbField2;
	}

	public String getBbField3() {
		return bbField3;
	}

	public void setBbField3(String bbField3) {
		this.bbField3 = bbField3;
	}

	public String getBbField4() {
		return bbField4;
	}

	public void setBbField4(String bbField4) {
		this.bbField4 = bbField4;
	}

	public String getBankBillNo() {
		return bankBillNo;
	}

	public void setBankBillNo(String bankBillNo) {
		this.bankBillNo = bankBillNo;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getBankAcctDate() {
		return bankAcctDate;
	}

	public void setBankAcctDate(String bankAcctDate) {
		this.bankAcctDate = bankAcctDate;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	
}
