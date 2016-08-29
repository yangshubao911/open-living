package com.shihui.openpf.living.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shihui.openpf.living.entity.OrderBillTop5;
import com.shihui.openpf.living.entity.support.BillStatusEnum;

/**
 * Created by zhoutc on 2015/12/16.
 */
@Repository
public class OrderBillTop5Dao extends AbstractDao<OrderBillTop5> {
	public List<OrderBillTop5> query(long userId, int count) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.order_id, a.user_id, a.group_id, a.mid, a.service_id, a.goods_id, a.goods_version, b.category_id, b.fee_name, b.city_id, b.company_id, b.company_no, b.bill_key, b.bill_key_type ");
		sql.append("FROM `order` AS a LEFT JOIN `bill` AS b ON a.order_id = b.order_id ");
//		sql.append("WHERE b.bill_status = ? AND a.user_id = ? ORDER BY b.pay_time DESC  LIMIT ? ");
		sql.append("WHERE a.user_id = ? ORDER BY b.pay_time DESC  LIMIT ? ");

//		return this.queryForList(sql.toString(), new Object[]{BillStatusEnum.CheckSuccess.getValue(),userId, count});
		return this.queryForList(sql.toString(), new Object[]{userId, count});
	}

}
