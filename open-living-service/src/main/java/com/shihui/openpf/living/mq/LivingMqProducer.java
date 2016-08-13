package com.shihui.openpf.living.mq;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.shihui.commons.mq.Producer;
import com.shihui.commons.mq.api.Topic;

//import me.weimi.api.commons.util.ApiLogger;
import com.shihui.commons.ApiLogger;

@Component
public class LivingMqProducer {

	public static final String KEY_KEY = "key";
	public static final String TAG_QUERY_REQUEST ="query_request";
	public static final String TAG_RECHARGE_REQUEST = "recharge_request";
	public static final String TAG_RESPONSE = "recharge";
	
	@Resource(name = "livingRocketProducer")
	Producer producer;
	
	public boolean sendQueryRequest(String key, String value) {
		ApiLogger.info("LivingMqProducer: sendQueryRequest() : key: [" + key + "] value: " + value);
		return producer.send(Topic.Open_Living_Guangda, key, TAG_QUERY_REQUEST,  value);
	}
	public boolean sendRechargeRequest(String key, String value) {
		ApiLogger.info("LivingMqProducer: sendRechargeRequest() : key: [" + key + "] value: " + value);
		return producer.send(Topic.Open_Living_Guangda, key, TAG_RECHARGE_REQUEST,  value);
	}
	public boolean sendKeyRequest(String value) {
		ApiLogger.info("LivingMqProducer: sendKeyRequest() : value: " + value);
		return producer.send(Topic.Open_Living_Guangda, KEY_KEY, TAG_RECHARGE_REQUEST,  value);
	}
	
	public boolean sendResponse(String key, String value) {
		ApiLogger.info("LivingMqProducer: sendResponse() : key: [" + key + "] value: " + value);
		return producer.send(Topic.Open_Living_Guangda, key, TAG_RESPONSE,  value);
	}

}
