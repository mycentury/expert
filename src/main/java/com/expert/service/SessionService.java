package com.expert.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.expert.constant.RoleType;
import com.expert.repository.mongodb.entity.FileEntity;
import com.expert.service.DaoService.Condition;

@Service
public class SessionService {
	@Autowired
	private DaoService daoService;

	public boolean isMobile(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		return userAgent.contains("Mobile") || userAgent.contains("mobile");
	}

	public boolean hasLogin(HttpServletRequest request) {
		return !StringUtils.isEmpty(request.getSession().getAttribute("username"));
	}

	public String getUsertype(HttpServletRequest request) {
		Object usertype = request.getSession().getAttribute("usertype");
		if (usertype == null) {
			return RoleType.VISITOR.getCode();
		}
		return usertype.toString();
	}

	public boolean isAdmin(HttpServletRequest request) {
		return "A".equals(request.getSession().getAttribute("usertype"));
	}

	public void setUsername(HttpServletRequest request, String username) {
		request.getSession().setAttribute("username", username);
	}

	public void setUsertype(HttpServletRequest request, String usertype) {
		request.getSession().setAttribute("usertype", usertype);
	}

	public void removeUsername(HttpServletRequest request) {
		request.getSession().removeAttribute("username");
	}

	public void removeUsertype(HttpServletRequest request) {
		request.getSession().removeAttribute("usertype");
	}

	public void setCheckcode(HttpServletRequest request, String checkcode) {
		request.getSession().setAttribute("checkcode", checkcode);
	}

	public String getCheckcode(HttpServletRequest request) {
		Object checkcode = request.getSession().getAttribute("checkcode");
		return checkcode == null ? null : checkcode.toString();
	}

	public void onCreate(HttpSession session) {
		this.countOnlines(session, 1);
		this.executeWhenOn(session);
	}

	public void onDestroy(HttpSession session) {
		this.countOnlines(session, -1);
		this.executeWhenOff(session);
	}

	private void countOnlines(HttpSession session, int change) {
		ServletContext ctx = session.getServletContext();
		Integer onlines = 17;
		if (ctx.getAttribute("onlines") != null) {
			onlines = (Integer) ctx.getAttribute("onlines");
		}
		ctx.setAttribute("onlines", Integer.valueOf(onlines + change));
	}

	private void executeWhenOn(HttpSession session) {
	}

	private void executeWhenOff(HttpSession session) {
		String sessionId = session.getId();
		Condition condition = new Condition();
		condition.addParam("sessionId", "=", sessionId);
		condition.addParam("isTemp", "=", "true");
		daoService.delete(condition, FileEntity.class);
	}
}
