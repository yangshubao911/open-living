package com.shihui.openpf.living.entity;


import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by zhoutc on 2016/2/2.
 */
@Entity(name="company")
public class Company implements Serializable {

    private static final long serialVersionUID = -1L;

    @Id
	private Integer companyId;

	private String companyNo;

	private String companyName;

	private Integer serviceType;

	private Integer cityId;

	private Integer feeType;

	private Integer userNoLengMin;
	
	private Integer userNoLengMax;

	private String payMin;

	private String payMax;

	private Integer dateChoice;
	
	private Integer barcode;

	private Integer status;
	//

	public Integer getBarcode() {
		return barcode;
	}

	public void setBarcode(Integer barcode) {
		this.barcode = barcode;
	}

	public void setUserNoLengMin(Integer userNoLengMin) {
		this.userNoLengMin = userNoLengMin;
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
	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}

	public Integer getUserNoLengMin() {
		return userNoLengMin;
	}

	public void setUserNoLeng_Min(Integer userNoLengMin) {
		this.userNoLengMin = userNoLengMin;
	}

	public Integer getUserNoLengMax() {
		return userNoLengMax;
	}

	public void setUserNoLengMax(Integer userNoLengMax) {
		this.userNoLengMax = userNoLengMax;
	}

	public String getPayMin() {
		return payMin;
	}

	public void setPayMin(String payMin) {
		this.payMin = payMin;
	}

	public String getPayMax() {
		return payMax;
	}

	public void setPayMax(String payMax) {
		this.payMax = payMax;
	}

	public Integer getDateChoice() {
		return dateChoice;
	}

	public void setDateChoice(Integer dateChoice) {
		this.dateChoice = dateChoice;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}