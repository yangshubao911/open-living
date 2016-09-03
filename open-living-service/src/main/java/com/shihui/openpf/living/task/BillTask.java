/**
 * 
 */
package com.shihui.openpf.living.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.shihui.api.order.vo.MerchantCancelParam;
import com.shihui.commons.ApiLogger;
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
import com.shihui.openpf.living.io3rd.CheckFile;
import com.shihui.openpf.living.io3rd.CheckItem;
import com.shihui.openpf.living.io3rd.Codec;
import com.shihui.openpf.living.io3rd.GuangdaResponse;
import com.shihui.openpf.living.io3rd.RefundeFile;
import com.shihui.openpf.living.io3rd.RefundeItem;
import com.shihui.openpf.living.io3rd.ResKey;
import com.shihui.openpf.living.service.OrderSystemService;
import com.shihui.openpf.living.util.FileUtil;

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

	@Scheduled(cron = "0 55 8 * * ?")
	public void billCheckNotify() {
		ApiLogger.info("BillTask: billCheckNotify() : start");
//TODO XXX		
//		if(cacheDao.lockTask()) {
		if(!cacheDao.lockTask()) {
			//流水号
			//cacheDao.resetSerialNo();
			//更换密钥
			//guangdaResponse.doReqKey();
	
			//处理对账
			//PacketNotify packetNotify = cacheDao.getNotify();

			File file = FileUtil.getCheckFile(url, username, password, checkPath);
			ApiLogger.info("BillTask: billCheckNotify() : file != null : " + (file != null));
			if(file != null) {
				CheckFile checkFile = FileUtil.getCheckFile(file);
				ApiLogger.info("BillTask: billCheckNotify() : checkFile != null : " + (checkFile != null));
				if(checkFile != null) {
					ArrayList<CheckItem> checkList = checkFile.getCheckList();
					if(checkList != null && checkList.size() > 0)
						check(checkList);
				}
			}
			file = FileUtil.getRefundeFile(url, username, password, refundePath);
			if(file != null) {
				RefundeFile refundeFile = FileUtil.getRefundeFile(file);
				ApiLogger.info("BillTask: billCheckNotify() : refundeFile != null : " + (refundeFile != null));
				if(refundeFile != null) {
					ArrayList<RefundeItem> refundeList = refundeFile.getRefundeList();
					if(refundeList != null && refundeList.size() > 0)
						refunde(refundeList);
				}
				
			}
			
			completeRestOrder();

		}
		
		//
		
		while(!Thread.currentThread().isInterrupted()) {
			try {
	    		Thread.sleep(10*60*1000);
	    	}catch(Exception e) {
	    	}
			if(!cacheDao.checkKeyDateExpired()) {
				ResKey resKey = cacheDao.getResKey();
				if(resKey != null) {
					try {
						Codec.writeKey(resKey.tout.keyValue, resKey.tout.verifyValue, resKey.tout.keyValue1, resKey.tout.verifyValue1);
						break;
					} catch(Exception e) {
						ApiLogger.info("!!!BillTask : billCheckNotify() : Codec.writeKey() : " + e.getMessage());
					}
				}
			}
		}

		ApiLogger.info("BillTask: billCheckNotify() : end");
	}

	private void check(ArrayList<CheckItem> checkList)  {
		ApiLogger.info("BillTask: billCheckNotify() : check() : start");

		for(CheckItem ci : checkList) {
			try {
				Bill bill = billDao.findByBillNo(ci.getBillNo());
				Order order = orderDao.findById(bill.getOrderId());
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
		        settlementJson.put("settlePrice", StringUtil.yuan2hao(order.getPrice()));//goods.getPrice()
		        settlementJson.put("settleMerchantId", merchant.getMerchantCode());
		
		        boolean payorderchange = orderSystemService.complete(order.getOrderId(), order.getGoodsId(),
		   			 settlementJson.toJSONString(), com.shihui.api.order.common.enums.OrderStatusEnum.OrderUnStockOut.getValue() );
		        ApiLogger.info("BillTask : check() : SUCCESS : billNO: [" + ci.getBillNo() + "] " + payorderchange);
			} catch(Exception e) {
		        ApiLogger.info("BillTask : check() : Exception : billNO: [" + ci.getBillNo() + "] msg: " +e.getMessage());
			}
		}
		ApiLogger.info("BillTask: billCheckNotify() : check() : end");
	}
	private void refunde(ArrayList<RefundeItem> refundeList) {
		ApiLogger.info("BillTask: billCheckNotify() : refunde() : start");

		for(RefundeItem ri : refundeList) {
			try {
				Bill bill = billDao.findByBillNo(ri.getBillNo());
				long orderId = bill.getOrderId();
				Order order = orderDao.findById(orderId);
				//
				if(order.getOrderStatus() != com.shihui.api.order.common.enums.OrderStatusEnum.OrderUnStockOut.getValue())
					continue;
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
			} catch(Exception e) {
				ApiLogger.info("BillTask : refunde() : Exception : billNO: [" + ri.getBillNo() + "] msg: " +e.getMessage());
			}
		}
		ApiLogger.info("BillTask: billCheckNotify() : refunde() : end");
	}
	private void completeRestOrder() {
		List<Order> orderList = orderDao.queryOrderUnStockOut();
		ApiLogger.info("BillTask : completeRestOrder() : start : orderList != null : " + (orderList != null));
		if(orderList != null && orderList.size() > 0) {
			for(Order order : orderList) {
				try {
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
				} catch(Exception e) {
					ApiLogger.info("BillTask : completeRestOrder() : Exception : orderId: [" + order.getOrderId() + "] msg: " +e.getMessage());
				}
			}
		}
		ApiLogger.info("BillTask : completeRestOrder() : end");
	}
}
