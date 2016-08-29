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
	private Long userId;
	private Long gid;
	private Long mid;
	private Integer serviceId;
	private Long goodsId;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGid() {
		return gid;
	}
	public void setGid(Long gid) {
		this.gid = gid;
	}
	public long getMid() {
		return mid;
	}
	public void setMid(long mid) {
		this.mid = mid;
	}
	public Integer getServiceId() {
		return serviceId;
	}
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
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
