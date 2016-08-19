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
	class TestData {
		public ArrayList<TestInput> tiList;
		public ArrayList<TestOutput> toList;
		
		public TestData () {
			tiList = new  ArrayList<TestInput>();
			toList = new  ArrayList<TestOutput>();
		}
	}
	class TestInput {
		public int		userId;			// 36050
		public int		serviceId;		// 38
		public int		categoryId;		// 2
		public int		cityId;			// 2
		public long		groupId;		// 532712
		public int		companyId;		// 1
		public String	companyNo;		// 021009006
		public String	userNo;			// 510070111304276000079004
		public long		goodsId;		// 1
		public int		goodsVersion;	// 1
		public String	field2;			// 1
		
		public TestInput(int userId, int serviceId, int categoryId, int cityId, long groupId, 
				int companyId, String companyNo, String userNo, long goodsId, int goodsVersion, String field2) {
			this.userId = userId;
			this.serviceId = serviceId;
			this.categoryId = categoryId;
			this.cityId = cityId;
			this.groupId = groupId;
			this.companyId = companyId;
			this.companyNo = companyNo;
			this.userNo = userNo;
			this.goodsId = goodsId;
			this.goodsVersion = goodsVersion;
			this.field2 = field2;
		}
	}
	public class TestOutput {
		public String tempId;
		public long orderId;
		
		public TestOutput() {
			
		}
	}
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
				new TestInput(36051, 38, 2, 1, 532712, 1, "021009006", "510070111304276000079004", 1, 1, "1")//,
//				new TestInput(36052, 38, 2, 1, 532712, 1, "021009006", "0060014216", 1, 1, "2"),
//				new TestInput(36053, 38, 2, 1, 532712, 1, "021009006", "609990231041328000100006", 1, 1, "1"),
//				new TestInput(36054, 38, 2, 1, 532712, 1, "021009006", "0210274168", 1, 1, "2")
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
//				new TestInput(36051, 38, 2, 1, 532712, 1, "021009006", "510070111304276000079004", 1, 1, "1"),
				new TestInput(36052, 38, 2, 1, 532712, 1, "021009006", "0060014216", 1, 1, "2"),
				new TestInput(36053, 38, 2, 1, 532712, 1, "021009006", "609990231041328000100006", 1, 1, "1"),
				new TestInput(36054, 38, 2, 1, 532712, 1, "021009006", "0210274168", 1, 1, "2")
				};

		TestData td = query(tia);
		if( td == null)
			return JSON.toJSON(new SimpleResponse(1, "TEST : queryDoc1 : td == null"));
		else {
			cacheDao.setTest(td);
			return JSON.toJSON(td);
		}
	}
	
	private boolean comfirm(TestData td) {
		
		for(int i = 0; i < td.tiList.size(); i++) {
			TestInput ti = td.tiList.get(i);
			TestOutput to = td.toList.get(i);
			
			JSONObject jo = (JSONObject)clientService.confirmOrder(ti.userId, to.tempId, null);
			
			if(jo.getJSONObject("response").getInteger("status") != 1)
				return false;
		}
		return true;
	}
	private boolean pay(TestData td) {
		
		for(int i = 0; i < td.tiList.size(); i++) {
			TestInput ti = td.tiList.get(i);
			TestOutput to = td.toList.get(i);
			
			JSONObject jo = (JSONObject)clientService.createOrder(ti.userId, to.tempId, 0, null);
			
			if(jo.getInteger("status") != 1)
				return false;
		}
		return true;
	}
	public Object pay() {
		TestData td = (TestData)cacheDao.getTest(TestData.class);
		if(td != null) {
			if(!comfirm(td))
				return JSON.toJSON(new SimpleResponse(2, "TEST : comfirm : Fail"));
			if(!pay(td))
				return JSON.toJSON(new SimpleResponse(3, "TEST : pay : Fail"));
			return JSON.toJSON(new SimpleResponse(0, "TEST : pay : OK"));
		}
		return JSON.toJSON(new SimpleResponse(1, "TEST : payDoc1 : td == null"));
	}
	
	public Object queryExc1() {
		JSONObject result = new JSONObject();
		
		
		return result;
	}
	public Object payExc1() {
		JSONObject result = new JSONObject();
		
		
		return result;
	}
}
