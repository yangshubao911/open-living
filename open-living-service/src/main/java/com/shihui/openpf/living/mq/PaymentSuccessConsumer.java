package com.shihui.openpf.living.mq;

//import java.util.List;
//import com.shihui.openpf.living.api.RechargeProcess;
//import com.shihui.openpf.living.dao.RequestDao;
//import com.shihui.openpf.living.entity.Request;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;

import com.shihui.api.order.common.enums.OrderStatusEnum;
import com.shihui.api.order.common.enums.OrderTypeEnum;
import com.shihui.api.order.common.enums.PayTypeEnum;
import com.shihui.api.order.service.OpenService;
import com.shihui.api.order.vo.SimpleResult;
import com.shihui.commons.ApacheHttpClient;
import com.shihui.commons.ApiHttpClient;
import com.shihui.commons.mq.annotation.ConsumerConfig;
import com.shihui.commons.mq.api.Consumer;
import com.shihui.commons.mq.api.Topic;
import com.shihui.openpf.common.dubbo.api.MerchantManage;
import com.shihui.openpf.common.dubbo.api.ServiceManage;
import com.shihui.openpf.common.model.Service;
//import com.shihui.openpf.living.api.bean.ProcessResult;
import com.shihui.openpf.living.cache.OrderCache;
import com.shihui.openpf.living.entity.Goods;
import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.living.entity.support.OrderVo;
import com.shihui.openpf.living.service.GoodsService;
import com.shihui.openpf.living.service.OrderService;

/**
 * Created by zhoutc on 2016/3/3.
 */

/**
 * 订单消息消费者
 */
@Component("paymentSuccessConsumer")
@ConsumerConfig(consumerName = "rechargeFlowConsumer", topic = Topic.UPDATE_ORDER_STATUS)
public class PaymentSuccessConsumer implements Consumer {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Resource
	private OrderService orderService;
	@Resource
	private ServiceManage serviceManage;
	@Resource
	private GoodsService goodsService;
	@Resource
	private MerchantManage merchantManage;
	@Resource
	private OrderCache orderCache;
	@Resource
	private OpenService openService;
	
	private ApiHttpClient httpClient;
	
	@Value("${app_push_url}")
	private String appPushUrl;
	
	@PostConstruct
	public void init(){
		this.httpClient = new ApacheHttpClient();
	}

	public void PaymentSuccessConsumer(){
		log.info("---------------------------PaymentSuccessConsumer------------------------------");
	}

