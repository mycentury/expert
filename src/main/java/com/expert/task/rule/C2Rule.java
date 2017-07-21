/**
 * 
 */
package com.expert.task.rule;

import org.springframework.stereotype.Service;

import com.expert.repository.mongodb.entity.RuleEntity;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年5月9日
 * @ClassName NeihanduanziRule
 */
@Service
public class C2Rule extends RuleTemplate {
	@Override
	public void execute(RuleEntity rule, boolean forward) {
	}

	@Override
	public String getRuleId() {
		return "C2";
	}
}
