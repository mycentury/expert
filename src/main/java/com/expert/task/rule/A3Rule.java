/**
 * 
 */
package com.expert.task.rule;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expert.constant.Method;
import com.expert.repository.mongodb.entity.RuleEntity;
import com.expert.repository.mongodb.entity.UserEntity;
import com.expert.repository.mongodb.entity.VideoEntity;
import com.expert.task.rule.A.Data;
import com.expert.task.rule.A.Group;
import com.expert.task.rule.A.Url;
import com.expert.util.DateUtil;
import com.expert.util.GsonUtil;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年5月9日
 * @ClassName NeihanduanziRule
 */
@Service
public class A3Rule extends RuleTemplate {

	@Override
	public void execute(RuleEntity rule, boolean forward) {
		String url = rule.getUrl();
		String param = forward ? "min_time=" + rule.getForwardParam() : "max_time=" + rule.getBackwardParam();
		Double max_time = null;
		Double min_time = null;
		url = url.replace("${0}", param);
		String response = httpService.request(url, Method.GET, null, 3);
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
				UserEntity randomSystemUser = cacheService.getRandomSystemUser();
				if (media_type != 3) {
					continue;
				}
				List<Url> url_list = group.getLarge_image().getUrl_list();
				VideoEntity video = new VideoEntity();
				for (Url sourceUrl : url_list) {
					try {
						video.setOriginalUrl(sourceUrl.getUrl());
						File downloadImage = httpService.downloadFile(sourceUrl.getUrl(), System.nanoTime() + ".mp4");
						video.setSourceUrl(sourceUrl.getUrl());
						video.setOriginalUrl(cacheService.getFileUrl() + cacheService.getImageFolder() + "/" + downloadImage.getName());
						String tempName = String.valueOf(System.nanoTime());
						String destImgPath = downloadImage.getAbsolutePath().replace(downloadImage.getName(), tempName + "." + "mp4");
						// File markedFile = ImageMarkUtil.defaultMarkWithText(downloadImage, destImgPath, "老司机带带我");
						// video.setUnmarkedUrl(cacheService.getFileUrl() + cacheService.getImageFolder() + "/" +
						// markedFile.getName());
						// video.setMarkedUrl(cacheService.getFileUrl() + cacheService.getImageFolder() + "/" +
						// markedFile.getName());
						video.setPublisherId(randomSystemUser.getId());
						video.setPublisherName(randomSystemUser.getName());
						video.setPublishDate(DateUtil._SECOND.format(new Date()));
						// 插入
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				daoService.insert(video);
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
		return "A3";
	}
}
