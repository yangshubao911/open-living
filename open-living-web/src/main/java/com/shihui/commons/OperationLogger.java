package com.shihui.commons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shihui.api.core.context.RequestContext;
import com.shihui.common.logger.CentralLogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-03-25 15:54
 */
public class OperationLogger {
	private static CentralLogger centralLogger = CentralLogger.getLogger();

    public static void log(String action, RequestContext rc, Map<String, Object> expand) {
        OperationLog olog = new OperationLog();
        olog.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        olog.setChannel("kuyue");
//        olog.setClientVersion(rc.getClient().getClientVersion().getMinor());
        olog.setClientVersion(rc.getClient().getVersion());
        olog.setDeviceId(rc.getClient().getDeviceId());
        olog.setCityId((String)expand.get("cityId"));
        olog.setGid((String)expand.get("gid"));
        olog.setServiceId((String)expand.get("serviceId"));
        olog.setUid(String.valueOf(rc.getUid()));
        olog.setIp(rc.getIp());
        if(expand.get("businessId")!=null) {
            Map<String, String> map = new HashMap<>();
            map.put("businessId",(String)expand.get("businessId"));
            olog.setExpand(map);
            
            olog.setAction(action + "?businessId=" + expand.get("businessId"));
        } else {
            olog.setAction(action);
        }
//        ApiLogger.info("***CENTRAL LOG: action:[" + action + "] data :[" + JSON.toJSONString(olog)+"]");
        centralLogger.log(action, (JSONObject)JSON.toJSON(olog));   
    }

}
