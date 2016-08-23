package com.shihui.openpf.living.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.shihui.commons.ApiLogger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.shihui.api.order.common.enums.OrderStatusEnum;
import com.shihui.api.order.common.enums.PayTypeEnum;
import com.shihui.api.order.emodel.OperatorTypeEnum;
import com.shihui.api.order.emodel.RefundModeEnum;
import com.shihui.api.order.vo.ApiResult;
import com.shihui.api.order.vo.SimpleResult;
import com.shihui.openpf.common.dubbo.api.MerchantManage;
import com.shihui.openpf.common.dubbo.api.ServiceManage;
import com.shihui.openpf.common.model.Merchant;
import com.shihui.openpf.common.tools.DataExportUtils;
import com.shihui.openpf.common.tools.StringUtil;
import com.shihui.openpf.living.entity.MerchantGoods;
import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.living.service.OrderManage;
import com.shihui.openpf.living.service.OrderSystemService;
import com.shihui.openpf.living.util.SimpleResponse;
import com.shihui.openpf.living.dao.OrderDao;
import com.shihui.openpf.living.dao.BillDao;
import com.shihui.openpf.living.dao.OrderBillDao;
import com.shihui.openpf.living.entity.support.BillStatusEnum;
import com.shihui.openpf.living.entity.support.ConditionVo;
import com.shihui.openpf.living.entity.OrderBill;
import com.shihui.openpf.living.entity.OrderHistory;
import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Company;
import com.shihui.openpf.living.dao.CompanyDao;
import com.shihui.openpf.living.entity.support.LivingCodeEnum;
import com.shihui.openpf.living.io3rd.PacketError;
import com.shihui.openpf.living.io3rd.PacketNotify;
import com.shihui.openpf.living.io3rd.ResPay;
import com.shihui.openpf.living.io3rd.ResQuery;
import com.shihui.openpf.living.dao.MerchantGoodsDao;
import com.shihui.openpf.living.dao.OrderHistoryDao;



/**
 * Created by zhoutc on 2016/1/21.
 */
@Service
public class OrderManage {

	@Resource
	OrderDao orderDao;
	@Resource
	BillDao billDao;
	@Resource
	OrderBillDao obDao;
	@Resource
	CompanyDao companyDao;
	@Resource
	MerchantGoodsDao merchantGoodsDao;
	@Resource
	OrderHistoryDao orderHistoryDao;

	@Resource
	OrderSystemService orderSystemService;
	@Resource
	MerchantManage merchantManage;
	@Resource
	ServiceManage serviceManage;
	
	//
	private CloseableHttpClient httpClient;
//	private Logger log;
	//
	@Value("${file_upload_url}")
	private String fileUploadUrl;
	//
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
		//
//		log = LoggerFactory.getLogger(getClass());
	}

	/**
	 * OPS查询订单
	 *
	 * @param queryOrder
	 *            查询订单条件
	 * @return 返回结果
	 */

	public Object queryOrderList(ConditionVo vo) {
		JSONObject result = new JSONObject();

		int total = obDao.queryCount(vo);
		result.put("total", total);
		result.put("page", vo.getPage());
		result.put("size", vo.getCount());

		if (total <= 0)
			return result;

		List<OrderBill> orderList = obDao.query(vo);
		for(OrderBill ob : orderList) {
			ob.setOrderStatusMsg(OrderStatusEnum.parse(ob.getOrderStatus()).getName());
			ob.setOrderIdString(String.valueOf(ob.getOrderId()));
		}
		result.put("orders", JSON.toJSON(orderList));

		return result;
	}

	/**
	 * 导出订单
	 * @param queryOrder 查询订单请求数据
	 *
	 * @return 返回结果
	 */

	public Object exportOrderList(ConditionVo vo) {
		JSONObject result = new JSONObject();
		List<OrderBill> orderList = obDao.query(vo);
		List<String> title = new ArrayList<>();
		title.add("缴费类型");
		title.add("订单号");
		title.add("城市");
		title.add("地址");
		title.add("户号");
		title.add("户名");
		title.add("用户ID");
		title.add("缴费金额");
		title.add("订单提交时间");
		title.add("充值状态");
		title.add("退款状态");

		List<List<Object>> data = new ArrayList<>();
		if(orderList!=null && orderList.size()>0)
		for(OrderBill order : orderList){
			List<Object> list = new ArrayList<>();
			list.add(order.getFeeName());
			list.add(order.getOrderId());
			list.add(order.getCityName());
			list.add(order.getUserAddress());
			list.add(order.getBillKey());
			list.add(order.getUserName());
			list.add(order.getUserId());
			list.add(order.getPrice());
			list.add(order.getPayTime());
			list.add(OrderStatusEnum.parse(order.getOrderStatus()).getName());
			list.add(order.getBillStatus() == BillStatusEnum.Refund.getValue() ? "已退款" : "");
			data.add(list);
		}
		String fileName = null;
		try {
			fileName = DataExportUtils.genExcel("open_living_" + System.currentTimeMillis()+".xlsx", "订单", title, data,
					"utf-8");
		} catch (Exception e) {
//			log.error("export order list error!!!",e);
			ApiLogger.error("export order list error!!!" + e.getMessage());
			result.put("code", 2);
			return result;
		}
		String fileId = "";
		try {
			fileId = uploadFile(fileName);
		}catch (Exception e){
//			log.error("upload file error!!!",e);
			ApiLogger.error("upload file error!!!" + e.getMessage());
			result.put("code",2);
			return result;
		}
		result.put("code",1);
		result.put("fileId", fileId);
		return result;
	}

	/**
	 * 上传文件流至TFS服务器
	 *
	 * @return
	 */
	private String uploadFile(String filePath) {
		JSONObject result = null;
//		log.info("开始上传文件：{}", filePath);
		ApiLogger.error("开始上传文件：{"+filePath+"}");
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
//					log.info("保存订单明细表至TFS失败！返回信息：{}", executeAsyncString);
					ApiLogger.error("保存订单明细表至TFS失败！返回信息：{"+executeAsyncString+"}");
					return null;
				}
