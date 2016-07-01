/**
 * 
 */
package com.shihui.openpf.living.util;

/**
 * @author zhouqisheng
 *
 * @version 1.0 Created at: 2016年1月25日 下午7:42:39
 */
public class SimpleResponse {
	private int status;
	private Object msg;

	public SimpleResponse(int status, Object msg) {
		super();
		this.status = status;
		this.msg = msg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"status\":").append(status).append(",\"msg\":\"").append(msg).append("\"}");
		return sb.toString();
	}
}
