package com.shihui.openpf.living.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The persistent class for the order database table.
 *
 */
@Entity(name="order_history")
public class OrderHistory implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    private long historyId;

    private long orderId;

    private Date changeTime;

    private int orderStatus;
    //

	public long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}
    
}