//				log.info("完成文件上传：{}，返回信息:{} ",filePath, result.toJSONString());
				ApiLogger.error("完成文件上传：{"+ filePath +"}，返回信息:{"+ result.toJSONString() +"} ");

				return result.getString("fileid");
			}
		} catch (Exception e) {
//			log.error("上传文件至TFS出错!".concat(JSON.toJSONString(result)));
			ApiLogger.error("上传文件至TFS出错!".concat(JSON.toJSONString(result)));
			return null;
		} finally {
			file.delete();
		}
	}

	/**
	 * 查询订单详情
	 *
	 * @param orderId
	 *            订单ID
	 * @return 返回订单详情
	 */

	public Object queryOrder(long orderId) {
		ApiLogger.info("OrderManage : queryOrder : " + orderId);
		try {
			JSONObject result = new JSONObject();
			
			Order order = orderDao.findById(orderId);
			Bill bill = billDao.findById(orderId);
			ApiLogger.info("OrderManage : queryOrder : (order == null || bill == null)" + (order == null || bill == null));
			if (order == null || bill == null)
				return null;
			//
			result.put("orderId", String.valueOf(orderId));
			//result.put("consumeTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getConsumeTime()));
			result.put("userId", order.getUserId());
			result.put("price", order.getPrice());
			result.put("orderStatus", OrderStatusEnum.parse(order.getOrderStatus()).getName());
			//
			result.put("feeName", bill.getFeeName());
			result.put("cityName", bill.getCityName());
			result.put("userAddress", bill.getUserAddress());
			result.put("userNo", bill.getBillKey());
			result.put("userName", bill.getUserName());
			result.put("billStatus", bill.getBillStatus() == BillStatusEnum.Refund.getValue() ? "已退款" : "无");
			//
			Company company = companyDao.findById(bill.getCompanyId());
			result.put("companyName", company.getCompanyName());
			//
			Merchant merchant = merchantManage.getById(order.getMerchantId());
			result.put("merchantName", merchant.getMerchantName());
			//
			SimpleResult simpleResult = orderSystemService.backendOrderDetail(orderId);
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
//				log.info("queryOrder--orderId:" + orderId + " backendOrderDetail status:" + simpleResult.getStatus() + " msg:" + simpleResult.getMsg());
				ApiLogger.info("queryOrder--orderId:" + orderId + " backendOrderDetail status:" + simpleResult.getStatus() + " msg:" + simpleResult.getMsg());
			}

			return result;
		} catch (Exception e) {
//			log.error("OrderManageImpl queryOrder error!!", e);
			ApiLogger.info("OrderManageImpl queryOrder error!!" + e.getMessage());
		}

		return null;
	}

	/**
	 * 取消平台订单
	 *
	 * @param orderId
	 *            订单ID
	 * @return 返回取消订单结果
	 */

	public Object cancelLocalOrder(long userId, String email, long orderId , String price, String reason, int refundSHCoin, int nowStatus) {
		try {
			Order order = orderDao.queryOrder(orderId);
			Bill bill = billDao.findById(orderId);
			if (order == null || bill == null) {
				return LivingCodeEnum.ORDER_NA;
			}
			if(nowStatus!=order.getOrderStatus()){
				return LivingCodeEnum.OTHER_NA;
			}
			
			Merchant merchant = merchantManage.getById(order.getMerchantId());
			if(merchant == null){
				return LivingCodeEnum.OTHER_NA;//.toJSONString("商户信息不存在");
			}

			OrderStatusEnum orderStatus = OrderStatusEnum.parse(order.getOrderStatus());
			if(order.getOrderStatus() == OrderStatusEnum.OrderUnStockOut.getValue()
					&& bill.getBillStatus() == BillStatusEnum.Refund.getValue()) {
				if(this.orderSystemService.updateOrderStatus(orderId, orderStatus, OrderStatusEnum.BackClose, OperatorTypeEnum.Admin, userId, email)){
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
						orderDao.update(updateOrder);
					}else{
//						log.error("后台取消订单，发起退款失败，订单号={}，原订单状态={}", order.getOrderId(), order.getOrderStatus());
						ApiLogger.error("后台取消订单，发起退款失败，订单号={"+ order.getOrderId() +"}，原订单状态={"+ order.getOrderStatus() +"}");
					}
					return LivingCodeEnum.SUCCESS;
				}else{
					return LivingCodeEnum.CANCEL_FAIL;//.toJSONString("更改订单状态失败");
				}
				
			}
			else
				return LivingCodeEnum.CANCEL_FAIL;//.toJSONString("订单不允许取消");
				
				
		} catch (Exception e) {
//			log.error("取消订单异常，订单号={}", orderId, e);
			ApiLogger.error("取消订单异常，订单号={"+ orderId +"}" + e.getMessage());
			return LivingCodeEnum.SYSTEM_ERR;
		}
	}


	/**
	 * 查询异常订单
	 *
	 * @return 返回订单详情
	 */

	public Object countunusual() {
		int total = orderDao.countUnusual();
		JSONObject result = new JSONObject();
		result.put("total", total);
		return result;

	}

	/**
	 * 查询异常订单
	 *
	 * @return 订单列表
	 */

	public Object queryUnusual() {

		List<Order> orders = orderDao.queryUnusual();
		JSONArray orders_json = (JSONArray)JSON.toJSON(orders);
		for (Order order : orders) {
			orders_json.add(buildOrderVo(order));
		}
		JSONObject result = new JSONObject();
		result.put("orders", orders_json);
		return result;
	}
	
	private JSONObject buildOrderVo(Order order) {
		try {
			JSONObject order_json = new JSONObject();
			order_json.put("orderId", String.valueOf(order.getOrderId()));
			order_json.put("userId", order.getUserId());
			order_json.put("phone", "");
			order_json.put("serviceId", order.getServiceId());

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
//			log.error("OrderManageImpl buildOrderVo error!!", e);
			ApiLogger.error("OrderManageImpl buildOrderVo error!!" + e.getMessage());
		}

		return null;
	}


	/**
	 * 导出异常订单
	 *
	 * @return 订单列表
	 */

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
		List<Order> orders = orderDao.queryUnusual();
		for (int i = 0; i < orders.size(); i++) {
			Order order = orders.get(i);
			List<Object> list = new ArrayList<>();
			list.add(i);
			list.add(order.getOrderId());
			list.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateTime()));
			com.shihui.openpf.common.model.Service service = serviceManage.findById(order.getServiceId());
			list.add(service.getServiceName());
			Merchant merchant = merchantManage.getById(order.getMerchantId());
			list.add(merchant.getMerchantName());
			MerchantGoods merchantGoods = new MerchantGoods();
			merchantGoods.setMerchantId(order.getMerchantId());
			merchantGoods.setGoodsId(order.getGoodsId());
			MerchantGoods db_merchantGoods = merchantGoodsDao.findById(merchantGoods);
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
    
