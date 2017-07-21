/**
 * 
 */
package com.expert.task.rule;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.expert.constant.Method;
import com.expert.repository.mongodb.entity.JokeEntity;
import com.expert.repository.mongodb.entity.RuleEntity;
import com.expert.repository.mongodb.entity.UserEntity;
import com.expert.task.rule.A.Data;
import com.expert.task.rule.A.Group;
import com.expert.util.DateUtil;
import com.expert.util.GsonUtil;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年5月9日
 * @ClassName NeihanduanziRule
 */
@Service
public class A1Rule extends RuleTemplate {

	@Override
	public void execute(RuleEntity rule, boolean forward) {
		String url = rule.getUrl();
		String param = forward ? "min_time=" + rule.getForwardParam() : "max_time=" + rule.getBackwardParam();
		Double max_time = null;
		Double min_time = null;
		url = url.replace("${0}", param);
		String response = httpService.request(url, Method.GET, null, 3);
		if (response.contains("来补充")) {
			System.out.println(url);
		}
		A fromJson = GsonUtil.fromJson(response, A.class);
		System.out.println(fromJson);
		if (fromJson != null && "success".equals(fromJson.getMessage()) && fromJson.getData() != null && fromJson.getData().getData() != null) {
			// 两个值反了
			Double temp_max_time = fromJson.getData().getMin_time();
			Double temp_min_time = fromJson.getData().getMax_time();
			if (max_time == null || max_time < temp_max_time) {
				max_time = temp_max_time;
			}
			if (min_time == null || min_time > temp_min_time) {
				min_time = temp_min_time;
			}
			for (Data data : fromJson.getData().getData()) {
				Group group = data.getGroup();
				Integer media_type = group.getMedia_type();
				if (media_type == null || media_type != 0) {
					continue;
				}
				UserEntity randomSystemUser = cacheService.getRandomSystemUser();
				JokeEntity joke = new JokeEntity();
				joke.setText(group.getText());
				joke.setPublishDate(DateUtil._SECOND.format(new Date()));
				joke.setPublisherId(randomSystemUser.getId());
				joke.setPublisherName(randomSystemUser.getName());
				// 插入
				daoService.insert(joke);
			}
		}
		// 更新配置
		if (forward) {
			rule.setForwardParam(String.valueOf(max_time));
		} else {
			rule.setBackwardParam(String.valueOf(min_time));
		}
		daoService.save(rule);
	}

	@Override
	public String getRuleId() {
		return "A1";
	}
}
