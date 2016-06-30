/**
 * 
 */
package com.shihui.commons;

import java.io.IOException;
import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.shihui.api.core.context.RequestContext;
import com.shihui.api.core.exception.NotPresentRequiredParamException;

/**
 * @author zhouqisheng
 *
 */
public class OpenpfJSONHttpMessageConverter extends FastJsonHttpMessageConverter {

	@Override
	protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		RequestContext.Result result = RequestContext.getRequestContext().getResult();
		boolean wrap = result.isWrap();
		int apistatus = result.getApistatus();

		if (obj instanceof NotPresentRequiredParamException) {
			StringBuffer stringBuilder = new StringBuffer();
			NotPresentRequiredParamException exception = (NotPresentRequiredParamException) obj;
			String errorMessage = exception.getMessage();
			if (StringUtils.isNotBlank(errorMessage)) {
				if (errorMessage.contains("Exception:")) {
					errorMessage = errorMessage.substring(errorMessage.lastIndexOf("Exception:"));
				}
				stringBuilder.append(errorMessage);
			} else {
				stringBuilder.append("缺少参数");
			}
			writeErrorMsg(stringBuilder.toString(), outputMessage);
		} if (obj instanceof Exception) {
			writeErrorMsg("系统内部错误！", outputMessage);
		} else if (wrap) {
			result.setResult(JSON.toJSONString(obj));
			writeJson(obj, outputMessage, apistatus);
		} else {
			super.writeInternal(obj, outputMessage);
		}
	}

	private void writeJson(Object obj, HttpOutputMessage outputMessage, int apistatus) throws IOException {
		JSONObject result = new JSONObject();
		result.put("apistatus", apistatus);
		result.put("result", obj);
		super.writeInternal(result, outputMessage);
	}

	private void writeErrorMsg(Serializable json, HttpOutputMessage outputMessage) throws IOException {
		JSONObject result = new JSONObject();
		result.put("apistatus", 0);
		result.put("errorMsg", json);
		super.writeInternal(result, outputMessage);
	}

}
