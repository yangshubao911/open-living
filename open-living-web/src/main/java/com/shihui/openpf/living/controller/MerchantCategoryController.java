package com.shihui.openpf.living.controller;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
import com.shihui.openpf.living.entity.MerchantCategory;
import com.shihui.openpf.living.service.MerchantCategoryService;
import com.shihui.openpf.living.util.SimpleResponse;


/**
 * Created by zhoutc on 2016/2/1.
 */
@Controller
@RequestMapping(path = "/v2/openpf/living/merchant/category", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
public class MerchantCategoryController {

	private Logger log = LoggerFactory.getLogger(getClass());
    @Resource
    MerchantCategoryService merchantCategoryService;

	@RequestMapping("/list")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
    public String list(
    		@RequestParam(name = "service_id",	required = true )	Integer service_id,
    		@RequestParam(name = "merchant_id",	required = true )	Integer merchant_id
    ){
    	List<MerchantCategory> list;
		try {
			list = merchantCategoryService.queryByConditions(merchant_id, service_id);
		} catch (Exception e) {
			log.error("查询商户商品分类异常", e);
			return new SimpleResponse(1, "查询失败").toString();
		}
        return JSON.toJSONString(list);
    }


	@RequestMapping("/update")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
    public String update(
    		@RequestParam(name = "status",		required = true )	Integer status,
    		@RequestParam(name = "merchant_id",	required = true )	Integer merchant_id,
    		@RequestParam(name = "service_id",	required = true )	Integer service_id,
    		@RequestParam(name = "category_id",	required = true )	Integer category_id

    ){
       MerchantCategory merchantCategory = new MerchantCategory();
        merchantCategory.setMerchantId(merchant_id);
        merchantCategory.setServiceId(service_id);
        merchantCategory.setCategoryId(category_id);
        merchantCategory.setStatus(status);
        return merchantCategoryService.updateCategory(merchantCategory);
    }

	@RequestMapping("/create")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
    public String create(
    		@RequestParam(name = "status",			required = true )	Integer status,
    		@RequestParam(name = "merchant_id",		required = true )	Integer merchant_id,
    		@RequestParam(name = "service_id",		required = true )	Integer service_id,
    		@RequestParam(name = "category_id",		required = true )	Integer category_id

    ){
        MerchantCategory merchantCategory = new MerchantCategory();
        merchantCategory.setMerchantId(merchant_id);
        merchantCategory.setServiceId(service_id);
        merchantCategory.setCategoryId(category_id);
        merchantCategory.setStatus(status);
        return merchantCategoryService.create(merchantCategory);
    }
    
	@RequestMapping("/batchAdd")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
    public String batchAdd(
    		@RequestParam(name = "data",		required = true )	String json
    ){
        List<MerchantCategory> merchantCategorys;
		try {
			merchantCategorys = JSON.parseArray(json, MerchantCategory.class);
		} catch (Exception e) {
			log.error("批量绑定商品分类参数错误", e);
			return JSON.toJSONString(new SimpleResponse(1,"参数格式错误"));
		}
        return merchantCategoryService.batchCreate(merchantCategorys);
    }
}
