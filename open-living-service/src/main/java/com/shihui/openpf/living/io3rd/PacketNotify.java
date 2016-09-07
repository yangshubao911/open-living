package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlRootElement;

/*
 * 光大对账通知报文的JavaObject
 */
@XmlRootElement(name = "in")
public class PacketNotify {

	public static class Tin {
		public String date;
		public String fileName;
		public String signDate;
		public String filed1;
	}
	
	//
	
	public PacketHead head = new PacketHead();
	public Tin tin = new Tin();
	
	public PacketNotify() {
		
	}
}
