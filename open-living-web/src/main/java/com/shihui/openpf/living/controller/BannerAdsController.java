/**
 * 
 */
package com.shihui.openpf.living.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
import com.shihui.openpf.living.entity.BannerAds;
import com.shihui.openpf.living.service.BannerAdsService;

/**
 * @author zhouqisheng
 *
 * @date 2016年3月2日 下午3:47:23
 *
 */
@Controller
@RequestMapping(path = "/v2/openpf/living/banner")
public class BannerAdsController {
	@Resource
	private BannerAdsService bannerAdsService;

	@RequestMapping(path = "/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object create(int position, 
			String name,
			@RequestParam(name = "imageId", required = false) String imageId,
			@RequestParam(name = "word", required = false) String word,
			@RequestParam(name = "url", required = false) String url,
			Integer type){
		BannerAds banner = new BannerAds();
		banner.setName(name);
		banner.setImageId(imageId);
		banner.setUrl(url);
		banner.setWord(word);
		banner.setType(type);
		banner.setPosition(position);
		return JSON.toJSON(bannerAdsService.save(banner));
	}

	@RequestMapping(path = "/update", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object update(int id, @RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "imageId", required = false) String imageId,
			@RequestParam(name = "word", required = false) String word,
			@RequestParam(name = "url", required = false) String url,
			@RequestParam(name = "type", required = false) Integer type) {
		BannerAds banner = new BannerAds();
		banner.setId(id);
		banner.setName(name);
		banner.setImageId(imageId);
		banner.setUrl(url);
		banner.setWord(word);
		banner.setType(type);
		return JSON.toJSON(bannerAdsService.update(banner));
	}
	
	@RequestMapping(path = "/delete", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object update(int id) {
		BannerAds banner = new BannerAds();
		banner.setId(id);
		return JSON.toJSON(bannerAdsService.delete(banner));
	}
	
	@RequestMapping(path = "/findbyid", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object query(int id){
		return JSON.toJSON(bannerAdsService.query(id));
	}
	
	@RequestMapping(path = "/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object list(@RequestParam(required = false) Integer position){
		List<BannerAds> list = bannerAdsService.list(position);
		if(list == null)
			return "[]";
		
		return JSON.toJSON(list);
	}
}
