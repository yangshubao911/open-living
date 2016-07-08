package com.shihui.openpf.living.http;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * 请求HTTP 回调处理接口 Created by SongJian on 2016/1/4.
 */
public interface IHttpCallbackHandler<T> {

	/**
	 * 处理异常时，执行该方法,设置isSuccess=false
	 *
	 * @return
	 */
	void failed(Exception e);

	/**
	 * 处理正常时，执行该方法
	 *
	 * @return
	 */
	void completed(int statusCode, T respBody);

	/**
	 * 处理取消时，执行该方法
	 *
	 * @return
	 */
	void cancelled();

	/**
	 * 是否成功,注意默认成功
	 *
	 * @return
	 */
	boolean isSuccess();

	/**
	 *
	 * @param isSuccess
	 */
	void setIsSuccess(boolean isSuccess);

	/**
	 * 取得异常信息
	 */
	Throwable getExceptions();

	/**
	 * 得到返回值
	 *
	 * @return
	 */
	T get();

	/**
	 * 获得http statuscode
	 * 
	 * @return
	 */
	int getStatusCode();

	/**
	 * 默认String 1.String 2.byte[] 3.OutputStream
	 *
	 * @return
	 */
	int getResultType();

	/**
	 * 关闭这个链接
	 * 
	 * @param http
	 */
	void setHttpRequestConnection(HttpRequestBase http);

}