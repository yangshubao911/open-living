package com.shihui.commons;


import me.weimi.api.commons.context.RequestContext;
import me.weimi.api.commons.logger.CentralLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

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
    private static Logger log = LoggerFactory.getLogger(OperationLogger.class);

    public static void log(String action, RequestContext rc, Map<String, Object> expand) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//        data.put("action", action);
//        data.put("uid", rc.getUid());
//        data.put("ip", rc.getIp());
////        data.put("client_version", rc.getClient().getVersion());
//        data.put("client_version", rc.getClient().getClientVersion().toString());
//        data.put("channel", "kuyue");
//        data.put("deviceid", rc.getClient().getDeviceId());
//        data.putAll(expand);
//
//        ApiLogger.info("***CENTRAL LOG: action:[" + action + "] data :[" + data.toString()+"]");
//        centralLogger.log(action, data);

        if (rc == null) {
            log.warn("RequestContext not got");
            return;
        }
        OperationLog olog = new OperationLog();
        ApiLogger.info(" * 1 - ");
        olog.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        ApiLogger.info(" * 2 - ");
        ApiLogger.info(" * 3 - ");
        olog.setChannel(rc.getClientVersion().channel);
        olog.setClientVersion(rc.getClientVersion().clientVersion + "");
        olog.setDeviceId((String)expand.get("ndeviceid"));
        olog.setCityId((String)expand.get("cityId"));
        olog.setGid((String)expand.get("gid"));
        olog.setServiceId((String)expand.get("serviceId"));
        olog.setUid(rc.getCurrentUid() + "");
        olog.setIp(rc.getIp());
        if(expand.get("businessId")!=null) {
            Map<String, String> map = new HashMap<>();
            map.put("businessId",(String)expand.get("businessId"));
            olog.setExpand(map);
            
            olog.setAction(action + "?businessId=" + expand.get("businessId"));
        } else {
            olog.setAction(action);
        }
        ApiLogger.info(" * 4 - " + JSON.toJSONString(olog)/*olog.toJSONString()*/);
       // log.info("TFS LOG:" + olog.toJSONString());
        ApiLogger.info(" * 5 - ");
        centralLogger.log(action, olog.toJSONObject());    
        ApiLogger.info(" * 6 - ");
    }

}
