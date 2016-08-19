package com.shihui.openpf.living.entity.support;

public class TestInput {
	public int		userId;			// 36050
	public int		serviceId;		// 38
	public int		categoryId;		// 2
	public int		cityId;			// 2
	public long		groupId;		// 532712
	public int		companyId;		// 1
	public String	companyNo;		// 021009006
	public String	userNo;			// 510070111304276000079004
	public long		goodsId;		// 1
	public int		goodsVersion;	// 1
	public String	field2;			// 1
	
	public String	price;			//
	
	public TestInput() {
		
	}
	public TestInput(int userId, int serviceId, int categoryId, int cityId, long groupId, 
			int companyId, String companyNo, String userNo, long goodsId, int goodsVersion, String field2,
			String price) {
		this.userId = userId;
		this.serviceId = serviceId;
		this.categoryId = categoryId;
		this.cityId = cityId;
		this.groupId = groupId;
		this.companyId = companyId;
		this.companyNo = companyNo;
		this.userNo = userNo;
		this.goodsId = goodsId;
		this.goodsVersion = goodsVersion;
		this.field2 = field2;
		this.price = price;
	}
}
