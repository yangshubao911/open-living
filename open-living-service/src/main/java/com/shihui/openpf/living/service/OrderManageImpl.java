package com.shihui.openpf.living.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shihui.api.order.common.enums.OrderStatusEnum;
import com.shihui.api.order.common.enums.OrderTypeEnum;
import com.shihui.api.order.common.enums.PayTypeEnum;
import com.shihui.api.order.emodel.OperatorTypeEnum;
import com.shihui.api.order.emodel.RefundModeEnum;
import com.shihui.api.order.vo.SimpleResult;
import com.shihui.openpf.common.dubbo.api.MerchantBusinessManage;
import com.shihui.openpf.common.dubbo.api.MerchantManage;
import com.shihui.openpf.common.dubbo.api.ServiceManage;
import com.shihui.openpf.common.model.Group;
import com.shihui.openpf.common.model.Merchant;
import com.shihui.openpf.common.model.MerchantBusiness;
import com.shihui.openpf.common.service.api.GroupManage;
import com.shihui.openpf.common.tools.AlgorithmUtil;
import com.shihui.openpf.common.tools.DataExportUtils;
import com.shihui.openpf.common.tools.SignUtil;
import com.shihui.openpf.common.tools.StringUtil;
import com.shihui.openpf.home.model.Category;
import com.shihui.openpf.home.model.Contact;
import com.shihui.openpf.home.model.Goods;
import com.shihui.openpf.home.model.HomeCodeEnum;
import com.shihui.openpf.home.model.HomeOrderStatusEnum;
import com.shihui.openpf.home.model.HomeResponse;
import com.shihui.openpf.home.model.MerchantGoods;
import com.shihui.openpf.home.model.Order;
import com.shihui.openpf.home.model.Request;
import com.shihui.openpf.home.model.YjzOrderStatusEnum;
import com.shihui.openpf.home.model.YjzUpdateResult;
import com.shihui.openpf.home.service.api.CategoryService;
import com.shihui.openpf.home.service.api.ContactService;
import com.shihui.openpf.home.service.api.GoodsService;
import com.shihui.openpf.home.service.api.HomeServProviderService;
import com.shihui.openpf.home.service.api.MerchantGoodsService;
import com.shihui.openpf.home.service.api.OrderManage;
import com.shihui.openpf.home.service.api.OrderService;
import com.shihui.openpf.home.service.api.OrderSystemService;
import com.shihui.openpf.home.service.api.RequestService;


/**
 * Created by zhoutc on 2016/1/21.
 */
@Service
public class OrderManageImpl implements OrderManage {

	@Resource(name = "openOrderService")
	OrderService orderService;
	@Resource
	GoodsService goodsService;
	@Resource
	ContactService contactService;
	@Resource
	MerchantManage merchantManage;
	@Resource
	HomeServProviderService homeServProviderService;
	@Resource
	RequestService requestService;
	@Resource
	MerchantGoodsService merchantGoodsService;
	@Resource
	ServiceManage serviceManage;
	@Resource
	MerchantBusinessManage merchantBusinessManage;
	@Resource
	GroupManage groupManage;
	@Resource
	OrderSystemService orderSystemService;
	@Resource
	CategoryService categoryService;
	private CloseableHttpClient httpClient;

	private Logger log = LoggerFactory.getLogger(getClass());

	@Value("${file_upload_url}")
	private String fileUploadUrl;

	@PostConstruct
	public void init() {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(2000)
				.setContentCompressionEnabled(false)
				.setSocketTimeout(3000)
				.build();
		httpClientBuilder.setDefaultRequestConfig(requestConfig);
		this.httpClient = httpClientBuilder.build();
	}

	/**
	 * OPS查询订单
	 *
	 * @param queryOrder
	 *            查询订单条件
	 * @return 返回结果
	 */
	@Override
	public String queryOrderList( Order queryOrder, String startTime, String endTime, int page,
			int size) {
		try {
			JSONObject result = new JSONObject();
			JSONArray orders_json = new JSONArray();
			// Order queryOrder = JSON.parseObject(json, Order.class);
			int total = orderService.countQueryOrder(queryOrder, startTime, endTime);
			result.put("total", total);
			result.put("page", page);
			result.put("size", size);
			if (total <= 0)
				return result.toJSONString();
			List<Order> orderList = orderService.queryOrderList(queryOrder, startTime, endTime, page, size);

			List<Integer> merchants = new ArrayList<>();
			for (Order order : orderList) {
				merchants.add(order.getMerchantId());
			}

			for (Order order : orderList) {
				JSONObject order_json = buildOrderVo(order);
				if (order_json != null)
					orders_json.add(order_json);
			}
			result.put("orders", orders_json);
			return result.toJSONString();
		} catch (Exception e) {
			log.error("OrderManageImpl queryOrderList error!!", e);
		}
		return "";
	}

