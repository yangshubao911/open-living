/**
 * 
 */
package com.shihui.openpf.living.cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import org.springframework.beans.factory.annotation.Value;
import com.alibaba.fastjson.JSON;
import com.shihui.openpf.common.tools.Constants;
import com.shihui.openpf.common.tools.StringUtil;
import com.shihui.openpf.living.entity.Goods;
import com.shihui.openpf.living.entity.support.OrderBillVo;
import com.shihui.openpf.living.entity.support.QueryOrderBillVo;
import com.shihui.openpf.living.io3rd.PacketNotify;
import com.shihui.openpf.living.util.LivingUtil;
import com.shihui.openpf.common.model.Campaign;
import com.shihui.openpf.common.model.Service;
import com.shihui.openpf.common.model.Group;
import com.shihui.openpf.common.model.Merchant;
import com.shihui.openpf.living.entity.MerchantGoods;
import com.shihui.openpf.living.io3rd.ResKey;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;


@Repository
public class CacheDao {

	public static final String CACHE_PREFIX = Constants.REDIS_KEY_PREFIX + Constants.REDIS_KEY_SEPARATOR + "living" + Constants.REDIS_KEY_SEPARATOR;

	//
    @Resource
    private ShardedJedisPool jedisPool;

    //
    @Value("${expire_sys}")
    private int EXPIRE_SYS = 3*60;//4*60*60;
	//
	private static final String BANNERADS = CACHE_PREFIX + "bannerAds";
	//
	private static final String TASK_0 = CACHE_PREFIX + "task_0";
	private static final int EXPIRE_TASK_0 = 60*60;
	public boolean lockTask() {
		return lock(TASK_0,EXPIRE_TASK_0);
	}
	//
//	private static final String MERCHANT_GOODS = CACHE_PREFIX + "merchant_goods";
//	public void setMerchantGoods(int serviceId, MerchantGoods merchant) {
//		hset(MERCHANT_GOODS, String.valueOf(serviceId), merchant, EXPIRE_SYS);
//	}
//	public MerchantGoods getMerchantGoods(int serviceId) {
//		return (MerchantGoods)hgetObject(MERCHANT_GOODS, String.valueOf(serviceId), MerchantGoods.class);
//	}
	//
	private static final String MERCHANT = CACHE_PREFIX + "merchant";
	public void setMerchant(int merchantId, Merchant merchant) {
		hset(MERCHANT, String.valueOf(merchantId), merchant, EXPIRE_SYS);
	}
	public Merchant getMerchant(int merchantId) {
		return (Merchant)hgetObject(MERCHANT, String.valueOf(merchantId), Merchant.class);
	}
	//
	private static final String GROUP = CACHE_PREFIX + "group";
	public void setGroup(long groupId, Group group) {
		hset(GROUP, String.valueOf(groupId), group, EXPIRE_SYS);
	}
	public Group getGroup(long groupId) {
		return (Group)hgetObject(GROUP, String.valueOf(groupId), Group.class);
	}
	//
	private static final String CITY = CACHE_PREFIX + "city";
	public void setCity(int categoryId, String value) {
		hset(CITY, String.valueOf(categoryId), value, EXPIRE_SYS);
	}
	public String getCity(int categoryId) {
		return hgetString(CITY, String.valueOf(categoryId));
	}
	//
	private static final String COMPANY_PREFIX = CACHE_PREFIX + "company" + Constants.REDIS_KEY_SEPARATOR;
	public void setCompany(int serviceId, int cityId, String value) {
		hset(COMPANY_PREFIX + serviceId, String.valueOf(cityId), value, EXPIRE_SYS);
	}
	public String getCompany(Integer serviceId, Integer cityId) {
		return hgetString(COMPANY_PREFIX + serviceId, String.valueOf(cityId));
	}
	//
	private static final String SERVICE_PREFIX = CACHE_PREFIX + "service";
	public void setService(int serviceId, Service value) {
		set(SERVICE_PREFIX + serviceId, value, EXPIRE_SYS);
	}
	public Service getService(int serviceId) {
		return (Service)getObject(SERVICE_PREFIX + serviceId, Service.class);
	}
	//
	private static final String CATEGORY = CACHE_PREFIX + "category";
	public void setCategory(String value) {
		set(CATEGORY,  value, EXPIRE_SYS);
	}
	public String getCategory() {
		return getString(CATEGORY);
	}
	
