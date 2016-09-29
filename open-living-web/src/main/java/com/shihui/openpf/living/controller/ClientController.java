/**
 * 
 */
package com.shihui.openpf.living.controller;

import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
import com.shihui.api.core.context.RequestContext;
import com.shihui.commons.ApiLogger;
import com.shihui.commons.OperationLogger;
import com.shihui.openpf.living.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping(path = "/v2/openpf/living/app", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
public class ClientController extends BasicController {
	
	@Autowired  
    private HttpServletRequest request;  
	
	@Resource
	private ClientService clientService;

	@RequestMapping("/homepage")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object homepage(
			@RequestParam(name="userId", required = true) Long userId,
			@RequestParam(name="cityId", required = true) Integer cityId,
			@RequestParam(name = "groupId", required = true) Long groupId,
			@RequestParam(name = "mid", required = false, defaultValue="0") long mid,

			@RequestParam(name="historyOrderCount", required = false, defaultValue = "5") int historyOrderCount) {
		
		ApiLogger.info("Controller: /v2/openpf/living/app/homepage : homepage() : "
				+ "userId: " + userId
				+ "cityId: " + cityId
				+ "groupId: " + groupId
				+ "mid: " + mid
				+ "historyOrderCount: " + historyOrderCount );
//ApiLogger.info("REQUEST : WVersion : " + request.getHeader("X-WVersion"));
		Map<String, Object> expand = new HashMap<>();
		expand.put("cityId", String.valueOf(cityId));
		expand.put("gid", String.valueOf(groupId));
		expand.put("serviceId", String.valueOf(mid));
		OperationLogger.log("operation.open-living.home", RequestContext.getRequestContext(), expand);

		return clientService.homepage(userId,cityId, historyOrderCount);
	}

	@RequestMapping("/listCity")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object queryCity(
			@RequestParam(name="serviceId", required = true) Integer serviceId,
			@RequestParam(name="categoryId", required = true) Integer categoryId) {

		ApiLogger.info("Controller: /v2/openpf/living/app/queryCity : queryCity() : "
				+ "categoryId: " + categoryId);
		
		return clientService.queryCity(serviceId, categoryId);
	}

	@RequestMapping("/listCompany")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object queryCompany(
			@RequestParam(name="serviceId", required = true) Integer serviceId,
			@RequestParam(name="categoryId", required = true) Integer categoryId,
			@RequestParam(name="cityId", required = true) Integer cityId) {

		ApiLogger.info("Controller: /v2/openpf/living/app/queryCompany : queryCompany() : "
				+ "serviceId: " + serviceId
				+ "cityId: " + cityId );

		return clientService.queryCompany(serviceId, categoryId, cityId);
	}
	/*
	 * 查询缴费单
	 */
	@RequestMapping("/queryBill")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object queryFee(
			@RequestParam(name="userId", required = true) Integer userId,
			@RequestParam(name = "groupId", required = true) Long groupId,
			@RequestParam(name = "mid", required = false, defaultValue="0") long mid,
			@RequestParam(name="serviceId", required = true) Integer serviceId,
			@RequestParam(name="categoryId", required = true) Integer categoryId,
			@RequestParam(name="cityId", required = true) Integer cityId,
			@RequestParam(name="goodsId", required = true) Long goodsId,
			@RequestParam(name="goodsVersion", required = true) Integer goodsVersion,
			@RequestParam(name="companyId", required = true) Integer companyId,
			@RequestParam(name="companyNo", required = false) String companyNo,
			@RequestParam(name="userNo", required = true) String userNo,
			@RequestParam(name="field2", required = true) String field2) {

//		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//		if(hour < 4 || 20 < hour) {
//			JSONObject result = new JSONObject();
//			result.put("response", new SimpleResponse(0,"请在04:00到20:00之间缴费") );
//			return result;
//		}

		Map<String, Object> expand = new HashMap<>();
		expand.put("cityId", String.valueOf(cityId));
		expand.put("gid", String.valueOf(groupId));
		expand.put("serviceId", String.valueOf(mid));
		expand.put("businessId", String.valueOf(serviceId));
//		expand.put("businessName", service.getServiceName() + "");
		OperationLogger.log("operation.open-living.queryFee", RequestContext.getRequestContext(), expand);
		return clientService.queryFee(userId, groupId, mid, serviceId, 
				categoryId, cityId, goodsId, goodsVersion, companyId,/* companyNo,*/ 
				userNo, field2,
				request.getHeader("ndeviceid"),
				request.getIntHeader("X-APP-ID"));
	}
	
	@RequestMapping("/confirmOrder")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object confirmOrder(
			@RequestParam(name="userId", required = true) Integer userId,
			@RequestParam(name="tempId", required = true) String tempId,
			@RequestParam(name="price", required = false) String price) {

		ApiLogger.info("Controller: /v2/openpf/living/app/confirmOrder : confirmOrder() : "
				+ "userId: " + userId
				+ "tempId: " + tempId
				+ "price: " + price );
		
		return clientService.confirmOrder(userId, tempId, price);
	}
	
	@RequestMapping("/createOrder")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object createOrder(
			@RequestParam(name="userId", required = true) Integer userId,
			@RequestParam(name="tempId", required = true) String tempId,
			@RequestParam(name="costSh", required = true) Integer costSh) {

		ApiLogger.info("Controller: /v2/openpf/living/app/createOrder : createOrder() : "
				+ "userId: " + userId
				+ "tempId: " + tempId
				+ "costSh: " + costSh );
		
		return clientService.createOrder(userId, tempId, costSh, request.getRemoteAddr());
	}
}