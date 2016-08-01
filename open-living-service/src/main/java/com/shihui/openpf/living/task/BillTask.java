/**
 * 
 */
package com.shihui.openpf.living.task;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.shihui.openpf.living.dao.OrderBadDao;
import com.shihui.openpf.living.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.shihui.api.order.common.enums.OrderStatusEnum;
import com.shihui.api.order.common.enums.OrderTypeEnum;
import com.shihui.api.order.service.OpenService;
import com.shihui.openpf.common.dubbo.api.MerchantBusinessManage;
import com.shihui.openpf.common.dubbo.api.MerchantManage;
import com.shihui.openpf.common.model.MerchantBusiness;
import com.shihui.openpf.common.model.Service;
import com.shihui.openpf.common.service.api.ServiceService;
import com.shihui.openpf.common.tools.StringUtil;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.dao.MerchantGoodsDao;
import com.shihui.openpf.living.dao.OrderDao;
import com.shihui.openpf.living.entity.support.OrderVo;
import com.shihui.openpf.living.entity.support.OrderBillVo;

/**
 * @author zhouqisheng
 *
 */
@Component
public class BillTask {
//	private Logger log = LoggerFactory.getLogger(getClass());
//	private int flowRechargeServiceId;
//	@Resource
//	private OrderCache orderCache;
//	@Resource
//	private OrderDao orderDao;
//	@Resource
//	private ServiceService serviceService;
//	@Resource
//	private MerchantGoodsDao merchantGoodsDao;
//	@Resource
//	private MerchantBusinessManage merchantBusinessManage;
//	@Resource
//	private RechargeProcess rechargeProcess;
//	@Resource
//    private OpenService openService;
//	@Resource
//	private MerchantManage merchantManage;
//	@Resource
//	private RechargeRequestService rechargeRequestService;
//	@Resource
//	private RetryService retryService;
//	@Resource
//	OrderBadDao orderBadDao;
//	@Resource
//	GoodsService goodsService;
	
//  private int BEGIN_FORBIDDEN_TIME_BUYBILL = 2;
//  private int END_FORBIDDEN_TIME_BUYBILL = 4;


	@PostConstruct
	public void init() {
//		log.info("开始初始化订单处理服务");
//		Order querOrder = new Order();
//		querOrder.setOrderStatus(OrderStatusEnum.OrderUnStockOut.getValue());
//		List<Order> orderList = orderDao.findByCondition(querOrder);
//		for (Order order : orderList) {
//			try {
//
//				if(orderCache.get(order.getServiceId(),order.getOrderId())!=null){
//					continue;
//				}
//				Goods goods = goodsService.findById(order.getGoodsId());
//				OrderVo orderVo = new OrderVo();
//				orderVo.setPayTime(order.getUpdateTime());
//				orderVo.setGoodsId(order.getGoodsId());
//				orderVo.setMerchantId(order.getMerchantId());
//				orderVo.setOrderId(order.getOrderId());
//				orderVo.setOrderStatus(order.getOrderStatus());
//				orderVo.setServiceId(order.getServiceId());
//				orderVo.setPhone(order.getPhone());
//				orderVo.setAmount(goods.getAmount());
//				orderCache.set(orderVo);
//			}catch (Exception e){
//				log.error("OrderWatchDog--init--error!!",e);
//			}
//		}
//		// 仅查询充值类型业务
//		List<Service> serviceList = serviceService.listBytype(3);
//		for (Service service : serviceList) {
//			// 默认用流量充值订单类型区分出流量充值业务
//			if (service.getOrderType().equals(OrderTypeEnum.Recharge_TrafficRecharge.getValue())) {
//				flowRechargeServiceId = service.getServiceId();
//				break;
//			}
//		}
//		log.info("完成初始化订单处理服务，serviceId={}", flowRechargeServiceId);
	}

