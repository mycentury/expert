/**
 * 
 */
package com.expert.repository.mongodb.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年12月28日
 * @ClassName SysConfigEntity
 */
@Document(collection = "sys_config")
@Data
@EqualsAndHashCode(callSuper = false)
public class SysConfigEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	/**
	 * 参数value
	 */
	private String value;
	private String preValue;
	private String nextValue;
	private String desc;
	/**
	 * 启用状态：1-启用，0-禁用
	 */
	private int status = 1;
}
