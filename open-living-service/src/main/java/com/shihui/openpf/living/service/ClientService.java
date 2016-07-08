/**
 * 
 */
package com.shihui.openpf.living.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shihui.api.core.context.RequestContext;
import com.shihui.api.order.common.enums.OrderStatusEnum;
import com.shihui.api.order.common.enums.OrderTypeEnum;
import com.shihui.api.order.vo.ApiResult;
import com.shihui.api.order.vo.SingleGoodsCreateOrderParam;
import com.shihui.openpf.common.dubbo.api.MerchantBusinessManage;
import com.shihui.openpf.common.dubbo.api.ServiceManage;
import com.shihui.openpf.common.model.Campaign;
import com.shihui.openpf.common.model.MerchantBusiness;
import com.shihui.openpf.common.service.api.CampaignService;
import com.shihui.openpf.common.service.api.ServiceService;
import com.shihui.openpf.common.tools.StringUtil;
//import com.shihui.openpf.living.api.RechargeProcess;
//import com.shihui.openpf.living.api.bean.ProcessResult;
import com.shihui.openpf.living.dao.BannerAdsDao;
import com.shihui.openpf.living.dao.GoodsDao;
import com.shihui.openpf.living.dao.MerchantGoodsDao;
//import com.shihui.openpf.living.dao.OrderDao;
//import com.shihui.openpf.living.dao.PhoneSectionDao;
import com.shihui.openpf.living.entity.BannerAds;
import com.shihui.openpf.living.entity.Goods;
import com.shihui.openpf.living.entity.MerchantGoods;
import com.shihui.openpf.living.entity.Order;
//import com.shihui.openpf.living.entity.PhoneSection;
//import com.shihui.openpf.living.entity.Request;
import com.shihui.openpf.living.entity.support.BannerAdsEnum;
//import com.shihui.openpf.living.entity.support.PositionMap;
//import com.shihui.openpf.living.entity.support.ProviderEnum;
import com.shihui.openpf.living.exception.SimpleRuntimeException;
//import com.shihui.openpf.living.util.ChoiceMerhantUtil;
import com.shihui.rpc.user.mobile.PhoneNumber;
import com.shihui.rpc.user.model.User;
import com.shihui.rpc.user.model.UserIdx;
import com.shihui.rpc.user.service.api.UserService;
import com.shihui.tradingcenter.commons.dispatcher.currency.AccountDubbo;

/**
 * @author zhouqisheng
 *
 */
@Service
public class ClientService {
	@Resource
	private BannerAdsDao bannerAdsDao;
//	@Resource
//	private PhoneSectionDao phoneSectionDao;
	@Resource
	private GoodsDao goodsDao;
	@Resource
	private MerchantGoodsDao merchantGoodsDao;
//	@Resource
//	private OrderDao orderDao;
	@Resource
	private UserService userServicenew;
	@Resource
	private AccountDubbo accountDubbo;
	@Resource
	private ServiceManage serviceManage;
	@Resource
	private ServiceService serviceService;
	@Resource
	private CampaignService campaignService;
//	@Resource
//	private RechargeOrderService rechargeOrderService;
//	@Resource
//	private RechargeProcess rechargeProcess;
//	@Resource
//	private RechargeRequestService rechargeRequestService;
	@Resource
	private MerchantBusinessManage merchantBusinessManage;

	//private Logger log = LoggerFactory.getLogger(getClass());

	public Object home(Long userId, Integer cityId, Integer historyOrderCount) {
		JSONObject result = new JSONObject();

		// 查询广告
		List<BannerAds> adsList = bannerAdsDao.queryForClient(BannerAdsEnum.HOME.getPostion());
		result.put("bannerAds", adsList);
		//查询服务
		JSONArray serviceList = new JSONArray();
		result.put("serviceList", serviceList);

		com.shihui.openpf.common.model.Service wFreeService, LFreeService, gasFreeService;

		wFreeService = getServiceObject(4/*TODO 生活缴费类的服务待定义,水费*/, OrderTypeEnum.Convenient_WFee.getValue());
		LFreeService = getServiceObject(5/*TODO 生活缴费类的服务待定义,电费*/, OrderTypeEnum.Convenient_LFee.getValue());
		gasFreeService = getServiceObject(6/*TODO 生活缴费类的服务待定义,,煤气费*/, OrderTypeEnum.Convenient_GasFee.getValue());


		if(wFreeService != null)
			serviceList.add(getServiceInfo(wFreeService, cityId));
		if(LFreeService != null)
			serviceList.add(getServiceInfo(LFreeService, cityId));
		if(gasFreeService != null)
			serviceList.add(getServiceInfo(gasFreeService, cityId));
		//查询历史订单
		int orderCount = (historyOrderCount == null || historyOrderCount==0)? 5 : historyOrderCount;
		JSONArray historyOrderList = getHistoryOrderList(userId, orderCount);
		if(historyOrderList != null)
			result.put("historyOrderList", historyOrderList);
		
		return result;
	}

	private com.shihui.openpf.common.model.Service getServiceObject(int type, int orderType) {
		List<com.shihui.openpf.common.model.Service> serviceList = serviceService.listBytype(type);
		for(com.shihui.openpf.common.model.Service service : serviceList){
			if(service.getOrderType().equals(orderType)){
				return service;
			}
		}
		return null;
	}
	
	private JSONObject getServiceInfo(com.shihui.openpf.common.model.Service service, Integer cityId) {
		JSONObject joService = new JSONObject();
		
		Integer serviceId = service.getServiceId();
		
		joService.put("serviceId", service.getServiceId());
		joService.put("serviceName", service.getServiceName());
		// 查询商品
		JSONArray goodsInfo = new JSONArray();
		joService.put("goodsInfo", goodsInfo);
		List<Goods> goodsList = goodsDao.queryForClient(serviceId, cityId);
		if (goodsList.size() != 0) {
			for (Goods goods : goodsList) {
				JSONObject jo = new JSONObject();
				jo.put("goodsId", goods.getGoodsId());
				jo.put("goodsName", goods.getGoodsName());
				jo.put("desc", goods.getGoodsDesc());
				jo.put("subtitle", goods.getGoodsSubtitle());
				jo.put("status", goods.getGoodsStatus());
				goodsInfo.add(jo);
				break;
			}
		}
		return joService;
	}
	
	private JSONArray getHistoryOrderList(Long userId, int orderCount) {
		JSONArray ja = new JSONArray();
		return ja;
	}
}
