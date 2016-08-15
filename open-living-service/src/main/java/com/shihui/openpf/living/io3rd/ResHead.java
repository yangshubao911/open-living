package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="out")
public class ResHead implements PacketCheck {

	public PacketHead head = new PacketHead();

	public ResHead() {
		
	}
	@Override
	public boolean check() {
		return (head.AnsTranCode.compareTo("BJCEBRWKRes") == 0
				&& head.InstId.compareTo(PacketHead.INSTID) == 0);
	}
}
