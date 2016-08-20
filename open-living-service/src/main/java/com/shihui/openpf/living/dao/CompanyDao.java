package com.shihui.openpf.living.dao;

import java.util.ArrayList;
import java.util.List;

import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Company;
import org.springframework.stereotype.Repository;

/**
 * Created by zhoutc on 2015/12/16.
 */
@Repository
public class CompanyDao extends AbstractDao<Company> {

	public List<Company> queryList(int cityId, int serviceType) {
		return queryForList("SELECT * FROM `company` WHERE city_id = ? AND service_type = ?",
				new Object[]{cityId, serviceType});
	}
	public List<Company> queryList(int cityId) {
		return queryForList("SELECT * FROM `company` WHERE city_id = ?",
				new Object[]{cityId});
	}
	
	public Company findById(Integer companyId){
		String sql = "SELECT * FROM `company` WHERE company_id = ? ";
		return queryForObject(sql, companyId);
	}

	public Company findByNo(String companyNo){
		String sql = "SELECT * FROM `company` WHERE company_no = ? ";
		return queryForObject(sql, companyNo);
	}

	public List<Company> queryList(Integer serviceId, Integer categoryId, Integer cityId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM `company` WHERE 1=1 ");
		
		ArrayList<Object> valueList = new ArrayList<Object>();
		
		if(serviceId != null) {
			sql.append(" AND service_id = ? ");
			valueList.add(serviceId);			
		}
		if(categoryId != null) {
			sql.append(" AND category_id = ? ");
			valueList.add(categoryId);			
		}
		if(cityId != null) {
			sql.append(" AND city_id = ? ");
			valueList.add(cityId);			
		}

		return queryForList(sql.toString(), valueList.toArray());
	}

}
