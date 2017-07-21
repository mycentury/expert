/**
 * 
 */
package com.expert.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.expert.BaseTest;
import com.expert.repository.mongodb.entity.JokeEntity;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年2月27日
 * @ClassName InitServiceTest
 */
public class InitServiceTest extends BaseTest {

	@Autowired
	private InitService initService;
	@Autowired
	private DaoService daoService;

	/**
	 * Test method for {@link com.expert.service.InitService#initMenus()}.
	 */
	@Test
	public void testInitMenus() {
		initService.initMenus();
	}

	/**
	 * Test method for {@link com.expert.service.InitService#initSysConfigs()}.
	 */
	@Test
	public void testInitSysConfigs() {
		initService.initSysConfigs();
	}

	/**
	 * Test method for {@link com.expert.service.InitService#initSystemUsers()}.
	 */
	@Test
	public void testInitSystemUsers() {
		initService.initSystemUsers();
	}

	@Test
	public void testInitRules() {
		initService.initRules();
	}

	@Test
	public void test() {
		List<JokeEntity> query = daoService.query(null, JokeEntity.class);
		for (JokeEntity jokeEntity : query) {
			jokeEntity.setPublisherName(jokeEntity.getPublisherName().replace("无名大侠", "新手试驾"));
		}
		daoService.delete(null, JokeEntity.class);
		daoService.insert(query, JokeEntity.class);
	}
}
