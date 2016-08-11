package com.shihui.commons;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.shihui.api.core.context.RequestContext;
import com.shihui.common.logger.CentralLogger;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-03-25 15:54
 */
public class OperationLogger {
    private static CentralLogger centralLogger = CentralLogger.getLogger();

    public static void log(String action, RequestContext rc, Map<String, Object> expand) {
        Map<String, Object> data = new HashMap<>();
        data.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        data.put("action", action);
        data.put("uid", rc.getUid());
        data.put("ip", rc.getIp());
//        data.put("client_version", rc.getClient().getVersion());
        data.put("client_version", rc.getClient().getClientVersion().toString());
        data.put("channel", "kuyue");
        data.put("deviceid", rc.getClient().getDeviceId());
        data.putAll(expand);

        ApiLogger.info("central log: " + data.toString());
        centralLogger.log(action, data);
    }

}
