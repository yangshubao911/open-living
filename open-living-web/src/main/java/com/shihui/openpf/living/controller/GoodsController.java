/**
 * 
 */
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
	private Logger log = LoggerFactory.getLogger(getClass());
	@Resource
	private GoodsService goodsService;

	@RequestMapping("/create")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public String create(
			@RequestParam(name = "category_id",			required = true )	Integer		category_id,
			@RequestParam(name = "city_id",				required = true )	Integer		city_id,
			@RequestParam(name = "city_name",			required = true )	String		city_name,
			@RequestParam(name = "goods_desc",			required = true )	String		goods_desc,
			@RequestParam(name = "goods_name",			required = true )	String		goods_name,
			@RequestParam(name = "image_id",			required = false)	String		image_id,
			@RequestParam(name = "detail_image",		required = true )	String		detail_image,
			@RequestParam(name = "service_id",			required = true )	Integer		service_id,
			@RequestParam(name = "goods_status",		required = true )	Integer		goods_status,
			@RequestParam(name = "sh_off_set",			required = true )	String		sh_off_set,
			@RequestParam(name = "sh_off_set_max",		required = false)	String		sh_off_set_max,
			@RequestParam(name = "first_sh_off_set",	required = true )	String		first_sh_off_set,
			@RequestParam(name = "first_sh_off_set_max",required = false)	String		first_sh_off_set_max,
			@RequestParam(name = "price",				required = true )	String		price,
			@RequestParam(name = "goods_subtitle",		required = true )	String		goods_subtitle,
			@RequestParam(name = "attention",			required = true )	String		attention
			) {
		Goods goods = new Goods();
		goods.setCategoryId(category_id);
		goods.setCityId(city_id);
		goods.setCityName(city_name);
		goods.setGoodsDesc(goods_desc);
		goods.setGoodsName(goods_name);
		goods.setImageId(image_id);
		goods.setDetailImage(detail_image);
		goods.setServiceId(service_id);
		goods.setGoodsStatus(goods_status);
		goods.setShOffSet(sh_off_set);
		goods.setShOffSetMax(sh_off_set_max);
		goods.setFirstShOffSet(first_sh_off_set);
		goods.setFirstShOffSetMax(first_sh_off_set_max);
		goods.setPrice(price);
		goods.setAttention(attention);
		goods.setGoodsSubtitle(goods_subtitle);
		String ret = null;
		try {
			ret = goodsService.create(goods);
		} catch (Exception e) {
			log.error("新增商品异常，{}", JSON.toJSONString(goods), e);
			return JSON.toJSONString(new SimpleResponse(1, "创建商品失败"));
		}
		return ret;
	}

	@RequestMapping("/update")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public String update(
			@RequestParam(name = "goods_id",			required = true )	Long	goods_id,
			@RequestParam(name = "goods_desc",			required = false)	String	goods_desc,
			@RequestParam(name = "goods_status",		required = false)	Integer	goods_status,
			@RequestParam(name = "goods_name",			required = false)	String	goods_name,
			@RequestParam(name = "image_id",			required = false)	String	image_id,
			@RequestParam(name = "detail_image",		required = false)	String	detail_image,
			@RequestParam(name = "sh_off_set",			required = false)	String	sh_off_set,
			@RequestParam(name = "sh_off_set_max",		required = false)	String	sh_off_set_max,
			@RequestParam(name = "first_sh_off_set",	required = false)	String	first_sh_off_set,
			@RequestParam(name = "first_sh_off_set_max",required = false)	String	first_sh_off_set_max,
			@RequestParam(name = "price",				required = false)	String	price,
			@RequestParam(name = "goods_subtitle",		required = false)	String	goods_subtitle,
			@RequestParam(name = "attention",			required = false)	String	attention
			) {
		Goods goods = new Goods();
		goods.setGoodsId(goods_id);
		goods.setGoodsDesc(goods_desc);
		goods.setGoodsName(goods_name);
		goods.setImageId(image_id);
		goods.setDetailImage(detail_image);
		goods.setShOffSet(sh_off_set);
		goods.setShOffSetMax(sh_off_set_max);
		goods.setFirstShOffSet(first_sh_off_set);
		goods.setFirstShOffSetMax(first_sh_off_set_max);
		goods.setPrice(price);
		goods.setGoodsStatus(goods_status);
		goods.setAttention(attention);
		goods.setGoodsSubtitle(goods_subtitle);
		String ret;
		try {
			ret = goodsService.update(goods);
		} catch (Exception e) {
			log.error("更新商品异常，{}", JSON.toJSONString(goods), e);
			return JSON.toJSONString(new SimpleResponse(1, "更新商品失败"));
		}
		
		return ret;
	}
	
	@RequestMapping("/list")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public String list(@RequestParam(name = "category_id", required = true) int categoryId) {
		return JSON.toJSONString(goodsService.list(categoryId));
	}


	@RequestMapping("/listByCity")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public String listByCity(
			@RequestParam(name = "category_id",	required = true)	int categoryId,
			@RequestParam(name = "city_id",		required = true)	int cityId) {
		return JSON.toJSONString(goodsService.findByCity(categoryId, cityId));
	}
	
	@RequestMapping("/batchUpdate")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public String batchUpdate( @RequestParam(name = "data", required = true) String json) {
		List<Goods> goodsList = JSON.parseArray(json, Goods.class);
		return goodsService.batchUpdate(goodsList);
	}

}
