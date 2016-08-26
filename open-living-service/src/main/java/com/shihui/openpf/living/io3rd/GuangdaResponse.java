package com.shihui.openpf.living.io3rd;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shihui.openpf.common.dubbo.api.MerchantManage;
import com.shihui.openpf.common.dubbo.api.ServiceManage;
import com.shihui.openpf.common.model.Campaign;
import com.shihui.openpf.common.model.Group;
import com.shihui.openpf.common.service.api.CampaignService;
import com.shihui.openpf.common.service.api.GroupManage;
import com.shihui.openpf.common.service.api.ServiceService;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.dao.BillDao;
import com.shihui.openpf.living.dao.CompanyDao;
import com.shihui.openpf.living.dao.GoodsDao;
import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Company;
import com.shihui.openpf.living.entity.Goods;
import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.living.entity.support.BillStatusEnum;
import com.shihui.openpf.living.entity.support.FeeTypeEnum;
import com.shihui.openpf.living.entity.support.OrderBillVo;
import com.shihui.openpf.living.entity.support.QueryModeEnum;
import com.shihui.openpf.living.entity.support.QueryOrderBillVo;
import com.shihui.openpf.living.mq.AppNotice;
import com.shihui.openpf.living.mq.LivingMqProducer;
import com.shihui.openpf.living.util.LivingUtil;
import com.shihui.openpf.living.util.PacketTypeEnum;
import com.shihui.openpf.living.util.ShangHaiChenNanShuiWuUtil;
import com.shihui.openpf.living.util.SimpleResponse;
import com.shihui.openpf.common.model.Merchant;
import com.shihui.openpf.living.entity.MerchantGoods;
import com.shihui.openpf.living.dao.MerchantGoodsDao;
import org.springframework.beans.factory.annotation.Value;

//import me.weimi.api.commons.util.ApiLogger;
import com.shihui.commons.ApiLogger;

@Component
public class GuangdaResponse {

	@Resource
	private CacheDao cacheDao;
	@Resource
	AppNotice appNotice;
	@Resource
	BillDao billDao;
	@Resource
	LivingMqProducer mqProducer;
	@Resource
	CompanyDao companyDao;
	@Resource
	GoodsDao goodsDao;
	@Resource
	private ServiceService serviceService;
	@Resource
	CampaignService campaignService;
	@Resource
	MerchantManage merchantManage;
	@Resource
	ServiceManage serviceManage;
	@Resource
	GroupManage groupManage;
	@Resource
	MerchantGoodsDao merchantGoodsDao;
	
	@Value("${cebenc_path}")
	String cebendPath;
	
	@PostConstruct
    public void init() {
		try {
			Codec.init(cebendPath);
			ResKey resKey = cacheDao.getResKey();
			if(resKey != null) {
				Codec.writeKey(resKey.tout.keyValue, resKey.tout.verifyValue, resKey.tout.keyValue1, resKey.tout.verifyValue1);
			}
//			ApiLogger.info("GuangdaResponse : init() : Codec.init(cebendPath) : OK");
		}catch(Exception e) {
			ApiLogger.info("!!!GuangdaResponse : init() : " + e.getMessage());
		}
	}
	
	private void resPay2Vo(ResPay resPay, OrderBillVo vo) {
		Bill bill = vo.getBill();		
		bill.setBankBillNo(resPay.tout.bankBillNo);
		bill.setReceiptNo(resPay.tout.receiptNo);
		bill.setBankAcctDate(resPay.tout.acctDate);
		//
		bill.setBillStatus(BillStatusEnum.BuySuccess.getValue());
		bill.setUpdateTime(new Date());
		billDao.update(bill);
	}
    public void doResPay(ResPay resPay) {
    	ApiLogger.info(">>>GuangdaResponse doResPay()");
//    	ApiLogger.info("GuangdaResponse : doResPay() : " + JSON.toJSONString(resPay));
    	
    	String tempId = resPay.head.TrmSeqNum;
    	OrderBillVo vo = cacheDao.getOrderBillVo(tempId);
    	if( vo != null) {
    		resPay2Vo(resPay, vo);
    		//cacheDao.setOrderBillVo(tempId, vo);
    		cacheDao.delOrderBillVo(tempId);
    	}
    	ApiLogger.info("<<<GuangdaResponse doResPay()");
    }

