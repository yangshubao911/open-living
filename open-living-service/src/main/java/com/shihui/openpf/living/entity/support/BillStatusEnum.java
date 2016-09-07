package com.shihui.openpf.living.entity.support;

//import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * 光大缴费单状态
 */
public enum  BillStatusEnum {
    UnPaid(0,"未付款"),
    Paid(1,"已付款"),
    BuyBill(2,"已发送销账请求"),
    BuySuccess(3,"销账返回成功"),
    BuyFail(4,"销账返回失败"),
    CheckSuccess(5,"对账成功"),
    CheckFail(6,"对账失败"),
    Refund(7,"退账"),
    Unusual(8,"无对账无退款异常文件"),
    Timeout(9,"超时关闭"),
    Process(10,"暂不退款错误"),
    Close(-1,"关闭");

    private Integer value;
    private String name;

    BillStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    private static Map<Integer, BillStatusEnum> values = new HashMap<>();
    static{
        for(BillStatusEnum billStatusEnum: BillStatusEnum.values()){
            values.put(billStatusEnum.value,billStatusEnum);
        }
    }

    public static BillStatusEnum parse(Integer value){
        return values.get(value);
    }

    public Integer getValue(){return value;}
    public String getName(){return name;}

//    @JsonValue
//    final Integer value() {
//        return this.value;
//    }
}
