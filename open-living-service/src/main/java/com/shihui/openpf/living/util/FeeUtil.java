package com.shihui.openpf.living.util;

import java.math.BigDecimal;

public class FeeUtil {

	private static BigDecimal calculatePay( BigDecimal bdPrice, String offSet, String offSetMax) {
		BigDecimal bdOffSet = new BigDecimal(offSet);
		BigDecimal bdOffSetMax = new BigDecimal(offSetMax);
		BigDecimal t = bdPrice.multiply(bdOffSet);
		return bdPrice.subtract(t.min(bdOffSetMax));
	}
	
	public static String calculatePay( String price, String offSet, String offSetMax) {
		BigDecimal bdPrice = new BigDecimal(price);
		BigDecimal bdOffSet = new BigDecimal(offSet);
		BigDecimal bdOffSetMax = new BigDecimal(offSetMax);
		BigDecimal t = bdPrice.multiply(bdOffSet);
		return bdPrice.subtract(t.min(bdOffSetMax)).toString();
	}
	
	public static String calculateOffSet( String price, String offSet, String offSetMax) {
		BigDecimal bdPrice = new BigDecimal(price);
		BigDecimal bdOffSet = new BigDecimal(offSet);
		BigDecimal bdOffSetMax = new BigDecimal(offSetMax);
		BigDecimal t = bdPrice.multiply(bdOffSet);
		return t.min(bdOffSetMax).toString();
	}
}
