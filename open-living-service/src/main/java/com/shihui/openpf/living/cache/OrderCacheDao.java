/**
 * 
 */
package com.shihui.openpf.living.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.shihui.openpf.common.tools.StringUtil;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shihui.openpf.common.tools.Constants;
//import com.shihui.openpf.living.entity.support.OrderVo;
import com.shihui.openpf.living.entity.Order;
import com.shihui.openpf.living.entity.Bill;
import com.shihui.openpf.living.entity.support.OrderBillVo;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author zhouqisheng
 *
 */
@Repository
public class OrderCacheDao {
	private String ORDER_PREFIX = Constants.REDIS_KEY_PREFIX + Constants.REDIS_KEY_SEPARATOR + "living-order" + Constants.REDIS_KEY_SEPARATOR;
    @Resource
    private ShardedJedisPool jedisPool;
    
    /**
     * 缓存订单信息
     * @param orderVo
     */
    public void set(Integer userId, String tempId, OrderBillVo vo){//tempId = CompanyNo+userNo
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.hset(ORDER_PREFIX + userId, tempId, JSON.toJSONString(vo));
    	}
    }
    
    /**
     * 获得缓存订单信息
     * @param serviceId
     * @param orderId
     */
    public OrderBillVo get(Integer userId, String tempId){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		String orderString = jedis.hget(ORDER_PREFIX + userId, tempId);
			if(StringUtil.isEmpty(orderString))return null;
			return JSON.parseObject(orderString,OrderBillVo.class);
    	}
    }
    
    /**
     * 获得所有缓存订单信息
     * @param serviceId
     * @return
     */
    public List<OrderBillVo> getAll(Integer userId){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		Map<String, String> result = jedis.hgetAll(ORDER_PREFIX + userId);
    		List<OrderBillVo> list = new ArrayList<>(result.size());
    		for(String s : result.values()){
    			list.add(JSON.parseObject(s, OrderBillVo.class));
    		}
    		return list;
    	} catch (Exception e) {
    		throw e;
    	}
    }
    
    /**
     * 删除订单缓存
     * @param orderId
     */
    public void del(Integer userId, String tempId){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.hdel(ORDER_PREFIX + userId, String.valueOf(tempId));
    	}
    }
    
    /**
     * 锁定订单
     * @param orderId
     * @return
     */
    public boolean lockVo(Integer userId, String tempId){
    	OrderBillVo ov = this.get(userId, tempId);
    	if(ov == null){
    		return false;
    	}
    	String key = ORDER_PREFIX + "lock_vo" +  Constants.REDIS_KEY_SEPARATOR + tempId;
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		Long result = jedis.incr(key);
    		if(result == 1){
    			jedis.expire(key, 60*60);
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * 解锁订单
     * @param orderId
     */
    public void unlockVo(String tempId){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.del(ORDER_PREFIX + "lock_vo" +  Constants.REDIS_KEY_SEPARATOR + tempId);
    	}
    }

    /**
     * 锁定订单
     * @param orderId
     * @return
     */
    public boolean lockOrder(long orderId){
    	String key = ORDER_PREFIX + "lock" +  Constants.REDIS_KEY_SEPARATOR + orderId;
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		Long result = jedis.incr(key);
    		if(result == 1){
    			jedis.expire(key, 60*10);
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * 解锁订单
     * @param orderId
     */
    public void unlockOrder(long orderId){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.del(ORDER_PREFIX + "lock" +  Constants.REDIS_KEY_SEPARATOR + orderId);
    	}
    }
}
