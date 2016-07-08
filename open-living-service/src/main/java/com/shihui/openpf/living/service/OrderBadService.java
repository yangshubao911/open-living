/**
 * 
 */
package com.shihui.openpf.living.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shihui.openpf.living.dao.OrderBadDao;
import com.shihui.openpf.living.entity.OrderBad;

/**
 * @author zhouqisheng
 * @date 2016年3月14日 下午2:20:13
 *
 */
@Service
public class OrderBadService {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Resource
	private OrderBadDao orderBadDao;

	/* (non-Javadoc)
	 * @see com.shihui.openpf.home.service.api.OrderBadService#save(com.shihui.openpf.home.model.OrderBad)
	 */

	public boolean save(OrderBad orderBad) {
		Date now = new Date();
		orderBad.setCreateTime(now);
		orderBad.setUpdateTime(now);
		
		int ret = 0;
		try {
			ret = orderBadDao.save(orderBad);
		} catch (Exception e) {
			log.error("保存异常订单异常，orderId={}", orderBad.getOrderId(), e);
		}
		
		return ret > 0;
	}

	/* (non-Javadoc)
	 * @see com.shihui.openpf.home.service.api.OrderBadService#update(com.shihui.openpf.home.model.OrderBad)
	 */

	public boolean update(OrderBad orderBad) {
		Date now = new Date();
		orderBad.setUpdateTime(now);
		
		int ret = 0;
		try {
			ret = orderBadDao.update(orderBad);
		} catch (Exception e) {
			log.error("更新异常订单异常，orderId={}", orderBad.getOrderId(), e);
		}
		
		return ret > 0;
	}

	/* (non-Javadoc)
	 * @see com.shihui.openpf.home.service.api.OrderBadService#delete(com.shihui.openpf.home.model.OrderBad)
	 */

	public boolean delete(OrderBad orderBad) {
		int ret = 0;
		try {
			ret = orderBadDao.delete(orderBad);
		} catch (Exception e) {
			log.error("删除异常订单异常，orderId={}", orderBad.getOrderId(), e);
		}
		
		return ret > 0;
	}

	/* (non-Javadoc)
	 * @see com.shihui.openpf.home.service.api.OrderBadService#querByCondition(com.shihui.openpf.home.model.OrderBad)
	 */

	public List<OrderBad> querByCondition(OrderBad orderBad) {
		List<OrderBad> ret = null;
		try {
			ret = orderBadDao.findByCondition(orderBad);
		} catch (Exception e) {
			log.error("查询异常订单异常，orderId={}", orderBad.getOrderId(), e);
		}
		
		return ret;
	}

}
