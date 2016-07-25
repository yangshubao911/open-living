/**
 * 
 */
package com.shihui.openpf.living.service;

//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import com.shihui.api.core.context.RequestContext;
//import com.shihui.api.order.common.enums.OrderStatusEnum;
//import com.shihui.api.order.common.enums.OrderTypeEnum;
//import com.shihui.api.order.vo.ApiResult;
//import com.shihui.api.order.vo.SingleGoodsCreateOrderParam;
//import com.shihui.openpf.common.dubbo.api.MerchantBusinessManage;
//import com.shihui.openpf.common.dubbo.api.ServiceManage;
//import com.shihui.openpf.common.model.Campaign;
//import com.shihui.openpf.common.model.MerchantBusiness;
//import com.shihui.openpf.common.service.api.CampaignService;
//import com.shihui.openpf.common.tools.StringUtil;
//import com.shihui.openpf.living.api.RechargeProcess;
//import com.shihui.openpf.living.api.bean.ProcessResult;
//import com.shihui.openpf.living.dao.OrderDao;
//import com.shihui.openpf.living.dao.MerchantGoodsDao;
//import com.shihui.openpf.living.entity.MerchantGoods;
//import com.shihui.openpf.living.entity.Order;
//import com.shihui.openpf.living.entity.Request;
//import com.shihui.openpf.living.entity.support.PositionMap;
//import com.shihui.openpf.living.entity.support.ProviderEnum;
//import com.shihui.openpf.living.exception.SimpleRuntimeException;
//import com.shihui.openpf.living.util.ChoiceMerhantUtil;
//import com.shihui.rpc.user.mobile.PhoneNumber;
//import com.shihui.rpc.user.model.User;
//import com.shihui.rpc.user.model.UserIdx;
//import com.shihui.rpc.user.service.api.UserService;
//import com.shihui.tradingcenter.commons.dispatcher.currency.AccountDubbo;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shihui.openpf.common.service.api.ServiceService;
import com.shihui.openpf.living.dao.BannerAdsDao;
import com.shihui.openpf.living.dao.BillDao;
import com.shihui.openpf.living.dao.GoodsDao;
import com.shihui.openpf.living.dao.CompanyDao;
import com.shihui.openpf.living.dao.CategoryDao;
import com.shihui.openpf.living.entity.BannerAds;
import com.shihui.openpf.living.entity.Goods;
import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Company;
import com.shihui.openpf.living.entity.Category;
import com.shihui.openpf.living.entity.support.BannerAdsEnum;

/**
 * @author zhouqisheng
 *
 */
@Service
public class ClientService {
	
	@Resource
	private BannerAdsDao bannerAdsDao;
	@Resource
	private BillDao billDao;
	@Resource
	private ServiceService serviceService;
	@Resource
	private CategoryDao categoryDao;
	
	@Resource
	private GoodsDao goodsDao;
	
	@Resource
	private CompanyDao companyDao;
	
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
			ja.add(jo);
		}
		return ja;
	}
	/*
	 * 
	 * 
	 * 
	 * 
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
			ja.add(jo);
		}
		return result;
	}
	/*
	 * 
	 * 
	 * 
	 * 
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
			ja.add(jo);
		}
		return result;
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	public Object queryFee(Integer userId, Integer serviceId, Integer cityId) {
		JSONObject result = new JSONObject();
//TODO	??????????????????????????????????????????????????????????????	
		return result;
	}
		
}
