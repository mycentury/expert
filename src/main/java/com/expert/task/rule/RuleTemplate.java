package com.expert.task.rule;

import org.springframework.beans.factory.annotation.Autowired;

import com.expert.repository.mongodb.entity.RuleEntity;
import com.expert.service.CacheService;
import com.expert.service.DaoService;
import com.expert.service.HttpService;

public abstract class RuleTemplate {
	@Autowired
	protected HttpService httpService;
	@Autowired
	protected CacheService cacheService;
	@Autowired
	protected DaoService daoService;

	public abstract void execute(RuleEntity rule, boolean forward);

	public abstract String getRuleId();
}
