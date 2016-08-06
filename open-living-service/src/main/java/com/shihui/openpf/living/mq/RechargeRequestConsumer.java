package com.shihui.openpf.living.mq;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.shihui.commons.mq.annotation.ConsumerConfig;
import com.shihui.commons.mq.api.Consumer;
import com.shihui.commons.mq.api.Topic;
import com.shihui.openpf.living.io3rd.GuangdaDao;
import com.shihui.openpf.living.io3rd.ReqPay;

import me.weimi.api.commons.util.ApiLogger;;

@Component("rechargeRequestConsumer")
@ConsumerConfig(consumerName = "livingRechargeRequestConsumer", topic = Topic.Open_Living_Guangda, tag=LivingMqProducer.TAG_RECHARGE_REQUEST)
public class RechargeRequestConsumer implements Consumer {

	@Resource
	private GuangdaDao guangdaDao;

	public RechargeRequestConsumer(){
	}

	@Override
	public boolean doit(String topic, String tags, String key, String msg) {
		ApiLogger.info("RechargeRequestConsumer : topic[" + topic + "] tags[" + tags + "] key[" + key + "] msg=" + msg);
		try {			
			ReqPay reqPay;
			if ((reqPay = JSONObject.parseObject(msg,ReqPay.class)) == null)
				return true;

			return guangdaDao.sendRequest(reqPay);
			
		} catch (Exception e) {
			ApiLogger.warn("RechargeRequestConsumer : Exception : " + e.getMessage());
		}

		return false;
	}

}
