/**
 * 
 */
package com.expert.constant;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年12月28日
 * @ClassName SysConfig
 */
public enum SysConfig {
	BASE_URL("JN-00-00", "基础访问路径"),
	FILE_URL("JN-00-01", "文件访问路径"),
	FILE_PATH("JN-00-02", "文件物理路径"),
	IMAGE_FOLDER("JN-00-03", "图片文件物理路径"),
	VIDEO_FOLDER("JN-00-04", "视频文件物理路径"),
	OTHER_FOLDER("JN-00-05", "其他文件物理路径");
	private String id;
	private String desc;

	private SysConfig(String id, String desc) {
		this.id = id;
		this.desc = desc;
	}

	public String getId() {
		return id;
	}

	public String getDesc() {
		return desc;
	}
}
