package com.shihui.openpf.living.mq;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.shihui.commons.mq.annotation.ConsumerConfig;
import com.shihui.commons.mq.api.Consumer;
import com.shihui.commons.mq.api.Topic;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.io3rd.GuangdaDao;
import com.shihui.openpf.living.io3rd.ReqKey;
import com.shihui.openpf.living.io3rd.ReqPay;

//import me.weimi.api.commons.util.ApiLogger;
import com.shihui.commons.ApiLogger;

@Component("rechargeRequestConsumer")
@ConsumerConfig(consumerName = "livingRechargeRequestConsumer", topic = Topic.Open_Living_Guangda, tag=LivingMqProducer.TAG_RECHARGE_REQUEST)
public class RechargeRequestConsumer implements Consumer {

	@Resource
	private GuangdaDao guangdaDao;
	@Resource
	private CacheDao cacheDao;

	public RechargeRequestConsumer(){
	}

	@Override
	public boolean doit(String topic, String tags, String key, String msg) {
		ApiLogger.info("RechargeRequestConsumer : topic[" + topic + "] tags[" + tags + "] key[" + key + "] msg=" + msg);
		
		try {	
			if( key.compareTo(LivingMqProducer.KEY_KEY) == 0) {
				ReqKey reqKey;
				if ((reqKey = JSONObject.parseObject(msg,ReqKey.class)) == null)
					return true;
				
				boolean b= guangdaDao.sendRequest(reqKey);
				ApiLogger.info("### guangdaDao.sendRequest(reqKey) = " + b);
				return b;
				
			} else {
				ReqPay reqPay;
				if ((reqPay = JSONObject.parseObject(msg,ReqPay.class)) == null)
					return true;
	
				return guangdaDao.sendRequest(reqPay);
			}
			
		} catch (Exception e) {
			ApiLogger.info("RechargeRequestConsumer : Exception : " + e.getMessage());
		}

		return false;
	}

}
