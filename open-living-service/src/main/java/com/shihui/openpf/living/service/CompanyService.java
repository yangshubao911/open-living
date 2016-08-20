package com.shihui.openpf.living.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shihui.commons.ApiLogger;
import com.shihui.openpf.living.dao.CompanyDao;
import com.shihui.openpf.living.entity.Company;
import com.shihui.openpf.living.entity.support.ServiceTypeEnum;
import com.shihui.openpf.living.util.SimpleResponse;

/**
 * Created by zhoutc on 2015/12/16.
 */
@Service
public class CompanyService {

    @Resource
    CompanyDao companyDao;

    public List<Company> queryCompanies(int cityId, ServiceTypeEnum serviceTypeEnum) {
        return companyDao.queryList(cityId, serviceTypeEnum.getValue());
    }
    
    public Object create(Company company){
    	JSONObject jo = new JSONObject();
		try {
			int companyId = (int)companyDao.insert(company);
			jo.put("companyId", companyId);
			jo.put("response", new SimpleResponse(0, "创建成功"));
			return jo;
		} catch (Exception e) {
			ApiLogger.error("创建广告位异常，参数={"+JSONObject.toJSONString(company)+"}" + e.getMessage());
		}
		jo.put("response", new SimpleResponse(1, "创建失败"));
		return jo;
	}
    
    public Object update(Company company){
		int result = 0;
		try {
			result = companyDao.update(company);
		} catch (Exception e) {
			ApiLogger.error("更新缴费单位异常，{"+JSON.toJSONString(company)+"}" + e.getMessage());
		}
		
		return JSON.toJSON(result > 0 ? new SimpleResponse(0, "更新成功") :  new SimpleResponse(1, "更新失败"));
	}
    
    public Object query(int companyId) {
    	JSONObject jo = new JSONObject();
    	Company company = companyDao.findById(companyId);
    	if(company == null)
    		jo.put("response", new SimpleResponse(1, "查询失败"));
    	else {
    		jo.put("response", new SimpleResponse(0, "查询成功"));
    		jo.put("company", company);
    	}
        return jo;
    }
    public Object query(String companyNo) {
    	JSONObject jo = new JSONObject();
    	Company company = companyDao.findByNo(companyNo);
    	if(company == null)
    		jo.put("response", new SimpleResponse(1, "查询失败"));
    	else {
    		jo.put("response", new SimpleResponse(0, "查询成功"));
    		jo.put("company", company);
    	}
        return jo;
    }
    
    public Object list(Integer serviceId, Integer categoryId, Integer cityId) {        
		JSONArray ja = new JSONArray();
		
		List<Company> companyList = companyDao.queryList(serviceId, categoryId, cityId);
		
		if(companyList != null) {
			for(Company company : companyList) {
				JSONObject jo = new JSONObject();
				jo.put("companyId", company.getCompanyId());
				jo.put("companyName", company.getCompanyName());
				jo.put("companyNo", company.getCompanyNo());
//				jo.put("serviceType", company.getServiceType());
//				jo.put("feeType", company.getFeeType());
//				jo.put("userNoLengMin", company.getUserNoLengMin());
//				jo.put("userNoLengMax", company.getUserNoLengMax());
//				jo.put("payMin", company.getPayMin());
//				jo.put("payMax", company.getPayMax());
//				jo.put("barcode", company.getBarcode());
				ja.add(jo);
			}
		}
		ApiLogger.info("CompanyService: list() : " + ja.toJSONString());
		return ja;
    }

}
