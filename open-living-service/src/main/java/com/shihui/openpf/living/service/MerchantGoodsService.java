package com.shihui.openpf.living.service;

import java.util.List;

import javax.annotation.Resource;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.shihui.commons.ApiLogger;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.shihui.openpf.living.dao.MerchantGoodsDao;
import com.shihui.openpf.living.entity.MerchantGoods;
import com.shihui.openpf.living.util.SimpleResponse;

/**
 * Created by zhoutc on 2016/2/1.
 */
@Service
public class MerchantGoodsService {
//	private Logger log = LoggerFactory.getLogger(getClass());
    @Resource
    MerchantGoodsDao merchantGoodsDao;

    /**
     * 查询商户业务商品信息
     * @param merchantGoods 商户商品信息
     * @return 商户商品关联信息
     */

    public List<MerchantGoods> queryMerchantGoodsList(MerchantGoods merchantGoods){
          return merchantGoodsDao.findByCondition(merchantGoods);
    }

    /**
     * 查询商户业务商品信息
     * @param merchantGoods 商户商品信息
     * @return 商户商品关联信息
     */

    public MerchantGoods queryMerchantGoods(MerchantGoods merchantGoods) {
        return merchantGoodsDao.findById(merchantGoods);
    }

    /**
     * 更新商户业务商品绑定信息
     * @param merchantGoods 商户业务商品绑定信息
     * @return 更新结果
     */

    public Object updateMerchantGoods(MerchantGoods merchantGoods) {
        if(merchantGoodsDao.update(merchantGoods)>0){
            return new SimpleResponse(0, "更新成功");
        }else {
            return new SimpleResponse(1, "更新失败");
        }
    }

    /**
     * 创建商户业务商品绑定信息
     *
     * @param merchantGoods 商户业务商品绑定信息
     * @return 创建结果
     */

    public Object createMerchantGoods(MerchantGoods merchantGoods) {
        try {
            boolean save = merchantGoodsDao.save(merchantGoods) > 0;
            if (save)
                return new SimpleResponse(0, "创建成功");

        } catch (Exception e) {
//            log.error("MerchantGoodsService error!!", e);
        	ApiLogger.error("MerchantGoodsService error!!" + e.getMessage());
        }

        return new SimpleResponse(1, "创建失败");
    }


	public Object batchAddGoods(List<MerchantGoods> list) {
		try {
			this.merchantGoodsDao.batchSave(list);
			return new SimpleResponse(0, "绑定成功");
		} catch (Exception e) {
//			log.error("批量绑定供应商商品异常", e);
			ApiLogger.error("批量绑定供应商商品异常" + e.getMessage());
			 return new SimpleResponse(1, "绑定失败");
		}
	}


	public Object batchUpdateAddedGoods(List<MerchantGoods> list) {
		try {
			this.merchantGoodsDao.batchUpdate(list);
			return new SimpleResponse(0, "更新成功");
		} catch (Exception e) {
//			log.error("批量更新已绑定供应商商品异常", e);
			ApiLogger.error("批量更新已绑定供应商商品异常" + e.getMessage());
			 return new SimpleResponse(1, "更新失败");
		}
	}
	

	public List<MerchantGoods> findByConditions(Integer merchantId, Integer serviceId, Integer categoryId){
		String sql = "select a.*,b.goods_name,b.price,b.sh_off_set,c.name as category_name from merchant_goods a, goods b, category c where a.goods_id=b.goods_id and a.category_id=c.id and a.merchant_id=? and a.service_id=?";
		if(categoryId == null)
		return merchantGoodsDao.queryForList(sql, merchantId, serviceId);
		sql = sql + " and a.category_id=?";
		return merchantGoodsDao.queryForList(sql, merchantId, serviceId, categoryId);
	}

    /**
     * 查询商品可提供的商户
     * @param goodsId
     * @return
     */

    public List<Integer> getAvailableMerchant(int goodsId) {
        return merchantGoodsDao.getAvailableMerchant(goodsId);
    }
}
