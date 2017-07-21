/**
 * 
 */
package com.expert.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年5月10日
 * @ClassName UrlTest
 */
public class UrlTest {
	public static void main(String[] args) {
		try {
			URL url = new URL(
					"http://v3.365yg.com/43285f673f48d6473ccd41f46aba3831/5912b0f3/video/m/2205eb2bb945d51449d839d83d4afa3edc4114681e00001a4510c92e12/");
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/D:/项目读写文件/image/" + System.currentTimeMillis()));
			byte[] buf = new byte[1024 * 10];
			int length;
			while ((length = bis.read(buf)) > 0) {
				bos.write(buf, 0, length);
			}
			bis.close();
			bos.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
