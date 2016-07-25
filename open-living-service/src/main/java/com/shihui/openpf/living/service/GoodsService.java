/**
 * 
 */
package com.shihui.openpf.living.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shihui.openpf.common.dubbo.api.ServiceManage;
import com.shihui.openpf.common.model.Service;
import com.shihui.openpf.living.dao.GoodsDao;
import com.shihui.openpf.living.entity.Goods;
import com.shihui.openpf.living.util.SimpleResponse;
import com.shihui.openpf.living.util.SnapShotUtil;

/**
 * @author zhouqisheng
 *
 * @version 1.0 Created at: 2016年1月20日 上午11:15:56
 */
@org.springframework.stereotype.Service
public class GoodsService {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Resource
	private GoodsDao goodsDao;
	@Resource
	private ServiceManage serviceManage;

	/* (non-Javadoc)
	 * @see com.shihui.openpf.home.api.GoodsService#create(com.shihui.openpf.home.model.Goods)
	 */

	public Object create(Goods goods) {
		Goods temp = this.findByCity(goods.getCategoryId(), goods.getCityId());
		if(temp != null){
			return new SimpleResponse(1,"该城市对应此类商品已存在");
		}
		
		Date now = new Date();
		//默认商品状态启用
		goods.setCreateTime(now);
		goods.setUpdateTime(now);
		//默认商品版本为1
		goods.setGoodsVersion(1);
		long id;
		try {
			id = this.goodsDao.insert(goods);
		} catch (Exception e) {
			log.error("创建商品失败，{}", JSON.toJSONString(goods), e);
			return new SimpleResponse(1,"创建商品失败");
		}
		try {
			//创建商品快照
			goods.setGoodsId(id);
			//获得业务对应商户ID，快照需要此字段
			
			Service service = this.serviceManage.findById(goods.getServiceId());
			if(service == null){
				return new SimpleResponse(1,"查询服务类型信息异常");
			}
			
			goods.setServiceMerchantCode(service.getServiceMerchantId());
			
			String result = SnapShotUtil.sendSnapShot(goods);
			if(result == null || result.isEmpty()){
				this.goodsDao.delete(goods);
				log.warn("【创建】调用创建商品快照接口失败，删除已创建商品，商品id={}", id);
				return new SimpleResponse(1,"创建商品快照失败");
			}
			JSONObject jo = JSONObject.parseObject(result);
			boolean status = jo.getBoolean("status");
			if (!status){
				this.goodsDao.delete(goods);
				log.warn("【创建】创建商品快照失败，删除已创建商品，商品id={}", id);
				return new SimpleResponse(1,"创建商品快照失败");
			}
			int version = jo.getIntValue("version_id");
			goods.setGoodsVersion(version);
			this.goodsDao.update(goods);
		} catch (Exception e) {
			log.error("创建商品快照异常,{}", JSON.toJSONString(goods), e);
			return new SimpleResponse(1, "创建商品快照失败");
		}
		
		return new SimpleResponse(0, id);
	}


	public Object update(Goods goods) {
		//设置更新时间
		goods.setUpdateTime(new Date());
		
		Goods oldGoods = this.goodsDao.findById(goods.getGoodsId());
		//仅复制需要更新的属性
		if(goods.getGoodsDesc() != null)
			oldGoods.setGoodsDesc(goods.getGoodsDesc());
		if(goods.getGoodsStatus() != null)
			oldGoods.setGoodsStatus(goods.getGoodsStatus());
		if(goods.getGoodsName() != null)
			oldGoods.setGoodsName(goods.getGoodsName());
		if(goods.getImageId() != null)
			oldGoods.setImageId(goods.getImageId());
		if(goods.getShOffSet() != null)
			oldGoods.setShOffSet(goods.getShOffSet());
		if(goods.getPrice() != null)
			oldGoods.setPrice(goods.getPrice());
		
		oldGoods.setUpdateTime(goods.getUpdateTime());
		Service service = this.serviceManage.findById(oldGoods.getServiceId());
		if(service == null){
			return new SimpleResponse(1,"查询服务类型信息异常");
		}
		oldGoods.setServiceMerchantCode(service.getServiceMerchantId());
		String result = SnapShotUtil.sendSnapShot(oldGoods);
		if(result == null || result.isEmpty()){
			log.warn("【更新】调用创建商品快照接口失败，商品id={}", goods.getGoodsId());
			return new SimpleResponse(1,"创建商品快照失败");
		}
		JSONObject jo = JSONObject.parseObject(result);
		boolean status = jo.getBoolean("status");
        if (!status){
			log.warn("【更新】创建商品快照失败，商品id={}", goods.getGoodsId());
			return new SimpleResponse(1,"更新商品快照失败");
        }
        int version = jo.getIntValue("version_id");
        goods.setGoodsVersion(version);
        
        int ret = 0;
		try {
			ret = this.goodsDao.update(goods);
		} catch (Exception e) {
			log.error("更新商品异常，{}", JSON.toJSONString(goods), e);
		}
		
		if(ret > 0){
			return new SimpleResponse(0, "更新商品信息成功");
		}else{
			return new SimpleResponse(1, "更新商品信息失败");
		}
		
		
	}


	public Goods findById(long id) {
		String sql = "select * from goods where goods_id = ?";
		return this.goodsDao.queryForObject(sql, id);
	}


	public Goods findById(Goods goods) {
		String sql = "select * from `goods` where goods_id = ? and goods_version = ?";
		return this.goodsDao.queryForObject(sql,new Object[]{goods.getGoodsId() , goods.getGoodsVersion()});
	}


	public Goods findByCity(int categoryId, int cityId) {
		String sql = "select * from goods where category_id=? and city_id=?";
		return this.goodsDao.queryForObject(sql, categoryId, cityId);
	}


	public List<Goods> list(int categoryId) {
		String sql = "select * from goods where category_id=?";
		return this.goodsDao.queryForList(sql, categoryId);
	}


	public List<Goods> list(int serviceId , int cityId) {
		String sql = "select * from goods where service_id = ? and city_id = ? order by category_id";
		return this.goodsDao.queryForList(sql, serviceId,cityId);
	}


	public Object batchUpdate(List<Goods> goodsList) {
		int successCount = 0;
		Date now = new Date();
		for(Goods goods : goodsList){
			try {
				goods.setUpdateTime(now);
				this.update(goods);
				successCount++;
			} catch (Exception e) {
				log.error("批量更新商品信息异常，goods_id={}", goods.getGoodsId(), e);
			}
		}
		return new SimpleResponse(0, "更新商品信息完成，成功"+successCount+"，失败"+ (goodsList.size() - successCount));
	}

}
