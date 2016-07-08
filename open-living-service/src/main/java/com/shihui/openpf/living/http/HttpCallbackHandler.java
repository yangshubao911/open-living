package com.shihui.openpf.living.http;

import org.apache.http.client.methods.HttpRequestBase;

import java.util.concurrent.CountDownLatch;

/**
 *
 * 回调处理基础功能类
 *
 * @author SongJian
 * @param <T>
 *
 * @param 返回结果类型
 */
public class HttpCallbackHandler<T> implements IHttpCallbackHandler<T> {
    /**
     * 返回结果
     */
    protected T result;
    /**
     * 产生错误时,错误载体
     */
    protected Throwable throwable;
    /**
     * 是否成功
     */
    protected boolean isSuccessed = false;
    
    /**
     * http 状态码
     */
    protected int statusCode;

    HttpRequestBase http ;
    /**
     * 异步get时的一把小锁
     */
    protected CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void failed(Exception e) {
        http.releaseConnection();
        this.throwable = e;
        latch.countDown();//-1
    }

    @Override
    public void completed(int statusCode, T respBody) {
        http.releaseConnection();
        this.result = respBody;
        this.statusCode = statusCode;
        latch.countDown();//-1
        this.setIsSuccess(true);
    }

    @Override
    public void cancelled() {
        latch.countDown();//-1
        http.releaseConnection();
    }

    @Override
    public T get() {
        try {
            latch.await();//+1
        } catch (InterruptedException e) {
            this.throwable = e;
        }
        return result;
    }

    @Override
    public boolean isSuccess() {
        return this.isSuccessed;
    }

    @Override
    public Throwable getExceptions() {
        return this.throwable;
    }

    @Override
    public int getResultType() {
        return 0;
    }

    @Override
    public void setIsSuccess(boolean isSuccess) {
        this.isSuccessed = isSuccess;
    }

    @Override
    public void setHttpRequestConnection(HttpRequestBase http) {
        this.http = http;
    }

	@Override
	public int getStatusCode() {
		return this.statusCode;
	}

}