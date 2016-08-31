package com.shihui.commons;


//import me.weimi.api.commons.context.RequestContext;
import com.shihui.api.core.context.RequestContext;
//import me.weimi.api.commons.logger.CentralLogger;
import com.shihui.common.logger.CentralLogger;
import com.shihui.commons.ApiLogger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
//    private static Logger log = LoggerFactory.getLogger(OperationLogger.class);

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

//        if (rc == null) {
//            log.warn("RequestContext not got");
//            return;
//        }
        OperationLog olog = new OperationLog();
        ApiLogger.info(" -  1 -");
        olog.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        ApiLogger.info(" -  2 -");
        olog.setChannel("kuyue");
        ApiLogger.info(" -  3 - : " + (rc.getClient().getClientVersion() == null));
        olog.setClientVersion( rc.getClient().getClientVersion().toString());
        ApiLogger.info(" -  4 - ");
        olog.setDeviceId(rc.getClient().getDeviceId());
        ApiLogger.info(" -  5 -");
        olog.setCityId((String)expand.get("cityId"));
        ApiLogger.info(" -  6 -");
        olog.setGid((String)expand.get("gid"));
        ApiLogger.info(" -  7 -");
        olog.setServiceId((String)expand.get("serviceId"));
        ApiLogger.info(" -  8 -");
        olog.setUid(String.valueOf(rc.getUid()));
        ApiLogger.info(" -  9 -");
        olog.setIp(rc.getIp());
        ApiLogger.info(" -  10 -");
        if(expand.get("businessId")!=null) {
            Map<String, String> map = new HashMap<>();
            ApiLogger.info(" -  11 -");
            map.put("businessId",(String)expand.get("businessId"));
            olog.setExpand(map);
            ApiLogger.info(" -  12 -");
            
            olog.setAction(action + "?businessId=" + expand.get("businessId"));
            ApiLogger.info(" -  13 -");

        } else {
            ApiLogger.info(" -  14 -");

            olog.setAction(action);
            ApiLogger.info(" -  15 -");

        }
//        log.info("TFS LOG:" + JSON.toJSONString(olog));
      ApiLogger.info("***CENTRAL LOG: action:[" + action + "] data :[" + JSON.toJSONString(olog)+"]");
        centralLogger.log(action, (JSONObject)JSON.toJSON(olog));   
        ApiLogger.info(" -  16 -");

    }

}
