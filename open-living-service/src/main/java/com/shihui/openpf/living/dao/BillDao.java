package com.shihui.openpf.living.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.support.BillStatusEnum;

/**
 * Created by zhoutc on 2015/12/16.
 */
@Repository
public class BillDao extends AbstractDao<Bill> {

	public List<Bill> queryTopN(long userId, int count) {
		return this.queryForList( "SELECT * FROM `bill` WHERE bill_status = ? "
								+" AND order_id in (SELECT order_id FROM `order` WHERE user_id = ? ))"
								+" ORDER BY pay_time DESC  LIMIT ? ",
				new Object[]{BillStatusEnum.CheckSuccess.getValue(),userId, count});
	}

	public Bill findById(Long orderId){
		return queryForObject("SELECT * FROM `bill` WHERE order_id = ? ", orderId);
	}

	public Bill findByBillNo(String billNo){
		return queryForObject("SELECT * FROM `bill` WHERE serial_no = ? ", billNo);
	}

	public int updateBillStatus(long orderId, int status) {
		return this.jdbcTemplate.update("UPDATE `bill SET bill_status = ?, update_time = ? WHERE order_id = ?", new Object[]{status, new Date(), orderId});
	}

	public long getOrderIdByBillNo(String billNo) {
		return queryLongValue("SELECT order_id FROM `bill` WHERE serial_no = ? ", new Object[]{billNo});
	}
}
