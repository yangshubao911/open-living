package com.shihui.openpf.living.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;


/**
 * The persistent class for the goods database table.
 *
 */
@Entity(name="goods")
public class Goods implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long goodsId;

	private Integer categoryId;

	private Integer cityId;
	
	private String cityName;

	@JSONField(name="createTime", format="yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	private String goodsDesc;

	private String goodsName;

	private Integer goodsStatus;

	private Integer goodsVersion;

	private String imageId;
	
	private String detailImage;

	private String price;

	private Integer serviceId;

	private String shOffSet;

	private String shOffSetMax;

	@JSONField(name="updateTime", format="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	private String attention;
	
	private String goodsSubtitle;
	
	private String firstShOffSet;

	private String firstShOffSetMax;
	
	@Transient
	private Integer serviceMerchantCode;//业务对应商户ID
	//
	public Goods() {
	}

	public String getShOffSetMax() {
		return shOffSetMax;
	}

	public void setShOffSetMax(String shOffSetMax) {
		this.shOffSetMax = shOffSetMax;
	}

	public String getFirstShOffSetMax() {
		return firstShOffSetMax;
	}

	public void setFirstShOffSetMax(String firstShOffSetMax) {
		this.firstShOffSetMax = firstShOffSetMax;
	}

	public Long getGoodsId() {
		return this.goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getCityId() {
		return this.cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getGoodsDesc() {
		return this.goodsDesc;
	}

	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public String getGoodsName() {
		return this.goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public Integer getGoodsStatus() {
		return this.goodsStatus;
	}

	public void setGoodsStatus(Integer goodsStatus) {
		this.goodsStatus = goodsStatus;
	}

	public Integer getGoodsVersion() {
		return this.goodsVersion;
	}

	public void setGoodsVersion(Integer goodsVersion) {
		this.goodsVersion = goodsVersion;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getPrice() {
		return this.price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Integer getServiceId() {
		return this.serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getShOffSet() {
		return this.shOffSet;
	}

	public void setShOffSet(String shOffSet) {
		this.shOffSet = shOffSet;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getServiceMerchantCode() {
		return serviceMerchantCode;
	}

	public void setServiceMerchantCode(Integer serviceMerchantCode) {
		this.serviceMerchantCode = serviceMerchantCode;
	}

	public String getAttention() {
		return attention;
	}

	public void setAttention(String attention) {
		this.attention = attention;
	}

	public String getGoodsSubtitle() {
		return goodsSubtitle;
	}

	public void setGoodsSubtitle(String goodsSubtitle) {
		this.goodsSubtitle = goodsSubtitle;
	}

	public String getDetailImage() {
		return detailImage;
	}

	public void setDetailImage(String detailImage) {
		this.detailImage = detailImage;
	}

	public String getFirstShOffSet() {
		return firstShOffSet;
	}

	public void setFirstShOffSet(String firstShOffSet) {
		this.firstShOffSet = firstShOffSet;
	}
}