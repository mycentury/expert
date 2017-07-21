/**
 * 
 */
package com.expert.repository.mongodb.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Desc 菜单
 * @author wewenge.yan
 * @Date 2016年12月8日
 * @ClassName MenuEntity
 */
@Document(collection = "remark")
@Data
@EqualsAndHashCode(callSuper = false)
public class RemarkEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	/**
	 * 类型
	 */
	private String type;// 1-joke，2-picture,3-video
	private String jokeId;
	private String seq;
	private String toRemarkId;
	/**
	 * 内容
	 */
	private String text;
	private String publisher;
	private String publishDate;
	/**
	 * 启用状态：1-启用，0-禁用
	 */
	private Integer status = 0;
}