	@Scheduled(cron = "0 30 2 * * ?")
	public void billCheckNotify() {
//		List<OrderVo> voList = orderCache.getAll(flowRechargeServiceId);
//		log.info("处理订单队列，待处理订单数：{}", voList.size());
//		if (voList.size() > 0) {
//			// 查找商户
//			MerchantBusiness merchantBusiness = new MerchantBusiness();
//			merchantBusiness.setServiceId(flowRechargeServiceId);
//			merchantBusiness.setStatus(1);
//			List<MerchantBusiness> m_s_merchantIdList = merchantBusinessManage.queryList(merchantBusiness);
//			List<Integer> m_s_merchantIds = new ArrayList<>(m_s_merchantIdList.size());
//			Map<Integer, Integer> mbMap = new HashMap<>();
//			for (MerchantBusiness mb : m_s_merchantIdList) {
//				m_s_merchantIds.add(mb.getMerchantId());
//				mbMap.put(mb.getMerchantId(), mb.getWeight());
//			}
//
//			for (OrderVo vo : voList) {
//				if ((System.currentTimeMillis() - vo.getPayTime().getTime()) >= 1000L * 60L * 10L) {
//					boolean lock = orderCache.lockOrder(flowRechargeServiceId, vo.getOrderId());
//					if (lock) {
//						try {
//							Request request = rechargeRequestService.getAvailableRequest(vo.getOrderId());
//							if (request == null) {
//								log.error("处理redis订单队列，未查询到对应第三方订单号，orderId={}", vo.getOrderId());
//								continue;
//							}
//							// 1.充值成功 2.充值失败可以重试 3.充值失败不能退款（异常订单） 4.待确认（需要接下来继续查询，包括充值正在进行中，或者第三方接口限制查询）
//							int ret = rechargeProcess.canRetry(vo.getServiceId(), vo.getMerchantId(), request.getRequestId());
//							switch (ret) {
//							case 1:
//								Order order =  orderDao.findById(vo.getOrderId());
//								if(!order.getOrderStatus().equals(OrderStatusEnum.OrderHadReceived.getValue())){
//									MerchantGoods mg = this.merchantGoodsDao.findById(vo.getGoodsId(), vo.getMerchantId());
//									  JSONObject settlementJson = new JSONObject();
//						            settlementJson.put("settlePrice", StringUtil.yuan2hao(mg.getSettlement()));
//						            settlementJson.put("settleMerchantId", mg.getMerchantId());
//									boolean orderRet = openService.complete(vo.getOrderId(), vo.getGoodsId(), settlementJson.toJSONString(), order.getOrderStatus());
//								    if(orderRet){
//								    	orderCache.del(vo.getServiceId(), vo.getOrderId());
//								    }
//								}else{
//									orderCache.del(vo.getServiceId(), vo.getOrderId());
//								}
//								break;
//							case 2:
//								Order dbOrder =  orderDao.findById(vo.getOrderId());
//								if(vo.getRetryCount() > 0){
//									retryService.refund(vo);
//								}else{
//									retryService.retry(vo, m_s_merchantIds, mbMap, dbOrder.getUserId());
//								}
//								break;
//							case 3:
//								Date date = new Date();
//								OrderBad orderBad = new OrderBad();
//								orderBad.setUpdateTime(date);
//								orderBad.setCreateTime(date);
//								orderBad.setOrderId(vo.getOrderId());
//								orderBad.setBadComment("异步通知处理异常");
//								OrderBad dbOrderBad = orderBadDao.findById(orderBad);
//								boolean update = false;
//								if(dbOrderBad == null){
//									update = orderBadDao.save(orderBad)>0;
//								}else {
//									update =  orderBadDao.update(orderBad)>0;
//								}
//								log.info("OrderWatchDog--saveBadOrder--orderId:{} save bad order result:{}", orderBad.getOrderId(), update);
//
//								orderCache.del(vo.getServiceId(), vo.getOrderId());
//								break;
//							}
//
//						} catch (Exception e) {
//							log.error("处理redis订单队列异常，orderId={}", vo.getOrderId(), e);
//						} finally {
//							orderCache.unlockOrder(vo.getOrderId());
//						}
//					}
//				}
//			}
//		}
	}

	@Scheduled(cron = "0 */10 0-2,4-23 * * ?")
	public void requestillChargeRequest() {
		
	}

	@Scheduled(cron = "0 */10 * * * ?")
	public void billChargeResponse() {
		
	}


}
