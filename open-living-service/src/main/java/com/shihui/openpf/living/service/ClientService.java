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
import com.shihui.openpf.living.entity.support.FeeTypeEnum;
import com.shihui.openpf.living.entity.support.OrderBillVo;
import com.shihui.openpf.living.entity.support.QueryOrderBillVo;
import com.shihui.openpf.living.io3rd.GuangdaDao;
import com.shihui.openpf.living.io3rd.ReqQuery;
import com.shihui.openpf.living.mq.LivingMqProducer;
import com.shihui.openpf.living.util.SimpleResponse;
import com.shihui.tradingcenter.commons.dispatcher.currency.AccountDubbo;

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
	 * 
	 * 
	 * 
	 * 
	 */
	public Object homepage(Long userId, Integer cityId, Integer historyOrderCount) {
		JSONObject result = new JSONObject();
		
		// 查询广告
		List<BannerAds> adsList = bannerAdsDao.queryForClient(BannerAdsEnum.HOME.getPostion());
		result.put("bannerAds", adsList);
		
		//查询服务
		result.put("categoryList", getCategoryList());
		
		//历史订单Top5
		result.put("billList", getBillList(userId, historyOrderCount));
		
		//
		return result;
	}
	
	private JSONArray getCategoryList() {
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
	public Object queryCity(Integer categoryId) {
		JSONObject result = new JSONObject();
		
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
		return result;
	}
	/*
	 * 
	 */
	public Object queryCompany(Integer serviceId, Integer cityId) {
		JSONObject result = new JSONObject();
		
		result.put("serviceId", serviceId);
		result.put("cityId", cityId);
		
		List<Company> companyList = companyDao.queryList(cityId,serviceService.findById(serviceId).getOrderType());
		
		JSONArray ja = new JSONArray();
		result.put("company_list", ja);
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
		return result;
	}

	/*
	 * 
	 */
	public Object queryFee(int userId, long groupId, Long mid,
			int serviceId, int categoryId, int cityId, long goodsId, int goodsVersion, 
			int companyId, String companyNo, String userNo,
			String deviceId, int appId) {
		JSONObject result = new JSONObject();

		String tempId = cacheDao.getTrmSeqNum();
		
		ReqQuery reqQuery = ReqQuery.instance( 
				tempId, 
				userNo, 
				companyNo, null, null, null, null);
		
		if(mqProducer.sendQueryRequest(tempId, JSON.toJSONString(reqQuery))) {
			result.put("tempId", tempId);
			result.put("response", new SimpleResponse(1,"已提交查询，请等待查询结果") );
			//TODO
			QueryOrderBillVo vo = new QueryOrderBillVo();
			vo.setTempId(tempId);
			vo.setGroupId(groupId);
			Order order = new Order();
			Bill bill = new Bill();
			order.setUserId(userId);
			order.setMid(mid);
			order.setServiceId(serviceId);
			order.setGoodsId(goodsId);
			order.setGoodsVersion(goodsVersion);
			order.setDeviceId(deviceId);
			order.setAppId(appId);
			bill.setCompanyId(companyId);
			bill.setBillKey(userNo);
			bill.setServiceId(serviceId);
			bill.setCategoryId(categoryId);
			vo.setOrder(order);
			vo.setBill(bill);
			vo.setCompanyNo(companyNo);
			vo.setCategoryId(categoryId);
			cacheDao.setQueryOrderBillVo(tempId, vo);
		}
		else {
			result.put("response", new SimpleResponse(0,"业务繁忙，查询失败，请稍侯再试") );
		}

		return result;
	}
	
	public Object checkQuery(int userId, String tempId) {
		JSONObject result = new JSONObject();

		QueryOrderBillVo vo = cacheDao.getQueryOrderBillVo(tempId);
		if( vo == null) {
			result.put("response", new SimpleResponse(0,"查询中，请耐心等待") );
		} else {
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
		}
		return result;
	}
	
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

		BigDecimal bdPrice = new BigDecimal(order.getPrice());
		BigDecimal bdOffSet = new BigDecimal(offSet);
		BigDecimal bdOffSetMax = new BigDecimal(offSetMax);
		BigDecimal t = bdPrice.multiply(bdOffSet);
		t = t.min(bdOffSetMax);
		
		long balance = accountDubbo.getUserSHGlodAmount(order.getUserId());
		t =  balance > 0 ? (new BigDecimal(balance).divide(new BigDecimal("10000")).compareTo(t) >= 0 
				? t : new BigDecimal("0"))
				:new BigDecimal("0");		

		order.setShOffSet(t.toString());
		order.setPay(bdPrice.subtract(t).toString());
	}
	
	public Object confirmOrder(int userId, String tempId, String price) {
		JSONObject result = new JSONObject();

		QueryOrderBillVo vo = cacheDao.getQueryOrderBillVo(tempId);
		if( vo == null) {
			result.put("response", new SimpleResponse(0,"操作超时，请重新查询下单") );
		} else {

			result.put("tempId", vo.getTempId());
			Bill bill = vo.getBill();
			Order order = vo.getOrder();
			//
			result.put("billDate", bill.getBillDate());
			
			if( bill.getFeeType() == FeeTypeEnum.Prepayment.getValue() ) {
				order.setPrice(price);
			}
			//
			calculateOffSet(vo);
			result.put("shOffSet", order.getShOffSet());
			result.put("pay", order.getPay());
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
			apiResult = new ApiResult();
			apiResult.setStatus(0);
			apiResult.setMsg("操作超时，请重新查询下单");

		} else {
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
			jo.put("categoryId", vo.getCategoryId());
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
			singleGoodsCreateOrderParam.setOriginPrice(StringUtil.yuan2hao(goods.getPrice()));
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
				order.setOrderId(orderId);
				bill.setOrderId(orderId);
				
				orderDao.save(order);
				billDao.save(bill);
				
				OrderBillVo obvo = new OrderBillVo();
				obvo.setOrder(order);
				obvo.setBill(bill);
				cacheDao.setOrderBillVo(orderId, obvo);
			}			
		}
		//
		return JSON.toJSON(apiResult);
	}

}
