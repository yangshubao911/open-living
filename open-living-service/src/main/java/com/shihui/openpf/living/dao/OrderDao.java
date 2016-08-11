/**
 * from open-home
 */
package com.shihui.openpf.living.dao;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.shihui.api.order.common.enums.OrderStatusEnum;
import com.shihui.openpf.living.entity.Order;

/**
 * @author zhouqisheng
 *
 */
@Repository
public class OrderDao extends AbstractDao<Order> {
	private Logger log = LoggerFactory.getLogger(getClass());
	/**
	 * 查询用户有效订单数
	 * 
	 * @param userId
	 * @return
	 */
	public int queryEffectiveOrder(Long userId) {
		String sql = "select count(*) from `" + this.tableName + "` where user_id=? and order_status in ("
				+ OrderStatusEnum.OrderUnpaid.getValue() + "," + OrderStatusEnum.OrderUnStockOut.getValue() + ","
				+ OrderStatusEnum.OrderDistribute.getValue() + "," + OrderStatusEnum.OrderHadReceived.getValue() + ")";
		return this.queryCount(sql, userId);
	}
	

	public Order findById(Long orderId){
		String sql = "select * from `" + this.tableName + "` where order_id=?";
		return this.queryForObject(sql, orderId);
	}

	public List<Order> queryTopN(int count) {
		return this.queryForList( "SELECT * FROM `order` WHERE order_status = ? ORDER BY update_time DESC  LIMIT ?", new Object[]{OrderStatusEnum.OrderHadReceived.getValue(),count});
	}

	public List<Order> queryOrderUnStockOut() {
		return this.queryForList( "SELECT * FROM `order` WHERE order_status = ? ORDER BY update_time ASC", new Object[]{OrderStatusEnum.OrderUnStockOut.getValue()});
	}
	
	public List<Order> queryOrder(Order order, String startTime, String endTime, Integer page, Integer size) {
		StringBuilder sql = new StringBuilder(
				"select a.*,b.request_id,c.service_start_time from (`order`a left join `request` b on a.order_id = b.order_id) left join `contact` as c on a.order_id = c.order_id where 1 = 1 ");

		Field[] fields = Order.class.getDeclaredFields();
		try {
			ArrayList<Object> valus = new ArrayList<Object>();
			for (Field field : fields) {
				String fieldName = this.fieldNameMap.get(field.getName());
				if (fieldName == null) {
					fieldName = this.idsFieldNameMap.get(field.getName());
				}
				if (fieldName == null) {
					continue;
				}
				field.setAccessible(true);
				Transient transientAno = field.getAnnotation(Transient.class);
				if (transientAno != null) {
					continue;
				}
				Object value = field.get(order);
				if (value != null) {
					sql.append("and a.").append(fieldName).append(" = ? ");
					valus.add(value);
				}

			}
			if (startTime != null && !startTime.equals("")) {
				sql.append("and a.create_time >= ? ");
				valus.add(new SimpleDateFormat("yyyyMMddHHmmss").parse(startTime));
			}
			if (endTime != null && !endTime.equals("")) {
				sql.append("and a.create_time <= ? ");
				valus.add(new SimpleDateFormat("yyyyMMddHHmmss").parse(endTime));
			}
			sql.append("order by a.create_time desc ");
			if (page != null && size != null) {
				sql.append("limit ").append((page - 1) * size).append(",").append(size);
			}
			return super.queryForList(sql.toString(), valus.toArray());
		} catch (Exception e) {
			log.error("OrderDao error!!", e);
		}
		return null;
	}

	public int countQueryOrder(Order order, String startTime, String endTime) {
		StringBuilder sql = new StringBuilder("select count(*) from `order` where 1 = 1 ");
		Field[] fields = Order.class.getDeclaredFields();
		try {
			ArrayList<Object> valus = new ArrayList<Object>();
			for (Field field : fields) {
				String fieldName = this.fieldNameMap.get(field.getName());
				if (fieldName == null) {
					fieldName = this.idsFieldNameMap.get(field.getName());
				}
				if (fieldName == null) {
					continue;
				}
				field.setAccessible(true);
				Transient transientAno = field.getAnnotation(Transient.class);
				if (transientAno != null) {
					continue;
				}
				Object value = field.get(order);

				if (value != null) {
					sql.append("and ").append(fieldName).append(" = ? ");
					valus.add(value);
				}
			}
			if (startTime != null && !startTime.equals("")) {
				sql.append("and create_time >= ? ");
				valus.add(new SimpleDateFormat("yyyyMMddHHmmss").parse(startTime));
			}
			if (endTime != null && !endTime.equals("")) {
				sql.append("and create_time <= ? ");
				valus.add(new SimpleDateFormat("yyyyMMddHHmmss").parse(endTime));
			}
			//return jdbcTemplate.queryForInt(sql.toString(), valus.toArray());
			return this.queryCount(sql.toString(), valus.toArray());
		} catch (Exception e) {
			log.error("OrderDao error!!", e);
		}
		return -1;
	}

	/**
	 * 根据订单ID查询订单
	 * 
	 * @param orderId
	 *            订单Id
	 * @return 订单详情
	 */
	public Order queryOrder(long orderId) {
		String sql = "select * from `order` where order_id = ?";
		try {
			return super.queryForObject(sql, orderId);
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * 查询异常订单数量
	 *
	 * @return 异常订单数量
	 */
	public int countUnusual() {
		return super.queryCount("SELECT count(*) FROM `order` WHERE order_status = ? AND update_time < DATE_ADD(NOW(), INTERVAL -24*3 HOUR) ", 
				OrderStatusEnum.OrderUnStockOut.getValue());
	}

	/**
	 * 查询异常订单数量
	 *
	 * @return 异常订单数量
	 */
	public List<Order> queryUnusual() {
		return super.queryForList("SELECT * FROM `order` WHERE order_status = ? AND update_time < DATE_ADD(NOW(), INTERVAL -24*3 HOUR) ORDER BY update_time DESC ", 
				OrderStatusEnum.OrderUnStockOut.getValue());
	}

	/**
	 * 查询同一用户或者同一设备有效订单数量
	 * @param userId
	 * @param serviceId
	 * @param deviceId
	 * @return 订单数量
	 */
	public int countOrders(long userId, int serviceId, String deviceId) {
		String sql = "select count(*) from `order` where (user_id = ? or device_id=?) and service_id = ? and order_status in ("
				+ OrderStatusEnum.OrderUnpaid.getValue() + "," + OrderStatusEnum.OrderUnConfirm.getValue() + ","
				+ OrderStatusEnum.OrderUnStockOut.getValue() + "," + OrderStatusEnum.OrderDistribute.getValue() + ","
				+ OrderStatusEnum.OrderHadReceived.getValue() + ")";
		return super.queryCount(sql, userId, deviceId, serviceId);
	}
	
	public int updateOrderStatus(long orderId, int status) {
		return this.jdbcTemplate.update("UPDATE `order` SET order_status = ?, update_time = ? WHERE order_id = ?", new Object[]{status, new Date(), orderId});
	}
}
