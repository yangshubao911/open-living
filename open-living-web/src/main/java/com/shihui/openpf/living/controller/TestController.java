package com.shihui.openpf.living.controller;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
import com.shihui.commons.ApiLogger;
import com.shihui.openpf.living.service.TestService;
import com.shihui.openpf.living.service.CompanyService;
import com.shihui.openpf.living.entity.Company;


/**
 * @author zhouqisheng
 *
 * @date 2016年3月2日 下午3:47:23
 *
 */
@Controller
@RequestMapping(path = "/v2/openpf/living/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TestController {

	@Resource private TestService testService;
	@Resource CompanyService companyService;

	@RequestMapping(path = "/reqKey")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object reqKey(){
		return JSON.toJSON(testService.reqKey());
	}
	
	/*

	private String ;

	private Integer ;

	private Integer ;

	private Integer ;

	private Integer userNoLengMin;
	
	private Integer userNoLengMax;

	private String payMin;

	private String payMax;

	private Integer dateChoice;
	
	private Integer barcode;

	private Integer status;
	 
	 */
//	@RequestMapping("/createCompany")
//	@ResponseBody
//	@Access(type = AccessType.COMMON)
//	public Object createOrder(
//			@RequestParam(name="companyNo", required = true) String companyNo,
//			@RequestParam(name="companyName", required = true) String companyName,
//			@RequestParam(name="serviceType", required = true) Integer serviceType,
//			@RequestParam(name="cityId", required = true) Integer cityId,
//			@RequestParam(name="feeType", required = true) Integer feeType,
//			
//			@RequestParam(name="userId", required = true) Integer userId,
//			@RequestParam(name="tempId", required = true) String tempId,
//			@RequestParam(name="costSh", required = true) Integer costSh) {
//
//		Company company;
//
//		company.companyNo;
//
//		company.companyName;
//
//		company.serviceType;
//
//		company.cityId;
//
//		company.feeType;
//
//		company.userNoLengMin;
//	
//		company.userNoLengMax;
//
//		company.payMin;
//
//		company.payMax;
//	
//		company.barcode;
//
//		company.status;		
//
//		return companyService.createCompany(company);
//	}
}
