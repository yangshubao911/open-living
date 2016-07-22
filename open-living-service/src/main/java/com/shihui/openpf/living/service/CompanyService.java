package com.shihui.openpf.living.service;

import java.util.List;
import com.shihui.openpf.living.entity.support.ServiceTypeEnum;
import com.shihui.openpf.living.entity.Company;
import com.shihui.openpf.living.dao.CompanyDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

}

