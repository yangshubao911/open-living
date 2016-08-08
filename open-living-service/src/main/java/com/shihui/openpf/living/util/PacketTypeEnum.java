/**
 * 
 */
package com.shihui.openpf.living.util;

/**
 * @author zhouqisheng
 *
 */
public enum PacketTypeEnum {
	
	KEY(0, "申请密钥"),
	QUERY(1, "查询缴费单"),
	RECHARGE(2, "销账");
	
	private int type;
	private String desc;
	
	PacketTypeEnum(int type, String desc){
		this.type = type;
		this.desc = desc;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