//	public Object confirmOrder(int userId, String tempID, String price) {
//		JSONObject result = new JSONObject();
////TODO ?????????????????????????????????????????????????????????????  
//		result.put("", "");
//		return result;
//	}
//
//    @Transactional(rollbackFor = Exception.class)
//    public Object createOrder(int userId, String tempID) {
////TODO ?????????????????????????????????????????????????????????????    	
//    	ApiResult result = new ApiResult();//orderSystemService.submitOrder(singleGoodsCreateOrderParam); Home->ClientServiceImpl.java->orderCreate
//    	if (result.getStatus() != 1) {
//			return JSON.toJSON(result);
//		}
//    	Order order = new Order();
//        if(orderDao.save(order)>0) {
//            Date date = new Date();
//            OrderHistory orderHistory = new OrderHistory();
//            orderHistory.setChangeTime(date);
//            orderHistory.setOrderId(order.getOrderId());
//            orderHistory.setOrderStatus(order.getOrderStatus());
//            orderHistoryDao.save(orderHistory);
//        }
//        return JSON.toJSON(result);
//    }

	/**
	 * 计算签名
	 *
	 * @param param
	 * @return
	 */
//	private String genSign(TreeMap<String, String> param, String md5Key) {
//		StringBuilder temp = new StringBuilder();
//		for (Map.Entry<String, String> entry : param.entrySet()) {
//			temp.append(entry.getKey()).append(entry.getValue());
//		}
//		temp.append(md5Key);
//		String sign = AlgorithmUtil.MD5(temp.toString());
//		return sign;
//	}

}
