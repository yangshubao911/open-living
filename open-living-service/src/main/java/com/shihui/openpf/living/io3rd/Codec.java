package com.shihui.openpf.living.io3rd;

import com.shihui.commons.ApiLogger;

import cebenc.softenc.SoftEnc;

/*
 * 光大报文加解密
 */
public class Codec {
	private static final String LOCK = "cebenc.softenc.SoftEnc";
	private static int PREFIX_LENGTH = 6;
	private static int SUFFIX_LENGTH = 16;

	public static void init(String keyPath) throws Exception {
		SoftEnc.Init(keyPath);
	}
	
	public static void writeKey(String pinKey, String pinKeyCheck, String macKey, String macKeyCheck ) throws Exception {
		synchronized(LOCK) {
			if(macKey != null && macKeyCheck != null)
				SoftEnc.WriteMACK(macKey, macKeyCheck);
			if( pinKey != null && pinKeyCheck != null)
				SoftEnc.WritePINK(pinKey, pinKeyCheck);
		}
	}

	private static String genMac(String xml) throws Exception {
		String mac;
		synchronized(LOCK) {
			mac = SoftEnc.GenMac(xml.getBytes("GBK"));
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
			String body = xml.substring(PREFIX_LENGTH, xml.length() - SUFFIX_LENGTH);
			String mac = genMac(body);
			return (mac.compareTo(xml.substring(xml.length() - SUFFIX_LENGTH)) == 0) ? body : null;
		}
	}
}
