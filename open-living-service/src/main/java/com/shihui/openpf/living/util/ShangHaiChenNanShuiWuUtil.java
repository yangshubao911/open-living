package com.shihui.openpf.living.util;

public class ShangHaiChenNanShuiWuUtil {

	public static boolean checkUserNo(String userNo) {
		if(userNo.length() == 24) {
			String str = String.valueOf(
					10 - (
			(Integer.valueOf(userNo.charAt(0)) 
			+ Integer.valueOf(userNo.charAt(2)) 
			+ Integer.valueOf(userNo.charAt(4)) 
			+ Integer.valueOf(userNo.charAt(6)) 
			+ Integer.valueOf(userNo.charAt(8)) 
			+ Integer.valueOf(userNo.charAt(10)) 
			+ Integer.valueOf(userNo.charAt(12)) 
			+ Integer.valueOf(userNo.charAt(14)) 
			+ Integer.valueOf(userNo.charAt(16)) 
			+ Integer.valueOf(userNo.charAt(18)) 
			+ Integer.valueOf(userNo.charAt(20)) 
			+ Integer.valueOf(userNo.charAt(22))) * 3
			+ Integer.valueOf(userNo.charAt(1))
			+ Integer.valueOf(userNo.charAt(3))
			+ Integer.valueOf(userNo.charAt(5))
			+ Integer.valueOf(userNo.charAt(7)) 
			+ Integer.valueOf(userNo.charAt(9)) 
			+ Integer.valueOf(userNo.charAt(11)) 
			+ Integer.valueOf(userNo.charAt(13)) 
			+ Integer.valueOf(userNo.charAt(15)) 
			+ Integer.valueOf(userNo.charAt(17)) 
			+ Integer.valueOf(userNo.charAt(19)) 
			+ Integer.valueOf(userNo.charAt(21))
			)%10
			);
			if( str.charAt(str.length() - 1) == userNo.charAt(userNo.length() - 1))
					return true;
		}
		return false;
	}
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
