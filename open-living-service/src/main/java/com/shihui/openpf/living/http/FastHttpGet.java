package com.shihui.openpf.living.http;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Args;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 *
 *
 * @author SongJian
 *
 */
public class FastHttpGet {

    private String  url = null;

    private HttpGet httpGet;

    private Map<String,String> params = new HashMap<String,String>();

    public FastHttpGet(String url) {
        Args.notBlank(url, "url can't set blank");
        this.url = url;
    }

    public FastHttpGet addParam(String key, String value) {
        params.put(key, value);
        return this;
    }
    public FastHttpGet addParam(Map<String, String> param) {
        if (param != null && param.size() > 0) {
            params.putAll(param);
        }
        return this;
    }
    /**
     * 同时添加多个参数 例如添加3个参数:
     * addStringParameters("param1","value1","param2","value2","param3","value3"
     * );
     *
     * @param strings
     * @return
     */
    public FastHttpGet addStringParameters(String... strings) {
        if (strings != null && strings.length > 0) {
            if (strings.length % 2 == 0) {
                for (int i = 0; i < strings.length; i++, i++) {
                    String key = strings[i];
                    String value = strings[i + 1];
                    params.put(key, value);
                }
            } else {
                throw new IllegalArgumentException("params's length errer,params's length %2 != 0");
            }
        }
        return this;
    }

    /**
     * Important,must call before get request
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public HttpGet build() {
        Args.notBlank(this.url,"target url is blank");
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setPath(this.url);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        uriBuilder.setParameters(pairs);
        try {
            this.httpGet = new HttpGet(uriBuilder.build());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return httpGet;
    }

    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, String> params){
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> param: params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(),param.getValue()));
        }
        return pairs;
    }

}