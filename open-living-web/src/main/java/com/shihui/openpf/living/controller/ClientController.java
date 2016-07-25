/**
 * 
 */
package com.shihui.openpf.living.controller;

//import java.util.HashMap;
//import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
//import com.shihui.api.core.context.RequestContext;
//import com.shihui.commons.OperationLogger;
import com.shihui.openpf.living.service.ClientService;
import com.shihui.openpf.living.controller.BasicController;

/**
 * @author zhouqisheng
 *
 */
@Controller
@RequestMapping(path = "/v2/openpf/living/app", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
public class ClientController extends BasicController {
	@Resource
	private ClientService clientService;

	@RequestMapping("/homepage")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object homepage(
			@RequestParam(name="user_id", required = true) Long userId,
			@RequestParam(name="city_id", required = true) Integer cityId,
			@RequestParam(name="history_order_count", required = false, defaultValue = "5") int historyOrderCount) {

		return clientService.homepage(userId,cityId, historyOrderCount);
	}

	@RequestMapping("/queryCity")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object queryCity(
			@RequestParam(name="category_id", required = true) Integer categoryId) {

		return clientService.queryCity(categoryId);
	}

	@RequestMapping("/queryCompany")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object queryCity(
			@RequestParam(name="service_id", required = true) Integer serviceId,
			@RequestParam(name="city_id", required = true) Integer cityId) {

		return clientService.queryCompany(serviceId, cityId);
	}
	/*
	 * 输入信息验证
	 */
	
	/*
	 * 查询缴费单
	 */
	
	/*
	 * 创建订单
	 */
	
}