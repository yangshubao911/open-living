package com.shihui.openpf.living.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Column;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * The persistent class for the order database table.
 * 
 */
@Entity(name = "order")
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long orderId;

	private Integer campaignId;

	@JSONField(name = "createTime", format = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	private String extend;

	private Long gid;

	private Long goodsId;

//	private Integer goodsNum;

	private Integer goodsVersion;

	private Integer merchantId;

	private Integer orderStatus;

	private String pay;

	private Integer paymentType;

	private String price;
	
	private String settlement;

	@Column(name = "sh_off_set")
	private String shOffSet;

	@JSONField(name = "updateTime", format = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	private Integer userId;

	private Integer serviceId;

	private String remark;

//	private String phone;
	
	private Long auditId;
	
	private Integer refundType;
	
	private String refundPrice;
	
	private Integer appId;

//  @Transient
//	private String requestId;
//    @Transient
//    private String serviceStartTime;
    
    private Long mid;
    @JSONField(name = "payTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;
    @JSONField(name = "consumeTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date consumeTime;
    private String transId;
    private String deviceId;

	public Order() {
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Integer campaignId) {
		this.campaignId = campaignId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public Long getGid() {
		return gid;
	}

	public void setGid(Long gid) {
		this.gid = gid;
	}

//	public Integer getGoodsNum() {
//		return goodsNum;
//	}
//
//	public void setGoodsNum(Integer goodsNum) {
//		this.goodsNum = goodsNum;
//	}

	public Integer getGoodsVersion() {
		return goodsVersion;
	}

	public void setGoodsVersion(Integer goodsVersion) {
		this.goodsVersion = goodsVersion;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getPay() {
		return pay;
	}

	public void setPay(String pay) {
		this.pay = pay;
	}

	public Integer getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSettlement() {
		return settlement;
	}

	public void setSettlement(String settlement) {
		this.settlement = settlement;
	}

	public String getShOffSet() {
		return shOffSet;
	}

	public void setShOffSet(String shOffSet) {
		this.shOffSet = shOffSet;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

//	public String getPhone() {
//		return phone;
//	}
//
//	public void setPhone(String phone) {
//		this.phone = phone;
//	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public Long getAuditId() {
		return auditId;
	}

	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}

	public Integer getRefundType() {
		return refundType;
	}

	public void setRefundType(Integer refundType) {
		this.refundType = refundType;
	}

	public String getRefundPrice() {
		return refundPrice;
	}

	public void setRefundPrice(String refundPrice) {
		this.refundPrice = refundPrice;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

//	public String getRequestId() {
//		return requestId;
//	}
//
//	public void setRequestId(String requestId) {
//		this.requestId = requestId;
//	}
//
//	public String getServiceStartTime() {
//		return serviceStartTime;
//	}
//
//	public void setServiceStartTime(String serviceStartTime) {
//		this.serviceStartTime = serviceStartTime;
//	}

	public Long getMid() {
		return mid;
	}

	public void setMid(Long mid) {
		this.mid = mid;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getConsumeTime() {
		return consumeTime;
	}

	public void setConsumeTime(Date consumeTime) {
		this.consumeTime = consumeTime;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}