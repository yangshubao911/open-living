package com.shihui.openpf.living.mq;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shihui.api.order.common.enums.OrderStatusEnum;
import com.shihui.api.order.common.enums.OrderTypeEnum;
import com.shihui.api.order.common.enums.PayTypeEnum;
import com.shihui.api.order.service.OpenService;
import com.shihui.api.order.vo.SimpleResult;
import com.shihui.commons.ApiLogger;
import com.shihui.commons.mq.annotation.ConsumerConfig;
import com.shihui.commons.mq.api.Consumer;
import com.shihui.commons.mq.api.Topic;
import com.shihui.openpf.common.dubbo.api.ServiceManage;
import com.shihui.openpf.common.model.Service;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.dao.BillDao;
import com.shihui.openpf.living.dao.CategoryDao;
import com.shihui.openpf.living.dao.CompanyDao;
import com.shihui.openpf.living.dao.OrderDao;
import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Category;
import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.living.entity.support.BillStatusEnum;
import com.shihui.openpf.living.entity.support.OrderBillVo;
import com.shihui.openpf.living.entity.support.QueryModeEnum;
import com.shihui.openpf.living.io3rd.ReqPay;
import com.shihui.openpf.living.service.OrderService;
import com.shihui.openpf.living.util.HttpUtil;
import com.shihui.openpf.living.util.LivingUtil;
import com.shihui.rpc.user.service.api.UserService;
/**
 * Created by zhoutc on 2016/3/3.
 */

/**
 * 订单消息消费者
 */
@Component("paymentSuccessConsumer")
@ConsumerConfig(consumerName = "livingOrderConsumer", topic = Topic.UPDATE_ORDER_STATUS)
public class PaymentSuccessConsumer implements Consumer {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Resource
	private OrderService orderService;
	@Resource
	private ServiceManage serviceManage;
	@Resource
	private BillDao billDao;
	@Resource
	private OpenService openService;
	@Resource
	AppNotice appNotice;
	@Resource
	CacheDao cacheDao;
	@Resource
	CompanyDao companyDao;
	@Resource
	LivingMqProducer mqProducer;
	@Resource
	private UserService userServicenew;
	@Resource
	private OrderDao orderDao;
	@Resource
	private CategoryDao  categoryDao ;
	@Value(value="${sms.url}")
	private URL smsUrl;

	
	public PaymentSuccessConsumer(){
//		log.info("---------------------------PaymentSuccessConsumer------------------------------");
	}

