package com.shihui.openpf.living.service;

import com.shihui.api.order.common.enums.OrderStatusEnum;
import com.shihui.openpf.living.dao.OrderDao;
import com.shihui.openpf.living.dao.OrderHistoryDao;
import com.shihui.openpf.living.entity.OrderHistory;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.living.entity.support.OrderCancelType;

import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.dao.BillDao;

/**
 * Created by zhoutc on 2016/1/25.
 */
@Service("openOrderService")
public class OrderService {

    @Resource
    OrderDao orderDao;

    @Resource
    BillDao billDao;

    @Resource
    OrderHistoryDao orderHistoryDao;

    /**
     * 创建订单
     * @param order
     * @return 创建成功返回订单号
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrder(Order order) {
        if(orderDao.save(order)>0) {
            Date date = new Date();
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setChangeTime(date);
            orderHistory.setOrderId(order.getOrderId());
            orderHistory.setOrderStatus(order.getOrderStatus());
            if (orderHistoryDao.save(orderHistory) > 0) {
                return true;
            }
        }
        return false;
    }


    public boolean cancelOrder(long orderId, OrderCancelType cancelType, String comment) {
        return false;
    }


    public boolean refund(long orderId, double refundAmount) {
        return false;
    }

	/**
	 * 根据orderId查询order信息
	 * 
	 * @param orderId
	 * @return
	 */
	public Order getOrderById(long orderId) {
		Order order = new Order();
		order.setOrderId(orderId);
		return this.orderDao.findById(order);
	}

	/**
	 * 更新订单状态
	 * @param orderId
	 * @param status
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateOrderStatus(long orderId, int status) {
		Date now = new Date();
		
		OrderHistory orderHistory = new OrderHistory();
		orderHistory.setOrderId(orderId);
		orderHistory.setOrderStatus(status);
		orderHistory.setChangeTime(now);
		this.orderHistoryDao.save(orderHistory);
		
		Order order = new Order();
		order.setOrderId(orderId);
		order.setOrderStatus(status);
		order.setUpdateTime(now);
		this.orderDao.update(order);
	}
	
    /**
     * 更新订单状态
     * @param orderId
     * @param orderStatus
     * @return 插入结果
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(long orderId, OrderStatusEnum orderStatus) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(orderStatus.getValue());
        if(orderDao.update(order)>0) {
            Date date = new Date();
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setChangeTime(date);
            orderHistory.setOrderId(order.getOrderId());
            orderHistory.setOrderStatus(order.getOrderStatus());
            if (orderHistoryDao.insert(orderHistory) > 0) {
                return true;
            }
        }
        return false;
    }

	/**
	 * 更新订单
	 * @param orderUpdate
	 */
	public void updateOrderByOrderId(Order orderUpdate){
		this.orderDao.update(orderUpdate);
	}

    /**
     * 根据条件查询订单
     * @param order
     * @return 订单列表
     */

    public List<Order> queryOrderList(Order order , String startTime , String endTime, Integer page , Integer size) {
        return orderDao.queryOrder(order , startTime , endTime , page , size);
    }



    /**
     * 查询订单总数
     * @param order
     * @return 订单数
     */
/*
    public int countQueryOrder(Order order,String startTime , String endTime) {
        return orderDao.countQueryOrder(order,startTime,endTime);
    }
*/
    /**
     * 根据条件分页查询订单
     * @param orderId    订单Id
     * @return 订单列表
     */

    public Order queryOrder(long orderId) {
        return orderDao.queryOrder(orderId);
    }

    /**
     * 查询异常订单总数
     * @return 订单数
     */

    public int countUnusual() {
        return orderDao.countUnusual();
    }

    /**
     * 查询异常订单
     * @return 订单列表
     */

    public List<Order> queryUnusual() {
        return orderDao.queryUnusual();
    }


    public String exportUnusual() {
        return null;
    }


	public boolean update(Order order) {
		return orderDao.update(order) > 0;
	}


    public int countOrders(long userId,int serviceId, String deviceId) {
        return orderDao.countOrders(userId,serviceId, deviceId);
    }
}
