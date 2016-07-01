package com.shihui.openpf.living.entity;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the category database table.
 * 
 */
@Entity(name="category")
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column(name="desc")
	private String desc;

	@Column(name="name")
	private String name;

	@Column(name="status")
	private Integer status;

	@JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "create_time")
	private Date createTime;

	@JSONField(name = "image_id")
	@Column(name = "image_id")
	private String imageId;

	@JSONField(name = "service_id")
	@Column(name = "service_id")
	private Integer serviceId;

	@JSONField(name = "product_id")
	@Column(name = "product_id")
	private String productId;

	@JSONField(name = "amount")
	@Column(name="amount")
	private Integer amount;

	@JSONField(name = "update_time", format = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "update_time")
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