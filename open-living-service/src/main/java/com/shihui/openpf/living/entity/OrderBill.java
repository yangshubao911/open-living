package com.shihui.openpf.living.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

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
    private Long orderId;
    @Transient
    private String orderIdString;
    
	private Long userId;

	private String price;

	@JSONField(name = "payTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

	private Integer orderStatus;

	@Transient
	private String orderStatusMsg;
	
    //

	private String feeName;

	private String cityName;
	
	
	@JSONField(name="userNo")
	private String billKey;

	private String userAddress;

	private Integer billStatus;


	public String getOrderStatusMsg() {
		return orderStatusMsg;
	}

	public void setOrderStatusMsg(String orderStatusMsg) {
		this.orderStatusMsg = orderStatusMsg;
	}

	@JSONField(name="user_name")
	private String userName;


	//
	
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderIdString() {
		return orderIdString;
	}

	public void setOrderIdString(String orderIdString) {
		this.orderIdString = orderIdString;
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

}
