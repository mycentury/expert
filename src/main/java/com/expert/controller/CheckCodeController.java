/**
 * 
 */
package com.expert.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.expert.constant.Constant;
import com.expert.service.SessionService;
import com.expert.util.CheckCodeUtil;

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
public class CheckCodeController {
	@Autowired
	private SessionService sessionService;

	@RequestMapping("/checkcode")
	public void checkCode(HttpServletRequest request, HttpServletResponse response) {
		String checkcode = CheckCodeUtil.getRandomCode(Constant.CHECK_CODE, 4);
		sessionService.setCheckcode(request, checkcode);
		BufferedImage image = CheckCodeUtil.getCheckCodeImg(checkcode, 60, 22);
		try {
			ImageIO.write(image, Constant.IMAGE_TYPE, response.getOutputStream());
			response.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
