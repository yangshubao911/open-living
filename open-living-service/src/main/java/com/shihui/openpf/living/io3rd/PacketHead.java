package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlElement;

public class PacketHead {

	public static final String INSTID = "100000000000156";
	
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
