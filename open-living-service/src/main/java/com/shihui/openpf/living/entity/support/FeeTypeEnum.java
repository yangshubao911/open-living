package com.shihui.openpf.living.entity.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhoutc on 2015/12/16.
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
