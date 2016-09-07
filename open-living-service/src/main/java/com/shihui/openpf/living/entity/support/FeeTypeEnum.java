package com.shihui.openpf.living.entity.support;

import java.util.HashMap;
import java.util.Map;

/**
 * 缴费类型
 */
public enum FeeTypeEnum {
    Default(0,"月结"),
	Prepayment(1,"预付费充值");

    private Integer value;
    private String name;


    FeeTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    private static Map<Integer, FeeTypeEnum> values = new HashMap<>();
    static{
        for(FeeTypeEnum serviceTypeEnum : FeeTypeEnum.values()){
            values.put(serviceTypeEnum.value, serviceTypeEnum);
        }
    }

    public static FeeTypeEnum parse(Integer value){
        return values.get(value);
    }

    public Integer getValue(){return value;}
    public String getName(){return name;}
}
