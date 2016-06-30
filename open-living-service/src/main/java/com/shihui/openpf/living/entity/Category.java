package com.shihui.openpf.living.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * The persistent class for the category database table.
 * 
 */
@Entity(name="category")
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	private String desc;

	private String name;

	private Integer status;

	@JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@JSONField(name = "image_id")
	private String imageId;

	@JSONField(name = "service_id")
	private Integer serviceId;

	@JSONField(name = "product_id")
	private String productId;

	private Integer amount;

	@JSONField(name = "update_time", format = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;


	public Category() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

}