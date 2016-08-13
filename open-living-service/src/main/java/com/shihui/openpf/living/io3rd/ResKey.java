package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="out")
public class ResKey implements PacketCheck {

	public class Tout {
		public String partnerCode; // Char(3)
		public String returnCode;
		public String errorDescription;
		public String keyName;
		public String keyValue;
		public String verifyValue;
		public String keyName1;
		public String keyValue1;
		public String verifyValue1;
	}

	//
	
	public PacketHead head = new PacketHead();
	public Tout tout = new Tout();

	public ResKey() {
		
	}
	@Override
	public boolean check() {
		return (head.AnsTranCode.compareTo("BJCEBRWKRes") == 0
				&& head.InstId.compareTo(PacketHead.INSTID) == 0);
	}
}
