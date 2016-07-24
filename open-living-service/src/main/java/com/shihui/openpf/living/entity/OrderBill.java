package com.shihui.openpf.living.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhoutc on 2016/2/2.
 */
@Entity
public class OrderBill implements Serializable {

	private static final long serialVersionUID = -1L;
	
    @Id
    @JSONField(name="order_id")
    private Long orderId;
    
	@JSONField(name = "user_id")
	private Long userId;

	private String price;

    @JSONField(name = "pay_time")
    private Date payTime;

	@JSONField(name = "order_status")
	private Integer orderStatus;

    //

	@JSONField(name="fee_name")
	private String feeName;

	@JSONField(name="city_name")
	private String cityName;
	
	
	@JSONField(name="bill_key")
	private String billKey;

	@JSONField(name="user_address")
	private String userAddress;

	@JSONField(name="bill_status")
	private Integer billStatus;

	@JSONField(name="user_name")
	private String userName;

	@JSONField(name="user_no")
	private String userNo;

	//
	
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getBillKey() {
		return billKey;
	}

	public void setBillKey(String billKey) {
		this.billKey = billKey;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public Integer getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(Integer billStatus) {
		this.billStatus = billStatus;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
    public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

}
