/**
 * 
 */
package com.shihui.openpf.living.entity.support;

/**
 * @author zhouqisheng
 *
 */
public enum QueryModeEnum {
	
	ShangHaiChenNanShuiWu(1, "条码计算");
	
	private int mode;
	private String desc;
	
	QueryModeEnum(int mode, String desc){
		this.mode = mode;
		this.desc = desc;
	}

	public int getMode() {
		return mode;
	}

	public String getDesc() {
		return desc;
	}

}
