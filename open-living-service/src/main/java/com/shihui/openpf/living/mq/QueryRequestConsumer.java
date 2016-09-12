package com.shihui.openpf.living.mq;

import com.alibaba.fastjson.JSONObject;
import com.shihui.commons.ApiLogger;
import com.shihui.commons.mq.annotation.ConsumerConfig;
import com.shihui.commons.mq.api.Consumer;
import com.shihui.commons.mq.api.Topic;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.io3rd.GuangdaDao;
import com.shihui.openpf.living.io3rd.ReqQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//import me.weimi.api.commons.util.ApiLogger;

@Component("queryRequestConsumer")
@ConsumerConfig(consumerName = "livingQueryRequestConsumer", topic = Topic.Open_Living_Guangda, tag=LivingMqProducer.TAG_QUERY_REQUEST)
public class QueryRequestConsumer implements Consumer {

	@Resource
	private GuangdaDao guangdaDao;
	@Resource
	private CacheDao cacheDao;

	public QueryRequestConsumer(){
	}

	@Override
	public boolean doit(String topic, String tags, String key, String msg) {
		ApiLogger.info("QueryRequestConsumer : topic[" + topic + "] tags[" + tags + "] key[" + key + "] msg=" + msg);

		try {			
			ReqQuery reqQuery;
			if ((reqQuery = JSONObject.parseObject(msg,ReqQuery.class)) == null)
				return true;

			return guangdaDao.sendRequest(reqQuery);
			
		} catch (Exception e) {
			ApiLogger.info("QueryRequestConsumer : Exception : " + e.getMessage());
		}

		return false;
	}

}
