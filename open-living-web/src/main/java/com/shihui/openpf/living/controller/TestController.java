package com.shihui.openpf.living.controller;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
import com.shihui.commons.ApiLogger;
import com.shihui.openpf.living.service.TestService;
import com.shihui.openpf.living.service.CompanyService;
import com.shihui.openpf.living.entity.Company;
import com.shihui.openpf.living.service.ClientService;
import com.shihui.openpf.living.util.SftpUtil;
import com.shihui.openpf.living.util.SimpleResponse;
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
	@Resource ClientService clientService;

	@RequestMapping(path = "/reqKey")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object reqKey(){
		return JSON.toJSON(testService.reqKey());
	}
	
	@RequestMapping("/company/create")
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
	
	@RequestMapping("/company/update")
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
	
	@RequestMapping("/company/detail")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object detail(
			@RequestParam(name = "companyId", required = true)Integer companyId) {
		return companyService.query(companyId);
	}
	
	@RequestMapping("/company/detailByNo")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object detailByNo(
			@RequestParam(name = "companyNo", required = true)String companyNo) {
		return companyService.query(companyNo);
	}

	//
	//
	//
	
	@RequestMapping("/query")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object queryDoc1(
			@RequestParam(name = "index", required = true, defaultValue="0")Integer index) {
		return testService.query(index);
	}
	@RequestMapping("/checkquery")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object checkQuery() {
		return testService.checkQuery();
	}	
	@RequestMapping("/checkpay")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object checkPay() {
		return testService.checkPay();
	}	

	@RequestMapping("/pay")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object payDoc1() {
		return testService.pay();
	}	
	@RequestMapping("/queryexc0")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object queryExc0() {
		return testService.queryExc1();
	}
	
	@RequestMapping("/queryexc1")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object queryExc1() {
		return testService.queryExc1();
	}
	
//	@RequestMapping("/sftp")
//	@ResponseBody
//	@Access(type = AccessType.INTERNAL)
//	public Object sftp() {
//		return testService.sftp();
//	}	
//	@RequestMapping("/sftpFile")
//	@ResponseBody
//	@Access(type = AccessType.INTERNAL)
//	public Object sftpFile() {
//		ApiLogger.info(" SftpUtil.download() : " 
//		+
//		SftpUtil.download("172.16.88.98", "guangdabank", "FKo7QOrVgxY9", "/home/guangdabank/order/HZKY_20160823_1.txt", "/home/guangdabank/order/HZKY_20160823_1.txt")
//
//				);
//		return JSONObject.toJSON(new SimpleResponse(0,"OK"));
//	}	
	
	@RequestMapping("/billCheck")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object billCheck() {
		return testService.billCheck();
	}	
	@RequestMapping("/billRefund")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object billRefund() {
		return testService.billRefund();
	}

//	@RequestMapping("/xmlTest")
//	@ResponseBody
//	@Access(type = AccessType.INTERNAL)
//	public Object xmlTest() {
//		return testService.xmlTest();
//	}
}
