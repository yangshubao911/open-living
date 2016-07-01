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
import com.shihui.openpf.living.entity.MerchantGoods;
import com.shihui.openpf.living.service.MerchantGoodsService;
import com.shihui.openpf.living.util.SimpleResponse;

/**
 * Created by zhoutc on 2016/2/1.
 */
@Controller
@RequestMapping(path = "/v2/openpf/living/merchant/goods", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
public class MerchantGoodsController {
	private Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    MerchantGoodsService merchantGoodsService;

	@RequestMapping("/list")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
    public String list(
    		@RequestParam(name = "merchant_id",		required = true )	Integer merchant_id,
    		@RequestParam(name = "service_id",		required = true )	Integer service_id,
    		@RequestParam(name = "category_id",		required = false )	Integer category_id
    ){
        List<MerchantGoods> list;
		try {
			list = merchantGoodsService.findByConditions(merchant_id, service_id, category_id);
		} catch (Exception e) {
			log.error("查询商户商品遗产", e);
			return new SimpleResponse(1, "查询失败").toString();
		}
        return JSON.toJSONString(list);
    }


	@RequestMapping("/update")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	  public String update(
			  @RequestParam(name = "merchant_id",	required = false )	Integer	merchant_id,
			  @RequestParam(name = "goods_id",		required = false )	Long	goods_id,
			  @RequestParam(name = "status",		required = false )	Integer	status,
			  @RequestParam(name = "settlement",	required = false )	String	settlement

    ){
        MerchantGoods merchantGoods = new MerchantGoods();
        merchantGoods.setMerchantId(merchant_id);
        merchantGoods.setGoodsId(goods_id);
        merchantGoods.setStatus(status);
        merchantGoods.setSettlement(settlement);
        return merchantGoodsService.updateMerchantGoods(merchantGoods);
    }

	@RequestMapping("/create")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
    public String create(
    		@RequestParam(name = "merchant_id",		required = true )	Integer	merchant_id,
    		@RequestParam(name = "goods_id",		required = true )	Long	goods_id,
    		@RequestParam(name = "category_id",		required = true )	Integer	category_id,
    		@RequestParam(name = "service_id",		required = true )	Integer	service_id,
    		@RequestParam(name = "status",			required = true )	Integer	status,
    		@RequestParam(name = "settlement",		required = true )	String	settlement

    ){
        MerchantGoods merchantGoods = new MerchantGoods();
        merchantGoods.setCategoryId(category_id);
        merchantGoods.setMerchantId(merchant_id);
        merchantGoods.setGoodsId(goods_id);
        merchantGoods.setServiceId(service_id);
        merchantGoods.setStatus(status);
        merchantGoods.setSettlement(settlement);
        return merchantGoodsService.createMerchantGoods(merchantGoods);
    }
    
	@RequestMapping("/batchAdd")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
    public String batchAdd(
    		@RequestParam(name = "data",	required = false )	String json
    ){
        List<MerchantGoods> list;
		try {
			list = JSON.parseArray(json, MerchantGoods.class);
		} catch (Exception e) {
			log.error("批量添加商户商品参数转换异常 param={}", json, e);
			return new SimpleResponse(1, "参数格式错误").toString();
		}
        return merchantGoodsService.batchAddGoods(list);
    }
    
	@RequestMapping("/batchUpdate")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
    public String batchUpdate(
    		@RequestParam(name = "data",	required = false )	String json
    ){
        List<MerchantGoods> list;
		try {
			list = JSON.parseArray(json, MerchantGoods.class);
		} catch (Exception e) {
			log.error("批量更新商户商品参数转换异常 param={}", json, e);
			return new SimpleResponse(1, "参数格式错误").toString();
		}
        return merchantGoodsService.batchUpdateAddedGoods(list);
    }
}
