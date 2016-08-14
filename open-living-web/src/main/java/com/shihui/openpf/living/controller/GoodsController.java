/**
 * 
 */
package com.shihui.openpf.living.controller;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.http.MediaType;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.shihui.commons.ApiLogger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
import com.shihui.openpf.living.entity.Goods;
import com.shihui.openpf.living.service.GoodsService;
import com.shihui.openpf.living.util.SimpleResponse;


/**
 * @author zhouqisheng
 *
 * @version 1.0 Created at: 2016年1月19日 下午3:09:50
 */
@Controller
@RequestMapping(path = "/v2/openpf/living/goods", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
public class GoodsController {
//	private Logger log = LoggerFactory.getLogger(getClass());
	@Resource
	private GoodsService goodsService;

	@RequestMapping("/create")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object create(
			@RequestParam(name = "categoryId",			required = true )	Integer		categoryId,
			@RequestParam(name = "cityId",				required = true )	Integer		cityId,
			@RequestParam(name = "cityName",			required = true )	String		cityName,
			@RequestParam(name = "goodsDesc",			required = true )	String		goodsDesc,
			@RequestParam(name = "goodsName",			required = true )	String		goodsName,
			@RequestParam(name = "imageId",				required = false)	String		imageId,
			@RequestParam(name = "detailImage",			required = true )	String		detailImage,
			@RequestParam(name = "serviceId",			required = true )	Integer		serviceId,
			@RequestParam(name = "goodsStatus",			required = true )	Integer		goodsStatus,
			@RequestParam(name = "shOffSet",			required = true )	String		shOffSet,
			@RequestParam(name = "shOffSetMax",			required = false)	String		shOffSetMax,
			@RequestParam(name = "firstShOffSet",		required = true )	String		firstShOffSet,
			@RequestParam(name = "firstShOffSetMax",	required = false)	String		firstShOffSetMax,
			@RequestParam(name = "price",				required = true )	String		price,
			@RequestParam(name = "goodsSubtitle",		required = true )	String		goodsSubtitle,
			@RequestParam(name = "attention",			required = true )	String		attention
			) {
		Goods goods = new Goods();
		goods.setCategoryId(categoryId);
		goods.setCityId(cityId);
		goods.setCityName(cityName);
		goods.setGoodsDesc(goodsDesc);
		goods.setGoodsName(goodsName);
		goods.setImageId(imageId);
		goods.setDetailImage(detailImage);
		goods.setServiceId(serviceId);
		goods.setGoodsStatus(goodsStatus);
		goods.setShOffSet(shOffSet);
		goods.setShOffSetMax(shOffSetMax);
		goods.setFirstShOffSet(firstShOffSet);
		goods.setFirstShOffSetMax(firstShOffSetMax);
		goods.setPrice(price);
		goods.setAttention(attention);
		goods.setGoodsSubtitle(goodsSubtitle);
		Object ret = null;
		try {
			ret = JSON.toJSON(goodsService.create(goods));
		} catch (Exception e) {
//			log.error("新增商品异常，{}", JSON.toJSONString(goods), e);
			ApiLogger.error("新增商品异常，{"+JSON.toJSONString(goods)+"}" + e.getMessage());
			return JSON.toJSON(new SimpleResponse(1, "创建商品失败"));
		}
		return ret;
	}

	@RequestMapping("/update")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object update(
			@RequestParam(name = "goodsId",				required = true )	Long	goodsId,
			@RequestParam(name = "goodsDesc",			required = false)	String	goodsDesc,
			@RequestParam(name = "goodsStatus",			required = false)	Integer	goodsStatus,
			@RequestParam(name = "goodsName",			required = false)	String	goodsName,
			@RequestParam(name = "imageId",				required = false)	String	imageId,
			@RequestParam(name = "detailImage",			required = false)	String	detailImage,
			@RequestParam(name = "shOffSet",			required = false)	String	shOffSet,
			@RequestParam(name = "shOffSetMax",			required = false)	String	shOffSetMax,
			@RequestParam(name = "firstShOffSet",		required = false)	String	firstShOffSet,
			@RequestParam(name = "firstShOffSetMax",	required = false)	String	firstShOffSetMax,
			@RequestParam(name = "price",				required = false)	String	price,
			@RequestParam(name = "goodsSubtitle",		required = false)	String	goodsSubtitle,
			@RequestParam(name = "attention",			required = false)	String	attention
			) {
		Goods goods = new Goods();
		goods.setGoodsId(goodsId);
		goods.setGoodsDesc(goodsDesc);
		goods.setGoodsName(goodsName);
		goods.setImageId(imageId);
		goods.setDetailImage(detailImage);
		goods.setShOffSet(shOffSet);
		goods.setShOffSetMax(shOffSetMax);
		goods.setFirstShOffSet(firstShOffSet);
		goods.setFirstShOffSetMax(firstShOffSetMax);
		goods.setPrice(price);
		goods.setGoodsStatus(goodsStatus);
		goods.setAttention(attention);
		goods.setGoodsSubtitle(goodsSubtitle);
		Object ret;
		try {
			ret = JSON.toJSON(goodsService.update(goods));
		} catch (Exception e) {
//			log.error("更新商品异常，{}", JSON.toJSONString(goods), e);
			ApiLogger.error("更新商品异常，{"+JSON.toJSONString(goods)+"}" + e.getMessage());
			return JSON.toJSON(new SimpleResponse(1, "更新商品失败"));
		}
		
		return ret;
	}
	
	@RequestMapping("/list")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object list(@RequestParam(name = "categoryId", required = true) int categoryId) {
		return JSON.toJSON(goodsService.list(categoryId));
	}


	@RequestMapping("/listByCity")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object listByCity(
			@RequestParam(name = "categoryId",	required = true)	int categoryId,
			@RequestParam(name = "cityId",		required = true)	int cityId) {
		return JSON.toJSON(goodsService.findByCity(categoryId, cityId));
	}
	
	@RequestMapping("/batchUpdate")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object batchUpdate( @RequestParam(name = "data", required = true) String json) {
		List<Goods> goodsList = JSON.parseArray(json, Goods.class);
		return JSON.toJSON(goodsService.batchUpdate(goodsList));
	}

}