	@Override
	public boolean doit(String topic, String tags, String key, String msg) {
		try {
			JSONObject jo = JSONObject.parseObject(msg);
			JSONObject orderJo = jo.getJSONObject("order");
			if (orderJo == null) {
				log.warn("订单消息无法处理，msg={}", msg);
				return true;
			}

			com.shihui.api.order.po.Order order_vo = jo.getObject("order", com.shihui.api.order.po.Order.class);
			long orderId = order_vo.getOrderId();
			OrderStatusEnum status = order_vo.getOrderStatus();
			OrderTypeEnum orderTypeEnum = order_vo.getOrderType();
			//
			if (!(orderTypeEnum == OrderTypeEnum.Convenient_WFee
					|| orderTypeEnum == OrderTypeEnum.Convenient_LFee
					|| orderTypeEnum == OrderTypeEnum.Convenient_GasFee
					)) {
				return true;
			}

			Order order = orderService.getOrderById(orderId);
			if (order == null) {
				return true;
			} else {
				log.info("消费订单状态变更消息 topic:"+ topic +",key:"+ key + ",msg:"+ msg);

				// 更新订单状态
				orderService.updateOrderStatus(orderId, status.getValue());
				
				if(status == OrderStatusEnum.OrderUnStockOut){
					SimpleResult simpleResult = openService.backendOrderDetail(orderId);
					if(simpleResult.getStatus()==1) {
						com.shihui.api.order.po.Order order_vo_detail = (com.shihui.api.order.po.Order) simpleResult.getData();
						PayTypeEnum payType = order_vo_detail.getPayType();
						String transId = String.valueOf(order_vo_detail.getTransId());
						
						//更新订单支付信息
					    Order orderUpdate = new Order();
					    orderUpdate.setOrderId(orderId);
					    orderUpdate.setPaymentType(payType.getValue());
					    orderUpdate.setPayTime(new Date(order_vo_detail.getPaymentTime()));
					    orderUpdate.setTransId(transId);
					    orderUpdate.setUpdateTime(new Date());
						orderService.updateOrderByOrderId(orderUpdate);
					}

					
					
					Goods goods = goodsService.findById(order.getGoodsId());

					//缓存订单信息
					OrderVo orderVo = new OrderVo();
					orderVo.setPayTime(new Date(order_vo.getPaymentTime()));
					orderVo.setGoodsId(order.getGoodsId());
					orderVo.setMerchantId(order.getMerchantId());
					orderVo.setOrderId(order.getOrderId());
					orderVo.setOrderStatus(status.getValue());
					orderVo.setServiceId(order.getServiceId());
					orderVo.setPhone("");
					
					orderCache.set(orderVo);

				} else if(status == OrderStatusEnum.OrderHadReceived){
					//更新订单消费时间
				    Order orderUpdate = new Order();
				    orderUpdate.setOrderId(orderId);
				    orderUpdate.setConsumeTime(new Date(order_vo.getLastStatusTime()));
				    orderUpdate.setUpdateTime(new Date());
					orderService.updateOrderByOrderId(orderUpdate);
				}

				//推送客户端消息
				if(status == OrderStatusEnum.OrderUnStockOut
						|| status == OrderStatusEnum.BackClose || status == OrderStatusEnum.OrderHadReceived
						|| status == OrderStatusEnum.PayedCancel){
					String pushMsg = null;
					Service service = serviceManage.findById(order.getServiceId());
					if(service == null){
						log.error("订单处理-push消息：业务信息未查到，serviceId={}, orderId={}, orderStatus={}", order.getServiceId(), orderId, order.getOrderStatus());
					} else {
						if(status == OrderStatusEnum.PayedCancel || status == OrderStatusEnum.BackClose){
							pushMsg = "非常抱歉，由于当前缴费人员较多，小惠虽然奋力尝试。但还是没能充值成功，我们以为您办理退款，通常需要1-3个工作日到账。订单号：" + orderId ;
						}else if(status == OrderStatusEnum.OrderUnStockOut){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd号HH时mm分");
							pushMsg = "尊敬的用户，您在"+ sdf.format(order.getCreateTime())
									+"通过实惠平台支付" + order.getPay()
									+"元进行生活缴费，小惠收到您的支付结果，已在火速安排充值，请您耐心等待。";
						}else if(status == OrderStatusEnum.OrderHadReceived){
							pushMsg = "亲，生活缴费已到账啦，赶快查查吧，感谢您的使用。";
						}
					}
					if(pushMsg != null){
						this.pushMsg(pushMsg, order.getUserId(), service.getServiceMerchantCode());
					}
				}
				return true;// 默认消息正确处理
			}
		} catch (Exception e) {
			log.error("处理mq订单消息异常, msg={}", msg, e);
		}

		return false;
	}
	
	/**
	 * push app通知
	 * @param msg
	 * @param userId
	 * @param merchantCode
	 */
	private void pushMsg(String msg, long userId, long merchantCode) {
		try {
			Map<String, Object> param = new HashMap<>();
			
			param.put("touid", userId);
			param.put("dataid", "10001");
			param.put("data", msg);
			param.put("fromuid", merchantCode);

			String result = httpClient.buildPost(appPushUrl).withHeader("X-Matrix-UID", "1000")
			.withParam(param).execute();
			log.info("push app消息 uid:{} result:{}",userId,result);
		} catch (Exception e) {
			log.error("push app消息异常", e);
		}
	}

}
