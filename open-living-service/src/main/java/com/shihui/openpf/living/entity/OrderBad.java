package com.shihui.openpf.living.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * The persistent class for the order database table.
 * 
 */
@Entity(name = "order_bad")
public class OrderBad implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long orderId;

	private Integer orderStatus;

	@JSONField(name = "update_time", format = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	@JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@JSONField(name = "bad_comment")
	private String badComment;

	public OrderBad() {
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getBadComment() {
		return badComment;
	}

	public void setBadComment(String badComment) {
		this.badComment = badComment;
	}

}