/**
 * 
 */
package com.expert.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.expert.service.CacheService;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年12月16日
 * @ClassName HistoryController
 */
@Controller
@RequestMapping(value = { "footer" })
public class FooterController {
	@Autowired
	private CacheService cacheService;

	@RequestMapping(value = { "description" })
	public String description(HttpServletRequest request, ModelMap map) {
		return "footer/description";
	}

	@RequestMapping(value = { "statistic" })
	public String statistic(HttpServletRequest request, ModelMap map) {
		Map<String, Integer> accessFrequencyMap = cacheService.getAccessFrequencyMap();
		Object[] keys = accessFrequencyMap.keySet().toArray();
		Object[] values = accessFrequencyMap.values().toArray();
		map.put("keys", keys);
		map.put("values", values);
		return "footer/statistic";
	}

	@RequestMapping(value = { "contact" })
	public String contact(HttpServletRequest request, ModelMap map) {
		return "footer/contact";
	}
}
