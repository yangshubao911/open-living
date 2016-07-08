/**
 *
 */

package com.shihui.openpf.living.entity;

/**
 * @author zhouqisheng
 *
 * @version 1.0 Created at: 2016年1月18日 下午7:23:33
 */

public enum OrderCancelType {
	NON_PAYMENT_CONSUMER(11, "用户取消未支付订单"),
	PAYMENT_CONSUMER(21,"用户取消已支付订单"),
	PAYMENT_MERCHANT(22, "商户取消已支付订单"),
	PAYMENT_OUT_TIME(31, "支付超时-实惠"),
	MERCHANT_OUT_TIME(32, "商户确认超时"),
	SYS_INTERVERNE(41, "客服干预"),
	REFUND_PARTIAL (51, "部分退款");

	private int value;
	private String comment;

	OrderCancelType(int value, String comment){
		this.value = value;
		this.comment = comment;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}

