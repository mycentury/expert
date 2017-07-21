/**
 * 
 */
package com.expert.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest {
	public static void main(String[] args) {
		String test = "手机：18243672134，邮箱：adscasgads@xxx.com，身份证：43466019880306156X，时间：下午4:48:20，小数：123.53";
		Pattern pattern = Pattern.compile("^-?[1-9]\\d*|0$");
		Matcher matcher = pattern.matcher(test);
		while (matcher.find()) {
			System.out.println(matcher.group());
			System.out.println(matcher.group(0));
			System.out.println(matcher.start());
			System.out.println(matcher.end());
		}
	}
}
