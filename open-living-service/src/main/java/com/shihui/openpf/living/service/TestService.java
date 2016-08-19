package com.shihui.openpf.living.service;

import java.util.Date;
import java.util.ArrayList;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shihui.commons.ApiLogger;
import com.shihui.openpf.living.entity.BannerAds;
import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.living.entity.support.OrderBillVo;
import com.shihui.openpf.living.io3rd.GuangdaResponse;
import com.shihui.openpf.living.io3rd.ReqPay;
import com.shihui.openpf.living.mq.LivingMqProducer;
import com.shihui.openpf.living.util.LivingUtil;
import com.shihui.openpf.living.util.SimpleResponse;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.service.ClientService;
import com.shihui.openpf.living.entity.support.QueryOrderBillVo;
import com.shihui.openpf.living.entity.support.TestInput;
import com.shihui.openpf.living.entity.support.TestOutput;
import com.shihui.openpf.living.entity.support.TestData;

/**
 * Created by zhoutc on 2015/12/16.
 */
@Service
public class TestService {

    @Resource GuangdaResponse guangdaResponse;
    @Resource CompanyService companyService;
    @Resource CacheDao cacheDao;
    @Resource LivingMqProducer mqProducer;
    @Resource ClientService clientService;
    
    public Object reqKey() {
    	JSONObject result = new JSONObject();
    	guangdaResponse.doReqKey();
    	result.put("response", new SimpleResponse(0,"已发送申请密钥报文"));
    	return result;
    }
    
    private Object reqPay(long orderId) {
    	JSONObject result;
    	OrderBillVo obvo = cacheDao.getOrderBillVo(orderId);
    	if(obvo == null)
    		result = (JSONObject)JSON.toJSON(new SimpleResponse(1,"ERR: reqPay"));
    	else {
    		result = (JSONObject)JSON.toJSON(new SimpleResponse(1,"OK : reqPay"));
    		doReqPay(obvo);
    	}
    	return result;
    }
    
	private void doReqPay(OrderBillVo obvo) {
		Bill bill = obvo.getBill();
		Order order = obvo.getOrder();
		String tempId = LivingUtil.getRechargeTrmSeqNum(order.getOrderId());
		ReqPay reqPay = ReqPay.instance(
				tempId, 
				bill.getBillKey(), 
				obvo.getCompany().getCompanyNo(), 
				cacheDao.getSerialNo(), 
				order.getPrice(),
				bill.getUserName(), 
				bill.getContractNo(), 
				bill.getBillDate(),bill.getField2(),null,null/*field1, filed2, filed3, filed4*/);
		mqProducer.sendRechargeRequest(tempId, JSON.toJSONString(reqPay));
	}

	//
	// test
	//
	
	private TestData query(TestInput[] tia) {
		TestData td = new TestData();
		
		for(TestInput ti : tia) {
			td.tiList.add(ti);
			
			JSONObject jo = (JSONObject)clientService.queryFee(ti.userId, ti.groupId, -1L, ti.serviceId, 
					ti.categoryId, ti.cityId, ti.goodsId, ti.goodsVersion, ti.companyId, ti.companyNo, 
					ti.userNo, ti.field2, "", -1);
			
			TestOutput to = new TestOutput();
			to.tempId = jo.getString("tempId");
			if( to.tempId == null) {
				ApiLogger.info("TEST: query() : to.tempId == null");
				return null;
			}
			td.toList.add(to);
		}
		ApiLogger.info("TEST: query() : " + JSON.toJSONString(td));
		return td;
	}

	public Object queryDoc0() {
		TestInput[] tia = {
				new TestInput(36051, 38, 2, 1, 532712, 1, "021009006", "510070111304276000079004", 1, 1, "1","116.68")//,
//				new TestInput(36052, 38, 2, 1, 532712, 1, "021009006", "0060014216", 1, 1, "2", "70.00"),
//				new TestInput(36053, 38, 2, 1, 532712, 1, "021009006", "609990231041328000100006", 1, 1, "1", "100.00"),
//				new TestInput(36054, 38, 2, 1, 532712, 1, "021009006", "0210274168", 1, 1, "2", "100.00")
				};

		TestData td = query(tia);
		if( td == null)
			return JSON.toJSON(new SimpleResponse(1, "TEST : queryDoc1 : td == null"));
		else {
			cacheDao.setTest(td);
			
			return JSON.toJSON(td);
		}
	}
	
	public Object queryDoc1() {
		TestInput[] tia = {
//				new TestInput(36051, 38, 2, 1, 532712, 1, "021009006", "510070111304276000079004", 1, 1, "1","116.68")//,
				new TestInput(36052, 38, 2, 1, 532712, 1, "021009006", "0060014216", 1, 1, "2", "70.00"),
				new TestInput(36053, 38, 2, 1, 532712, 1, "021009006", "609990231041328000100006", 1, 1, "1", "100.00"),
				new TestInput(36054, 38, 2, 1, 532712, 1, "021009006", "0210274168", 1, 1, "2", "100.00")
				};

		TestData td = query(tia);
		if( td == null)
			return JSON.toJSON(new SimpleResponse(1, "TEST : queryDoc1 : td == null"));
		else {
			cacheDao.setTest(td);
			return JSON.toJSON(td);
		}
	}
	
