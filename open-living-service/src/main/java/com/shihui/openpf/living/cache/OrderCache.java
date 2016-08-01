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
import com.shihui.openpf.common.tools.Constants;
import com.shihui.openpf.living.entity.support.OrderVo;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author zhouqisheng
 *
 */
@Repository
public class OrderCache {
	private String ORDER_PREFIX = Constants.REDIS_KEY_PREFIX + Constants.REDIS_KEY_SEPARATOR + "living-order";
    @Resource
    private ShardedJedisPool jedisPool;
    
    /**
     * 缓存订单信息
     * @param orderVo
     */
    public void set(OrderVo orderVo){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.hset(ORDER_PREFIX + Constants.REDIS_KEY_SEPARATOR + orderVo.getServiceId(), String.valueOf(orderVo.getOrderId()), JSON.toJSONString(orderVo));
    	}
    }
    
    /**
     * 获得缓存订单信息
     * @param serviceId
     * @param orderId
     */
    public OrderVo get(Integer serviceId, Long orderId){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		String orderString = jedis.hget(ORDER_PREFIX + Constants.REDIS_KEY_SEPARATOR + serviceId, String.valueOf(orderId));
			if(StringUtil.isEmpty(orderString))return null;
			return JSON.parseObject(orderString,OrderVo.class);
    	}
    }
    
    /**
     * 获得所有缓存订单信息
     * @param serviceId
     * @return
     */
    public List<OrderVo> getAll(Integer serviceId){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		Map<String, String> result = jedis.hgetAll(ORDER_PREFIX + Constants.REDIS_KEY_SEPARATOR + serviceId);
    		List<OrderVo> list = new ArrayList<>(result.size());
    		for(String s : result.values()){
    			list.add(JSON.parseObject(s, OrderVo.class));
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
    public void del(Integer serviceId, Long orderId){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.hdel(ORDER_PREFIX + Constants.REDIS_KEY_SEPARATOR + serviceId, String.valueOf(orderId));
    	}
    }
    
    /**
     * 锁定订单
     * @param orderId
     * @return
     */
    public boolean lockOrder(Integer serviceId, Long orderId){
    	OrderVo ov = this.get(serviceId, orderId);
    	if(ov == null){
    		return false;
    	}
    	String key = ORDER_PREFIX +  Constants.REDIS_KEY_SEPARATOR + "lock" +  Constants.REDIS_KEY_SEPARATOR + orderId;
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		Long result = jedis.incr(key);
    		if(result == 1){
    			jedis.expire(key, 30);
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * 解锁订单
     * @param orderId
     */
    public void unlockOrder(Long orderId){
    	String key = ORDER_PREFIX +  Constants.REDIS_KEY_SEPARATOR + "lock" +  Constants.REDIS_KEY_SEPARATOR + orderId;
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.del(key);
    	}
    }
}
