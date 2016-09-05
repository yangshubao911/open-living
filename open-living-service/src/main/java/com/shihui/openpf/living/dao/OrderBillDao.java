package com.shihui.openpf.living.dao;

import com.shihui.openpf.living.entity.support.BillStatusEnum;
import com.shihui.commons.ApiLogger;
import com.shihui.openpf.living.entity.OrderBill;
import com.shihui.openpf.living.entity.support.ConditionVo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.stereotype.Repository;

/**
 * Created by zhoutc on 2015/12/16.
 */
@Repository
public class OrderBillDao extends AbstractDao<OrderBill> {

	public List<OrderBill> query(ConditionVo vo) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.order_id,a.user_id,a.price,a.pay_time,a.order_status,b.fee_name,c.city_name,b.bill_key,b.user_address,b.bill_status,b.user_name ");
		sql.append("FROM `livingorder` AS a LEFT JOIN `bill` AS b ON a.order_id = b.order_id ");
		sql.append("LEFT JOIN `goods` AS c ON a.goods_id = c.goods_id ");
		sql.append("WHERE 1=1 ");
		
		ArrayList<Object> valueList = new ArrayList<Object>();
		
		String startTime = vo.getStartTime();
		if(startTime != null) {
			try{
				Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(startTime);
				valueList.add(date);
				sql.append(" AND a.create_time >= ? ");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		String endTime = vo.getEndTime();
		if(endTime != null) {
			try {
				Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(endTime);
				valueList.add(date);
				sql.append(" AND a.create_time <= ? ");

			}catch(Exception e) {
				e.printStackTrace();
			}
		}

		Integer cityId = vo.getCityId();
		if(cityId != null) {
			sql.append(" AND c.city_id = ? ");
			valueList.add(cityId);
		}

		String userAddress = vo.getUserAddress();
		if(userAddress != null) {
			sql.append(" AND b.user_address LIKE ? ");
			valueList.add("%" + userAddress.trim() + "%");
		}

		Integer companyId = vo.getCompanyId();
		if(companyId != null) {
			sql.append(" AND b.company_id = ? ");
			valueList.add(companyId);
		}

		String userNo = vo.getUserNo();
		if(userNo != null) {
			sql.append(" AND b.bill_key = ? ");
			valueList.add(userNo);
		}

		String userName = vo.getUserName();
		if(userName != null) {
			sql.append(" AND b.user_name LIKE ? ");
			valueList.add("%" + userName + "%");
		}

		Long orderId = vo.getOrderId();
		if(orderId != null) {
			sql.append(" AND a.order_id = ? ");
			valueList.add(orderId);
		}

		Integer userId = vo.getUserId();
		if(userId != null) {
			sql.append(" AND a.user_id = ? ");
			valueList.add(userId);
		}

		Integer orderStatus = vo.getOrderStatus();
		if(orderStatus != null) {
			sql.append(" AND a.order_status = ? ");
			valueList.add(orderStatus);
		}

		Integer serviceId = vo.getServiceId();
		if(serviceId != null) {
			sql.append(" AND a.service_id = ? ");
			valueList.add(serviceId);
		}

		if(vo.getIndex() != null && vo.getCount() != null) {
			sql.append(" LIMIT ?, ? "); 
			valueList.add(vo.getIndex());
			valueList.add(vo.getCount());
		}

ApiLogger.info("OrderBillDao : query : " + sql.toString() + " : " + valueList.toString());
		return this.queryForList(sql.toString(), valueList.toArray());
	}
	
	public int queryCount(ConditionVo vo) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(*) ");
		sql.append("FROM `livingorder` AS a LEFT JOIN `bill` AS b ON a.order_id = b.order_id ");
		sql.append("LEFT JOIN `goods` AS c ON a.goods_id = c.goods_id ");
		sql.append("WHERE 1=1 ");
		
		ArrayList<Object> valueList = new ArrayList<Object>();
		
		String startTime = vo.getStartTime();
		if(startTime != null) {
			try{
				Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(startTime);
				valueList.add(date);
				sql.append(" AND a.create_time >= ? ");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		String endTime = vo.getEndTime();
		if(endTime != null) {
			try {
				Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(endTime);
				valueList.add(date);
				sql.append(" AND a.create_time <= ? ");

			}catch(Exception e) {
				e.printStackTrace();
			}
		}

		Integer cityId = vo.getCityId();
		if(cityId != null) {
			sql.append(" AND c.city_id = ? ");
			valueList.add(cityId);
		}

		String userAddress = vo.getUserAddress();
		if(userAddress != null) {
			sql.append(" AND b.user_address LIKE ? ");
			valueList.add("%" + userAddress.trim() + "%");
		}

		Integer companyId = vo.getCompanyId();
		if(companyId != null) {
			sql.append(" AND b.company_id = ? ");
			valueList.add(companyId);
		}

		String userNo = vo.getUserNo();
		if(userNo != null) {
			sql.append(" AND b.bill_key = ? ");
			valueList.add(userNo);
		}

		String userName = vo.getUserName();
		if(userName != null) {
			sql.append(" AND b.user_name LIKE ? ");
			valueList.add("%" + userName + "%");
		}

		Long orderId = vo.getOrderId();
		if(orderId != null) {
			sql.append(" AND a.order_id = ? ");
			valueList.add(orderId);
		}

		Integer userId = vo.getUserId();
		if(userId != null) {
			sql.append(" AND a.user_id = ? ");
			valueList.add(userId);
		}

		Integer orderStatus = vo.getOrderStatus();
		if(orderStatus != null) {
			sql.append(" AND a.order_status = ? ");
			valueList.add(orderStatus);
		}

		Integer serviceId = vo.getServiceId();
		if(serviceId != null) {
			sql.append(" AND a.service_id = ? ");
			valueList.add(serviceId);
		}
		
ApiLogger.info("OrderBillDao : query : " + sql.toString() + " : " + valueList.toString());
		return this.queryCount(sql.toString(), valueList.toArray());
	}
}
