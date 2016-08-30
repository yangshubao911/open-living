package com.shihui.openpf.living.util;

import java.util.Date;
import java.io.FileWriter;
import java.io.File;

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
    
    //
    private static final String LOG_FILE_PATH = "../logs/xml.txt";
    public static void log(String log) {
    	if(log == null) {
    		File f = new File(LOG_FILE_PATH);
    		if(f.exists())
    			f.delete();
    	} else {
	    	FileWriter fw = null;
	    	try {
	    		fw = new FileWriter(LOG_FILE_PATH, true);
	    		fw.write(log);
	    		fw.write("\n\n");
	    	}catch(Exception E) {
	    	}finally {
	    		if(fw != null) {
	    			try {
	    				fw.close();
	    			} catch(Exception e) {
	    			}
	    		}
	    	}
    	}
    }
    
}
