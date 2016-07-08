/**
 * 
 */
package com.shihui.openpf.living.dao;

import java.util.List;

//import javax.annotation.Resource;

//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.shihui.openpf.living.entity.BannerAds;

/**
 * @author zhouqisheng
 *
 * @date 2016年3月1日 下午8:26:07
 *
 */
@Repository
public class BannerAdsDao extends AbstractDao<BannerAds> {
	
	public List<BannerAds> queryForClient(Integer postion){
		String sql = "select word,image_id,url from `" + this.tableName + "` where position=?";
		return this.queryForList(sql, postion);
	}

}
