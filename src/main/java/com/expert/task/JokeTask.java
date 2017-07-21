/**
 * 
 */
package com.expert.task;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.expert.repository.mongodb.entity.RuleEntity;
import com.expert.service.DaoService;
import com.expert.service.DaoService.Condition;
import com.expert.task.rule.RuleTemplate;
import com.expert.util.SpringContextUtil;

/**
 * @Desc 错误任务重试任务，编制外，不循环自身任务,重写execute方法
 * @author wewenge.yan
 * @Date 2016年12月29日
 * @ClassName ErrorRetryTask
 */
@Service
public class JokeTask extends TaskTemplete {
	private static final Logger logger = Logger.getLogger(JokeTask.class);

	@Autowired
	private DaoService daoService;

	@Override
	@Scheduled(cron = "${task.cron.joke}")
	public void execute() {
		this.doInTask();
	}

	@Override
	public void doInTask() {
		Condition condition = new Condition();
		condition.addParam("status", "=", 1);
		List<RuleEntity> query = daoService.query(condition, RuleEntity.class);
		if (CollectionUtils.isEmpty(query)) {
			return;
		}
		int index = (int) (query.size() * Math.random());
		RuleEntity rule = query.get(index);
		Map<String, RuleTemplate> beans = SpringContextUtil.getBeans(RuleTemplate.class);
		for (RuleTemplate ruleTemplate : beans.values()) {
			if (rule.getId().equals(ruleTemplate.getRuleId())) {
				boolean forward = Math.random() >= 0.5 ? true : false;
				ruleTemplate.execute(rule, forward);
			}
		}

	}
}
