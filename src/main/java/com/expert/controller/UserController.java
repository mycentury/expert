/**
 * 
 */
package com.expert.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.expert.constant.OperType;
import com.expert.constant.ResultType;
import com.expert.constant.RoleType;
import com.expert.domain.Result;
import com.expert.repository.mongodb.entity.RecordEntity;
import com.expert.repository.mongodb.entity.UserEntity;
import com.expert.service.DaoService;
import com.expert.service.DaoService.Condition;
import com.expert.service.RecordService;
import com.expert.service.SessionService;
import com.expert.util.AddressUtil;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年11月25日
 * @ClassName LotterySsqController
 */
/**
 * @author yanwenge
 */
@Controller
@RequestMapping("user")
public class UserController {
	private final static Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	private RecordService recordService;
	@Autowired
	private SessionService sessionService;
	@Autowired
	private DaoService daoService;

	@RequestMapping("login")
	public String login(HttpServletRequest request, ModelMap map) {
		if (sessionService.hasLogin(request)) {
			return "redirect:/";
		}
		map.put("usertype", RoleType.GENERAL.getCode());
		String errorMsg = request.getParameter("errorMsg");
		if (!StringUtils.isEmpty(errorMsg)) {
			map.put("errorMsg", errorMsg);
		}
		return sessionService.isMobile(request) ? "user/m_login" : "user/login";
	}

	@RequestMapping(value = "user_login", method = { RequestMethod.POST })
	public String userLogin(HttpServletRequest request, UserEntity user, String checkcode, RedirectAttributes attr) {
		attr.addFlashAttribute("user", user);
		RecordEntity record = recordService.assembleRocordEntity(request);
		record.setOpertype(OperType.LOGIN.name());
		if (StringUtils.isEmpty(record.getUsername())) {
			record.setUsername(user.getId());
			record.setUsertype(user.getUsertype());
		}
		if (!checkcode.equalsIgnoreCase(sessionService.getCheckcode(request))) {
			attr.addFlashAttribute("errorMsg", "验证码错误！");
			record.setAfter(checkcode + "-验证码错误！");
			recordService.insert(record);
			return "redirect:/user/login";
		}
		if (!RoleType.GENERAL.getCode().equals(user.getUsertype())) {
			user.setUsertype(RoleType.GENERAL.getCode());
			RecordEntity record2 = recordService.assembleRocordEntity(request);
			record2.setOpertype(OperType.ATTACK.name());
			record2.setAfter(user.getUsertype() + "-用户类型被调试修改！");
			recordService.insert(record2);
			logger.error("检测到调试攻击，IP=" + AddressUtil.getIpAddress(request));
		}
		Condition condition = new Condition();
		condition.addParam("id", "=", user.getId());
		condition.addParam("password", "=", user.getPassword());
		boolean exists = daoService.exists(condition, UserEntity.class);
		if (!exists) {
			attr.addFlashAttribute("errorMsg", "username or password error");
			record.setAfter(user.getId() + "/" + user.getPassword() + "-username or password error");
			recordService.insert(record);
			return "redirect:/user/login";
		}

		sessionService.setUsername(request, user.getId());
		sessionService.setUsertype(request, user.getUsertype());
		record.setAfter(user.getId() + "-login successfully");
		recordService.insert(record);
		return "redirect:/";
	}

	@RequestMapping(value = "logout")
	public String logout(HttpServletRequest request) {
		sessionService.removeUsername(request);
		sessionService.removeUsertype(request);
		return "redirect:/user/login";
	}

	@RequestMapping("register")
	public String register(HttpServletRequest request, ModelMap map) {
		map.put("usertype", RoleType.GENERAL.getCode());
		return sessionService.isMobile(request) ? "user/m_register" : "user/register";
	}

	@RequestMapping("check_username")
	public @ResponseBody Result<Boolean> checkUsername(HttpServletRequest request, String username, ModelMap map) {
		Result<Boolean> result = new Result<Boolean>();
		String id = RoleType.GENERAL.getCode() + "-" + username;
		boolean exists = daoService.existsById(id, UserEntity.class);
		if (exists) {
			result.setResultStatusAndMsg(ResultType.USER_EXISTS, null);
			result.setData(false);
			return result;
		}
		result.setResultStatusAndMsg(ResultType.SUCCESS, null);
		result.setData(false);
		return result;
	}

	@RequestMapping(value = "user_register", method = { RequestMethod.POST })
	public String register(HttpServletRequest request, UserEntity user, RedirectAttributes attr) {
		attr.addFlashAttribute("user", user);
		RecordEntity record = recordService.assembleRocordEntity(request);
		record.setOpertype(OperType.REGISTER.name());
		boolean exists = daoService.existsById(user.getId(), UserEntity.class);
		if (exists) {
			record.setAfter(user.getId() + "-userId allready exists");
			recordService.insert(record);
			attr.addFlashAttribute("errorMsg", "username allready exists");
			return "redirect:/user/register";
		}
		if (!RoleType.GENERAL.getCode().equals(user.getUsertype())) {
			RecordEntity record2 = recordService.assembleRocordEntity(request);
			record2.setOpertype(OperType.ATTACK.name());
			record2.setAfter(user.getUsertype() + "-用户类型被调试修改！");
			recordService.insert(record2);
			user.setUsertype(RoleType.GENERAL.getCode());
			logger.error("检测到调试攻击，IP=" + AddressUtil.getIpAddress(request));
		}
		user.setUsertype("G");
		user.setName(user.getName() + (1000 + daoService.count(null, UserEntity.class) + 1));
		daoService.insert(user);
		sessionService.setUsername(request, user.getId());
		sessionService.setUsertype(request, user.getUsertype());
		record.setAfter(user.getId() + "-register successfully！");
		recordService.insert(record);
		return "redirect:/";
	}
}