	private void doReqPay(OrderBillVo obvo) {
		Bill bill = obvo.getBill();
		Order order = obvo.getOrder();
		String tempId = LivingUtil.getRechargeTrmSeqNum(order.getOrderId());
		
		ReqPay reqPay;
		
		if(obvo.getCompany().getQueryMode() == QueryModeEnum.ShangHaiChenNanShuiWu.getMode()) {
			reqPay = ReqPay.instance(
					tempId, 
					bill.getBillKey(), 
					obvo.getCompany().getCompanyNo(), 
					cacheDao.getSerialNo(), 
					new BigDecimal(order.getPrice()).multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP).toString(),
					bill.getUserName(), 
					bill.getContractNo(), 
					null,null,null,null/*field1, filed2, filed3, filed4*/);
			
		} else {
			reqPay = ReqPay.instance(
					tempId, 
					bill.getBillKey(), 
					obvo.getCompany().getCompanyNo(), 
					cacheDao.getSerialNo(), 
					new BigDecimal(order.getPrice()).multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP).toString(),
					bill.getUserName(), 
					bill.getContractNo(), 
					bill.getBillDate(),bill.getBillKeyType(),null,null/*field1, filed2, filed3, filed4*/);
		}
		
		if(mqProducer.sendRechargeRequest(tempId, JSON.toJSONString(reqPay))) {
			bill.setSerialNo(reqPay.tin.billNo);
			bill.setPayTime(reqPay.tin.payDate);
			cacheDao.setOrderBillVo(bill.getOrderId(), obvo);
			
			Bill billUpdate = new Bill();
			billUpdate.setOrderId(bill.getOrderId());
			billUpdate.setSerialNo(reqPay.tin.billNo);
			billUpdate.setPayTime(reqPay.tin.payDate);
			billDao.update(billUpdate);
//			ApiLogger.info("PaymentSuccessConsumer : doReqPay() : mqProducer.sendRechargeRequest() : billKey:[" + reqPay.tin.billKey +"] payAmount:["+ reqPay.tin.payAmount+"]");
		}
	}
	
	@Override
	public boolean doit(String topic, String tags, String key, String msg) {
		ApiLogger.info("PaymentSuccessConsumer : topic[" + topic + "] tags[" + tags + "] key[" + key + "] msg=" + msg);

		try {
			JSONObject jo = JSONObject.parseObject(msg);
			JSONObject orderJo = jo.getJSONObject("order");
			if (orderJo == null) {
//				log.warn("订单消息无法处理，msg={}", msg);
				ApiLogger.info("PaymentSuccessConsumer : 订单消息无法处理，msg={" + msg + "}");
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

			//Order order = orderService.getOrderById(orderId);
			Order order;
			Bill bill;
			OrderBillVo obvo = cacheDao.getOrderBillVo(orderId);
			if(obvo == null) {
				obvo = new OrderBillVo();
				order = orderService.getOrderById(orderId);
				bill = billDao.findById(orderId);
				obvo.setBill(bill);
				obvo.setOrder(order);
				obvo.setCompany(companyDao.findById(bill.getCompanyId()));
			}
			else {
				order = obvo.getOrder();
				bill = obvo.getBill();
			}
			
			if (order == null || bill == null) {
				return true;
			} else {
//				log.info("消费订单状态变更消息 topic:"+ topic +",key:"+ key + ",msg:"+ msg);
//				ApiLogger.info("PaymentSuccessConsumer : 消费订单状态变更消息 topic:" + topic + ", key:" +key + ", msg:" + msg);

				// 更新订单状态
				orderService.updateOrderStatus(orderId, status.getValue());
				order.setOrderStatus(status.getValue());
				order.setUpdateTime(new Date());
				
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
						//TODO
						order.setPaymentType(orderUpdate.getPaymentType());
						order.setPayTime(orderUpdate.getPayTime());
						order.setTransId(orderUpdate.getTransId());
						order.setUpdateTime(orderUpdate.getUpdateTime());
						//20160830 孙兆英站在内信
						try{
	                        com.shihui.rpc.user.model.User user = userServicenew.getUser(order_vo.getUserId());
	                        long to = user.getUserIdx().getPhone().getNumber();
	                        //                    
	                        Order orderPo = orderDao.findById(orderId);
	                        Integer serviceId = orderPo.getServiceId();
	                        Category category = new Category();
	                        category.setServiceId(serviceId);
	                        List<Category> categories = categoryDao.findByCondition(category);
	                        String categoryName = categories.get(0).getName();
	                        Map<String, String> param = new HashMap<String, String>();
	                        param.put("from", "生活缴费");
	                        param.put("to", to+"");
	                        param.put("type", "0");
	                        param.put("hash", "1");
	                        param.put("msg", "您的" + categoryName + ":" + order.getPay() + "元，小惠已收到您的付款，正火速帮您缴费中，预计1-3个工作日内到账。若本月已缴纳，本次缴费金额将自动退款至您支付账号。");
	                        HttpUtil.doPost(smsUrl, param);
	                    }catch (Exception e) {
	                        log.error(e.getMessage());
	                    }
					}

					//Goods goods = goodsService.findById(order.getGoodsId());

//					//缓存订单信息
//					OrderVo orderVo = new OrderVo();
//					orderVo.setPayTime(new Date(order_vo.getPaymentTime()));
//					orderVo.setGoodsId(order.getGoodsId());
//					orderVo.setMerchantId(order.getMerchantId());
//					orderVo.setOrderId(order.getOrderId());
//					orderVo.setOrderStatus(status.getValue());
//					orderVo.setServiceId(order.getServiceId());
//					orderVo.setPhone("");
//					
//					orderCache.set(orderVo);

					bill.setBillStatus(BillStatusEnum.Paid.getValue());
					bill.setUpdateTime(new Date());
					billDao.updateBillStatus(orderId, bill.getBillStatus());

					cacheDao.setOrderBillVo(orderId, obvo);
					//
					//
					doReqPay(obvo);
				} else if(status == OrderStatusEnum.OrderHadReceived){
				    
					//更新订单消费时间
				    Order orderUpdate = new Order();
				    orderUpdate.setOrderId(orderId);
				    orderUpdate.setConsumeTime(new Date(order_vo.getLastStatusTime()));
				    orderUpdate.setUpdateTime(new Date());
					orderService.updateOrderByOrderId(orderUpdate);
					//TODO
					order.setConsumeTime(orderUpdate.getConsumeTime());
					order.setUpdateTime(orderUpdate.getUpdateTime());
					
					bill.setBillStatus(BillStatusEnum.CheckSuccess.getValue());
					bill.setUpdateTime(new Date());
					billDao.updateBillStatus(orderId, bill.getBillStatus());
					
					//cacheDao.setOrderBillVo(orderId, obvo);
					cacheDao.delOrderBillVo(orderId);
					
					//20160830 孙兆英站在内信
					try{
    					com.shihui.rpc.user.model.User user = userServicenew.getUser(order_vo.getUserId());
    					long to = user.getUserIdx().getPhone().getNumber();
    				    //                    
                        Order orderPo = orderDao.findById(orderId);
                        Integer serviceId = orderPo.getServiceId();
                        Category category = new Category();
                        category.setServiceId(serviceId);
                        List<Category> categories = categoryDao.findByCondition(category);
                        String categoryName = categories.get(0).getName();
                        Map<String, String> param = new HashMap<String, String>();
                        param.put("from", "生活缴费");
                        param.put("to", to+"");
                        param.put("type", "0");
                        param.put("hash", "1");
                        param.put("msg", "您的" + categoryName + ":" +order_vo.getPrice()+ "元，已成功缴纳。感谢您的使用。");
                        HttpUtil.doPost(smsUrl, param);
					}catch (Exception e) {
					    log.error(e.getMessage());
                    }
				}
				else {
					//TODO
					if(status == OrderStatusEnum.OrderCloseByOutTime) {
						billDao.updateBillStatus(orderId, BillStatusEnum.Timeout.getValue());
						cacheDao.delOrderBillVo(orderId);
					}
					else if ( status == OrderStatusEnum.BackClose) {
						billDao.updateBillStatus(orderId, BillStatusEnum.Close.getValue());
						cacheDao.delOrderBillVo(orderId);
					}
					else if ( status == OrderStatusEnum.PayedCancel) {
						billDao.updateBillStatus(orderId, BillStatusEnum.Refund.getValue());
						cacheDao.delOrderBillVo(orderId);
		                  //20160830 孙兆英站在内信
	                    try{
	                        com.shihui.rpc.user.model.User user = userServicenew.getUser(order_vo.getUserId());
	                        long to = user.getUserIdx().getPhone().getNumber();
	                        //                    
	                        Order orderPo = orderDao.findById(orderId);
	                        Integer serviceId = orderPo.getServiceId();
	                        Category category = new Category();
	                        category.setServiceId(serviceId);
	                        List<Category> categories = categoryDao.findByCondition(category);
	                        String categoryName = categories.get(0).getName();
	                        Map<String, String> param = new HashMap<String, String>();
	                        param.put("from", "生活缴费");
	                        param.put("to", to+"");
	                        param.put("type", "0");
	                        param.put("hash", "1");
	                        param.put("msg", "您的" + categoryName + ":" +order_vo.getPrice()+ "元缴纳失败。已退款至您的支付账号。具体到账时间以微信（支付宝）平台为准。");
	                        HttpUtil.doPost(smsUrl, param);
	                    }catch (Exception e) {
	                        log.error(e.getMessage());
	                    }

					}
				}

				//
				//推送客户端消息
				if(status == OrderStatusEnum.OrderUnStockOut
						|| status == OrderStatusEnum.BackClose || status == OrderStatusEnum.OrderHadReceived
						|| status == OrderStatusEnum.PayedCancel){
					String pushMsg = null;
					Service service = serviceManage.findById(order.getServiceId());
					if(service == null){
//						log.error("订单处理-push消息：业务信息未查到，serviceId={}, orderId={}, orderStatus={}", order.getServiceId(), orderId, order.getOrderStatus());
						ApiLogger.info("PaymentSuccessConsumer : 订单处理-push消息：业务信息未查到，serviceId={" + order.getServiceId() + "} orderId={" + orderId +"} orderStatus={"+order.getOrderStatus()+"}");
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
						appNotice.pushMsg(pushMsg, order.getUserId(), service.getServiceMerchantCode());
					}
				}
				return true;// 默认消息正确处理
			}
		} catch (Exception e) {
//			log.error("处理mq订单消息异常, msg={}", msg, e);
			ApiLogger.info("PaymentSuccessConsumer : 处理mq订单消息异常, msg={" + msg + "} " + e.getMessage());
		}

		return false;
	}

}
