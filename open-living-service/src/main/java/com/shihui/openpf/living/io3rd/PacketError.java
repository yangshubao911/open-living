package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlRootElement;
/*
 * 光大返回错误报文转JavaObject
 */
@XmlRootElement(name = "out")
public class PacketError {

	public static class Tout {
		public String errorCode;
		public String errorMessage;
		public String errorDetail;
	}

	//
	
	public PacketHead head = new PacketHead();
	public Tout tout = new Tout();
	
	public PacketError() {
		
	}
}
