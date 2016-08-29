package com.shihui.commons;

import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.shihui.common.utils.DateFormatUtils;
import org.apache.commons.collections.MapUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-03-25 15:43
 */
public class OperationLog /*implements JSONAware*/ {

    private String time;
    private String uid;
    @JSONField(name = "deviceid")
    private String deviceId;
    private String channel;
    @JSONField(name = "client_version")
    private String clientVersion;
    @JSONField(name = "city_id")
    private String cityId;
    private String gid;
    @JSONField(name = "service_id")
    private String serviceId;
    private String action;
    private String ip;
    private Map<String, String> expand;

    public String getTime() {
        return DateFormatUtils.format(new Date());
    }

    public void addExpand(String key, String value) {
        if (expand == null) {
            expand = new HashMap<>();
        }
        expand.put(key, value);
    }

    public void addExpand(Map<String, String> maps) {
        if (MapUtils.isEmpty(maps)) {
            return;
        }
        if (expand == null) {
            expand = new HashMap<>();
        }
        expand.putAll(maps);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Map<String, String> getExpand() {
        return expand;
    }

    public void setExpand(Map<String, String> expand) {
        this.expand = expand;
    }
//
//    @Override
//    public String toJSONString() {
//        return JSONObject.toJSONString(this);
//    }
//
//    public JSONObject toJSONObject() {
//        return (JSONObject) JSONObject.toJSON(this);
//    }
}
