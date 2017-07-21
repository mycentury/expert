/**
 * 
 */
package com.expert.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.expert.constant.ExecuteStatus;
import com.expert.repository.mongodb.entity.TaskEntity;
import com.expert.service.DaoService;
import com.expert.service.DaoService.Condition;
import com.expert.util.DateUtil;
import com.expert.util.SpringContextUtil;
import com.expert.util.StringUtil;

/**
 * @Desc 错误任务重试任务，编制外，不循环自身任务,重写execute方法
 * @author wewenge.yan
 * @Date 2016年12月29日
 * @ClassName ErrorRetryTask
 */
@Service
public class ErrorRetryTask extends TaskTemplete {
	private static final Logger logger = Logger.getLogger(ErrorRetryTask.class);

	@Autowired
	private DaoService daoService;

	@Override
	// @Scheduled(cron = "${task.cron.ssq.retry}")
	public void execute() {
		this.doInTask();
	}

	@Override
	public void doInTask() {
		Condition condition = new Condition();
		condition.addParam("executeStatus", "=", ExecuteStatus.FAILED.name());
		List<TaskEntity> tasks = daoService.query(condition, TaskEntity.class);
		if (CollectionUtils.isEmpty(tasks)) {
			logger.info("恭喜,不存在错误任务！");
			return;
		}

		logger.info("错误任务" + tasks.size() + "个：" + tasks);
		Map<String, TaskTemplete> beans = SpringContextUtil.getBeans(TaskTemplete.class);
		for (TaskEntity task : tasks) {
			TaskTemplete taskTemplete = beans.get(StringUtil.initialToLower(task.getId()));
			if (taskTemplete != null) {
				try {
					// 任务前更新状态
					task.setExecuteStatus(ExecuteStatus.RUNNING.name());
					task.setUpdateTime(DateUtil._SECOND.format(new Date()));
					daoService.save(task);
					// 注意此处调用的是doInTask,而不是execute，因为执行条件不同
					taskTemplete.doInTask();
					// 任务后更新状态
					task.setExecuteStatus(ExecuteStatus.SUCCESS.name());
					task.setUpdateTime(DateUtil._SECOND.format(new Date()));
					daoService.save(task);
					logger.info("重跑任务成功，task=" + task);
				} catch (Exception e) {
					logger.error("重跑任务依然报错，task=" + task);
					task.setExecuteStatus(ExecuteStatus.FAILED.name());
					task.setUpdateTime(DateUtil._SECOND.format(new Date()));
					daoService.save(task);
				}
			}
		}
	}
}
