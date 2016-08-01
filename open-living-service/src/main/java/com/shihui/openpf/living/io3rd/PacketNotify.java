package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "in")
public class PacketNotify {

	public class Tin {
		public String date;
		public String fileName;
		public String signDate;
		public String filed1;
	}
	
	//
	
	public PacketHead head = new PacketHead();
	public Tin tin = new Tin();
}
