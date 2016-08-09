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
import com.shihui.openpf.common.model.Merchant;
import com.shihui.openpf.common.model.MerchantBusiness;
import com.shihui.openpf.common.model.Service;
import com.shihui.openpf.common.service.api.ServiceService;
import com.shihui.openpf.common.tools.StringUtil;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.dao.MerchantGoodsDao;
import com.shihui.openpf.living.dao.OrderDao;
import com.shihui.openpf.living.entity.support.OrderVo;
import com.shihui.openpf.living.entity.support.QueryOrderBillVo;
import com.shihui.openpf.living.io3rd.GuangdaResponse;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.io3rd.PacketNotify;
import org.springframework.beans.factory.annotation.Value;
import com.shihui.openpf.living.io3rd.CheckRefundeVo;
import com.shihui.openpf.living.util.FTPUtil;
import com.shihui.openpf.living.dao.GoodsDao;
import com.shihui.openpf.living.dao.BillDao;
import com.shihui.openpf.living.entity.Bill;

import me.weimi.api.commons.util.ApiLogger;

import com.shihui.openpf.living.io3rd.CheckFile;
import com.shihui.openpf.living.io3rd.RefundeFile;
import com.shihui.openpf.living.io3rd.CheckItem;
import com.shihui.openpf.living.io3rd.RefundeItem;
import com.shihui.openpf.living.service.OrderSystemService;
import com.shihui.openpf.living.service.MerchantGoodsService;

@Component
public class BillTask {
	
//	private Logger log = LoggerFactory.getLogger(getClass());
//	private int flowRechargeServiceId;
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
	GuangdaResponse guangdaResponse;
	@Resource
	CacheDao cacheDao;

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

		//更换密钥
		guangdaResponse.doReqKey();

		//处理对账
		//PacketNotify packetNotify = cacheDao.getNotify();
		//if(packetNotify != null)
		CheckRefundeVo vo = FTPUtil.downFile(url, port, username, password, checkPath, refundePath);
		if( vo != null) {
			CheckFile checkFile = vo.getCheckFile();
			if(checkFile != null) {
				ArrayList<CheckItem> checkList = checkFile.getCheckList();
				if(checkList != null && checkList.size() > 0)
					check(checkList);
			}
			RefundeFile refundeFile = vo.getRefundeFile();
			if(refundeFile != null) {
				ArrayList<RefundeItem> refundeList = refundeFile.getRefundeList();
				if(refundeList != null && refundeList.size() > 0)
					refunde(refundeList);
			}
			
		}
		
		completeRestOrder();
		
	}

	private void check(ArrayList<CheckItem> checkList)  {

		for(CheckItem ci : checkList) {
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
				merchant = merchantManage.getById(service.getServiceMerchantId());
				cacheDao.setMerchant(merchant.getMerchantId(), merchant);
			}

	        JSONObject settlementJson = new JSONObject();
	        settlementJson.put("settlePrice", StringUtil.yuan2hao(goods.getPrice()));
	        settlementJson.put("settleMerchantId", retryResultBean.getMerchatCode());
	
	        boolean payorderchange = orderSystemService.complete(order.getOrderid(), order.getPid(),
	   			 settlementJson.toJSONString(), com.shihui.api.order.common.enums.OrderStatusEnum.OrderUnStockOut.getValue() );
	        ApiLogger.info("SUCCESS :  [" + order.getOrderid() + "] result: " + payorderchange);
	        //
		}
		
	}
	private void refunde(ArrayList<RefundeItem> refundeList) {
		
	}
	private void completeRestOrder() {
		
	}
}
