/**
 * 
 */
package com.expert.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年4月18日
 * @ClassName Test
 */
public class Test {
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(null, "1");
		map.put(null, "2");
		for (int i = 0; i < 1000; i++) {
			String key = String.valueOf(i);
			map.put(key, key);
		}
		System.out.println(map.get(null));
		System.out.println(new Integer(3).hashCode());
		System.out.println(new String("11").hashCode());
		System.out.println('3' * (int) Math.pow(31, 1) + '2');
		map = new TreeMap<String, String>();
		map.put("1", "1");
		map.put("3", "3");
		map.put("2", "2");
		System.out.println(map);
		String s = null;
		System.out.println(s instanceof String);
	}
}
