package com.shihui.openpf.living.util;

public class ShangHaiChenNanShuiWuUtil {

	public static boolean checkUserNo(String userNo) {
		if(userNo.length() == 24) {
			int m,i;
			for(m=0, i=0; i < 23; i += 2)
				m += Integer.parseInt(userNo.substring(i, i+1));
			m *= 3;
			for(i=1; i< 23; i+=2)
				m += Integer.parseInt(userNo.substring(i, i+1));
			m = 10 - (m % 10);
			String str = String.valueOf(m);
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