    private void resQuery2Vo(ResQuery resQuery, QueryOrderBillVo vo) {
    	Order order = vo.getOrder();
    	Bill bill = vo.getBill();
		bill.setItem1(resQuery.tout.item1);
		bill.setItem2(resQuery.tout.item2);
		bill.setItem3(resQuery.tout.item3);
		bill.setItem4(resQuery.tout.item4);
		bill.setItem5(resQuery.tout.item5);
		bill.setItem6(resQuery.tout.item6);
		bill.setItem7(resQuery.tout.item7);
	
		ResQuery.ToutData td = resQuery.tout.dataList.get(0);
		bill.setContractNo(td.contractNo);
		bill.setUserName(td.customerName);
		//bill.setBalance(String.valueOf(td.balance));
		BigDecimal bdBalance = new BigDecimal(td.balance/100).setScale(2, BigDecimal.ROUND_HALF_UP);
		bill.setBalance(bdBalance.toString());
		BigDecimal bdPayAmount = new BigDecimal(td.payAmount/100).setScale(2, BigDecimal.ROUND_HALF_UP);
		bill.setPayment(bdPayAmount.toString());
		order.setPrice(bdPayAmount.toString());
		bill.setStartTime(td.beginDate);
		bill.setEndTime(td.endDate);

		bill.setField1(td.filed1);
		bill.setField2(td.filed2);
		bill.setField3(td.filed3);
		bill.setField4(td.filed4);
		bill.setField5(td.filed5);
    	//
		if( vo.getCompany().getQueryMode() == QueryModeEnum.ShangHaiChenNanShuiWu.getMode()) {
			bill.setBillDate(ShangHaiChenNanShuiWuUtil.getBillDate(bill.getUserNo()));
		} else {
			bill.setBillDate(bill.getField1());
		}
    }
	private void noticeApp(QueryOrderBillVo vo) {
		JSONObject result = new JSONObject();
		//
		result.put("tempId", vo.getTempId());
		Bill bill = vo.getBill();
		Order order = vo.getOrder();
		result.put("billDate", bill.getBillDate());
		
		Goods goods = vo.getGoods();
		result.put("shOffSet", goods.getShOffSet());
		result.put("shOffSetMax", goods.getShOffSetMax());
		result.put("firstShOffSet", goods.getFirstShOffSet());
		result.put("firstShOffSetMax", goods.getFirstShOffSetMax());

		if( bill.getFeeType() == FeeTypeEnum.Default.getValue() ) {
			result.put("price", order.getPrice());
			result.put("pay", order.getPay());
		} else {
			result.put("balance", bill.getBalance());
		}

		result.put("feeType", bill.getFeeType());
		result.put("feeName", bill.getFeeName());
		result.put("userNo", bill.getBillKey());
		result.put("userAddress", bill.getUserAddress());
		result.put("userName", bill.getUserName());
		
		Company company = vo.getCompany();
		result.put("companyName", company.getCompanyName());
		
		result.put("campaignId", order.getCampaignId());
		result.put("serviceType", company.getServiceType());
		
		result.put("response", new SimpleResponse(1,"查询成功") );
		//
		ApiLogger.info("@@@@GuangdaResponse : noticeApp() : userId :[" + order.getUserId() + "] result : " + result.toJSONString());
//		ApiLogger.info("@@@@GuangdaResponse : noticeApp() : vo : " + result.toJSONString());
		//
		appNotice.pushQueryResult(order.getUserId(), result);
	}
	private void load_vo_elements(QueryOrderBillVo vo) {
		Order order = vo.getOrder();		
		Bill bill = vo.getBill();
//		Company company = companyDao.findById(bill.getCompanyId());
//		Company company = cacheDao.getCompany(bill.getCompanyId());
//		if(company == null) {
//			company = companyDao.findById(bill.getCompanyId());
//			cacheDao.setCompany(bill.getCompanyId(), company);
//		}
		ApiLogger.info(" ** 1 **");
		Company company = vo.getCompany();
		
//		vo.setCompany(company);
		Goods goods = cacheDao.getGoods(bill.getCategoryId(), order.getGoodsId());
		if( goods == null) {
			goods = goodsDao.findById(order.getGoodsId());
			cacheDao.setGoods(bill.getCategoryId(), order.getGoodsId(), goods);
		}
		vo.setGoods(goods);
		//
		ApiLogger.info(" ** 2 **");
//		Campaign campaign;
//		List<Campaign> campaigns = cacheDao.getCampaignList(goods.getServiceId());
//		if(campaigns == null) {
//			campaign = new Campaign();
//			campaign.setServiceId(goods.getServiceId());
//			campaigns = campaignService.findByCondition(campaign);
//			cacheDao.setCampaignList(goods.getServiceId(), campaigns);
//		}
//		if (campaigns != null && campaigns.size() > 0) {
//			// 默认活动就一个首单优惠
//			campaign = campaigns.get(0);
//			Date now = new Date();
//			if (campaign.getStatus() == 1 && campaign.getStartTime().before(now) && campaign.getEndTime().after(now)) {
//				vo.setCampaign(campaign);
//			}
//		}
		Campaign campaign = cacheDao.getCampaign(goods.getServiceId());
		if( campaign == null) {
			campaign = new Campaign();
			campaign.setServiceId(goods.getServiceId());
			List<Campaign> campaigns = campaignService.findByCondition(campaign);
			if (campaigns != null && campaigns.size() > 0) {
				campaign = campaigns.get(0);
				Date now = new Date();
				if (campaign.getStatus() == 1 && campaign.getStartTime().before(now) && campaign.getEndTime().after(now)) {
					vo.setCampaign(campaign);
				} else {
					order.setCampaignId(0);
				}
				cacheDao.setCampaign(goods.getServiceId(), campaign);
			}
		} else {
			Date now = new Date();
			if (campaign.getStatus() == 1 && campaign.getStartTime().before(now) && campaign.getEndTime().after(now)) {
				vo.setCampaign(campaign);
			} else {
				order.setCampaignId(0);
			}
		}
		//
		ApiLogger.info(" ** 3 **");
		//
		com.shihui.openpf.common.model.Service service = cacheDao.getService(order.getServiceId());
		if(service == null) {
			service = serviceManage.findById(order.getServiceId());
			cacheDao.setService(order.getServiceId(), service);
		}
		vo.setService(service);
		ApiLogger.info(" ** 4 **");
		Merchant merchant = cacheDao.getMerchant(goods.getServiceId(), goods.getCategoryId(), goods.getGoodsId());
		if(merchant == null) {
			Integer merchantId = merchantGoodsDao.queryMerchantId(goods.getServiceId(), goods.getCategoryId(), goods.getGoodsId());
			merchant = merchantManage.getById(merchantId);
			cacheDao.setMerchant(goods.getServiceId(), goods.getCategoryId(), goods.getGoodsId(), merchant);
			cacheDao.setMerchant(merchantId, merchant);
		}
		vo.setMerchant(merchant);
		ApiLogger.info(" ** 5 **" + order.getGid());
		Group group = cacheDao.getGroup(order.getGid());
		if( group == null) {
			ApiLogger.info(" ** 5.1 **" + order.getGid());
			group = groupManage.getGroupInfoByGid(order.getGid());
			ApiLogger.info(" ** 5.2 **" + order.getGid());
			cacheDao.setGroup(group.getGid(), group);
		}
		vo.setGroup(group);
		//
		//
		//
		ApiLogger.info(" ** 6 **");
		bill.setFeeName(service.getServiceName());
		bill.setCityName(goods.getCityName());
		bill.setFeeType(company.getFeeType());
		ApiLogger.info(" ** 7 **");
	}
    public void doResQuery(ResQuery resQuery) {
    	ApiLogger.info(">>>GuangdaResponse : doResQuery()");
//    	ApiLogger.info("GuangdaResponse : doResQuery() : " + JSON.toJSONString(resQuery));
    	String tempId = resQuery.head.TrmSeqNum;
    	QueryOrderBillVo vo = cacheDao.getQueryOrderBillVo(tempId);
    	if(vo != null && Integer.parseInt(resQuery.tout.totalNum) > 0 ) {
    		ApiLogger.info(" - 1 -");
	    		resQuery2Vo(resQuery, vo);
	    		ApiLogger.info(" - 2 -");
	    		load_vo_elements(vo);
	    		ApiLogger.info(" - 3 -");
	    		cacheDao.setQueryOrderBillVo(tempId, vo);
	    		ApiLogger.info(" - 4 -");
	    		//
	    		noticeApp(vo);
	    		ApiLogger.info("OK: GuangdaResponse : resQuery()");
    	} else {
    		ApiLogger.info("ERR: GuangdaResponse : resQuery() : {vo != null && Integer.parseInt(resQuery.tout.totalNum) > 0}");
    	}
    	ApiLogger.info("<<<GuangdaResponse : doResQuery() : OK");
    }
    
