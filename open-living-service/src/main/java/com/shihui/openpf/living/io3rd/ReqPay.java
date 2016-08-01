package com.shihui.openpf.living.io3rd;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "in")
public class ReqPay {

	public class Tin {
		public String billKey;
		public String companyId;
		public String billNo;
		public String payDate;
		public String filed1;
		public String filed2;
		public String filed3;
		public String filed4;
		public String customerName;
		public String payAccount;
		public String pin;
		public String payAmount;
		public String acType;
		public String contractNo;
	}
	
	//
	
	public PacketHead head = new PacketHead("BJCEBBCReq");
	public Tin tin = new Tin();

	public ReqPay(String TrmSeqNum) {
		head.TrmSeqNum = TrmSeqNum;
	}

	public static ReqPay instance(
			String TrmSeqNum,
			String billKey,
			String companyId,
			String billNo,
			String payAmount,
			String customerName,
			String contractNo,
			String filed1,
			String filed2,
			String filed3,
			String filed4
			) {
		ReqPay req = new ReqPay(TrmSeqNum);
		
		req.tin.billKey = billKey;
		req.tin.companyId = companyId;
		req.tin.billNo = billNo;
		req.tin.customerName = customerName;
		req.tin.payAmount = payAmount;
		req.tin.contractNo = contractNo;
		req.tin.filed1 = filed1;
		req.tin.filed2 = filed2;
		req.tin.filed3 = filed3;
		req.tin.filed4 = filed4;
		
		req.tin.payDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		return req;
	}
}
