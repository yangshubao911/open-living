/**
 * 
 */
package com.shihui.openpf.living.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shihui.api.order.common.enums.OrderStatusEnum;
import com.shihui.api.order.common.enums.OrderTypeEnum;
import com.shihui.api.order.vo.ApiResult;
import com.shihui.api.order.vo.SingleGoodsCreateOrderParam;
import com.shihui.openpf.common.model.Campaign;
import com.shihui.openpf.common.model.Group;
import com.shihui.openpf.common.model.Merchant;
import com.shihui.openpf.common.service.api.CampaignService;
import com.shihui.openpf.common.service.api.ServiceService;
import com.shihui.openpf.common.tools.StringUtil;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.dao.BannerAdsDao;
import com.shihui.openpf.living.dao.BillDao;
import com.shihui.openpf.living.dao.CategoryDao;
import com.shihui.openpf.living.dao.CompanyDao;
import com.shihui.openpf.living.dao.GoodsDao;
import com.shihui.openpf.living.dao.OrderDao;
import com.shihui.openpf.living.entity.BannerAds;
import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Category;
import com.shihui.openpf.living.entity.Company;
import com.shihui.openpf.living.entity.Goods;
import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.living.entity.support.BannerAdsEnum;
import com.shihui.openpf.living.entity.support.BillStatusEnum;
import com.shihui.openpf.living.entity.support.FeeTypeEnum;
import com.shihui.openpf.living.entity.support.OrderBillVo;
import com.shihui.openpf.living.entity.support.QueryModeEnum;
import com.shihui.openpf.living.entity.support.QueryOrderBillVo;
import com.shihui.openpf.living.io3rd.GuangdaDao;
import com.shihui.openpf.living.io3rd.ReqQuery;
import com.shihui.openpf.living.mq.LivingMqProducer;
import com.shihui.openpf.living.util.LivingUtil;
import com.shihui.openpf.living.util.SimpleResponse;
import com.shihui.tradingcenter.commons.dispatcher.currency.AccountDubbo;
import com.shihui.openpf.living.util.ShangHaiChenNanShuiWuUtil;

//import me.weimi.api.commons.util.ApiLogger;
import com.shihui.commons.ApiLogger;

@Service
public class ClientService {
	
	@Resource
	private ServiceService serviceService;
	@Resource
	CampaignService campaignService;
	@Resource
	OrderSystemService orderSystemService;
	@Resource
	AccountDubbo accountDubbo;
	@Resource
	private CategoryDao categoryDao;
	
	@Resource
	private GoodsDao goodsDao;
	
	@Resource
	private CompanyDao companyDao;
	@Resource
	private GuangdaDao guangdaDao;
	@Resource
	private CacheDao cacheDao;
	@Resource
	private OrderDao orderDao;
	@Resource
	private BannerAdsDao bannerAdsDao;
	@Resource
	private BillDao billDao;

	@Resource
	LivingMqProducer mqProducer;
	/*
	 * 
	 */
	public Object homepage(Long userId, Integer cityId, Integer historyOrderCount) {
		String info = cacheDao.getUserHome(userId);
		ApiLogger.info("Service: homepage() : info != null : " + (info!=null) + " : userId:[" + userId + "] : "+ info);
		if(info != null) {
			ApiLogger.info("Service: homepage() : info != null : " + info);
			return JSON.parse(info);
		}
		//
		JSONObject result = new JSONObject();
		
		// 查询广告
		List<BannerAds> adsList = bannerAdsDao.queryForClient(BannerAdsEnum.HOME.getPostion());
		result.put("bannerAds", adsList);
		
		//查询服务
		result.put("categoryList", getCategoryList());
		
		//历史订单Top5
		result.put("billList", getBillList(userId, historyOrderCount));
		
		//
		ApiLogger.info("Service: homepage() : " + result.toJSONString());
		cacheDao.setUserHome(userId, result.toJSONString());
		return result;

	}
	
	private JSONArray getCategoryList() {
		String info = cacheDao.getCategory();
		if(info != null) {
			ApiLogger.info("Service: getCategoryList() : info != null : " + info);
			return (JSONArray)JSON.parse(info);
		}
		//
		JSONArray ja = new JSONArray();
		List<Category> categoryList = categoryDao.findAll();
		for(Category category : categoryList) {
			JSONObject jo = new JSONObject();
			jo.put("categoryId", category.getId());
			jo.put("categoryName", category.getName());
			jo.put("imageId", category.getImageId());
			jo.put("serviceId", category.getServiceId());
			jo.put("categoryStatus", category.getStatus());	
			jo.put("productId", category.getProductId());	
			ja.add(jo);
		}	
		ApiLogger.info("Service: getCategoryList() : " + ja.toJSONString());
		cacheDao.setCategory(ja.toJSONString());
		return ja;

	}

