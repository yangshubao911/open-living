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
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.shihui.openpf.living.io3rd.PacketNotify;

/**
 * @author zhouqisheng
 *
 */
@Repository
public class CacheDao {
	private String ORDER_PREFIX = Constants.REDIS_KEY_PREFIX + Constants.REDIS_KEY_SEPARATOR + "living" + Constants.REDIS_KEY_SEPARATOR;
	
	private String LIVING_TEMPID = ORDER_PREFIX + "tempId" + Constants.REDIS_KEY_SEPARATOR;	//uuid
	private String LIVING_SERIALNO = ORDER_PREFIX + "serialNo" + Constants.REDIS_KEY_SEPARATOR;		//yymmdd+1234567890
	private String LIVING_GEN_SERIALNO_PREFIX = ORDER_PREFIX + "serialNo_gen_prefix";
	private String LIVING_GEN_SERIALNO = ORDER_PREFIX + "serialNo_gen";	
	private String LIVING_NOTIFY = ORDER_PREFIX + "notify";	
	
	private String LOCK_SERIALNO = ORDER_PREFIX + "lock" +  Constants.REDIS_KEY_SEPARATOR + "serialNo" +  Constants.REDIS_KEY_SEPARATOR;
	private String LOCK_ORDERID = ORDER_PREFIX + "lock" +  Constants.REDIS_KEY_SEPARATOR + "orderId" +  Constants.REDIS_KEY_SEPARATOR;
		
	private int EXPIRE_SERIALNO = 5*60;
	private int EXPIRE_ORDER = 5*60;
	private int EXPIRE_TEMPID = 60*60;
	private int EXPIRE_NOTIFY = 12*60*60;
	
	private String FIELD_VO = "vo";
	private String FIELD_RESPONSE = "response";
	
    @Resource
    private ShardedJedisPool jedisPool;
    
    /**
     * 锁定订单
     * @param orderId
     * @return
     */
    public boolean lockOrder(long orderId){
    	String key = LOCK_ORDERID + orderId;
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		Long result = jedis.incr(key);
    		if(result == 1){
    			jedis.expire(key, EXPIRE_ORDER);
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
    		jedis.del(LOCK_ORDERID + orderId);
    	}
    }
    
    /**
     * 锁定订单
     * @param orderId
     * @return
     */

    public static String getTrmSeqNum() {
    	return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    public String getSerialNo() {
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		Long result = jedis.incr(LIVING_GEN_SERIALNO);
    		String prefix = jedis.get("LIVING_GEN_SERIALNO_PREFIX");
    		if(prefix == null) {
    			prefix = new SimpleDateFormat("yyyyMMdd").format(new Date()).toString();
    			jedis.set("LIVING_GEN_SERIALNO_PREFIX",prefix);
    		}
    		return  prefix + String.format("%08d", result);
    	}
    }
    
    public void resetSerialNo() {
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.del(LIVING_GEN_SERIALNO);
    		String prefix = new SimpleDateFormat("yyyyMMdd").format(new Date()).toString();
			jedis.set("LIVING_GEN_SERIALNO_PREFIX",prefix);
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

    public void newVoByTempId(String tempId, OrderBillVo vo){//tempId = userid+#+CompanyNo+#+userNo
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		String key = LIVING_TEMPID + tempId;
    		jedis.hset(key, FIELD_VO,JSON.toJSONString(vo));
    		jedis.expire(key, EXPIRE_TEMPID);
    	}
    }

    public void setVoByTempId(String tempId, OrderBillVo vo){//tempId = userid+#+CompanyNo+#+userNo
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		String key = LIVING_TEMPID + tempId;
    		jedis.hset(key, FIELD_VO,JSON.toJSONString(vo));
    		jedis.expire(key, EXPIRE_TEMPID);
    	}
    }
    
    public OrderBillVo getVoByTempId(String tempId){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		String voString = jedis.hget(LIVING_TEMPID + tempId, FIELD_VO);
			if(StringUtil.isEmpty(voString))return null;
			return JSON.parseObject(voString,OrderBillVo.class);
    	}
    }

    public void markVoByTempId(String tempId){//tempId = userid+#+CompanyNo+#+userNo
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.hset(LIVING_TEMPID + tempId, FIELD_RESPONSE, "1");
    	}
    }
    public boolean checkMarkedVoByTempId(String tempId){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		String voString = jedis.hget(LIVING_TEMPID + tempId, FIELD_RESPONSE);
			return (voString == null || StringUtil.isEmpty(voString));
    	}
    }
    public void delVoByTempId(String tempId){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.del(LIVING_TEMPID + tempId);
    	}
    }

    public void setVoBySerialNo(String serialNo, OrderBillVo vo){//tempId = userid+#+CompanyNo+#+userNo
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		String key = LIVING_SERIALNO + serialNo;
    		jedis.set(key, JSON.toJSONString(vo));
    		jedis.expire(key, EXPIRE_SERIALNO);
    	}
    }
    
    public OrderBillVo getVoBySerialNo(String serialNo){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		String voString = jedis.get(LIVING_SERIALNO + serialNo);
			if(StringUtil.isEmpty(voString))return null;
			return JSON.parseObject(voString,OrderBillVo.class);
    	}
    }
 
    public void delVoBySerialNo(String serialNo){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.del(LIVING_SERIALNO + serialNo);
    	}
    }

    public void setNotify(PacketNotify packetNotify){//tempId = userid+#+CompanyNo+#+userNo
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.set(LIVING_NOTIFY, JSON.toJSONString(packetNotify));
    		jedis.expire(LIVING_NOTIFY, EXPIRE_NOTIFY);
    	}
    }
    
    public PacketNotify getNotify(){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		String voString = jedis.get(LIVING_NOTIFY);
			if(StringUtil.isEmpty(voString))return null;
			return JSON.parseObject(voString,PacketNotify.class);
    	}
    }
}
