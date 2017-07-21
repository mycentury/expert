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
@Document(collection = "rule")
@Data
@EqualsAndHashCode(callSuper = false)
public class RuleEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String url;
	private String forwardParam;
	private String splitParam;
	private String backwardParam;
	private Integer stepSize;
	private String desc;
	/**
	 * 启用状态：1-启用，0-禁用
	 */
	private Integer status = 1;
}
