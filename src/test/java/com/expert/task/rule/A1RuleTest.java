/**
 * 
 */
package com.expert.task.rule;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.expert.BaseTest;
import com.expert.repository.mongodb.entity.RuleEntity;
import com.expert.service.DaoService;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年5月9日
 * @ClassName ARuleTest
 */
public class A1RuleTest extends BaseTest {

	@Autowired
	private A1Rule neihanduanziRule;
	@Autowired
	private DaoService daoService;

	/**
	 * Test method for {@link com.expert.task.rule.A1Rule#execute(java.lang.String)}.
	 */
	@Test
	public void testExecute() {
		RuleEntity rule = daoService.queryById("A1", RuleEntity.class);
		neihanduanziRule.execute(rule, false);
	}

}
