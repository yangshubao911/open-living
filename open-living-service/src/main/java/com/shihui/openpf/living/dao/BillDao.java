package com.shihui.openpf.living.dao;

import com.shihui.openpf.living.entity.support.BillStatusEnum;
import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.Order;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * Created by zhoutc on 2015/12/16.
 */
@Repository
public class BillDao extends AbstractDao<Bill> {

	public List<Bill> queryTopN(long userId, int count) {
		return this.queryForList( "SELECT * FROM `bill` WHERE bill_status = ? "
								+" AND orderId in (SELECT orderId FROM `order` WHERE user_id = ? ))"
								+" ORDER BY pay_time DESC  LIMIT ? ",
				new Object[]{BillStatusEnum.CheckSuccess.getValue(),userId, count});
	}

	public Bill findById(Long orderId){
		String sql = "SELECT * FROM `bill` WHERE order_id = ? ";
		return queryForObject(sql, orderId);
	}

}
