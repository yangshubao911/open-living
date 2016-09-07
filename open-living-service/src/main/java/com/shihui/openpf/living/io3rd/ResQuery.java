package com.shihui.openpf.living.io3rd;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

/*
 * 光大返回查询缴费单报文的JavaObject
 */
@XmlRootElement(name = "out")
public class ResQuery implements PacketCheck{

	public static class ToutData {
		public String contractNo;
		public String customerName;
		public double balance;
		public double payAmount;
		public String beginDate;
		public String endDate;
		public String filed1;
		public String filed2;
		public String filed3;
		public String filed4;
		public String filed5;
	}

	public static class Tout {
		public String billKey;
		public String companyId;
		public String item1;
		public String item2;
		public String item3;
		public String item4;
		public String item5;
		public String item6;
		public String item7;
		public String totalNum;
		
		@XmlElement(name = "Data")
		public ArrayList<ToutData> dataList = new ArrayList<ToutData>();
	}
	
	//
	
	public PacketHead head = new PacketHead();
	public Tout tout = new Tout();
	
	public ResQuery() {
		
	}
	@Override
	public boolean check() {
		return (head.AnsTranCode.compareTo("BJCEBQBIRes") == 0
				&& head.InstId.compareTo(PacketHead.INSTID) == 0);
	}

}
