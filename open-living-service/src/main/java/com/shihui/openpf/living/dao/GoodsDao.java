/**
 * 
 */
package com.shihui.openpf.living.dao;

import com.shihui.openpf.living.entity.Goods;
import org.springframework.stereotype.Repository;

/**
 * @author zhouqisheng
 *
 * @version 1.0 Created at: 2016年1月19日 下午3:42:49
 */
@Repository
public class GoodsDao extends AbstractDao<Goods> {
	
	public Goods findById(Long id){
		String sql = "select * from " + this.tableName + " where goods_id=?";
		return this.queryForObject(sql, id);
	}

}
