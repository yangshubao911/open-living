package com.shihui.openpf.living.util;

import java.util.Date;

public class LivingUtil {

	public static String getKeyTrmSeqNum() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(PacketTypeEnum.KEY.getType());
    	sb.append(String.format("%012d", 0));
    	sb.append(String.format("%015d", new Date().getTime()));
    	return sb.toString();
	}
	public static String getQueryTrmSeqNum(long userId) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(PacketTypeEnum.QUERY.getType());
    	sb.append(String.format("%012d", userId));
    	sb.append(String.format("%015d", new Date().getTime()));
    	return sb.toString();
	}
	public static String getRechargeTrmSeqNum(long orderId) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(PacketTypeEnum.RECHARGE.getType());
    	sb.append(String.format("%030d", orderId));
    	return sb.toString();
	}

	//
	
    public static String getUUID() {
    	return java.util.UUID.randomUUID().toString();
    }
    
}