	private JSONArray getBillList(long userId, int count) {
		JSONArray ja = new JSONArray();
		
		List<Bill> billList = billDao.queryTopN(userId, count);
		for(Bill bill : billList) {
			JSONObject jo = new JSONObject();
			jo.put("feeName", bill.getFeeName());
			jo.put("userNo", bill.getBillKey());
			jo.put("categoryId", bill.getCategoryId());
			jo.put("serviceId", bill.getServiceId());
			ja.add(jo);
		}
		return ja;
	}
	/*
	 * 
	 */
	public Object queryCity(Integer serviceId, Integer categoryId) {
		String info = cacheDao.getCity(categoryId);
		if(info != null) {
			ApiLogger.info("Service: queryCity() : info != null : " + info);
			return JSON.parse(info);
		}
		//
		JSONObject result = new JSONObject();
		
		result.put("serviceId", serviceId);
		result.put("categoryId", categoryId);
		
		List<Goods> goodsList = goodsDao.queryByCategory(categoryId);
		
		JSONArray ja = new JSONArray();
		result.put("cityList", ja);
		
		for(Goods goods : goodsList) {
			JSONObject jo = new JSONObject();
			jo.put("cityId", goods.getCityId());
			jo.put("cityName", goods.getCityName());
			jo.put("goodsId", goods.getGoodsId());
			jo.put("goodsVersion", goods.getGoodsVersion());
			ja.add(jo);
		}
		ApiLogger.info("Service: queryCity() : " + result.toJSONString());
		cacheDao.setCity(categoryId, result.toJSONString());
		return result;
	}
	/*
	 * 
	 */
	public Object queryCompany(Integer serviceId, Integer categoryId, Integer cityId) {
		String info = cacheDao.getCompanyList(serviceId, cityId);
		if(info != null) {
			ApiLogger.info("Service: queryCompany() : info != null : " + info);
			return JSON.parse(info);
		}
		
		JSONObject result = new JSONObject();
		
		result.put("serviceId", serviceId);
		result.put("categoryId", categoryId);
		result.put("cityId", cityId);
		
		List<Company> companyList = companyDao.queryList(cityId,serviceService.findById(serviceId).getOrderType());
		
		JSONArray ja = new JSONArray();
		result.put("companyList", ja);
		for(Company company : companyList) {
			JSONObject jo = new JSONObject();
			jo.put("companyId", company.getCompanyId());
			jo.put("companyName", company.getCompanyName());
			jo.put("companyNo", company.getCompanyNo());
			jo.put("serviceType", company.getServiceType());
			jo.put("feeType", company.getFeeType());
			jo.put("userNoLengMin", company.getUserNoLengMin());
			jo.put("userNoLengMax", company.getUserNoLengMax());
			jo.put("payMin", company.getPayMin());
			jo.put("payMax", company.getPayMax());
			jo.put("barcode", company.getBarcode());
			ja.add(jo);
		}
		ApiLogger.info("Service: queryCompany() : " + result.toJSONString());
		cacheDao.setCompanyList(serviceId, cityId, result.toJSONString());
		return result;
	}

