/**
 * 
 */
package com.expert.service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expert.constant.SysConfig;
import com.expert.repository.mongodb.entity.MenuEntity;
import com.expert.repository.mongodb.entity.RecordEntity;
import com.expert.repository.mongodb.entity.SysConfigEntity;
import com.expert.repository.mongodb.entity.UserEntity;

/**
 * @Desc 统一管理应用缓存
 * @author wewenge.yan
 * @Date 2017年3月13日
 * @ClassName CacheService
 */
@Service
public class CacheService implements InitializingBean {
	private List<MenuEntity> menuList;
	private Map<String, Integer> accessFrequencyMap = new HashMap<String, Integer>();
	private Map<String, String> sysConfigMap = new HashMap<String, String>();
	private List<UserEntity> systemUsers = new ArrayList<UserEntity>();
	private Map<String, BufferedImage> imageMap = new HashMap<String, BufferedImage>();
	@Autowired
	private DaoService daoService;

	public String getBaseUrl() {
		return this.getSysConfig(SysConfig.BASE_URL);
	}

	public String getFileUrl() {
		return this.getSysConfig(SysConfig.FILE_URL);
	}

	public String getFilePath() {
		return this.getSysConfig(SysConfig.FILE_PATH);
	}

	public String getImageFolder() {
		return this.getSysConfig(SysConfig.IMAGE_FOLDER);
	}

	public String getVideoFolder() {
		return this.getSysConfig(SysConfig.VIDEO_FOLDER);
	}

	public String getOtherFolder() {
		return this.getSysConfig(SysConfig.OTHER_FOLDER);
	}

	public Map<String, Integer> getAccessFrequencyMap() {
		return accessFrequencyMap;
	}

	public List<UserEntity> getSystemUsers() {
		return systemUsers;
	}

	public UserEntity getRandomSystemUser() {
		int randomIndex = (int) (Math.random() * systemUsers.size());
		return systemUsers.get(randomIndex);
	}

	public String getSysConfig(SysConfig sysConfig) {
		return sysConfigMap.get(sysConfig.getId());
	}

	public List<MenuEntity> getMenuList() {
		if (menuList == null) {
			this.initMenuList();
		}
		return menuList;
	}

	public void initMenuList() {
		menuList = daoService.query(null, MenuEntity.class);
	}

	private void initSysConfigMap() {
		List<SysConfigEntity> allConfigs = daoService.query(null, SysConfigEntity.class);
		for (SysConfigEntity sysConfig : allConfigs) {
			sysConfigMap.put(sysConfig.getId(), sysConfig.getValue());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initSysConfigMap();
		this.initAccessFrequencyMap();
		this.initSystemUsers();
		this.initImageMap();
	}

	private void initImageMap() {
	}

	private void initSystemUsers() {
		this.systemUsers = daoService.query(null, UserEntity.class);
	}

	private void initAccessFrequencyMap() {
		List<RecordEntity> allRecords = daoService.query(null, RecordEntity.class);
		for (RecordEntity recordEntity : allRecords) {
			this.updateAccessFrequencyMap(recordEntity);
		}
	}

	public void updateAccessFrequencyMap(RecordEntity recordEntity) {
		String key = recordEntity.getAfter();
		Integer value = accessFrequencyMap.get(key);
		if (value == null) {
			value = 0;
		}
		accessFrequencyMap.put(key, value + 1);
	}
}