	//
	private static final String GOODS_PREFIX = CACHE_PREFIX + "goods";
	public void setGoods(int category, long goodsId, Goods goods) {
		hset(GOODS_PREFIX + category, String.valueOf(goodsId), goods, EXPIRE_SYS);
	}
	public Goods getGoods(int category, long goodsId) {
		return (Goods)hgetObject(GOODS_PREFIX + category, String.valueOf(goodsId),Goods.class);
	}
	//
	private static final String CAMPAIGN_LIST = CACHE_PREFIX + "campaignList";
	public void setCampaignList(int serviceId, List<Campaign> campaignList) {
		hset(CAMPAIGN_LIST, String.valueOf(serviceId), campaignList, EXPIRE_SYS);
	}
	public List<Campaign> getCampaignList(int serviceId) {
		return (List<Campaign>)hgetObject(CAMPAIGN_LIST, String.valueOf(serviceId), Campaign.class);
	}
	//
	private static final String USER_HOME_PREFIX = CACHE_PREFIX + "user_home" + Constants.REDIS_KEY_SEPARATOR;
	private static final String FIELD_HOME = "home";
	@Value("${expire_user}")
	private int EXPIRE_USER = 60*60;
	public void setUserHome(long userId, String value) {
		hset(USER_HOME_PREFIX + userId, FIELD_HOME, value, EXPIRE_USER);
	}
	public String getUserHome(long userId) {
		return hgetString(USER_HOME_PREFIX + userId, FIELD_HOME);
	}
	//
	private static final String KEY_RESKEY = CACHE_PREFIX + "key_ResKey";
	private static final String KEY_GUANGDA = CACHE_PREFIX + "key_guangda";
	private static final String KEY_DATE_GUANGDA = CACHE_PREFIX + "key_date_guangda";
	private static final int EXPIRE_KEY = 90;
	public boolean lockKey() {
		return lock(KEY_GUANGDA, EXPIRE_KEY);
	}
	public void setKeyDate() {
		set(KEY_DATE_GUANGDA,String.valueOf(new Date().getTime()));
	}
	public boolean checkKeyDateExpired() {
		String value = getString(KEY_DATE_GUANGDA);
		if( value != null) {
			Calendar keyDate = Calendar.getInstance();
			keyDate.setTimeInMillis(Long.parseLong(value));
			Calendar nowDate = Calendar.getInstance();
			nowDate.setTime(new Date());
			return !( keyDate.get(Calendar.YEAR) == nowDate.get(Calendar.YEAR)
					&& keyDate.get(Calendar.MONTH) == nowDate.get(Calendar.MONTH)
					&& keyDate.get(Calendar.DAY_OF_MONTH) == nowDate.get(Calendar.DAY_OF_MONTH)
					);
				
		}
		return true;
	}
	public void setResKey(ResKey resKey) {
		set(KEY_RESKEY,resKey);
	}
	public ResKey getResKey() {
		return (ResKey)getObject(KEY_RESKEY, ResKey.class);
	}
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
	private static final int EXPIRE_ORDERBILLVO = 60*60*24;
	
    public void setOrderBillVo(String tempId, OrderBillVo vo){
    	set(ORDERBILLVO_PREFIX + tempId, vo, EXPIRE_ORDERBILLVO);
    }
    public void setOrderBillVo(long orderId, OrderBillVo vo){
    	set(ORDERBILLVO_PREFIX + LivingUtil.getRechargeTrmSeqNum(orderId), vo, EXPIRE_ORDERBILLVO);
    }
    
    public OrderBillVo getOrderBillVo(String tempId){
    	return (OrderBillVo)getObject(ORDERBILLVO_PREFIX + tempId, OrderBillVo.class);
    }
    public OrderBillVo getOrderBillVo(long orderId){
    	return (OrderBillVo)getObject(ORDERBILLVO_PREFIX + LivingUtil.getRechargeTrmSeqNum(orderId), OrderBillVo.class);
    }

    public void delOrderBillVo(String tempId){
    	del(ORDERBILLVO_PREFIX + tempId);
    }
    public void delOrderBillVo(long orderId){
    	del(ORDERBILLVO_PREFIX + LivingUtil.getRechargeTrmSeqNum(orderId));
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
    private void set(String key, String value) {
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.set(key, value);
    	}   	
    }
    
    private void set(String key, String value, int expire) {
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.set(key, value);
    		jedis.expire(key, expire);
    	}   	
    }

    private void set(String key, Object object) {
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.set(key, JSON.toJSONString(object));
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
    
    private void hset(String key, String field, String value, int expire){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.hset(key, field, value);
    		jedis.expire(key, expire);
    	}
    }
    private void hset(String key, String field, String value){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.hset(key, field, value);
    	}
    }    
    private void hset(String key, String field, Object object, int expire){
    	try(ShardedJedis jedis = jedisPool.getResource()){
    		jedis.hset(key, field, JSON.toJSONString(object));
    		jedis.expire(key, expire);
    	}
    }    
    private void hset(String key, String field, Object object){
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
