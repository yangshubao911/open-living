package com.shihui.openpf.living.io3rd;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * 密钥交换请求报文的JavaObject
 */
@XmlRootElement(name="in")
public class ReqKey {

	public static  class Tin {
		public String partnerCode = "727"; // Char(3)
		public String operationDate; // YYYYMMDD
	}

	//
	
	public PacketHead head = new PacketHead("BJCEBRWKReq");
	public Tin tin = new Tin();
	
	public ReqKey() {
		
	}
	public ReqKey(String TrmSeqNum) {
		head.TrmSeqNum = TrmSeqNum;
	}
	
	public static ReqKey instance(String TrmSeqNum) {
		ReqKey req = new ReqKey(TrmSeqNum);
		req.tin.operationDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		return req;
	}
}
