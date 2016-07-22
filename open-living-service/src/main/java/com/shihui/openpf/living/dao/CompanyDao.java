package com.shihui.openpf.living.dao;

import java.util.List;
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
}
