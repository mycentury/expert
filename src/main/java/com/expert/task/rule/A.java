/**
 * 
 */
package com.expert.task.rule;

import java.util.List;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年5月9日
 * @ClassName A
 */
@lombok.Data
public class A {
	private String message;
	private String category_id;
	private Detail data;

	@lombok.Data
	static class Detail {
		private Boolean has_more;
		private Double min_time;
		private Double max_time;
		private List<Data> data;
	}

	@lombok.Data
	static class Data {
		private Group group;
		private String type;
	}

	@lombok.Data
	static class Group {
		private String text;
		private Integer media_type;// 0-段子，1-静图，2-动图，3-视频
		private Media large_image;
		private Media origin_video;
	}

	@lombok.Data
	static class Media {
		private Integer width;
		private Integer height;
		private List<Url> url_list;
	}

	@lombok.Data
	static class OriginVideo {

	}

	@lombok.Data
	static class Url {
		private String url;
	}
}
