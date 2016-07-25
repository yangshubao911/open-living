/**
 * 
 */
package com.shihui.openpf.living.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.shihui.openpf.living.dao.BannerAdsDao;
import com.shihui.openpf.living.entity.BannerAds;
import com.shihui.openpf.living.util.SimpleResponse;

/**
 * @author zhouqisheng
 *
 * @date 2016年3月2日 下午3:49:19
 *
 */
@Service
public class BannerAdsService {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Resource
	private BannerAdsDao bannerAdsDao;
	
	/**
	 * 创建广告位
	 * @param banner
	 * @return
	 */
	public Object save(BannerAds banner){
		try {
			Date now = new Date();
			banner.setCreateTime(now);
			banner.setUpdateTime(now);
			bannerAdsDao.save(banner);
			return new SimpleResponse(0, "创建成功");
		} catch (Exception e) {
			log.error("创建广告位异常，参数={}", JSON.toJSONString(banner), e);
		}
		return new SimpleResponse(1, "创建失败");
	}
	
	/**
	 * 更新广告位
	 * @param banner
	 * @return
	 */
	public Object update(BannerAds banner){
		try {
			banner.setUpdateTime(new Date());
			bannerAdsDao.update(banner);
			return new SimpleResponse(0, "更新成功");
		} catch (Exception e) {
			log.error("更新广告位异常，参数={}", JSON.toJSONString(banner), e);
		}
		return new SimpleResponse(1, "更新失败");
	}
	
	/**
	 * 删除广告位
	 * @param banner
	 * @return
	 */
	public Object delete(BannerAds banner){
		try {
			bannerAdsDao.delete(banner);
			return new SimpleResponse(0, "删除成功");
		} catch (Exception e) {
			log.error("删除广告位异常，参数={}", JSON.toJSONString(banner), e);
		}
		return new SimpleResponse(1, "删除失败");
	}
	
	/**
	 * 查询
	 * @param id
	 * @return
	 */
	public BannerAds query(int id){
		BannerAds banner = new BannerAds();
		banner.setId(id);
		return this.bannerAdsDao.findById(banner);
	}
	
	/**
	 * 查询
	 * @param position
	 * @return
	 */
	public List<BannerAds> list(Integer position){
		BannerAds banner = new BannerAds();
		banner.setPosition(position);
		List<BannerAds> list = null;
		try {
			list = bannerAdsDao.findByCondition(banner);
		} catch (Exception e) {
			log.error("查询广告位异常", e);
		}
		return list;
	}
}
