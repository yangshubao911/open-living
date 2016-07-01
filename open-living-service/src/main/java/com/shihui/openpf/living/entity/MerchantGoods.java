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
@Entity(name="merchant_goods")
public class MerchantGoods implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @JSONField(name="goods_id")
    @Column(name="goods_id")
    private Long goodsId;
    
    @Transient
    @JSONField(name="goods_name")
    private String goodsName;

    private Integer status;

    @JSONField(name="settlement")
    @Column(name="settlement")
    private String settlement;
    @Id
    @JSONField(name="merchant_id")
    @Column(name="merchant_id")
    private Integer merchantId;
    @Id
    @JSONField(name="service_id")
    @Column(name="service_id")
    private Integer serviceId;

    @JSONField(name="category_id")
    @Column(name="category_id")
    private Integer categoryId;
    
    @Transient
    @JSONField(name="category_name")
    private String categoryName;
    
    @Transient
    private String price;
    
    @Transient
    @JSONField(name="sh_off_set")
    private String shOffSet;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getShOffSet() {
		return shOffSet;
	}

	public void setShOffSet(String shOffSet) {
		this.shOffSet = shOffSet;
	}
}

