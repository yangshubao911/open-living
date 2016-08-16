package com.shihui.openpf.living.io3rd;

import com.shihui.commons.ApiLogger;

import cebenc.softenc.SoftEnc;

public class Codec {
	private static final String LOCK = "cebenc.softenc.SoftEnc";
	private static int PREFIX_LENGTH = 6;
	private static int SUFFIX_LENGTH = 16;

	public static void init(String keyPath) throws Exception {
		SoftEnc.Init(keyPath);
	}
	
	public static void writeKey(String macKey, String macKeyCheck, String pinKey, String pinKeyCheck) throws Exception {
		synchronized(LOCK) {
			if(macKey != null && macKeyCheck != null)
				SoftEnc.WriteMACK(macKey, macKeyCheck);
			if( pinKey != null && pinKeyCheck != null)
				SoftEnc.WritePINK(pinKey, pinKeyCheck);
		}
	}
	
//	private static String genMac(String xml) throws Exception {
//		byte[] ba = xml.getBytes("GBK");
//		String bcd = new String(ba);
//		String mac;
//		synchronized(LOCK) {
//			mac = SoftEnc.GenMac(SoftEnc.asctobcd(bcd, ba.length).getBytes());
//		}
//		return mac;
//		
//	}
//TODO ??
	private static String genMac(String xml) throws Exception {
		//byte[] ba = xml.getBytes("GBK");
		//String bcd = new String(ba);
		String mac;
		synchronized(LOCK) {
			mac = SoftEnc.GenMac(SoftEnc.asctobcd(xml, xml.length()).getBytes());
		}
		return mac;
		
	}

	public static  String encodeNoMac(String xml) throws Exception {
			String head =  String.format("%06d", xml.length());
			return head + xml;
	}

	public static String encode(String xml) throws Exception {
		String mac = genMac(xml);
		String head = String.format("%06d", xml.length() + mac.length());
		return head + xml + mac;
	}
	
	public static String decode(String xml) throws Exception {
		if( xml.charAt(xml.length() - 1) == '>') {
			return xml.substring(PREFIX_LENGTH);
		}
		else {
			ApiLogger.info("Codec : decode() : xml =[" + xml + "]");
			String body = xml.substring(PREFIX_LENGTH, xml.length() - SUFFIX_LENGTH);
			ApiLogger.info("Codec : decode() : body =[" + body + "]");
			String mac = genMac(body);
			ApiLogger.info("Codec : decode() : mac =[" + mac + "]");
			return (mac.compareTo(xml.substring(xml.length() - SUFFIX_LENGTH)) == 0) ? body : null;
		}
	}
}
