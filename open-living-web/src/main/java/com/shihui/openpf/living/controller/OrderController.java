/**
 * 
 */
package com.shihui.openpf.living.controller;

import java.io.File;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
import com.shihui.openpf.common.tools.StringUtil;
import com.shihui.openpf.living.service.OrderManage;


import com.shihui.openpf.living.entity.support.ConditionVo;
/**
 * @author zhouqisheng
 *
 * @version 1.0 Created at: 2016年1月19日 下午3:00:15
 */
@Controller
@RequestMapping(path = "/v2/openpf/living/order", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
public class OrderController {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Resource
	OrderManage orderManage;

	@RequestMapping("/search")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public String listById(
			@RequestParam(name="start_time",	required = false)						String startTime,
			@RequestParam(name="end_time",		required = false)						String endTime,
			@RequestParam(name="city_td",		required = false)						String cityId,
			@RequestParam(name="user_address",	required = false)						String userAddress,
			@RequestParam(name="company_id",	required = false)						String companyId,
			@RequestParam(name="user_no",		required = false)						String userNo,
			@RequestParam(name="user_name",		required = false)						String userName,
			@RequestParam(name="order_id",		required = false)						String orderId,
			@RequestParam(name="user_id",		required = false)						String userId,
			@RequestParam(name="order_status",	required = false)						String orderStatus,
			@RequestParam(name="page",			required = false, defaultValue="1")		int page,
			@RequestParam(name="size",			required = false, defaultValue="10")	int size) {

		ConditionVo vo = new ConditionVo();
		try {
			if (!StringUtil.isEmpty(startTime))
				vo.setStartTime(startTime);
			if (!StringUtil.isEmpty(endTime))
				vo.setStartTime(endTime);
			if (!StringUtil.isEmpty(cityId))
				vo.setCityId(Integer.parseInt(cityId));
			if (!StringUtil.isEmpty(userAddress))
				vo.setUserAddress(userAddress);
			if (!StringUtil.isEmpty(companyId))
				vo.setCompanyId(Integer.parseInt(companyId));
			if (!StringUtil.isEmpty(userNo))
				vo.setUserNo(userNo);
			if (!StringUtil.isEmpty(userName))
				vo.setUserName(userName);	
			if (!StringUtil.isEmpty(orderId))
				vo.setOrderId(Long.parseLong(orderId));
			if (!StringUtil.isEmpty(userId))
				vo.setUserId(Integer.parseInt(userId));
			if (!StringUtil.isEmpty(orderStatus))
				vo.setOrderStatus(Integer.parseInt(orderStatus));
			
			vo.setIndex((page-1)*size);
			vo.setCount(size);
			vo.setPage(page);
			
			return orderManage.queryOrderList(vo);
		} catch (Exception e) {
			log.error("查询订单列表异常，param={}", JSON.toJSONString(vo), e);
		}
		return null;
	}


	@RequestMapping("/export")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public String export(
			@RequestParam(name="start_time",	required = false)	String startTime,
			@RequestParam(name="end_time",		required = false)	String endTime,
			@RequestParam(name="city_td",		required = false)	String cityId,
			@RequestParam(name="user_address",	required = false)	String userAddress,
			@RequestParam(name="company_id",	required = false)	String companyId,
			@RequestParam(name="user_no",		required = false)	String userNo,
			@RequestParam(name="user_name",		required = false)	String userName,
			@RequestParam(name="order_id",		required = false)	String orderId,
			@RequestParam(name="user_id",		required = false)	String userId,
			@RequestParam(name="order_status",	required = false)	String orderStatus) {

		ConditionVo vo = new ConditionVo();
		
		try {
			if (!StringUtil.isEmpty(startTime))
				vo.setStartTime(startTime);
			if (!StringUtil.isEmpty(endTime))
				vo.setStartTime(endTime);
			if (!StringUtil.isEmpty(cityId))
				vo.setCityId(Integer.parseInt(cityId));
			if (!StringUtil.isEmpty(userAddress))
				vo.setUserAddress(userAddress);
			if (!StringUtil.isEmpty(companyId))
				vo.setCompanyId(Integer.parseInt(companyId));
			if (!StringUtil.isEmpty(userNo))
				vo.setUserNo(userNo);
			if (!StringUtil.isEmpty(userName))
				vo.setUserName(userName);	
			if (!StringUtil.isEmpty(orderId))
				vo.setOrderId(Long.parseLong(orderId));
			if (!StringUtil.isEmpty(userId))
				vo.setUserId(Integer.parseInt(userId));
			if (!StringUtil.isEmpty(orderStatus))
				vo.setOrderStatus(Integer.parseInt(orderStatus));
			
			vo.setIndex(new Integer(0));
			vo.setCount(new Integer(-1));

			return orderManage.exportOrderList(vo);
		} catch (Exception e) {
			log.error("查询订单列表异常，param={}", JSON.toJSONString(vo), e);
		}
		return null;
	}

	@RequestMapping("/detail")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public String detail(
			@RequestParam(name="order_id",		required = true)	long orderId) {
		return orderManage.queryOrder(orderId);
	}

	@RequestMapping("/cancel")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public String cancel(
			@RequestParam(name="user_id",		required = true)	int userId,
			@RequestParam(name="email",		required = true)		String email,
			@RequestParam(name="order_id",		required = true)	long orderId,
			@RequestParam(name="price",		required = true)		String price,
			@RequestParam(name="reason",		required = true)	String reason,
			@RequestParam(name="status",		required = true)	int status,
			@RequestParam(name="refund_sh_coin",		required = true)	Integer refundSHCoin) {//是否退实惠现金，1-是，2-否
		return orderManage.cancelLocalOrder(userId, email, orderId, price, reason, refundSHCoin, status);
	}

	@RequestMapping("/unusualOrder/count")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public String count( ) {
		return orderManage.countunusual();
	}

	@RequestMapping("/unusualOrder/query")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public String query( ) {
		return orderManage.queryUnusual();
	}

	@RequestMapping(path = "/unusualOrder/export", produces = { "application/vnd.ms-excel; charset=UTF-8" })
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Response export( ) {
		String fileName = orderManage.exportUnusual();
		File file = new File(fileName);
		Response.ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=\"unusualOrder.xlsx\"");
		response.header("content-transfer-encoding", "binary");

		return response.build();
	}

}
