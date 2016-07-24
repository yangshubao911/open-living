/**
 * 
 */
package com.shihui.openpf.living.entity.support;

/**
 * 开放平台标准化接口状态码定义
 * 
 * @author zhouqisheng
 * @date 2016年3月9日 下午8:02:50
 *
 */
public enum LivingCodeEnum {
	SUCCESS(0, "成功"), 
	PARAM_ERR(1001, "参数错误"),
	SIGN_ERR(1002, "签名错误"),
	SYSTEM_ERR(1003, "内部错误"),
	OTHER_ERR(1004, "其他错误"),
	AREA_NA(2001, "地区不可用"),
	TIME_NA(2002, "时间不可用"),
	SERVICE_NA(2003, "服务类型不支持"),
	OTHER_NA(2004, "其他不可用状态"),
	ORDER_NA(3001, "未查询到订单"),
	CANCEL_FAIL(3101, "取消订单失败"),
	CANCEL_TIME_OUT(3102, "取消订单超时");

	private int code;
	private String msg;

	LivingCodeEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
	
	/**
	 * 根据状态码获得状态定义
	 * @param code
	 * @return
	 */
	public LivingCodeEnum parseCode(int code){
		for(LivingCodeEnum e : LivingCodeEnum.values()){
			if(e.getCode() == code)
				return e;
		}
		return null;
	}
	
	public String toJSONString(){
		return "{\"code\":" + this.code + ",\"msg\":\"" + this.msg + "\"}";
	}
	
	public String toJSONString(String appendMsg){
		return "{\"code\":" + this.code + ",\"msg\":\"" + this.msg + ";" + appendMsg + "\"}";
	}

}
