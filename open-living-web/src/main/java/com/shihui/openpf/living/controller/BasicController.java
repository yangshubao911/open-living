/**
 * 
 */
package com.shihui.openpf.living.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shihui.openpf.living.exception.SimpleRuntimeException;

/**
 * @author zhouqisheng
 *
 */
public class BasicController {
    /**
     * 统一异常处理
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value=SimpleRuntimeException.class)
    @ResponseBody
    public Object exceptionHandle(HttpServletRequest request, SimpleRuntimeException e){
    	if(e.getErrorCode() == null)
    		return this.getFailResult(e.getErrorMsg());
    	return this.getFailResult(e.getErrorCode(), e.getErrorMsg());
    }


    /**
     * 成功的Status Code
     */
    private static final int RESCODE_FAIL = 1;
    /**
     * 失败的Status Code
     */
    private static final int RESCODE_OK = 0;
    

    /**
     * 获取默认ajax成功信息
     */
    protected Map<String, Object> getSuccessResult() {
        return getResult(RESCODE_OK, "操作成功！", Collections.EMPTY_MAP);
    }

    /**
     * 描述：获取成功结果
     *
     * @param obj
     * @return
     */
    protected Map<String, Object> getSuccessResult(Object obj) {
        return getResult(RESCODE_OK, "操作成功", obj);
    }


    /**
     * 描述：获取失败结果
     *
     * @param msg
     * @return
     */
    protected Map<String, Object> getFailResult(String msg) {
        return getResult(RESCODE_FAIL, msg, Collections.EMPTY_MAP);
    }
    
    /**
     * 获取失败结果
     * @param code
     * @param msg
     * @return
     */
    protected Map<String, Object> getFailResult(Integer code, String msg) {
        return getResult(code, msg, Collections.EMPTY_MAP);
    }

    /**
     * 描述：获取返回结果
     *
     * @param isOk
     * @param resCode
     * @param errorMsg
     * @return
     */
    protected Map<String, Object> getResult(int resCode, String errorMsg, Object obj) {
    	Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("status", resCode);
        jsonMap.put("msg", errorMsg);
        jsonMap.put("data", obj);
        return jsonMap;
    }

    /**
     * 获取request
     *
     * @return
     */
    protected HttpServletRequest getRequestContext() {
        return ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
    }

    /**
     * 获取session
     *
     * @return
     */
    protected HttpSession getSessionContext() {
        return ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest().getSession(false);
    }

}
