package com.expert.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.expert.constant.OperType;
import com.expert.repository.mongodb.entity.RecordEntity;
import com.expert.util.AddressUtil;
import com.expert.util.DateUtil;

@Service
public class RecordService {
	@Autowired
	private DaoService daoService;
	@Autowired
	private CacheService cacheService;

	@Value("${log.record.open}")
	private boolean openRecord;

	public void insert(RecordEntity record) {
		if (!openRecord) {
			return;
		}
		String now = DateUtil._SECOND.format(new Date());
		record.setCreateTime(now);
		record.setUpdateTime(now);
		daoService.insert(record);
	}

	/**
	 * @param request
	 * @return
	 */
	public RecordEntity assembleRocordEntity(HttpServletRequest request) {
		RecordEntity record = new RecordEntity();
		if (!openRecord) {
			return record;
		}
		record.setOpertype(OperType.REQUEST.name());
		record.setUsername(String.valueOf(request.getSession().getAttribute("username")));
		record.setUsertype(String.valueOf(request.getSession().getAttribute("usertype")));
		if (StringUtils.isEmpty(record.getUsername())) {
			record.setUsername(String.valueOf(request.getAttribute("username")));
			record.setUsertype(String.valueOf(request.getAttribute("usertype")));
		}

		String baseUrl = cacheService.getBaseUrl();
		String before = request.getHeader("Referer");
		String after = request.getRequestURL().toString();
		if (!StringUtils.isEmpty(before)) {
			before = before.replace(baseUrl, "");
		}
		if (!StringUtils.isEmpty(after)) {
			after = after.replace(baseUrl, "");
		}
		record.setBefore(before);
		record.setAfter(after);
		record.setIp(AddressUtil.getIpAddress(request));
		return record;
	}
}
