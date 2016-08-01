package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "out")
public class ResPay implements PacketCheck{

	public class Tout {
		public String billKey;
		public String companyId;
		public String billNo;
		public String payDate;
		public double payAmount;
		public String bankBillNo;
		public String receiptNo;
		public String acctDate;
	}
	
	//
	
	public PacketHead head = new PacketHead();
	public Tout tout = new Tout();
	
	@Override
	public boolean check() {
		return (head.AnsTranCode.compareTo("BJCEBBCRes") == 0
				&& head.InstId.compareTo(PacketHead.INSTID) == 0);
	}

}
