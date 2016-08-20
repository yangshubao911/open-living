package com.shihui.openpf.living.controller;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
import com.shihui.commons.ApiLogger;
import com.shihui.openpf.living.service.TestService;
import com.shihui.openpf.living.service.CompanyService;
import com.shihui.openpf.living.entity.Company;
import com.shihui.openpf.living.service.ClientService;

/**
 * @author zhouqisheng
 *
 * @date 2016年3月2日 下午3:47:23
 *
 */
@Controller
@RequestMapping(path = "/v2/openpf/living/company", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CompanyController {

	@Resource CompanyService companyService;

	@RequestMapping("/create")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object createCompany(
			@RequestParam(name="companyNo",		required = true) 					String companyNo,
			@RequestParam(name="companyName",	required = true) 					String companyName,
			@RequestParam(name="serviceId",		required = true) 					Integer serviceId,
			@RequestParam(name="serviceType",	required = true) 					Integer serviceType,
			@RequestParam(name="cityId",		required = true) 					Integer cityId,
			@RequestParam(name="feeType",		required = true) 					Integer feeType,
			@RequestParam(name="userNoLengMin",	required = false, defaultValue="0") Integer userNoLengMin,
			@RequestParam(name="userNoLengMax",	required = false, defaultValue="0") Integer userNoLengMax,
			@RequestParam(name="payMin",		required = false, defaultValue="0") String payMin,
			@RequestParam(name="payMax",		required = false, defaultValue="0") String payMax,
			@RequestParam(name="barcode",		required = false, defaultValue="0") Integer barcode) {

		Company company = new Company();
		company.setCompanyNo(companyNo);
		company.setCompanyName(companyName);
		company.setServiceType(serviceId);
		company.setServiceType(serviceType);
		company.setCityId(cityId);
		company.setFeeType(feeType);
		company.setUserNoLengMin(userNoLengMin);
		company.setUserNoLengMax(userNoLengMax);
		company.setPayMin(payMin);
		company.setPayMax(payMax);
		company.setBarcode(barcode);
		company.setStatus(0);		
		
		return companyService.create(company);
	}
	
	@RequestMapping("/update")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object updateCompany(
			@RequestParam(name="companyId",		required = true)	Integer companyId,
			@RequestParam(name="companyNo",		required = false) 	String companyNo,
			@RequestParam(name="companyName",	required = false) 	String companyName,
			@RequestParam(name="serviceId",		required = false) 	Integer serviceId,
			@RequestParam(name="serviceType",	required = false) 	Integer serviceType,
			@RequestParam(name="cityId",		required = false) 	Integer cityId,
			@RequestParam(name="feeType",		required = false) 	Integer feeType,
			@RequestParam(name="userNoLengMin",	required = false)	Integer userNoLengMin,
			@RequestParam(name="userNoLengMax",	required = false)	Integer userNoLengMax,
			@RequestParam(name="payMin",		required = false)	String payMin,
			@RequestParam(name="payMax",		required = false)	String payMax,
			@RequestParam(name="barcode",		required = false)	Integer barcode,
			@RequestParam(name="status",		required = false)	Integer status) {

		Company company = new Company();
		company.setCompanyId(companyId);
		company.setCompanyNo(companyNo);
		company.setCompanyName(companyName);
		company.setServiceType(serviceId);
		company.setServiceType(serviceType);
		company.setCityId(cityId);
		company.setFeeType(feeType);
		company.setUserNoLengMin(userNoLengMin);
		company.setUserNoLengMax(userNoLengMax);
		company.setPayMin(payMin);
		company.setPayMax(payMax);
		company.setBarcode(barcode);
		company.setStatus(status);		
		
		return companyService.update(company);
	}
	
	@RequestMapping("/detail")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object detail(
			@RequestParam(name = "companyId", required = true)Integer companyId) {
		return companyService.query(companyId);
	}
	
	@RequestMapping("/detailByNo")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object detailByNo(
			@RequestParam(name = "companyNo", required = true)String companyNo) {
		return companyService.query(companyNo);
	}

	@RequestMapping("/list")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object list(
			@RequestParam(name="serviceId", required = true) Integer serviceId,
			@RequestParam(name="categoryId", required = true) Integer categoryId,
			@RequestParam(name="cityId", required = true) Integer cityId) {

		ApiLogger.info("Controller: /v2/openpf/living/company/list : list() : "
				+ "serviceId: " + serviceId
				+ "categoryId: " + categoryId
				+ "cityId: " + cityId );

		return companyService.list(serviceId, categoryId, cityId);
	}
}
