package com.expert.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expert.constant.SysConfig;
import com.expert.repository.mongodb.entity.IconEntity;
import com.expert.repository.mongodb.entity.MenuEntity;
import com.expert.repository.mongodb.entity.RuleEntity;
import com.expert.repository.mongodb.entity.SysConfigEntity;
import com.expert.repository.mongodb.entity.UserEntity;
import com.expert.util.DateUtil;

@Service
public class InitService {
	private static final Logger logger = Logger.getLogger(InitService.class);
	@Autowired
	private DaoService daoService;

	public void initMenus() {
		List<MenuEntity> menuEntities = new ArrayList<MenuEntity>();
		MenuEntity entity = new MenuEntity();
		entity.setId("Joke");
		entity.setSeq(0);
		entity.setNameZh("段子");
		entity.setNameEn("joke");
		entity.setPath("/");
		menuEntities.add(entity);

		entity = new MenuEntity();
		entity.setId("Picture");
		entity.setHasContent(false);
		entity.setSeq(1);
		entity.setNameZh("图片");
		entity.setNameEn("picture");
		entity.setPath("/picture");
		menuEntities.add(entity);

		entity = new MenuEntity();
		entity.setId("Picture");
		entity.setHasContent(false);
		entity.setSeq(1);
		entity.setNameZh("图片");
		entity.setNameEn("picture");
		entity.setPath("/picture");
		menuEntities.add(entity);

		daoService.delete(null, MenuEntity.class);
		daoService.insert(menuEntities, MenuEntity.class);
	}

	public void initSysConfigs() {
		List<SysConfigEntity> entities = new ArrayList<SysConfigEntity>();
		SysConfigEntity entity = null;

		entity = new SysConfigEntity();
		entity.setId(SysConfig.BASE_URL.getId());
		entity.setDesc(SysConfig.BASE_URL.getDesc());
		entity.setValue("http://localhost:8280");
		entities.add(entity);

		entity = new SysConfigEntity();
		entity.setId(SysConfig.FILE_URL.getId());
		entity.setDesc(SysConfig.FILE_URL.getDesc());
		entity.setValue("/file");
		entities.add(entity);

		entity = new SysConfigEntity();
		entity.setId(SysConfig.FILE_PATH.getId());
		entity.setDesc(SysConfig.FILE_PATH.getDesc());
		entity.setValue("/D:/home/app/file");
		entities.add(entity);

		entity = new SysConfigEntity();
		entity.setId(SysConfig.IMAGE_FOLDER.getId());
		entity.setDesc(SysConfig.IMAGE_FOLDER.getDesc());
		entity.setValue("/image");
		entities.add(entity);

		entity = new SysConfigEntity();
		entity.setId(SysConfig.VIDEO_FOLDER.getId());
		entity.setDesc(SysConfig.VIDEO_FOLDER.getDesc());
		entity.setValue("/video");
		entities.add(entity);

		entity = new SysConfigEntity();
		entity.setId(SysConfig.OTHER_FOLDER.getId());
		entity.setDesc(SysConfig.OTHER_FOLDER.getDesc());
		entity.setValue("/other");
		entities.add(entity);

		daoService.delete(null, SysConfigEntity.class);
		daoService.insert(entities, SysConfigEntity.class);
	}

	public void initSystemUsers() {
		List<UserEntity> entities = new ArrayList<UserEntity>();
		Date now = new Date();
		for (int i = 0; i < 100; i++) {
			UserEntity entity = new UserEntity();
			entity.setId("System" + (1000 + i + 1));
			entity.setUsertype("S");
			entity.setPhoto("/img/logo.jpg");
			entity.setName(entity.getName() + (i + 1) + "号");
			entity.setPassword("Password");
			entity.setRegisterDate(DateUtil._DAY.format(DateUtil.addDays(now, 0 - (int) (Math.random() * 200))));
			entities.add(entity);
		}
		daoService.delete(null, UserEntity.class);
		daoService.insert(entities, UserEntity.class);
	}

	public void initRules() {
		List<RuleEntity> entities = new ArrayList<RuleEntity>();
		RuleEntity entity = null;

		entity = new RuleEntity();
		entity.setId("A1");
		entity.setUrl("http://neihanshequ.com/joke/?is_json=1&app_name=neihanshequ_app&${0}");
		entity.setSplitParam("1494223658");
		entity.setForwardParam("1494223658");
		entity.setBackwardParam("1494223658");
		entity.setStepSize(30);
		entities.add(entity);

		entity = new RuleEntity();
		entity.setId("A2");
		entity.setUrl("http://neihanshequ.com/pic/?is_json=1&app_name=neihanshequ_app&${0}");
		entity.setSplitParam("1494223658");
		entity.setForwardParam("1494223658");
		entity.setBackwardParam("1494223658");
		entity.setStepSize(30);
		entities.add(entity);

		entity = new RuleEntity();
		entity.setId("A3");
		entity.setUrl("http://neihanshequ.com/video/?is_json=1&app_name=neihanshequ_app&${0}");
		entity.setSplitParam("1494223658");
		entity.setForwardParam("1494223658");
		entity.setBackwardParam("1494223658");
		entity.setStepSize(30);
		entities.add(entity);

		entity = new RuleEntity();
		entity.setId("B1");
		entity.setUrl("http://www.budejie.com/detail-${0}.html");
		entity.setSplitParam("24890820");
		entity.setForwardParam("24890820");
		entity.setBackwardParam("24890820");
		entity.setStepSize(1);
		entities.add(entity);

		entity = new RuleEntity();
		entity.setId("B2");
		entity.setUrl("http://www.budejie.com/detail-${0}.html");
		entity.setSplitParam("24890820");
		entity.setForwardParam("24890820");
		entity.setBackwardParam("24890820");
		entity.setStepSize(1);
		entities.add(entity);

		entity = new RuleEntity();
		entity.setId("B3");
		entity.setUrl("http://www.budejie.com/detail-${0}.html");
		entity.setSplitParam("24890820");
		entity.setForwardParam("24890820");
		entity.setBackwardParam("24890820");
		entity.setStepSize(1);
		entities.add(entity);

		entity = new RuleEntity();
		entity.setId("C1");
		entity.setUrl("http://www.qiushibaike.com/article/${0}");
		entity.setSplitParam("118997139");
		entity.setForwardParam("118997139");
		entity.setBackwardParam("118997139");
		entity.setStepSize(1);
		entities.add(entity);

		entity = new RuleEntity();
		entity.setId("C2");
		entity.setUrl("http://www.qiushibaike.com/article/${0}");
		entity.setSplitParam("118997139");
		entity.setForwardParam("118997139");
		entity.setBackwardParam("118997139");
		entity.setStepSize(1);
		entities.add(entity);

		entity = new RuleEntity();
		entity.setId("C3");
		entity.setUrl("http://www.qiushibaike.com/article/${0}");
		entity.setSplitParam("118997139");
		entity.setForwardParam("118997139");
		entity.setBackwardParam("118997139");
		entity.setStepSize(1);
		entities.add(entity);

		daoService.delete(null, RuleEntity.class);
		daoService.insert(entities, RuleEntity.class);
	}

	public void initIcons() {
		List<IconEntity> entities = new ArrayList<IconEntity>();
		IconEntity entity = new IconEntity();
		entity.setId("1000001");
		entity.setUrl("/img/logo.jpg");
		entity.setOriginalUrl("/img/logo.jpg");
		entity.setPublisher("SYSTEM");
		entities.add(entity);
		daoService.delete(null, IconEntity.class);
		daoService.insert(entities, IconEntity.class);
	}
}
