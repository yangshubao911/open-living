package com.shihui.openpf.living.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhoutc on 2016/2/2.
 */
@Entity
public class OrderBillTop5{
	
    @Id
    private Long orderId;
	private Integer userId;
	private Integer gid;
	private Integer mid;
	private Integer serviceId;
	private Integer goodsId;
	private Integer goodsVersion;
	
	private Integer categoryId;
	private String feeName;
	private Integer cityId;
	private Integer companyId;
	private String companyNo;
	@JSONField(name="userNo")
	private String billKey;
	private String billKeyType;
	
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getGid() {
		return gid;
	}
	public void setGid(Integer gid) {
		this.gid = gid;
	}
	public Integer getMid() {
		return mid;
	}
	public void setMid(Integer mid) {
		this.mid = mid;
	}
	public Integer getServiceId() {
		return serviceId;
	}
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	public Integer getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}
	public Integer getGoodsVersion() {
		return goodsVersion;
	}
	public void setGoodsVersion(Integer goodsVersion) {
		this.goodsVersion = goodsVersion;
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
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public String getCompanyNo() {
		return companyNo;
	}
	public void setCompanyNo(String companyNo) {
		this.companyNo = companyNo;
	}
	public String getBillKey() {
		return billKey;
	}
	public void setBillKey(String billKey) {
		this.billKey = billKey;
	}
	public String getBillKeyType() {
		return billKeyType;
	}
	public void setBillKeyType(String billKeyType) {
		this.billKeyType = billKeyType;
	}

	
}
