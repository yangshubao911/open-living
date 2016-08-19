package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="in")
public class ReqQuery {

	public static  class Tin {
		
		public String billKey;
		public String companyId;
		public int beginNum = 1;
		public int queryNum = 1;
		public String filed1;
		public String filed2;
		public String filed3;
		public String filed4;

	}
	
	//
	
	public PacketHead head = new PacketHead("BJCEBQBIReq");
	public Tin tin = new Tin();

	public ReqQuery() {
		
	}
	public ReqQuery(String TrmSeqNum) {
		head.TrmSeqNum = TrmSeqNum;
	}

	public static ReqQuery instance(
			String TrmSeqNum,
			String billKey,
			String companyId,
			String filed1,
			String filed2,
			String filed3,
			String filed4
			) {
		ReqQuery req = new ReqQuery(TrmSeqNum);
		
		req.tin.billKey = billKey;
		req.tin.companyId = companyId;
		req.tin.filed1 = filed1;
		req.tin.filed2 = filed2;
		req.tin.filed3 = filed3;
		req.tin.filed4 = filed4;
		
		return req;
	}
}
