/**
 * 
 */
package com.shihui.openpf.living.io3rd;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouqisheng
 *
 */
public enum PayErrorCodeEnum {

	NPP0001(1,"NPP0001","报文错误(包含MAC验证错误)", "退款"),
	NPP0002(2,"NPP0002","系统错误", "退款"),
	NPP0003(3,"NPP0003","系统异常", "暂不退款"),
	NPP0004(4,"NPP0004","尚未开通此业务", "退款"),
	NPP0005(5,"NPP0005","系统超时", "暂不退款"),
	NPP0006(6,"NPP0006","保证金账户金额不足", "退款"),
	NPP0007(7,"NPP0007","账户冻结异常", "退款"),
	EGG0001(8,"EGG0001","扣款失败", "退款"),
	DEF0001(9,"DEF0001","无相应记录", "退款"),
	DEF0002(10,"DEF0002","用户未欠费", "退款"),
	DEF0003(11,"DEF0003","与第三方通讯失败", "暂不退款"),
	DEF0004(12,"DEF0004","超过受理期,银行不予受理,请至缴费单位缴费", "退款"),
	DEF0005(13,"DEF0005","超过缴费时间", "退款"),
	DEF0006(14,"DEF0006","业务状态异常", "退款"),
	DEF0007(15,"DEF0007","获取代理业务失败", "退款"),
	DEF0008(16,"DEF0008","预付费交易金额不足", "退款"),
	DEF0009(17,"DEF0009","超过限定金额", "退款"),
	DEF0010(18,"DEF0010","用户不存在", "退款"),
	DEF0011(19,"DEF0011","第三方返回信息含有错误内容", "暂不退款"),
	DEF0012(20,"DEF0012","该用户不支持网上缴费，请联系公共事业单位", "退款"),
	DEF0013(21,"DEF0013","金额不符合规则", "退款"),
	DEF0014(22,"DEF0014","卡过期或状态无效", "退款（备用）"),
	DEF0015(23,"DEF0015","卡为副卡或不存在", "退款（备用）"),
	DEF0016(24,"DEF0016","密码错误", "退款（备用）"),
	DEF0017(25,"DEF0017","待定", "退款（备用）"),
	DEF0018(26,"DEF0018","该户正在银行批扣，暂时无法进行查询与缴费", "银行批扣时间，暂时无法缴费");


	
	private int index;
	private String code;
	private String desc;
	private String tip;
	
	PayErrorCodeEnum(int index, String code, String desc, String tip){
		this.index = index;
		this.code = code;
		this.desc = desc;
		this.tip = tip;
	}

    private static Map<String, PayErrorCodeEnum> values = new HashMap<>();
    static{
        for(PayErrorCodeEnum qece: PayErrorCodeEnum.values()){
            values.put(qece.code,qece);
        }
    }

    public static PayErrorCodeEnum parse(String errorCode){
        return values.get(errorCode);
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
