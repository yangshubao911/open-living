/**
 * 
 */
package com.shihui.openpf.living.io3rd;

import java.util.HashMap;
import java.util.Map;

/**
 * 光大查询错误定义
 *
 */
public enum QueryErrorCodeEnum {
	
	SYS0000(0,"0000000","请输入正确的户号或条形码","请输入正确的户号或条形码"),//未知错误
	NPP0001(1,"NPP0001","报文错误(包含MAC验证错误)", "系统繁忙,请稍后再试"),
	NPP0002(2,"NPP0002","系统错误", "系统繁忙,请稍后再试"),
	NPP0003(3,"NPP0003","系统异常", "系统繁忙,请稍后再试"),
	NPP0004(4,"NPP0004","尚未开通此业务", "尚未开通此业务"),
	NPP0005(5,"NPP0005","系统超时", "系统繁忙,请稍后再试"),
//	DEF0001(6,"DEF0001","无相应记录", "未出账或已经缴纳,暂时无需缴费"), //光大提示
	DEF0001(6,"DEF0001","无相应记录", "请输入正确的户号或条形码"),//产品经理要求修改提示
	DEF0002(7,"DEF0002","用户未欠费", "未出账或已经缴纳,暂时无需缴费"),
	DEF0003(8,"DEF0003","与第三方通讯失败", "系统繁忙,请稍后再试"),
	DEF0004(9,"DEF0004","超过受理期,银行不予受理,请至缴费单位缴费", "超过受理期，请联系公共事业单位"),
	DEF0005(10,"DEF0005","超过缴费时间", "超过公共事业单位受理时间，请在有效时间段内缴费"),
	DEF0006(11,"DEF0006","业务状态异常", "暂时无法缴费,请联系公共事业单位"),
	DEF0007(12,"DEF0007","获取代理业务失败", "系统繁忙,请稍后再试"),
	DEF0008(13,"DEF0008","预付费交易金额不足", "预付费交易金额不足"),
	DEF0009(14,"DEF0009","超过限定金额", "超过限定金额"),
	DEF0010(15,"DEF0010","用户不存在", "该户号不存在"),
	DEF0011(16,"DEF0011","第三方返回信息含有错误内容", "系统繁忙,请稍后再试"),
	DEF0012(17,"DEF0012","该用户不支持网上缴费，请联系公共事业单位", "该用户不支持网上缴费，请联系公共事业单位！(备用)"),
	DEF0013(18,"DEF0013","金额不符合规则", "金额不符合规则，请联系公共事业单位！(备用)"),
	DEF0014(19,"DEF0014","卡过期或状态无效", "卡过期或状态无效，请联系公共事业单位！"),
	DEF0015(20,"DEF0015","卡为副卡或不存在", "卡为副卡或不存在，请联系公共事业单位！"),
	DEF0016(21,"DEF0016","密码错误", "密码错误，请重新输入！（备用）"),
	DEF0017(22,"DEF0017","昆明电力代缴专用错误码，其他缴费项目查询不能用此错误码", "非昆明市职工区用户"),
	DEF0018(23,"DEF0018","该户正在银行批扣，暂时无法进行查询与缴费", "银行批扣时间，暂时无法查询缴费");


	
	private int index;
	private String code;
	private String desc;
	private String tip;
	
	QueryErrorCodeEnum(int index, String code, String desc, String tip){
		this.index = index;
		this.code = code;
		this.desc = desc;
		this.tip = tip;
	}

    private static Map<String, QueryErrorCodeEnum> values = new HashMap<>();
    static{
        for(QueryErrorCodeEnum qece: QueryErrorCodeEnum.values()){
            values.put(qece.code,qece);
        }
    }

    public static QueryErrorCodeEnum parse(String errorCode){
    	QueryErrorCodeEnum qece = values.get(errorCode);
    	return (qece == null ? SYS0000 : qece);
    }

	public static String getErrorMessage(String errorCode) {
		return parse(errorCode).getTip();
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

}
