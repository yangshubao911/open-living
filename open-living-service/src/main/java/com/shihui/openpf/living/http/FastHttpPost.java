package com.shihui.openpf.living.http;


import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Args;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 简单的Post
 *
 * @author SongJian
 *
 */
public class FastHttpPost {

    private HttpPost httpPost;
    private String postEncode = "utf-8";
    private UrlEncodedFormEntity urlEncodedFormEntity;
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();

    public FastHttpPost(String url) throws UnsupportedEncodingException {
        Args.notBlank(url, "url can't set blank");
        httpPost = new HttpPost(url);
}

    public FastHttpPost(String url, String encode) throws UnsupportedEncodingException {
        Args.notBlank(url, "url can't set blank");
        Args.notBlank(encode, "encode can't set blank");
        httpPost = new HttpPost(url);
        this.postEncode = encode;
    }

    public FastHttpPost addParam(String key, String value) {
        nvps.add(new BasicNameValuePair(key, value));
        return this;
    }

    public FastHttpPost addParam(Map<String, String> param) {
        if (param != null && param.size() > 0) {
            for (Entry<String, String> entry : param.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
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
    public FastHttpPost addStringParameters(String... strings) {
        if (strings != null && strings.length > 0) {
            if (strings.length % 2 == 0) {
                for (int i = 0; i < strings.length; i++, i++) {
                    String key = strings[i];
                    String value = strings[i + 1];
                    this.addParam(key, value);
                }
            } else {
                throw new IllegalArgumentException("params's length errer,params's length %2 != 0");
            }
        }
        return this;
    }

    /**
     * Important,must call before post request
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public HttpPost build() throws UnsupportedEncodingException {
        urlEncodedFormEntity = new UrlEncodedFormEntity(nvps, this.postEncode);
        this.httpPost.setEntity(urlEncodedFormEntity);
        return this.httpPost;
    }

}
