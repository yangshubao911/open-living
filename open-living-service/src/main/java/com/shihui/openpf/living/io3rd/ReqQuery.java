package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="in")
public class ReqQuery {

	public class Tin {
		
		public String billKey;
		public String companyId;
		public int beginNum = 1;
		public int queryNum = 1;
		public String field1;
		public String field2;
		public String field3;
		public String field4;

	}
	
	//
	
	public PacketHead head = new PacketHead("BJCEBQBIReq");
	public Tin tin = new Tin();

	public ReqQuery(String TrmSeqNum) {
		head.TrmSeqNum = TrmSeqNum;
	}

	public static ReqQuery instance(
			String TrmSeqNum,
			String billKey,
			String companyId,
			String field1,
			String field2,
			String field3,
			String field4
			) {
		ReqQuery req = new ReqQuery(TrmSeqNum);
		
		req.tin.billKey = billKey;
		req.tin.companyId = companyId;
		req.tin.field1 = field1;
		req.tin.field2 = field2;
		req.tin.field3 = field3;
		req.tin.field4 = field4;
		
		return req;
	}
}
