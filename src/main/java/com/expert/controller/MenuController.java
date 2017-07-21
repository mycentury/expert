/**
 * 
 */
package com.expert.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.expert.repository.mongodb.entity.JokeEntity;
import com.expert.service.CacheService;
import com.expert.service.DaoService;
import com.expert.service.DaoService.Condition;
import com.expert.service.SessionService;
import com.expert.util.DateUtil;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年11月25日
 * @ClassName LotterySsqController
 */
/**
 * @author yanwenge
 */
@Controller
public class MenuController {
	@Autowired
	private SessionService sessionService;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private DaoService daoService;

	@RequestMapping(value = { "/picture" })
	public String picture(HttpServletRequest request, ModelMap map) {
		return sessionService.isMobile(request) ? "m_picture" : "picture";
	}

	@RequestMapping(value = { "/joke" })
	public String home(HttpServletRequest request, ModelMap map) {
		return "direct:/";
	}

	@RequestMapping(value = { "/" })
	public String joke(HttpServletRequest request, ModelMap map) {
		Condition condition = new Condition();
		condition.addParam("publishDate", "<=", DateUtil._SECOND.format(new Date()));
		condition.setOrder(Direction.DESC);
		condition.setOrderBy("publishDate");
		condition.setLimit(20);
		List<JokeEntity> jokes = daoService.query(condition, JokeEntity.class);
		map.put("jokes", jokes);
		return sessionService.isMobile(request) ? "m_joke" : "joke";
	}
}
