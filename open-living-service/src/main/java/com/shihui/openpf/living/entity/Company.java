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
	@JSONField(name="company_id")
	private Integer companyId;

	@JSONField(name="company_no")
	private String companyNo;

	@JSONField(name="company_name")
	private String companyName;

	@JSONField(name="service_type")
	private Integer serviceType;

	@JSONField(name="city_id")
	private Integer cityId;

	@JSONField(name="fee_type")
	private Integer feeType;

	@JSONField(name="user_no_leng_min")
	private Integer userNoLeng_Min;
	
	@JSONField(name="user_no_leng_max")
	private Integer userNoLengMax;

	@JSONField(name="pay_min")
	private String payMin;

	@JSONField(name="pay_max")
	private String payMax;

	@JSONField(name="date_choice")
	private Integer dateChoice;

	@JSONField(name="status")
	private Integer status;
	//

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

	public Integer getUserNoLeng_Min() {
		return userNoLeng_Min;
	}

	public void setUserNoLeng_Min(Integer userNoLeng_Min) {
		this.userNoLeng_Min = userNoLeng_Min;
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