    public void doPacketError(PacketError packetError) {
    	ApiLogger.info(">>>GuangdaResponse : doPacketError()");
//    	ApiLogger.info("GuangdaResponse : doPacketError() : " + JSON.toJSONString(packetError));
    	
    	String tempId = packetError.head.TrmSeqNum;
    	int packetType = Integer.parseInt(tempId.substring(0,1));
    	if(packetType == PacketTypeEnum.QUERY.getType()) {
    		
    		QueryOrderBillVo vo = cacheDao.getQueryOrderBillVo(tempId);
        	if(vo != null) {
	    		JSONObject result = new JSONObject();
	    		if(packetError.tout.errorCode.compareTo(QueryErrorCodeEnum.DEF0002.getCode()) == 0)
	    			result.put("response", new SimpleResponse(2,QueryErrorCodeEnum.getErrorMessage(packetError.tout.errorCode)));
	    		else if(packetError.tout.errorCode.compareTo(QueryErrorCodeEnum.DEF0010.getCode()) == 0)
	    			result.put("response", new SimpleResponse(0,QueryErrorCodeEnum.getErrorMessage(packetError.tout.errorCode)));
	    		else
	    			result.put("response", new SimpleResponse(3,QueryErrorCodeEnum.getErrorMessage(packetError.tout.errorCode)));
	    		
	    		appNotice.pushQueryResult(vo.getOrder().getUserId(), result);
	    		cacheDao.delQueryOrderBillVo(tempId);
//TODO	XXX    		
	    		cacheDao.setErrorCode(tempId, packetError.tout.errorCode);
//	    		ApiLogger.info("OK: GuangdaResponse : doPacketError() : QUERY : " + result.toJSONString());
        	} else {
        		ApiLogger.info("ERR: GuangdaResponse : doPacketError() : QUERY : QueryOrderBillVo vo == null");
        	}
    	} else if(packetType == PacketTypeEnum.RECHARGE.getType()) {
//    		ApiLogger.info("GuangdaResponse : doPacketError() : RECHARGE");

    		OrderBillVo vo = cacheDao.getOrderBillVo(tempId);
        	if(vo != null) {
        		if(packetError.tout.errorCode.compareTo(PayErrorCodeEnum.NPP0003.getCode()) == 0
        				|| packetError.tout.errorCode.compareTo(PayErrorCodeEnum.NPP0005.getCode()) == 0
        				|| packetError.tout.errorCode.compareTo(PayErrorCodeEnum.DEF0003.getCode()) == 0
        				|| packetError.tout.errorCode.compareTo(PayErrorCodeEnum.DEF0011.getCode()) == 0 ) {
        			
	        		Bill bill = vo.getBill();
	        		bill.setBillStatus(BillStatusEnum.Process.getValue());
	        		bill.setUpdateTime(new Date());
	        		billDao.updateBillStatus(bill.getOrderId(), BillStatusEnum.BuyFail.getValue());
        		} else {
	        		Bill bill = vo.getBill();
	        		bill.setBillStatus(BillStatusEnum.BuyFail.getValue());
	        		bill.setUpdateTime(new Date());
	        		billDao.updateBillStatus(bill.getOrderId(), BillStatusEnum.BuyFail.getValue());
        		}
        		cacheDao.setOrderBillVo(tempId, vo);
//        		ApiLogger.info("OK: GuangdaResponse : doPacketError() : RECHARGE");
        	} else {
        		ApiLogger.info("GuangdaResponse : doPacketError() : KEY");
        		ApiLogger.info("ERR: GuangdaResponse : doPacketError() : RECHARGE : OrderBillVo vo == null");
        	}
//TODO	XXX    		
    		cacheDao.setErrorCode(String.valueOf(vo.getOrder().getOrderId()), packetError.tout.errorCode);
    		ApiLogger.info("GuangdaResponse : doPacketError() : RECHARGE : tempId :[" + tempId + "] errorCode:[" + packetError.tout.errorCode + "]");

    	} else if(packetType == PacketTypeEnum.KEY.getType()) {
    		doReqKey();
    	}
    	ApiLogger.info("<<<GuangdaResponse : doPacketError()");
    }
    