	/**
	 * 导出订单
	 * @param queryOrder 查询订单请求数据
	 *
	 * @return 返回结果
	 */
	@Override
	public String exportOrderList(Order queryOrder, String startTime, String endTime) {
		JSONObject result = new JSONObject();
		List<Order> orderList = orderService.queryOrderList(queryOrder, startTime, endTime , null , null);
		List<String> title = new ArrayList<>();
		title.add("实惠订单号");
		title.add("第三方订单号");
		title.add("交易ID");
		title.add("实惠ID");
		title.add("订单状态");
		title.add("下单时间");
		title.add("完成时间");
		title.add("购买服务");
		title.add("姓名");
		title.add("手机号");
		title.add("城市");
		title.add("地址");
		title.add("实惠价");
		title.add("现金补贴");
		title.add("用户支付金额");
		title.add("是否首次订单");
		title.add("支付类型");
		title.add("服务提供商");
		title.add("要求服务时间");
		List<List<Object>> data = new ArrayList<>();
		if(orderList!=null && orderList.size()>0)
		for(Order order : orderList){
			List<Object> list = new ArrayList<>();
			list.add(String.valueOf(order.getOrderId()));
			list.add(String.valueOf(order.getRequestId()));
			String payTypeName = "未知";
			String transId = "";
			SimpleResult simpleResult = orderSystemService.backendOrderDetail(order.getOrderId());
			if(simpleResult.getStatus()==1) {
				com.shihui.api.order.po.Order order_vo = (com.shihui.api.order.po.Order) simpleResult.getData();
				PayTypeEnum payType = order_vo.getPayType();
				transId = String.valueOf(order_vo.getTransId());
				if(payType!=null){
					payTypeName = payType.getName();
				}
			}
			list.add(transId);
			list.add(order.getUserId());
			list.add(OrderStatusEnum.parse(order.getOrderStatus()).getName());
			list.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateTime()));
			if(order.getOrderStatus()==OrderStatusEnum.OrderHadReceived.getValue()) {
				list.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getUpdateTime()));
			}else {
				list.add("");
			}
			Goods goods = goodsService.findById(order.getGoodsId());
			if(goods==null){
				list.add("");
			}else {
				list.add(goods.getGoodsName());
			}
			Contact contact = contactService.queryByOrderId(order.getOrderId());
			if(contact==null){
				list.add("");
			}else {
				list.add(contact.getContactName());
			}
			list.add(order.getPhone());
			list.add(goods.getCityName());
			list.add(contact.getServiceAddress()+contact.getDetailAddress());
			list.add(order.getPrice());
			list.add(order.getShOffSet());
			list.add(order.getPay());
			if(order.getCampaignId()==2) {
				list.add("是");
			}else{
				list.add("否");
			}

			list.add(payTypeName);
			Merchant merchant = merchantManage.getById(order.getMerchantId());
			if(merchant==null)
				list.add("");
			else
				list.add(merchant.getMerchantName());

			list.add(order.getServiceStartTime());
			data.add(list);
		}
		String fileName = null;
		try {
			fileName = DataExportUtils.genExcel("open_home_" + System.currentTimeMillis()+".xlsx", "订单", title, data,
					"utf-8");
		} catch (Exception e) {
			log.error("export order list error!!!",e);
			result.put("code", 2);
			return result.toJSONString();
		}
		String fileId = "";
		try {
			fileId = uploadFile(fileName);
		}catch (Exception e){
			log.error("upload file error!!!",e);
			result.put("code",2);
			return result.toJSONString();
		}

		result.put("code",1);
		result.put("fileId", fileId);
		return result.toJSONString();
	}

	/**
	 * 上传文件流至TFS服务器
	 *
	 * @return
	 */
	public String uploadFile(String filePath) {
		JSONObject result = null;
		log.info("开始上传文件：{}", filePath);
		File file = new File(filePath);
		try {
			HttpPost post = new HttpPost(fileUploadUrl);
			FileBody bin = new FileBody(file);
			MultipartEntityBuilder reqEntityBuilder = MultipartEntityBuilder.create()
					.addPart("file", bin);
			post.setEntity(reqEntityBuilder.build());
			try(CloseableHttpResponse response = httpClient.execute(post)) {
				String executeAsyncString = EntityUtils.toString(response.getEntity(), "utf8");

				result = JSON.parseObject(executeAsyncString);

				if (null == result.get("fileid")) {
					log.info("保存订单明细表至TFS失败！返回信息：{}", executeAsyncString);
					return null;
				}
				log.info("完成文件上传：{}，返回信息:{} ",filePath, result.toJSONString());

				return result.getString("fileid");
			}
		} catch (Exception e) {
			log.error("上传文件至TFS出错!".concat(JSON.toJSONString(result)));
			return null;
		} finally {
			file.delete();
		}
	}

	public JSONObject buildOrderVo(Order order) {
		try {
			JSONObject order_json = new JSONObject();
			order_json.put("orderId", String.valueOf(order.getOrderId()));
			order_json.put("userId", order.getUserId());
			order_json.put("phone", order.getPhone());
			order_json.put("serviceId", order.getService_id());

			order_json.put("price", order.getPrice());
			order_json.put("shOffset", order.getShOffSet());
			order_json.put("merchantId", order.getMerchantId());
			Merchant merchant = merchantManage.getById(order.getMerchantId());
			order_json.put("merchantName", merchant.getMerchantName());
			order_json.put("settlement", order.getSettlement());
			DateTime dateTime = new DateTime(order.getCreateTime());
			order_json.put("createTime", dateTime.toString("yyyy-MM-dd HH:mm:ss"));
			order_json.put("pay", order.getPay());
			order_json.put("status", order.getOrderStatus());
			order_json.put("statusName", OrderStatusEnum.parse(order.getOrderStatus()).getName());
			return order_json;
		} catch (Exception e) {
			log.error("OrderManageImpl buildOrderVo error!!", e);
		}

		return null;
	}

	/**
	 * 查询订单详情
	 *
	 * @param orderId
	 *            订单ID
	 * @return 返回订单详情
	 */

	@Override
	public String queryOrder(long orderId) {
		try {
			JSONObject result = new JSONObject();
			result.put("orderId", String.valueOf(orderId));
			Order order = orderService.queryOrder(orderId);

			if (order == null)
				return null;

			Contact contact = contactService.queryByOrderId(orderId);

			if(contact!=null){
				result.put("serviceStartTime",contact.getServiceStartTime());
				result.put("serviceAddress",contact.getServiceAddress()+contact.getDetailAddress());
				result.put("contactName", contact.getContactName());
				result.put("contactPhone", contact.getPhoneNum());
			}



			SimpleResult simpleResult = orderSystemService.backendOrderDetail(orderId);
			/*JSONObject json = JSONObject.parseObject(simpleResult.getData().toString());
			com.shihui.api.order.po.Order order_vo = json.getObject("order", com.shihui.api.order.po.Order.class);*/
			if(simpleResult.getStatus()==1) {
				com.shihui.api.order.po.Order order_vo = (com.shihui.api.order.po.Order)simpleResult.getData();
				PayTypeEnum payType = order_vo.getPayType();
				if(payType!=null)
				 result.put("payType", payType.getValue());
				result.put("transId", String.valueOf(order_vo.getTransId()));
				result.put("payTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order_vo.getPaymentTime())));
				if (order.getOrderStatus() == OrderStatusEnum.OrderHadReceived.getValue()) {
					result.put("consumeTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getUpdateTime()));
				}
			}else {
				log.info("queryOrder--orderId:" + orderId + " backendOrderDetail status:" + simpleResult.getStatus() + " msg:" + simpleResult.getMsg());
			}

			Goods goods = goodsService.findById(order.getGoodsId());
			if (goods != null) {
				Category category = new Category();
				category.setId(goods.getCategoryId());
				Category db_category = categoryService.findById(category);

				if(db_category!=null){
					result.put("amount", db_category.getAmount());
				}

				result.put("goodsName", goods.getGoodsName());

			}
			result.put("createTime", new DateTime(order.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"));
			result.put("userId", order.getUserId());
			result.put("phone", order.getPhone());
			
			Request request = requestService.queryOrderRequest(order.getOrderId());
			if(request != null){
				result.put("serverName", request.getServerName());
				result.put("serverPhone", request.getServerPhone());
			}


			result.put("price", order.getPrice());

			Merchant merchant = merchantManage.getById(order.getMerchantId());
			result.put("merchantId", order.getMerchantId());
			result.put("merchantName", merchant.getMerchantName());

			MerchantGoods merchantGoods = new MerchantGoods();
			merchantGoods.setGoodsId(order.getGoodsId());
			merchantGoods.setMerchantId(order.getMerchantId());
			MerchantGoods db_merchantGoods = merchantGoodsService.queryMerchantGoods(merchantGoods);

			result.put("settlement", db_merchantGoods.getSettlement());
			BigDecimal originalPrice = new BigDecimal(order.getPay()).add(new BigDecimal(order.getShOffSet()));
			result.put("originalPrice", originalPrice.stripTrailingZeros().toPlainString());
			result.put("pay", order.getPay());
			result.put("shOffset", order.getShOffSet());
			result.put("status", order.getOrderStatus());
			result.put("statusName", OrderStatusEnum.parse(order.getOrderStatus()).getName());
			result.put("due", new BigDecimal(goods.getPrice()).subtract(new BigDecimal(goods.getShOffSet()))
					.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			return result.toJSONString();
		} catch (Exception e) {
			log.error("OrderManageImpl queryOrder error!!", e);
		}

		return "";
	}

	/**
	 * 查询第三方订单
	 *
	 * @param orderId
	 *            第三方订单ID
	 * @return 返回订单详情
	 */
	@Override
	public String queryThirdOrder(String key, Integer serviceType, String orderId, String version, String sign) {
		try {
			Merchant merchant = merchantManage.getByKey(key);
			if (merchant == null) {
				return LivingCodeEnum.PARAM_ERR.toJSONString("key 不存在");
			}
			com.shihui.openpf.common.model.Service service = serviceManage.findById(serviceType);
			if (service == null) {
				return LivingCodeEnum.SERVICE_NA.toJSONString();
			}
			MerchantBusiness search = new MerchantBusiness();
			search.setMerchantId(merchant.getMerchantId());
			search.setServiceId(service.getServiceId());
			MerchantBusiness merchantBusiness = merchantBusinessManage.queryById(search);
			if (merchantBusiness == null) {
				return LivingCodeEnum.SERVICE_NA.toJSONString();
			}
			if (merchant.getMerchantStatus() != 1 || service.getServiceStatus() != 1
					|| merchantBusiness.getStatus() != 1) {
				return LivingCodeEnum.SERVICE_NA.toJSONString();
			}

			TreeMap<String, String> param = new TreeMap<>();
			param.put("key", key);
			param.put("serviceType", String.valueOf(serviceType));
			param.put("orderId", orderId);
			param.put("version", version);
			String server_sign = SignUtil.genSign(param, merchant.getMd5Key());
			if (server_sign.compareTo(sign) != 0) {
				return LivingCodeEnum.SIGN_ERR.toJSONString();
			}
			Request request = new Request();
			request.setRequestId(orderId);
			request.setMerchantId(merchant.getMerchantId());
			Request db_request = requestService.queryById(request);
			if (db_request == null) {
				return LivingCodeEnum.ORDER_NA.toJSONString();
			}
			Order order = orderService.queryOrder(db_request.getOrderId());
			if (order == null) {
				return LivingCodeEnum.ORDER_NA.toJSONString();
			}

			Goods goods = goodsService.findById(order.getGoodsId());
			if (goods == null) {
				return LivingCodeEnum.OTHER_ERR.toJSONString();
			}
			Contact contact = contactService.queryByOrderId(db_request.getOrderId());

			JSONObject result = new JSONObject();
			result.put("code", 0);
			result.put("msg", "查询成功");

			JSONObject order_json = new JSONObject();
			order_json.put("orderId", orderId);

			Group group = groupManage.getGroupInfoByGid(order.getGid());
			if (group != null) {
				order_json.put("cityId", group.getCityId());
				order_json.put("cityName", group.getName());
			}
			order_json.put("serviceAddress", contact.getServiceAddress());
			order_json.put("detailAddress", contact.getDetailAddress());
			order_json.put("longitude", contact.getLongitude());
			order_json.put("latitude", contact.getLatitude());
			order_json.put("phone", contact.getPhoneNum());
			order_json.put("amount", 1);
			order_json.put("goodsId", goods.getCategoryId());
			order_json.put("serviceStartTime", contact.getServiceStartTime());
			order_json.put("remark", order.getRemark());
			order_json.put("extend", order.getExtend());
			result.put("result", order_json);
			return result.toJSONString();
		} catch (Exception e) {
			log.error("OrderManageImpl queryThirdOrder error", e);
			return LivingCodeEnum.SYSTEM_ERR.toJSONString();
		}

	}

	/**
	 * 取消第三方订单
	 *
	 * @param orderId
	 *            订单ID
	 * @return 返回取消订单结果
	 */
	@Override
	public String cancelThirdOrder(String key, int serviceType, String orderId, String version, String sign) {
		try {
			Merchant merchant = merchantManage.getByKey(key);
			if (merchant == null) {
				return LivingCodeEnum.PARAM_ERR.toJSONString("key 不存在");
			}
			com.shihui.openpf.common.model.Service service = serviceManage.findById(serviceType);
			if (service == null) {
				return LivingCodeEnum.SERVICE_NA.toJSONString();
			}
			MerchantBusiness search = new MerchantBusiness();
			search.setMerchantId(merchant.getMerchantId());
			search.setServiceId(service.getServiceId());
			MerchantBusiness merchantBusiness = merchantBusinessManage.queryById(search);
			if (merchantBusiness == null) {
				return LivingCodeEnum.SERVICE_NA.toJSONString();
			}
			if (merchant.getMerchantStatus() != 1 || service.getServiceStatus() != 1
					|| merchantBusiness.getStatus() != 1) {
				return LivingCodeEnum.SERVICE_NA.toJSONString();
			}

			TreeMap<String, String> param = new TreeMap<>();
			param.put("key", key);
			param.put("serviceType", String.valueOf(serviceType));
			param.put("orderId", orderId);
			param.put("version", version);
			String server_sign = SignUtil.genSign(param, merchant.getMd5Key());
			if (server_sign.compareTo(sign) != 0) {
				return LivingCodeEnum.SIGN_ERR.toJSONString();
			}
			Request request = new Request();
			request.setRequestId(orderId);
			request.setMerchantId(merchant.getMerchantId());
			Request db_request = requestService.queryById(request);
			if (db_request == null) {
				return LivingCodeEnum.ORDER_NA.toJSONString();
			}
			Order order = orderService.queryOrder(db_request.getOrderId());
			if (order == null) {
				return LivingCodeEnum.ORDER_NA.toJSONString();
			}

			OrderStatusEnum status = OrderStatusEnum.parse(order.getOrderStatus());
			switch (status) {
			case OrderUnpaid:
				if (this.orderSystemService.updateOrderStatus(order.getOrderId(), status,
						OrderStatusEnum.OrderCloseByOutTime, OperatorTypeEnum.User, merchant.getMerchantId(), "")) {
					// 更新request表
					request.setRequestStatus(HomeOrderStatusEnum.OrderCancel.getValue());
					this.requestService.updateStatus(request);
					return LivingCodeEnum.SUCCESS.toJSONString();
				} else {
					return LivingCodeEnum.SYSTEM_ERR.toJSONString();
				}
			case OrderUnStockOut://未出库
			case OrderDistribute://配送中
				request.setRequestStatus(HomeOrderStatusEnum.OrderCancel.getValue());
				this.requestService.updateStatus(request);
				// 商户取消订单，全额退款，无需审核，退回实惠现金
				SimpleResult sr = orderSystemService.merchantCancel(order.getOrderId(), merchant.getMerchantCode(), StringUtil.yuan2hao(order.getPay()), order.getOrderStatus(), "商户取消订单");
				log.info("Third Cancel  status:" +sr.getStatus() + " msg" + sr.getMsg() + " data" + sr.getData());
				if (sr.getStatus() == 1) {
					// 保存审核id
					Order updateOrder = new Order();
					updateOrder.setOrderId(order.getOrderId());
					updateOrder.setAuditId((long) sr.getData());
					updateOrder.setRefundType(1);//全额退款
					updateOrder.setRefundPrice(order.getPay());
					updateOrder.setUpdateTime(new Date());
					this.orderService.update(updateOrder);
				}else{
					log.error("商户取消订单并发起退款失败，订单号={}，原订单状态={}", order.getOrderId(), order.getOrderStatus());
				}
				return LivingCodeEnum.SUCCESS.toJSONString();
			case OrderCancelByCustom:
			case OrderCloseByOutTime:
			case PayedCancel:
			case BackClose:
				return LivingCodeEnum.SUCCESS.toJSONString();
			default:
				return LivingCodeEnum.CANCEL_FAIL.toJSONString("订单不允许取消");
			}
		} catch (Exception e) {
			log.error("第三方取消订单异常，orderid={}", orderId, e);
			return LivingCodeEnum.SYSTEM_ERR.toJSONString();
		}
	}

	/**
	 * 更新第三方订单
	 *
	 * @param orderId
	 *            订单ID
	 * @return 返回更新订单结果
	 */
	@Override
	public String updateThirdOrder(String key, int serviceType, String orderId, String version, String sign,
			int status) {
		try {
			Merchant merchant = merchantManage.getByKey(key);
			if (merchant == null) {
				return LivingCodeEnum.PARAM_ERR.toJSONString("key 不存在");
			}
			com.shihui.openpf.common.model.Service service = serviceManage.findById(serviceType);
			if (service == null) {
				return LivingCodeEnum.SERVICE_NA.toJSONString();
			}
			MerchantBusiness search = new MerchantBusiness();
			search.setMerchantId(merchant.getMerchantId());
			search.setServiceId(service.getServiceId());
			MerchantBusiness merchantBusiness = merchantBusinessManage.queryById(search);
			if (merchantBusiness == null) {
				return LivingCodeEnum.SERVICE_NA.toJSONString();
			}
			if (merchant.getMerchantStatus() != 1 || service.getServiceStatus() != 1
					|| merchantBusiness.getStatus() != 1) {
				return LivingCodeEnum.SERVICE_NA.toJSONString();
			}

			TreeMap<String, String> param = new TreeMap<>();
			param.put("key", key);
			param.put("serviceType", String.valueOf(serviceType));
			param.put("orderId", orderId);
			param.put("version", version);
			param.put("status", String.valueOf(status));
			String server_sign = SignUtil.genSign(param, merchant.getMd5Key());
			if (server_sign.compareTo(sign) != 0) {
				return LivingCodeEnum.SIGN_ERR.toJSONString();
			}
			Request request = new Request();
			request.setRequestId(orderId);
			request.setMerchantId(merchant.getMerchantId());
			Request db_request = requestService.queryById(request);
			if (db_request == null) {
				return LivingCodeEnum.ORDER_NA.toJSONString();
			}
			Order order = orderService.queryOrder(db_request.getOrderId());
			if (order == null) {
				return LivingCodeEnum.ORDER_NA.toJSONString();
			}

			MerchantGoods merchantGoods_search = new MerchantGoods();
			merchantGoods_search.setMerchantId(order.getMerchantId());
			merchantGoods_search.setGoodsId(order.getGoodsId());
			MerchantGoods merchantGoods = merchantGoodsService.queryMerchantGoods(merchantGoods_search);

			HomeOrderStatusEnum db_statusEnum = HomeOrderStatusEnum.parse(db_request.getRequestStatus());
			HomeOrderStatusEnum statusEnum = HomeOrderStatusEnum.parse(status);
			JSONObject settlementJson = new JSONObject();
			settlementJson.put("settlePrice", StringUtil.yuan2hao(merchantGoods.getSettlement()));
			settlementJson.put("settleMerchantId", merchant.getMerchantCode());
			//更新状态跟现在状态一致，则直接返回成功
			if(db_statusEnum == statusEnum){
				return LivingCodeEnum.SUCCESS.toJSONString();
			}

			switch (statusEnum) {

			case OrderConfirmed:
				if (db_statusEnum.getValue() != HomeOrderStatusEnum.OrderUnConfirm.getValue()) {
					return LivingCodeEnum.OTHER_ERR.toJSONString("状态流转错误");
				}
				boolean updateRequest = updateRequest(orderId, statusEnum.getValue());

				if (updateRequest) {
					boolean success = orderSystemService.success(OrderTypeEnum.DoorTDoor.getValue(), order.getOrderId(),
							order.getGoodsId(), settlementJson.toString(), OrderStatusEnum.OrderUnStockOut.getValue());

					if (success) {
						return LivingCodeEnum.SUCCESS.toJSONString();
					} else {
						return LivingCodeEnum.OTHER_ERR.toJSONString("更新失败");
					}

				} else {
					return LivingCodeEnum.OTHER_ERR.toJSONString("更新失败");
				}
			case OrderComplete:
				if (db_statusEnum.getValue() != HomeOrderStatusEnum.OrderConfirmed.getValue()) {
					return LivingCodeEnum.OTHER_ERR.toJSONString("状态流转错误");
				}
				boolean updateRequest1 = updateRequest(orderId, statusEnum.getValue());
				if (updateRequest1) {
					boolean success = orderSystemService.complete(order.getOrderId(), order.getGoodsId(),
							settlementJson.toString(), OrderStatusEnum.OrderDistribute.getValue());

					if (success) {
						return LivingCodeEnum.SUCCESS.toJSONString();
					} else {
						return LivingCodeEnum.OTHER_ERR.toJSONString("更新失败");
					}

				} else {
					return LivingCodeEnum.OTHER_ERR.toJSONString("更新失败");
				}
			default:
				return LivingCodeEnum.OTHER_ERR.toJSONString("状态流转错误");
			}
		} catch (Exception e) {
			log.error("第三方更新订单状态异常", e);
			return LivingCodeEnum.OTHER_ERR.toJSONString();
		}
	}

	/**
	 * 取消平台订单
	 *
	 * @param orderId
	 *            订单ID
	 * @return 返回取消订单结果
	 */
	@Override
	public String cancelLocalOrder(long userId, String email, long orderId , String price, String reason, int refundSHCoin, int nowStatus) {
		try {
			Order order = orderService.queryOrder(orderId);
			if (order == null) {
				return LivingCodeEnum.ORDER_NA.toJSONString();
			}
			if(nowStatus!=order.getOrderStatus()){
				return LivingCodeEnum.OTHER_NA.toJSONString();
			}

			Contact contact = contactService.queryByOrderId(orderId);
			if (contact == null) {
				return LivingCodeEnum.OTHER_NA.toJSONString("订单附加信息不存在");
			}
			
			Request request = requestService.queryOrderRequest(orderId);
			if (request == null) {
				return LivingCodeEnum.OTHER_NA.toJSONString("订单信息缺失");
			}
			
			Merchant merchant = merchantManage.getById(order.getMerchantId());
			if(merchant == null){
				return LivingCodeEnum.OTHER_NA.toJSONString("商户信息不存在");
			}
			//调用第三方接口取消订单
			HomeResponse homeResponse = homeServProviderService.cancelOrder(merchant, order.getService_id(), request.getRequestId());
			
			if (homeResponse.getCode() == 0) {
				OrderStatusEnum status = OrderStatusEnum.parse(order.getOrderStatus());
				switch(status){
				    case OrderUnStockOut:
				    case OrderDistribute:
					case OrderHadReceived:
					if(this.orderSystemService.updateOrderStatus(orderId, status, OrderStatusEnum.BackClose, OperatorTypeEnum.Admin, userId, email)){
						//后台关闭订单，不审核
						SimpleResult result = this.orderSystemService.openRefund(RefundModeEnum.ORIGINAL, orderId, StringUtil.yuan2hao(price), reason, 2, refundSHCoin);
						if (result.getStatus() == 1) {
							// 保存审核id
							Order updateOrder = new Order();
							updateOrder.setOrderId(order.getOrderId());
							updateOrder.setAuditId((long) result.getData());
							if(price.equals(order.getPay()))
								updateOrder.setRefundType(1);//全额退款
							else
								updateOrder.setRefundType(2);//部分退款
							updateOrder.setRefundPrice(price);
							updateOrder.setUpdateTime(new Date());
							this.orderService.update(updateOrder);
						}else{
							log.error("后台取消订单，发起退款失败，订单号={}，原订单状态={}", order.getOrderId(), order.getOrderStatus());
						}
						return LivingCodeEnum.SUCCESS.toJSONString();
					}else{
						return LivingCodeEnum.CANCEL_FAIL.toJSONString("更改订单状态失败");
					}
					
				default:
					return LivingCodeEnum.CANCEL_FAIL.toJSONString("订单不允许取消");
					
				}
			}else {
				return LivingCodeEnum.CANCEL_FAIL.toJSONString("取消第三方订单失败，返回信息【" + homeResponse.getMsg() + "】");
			}
				
		} catch (Exception e) {
			log.error("取消订单异常，订单号={}", orderId, e);
			return LivingCodeEnum.SYSTEM_ERR.toJSONString();
		}
	}


	/**
	 * 查询异常订单
	 *
	 * @return 返回订单详情
	 */
	@Override
	public String countunusual() {
		int total = orderService.countUnusual();
		JSONObject result = new JSONObject();
		result.put("total", total);
		return result.toJSONString();

	}

	/**
	 * 查询异常订单
	 *
	 * @return 订单列表
	 */
	@Override
	public String queryUnusual() {

		List<Order> orders = orderService.queryUnusual();
		JSONArray orders_json = new JSONArray();
		for (Order order : orders) {
			orders_json.add(buildOrderVo(order));
		}
		JSONObject result = new JSONObject();
		result.put("orders", orders_json);
		return result.toJSONString();
	}


	/**
	 * 导出异常订单
	 *
	 * @return 订单列表
	 */
	@Override
	public String exportUnusual() {

		List<String> title = new ArrayList<>();
		title.add("序号");
		title.add("订单号");
		title.add("下单时间");
		title.add("业务类型");
		title.add("服务提供商");
		title.add("服务商结算价");
		title.add("实惠价（元）");
		title.add("实惠现金补贴（元）");
		title.add("实际用户支付（元）");
		title.add("下单状态");

		List<List<Object>> data = new ArrayList<>();
		List<Order> orders = orderService.queryUnusual();
		for (int i = 0; i < orders.size(); i++) {
			Order order = orders.get(i);
			List<Object> list = new ArrayList<>();
			list.add(i);
			list.add(order.getOrderId());
			list.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateTime()));
			com.shihui.openpf.common.model.Service service = serviceManage.findById(order.getService_id());
			list.add(service.getServiceName());
			Merchant merchant = merchantManage.getById(order.getMerchantId());
			list.add(merchant.getMerchantName());
			MerchantGoods merchantGoods = new MerchantGoods();
			merchantGoods.setMerchantId(order.getMerchantId());
			merchantGoods.setGoodsId(order.getGoodsId());
			MerchantGoods db_merchantGoods = merchantGoodsService.queryMerchantGoods(merchantGoods);
			list.add(db_merchantGoods.getSettlement());
			BigDecimal price = new BigDecimal(order.getShOffSet()).add(new BigDecimal(order.getPay()));
			list.add(price.stripTrailingZeros().toPlainString());
			list.add(order.getShOffSet());
			list.add(order.getPay());
			list.add(OrderStatusEnum.parse(order.getOrderStatus()).getName());
			data.add(list);
		}

		String fileName = null;
		try {
			fileName = DataExportUtils.genExcel(String.valueOf(System.currentTimeMillis()), "unusualOrder", title, data,
					"utf-8");
		} catch (Exception e) {

		}

		return fileName;
	}

	@Override
	public String yjzConverter(String orderId, int status, String pId, String version, String methodName, String sign) {
		TreeMap<String, String> map = new TreeMap<>();
		map.put("orderId", orderId);
		map.put("status", String.valueOf(status));
		if (pId != null)
			map.put("pId", pId);
		if (version != null)
			map.put("version", version);
		if (methodName != null)
			map.put("methodName", methodName);
		//map.put("sign", sign);

		Request request = new Request();
		request.setRequestId(orderId);
		Request db_request = requestService.queryById(request);
		if(db_request == null){
			log.error("云家政订单更新状态异常，订单不存在，orderId={}", orderId);
			return JSONObject.toJSONString(new YjzUpdateResult(5, "订单信息错误", new String[0]));
		}
		
		Merchant merchant = merchantManage.getById(db_request.getMerchantId());

		Order order = orderService.queryOrder(db_request.getOrderId());

		MerchantGoods merchantGoods_search = new MerchantGoods();
		merchantGoods_search.setMerchantId(order.getMerchantId());
		merchantGoods_search.setGoodsId(order.getGoodsId());
		MerchantGoods merchantGoods = merchantGoodsService.queryMerchantGoods(merchantGoods_search);

		if (!genSign(map, merchant.getMd5Key()).equals(sign))
			return JSONObject.toJSONString(new YjzUpdateResult(5, "签名错误", new String[0]));

		try {
			YjzOrderStatusEnum db_statusEnum = YjzOrderStatusEnum.parseServerValues(db_request.getRequestStatus());
			YjzOrderStatusEnum statusEnum = YjzOrderStatusEnum.parse(status);

			JSONObject settlementJson = new JSONObject();
			settlementJson.put("settlePrice", StringUtil.yuan2hao(merchantGoods.getSettlement()));
			settlementJson.put("settleMerchantId", merchant.getMerchantCode());

			switch (statusEnum) {
			case OrderConfirmed:
				if (db_statusEnum.getValue() != YjzOrderStatusEnum.OrderUnConfirm.getValue()) {
					return JSONObject
							.toJSONString(new YjzUpdateResult(2, "状态流转错误:" + statusEnum.getValue(), new String[0]));
				}
				boolean updateRequest = updateRequest(orderId, statusEnum.getServerValue());

				if (updateRequest) {
					boolean success = orderSystemService.success(OrderTypeEnum.DoorTDoor.getValue(), order.getOrderId(),
							order.getGoodsId(), settlementJson.toString(), OrderStatusEnum.OrderUnStockOut.getValue());

					if (success) {
						return JSONObject.toJSONString(new YjzUpdateResult(0, "success", new String[0]));
					} else {
						return JSONObject.toJSONString(new YjzUpdateResult(1, "更新订单失败", new String[0]));
					}

				} else {
					return JSONObject.toJSONString(new YjzUpdateResult(1, "更新失败", new String[0]));
				}
			case OrderComplete:
				if (db_statusEnum.getValue() != YjzOrderStatusEnum.OrderConfirmed.getValue()) {
					return JSONObject
							.toJSONString(new YjzUpdateResult(2, "状态流转错误:" + statusEnum.getValue(), new String[0]));
				}
				boolean updateRequest1 = updateRequest(orderId, statusEnum.getServerValue());
				if (updateRequest1) {
					boolean success = orderSystemService.complete(order.getOrderId(), order.getGoodsId(),
							settlementJson.toJSONString(), OrderStatusEnum.OrderDistribute.getValue());

					if (success) {
						return JSONObject.toJSONString(new YjzUpdateResult(0, "success", new String[0]));
					} else {
						return JSONObject.toJSONString(new YjzUpdateResult(1, "更新订单失败", new String[0]));
					}

				} else {
					return JSONObject.toJSONString(new YjzUpdateResult(1, "更新失败", new String[0]));
				}

			case OrderCancel:
				OrderStatusEnum db_status = OrderStatusEnum.parse(order.getOrderStatus());
				switch (db_status) {
					case OrderUnpaid:
						if (this.orderSystemService.updateOrderStatus(order.getOrderId(), db_status,
								OrderStatusEnum.OrderCloseByOutTime, OperatorTypeEnum.User, merchant.getMerchantId(), "")) {
							// 更新request表
							request.setRequestStatus(HomeOrderStatusEnum.OrderCancel.getValue());
							this.requestService.updateStatus(request);
							return LivingCodeEnum.SUCCESS.toJSONString();
						} else {
							return LivingCodeEnum.SYSTEM_ERR.toJSONString();
						}
					case OrderUnStockOut:
						if (this.orderSystemService.updateOrderStatus(order.getOrderId(),
								OrderStatusEnum.parse(order.getOrderStatus()), OrderStatusEnum.PayedCancel,
								OperatorTypeEnum.User, merchant.getMerchantId(), "")) {
							request.setRequestStatus(HomeOrderStatusEnum.OrderCancel.getValue());
							this.requestService.updateStatus(request);
							// 商户取消订单，全额退款，无需审核，退回实惠现金
							SimpleResult sr = orderSystemService.merchantCancel(order.getOrderId(), merchant.getMerchantCode(), StringUtil.yuan2hao(order.getPrice()), order.getOrderStatus(), "商户取消订单");
							if (sr.getStatus() == 1) {
								// 保存审核id
								Order updateOrder = new Order();
								updateOrder.setOrderId(order.getOrderId());
								updateOrder.setAuditId((long) sr.getData());
								updateOrder.setRefundType(1);//全额退款
								updateOrder.setRefundPrice(order.getPay());
								updateOrder.setUpdateTime(new Date());
								this.orderService.update(updateOrder);
							}else{
								log.error("商户取消订单并发起退款失败，订单号={}，原订单状态={}", order.getOrderId(), order.getOrderStatus());
							}
							return LivingCodeEnum.SUCCESS.toJSONString();
						} else {
							return LivingCodeEnum.SYSTEM_ERR.toJSONString();
						}
					case OrderCancelByCustom:
					case OrderCloseByOutTime:
					case BackClose:
						return LivingCodeEnum.SUCCESS.toJSONString();
					default:
						return LivingCodeEnum.CANCEL_FAIL.toJSONString("订单不允许取消");
				}

			default:
				return JSONObject
						.toJSONString(new YjzUpdateResult(2, "状态流转错误:" + statusEnum.getValue(), new String[0]));
			}

		} catch (Exception e) {
			log.error("yjzConverter -- orderId:{} update status error!!!", e);
		}

		return JSONObject.toJSONString(new YjzUpdateResult(6, "状态码错误", new String[0]));

	}

	public boolean updateRequest(String requestId, int status) {
		Request update_request = new Request();
		update_request.setRequestId(requestId);
		update_request.setRequestStatus(status);
		return requestService.updateStatus(update_request);
	}


	/**
	 * 计算签名
	 *
	 * @param param
	 * @return
	 */
	private String genSign(TreeMap<String, String> param, String md5Key) {
		StringBuilder temp = new StringBuilder();
		for (Map.Entry<String, String> entry : param.entrySet()) {
			temp.append(entry.getKey()).append(entry.getValue());
		}
		temp.append(md5Key);
		String sign = AlgorithmUtil.MD5(temp.toString());
		return sign;
	}

	@Override
	public String updateThirdOrderServiceStatus(String key, int serviceType, String orderId, String version,
			String sign, Integer status, String statusName, String serverName, String serverPhone, String comment) {
        try{
    		Merchant merchant = merchantManage.getByKey(key);
    		if (merchant == null) {
    			return LivingCodeEnum.PARAM_ERR.toJSONString("key 不存在");
    		}
    		com.shihui.openpf.common.model.Service service = serviceManage.findById(serviceType);
    		if (service == null) {
    			return LivingCodeEnum.SERVICE_NA.toJSONString();
    		}
    		MerchantBusiness search = new MerchantBusiness();
    		search.setMerchantId(merchant.getMerchantId());
    		search.setServiceId(service.getServiceId());
    		MerchantBusiness merchantBusiness = merchantBusinessManage.queryById(search);
    		if (merchantBusiness == null) {
    			return LivingCodeEnum.SERVICE_NA.toJSONString();
    		}
    		if (merchant.getMerchantStatus() != 1 || service.getServiceStatus() != 1
    				|| merchantBusiness.getStatus() != 1) {
    			return LivingCodeEnum.SERVICE_NA.toJSONString();
    		}

    		TreeMap<String, String> param = new TreeMap<>();
    		param.put("key", key);
    		param.put("serviceType", String.valueOf(serviceType));
    		param.put("orderId", orderId);
    		param.put("version", version);
    		if(status != null){
    			param.put("status", String.valueOf(status));
    		}
    		param.put("statusName", statusName);
    		if(serverName != null){
    			param.put("serverName", serverName);
    		}
    		if(serverPhone != null){
    			param.put("serverPhone", serverPhone);
    		}
    		if(comment != null){
    			param.put("comment", comment);
    		}
    		
    		String server_sign = SignUtil.genSign(param, merchant.getMd5Key());
    		if (server_sign.compareTo(sign) != 0) {
    			return LivingCodeEnum.SIGN_ERR.toJSONString();
    		}
    		requestService.updateServiceStatus(orderId, status, statusName, serverName, serverPhone, comment);
        }catch (Exception e){
        	log.error("更新第三方订单服务状态异常，第三方订单号:"+orderId, e);
        	return LivingCodeEnum.SYSTEM_ERR.toJSONString();
        }
        return LivingCodeEnum.SUCCESS.toJSONString();
	}

	

	@Override
	@Transactional()
	public String updateThirdOrderServiceStartTime(String key, int serviceType, String orderId, String version,
			String sign, String serviceStartTime, String comment) {
		   try{
			   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			   try{
				   sdf.parse(serviceStartTime);
			   }catch (Exception e){
				   return LivingCodeEnum.PARAM_ERR.toJSONString("时间格式错误"); 
			   }
			   
	    		Merchant merchant = merchantManage.getByKey(key);
	    		if (merchant == null) {
	    			return LivingCodeEnum.PARAM_ERR.toJSONString("key 不存在");
	    		}
	    		com.shihui.openpf.common.model.Service service = serviceManage.findById(serviceType);
	    		if (service == null) {
	    		}
	    		MerchantBusiness search = new MerchantBusiness();
	    		search.setMerchantId(merchant.getMerchantId());
	    		search.setServiceId(service.getServiceId());
	    		MerchantBusiness merchantBusiness = merchantBusinessManage.queryById(search);
	    		if (merchantBusiness == null) {
	    			return LivingCodeEnum.SERVICE_NA.toJSONString();
	    		}
	    		if (merchant.getMerchantStatus() != 1 || service.getServiceStatus() != 1
	    				|| merchantBusiness.getStatus() != 1) {
	    			return LivingCodeEnum.SERVICE_NA.toJSONString();
	    		}

	    		TreeMap<String, String> param = new TreeMap<>();
	    		param.put("key", key);
	    		param.put("serviceType", String.valueOf(serviceType));
	    		param.put("orderId", orderId);
	    		param.put("version", version);
	    		param.put("serviceStartTime", serviceStartTime);
	    		if(comment != null){
	    			param.put("comment", comment);
	    		}
	    		
	    		String server_sign = SignUtil.genSign(param, merchant.getMd5Key());
	    		if (server_sign.compareTo(sign) != 0) {
	    			return LivingCodeEnum.SIGN_ERR.toJSONString();
	    		}
	    		
	    		Request request = new Request();
				request.setRequestId(orderId);
				request.setMerchantId(merchant.getMerchantId());
				Request db_request = requestService.queryById(request);
				if (db_request == null) {
					return LivingCodeEnum.ORDER_NA.toJSONString();
				}
	    		contactService.updateServiceStartTime(orderId, serviceStartTime, comment, db_request);
	        }catch (Exception e){
	        	log.error("更新第三方订单服务开始时间异常，第三方订单号:"+orderId, e);
	        	return LivingCodeEnum.SYSTEM_ERR.toJSONString();
	        }
	        return LivingCodeEnum.SUCCESS.toJSONString();
	}



}
