package com.shihui.openpf.living.util;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.shihui.openpf.living.entity.Goods;

import me.weimi.api.commons.http.ApacheHttpClient;
import me.weimi.api.commons.http.ApiHttpClient;
//import me.weimi.api.commons.util.ApiLogger;
import com.shihui.commons.ApiLogger;
/**
 * Created by zhoutc on 2015/9/6.
 */
public class SnapShotUtil {

	/**
	 * ApacheHttpClient
	 */
	private static final ApacheHttpClient APACHE_HTTP_CLIENT = new ApacheHttpClient(10000, 10000);

	private static String APPID = OpenHomeConfig.snapshot_appid;
	private static String SALT = OpenHomeConfig.snapshot_salt;
	private static String APPSN = OpenHomeConfig.snapshot_appsn;
	private static String SNAPSHOT_URL = OpenHomeConfig.snapshot_url;


	/**
	 * 创建商品快照
	 * 
	 * @param goods
	 * @return
	 */
	public static String sendSnapShot(Goods goods) {
		try {

			Map<String, Object> goods_info = new HashMap<String, Object>();
			goods_info.put("goods_id", goods.getGoodsId());
			goods_info.put("goods_name", goods.getGoodsName());
		//	goods_info.put("goods_img_multi", goods.getImageId());
			List<String> big = new ArrayList<>();
			big.add(goods.getDetailImage());
			List<String> detail = new ArrayList<>();
			detail.add(goods.getImageId());
			goods_info.put("goods_big_img",big );
			goods_info.put("goods_small_img", detail);
			goods_info.put("category_id", goods.getCategoryId());
			goods_info.put("merchant_id", goods.getServiceMerchantCode());
			goods_info.put("goods_type", 2);
			goods_info.put("goods_desc", goods.getGoodsDesc());
			goods_info.put("market_price", goods.getPrice());
			goods_info.put("shihui_price", goods.getPrice());
			goods_info.put("goods_lable", goods.getServiceId());
			goods_info.put("goods_status", goods.getGoodsStatus());
			goods_info.put("created_by", "openpf");
			goods_info.put("create_time", goods.getCreateTime());
			goods_info.put("settle_price", goods.getPrice());
			goods_info.put("settle_method", 1);// 未结算

			Map<String, Object> action_info = new HashMap<String, Object>();
			action_info.put("auction_id", 0);
			action_info.put("auction_name", goods.getGoodsName());
			action_info.put("auction_desc", goods.getGoodsName());
			action_info.put("goods_id", goods.getGoodsId());
			action_info.put("merchant_id", goods.getServiceMerchantCode());
			action_info.put("receive_mode", 6);// 上门服务

			Map<String, Object> content = new HashMap<String, Object>();
			content.put("goods_info", goods_info);
			content.put("action_info", action_info);

			long timestamp = System.currentTimeMillis() / 1000;
			String value = md5String(String.valueOf(timestamp)) + SALT + md5String(APPSN);
			String md5value = md5String(String.valueOf(value));

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("id", goods.getGoodsId());
			param.put("type", APPID);
			param.put("content", JSON.toJSONString(content));
			param.put("code", md5value);
			param.put("timestamp", timestamp);

			ApiHttpClient.RequestBuilder requestBuilder = APACHE_HTTP_CLIENT.buildPost(SNAPSHOT_URL).withParam(param)
			        .withHeader("Content-Type", "application/x-www-form-urlencoded");
			return requestBuilder.execute();

		} catch (Exception e) {
			ApiLogger.error("call snapshot error", e);
		}
		return null;
	}

	public static String md5String(String input) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = input.getBytes();
			// 使用MD5创建MessageDigest对象
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				// System.out.println((int)b);
				// 将没个数(int)b进行双字节加密
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}

	}

	public static void main(String[] args) {
		Map<String,Object> a = new HashMap();
		List<String> b = new ArrayList<>();
        a.put("aaa",b.toArray());
		System.out.println(JSON.toJSON(a));

	}
}