    public void doPacketNotify(PacketNotify packetNotify) {
    	ApiLogger.info(">>>GuangdaResponse : doPacketNotify()");
//    	ApiLogger.info("GuangdaResponse : doPacketNotify() : " + JSON.toJSONString(packetNotify));
    	
    	cacheDao.setPacketNotify(packetNotify);
    	ApiLogger.info("<<<GuangdaResponse : doPacketNotify() : OK");
    }
    
    public void doResKey(ResKey resKey) {
    	ApiLogger.info(">>>GuangdaResponse doResKey()");
    	
    	synchronized(this) {
	    	if(cacheDao.checkKeyDateExpired()) {
//	    		ApiLogger.info("GuangdaResponse : doResKey() : cacheDao.checkKeyDateExpired() == true");
		    	for(int i=0; i< 3; i++) {
			    	try {
			    		Codec.writeKey(resKey.tout.keyValue, resKey.tout.verifyValue, resKey.tout.keyValue1, resKey.tout.verifyValue1);
			    		cacheDao.setResKey(resKey);
			    		cacheDao.setKeyDate();
			    		ApiLogger.info("OK: GuangdaResponse doResKey()");
			    		return;
			    	}catch(Exception e) {
			    		ApiLogger.info("ERR: GuangdaResponse Exception : doResKey() : " + e.getMessage());
			    	}
			    	try {
			    		Thread.sleep(3000);
			    	}catch(Exception e) {
			    	}
		    	}
	    	}
    	}
    	ApiLogger.info("<<<GuangdaResponse doResKey()");
    }

    public void doReqKey() {  
    	ApiLogger.info(">>>GuangdaResponse doReqKey()");
    	if(cacheDao.checkKeyDateExpired() && cacheDao.lockKey()) {
	    	ReqKey reqKey = ReqKey.instance(LivingUtil.getKeyTrmSeqNum());
	    	mqProducer.sendKeyRequest(reqKey.head.TrmSeqNum, JSON.toJSONString(reqKey));
	    	ApiLogger.info("OK: GuangdaResponse : doReqKey()");
    	}
    	ApiLogger.info("<<<GuangdaResponse : doReqKey()");
    }

}
