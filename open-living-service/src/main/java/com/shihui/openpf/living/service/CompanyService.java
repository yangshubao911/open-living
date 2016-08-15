package com.shihui.openpf.living.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
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
    
    /**
     * 
     *
     * @param serviceTypeEnum 
     *
     * @return 
     */
    public List<Company> queryCompanies(int cityId, ServiceTypeEnum serviceTypeEnum) {
        return companyDao.queryList(cityId, serviceTypeEnum.getValue());
    }
    
    public Object createCompany(Company company){
		try {
			Date now = new Date();

			companyDao.save(company);
			return new SimpleResponse(0, "创建成功");
		} catch (Exception e) {
			ApiLogger.error("创建广告位异常，参数={"+JSON.toJSONString(company)+"}" + e.getMessage());
		}
		return new SimpleResponse(1, "创建失败");
	}
}

