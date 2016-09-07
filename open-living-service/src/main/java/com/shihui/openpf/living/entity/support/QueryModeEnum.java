/**
 * 
 */
package com.shihui.openpf.living.entity.support;

/**
 * 查询模式定义，上海城投水务需要计算条形码
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
