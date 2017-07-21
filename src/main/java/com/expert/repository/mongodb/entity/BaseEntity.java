/**
 * 
 */
package com.expert.repository.mongodb.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年11月22日
 * @ClassName BaseEntity
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String updateTime;
	private String createTime;

	public abstract String getId();
}