	/*
	 * 
	 */
	public Object queryFee(int userId, long groupId, Long mid,
			int serviceId, int categoryId, int cityId, long goodsId, int goodsVersion, 
			int companyId, String companyNo, String userNo, String field2,
			String deviceId, int appId) {
		JSONObject result = new JSONObject();

		String tempId = LivingUtil.getQueryTrmSeqNum(userId);
		
		ApiLogger.info("Service: queryFee() : "
						+ "tempId: " + tempId
						+ ", userNo: " + userNo
						+ ", companyNo: " + companyNo
						+ ", field2: " + field2 );
		//TODO
		Company company = cacheDao.getCompany(companyId);
		if(company == null) {
			company = companyDao.findById(companyId);
			if(company == null) {
				return null;
			}
			cacheDao.setCompany(companyId, company);
		}
		ReqQuery reqQuery;
		String billKey;
		String money;
		if( company.getQueryMode() == QueryModeEnum.ShangHaiChenNanShuiWu.getMode()) {
			billKey = ShangHaiChenNanShuiWuUtil.getBillKey(userNo);

			reqQuery = ReqQuery.instance( 
					tempId, 
					billKey, 
					companyNo, String.valueOf(Integer.parseInt(ShangHaiChenNanShuiWuUtil.getMoney(userNo))), null, null, null);
			
		} else {
			billKey = userNo;
			
			reqQuery = ReqQuery.instance( 
					tempId, 
					billKey, 
					companyNo, null, field2, null, null);
		}
		
		//
		
		//TODO
		QueryOrderBillVo vo = new QueryOrderBillVo();
		vo.setTempId(tempId);
		vo.setCompanyNo(companyNo);
//		vo.setCategoryId(categoryId);
		vo.setCompany(company);
		
		Order order = new Order();
		Bill bill = new Bill();
		order.setUserId(userId);
		order.setGid(groupId);
		order.setMid(mid);
		order.setServiceId(serviceId);
		order.setGoodsId(goodsId);
		order.setGoodsVersion(goodsVersion);
		order.setDeviceId(deviceId);
		order.setAppId(appId);
		bill.setCompanyId(companyId);
		bill.setUserNo(userNo);
		bill.setBillKey(billKey);
		bill.setServiceId(serviceId);
		bill.setCategoryId(categoryId);
		bill.setCityId(cityId);
		bill.setBillKeyType(field2);

		vo.setOrder(order);
		vo.setBill(bill);
		cacheDao.setQueryOrderBillVo(tempId, vo);
		//
		if(mqProducer.sendQueryRequest(tempId, JSON.toJSONString(reqQuery))) {
			result.put("tempId", tempId);
			result.put("response", new SimpleResponse(1,"已提交查询，请等待查询结果") );
		}
		else {
			cacheDao.delQueryOrderBillVo(tempId);
			result.put("response", new SimpleResponse(0,"业务繁忙，查询失败，请稍侯再试") );
		}

		ApiLogger.info("Service: queryFee() : " + result.toJSONString());
		return result;
	}
	
//	public Object checkQuery(int userId, String tempId) {
//		JSONObject result = new JSONObject();
//
//		QueryOrderBillVo vo = cacheDao.getQueryOrderBillVo(tempId);
//		if( vo == null) {
//			result.put("response", new SimpleResponse(0,"查询中，请耐心等待") );
//		} else {
//			result.put("tempId", vo.getTempId());
//			Bill bill = vo.getBill();
//			Order order = vo.getOrder();
//			result.put("billDate", bill.getBillDate());
//			
//			Goods goods = vo.getGoods();
//			result.put("shOffSet", goods.getShOffSet());
//			result.put("shOffSetMax", goods.getShOffSetMax());
//			result.put("firstShOffSet", goods.getFirstShOffSet());
//			result.put("firstShOffSetMax", goods.getFirstShOffSetMax());
//
//			if( bill.getFeeType() == FeeTypeEnum.Default.getValue() ) {
//				result.put("price", order.getPrice());
//				result.put("pay", order.getPay());
//			} else {
//				result.put("balance", bill.getBalance());
//			}
//
//			result.put("feeType", bill.getFeeType());
//			result.put("feeName", bill.getFeeName());
//			result.put("userNo", bill.getBillKey());
//			result.put("userAddress", bill.getUserAddress());
//			result.put("userName", bill.getUserName());
//			
//			Company company = vo.getCompany();
//			result.put("companyName", company.getCompanyName());
//			
//			result.put("campaignId", order.getCampaignId());
//			result.put("serviceType", company.getServiceType());
//			
//			result.put("response", new SimpleResponse(1,"查询成功") );
//		}
//		return result;
//	}
	
