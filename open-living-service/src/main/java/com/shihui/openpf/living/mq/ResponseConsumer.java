package com.shihui.openpf.living.mq;

import com.shihui.commons.ApiLogger;
import com.shihui.commons.mq.annotation.ConsumerConfig;
import com.shihui.commons.mq.api.Consumer;
import com.shihui.commons.mq.api.Topic;
import com.shihui.openpf.living.task.BillExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//import me.weimi.api.commons.util.ApiLogger;

@Component("responseConsumer")
@ConsumerConfig(consumerName = "livingResponseConsumer", topic = Topic.Open_Living_Guangda, tag=LivingMqProducer.TAG_RESPONSE)
public class ResponseConsumer implements Consumer {

	@Resource
	BillExecutor billExecutor;

	public ResponseConsumer(){
	}

	@Override
	public boolean doit(String topic, String tags, String key, String msg) {
		ApiLogger.info("ResponseConsumer : topic[" + topic + "] tags[" + tags + "] key[" + key + "] msg=" + msg);
		
		try {			
			if (msg == null)
				return true;

			return billExecutor.newExecuteAnalysePacketTask(msg);
			
		} catch (Exception e) {
			ApiLogger.info("ResponseConsumer : Exception : " + e.getMessage());
		}

		return false;
	}

}
