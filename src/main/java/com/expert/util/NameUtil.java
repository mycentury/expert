/**
 * 
 */
package com.expert.util;

/**
 * @Desc 按照某种规则给文件夹内文件重命名
 * @author wewenge.yan
 * @Date 2016年7月14日
 * @ClassName FileNamingUtil
 */
public class NameUtil {
	public synchronized static String generateNanoTime() {
		String time = String.valueOf(System.nanoTime());
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return time;
	}

	public synchronized static String generateMilliTime() {
		String time = String.valueOf(System.currentTimeMillis());
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return time;
	}
}
