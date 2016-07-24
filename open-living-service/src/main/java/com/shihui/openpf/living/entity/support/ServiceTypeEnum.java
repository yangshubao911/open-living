package com.shihui.openpf.living.entity.support;

import java.util.HashMap;
import java.util.Map;

import com.shihui.api.order.common.enums.OrderTypeEnum;

/**
 * Created by zhoutc on 2015/12/16.
 */
public enum ServiceTypeEnum {
    WaterRate(303,"水费"),//OrderTypeEnum.Convenient_WFee
    PowerRate(304,"电费"),//OrderTypeEnum.Convenient_LFee
	GasRate(306,"煤气费");//OrderTypeEnum.Convenient_GasFee

    private Integer value;
    private String name;


    ServiceTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    private static Map<Integer, ServiceTypeEnum> values = new HashMap<>();
    static{
        for(ServiceTypeEnum serviceTypeEnum : ServiceTypeEnum.values()){
            values.put(serviceTypeEnum.value, serviceTypeEnum);
        }
    }

    public static ServiceTypeEnum parse(Integer value){
        return values.get(value);
    }

    public Integer getValue(){return value;}
    public String getName(){return name;}
}
