package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlRootElement;

/*
 * 光大返回报文的揶文头的JavaObject
 */
@XmlRootElement(name="out")
public class ResHead implements PacketCheck {

	public PacketHead head = new PacketHead();

	public ResHead() {
		
	}
	@Override
	public boolean check() {
		return (head.InstId.compareTo(PacketHead.INSTID) == 0);
	}
}
