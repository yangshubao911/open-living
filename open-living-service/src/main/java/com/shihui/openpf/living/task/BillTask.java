/**
 * 
 */
package com.shihui.openpf.living.task;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.shihui.api.order.vo.MerchantCancelParam;
import com.shihui.openpf.common.dubbo.api.MerchantManage;
import com.shihui.openpf.common.model.Merchant;
import com.shihui.openpf.common.tools.StringUtil;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.dao.BillDao;
import com.shihui.openpf.living.dao.GoodsDao;
import com.shihui.openpf.living.dao.OrderDao;
import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Goods;
import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.living.entity.support.OrderBillVo;
import com.shihui.openpf.living.io3rd.CheckFile;
import com.shihui.openpf.living.io3rd.CheckItem;
import com.shihui.openpf.living.io3rd.CheckRefundeVo;
import com.shihui.openpf.living.io3rd.GuangdaResponse;
import com.shihui.openpf.living.io3rd.RefundeFile;
import com.shihui.openpf.living.io3rd.RefundeItem;
import com.shihui.openpf.living.service.OrderSystemService;
import com.shihui.openpf.living.util.FTPUtil;

//import me.weimi.api.commons.util.ApiLogger;
import com.shihui.commons.ApiLogger;

@Component
public class BillTask {
	
	@Resource
	OrderSystemService orderSystemService;
	@Resource
	GoodsDao goodsDao;
	@Resource
	BillDao billDao;
	@Resource
	private CacheDao cacheDao;
	@Resource
	private OrderDao orderDao;
	@Resource
	MerchantManage merchantManage;	
	@Resource
	GuangdaResponse guangdaResponse;


	@Value("${sftp_url}")
	String url;
	@Value("${sftp_port}")
	int port;
	@Value("${sftp_username}")
	String username;
	@Value("${sftp_password}")
	String password;
	@Value("${sftp_checkpath}")
	String checkPath;
	@Value("${sftp_refundepath}")
	String refundePath;
	
	@PostConstruct
	public void init() {

	}

	@Scheduled(cron = "0 0 1 * * ?")
	public void billCheckNotify() {
		ApiLogger.info("BillTask: billCheckNotify() : start");
		
		if(cacheDao.lockTask()) {
			//更换密钥
			guangdaResponse.doReqKey();
	
			//处理对账
			//PacketNotify packetNotify = cacheDao.getNotify();
			//if(packetNotify != null)
			CheckRefundeVo vo = FTPUtil.downFile(url, port, username, password, checkPath, refundePath);
			ApiLogger.info("BillTask: billCheckNotify() : vo != null : " + (vo != null));
			if( vo != null) {
				CheckFile checkFile = vo.getCheckFile();
				ApiLogger.info("BillTask: billCheckNotify() : checkFile != null : " + (checkFile != null));
				if(checkFile != null) {
					ArrayList<CheckItem> checkList = checkFile.getCheckList();
					if(checkList != null && checkList.size() > 0)
						check(checkList);
				}
				RefundeFile refundeFile = vo.getRefundeFile();
				ApiLogger.info("BillTask: billCheckNotify() : refundeFile != null : " + (refundeFile != null));
				if(refundeFile != null) {
					ArrayList<RefundeItem> refundeList = refundeFile.getRefundeList();
					if(refundeList != null && refundeList.size() > 0)
						refunde(refundeList);
				}
				
			}
			
			completeRestOrder();
		}
		ApiLogger.info("BillTask: billCheckNotify() : end");
	}

