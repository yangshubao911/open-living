package com.shihui.openpf.living.entity.support;

import java.util.HashMap;
import java.util.Map;

import com.shihui.api.order.common.enums.OrderTypeEnum;

/**
 * Created by zhoutc on 2015/12/16.
 */
public enum ServiceTypeEnum {
    PowerRate(OrderTypeEnum.Convenient_LFee.getValue(),"电费"),
    WaterRate(OrderTypeEnum.Convenient_WFee.getValue(),"水费"),
	GasRate(OrderTypeEnum.Convenient_GasFee.getValue(),"煤气费");

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
