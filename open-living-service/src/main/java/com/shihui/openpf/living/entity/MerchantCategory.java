package com.shihui.openpf.living.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by zhoutc on 2016/1/26.
 */
@Entity(name="merchant_category")
public class MerchantCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @JSONField(name="merchant_id")
    @Column(name="merchant_id")
    private Integer merchantId;

    @Id
    @JSONField(name="category_id")
    @Column(name="category_id")
    private Integer categoryId;
    
    @Transient
    @JSONField(name="category_name")
    private String categoryName;

    @JSONField(name="status")
    @Column(name="status")
    private Integer status;

    @Id
    @JSONField(name="service_id")
    @Column(name="service_id")
    private Integer serviceId;



    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

}

