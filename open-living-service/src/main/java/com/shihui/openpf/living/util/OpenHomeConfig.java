package com.shihui.openpf.living.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by zhoutc on 2016/2/26.
 */
@Component
public class OpenHomeConfig {

    public static String snapshot_url;
    public static String snapshot_appid;
    public static String snapshot_salt;
    public static String snapshot_appsn;
    public static String order_submit_url;
    
    @Value("${snapshot_url}")
    public void setSnapshot_url(String snapshot_url) {
        OpenHomeConfig.snapshot_url = snapshot_url;
    }
    @Value("${snapshot_appid}")
    public void setSnapshot_appid(String snapshot_appid) {
        OpenHomeConfig.snapshot_appid = snapshot_appid;
    }
    @Value("${snapshot_salt}")
    public void setSnapshot_salt(String snapshot_salt) {
        OpenHomeConfig.snapshot_salt = snapshot_salt;
    }
    @Value("${snapshot_appsn}")
    public void setSnapshot_appsn(String snapshot_appsn) {
        OpenHomeConfig.snapshot_appsn = snapshot_appsn;
    }
    @Value("${order_submit_url}")
    public void setOrder_submit_url(String order_submit_url) {
        OpenHomeConfig.order_submit_url = order_submit_url;
    }
}