	private void calculateOffSet(QueryOrderBillVo vo) {
		
		String offSet, offSetMax;

		Goods goods = vo.getGoods();
		Order order = vo.getOrder();
		
		Date now = new Date();
		Campaign campaign = vo.getCampaign();
		if (campaign != null 
				&& campaign.getStatus() == 1
				&& now.getTime() <= campaign.getEndTime().getTime()
				&& now.getTime() >= campaign.getStartTime().getTime()) {
			offSet = goods.getFirstShOffSet();
			offSetMax = goods.getFirstShOffSetMax();
			order.setCampaignId(campaign.getId());
		} else {
			offSet = goods.getShOffSet();
			offSetMax = goods.getShOffSetMax();
		}

		BigDecimal bdZero = new BigDecimal("0");
		BigDecimal bdPrice = new BigDecimal(order.getPrice());
		BigDecimal bdOffSet = new BigDecimal(offSet);
		BigDecimal bdOffSetMax = new BigDecimal(offSetMax);
		BigDecimal t = bdPrice.multiply(bdOffSet).divide( new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP);
		if(bdOffSetMax.compareTo(bdZero) > 0)
			t = t.min(bdOffSetMax);
		long balance = accountDubbo.getUserSHGlodAmount(order.getUserId());
		BigDecimal bdBalance = new BigDecimal(balance).divide(new BigDecimal("10000"));
		vo.setShGold(bdBalance.toString());
		t =  balance > 0 ? (bdBalance.compareTo(t) >= 0 
				? t : bdZero)
				: bdZero;		
		order.setShOffSet(t.toString());
		order.setPay(bdPrice.subtract(t).toString());
		ApiLogger.info("Service: confirmOrder() : calculateOffSet() : " 
				+ "shOffSet: " + order.getShOffSet()
				+ ", pay: " + order.getPay() );
	}

