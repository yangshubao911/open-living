/**
 * 
 */
package com.shihui.openpf.living.controller;

import javax.annotation.Resource;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
import com.shihui.openpf.living.entity.Category;
import com.shihui.openpf.living.service.CategoryService;

/**
 * @author zhouqisheng
 *
 * @version 1.0 Created at: 2016年1月21日 下午5:43:19
 */

@Controller
@RequestMapping(path = "/v2/openpf/living/category", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
public class CategoryController {
	
	@Resource
	private CategoryService CategoryService;
	
	@RequestMapping("/create")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object create(
			@RequestParam(name = "desc",		required = true)					String	desc,
			@RequestParam(name = "name",		required = true)					String	name,
			@RequestParam(name = "imageId",		required = true)					String	imageId,
			@RequestParam(name = "serviceId",	required = true)					Integer	serviceId,
			@RequestParam(name = "amount",		required = true, defaultValue="1")	Integer	amount,
			@RequestParam(name = "productId",	required = true )					String	productId,
			@RequestParam(name = "status",		required = true)					Integer	status
			){
    	Category category = new Category();
    	category.setDesc(desc);
    	category.setImageId(imageId);
    	category.setName(name);
    	category.setServiceId(serviceId);
    	category.setStatus(status);
    	category.setAmount(amount);
    	category.setProductId(productId);
		return JSON.toJSON(CategoryService.create(category));
    }
    
	@RequestMapping("/update")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object update(
			@RequestParam(name = "desc",		required = false)	String desc,
			@RequestParam(name = "name",		required = false)	String name,
			@RequestParam(name = "imageId",		required = false)	String imageId,
			@RequestParam(name = "id",			required = true )	Integer id,
			@RequestParam(name = "amount",		required = false)	Integer amount,
			@RequestParam(name = "productId",	required = false)	String productId,
			@RequestParam(name = "status",		required = false)	Integer status
			){
    	Category category = new Category();
    	category.setDesc(desc);
    	category.setImageId(imageId);
    	category.setName(name);
    	category.setId(id);
    	category.setStatus(status);
		return JSON.toJSON(CategoryService.update(category));
    }
    
	@RequestMapping("/list")
	@ResponseBody
	@Access(type = AccessType.INTERNAL)
	public Object list(
			@RequestParam(name = "serviceId",	required = true )	int serviceId
			){
		return JSON.toJSON(CategoryService.list(serviceId));
    }


}
