/**
 * 
 */
package com.shihui.openpf.living.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shihui.api.core.auth.Access;
import com.shihui.api.core.auth.Access.AccessType;
import com.shihui.api.core.context.RequestContext;
import com.shihui.commons.OperationLogger;
import com.shihui.openpf.living.service.ClientService;
import com.shihui.openpf.living.controller.BasicController;

/**
 * @author zhouqisheng
 *
 */
@Controller
@RequestMapping(path = "/v2/openpf/living", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
public class ClientController extends BasicController {
	@Resource
	private ClientService clientService;

	@RequestMapping("/home")
	@ResponseBody
	@Access(type = AccessType.COMMON)
	public Object home(
			@RequestParam(required = true) Long userId,
			@RequestParam(required = true) Integer cityId,
			@RequestParam(required = false) Integer historyOrderCount) {
		Map<String, Object> expand = new HashMap<>();
		expand.put("userId", userId);
		expand.put("city_id", cityId);
		expand.put("historyOrderCount", historyOrderCount);
		
		OperationLogger.log("operation.living.home", RequestContext.getRequestContext(), expand);
		return clientService.home(userId,cityId, historyOrderCount);
	}

}