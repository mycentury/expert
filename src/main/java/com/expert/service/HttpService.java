/**
 * 
 */
package com.expert.service;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.expert.constant.Method;
import com.expert.util.ImageAnalyseUtil;
import com.expert.util.TypeConverterUtil;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年3月9日
 * @ClassName HttpService
 */
@Service
public class HttpService {
	private static final Logger logger = Logger.getLogger(HttpService.class);
	@Value("${http.connect.timeout}")
	private Integer connectTimeout;
	@Value("${http.socket.timeout}")
	private Integer socketTimeout;

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private CacheService cacheService;

	public <T> T requestByRestTemplate(String url, Method method, Object request, Class<T> responseType) {
		if (Method.POST.equals(method)) {
			return restTemplate.postForObject(url, request, responseType);
		} else {
			url += request == null ? "" : this.generateParamUrlByRequest(request);
			return restTemplate.getForObject(url, responseType);
		}
	}

	public Document requestByJsoup(String url, Method method, Object request) {
		Document result = null;
		try {
			if (Method.POST.equals(method)) {
				Connection connect = Jsoup.connect(url);
				result = connect.post();
			} else {
				url += request == null ? "" : this.generateParamUrlByRequest(request);
				Connection connect = Jsoup.connect(url);
				result = connect.get();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String request(String url, Method method, Object request, int times) {
		Request req = null;
		if (Method.POST.equals(method)) {
			req = Request.Post(url);
			req.bodyString(this.generateParamUrlByRequest(request), ContentType.APPLICATION_JSON);
		} else {
			req = Request.Get(url).addHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
		}
		req.connectTimeout(connectTimeout).socketTimeout(socketTimeout);
		for (int i = 0; i < times; i++) {
			try {
				return req.execute().returnContent().asString();
			} catch (Exception e) {
				if (i + 1 < times) {
					logger.warn(url + "请求失败，正在尝试重试第" + (i + 1) + "次");
				} else {
					logger.error(url + "请求重试" + (times - 1) + "次后依然失败！");
				}
				try {
					Thread.sleep(10000);
				} catch (Exception e2) {
				}
			}
		}
		return null;
	}

	public String generateParamUrlByRequest(Object request) {
		Map<String, Object> map = TypeConverterUtil.changeSourceToMap(request);
		StringBuilder result = new StringBuilder();
		int i = 0;
		for (Entry<String, Object> entry : map.entrySet()) {
			result.append(i == 0 ? "?" : "&");
			result.append(entry.getKey()).append("=").append(entry.getValue());
			i++;
		}
		return result.toString();
	}

	public File downloadFile(String urlString, String filename) {
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		String filepath = cacheService.getFilePath() + cacheService.getOtherFolder() + "/" + filename;
		File file = new File(filepath);
		if (file.exists()) {
			return null;
		}
		try {
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			byte[] buf = new byte[1024 * 10];
			int length;
			while ((length = bis.read(buf)) > 0) {
				bos.write(buf, 0, length);
			}
			bis.close();
			bos.close();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public File downloadImage(String urlString, String formatName) {
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		try {
			BufferedImage source = ImageIO.read(url);
			String generateFingerprint = ImageAnalyseUtil.generateFingerprint(source);
			String imageName = generateFingerprint + "." + formatName;
			File file = new File(cacheService.getFilePath() + cacheService.getImageFolder() + "/" + imageName);
			if (file.exists()) {
				return file;
			} else {
				file.createNewFile();
			}
			ImageIO.write(source, formatName, file);
			return file;
		} catch (IOException e) {
			logger.error(url);
			e.printStackTrace();
		}
		return null;
	}
}