	public Object confirmOrder(int userId, String tempId, String price) {
		JSONObject result = new JSONObject();

		QueryOrderBillVo vo = cacheDao.getQueryOrderBillVo(tempId);
		if( vo == null) {
			ApiLogger.info("Service: confirmOrder() : vo == null");
			result.put("response", new SimpleResponse(0,"操作超时，请重新查询下单") );
		} else {
			ApiLogger.info("Service: confirmOrder() : " 
					+ "userId: " + userId
					+ ", tempId: " + tempId
					+ ", price: " + price);

			result.put("tempId", vo.getTempId());
			Bill bill = vo.getBill();
			Order order = vo.getOrder();
			//
			Goods goods = vo.getGoods();
			String desc = "【生活缴费】" + goods.getGoodsName()
								 + (bill.getFeeType() == 0 ? "" : "预缴" )
								+ ",户号" + bill.getBillKey();
			result.put("desc", desc);
			//TODO??
			result.put("billDate", bill.getBillDate());	
			if( bill.getFeeType() == FeeTypeEnum.Prepayment.getValue() ) {
				order.setPrice(price);
			}
			//
			calculateOffSet(vo);
			result.put("shOffSet", order.getShOffSet());
			result.put("pay", order.getPay());
			result.put("shGold", vo.getShGold());
			//
			result.put("price", order.getPrice());

			result.put("feeType", bill.getFeeType());
			result.put("feeName", bill.getFeeName());
			result.put("userNo", bill.getBillKey());
			result.put("userAddress", bill.getUserAddress());
			result.put("userName", bill.getUserName());
			Company company = vo.getCompany();
			result.put("companyName", company.getCompanyName());
			
			result.put("campaignId", order.getCampaignId());
			result.put("serviceType", company.getServiceType());
			
			result.put("response", new SimpleResponse(1,"成功") );
			cacheDao.setQueryOrderBillVo(tempId, vo);
		}
		ApiLogger.info("Service: confirmOrder() : " + result.toJSONString());
		return result;
	}
/*
 
order_id 
*goods_id 
*goods_version 
*user_id 
*merchant_id 
*campaign_id 
*price
*pay 
settlement 
*sh_off_set 
payment_type
*order_status 
gid
extend 
remark 
*service_id
audit_id
refund_type 
refund_price
mid
pay_time
consume_time
trans_id
*app_id
*device_id
*create_time
*update_time

 */
	public Object createOrder(int userId, String tempId, int costSh, String ip) {
		ApiResult apiResult;
		QueryOrderBillVo vo = cacheDao.getQueryOrderBillVo(tempId);
		if( vo == null) {
			ApiLogger.info("Service: createOrder() : vo == null");
			apiResult = new ApiResult();
			apiResult.setStatus(0);
			apiResult.setMsg("操作超时，请重新查询下单");

		} else {
			ApiLogger.info("Service: createOrder() : " 
					+ "userId: " + userId
					+ ", tempId: " + tempId
					+ ", costSh: " + costSh
					+ ", ip: " + ip);
			
			ApiLogger.info("Service : createOrder() : QueryOrderBillVo : " + JSON.toJSONString(vo));
			cacheDao.delQueryOrderBillVo(vo.getTempId());
			//
			Order order = vo.getOrder();
			Bill bill = vo.getBill();
		
			if(costSh == 0) {
				order.setPay(order.getPrice());
				order.setShOffSet(new BigDecimal("0").toString());
			}
			//
			Merchant merchant = vo.getMerchant();
			order.setMerchantId(merchant.getMerchantId());
			order.setOrderStatus(OrderStatusEnum.OrderUnpaid.getValue());
						
			Date now = new Date();
			order.setCreateTime(now);
			order.setUpdateTime(now);

			bill.setBillStatus(BillStatusEnum.UnPaid.getValue());
			bill.setUpdateTime(now);
			//
			//
			SingleGoodsCreateOrderParam singleGoodsCreateOrderParam = new SingleGoodsCreateOrderParam();
			Campaign campaign = vo.getCampaign();
			if(campaign != null)
				singleGoodsCreateOrderParam.setCampaignId(campaign.getId());
			Group group = vo.getGroup();
			singleGoodsCreateOrderParam.setCityId(group.getCityId());
			singleGoodsCreateOrderParam.setCommunityId(group.getGid());
			//TODO 添加扩展字段
			com.shihui.openpf.common.model.Service service = vo.getService();
			Goods goods = vo.getGoods();
			JSONObject jo = new JSONObject();
			jo.put("serviceId", service.getServiceId());
			jo.put("merchantId", service.getServiceMerchantId());
			jo.put("categoryId", bill.getCategoryId());
			jo.put("title", service.getServiceName());
			//String title = "【" + service.getServiceName() + "】" + goods.getGoodsName();
			String title = "【生活缴费】" + goods.getGoodsName()
							 + (bill.getFeeType() == 0 ? "" : "预缴" )
							+ ",户号" + bill.getBillKey();
			jo.put("goodsName", title);
			jo.put("appId", order.getAppId());
			Company company = vo.getCompany();
			jo.put("companyName", company.getCompanyName());
			jo.put("userNo", bill.getBillKey());
			jo.put("userName", bill.getUserName());
			singleGoodsCreateOrderParam.setExt(jo.toJSONString());
			//
			singleGoodsCreateOrderParam.setOriginPrice(StringUtil.yuan2hao(order.getPrice()));
			singleGoodsCreateOrderParam.setIp(ip);
			singleGoodsCreateOrderParam.setGoodsVersion(goods.getGoodsVersion());
			singleGoodsCreateOrderParam.setGoodsId(goods.getGoodsId());
			singleGoodsCreateOrderParam.setGoodsName(goods.getGoodsName());
			singleGoodsCreateOrderParam.setUserId(order.getUserId());
			singleGoodsCreateOrderParam.setOrderType(OrderTypeEnum.parse(service.getOrderType()));

			long overTime = System.currentTimeMillis() + 1000 * 60 * 60;
			singleGoodsCreateOrderParam.setOverdueTime(overTime);
			singleGoodsCreateOrderParam.setMerchantId(service.getServiceMerchantId());
			singleGoodsCreateOrderParam.setPrice(StringUtil.yuan2hao(order.getPay()));
			singleGoodsCreateOrderParam.setOffset(StringUtil.yuan2hao(order.getShOffSet()));
			
			singleGoodsCreateOrderParam.setProvinceId(group.getProvinceId());
			singleGoodsCreateOrderParam.setDistrictId(group.getDistrictId());
			
			apiResult = orderSystemService.submitOrder(singleGoodsCreateOrderParam);
			if (apiResult.getStatus() == 1) {
				long orderId = Long.parseLong(apiResult.getOrderId().get(0));
ApiLogger.info("Service: createOrder() : apiResult : orderId : " + orderId);
				order.setOrderId(orderId);
				bill.setOrderId(orderId);
				
				orderDao.save(order);
				billDao.save(bill);
				OrderBillVo obvo = new OrderBillVo();
				obvo.setOrder(order);
				obvo.setBill(bill);
				obvo.setCompany(vo.getCompany());
				cacheDao.setOrderBillVo(orderId, obvo);
			}	
		}
		//
		ApiLogger.info("Service: createOrder() : " + JSON.toJSON(apiResult));
		return JSON.toJSON(apiResult);
	}

}
