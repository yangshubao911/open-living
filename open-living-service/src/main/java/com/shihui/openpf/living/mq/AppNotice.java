package com.shihui.openpf.living.mq;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
//import me.weimi.api.commons.util.ApiLogger;
import com.shihui.commons.ApiLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.shihui.commons.ApacheHttpClient;
import com.shihui.commons.ApiHttpClient;

@Component("appNotice")
public class AppNotice {

	
	@Value("${app_push_url}")
	private String appPushUrl;

	private ApiHttpClient httpClient;
	
	@PostConstruct
	public void init(){
		this.httpClient = new ApacheHttpClient();
	}

	/**
	 * push app通知
	 * @param msg
	 * @param userId
	 * @param merchantCode
	 */
	public void pushMsg(String msg, long userId, long merchantCode) {
		try {
			Map<String, Object> param = new HashMap<>();
			
			param.put("touid", userId);
			param.put("dataid", "10001");
			param.put("data", msg);
			param.put("fromuid", merchantCode);

			String result = httpClient.buildPost(appPushUrl).withHeader("X-Matrix-UID", "1000").withParam(param).execute();
			
//			ApiLogger.info("AppNotice : pushMsg() : uid:["+userId+"] result:["+result+"]");
		} catch (Exception e) {
			ApiLogger.error("AppNotice : pushMsg() : push app消息异常", e);
		}
	}
	public void pushQueryResult(long userId, Object queryResult) {
		try {
			Map<String, Object> param = new HashMap<>();
			
			param.put("touid", userId);
			param.put("dataid", "60002");
			param.put("fromuid", "1010");
			
			JSONObject jo = new JSONObject();
			jo.put("push_desc", "生活缴费查询通知");
			jo.put("result", queryResult);
			param.put("data", jo.toJSONString());

			String result = httpClient.buildPost(appPushUrl).withHeader("X-Matrix-UID", "1000").withParam(param).execute();
			
//			ApiLogger.info("AppNotice : pushQueryResult() : uid:["+userId+"] result: ["+result+"]");
		} catch (Exception e) {
			ApiLogger.error("AppNotice : pushQueryResult() : push app消息异常", e);
		}
	}
}
