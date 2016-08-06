package com.shihui.openpf.living.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.shihui.api.order.common.enums.OrderStatusEnum;
import com.shihui.api.order.emodel.OperatorTypeEnum;
import com.shihui.api.order.emodel.RefundModeEnum;
import com.shihui.api.order.service.OpenService;
import com.shihui.api.order.service.OrderRefundService;
import com.shihui.api.order.service.OrderService;
import com.shihui.api.order.vo.ApiResult;
import com.shihui.api.order.vo.MerchantCancelParam;
import com.shihui.api.order.vo.SimpleResult;
import com.shihui.api.order.vo.SingleGoodsCreateOrderParam;
import com.shihui.commons.ApiLogger;

/**
 * Created by zhoutc on 2016/5/5.
 */
@Service
public class OrderSystemService {
    @Resource
    OpenService openService;
    
    @Resource(name = "omsOrderService")
    OrderService orderService;

    @Resource
    OrderRefundService orderRefundService;

    public ApiResult submitOrder(SingleGoodsCreateOrderParam singleGoodsCreateOrderParam) {

        try {
            ApiResult result = openService.createOrder(singleGoodsCreateOrderParam);
            String json = JSON.toJSONString(result);
            ApiLogger.info(json);
            return result;
        } catch (Exception e) {
            ApiLogger.error("OrderSystemServiceImpl userId：" + singleGoodsCreateOrderParam.getUserId() + "submitOrder error", e);
        }
        return null;
    }


    public boolean merchantCancelOrder(MerchantCancelParam var) {
        try {
            SimpleResult result = openService.merchantCancel(var);
            if (result.getStatus() == 1) {
                return true;
            }

        } catch (Exception e) {
            ApiLogger.error("merchantCancelOrder -- orderId:"+ var.getOrderId() +" cancel error!!!", e);
        }
        return false;
    }

    public boolean success(int orderType, long orderId, long goodsId, String settlementInfo, int nowStatus) {
        try {
            boolean result = openService.success(orderType, orderId, goodsId, settlementInfo, nowStatus);
            return result;
        } catch (Exception e) {
            ApiLogger.error("success exception !!!", e);
        }
        return false;
    }

    public boolean complete(long orderId, long goodsId, String settlementInfo, int nowStatus) {
        try {
            boolean result = openService.complete(orderId, goodsId, settlementInfo, nowStatus);
            return result;
        } catch (Exception e) {
            ApiLogger.error("complete exception !!!", e);
        }
        return false;
    }

    public boolean fail(int orderType , long orderId, long goodsId, long merchantCode,long refundsPrice,int nowStatus, String reason) {
        try {
            //boolean result = openService.merchantCancel(orderType, orderId, goodsId ,merchantCode, refundsPrice, nowStatus, reason);
            return false;
        } catch (Exception e) {
            ApiLogger.error("complete exception !!!", e);
        }
        return false;
    }

    public SimpleResult backendOrderDetail(long orderId) {
        try {
            SimpleResult simpleResult = openService.backendOrderDetail(orderId);
            return simpleResult;
        } catch (Exception e) {
            ApiLogger.error("backendOrderDetail exception !!!", e);
        }
        return null;
    }

    public SimpleResult openRefund(RefundModeEnum refundMode, long orderId, long price, String reason, int isReview, int isShihui) {
        SimpleResult result = null;
        try {
            result = this.orderRefundService.openRefund(refundMode, orderId, price, reason, isReview, isShihui);
        } catch (Exception e) {
            ApiLogger.error("order refund exception !!!, order_id="+orderId, e);
        }
        return result;
    }

    public OpenService getOpenService() {
        return openService;
    }
    
    public boolean updateOrderStatus(long orderId, OrderStatusEnum oldStatus, OrderStatusEnum newStatus, OperatorTypeEnum operatorTypeEnum, long operatorId, String adminEmail) {
        try {
            int count = orderService.updateOrderStatus(orderId, oldStatus, newStatus, operatorTypeEnum, operatorId, adminEmail);
            if (count > 0) {
                return true;
            }

        } catch (Exception e) {
        	ApiLogger.error("updateOrderStatus -- orderId:{} update error!!!" + orderId, e);
        }
        return false;
    }
}