	public Object check() {
		ApiLogger.info("TEST : check() : start...");
		TestData td;
		try {
			td = (TestData)cacheDao.getTest(TestData.class);
		} catch(Exception e) {
			ApiLogger.info("TEST : check() : Exception : " + e.getMessage());
			return JSON.toJSON(new SimpleResponse(1, "TEST : check() : Exception : " + e.getMessage()));
		}
		if(td == null){
			ApiLogger.info("TEST : check() : td == null");
			return JSON.toJSON(new SimpleResponse(3, "TEST : check() : td == null"));
		}
			
		for(int i = 0; i < td.tiList.size(); i++) {
			TestInput ti = td.tiList.get(i);
			TestOutput to = td.toList.get(i);
			
			QueryOrderBillVo vo = cacheDao.getQueryOrderBillVo(to.tempId);
			if(vo == null) {
				ApiLogger.info("TEST : check() : vo == null");
				return JSON.toJSON(new SimpleResponse(1, "TEST : check() : vo == null"));
			}
			
			if(ti.price.compareTo(vo.getOrder().getPrice()) != 0) {
				ApiLogger.info("TEST : check() : tempId :[" + to.tempId + "] companyNo:["+ ti.companyNo+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] price:[" +ti.price+"] o_price:["+vo.getOrder().getPrice()+"]");
				return JSON.toJSON(new SimpleResponse(2, "TEST : check() : tempId :[" + to.tempId + "] companyNo:["+ ti.companyNo+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] price:[" +ti.price+"] o_price:["+vo.getOrder().getPrice()+"]"));
			}
		}
		ApiLogger.info("TEST : check() : OK");
		return JSON.toJSON(new SimpleResponse(0, "TEST : check() : OK"));
	}
	
	private boolean comfirm(TestData td) {
		
		for(int i = 0; i < td.tiList.size(); i++) {
			TestInput ti = td.tiList.get(i);
			TestOutput to = td.toList.get(i);
			
			if(to.orderId != 0) {
				JSONObject jo = (JSONObject)clientService.confirmOrder(ti.userId, to.tempId, null);
			
				if(jo.getJSONObject("response").getInteger("status") != 1)
					return false;
			}
		}
		return true;
	}
	private boolean create(TestData td) {
		
		for(int i = 0; i < td.tiList.size(); i++) {
			TestInput ti = td.tiList.get(i);
			TestOutput to = td.toList.get(i);
			
			if(to.orderId != 0) {
				JSONObject jo = (JSONObject)clientService.createOrder(ti.userId, to.tempId, 0, null);
				
				if(jo.getInteger("status") != 1)
					return false;
				
				to.orderId = jo.getLongValue("orderId");
			}
		}
		cacheDao.setTest(td);
		return true;
	}
	private boolean pay(TestData td) {
		
		for(int i = 0; i < td.tiList.size(); i++) {
			TestInput ti = td.tiList.get(i);
			TestOutput to = td.toList.get(i);
			
			if(to.orderId != 0) {
				OrderBillVo vo = cacheDao.getOrderBillVo(to.orderId);
				if(vo == null) {
					ApiLogger.info("TEST : pay() : vo == null");
					return false;
				}
				doReqPay(vo);
			}
		}
		return true;
	}	
	public Object pay() {
		TestData td = (TestData)cacheDao.getTest(TestData.class);
		if(td != null) {
			if(!comfirm(td)) {
				ApiLogger.info("TEST : comfirm : Fail");
				return JSON.toJSON(new SimpleResponse(2, "TEST : comfirm : Fail"));
			}
			if(!create(td)) {
				ApiLogger.info("TEST : create : Fail");
				return JSON.toJSON(new SimpleResponse(3, "TEST : create : Fail"));
			}
			if(!pay(td)) {
				ApiLogger.info("TEST : pay : Fail");
				return JSON.toJSON(new SimpleResponse(4, "TEST : pay : Fail"));
			}
			ApiLogger.info("TEST : pay : OK");
			return JSON.toJSON(new SimpleResponse(0, "TEST : pay : OK"));
		}
		ApiLogger.info("TEST : pay : td == null");
		return JSON.toJSON(new SimpleResponse(1, "TEST : pay : td == null"));
	}
	
	public Object queryExc1() {
		TestInput[] tia = {
				new TestInput(36051, 38, 2, 1, 532712, 1, "021009006", "510070111304276000079004", 1, 1, "1","116.68")//,
//				new TestInput(36052, 38, 2, 1, 532712, 1, "021009006", "0060014216", 1, 1, "2", "70.00"),
//				new TestInput(36053, 38, 2, 1, 532712, 1, "021009006", "609990231041328000100006", 1, 1, "1", "100.00"),
//				new TestInput(36054, 38, 2, 1, 532712, 1, "021009006", "0210274168", 1, 1, "2", "100.00")
				};

		TestData td = query(tia);
		if( td == null)
			return JSON.toJSON(new SimpleResponse(1, "TEST : queryDoc1 : td == null"));
		else {
			cacheDao.setTest(td);
			
			return JSON.toJSON(td);
		}
	}
	public Object payExc1() {
		JSONObject result = new JSONObject();
		
		
		return result;
	}
}
