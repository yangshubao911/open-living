/**
 * 
 */
package com.shihui.openpf.living.entity.support;

/**
 * @author zhouqisheng
 *
 */
public enum BannerAdsEnum {
	
	HOME(1, "首页");
	
	private int postion;
	private String desc;
	
	BannerAdsEnum(int postion, String desc){
		this.postion = postion;
		this.desc = desc;
	}

	public int getPostion() {
		return postion;
	}

	public String getDesc() {
		return desc;
	}

}
