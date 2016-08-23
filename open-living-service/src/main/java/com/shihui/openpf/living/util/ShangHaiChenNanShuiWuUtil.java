package com.shihui.openpf.living.util;

public class ShangHaiChenNanShuiWuUtil {

	public static String getBillKey(String userNo) {
		return userNo.substring(0,9);
	}
	public static String getBillDate(String userNo) {
		return userNo.substring(9,13);
	}
	public static String getMoney(String userNo) {
		return userNo.substring(14,23);
	}

}
