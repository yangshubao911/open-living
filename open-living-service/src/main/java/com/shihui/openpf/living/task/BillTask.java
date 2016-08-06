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
import com.shihui.openpf.common.model.MerchantBusiness;
import com.shihui.openpf.common.model.Service;
import com.shihui.openpf.common.service.api.ServiceService;
import com.shihui.openpf.common.tools.StringUtil;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.dao.MerchantGoodsDao;
import com.shihui.openpf.living.dao.OrderDao;
import com.shihui.openpf.living.entity.support.OrderVo;
import com.shihui.openpf.living.entity.support.QueryOrderBillVo;

@Component
public class BillTask {
	
//	private Logger log = LoggerFactory.getLogger(getClass());
//	private int flowRechargeServiceId;
//	@Resource
//	private OrderCache orderCache;
//	@Resource
//	private OrderDao orderDao;
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
	


	@PostConstruct
	public void init() {

	}

	@Scheduled(cron = "0 30 2 * * ?")
	public void billCheckNotify() {

	}

}
