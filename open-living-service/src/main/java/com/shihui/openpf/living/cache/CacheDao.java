/**
 * 
 */
package com.shihui.openpf.living.cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.shihui.openpf.common.tools.Constants;
import com.shihui.openpf.common.tools.StringUtil;
import com.shihui.openpf.living.entity.support.OrderBillVo;
import com.shihui.openpf.living.entity.support.QueryOrderBillVo;
import com.shihui.openpf.living.io3rd.PacketNotify;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;


@Repository
public class CacheDao {

	public static final String CACHE_PREFIX = Constants.REDIS_KEY_PREFIX + Constants.REDIS_KEY_SEPARATOR + "living" + Constants.REDIS_KEY_SEPARATOR;

	//
    @Resource
    private ShardedJedisPool jedisPool;

	//
	private static final String BANNERADS = CACHE_PREFIX + "bannerAds";
	//
	private static final String CITY_PREFIX = CACHE_PREFIX + "city";
	//
	private static final String COMPANY_PREFIX = CACHE_PREFIX + "company";
	//
	private static final String SERVICE_LIST = CACHE_PREFIX + "service";
	//
	private static final String CATEGORY_PREFIX = CACHE_PREFIX + "category";
	//
	private static final String GOODS_PREFIX = CACHE_PREFIX + "goods";
	//
	private static final String CAMPAIGN_LIST = CACHE_PREFIX + "campaign";
	//
	private static final String FIELD_TOPN = "topN";
	//
	private static final String LIVING_GEN_SERIALNO_PREFIX = CACHE_PREFIX + "serialNo_gen_prefix";
	private static final String LIVING_GEN_SERIALNO = CACHE_PREFIX + "serialNo_gen";	
	private static final String LOCK_SERIALNO = CACHE_PREFIX + "lock" +  Constants.REDIS_KEY_SEPARATOR + "serialNo_gen" +  Constants.REDIS_KEY_SEPARATOR;
	private static final int EXPIRE_SERIALNO = 5*60;
	
    public String getSerialNo() {
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		Long result = jedis.incr(LIVING_GEN_SERIALNO);
    		String prefix = jedis.get("LIVING_GEN_SERIALNO_PREFIX");
    		if(prefix == null) 
    			resetSerialNo();
    		return  prefix + String.format("%08d", result);
    	}
    }
    
    public void resetSerialNo() {
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.del(LIVING_GEN_SERIALNO);
    		String prefix = new SimpleDateFormat("yyyyMMdd").format(new Date()).toString();
			jedis.set(LIVING_GEN_SERIALNO_PREFIX,prefix);
    	}
    }
    
    public boolean lockSerialNo(String serialNo){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		Long result = jedis.incr(LOCK_SERIALNO + serialNo);
    		if(result == 1){
    			jedis.expire(LOCK_SERIALNO, EXPIRE_SERIALNO);
    			return true;
    		}
    	}
    	return false;
    }	
	//
	private static final String QUERYORDERBILLVO_PREFIX = CACHE_PREFIX + "obvo_q" + Constants.REDIS_KEY_SEPARATOR;
	private static final int EXPIRE_QUERYORDERBILLVO = 60*60;
	
    public void setQueryOrderBillVo(String tempId, QueryOrderBillVo vo){
    	set(QUERYORDERBILLVO_PREFIX + tempId, vo, EXPIRE_QUERYORDERBILLVO);
    }
    
    public QueryOrderBillVo getQueryOrderBillVo(String tempId){
    	return (QueryOrderBillVo)getObject(QUERYORDERBILLVO_PREFIX + tempId, QueryOrderBillVo.class);
    }

    public void delQueryOrderBillVo(String tempId){
    	del(QUERYORDERBILLVO_PREFIX + tempId);
    }
    
	//
	private static final String ORDERBILLVO_PREFIX = CACHE_PREFIX + "obvo" + Constants.REDIS_KEY_SEPARATOR;
	private static final int EXPIRE_ORDERBILLVO = 60*60*24*3;
	
    public void setOrderBillVo(long orderId, OrderBillVo vo){
    	set(ORDERBILLVO_PREFIX + orderId, vo, EXPIRE_ORDERBILLVO);
    }
    
    public OrderBillVo getOrderBillVo(long orderId){
    	return (OrderBillVo)getObject(ORDERBILLVO_PREFIX + orderId, OrderBillVo.class);
    }

    public void delOrderBillVo(long orderId){
    	del(ORDERBILLVO_PREFIX + orderId);
    }
    
    //
	private static final String LOCK_ORDERID = CACHE_PREFIX + "lock" +  Constants.REDIS_KEY_SEPARATOR + "orderId" +  Constants.REDIS_KEY_SEPARATOR;
	private static final int EXPIRE_ORDER = 5*60;
    
    public boolean lockOrder(long orderId){
    	return lock(LOCK_ORDERID + orderId, EXPIRE_ORDER);
    }

    public void unlockOrder(long orderId){
    	unlock(LOCK_ORDERID + orderId);
    }

    //
	private String PACKET_NOTIFY = CACHE_PREFIX + "notify";	
	private int EXPIRE_NOTIFY = 12*60*60;

    public void setPacketNotify(PacketNotify packetNotify){
    	set(PACKET_NOTIFY, packetNotify, EXPIRE_NOTIFY);
    }
    
    public PacketNotify getNotify(){
    	return (PacketNotify) getObject(PACKET_NOTIFY, PacketNotify.class);
    }
    
    //
    //
    //
    
    public String getTrmSeqNum() {
    	return java.util.UUID.randomUUID().toString().replace("-", "");
    }
    
    public String getUUID() {
    	return java.util.UUID.randomUUID().toString();
    }
    
    //
    //
    //
    
    private void set(String key, String value, int expire) {
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.set(key, value);
    		jedis.expire(key, expire);
    	}   	
    }

    private void set(String key, Object object, int expire) {
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.set(key, JSON.toJSONString(object));
    		jedis.expire(key, expire);
    	}   	
    }

    private String getString(String key) {
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		return jedis.get(key);
    	}    	
    }
    
    private Object getObject(String key, Class objectClass) {
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		String value = jedis.get(key);
			if(StringUtil.isEmpty(value))return null;
			return JSON.parseObject(value,objectClass);
    	}
    }
    
    private void del(String key){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.del(key);
    	}
    }

    //
    //
    //
    
    private void set(String key, String field, String value){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.hset(key, field, value);
    	}
    }
    
    private void set(String key, String field, Object object){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.hset(key, field, JSON.toJSONString(object));
    	}
    }    

    
    private String hgetString(String key, String field){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		return jedis.hget(key, field);
    	}
    } 
    
    private Object hgetObject(String key, String field, Class objectClass){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		String value = jedis.hget(key, field);
			if(StringUtil.isEmpty(value))return null;
			return JSON.parseObject(value,objectClass);
    	}
    }
   
    private List hgetAll(String key, Class objectClass){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		Map<String, String> result = jedis.hgetAll(key);
    		List list = new ArrayList<>(result.size());
    		for(String s : result.values())
    			list.add(JSON.parseObject(s, objectClass));
    		return list;
    	} catch (Exception e) {
    		throw e;
    	}
    }

    private void hdel(String key, String field){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.hdel(key, field);
    	}
    }
    
    //
    //
    //
    
    private boolean lock(String key, int expire){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		Long result = jedis.incr(key);
    		if(result == 1){
    			jedis.expire(key, expire);
    			return true;
    		}
    	}
    	return false;
    }

    private void unlock(String key){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.del(key);
    	}
    }
}
