package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlElement;

/*
 * 光大报文的报文头JavaObject定义
 */
public class PacketHead {

	public static final String INSTID				= "100000000000156";
	
	public static final String ANSTRANCODE_KEY		= "BJCEBRWKRes";
	public static final String ANSTRANCODE_QUERY	= "BJCEBQBIRes";
	public static final String ANSTRANCODE_PAY		= "BJCEBBCRes";
	public static final String ANSTRANCODE_NOTIFY	= "BJCEBBCNotify";
	public static final String ANSTRANCODE_ERROR	= "Error";
	
	@XmlElement(name="Version")
	public String version = "1.0.1";
	
	@XmlElement(name="InstId")
	public String InstId = INSTID;
	
	@XmlElement(name="AnsTranCode")
	public String AnsTranCode;

	@XmlElement(name="TrmSeqNum")
	public String TrmSeqNum;
	
	public PacketHead() {
	}
	
	public PacketHead( String AnsTranCode ) {
		this.AnsTranCode = AnsTranCode;
	}
}
