package com.shihui.openpf.living.service;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shihui.commons.ApiLogger;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.dao.BillDao;
import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Company;
import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.living.entity.support.OrderBillVo;
import com.shihui.openpf.living.entity.support.QueryModeEnum;
import com.shihui.openpf.living.entity.support.QueryOrderBillVo;
import com.shihui.openpf.living.entity.support.TestData;
import com.shihui.openpf.living.entity.support.TestInput;
import com.shihui.openpf.living.entity.support.TestOutput;
import com.shihui.openpf.living.io3rd.GuangdaResponse;
import com.shihui.openpf.living.io3rd.ReqPay;
import com.shihui.openpf.living.mq.LivingMqProducer;
import com.shihui.openpf.living.util.LivingUtil;
import com.shihui.openpf.living.util.SimpleResponse;

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
    @Resource BillDao billDao;
    
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
					ti.categoryId, ti.cityId, ti.goodsId, ti.goodsVersion, ti.companyId,/* ti.companyId,*/ 
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
	
	private TestData query(TestInput ti) {
		TestData td = new TestData();
		
			td.tiList.add(ti);
			
			JSONObject jo = (JSONObject)clientService.queryFee(ti.userId, ti.groupId, -1L, ti.serviceId, 
					ti.categoryId, ti.cityId, ti.goodsId, ti.goodsVersion, ti.companyId,/* ti.companyNo, */
					ti.userNo, ti.field2, "", -1);
			
			TestOutput to = new TestOutput();
			to.tempId = jo.getString("tempId");
			if( to.tempId == null) {
				ApiLogger.info("TEST: query() : to.tempId == null");
				return null;
			}
			td.toList.add(to);

		ApiLogger.info("TEST: query() : " + JSON.toJSONString(td));
		return td;
	}
	
	public Object query(Integer index) {
		TestInput[] tia = {
				//上海电力，正确测试
//				new TestInput(36061, 38, 2, 1, 532712, 1, ""510070111304276000079004", 1, 1, "1","116.68", 3, ""),
//				new TestInput(36062, 38, 2, 1, 532712, 1, ""0060014216", 1, 1, "2", "70.00", 3, ""),
//				new TestInput(36063, 38, 2, 1, 532712, 1, ""609990231041328000100006", 1, 1, "1", "100.00", 3, ""),
//				new TestInput(36064, 38, 2, 1, 532712, 1, ""0210274168", 1, 1, "2", "100.00", 3, "")
				//上海电力，错误查询测试
//				new TestInput(36071, 38, 2, 1, 532712, 1, ""510070111304276000079005", 1, 1, "1","32.00", 0, "DEF0001"),
//				new TestInput(36072, 38, 2, 1, 532712, 1, ""510070114098382000155402", 1, 1, "1","39.00", 0, "DEF0002"),
//				new TestInput(36073, 38, 2, 1, 532712, 1, ""9111167666", 1, 1, "2", "38.00", 0, "DEF0010"),
//				new TestInput(36074, 38, 2, 1, 532712, 1, ""0111003482", 1, 1, "2", "338.00", 0, "DEF0002"),
//				new TestInput(36075, 38, 2, 1, 532712, 1, ""510070111304290000007706", 1, 1, "1","7.70", 3, ""),
//				new TestInput(36076, 38, 2, 1, 532712, 1, ""510070114083920000025301", 1, 1, "1","28.80", 3, ""),
//				new TestInput(36077, 38, 2, 1, 532712, 1, ""0111003386", 1, 1, "2", "40.76", 3, ""),
//				new TestInput(36078, 38, 2, 1, 532712, 1, ""511090060549152000169101", 1, 1, "1","169.10", 3, ""),
//				new TestInput(36079, 38, 2, 1, 532712, 1, ""0111003464", 1, 1, "2", "169.11", 3, "")
				//上海电力，错误缴费测试
//				new TestInput(36075, 38, 2, 1, 532712, 1, ""510070111304290000007706", 1, 1, "1","7.70", 3, "EGG0001"),//EGG0675
//				new TestInput(36076, 38, 2, 1, 532712, 1, ""510070114083920000025301", 1, 1, "1","28.80", 3, "DEF0013"),
//				new TestInput(36077, 38, 2, 1, 532712, 1, ""0111003386", 1, 1, "2", "40.76", 3, "DEF0013"),
//				new TestInput(36078, 38, 2, 1, 532712, 1, ""511090060549152000169101", 1, 1, "1","169.10", 3, "DEF0006"),
//				new TestInput(36079, 38, 2, 1, 532712, 1, ""0111003464", 1, 1, "2", "169.11", 3, "DEF0006")
				
				//上海燃气费（条形码）
//				new TestInput(36171, 39, 3, 1, 532712, 2, ""81022442", 2, 1, "2","10.30", 3, "EGG0001"),
//				new TestInput(36172, 39, 3, 1, 532712, 2, ""81022431", 2, 1, "2","70.80", 3, "DEF0010"),
//				new TestInput(36173, 39, 3, 1, 532712, 2, ""81022334", 2, 1, "2","18.10", 3, "DEF0010"),//?
//				new TestInput(36174, 39, 3, 1, 532712, 2, ""81022414", 2, 1, "2","90.00", 3, "DEF0006"),
//				new TestInput(36175, 39, 3, 1, 532712, 2, ""81022334", 2, 1, "2","18.10", 3, "DEF0013"),//?
//				new TestInput(36176, 39, 3, 1, 532712, 2, ""014820693715111000020003", 2, 1, "1","20.00", 3, ""),
//				new TestInput(36177, 39, 3, 1, 532712, 2, ""81022532", 2, 1, "2","32.10", 3, ""),
//				new TestInput(36178, 39, 3, 1, 532712, 2, ""81022350", 2, 1, "2","0.00", 0, "DEF0002"),
//				new TestInput(36179, 39, 3, 1, 532712, 2, ""81022363", 2, 1, "2","0.00", 0, "DEF0003"),
//				new TestInput(36170, 39, 3, 1, 532712, 2, ""91022334", 2, 1, "2","0.00", 0, "DEF0010")
				
				//上海市大众燃气条形码
//				new TestInput(36178, 39, 3, 1, 532712, 3, "39163410", 2, 1, "2","142.00", 3, "EGG0001"),
//				new TestInput(36179, 39, 3, 1, 532712, 3, "39163406", 2, 1, "2","281.00", 3, "DEF0006"),
//				new TestInput(36278, 39, 3, 1, 532712, 3, "39163405", 2, 1, "2","111.80", 3, "DEF0002"),
//				new TestInput(36177, 39, 3, 1, 532712, 3, "39163386", 2, 1, "2","130.80", 3, "DEF0013"),//?
//				new TestInput(36176, 39, 3, 1, 532712, 3, "033920150516337100108802", 2, 1, "1","108.80", 3, ""),
//				new TestInput(36175, 39, 3, 1, 532712, 3, "39163325", 2, 1, "2","135.90", 3, ""),
//				new TestInput(36174, 39, 3, 1, 532712, 3, "39163381", 2, 1, "2","0.00", 0, "DEF0003"),
//				new TestInput(36173, 39, 3, 1, 532712, 3, "39163371", 2, 1, "2","0.00", 0, "DEF0002"),
//				new TestInput(36172, 39, 3, 1, 532712, 3, "933920030519863200006004", 2, 1, "1","0.00", 0, "DEF0001")
				
				//上海市城投水务（条形码）
//				new TestInput(36370, 37, 1, 1, 532712, 4, "296057225141110000338107", 3, 1, "1","338.10", 3, ""),
//				new TestInput(36371, 37, 1, 1, 532712, 4, "306040356141110000031104", 3, 1, "1","31.10", 0, "DEF0002"),
//				new TestInput(36372, 37, 1, 1, 532712, 4, "272520138141010000020706", 3, 1, "1","20.80", 0, "DEF0013"),//?
//				new TestInput(36373, 37, 1, 1, 532712, 4, "316034680141110000127603", 3, 1, "1","127.60", 3, "DEF0010"),
//				new TestInput(36374, 37, 1, 1, 532712, 4, "970115218141210000062102", 3, 1, "1","62.10", 0, "DEF0010"),
//				new TestInput(36375, 37, 1, 1, 532712, 4, "291765185130810000095205", 3, 1, "1","95.20", 0, "DEF0004"),
//				new TestInput(36376, 37, 1, 1, 532712, 4, "300397191141110000079304", 3, 1, "1","79.30", 3, "DEF0013"),//?
//				new TestInput(36377, 37, 1, 1, 532712, 4, "300211814141210000037904", 3, 1, "1","37.90", 3, "DEF0002"),//?
//				new TestInput(36378, 37, 1, 1, 532712, 4, "270684101141210000086307", 3, 1, "1","86.30", 3, "EGG0001"),
//				new TestInput(36379, 37, 1, 1, 532712, 4, "270861809141210000079400", 3, 1, "1","79.40", 3, "DEF0004") //??NPP9998

				//上海威立雅
//				new TestInput(36470, 37, 1, 1, 532712, 5, "930023245141210000013807", 3, 1, "1","13.80", 3, "EGG0001"),
//				new TestInput(36471, 37, 1, 1, 532712, 5, "930023243141210000082809", 3, 1, "1","82.80", 3, "DEF0010"),//?
//				new TestInput(36472, 37, 1, 1, 532712, 5, "930023242141210000020705", 3, 1, "1","20.70", 3, "DEF0006"),//?
//				new TestInput(36473, 37, 1, 1, 532712, 5, "961057905141210000131109", 3, 1, "1","131.10", 3, ""),
//				new TestInput(36474, 37, 1, 1, 532712, 5, "930023288", 3, 1, "2","58.60", 3, ""),
//				new TestInput(36475, 37, 1, 1, 532712, 5, "930023241141210000051801", 3, 1, "1","52.80", 3, "DEF0013"),//?
//				new TestInput(36476, 37, 1, 1, 532712, 5, "930023316", 3, 1, "2","24.20", 3, "EGG0001"),
//				new TestInput(36477, 37, 1, 1, 532712, 5, "930023300", 3, 1, "2","34.50", 3, "DEF0010"),//?
//				new TestInput(36478, 37, 1, 1, 532712, 5, "930023290", 3, 1, "2","96.60", 3, "DEF0006"),//?
//				new TestInput(36479, 37, 1, 1, 532712, 5, "930023290", 3, 1, "2","95.60", 3, "DEF0013"),//?
//				new TestInput(36480, 37, 1, 1, 532712, 5, "962197085", 3, 1, "2","3.50", 3, ""),//? money:6.90
//				new TestInput(36481, 37, 1, 1, 532712, 5, "962197085", 3, 1, "2","3.50", 3, ""),//??
//				new TestInput(36482, 37, 1, 1, 532712, 5, "930023000", 3, 1, "2","0.00", 0, "DEF0002"),
//				new TestInput(36483, 37, 1, 1, 532712, 5, "130023000", 3, 1, "2","0.00", 0, "DEF0010"),
//				new TestInput(36484, 37, 1, 1, 532712, 5, "230023000", 3, 1, "2","0.00", 0, "DEF0003"),
//				new TestInput(36485, 37, 1, 1, 532712, 5, "930023176141210000051806", 3, 1, "1","0.00", 0, "DEF0002"),
//				new TestInput(36486, 37, 1, 1, 532712, 5, "961052205141210000131109", 3, 1, "1","0.00", 0, "DEF0001")
				
				//上海市松江自来水
				new TestInput(36570, 37, 1, 1, 532712, 6, "1114C0000047600000005705", 3, 1, "1","5.70", 3, "DEF0006"),
				new TestInput(36571, 37, 1, 1, 532712, 6, "1114C000004750000002890B", 3, 1, "1","28.90", 3, "EGG0001"),
				new TestInput(36572, 37, 1, 1, 532712, 6, "00000469", 3, 1, "2","17.30", 3, ""),
				new TestInput(36573, 37, 1, 1, 532712, 6, "1114C0000046700000005705", 3, 1, "1","5.70", 3, ""),
				new TestInput(36574, 37, 1, 1, 532712, 6, "1114C0000047400000014400", 3, 1, "1","14.40", 3, "DEF0010"),
				new TestInput(36575, 37, 1, 1, 532712, 6, "1114C0000047300000017301", 3, 1, "1","17.50", 3, "DEF0013"),
				new TestInput(36576, 37, 1, 1, 532712, 6, "1114C0000045800000026001", 3, 1, "1","0.00", 0, "DEF0002"),
				new TestInput(36577, 37, 1, 1, 532712, 6, "1114C0000046700000005707", 3, 1, "1","0.00", 0, "DEF0010"),
				new TestInput(36578, 37, 1, 1, 532712, 6, "1114C000004700000002600@", 3, 1, "1","0.00", 0, "DEF0003")

				//上海嘉定自来水，正确测试
//				new TestInput(36281, 37, 1, 1, 532712, 1, "021009013", "029000099445091000004581", 1, 1, "1","45.80", 3, ""),
				};
//TODO
		TestData td = (index == null) ? query(tia) : query(tia[index]);
		if( td == null)
			return JSON.toJSON(new SimpleResponse(1, "TEST : query : td == null"));
		else {
			cacheDao.setTest(td);
			return JSON.toJSON(td);
		}
	}
	
	public Object checkQuery() {
		ApiLogger.info("TEST : checkQuery() : start...");
		TestData td;
		try {
			td = (TestData)cacheDao.getTest(TestData.class);
		} catch(Exception e) {
			ApiLogger.info("TEST : checkQuery() : Exception : " + e.getMessage());
			return JSON.toJSON(new SimpleResponse(1, "TEST : checkQuery() : Exception : " + e.getMessage()));
		}
		if(td == null){
			ApiLogger.info("TEST : checkQuery() : td == null");
			return JSON.toJSON(new SimpleResponse(3, "TEST : checkQuery() : td == null"));
		}
		for(int i = 0; i < td.tiList.size(); i++) {
			TestInput ti = td.tiList.get(i);
			TestOutput to = td.toList.get(i);
			QueryOrderBillVo vo = cacheDao.getQueryOrderBillVo(to.tempId);
			if(ti.billStatus == 3) {
				if(vo == null) {
					ApiLogger.info("TEST : checkQuery() : vo == null && ti.billStatus == 3 : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] errorCode:[" +ti.errorCode+"]");
					return JSON.toJSON(new SimpleResponse(1, "TEST : checkQuery() : vo == null && ti.billStatus == 3 : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] errorCode:[" +ti.errorCode+"]"));
				}
				if((vo.getOrder().getPrice() == null) || (ti.price.compareTo(vo.getOrder().getPrice()) != 0)) {
//				if(vo.getOrder().getPrice().trim().isEmpty()) {
					ApiLogger.info("TEST : checkQuery() : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] price:[" +ti.price+"] o_price:["+vo.getOrder().getPrice()+"]");
					return JSON.toJSON(new SimpleResponse(2, "TEST : checkQuery() : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] price:[" +ti.price+"] o_price:["+vo.getOrder().getPrice()+"]"));
				}
			} else {
				String errorCode = cacheDao.getErrorCode(to.tempId);
				if(errorCode == null || errorCode.compareTo(ti.errorCode) != 0) {
					ApiLogger.info("TEST : checkQuery() : vo != null && ti.billStatus != 3 : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] errorCode:[" +errorCode + "] ti.errorCode:[" + ti.errorCode+"]");
					return JSON.toJSON(new SimpleResponse(5, "TEST : checkQuery() : vo != null && ti.billStatus != 3 : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] errorCode:[" + errorCode + "] ti.errorCode:[" +ti.errorCode+"]"));
				}
			}
		}
		ApiLogger.info("TEST : checkQuery() : OK");
		return JSON.toJSON(new SimpleResponse(0, "TEST : checkQuery() : OK"));
	}

	public Object checkPay() {
		ApiLogger.info("TEST : checkPay() : start...");
		TestData td;
		try {
			td = (TestData)cacheDao.getTest(TestData.class);
		} catch(Exception e) {
			ApiLogger.info("TEST : checkPay() : Exception : " + e.getMessage());
			return JSON.toJSON(new SimpleResponse(1, "TEST : checkPay() : Exception : " + e.getMessage()));
		}
		if(td == null){
			ApiLogger.info("TEST : checkPay() : td == null");
			return JSON.toJSON(new SimpleResponse(3, "TEST : checkPay() : td == null"));
		}
			
		for(int i = 0; i < td.tiList.size(); i++) {
			TestInput ti = td.tiList.get(i);
			TestOutput to = td.toList.get(i);
			
			Bill bill = billDao.findById(to.orderId);
			if(bill == null) {
				ApiLogger.info("TEST : checkPay() : bill == null");
				return JSON.toJSON(new SimpleResponse(1, "TEST : checkPay() : bill == null"));
			}
			
//			if((bill.getBillStatus() != 3 && ti.billStatus == 3) || (bill.getBillStatus() == 3 && ti.billStatus != 3)) {
//				ApiLogger.info("TEST : checkPay() : (bill.getBillStatus() != 3) : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] bankBillNo:[" +bill.getBankBillNo()+"] bankAcctDate:["+bill.getBankAcctDate()+"]");
//				return JSON.toJSON(new SimpleResponse(2, "TEST : checkPay() : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] bankBillNo:[" +bill.getBankBillNo()+"] bankAcctDate:["+bill.getBankAcctDate()+"]"));
//			}
			if(ti.billStatus == 3) {
				if(ti.errorCode.isEmpty()) {
					if(bill.getBillStatus() != 3) {
						ApiLogger.info("TEST : checkPay() : (bill.getBillStatus() != 3) : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] UNKNOWN");
						return JSON.toJSON(new SimpleResponse(2, "TEST : checkPay() : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] UNKNOWN"));
					}
				} else {
					String errorCode = cacheDao.getErrorCode(String.valueOf(to.orderId));
					if(ti.errorCode.compareTo(errorCode) != 0) {
						ApiLogger.info("TEST : checkPay() : (errorCode.compareTo(ti.errorCode) != 0) : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] errorCode:[" +errorCode+"] ti.errorCode:[" +ti.errorCode+"]");
						return JSON.toJSON(new SimpleResponse(2, "TEST : checkPay() : tempId :[" + to.tempId + "] companyId:["+ ti.companyId+"] userNo: [" +ti.userNo+ "] field2:[" +ti.field2+ "] errorCode:[" +errorCode+"] ti.errorCode:[" +ti.errorCode+"]"));
					}
				}
			}
		}
		ApiLogger.info("TEST : checkPay() : OK");
		return JSON.toJSON(new SimpleResponse(0, "TEST : checkPay() : OK"));
	}
	private boolean confirm(TestData td) {
		
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
			
			ApiLogger.info("TEST : create : tempId : [" + to.tempId + "] orderId: [" + to.orderId + "]");
			
			if(to.orderId == 0) {
				JSONObject jo = (JSONObject)clientService.createOrder(ti.userId, to.tempId, 0, null);
				
				ApiLogger.info("TEST : create : jo : " + jo.toJSONString());
				ApiLogger.info("TEST : create : status : [" + jo.getInteger("status") + "] orderId: [" + jo.getJSONArray("orderId").getLongValue(0) + "]");
				
				if(jo.getInteger("status") != 1)
					return false;
								
				to.orderId = jo.getJSONArray("orderId").getLongValue(0);
				
				ApiLogger.info("TEST : create : tempId : [" + to.tempId + "] orderId: [" + to.orderId + "]");
			}
		}
		cacheDao.setTest(td);
		return true;
	}
	private boolean pay(TestData td) {
		
		for(int i = 0; i < td.tiList.size(); i++) {
			TestInput ti = td.tiList.get(i);
			TestOutput to = td.toList.get(i);
			
			ApiLogger.info("TEST : pay : orderId : " + to.orderId);
						
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
			ApiLogger.info("TEST : comfirm : start...");
			if(!confirm(td)) {
				ApiLogger.info("TEST : comfirm : Fail");
				return JSON.toJSON(new SimpleResponse(2, "TEST : comfirm : Fail"));
			}
			ApiLogger.info("TEST : create : start...");
			if(!create(td)) {
				ApiLogger.info("TEST : create : Fail");
				return JSON.toJSON(new SimpleResponse(3, "TEST : create : Fail"));
			}
			ApiLogger.info("TEST : pay : start...");
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
				new TestInput(36051, 38, 2, 1, 532712, 1, "510070111304276000079004", 1, 1, "1","116.68", 3, "")//,
//				new TestInput(36052, 38, 2, 1, 532712, 1, "0060014216", 1, 1, "2", "70.00"),
//				new TestInput(36053, 38, 2, 1, 532712, 1, "609990231041328000100006", 1, 1, "1", "100.00"),
//				new TestInput(36054, 38, 2, 1, 532712, 1, "0210274168", 1, 1, "2", "100.00")
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
