package com.shihui.openpf.living.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shihui.openpf.living.io3rd.GuangdaResponse;
import com.shihui.openpf.living.util.SimpleResponse;

/**
 * Created by zhoutc on 2015/12/16.
 */
@Service
public class TestService {

    @Resource
    GuangdaResponse guangdaResponse;

    public Object reqKey() {
    	JSONObject result = new JSONObject();
    	guangdaResponse.doReqKey();
    	result.put("response", new SimpleResponse(0,"已发送申请密钥报文"));
    	return result;
    }
}
