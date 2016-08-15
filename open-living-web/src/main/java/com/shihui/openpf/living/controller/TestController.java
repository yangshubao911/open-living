package com.shihui.openpf.living.controller;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
import com.shihui.openpf.living.service.TestService;

/**
 * @author zhouqisheng
 *
 * @date 2016年3月2日 下午3:47:23
 *
 */
@Controller
@RequestMapping(path = "/v2/openpf/living/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TestController {

	@Resource
	private TestService testService;

	@RequestMapping(path = "/reqKey")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object reqKey(){
		return JSON.toJSON(testService.reqKey());
	}
	
}