	private void check(ArrayList<CheckItem> checkList)  {
		ApiLogger.info("BillTask: billCheckNotify() : check() : start");

		for(CheckItem ci : checkList) {
//			Bill bill = billDao.findByBillNo(ci.getBillNo());
//			Order order = orderDao.findById(bill.getOrderId());
			long orderId = billDao.getOrderIdByBillNo(ci.getBillNo());
			if( orderId == -1L) {
				ApiLogger.info("BillTask : check() : orderId == -1L [" + ci.getBillNo() + "] ");
				continue;
			}
			
			OrderBillVo obvo = cacheDao.getOrderBillVo(orderId);
			Bill bill = obvo.getBill();
			Order order = obvo.getOrder();
			//
			Goods goods = cacheDao.getGoods(bill.getCategoryId(), order.getGoodsId());
			if( goods == null) {
				goods = goodsDao.findById(order.getGoodsId());
				cacheDao.setGoods(bill.getCategoryId(), order.getGoodsId(), goods);
			}
			Merchant merchant = cacheDao.getMerchant(order.getMerchantId());
			if(merchant == null) {
				merchant = merchantManage.getById(order.getMerchantId());
				cacheDao.setMerchant(merchant.getMerchantId(), merchant);
			}

	        JSONObject settlementJson = new JSONObject();
	        settlementJson.put("settlePrice", StringUtil.yuan2hao(goods.getPrice()));
	        settlementJson.put("settleMerchantId", merchant.getMerchantCode());
	
	        boolean payorderchange = orderSystemService.complete(order.getOrderId(), order.getGoodsId(),
	   			 settlementJson.toJSONString(), com.shihui.api.order.common.enums.OrderStatusEnum.OrderUnStockOut.getValue() );

	        ApiLogger.info("BillTask : check() : SUCCESS : billNO: [" + ci.getBillNo() + "] " + payorderchange);
		}
		ApiLogger.info("BillTask: billCheckNotify() : check() : end");
	}
	private void refunde(ArrayList<RefundeItem> refundeList) {
		ApiLogger.info("BillTask: billCheckNotify() : refunde() : start");

		for(RefundeItem ri : refundeList) {
//			Bill bill = billDao.findByBillNo(ri.getBillNo());
//			long orderId = bill.getOrderId();
//			Order order = orderDao.findById(orderId);
			long orderId = billDao.getOrderIdByBillNo(ri.getBillNo());
			if( orderId == -1L) {
				ApiLogger.info("BillTask : refunde() : orderId == -1L [" + ri.getBillNo() + "] ");
				continue;
			}
			
			OrderBillVo obvo = cacheDao.getOrderBillVo(orderId);
//			Bill bill = obvo.getBill();
			Order order = obvo.getOrder();
			//
			//
	        MerchantCancelParam var = new MerchantCancelParam();
			Merchant merchant = cacheDao.getMerchant(order.getMerchantId());
			if(merchant == null) {
				merchant = merchantManage.getById(order.getMerchantId());
				cacheDao.setMerchant(merchant.getMerchantId(), merchant);
			}
	        var.setMerchantId(merchant.getMerchantCode());
	        var.setIsReturnShihui(1);
	        var.setIsNeedReview(2);
	        var.setRemark("生活缴费退款");
	        var.setOrderId(orderId);
	        var.setOrderStatus(com.shihui.api.order.common.enums.OrderStatusEnum.OrderUnStockOut);
	        var.setPrice(StringUtil.yuan2hao(order.getPay()));
	        var.setReason("缴费失败");
	
	        if (orderSystemService.merchantCancelOrder(var)) {
	            ApiLogger.info("OK : BillTask : refunde() : orderSystemService.merchantCancelOrder : [true ] : orderId: [" + orderId + "] billNo: " + ri.getBillNo());
	        } else {
	        	ApiLogger.info("ERR: BillTask : refunde() : orderSystemService.merchantCancelOrder : [false] : orderId: [" + orderId + "] billNo: " + ri.getBillNo());
	        }
		}
		ApiLogger.info("BillTask: billCheckNotify() : refunde() : end");
	}
	private void completeRestOrder() {
		List<Order> orderList = orderDao.queryOrderUnStockOut();
		ApiLogger.info("BillTask : completeRestOrder() : start : orderList != null : " + (orderList != null));
		if(orderList != null && orderList.size() > 0) {
			for(Order order : orderList) {
				long orderId = order.getOrderId();
//				Bill bill = billDao.findById(orderId);
				ApiLogger.info("BillTask : completeRestOrder() : [" + orderId + "]");
				//
		        MerchantCancelParam var = new MerchantCancelParam();
				Merchant merchant = cacheDao.getMerchant(order.getMerchantId());
				if(merchant == null) {
					merchant = merchantManage.getById(order.getMerchantId());
					cacheDao.setMerchant(merchant.getMerchantId(), merchant);
				}
		        var.setMerchantId(merchant.getMerchantCode());
		        var.setIsReturnShihui(1);
		        var.setIsNeedReview(2);
		        var.setRemark("生活缴费退款");
		        var.setOrderId(orderId);
		        var.setOrderStatus(com.shihui.api.order.common.enums.OrderStatusEnum.OrderUnStockOut);
		        var.setPrice(StringUtil.yuan2hao(order.getPay()));
		        var.setReason("缴费失败");
		
		        if (orderSystemService.merchantCancelOrder(var)) {
		        	ApiLogger.info("OK : BillTask : completeRestOrder() : orderSystemService.merchantCancelOrder : [true ] : orderId: [" + orderId + "]");
		        } else {
		        	ApiLogger.info("OK : BillTask : completeRestOrder() : orderSystemService.merchantCancelOrder : [false] : orderId: [" + orderId + "]");
		        }
			}
		}
		ApiLogger.info("BillTask : completeRestOrder() : end");
	}
}
