/**
 * 
 */
package com.expert.util;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年12月26日
 * @ClassName GsonUtil
 */
public class GsonUtil {
	private final static Gson gson = new Gson();

	public static <T> T fromJson(String json, Class<T> classOfT) {
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJson(String json, Type typeOfT) {
		// Type jsonType = new TypeToken<Map<String, Integer>>() {}.getType();
		return gson.fromJson(json, typeOfT);
	}

	public static String toJson(Object obj) {
		return gson.toJson(obj);
	}

	public static JsonElement readJson(String sourceJson) {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = null;
		try {
			jsonElement = jsonParser.parse(sourceJson);
		} catch (Exception e) {
		}
		return jsonElement;
	}

	public static String formatJson(String sourceJson) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isEmpty(sourceJson)) {
			return sb.toString();
		}
		sourceJson = sourceJson.replaceAll("[\\n\\t\\s]", "");
		Pattern pattern = Pattern.compile("\\{|\\[|\\]|\\}");
		String startPattern = "\\{|\\[";
		Matcher matcher = pattern.matcher(sourceJson);
		int lastIndex = 0;
		int currIndex = 0;
		int tabCount = 0;
		while (matcher.find()) {
			String group = matcher.group();
			currIndex = matcher.start();
			String tabsByCount = getTabsByCount(tabCount);
			String before = sourceJson.substring(lastIndex, currIndex);
			before = before.replace(",", ",\n" + tabsByCount);
			if (before.endsWith(":")) {
				before += "\n" + tabsByCount;
			}
			before = before.replace(":", " : ");
			if (group.matches(startPattern)) {
				tabsByCount = getTabsByCount(++tabCount);
				sb.append(before).append(group).append("\n").append(tabsByCount);
			} else {
				tabsByCount = getTabsByCount(--tabCount);
				sb.append(before).append("\n").append(tabsByCount).append(group);
			}
			lastIndex = matcher.end();
		}
		return sb.toString();
	}

	/**
	 * @param tabCount
	 * @param sb
	 */
	private static String getTabsByCount(int tabCount) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tabCount; i++) {
			sb.append("\t");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String properties = "[{\"age\":28,\"gender\":\"male\"},{\"age\":28,\"gender\":\"male\"}]";
		String sourceJson = "[{\"name\":\"yanwenge\",\"properties\":" + properties + "},{\"name\":\"yanwenge\",\"properties\":" + properties + "}]";
		// String formatJson = formatJson(sourceJson);
		// System.out.println(formatJson);
		JsonElement readJson = readJson(sourceJson);
		System.out.println(readJson);
		System.out.println(readJson.isJsonArray());
		System.out.println(readJson.isJsonObject());
		System.out.println(readJson.isJsonPrimitive());
		System.out.println(readJson("").isJsonObject());
		System.out.println(readJson("").isJsonNull());
		System.out.println(readJson("").isJsonPrimitive());
	}
}
