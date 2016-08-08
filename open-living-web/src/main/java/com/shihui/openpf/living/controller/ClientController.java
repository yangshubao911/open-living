/**
 * 
 */
package com.shihui.openpf.living.controller;

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
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhouqisheng
 *
 */
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
			@RequestParam(name="historyOrderCount", required = false, defaultValue = "5") int historyOrderCount) {

		return clientService.homepage(userId,cityId, historyOrderCount);
	}

	@RequestMapping("/queryCity")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object queryCity(
			@RequestParam(name="categoryId", required = true) Integer categoryId) {

		return clientService.queryCity(categoryId);
	}

	@RequestMapping("/queryCompany")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object queryCompany(
			@RequestParam(name="serviceId", required = true) Integer serviceId,
			@RequestParam(name="cityId", required = true) Integer cityId) {

		return clientService.queryCompany(serviceId, cityId);
	}
	/*
	 * 查询缴费单
	 */
	@RequestMapping("/queryFee")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object queryFee(
			@RequestParam(name="userId", required = true) Integer userId,
			@RequestParam(name = "groupId", required = true) long groupId,
			@RequestParam(name = "mid", required = false) Long mid,
			@RequestParam(name="serviceId", required = true) Integer serviceId,
			@RequestParam(name="categoryId", required = true) Integer categoryId,
			@RequestParam(name="cityId", required = true) Integer cityId,
			@RequestParam(name="goodsId", required = true) Long goodsId,
			@RequestParam(name="goodsVersion", required = true) Integer goodsVersion,
			@RequestParam(name="companyId", required = true) Integer companyId,
			@RequestParam(name="companyNo", required = true) String companyNo,
			@RequestParam(name="userNo", required = true) String userNo) {

		return clientService.queryFee(userId, groupId, mid, serviceId, categoryId, cityId, goodsId, goodsVersion, companyId, companyNo, userNo, 
				request.getHeader("ndeviceid"),
				request.getIntHeader("X-APP-ID"));
	}

//	@RequestMapping("/checkQuery")
//	@ResponseBody
//	@Access(type = AccessType.COMMON)
//	public Object checkQuery(
//			@RequestParam(name="userId", required = true) Integer userId,
//			@RequestParam(name="tempId", required = true) String tempId) {
//
//		return clientService.checkQuery(userId, tempId);
//	}
	
	@RequestMapping("/confirmOrder")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object confirmOrder(
			@RequestParam(name="userId", required = true) Integer userId,
			@RequestParam(name="tempId", required = true) String tempId,
			@RequestParam(name="price", required = false) String price) {

		return clientService.confirmOrder(userId, tempId, price);
	}
	
	@RequestMapping("/createOrder")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object createOrder(
			@RequestParam(name="userId", required = true) Integer userId,
			@RequestParam(name="tempId", required = true) String tempId,
			@RequestParam(name="costSh", required = true) Integer costSh) {

		return clientService.createOrder(userId, tempId, costSh, request.getRemoteAddr());
	}
}